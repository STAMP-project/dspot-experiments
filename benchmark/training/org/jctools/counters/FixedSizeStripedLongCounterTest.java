package org.jctools.counters;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.jctools.util.PortableJvmInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Tolstopyatov Vsevolod
 */
@RunWith(Parameterized.class)
public class FixedSizeStripedLongCounterTest {
    private final Counter counter;

    public FixedSizeStripedLongCounterTest(Counter counter) {
        this.counter = counter;
    }

    @Test
    public void testCounterSanity() {
        long expected = 1000L;
        for (int i = 0; i < expected; i++) {
            counter.inc();
        }
        assertSanity(expected);
    }

    @Test
    public void testMultipleThreadsCounterSanity() throws Exception {
        int threadsCount = PortableJvmInfo.CPUs;
        AtomicLong summary = new AtomicLong();
        AtomicBoolean running = new AtomicBoolean(true);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadsCount);
        AtomicBoolean fail = new AtomicBoolean(false);
        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                try {
                    Counter c = counter;
                    startLatch.await();
                    long local = 0;
                    while (running.get()) {
                        c.inc();
                        local++;
                    } 
                    summary.addAndGet(local);
                } catch (Exception e) {
                    fail.set(true);
                } finally {
                    finishLatch.countDown();
                }
            }).start();
        }
        startLatch.countDown();
        Thread.sleep(1000);
        running.set(false);
        finishLatch.await();
        Assert.assertFalse(fail.get());
        assertSanity(summary.get());
    }
}

