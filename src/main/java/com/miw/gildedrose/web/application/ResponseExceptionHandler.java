package com.miw.gildedrose.web.application;

import com.miw.gildedrose.business.exception.InsufficientQuantityException;
import com.miw.gildedrose.business.exception.ItemNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(Exception e, WebRequest request) {
        return handleExceptionInternal(e, createErrorResponse("Login required"), new HttpHeaders(),
                HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFound(Exception e, WebRequest request) {
        return handleExceptionInternal(e, createErrorResponse(e.getMessage()), new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<Object> handleInsufficientQuantity(Exception e, WebRequest request) {
        return handleExceptionInternal(e, createErrorResponse("Insufficient quantity to purchase"), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    private Map<String, Object> createErrorResponse(String message) {
        final Map<String, Object> map = new HashMap<>();
        map.put("status", false);
        map.put("message", message);
        return map;
    }
}
