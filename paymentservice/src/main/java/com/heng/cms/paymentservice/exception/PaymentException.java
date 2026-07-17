package com.heng.cms.paymentservice.exception;

public class PaymentException extends RuntimeException{
    public PaymentException(String message){
        super(message);
    }
    public PaymentException(){
        super("PaymentException");
    }
}
