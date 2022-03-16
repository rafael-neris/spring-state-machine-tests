package com.springstatemachine.ssm.services;

import com.springstatemachine.ssm.domain.Payment;
import com.springstatemachine.ssm.domain.PaymentEvent;
import com.springstatemachine.ssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentService {
    Payment newPayment(Payment payment);
    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> approvePreAuth(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> declinePreAuth(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> refund(Long paymentId);

    @Transactional
    StateMachine<PaymentState, PaymentEvent> notify(Long paymentId);

    @Transactional
    StateMachine<PaymentState, PaymentEvent> confirmNotify(Long paymentId);

    @Transactional
    StateMachine<PaymentState, PaymentEvent> initRefundFlow(Long paymentId);

    @Transactional
    StateMachine<PaymentState, PaymentEvent> reserveRefund(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> confirmRefund(Long paymentId);
    @Transactional
    StateMachine<PaymentState, PaymentEvent> completeRefund(Long paymentId);
}
