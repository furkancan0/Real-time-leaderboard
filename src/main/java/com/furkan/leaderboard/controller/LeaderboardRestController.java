package com.furkan.leaderboard.controller;

import com.furkan.leaderboard.Services.LeaderboardService;
import com.furkan.leaderboard.dto.ScoreDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LeaderboardRestController {

    private final LeaderboardService service;

    public LeaderboardRestController(LeaderboardService service) {
        this.service = service;
    }

    @GetMapping("/{game}")
    public List<ScoreDTO> getLeaderboard(@PathVariable String game,
                                         @RequestParam(defaultValue = "10") int limit) {
        return service.getLeaderboard(game, limit);
    }

    @PostMapping("/{game}")
    public List<ScoreDTO> updateScore(@PathVariable String game,
                                         @RequestBody ScoreDTO scoreDTO) {
        return service.updateScore(scoreDTO.getPlayer(), game, scoreDTO.getScore());
    }

    @GetMapping("/{game}/rank/{player}")
    public Long getRanking(@PathVariable String game, @PathVariable String player) {
        return service.getRanking(player, game);
    }

    @GetMapping("/{game}/highest")
    public ScoreDTO getHighestScore(@PathVariable String game) {
        return service.getHighestScore(game);
    }
}