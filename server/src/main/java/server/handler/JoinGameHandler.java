package server.handler;

import com.google.gson.Gson;
import server.request.ChessRequest;
import server.response.ServerResponse;
import service.GameService;
import spark.Request;
import spark.Response;
public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleJoinGame(Request req, Response res) {
        // Get the request
        var joinGameRequest = new Gson().fromJson(req.body(), ChessRequest.class);
        String authToken = req.headers("Authorization");
        joinGameRequest.setAuthToken(authToken);

        ServerResponse createGameResponse = gameService.joinGame(joinGameRequest);
        return new Gson().toJson(createGameResponse);
    }
}
