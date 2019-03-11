/**
 * *  Copyright 2010-2017 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.common.concur.executors;


import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class SubScheduledExecutorServiceTest {
    private ScheduledThreadPoolExecutor executor;

    private ScheduledExecutorService subExecutor;

    @Test
    public void testScheduleRunnable() throws InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final AtomicBoolean ran100 = new AtomicBoolean(false);
        final ScheduledFuture<?> future100 = subExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                ran100.set(true);
            }
        }, 100, TimeUnit.MILLISECONDS);
        final AtomicBoolean ran200 = new AtomicBoolean(false);
        final ScheduledFuture<?> future200 = subExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                ran200.set(true);
            }
        }, 200, TimeUnit.MILLISECONDS);
        final AtomicBoolean ran50 = new AtomicBoolean(false);
        final ScheduledFuture<?> future50 = subExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                ran50.set(true);
            }
        }, 50, TimeUnit.MILLISECONDS);
        future50.get();
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 50));
        Assert.assertTrue(ran50.get());
        future100.get();
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 100));
        Assert.assertTrue(ran100.get());
        future200.get();
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 200));
        Assert.assertTrue(ran200.get());
    }

    @Test
    public void testScheduleCallable() throws InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final ScheduledFuture<Boolean> future100 = subExecutor.schedule(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return true;
            }
        }, 100, TimeUnit.MILLISECONDS);
        final ScheduledFuture<Boolean> future200 = subExecutor.schedule(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return true;
            }
        }, 200, TimeUnit.MILLISECONDS);
        final ScheduledFuture<Boolean> future50 = subExecutor.schedule(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return true;
            }
        }, 50, TimeUnit.MILLISECONDS);
        Assert.assertTrue(future50.get());
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 50));
        Assert.assertTrue(future100.get());
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 100));
        Assert.assertTrue(future200.get());
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 200));
    }

    @Test
    public void testScheduleAtFixedRate() throws Exception {
        final long start = System.currentTimeMillis();
        final AtomicInteger counter = new AtomicInteger(0);
        subExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
        Assert.assertTrue(SubScheduledExecutorServiceTest.busyWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return (counter.get()) >= 5;
            }
        }));
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), (5 * 50)));
    }

    @Test
    public void testScheduleWithFixedDelay() throws Exception {
        final long start = System.currentTimeMillis();
        final AtomicInteger counter = new AtomicInteger(0);
        subExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
        Assert.assertTrue(SubScheduledExecutorServiceTest.busyWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return (counter.get()) >= 5;
            }
        }));
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), (5 * 50)));
    }

    @Test(expected = CancellationException.class)
    public void testCancelDelayed() throws InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final ScheduledFuture<Boolean> future = subExecutor.schedule(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        }, 200, TimeUnit.MILLISECONDS);
        future.cancel(false);
        subExecutor.shutdown();
        subExecutor.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyLess(SubScheduledExecutorServiceTest.span(start), 200));
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void testCancelPeriodic() throws InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final ScheduledFuture<?> future = subExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
        future.cancel(false);
        subExecutor.shutdown();
        subExecutor.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyLess(SubScheduledExecutorServiceTest.span(start), 200));
        future.get();
    }

    @Test
    public void testShutdown() throws Exception {
        final long start = System.currentTimeMillis();
        final AtomicBoolean delayedRan = new AtomicBoolean(false);
        subExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                delayedRan.set(true);
            }
        }, 100, TimeUnit.MILLISECONDS);
        subExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
        subExecutor.shutdown();
        Assert.assertTrue(subExecutor.isShutdown());
        Assert.assertFalse(subExecutor.isTerminated());
        Assert.assertFalse(delayedRan.get());
        Assert.assertTrue(subExecutor.awaitTermination(5, TimeUnit.SECONDS));
        Assert.assertTrue(delayedRan.get());
        Assert.assertTrue(SubScheduledExecutorServiceTest.fuzzyGreater(SubScheduledExecutorServiceTest.span(start), 100));
        Assert.assertTrue(subExecutor.isShutdown());
        Assert.assertTrue(subExecutor.isTerminated());
        Assert.assertFalse(executor.isShutdown());
        Assert.assertFalse(executor.isTerminated());
    }
}

