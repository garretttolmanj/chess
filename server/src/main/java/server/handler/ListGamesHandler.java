package server.handler;

import dataaccess.DataAccessException;
import server.request.ChessRequest;
import server.response.ErrorResponse;
import server.response.ResponseException;
import server.response.ServerResponse;
import service.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleListGames(Request req, Response res) {
        // Get the request
        ChessRequest listGamesRequest = new ChessRequest();
        String authToken = req.headers("Authorization");
        listGamesRequest.setAuthToken(authToken);

        ServerResponse listGamesResponse = gameService.listGames(listGamesRequest);
        return new Gson().toJson(listGamesResponse);

    }
}
