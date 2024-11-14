package ui;

import model.GameInfo;
import requestResponse.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;

public class PostLoginClient implements Client{
    private final ServerFacade server;
    private final Repl repl;
    private final String authToken;

    public PostLoginClient(String serverUrl, Repl repl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> listGames();
                case "logout" -> logout();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String listGames() throws RuntimeException {
        ServerResponse response = server.listGames(authToken);
        ArrayList<GameInfo> gameList = response.getGames();
        String games = "";

        int i = 0;
        while (i < gameList.size()) {
            String gameName = gameList.get(i).gameName();
            games.concat("-" + i+1 + gameName + "\n");
            i++;
        }

        return String.format("Current games: \n" + games);
    }

    public String logout() throws RuntimeException {
        server.logout(authToken);
        repl.signOut();
        return "Successful: User signed out.";
    }

    public String help() {
        return """
                - list
                - create <gameName>
                - play <gameName> <BLACK/WHITE>
                - observe <gameName>
                - logout
                """;
    }

}
