/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.netty.channel.local.LocalChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;


public class SingleThreadEventLoopTest {
    private static final Runnable NOOP = new Runnable() {
        @Override
        public void run() {
        }
    };

    private SingleThreadEventLoopTest.SingleThreadEventLoopA loopA;

    private SingleThreadEventLoopTest.SingleThreadEventLoopB loopB;

    private SingleThreadEventLoopTest.SingleThreadEventLoopC loopC;

    @Test
    @SuppressWarnings("deprecation")
    public void shutdownBeforeStart() throws Exception {
        shutdown();
        SingleThreadEventLoopTest.assertRejection(loopA);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shutdownAfterStart() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        loopA.execute(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        });
        // Wait for the event loop thread to start.
        latch.await();
        // Request the event loop thread to stop.
        shutdown();
        SingleThreadEventLoopTest.assertRejection(loopA);
        Assert.assertTrue(isShutdown());
        // Wait until the event loop is terminated.
        while (!(isTerminated())) {
            awaitTermination(1, TimeUnit.DAYS);
        } 
    }

    @Test
    public void scheduleTaskA() throws Exception {
        SingleThreadEventLoopTest.testScheduleTask(loopA);
    }

    @Test
    public void scheduleTaskB() throws Exception {
        SingleThreadEventLoopTest.testScheduleTask(loopB);
    }

    @Test
    public void scheduleTaskC() throws Exception {
        SingleThreadEventLoopTest.testScheduleTask(loopC);
    }

    @Test(timeout = 5000)
    public void scheduleTaskAtFixedRateA() throws Exception {
        SingleThreadEventLoopTest.testScheduleTaskAtFixedRate(loopA);
    }

    @Test(timeout = 5000)
    public void scheduleTaskAtFixedRateB() throws Exception {
        SingleThreadEventLoopTest.testScheduleTaskAtFixedRate(loopB);
    }

    @Test(timeout = 5000)
    public void scheduleLaggyTaskAtFixedRateA() throws Exception {
        SingleThreadEventLoopTest.testScheduleLaggyTaskAtFixedRate(loopA);
    }

    @Test(timeout = 5000)
    public void scheduleLaggyTaskAtFixedRateB() throws Exception {
        SingleThreadEventLoopTest.testScheduleLaggyTaskAtFixedRate(loopB);
    }

    @Test(timeout = 5000)
    public void scheduleTaskWithFixedDelayA() throws Exception {
        SingleThreadEventLoopTest.testScheduleTaskWithFixedDelay(loopA);
    }

    @Test(timeout = 5000)
    public void scheduleTaskWithFixedDelayB() throws Exception {
        SingleThreadEventLoopTest.testScheduleTaskWithFixedDelay(loopB);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shutdownWithPendingTasks() throws Exception {
        final int NUM_TASKS = 3;
        final AtomicInteger ranTasks = new AtomicInteger();
        final CountDownLatch latch = new CountDownLatch(1);
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                ranTasks.incrementAndGet();
                while ((latch.getCount()) > 0) {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        // Ignored
                    }
                } 
            }
        };
        for (int i = 0; i < NUM_TASKS; i++) {
            execute(task);
        }
        // At this point, the first task should be running and stuck at latch.await().
        while ((ranTasks.get()) == 0) {
            Thread.yield();
        } 
        Assert.assertEquals(1, ranTasks.get());
        // Shut down the event loop to test if the other tasks are run before termination.
        shutdown();
        // Let the other tasks run.
        latch.countDown();
        // Wait until the event loop is terminated.
        while (!(isTerminated())) {
            awaitTermination(1, TimeUnit.DAYS);
        } 
        // Make sure loop.shutdown() above triggered wakeup().
        Assert.assertEquals(NUM_TASKS, ranTasks.get());
    }

    @Test(timeout = 10000)
    @SuppressWarnings("deprecation")
    public void testRegistrationAfterShutdown() throws Exception {
        shutdown();
        // Disable logging temporarily.
        Logger root = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        List<Appender<ILoggingEvent>> appenders = new ArrayList<Appender<ILoggingEvent>>();
        for (Iterator<Appender<ILoggingEvent>> i = root.iteratorForAppenders(); i.hasNext();) {
            Appender<ILoggingEvent> a = i.next();
            appenders.add(a);
            root.detachAppender(a);
        }
        try {
            ChannelFuture f = loopA.register(new LocalChannel());
            f.awaitUninterruptibly();
            Assert.assertFalse(f.isSuccess());
            Assert.assertThat(f.cause(), is(instanceOf(RejectedExecutionException.class)));
            Assert.assertFalse(f.channel().isOpen());
        } finally {
            for (Appender<ILoggingEvent> a : appenders) {
                root.addAppender(a);
            }
        }
    }

    @Test(timeout = 10000)
    @SuppressWarnings("deprecation")
    public void testRegistrationAfterShutdown2() throws Exception {
        shutdown();
        final CountDownLatch latch = new CountDownLatch(1);
        Channel ch = new LocalChannel();
        ChannelPromise promise = ch.newPromise();
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                latch.countDown();
            }
        });
        // Disable logging temporarily.
        Logger root = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        List<Appender<ILoggingEvent>> appenders = new ArrayList<Appender<ILoggingEvent>>();
        for (Iterator<Appender<ILoggingEvent>> i = root.iteratorForAppenders(); i.hasNext();) {
            Appender<ILoggingEvent> a = i.next();
            appenders.add(a);
            root.detachAppender(a);
        }
        try {
            ChannelFuture f = loopA.register(promise);
            f.awaitUninterruptibly();
            Assert.assertFalse(f.isSuccess());
            Assert.assertThat(f.cause(), is(instanceOf(RejectedExecutionException.class)));
            // Ensure the listener was notified.
            Assert.assertFalse(latch.await(1, TimeUnit.SECONDS));
            Assert.assertFalse(ch.isOpen());
        } finally {
            for (Appender<ILoggingEvent> a : appenders) {
                root.addAppender(a);
            }
        }
    }

    @Test(timeout = 5000)
    public void testGracefulShutdownQuietPeriod() throws Exception {
        shutdownGracefully(1, Integer.MAX_VALUE, TimeUnit.SECONDS);
        // Keep Scheduling tasks for another 2 seconds.
        for (int i = 0; i < 20; i++) {
            Thread.sleep(100);
            execute(SingleThreadEventLoopTest.NOOP);
        }
        long startTime = System.nanoTime();
        Assert.assertThat(isShuttingDown(), is(true));
        Assert.assertThat(isShutdown(), is(false));
        while (!(isTerminated())) {
            awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } 
        Assert.assertThat(((System.nanoTime()) - startTime), is(greaterThanOrEqualTo(TimeUnit.SECONDS.toNanos(1))));
    }

    @Test(timeout = 5000)
    public void testGracefulShutdownTimeout() throws Exception {
        shutdownGracefully(2, 2, TimeUnit.SECONDS);
        // Keep Scheduling tasks for another 3 seconds.
        // Submitted tasks must be rejected after 2 second timeout.
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            execute(SingleThreadEventLoopTest.NOOP);
        }
        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(100);
                execute(SingleThreadEventLoopTest.NOOP);
            }
            Assert.fail("shutdownGracefully() must reject a task after timeout.");
        } catch (RejectedExecutionException e) {
            // Expected
        }
        Assert.assertThat(isShuttingDown(), is(true));
        Assert.assertThat(isShutdown(), is(true));
    }

    @Test(timeout = 10000)
    public void testOnEventLoopIteration() throws Exception {
        SingleThreadEventLoopTest.CountingRunnable onIteration = new SingleThreadEventLoopTest.CountingRunnable();
        executeAfterEventLoopIteration(onIteration);
        SingleThreadEventLoopTest.CountingRunnable noopTask = new SingleThreadEventLoopTest.CountingRunnable();
        submit(noopTask).sync();
        loopC.iterationEndSignal.take();
        MatcherAssert.assertThat("Unexpected invocation count for regular task.", noopTask.getInvocationCount(), is(1));
        MatcherAssert.assertThat("Unexpected invocation count for on every eventloop iteration task.", onIteration.getInvocationCount(), is(1));
    }

    @Test(timeout = 10000)
    public void testRemoveOnEventLoopIteration() throws Exception {
        SingleThreadEventLoopTest.CountingRunnable onIteration1 = new SingleThreadEventLoopTest.CountingRunnable();
        executeAfterEventLoopIteration(onIteration1);
        SingleThreadEventLoopTest.CountingRunnable onIteration2 = new SingleThreadEventLoopTest.CountingRunnable();
        executeAfterEventLoopIteration(onIteration2);
        removeAfterEventLoopIterationTask(onIteration1);
        SingleThreadEventLoopTest.CountingRunnable noopTask = new SingleThreadEventLoopTest.CountingRunnable();
        submit(noopTask).sync();
        loopC.iterationEndSignal.take();
        MatcherAssert.assertThat("Unexpected invocation count for regular task.", noopTask.getInvocationCount(), is(1));
        MatcherAssert.assertThat("Unexpected invocation count for on every eventloop iteration task.", onIteration2.getInvocationCount(), is(1));
        MatcherAssert.assertThat("Unexpected invocation count for on every eventloop iteration task.", onIteration1.getInvocationCount(), is(0));
    }

    private static final class SingleThreadEventLoopA extends SingleThreadEventLoop {
        final AtomicInteger cleanedUp = new AtomicInteger();

        SingleThreadEventLoopA() {
            super(null, Executors.defaultThreadFactory(), true);
        }

        @Override
        protected void run() {
            for (; ;) {
                Runnable task = takeTask();
                if (task != null) {
                    task.run();
                    updateLastExecutionTime();
                }
                if (confirmShutdown()) {
                    break;
                }
            }
        }

        @Override
        protected void cleanup() {
            cleanedUp.incrementAndGet();
        }
    }

    private static class SingleThreadEventLoopB extends SingleThreadEventLoop {
        SingleThreadEventLoopB() {
            super(null, Executors.defaultThreadFactory(), false);
        }

        @Override
        protected void run() {
            for (; ;) {
                try {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(delayNanos(System.nanoTime())));
                } catch (InterruptedException e) {
                    // Waken up by interruptThread()
                }
                runTasks0();
                if (confirmShutdown()) {
                    break;
                }
            }
        }

        protected void runTasks0() {
            runAllTasks();
        }

        @Override
        protected void wakeup(boolean inEventLoop) {
            interruptThread();
        }
    }

    private static final class SingleThreadEventLoopC extends SingleThreadEventLoopTest.SingleThreadEventLoopB {
        final LinkedBlockingQueue<Boolean> iterationEndSignal = new LinkedBlockingQueue<Boolean>(1);

        @Override
        protected void afterRunningAllTasks() {
            super.afterRunningAllTasks();
            iterationEndSignal.offer(true);
        }

        @Override
        protected void runTasks0() {
            runAllTasks(TimeUnit.MINUTES.toNanos(1));
        }
    }

    private static class CountingRunnable implements Runnable {
        private final AtomicInteger invocationCount = new AtomicInteger();

        @Override
        public void run() {
            invocationCount.incrementAndGet();
        }

        public int getInvocationCount() {
            return invocationCount.get();
        }

        public void resetInvocationCount() {
            invocationCount.set(0);
        }
    }
}

