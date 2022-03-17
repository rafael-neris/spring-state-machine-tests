package com.springstatemachine.ssm.domain;

public enum PaymentEvent {
    AUTHORIZE, AUTH_DECLINED,
    REFUND_FLOW, REFUND, RESERVE, CONFIRM, REFUND_COMPLETED,
    START_NOTIFY, CONFIRM_NOTIFY
}
