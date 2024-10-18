package service;

import dataaccess.*;
import model.*;
import server.request.ChessRequest;
import server.response.ResponseException;
import server.response.ServerResponse;

import java.util.UUID;

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public void clear() {
        userAccess.clear();
        authAccess.clear();
    }

    public ServerResponse register(ChessRequest registerRequest) throws DataAccessException {
        UserData userData = new UserData(registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail());
        if (userAccess.getUser(registerRequest.getUsername()) == null) {
            userAccess.createUser(userData);
            String token = UUID.randomUUID().toString();
            AuthData authData = new AuthData(token, registerRequest.getUsername());
            authAccess.createAuth(authData);
            ServerResponse registerResponse = new ServerResponse();
            registerResponse.setUsername(registerRequest.getUsername());
            registerResponse.setAuthToken(token);
            return registerResponse;
        } else {
            throw new DataAccessException("Error: already taken");
        }
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
    public ServerResponse login(ChessRequest loginRequest) throws DataAccessException, ResponseException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        UserData userData = userAccess.getUser(username);

        if (userData.password().equals(password)) {
            String token = UUID.randomUUID().toString();
            AuthData authData = new AuthData(token, username);
            authAccess.createAuth(authData);
            ServerResponse loginResponse = new ServerResponse();
            loginResponse.setUsername(username);
            loginResponse.setAuthToken(token);
            return loginResponse;
        } else {
            throw new ResponseException("Error: unauthorized");
        }


    }
    public void logout(AuthData auth) {}


    public String getUserAccess() {
        return userAccess.toString();
    }

    public String getAuthAccess() {
        return authAccess.toString();
    }
}
