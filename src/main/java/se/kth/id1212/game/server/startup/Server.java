package se.kth.id1212.game.server.startup;

import se.kth.id1212.game.common.Game;
import se.kth.id1212.game.server.controller.Controller;
import se.kth.id1212.game.server.integration.GameDAO;



import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Server {
    private String gameName = Game.GAME_NAME_IN_REGISTRY;

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startRMIServant();

        } catch (RemoteException | MalformedURLException | SQLException e) {
            System.out.println("Failed to start game server.");
        }
    }



    private void startRMIServant() throws RemoteException, MalformedURLException, SQLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller();

        Naming.rebind(gameName, contr);

        System.out.println("Game server started.");

        System.out.println("As an admin you have some options:\n info: getting all the information regarding games and " +
                "players stored in database\n delete players: removing all the rows in players table.\n " +
                "delete history: removing rows in player_game table\n if you don't get any responses, the tables are " +
                "either empty or your command is illegal!");

        // Read input from the terminal and check for cancel command
        Scanner scanner = new Scanner(System.in);
        while (true) {


            String input = scanner.nextLine();



            if (input.equals("info")) {

                try {
                    GameDAO.getAllInfo();
                } catch (Exception e) {
                    System.out.println("could not show info");
                }}
                if (input.equals("delete players")) {

                    try {


                        GameDAO.deletePlayers();
                    } catch (Exception e) {
                        System.out.println("could not perform deletion");
                    }


                }
            if (input.equals("delete history")) {

                try {


                    GameDAO.deleteHistory();
                } catch (Exception e) {
                    System.out.println("could not perform deletion");
                }


            }


            }
        }
    }


