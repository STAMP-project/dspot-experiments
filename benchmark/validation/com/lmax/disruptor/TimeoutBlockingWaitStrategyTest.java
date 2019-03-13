package com.lmax.disruptor;


import com.lmax.disruptor.support.DummySequenceBarrier;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class TimeoutBlockingWaitStrategyTest {
    @Test
    public void shouldTimeoutWaitFor() throws Exception {
        final SequenceBarrier sequenceBarrier = new DummySequenceBarrier();
        long theTimeout = 500;
        TimeoutBlockingWaitStrategy waitStrategy = new TimeoutBlockingWaitStrategy(theTimeout, TimeUnit.MILLISECONDS);
        Sequence cursor = new Sequence(5);
        Sequence dependent = cursor;
        long t0 = System.currentTimeMillis();
        try {
            waitStrategy.waitFor(6, cursor, dependent, sequenceBarrier);
            Assert.fail("TimeoutException should have been thrown");
        } catch (TimeoutException e) {
        }
        long t1 = System.currentTimeMillis();
        long timeWaiting = t1 - t0;
        Assert.assertTrue((timeWaiting >= theTimeout));
    }
}

