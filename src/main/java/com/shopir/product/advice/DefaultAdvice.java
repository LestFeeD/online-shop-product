package com.shopir.product.advice;

import com.shopir.product.exceptions.BadRequestException;
import com.shopir.product.exceptions.NotFoundException;
import com.shopir.product.exceptions.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<ResponseException> handleExceptionBadRequest(BadRequestException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response,badRequest);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ResponseException> handleExceptionNotFound(NotFoundException exception) {
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response, badRequest);
    }
}
