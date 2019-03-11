package com.baeldung.algorithms;


import org.junit.Assert;
import org.junit.Test;


public final class RtFiniteStateMachineLongRunningUnitTest {
    @Test
    public void acceptsSimplePair() {
        String json = "{\"key\":\"value\"}";
        FiniteStateMachine machine = this.buildJsonStateMachine();
        for (int i = 0; i < (json.length()); i++) {
            machine = machine.switchState(String.valueOf(json.charAt(i)));
        }
        Assert.assertTrue(machine.canStop());
    }

    @Test
    public void acceptsMorePairs() {
        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        FiniteStateMachine machine = this.buildJsonStateMachine();
        for (int i = 0; i < (json.length()); i++) {
            machine = machine.switchState(String.valueOf(json.charAt(i)));
        }
        Assert.assertTrue(machine.canStop());
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingColon() {
        String json = "{\"key\"\"value\"}";
        FiniteStateMachine machine = this.buildJsonStateMachine();
        for (int i = 0; i < (json.length()); i++) {
            machine = machine.switchState(String.valueOf(json.charAt(i)));
        }
    }
}

