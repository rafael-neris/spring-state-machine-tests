package com.springstatemachine.ssm.domain;

public enum PaymentState {
    NEW, AUTHORIZED, AUTHORIZE_ERROR,
    REFUND, RESERVED, CONFIRMED, REFUND_COMPLETED,
    NOTIFY, WAITING_NOTIFY, NOTIFIED,
    REFUND_FLOW,
    REFUNDED;
}
