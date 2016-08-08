package terminalExceptions;

public class AccountIsLockedException extends TerminalException{

    private int unlockTime;

    public AccountIsLockedException(String message, Throwable cause, String reason, int unlockTime) {
        super(message, cause, reason);
        this.unlockTime = unlockTime;
    }
}
