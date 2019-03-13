/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel;


import ThreadPoolRejectedPolicy.Abort;
import ThreadPoolRejectedPolicy.CallerRuns;
import ThreadPoolRejectedPolicy.Discard;
import ThreadPoolRejectedPolicy.DiscardOldest;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.util.concurrent.Rejectable;
import org.junit.Assert;
import org.junit.Test;


public class ThreadPoolRejectedPolicyTest extends TestSupport {
    @Test
    public void testAbortAsRejectedExecutionHandler() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(Abort.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockCallable<String> task1 = new ThreadPoolRejectedPolicyTest.MockCallable<>();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockCallable<String> task3 = new ThreadPoolRejectedPolicyTest.MockCallable<>();
        try {
            executorService.submit(task3);
            Assert.fail("Third task should have been rejected by a threadpool is full with 1 task and queue is full with 1 task.");
        } catch (RejectedExecutionException e) {
        }
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertInvoked(task2, result2);
        assertRejected(task3, null);
    }

    @Test
    public void testAbortAsRejectedExecutionHandlerWithRejectableTasks() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(Abort.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRejectableCallable<String> task2 = new ThreadPoolRejectedPolicyTest.MockRejectableCallable<>();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result3 = executorService.submit(task3);
        final ThreadPoolRejectedPolicyTest.MockRejectableCallable<String> task4 = new ThreadPoolRejectedPolicyTest.MockRejectableCallable<>();
        final Future<?> result4 = executorService.submit(task4);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertInvoked(task2, result2);
        assertRejected(task3, result3);
        assertRejected(task4, result4);
    }

    @Test
    public void testCallerRunsAsRejectedExecutionHandler() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(CallerRuns.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result3 = executorService.submit(task3);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertInvoked(task2, result2);
        assertInvoked(task3, result3);
    }

    @Test
    public void testCallerRunsAsRejectedExecutionHandlerWithRejectableTasks() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(CallerRuns.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result3 = executorService.submit(task3);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertInvoked(task2, result2);
        assertInvoked(task3, result3);
    }

    @Test
    public void testDiscardAsRejectedExecutionHandler() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(Discard.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result3 = executorService.submit(task3);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertInvoked(task2, result2);
        assertRejected(task3, result3);
    }

    @Test
    public void testDiscardAsRejectedExecutionHandlerWithRejectableTasks() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(Discard.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result3 = executorService.submit(task3);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertInvoked(task2, result2);
        assertRejected(task3, result3);
    }

    @Test
    public void testDiscardOldestAsRejectedExecutionHandler() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(DiscardOldest.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRunnable();
        final Future<?> result3 = executorService.submit(task3);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertRejected(task2, result2);
        assertInvoked(task3, result3);
    }

    @Test
    public void testDiscardOldestAsRejectedExecutionHandlerWithRejectableTasks() throws InterruptedException {
        final ExecutorService executorService = createTestExecutorService(DiscardOldest.asRejectedExecutionHandler());
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task1 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result1 = executorService.submit(task1);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task2 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result2 = executorService.submit(task2);
        final ThreadPoolRejectedPolicyTest.MockRejectableRunnable task3 = new ThreadPoolRejectedPolicyTest.MockRejectableRunnable();
        final Future<?> result3 = executorService.submit(task3);
        shutdownAndAwait(executorService);
        assertInvoked(task1, result1);
        assertRejected(task2, result2);
        assertInvoked(task3, result3);
    }

    private abstract static class MockTask {
        private final AtomicInteger invocationCount = new AtomicInteger();

        private final AtomicInteger rejectionCount = new AtomicInteger();

        public int getInvocationCount() {
            return invocationCount.get();
        }

        protected void countInvocation() {
            invocationCount.incrementAndGet();
        }

        public int getRejectionCount() {
            return rejectionCount.get();
        }

        protected void countRejection() {
            rejectionCount.incrementAndGet();
        }
    }

    private static class MockRunnable extends ThreadPoolRejectedPolicyTest.MockTask implements Runnable {
        @Override
        public void run() {
            countInvocation();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Assert.fail("MockRunnable task is not expected to be interrupted.");
            }
        }
    }

    private static class MockRejectableRunnable extends ThreadPoolRejectedPolicyTest.MockRunnable implements Rejectable {
        @Override
        public void reject() {
            countRejection();
        }
    }

    private static class MockCallable<T> extends ThreadPoolRejectedPolicyTest.MockTask implements Callable<T> {
        @Override
        public T call() throws Exception {
            countInvocation();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Assert.fail("MockCallable task is not expected to be interrupted.");
            }
            return null;
        }
    }

    private static class MockRejectableCallable<T> extends ThreadPoolRejectedPolicyTest.MockCallable<T> implements Rejectable {
        @Override
        public void reject() {
            countRejection();
        }
    }
}

