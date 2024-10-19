package server.response;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerResponse that = (ServerResponse) o;
        return Objects.equals(username, that.username) && Objects.equals(authToken, that.authToken) && Objects.equals(games, that.games) && Objects.equals(gameID, that.gameID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken, games, gameID);
    }
}