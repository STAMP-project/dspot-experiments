/**
 * Copyright 2017 The Netty Project
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
package io.netty.util.concurrent;


import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class AbstractScheduledEventExecutorTest {
    private static final Runnable TEST_RUNNABLE = new Runnable() {
        @Override
        public void run() {
        }
    };

    private static final Callable<?> TEST_CALLABLE = Executors.callable(AbstractScheduledEventExecutorTest.TEST_RUNNABLE);

    @Test
    public void testScheduleRunnableZero() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        ScheduledFuture<?> future = executor.schedule(AbstractScheduledEventExecutorTest.TEST_RUNNABLE, 0, TimeUnit.NANOSECONDS);
        Assert.assertEquals(0, future.getDelay(TimeUnit.NANOSECONDS));
        Assert.assertNotNull(pollScheduledTask());
        Assert.assertNull(pollScheduledTask());
    }

    @Test
    public void testScheduleRunnableNegative() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        ScheduledFuture<?> future = executor.schedule(AbstractScheduledEventExecutorTest.TEST_RUNNABLE, (-1), TimeUnit.NANOSECONDS);
        Assert.assertEquals(0, future.getDelay(TimeUnit.NANOSECONDS));
        Assert.assertNotNull(pollScheduledTask());
        Assert.assertNull(pollScheduledTask());
    }

    @Test
    public void testScheduleCallableZero() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        ScheduledFuture<?> future = schedule(AbstractScheduledEventExecutorTest.TEST_CALLABLE, 0, TimeUnit.NANOSECONDS);
        Assert.assertEquals(0, future.getDelay(TimeUnit.NANOSECONDS));
        Assert.assertNotNull(pollScheduledTask());
        Assert.assertNull(pollScheduledTask());
    }

    @Test
    public void testScheduleCallableNegative() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        ScheduledFuture<?> future = schedule(AbstractScheduledEventExecutorTest.TEST_CALLABLE, (-1), TimeUnit.NANOSECONDS);
        Assert.assertEquals(0, future.getDelay(TimeUnit.NANOSECONDS));
        Assert.assertNotNull(pollScheduledTask());
        Assert.assertNull(pollScheduledTask());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleAtFixedRateRunnableZero() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        scheduleAtFixedRate(AbstractScheduledEventExecutorTest.TEST_RUNNABLE, 0, 0, TimeUnit.DAYS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleAtFixedRateRunnableNegative() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        scheduleAtFixedRate(AbstractScheduledEventExecutorTest.TEST_RUNNABLE, 0, (-1), TimeUnit.DAYS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithFixedDelayZero() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        scheduleWithFixedDelay(AbstractScheduledEventExecutorTest.TEST_RUNNABLE, 0, (-1), TimeUnit.DAYS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithFixedDelayNegative() {
        AbstractScheduledEventExecutorTest.TestScheduledEventExecutor executor = new AbstractScheduledEventExecutorTest.TestScheduledEventExecutor();
        scheduleWithFixedDelay(AbstractScheduledEventExecutorTest.TEST_RUNNABLE, 0, (-1), TimeUnit.DAYS);
    }

    private static final class TestScheduledEventExecutor extends AbstractScheduledEventExecutor {
        @Override
        public boolean isShuttingDown() {
            return false;
        }

        @Override
        public boolean inEventLoop(Thread thread) {
            return true;
        }

        @Override
        public void shutdown() {
            // NOOP
        }

        @Override
        public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Future<?> terminationFuture() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            throw new UnsupportedOperationException();
        }
    }
}

