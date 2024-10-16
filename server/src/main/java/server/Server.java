package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import server.request.LoginRequest;
import server.response.LoginResponse;
import server.response.LoginResponseError;
import server.response.ResponseException;
import spark.*;
import com.google.gson.Gson;
import model.*;
import server.handler.*;
import service.*;

public class Server {
    private final UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    public Server() {
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/session", this::login);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    /**
     * Gson fromJson body
     * Initialize UserService
     * Pass body to UserService
     * Receive Response
     * Serialize to JSON
     * Return HTTP Response
     *
     */
    private Object login(Request req, Response res) throws ResponseException, DataAccessException {
        try {
            var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResponse loginResponse = userService.login(loginRequest);
            return new Gson().toJson(loginResponse);
        } catch (DataAccessException e) {
            // Set the response status to indicate a server error (500)
            res.status(500);

            // Return a JSON-formatted error message
            return new Gson().toJson(new LoginResponseError("DataAccessException", e.getMessage()));

        } catch (ResponseException e) {
            // Set the response status to indicate a bad request (400)
            res.status(400);

            // Return a JSON-formatted error message
            return new Gson().toJson(new LoginResponseError("ResponseException", e.getMessage()));
        }

    }
}
