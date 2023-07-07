package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDataValidationException(DataValidationException e, HttpStatus status) {
        log.error("Ошибка в поступивших данных", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(status, "Ошибка в поступивших данных", e.getMessage(), Collections.singletonList(stackTrace));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFoundException(CategoryNotFoundException e, HttpStatus status) {
        log.error("Ошибка поиска категории", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(status, "Ошибка поиска категории", e.getMessage(), Collections.singletonList(stackTrace));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnknownException(Throwable e, HttpStatus status) {
        log.error("Возникла непредвиденная ошибка", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(status, "Непредвиденная ошибка", e.getMessage(), Collections.singletonList(stackTrace));
    }
}
