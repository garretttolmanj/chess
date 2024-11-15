package ui;

import model.GameInfo;
import requestResponse.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import static ui.EscapeSequences.*;

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
                case "create" -> createGame(params);
                case "play" -> play(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws RuntimeException {
        if (params.length == 1) {
            String gameName = params[0];
            server.createGame(gameName, authToken);

            listGames();

            return String.format("Successful: Created game called " + gameName);
        }
        throw new RuntimeException("Expected: create <gameName>");
    }

    public String listGames() throws RuntimeException {
        ServerResponse response = server.listGames(authToken);
        ArrayList<GameInfo> gameList = response.getGames();
        gameIDs.clear();  // Clear the gameIDs list before adding updated game IDs

        String games = "";
        int i = 0;
        while (i < gameList.size()) {
            Integer gameID = gameList.get(i).gameID();
            String gameName = gameList.get(i).gameName();
            String whiteUsername = (gameList.get(i).whiteUsername() != null) ? gameList.get(i).whiteUsername() : "";
            String blackUsername = (gameList.get(i).blackUsername() != null) ? gameList.get(i).blackUsername() : "";
            games += "- " + (i + 1) + " " + gameName + " WHITE[" + whiteUsername + "] BLACK[" + blackUsername + "]\n";

            gameIDs.add(gameID);  // Add each game ID to the list
            i++;
        }

        return "Current games: \n" + "- ID Name Players \n" + games;
    }


    public String play(String... params) throws RuntimeException {
        if (params.length == 2) {
            try {
                int ID = Integer.parseInt(params[0]);
                int gameID = gameIDs.get(ID - 1);
                String color = params[1].toUpperCase();

                // Use .equals() to compare strings
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    return "Expected: play <ID> <WHITE OR BLACK> ";
                }

                server.joinGame(color, gameID, authToken);
                repl.joinGame(authToken, gameID, color);
                return SET_TEXT_COLOR_BLUE + "Successful: Joined game as " + color + " player";
            } catch (RuntimeException e) {
                if (e.getMessage().equals("failure 403: Forbidden")) {
                    return "Game already taken";
                }
                throw new RuntimeException("Expected: play <ID> <WHITE OR BLACK>");
            }
        }
        throw new RuntimeException("Expected: play <ID> <WHITE OR BLACK>");
    }


    public String observe(String... params) throws RuntimeException {
        if (params.length == 1) {
            try {
                int ID = Integer.parseInt(params[0]);
                int gameID = gameIDs.get(ID - 1);
                repl.observeGame(authToken, gameID);
                return SET_TEXT_COLOR_BLUE + "Successful: Joined game as an observer";
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                throw new RuntimeException("Expected: observe <ID>");
            }
        }
        throw new RuntimeException("Expected: observe <ID>");
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
