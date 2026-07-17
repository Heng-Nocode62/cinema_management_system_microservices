package com.heng.cms.paymentservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ProblemDetail handlePaymentException(PaymentException e){
        log.warn("Payment error: {}", e.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
        pd.setType(URI.create("urn:cinema:payment:error"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.warn("Method argument not valid: {}", e.getMessage());
        Map<String, String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField,fieldError -> fieldError.getDefaultMessage()!=null? fieldError.getDefaultMessage():"invalid"));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Validation failed");
        pd.setType(URI.create("urn:cinema:payment:validation-error"));
        pd.setProperty("errors", errors);
        return pd;

    }
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception e){
        log.error("Unexpected error:", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
