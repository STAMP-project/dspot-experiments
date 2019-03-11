package com.baeldung.spring.statemachine;


import com.baeldung.spring.statemachine.config.HierarchicalStateMachineConfiguration;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HierarchicalStateMachineConfiguration.class)
public class HierarchicalStateMachineIntegrationTest {
    @Autowired
    private StateMachine<String, String> stateMachine;

    @Test
    public void whenTransitionToSubMachine_thenSubStateIsEntered() {
        Assert.assertEquals(Arrays.asList("SI", "SUB1"), stateMachine.getState().getIds());
        stateMachine.sendEvent("se1");
        Assert.assertEquals(Arrays.asList("SI", "SUB2"), stateMachine.getState().getIds());
        stateMachine.sendEvent("s-end");
        Assert.assertEquals(Arrays.asList("SI", "SUBEND"), stateMachine.getState().getIds());
        stateMachine.sendEvent("end");
        Assert.assertEquals(1, stateMachine.getState().getIds().size());
        Assert.assertEquals("SF", stateMachine.getState().getId());
    }
}

