/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import com.google.common.base.Stopwatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link FakeClock}.
 */
@RunWith(JUnit4.class)
public class FakeClockTest {
    @Test
    public void testScheduledExecutorService_sameInstance() {
        FakeClock fakeClock = new FakeClock();
        ScheduledExecutorService scheduledExecutorService1 = fakeClock.getScheduledExecutorService();
        ScheduledExecutorService scheduledExecutorService2 = fakeClock.getScheduledExecutorService();
        Assert.assertTrue((scheduledExecutorService1 == scheduledExecutorService2));
    }

    @Test
    public void testScheduledExecutorService_isDone() {
        FakeClock fakeClock = new FakeClock();
        ScheduledFuture<?> future = fakeClock.getScheduledExecutorService().schedule(newRunnable(), 100L, TimeUnit.NANOSECONDS);
        fakeClock.forwardNanos(99L);
        Assert.assertFalse(future.isDone());
        fakeClock.forwardNanos(2L);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void testScheduledExecutorService_cancel() {
        FakeClock fakeClock = new FakeClock();
        ScheduledFuture<?> future = fakeClock.getScheduledExecutorService().schedule(newRunnable(), 100L, TimeUnit.NANOSECONDS);
        fakeClock.forwardNanos(99L);
        future.cancel(false);
        fakeClock.forwardNanos(2);
        Assert.assertTrue(future.isCancelled());
    }

    @Test
    public void testScheduledExecutorService_getDelay() {
        FakeClock fakeClock = new FakeClock();
        ScheduledFuture<?> future = fakeClock.getScheduledExecutorService().schedule(newRunnable(), 100L, TimeUnit.NANOSECONDS);
        fakeClock.forwardNanos(90L);
        Assert.assertEquals(10L, future.getDelay(TimeUnit.NANOSECONDS));
    }

    @Test
    public void testScheduledExecutorService_result() {
        FakeClock fakeClock = new FakeClock();
        final boolean[] result = new boolean[]{ false };
        ScheduledFuture<?> unused = fakeClock.getScheduledExecutorService().schedule(new Runnable() {
            @Override
            public void run() {
                result[0] = true;
            }
        }, 100L, TimeUnit.NANOSECONDS);
        fakeClock.forwardNanos(100L);
        Assert.assertTrue(result[0]);
    }

    @Test
    public void testStopWatch() {
        FakeClock fakeClock = new FakeClock();
        Stopwatch stopwatch = fakeClock.getStopwatchSupplier().get();
        long expectedElapsedNanos = 0L;
        stopwatch.start();
        fakeClock.forwardNanos(100L);
        expectedElapsedNanos += 100L;
        Assert.assertEquals(expectedElapsedNanos, stopwatch.elapsed(TimeUnit.NANOSECONDS));
        fakeClock.forwardTime(10L, TimeUnit.MINUTES);
        expectedElapsedNanos += TimeUnit.MINUTES.toNanos(10L);
        Assert.assertEquals(expectedElapsedNanos, stopwatch.elapsed(TimeUnit.NANOSECONDS));
        stopwatch.stop();
        fakeClock.forwardNanos(1000L);
        Assert.assertEquals(expectedElapsedNanos, stopwatch.elapsed(TimeUnit.NANOSECONDS));
        stopwatch.reset();
        expectedElapsedNanos = 0L;
        Assert.assertEquals(expectedElapsedNanos, stopwatch.elapsed(TimeUnit.NANOSECONDS));
    }

    @Test
    @SuppressWarnings("FutureReturnValueIgnored")
    public void testPendingAndDueTasks() {
        FakeClock fakeClock = new FakeClock();
        ScheduledExecutorService scheduledExecutorService = fakeClock.getScheduledExecutorService();
        scheduledExecutorService.schedule(newRunnable(), 200L, TimeUnit.NANOSECONDS);
        scheduledExecutorService.execute(newRunnable());
        scheduledExecutorService.schedule(newRunnable(), 0L, TimeUnit.NANOSECONDS);
        scheduledExecutorService.schedule(newRunnable(), 80L, TimeUnit.NANOSECONDS);
        scheduledExecutorService.schedule(newRunnable(), 90L, TimeUnit.NANOSECONDS);
        scheduledExecutorService.schedule(newRunnable(), 100L, TimeUnit.NANOSECONDS);
        scheduledExecutorService.schedule(newRunnable(), 110L, TimeUnit.NANOSECONDS);
        scheduledExecutorService.schedule(newRunnable(), 120L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(8, fakeClock.numPendingTasks());
        Assert.assertEquals(2, fakeClock.getDueTasks().size());
        fakeClock.runDueTasks();
        Assert.assertEquals(6, fakeClock.numPendingTasks());
        Assert.assertEquals(0, fakeClock.getDueTasks().size());
        fakeClock.forwardNanos(90L);
        Assert.assertEquals(4, fakeClock.numPendingTasks());
        Assert.assertEquals(0, fakeClock.getDueTasks().size());
        fakeClock.forwardNanos(20L);
        Assert.assertEquals(2, fakeClock.numPendingTasks());
        Assert.assertEquals(0, fakeClock.getDueTasks().size());
    }

    @Test
    public void testTaskFilter() {
        FakeClock fakeClock = new FakeClock();
        ScheduledExecutorService scheduledExecutorService = fakeClock.getScheduledExecutorService();
        final AtomicBoolean selectedDone = new AtomicBoolean();
        final AtomicBoolean ignoredDone = new AtomicBoolean();
        final Runnable selectedRunnable = new Runnable() {
            @Override
            public void run() {
                selectedDone.set(true);
            }
        };
        Runnable ignoredRunnable = new Runnable() {
            @Override
            public void run() {
                ignoredDone.set(true);
            }
        };
        FakeClock.TaskFilter filter = new FakeClock.TaskFilter() {
            @Override
            public boolean shouldAccept(Runnable runnable) {
                return runnable == selectedRunnable;
            }
        };
        scheduledExecutorService.execute(selectedRunnable);
        scheduledExecutorService.execute(ignoredRunnable);
        Assert.assertEquals(2, fakeClock.numPendingTasks());
        Assert.assertEquals(1, fakeClock.numPendingTasks(filter));
        Assert.assertEquals(2, fakeClock.getPendingTasks().size());
        Assert.assertEquals(1, fakeClock.getPendingTasks(filter).size());
        Assert.assertSame(selectedRunnable, fakeClock.getPendingTasks(filter).iterator().next().command);
        Assert.assertEquals(2, fakeClock.runDueTasks());
        Assert.assertTrue(selectedDone.get());
        Assert.assertTrue(ignoredDone.get());
    }
}

