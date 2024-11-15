package server.handler;

import dataaccess.DataAccessException;
import requestresponse.*;
import service.UserService;

public class LoginHandler extends Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) throws DataAccessException {
        return userService.login(request);
    }
}

