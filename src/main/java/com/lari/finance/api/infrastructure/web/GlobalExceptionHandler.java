package com.lari.finance.api.infrastructure.web;

import com.lari.finance.api.application.exception.BusinessException;
import com.lari.finance.api.application.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(RuntimeException exception, HttpServletRequest request) {
        return ApiError.of(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(RuntimeException exception, HttpServletRequest request) {
        return ApiError.of(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError unauthorized(RuntimeException exception, HttpServletRequest request) {
        return ApiError.of(HttpStatus.UNAUTHORIZED, "Credenciales invalidas.", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> details = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
        return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Datos invalidos.", request.getRequestURI(), details);
    }

    public record ApiError(Instant timestamp, int status, String message, String path, List<String> details) {
        static ApiError of(HttpStatus status, String message, String path) {
            return new ApiError(Instant.now(), status.value(), message, path, List.of());
        }
    }
}
