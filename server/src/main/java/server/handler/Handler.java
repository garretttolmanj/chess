package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresponse.*;
import spark.Request;
import spark.Response;

// Handler Base Class. The Handler subclasses use this logic to translate JSON and call services.
public abstract class Handler {
    protected abstract ServerResponse handleRequest(ChessRequest request) throws DataAccessException;

    public Object handleRequest(Request req, Response res) throws DataAccessException {
        String body = req.body();
        String authToken = req.headers("Authorization");
        if (body == null || body.isEmpty()) {
            ChessRequest request = new ChessRequest();
            request.setAuthToken(authToken);
            ServerResponse response = handleRequest(request);
            return new Gson().toJson(response);
        } else {
            var chessRequest = new Gson().fromJson(req.body(), ChessRequest.class);
            if (authToken == null || authToken.isEmpty()) {
                ServerResponse response = handleRequest(chessRequest);
                return new Gson().toJson(response);
            } else {
                chessRequest.setAuthToken(authToken);
                ServerResponse response = handleRequest(chessRequest);
                return new Gson().toJson(response);
            }
        }
    }
}
