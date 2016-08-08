package terminalExceptions;

public class TerminalException extends RuntimeException {
    private final String reason;

    public TerminalException(String message, Throwable cause, String reason) {
        super(message, cause);
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }
}
