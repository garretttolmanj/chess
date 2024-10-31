package server;

import com.google.gson.Gson;
import dataaccess.*;
import server.handler.*;
import server.response.ErrorResponse;
import service.*;
import spark.Spark;

public class Server {
    //Set up the Services and Data Access Objects
    private final AuthDAO authDAO = new SqlAuthDAO();
    private final UserService userService = new UserService(new SqlUserDAO(), authDAO);
    private final GameService gameService = new GameService(new SqlGameDAO(), authDAO);

    //Set up handlers
    private final ClearHandler clearHandler;
    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final ListGamesHandler listGamesHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;

    public Server() {
        this.clearHandler = new ClearHandler(userService, gameService);
        this.registerHandler = new RegisterHandler(userService);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.listGamesHandler = new ListGamesHandler(gameService);
        this.createGameHandler = new CreateGameHandler(gameService);
        this.joinGameHandler = new JoinGameHandler(gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Chess Endpoints
        Spark.delete("/db", clearHandler::handleRequest);
        Spark.post("/user", registerHandler::handleRequest);
        Spark.post("/session", loginHandler::handleRequest);
        Spark.delete("/session", logoutHandler::handleRequest);
        Spark.get("/game", listGamesHandler::handleRequest);
        Spark.post("/game", createGameHandler::handleRequest);
        Spark.put("/game", joinGameHandler::handleRequest);

        // Exception Handling
        Spark.exception(BadRequestException.class, (e, req, res) -> {
            res.status(400);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(UnauthorizedException.class, (e, req, res) -> {
            res.status(401);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(AlreadyTakenException.class, (e, req, res) -> {
            res.status(403);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(DataAccessException.class, (e, req, res) -> {
            res.status(500);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.notFound((req, res) -> {
            res.type("application/json");  // Ensure the response is set to JSON
            res.status(404);
            return new Gson().toJson(new ErrorResponse("Error: path not found"));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
