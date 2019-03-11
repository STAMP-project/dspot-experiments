package com.baeldung.concurrent.cyclicbarrier;


import org.junit.Assert;
import org.junit.Test;


public class CyclicBarrierResetExampleUnitTest {
    @Test
    public void whenCyclicBarrier_reset() {
        CyclicBarrierResetExample ex = new CyclicBarrierResetExample(7, 20);
        int lineCount = ex.countWaits();
        Assert.assertTrue((lineCount > 7));
    }
}

