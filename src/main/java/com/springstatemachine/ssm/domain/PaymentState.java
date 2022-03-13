package com.springstatemachine.ssm.domain;

public enum PaymentState {
    NEW, PRE_AUTH, PRE_AUTH_ERROR, AUTHORIZED, AUTHORIZE_ERROR,
    REFUND, RESERVED, CONFIRMED, REFUND_COMPLETED
}
