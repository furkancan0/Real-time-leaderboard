package com.furkan.leaderboard.repository;

import com.furkan.leaderboard.Entity.Game;
import com.furkan.leaderboard.Entity.Player;
import com.furkan.leaderboard.Entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Score findByPlayerAndGame(Player player, Game game);
}