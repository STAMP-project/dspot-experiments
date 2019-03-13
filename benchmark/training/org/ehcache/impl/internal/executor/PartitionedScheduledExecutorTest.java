/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.impl.internal.executor;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class PartitionedScheduledExecutorTest {
    private OutOfBandScheduledExecutor scheduler = new OutOfBandScheduledExecutor();

    @Test
    public void testShutdownOfIdleExecutor() throws InterruptedException {
        ExecutorService worker = Executors.newCachedThreadPool();
        PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
        executor.shutdown();
        Assert.assertThat(executor.isShutdown(), Is.is(true));
        Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
        Assert.assertThat(executor.isTerminated(), Is.is(true));
    }

    @Test
    public void testShutdownNowOfIdleExecutor() throws InterruptedException {
        ExecutorService worker = Executors.newCachedThreadPool();
        PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
        Assert.assertThat(executor.shutdownNow(), empty());
        Assert.assertThat(executor.isShutdown(), Is.is(true));
        Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
        Assert.assertThat(executor.isTerminated(), Is.is(true));
    }

    @Test
    public void testShutdownLeavesJobRunning() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            final Semaphore semaphore = new Semaphore(0);
            executor.execute(semaphore::acquireUninterruptibly);
            executor.shutdown();
            Assert.assertThat(executor.awaitTermination(100, TimeUnit.MILLISECONDS), Is.is(false));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(false));
            semaphore.release();
            Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(semaphore.availablePermits(), Is.is(0));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testQueuedJobRunsAfterShutdown() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            final Semaphore jobSemaphore = new Semaphore(0);
            final Semaphore testSemaphore = new Semaphore(0);
            executor.submit(() -> {
                testSemaphore.release();
                jobSemaphore.acquireUninterruptibly();
            });
            executor.submit(((Runnable) (jobSemaphore::acquireUninterruptibly)));
            testSemaphore.acquireUninterruptibly();
            executor.shutdown();
            Assert.assertThat(executor.awaitTermination(100, TimeUnit.MILLISECONDS), Is.is(false));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(false));
            jobSemaphore.release();
            Assert.assertThat(executor.awaitTermination(100, TimeUnit.MILLISECONDS), Is.is(false));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(false));
            jobSemaphore.release();
            Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(jobSemaphore.availablePermits(), Is.is(0));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testQueuedJobIsStoppedAfterShutdownNow() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            final Semaphore jobSemaphore = new Semaphore(0);
            final Semaphore testSemaphore = new Semaphore(0);
            executor.submit(() -> {
                testSemaphore.release();
                jobSemaphore.acquireUninterruptibly();
            });
            final AtomicBoolean called = new AtomicBoolean();
            executor.submit(() -> called.set(true));
            testSemaphore.acquireUninterruptibly();
            Assert.assertThat(executor.shutdownNow(), hasSize(1));
            Assert.assertThat(executor.awaitTermination(100, TimeUnit.MILLISECONDS), Is.is(false));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(false));
            jobSemaphore.release();
            Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(jobSemaphore.availablePermits(), Is.is(0));
            Assert.assertThat(called.get(), Is.is(false));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testRunningJobIsInterruptedAfterShutdownNow() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            final Semaphore jobSemaphore = new Semaphore(0);
            final Semaphore testSemaphore = new Semaphore(0);
            final AtomicBoolean interrupted = new AtomicBoolean();
            executor.submit(() -> {
                testSemaphore.release();
                try {
                    jobSemaphore.acquire();
                } catch (InterruptedException e) {
                    interrupted.set(true);
                }
            });
            testSemaphore.acquireUninterruptibly();
            Assert.assertThat(executor.shutdownNow(), empty());
            Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(jobSemaphore.availablePermits(), Is.is(0));
            Assert.assertThat(interrupted.get(), Is.is(true));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testRunningJobsAreInterruptedAfterShutdownNow() throws InterruptedException {
        final int jobCount = 4;
        ExecutorService worker = Executors.newCachedThreadPool();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            final Semaphore jobSemaphore = new Semaphore(0);
            final Semaphore testSemaphore = new Semaphore(0);
            final AtomicInteger interrupted = new AtomicInteger();
            for (int i = 0; i < jobCount; i++) {
                executor.submit(() -> {
                    testSemaphore.release();
                    try {
                        jobSemaphore.acquire();
                    } catch (InterruptedException e) {
                        interrupted.incrementAndGet();
                    }
                });
            }
            testSemaphore.acquireUninterruptibly(jobCount);
            Assert.assertThat(executor.shutdownNow(), empty());
            Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(jobSemaphore.availablePermits(), Is.is(0));
            Assert.assertThat(interrupted.get(), Is.is(jobCount));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testFixedRatePeriodicTaskIsCancelledByShutdown() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> Assert.fail("Should not run!"), 2, 1, TimeUnit.MINUTES);
            executor.shutdown();
            Assert.assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(future.isCancelled(), Is.is(true));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testFixedDelayPeriodicTaskIsCancelledByShutdown() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<?> future = executor.scheduleWithFixedDelay(() -> Assert.fail("Should not run!"), 2, 1, TimeUnit.MINUTES);
            executor.shutdown();
            Assert.assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(future.isCancelled(), Is.is(true));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testFixedRatePeriodicTaskIsCancelledByShutdownNow() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
                // no-op
            }, 2, 1, TimeUnit.MINUTES);
            Assert.assertThat(executor.shutdownNow(), hasSize(1));
            Assert.assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(future.isDone(), Is.is(false));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testFixedDelayPeriodicTaskIsRemovedByShutdownNow() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<?> future = executor.scheduleWithFixedDelay(() -> Assert.fail("Should not run!"), 2, 1, TimeUnit.MINUTES);
            Assert.assertThat(executor.shutdownNow(), hasSize(1));
            Assert.assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(future.isDone(), Is.is(false));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testDelayedTaskIsRemovedByShutdownNow() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<?> future = executor.schedule(() -> Assert.fail("Should not run!"), 2, TimeUnit.MINUTES);
            List<Runnable> remainingTasks = executor.shutdownNow();
            Assert.assertThat(remainingTasks, hasSize(1));
            Assert.assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(future.isDone(), Is.is(false));
            for (Runnable r : remainingTasks)
                r.run();

            Assert.assertThat(future.isDone(), Is.is(true));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testTerminationAfterShutdownWaitsForDelayedTask() throws InterruptedException {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<?> future = executor.schedule(() -> {
                // no-op
            }, 200, TimeUnit.MILLISECONDS);
            executor.shutdown();
            Assert.assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), Is.is(true));
            Assert.assertThat(executor.isShutdown(), Is.is(true));
            Assert.assertThat(executor.isTerminated(), Is.is(true));
            Assert.assertThat(future.isDone(), Is.is(true));
        } finally {
            worker.shutdown();
        }
    }

    @Test
    public void testScheduledTasksRunOnDeclaredPool() throws InterruptedException, ExecutionException {
        ExecutorService worker = Executors.newSingleThreadExecutor(( r) -> new Thread(r, "testScheduledTasksRunOnDeclaredPool"));
        try {
            PartitionedScheduledExecutor executor = new PartitionedScheduledExecutor(scheduler, worker);
            ScheduledFuture<Thread> future = executor.schedule(Thread::currentThread, 0, TimeUnit.MILLISECONDS);
            Assert.assertThat(ExecutorUtil.waitFor(future).getName(), Is.is("testScheduledTasksRunOnDeclaredPool"));
            executor.shutdown();
        } finally {
            worker.shutdown();
        }
    }
}

