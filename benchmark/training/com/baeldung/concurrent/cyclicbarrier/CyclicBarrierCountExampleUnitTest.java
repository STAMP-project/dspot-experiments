package com.baeldung.concurrent.cyclicbarrier;


import org.junit.Assert;
import org.junit.Test;


public class CyclicBarrierCountExampleUnitTest {
    @Test
    public void whenCyclicBarrier_notCompleted() {
        CyclicBarrierCountExample ex = new CyclicBarrierCountExample(2);
        boolean isCompleted = ex.callTwiceInSameThread();
        Assert.assertFalse(isCompleted);
    }
}

