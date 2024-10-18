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

public class RegisterHandler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }
    public Object handleRegister(Request req, Response res) throws DataAccessException {
        var registerRequest = new Gson().fromJson(req.body(), ChessRequest.class);
        ServerResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
    }


}
