package com.baeldung.concurrent.cyclicbarrier;


import org.junit.Assert;
import org.junit.Test;


public class CyclicBarrierCompletionMethodExampleUnitTest {
    @Test
    public void whenCyclicBarrier_countTrips() {
        CyclicBarrierCompletionMethodExample ex = new CyclicBarrierCompletionMethodExample(7, 20);
        int lineCount = ex.countTrips();
        Assert.assertEquals(2, lineCount);
    }
}

