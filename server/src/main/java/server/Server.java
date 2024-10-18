package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import server.request.ChessRequest;
import server.response.*;
import spark.*;
import com.google.gson.Gson;
import service.*;

public class Server {
    private final UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    public Server() {
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.post("/test", this::test);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private Object clear(Request req, Response res) throws DataAccessException {
        userService.clear();
        return new Gson().toJson(new Object());
    }

    private Object test(Request req, Response res) {

        ChessRequest chessRequest = new Gson().fromJson(req.body(), ChessRequest.class);

        String authToken = req.headers("Authorization");
        ServerResponse response = new ServerResponse();
        response.setUsername(chessRequest.getUsername());
        response.setAuthToken(authToken);

        return new Gson().toJson(response);
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var registerRequest = new Gson().fromJson(req.body(), ChessRequest.class);
        ServerResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
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
            var loginRequest = new Gson().fromJson(req.body(), ChessRequest.class);
            ServerResponse loginResponse = userService.login(loginRequest);
            return new Gson().toJson(loginResponse);
        } catch (DataAccessException e) {
            // Set the response status to indicate a server error (500)
            res.status(401);
            // Return a JSON-formatted error message
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        } catch (ResponseException e) {
            // Set the response status to indicate a bad request (400)
            res.status(401);
            // Return a JSON-formatted error message
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }

    }
}
