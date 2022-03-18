package com.springstatemachine.ssm.config;

import com.springstatemachine.ssm.domain.PaymentEvent;
import com.springstatemachine.ssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states
            .withStates()
                .initial(PaymentState.NEW)
                .state(PaymentState.AUTHORIZE_ERROR)
                .state(PaymentState.NOTIFY)
                .state(PaymentState.REFUND)
                .fork(PaymentState.REFUND_FLOW)
                .join(PaymentState.REFUND_COMPLETED)
                .end(PaymentState.AUTHORIZED)
                .end(PaymentState.REFUNDED)
            .and()
            .withStates()
                .parent(PaymentState.REFUND)
                .initial(PaymentState.RESERVED)
                .end(PaymentState.CONFIRMED)
            .and()
            .withStates()
                .parent(PaymentState.REFUND)
                .initial(PaymentState.WAITING_NOTIFY)
                .end(PaymentState.NOTIFIED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PaymentState.NEW).target(PaymentState.AUTHORIZED).event(PaymentEvent.AUTHORIZE)
                .and()
                .withExternal()
                    .source(PaymentState.NEW).target(PaymentState.AUTHORIZE_ERROR).event(PaymentEvent.AUTH_DECLINED)
                .and()
                .withExternal()
                    .source(PaymentState.AUTHORIZE_ERROR).target(PaymentState.REFUND_FLOW).event(PaymentEvent.REFUND_FLOW)
                .and()
                .withFork()
                    .source(PaymentState.REFUND_FLOW)
                    .target(PaymentState.REFUND)
                .and()
                .withJoin()
                    .source(PaymentState.REFUND)
                    .target(PaymentState.REFUND_COMPLETED)
                .and()
                .withExternal()
                    .source(PaymentState.REFUND_COMPLETED)
                    .target(PaymentState.REFUNDED)
                .and()
                .withExternal()
                    .source(PaymentState.RESERVED).target(PaymentState.CONFIRMED).event(PaymentEvent.CONFIRM)
                .and()
                .withExternal()
                    .source(PaymentState.WAITING_NOTIFY).target(PaymentState.NOTIFIED).event(PaymentEvent.CONFIRM_NOTIFY);
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
