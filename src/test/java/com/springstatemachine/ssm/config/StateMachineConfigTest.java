package com.springstatemachine.ssm.config;

import com.springstatemachine.ssm.domain.PaymentEvent;
import com.springstatemachine.ssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        boolean resultAuthDeclined = sm.sendEvent(PaymentEvent.AUTH_DECLINED);
        assertTrue(resultAuthDeclined);
        assertTrue(sm.sendEvent(PaymentEvent.REFUND_FLOW));
        assertTrue(sm.sendEvent(PaymentEvent.CONFIRM));
        assertTrue(sm.sendEvent(PaymentEvent.CONFIRM_NOTIFY));
        assertSame(PaymentState.REFUNDED, sm.getState().getId());
        sm.stop();
    }
}