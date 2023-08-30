package io.renatofreire.passwordmanager.exception;

public class BodyIsMissingException extends RuntimeException {
    public BodyIsMissingException() {
    }

    public BodyIsMissingException(String message) {
        super(message);
    }
}
