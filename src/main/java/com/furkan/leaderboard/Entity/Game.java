package com.furkan.leaderboard.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "game",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Score> scores = new HashSet<>();

    @Column(name = "game_date", nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime gameDate = LocalDateTime.now();
}
