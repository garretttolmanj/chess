package server.handler;

import dataaccess.DataAccessException;
import server.response.ErrorResponse;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class ErrorHandler {

    public ErrorHandler() {
    }

    public String handleBadRequest(BadRequestException e, Request req, Response res) {
        res.type("application/json");
        res.status(400);
        return new Gson().toJson(new ErrorResponse(e.getMessage()));
    }

    public String handleUnauthorized(UnauthorizedException e, Request req, Response res) {
        res.type("application/json");
        res.status(401);
        return new Gson().toJson(new ErrorResponse(e.getMessage()));
    }

    public String handleAlreadyTaken(AlreadyTakenException e, Request req, Response res) {
        res.type("application/json");
        res.status(403);
        return new Gson().toJson(new ErrorResponse(e.getMessage()));
    }

    public String handleNotFound(Request req, Response res) {
        res.status(404);  // Not Found
        return new Gson().toJson(new ErrorResponse("Endpoint not found: " + req.pathInfo()));
    }

    public String handleDataAccess(DataAccessException e, Request req, Response res) {
        res.type("application/json");
        res.status(500);  // Server Error
        return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
    }

    public Object handleError(Exception e, Request req, Response res) {
        res.type("application/json");
        res.status(500);  // Server Error
        return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
    }
}
