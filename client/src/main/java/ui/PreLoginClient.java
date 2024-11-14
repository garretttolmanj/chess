package ui;

import requestResponse.ServerResponse;

import java.util.Arrays;

public class PreLoginClient implements Client {
    private final ServerFacade server;
    private final Repl repl;

    public PreLoginClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws RuntimeException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            ServerResponse response = server.register(username, password, email);
            String authToken = response.getAuthToken();
            repl.signIn(authToken);
            return String.format("Successful: Signed in as " + username);
        }
        throw new RuntimeException("Expected: <yourname> <password> <email>");
    }

    public String login(String... params) throws RuntimeException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            ServerResponse response = server.login(username, password);
            String authToken = response.getAuthToken();
            repl.signIn(authToken);
            return String.format("Successful: Signed in as " + username);
        }
        throw new RuntimeException("Expected: login <username> <password>");
    }

    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }
}
