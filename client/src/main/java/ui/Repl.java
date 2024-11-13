package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;
    private boolean signedIn;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
        signedIn = false;
    }

    public void signIn() {
        signedIn = true;
    }
    public void signOut() {
        signedIn = false;
    }

    public void run() {
        System.out.println(BLACK_KING + "Welcome to chess!! Sign in to start.");

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
        System.out.print("\n" + RESET_TEXT_COLOR + ">>>" + SET_TEXT_COLOR_BLUE);
    }
}
