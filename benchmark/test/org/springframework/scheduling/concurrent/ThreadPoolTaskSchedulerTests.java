/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.scheduling.concurrent;


import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.ErrorHandler;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ThreadPoolTaskSchedulerTests extends AbstractSchedulingTaskExecutorTests {
    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    @Test
    public void executeFailingRunnableWithErrorHandler() {
        ThreadPoolTaskSchedulerTests.TestTask task = new ThreadPoolTaskSchedulerTests.TestTask(0);
        ThreadPoolTaskSchedulerTests.TestErrorHandler errorHandler = new ThreadPoolTaskSchedulerTests.TestErrorHandler(1);
        scheduler.setErrorHandler(errorHandler);
        scheduler.execute(task);
        await(errorHandler);
        Assert.assertNotNull(errorHandler.lastError);
    }

    @Test
    public void submitFailingRunnableWithErrorHandler() throws Exception {
        ThreadPoolTaskSchedulerTests.TestTask task = new ThreadPoolTaskSchedulerTests.TestTask(0);
        ThreadPoolTaskSchedulerTests.TestErrorHandler errorHandler = new ThreadPoolTaskSchedulerTests.TestErrorHandler(1);
        scheduler.setErrorHandler(errorHandler);
        Future<?> future = scheduler.submit(task);
        Object result = future.get(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(future.isDone());
        Assert.assertNull(result);
        Assert.assertNotNull(errorHandler.lastError);
    }

    @Test
    public void submitFailingCallableWithErrorHandler() throws Exception {
        ThreadPoolTaskSchedulerTests.TestCallable task = new ThreadPoolTaskSchedulerTests.TestCallable(0);
        ThreadPoolTaskSchedulerTests.TestErrorHandler errorHandler = new ThreadPoolTaskSchedulerTests.TestErrorHandler(1);
        scheduler.setErrorHandler(errorHandler);
        Future<String> future = scheduler.submit(task);
        Object result = future.get(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(future.isDone());
        Assert.assertNull(result);
        Assert.assertNotNull(errorHandler.lastError);
    }

    @Test
    public void scheduleOneTimeTask() throws Exception {
        ThreadPoolTaskSchedulerTests.TestTask task = new ThreadPoolTaskSchedulerTests.TestTask(1);
        Future<?> future = scheduler.schedule(task, new Date());
        Object result = future.get(1000, TimeUnit.MILLISECONDS);
        Assert.assertNull(result);
        Assert.assertTrue(future.isDone());
        assertThreadNamePrefix(task);
    }

    @Test(expected = ExecutionException.class)
    public void scheduleOneTimeFailingTaskWithoutErrorHandler() throws Exception {
        ThreadPoolTaskSchedulerTests.TestTask task = new ThreadPoolTaskSchedulerTests.TestTask(0);
        Future<?> future = scheduler.schedule(task, new Date());
        try {
            future.get(1000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException ex) {
            Assert.assertTrue(future.isDone());
            throw ex;
        }
    }

    @Test
    public void scheduleOneTimeFailingTaskWithErrorHandler() throws Exception {
        ThreadPoolTaskSchedulerTests.TestTask task = new ThreadPoolTaskSchedulerTests.TestTask(0);
        ThreadPoolTaskSchedulerTests.TestErrorHandler errorHandler = new ThreadPoolTaskSchedulerTests.TestErrorHandler(1);
        scheduler.setErrorHandler(errorHandler);
        Future<?> future = scheduler.schedule(task, new Date());
        Object result = future.get(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(future.isDone());
        Assert.assertNull(result);
        Assert.assertNotNull(errorHandler.lastError);
    }

    @Test
    public void scheduleTriggerTask() throws Exception {
        ThreadPoolTaskSchedulerTests.TestTask task = new ThreadPoolTaskSchedulerTests.TestTask(3);
        Future<?> future = scheduler.schedule(task, new ThreadPoolTaskSchedulerTests.TestTrigger(3));
        Object result = future.get(1000, TimeUnit.MILLISECONDS);
        Assert.assertNull(result);
        await(task);
        assertThreadNamePrefix(task);
    }

    @Test
    public void scheduleMultipleTriggerTasks() throws Exception {
        for (int i = 0; i < 1000; i++) {
            scheduleTriggerTask();
        }
    }

    private static class TestTask implements Runnable {
        private final int expectedRunCount;

        private final AtomicInteger actualRunCount = new AtomicInteger();

        private final CountDownLatch latch;

        private Thread lastThread;

        TestTask(int expectedRunCount) {
            this.expectedRunCount = expectedRunCount;
            this.latch = new CountDownLatch(expectedRunCount);
        }

        @Override
        public void run() {
            lastThread = Thread.currentThread();
            if ((actualRunCount.incrementAndGet()) > (expectedRunCount)) {
                throw new RuntimeException("intentional test failure");
            }
            latch.countDown();
        }
    }

    private static class TestCallable implements Callable<String> {
        private final int expectedRunCount;

        private final AtomicInteger actualRunCount = new AtomicInteger();

        TestCallable(int expectedRunCount) {
            this.expectedRunCount = expectedRunCount;
        }

        @Override
        public String call() throws Exception {
            if ((actualRunCount.incrementAndGet()) > (expectedRunCount)) {
                throw new RuntimeException("intentional test failure");
            }
            return Thread.currentThread().getName();
        }
    }

    private static class TestErrorHandler implements ErrorHandler {
        private final CountDownLatch latch;

        private volatile Throwable lastError;

        TestErrorHandler(int expectedErrorCount) {
            this.latch = new CountDownLatch(expectedErrorCount);
        }

        @Override
        public void handleError(Throwable t) {
            this.lastError = t;
            this.latch.countDown();
        }
    }

    private static class TestTrigger implements Trigger {
        private final int maxRunCount;

        private final AtomicInteger actualRunCount = new AtomicInteger();

        TestTrigger(int maxRunCount) {
            this.maxRunCount = maxRunCount;
        }

        @Override
        public Date nextExecutionTime(TriggerContext triggerContext) {
            if ((this.actualRunCount.incrementAndGet()) > (this.maxRunCount)) {
                return null;
            }
            return new Date();
        }
    }
}

