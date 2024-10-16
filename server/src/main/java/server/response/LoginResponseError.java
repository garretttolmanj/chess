package server.response;

public class LoginResponseError {
    private final String errorType;
    private final String message;

    public LoginResponseError(String errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }
}
