package server.handler;

import dataaccess.DataAccessException;
import server.request.ChessRequest;
import server.response.ErrorResponse;
import server.response.ResponseException;
import server.response.ServerResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class ClearHandler {
    private final UserService userService;

    public ClearHandler(UserService userService) {
        this.userService = userService;
    }
    public Object handleClear(Request req, Response res) throws DataAccessException {
        userService.clear();
        return new Gson().toJson(new Object());
    }
}
