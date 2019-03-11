package jenkins.util;


import hudson.util.OneShotEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class AtmostOneTaskExecutorTest {
    @SuppressWarnings("empty-statement")
    @Test
    public void doubleBooking() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        OneShotEvent lock = new OneShotEvent();
        Future<?> f1;
        Future<?> f2;
        ExecutorService base = Executors.newCachedThreadPool();
        AtmostOneTaskExecutor<?> es = new AtmostOneTaskExecutor<Void>(base, () -> {
            counter.incrementAndGet();
            try {
                lock.block();
            } catch ( x) {
                assert false : x;
            }
            return null;
        });
        f1 = es.submit();
        while ((counter.get()) == 0) {
            // spin lock until executor gets to the choking point
        } 
        f2 = es.submit();// this should hang

        Thread.sleep(500);// make sure the 2nd task is hanging

        Assert.assertEquals(1, counter.get());
        Assert.assertFalse(f2.isDone());
        lock.signal();// let the first one go

        f1.get();// first one should complete

        // now 2nd one gets going and hits the choke point
        f2.get();
        Assert.assertEquals(2, counter.get());
    }
}

