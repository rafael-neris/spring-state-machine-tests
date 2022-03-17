package com.springstatemachine.ssm.services;

import com.springstatemachine.ssm.domain.Payment;
import com.springstatemachine.ssm.domain.PaymentEvent;
import com.springstatemachine.ssm.domain.PaymentState;
import com.springstatemachine.ssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentServiceImpl paymentService;
    Payment payment;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    public void testShouldRefundWithNotifyAndRefundDifferentOrder() {
        Payment savedPayment = paymentService.newPayment(payment);

        paymentService.declineAuth(savedPayment.getId());
        assertSame(PaymentState.AUTHORIZE_ERROR, savedPayment.getPaymentState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.initRefundFlow(savedPayment.getId());
        System.out.println(sm.getState().toString());
        paymentService.notify(savedPayment.getId());
        assertSame(PaymentState.WAITING_NOTIFY, savedPayment.getPaymentState());

        paymentService.confirmRefund(savedPayment.getId());
        assertSame(PaymentState.CONFIRMED, savedPayment.getPaymentState());

        paymentService.confirmNotify(savedPayment.getId());
        assertSame(PaymentState.NOTIFIED, savedPayment.getPaymentState());

        paymentService.completeRefund(savedPayment.getId());
        assertSame(PaymentState.REFUND_COMPLETED, savedPayment.getPaymentState());
    }
}