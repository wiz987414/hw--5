package terminalExceptions;

public class CheckAccountException extends TerminalException {

    public CheckAccountException(String message, Throwable cause, String reason) {

        super(message, cause, reason);
    }
}
