package ru.nelakov.libraryapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Centralizes error responses as RFC 9457 ProblemDetail. Framework exceptions
 * (validation 400, 404/405/415) are rendered as ProblemDetail by Spring when
 * {@code spring.mvc.problemdetails.enabled=true}; this advice maps the domain exceptions.
 * Errors are logged once here (single point) to avoid the log-and-throw double-logging smell.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found", kv("detail", ex.getMessage()), kv("status", 404));
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleConflict(ResourceAlreadyExistsException ex) {
        log.warn("Resource already exists", kv("detail", ex.getMessage()), kv("status", 409));
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}
