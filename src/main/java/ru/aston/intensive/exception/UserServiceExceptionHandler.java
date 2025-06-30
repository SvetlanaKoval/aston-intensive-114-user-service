package ru.aston.intensive.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class UserServiceExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UserServiceExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException e) {
        log.error("User not found error: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidate(ValidateException e) {
        log.error("No valid data error: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

}
