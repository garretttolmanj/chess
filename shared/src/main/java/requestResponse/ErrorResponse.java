package requestResponse;

// Object that gets translated to JSON when an Exception is being handled.
public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
