package com.baeldung.breakcontinue;


import org.junit.Assert;
import org.junit.Test;


public class BreakContinueUnitTest {
    @Test
    public void whenUnlabeledBreak_ThenEqual() {
        Assert.assertEquals(4, BreakContinue.unlabeledBreak());
    }

    @Test
    public void whenUnlabeledBreakNestedLoops_ThenEqual() {
        Assert.assertEquals(2, BreakContinue.unlabeledBreakNestedLoops());
    }

    @Test
    public void whenLabeledBreak_ThenEqual() {
        Assert.assertEquals(1, BreakContinue.labeledBreak());
    }

    @Test
    public void whenUnlabeledContinue_ThenEqual() {
        Assert.assertEquals(5, BreakContinue.unlabeledContinue());
    }

    @Test
    public void whenLabeledContinue_ThenEqual() {
        Assert.assertEquals(3, BreakContinue.labeledContinue());
    }
}

