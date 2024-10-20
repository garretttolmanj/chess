package server;

import dataaccess.*;
import server.handler.*;
import server.request.ChessRequest;
import server.response.*;
import spark.*;
import com.google.gson.Gson;
import service.*;

public class Server {
    //Set up the Services and Data Access Objects
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserService userService = new UserService(new MemoryUserDAO(), authDAO);
    private final GameService gameService = new GameService(new MemoryGameDAO(), authDAO);

    //Set up handlers
    private final ClearHandler clearHandler;
    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final ListGamesHandler listGamesHandler;
    private final CreateGameHandler createGameHandler;
    private final ErrorHandler errorHandler;

    public Server() {
        this.clearHandler = new ClearHandler(userService, gameService);
        this.registerHandler = new RegisterHandler(userService);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.listGamesHandler = new ListGamesHandler(gameService);
        this.createGameHandler = new CreateGameHandler(gameService);
        this.errorHandler = new ErrorHandler();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler::handleClear);
        Spark.post("/user", registerHandler::handleRegister);
        Spark.post("/session", loginHandler::handleLogin);
        Spark.delete("/session", logoutHandler::handleLogout);
        Spark.get("/game", listGamesHandler::handleListGames);
        Spark.post("/game", createGameHandler::handleCreateGame);

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

        // This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
