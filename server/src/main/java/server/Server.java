package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import server.handler.ClearHandler;
import server.handler.LoginHandler;
import server.handler.RegisterHandler;
import server.request.ChessRequest;
import server.response.*;
import spark.*;
import com.google.gson.Gson;
import service.*;

public class Server {
    private final UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    private final LoginHandler loginHandler;
    private final RegisterHandler registerHandler;
    private final ClearHandler clearHandler;

    public Server() {
        this.loginHandler = new LoginHandler(userService);
        this.registerHandler = new RegisterHandler(userService);
        this.clearHandler = new ClearHandler(userService);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler::handleClear);
        Spark.post("/user", registerHandler::handleRegister);
        Spark.post("/session", loginHandler::handleLogin);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
//    private void exceptionHandler(ResponseException ex, Request req, Response res) {
//        res.status(ex.StatusCode());
//    }
}
