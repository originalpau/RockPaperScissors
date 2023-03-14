package se.kth.id1212.game.common;

import java.io.Serializable;
import java.sql.Timestamp;

public interface GameDTO extends Serializable {
    public String getId();
    public Timestamp getTime();
    public int getRound();
    public String getName();
    public int getScore();
}
