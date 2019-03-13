package com.baeldung.spring.statemachine;


import ApplicationReviewEvents.APPROVE;
import ApplicationReviewEvents.REJECT;
import ApplicationReviewStates.PRINCIPAL_REVIEW;
import ApplicationReviewStates.REJECTED;
import com.baeldung.spring.statemachine.applicationreview.ApplicationReviewEvents;
import com.baeldung.spring.statemachine.applicationreview.ApplicationReviewStates;
import com.baeldung.spring.statemachine.config.SimpleEnumStateMachineConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleEnumStateMachineConfiguration.class)
public class StateEnumMachineIntegrationTest {
    @Autowired
    private StateMachine<ApplicationReviewStates, ApplicationReviewEvents> stateMachine;

    @Test
    public void whenStateMachineConfiguredWithEnums_thenStateMachineAcceptsEnumEvents() {
        Assert.assertTrue(stateMachine.sendEvent(APPROVE));
        Assert.assertEquals(PRINCIPAL_REVIEW, stateMachine.getState().getId());
        Assert.assertTrue(stateMachine.sendEvent(REJECT));
        Assert.assertEquals(REJECTED, stateMachine.getState().getId());
    }
}

