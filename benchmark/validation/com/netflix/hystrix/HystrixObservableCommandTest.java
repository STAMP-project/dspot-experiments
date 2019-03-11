/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix;


import AbstractCommand.TryableSemaphore;
import ExecutionIsolationStrategy.SEMAPHORE;
import ExecutionIsolationStrategy.THREAD;
import HystrixCommandGroupKey.Factory;
import HystrixEventType.EMIT;
import HystrixEventType.FAILURE;
import HystrixEventType.FALLBACK_EMIT;
import HystrixEventType.FALLBACK_FAILURE;
import HystrixEventType.FALLBACK_MISSING;
import HystrixEventType.FALLBACK_SUCCESS;
import HystrixEventType.RESPONSE_FROM_CACHE;
import HystrixEventType.SEMAPHORE_REJECTED;
import HystrixEventType.SHORT_CIRCUITED;
import HystrixEventType.SUCCESS;
import HystrixEventType.THREAD_POOL_REJECTED;
import HystrixEventType.TIMEOUT;
import HystrixRuntimeException.FailureType;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.hystrix.AbstractCommand.TryableSemaphoreActual;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static com.netflix.hystrix.AbstractTestHystrixCommand.CacheEnabled.YES;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.ASYNC_BAD_REQUEST;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.ASYNC_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.ASYNC_HYSTRIX_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.ASYNC_RECOVERABLE_ERROR;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.ASYNC_UNRECOVERABLE_ERROR;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.BAD_REQUEST;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.HYSTRIX_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.MULTIPLE_EMITS_THEN_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.MULTIPLE_EMITS_THEN_SUCCESS;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.NOT_WRAPPED_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.RECOVERABLE_ERROR;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.SUCCESS;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.UNRECOVERABLE_ERROR;
import static com.netflix.hystrix.AbstractTestHystrixCommand.FallbackResult.FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.FallbackResult.UNIMPLEMENTED;
import static com.netflix.hystrix.InspectableBuilder.CommandGroupForUnitTest.OWNER_ONE;
import static com.netflix.hystrix.InspectableBuilder.CommandKeyForUnitTest.KEY_ONE;
import static com.netflix.hystrix.InspectableBuilder.CommandKeyForUnitTest.KEY_TWO;


public class HystrixObservableCommandTest extends CommonHystrixCommandTests<TestHystrixObservableCommand<Integer>> {
    @Rule
    public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

    class CompletableCommand extends HystrixObservableCommand<Integer> {
        CompletableCommand() {
            super(Setter.withGroupKey(Factory.asKey("COMPLETABLE")));
        }

        @Override
        protected Observable<Integer> construct() {
            return Completable.complete().toObservable();
        }
    }

    @Test
    public void testCompletable() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final HystrixObservableCommand<Integer> command = new HystrixObservableCommandTest.CompletableCommand();
        command.observe().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " OnCompleted"));
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " OnError : ") + e));
                latch.countDown();
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " OnNext : ") + integer));
            }
        });
        latch.await();
        Assert.assertEquals(null, command.getFailedExecutionException());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isSuccessfulExecution());
        Assert.assertFalse(command.isResponseFromFallback());
        assertCommandExecutionEvents(command, SUCCESS);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        Assert.assertNull(command.getExecutionException());
    }

    /**
     * Test a successful semaphore-isolated command execution.
     */
    @Test
    public void testSemaphoreObserveSuccess() {
        testObserveSuccess(SEMAPHORE);
    }

    /**
     * Test a successful thread-isolated command execution.
     */
    @Test
    public void testThreadObserveSuccess() {
        testObserveSuccess(THREAD);
    }

    /**
     * Test that a semaphore command can not be executed multiple times.
     */
    @Test
    public void testSemaphoreIsolatedObserveMultipleTimes() {
        testObserveMultipleTimes(SEMAPHORE);
    }

    /**
     * Test that a thread command can not be executed multiple times.
     */
    @Test
    public void testThreadIsolatedObserveMultipleTimes() {
        testObserveMultipleTimes(THREAD);
    }

    /**
     * Test a semaphore command execution that throws an HystrixException synchronously and didn't implement getFallback.
     */
    @Test
    public void testSemaphoreIsolatedObserveKnownSyncFailureWithNoFallback() {
        testObserveKnownFailureWithNoFallback(SEMAPHORE, false);
    }

    /**
     * Test a semaphore command execution that throws an HystrixException asynchronously and didn't implement getFallback.
     */
    @Test
    public void testSemaphoreIsolatedObserveKnownAsyncFailureWithNoFallback() {
        testObserveKnownFailureWithNoFallback(SEMAPHORE, true);
    }

    /**
     * Test a thread command execution that throws an HystrixException synchronously and didn't implement getFallback.
     */
    @Test
    public void testThreadIsolatedObserveKnownSyncFailureWithNoFallback() {
        testObserveKnownFailureWithNoFallback(THREAD, false);
    }

    /**
     * Test a thread command execution that throws an HystrixException asynchronously and didn't implement getFallback.
     */
    @Test
    public void testThreadIsolatedObserveKnownAsyncFailureWithNoFallback() {
        testObserveKnownFailureWithNoFallback(THREAD, true);
    }

    /**
     * Test a semaphore command execution that throws an unknown exception (not HystrixException) synchronously and didn't implement getFallback.
     */
    @Test
    public void testSemaphoreIsolatedObserveUnknownSyncFailureWithNoFallback() {
        testObserveUnknownFailureWithNoFallback(SEMAPHORE, false);
    }

    /**
     * Test a semaphore command execution that throws an unknown exception (not HystrixException) asynchronously and didn't implement getFallback.
     */
    @Test
    public void testSemaphoreIsolatedObserveUnknownAsyncFailureWithNoFallback() {
        testObserveUnknownFailureWithNoFallback(SEMAPHORE, true);
    }

    /**
     * Test a thread command execution that throws an unknown exception (not HystrixException) synchronously and didn't implement getFallback.
     */
    @Test
    public void testThreadIsolatedObserveUnknownSyncFailureWithNoFallback() {
        testObserveUnknownFailureWithNoFallback(THREAD, false);
    }

    /**
     * Test a thread command execution that throws an unknown exception (not HystrixException) asynchronously and didn't implement getFallback.
     */
    @Test
    public void testThreadIsolatedObserveUnknownAsyncFailureWithNoFallback() {
        testObserveUnknownFailureWithNoFallback(THREAD, true);
    }

    /**
     * Test a semaphore command execution that fails synchronously but has a fallback.
     */
    @Test
    public void testSemaphoreIsolatedObserveSyncFailureWithFallback() {
        testObserveFailureWithFallback(SEMAPHORE, false);
    }

    /**
     * Test a semaphore command execution that fails asynchronously but has a fallback.
     */
    @Test
    public void testSemaphoreIsolatedObserveAsyncFailureWithFallback() {
        testObserveFailureWithFallback(SEMAPHORE, true);
    }

    /**
     * Test a thread command execution that fails synchronously but has a fallback.
     */
    @Test
    public void testThreadIsolatedObserveSyncFailureWithFallback() {
        testObserveFailureWithFallback(THREAD, false);
    }

    /**
     * Test a thread command execution that fails asynchronously but has a fallback.
     */
    @Test
    public void testThreadIsolatedObserveAsyncFailureWithFallback() {
        testObserveFailureWithFallback(THREAD, true);
    }

    /**
     * Test a command execution that fails synchronously, has getFallback implemented but that fails as well (synchronously).
     */
    @Test
    public void testSemaphoreIsolatedObserveSyncFailureWithSyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(SEMAPHORE, false, false);
    }

    /**
     * Test a command execution that fails synchronously, has getFallback implemented but that fails as well (asynchronously).
     */
    @Test
    public void testSemaphoreIsolatedObserveSyncFailureWithAsyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(SEMAPHORE, false, true);
    }

    /**
     * Test a command execution that fails asynchronously, has getFallback implemented but that fails as well (synchronously).
     */
    @Test
    public void testSemaphoreIsolatedObserveAyncFailureWithSyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(SEMAPHORE, true, false);
    }

    /**
     * Test a command execution that fails asynchronously, has getFallback implemented but that fails as well (asynchronously).
     */
    @Test
    public void testSemaphoreIsolatedObserveAsyncFailureWithAsyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(SEMAPHORE, true, true);
    }

    /**
     * Test a command execution that fails synchronously, has getFallback implemented but that fails as well (synchronously).
     */
    @Test
    public void testThreadIsolatedObserveSyncFailureWithSyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(THREAD, false, false);
    }

    /**
     * Test a command execution that fails synchronously, has getFallback implemented but that fails as well (asynchronously).
     */
    @Test
    public void testThreadIsolatedObserveSyncFailureWithAsyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(THREAD, true, false);
    }

    /**
     * Test a command execution that fails asynchronously, has getFallback implemented but that fails as well (synchronously).
     */
    @Test
    public void testThreadIsolatedObserveAyncFailureWithSyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(THREAD, false, true);
    }

    /**
     * Test a command execution that fails asynchronously, has getFallback implemented but that fails as well (asynchronously).
     */
    @Test
    public void testThreadIsolatedObserveAsyncFailureWithAsyncFallbackFailure() {
        testObserveFailureWithFallbackFailure(THREAD, true, true);
    }

    /**
     * Test a semaphore command execution that times out with a fallback and eventually succeeds.
     */
    @Test
    public void testSemaphoreIsolatedObserveTimeoutWithSuccessAndFallback() {
        testObserveFailureWithTimeoutAndFallback(SEMAPHORE, MULTIPLE_EMITS_THEN_SUCCESS);
    }

    /**
     * Test a semaphore command execution that times out with a fallback and eventually fails.
     */
    @Test
    public void testSemaphoreIsolatedObserveTimeoutWithFailureAndFallback() {
        testObserveFailureWithTimeoutAndFallback(SEMAPHORE, MULTIPLE_EMITS_THEN_FAILURE);
    }

    /**
     * Test a thread command execution that times out with a fallback and eventually succeeds.
     */
    @Test
    public void testThreadIsolatedObserveTimeoutWithSuccessAndFallback() {
        testObserveFailureWithTimeoutAndFallback(THREAD, MULTIPLE_EMITS_THEN_SUCCESS);
    }

    /**
     * Test a thread command execution that times out with a fallback and eventually fails.
     */
    @Test
    public void testThreadIsolatedObserveTimeoutWithFailureAndFallback() {
        testObserveFailureWithTimeoutAndFallback(THREAD, MULTIPLE_EMITS_THEN_FAILURE);
    }

    /**
     * Test a successful command execution.
     */
    @Test
    public void testObserveOnImmediateSchedulerByDefaultForSemaphoreIsolation() throws Exception {
        final AtomicReference<Thread> commandThread = new AtomicReference<Thread>();
        final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
        TestHystrixObservableCommand<Boolean> command = new TestHystrixObservableCommand<Boolean>(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE))) {
            @Override
            protected Observable<Boolean> construct() {
                commandThread.set(Thread.currentThread());
                return Observable.just(true);
            }
        };
        final CountDownLatch latch = new CountDownLatch(1);
        command.toObservable().subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
                e.printStackTrace();
            }

            @Override
            public void onNext(Boolean args) {
                subscribeThread.set(Thread.currentThread());
            }
        });
        if (!(latch.await(2000, TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out");
        }
        Assert.assertNotNull(commandThread.get());
        Assert.assertNotNull(subscribeThread.get());
        System.out.println(("Command Thread: " + (commandThread.get())));
        System.out.println(("Subscribe Thread: " + (subscribeThread.get())));
        String mainThreadName = Thread.currentThread().getName();
        // semaphore should be on the calling thread
        Assert.assertTrue(commandThread.get().getName().equals(mainThreadName));
        System.out.println(((("testObserveOnImmediateSchedulerByDefaultForSemaphoreIsolation: " + (subscribeThread.get())) + " => ") + mainThreadName));
        Assert.assertTrue(subscribeThread.get().getName().equals(mainThreadName));
        // semaphore isolated
        Assert.assertFalse(command.isExecutedInThread());
        assertCommandExecutionEvents(command, EMIT, SUCCESS);
        assertSaneHystrixRequestLog(1);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        Assert.assertNull(command.getExecutionException());
        Assert.assertFalse(command.isResponseFromFallback());
    }

    /**
     * Test that the circuit-breaker will 'trip' and prevent command execution on subsequent calls.
     */
    @Test
    public void testCircuitBreakerTripsAfterFailures() throws InterruptedException {
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("KnownFailureTestCommandWithFallback");
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker(commandKey);
        /* fail 3 times and then it should trip the circuit and stop executing */
        // failure 1
        HystrixObservableCommandTest.KnownFailureTestCommandWithFallback attempt1 = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE, true);
        observe().toBlocking().single();
        Thread.sleep(100);
        Assert.assertTrue(isResponseFromFallback());
        Assert.assertFalse(isCircuitBreakerOpen());
        Assert.assertFalse(isResponseShortCircuited());
        // failure 2
        HystrixObservableCommandTest.KnownFailureTestCommandWithFallback attempt2 = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE, true);
        observe().toBlocking().single();
        Thread.sleep(100);
        Assert.assertTrue(isResponseFromFallback());
        Assert.assertFalse(isCircuitBreakerOpen());
        Assert.assertFalse(isResponseShortCircuited());
        // failure 3
        HystrixObservableCommandTest.KnownFailureTestCommandWithFallback attempt3 = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE, true);
        observe().toBlocking().single();
        Thread.sleep(100);
        Assert.assertTrue(isResponseFromFallback());
        Assert.assertFalse(isResponseShortCircuited());
        // it should now be 'open' and prevent further executions
        Assert.assertTrue(isCircuitBreakerOpen());
        // attempt 4
        HystrixObservableCommandTest.KnownFailureTestCommandWithFallback attempt4 = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE, true);
        observe().toBlocking().single();
        Thread.sleep(100);
        Assert.assertTrue(isResponseFromFallback());
        // this should now be true as the response will be short-circuited
        Assert.assertTrue(isResponseShortCircuited());
        // this should remain open
        Assert.assertTrue(isCircuitBreakerOpen());
        assertCommandExecutionEvents(attempt1, FAILURE, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(attempt2, FAILURE, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(attempt3, FAILURE, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(attempt4, SHORT_CIRCUITED, FALLBACK_EMIT, FALLBACK_SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        Assert.assertEquals(4, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    /**
     * Test that the circuit-breaker being disabled doesn't wreak havoc.
     */
    @Test
    public void testExecutionSuccessWithCircuitBreakerDisabled() {
        TestHystrixObservableCommand<Boolean> command = new HystrixObservableCommandTest.TestCommandWithoutCircuitBreaker();
        try {
            Assert.assertEquals(true, command.observe().toBlocking().single());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We received an exception.");
        }
        assertCommandExecutionEvents(command, EMIT, SUCCESS);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        Assert.assertNull(command.getExecutionException());
    }

    /**
     * Test a command execution timeout where the command didn't implement getFallback.
     */
    @Test
    public void testExecutionTimeoutWithNoFallbackUsingSemaphoreIsolation() {
        TestHystrixObservableCommand<Integer> command = getCommand(SEMAPHORE, SUCCESS, 200, UNIMPLEMENTED, 100);
        try {
            command.observe().toBlocking().single();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof HystrixRuntimeException) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertTrue(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
            } else {
                Assert.fail("the exception should be HystrixRuntimeException");
            }
        }
        // the time should be 50+ since we timeout at 50ms
        Assert.assertTrue(("Execution Time is: " + (command.getExecutionTimeInMilliseconds())), ((command.getExecutionTimeInMilliseconds()) >= 50));
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertFalse(command.isResponseFromFallback());
        Assert.assertFalse(command.isResponseRejected());
        Assert.assertNotNull(command.getExecutionException());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_MISSING);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        // semaphore isolated
        Assert.assertFalse(command.isExecutedInThread());
    }

    /**
     * Test a command execution timeout where the command implemented getFallback.
     */
    @Test
    public void testExecutionTimeoutWithFallbackUsingSemaphoreIsolation() {
        TestHystrixObservableCommand<Integer> command = getCommand(SEMAPHORE, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 50);
        try {
            Assert.assertEquals(HystrixObservableCommandTest.FlexibleTestHystrixObservableCommand.FALLBACK_VALUE, command.observe().toBlocking().single());
            // the time should be 50+ since we timeout at 50ms
            Assert.assertTrue(("Execution Time is: " + (command.getExecutionTimeInMilliseconds())), ((command.getExecutionTimeInMilliseconds()) >= 50));
            Assert.assertTrue(command.isResponseTimedOut());
            Assert.assertTrue(command.isResponseFromFallback());
            Assert.assertNotNull(command.getExecutionException());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We should have received a response from the fallback.");
        }
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        // semaphore isolated
        Assert.assertFalse(command.isExecutedInThread());
    }

    /**
     * Test a command execution timeout where the command implemented getFallback but it fails.
     */
    @Test
    public void testExecutionTimeoutFallbackFailureUsingSemaphoreIsolation() {
        TestHystrixObservableCommand<Integer> command = getCommand(SEMAPHORE, SUCCESS, 500, FAILURE, 200);
        try {
            command.observe().toBlocking().single();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            if (e instanceof HystrixRuntimeException) {
                e.printStackTrace();
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertFalse(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
                Assert.assertNotNull(command.getExecutionException());
            } else {
                Assert.fail("the exception should be HystrixRuntimeException");
            }
        }
        // the time should be 200+ since we timeout at 200ms
        Assert.assertTrue(("Execution Time is: " + (command.getExecutionTimeInMilliseconds())), ((command.getExecutionTimeInMilliseconds()) >= 200));
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_FAILURE);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        // semaphore isolated
        Assert.assertFalse(command.isExecutedInThread());
    }

    /**
     * Test a semaphore command execution timeout where the command didn't implement getFallback.
     */
    @Test
    public void testSemaphoreExecutionTimeoutWithNoFallback() {
        testExecutionTimeoutWithNoFallback(SEMAPHORE);
    }

    /**
     * Test a thread command execution timeout where the command didn't implement getFallback.
     */
    @Test
    public void testThreadExecutionTimeoutWithNoFallback() {
        testExecutionTimeoutWithNoFallback(THREAD);
    }

    /**
     * Test a semaphore command execution timeout where the command implemented getFallback.
     */
    @Test
    public void testSemaphoreIsolatedExecutionTimeoutWithSuccessfulFallback() {
        testExecutionTimeoutWithSuccessfulFallback(SEMAPHORE);
    }

    /**
     * Test a thread command execution timeout where the command implemented getFallback.
     */
    @Test
    public void testThreadIsolatedExecutionTimeoutWithSuccessfulFallback() {
        testExecutionTimeoutWithSuccessfulFallback(THREAD);
    }

    /**
     * Test a semaphore command execution timeout where the command implemented getFallback but it fails synchronously.
     */
    @Test
    public void testSemaphoreExecutionTimeoutSyncFallbackFailure() {
        testExecutionTimeoutFallbackFailure(SEMAPHORE, false);
    }

    /**
     * Test a semaphore command execution timeout where the command implemented getFallback but it fails asynchronously.
     */
    @Test
    public void testSemaphoreExecutionTimeoutAsyncFallbackFailure() {
        testExecutionTimeoutFallbackFailure(SEMAPHORE, true);
    }

    /**
     * Test a thread command execution timeout where the command implemented getFallback but it fails synchronously.
     */
    @Test
    public void testThreadExecutionTimeoutSyncFallbackFailure() {
        testExecutionTimeoutFallbackFailure(THREAD, false);
    }

    /**
     * Test a thread command execution timeout where the command implemented getFallback but it fails asynchronously.
     */
    @Test
    public void testThreadExecutionTimeoutAsyncFallbackFailure() {
        testExecutionTimeoutFallbackFailure(THREAD, true);
    }

    /**
     * Test that the circuit-breaker counts a command execution timeout as a 'timeout' and not just failure.
     */
    @Test
    public void testShortCircuitFallbackCounter() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker().setForceShortCircuit(true);
        HystrixObservableCommandTest.KnownFailureTestCommandWithFallback command1 = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE, true);
        HystrixObservableCommandTest.KnownFailureTestCommandWithFallback command2 = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE, true);
        try {
            observe().toBlocking().single();
            observe().toBlocking().single();
            // will be -1 because it never attempted execution
            Assert.assertEquals((-1), getExecutionTimeInMilliseconds());
            Assert.assertTrue(isResponseShortCircuited());
            Assert.assertFalse(isResponseTimedOut());
            Assert.assertNotNull(getExecutionException());
            // semaphore isolated
            Assert.assertFalse(isExecutedInThread());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We should have received a response from the fallback.");
        }
        assertCommandExecutionEvents(command1, SHORT_CIRCUITED, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(command2, SHORT_CIRCUITED, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertSaneHystrixRequestLog(2);
    }

    @Test
    public void testExecutionSemaphoreWithObserve() {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.TestSemaphoreCommand command1 = new HystrixObservableCommandTest.TestSemaphoreCommand(circuitBreaker, 1, 200, HystrixObservableCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        // single thread should work
        try {
            boolean result = observe().toBlocking().toFuture().get();
            Assert.assertTrue(result);
        } catch (Exception e) {
            // we shouldn't fail on this one
            throw new RuntimeException(e);
        }
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final TryableSemaphoreActual semaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(1));
        final HystrixObservableCommandTest.TestSemaphoreCommand command2 = new HystrixObservableCommandTest.TestSemaphoreCommand(circuitBreaker, semaphore, 200, HystrixObservableCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    observe().toBlocking().toFuture().get();
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        final HystrixObservableCommandTest.TestSemaphoreCommand command3 = new HystrixObservableCommandTest.TestSemaphoreCommand(circuitBreaker, semaphore, 200, HystrixObservableCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        Runnable r3 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    observe().toBlocking().toFuture().get();
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        // 2 threads, the second should be rejected by the semaphore
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);
        t2.start();
        try {
            Thread.sleep(100);
        } catch (Throwable ex) {
            Assert.fail(ex.getMessage());
        }
        t3.start();
        try {
            t2.join();
            t3.join();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("failed waiting on threads");
        }
        if (!(exceptionReceived.get())) {
            Assert.fail("We expected an exception on the 2nd get");
        }
        System.out.println(("CMD1 : " + (getExecutionEvents())));
        System.out.println(("CMD2 : " + (getExecutionEvents())));
        System.out.println(("CMD3 : " + (getExecutionEvents())));
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        assertCommandExecutionEvents(command2, EMIT, SUCCESS);
        assertCommandExecutionEvents(command3, SEMAPHORE_REJECTED, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    @Test
    public void testRejectedExecutionSemaphoreWithFallback() {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        final ArrayBlockingQueue<Boolean> results = new ArrayBlockingQueue<Boolean>(2);
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final HystrixObservableCommandTest.TestSemaphoreCommandWithFallback command1 = new HystrixObservableCommandTest.TestSemaphoreCommandWithFallback(circuitBreaker, 1, 200, false);
        Runnable r1 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(observe().toBlocking().single());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        final HystrixObservableCommandTest.TestSemaphoreCommandWithFallback command2 = new HystrixObservableCommandTest.TestSemaphoreCommandWithFallback(circuitBreaker, 1, 200, false);
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(observe().toBlocking().single());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        // 2 threads, the second should be rejected by the semaphore and return fallback
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        try {
            // give t1 a headstart
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("failed waiting on threads");
        }
        if (exceptionReceived.get()) {
            Assert.fail("We should have received a fallback response");
        }
        // both threads should have returned values
        Assert.assertEquals(2, results.size());
        // should contain both a true and false result
        Assert.assertTrue(results.contains(Boolean.TRUE));
        Assert.assertTrue(results.contains(Boolean.FALSE));
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        assertCommandExecutionEvents(command2, SEMAPHORE_REJECTED, FALLBACK_EMIT, FALLBACK_SUCCESS);
        Assert.assertEquals(0, command1.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    @Test
    public void testSemaphorePermitsInUse() {
        // this semaphore will be shared across multiple command instances
        final TryableSemaphoreActual sharedSemaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(3));
        // creates thread using isolated semaphore
        final TryableSemaphoreActual isolatedSemaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(1));
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        // used to wait until all commands are started
        final CountDownLatch startLatch = new CountDownLatch((((sharedSemaphore.numberOfPermits.get()) * 2) + 1));
        // used to signal that all command can finish
        final CountDownLatch sharedLatch = new CountDownLatch(1);
        final CountDownLatch isolatedLatch = new CountDownLatch(1);
        final List<HystrixObservableCommand<Boolean>> commands = new ArrayList<HystrixObservableCommand<Boolean>>();
        final List<Observable<Boolean>> results = new ArrayList<Observable<Boolean>>();
        HystrixObservableCommand<Boolean> isolated = new HystrixObservableCommandTest.LatchedSemaphoreCommand("ObservableCommand-Isolated", circuitBreaker, isolatedSemaphore, startLatch, isolatedLatch);
        commands.add(isolated);
        for (int s = 0; s < ((sharedSemaphore.numberOfPermits.get()) * 2); s++) {
            HystrixObservableCommand<Boolean> shared = new HystrixObservableCommandTest.LatchedSemaphoreCommand("ObservableCommand-Shared", circuitBreaker, sharedSemaphore, startLatch, sharedLatch);
            commands.add(shared);
            Observable<Boolean> result = shared.toObservable();
            results.add(result);
        }
        Observable<Boolean> isolatedResult = isolated.toObservable();
        results.add(isolatedResult);
        // verifies no permits in use before starting commands
        Assert.assertEquals("before commands start, shared semaphore should be unused", 0, sharedSemaphore.getNumberOfPermitsUsed());
        Assert.assertEquals("before commands start, isolated semaphore should be unused", 0, isolatedSemaphore.getNumberOfPermitsUsed());
        final CountDownLatch allTerminal = new CountDownLatch(1);
        Observable.merge(results).subscribeOn(Schedulers.computation()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((Thread.currentThread().getName()) + " OnCompleted"));
                allTerminal.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((Thread.currentThread().getName()) + " OnError : ") + e));
                allTerminal.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((Thread.currentThread().getName()) + " OnNext : ") + b));
            }
        });
        try {
            Assert.assertTrue(startLatch.await(1000, TimeUnit.MILLISECONDS));
        } catch (Throwable ex) {
            Assert.fail(ex.getMessage());
        }
        // verifies that all semaphores are in use
        Assert.assertEquals("immediately after command start, all shared semaphores should be in-use", sharedSemaphore.numberOfPermits.get().longValue(), sharedSemaphore.getNumberOfPermitsUsed());
        Assert.assertEquals("immediately after command start, isolated semaphore should be in-use", isolatedSemaphore.numberOfPermits.get().longValue(), isolatedSemaphore.getNumberOfPermitsUsed());
        // signals commands to finish
        sharedLatch.countDown();
        isolatedLatch.countDown();
        try {
            Assert.assertTrue(allTerminal.await(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("failed waiting on commands");
        }
        // verifies no permits in use after finishing threads
        Assert.assertEquals("after all threads have finished, no shared semaphores should be in-use", 0, sharedSemaphore.getNumberOfPermitsUsed());
        Assert.assertEquals("after all threads have finished, isolated semaphore not in-use", 0, isolatedSemaphore.getNumberOfPermitsUsed());
        // verifies that some executions failed
        int numSemaphoreRejected = 0;
        for (HystrixObservableCommand<Boolean> cmd : commands) {
            if (cmd.isResponseSemaphoreRejected()) {
                numSemaphoreRejected++;
            }
        }
        Assert.assertEquals("expected some of shared semaphore commands to get rejected", sharedSemaphore.numberOfPermits.get().longValue(), numSemaphoreRejected);
    }

    /**
     * Test that HystrixOwner can be passed in dynamically.
     */
    @Test
    public void testDynamicOwner() {
        try {
            TestHystrixObservableCommand<Boolean> command = new HystrixObservableCommandTest.DynamicOwnerTestCommand(OWNER_ONE);
            Assert.assertEquals(true, command.observe().toBlocking().single());
            assertCommandExecutionEvents(command, EMIT, SUCCESS);
            // semaphore isolated
            Assert.assertFalse(command.isExecutedInThread());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We received an exception.");
        }
    }

    /**
     * Test a successful command execution.
     */
    @Test
    public void testDynamicOwnerFails() {
        try {
            TestHystrixObservableCommand<Boolean> command = new HystrixObservableCommandTest.DynamicOwnerTestCommand(null);
            Assert.assertEquals(true, command.observe().toBlocking().single());
            Assert.fail("we should have thrown an exception as we need an owner");
            // semaphore isolated
            Assert.assertFalse(command.isExecutedInThread());
            Assert.assertEquals(0, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        } catch (Exception e) {
            // success if we get here
        }
    }

    /**
     * Test that HystrixCommandKey can be passed in dynamically.
     */
    @Test
    public void testDynamicKey() {
        try {
            HystrixObservableCommandTest.DynamicOwnerAndKeyTestCommand command1 = new HystrixObservableCommandTest.DynamicOwnerAndKeyTestCommand(OWNER_ONE, KEY_ONE);
            Assert.assertEquals(true, observe().toBlocking().single());
            HystrixObservableCommandTest.DynamicOwnerAndKeyTestCommand command2 = new HystrixObservableCommandTest.DynamicOwnerAndKeyTestCommand(OWNER_ONE, KEY_TWO);
            Assert.assertEquals(true, observe().toBlocking().single());
            // 2 different circuit breakers should be created
            Assert.assertNotSame(getCircuitBreaker(), getCircuitBreaker());
            // semaphore isolated
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We received an exception.");
        }
    }

    /**
     * Test Request scoped caching of commands so that a 2nd duplicate call doesn't execute but returns the previous Future
     */
    @Test
    public void testRequestCache1UsingThreadIsolation() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.observe().toBlocking().toFuture();
        Future<String> f2 = command2.observe().toBlocking().toFuture();
        try {
            Assert.assertEquals("A", f1.get());
            Assert.assertEquals("A", f2.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(command1.executed);
        // the second one should not have executed as it should have received the cached value instead
        Assert.assertFalse(command2.executed);
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        Assert.assertTrue(((command1.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertFalse(command1.isResponseFromCache());
        Assert.assertNull(command1.getExecutionException());
        // the execution log for command2 should show it came from cache
        assertCommandExecutionEvents(command2, EMIT, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertTrue(((command2.getExecutionTimeInMilliseconds()) == (-1)));
        Assert.assertTrue(command2.isResponseFromCache());
        Assert.assertNull(command2.getExecutionException());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test Request scoped caching doesn't prevent different ones from executing
     */
    @Test
    public void testRequestCache2UsingThreadIsolation() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "B");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.observe().toBlocking().toFuture();
        Future<String> f2 = command2.observe().toBlocking().toFuture();
        try {
            Assert.assertEquals("A", f1.get());
            Assert.assertEquals("B", f2.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        assertCommandExecutionEvents(command2, EMIT, SUCCESS);
        Assert.assertTrue(((command2.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertFalse(command2.isResponseFromCache());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testRequestCache3UsingThreadIsolation() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "B");
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command3 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.observe().toBlocking().toFuture();
        Future<String> f2 = command2.observe().toBlocking().toFuture();
        Future<String> f3 = command3.observe().toBlocking().toFuture();
        try {
            Assert.assertEquals("A", f1.get());
            Assert.assertEquals("B", f2.get());
            Assert.assertEquals("A", f3.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // but the 3rd should come from cache
        Assert.assertFalse(command3.executed);
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        assertCommandExecutionEvents(command2, EMIT, SUCCESS);
        assertCommandExecutionEvents(command3, EMIT, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertTrue(((command3.getExecutionTimeInMilliseconds()) == (-1)));
        Assert.assertTrue(command3.isResponseFromCache());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test Request scoped caching of commands so that a 2nd duplicate call doesn't execute but returns the previous Future
     */
    @Test
    public void testRequestCacheWithSlowExecution() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.SlowCacheableCommand command1 = new HystrixObservableCommandTest.SlowCacheableCommand(circuitBreaker, "A", 200);
        HystrixObservableCommandTest.SlowCacheableCommand command2 = new HystrixObservableCommandTest.SlowCacheableCommand(circuitBreaker, "A", 100);
        HystrixObservableCommandTest.SlowCacheableCommand command3 = new HystrixObservableCommandTest.SlowCacheableCommand(circuitBreaker, "A", 100);
        HystrixObservableCommandTest.SlowCacheableCommand command4 = new HystrixObservableCommandTest.SlowCacheableCommand(circuitBreaker, "A", 100);
        Future<String> f1 = observe().toBlocking().toFuture();
        Future<String> f2 = observe().toBlocking().toFuture();
        Future<String> f3 = observe().toBlocking().toFuture();
        Future<String> f4 = observe().toBlocking().toFuture();
        try {
            Assert.assertEquals("A", f2.get());
            Assert.assertEquals("A", f3.get());
            Assert.assertEquals("A", f4.get());
            Assert.assertEquals("A", f1.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(command1.executed);
        // the second one should not have executed as it should have received the cached value instead
        Assert.assertFalse(command2.executed);
        Assert.assertFalse(command3.executed);
        Assert.assertFalse(command4.executed);
        // the execution log for command1 should show an EMIT and a SUCCESS
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertFalse(isResponseFromCache());
        // the execution log for command2 should show it came from cache
        assertCommandExecutionEvents(command2, EMIT, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        Assert.assertTrue(isResponseFromCache());
        assertCommandExecutionEvents(command3, EMIT, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertTrue(isResponseFromCache());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        assertCommandExecutionEvents(command4, EMIT, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertTrue(isResponseFromCache());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        assertSaneHystrixRequestLog(4);
        // semaphore isolated
        Assert.assertFalse(isExecutedInThread());
        Assert.assertFalse(isExecutedInThread());
        Assert.assertFalse(isExecutedInThread());
        Assert.assertFalse(isExecutedInThread());
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testNoRequestCache3UsingThreadIsolation() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, false, "A");
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, false, "B");
        HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command3 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, false, "A");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.observe().toBlocking().toFuture();
        Future<String> f2 = command2.observe().toBlocking().toFuture();
        Future<String> f3 = command3.observe().toBlocking().toFuture();
        try {
            Assert.assertEquals("A", f1.get());
            Assert.assertEquals("B", f2.get());
            Assert.assertEquals("A", f3.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // this should also execute since we disabled the cache
        Assert.assertTrue(command3.executed);
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        assertCommandExecutionEvents(command2, EMIT, SUCCESS);
        assertCommandExecutionEvents(command3, EMIT, SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
        // thread isolated
        Assert.assertTrue(command1.isExecutedInThread());
        Assert.assertTrue(command2.isExecutedInThread());
        Assert.assertTrue(command3.isExecutedInThread());
    }

    @Test
    public void testNoRequestCacheOnTimeoutThrowsException() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback r1 = new HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            System.out.println(("r1 value: " + (observe().toBlocking().single())));
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback r2 = new HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            observe().toBlocking().single();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback r3 = new HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        Future<Boolean> f3 = observe().toBlocking().toFuture();
        try {
            f3.get();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        Thread.sleep(500);// timeout on command is set to 200ms

        HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback r4 = new HystrixObservableCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            observe().toBlocking().single();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.assertTrue(isResponseTimedOut());
            Assert.assertFalse(isResponseFromFallback());
            // what we want
        }
        assertCommandExecutionEvents(r1, TIMEOUT, FALLBACK_MISSING);
        assertCommandExecutionEvents(r2, TIMEOUT, FALLBACK_MISSING);
        assertCommandExecutionEvents(r3, TIMEOUT, FALLBACK_MISSING);
        assertCommandExecutionEvents(r4, TIMEOUT, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(4);
    }

    @Test
    public void testRequestCacheOnTimeoutCausesNullPointerException() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase command1 = new HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase command2 = new HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase command3 = new HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase command4 = new HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase command5 = new HystrixObservableCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        // Expect it to time out - all results should be false
        Assert.assertFalse(observe().toBlocking().single());
        Assert.assertFalse(observe().toBlocking().single());// return from cache #1

        Assert.assertFalse(observe().toBlocking().single());// return from cache #2

        Thread.sleep(500);// timeout on command is set to 200ms

        Boolean value = observe().toBlocking().single();// return from cache #3

        Assert.assertFalse(value);
        Future<Boolean> f = observe().toBlocking().toFuture();// return from cache #4

        // the bug is that we're getting a null Future back, rather than a Future that returns false
        Assert.assertNotNull(f);
        Assert.assertFalse(f.get());
        Assert.assertTrue(isResponseFromFallback());
        Assert.assertTrue(isResponseTimedOut());
        Assert.assertFalse(isFailedExecution());
        Assert.assertFalse(isResponseShortCircuited());
        Assert.assertNotNull(getExecutionException());
        assertCommandExecutionEvents(command1, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(command2, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command3, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command4, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command5, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(5);
    }

    @Test
    public void testRequestCacheOnTimeoutThrowsException() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback r1 = new HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            System.out.println(("r1 value: " + (observe().toBlocking().single())));
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            Assert.assertNotNull(getExecutionException());
            // what we want
        }
        HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback r2 = new HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            observe().toBlocking().single();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            Assert.assertNotNull(getExecutionException());
            // what we want
        }
        HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback r3 = new HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        Future<Boolean> f3 = observe().toBlocking().toFuture();
        try {
            f3.get();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Assert.assertTrue(isResponseTimedOut());
            Assert.assertNotNull(getExecutionException());
            // what we want
        }
        Thread.sleep(500);// timeout on command is set to 200ms

        HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback r4 = new HystrixObservableCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            observe().toBlocking().single();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            Assert.assertFalse(isResponseFromFallback());
            Assert.assertNotNull(getExecutionException());
        }
        assertCommandExecutionEvents(r1, TIMEOUT, FALLBACK_MISSING);
        assertCommandExecutionEvents(r2, TIMEOUT, FALLBACK_MISSING, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(r3, TIMEOUT, FALLBACK_MISSING, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(r4, TIMEOUT, FALLBACK_MISSING, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(4);
    }

    @Test
    public void testRequestCacheOnThreadRejectionThrowsException() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        CountDownLatch completionLatch = new CountDownLatch(1);
        HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback r1 = new HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("r1: " + (observe().toBlocking().single())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            Assert.assertNotNull(getExecutionException());
            // what we want
        }
        HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback r2 = new HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("r2: " + (observe().toBlocking().single())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            // e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            Assert.assertNotNull(getExecutionException());
            // what we want
        }
        HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback r3 = new HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("f3: " + (observe().toBlocking().toFuture().get())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (ExecutionException e) {
            Assert.assertTrue(isResponseRejected());
            Assert.assertTrue(((e.getCause()) instanceof HystrixRuntimeException));
            Assert.assertNotNull(getExecutionException());
        }
        // let the command finish (only 1 should actually be blocked on this due to the response cache)
        completionLatch.countDown();
        // then another after the command has completed
        HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback r4 = new HystrixObservableCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("r4: " + (observe().toBlocking().single())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            // e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            Assert.assertFalse(isResponseFromFallback());
            Assert.assertNotNull(getExecutionException());
            // what we want
        }
        assertCommandExecutionEvents(r1, THREAD_POOL_REJECTED, FALLBACK_MISSING);
        assertCommandExecutionEvents(r2, THREAD_POOL_REJECTED, FALLBACK_MISSING, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(r3, THREAD_POOL_REJECTED, FALLBACK_MISSING, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(r4, THREAD_POOL_REJECTED, FALLBACK_MISSING, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(4);
    }

    /**
     * Test that we can do basic execution without a RequestVariable being initialized.
     */
    @Test
    public void testBasicExecutionWorksWithoutRequestVariable() {
        try {
            /* force the RequestVariable to not be initialized */
            HystrixRequestContext.setContextOnCurrentThread(null);
            TestHystrixObservableCommand<Boolean> command = new HystrixObservableCommandTest.SuccessfulTestCommand(ExecutionIsolationStrategy.SEMAPHORE);
            Assert.assertEquals(true, command.observe().toBlocking().single());
            TestHystrixObservableCommand<Boolean> command2 = new HystrixObservableCommandTest.SuccessfulTestCommand(ExecutionIsolationStrategy.SEMAPHORE);
            Assert.assertEquals(true, command2.observe().toBlocking().toFuture().get());
            // we should be able to execute without a RequestVariable if ...
            // 1) We don't have a cacheKey
            // 2) We don't ask for the RequestLog
            // 3) We don't do collapsing
            // semaphore isolated
            Assert.assertFalse(command.isExecutedInThread());
            Assert.assertNull(command.getExecutionException());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("We received an exception => " + (e.getMessage())));
        }
        Assert.assertNull(HystrixRequestLog.getCurrentRequest());
    }

    /**
     * Test that if we try and execute a command with a cacheKey without initializing RequestVariable that it gives an error.
     */
    @Test
    public void testCacheKeyExecutionRequiresRequestVariable() {
        try {
            /* force the RequestVariable to not be initialized */
            HystrixRequestContext.setContextOnCurrentThread(null);
            HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
            HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "one");
            Assert.assertEquals("one", command.observe().toBlocking().single());
            HystrixObservableCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixObservableCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "two");
            Assert.assertEquals("two", command2.observe().toBlocking().toFuture().get());
            Assert.fail("We expect an exception because cacheKey requires RequestVariable.");
            // semaphore isolated
            Assert.assertFalse(command.isExecutedInThread());
            Assert.assertNull(command.getExecutionException());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test that a BadRequestException can be synchronously thrown from a semaphore-isolated command and not count towards errors and bypasses fallback.
     */
    @Test
    public void testSemaphoreIsolatedBadRequestSyncExceptionObserve() {
        testBadRequestExceptionObserve(SEMAPHORE, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.SYNC_EXCEPTION);
    }

    /**
     * Test that a BadRequestException can be asynchronously thrown from a semaphore-isolated command and not count towards errors and bypasses fallback.
     */
    @Test
    public void testSemaphoreIsolatedBadRequestAsyncExceptionObserve() {
        testBadRequestExceptionObserve(SEMAPHORE, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.ASYNC_EXCEPTION);
    }

    /**
     * Test that a BadRequestException can be synchronously thrown from a thread-isolated command and not count towards errors and bypasses fallback.
     */
    @Test
    public void testThreadIsolatedBadRequestSyncExceptionObserve() {
        testBadRequestExceptionObserve(THREAD, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.SYNC_EXCEPTION);
    }

    /**
     * Test that a BadRequestException can be asynchronously thrown from a thread-isolated command and not count towards errors and bypasses fallback.
     */
    @Test
    public void testThreadIsolatedBadRequestAsyncExceptionObserve() {
        testBadRequestExceptionObserve(THREAD, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.ASYNC_EXCEPTION);
    }

    /**
     * Test that synchronous BadRequestException behavior works the same on a cached response for a semaphore-isolated command.
     */
    @Test
    public void testSyncBadRequestExceptionOnResponseFromCacheInSempahore() {
        testBadRequestExceptionOnResponseFromCache(SEMAPHORE, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.SYNC_EXCEPTION);
    }

    /**
     * Test that asynchronous BadRequestException behavior works the same on a cached response for a semaphore-isolated command.
     */
    @Test
    public void testAsyncBadRequestExceptionOnResponseFromCacheInSemaphore() {
        testBadRequestExceptionOnResponseFromCache(SEMAPHORE, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.ASYNC_EXCEPTION);
    }

    /**
     * Test that synchronous BadRequestException behavior works the same on a cached response for a thread-isolated command.
     */
    @Test
    public void testSyncBadRequestExceptionOnResponseFromCacheInThread() {
        testBadRequestExceptionOnResponseFromCache(THREAD, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.SYNC_EXCEPTION);
    }

    /**
     * Test that asynchronous BadRequestException behavior works the same on a cached response for a thread-isolated command.
     */
    @Test
    public void testAsyncBadRequestExceptionOnResponseFromCacheInThread() {
        testBadRequestExceptionOnResponseFromCache(THREAD, HystrixObservableCommandTest.KnownHystrixBadRequestFailureTestCommand.ASYNC_EXCEPTION);
    }

    /**
     * Test a checked Exception being thrown
     */
    @Test
    public void testCheckedExceptionViaExecute() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.CommandWithCheckedException command = new HystrixObservableCommandTest.CommandWithCheckedException(circuitBreaker);
        try {
            observe().toBlocking().single();
            Assert.fail(("we expect to receive a " + (Exception.class.getSimpleName())));
        } catch (Exception e) {
            Assert.assertEquals("simulated checked exception message", e.getCause().getMessage());
        }
        Assert.assertEquals("simulated checked exception message", getFailedExecutionException().getMessage());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(isFailedExecution());
        Assert.assertNotNull(getExecutionException());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a java.lang.Error being thrown
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testCheckedExceptionViaObserve() throws InterruptedException {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.CommandWithCheckedException command = new HystrixObservableCommandTest.CommandWithCheckedException(circuitBreaker);
        final AtomicReference<Throwable> t = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            observe().subscribe(new Observer<Boolean>() {
                @Override
                public void onCompleted() {
                    latch.countDown();
                }

                @Override
                public void onError(Throwable e) {
                    t.set(e);
                    latch.countDown();
                }

                @Override
                public void onNext(Boolean args) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("we should not get anything thrown, it should be emitted via the Observer#onError method");
        }
        latch.await(1, TimeUnit.SECONDS);
        Assert.assertNotNull(t.get());
        t.get().printStackTrace();
        Assert.assertTrue(((t.get()) instanceof HystrixRuntimeException));
        Assert.assertEquals("simulated checked exception message", t.get().getCause().getMessage());
        Assert.assertEquals("simulated checked exception message", getFailedExecutionException().getMessage());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(getExecutionException());
        // semaphore isolated
        Assert.assertFalse(isExecutedInThread());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a java.lang.Error being thrown
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testErrorThrownViaObserve() throws InterruptedException {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixObservableCommandTest.CommandWithErrorThrown command = new HystrixObservableCommandTest.CommandWithErrorThrown(circuitBreaker, true);
        final AtomicReference<Throwable> t = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            observe().subscribe(new Observer<Boolean>() {
                @Override
                public void onCompleted() {
                    latch.countDown();
                }

                @Override
                public void onError(Throwable e) {
                    t.set(e);
                    latch.countDown();
                }

                @Override
                public void onNext(Boolean args) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("we should not get anything thrown, it should be emitted via the Observer#onError method");
        }
        latch.await(1, TimeUnit.SECONDS);
        Assert.assertNotNull(t.get());
        t.get().printStackTrace();
        Assert.assertTrue(((t.get()) instanceof HystrixRuntimeException));
        // the actual error is an extra cause level deep because Hystrix needs to wrap Throwable/Error as it's public
        // methods only support Exception and it's not a strong enough reason to break backwards compatibility and jump to version 2.x
        Assert.assertEquals("simulated java.lang.Error message", t.get().getCause().getCause().getMessage());
        Assert.assertEquals("simulated java.lang.Error message", getFailedExecutionException().getCause().getMessage());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(getExecutionException());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        Assert.assertFalse(isExecutedInThread());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testInterruptObserveOnTimeout() throws InterruptedException {
        // given
        HystrixObservableCommandTest.InterruptibleCommand cmd = new HystrixObservableCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true);
        // when
        observe().subscribe();
        // then
        Thread.sleep(500);
        Assert.assertTrue(cmd.hasBeenInterrupted());
    }

    @Test
    public void testInterruptToObservableOnTimeout() throws InterruptedException {
        // given
        HystrixObservableCommandTest.InterruptibleCommand cmd = new HystrixObservableCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true);
        // when
        toObservable().subscribe();
        // then
        Thread.sleep(500);
        Assert.assertTrue(cmd.hasBeenInterrupted());
    }

    /**
     * ********************** HystrixObservableCommand-specific THREAD-ISOLATED Execution Hook Tests **************************************
     */
    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: EMITx4, SUCCESS
     */
    @Test
    public void testExecutionHookThreadMultipleEmitsAndThenSuccess() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, MULTIPLE_EMITS_THEN_SUCCESS);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(4, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(4, 0, 1));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionEmit - !onRunSuccess - !onComplete - onEmit - onExecutionEmit - !onRunSuccess - !onComplete - onEmit - onExecutionEmit - !onRunSuccess - !onComplete - onEmit - onExecutionEmit - !onRunSuccess - !onComplete - onEmit - onExecutionSuccess - onThreadComplete - onSuccess - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: EMITx4, FAILURE, FALLBACK_EMITx4, FALLBACK_SUCCESS
     */
    @Test
    public void testExecutionHookThreadMultipleEmitsThenErrorThenMultipleFallbackEmitsAndThenFallbackSuccess() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, MULTIPLE_EMITS_THEN_FAILURE, 0, AbstractTestHystrixCommand.FallbackResult.MULTIPLE_EMITS_THEN_SUCCESS);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(8, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(4, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(4, 0, 1));
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(("onStart - onThreadStart - !onRunStart - onExecutionStart - " + ((((((((("onExecutionEmit - !onRunSuccess - !onComplete - onEmit - " + "onExecutionEmit - !onRunSuccess - !onComplete - onEmit - ") + "onExecutionEmit - !onRunSuccess - !onComplete - onEmit - ") + "onExecutionEmit - !onRunSuccess - !onComplete - onEmit - ") + "onExecutionError - !onRunError - onThreadComplete - onFallbackStart - ") + "onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - ") + "onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - ") + "onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - ") + "onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - ") + "onFallbackSuccess - onSuccess - ")), command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: asynchronous HystrixBadRequestException
     */
    @Test
    public void testExecutionHookThreadAsyncBadRequestException() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, ASYNC_BAD_REQUEST);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(HystrixBadRequestException.class, hook.getCommandException().getClass());
                Assert.assertEquals(HystrixBadRequestException.class, hook.getExecutionException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: async HystrixRuntimeException
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadAsyncExceptionNoFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, ASYNC_FAILURE, UNIMPLEMENTED);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: async HystrixRuntimeException
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadAsyncExceptionSuccessfulFallback() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, ASYNC_FAILURE, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: sync HystrixRuntimeException
     * Fallback: async HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadSyncExceptionAsyncUnsuccessfulFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, AbstractTestHystrixCommand.ExecutionResult.FAILURE, AbstractTestHystrixCommand.FallbackResult.ASYNC_FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onFallbackStart - onFallbackError - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: async HystrixRuntimeException
     * Fallback: sync HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadAsyncExceptionSyncUnsuccessfulFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, ASYNC_FAILURE, FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onFallbackStart - onFallbackError - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: THREAD
     * Thread Pool fullInteger : NO
     * Thread Pool Queue fullInteger: NO
     * Timeout: NO
     * Execution Result: async HystrixRuntimeException
     * Fallback: async HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadAsyncExceptionAsyncUnsuccessfulFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(THREAD, ASYNC_FAILURE, AbstractTestHystrixCommand.FallbackResult.ASYNC_FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onFallbackStart - onFallbackError - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuitInteger : YES
     * Thread/semaphore: THREAD
     * Fallback: async HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadShortCircuitAsyncUnsuccessfulFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCircuitOpenCommand(THREAD, AbstractTestHystrixCommand.FallbackResult.ASYNC_FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onFallbackStart - onFallbackError - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * ********************** END HystrixObservableCommand-specific THREAD-ISOLATED Execution Hook Tests **************************************
     */
    /**
     * ******************** HystrixObservableCommand-specific SEMAPHORE-ISOLATED Execution Hook Tests ***********************************
     */
    /**
     * Short-circuitInteger : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reachedInteger : NO
     * Execution Result: HystrixRuntimeException
     * Fallback: asynchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookSemaphoreExceptionUnsuccessfulAsynchronousFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixObservableCommand<Integer>>() {
            @Override
            public TestHystrixObservableCommand<Integer> call() {
                return getCommand(SEMAPHORE, AbstractTestHystrixCommand.ExecutionResult.FAILURE, AbstractTestHystrixCommand.FallbackResult.ASYNC_FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixObservableCommand<Integer>>() {
            @Override
            public void call(TestHystrixObservableCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onFallbackStart - onFallbackError - onError - ", command.getBuilder().executionHook.executionSequence.toString());
            }
        });
    }

    /**
     * ******************** END HystrixObservableCommand-specific SEMAPHORE-ISOLATED Execution Hook Tests ***********************************
     */
    /**
     * Test a command execution that fails but has a fallback.
     */
    @Test
    public void testExecutionFailureWithFallbackImplementedButDisabled() {
        TestHystrixObservableCommand<Boolean> commandEnabled = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true, true);
        try {
            Assert.assertEquals(false, commandEnabled.observe().toBlocking().single());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We should have received a response from the fallback.");
        }
        TestHystrixObservableCommand<Boolean> commandDisabled = new HystrixObservableCommandTest.KnownFailureTestCommandWithFallback(new HystrixCircuitBreakerTest.TestCircuitBreaker(), false, true);
        try {
            Assert.assertEquals(false, commandDisabled.observe().toBlocking().single());
            Assert.fail("expect exception thrown");
        } catch (Exception e) {
            // expected
        }
        Assert.assertEquals("we failed with a simulated issue", commandDisabled.getFailedExecutionException().getMessage());
        Assert.assertTrue(commandDisabled.isFailedExecution());
        Assert.assertNotNull(commandDisabled.getExecutionException());
        assertCommandExecutionEvents(commandEnabled, FAILURE, FALLBACK_EMIT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(commandDisabled, FAILURE);
        Assert.assertEquals(0, commandDisabled.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test that we can still use thread isolation if desired.
     */
    @Test
    public void testSynchronousExecutionTimeoutValueViaExecute() {
        HystrixObservableCommand.Setter properties = HystrixObservableCommand.Setter.withGroupKey(Factory.asKey("TestKey")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(THREAD).withExecutionTimeoutInMilliseconds(50));
        System.out.println((">>>>> Begin: " + (System.currentTimeMillis())));
        HystrixObservableCommand<String> command = new HystrixObservableCommand<String>(properties) {
            @Override
            protected Observable<String> construct() {
                return Observable.create(new rx.Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> t1) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        t1.onNext("hello");
                        t1.onCompleted();
                    }
                });
            }

            @Override
            protected Observable<String> resumeWithFallback() {
                if (isResponseTimedOut()) {
                    return Observable.just("timed-out");
                } else {
                    return Observable.just("abc");
                }
            }
        };
        System.out.println((">>>>> Start: " + (System.currentTimeMillis())));
        String value = command.observe().toBlocking().single();
        System.out.println((">>>>> End: " + (System.currentTimeMillis())));
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertEquals("expected fallback value", "timed-out", value);
        // Thread isolated
        Assert.assertTrue(command.isExecutedInThread());
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    @Test
    public void testSynchronousExecutionUsingThreadIsolationTimeoutValueViaObserve() {
        HystrixObservableCommand.Setter properties = HystrixObservableCommand.Setter.withGroupKey(Factory.asKey("TestKey")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(THREAD).withExecutionTimeoutInMilliseconds(50));
        HystrixObservableCommand<String> command = new HystrixObservableCommand<String>(properties) {
            @Override
            protected Observable<String> construct() {
                return Observable.create(new rx.Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> t1) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        t1.onNext("hello");
                        t1.onCompleted();
                    }
                });
            }

            @Override
            protected Observable<String> resumeWithFallback() {
                if (isResponseTimedOut()) {
                    return Observable.just("timed-out");
                } else {
                    return Observable.just("abc");
                }
            }
        };
        String value = command.observe().toBlocking().last();
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertEquals("expected fallback value", "timed-out", value);
        // Thread isolated
        Assert.assertTrue(command.isExecutedInThread());
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    @Test
    public void testAsyncExecutionTimeoutValueViaObserve() {
        HystrixObservableCommand.Setter properties = HystrixObservableCommand.Setter.withGroupKey(Factory.asKey("TestKey")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(50));
        HystrixObservableCommand<String> command = new HystrixObservableCommand<String>(properties) {
            @Override
            protected Observable<String> construct() {
                return Observable.create(new rx.Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> t1) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            System.out.println("********** interrupted on timeout");
                            e.printStackTrace();
                        }
                        // should never reach here
                        t1.onNext("hello");
                        t1.onCompleted();
                    }
                }).subscribeOn(Schedulers.newThread());
            }

            @Override
            protected Observable<String> resumeWithFallback() {
                if (isResponseTimedOut()) {
                    return Observable.just("timed-out");
                } else {
                    return Observable.just("abc");
                }
            }
        };
        String value = command.observe().toBlocking().last();
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertEquals("expected fallback value", "timed-out", value);
        // semaphore isolated
        Assert.assertFalse(command.isExecutedInThread());
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    /**
     * See https://github.com/Netflix/Hystrix/issues/212
     */
    @Test
    public void testObservableTimeoutNoFallbackThreadContext() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final AtomicReference<Thread> onErrorThread = new AtomicReference<Thread>();
        final AtomicBoolean isRequestContextInitialized = new AtomicBoolean();
        TestHystrixObservableCommand<Integer> command = getCommand(SEMAPHORE, SUCCESS, 200, UNIMPLEMENTED, 100);
        command.toObservable().doOnError(new rx.functions.Action1<Throwable>() {
            @Override
            public void call(Throwable t1) {
                System.out.println(("onError: " + t1));
                System.out.println(("onError Thread: " + (Thread.currentThread())));
                System.out.println(("ThreadContext in onError: " + (HystrixRequestContext.isCurrentThreadInitialized())));
                onErrorThread.set(Thread.currentThread());
                isRequestContextInitialized.set(HystrixRequestContext.isCurrentThreadInitialized());
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        Assert.assertTrue(isRequestContextInitialized.get());
        Assert.assertTrue(onErrorThread.get().getName().startsWith("HystrixTimer"));
        List<Throwable> errors = ts.getOnErrorEvents();
        Assert.assertEquals(1, errors.size());
        Throwable e = errors.get(0);
        if ((errors.get(0)) instanceof HystrixRuntimeException) {
            HystrixRuntimeException de = ((HystrixRuntimeException) (e));
            Assert.assertNotNull(de.getFallbackException());
            Assert.assertTrue(((de.getFallbackException()) instanceof UnsupportedOperationException));
            Assert.assertNotNull(de.getImplementingClass());
            Assert.assertNotNull(de.getCause());
            Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
        } else {
            Assert.fail("the exception should be ExecutionException with cause as HystrixRuntimeException");
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertNotNull(command.getExecutionException());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_MISSING);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        Assert.assertFalse(command.isExecutedInThread());
    }

    /**
     * See https://github.com/Netflix/Hystrix/issues/212
     */
    @Test
    public void testObservableTimeoutFallbackThreadContext() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        final AtomicReference<Thread> onErrorThread = new AtomicReference<Thread>();
        final AtomicBoolean isRequestContextInitialized = new AtomicBoolean();
        TestHystrixObservableCommand<Integer> command = getCommand(SEMAPHORE, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 100);
        command.toObservable().doOnNext(new rx.functions.Action1<Object>() {
            @Override
            public void call(Object t1) {
                System.out.println(("onNext: " + t1));
                System.out.println(("onNext Thread: " + (Thread.currentThread())));
                System.out.println(("ThreadContext in onNext: " + (HystrixRequestContext.isCurrentThreadInitialized())));
                onErrorThread.set(Thread.currentThread());
                isRequestContextInitialized.set(HystrixRequestContext.isCurrentThreadInitialized());
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        System.out.println(("events: " + (ts.getOnNextEvents())));
        Assert.assertTrue(isRequestContextInitialized.get());
        Assert.assertTrue(onErrorThread.get().getName().startsWith("HystrixTimer"));
        List<Object> onNexts = ts.getOnNextEvents();
        Assert.assertEquals(1, onNexts.size());
        // assertFalse( onNexts.get(0));
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertNotNull(command.getExecutionException());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_EMIT, FALLBACK_SUCCESS);
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        Assert.assertFalse(command.isExecutedInThread());
    }

    @Test
    public void testRejectedViaSemaphoreIsolation() {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        final ArrayBlockingQueue<Boolean> results = new ArrayBlockingQueue<Boolean>(2);
        final TryableSemaphoreActual semaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(1));
        // used to wait until all commands have started
        final CountDownLatch startLatch = new CountDownLatch(2);
        // used to signal that all command can finish
        final CountDownLatch sharedLatch = new CountDownLatch(1);
        final HystrixObservableCommandTest.LatchedSemaphoreCommand command1 = new HystrixObservableCommandTest.LatchedSemaphoreCommand(circuitBreaker, semaphore, startLatch, sharedLatch);
        final HystrixObservableCommandTest.LatchedSemaphoreCommand command2 = new HystrixObservableCommandTest.LatchedSemaphoreCommand(circuitBreaker, semaphore, startLatch, sharedLatch);
        Observable<Boolean> merged = Observable.merge(toObservable(), toObservable()).subscribeOn(Schedulers.computation());
        final CountDownLatch terminal = new CountDownLatch(1);
        merged.subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((Thread.currentThread().getName()) + " OnCompleted"));
                terminal.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((Thread.currentThread().getName()) + " OnError : ") + e));
                terminal.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((Thread.currentThread().getName()) + " OnNext : ") + b));
                results.offer(b);
            }
        });
        try {
            Assert.assertTrue(startLatch.await(1000, TimeUnit.MILLISECONDS));
            sharedLatch.countDown();
            Assert.assertTrue(terminal.await(1000, TimeUnit.MILLISECONDS));
        } catch (Throwable ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
        // one thread should have returned values
        Assert.assertEquals(2, results.size());
        // 1 should have gotten the normal value, the other - the fallback
        Assert.assertTrue(results.contains(Boolean.TRUE));
        Assert.assertTrue(results.contains(Boolean.FALSE));
        System.out.println(("REQ LOG : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        assertCommandExecutionEvents(command1, EMIT, SUCCESS);
        assertCommandExecutionEvents(command2, SEMAPHORE_REJECTED, FALLBACK_EMIT, FALLBACK_SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    @Test
    public void testRejectedViaThreadIsolation() throws InterruptedException {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        final ArrayBlockingQueue<Boolean> results = new ArrayBlockingQueue<Boolean>(10);
        final List<Thread> executionThreads = Collections.synchronizedList(new ArrayList<Thread>(20));
        final List<Thread> responseThreads = Collections.synchronizedList(new ArrayList<Thread>(10));
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final CountDownLatch scheduleLatch = new CountDownLatch(2);
        final CountDownLatch successLatch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand> command1Ref = new AtomicReference<HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand>();
        final AtomicReference<HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand> command2Ref = new AtomicReference<HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand>();
        final AtomicReference<HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand> command3Ref = new AtomicReference<HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand>();
        Runnable r1 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                final boolean shouldExecute = (count.incrementAndGet()) < 3;
                try {
                    executionThreads.add(Thread.currentThread());
                    HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand command1 = new HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand(circuitBreaker, 2, new Action0() {
                        @Override
                        public void call() {
                            // make sure it's deterministic and we put 2 threads into the pool before the 3rd is submitted
                            if (shouldExecute) {
                                try {
                                    scheduleLatch.countDown();
                                    successLatch.await();
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    });
                    command1Ref.set(command1);
                    results.add(toObservable().map(new rx.functions.Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean b) {
                            responseThreads.add(Thread.currentThread());
                            return b;
                        }
                    }).doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            if (!shouldExecute) {
                                // the final thread that shouldn't execute releases the latch once it has run
                                // so it is deterministic that the other two fill the thread pool until this one rejects
                                successLatch.countDown();
                            }
                        }
                    }).toBlocking().single());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                final boolean shouldExecute = (count.incrementAndGet()) < 3;
                try {
                    executionThreads.add(Thread.currentThread());
                    HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand command2 = new HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand(circuitBreaker, 2, new Action0() {
                        @Override
                        public void call() {
                            // make sure it's deterministic and we put 2 threads into the pool before the 3rd is submitted
                            if (shouldExecute) {
                                try {
                                    scheduleLatch.countDown();
                                    successLatch.await();
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    });
                    command2Ref.set(command2);
                    results.add(toObservable().map(new rx.functions.Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean b) {
                            responseThreads.add(Thread.currentThread());
                            return b;
                        }
                    }).doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            if (!shouldExecute) {
                                // the final thread that shouldn't execute releases the latch once it has run
                                // so it is deterministic that the other two fill the thread pool until this one rejects
                                successLatch.countDown();
                            }
                        }
                    }).toBlocking().single());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        Runnable r3 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                final boolean shouldExecute = (count.incrementAndGet()) < 3;
                try {
                    executionThreads.add(Thread.currentThread());
                    HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand command3 = new HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand(circuitBreaker, 2, new Action0() {
                        @Override
                        public void call() {
                            // make sure it's deterministic and we put 2 threads into the pool before the 3rd is submitted
                            if (shouldExecute) {
                                try {
                                    scheduleLatch.countDown();
                                    successLatch.await();
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    });
                    command3Ref.set(command3);
                    results.add(toObservable().map(new rx.functions.Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean b) {
                            responseThreads.add(Thread.currentThread());
                            return b;
                        }
                    }).doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            if (!shouldExecute) {
                                // the final thread that shouldn't execute releases the latch once it has run
                                // so it is deterministic that the other two fill the thread pool until this one rejects
                                successLatch.countDown();
                            }
                        }
                    }).toBlocking().single());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        // 2 threads, the second should be rejected by the semaphore and return fallback
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);
        t1.start();
        t2.start();
        // wait for the previous 2 thread to be running before starting otherwise it can race
        scheduleLatch.await(500, TimeUnit.MILLISECONDS);
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("failed waiting on threads");
        }
        // we should have 2 of the 3 return results
        Assert.assertEquals(2, results.size());
        // the other thread should have thrown an Exception
        Assert.assertTrue(exceptionReceived.get());
        assertCommandExecutionEvents(command1Ref.get(), EMIT, SUCCESS);
        assertCommandExecutionEvents(command2Ref.get(), EMIT, SUCCESS);
        assertCommandExecutionEvents(command3Ref.get(), THREAD_POOL_REJECTED, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    private final class RequestContextTestResults {
        volatile TestHystrixObservableCommand<Boolean> command;

        final AtomicReference<Thread> originThread = new AtomicReference<Thread>();

        final AtomicBoolean isContextInitialized = new AtomicBoolean();

        TestSubscriber<Boolean> ts = new TestSubscriber<Boolean>();

        final AtomicBoolean isContextInitializedObserveOn = new AtomicBoolean();

        final AtomicReference<Thread> observeOnThread = new AtomicReference<Thread>();
    }

    /* *************************************** testSuccessfulRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testSuccessfulRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnSuccess(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// all synchronous

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testSuccessfulRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnSuccess(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testSuccessfulRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnSuccess(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [RxComputation]
     */
    @Test
    public void testSuccessfulRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnSuccess(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().getName().startsWith("hystrix-OWNER_ONE"));// thread isolated on a HystrixThreadPool

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("hystrix-OWNER_ONE"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testSuccessfulRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnSuccess(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testSuccessfulRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnSuccess(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /* *************************************** testGracefulFailureRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testGracefulFailureRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnGracefulFailure(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// all synchronous

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testGracefulFailureRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnGracefulFailure(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testGracefulFailureRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnGracefulFailure(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [RxComputation]
     */
    @Test
    public void testGracefulFailureRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnGracefulFailure(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().getName().startsWith("hystrix-OWNER_ONE"));// thread isolated on a HystrixThreadPool

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("hystrix-OWNER_ONE"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testGracefulFailureRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnGracefulFailure(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testGracefulFailureRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnGracefulFailure(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /* *************************************** testBadFailureRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testBadFailureRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnBadFailure(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// all synchronous

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testBadFailureRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnBadFailure(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testBadFailureRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnBadFailure(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [RxComputation]
     */
    @Test
    public void testBadFailureRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnBadFailure(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().getName().startsWith("hystrix-OWNER_ONE"));// thread isolated on a HystrixThreadPool

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("hystrix-OWNER_ONE"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testBadFailureRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnBadFailure(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testBadFailureRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnBadFailure(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /* *************************************** testFailureWithFallbackRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testFailureWithFallbackRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnFailureWithFallback(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// all synchronous

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testFailureWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnFailureWithFallback(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testFailureWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnFailureWithFallback(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [RxComputation]
     */
    @Test
    public void testFailureWithFallbackRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnFailureWithFallback(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().getName().startsWith("hystrix-OWNER_ONE"));// thread isolated on a HystrixThreadPool

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("hystrix-OWNER_ONE"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testFailureWithFallbackRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnFailureWithFallback(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testFailureWithFallbackRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnFailureWithFallback(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
    }

    /* *************************************** testRejectionWithFallbackRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testRejectionWithFallbackRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnRejectionWithFallback(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// all synchronous

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testRejectionWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnRejectionWithFallback(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testRejectionWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnRejectionWithFallback(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [RxComputation]
     */
    @Test
    public void testRejectionWithFallbackRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnRejectionWithFallback(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// fallback is performed by the calling thread

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        System.out.println(((("results.observeOnThread.get(): " + (results.observeOnThread.get())) + "  ") + (Thread.currentThread())));
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// rejected so we stay on calling thread

        // thread isolated, but rejected, so this is false
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testRejectionWithFallbackRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnRejectionWithFallback(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));
        // thread isolated, but rejected, so this is false
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testRejectionWithFallbackRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnRejectionWithFallback(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler for getFallback

        // thread isolated, but rejected, so this is false
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /* *************************************** testShortCircuitedWithFallbackRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testShortCircuitedWithFallbackRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnShortCircuitedWithFallback(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// all synchronous

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testShortCircuitedWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnShortCircuitedWithFallback(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testShortCircuitedWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnShortCircuitedWithFallback(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [RxComputation]
     */
    @Test
    public void testShortCircuitedWithFallbackRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnShortCircuitedWithFallback(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// fallback is performed by the calling thread

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().equals(Thread.currentThread()));// rejected so we stay on calling thread

        // thread isolated ... but rejected so not executed in a thread
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testShortCircuitedWithFallbackRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnShortCircuitedWithFallback(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// we capture and set the context once the user provided Observable emits

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler from getFallback

        // thread isolated ... but rejected so not executed in a thread
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testShortCircuitedWithFallbackRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnShortCircuitedWithFallback(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler from getFallback

        // thread isolated ... but rejected so not executed in a thread
        Assert.assertFalse(results.command.isExecutedInThread());
    }

    /* *************************************** testTimeoutRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation. Only [Main] thread is involved in this.
     */
    @Test
    public void testTimeoutRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeout(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().equals(Thread.currentThread()));// all synchronous

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("HystrixTimer"));// timeout schedules on HystrixTimer since the original thread was timed out

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testTimeoutRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeout(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the timeout captures the context so it exists

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("HystrixTimer"));// timeout schedules on HystrixTimer since the original thread was timed out

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testTimeoutRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeout(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("HystrixTimer"));// timeout schedules on HystrixTimer since the original thread was timed out

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /* *************************************** testTimeoutWithFallbackRequestContext *********************************** */
    /**
     * Synchronous Observable and semaphore isolation.
     */
    @Test
    public void testTimeoutWithFallbackRequestContextWithSemaphoreIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeoutWithFallback(SEMAPHORE, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().getName().startsWith("HystrixTimer"));// timeout uses HystrixTimer thread

        // (this use case is a little odd as it should generally not be the case that we are "timing out" a synchronous observable on semaphore isolation)
        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("HystrixTimer"));// timeout uses HystrixTimer thread

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Async Observable and semaphore isolation. User provided thread [RxNewThread] does everything.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testTimeoutWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeoutWithFallback(SEMAPHORE, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the timeout captures the context so it exists

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testTimeoutWithFallbackRequestContextWithSemaphoreIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeoutWithFallback(SEMAPHORE, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // semaphore isolated
        Assert.assertFalse(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Synchronous Observable and thread isolation. Work done on [hystrix-OWNER_ONE] thread and then observed on [HystrixTimer]
     */
    @Test
    public void testTimeoutWithFallbackRequestContextWithThreadIsolatedSynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeoutWithFallback(THREAD, Schedulers.immediate());
        Assert.assertTrue(results.isContextInitialized.get());
        Assert.assertTrue(results.originThread.get().getName().startsWith("HystrixTimer"));// timeout uses HystrixTimer thread for fallback

        Assert.assertTrue(results.isContextInitializedObserveOn.get());
        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("HystrixTimer"));// fallback uses the timeout thread

        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Async Observable and thread isolation. User provided thread [RxNetThread] executes Observable and then [RxComputation] observes the onNext calls.
     *
     * NOTE: RequestContext will NOT exist on that thread.
     *
     * An async Observable running on its own thread will not have access to the request context unless the user manages the context.
     */
    @Test
    public void testTimeoutWithFallbackRequestContextWithThreadIsolatedAsynchronousObservable() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeoutWithFallback(THREAD, Schedulers.newThread());
        Assert.assertFalse(results.isContextInitialized.get());// it won't have request context as it's on a user provided thread/scheduler

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the timeout captures the context so it exists

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
        HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Async Observable and semaphore isolation WITH functioning RequestContext
     *
     * Use HystrixContextScheduler to make the user provided scheduler capture context.
     */
    @Test
    public void testTimeoutWithFallbackRequestContextWithThreadIsolatedAsynchronousObservableAndCapturedContextScheduler() {
        HystrixObservableCommandTest.RequestContextTestResults results = testRequestContextOnTimeoutWithFallback(THREAD, new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(Schedulers.newThread()));
        Assert.assertTrue(results.isContextInitialized.get());// the user scheduler captures context

        Assert.assertTrue(results.originThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        Assert.assertTrue(results.isContextInitializedObserveOn.get());// the user scheduler captures context

        Assert.assertTrue(results.observeOnThread.get().getName().startsWith("RxNewThread"));// the user provided thread/scheduler

        // thread isolated
        Assert.assertTrue(results.command.isExecutedInThread());
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        // HystrixCircuitBreaker.Factory.reset();
    }

    /**
     * Test support of multiple onNext events.
     */
    @Test
    public void testExecutionSuccessWithMultipleEvents() {
        try {
            HystrixObservableCommandTest.TestCommandWithMultipleValues command = new HystrixObservableCommandTest.TestCommandWithMultipleValues();
            Assert.assertEquals(Arrays.asList(true, false, true), observe().toList().toBlocking().single());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertTrue(isSuccessfulExecution());
            assertCommandExecutionEvents(command, EMIT, EMIT, EMIT, SUCCESS);
            Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(1);
            // semaphore isolated
            Assert.assertFalse(isExecutedInThread());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We received an exception.");
        }
    }

    /**
     * Test behavior when some onNext are received and then a failure.
     */
    @Test
    public void testExecutionPartialSuccess() {
        try {
            HystrixObservableCommandTest.TestPartialSuccess command = new HystrixObservableCommandTest.TestPartialSuccess();
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            toObservable().subscribe(ts);
            ts.awaitTerminalEvent();
            ts.assertReceivedOnNext(Arrays.asList(1, 2, 3));
            Assert.assertEquals(1, ts.getOnErrorEvents().size());
            Assert.assertFalse(isSuccessfulExecution());
            Assert.assertTrue(isFailedExecution());
            // we will have an exception
            Assert.assertNotNull(getFailedExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            assertCommandExecutionEvents(command, EMIT, EMIT, EMIT, FAILURE, FALLBACK_MISSING);
            assertSaneHystrixRequestLog(1);
            Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
            // semaphore isolated
            Assert.assertFalse(isExecutedInThread());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We received an exception.");
        }
    }

    /**
     * Test behavior when some onNext are received and then a failure.
     */
    @Test
    public void testExecutionPartialSuccessWithFallback() {
        try {
            HystrixObservableCommandTest.TestPartialSuccessWithFallback command = new HystrixObservableCommandTest.TestPartialSuccessWithFallback();
            TestSubscriber<Boolean> ts = new TestSubscriber<Boolean>();
            toObservable().subscribe(ts);
            ts.awaitTerminalEvent();
            ts.assertReceivedOnNext(Arrays.asList(false, true, false, true, false, true, false));
            ts.assertNoErrors();
            Assert.assertFalse(isSuccessfulExecution());
            Assert.assertTrue(isFailedExecution());
            Assert.assertNotNull(getFailedExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            assertCommandExecutionEvents(command, EMIT, EMIT, EMIT, FAILURE, FALLBACK_EMIT, FALLBACK_EMIT, FALLBACK_EMIT, FALLBACK_EMIT, FALLBACK_SUCCESS);
            Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(1);
            // semaphore isolated
            Assert.assertFalse(isExecutedInThread());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We received an exception.");
        }
    }

    @Test
    public void testEarlyUnsubscribeDuringExecutionViaToObservable() {
        class AsyncCommand extends HystrixObservableCommand<Boolean> {
            public AsyncCommand() {
                super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            }

            @Override
            protected Observable<Boolean> construct() {
                return Observable.defer(new rx.functions.Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        try {
                            Thread.sleep(100);
                            return Observable.just(true);
                        } catch (InterruptedException ex) {
                            return Observable.error(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        }
        HystrixObservableCommand<Boolean> cmd = new AsyncCommand();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Boolean> o = cmd.toObservable();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("OnUnsubscribe");
                latch.countDown();
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("OnCompleted");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("OnError : " + e));
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println(("OnNext : " + b));
            }
        });
        try {
            s.unsubscribe();
            Assert.assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
            Assert.assertEquals("Number of execution semaphores in use", 0, cmd.getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use", 0, cmd.getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(cmd.isExecutionComplete());
            Assert.assertFalse(cmd.isExecutedInThread());
            System.out.println(("EventCounts : " + (cmd.getEventCounts())));
            System.out.println(("Execution Time : " + (cmd.getExecutionTimeInMilliseconds())));
            System.out.println(("Is Successful : " + (cmd.isSuccessfulExecution())));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testEarlyUnsubscribeDuringExecutionViaObserve() {
        class AsyncCommand extends HystrixObservableCommand<Boolean> {
            public AsyncCommand() {
                super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            }

            @Override
            protected Observable<Boolean> construct() {
                return Observable.defer(new rx.functions.Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        try {
                            Thread.sleep(100);
                            return Observable.just(true);
                        } catch (InterruptedException ex) {
                            return Observable.error(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        }
        HystrixObservableCommand<Boolean> cmd = new AsyncCommand();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Boolean> o = cmd.observe();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("OnUnsubscribe");
                latch.countDown();
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("OnCompleted");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("OnError : " + e));
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println(("OnNext : " + b));
            }
        });
        try {
            s.unsubscribe();
            Assert.assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
            Assert.assertEquals("Number of execution semaphores in use", 0, cmd.getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use", 0, cmd.getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(cmd.isExecutionComplete());
            Assert.assertFalse(cmd.isExecutedInThread());
            System.out.println(("EventCounts : " + (cmd.getEventCounts())));
            System.out.println(("Execution Time : " + (cmd.getExecutionTimeInMilliseconds())));
            System.out.println(("Is Successful : " + (cmd.isSuccessfulExecution())));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testEarlyUnsubscribeDuringFallback() {
        class AsyncCommand extends HystrixObservableCommand<Boolean> {
            public AsyncCommand() {
                super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            }

            @Override
            protected Observable<Boolean> construct() {
                return Observable.error(new RuntimeException("construct failure"));
            }

            @Override
            protected Observable<Boolean> resumeWithFallback() {
                return Observable.defer(new rx.functions.Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        try {
                            Thread.sleep(100);
                            return Observable.just(false);
                        } catch (InterruptedException ex) {
                            return Observable.error(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        }
        HystrixObservableCommand<Boolean> cmd = new AsyncCommand();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Boolean> o = cmd.toObservable();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("OnUnsubscribe");
                latch.countDown();
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("OnCompleted");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("OnError : " + e));
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println(("OnNext : " + b));
            }
        });
        try {
            Thread.sleep(10);// give fallback a chance to fire

            s.unsubscribe();
            Assert.assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
            Assert.assertEquals("Number of execution semaphores in use", 0, cmd.getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use", 0, cmd.getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(cmd.isExecutionComplete());
            Assert.assertFalse(cmd.isExecutedInThread());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /* ******************************************************************************** */
    /* ******************************************************************************** */
    /* private HystrixCommand class implementations for unit testing */
    /* ******************************************************************************** */
    /* ******************************************************************************** */
    static AtomicInteger uniqueNameCounter = new AtomicInteger(0);

    private static class FlexibleTestHystrixObservableCommand {
        public static Integer EXECUTE_VALUE = 1;

        public static Integer FALLBACK_VALUE = 11;

        public static HystrixObservableCommandTest.AbstractFlexibleTestHystrixObservableCommand from(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, AbstractTestHystrixCommand.FallbackResult fallbackResult, int fallbackLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, AbstractCommand.TryableSemaphore executionSemaphore, AbstractCommand.TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            if (fallbackResult.equals(UNIMPLEMENTED)) {
                return new HystrixObservableCommandTest.FlexibleTestHystrixObservableCommandNoFallback(commandKey, isolationStrategy, executionResult, executionLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
            } else {
                return new HystrixObservableCommandTest.FlexibleTestHystrixObservableCommandWithFallback(commandKey, isolationStrategy, executionResult, executionLatency, fallbackResult, fallbackLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
            }
        }
    }

    private static class AbstractFlexibleTestHystrixObservableCommand extends TestHystrixObservableCommand<Integer> {
        private final AbstractTestHystrixCommand.ExecutionResult executionResult;

        private final int executionLatency;

        private final AbstractTestHystrixCommand.CacheEnabled cacheEnabled;

        private final Object value;

        public AbstractFlexibleTestHystrixObservableCommand(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            super(TestHystrixObservableCommand.testPropsBuilder(circuitBreaker).setCommandKey(commandKey).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setThreadPool(threadPool).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationStrategy).withExecutionTimeoutInMilliseconds(timeout).withCircuitBreakerEnabled((!circuitBreakerDisabled))).setExecutionSemaphore(executionSemaphore).setFallbackSemaphore(fallbackSemaphore));
            this.executionResult = executionResult;
            this.executionLatency = executionLatency;
            this.cacheEnabled = cacheEnabled;
            this.value = value;
        }

        @Override
        protected Observable<Integer> construct() {
            if ((executionResult) == (AbstractTestHystrixCommand.ExecutionResult.FAILURE)) {
                addLatency(executionLatency);
                throw new RuntimeException("Execution Sync Failure for TestHystrixObservableCommand");
            } else
                if ((executionResult) == (HYSTRIX_FAILURE)) {
                    addLatency(executionLatency);
                    throw new HystrixRuntimeException(FailureType.COMMAND_EXCEPTION, HystrixObservableCommandTest.AbstractFlexibleTestHystrixObservableCommand.class, "Execution Hystrix Failure for TestHystrixObservableCommand", new RuntimeException("Execution Failure for TestHystrixObservableCommand"), new RuntimeException("Fallback Failure for TestHystrixObservableCommand"));
                } else
                    if ((executionResult) == (NOT_WRAPPED_FAILURE)) {
                        addLatency(executionLatency);
                        throw new NotWrappedByHystrixTestRuntimeException();
                    } else
                        if ((executionResult) == (RECOVERABLE_ERROR)) {
                            addLatency(executionLatency);
                            throw new Error("Execution Sync Error for TestHystrixObservableCommand");
                        } else
                            if ((executionResult) == (UNRECOVERABLE_ERROR)) {
                                addLatency(executionLatency);
                                throw new OutOfMemoryError("Execution Sync OOME for TestHystrixObservableCommand");
                            } else
                                if ((executionResult) == (BAD_REQUEST)) {
                                    addLatency(executionLatency);
                                    throw new HystrixBadRequestException("Execution Bad Request Exception for TestHystrixObservableCommand");
                                }





            return Observable.create(new rx.Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " construct() method has been subscribed to"));
                    addLatency(executionLatency);
                    if ((executionResult) == (SUCCESS)) {
                        subscriber.onNext(1);
                        subscriber.onCompleted();
                    } else
                        if ((executionResult) == (MULTIPLE_EMITS_THEN_SUCCESS)) {
                            subscriber.onNext(2);
                            subscriber.onNext(3);
                            subscriber.onNext(4);
                            subscriber.onNext(5);
                            subscriber.onCompleted();
                        } else
                            if ((executionResult) == (MULTIPLE_EMITS_THEN_FAILURE)) {
                                subscriber.onNext(6);
                                subscriber.onNext(7);
                                subscriber.onNext(8);
                                subscriber.onNext(9);
                                subscriber.onError(new RuntimeException("Execution Async Failure For TestHystrixObservableCommand after 4 emits"));
                            } else
                                if ((executionResult) == (ASYNC_FAILURE)) {
                                    subscriber.onError(new RuntimeException("Execution Async Failure for TestHystrixObservableCommand after 0 emits"));
                                } else
                                    if ((executionResult) == (ASYNC_HYSTRIX_FAILURE)) {
                                        subscriber.onError(new HystrixRuntimeException(FailureType.COMMAND_EXCEPTION, HystrixObservableCommandTest.AbstractFlexibleTestHystrixObservableCommand.class, "Execution Hystrix Failure for TestHystrixObservableCommand", new RuntimeException("Execution Failure for TestHystrixObservableCommand"), new RuntimeException("Fallback Failure for TestHystrixObservableCommand")));
                                    } else
                                        if ((executionResult) == (ASYNC_RECOVERABLE_ERROR)) {
                                            subscriber.onError(new Error("Execution Async Error for TestHystrixObservableCommand"));
                                        } else
                                            if ((executionResult) == (ASYNC_UNRECOVERABLE_ERROR)) {
                                                subscriber.onError(new OutOfMemoryError("Execution Async OOME for TestHystrixObservableCommand"));
                                            } else
                                                if ((executionResult) == (ASYNC_BAD_REQUEST)) {
                                                    subscriber.onError(new HystrixBadRequestException("Execution Async Bad Request Exception for TestHystrixObservableCommand"));
                                                } else {
                                                    subscriber.onError(new RuntimeException(("You passed in a executionResult enum that can't be represented in HystrixObservableCommand: " + (executionResult))));
                                                }







                }
            });
        }

        @Override
        public String getCacheKey() {
            if ((cacheEnabled) == (YES))
                return value.toString();
            else
                return null;

        }

        protected void addLatency(int latency) {
            if (latency > 0) {
                try {
                    System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " About to sleep for : ") + latency));
                    Thread.sleep(latency);
                    System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Woke up from sleep!"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // ignore and sleep some more to simulate a dependency that doesn't obey interrupts
                    try {
                        Thread.sleep(latency);
                    } catch (Exception e2) {
                        // ignore
                    }
                    System.out.println("after interruption with extra sleep");
                }
            }
        }
    }

    private static class FlexibleTestHystrixObservableCommandWithFallback extends HystrixObservableCommandTest.AbstractFlexibleTestHystrixObservableCommand {
        private final AbstractTestHystrixCommand.FallbackResult fallbackResult;

        private final int fallbackLatency;

        public FlexibleTestHystrixObservableCommandWithFallback(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, AbstractTestHystrixCommand.FallbackResult fallbackResult, int fallbackLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            super(commandKey, isolationStrategy, executionResult, executionLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
            this.fallbackResult = fallbackResult;
            this.fallbackLatency = fallbackLatency;
        }

        @Override
        protected Observable<Integer> resumeWithFallback() {
            if ((fallbackResult) == (FAILURE)) {
                addLatency(fallbackLatency);
                throw new RuntimeException("Fallback Sync Failure for TestHystrixCommand");
            } else
                if ((fallbackResult) == (UNIMPLEMENTED)) {
                    addLatency(fallbackLatency);
                    return super.resumeWithFallback();
                }

            return Observable.create(new rx.Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    addLatency(fallbackLatency);
                    if ((fallbackResult) == (AbstractTestHystrixCommand.FallbackResult.SUCCESS)) {
                        subscriber.onNext(11);
                        subscriber.onCompleted();
                    } else
                        if ((fallbackResult) == (AbstractTestHystrixCommand.FallbackResult.MULTIPLE_EMITS_THEN_SUCCESS)) {
                            subscriber.onNext(12);
                            subscriber.onNext(13);
                            subscriber.onNext(14);
                            subscriber.onNext(15);
                            subscriber.onCompleted();
                        } else
                            if ((fallbackResult) == (AbstractTestHystrixCommand.FallbackResult.MULTIPLE_EMITS_THEN_FAILURE)) {
                                subscriber.onNext(16);
                                subscriber.onNext(17);
                                subscriber.onNext(18);
                                subscriber.onNext(19);
                                subscriber.onError(new RuntimeException("Fallback Async Failure For TestHystrixObservableCommand after 4 emits"));
                            } else
                                if ((fallbackResult) == (AbstractTestHystrixCommand.FallbackResult.ASYNC_FAILURE)) {
                                    subscriber.onError(new RuntimeException("Fallback Async Failure for TestHystrixCommand after 0 fallback emits"));
                                } else {
                                    subscriber.onError(new RuntimeException(("You passed in a fallbackResult enum that can't be represented in HystrixObservableCommand: " + (fallbackResult))));
                                }



                }
            });
        }
    }

    private static class FlexibleTestHystrixObservableCommandNoFallback extends HystrixObservableCommandTest.AbstractFlexibleTestHystrixObservableCommand {
        public FlexibleTestHystrixObservableCommandNoFallback(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            super(commandKey, isolationStrategy, executionResult, executionLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class SuccessfulTestCommand extends TestHystrixObservableCommand<Boolean> {
        public SuccessfulTestCommand(ExecutionIsolationStrategy isolationStrategy) {
            this(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationStrategy));
        }

        public SuccessfulTestCommand(HystrixCommandProperties.Setter properties) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(properties));
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.just(true).subscribeOn(Schedulers.computation());
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class TestCommandWithMultipleValues extends TestHystrixObservableCommand<Boolean> {
        public TestCommandWithMultipleValues() {
            this(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE));
        }

        public TestCommandWithMultipleValues(HystrixCommandProperties.Setter properties) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(properties));
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.just(true, false, true).subscribeOn(Schedulers.computation());
        }
    }

    private static class TestPartialSuccess extends TestHystrixObservableCommand<Integer> {
        TestPartialSuccess() {
            super(TestHystrixObservableCommand.testPropsBuilder());
        }

        @Override
        protected Observable<Integer> construct() {
            return Observable.just(1, 2, 3).concatWith(Observable.<Integer>error(new RuntimeException("forced error"))).subscribeOn(Schedulers.computation());
        }
    }

    private static class TestPartialSuccessWithFallback extends TestHystrixObservableCommand<Boolean> {
        TestPartialSuccessWithFallback() {
            super(TestHystrixObservableCommand.testPropsBuilder());
        }

        public TestPartialSuccessWithFallback(ExecutionIsolationStrategy isolationStrategy) {
            this(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationStrategy));
        }

        public TestPartialSuccessWithFallback(HystrixCommandProperties.Setter properties) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(properties));
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.just(false, true, false).concatWith(Observable.<Boolean>error(new RuntimeException("forced error"))).subscribeOn(Schedulers.computation());
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            return Observable.just(true, false, true, false);
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class DynamicOwnerTestCommand extends TestHystrixObservableCommand<Boolean> {
        public DynamicOwnerTestCommand(HystrixCommandGroupKey owner) {
            super(TestHystrixObservableCommand.testPropsBuilder().setOwner(owner));
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("successfully executed");
            return Observable.just(true).subscribeOn(Schedulers.computation());
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class DynamicOwnerAndKeyTestCommand extends TestHystrixObservableCommand<Boolean> {
        public DynamicOwnerAndKeyTestCommand(HystrixCommandGroupKey owner, HystrixCommandKey key) {
            super(TestHystrixObservableCommand.testPropsBuilder().setOwner(owner).setCommandKey(key).setCircuitBreaker(null).setMetrics(null));
            // we specifically are NOT passing in a circuit breaker here so we test that it creates a new one correctly based on the dynamic key
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("successfully executed");
            return Observable.just(true).subscribeOn(Schedulers.computation());
        }
    }

    /**
     * Failed execution with unknown exception (not HystrixException) - no fallback implementation.
     */
    private static class UnknownFailureTestCommandWithoutFallback extends TestHystrixObservableCommand<Boolean> {
        private final boolean asyncException;

        private UnknownFailureTestCommandWithoutFallback(ExecutionIsolationStrategy isolationStrategy, boolean asyncException) {
            super(TestHystrixObservableCommand.testPropsBuilder(isolationStrategy, new HystrixCircuitBreakerTest.TestCircuitBreaker()));
            this.asyncException = asyncException;
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("*** simulated failed execution ***");
            RuntimeException ex = new RuntimeException("we failed with an unknown issue");
            if (asyncException) {
                return Observable.error(ex);
            } else {
                throw ex;
            }
        }
    }

    /**
     * Failed execution with known exception (HystrixException) - no fallback implementation.
     */
    private static class KnownFailureTestCommandWithoutFallback extends TestHystrixObservableCommand<Boolean> {
        final boolean asyncException;

        private KnownFailureTestCommandWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, ExecutionIsolationStrategy isolationStrategy, boolean asyncException) {
            super(TestHystrixObservableCommand.testPropsBuilder(isolationStrategy, circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.asyncException = asyncException;
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("*** simulated failed execution ***");
            RuntimeException ex = new RuntimeException("we failed with a simulated issue");
            if (asyncException) {
                return Observable.error(ex);
            } else {
                throw ex;
            }
        }
    }

    /**
     * Failed execution - fallback implementation successfully returns value.
     */
    private static class KnownFailureTestCommandWithFallback extends TestHystrixObservableCommand<Boolean> {
        private final boolean asyncException;

        public KnownFailureTestCommandWithFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, ExecutionIsolationStrategy isolationStrategy, boolean asyncException) {
            super(TestHystrixObservableCommand.testPropsBuilder(isolationStrategy, circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.asyncException = asyncException;
        }

        public KnownFailureTestCommandWithFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean fallbackEnabled, boolean asyncException) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withFallbackEnabled(fallbackEnabled).withExecutionIsolationStrategy(SEMAPHORE)));
            this.asyncException = asyncException;
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("*** simulated failed execution ***");
            RuntimeException ex = new RuntimeException("we failed with a simulated issue");
            if (asyncException) {
                return Observable.error(ex);
            } else {
                throw ex;
            }
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            return Observable.just(false).subscribeOn(Schedulers.computation());
        }
    }

    /**
     * Failed execution with {@link HystrixBadRequestException}
     */
    private static class KnownHystrixBadRequestFailureTestCommand extends TestHystrixObservableCommand<Boolean> {
        public static final boolean ASYNC_EXCEPTION = true;

        public static final boolean SYNC_EXCEPTION = false;

        private final boolean asyncException;

        public KnownHystrixBadRequestFailureTestCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, ExecutionIsolationStrategy isolationStrategy, boolean asyncException) {
            super(TestHystrixObservableCommand.testPropsBuilder(isolationStrategy, circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.asyncException = asyncException;
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("*** simulated failed with HystrixBadRequestException  ***");
            RuntimeException ex = new HystrixBadRequestException("we failed with a simulated issue");
            if (asyncException) {
                return Observable.error(ex);
            } else {
                throw ex;
            }
        }
    }

    /**
     * Failed execution - fallback implementation throws exception.
     */
    private static class KnownFailureTestCommandWithFallbackFailure extends TestHystrixObservableCommand<Boolean> {
        private final boolean asyncConstructException;

        private final boolean asyncFallbackException;

        private KnownFailureTestCommandWithFallbackFailure(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, ExecutionIsolationStrategy isolationStrategy, boolean asyncConstructException, boolean asyncFallbackException) {
            super(TestHystrixObservableCommand.testPropsBuilder(isolationStrategy, circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.asyncConstructException = asyncConstructException;
            this.asyncFallbackException = asyncFallbackException;
        }

        @Override
        protected Observable<Boolean> construct() {
            RuntimeException ex = new RuntimeException("we failed with a simulated issue");
            System.out.println("*** simulated failed execution ***");
            if (asyncConstructException) {
                return Observable.error(ex);
            } else {
                throw ex;
            }
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            RuntimeException ex = new RuntimeException("failed while getting fallback");
            if (asyncFallbackException) {
                return Observable.error(ex);
            } else {
                throw ex;
            }
        }
    }

    /**
     * A Command implementation that supports caching.
     */
    private static class SuccessfulCacheableCommand<T> extends TestHystrixObservableCommand<T> {
        private final boolean cacheEnabled;

        private volatile boolean executed = false;

        private final T value;

        public SuccessfulCacheableCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean cacheEnabled, T value) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(THREAD)));
            this.value = value;
            this.cacheEnabled = cacheEnabled;
        }

        @Override
        protected Observable<T> construct() {
            executed = true;
            System.out.println("successfully executed");
            return Observable.just(value).subscribeOn(Schedulers.computation());
        }

        public boolean isCommandRunningInThread() {
            return super.getProperties().executionIsolationStrategy().get().equals(THREAD);
        }

        @Override
        public String getCacheKey() {
            if (cacheEnabled)
                return value.toString();
            else
                return null;

        }
    }

    /**
     * A Command implementation that supports caching.
     */
    private static class SuccessfulCacheableCommandViaSemaphore extends TestHystrixObservableCommand<String> {
        private final boolean cacheEnabled;

        private volatile boolean executed = false;

        private final String value;

        public SuccessfulCacheableCommandViaSemaphore(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean cacheEnabled, String value) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE)));
            this.value = value;
            this.cacheEnabled = cacheEnabled;
        }

        @Override
        protected Observable<String> construct() {
            executed = true;
            System.out.println("successfully executed");
            return Observable.just(value).subscribeOn(Schedulers.computation());
        }

        public boolean isCommandRunningInThread() {
            return super.getProperties().executionIsolationStrategy().get().equals(THREAD);
        }

        @Override
        public String getCacheKey() {
            if (cacheEnabled)
                return value;
            else
                return null;

        }
    }

    /**
     * A Command implementation that supports caching and execution takes a while.
     * <p>
     * Used to test scenario where Futures are returned with a backing call still executing.
     */
    private static class SlowCacheableCommand extends TestHystrixObservableCommand<String> {
        private final String value;

        private final int duration;

        private volatile boolean executed = false;

        public SlowCacheableCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, String value, int duration) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandKey(HystrixCommandKey.Factory.asKey("ObservableSlowCacheable")).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.value = value;
            this.duration = duration;
        }

        @Override
        protected Observable<String> construct() {
            executed = true;
            return Observable.just(value).delay(duration, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation()).doOnNext(new rx.functions.Action1<String>() {
                @Override
                public void call(String t1) {
                    System.out.println("successfully executed");
                }
            });
        }

        @Override
        public String getCacheKey() {
            return value;
        }
    }

    /**
     * Successful execution - no fallback implementation, circuit-breaker disabled.
     */
    private static class TestCommandWithoutCircuitBreaker extends TestHystrixObservableCommand<Boolean> {
        private TestCommandWithoutCircuitBreaker() {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withCircuitBreakerEnabled(false)));
        }

        @Override
        protected Observable<Boolean> construct() {
            System.out.println("successfully executed");
            return Observable.just(true).subscribeOn(Schedulers.computation());
        }
    }

    private static class NoRequestCacheTimeoutWithoutFallback extends TestHystrixObservableCommand<Boolean> {
        public NoRequestCacheTimeoutWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixObservableCommand.testPropsBuilder(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionTimeoutInMilliseconds(200).withCircuitBreakerEnabled(false)));
            // we want it to timeout
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> s) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println((">>>> Sleep Interrupted: " + (e.getMessage())));
                        // e.printStackTrace();
                    }
                    s.onNext(true);
                    s.onCompleted();
                }
            }).subscribeOn(Schedulers.computation());
        }

        @Override
        public String getCacheKey() {
            return null;
        }
    }

    /**
     * The run() will take time. Configurable fallback implementation.
     */
    private static class TestSemaphoreCommand extends TestHystrixObservableCommand<Boolean> {
        private final long executionSleep;

        private static final int RESULT_SUCCESS = 1;

        private static final int RESULT_FAILURE = 2;

        private static final int RESULT_BAD_REQUEST_EXCEPTION = 3;

        private final int resultBehavior;

        private static final int FALLBACK_SUCCESS = 10;

        private static final int FALLBACK_NOT_IMPLEMENTED = 11;

        private static final int FALLBACK_FAILURE = 12;

        private final int fallbackBehavior;

        private static final boolean FALLBACK_FAILURE_SYNC = false;

        private static final boolean FALLBACK_FAILURE_ASYNC = true;

        private final boolean asyncFallbackException;

        private TestSemaphoreCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, int executionSemaphoreCount, long executionSleep, int resultBehavior, int fallbackBehavior) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionIsolationSemaphoreMaxConcurrentRequests(executionSemaphoreCount)));
            this.executionSleep = executionSleep;
            this.resultBehavior = resultBehavior;
            this.fallbackBehavior = fallbackBehavior;
            this.asyncFallbackException = HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_FAILURE_ASYNC;
        }

        private TestSemaphoreCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, TryableSemaphore semaphore, long executionSleep, int resultBehavior, int fallbackBehavior) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE)).setExecutionSemaphore(semaphore));
            this.executionSleep = executionSleep;
            this.resultBehavior = resultBehavior;
            this.fallbackBehavior = fallbackBehavior;
            this.asyncFallbackException = HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_FAILURE_ASYNC;
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    try {
                        Thread.sleep(executionSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((resultBehavior) == (HystrixObservableCommandTest.TestSemaphoreCommand.RESULT_SUCCESS)) {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    } else
                        if ((resultBehavior) == (HystrixObservableCommandTest.TestSemaphoreCommand.RESULT_FAILURE)) {
                            subscriber.onError(new RuntimeException("TestSemaphoreCommand failure"));
                        } else
                            if ((resultBehavior) == (HystrixObservableCommandTest.TestSemaphoreCommand.RESULT_BAD_REQUEST_EXCEPTION)) {
                                subscriber.onError(new HystrixBadRequestException("TestSemaphoreCommand BadRequestException"));
                            } else {
                                subscriber.onError(new IllegalStateException("Didn't use a proper enum for result behavior"));
                            }


                }
            });
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            if ((fallbackBehavior) == (HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_SUCCESS)) {
                return Observable.just(false);
            } else
                if ((fallbackBehavior) == (HystrixObservableCommandTest.TestSemaphoreCommand.FALLBACK_FAILURE)) {
                    RuntimeException ex = new RuntimeException("fallback failure");
                    if (asyncFallbackException) {
                        return Observable.error(ex);
                    } else {
                        throw ex;
                    }
                } else {
                    // FALLBACK_NOT_IMPLEMENTED
                    return super.resumeWithFallback();
                }

        }
    }

    /**
     * The construct() will take time once subscribed to. No fallback implementation.
     *
     * Used for making sure Thread and Semaphore isolation are separated from each other.
     */
    private static class TestThreadIsolationWithSemaphoreSetSmallCommand extends TestHystrixObservableCommand<Boolean> {
        private final Action0 action;

        private TestThreadIsolationWithSemaphoreSetSmallCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, int poolSize, Action0 action) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(HystrixObservableCommandTest.TestThreadIsolationWithSemaphoreSetSmallCommand.class.getSimpleName())).setThreadPoolPropertiesDefaults(HystrixThreadPoolPropertiesTest.getUnitTestPropertiesBuilder().withCoreSize(poolSize).withMaximumSize(poolSize).withMaxQueueSize(0)).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(THREAD).withExecutionIsolationSemaphoreMaxConcurrentRequests(1)));
            this.action = action;
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> s) {
                    action.call();
                    s.onNext(true);
                    s.onCompleted();
                }
            });
        }
    }

    /**
     * Semaphore based command that allows caller to use latches to know when it has started and signal when it
     * would like the command to finish
     */
    private static class LatchedSemaphoreCommand extends TestHystrixObservableCommand<Boolean> {
        private final CountDownLatch startLatch;

        private final CountDownLatch waitLatch;

        /**
         *
         *
         * @param circuitBreaker
         * 		circuit breaker (passed in so it may be shared)
         * @param semaphore
         * 		semaphore (passed in so it may be shared)
         * @param startLatch
         * 		this command calls {@link CountDownLatch#countDown()} immediately upon running
         * @param waitLatch
         * 		this command calls {@link CountDownLatch#await()} once it starts
         * 		to run. The caller can use the latch to signal the command to finish
         */
        private LatchedSemaphoreCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, TryableSemaphoreActual semaphore, CountDownLatch startLatch, CountDownLatch waitLatch) {
            this("Latched", circuitBreaker, semaphore, startLatch, waitLatch);
        }

        private LatchedSemaphoreCommand(String commandName, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, TryableSemaphoreActual semaphore, CountDownLatch startLatch, CountDownLatch waitLatch) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandKey(HystrixCommandKey.Factory.asKey(commandName)).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withCircuitBreakerEnabled(false)).setExecutionSemaphore(semaphore));
            this.startLatch = startLatch;
            this.waitLatch = waitLatch;
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> s) {
                    // signals caller that run has started
                    startLatch.countDown();
                    try {
                        // waits for caller to countDown latch
                        waitLatch.await();
                        s.onNext(true);
                        s.onCompleted();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        s.onNext(false);
                        s.onCompleted();
                    }
                }
            }).subscribeOn(Schedulers.computation());
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            return Observable.defer(new rx.functions.Func0<Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call() {
                    startLatch.countDown();
                    return Observable.just(false);
                }
            });
        }
    }

    /**
     * The construct() will take time once subscribed to. Contains fallback.
     */
    private static class TestSemaphoreCommandWithFallback extends TestHystrixObservableCommand<Boolean> {
        private final long executionSleep;

        private final Observable<Boolean> fallback;

        private TestSemaphoreCommandWithFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, int executionSemaphoreCount, long executionSleep, Boolean fallback) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionIsolationSemaphoreMaxConcurrentRequests(executionSemaphoreCount)));
            this.executionSleep = executionSleep;
            this.fallback = Observable.just(fallback);
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> s) {
                    try {
                        Thread.sleep(executionSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.onNext(true);
                    s.onCompleted();
                }
            }).subscribeOn(Schedulers.io());
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            return fallback;
        }
    }

    private static class InterruptibleCommand extends TestHystrixObservableCommand<Boolean> {
        public InterruptibleCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean shouldInterrupt) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationThreadInterruptOnTimeout(shouldInterrupt).withExecutionTimeoutInMilliseconds(100)));
        }

        private volatile boolean hasBeenInterrupted;

        public boolean hasBeenInterrupted() {
            return hasBeenInterrupted;
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.defer(new rx.functions.Func0<Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted!");
                        e.printStackTrace();
                        hasBeenInterrupted = true;
                    }
                    return Observable.just(hasBeenInterrupted);
                }
            }).subscribeOn(Schedulers.io());
        }
    }

    private static class RequestCacheNullPointerExceptionCase extends TestHystrixObservableCommand<Boolean> {
        public RequestCacheNullPointerExceptionCase(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionTimeoutInMilliseconds(200)));
            // we want it to timeout
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> s) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.onNext(true);
                    s.onCompleted();
                }
            }).subscribeOn(Schedulers.computation());
        }

        @Override
        protected Observable<Boolean> resumeWithFallback() {
            return Observable.just(false).subscribeOn(Schedulers.computation());
        }

        @Override
        public String getCacheKey() {
            return "A";
        }
    }

    private static class RequestCacheTimeoutWithoutFallback extends TestHystrixObservableCommand<Boolean> {
        public RequestCacheTimeoutWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionTimeoutInMilliseconds(200)));
            // we want it to timeout
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> s) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println((">>>> Sleep Interrupted: " + (e.getMessage())));
                        // e.printStackTrace();
                    }
                    s.onNext(true);
                    s.onCompleted();
                }
            }).subscribeOn(Schedulers.computation());
        }

        @Override
        public String getCacheKey() {
            return "A";
        }
    }

    private static class RequestCacheThreadRejectionWithoutFallback extends TestHystrixObservableCommand<Boolean> {
        final CountDownLatch completionLatch;

        public RequestCacheThreadRejectionWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, CountDownLatch completionLatch) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(THREAD)).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setThreadPool(new HystrixThreadPool() {
                @Override
                public ThreadPoolExecutor getExecutor() {
                    return null;
                }

                @Override
                public void markThreadExecution() {
                }

                @Override
                public void markThreadCompletion() {
                }

                @Override
                public void markThreadRejection() {
                }

                @Override
                public boolean isQueueSpaceAvailable() {
                    // always return false so we reject everything
                    return false;
                }

                @Override
                public Scheduler getScheduler() {
                    return new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(HystrixPlugins.getInstance().getConcurrencyStrategy(), this);
                }

                @Override
                public Scheduler getScheduler(rx.functions.Func0<Boolean> shouldInterruptThread) {
                    return new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(HystrixPlugins.getInstance().getConcurrencyStrategy(), this, shouldInterruptThread);
                }
            }));
            this.completionLatch = completionLatch;
        }

        @Override
        protected Observable<Boolean> construct() {
            try {
                if (completionLatch.await(1000, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("timed out waiting on completionLatch");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Observable.just(true);
        }

        @Override
        public String getCacheKey() {
            return "A";
        }
    }

    private static class CommandWithErrorThrown extends TestHystrixObservableCommand<Boolean> {
        private final boolean asyncException;

        public CommandWithErrorThrown(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean asyncException) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.asyncException = asyncException;
        }

        @Override
        protected Observable<Boolean> construct() {
            Error error = new Error("simulated java.lang.Error message");
            if (asyncException) {
                return Observable.error(error);
            } else {
                throw error;
            }
        }
    }

    private static class CommandWithCheckedException extends TestHystrixObservableCommand<Boolean> {
        public CommandWithCheckedException(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
        }

        @Override
        protected Observable<Boolean> construct() {
            return Observable.error(new IOException("simulated checked exception message"));
        }
    }
}

