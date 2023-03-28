package se.kth.id1212.game.server.model;

import se.kth.id1212.game.client.view.Command;

import java.rmi.server.UnicastRemoteObject;
import java.util.Random;


/**
 * Keeps track of players in an active game, and is also responsible for sending
 * messages to players.
 */
public class gameManager {
    private final static String WELCOME_MESSAGE = "Welcome to this game - Rock Paper Scissors!\n";
    private final static String RULES = "\nRules:\n - Rock beats Scissors\n - Scissors beats Paper\n - Paper beats Rock\n";
    private final static String OPTIONS = "\nChoose an option to play: [rock, paper, scissors] or [quit]\n";
    static int point1 = 0;
    static int point2 = 0;
    static int rounds = 0;
    static Player activePlayer;

    public gameManager() {
    }

    public void cancelGame(){

        activePlayer.send("the admin canceled this game, you will be back to the menu!");
        for (Command command : Command.values()) {
           activePlayer.send(command.toString().toLowerCase());
        }


    }

    public void registerActivePlayer(Player activePlayer) {
        gameManager.activePlayer = activePlayer;
    }

    public void play() {
        activePlayer.send(WELCOME_MESSAGE + RULES + OPTIONS);
    }

    public static void recvUserInput(String userInput) {
        if (!GameSigns.contains(userInput)) {
            activePlayer.send(OPTIONS);
            return;
        }
        String result = "";
        String robotInput = GameSigns.getRandomSigns().toString();
        if (userInput.equals(robotInput)) {
            point1++;
            point2++;
            result = "DRAW";
        } else if (userInput.equals("rock") && robotInput.equals("scissors")) {
            point1++;
            result = "YOU WIN";
        } else if (userInput.equals("scissors") && robotInput.equals("rock")) {
            point2++;
            result = "YOU LOSE";
        } else if (userInput.equals("rock") && robotInput.equals("paper")) {
            point2++;
            result = "YOU LOSE";
        } else if (userInput.equals("paper") && robotInput.equals("rock")) {
            point1++;
            result = "YOU WIN";
        } else if (userInput.equals("scissors") && robotInput.equals("paper")) {
            point1++;
            result = "YOU WIN";
        } else if (userInput.equals("paper") && robotInput.equals("scissors")) {
            point2++;
            result = "YOU LOSE";
        }

        rounds++;
        String inputs = userInput + "[" + activePlayer.getName() + "] vs " + robotInput + "[robot] -> " + result + "\n";
        String stats = "ROUND " + rounds + "\n" + activePlayer.getName() + ": " + point1 + " points\nRobot: " + point2 + " points\n";
        activePlayer.send(inputs + stats);
    }

    public static int getPoint1() {
        return point1;
    }

    public static int getPoint2() {
        return point2;
    }

    public static int getRounds() {
        return rounds;
    }

    public static Player getActivePlayer() {
        return activePlayer;
    }

    private enum GameSigns {
        rock,
        paper,
        scissors;

        public static GameSigns getRandomSigns() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }






        public static boolean contains(String input) {
            for (GameSigns c : GameSigns.values()) {
                if (c.name().equals(input)) {
                    return true;
                }
            }
            return false;
        }

    }


}
