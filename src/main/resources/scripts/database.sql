CREATE TABLE game (
 id SERIAL PRIMARY KEY,
 time TIMESTAMP NOT NULL,
 round SMALLINT
);

CREATE TABLE player (
 id SERIAL PRIMARY KEY,
 name VARCHAR(32) NOT NULL,
  status boolean NOT NULL
);

CREATE TABLE player_game (
 player_id INT NOT NULL,
 game_id INT NOT NULL,
 score SMALLINT NOT NULL,

 PRIMARY KEY (player_id,game_id),
 FOREIGN KEY (player_id) REFERENCES player (id) ON DELETE CASCADE,
 FOREIGN KEY (game_id) REFERENCES game (id) ON DELETE CASCADE
);

INSERT INTO player(name, status) VALUES ('robot', true);