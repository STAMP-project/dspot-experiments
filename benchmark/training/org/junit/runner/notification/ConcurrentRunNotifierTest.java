package org.junit.runner.notification;


import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;


/**
 * Testing RunNotifier in concurrent access.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
public final class ConcurrentRunNotifierTest {
    private static final long TIMEOUT = 3;

    private final RunNotifier fNotifier = new RunNotifier();

    private static class ConcurrentRunListener extends RunListener {
        final AtomicInteger fTestStarted = new AtomicInteger(0);

        @Override
        public void testStarted(Description description) throws Exception {
            fTestStarted.incrementAndGet();
        }
    }

    @Test
    public void realUsage() throws Exception {
        ConcurrentRunNotifierTest.ConcurrentRunListener listener1 = new ConcurrentRunNotifierTest.ConcurrentRunListener();
        ConcurrentRunNotifierTest.ConcurrentRunListener listener2 = new ConcurrentRunNotifierTest.ConcurrentRunListener();
        fNotifier.addListener(listener1);
        fNotifier.addListener(listener2);
        final int numParallelTests = 4;
        ExecutorService pool = Executors.newFixedThreadPool(numParallelTests);
        for (int i = 0; i < numParallelTests; ++i) {
            pool.submit(new Runnable() {
                public void run() {
                    fNotifier.fireTestStarted(null);
                }
            });
        }
        pool.shutdown();
        Assert.assertTrue(pool.awaitTermination(ConcurrentRunNotifierTest.TIMEOUT, TimeUnit.SECONDS));
        fNotifier.removeListener(listener1);
        fNotifier.removeListener(listener2);
        Assert.assertThat(listener1.fTestStarted.get(), Is.is(numParallelTests));
        Assert.assertThat(listener2.fTestStarted.get(), Is.is(numParallelTests));
    }

    private static class ExaminedListener extends RunListener {
        final boolean throwFromTestStarted;

        volatile boolean hasTestFailure = false;

        ExaminedListener(boolean throwFromTestStarted) {
            this.throwFromTestStarted = throwFromTestStarted;
        }

        @Override
        public void testStarted(Description description) throws Exception {
            if (throwFromTestStarted) {
                throw new Exception();
            }
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            hasTestFailure = true;
        }
    }

    private abstract class AbstractConcurrentFailuresTest {
        protected abstract void addListener(ConcurrentRunNotifierTest.ExaminedListener listener);

        public void test() throws Exception {
            int totalListenersFailures = 0;
            Random random = new Random(42);
            ConcurrentRunNotifierTest.ExaminedListener[] examinedListeners = new ConcurrentRunNotifierTest.ExaminedListener[1000];
            for (int i = 0; i < (examinedListeners.length); ++i) {
                boolean fail = (random.nextDouble()) >= 0.5;
                if (fail) {
                    ++totalListenersFailures;
                }
                examinedListeners[i] = new ConcurrentRunNotifierTest.ExaminedListener(fail);
            }
            final AtomicBoolean condition = new AtomicBoolean(true);
            final CyclicBarrier trigger = new CyclicBarrier(2);
            final CountDownLatch latch = new CountDownLatch(10);
            ExecutorService notificationsPool = Executors.newFixedThreadPool(4);
            notificationsPool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    trigger.await();
                    while (condition.get()) {
                        fNotifier.fireTestStarted(null);
                        latch.countDown();
                    } 
                    fNotifier.fireTestStarted(null);
                    return null;
                }
            });
            // Wait for callable to start
            trigger.await(ConcurrentRunNotifierTest.TIMEOUT, TimeUnit.SECONDS);
            // Wait for callable to fire a few events
            latch.await(ConcurrentRunNotifierTest.TIMEOUT, TimeUnit.SECONDS);
            for (ConcurrentRunNotifierTest.ExaminedListener examinedListener : examinedListeners) {
                addListener(examinedListener);
            }
            notificationsPool.shutdown();
            condition.set(false);
            Assert.assertTrue(notificationsPool.awaitTermination(ConcurrentRunNotifierTest.TIMEOUT, TimeUnit.SECONDS));
            if (totalListenersFailures != 0) {
                // If no listener failures, then all the listeners do not report any failure.
                int countTestFailures = (examinedListeners.length) - (ConcurrentRunNotifierTest.countReportedTestFailures(examinedListeners));
                Assert.assertThat(totalListenersFailures, Is.is(countTestFailures));
            }
        }
    }

    /**
     * Verifies that listeners added while tests are run concurrently are
     * notified about test failures.
     */
    @Test
    public void reportConcurrentFailuresAfterAddListener() throws Exception {
        new ConcurrentRunNotifierTest.AbstractConcurrentFailuresTest() {
            @Override
            protected void addListener(ConcurrentRunNotifierTest.ExaminedListener listener) {
                fNotifier.addListener(listener);
            }
        }.test();
    }

    /**
     * Verifies that listeners added with addFirstListener() while tests are run concurrently are
     * notified about test failures.
     */
    @Test
    public void reportConcurrentFailuresAfterAddFirstListener() throws Exception {
        new ConcurrentRunNotifierTest.AbstractConcurrentFailuresTest() {
            @Override
            protected void addListener(ConcurrentRunNotifierTest.ExaminedListener listener) {
                fNotifier.addFirstListener(listener);
            }
        }.test();
    }
}

