package com.furkan.leaderboard.repository;

import com.furkan.leaderboard.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByName(String name);
}