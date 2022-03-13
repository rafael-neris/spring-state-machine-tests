package com.springstatemachine.ssm.domain;

public enum PaymentEvent {
    PRE_AUTHORIZE, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTHORIZED, AUTH_DECLINED,
    REFUND, RESERVE, CONFIRM, REFUND_COMPLETED
}
