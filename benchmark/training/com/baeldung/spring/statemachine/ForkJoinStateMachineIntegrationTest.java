package com.baeldung.spring.statemachine;


import com.baeldung.spring.statemachine.config.ForkJoinStateMachineConfiguration;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForkJoinStateMachineConfiguration.class)
public class ForkJoinStateMachineIntegrationTest {
    @Autowired
    private StateMachine<String, String> stateMachine;

    @Test
    public void whenForkStateEntered_thenMultipleSubStatesEntered() {
        boolean success = stateMachine.sendEvent("E1");
        Assert.assertTrue(success);
        Assert.assertTrue(Arrays.asList("SFork", "Sub1-1", "Sub2-1").containsAll(stateMachine.getState().getIds()));
    }

    @Test
    public void whenAllConfiguredJoinEntryStatesAreEntered_thenTransitionToJoinState() {
        boolean success = stateMachine.sendEvent("E1");
        Assert.assertTrue(success);
        Assert.assertTrue(Arrays.asList("SFork", "Sub1-1", "Sub2-1").containsAll(stateMachine.getState().getIds()));
        Assert.assertTrue(stateMachine.sendEvent("sub1"));
        Assert.assertTrue(stateMachine.sendEvent("sub2"));
        Assert.assertEquals("SJoin", stateMachine.getState().getId());
    }
}

