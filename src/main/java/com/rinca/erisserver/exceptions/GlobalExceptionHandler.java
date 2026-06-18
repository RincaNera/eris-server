package com.rinca.erisserver.exceptions;

import com.rinca.erisserver.exceptions.constraint.ConstraintParserStrategy;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final ConstraintParserStrategy parser;

    public GlobalExceptionHandler(ConstraintParserStrategy parser) {
        this.parser = parser;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse> methodArgumentNotValidHandler(MethodArgumentNotValidException errors) {
        String error = errors.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new DefaultResponse(HttpStatus.BAD_REQUEST.toString(), error));
    }

    @ExceptionHandler({JpaSystemException.class, DataIntegrityViolationException.class})
    public ResponseEntity<DefaultResponse> databaseIntegrityHandler(Exception error) {
        String message = parser.parse(error.getMessage());
        return ResponseEntity.badRequest().body(new DefaultResponse(HttpStatus.BAD_REQUEST.toString(), message));
    }

    @ExceptionHandler({InvalidTokenException.class, BadCredentialsException.class})
    public ResponseEntity<DefaultResponse> forbiddenHandler(Exception error) {
        String message = "Identifiants invalides";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new DefaultResponse(status.toString(), message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultResponse> exceptionHandler(RuntimeException error) {
        System.out.printf("%s: %s", error.getClass(), error.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(
                new DefaultResponse(status.toString(), "Une erreur est survenue lors de la requête.")
        );
    }
}
