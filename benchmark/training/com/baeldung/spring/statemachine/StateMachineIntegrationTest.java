package com.baeldung.spring.statemachine;


import com.baeldung.spring.statemachine.config.SimpleStateMachineConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleStateMachineConfiguration.class)
public class StateMachineIntegrationTest {
    @Autowired
    private StateMachine<String, String> stateMachine;

    @Test
    public void whenSimpleStringStateMachineEvents_thenEndState() {
        Assert.assertEquals("SI", stateMachine.getState().getId());
        stateMachine.sendEvent("E1");
        Assert.assertEquals("S1", stateMachine.getState().getId());
        stateMachine.sendEvent("E2");
        Assert.assertEquals("S2", stateMachine.getState().getId());
    }

    @Test
    public void whenSimpleStringMachineActionState_thenActionExecuted() throws InterruptedException {
        stateMachine.sendEvent("E3");
        Assert.assertEquals("S3", stateMachine.getState().getId());
        boolean acceptedE4 = stateMachine.sendEvent("E4");
        Assert.assertTrue(acceptedE4);
        Assert.assertEquals("S4", stateMachine.getState().getId());
        stateMachine.sendEvent("E5");
        Assert.assertEquals("S5", stateMachine.getState().getId());
        stateMachine.sendEvent("end");
        Assert.assertEquals("SF", stateMachine.getState().getId());
        Assert.assertEquals(2, stateMachine.getExtendedState().getVariables().get("approvalCount"));
    }
}

