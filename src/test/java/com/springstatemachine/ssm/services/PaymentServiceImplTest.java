package com.springstatemachine.ssm.services;

import com.springstatemachine.ssm.domain.Payment;
import com.springstatemachine.ssm.domain.PaymentState;
import com.springstatemachine.ssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        paymentService.confirmNotify(savedPayment.getId());
        paymentService.confirmRefund(savedPayment.getId());
        paymentService.completeRefund(savedPayment.getId());
        assertSame(PaymentState.REFUND_COMPLETED, savedPayment.getPaymentState());

    }
}