package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameClient {
    private final static String WELCOME_MESSAGE = "Welcome to this Rock Paper Scissors game!\n";
    private final static String RULES = "\nRules:\n - Rock beats Scissors\n - Scissors beats Paper\n - Paper beats Rock\n";
    private final static String MENU = "You have 5 options, enter the option to choose: login, start, rules, online and quit\n";
    private static String host = "localhost";
    private static Integer port = 5555;

    public static void main(String[] args) throws UnknownHostException, IOException {
        System.out.print(WELCOME_MESSAGE);
        Scanner scanner = new Scanner(System.in);
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(host, port);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        boolean keepSendingCmds = true;

        System.out.println(MENU);
        while(keepSendingCmds) {
            String input = inFromUser.readLine();
            switch(input.toLowerCase()) {
                case "login":
                    outToServer.writeBytes("login" + "\n");
                    outToServer.flush();
                    System.out.println("What's your name?");
                    String name = scanner.nextLine();
                    outToServer.writeBytes(name + "\n");
                    outToServer.flush();
                    break;
                case "online":
                    outToServer.writeBytes("online" + "\n");
                    outToServer.flush();
                    break;
                case "start":
                    outToServer.writeBytes("start" + "\n");
                    outToServer.flush();
                    play(inFromUser, outToServer, inFromServer, clientSocket);
                    //play(inFromUser, outToServer, inFromServer, clientSocket);
                    //play(inFromUser, outToServer, inFromServer, clientSocket);
                    break;
                case "quit":
                    //outToServer.writeBytes("quit" + "\n");
                    //outToServer.flush();
                    keepSendingCmds = false;
                    break;
                default:
                    System.out.println(MENU);
            }
        }
        clientSocket.close();


/*         String message = "";
        while((message = inFromServer.readLine()) != null) {
            if(message.equals("start")) {
                play(inFromUser, outToServer, inFromServer, clientSocket);
            }
            if(message.equals("quit")) {
                System.out.println("At least one player left this game...Quitting...");
                clientSocket.close();
                break;
            }
        } */
    }

    private static void play(BufferedReader inFromUser, DataOutputStream outToServer, BufferedReader inFromServer, Socket clientSocket) throws IOException {
        String input = "";
        String response;
        do {
            if (input.equals("-rules")) {
                System.out.println(RULES);
            }
            help();
            input = inFromUser.readLine();

        } while (!(input.equals("R") || input.equals("P") || input.equals("S")));

        // Transmit input to the server and provide some feedback for the user
        outToServer.writeBytes(input + "\n");
        outToServer.flush();
        System.out.println("\nYour input (" + input +") was successfully transmitted to the server. " +
                "The result is ...");

        // Catch response
        response = inFromServer.readLine();

        // Display response
        System.out.println("Response from server: " + response);
        // input = playAgain(inFromUser);
        // outToServer.writeBytes(input + "\n");
        // outToServer.flush();
    }

    private static String playAgain(BufferedReader inFromUser) throws IOException {
        String input = "";
        System.out.println("Play again? Answer yes or no");
        input = inFromUser.readLine();
        if (!(input.equals("yes") || input.equals("no"))) {
            playAgain(inFromUser);
        }
        return input;
    }

    private static void help() {
        System.out.println("\nSelect (R)ock, (P)aper, (S)cissors" +
                " or type \"-rules\": ");
    }
}