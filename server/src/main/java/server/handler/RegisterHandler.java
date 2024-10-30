package server.handler;

import dataaccess.DataAccessException;
import server.request.ChessRequest;
import server.response.ServerResponse;
import service.UserService;

public class RegisterHandler extends Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) throws DataAccessException {
        return userService.register(request);
    }
}