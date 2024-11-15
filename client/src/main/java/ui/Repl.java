package ui;

import chess.ChessGame;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
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

    public void joinGame(String authToken, Integer gameID, String teamColor) {
        client = new GameClient(serverUrl, this, authToken, gameID, teamColor);

        // Use .equals() for string comparison instead of ==
        if (teamColor.equals("BLACK")) {
            System.out.print(client.eval("drawBlack"));
        } else if (teamColor.equals("WHITE")) {
            System.out.print(client.eval("drawWhite"));
        } else {
            // Optional: Handle the case when teamColor is neither "BLACK" nor "WHITE"
            System.out.print("Invalid team color: " + teamColor);
        }
    }


    public void observeGame(String authToken, Integer gameID) {
        client = new GameClient(serverUrl, this, authToken, gameID, null);
        System.out.print(client.eval("drawBlack"));
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

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }
}
