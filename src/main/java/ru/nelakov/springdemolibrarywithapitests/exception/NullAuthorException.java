package ru.nelakov.springdemolibrarywithapitests.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class NullAuthorException extends RuntimeException{
}
