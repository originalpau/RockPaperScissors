package se.kth.id1212.game.client.view;

import se.kth.id1212.game.common.Client;
import se.kth.id1212.game.common.Game;
import se.kth.id1212.game.common.PlayerDTO;

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
                            play();
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
                    default:
                        outMgr.println("illegal command");
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
                outMgr.println(e.getMessage());
            }
        }
    }

    private void play() throws RemoteException {
        String input = console.nextLine();
        while(!input.equals("quit")) {
            game.userInput(input);
            input = console.nextLine();
        }
        game.endGame();
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
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
