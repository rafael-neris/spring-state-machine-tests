package com.springstatemachine.ssm.config;

import com.springstatemachine.ssm.domain.PaymentEvent;
import com.springstatemachine.ssm.domain.PaymentState;
import com.springstatemachine.ssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import java.util.EnumSet;
import java.util.Random;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        configureNewPaymentStates(states);
        states.withStates()
                .fork(PaymentState.REFUND_FLOW)
                .and();
        configureRefund(states);
        configureNotify(states);
    }

    private void configureNotify(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states
                .withStates()
                .region("NOTIFY")
                .parent(PaymentState.REFUND_FLOW)
                .initial(PaymentState.NOTIFY)
                .state(PaymentState.WAITING_NOTIFY)
                .end(PaymentState.NOTIFIED)
                .and();
    }
    private void configureNewPaymentStates(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states
            .withStates()
            .initial(PaymentState.NEW)
            .and()
            .withStates()
            .initial(PaymentState.PRE_AUTH)
            .parent(PaymentState.NEW)
            .state(PaymentState.PRE_AUTH)
            .state(PaymentState.AUTHORIZED)
            .state(PaymentState.PRE_AUTH_ERROR)
            .state(PaymentState.AUTHORIZE_ERROR)
            .fork(PaymentState.REFUND_FLOW)
            .end(PaymentState.AUTHORIZE_ERROR)
            .end(PaymentState.PRE_AUTH_ERROR)
            .end(PaymentState.AUTHORIZED)
            .and();
    }

    private void configureRefund(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states
            .withStates()
            .region("REFUND")
            .parent(PaymentState.REFUND_FLOW)
            .initial(PaymentState.REFUND)
            .state(PaymentState.RESERVED)
            .state(PaymentState.CONFIRMED)
            .end(PaymentState.REFUND_COMPLETED).and();
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTHORIZED).event(PaymentEvent.AUTHORIZED)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTHORIZE_ERROR).event(PaymentEvent.AUTH_DECLINED)
                .and()
                .withExternal().source(PaymentState.REFUND).target(PaymentState.RESERVED).event(PaymentEvent.RESERVE)
                .and()
                .withExternal().source(PaymentState.RESERVED).target(PaymentState.CONFIRMED).event(PaymentEvent.CONFIRM)
                .and()
                .withExternal().source(PaymentState.CONFIRMED).target(PaymentState.REFUND_COMPLETED).event(PaymentEvent.REFUND_COMPLETED)
                .and()
                .withFork()
                    .source(PaymentState.REFUND_FLOW)
                    .target(PaymentState.REFUND)
                    .target(PaymentState.NOTIFY)
                .and()
                .withExternal().source(PaymentState.AUTHORIZE_ERROR).target(PaymentState.REFUND_FLOW).event(PaymentEvent.REFUND_FLOW)
                .and()
                .withExternal()
                    .source(PaymentState.NOTIFY)
                    .target(PaymentState.WAITING_NOTIFY)
                    .event(PaymentEvent.START_NOTIFY)
                .and()
                .withExternal()
                    .source(PaymentState.WAITING_NOTIFY)
                    .target(PaymentState.NOTIFIED)
                    .event(PaymentEvent.CONFIRM_NOTIFY);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
          @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
              log.info(String.format("State Changed(From: %s, to: %s)", from, to));
            }
        };
        config.withConfiguration()
                .listener(adapter);
    }
}
