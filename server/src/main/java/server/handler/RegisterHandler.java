package server.handler;

import dataaccess.DataAccessException;
import requestResponse.*;
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