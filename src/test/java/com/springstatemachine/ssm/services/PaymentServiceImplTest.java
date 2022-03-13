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
    public void testShouldRefund() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.preAuth(savedPayment.getId());
        paymentService.approvePreAuth(savedPayment.getId());
        paymentService.declineAuth(savedPayment.getId());
        paymentService.refund(savedPayment.getId());
        paymentService.reserveRefund(savedPayment.getId());
        paymentService.confirmRefund(savedPayment.getId());
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.completeRefund(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getById(savedPayment.getId());

        assertSame(PaymentState.REFUND_COMPLETED, preAuthedPayment.getPaymentState());
    }

    @Transactional
    @Test
    public void testShouldCompleteTransaction() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.preAuth(savedPayment.getId());
        paymentService.authorizePayment(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getById(savedPayment.getId());

        assertSame(PaymentState.AUTHORIZED, preAuthedPayment.getPaymentState());
    }

    @Transactional
    @Test
    public void testShouldNotCompleteTransaction() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.declinePreAuth(savedPayment.getId());
        paymentService.authorizePayment(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getById(savedPayment.getId());

        assertSame(PaymentState.PRE_AUTH_ERROR, preAuthedPayment.getPaymentState());
    }

    @Transactional
    @Test
    public void testShouldNotCompleteTransactionWithAuthorizeError() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.approvePreAuth(savedPayment.getId());
        paymentService.declineAuth(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getById(savedPayment.getId());

        assertSame(PaymentState.AUTHORIZE_ERROR, preAuthedPayment.getPaymentState());
    }

    @Transactional
    @Test
    public void testShouldNotRefundTransactionWithPreAuthorizeError() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.declinePreAuth(savedPayment.getId());
        paymentService.refund(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getById(savedPayment.getId());

        assertSame(PaymentState.PRE_AUTH_ERROR, preAuthedPayment.getPaymentState());
    }


    @Transactional
    @Test
    public void testDeclinePreAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.declinePreAuth(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getById(savedPayment.getId());

        assertSame(PaymentState.PRE_AUTH_ERROR, preAuthedPayment.getPaymentState());
    }
}