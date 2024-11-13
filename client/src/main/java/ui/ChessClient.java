package ui;

import requestResponse.ServerResponse;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final Repl repl;

    public ChessClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
//                case "login" -> login(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws RuntimeException {
        if (params.length == 3) {
//            state = State.SIGNEDIN;
            String username = params[0];
            String password = params[1];
            String email = params[2];
            ServerResponse response = server.register(username, password, email);
            repl.signIn();
            return String.format("Successful: Signed in as " + username);
        }
        throw new RuntimeException("Expected: <yourname> <password> <email>");
    }

    public String help() {

        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }
}
