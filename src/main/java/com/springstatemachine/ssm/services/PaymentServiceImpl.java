package com.springstatemachine.ssm.services;

import com.springstatemachine.ssm.domain.Payment;
import com.springstatemachine.ssm.domain.PaymentEvent;
import com.springstatemachine.ssm.domain.PaymentState;
import com.springstatemachine.ssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeListener;

    @Transactional
    @Override
    public Payment newPayment(Payment payment) {
        payment.setPaymentState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> refund(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.REFUND);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> notify(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.START_NOTIFY);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> confirmNotify(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.CONFIRM_NOTIFY);

        return sm;
    }


    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> initRefundFlow(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.REFUND_FLOW);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> reserveRefund(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.RESERVE);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> confirmRefund(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.CONFIRM);

        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> completeRefund(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.REFUND_COMPLETED);

        return sm;
    }


    @Transactional
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = paymentRepository.getById(paymentId);
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(paymentStateChangeListener);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getPaymentState(), null, null, null));
                });
        sm.start();
        return sm;
    }

    @Transactional
    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
        Message<PaymentEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        boolean statusIsAccepted = sm.sendEvent(msg);
        System.out.println();
        if (!statusIsAccepted) {
            System.out.println(sm.isComplete());;
        }
    }
}
