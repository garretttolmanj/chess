package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private Client client;
    private final String serverUrl;


    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new PreLoginClient(serverUrl, this);
    }

    public void signIn(String authToken) {
        client = new PostLoginClient(serverUrl, this, authToken); // Switch to post-login client
    }


    public void signOut() {
        client = new PreLoginClient(serverUrl, this); // Switch back to pre-login client
    }

    // Switch to game client which implements websocket functionality.
    public void joinGame(String authToken, Integer gameID, String teamColor) {
        client = new GameClient(serverUrl, this, authToken, gameID, teamColor);
    }


    public void observeGame(String authToken, Integer gameID) {
        client = new GameClient(serverUrl, this, authToken, gameID, null);
    }

    public void run() {
        System.out.println(BLACK_KING + "Welcome to chess! Sign in to start.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            ChessGame chessGame = notification.getChessGame();
            if (chessGame == null) {
                System.err.println("Error: ChessGame is null in LOAD_GAME notification.");
            } else {
                ((GameClient) client).loadGame(chessGame);
                System.out.println(client.eval("drawBoard"));
            }
        } else {
            System.out.println("Message from Server");
        }
        printPrompt();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }

}
