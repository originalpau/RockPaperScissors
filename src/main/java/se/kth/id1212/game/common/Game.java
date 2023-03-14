package se.kth.id1212.game.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Game extends Remote {
    public static final String GAME_NAME_IN_REGISTRY = "game";

    public void login(Client remoteNode, String name) throws RemoteException;
    public List<? extends PlayerDTO> listPlayers() throws RemoteException;
    public void exit(String name) throws RemoteException;
    public void start(String nameClient, String name) throws RemoteException;
    public void startRobotGame(String name, Client remoteObj) throws RemoteException;

    public void userInput(String input) throws RemoteException;
    public void endGame(String clientName) throws RemoteException;
}
