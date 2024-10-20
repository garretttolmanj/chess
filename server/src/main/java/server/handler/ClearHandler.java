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

public class ClearHandler {
    private final UserService userService;
    private final GameService gameService;
    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }
    public Object handleClear(Request req, Response res) {
        gameService.clear();
        ServerResponse clearResponse = userService.clear();
        return new Gson().toJson(clearResponse);
    }
}
