package service;

import dataaccess.*;
import model.*;
import server.request.LoginRequest;
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

    public void clear() throws DataAccessException {
        userAccess.clear();
        authAccess.clear();
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
     * @param loginRequest
     * @return AuthData
     */
    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException, ResponseException {
        UserData userData = userAccess.getUser(loginRequest.getUsername());

        if (userData.password().equals(loginRequest.getPassword())) {
            String token = UUID.randomUUID().toString();
            AuthData authData = new AuthData(token, loginRequest.getUsername());
            authAccess.createAuth(authData);

            return new LoginResponse(loginRequest.getUsername(), token);
        } else {
            throw new ResponseException("Password doesn't match!");
        }


    }
    public void logout(AuthData auth) {}
}
