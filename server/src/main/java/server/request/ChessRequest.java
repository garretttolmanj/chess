package server.request;

public class ChessRequest {
    private String username;
    private String password;
    private String email;
    private String authToken;
    private String gameName;
    private String playerColor;
    private Integer gameID;

    public ChessRequest() {
        this.username = null;
        this.password = null;
        this.email = null;
        this.authToken = null;
        this.gameName = null;
        this.playerColor = null;
        this.gameID = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return "ChessRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", authToken='" + authToken + '\'' +
                ", gameName='" + gameName + '\'' +
                ", playerColor='" + playerColor + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}
