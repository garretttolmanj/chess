package ui;

import requestResponse.ServerResponse;

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
                case "logout" -> logout();
//                case "login" -> login(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws RuntimeException {
        server.logout(authToken);
        repl.signOut();
        return String.format("Successful: User signed out.");
    }

    public String help() {
        return """
                - listGames
                - createGame <gameName>
                - playGame <gameName> <BLACK/WHITE>
                - observeGame <gameName>
                - logout
                """;
    }

}
