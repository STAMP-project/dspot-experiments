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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PartitionedOrderedExecutorTest {
    @Test
    public void testShutdownOfIdleExecutor() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Mockito.mock(ExecutorService.class);
        PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
        executor.shutdown();
        Assert.assertThat(executor.isShutdown(), Is.is(true));
        Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
        Assert.assertThat(executor.isTerminated(), Is.is(true));
    }

    @Test
    public void testShutdownNowOfIdleExecutor() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Mockito.mock(ExecutorService.class);
        PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
        Assert.assertThat(executor.shutdownNow(), empty());
        Assert.assertThat(executor.isShutdown(), Is.is(true));
        Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
        Assert.assertThat(executor.isTerminated(), Is.is(true));
    }

    @Test
    public void testTerminatedExecutorRejectsJob() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Mockito.mock(ExecutorService.class);
        PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
        executor.shutdown();
        Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
        try {
            executor.execute(() -> {
                // no-op
            });
            Assert.fail("Expected RejectedExecutionException");
        } catch (RejectedExecutionException e) {
            // expected
        }
    }

    @Test
    public void testShutdownButNonTerminatedExecutorRejectsJob() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
            final Semaphore semaphore = new Semaphore(0);
            executor.execute(semaphore::acquireUninterruptibly);
            executor.shutdown();
            try {
                executor.execute(() -> {
                    throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

                });
                Assert.fail("Expected RejectedExecutionException");
            } catch (RejectedExecutionException e) {
                // expected
            }
            semaphore.release();
            Assert.assertThat(executor.awaitTermination(2, TimeUnit.MINUTES), Is.is(true));
        } finally {
            service.shutdown();
        }
    }

    @Test
    public void testShutdownLeavesJobRunning() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
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
            service.shutdown();
        }
    }

    @Test
    public void testQueuedJobRunsAfterShutdown() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
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
            service.shutdown();
        }
    }

    @Test
    public void testQueuedJobIsStoppedAfterShutdownNow() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
            final Semaphore jobSemaphore = new Semaphore(0);
            final Semaphore testSemaphore = new Semaphore(0);
            executor.submit(() -> {
                testSemaphore.release();
                jobSemaphore.acquireUninterruptibly();
            });
            final AtomicBoolean called = new AtomicBoolean();
            Callable<?> leftBehind = ((Callable<Void>) (() -> {
                called.set(true);
                return null;
            }));
            executor.submit(leftBehind);
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
            service.shutdown();
        }
    }

    @Test
    public void testRunningJobIsInterruptedAfterShutdownNow() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
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
            service.shutdown();
        }
    }

    @Test
    public void testJobsAreExecutedInOrder() throws InterruptedException, ExecutionException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService service = Executors.newFixedThreadPool(2);
        try {
            PartitionedOrderedExecutor executor = new PartitionedOrderedExecutor(queue, service);
            final AtomicInteger sequence = new AtomicInteger((-1));
            List<Future<?>> tasks = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                final int index = i;
                tasks.add(executor.submit(() -> {
                    Assert.assertThat(sequence.getAndSet(index), Is.is((index - 1)));
                    return null;
                }));
            }
            for (Future<?> task : tasks) {
                task.get();
            }
        } finally {
            service.shutdown();
        }
    }
}

