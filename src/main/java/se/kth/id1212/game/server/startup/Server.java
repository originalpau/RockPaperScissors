package se.kth.id1212.game.server.startup;

import se.kth.id1212.game.common.Game;
import se.kth.id1212.game.server.controller.Controller;
import se.kth.id1212.game.server.integration.GameDAO;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Scanner;

public class Server {
    private String gameName = Game.GAME_NAME_IN_REGISTRY;

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startRMIServant();

        } catch (RemoteException | MalformedURLException  e) {
            System.out.println("Failed to start game server.");
        }
    }



    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller();

        Naming.rebind(gameName, contr);

        System.out.println("Game server started.");

        // Read input from the terminal and check for cancel command
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if(input.equals("info")){
                GameDAO.getAllInfo();


            if (input.equals("cancel")) {
                try {
                    contr.cancelGame();
                    System.out.println("Game canceled.");
                } catch (RemoteException e) {
                    System.out.println("Failed to cancel game.");
                }


                }
            }
        }
    }

}
