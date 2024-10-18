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

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handleLogin(Request req, Response res) {
        try {
            var loginRequest = new Gson().fromJson(req.body(), ChessRequest.class);
            ServerResponse loginResponse = userService.login(loginRequest);
            return new Gson().toJson(loginResponse);
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        } catch (ResponseException e) {
            res.status(400);
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }
    }
}
