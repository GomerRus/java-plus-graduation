package ru.practicum.stats.server.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus
    protected ResponseEntity<?> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        String error = e.getParameterName() + " parameter is missing";
        StringWriter sw = new StringWriter();
        String stackTrace = sw.toString();
        AppError apiError = new AppError(HttpStatus.BAD_REQUEST, error, e.getMessage(), stackTrace);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleBadRequest(final BadRequestException e) {
        log.info("400 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        String stackTrace = sw.toString();
        return new AppError(HttpStatus.BAD_REQUEST, "BAD REQUEST проверьте запрос", e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleException(final Exception exp) {
        log.info("500 {}", exp.getMessage(), exp);
        StringWriter sw = new StringWriter();
        String stackTrace = sw.toString();
        return new AppError(HttpStatus.INTERNAL_SERVER_ERROR, "Error.....", exp.getMessage(), stackTrace);
    }

}
