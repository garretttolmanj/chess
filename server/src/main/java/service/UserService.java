package service;

import dataaccess.*;
import model.*;
import server.response.LoginResponse;
import server.response.ResponseException;

import java.util.UUID;

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public AuthData register(UserData user) {
        return null;
    }

    /**
     * Receives a userData object from the handler
     * Initializes a UserDAO
     * Calls the UserDAO, getUser method
     * Verify password,
     * create AuthData object
     * Initialize AuthDAO
     * Use AuthDAO Add AuthData
     * Creates a Result Object
     *
     * @param user
     * @return AuthData
     */
    public LoginResponse login(UserData user) throws DataAccessException, ResponseException {
        UserData userData = userAccess.getUser(user.username());

        if (userData.password().equals(user.password())) {
            String token = UUID.randomUUID().toString();
            AuthData authData = new AuthData(token, user.username());
            authAccess.createAuth(authData);

            return new LoginResponse(token, user.username());
        } else {
            throw new ResponseException("Password doesn't match!");
        }


    }
    public void logout(AuthData auth) {}
}
