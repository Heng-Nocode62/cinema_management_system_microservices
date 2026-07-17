package com.heng.cms.paymentservice.gateway;

import com.heng.cms.paymentservice.dto.GatewayInitResult;
import com.heng.cms.paymentservice.dto.GatewayRefundResult;
import com.heng.cms.paymentservice.dto.GatewayStatusResult;
import com.heng.cms.paymentservice.dto.PaymentInitCommand;

import java.math.BigDecimal;

public interface PaymentGateway {

    GatewayInitResult initiatePayment(PaymentInitCommand command);

    GatewayStatusResult checkStatus(String gatewayReference);

    GatewayRefundResult refund(String gatewayReference, BigDecimal amount, String reason);

    String gatewayName();
}
