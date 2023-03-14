package se.kth.id1212.game.client.startup;

import se.kth.id1212.game.client.view.NonBlockingInterpreter;
import se.kth.id1212.game.common.Game;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static se.kth.id1212.game.common.Game.GAME_NAME_IN_REGISTRY;

public class Main {

    public static void main(String[] args) {
        try {
            Game game = (Game) Naming.lookup(GAME_NAME_IN_REGISTRY);
            new NonBlockingInterpreter().start(game);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Could not start game client.");
        }
    }
}
