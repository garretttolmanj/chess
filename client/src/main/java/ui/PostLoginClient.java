package ui;

import model.GameInfo;
import requestResponse.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PostLoginClient implements Client{
    private final ServerFacade server;
    private final Repl repl;
    private final String authToken;
    private ArrayList<Integer> gameIDs;

    public PostLoginClient(String serverUrl, Repl repl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
        this.gameIDs = new ArrayList<>();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> listGames();
                case "logout" -> logout();
                case "create" -> createGame(params);
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws RuntimeException {
        if (params.length == 1) {
            String gameName = params[0];
            ServerResponse response = server.createGame(gameName, authToken);
            return String.format("Successful: Created game called " + gameName);
        }
        throw new RuntimeException("Expected: create <gameName>");
    }

    public String listGames() throws RuntimeException {
        ServerResponse response = server.listGames(authToken);
        ArrayList<GameInfo> gameList = response.getGames();
        String games = "";

        int i = 0;
        while (i < gameList.size()) {
            Integer gameID = gameList.get(i).gameID();
            String gameName = gameList.get(i).gameName();
            String whiteUsername = (gameList.get(i).whiteUsername() != null) ? gameList.get(i).whiteUsername() : "";
            String blackUsername = (gameList.get(i).blackUsername() != null) ? gameList.get(i).blackUsername() : "";
            games += "- " + (i + 1) + " " + gameName + " WHITE[" + whiteUsername + "] BLACK[" + blackUsername +"]" + "\n";
            gameIDs.add(gameID);
            i++;
        }

        return "Current games: \n" + "- ID Name Players \n" + games;
    }



    public String logout() throws RuntimeException {
        server.logout(authToken);
        repl.signOut();
        return "Successful: User signed out";
    }

    public String help() {
        return """
                - list
                - create <gameName>
                - play <ID> <BLACK/WHITE>
                - observe <ID>
                - logout
                """;
    }

}
