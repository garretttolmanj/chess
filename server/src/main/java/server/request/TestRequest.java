package server.request;

public record TestRequest(String username,
                          String password,
                          String email,
                          String authToken) {
}
