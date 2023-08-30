package io.renatofreire.passwordmanager.exceptionHandler;

import io.renatofreire.passwordmanager.exception.BodyIsMissingException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BodyIsMissingException.class)
    ResponseEntity<String> BodyIsMissingExceptionHandler(BodyIsMissingException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT );
    }

    @ExceptionHandler(value = InvalidFieldException.class)
    ResponseEntity<String> InvalidFieldExceptionHandler(InvalidFieldException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    ResponseEntity<String> EntityNotFoundExceptionHandler(EntityNotFoundException exception){
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<String> AccessDeniedExceptionHandler(AccessDeniedException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

}

