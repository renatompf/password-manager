package io.renatofreire.passwordmanager.exception;

public class InvalidFieldException extends Throwable {
    public InvalidFieldException() {
    }

    public InvalidFieldException(String message) {
        super(message);
    }
}
