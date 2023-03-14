package se.kth.id1212.game.common;

import java.io.Serializable;

public interface PlayerDTO extends Serializable {

    public String getName();

    /**
     * @return The status of player, if he/she is playing at the moment.
     */
    public boolean getStatus();
}
