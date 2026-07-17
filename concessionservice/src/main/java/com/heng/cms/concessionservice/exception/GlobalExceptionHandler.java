package com.heng.cms.concessionservice.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error. path: {}", request.getRequestURI());

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Validation error", "Validation failed", request.getRequestURI());

        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        pd.setProperty("errors", errors);

        return pd;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(e.getHttpStatus(), e.getMessage());
        detail.setTitle("Business validation failed.");
        detail.setProperty("errorCode", e.getErrorCode());
        return new ResponseEntity<>(detail, e.getHttpStatus());

    }
    private ProblemDetail problem(HttpStatus status, String title, String detail, String path) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setProperty("path", path);
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }
}
