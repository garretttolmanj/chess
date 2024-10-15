package server.response;

import spark.Request;

public class LoginResponse {
    final String authToken;
    final String username;


    public LoginResponse(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;

    }

    public String getUsername() {
        return username;
    }
    public String getAuthToken() {
        return authToken;
    }


    @Override
    public String toString() {
        return "LoginResponse{" +
                ", authToken='" + authToken + '\'' +
                "username='" + username + '\'' +
                '}';
    }
}
