package se.kth.id1212.game.server.model;

import se.kth.id1212.game.common.Client;
import se.kth.id1212.game.common.PlayerDTO;

import java.rmi.RemoteException;

public class Player implements PlayerDTO {
    private String id;
    private String name;
    private boolean status;
    private Client remoteNode;

    public Player(Client remoteNode, String name) {
        this.name = name;
        this.remoteNode = remoteNode;
    }

    public Player(String id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    public Client getRemoteNode() {return remoteNode; }

    /**
     * Send the specified message to the participant's remote node.
     *
     * @param msg The message to send.
     */
    public void send(String msg) {
        try {
            remoteNode.recvMsg(msg);
        } catch (RemoteException e) {
            System.out.println("Failed to deliver msg to " + name);
            e.printStackTrace();
        }

    }
}
