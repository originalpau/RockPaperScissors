package se.kth.id1212.game.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Callback method so server has a reference to send messages to player.
 */
public interface Client extends Remote {
    /**
     * The specified message is received by the client.
     *
     * @param msg The message that shall be received.
     */
    void recvMsg(String msg) throws RemoteException;
}
