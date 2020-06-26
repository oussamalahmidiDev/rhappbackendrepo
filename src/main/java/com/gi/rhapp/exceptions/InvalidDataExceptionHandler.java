package com.gi.rhapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class InvalidDataExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(Exception ex)  {
        if (ex instanceof ConstraintViolationException)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données invalides");
    }

    @ExceptionHandler({NullPointerException.class, NoSuchElementException.class})
    public void handleNullPointerException(Exception ex)  {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Données introuvables");
    }
}
