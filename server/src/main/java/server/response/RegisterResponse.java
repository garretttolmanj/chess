package server.response;

import java.util.Objects;

public class RegisterResponse {
    final String username;
    final String authToken;


    public RegisterResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }
    public String getAuthToken() {
        return authToken;
    }


    @Override
    public String toString() {
        return "RegisterResponse{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResponse that = (RegisterResponse) o;
        return Objects.equals(authToken, that.authToken) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }
}
