package se.kth.id1212.game.server.controller;

import se.kth.id1212.game.common.Client;
import se.kth.id1212.game.common.Game;
import se.kth.id1212.game.common.PlayerDTO;
import se.kth.id1212.game.server.integration.GameDAO;
import se.kth.id1212.game.server.model.Player;
import se.kth.id1212.game.server.model.gameManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller extends UnicastRemoteObject implements Game {
    private final GameDAO gameDb;
    private final Map<String, Client> onlinePlayers = Collections.synchronizedMap(new HashMap<>());


    public Controller() throws RemoteException {
        super();
        gameDb = new GameDAO();
    }

    @Override
    public void login(Client remoteNode, String name) throws RemoteException {
        //save a Player
        onlinePlayers.put(name, remoteNode);
        gameDb.login(name);
    }

    @Override
    public List<? extends PlayerDTO> listPlayers() throws RemoteException {
        return gameDb.findAllOnlinePlayers();
    }

    @Override
    public void exit(String name) throws RemoteException {
        onlinePlayers.remove(name);
        gameDb.updateStatus(name, false);
    }

    @Override
    public synchronized void start(String nameAsking, String nameInvited) throws RemoteException {
        Client invitedPlayer = onlinePlayers.get(nameInvited);
        invitedPlayer.recvMsg(nameAsking + " has invited you to start a new game...");
    }

    @Override
    public synchronized void startRobotGame(String name, Client remoteObj) throws RemoteException {
        //mark player as not active in database
        gameDb.updateStatus(name, false);
        //fetch player and save its name/remoteNode in model.
        Player activePlayer = new Player(remoteObj, name);
        //send player to gameManager and start game.
        gameManager gameManager = new gameManager(activePlayer);
        gameManager.play();
    }

    public void userInput(String msg) {
        gameManager.recvUserInput(msg);
    }

    @Override
    public void endGame() throws RemoteException {
        //register in database;
        //updatePlayerStatus
    }

}
