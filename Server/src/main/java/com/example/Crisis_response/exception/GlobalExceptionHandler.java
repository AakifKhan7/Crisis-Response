package com.example.Crisis_response.exception;

import com.example.Crisis_response.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiExceptions.NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ApiExceptions.NotFoundException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(ex.getMessage(), null, req.getRequestURI(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ApiExceptions.BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(ApiExceptions.BadRequestException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(ex.getMessage(), null, req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ApiExceptions.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(ApiExceptions.UnauthorizedException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(ex.getMessage(), null, req.getRequestURI(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ApiExceptions.ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(ApiExceptions.ForbiddenException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(ex.getMessage(), null, req.getRequestURI(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ApiExceptions.ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ApiExceptions.ConflictException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(ex.getMessage(), null, req.getRequestURI(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        logger.error("Validation failed: {}", ex.getMessage(), ex);
        String message = "Validation failed";
        if (ex.getBindingResult() != null && ex.getBindingResult().getFieldErrors() != null && !ex.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError fe = ex.getBindingResult().getFieldErrors().get(0);
            if (fe != null) {
                message = fe.getField() + ": " + fe.getDefaultMessage();
            }
        }
        ApiResponse<Object> body = ApiResponse.error(message, null, req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        logger.error("Malformed JSON: {}", ex.getMessage(), ex);
        ApiResponse<Object> body = ApiResponse.error("Malformed JSON request", null, req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error("Method not allowed", null, req.getRequestURI(), HttpStatus.METHOD_NOT_ALLOWED.value());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error(ex.getMessage(), null, req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        ApiResponse<Object> body = ApiResponse.error("Internal server error", null, req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        logger.error("Unhandled runtime exception", ex);
        String msg = ex.getMessage();
        int status = HttpStatus.BAD_REQUEST.value();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if (msg != null) {
            String m = msg.toLowerCase();
            if (m.contains("not found")) {
                httpStatus = HttpStatus.NOT_FOUND;
                status = httpStatus.value();
            } else if (m.contains("unauthenticated")) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                status = httpStatus.value();
            } else if (m.contains("unauthorized") || m.contains("forbidden")) {
                httpStatus = HttpStatus.FORBIDDEN;
                status = httpStatus.value();
            } else if (m.contains("already in use") || m.contains("already exists") || m.contains("exists")) {
                httpStatus = HttpStatus.CONFLICT;
                status = httpStatus.value();
            }
        }
        ApiResponse<Object> body = ApiResponse.error(msg != null ? msg : "Bad request", null, req.getRequestURI(), status);
        return ResponseEntity.status(httpStatus).body(body);
    }
}


