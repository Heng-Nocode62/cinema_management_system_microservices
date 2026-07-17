package com.heng.cms.paymentservice.dto;

import java.math.BigDecimal;

public record BakongTransactionResult(
        int responseCode,
        int errorCode,
        String responseMessage,
        String hash,
        String fromAccountId,
        String toAccountId,
        String currency,
        BigDecimal amount,
        String description
) {
    public boolean isPaid(){
        return responseCode ==0;
    }
    public boolean isFailed(){
        return responseCode ==1 && errorCode ==3;
    }

    public boolean isNotFound(){
        return responseCode == 1 && errorCode ==1;
    }
}
