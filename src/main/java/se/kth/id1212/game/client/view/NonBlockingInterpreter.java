package se.kth.id1212.game.client.view;

import se.kth.id1212.game.common.Client;
import se.kth.id1212.game.common.Game;
import se.kth.id1212.game.common.GameDTO;
import se.kth.id1212.game.common.PlayerDTO;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;


/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private Game game;
    private boolean receivingCmds = false;
    private final Client myRemoteObj;

    public NonBlockingInterpreter() throws RemoteException {
        myRemoteObj = new ConsoleOutput();
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     *
     */
    public void start(Game game) {
        System.out.println("Welcome! type help to see options. Here is what each option does: \n whoami: shows who is logged in \n login: type login followed by your name \n play: start playing. \n online: shows online players. \n history: shows history of games \n help: use help to see the menu \n exit: use this to leave the game \n Good Luck!") ;
        this.game = game;
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        String clientName = null;
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case WHOAMI:
                        if (clientName != null) {
                            outMgr.println("You are logged in as [" + clientName + "]");
                        } else {
                            outMgr.println("Not logged in");
                        }
                        break;
                    case HELP:
                        for (Command command : Command.values()) {
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case LOGIN:
                        if (clientName != null) {
                            outMgr.println("You are logged in as [" + clientName + "]");
                        } else if (cmdLine.getParameter(0) == null) {
                            clientName = "guest";
                            game.login(myRemoteObj, clientName);
                        } else {
                            clientName = cmdLine.getParameter(0);
                            game.login(myRemoteObj, clientName);
                        }
                        break;


                    case EXIT:
                        receivingCmds = false;
                        if (clientName != null) game.exit(clientName);
                        UnicastRemoteObject.unexportObject(myRemoteObj, false);
                        break;
                    case ONLINE:
                        List<? extends PlayerDTO> players = game.listPlayers();
                        for (PlayerDTO player : players) {
                            outMgr.println(player.getName() + ": " + player.getStatus());
                        }
                        break;
                    case PLAY:
                        if (clientName != null) {
                            game.startRobotGame(clientName, myRemoteObj);
                            play(clientName);
                        } else {
                            outMgr.println("Not logged in");
                        }
                        /*
                        if (cmdLine.getParameter(0) == null) {
                            game.startRobotGame(clientName, myRemoteObj);
                        } else {
                            game.start(clientName, cmdLine.getParameter(0));
                        }
                         */
                        break;
                    case HISTORY:
                        if (clientName != null) {
                            List<? extends GameDTO> gameHistory = game.history(clientName);
                            for (GameDTO game : gameHistory) {
                                outMgr.println(game.toString());
                            }
                        } else {
                            outMgr.println("Not logged in");
                        }
                        break;
                    default:
                        outMgr.println("illegal command");
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
                outMgr.println(e.getMessage());
            }
        }
    }

    private void play(String clientName) throws RemoteException {
        String input = console.nextLine();
        while(!input.equals("quit")) {
            game.userInput(input);
            input = console.nextLine();
        }
        game.endGame(clientName);
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    public void executeCommand(Command command) throws RemoteException {
        String clientName = null;


        switch (command) {
            case EXIT:
                receivingCmds = false;
                if (clientName != null) game.exit(clientName);
                UnicastRemoteObject.unexportObject(myRemoteObj, false);
                break;
        }
    }

    private class ConsoleOutput extends UnicastRemoteObject implements Client {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void recvMsg(String msg) {
            outMgr.println((String) msg);
        }
    }
}
