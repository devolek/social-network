package com.skillbox.socialnetwork.main.exception;

import com.skillbox.socialnetwork.main.dto.universal.ErrorResponse;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.exception.not.found.NotFoundException;
import com.skillbox.socialnetwork.main.exception.user.input.UserException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<Object> handleUncaughtException(
            ExpiredJwtException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(
                ex,
                ResponseFactory.getErrorResponse("TokenException", ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {UserException.class})
    protected ResponseEntity<?> handleUserException(
            UserException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(
                ex,
                new ErrorResponse(ex.getErrorName(), ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    protected ResponseEntity<?> handleInvalidRequestException(
            InvalidRequestException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(
                ex,
                new ErrorResponse("invalid_request", ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<?> handleNotFoundException(
            AuthenticationException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(
                ex,
                new ErrorResponse("invalid_request", ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<?> handleNotFoundException(
            NotFoundException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(
                ex,
                new ErrorResponse(ex.getErrorName(), ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }
}
