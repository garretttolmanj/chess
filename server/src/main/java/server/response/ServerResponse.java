package server.response;

import chess.ChessGame;

public class ServerResponse {
    private String username;
    private String password;
    private String authToken;
    private ChessGame game;

    public ServerResponse() {
        this.username = null;
        this.password = null;
        this.authToken = null;
        this.game = null;
    }

    // Getters and setters
    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public ChessGame getGame() { return game; }
    public void setGame(ChessGame game) { this.game = game; }
}