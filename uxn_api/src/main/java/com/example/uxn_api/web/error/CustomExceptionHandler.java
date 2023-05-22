package com.example.uxn_api.web.error;

import java.util.NoSuchElementException;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    public CustomExceptionHandler() {
        super();
    }

    //400
    @ExceptionHandler({ LoginException.class })
    public ResponseEntity<ErrorResponse> handleLoginException(final LoginException ex, final WebRequest request) {
        logger.error("LoginException :"+ ex.getErrorCode().getStatus());
        final ErrorResponse bodyOfResponse = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(bodyOfResponse,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
        //return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
