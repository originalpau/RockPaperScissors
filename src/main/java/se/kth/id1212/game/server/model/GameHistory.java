package se.kth.id1212.game.server.model;

import se.kth.id1212.game.common.GameDTO;

import java.sql.Timestamp;

public class GameHistory implements GameDTO {
    private String id;
    private Timestamp time;
    private int round;
    private String name;
    private int score;

    public GameHistory(String id, Timestamp time, int round, String name, int score) {
        this.id = id;
        this.time = time;
        this.round = round;
        this.name = name;
        this.score = score;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Timestamp getTime() {
        return time;
    }

    @Override
    public int getRound() {
        return round;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", round=" + round +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
