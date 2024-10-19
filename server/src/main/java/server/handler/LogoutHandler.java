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

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handleLogout(Request req, Response res) {
        ChessRequest logoutRequest = new ChessRequest();
        String authToken = req.headers("Authorization");
        logoutRequest.setAuthToken(authToken);

        ServerResponse logoutResponse = userService.logout(logoutRequest);
        return new Gson().toJson(logoutResponse);

    }
}
