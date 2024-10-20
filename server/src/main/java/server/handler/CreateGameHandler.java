package server.handler;

import com.google.gson.Gson;
import server.request.ChessRequest;
import server.response.ServerResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleCreateGame(Request req, Response res) {
        // Get the request
        var createGameRequest = new Gson().fromJson(req.body(), ChessRequest.class);
        String authToken = req.headers("Authorization");
        createGameRequest.setAuthToken(authToken);

        ServerResponse createGameResponse = gameService.createGame(createGameRequest);
        return new Gson().toJson(createGameResponse);

    }
}
