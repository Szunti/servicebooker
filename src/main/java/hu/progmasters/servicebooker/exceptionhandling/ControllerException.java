package hu.progmasters.servicebooker.exceptionhandling;

public abstract class ControllerException extends RuntimeException {

    public ControllerException() {
    }

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException(Throwable cause) {
        super(causeMessage(cause), cause);
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    private static String causeMessage(Throwable cause) {
        return cause == null ? null : cause.getMessage();
    }
}
