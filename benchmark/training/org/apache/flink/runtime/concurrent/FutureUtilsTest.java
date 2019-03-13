/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.concurrent;


import FutureUtils.RetryException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the utility methods in {@link FutureUtils}.
 */
public class FutureUtilsTest extends TestLogger {
    /**
     * Tests that we can retry an operation.
     */
    @Test
    public void testRetrySuccess() throws Exception {
        final int retries = 10;
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        CompletableFuture<Boolean> retryFuture = FutureUtils.retry(() -> CompletableFuture.supplyAsync(() -> {
            if ((atomicInteger.incrementAndGet()) == retries) {
                return true;
            } else {
                throw new CompletionException(new FlinkException("Test exception"));
            }
        }, TestingUtils.defaultExecutor()), retries, TestingUtils.defaultExecutor());
        Assert.assertTrue(retryFuture.get());
        Assert.assertEquals(retries, atomicInteger.get());
    }

    /**
     * Tests that a retry future is failed after all retries have been consumed.
     */
    @Test(expected = RetryException.class)
    public void testRetryFailure() throws Throwable {
        final int retries = 3;
        CompletableFuture<?> retryFuture = FutureUtils.retry(() -> FutureUtils.completedExceptionally(new FlinkException("Test exception")), retries, TestingUtils.defaultExecutor());
        try {
            retryFuture.get();
        } catch (ExecutionException ee) {
            throw ExceptionUtils.stripExecutionException(ee);
        }
    }

    /**
     * Tests that we can cancel a retry future.
     */
    @Test
    public void testRetryCancellation() throws Exception {
        final int retries = 10;
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final OneShotLatch notificationLatch = new OneShotLatch();
        final OneShotLatch waitLatch = new OneShotLatch();
        final AtomicReference<Throwable> atomicThrowable = new AtomicReference<>(null);
        CompletableFuture<?> retryFuture = FutureUtils.retry(() -> CompletableFuture.supplyAsync(() -> {
            if ((atomicInteger.incrementAndGet()) == 2) {
                notificationLatch.trigger();
                try {
                    waitLatch.await();
                } catch ( e) {
                    atomicThrowable.compareAndSet(null, e);
                }
            }
            throw new CompletionException(new FlinkException("Test exception"));
        }, TestingUtils.defaultExecutor()), retries, TestingUtils.defaultExecutor());
        // await that we have failed once
        notificationLatch.await();
        Assert.assertFalse(retryFuture.isDone());
        // cancel the retry future
        retryFuture.cancel(false);
        // let the retry operation continue
        waitLatch.trigger();
        Assert.assertTrue(retryFuture.isCancelled());
        Assert.assertEquals(2, atomicInteger.get());
        if ((atomicThrowable.get()) != null) {
            throw new FlinkException("Exception occurred in the retry operation.", atomicThrowable.get());
        }
    }

    /**
     * Tests that retry with delay fails after having exceeded all retries.
     */
    @Test(expected = RetryException.class)
    public void testRetryWithDelayFailure() throws Throwable {
        CompletableFuture<?> retryFuture = FutureUtils.retryWithDelay(() -> FutureUtils.completedExceptionally(new FlinkException("Test exception")), 3, Time.milliseconds(1L), TestingUtils.defaultScheduledExecutor());
        try {
            retryFuture.get(TestingUtils.TIMEOUT().toMilliseconds(), TimeUnit.MILLISECONDS);
        } catch (ExecutionException ee) {
            throw ExceptionUtils.stripExecutionException(ee);
        }
    }

    /**
     * Tests that the delay is respected between subsequent retries of a retry future with retry delay.
     */
    @Test
    public void testRetryWithDelay() throws Exception {
        final int retries = 4;
        final Time delay = Time.milliseconds(5L);
        final AtomicInteger countDown = new AtomicInteger(retries);
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> retryFuture = FutureUtils.retryWithDelay(() -> {
            if ((countDown.getAndDecrement()) == 0) {
                return CompletableFuture.completedFuture(true);
            } else {
                return FutureUtils.completedExceptionally(new FlinkException("Test exception."));
            }
        }, retries, delay, TestingUtils.defaultScheduledExecutor());
        Boolean result = retryFuture.get();
        long completionTime = (System.currentTimeMillis()) - start;
        Assert.assertTrue(result);
        Assert.assertTrue("The completion time should be at least rertries times delay between retries.", (completionTime >= (retries * (delay.toMilliseconds()))));
    }

    /**
     * Tests that all scheduled tasks are canceled if the retry future is being cancelled.
     */
    @Test
    public void testRetryWithDelayCancellation() {
        final ManuallyTriggeredScheduledExecutor scheduledExecutor = new ManuallyTriggeredScheduledExecutor();
        CompletableFuture<?> retryFuture = FutureUtils.retryWithDelay(() -> FutureUtils.completedExceptionally(new FlinkException("Test exception")), 1, TestingUtils.infiniteTime(), scheduledExecutor);
        Assert.assertFalse(retryFuture.isDone());
        final Collection<ScheduledFuture<?>> scheduledTasks = scheduledExecutor.getScheduledTasks();
        Assert.assertFalse(scheduledTasks.isEmpty());
        final ScheduledFuture<?> scheduledFuture = scheduledTasks.iterator().next();
        Assert.assertFalse(scheduledFuture.isDone());
        retryFuture.cancel(false);
        Assert.assertTrue(retryFuture.isCancelled());
        Assert.assertTrue(scheduledFuture.isCancelled());
    }

    /**
     * Tests that a future is timed out after the specified timeout.
     */
    @Test
    public void testOrTimeout() throws Exception {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final long timeout = 10L;
        FutureUtils.orTimeout(future, timeout, TimeUnit.MILLISECONDS);
        try {
            future.get();
        } catch (ExecutionException e) {
            Assert.assertTrue(((ExceptionUtils.stripExecutionException(e)) instanceof TimeoutException));
        }
    }

    @Test
    public void testRetryWithDelayAndPredicate() throws Exception {
        final ScheduledExecutorService retryExecutor = Executors.newSingleThreadScheduledExecutor();
        final String retryableExceptionMessage = "first exception";
        class TestStringSupplier implements Supplier<CompletableFuture<String>> {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public CompletableFuture<String> get() {
                if ((counter.getAndIncrement()) == 0) {
                    return FutureUtils.completedExceptionally(new RuntimeException(retryableExceptionMessage));
                } else {
                    return FutureUtils.completedExceptionally(new RuntimeException("should propagate"));
                }
            }
        }
        try {
            FutureUtils.retryWithDelay(new TestStringSupplier(), 1, Time.seconds(0), ( throwable) -> (throwable instanceof RuntimeException) && (throwable.getMessage().contains(retryableExceptionMessage)), new ScheduledExecutorServiceAdapter(retryExecutor)).get();
        } catch (final ExecutionException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("should propagate"));
        } finally {
            retryExecutor.shutdownNow();
        }
    }

    @Test
    public void testRunAfterwards() throws Exception {
        final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
        final OneShotLatch runnableLatch = new OneShotLatch();
        final CompletableFuture<Void> runFuture = FutureUtils.runAfterwards(inputFuture, runnableLatch::trigger);
        Assert.assertThat(runnableLatch.isTriggered(), Matchers.is(false));
        Assert.assertThat(runFuture.isDone(), Matchers.is(false));
        inputFuture.complete(null);
        Assert.assertThat(runnableLatch.isTriggered(), Matchers.is(true));
        Assert.assertThat(runFuture.isDone(), Matchers.is(true));
        // check that this future is not exceptionally completed
        runFuture.get();
    }

    @Test
    public void testRunAfterwardsExceptional() throws Exception {
        final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
        final OneShotLatch runnableLatch = new OneShotLatch();
        final FlinkException testException = new FlinkException("Test exception");
        final CompletableFuture<Void> runFuture = FutureUtils.runAfterwards(inputFuture, runnableLatch::trigger);
        Assert.assertThat(runnableLatch.isTriggered(), Matchers.is(false));
        Assert.assertThat(runFuture.isDone(), Matchers.is(false));
        inputFuture.completeExceptionally(testException);
        Assert.assertThat(runnableLatch.isTriggered(), Matchers.is(true));
        Assert.assertThat(runFuture.isDone(), Matchers.is(true));
        try {
            runFuture.get();
            Assert.fail("Expected an exceptional completion");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.is(testException));
        }
    }

    @Test
    public void testComposeAfterwards() throws InterruptedException, ExecutionException {
        final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
        final OneShotLatch composeLatch = new OneShotLatch();
        final CompletableFuture<Void> composeFuture = FutureUtils.composeAfterwards(inputFuture, () -> {
            composeLatch.trigger();
            return CompletableFuture.completedFuture(null);
        });
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(false));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(false));
        inputFuture.complete(null);
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(true));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(true));
        // check that tthis future is not exceptionally completed
        composeFuture.get();
    }

    @Test
    public void testComposeAfterwardsFirstExceptional() throws InterruptedException {
        final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
        final OneShotLatch composeLatch = new OneShotLatch();
        final FlinkException testException = new FlinkException("Test exception");
        final CompletableFuture<Void> composeFuture = FutureUtils.composeAfterwards(inputFuture, () -> {
            composeLatch.trigger();
            return CompletableFuture.completedFuture(null);
        });
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(false));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(false));
        inputFuture.completeExceptionally(testException);
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(true));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(true));
        // check that this future is not exceptionally completed
        try {
            composeFuture.get();
            Assert.fail("Expected an exceptional completion");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.is(testException));
        }
    }

    @Test
    public void testComposeAfterwardsSecondExceptional() throws InterruptedException {
        final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
        final OneShotLatch composeLatch = new OneShotLatch();
        final FlinkException testException = new FlinkException("Test exception");
        final CompletableFuture<Void> composeFuture = FutureUtils.composeAfterwards(inputFuture, () -> {
            composeLatch.trigger();
            return FutureUtils.completedExceptionally(testException);
        });
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(false));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(false));
        inputFuture.complete(null);
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(true));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(true));
        // check that this future is not exceptionally completed
        try {
            composeFuture.get();
            Assert.fail("Expected an exceptional completion");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.is(testException));
        }
    }

    @Test
    public void testComposeAfterwardsBothExceptional() throws InterruptedException {
        final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
        final FlinkException testException1 = new FlinkException("Test exception1");
        final FlinkException testException2 = new FlinkException("Test exception2");
        final OneShotLatch composeLatch = new OneShotLatch();
        final CompletableFuture<Void> composeFuture = FutureUtils.composeAfterwards(inputFuture, () -> {
            composeLatch.trigger();
            return FutureUtils.completedExceptionally(testException2);
        });
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(false));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(false));
        inputFuture.completeExceptionally(testException1);
        Assert.assertThat(composeLatch.isTriggered(), Matchers.is(true));
        Assert.assertThat(composeFuture.isDone(), Matchers.is(true));
        // check that this future is not exceptionally completed
        try {
            composeFuture.get();
            Assert.fail("Expected an exceptional completion");
        } catch (ExecutionException ee) {
            final Throwable actual = ExceptionUtils.stripExecutionException(ee);
            Assert.assertThat(actual, Matchers.is(testException1));
            Assert.assertThat(actual.getSuppressed(), Matchers.arrayWithSize(1));
            Assert.assertThat(actual.getSuppressed()[0], Matchers.is(testException2));
        }
    }

    @Test
    public void testCompleteAll() throws Exception {
        final CompletableFuture<String> inputFuture1 = new CompletableFuture<>();
        final CompletableFuture<Integer> inputFuture2 = new CompletableFuture<>();
        final List<CompletableFuture<?>> futuresToComplete = Arrays.asList(inputFuture1, inputFuture2);
        final FutureUtils.ConjunctFuture<Void> completeFuture = FutureUtils.completeAll(futuresToComplete);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(false));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(0));
        Assert.assertThat(completeFuture.getNumFuturesTotal(), Matchers.is(futuresToComplete.size()));
        inputFuture2.complete(42);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(false));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(1));
        inputFuture1.complete("foobar");
        Assert.assertThat(completeFuture.isDone(), Matchers.is(true));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(2));
        completeFuture.get();
    }

    @Test
    public void testCompleteAllPartialExceptional() throws Exception {
        final CompletableFuture<String> inputFuture1 = new CompletableFuture<>();
        final CompletableFuture<Integer> inputFuture2 = new CompletableFuture<>();
        final List<CompletableFuture<?>> futuresToComplete = Arrays.asList(inputFuture1, inputFuture2);
        final FutureUtils.ConjunctFuture<Void> completeFuture = FutureUtils.completeAll(futuresToComplete);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(false));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(0));
        Assert.assertThat(completeFuture.getNumFuturesTotal(), Matchers.is(futuresToComplete.size()));
        final FlinkException testException1 = new FlinkException("Test exception 1");
        inputFuture2.completeExceptionally(testException1);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(false));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(1));
        inputFuture1.complete("foobar");
        Assert.assertThat(completeFuture.isDone(), Matchers.is(true));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(2));
        try {
            completeFuture.get();
            Assert.fail("Expected an exceptional completion");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.is(testException1));
        }
    }

    @Test
    public void testCompleteAllExceptional() throws Exception {
        final CompletableFuture<String> inputFuture1 = new CompletableFuture<>();
        final CompletableFuture<Integer> inputFuture2 = new CompletableFuture<>();
        final List<CompletableFuture<?>> futuresToComplete = Arrays.asList(inputFuture1, inputFuture2);
        final FutureUtils.ConjunctFuture<Void> completeFuture = FutureUtils.completeAll(futuresToComplete);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(false));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(0));
        Assert.assertThat(completeFuture.getNumFuturesTotal(), Matchers.is(futuresToComplete.size()));
        final FlinkException testException1 = new FlinkException("Test exception 1");
        inputFuture1.completeExceptionally(testException1);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(false));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(1));
        final FlinkException testException2 = new FlinkException("Test exception 2");
        inputFuture2.completeExceptionally(testException2);
        Assert.assertThat(completeFuture.isDone(), Matchers.is(true));
        Assert.assertThat(completeFuture.getNumFuturesCompleted(), Matchers.is(2));
        try {
            completeFuture.get();
            Assert.fail("Expected an exceptional completion");
        } catch (ExecutionException ee) {
            final Throwable actual = ExceptionUtils.stripExecutionException(ee);
            final Throwable[] suppressed = actual.getSuppressed();
            final FlinkException suppressedException;
            if (actual.equals(testException1)) {
                suppressedException = testException2;
            } else {
                suppressedException = testException1;
            }
            Assert.assertThat(suppressed, Matchers.is(Matchers.not(Matchers.emptyArray())));
            Assert.assertThat(suppressed, Matchers.arrayContaining(suppressedException));
        }
    }

    @Test
    public void testSupplyAsyncFailure() throws Exception {
        final String exceptionMessage = "Test exception";
        final FlinkException testException = new FlinkException(exceptionMessage);
        final CompletableFuture<Object> future = FutureUtils.supplyAsync(() -> {
            throw testException;
        }, TestingUtils.defaultExecutor());
        try {
            future.get();
            Assert.fail("Expected an exception.");
        } catch (ExecutionException e) {
            Assert.assertThat(ExceptionUtils.findThrowableWithMessage(e, exceptionMessage).isPresent(), Matchers.is(true));
        }
    }

    @Test
    public void testSupplyAsync() throws Exception {
        final CompletableFuture<Acknowledge> future = FutureUtils.supplyAsync(Acknowledge::get, TestingUtils.defaultExecutor());
        Assert.assertThat(future.get(), Matchers.is(Acknowledge.get()));
    }

    @Test
    public void testHandleAsyncIfNotDone() {
        testFutureContinuation((CompletableFuture<?> future,Executor executor) -> FutureUtils.handleAsyncIfNotDone(future, executor, ( o, t) -> null));
    }

    @Test
    public void testApplyAsyncIfNotDone() {
        testFutureContinuation((CompletableFuture<?> future,Executor executor) -> FutureUtils.thenApplyAsyncIfNotDone(future, executor, ( o) -> null));
    }

    @Test
    public void testComposeAsyncIfNotDone() {
        testFutureContinuation((CompletableFuture<?> future,Executor executor) -> FutureUtils.thenComposeAsyncIfNotDone(future, executor, ( o) -> null));
    }

    @Test
    public void testWhenCompleteAsyncIfNotDone() {
        testFutureContinuation((CompletableFuture<?> future,Executor executor) -> FutureUtils.whenCompleteAsyncIfNotDone(future, executor, ( o, throwable) -> {
        }));
    }

    @Test
    public void testThenAcceptAsyncIfNotDone() {
        testFutureContinuation((CompletableFuture<?> future,Executor executor) -> FutureUtils.thenAcceptAsyncIfNotDone(future, executor, ( o) -> {
        }));
    }
}

