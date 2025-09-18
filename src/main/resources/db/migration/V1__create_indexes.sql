CREATE TABLE games
(
    id     INT8         NOT NULL,
    name    VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE players
(
    id     INT8         NOT NULL,
    username    VARCHAR(255) NOT NULL,
    width  INT8,
    PRIMARY KEY (id)
);

CREATE TABLE score
(
    id               INT8       NOT NULL,
    score            INT8,
    player_id        INT8       NOT NULL REFERENCES players,
    game_id          INT8       NOT NULL REFERENCES games,
    PRIMARY KEY (id)
);

CREATE INDEX idx_score_game_score ON score (game_id, score DESC);

CREATE UNIQUE INDEX score_player_game_unique
    ON score (player_id, game_id);