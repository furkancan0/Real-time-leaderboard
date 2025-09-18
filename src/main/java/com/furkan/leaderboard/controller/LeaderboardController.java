package com.furkan.leaderboard.controller;

import com.furkan.leaderboard.Entity.Score;
import com.furkan.leaderboard.Services.LeaderboardService;
import com.furkan.leaderboard.dto.ScoreDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class LeaderboardController {
    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) {
        this.service = service;
    }

    @MessageMapping("/updateScore")
    @SendTo("/topic/leaderboard")
    public List<ScoreDTO> updateScore(ScoreDTO newScore) {
        return service.updateScore(newScore.getPlayer(), newScore.getGame(), newScore.getScore());
    }

    @MessageMapping("/getRanking")
    @SendTo("/topic/ranking")
    public Long getRanking(ScoreDTO request) {
        return service.getRanking(request.getPlayer(), request.getGame());
    }

    @MessageMapping("/getHighestScore")
    @SendTo("/topic/highestScore")
    public ScoreDTO getHighestScore(ScoreDTO request) {
        return service.getHighestScore(request.getGame());
    }
}