package requestresponse;

import model.GameInfo;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Object that gets translated to JSON after a successful service call.
 * It contains all the possible parameters with null default values.
 */

public class ServerResponse {
    private String username;
    private String authToken;
    private ArrayList<GameInfo> games;
    private Integer gameID;

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

    public ArrayList<GameInfo> getGames() {
        return games;
    }

    public void setGames(ArrayList<GameInfo> games) {
        this.games = games;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerResponse that = (ServerResponse) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(games, that.games) &&
                Objects.equals(gameID, that.gameID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken, games, gameID);
    }
}