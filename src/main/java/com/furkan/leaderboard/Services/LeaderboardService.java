package com.furkan.leaderboard.Services;

import com.furkan.leaderboard.Entity.Game;
import com.furkan.leaderboard.Entity.Player;
import com.furkan.leaderboard.Entity.Score;
import com.furkan.leaderboard.dto.ScoreDTO;
import com.furkan.leaderboard.repository.GameRepository;
import com.furkan.leaderboard.repository.PlayerRepository;
import com.furkan.leaderboard.repository.ScoreRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    private final StringRedisTemplate redisTemplate;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final ScoreRepository scoreRepository;

    public LeaderboardService(StringRedisTemplate redisTemplate, PlayerRepository playerRepository, GameRepository gameRepository, ScoreRepository scoreRepository) {
        this.redisTemplate = redisTemplate;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.scoreRepository = scoreRepository;
    }

    private String redisKeyForGame(String gameName) {
        return "leaderboard:game:" + gameName.toLowerCase();
    }

    @PostConstruct
    public void loadFromDatabaseToRedis() {
        System.out.println("🔄 Loading scores from DB into Redis...");

        List<Score> allScores = scoreRepository.findAll();
        for (Score score : allScores) {
            String redisKey = redisKeyForGame(score.getGame().getName());
            redisTemplate.opsForZSet().add(redisKey, score.getPlayer().getUsername(), score.getScore());
        }

        System.out.println("✅ Leaderboards initialized in Redis");
    }

    //Update score in Redis
    public List<ScoreDTO> updateScore(String username, String gameName, int score) {
        String key = redisKeyForGame(gameName);

        redisTemplate.opsForZSet().add(key, username, score);
        return getLeaderboard(gameName, 10);
    }

    //Get leaderboard from Redis
    public List<ScoreDTO> getLeaderboard(String gameName, int limit) {
        String key = redisKeyForGame(gameName);
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

        var results = zSet.reverseRangeWithScores(key, 0, limit - 1);
        if (results == null) return List.of();

        return results.stream()
                .map(e -> new ScoreDTO(e.getValue(), gameName, e.getScore().intValue()))
                .collect(Collectors.toList());
    }

    //Get player's rank
    public Long getRanking(String username, String gameName) {
        String key = redisKeyForGame(gameName);
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

        Long rank = zSet.reverseRank(key, username);
        return rank != null ? rank + 1 : null;
    }

    //Get the highest score in game
    public ScoreDTO getHighestScore(String gameName) {
        String key = redisKeyForGame(gameName);
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

        var top = zSet.reverseRangeWithScores(key, 0, 0);
        if (top == null || top.isEmpty()) return null;

        var entry = top.iterator().next();
        return new ScoreDTO(entry.getValue(), gameName, entry.getScore().intValue());
    }

    //Sync Redis to DB every 60 seconds
    @Transactional
    @Scheduled(fixedRate = 60000) // runs every 60s
    public void syncToDatabase() {
        // 1. Get all games from DB
        List<Game> games = gameRepository.findAll();

        for (Game game : games) {
            String redisKey = redisKeyForGame(game.getName());

            // 2. Get all scores from Redis for this game
            var redisScores = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(redisKey, 0, -1);

            if (redisScores == null) continue;

            for (var entry : redisScores) {
                String username = entry.getValue();      // player name
                int scoreValue = entry.getScore().intValue();

                // 3. Ensure player exists
                Player player = playerRepository.findByUsername(username);
                if (player == null) {
                    player = playerRepository.save(Player.builder().username(username).build());
                }

                // 4. Upsert score for (player, game)
                Score score = scoreRepository.findByPlayerAndGame(player, game);
                if (score == null) {
                    score = Score.builder().player(player).game(game).score(scoreValue).build();
                } else {
                    score.setScore(scoreValue);
                }
                scoreRepository.save(score); // always save
            }
        }
    }
}
