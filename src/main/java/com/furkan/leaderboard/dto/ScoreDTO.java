package com.furkan.leaderboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ScoreDTO {
    private String player;
    private String game;
    private int score;
}