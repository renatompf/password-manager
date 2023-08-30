package io.renatofreire.passwordmanager.exception;

public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException() {
    }

    public InvalidFieldException(String message) {
        super(message);
    }
}
