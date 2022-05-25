package ru.nelakov.springdemolibrarywithapitests.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidAuthorException extends ResponseStatusException {
    public InvalidAuthorException(HttpStatus status) {
        super(status);
    }
}
