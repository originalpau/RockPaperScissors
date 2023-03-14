package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class GameServer {
    private static GameDAO gameDB;
    static HashMap<Long, String> onlinePlayers = new HashMap<Long, String>();

    public static HashMap<Long,String> getOnlinePlayers() {
        if(onlinePlayers == null) {
            onlinePlayers = new HashMap<Long, String>();
        }
        return onlinePlayers;
    }

    public GameServer() {
        gameDB = new GameDAO();
    }

    public static GameDAO getGameDB() {
        if (gameDB == null) {
            gameDB = new GameDAO();
        }
        return gameDB;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5555);
        System.out.println("\nServer is up and running on port " + serverSocket.getLocalPort() + " ...");

        while (!serverSocket.isClosed()) {
            Socket player = serverSocket.accept();
            System.out.println("Client connected");

            Thread t = new ClientHandler(player, getGameDB(), getOnlinePlayers());
            t.start();
        }
    }
}

class ClientHandler extends Thread {
    GameDAO gameDB;
    private Socket firstPlayer;
    private final DataOutputStream OUT_TO_FIRST_PLAYER;
    private final BufferedReader IN_FROM_FIRST_PLAYER;
    String name_1;
    String name_2;
    int point1 = 0;
    int point2 = 0;
    int rounds = 0;
    private final String WIN = "YOU WIN";
    private final String LOSE = "YOU LOSE";
    private final String DRAW = "DRAW";
    private boolean keepReceivingCmds = false;
    HashMap<Long, String> onlinePlayers;

    public ClientHandler(Socket player, GameDAO gameDB, HashMap<Long, String> onlinePlayers) throws IOException {
        this.onlinePlayers = onlinePlayers;
        this.gameDB = gameDB;
        this.firstPlayer = player;
        OUT_TO_FIRST_PLAYER = new DataOutputStream(firstPlayer.getOutputStream());
        IN_FROM_FIRST_PLAYER = new BufferedReader(new InputStreamReader(firstPlayer.getInputStream()));
    }

    @Override
    public void run() {
        keepReceivingCmds = true;
        while(keepReceivingCmds) {
            String cmdLine;
            try {
                while ((cmdLine = IN_FROM_FIRST_PLAYER.readLine()) != null) {
                    //String cmdLine = IN_FROM_FIRST_PLAYER.readLine();
                    switch (cmdLine.toUpperCase()) {
                        case "LOGIN":
                            readName();
                            break;
                        case "START":
                            name_2 = "robot";
                            playWithRobot();
                            //playWithRobot();
                            //playWithRobot();
                            registerGame();
                            break;
                        case "ONLINE":
                            printToPlayer(OUT_TO_FIRST_PLAYER, onlinePlayers.toString());
                            break;
                        case "QUIT":
                            System.out.println("Client disconnected");
                            firstPlayer.close();
                            keepReceivingCmds = false;
                            break;
                        default:
                            System.out.println("illegal command");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readName() throws IOException {
        name_1 = IN_FROM_FIRST_PLAYER.readLine();
        onlinePlayers.put(Thread.currentThread().getId(), name_1);
    }

    public void playWithRobot() throws IOException {
        String robotInput = GameSigns.getRandomSigns().toString();
        String inputClient_1 = IN_FROM_FIRST_PLAYER.readLine();

        if (inputClient_1.equals(robotInput)) {
            point1++;
            point2++;
            printToPlayer(OUT_TO_FIRST_PLAYER, DRAW);
        } else if (inputClient_1.equals("R") && robotInput.equals("S")) {
            point1++;
            nrOneWins();
        } else if (inputClient_1.equals("S") && robotInput.equals("R")) {
            point2++;
            nrOneLose();
        } else if (inputClient_1.equals("R") && robotInput.equals("P")) {
            point2++;
            nrOneLose();
        } else if (inputClient_1.equals("P") && robotInput.equals("R")) {
            point1++;
            nrOneWins();
        } else if (inputClient_1.equals("S") && robotInput.equals("P")) {
            point1++;
            nrOneWins();
        } else if (inputClient_1.equals("P") && robotInput.equals("S")) {
            point2++;
            nrOneLose();
        }

        rounds++;
        String result = "ROUND " + rounds + "\nPlayer 1: " + point1 + " points\nPlayer 2: " + point2 + " points\n";
        printToPlayer(OUT_TO_FIRST_PLAYER, result);
    }

    private enum GameSigns {
        R,
        P,
        S;

        public static GameSigns getRandomSigns() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }


    private void nrOneWins() {
        printToPlayer(OUT_TO_FIRST_PLAYER, WIN);
    }

    private void nrOneLose() {
        printToPlayer(OUT_TO_FIRST_PLAYER, LOSE);
    }

    private void printToPlayer(DataOutputStream outToPlayer, String message) {
        try {
            outToPlayer.writeBytes(message + "\n");
            outToPlayer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean wantToPlay(String input1, String input2, DataOutputStream OUT_TO_FIRST_PLAYER, DataOutputStream OUT_TO_SECOND_PLAYER) throws IOException {
        if (input1.equals("yes") && input2.equals("yes")) {
            printToPlayer(OUT_TO_FIRST_PLAYER, "start");
            printToPlayer(OUT_TO_SECOND_PLAYER, "start");
            return true;
        } else {
            registerGame();
            printToPlayer(OUT_TO_FIRST_PLAYER, "quit");
            printToPlayer(OUT_TO_SECOND_PLAYER, "quit");
            return false;
        }
    }

    private void registerGame() {
        int gameId = gameDB.saveGame(rounds);
        gameDB.addPlayer(name_1);
        gameDB.addPlayer(name_2);
        gameDB.updatePlayerGame(name_1, gameId, point1);
        gameDB.updatePlayerGame(name_2, gameId, point2);
    }
}
