package ui;

import com.google.gson.Gson;
import requestResponse.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public ServerResponse register(String username, String password, String email) {
        var path = "/user";
        ChessRequest request = new ChessRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        return this.makeRequest("POST", path, request, ServerResponse.class);
    }

    public ServerResponse login(String username, String password) {
        var path = "/session";
        ChessRequest request = new ChessRequest();
        request.setUsername(username);
        request.setPassword(password);
        return this.makeRequest("POST", path, request, ServerResponse.class);
    }

    public ServerResponse logout(String authToken) {
        var path = "/session";
        ChessRequest request = new ChessRequest();
        request.setAuthToken(authToken);
        return this.makeRequest("DELETE", path, request, ServerResponse.class);
    }

//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        this.makeRequest("DELETE", path, null, null);
//    }

//    public void deleteAllPets() throws ResponseException {
//        var path = "/pet";
//        this.makeRequest("DELETE", path, null, null);
//    }

//    public Pet[] listPets() throws RuntimeException {
//        var path = "/pet";
//        record listPetResponse(Pet[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws RuntimeException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, RuntimeException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new RuntimeException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
