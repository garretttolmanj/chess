package server.response;

import chess.ChessGame;

import java.util.ArrayList;


public class ServerResponse {
    private String username;
    private String authToken;
    private ArrayList games;
    private String gameID;

    public ServerResponse() {
        this.username = null;
        this.authToken = null;
        this.games = null;
        this.gameID = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ArrayList getGames() {
        return games;
    }

    public void setGames(ArrayList games) {
        this.games = games;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}