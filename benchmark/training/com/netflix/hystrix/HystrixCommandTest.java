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


import ExecutionIsolationStrategy.SEMAPHORE;
import ExecutionIsolationStrategy.THREAD;
import HystrixCommand.Setter;
import HystrixCommandGroupKey.Factory;
import HystrixEventType.BAD_REQUEST;
import HystrixEventType.CANCELLED;
import HystrixEventType.FAILURE;
import HystrixEventType.FALLBACK_FAILURE;
import HystrixEventType.FALLBACK_MISSING;
import HystrixEventType.FALLBACK_REJECTION;
import HystrixEventType.FALLBACK_SUCCESS;
import HystrixEventType.RESPONSE_FROM_CACHE;
import HystrixEventType.SEMAPHORE_REJECTED;
import HystrixEventType.SHORT_CIRCUITED;
import HystrixEventType.SUCCESS;
import HystrixEventType.THREAD_POOL_REJECTED;
import HystrixEventType.TIMEOUT;
import HystrixRuntimeException.FailureType;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.hystrix.AbstractCommand.TryableSemaphore;
import com.netflix.hystrix.AbstractCommand.TryableSemaphoreActual;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static com.netflix.hystrix.AbstractTestHystrixCommand.CacheEnabled.YES;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.BAD_REQUEST;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.BAD_REQUEST_NOT_WRAPPED;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.HYSTRIX_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.NOT_WRAPPED_FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.RECOVERABLE_ERROR;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.SUCCESS;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.UNRECOVERABLE_ERROR;
import static com.netflix.hystrix.AbstractTestHystrixCommand.FallbackResult.UNIMPLEMENTED;
import static com.netflix.hystrix.InspectableBuilder.CommandGroupForUnitTest.OWNER_ONE;
import static com.netflix.hystrix.InspectableBuilder.CommandKeyForUnitTest.KEY_ONE;
import static com.netflix.hystrix.InspectableBuilder.CommandKeyForUnitTest.KEY_TWO;


public class HystrixCommandTest extends CommonHystrixCommandTests<TestHystrixCommand<Integer>> {
    @Rule
    public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

    /**
     * Test a successful command execution.
     */
    @Test
    public void testExecutionSuccess() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.EXECUTE_VALUE, command.execute());
        Assert.assertEquals(null, command.getFailedExecutionException());
        Assert.assertNull(command.getExecutionException());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isSuccessfulExecution());
        assertCommandExecutionEvents(command, SUCCESS);
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test that a command can not be executed multiple times.
     */
    @Test
    public void testExecutionMultipleTimes() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS);
        Assert.assertFalse(command.isExecutionComplete());
        // first should succeed
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.EXECUTE_VALUE, command.execute());
        Assert.assertTrue(command.isExecutionComplete());
        Assert.assertTrue(command.isExecutedInThread());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isSuccessfulExecution());
        Assert.assertNull(command.getExecutionException());
        try {
            // second should fail
            command.execute();
            Assert.fail("we should not allow this ... it breaks the state of request logs");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            // we want to get here
        }
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        assertCommandExecutionEvents(command, SUCCESS);
    }

    /**
     * Test a command execution that throws an HystrixException and didn't implement getFallback.
     */
    @Test
    public void testExecutionHystrixFailureWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, HYSTRIX_FAILURE, UNIMPLEMENTED);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.assertNotNull(e.getFallbackException());
            Assert.assertNotNull(e.getImplementingClass());
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution that throws an unknown exception (not HystrixException) and didn't implement getFallback.
     */
    @Test
    public void testExecutionFailureWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, FAILURE, UNIMPLEMENTED);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.assertNotNull(e.getFallbackException());
            Assert.assertNotNull(e.getImplementingClass());
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution that throws an exception that should not be wrapped.
     */
    @Test
    public void testNotWrappedExceptionWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, NOT_WRAPPED_FAILURE, UNIMPLEMENTED);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.fail("we shouldn't get a HystrixRuntimeException");
        } catch (RuntimeException e) {
            Assert.assertTrue((e instanceof NotWrappedByHystrixTestRuntimeException));
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertTrue(((command.getExecutionException()) instanceof NotWrappedByHystrixTestRuntimeException));
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution that throws an exception that should not be wrapped.
     */
    @Test
    public void testNotWrappedBadRequestWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, BAD_REQUEST_NOT_WRAPPED, UNIMPLEMENTED);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.fail("we shouldn't get a HystrixRuntimeException");
        } catch (RuntimeException e) {
            Assert.assertTrue((e instanceof NotWrappedByHystrixTestRuntimeException));
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.getEventCounts().contains(BAD_REQUEST));
        assertCommandExecutionEvents(command, BAD_REQUEST);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertTrue(((command.getExecutionException()) instanceof HystrixBadRequestException));
        Assert.assertTrue(((command.getExecutionException().getCause()) instanceof NotWrappedByHystrixTestRuntimeException));
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testNotWrappedBadRequestWithFallback() throws Exception {
        TestHystrixCommand<Integer> command = getCommand(THREAD, BAD_REQUEST_NOT_WRAPPED, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (HystrixRuntimeException e) {
            e.printStackTrace();
            Assert.fail("we shouldn't get a HystrixRuntimeException");
        } catch (RuntimeException e) {
            Assert.assertTrue((e instanceof NotWrappedByHystrixTestRuntimeException));
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.getEventCounts().contains(BAD_REQUEST));
        assertCommandExecutionEvents(command, BAD_REQUEST);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertTrue(((command.getExecutionException()) instanceof HystrixBadRequestException));
        Assert.assertTrue(((command.getExecutionException().getCause()) instanceof NotWrappedByHystrixTestRuntimeException));
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution that fails but has a fallback.
     */
    @Test
    public void testExecutionFailureWithFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, FAILURE, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, command.execute());
        Assert.assertEquals("Execution Failure for TestHystrixCommand", command.getFailedExecutionException().getMessage());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution that throws exception that should not be wrapped but has a fallback.
     */
    @Test
    public void testNotWrappedExceptionWithFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, NOT_WRAPPED_FAILURE, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, command.execute());
        Assert.assertEquals("Raw exception for TestHystrixCommand", command.getFailedExecutionException().getMessage());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution that fails, has getFallback implemented but that fails as well.
     */
    @Test
    public void testExecutionFailureWithFallbackFailure() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, FAILURE, AbstractTestHystrixCommand.FallbackResult.FAILURE);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (HystrixRuntimeException e) {
            System.out.println("------------------------------------------------");
            e.printStackTrace();
            System.out.println("------------------------------------------------");
            Assert.assertNotNull(e.getFallbackException());
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_FAILURE);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a successful command execution (asynchronously).
     */
    @Test
    public void testQueueSuccess() throws Exception {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS);
        Future<Integer> future = command.queue();
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.EXECUTE_VALUE, future.get());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isSuccessfulExecution());
        assertCommandExecutionEvents(command, SUCCESS);
        Assert.assertNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution (asynchronously) that throws an HystrixException and didn't implement getFallback.
     */
    @Test
    public void testQueueKnownFailureWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, HYSTRIX_FAILURE, UNIMPLEMENTED);
        try {
            command.queue().get();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            if ((e.getCause()) instanceof HystrixRuntimeException) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e.getCause()));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertNotNull(de.getImplementingClass());
            } else {
                Assert.fail("the cause should be HystrixRuntimeException");
            }
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution (asynchronously) that throws an unknown exception (not HystrixException) and didn't implement getFallback.
     */
    @Test
    public void testQueueUnknownFailureWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, FAILURE, UNIMPLEMENTED);
        try {
            command.queue().get();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            if ((e.getCause()) instanceof HystrixRuntimeException) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e.getCause()));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertNotNull(de.getImplementingClass());
            } else {
                Assert.fail("the cause should be HystrixRuntimeException");
            }
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution (asynchronously) that fails but has a fallback.
     */
    @Test
    public void testQueueFailureWithFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, FAILURE, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
        try {
            Future<Integer> future = command.queue();
            Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, future.get());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We should have received a response from the fallback.");
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution (asynchronously) that fails, has getFallback implemented but that fails as well.
     */
    @Test
    public void testQueueFailureWithFallbackFailure() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, FAILURE, AbstractTestHystrixCommand.FallbackResult.FAILURE);
        try {
            command.queue().get();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            if ((e.getCause()) instanceof HystrixRuntimeException) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e.getCause()));
                e.printStackTrace();
                Assert.assertNotNull(de.getFallbackException());
            } else {
                Assert.fail("the cause should be HystrixRuntimeException");
            }
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_FAILURE);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a successful command execution.
     */
    @Test
    public void testObserveSuccess() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.EXECUTE_VALUE, command.observe().toBlocking().single());
        Assert.assertEquals(null, command.getFailedExecutionException());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isSuccessfulExecution());
        assertCommandExecutionEvents(command, SUCCESS);
        Assert.assertNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a successful command execution.
     */
    @Test
    public void testCallbackThreadForThreadIsolation() throws Exception {
        final AtomicReference<Thread> commandThread = new AtomicReference<Thread>();
        final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
        TestHystrixCommand<Boolean> command = new TestHystrixCommand<Boolean>(TestHystrixCommand.testPropsBuilder()) {
            @Override
            protected Boolean run() {
                commandThread.set(Thread.currentThread());
                return true;
            }
        };
        final CountDownLatch latch = new CountDownLatch(1);
        command.toObservable().subscribe(new rx.Observer<Boolean>() {
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
        Assert.assertTrue(commandThread.get().getName().startsWith("hystrix-"));
        Assert.assertTrue(subscribeThread.get().getName().startsWith("hystrix-"));
    }

    /**
     * Test a successful command execution.
     */
    @Test
    public void testCallbackThreadForSemaphoreIsolation() throws Exception {
        final AtomicReference<Thread> commandThread = new AtomicReference<Thread>();
        final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
        TestHystrixCommand<Boolean> command = new TestHystrixCommand<Boolean>(TestHystrixCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE))) {
            @Override
            protected Boolean run() {
                commandThread.set(Thread.currentThread());
                return true;
            }
        };
        final CountDownLatch latch = new CountDownLatch(1);
        command.toObservable().subscribe(new rx.Observer<Boolean>() {
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
        Assert.assertTrue(subscribeThread.get().getName().equals(mainThreadName));
    }

    /**
     * Tests that the circuit-breaker reports itself as "OPEN" if set as forced-open
     */
    @Test
    public void testCircuitBreakerReportsOpenIfForcedOpen() {
        HystrixCommand<Boolean> cmd = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("GROUP")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withCircuitBreakerForceOpen(true))) {
            @Override
            protected Boolean run() throws Exception {
                return true;
            }

            @Override
            protected Boolean getFallback() {
                return false;
            }
        };
        Assert.assertFalse(cmd.execute());// fallback should fire

        System.out.println(("RESULT : " + (cmd.getExecutionEvents())));
        Assert.assertTrue(cmd.isCircuitBreakerOpen());
    }

    /**
     * Tests that the circuit-breaker reports itself as "CLOSED" if set as forced-closed
     */
    @Test
    public void testCircuitBreakerReportsClosedIfForcedClosed() {
        HystrixCommand<Boolean> cmd = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("GROUP")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withCircuitBreakerForceOpen(false).withCircuitBreakerForceClosed(true))) {
            @Override
            protected Boolean run() throws Exception {
                return true;
            }

            @Override
            protected Boolean getFallback() {
                return false;
            }
        };
        Assert.assertTrue(cmd.execute());
        System.out.println(("RESULT : " + (cmd.getExecutionEvents())));
        Assert.assertFalse(cmd.isCircuitBreakerOpen());
    }

    /**
     * Test that the circuit-breaker is shared across HystrixCommand objects with the same CommandKey.
     * <p>
     * This will test HystrixCommand objects with a single circuit-breaker (as if each injected with same CommandKey)
     * <p>
     * Multiple HystrixCommand objects with the same dependency use the same circuit-breaker.
     */
    @Test
    public void testCircuitBreakerAcrossMultipleCommandsButSameCircuitBreaker() throws InterruptedException {
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("SharedCircuitBreaker");
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker(key);
        /* fail 3 times and then it should trip the circuit and stop executing */
        // failure 1
        TestHystrixCommand<Integer> attempt1 = getSharedCircuitBreakerCommand(key, THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker);
        System.out.println(("COMMAND KEY (from cmd): " + (attempt1.commandKey.name())));
        attempt1.execute();
        Thread.sleep(100);
        Assert.assertTrue(attempt1.isResponseFromFallback());
        Assert.assertFalse(attempt1.isCircuitBreakerOpen());
        Assert.assertFalse(attempt1.isResponseShortCircuited());
        // failure 2 with a different command, same circuit breaker
        TestHystrixCommand<Integer> attempt2 = getSharedCircuitBreakerCommand(key, THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker);
        attempt2.execute();
        Thread.sleep(100);
        Assert.assertTrue(attempt2.isFailedExecution());
        Assert.assertTrue(attempt2.isResponseFromFallback());
        Assert.assertFalse(attempt2.isCircuitBreakerOpen());
        Assert.assertFalse(attempt2.isResponseShortCircuited());
        // failure 3 of the Hystrix, 2nd for this particular HystrixCommand
        TestHystrixCommand<Integer> attempt3 = getSharedCircuitBreakerCommand(key, THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker);
        attempt3.execute();
        Thread.sleep(100);
        Assert.assertTrue(attempt3.isFailedExecution());
        Assert.assertTrue(attempt3.isResponseFromFallback());
        Assert.assertFalse(attempt3.isResponseShortCircuited());
        // it should now be 'open' and prevent further executions
        // after having 3 failures on the Hystrix that these 2 different HystrixCommand objects are for
        Assert.assertTrue(attempt3.isCircuitBreakerOpen());
        // attempt 4
        TestHystrixCommand<Integer> attempt4 = getSharedCircuitBreakerCommand(key, THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker);
        attempt4.execute();
        Thread.sleep(100);
        Assert.assertTrue(attempt4.isResponseFromFallback());
        // this should now be true as the response will be short-circuited
        Assert.assertTrue(attempt4.isResponseShortCircuited());
        // this should remain open
        Assert.assertTrue(attempt4.isCircuitBreakerOpen());
        assertSaneHystrixRequestLog(4);
        assertCommandExecutionEvents(attempt1, FAILURE, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(attempt2, FAILURE, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(attempt3, FAILURE, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(attempt4, SHORT_CIRCUITED, FALLBACK_SUCCESS);
    }

    /**
     * Test that the circuit-breaker being disabled doesn't wreak havoc.
     */
    @Test
    public void testExecutionSuccessWithCircuitBreakerDisabled() {
        TestHystrixCommand<Integer> command = getCircuitBreakerDisabledCommand(THREAD, SUCCESS);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.EXECUTE_VALUE, command.execute());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
        // we'll still get metrics ... just not the circuit breaker opening/closing
        assertCommandExecutionEvents(command, SUCCESS);
    }

    /**
     * Test a command execution timeout where the command didn't implement getFallback.
     */
    @Test
    public void testExecutionTimeoutWithNoFallback() {
        TestHystrixCommand<Integer> command = getLatentCommand(THREAD, SUCCESS, 200, UNIMPLEMENTED, 50);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            // e.printStackTrace();
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
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution timeout where the command implemented getFallback.
     */
    @Test
    public void testExecutionTimeoutWithFallback() {
        TestHystrixCommand<Integer> command = getLatentCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 50);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, command.execute());
        // the time should be 50+ since we timeout at 50ms
        Assert.assertTrue(("Execution Time is: " + (command.getExecutionTimeInMilliseconds())), ((command.getExecutionTimeInMilliseconds()) >= 50));
        Assert.assertFalse(command.isCircuitBreakerOpen());
        Assert.assertFalse(command.isResponseShortCircuited());
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertTrue(command.isResponseFromFallback());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a command execution timeout where the command implemented getFallback but it fails.
     */
    @Test
    public void testExecutionTimeoutFallbackFailure() {
        TestHystrixCommand<Integer> command = getLatentCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.FAILURE, 50);
        try {
            command.execute();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            if (e instanceof HystrixRuntimeException) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertFalse(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
            } else {
                Assert.fail("the exception should be HystrixRuntimeException");
            }
        }
        Assert.assertNotNull(command.getExecutionException());
        // the time should be 50+ since we timeout at 50ms
        Assert.assertTrue(("Execution Time is: " + (command.getExecutionTimeInMilliseconds())), ((command.getExecutionTimeInMilliseconds()) >= 50));
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_FAILURE);
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test that the command finishing AFTER a timeout (because thread continues in background) does not register a SUCCESS
     */
    @Test
    public void testCountersOnExecutionTimeout() throws Exception {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 50);
        command.execute();
        /* wait long enough for the command to have finished */
        Thread.sleep(200);
        /* response should still be the same as 'testCircuitBreakerOnExecutionTimeout' */
        Assert.assertTrue(command.isResponseFromFallback());
        Assert.assertFalse(command.isCircuitBreakerOpen());
        Assert.assertFalse(command.isResponseShortCircuited());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertFalse(command.isSuccessfulExecution());
        Assert.assertNotNull(command.getExecutionException());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_SUCCESS);
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a queued command execution timeout where the command didn't implement getFallback.
     * <p>
     * We specifically want to protect against developers queuing commands and using queue().get() without a timeout (such as queue().get(3000, TimeUnit.Milliseconds)) and ending up blocking
     * indefinitely by skipping the timeout protection of the execute() command.
     */
    @Test
    public void testQueuedExecutionTimeoutWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, UNIMPLEMENTED, 50);
        try {
            command.queue().get();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            if ((e instanceof ExecutionException) && ((e.getCause()) instanceof HystrixRuntimeException)) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e.getCause()));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertTrue(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
            } else {
                Assert.fail("the exception should be ExecutionException with cause as HystrixRuntimeException");
            }
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isResponseTimedOut());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a queued command execution timeout where the command implemented getFallback.
     * <p>
     * We specifically want to protect against developers queuing commands and using queue().get() without a timeout (such as queue().get(3000, TimeUnit.Milliseconds)) and ending up blocking
     * indefinitely by skipping the timeout protection of the execute() command.
     */
    @Test
    public void testQueuedExecutionTimeoutWithFallback() throws Exception {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 50);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, command.queue().get());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a queued command execution timeout where the command implemented getFallback but it fails.
     * <p>
     * We specifically want to protect against developers queuing commands and using queue().get() without a timeout (such as queue().get(3000, TimeUnit.Milliseconds)) and ending up blocking
     * indefinitely by skipping the timeout protection of the execute() command.
     */
    @Test
    public void testQueuedExecutionTimeoutFallbackFailure() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.FAILURE, 50);
        try {
            command.queue().get();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            if ((e instanceof ExecutionException) && ((e.getCause()) instanceof HystrixRuntimeException)) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e.getCause()));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertFalse(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
            } else {
                Assert.fail("the exception should be ExecutionException with cause as HystrixRuntimeException");
            }
        }
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_FAILURE);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a queued command execution timeout where the command didn't implement getFallback.
     * <p>
     * We specifically want to protect against developers queuing commands and using queue().get() without a timeout (such as queue().get(3000, TimeUnit.Milliseconds)) and ending up blocking
     * indefinitely by skipping the timeout protection of the execute() command.
     */
    @Test
    public void testObservedExecutionTimeoutWithNoFallback() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, UNIMPLEMENTED, 50);
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
                Assert.fail("the exception should be ExecutionException with cause as HystrixRuntimeException");
            }
        }
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isResponseTimedOut());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a queued command execution timeout where the command implemented getFallback.
     * <p>
     * We specifically want to protect against developers queuing commands and using queue().get() without a timeout (such as queue().get(3000, TimeUnit.Milliseconds)) and ending up blocking
     * indefinitely by skipping the timeout protection of the execute() command.
     */
    @Test
    public void testObservedExecutionTimeoutWithFallback() throws Exception {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 50);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, command.observe().toBlocking().single());
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a queued command execution timeout where the command implemented getFallback but it fails.
     * <p>
     * We specifically want to protect against developers queuing commands and using queue().get() without a timeout (such as queue().get(3000, TimeUnit.Milliseconds)) and ending up blocking
     * indefinitely by skipping the timeout protection of the execute() command.
     */
    @Test
    public void testObservedExecutionTimeoutFallbackFailure() {
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.FAILURE, 50);
        try {
            command.observe().toBlocking().single();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            if (e instanceof HystrixRuntimeException) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertFalse(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof TimeoutException));
            } else {
                Assert.fail("the exception should be ExecutionException with cause as HystrixRuntimeException");
            }
        }
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_FAILURE);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testShortCircuitFallbackCounter() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker().setForceShortCircuit(true);
        HystrixCommandTest.KnownFailureTestCommandWithFallback command1 = new HystrixCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker);
        execute();
        HystrixCommandTest.KnownFailureTestCommandWithFallback command2 = new HystrixCommandTest.KnownFailureTestCommandWithFallback(circuitBreaker);
        execute();
        // will be -1 because it never attempted execution
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        Assert.assertTrue(isResponseShortCircuited());
        Assert.assertFalse(isResponseTimedOut());
        Assert.assertNotNull(getExecutionException());
        assertCommandExecutionEvents(command1, SHORT_CIRCUITED, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(command2, SHORT_CIRCUITED, FALLBACK_SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test when a command fails to get queued up in the threadpool where the command didn't implement getFallback.
     * <p>
     * We specifically want to protect against developers getting random thread exceptions and instead just correctly receiving HystrixRuntimeException when no fallback exists.
     */
    @Test
    public void testRejectedThreadWithNoFallback() throws Exception {
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("Rejection-NoFallback");
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        CommonHystrixCommandTests.SingleThreadedPoolWithQueue pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(1);
        // fill up the queue
        pool.queue.add(new Runnable() {
            @Override
            public void run() {
                System.out.println("**** queue filler1 ****");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Future<Boolean> f = null;
        HystrixCommandTest.TestCommandRejection command1 = null;
        HystrixCommandTest.TestCommandRejection command2 = null;
        try {
            command1 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_NOT_IMPLEMENTED);
            f = queue();
            command2 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_NOT_IMPLEMENTED);
            command2.queue();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(("command.getExecutionTimeInMilliseconds(): " + (getExecutionTimeInMilliseconds())));
            // will be -1 because it never attempted execution
            Assert.assertTrue(isResponseRejected());
            Assert.assertFalse(isResponseShortCircuited());
            Assert.assertFalse(isResponseTimedOut());
            Assert.assertNotNull(getExecutionException());
            if ((e instanceof HystrixRuntimeException) && ((e.getCause()) instanceof RejectedExecutionException)) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertTrue(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof RejectedExecutionException));
            } else {
                Assert.fail("the exception should be HystrixRuntimeException with cause as RejectedExecutionException");
            }
        }
        f.get();
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, THREAD_POOL_REJECTED, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test when a command fails to get queued up in the threadpool where the command implemented getFallback.
     * <p>
     * We specifically want to protect against developers getting random thread exceptions and instead just correctly receives a fallback.
     */
    @Test
    public void testRejectedThreadWithFallback() throws InterruptedException {
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("Rejection-Fallback");
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        CommonHystrixCommandTests.SingleThreadedPoolWithQueue pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(1);
        // command 1 will execute in threadpool (passing through the queue)
        // command 2 will execute after spending time in the queue (after command1 completes)
        // command 3 will get rejected, since it finds pool and queue both full
        HystrixCommandTest.TestCommandRejection command1 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_SUCCESS);
        HystrixCommandTest.TestCommandRejection command2 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_SUCCESS);
        HystrixCommandTest.TestCommandRejection command3 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_SUCCESS);
        Observable<Boolean> result1 = observe();
        Observable<Boolean> result2 = observe();
        Thread.sleep(100);
        // command3 should find queue filled, and get rejected
        Assert.assertFalse(execute());
        Assert.assertTrue(isResponseRejected());
        Assert.assertFalse(isResponseRejected());
        Assert.assertFalse(isResponseRejected());
        Assert.assertTrue(isResponseFromFallback());
        Assert.assertNotNull(getExecutionException());
        assertCommandExecutionEvents(command3, THREAD_POOL_REJECTED, FALLBACK_SUCCESS);
        Observable.merge(result1, result2).toList().toBlocking().single();// await the 2 latent commands

        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test when a command fails to get queued up in the threadpool where the command implemented getFallback but it fails.
     * <p>
     * We specifically want to protect against developers getting random thread exceptions and instead just correctly receives an HystrixRuntimeException.
     */
    @Test
    public void testRejectedThreadWithFallbackFailure() throws InterruptedException, ExecutionException {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        CommonHystrixCommandTests.SingleThreadedPoolWithQueue pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(1);
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("Rejection-A");
        HystrixCommandTest.TestCommandRejection command1 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_FAILURE);// this should pass through the queue and sit in the pool

        HystrixCommandTest.TestCommandRejection command2 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_SUCCESS);// this should sit in the queue

        HystrixCommandTest.TestCommandRejection command3 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_FAILURE);// this should observe full queue and get rejected

        Future<Boolean> f1 = null;
        Future<Boolean> f2 = null;
        try {
            f1 = queue();
            f2 = queue();
            Assert.assertEquals(false, command3.queue().get());// should get thread-pool rejected

            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            if ((e instanceof HystrixRuntimeException) && ((e.getCause()) instanceof RejectedExecutionException)) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertFalse(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof RejectedExecutionException));
            } else {
                Assert.fail("the exception should be HystrixRuntimeException with cause as RejectedExecutionException");
            }
        }
        assertCommandExecutionEvents(command1);// still in-flight, no events yet

        assertCommandExecutionEvents(command2);// still in-flight, no events yet

        assertCommandExecutionEvents(command3, THREAD_POOL_REJECTED, FALLBACK_FAILURE);
        int numInFlight = circuitBreaker.metrics.getCurrentConcurrentExecutionCount();
        Assert.assertTrue(("Expected at most 1 in flight but got : " + numInFlight), (numInFlight <= 1));// pool-filler still going

        // This is a case where we knowingly walk away from executing Hystrix threads. They should have an in-flight status ("Executed").  You should avoid this in a production environment
        HystrixRequestLog requestLog = HystrixRequestLog.getCurrentRequest();
        Assert.assertEquals(3, requestLog.getAllExecutedCommands().size());
        Assert.assertTrue(requestLog.getExecutedCommandsAsString().contains("Executed"));
        // block on the outstanding work, so we don't inadvertently affect any other tests
        long startTime = System.currentTimeMillis();
        f1.get();
        f2.get();
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        System.out.println(("Time blocked : " + ((System.currentTimeMillis()) - startTime)));
    }

    /**
     * Test that we can reject a thread using isQueueSpaceAvailable() instead of just when the pool rejects.
     * <p>
     * For example, we have queue size set to 100 but want to reject when we hit 10.
     * <p>
     * This allows us to use FastProperties to control our rejection point whereas we can't resize a queue after it's created.
     */
    @Test
    public void testRejectedThreadUsingQueueSize() {
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("Rejection-B");
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        CommonHystrixCommandTests.SingleThreadedPoolWithQueue pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(10, 1);
        // put 1 item in the queue
        // the thread pool won't pick it up because we're bypassing the pool and adding to the queue directly so this will keep the queue full
        pool.queue.add(new Runnable() {
            @Override
            public void run() {
                System.out.println("**** queue filler1 ****");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HystrixCommandTest.TestCommandRejection command = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_NOT_IMPLEMENTED);
        try {
            // this should fail as we already have 1 in the queue
            command.queue();
            Assert.fail("we shouldn't get here");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            Assert.assertFalse(isResponseShortCircuited());
            Assert.assertFalse(isResponseTimedOut());
            Assert.assertNotNull(getExecutionException());
            if ((e instanceof HystrixRuntimeException) && ((e.getCause()) instanceof RejectedExecutionException)) {
                HystrixRuntimeException de = ((HystrixRuntimeException) (e));
                Assert.assertNotNull(de.getFallbackException());
                Assert.assertTrue(((de.getFallbackException()) instanceof UnsupportedOperationException));
                Assert.assertNotNull(de.getImplementingClass());
                Assert.assertNotNull(de.getCause());
                Assert.assertTrue(((de.getCause()) instanceof RejectedExecutionException));
            } else {
                Assert.fail("the exception should be HystrixRuntimeException with cause as RejectedExecutionException");
            }
        }
        assertCommandExecutionEvents(command, THREAD_POOL_REJECTED, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testDisabledTimeoutWorks() {
        HystrixCommandTest.CommandWithDisabledTimeout cmd = new HystrixCommandTest.CommandWithDisabledTimeout(100, 900);
        boolean result = cmd.execute();
        Assert.assertEquals(true, result);
        Assert.assertFalse(isResponseTimedOut());
        Assert.assertNull(getExecutionException());
        System.out.println(("CMD : " + (cmd.currentRequestLog.getExecutedCommandsAsString())));
        Assert.assertTrue(((cmd.executionResult.getExecutionLatency()) >= 900));
        assertCommandExecutionEvents(cmd, SUCCESS);
    }

    @Test
    public void testFallbackSemaphore() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        // single thread should work
        HystrixCommandTest.TestSemaphoreCommandWithSlowFallback command1 = new HystrixCommandTest.TestSemaphoreCommandWithSlowFallback(circuitBreaker, 1, 200);
        boolean result = command1.queue().get();
        Assert.assertTrue(result);
        // 2 threads, the second should be rejected by the fallback semaphore
        boolean exceptionReceived = false;
        Future<Boolean> result2 = null;
        HystrixCommandTest.TestSemaphoreCommandWithSlowFallback command2 = null;
        HystrixCommandTest.TestSemaphoreCommandWithSlowFallback command3 = null;
        try {
            System.out.println(("c2 start: " + (System.currentTimeMillis())));
            command2 = new HystrixCommandTest.TestSemaphoreCommandWithSlowFallback(circuitBreaker, 1, 800);
            result2 = queue();
            System.out.println(("c2 after queue: " + (System.currentTimeMillis())));
            // make sure that thread gets a chance to run before queuing the next one
            Thread.sleep(50);
            System.out.println(("c3 start: " + (System.currentTimeMillis())));
            command3 = new HystrixCommandTest.TestSemaphoreCommandWithSlowFallback(circuitBreaker, 1, 200);
            Future<Boolean> result3 = queue();
            System.out.println(("c3 after queue: " + (System.currentTimeMillis())));
            result3.get();
        } catch (Exception e) {
            e.printStackTrace();
            exceptionReceived = true;
        }
        Assert.assertTrue(result2.get());
        if (!exceptionReceived) {
            Assert.fail("We expected an exception on the 2nd get");
        }
        assertCommandExecutionEvents(command1, FAILURE, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(command2, FAILURE, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(command3, FAILURE, FALLBACK_REJECTION);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    @Test
    public void testExecutionSemaphoreWithQueue() throws Exception {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        // single thread should work
        HystrixCommandTest.TestSemaphoreCommand command1 = new HystrixCommandTest.TestSemaphoreCommand(circuitBreaker, 1, 200, HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        boolean result = command1.queue().get();
        Assert.assertTrue(result);
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final TryableSemaphore semaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(1));
        final HystrixCommandTest.TestSemaphoreCommand command2 = new HystrixCommandTest.TestSemaphoreCommand(circuitBreaker, semaphore, 200, HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    command2.queue().get();
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        final HystrixCommandTest.TestSemaphoreCommand command3 = new HystrixCommandTest.TestSemaphoreCommand(circuitBreaker, semaphore, 200, HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        Runnable r3 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    command3.queue().get();
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
        // make sure that t2 gets a chance to run before queuing the next one
        Thread.sleep(50);
        t3.start();
        t2.join();
        t3.join();
        if (!(exceptionReceived.get())) {
            Assert.fail("We expected an exception on the 2nd get");
        }
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SEMAPHORE_REJECTED, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    @Test
    public void testExecutionSemaphoreWithExecution() throws Exception {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        // single thread should work
        HystrixCommandTest.TestSemaphoreCommand command1 = new HystrixCommandTest.TestSemaphoreCommand(circuitBreaker, 1, 200, HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        boolean result = command1.execute();
        Assert.assertFalse(isExecutedInThread());
        Assert.assertTrue(result);
        final ArrayBlockingQueue<Boolean> results = new ArrayBlockingQueue<Boolean>(2);
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final TryableSemaphore semaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(1));
        final HystrixCommandTest.TestSemaphoreCommand command2 = new HystrixCommandTest.TestSemaphoreCommand(circuitBreaker, semaphore, 200, HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(execute());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        final HystrixCommandTest.TestSemaphoreCommand command3 = new HystrixCommandTest.TestSemaphoreCommand(circuitBreaker, semaphore, 200, HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS, HystrixCommandTest.TestSemaphoreCommand.FALLBACK_NOT_IMPLEMENTED);
        Runnable r3 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(execute());
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
        // make sure that t2 gets a chance to run before queuing the next one
        Thread.sleep(50);
        t3.start();
        t2.join();
        t3.join();
        if (!(exceptionReceived.get())) {
            Assert.fail("We expected an exception on the 2nd get");
        }
        // only 1 value is expected as the other should have thrown an exception
        Assert.assertEquals(1, results.size());
        // should contain only a true result
        Assert.assertTrue(results.contains(Boolean.TRUE));
        Assert.assertFalse(results.contains(Boolean.FALSE));
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SEMAPHORE_REJECTED, FALLBACK_MISSING);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    @Test
    public void testRejectedExecutionSemaphoreWithFallbackViaExecute() throws Exception {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        final ArrayBlockingQueue<Boolean> results = new ArrayBlockingQueue<Boolean>(2);
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final HystrixCommandTest.TestSemaphoreCommandWithFallback command1 = new HystrixCommandTest.TestSemaphoreCommandWithFallback(circuitBreaker, 1, 200, false);
        Runnable r1 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(execute());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        final HystrixCommandTest.TestSemaphoreCommandWithFallback command2 = new HystrixCommandTest.TestSemaphoreCommandWithFallback(circuitBreaker, 1, 200, false);
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(execute());
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
        // make sure that t2 gets a chance to run before queuing the next one
        Thread.sleep(50);
        t2.start();
        t1.join();
        t2.join();
        if (exceptionReceived.get()) {
            Assert.fail("We should have received a fallback response");
        }
        // both threads should have returned values
        Assert.assertEquals(2, results.size());
        // should contain both a true and false result
        Assert.assertTrue(results.contains(Boolean.TRUE));
        Assert.assertTrue(results.contains(Boolean.FALSE));
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SEMAPHORE_REJECTED, FALLBACK_SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    @Test
    public void testRejectedExecutionSemaphoreWithFallbackViaObserve() throws Exception {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        final ArrayBlockingQueue<Observable<Boolean>> results = new ArrayBlockingQueue<Observable<Boolean>>(2);
        final AtomicBoolean exceptionReceived = new AtomicBoolean();
        final HystrixCommandTest.TestSemaphoreCommandWithFallback command1 = new HystrixCommandTest.TestSemaphoreCommandWithFallback(circuitBreaker, 1, 200, false);
        Runnable r1 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(command1.observe());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionReceived.set(true);
                }
            }
        });
        final HystrixCommandTest.TestSemaphoreCommandWithFallback command2 = new HystrixCommandTest.TestSemaphoreCommandWithFallback(circuitBreaker, 1, 200, false);
        Runnable r2 = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            @Override
            public void run() {
                try {
                    results.add(command2.observe());
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
        // make sure that t2 gets a chance to run before queuing the next one
        Thread.sleep(50);
        t2.start();
        t1.join();
        t2.join();
        if (exceptionReceived.get()) {
            Assert.fail("We should have received a fallback response");
        }
        final List<Boolean> blockingList = Observable.merge(results).toList().toBlocking().single();
        // both threads should have returned values
        Assert.assertEquals(2, blockingList.size());
        // should contain both a true and false result
        Assert.assertTrue(blockingList.contains(Boolean.TRUE));
        Assert.assertTrue(blockingList.contains(Boolean.FALSE));
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SEMAPHORE_REJECTED, FALLBACK_SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Tests that semaphores are counted separately for commands with unique keys
     */
    @Test
    public void testSemaphorePermitsInUse() throws Exception {
        final HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        // this semaphore will be shared across multiple command instances
        final TryableSemaphoreActual sharedSemaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(3));
        // used to wait until all commands have started
        final CountDownLatch startLatch = new CountDownLatch((((sharedSemaphore.numberOfPermits.get()) * 2) + 1));
        // used to signal that all command can finish
        final CountDownLatch sharedLatch = new CountDownLatch(1);
        // tracks failures to obtain semaphores
        final AtomicInteger failureCount = new AtomicInteger();
        final Runnable sharedSemaphoreRunnable = new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            public void run() {
                try {
                    execute();
                } catch (Exception e) {
                    startLatch.countDown();
                    e.printStackTrace();
                    failureCount.incrementAndGet();
                }
            }
        });
        // creates group of threads each using command sharing a single semaphore
        // I create extra threads and commands so that I can verify that some of them fail to obtain a semaphore
        final int sharedThreadCount = (sharedSemaphore.numberOfPermits.get()) * 2;
        final Thread[] sharedSemaphoreThreads = new Thread[sharedThreadCount];
        for (int i = 0; i < sharedThreadCount; i++) {
            sharedSemaphoreThreads[i] = new Thread(sharedSemaphoreRunnable);
        }
        // creates thread using isolated semaphore
        final TryableSemaphoreActual isolatedSemaphore = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(1));
        final CountDownLatch isolatedLatch = new CountDownLatch(1);
        final Thread isolatedThread = new Thread(new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
            public void run() {
                try {
                    execute();
                } catch (Exception e) {
                    startLatch.countDown();
                    e.printStackTrace();
                    failureCount.incrementAndGet();
                }
            }
        }));
        // verifies no permits in use before starting threads
        Assert.assertEquals("before threads start, shared semaphore should be unused", 0, sharedSemaphore.getNumberOfPermitsUsed());
        Assert.assertEquals("before threads start, isolated semaphore should be unused", 0, isolatedSemaphore.getNumberOfPermitsUsed());
        for (int i = 0; i < sharedThreadCount; i++) {
            sharedSemaphoreThreads[i].start();
        }
        isolatedThread.start();
        // waits until all commands have started
        startLatch.await(1000, TimeUnit.MILLISECONDS);
        // verifies that all semaphores are in use
        Assert.assertEquals("immediately after command start, all shared semaphores should be in-use", sharedSemaphore.numberOfPermits.get().longValue(), sharedSemaphore.getNumberOfPermitsUsed());
        Assert.assertEquals("immediately after command start, isolated semaphore should be in-use", isolatedSemaphore.numberOfPermits.get().longValue(), isolatedSemaphore.getNumberOfPermitsUsed());
        // signals commands to finish
        sharedLatch.countDown();
        isolatedLatch.countDown();
        for (int i = 0; i < sharedThreadCount; i++) {
            sharedSemaphoreThreads[i].join();
        }
        isolatedThread.join();
        // verifies no permits in use after finishing threads
        System.out.println(("REQLOG : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals("after all threads have finished, no shared semaphores should be in-use", 0, sharedSemaphore.getNumberOfPermitsUsed());
        Assert.assertEquals("after all threads have finished, isolated semaphore not in-use", 0, isolatedSemaphore.getNumberOfPermitsUsed());
        // verifies that some executions failed
        Assert.assertEquals("expected some of shared semaphore commands to get rejected", sharedSemaphore.numberOfPermits.get().longValue(), failureCount.get());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
    }

    /**
     * Test that HystrixOwner can be passed in dynamically.
     */
    @Test
    public void testDynamicOwner() {
        TestHystrixCommand<Boolean> command = new HystrixCommandTest.DynamicOwnerTestCommand(OWNER_ONE);
        Assert.assertEquals(true, command.execute());
        assertCommandExecutionEvents(command, SUCCESS);
    }

    /**
     * Test a successful command execution.
     */
    @Test(expected = IllegalStateException.class)
    public void testDynamicOwnerFails() {
        TestHystrixCommand<Boolean> command = new HystrixCommandTest.DynamicOwnerTestCommand(null);
        Assert.assertEquals(true, command.execute());
    }

    /**
     * Test that HystrixCommandKey can be passed in dynamically.
     */
    @Test
    public void testDynamicKey() throws Exception {
        HystrixCommandTest.DynamicOwnerAndKeyTestCommand command1 = new HystrixCommandTest.DynamicOwnerAndKeyTestCommand(OWNER_ONE, KEY_ONE);
        Assert.assertEquals(true, execute());
        HystrixCommandTest.DynamicOwnerAndKeyTestCommand command2 = new HystrixCommandTest.DynamicOwnerAndKeyTestCommand(OWNER_ONE, KEY_TWO);
        Assert.assertEquals(true, execute());
        // 2 different circuit breakers should be created
        Assert.assertNotSame(getCircuitBreaker(), getCircuitBreaker());
    }

    /**
     * Test Request scoped caching of commands so that a 2nd duplicate call doesn't execute but returns the previous Future
     */
    @Test
    public void testRequestCache1() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        HystrixCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.queue();
        Future<String> f2 = command2.queue();
        Assert.assertEquals("A", f1.get());
        Assert.assertEquals("A", f2.get());
        Assert.assertTrue(command1.executed);
        // the second one should not have executed as it should have received the cached value instead
        Assert.assertFalse(command2.executed);
        Assert.assertTrue(((command1.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertFalse(command1.isResponseFromCache());
        Assert.assertTrue(command2.isResponseFromCache());
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test Request scoped caching doesn't prevent different ones from executing
     */
    @Test
    public void testRequestCache2() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        HystrixCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "B");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.queue();
        Future<String> f2 = command2.queue();
        Assert.assertEquals("A", f1.get());
        Assert.assertEquals("B", f2.get());
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        Assert.assertTrue(((command2.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertFalse(command2.isResponseFromCache());
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        Assert.assertNull(command1.getExecutionException());
        Assert.assertFalse(command2.isResponseFromCache());
        Assert.assertNull(command2.getExecutionException());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testRequestCache3() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        HystrixCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "B");
        HystrixCommandTest.SuccessfulCacheableCommand<String> command3 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "A");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.queue();
        Future<String> f2 = command2.queue();
        Future<String> f3 = command3.queue();
        Assert.assertEquals("A", f1.get());
        Assert.assertEquals("B", f2.get());
        Assert.assertEquals("A", f3.get());
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // but the 3rd should come from cache
        Assert.assertFalse(command3.executed);
        Assert.assertTrue(command3.isResponseFromCache());
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test Request scoped caching of commands so that a 2nd duplicate call doesn't execute but returns the previous Future
     */
    @Test
    public void testRequestCacheWithSlowExecution() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SlowCacheableCommand command1 = new HystrixCommandTest.SlowCacheableCommand(circuitBreaker, "A", 200);
        HystrixCommandTest.SlowCacheableCommand command2 = new HystrixCommandTest.SlowCacheableCommand(circuitBreaker, "A", 100);
        HystrixCommandTest.SlowCacheableCommand command3 = new HystrixCommandTest.SlowCacheableCommand(circuitBreaker, "A", 100);
        HystrixCommandTest.SlowCacheableCommand command4 = new HystrixCommandTest.SlowCacheableCommand(circuitBreaker, "A", 100);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        Future<String> f3 = queue();
        Future<String> f4 = queue();
        Assert.assertEquals("A", f2.get());
        Assert.assertEquals("A", f3.get());
        Assert.assertEquals("A", f4.get());
        Assert.assertEquals("A", f1.get());
        Assert.assertTrue(command1.executed);
        // the second one should not have executed as it should have received the cached value instead
        Assert.assertFalse(command2.executed);
        Assert.assertFalse(command3.executed);
        Assert.assertFalse(command4.executed);
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertFalse(isResponseFromCache());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        Assert.assertTrue(isResponseFromCache());
        Assert.assertTrue(isResponseFromCache());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        Assert.assertTrue(isResponseFromCache());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command3, SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command4, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(4);
        System.out.println(("HystrixRequestLog: " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testNoRequestCache3() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommand<String> command1 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, false, "A");
        HystrixCommandTest.SuccessfulCacheableCommand<String> command2 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, false, "B");
        HystrixCommandTest.SuccessfulCacheableCommand<String> command3 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, false, "A");
        Assert.assertTrue(command1.isCommandRunningInThread());
        Future<String> f1 = command1.queue();
        Future<String> f2 = command2.queue();
        Future<String> f3 = command3.queue();
        Assert.assertEquals("A", f1.get());
        Assert.assertEquals("B", f2.get());
        Assert.assertEquals("A", f3.get());
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // this should also execute since we disabled the cache
        Assert.assertTrue(command3.executed);
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testRequestCacheViaQueueSemaphore1() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command1 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, true, "A");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command2 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, true, "B");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command3 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, true, "A");
        Assert.assertFalse(command1.isCommandRunningInThread());
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        Future<String> f3 = queue();
        Assert.assertEquals("A", f1.get());
        Assert.assertEquals("B", f2.get());
        Assert.assertEquals("A", f3.get());
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // but the 3rd should come from cache
        Assert.assertFalse(command3.executed);
        Assert.assertTrue(isResponseFromCache());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testNoRequestCacheViaQueueSemaphore1() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command1 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, false, "A");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command2 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, false, "B");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command3 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, false, "A");
        Assert.assertFalse(command1.isCommandRunningInThread());
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        Future<String> f3 = queue();
        Assert.assertEquals("A", f1.get());
        Assert.assertEquals("B", f2.get());
        Assert.assertEquals("A", f3.get());
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // this should also execute because caching is disabled
        Assert.assertTrue(command3.executed);
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testRequestCacheViaExecuteSemaphore1() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command1 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, true, "A");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command2 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, true, "B");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command3 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, true, "A");
        Assert.assertFalse(command1.isCommandRunningInThread());
        String f1 = command1.execute();
        String f2 = command2.execute();
        String f3 = command3.execute();
        Assert.assertEquals("A", f1);
        Assert.assertEquals("B", f2);
        Assert.assertEquals("A", f3);
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // but the 3rd should come from cache
        Assert.assertFalse(command3.executed);
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testNoRequestCacheViaExecuteSemaphore1() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command1 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, false, "A");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command2 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, false, "B");
        HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore command3 = new HystrixCommandTest.SuccessfulCacheableCommandViaSemaphore(circuitBreaker, false, "A");
        Assert.assertFalse(command1.isCommandRunningInThread());
        String f1 = command1.execute();
        String f2 = command2.execute();
        String f3 = command3.execute();
        Assert.assertEquals("A", f1);
        Assert.assertEquals("B", f2);
        Assert.assertEquals("A", f3);
        Assert.assertTrue(command1.executed);
        // both should execute as they are different
        Assert.assertTrue(command2.executed);
        // this should also execute because caching is disabled
        Assert.assertTrue(command3.executed);
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, SUCCESS);
        assertCommandExecutionEvents(command3, SUCCESS);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(3);
    }

    @Test
    public void testNoRequestCacheOnTimeoutThrowsException() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback r1 = new HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            System.out.println(("r1 value: " + (execute())));
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback r2 = new HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            execute();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback r3 = new HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        Future<Boolean> f3 = queue();
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

        HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback r4 = new HystrixCommandTest.NoRequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            execute();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
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
        HystrixCommandTest.RequestCacheNullPointerExceptionCase command1 = new HystrixCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        HystrixCommandTest.RequestCacheNullPointerExceptionCase command2 = new HystrixCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        HystrixCommandTest.RequestCacheNullPointerExceptionCase command3 = new HystrixCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        // Expect it to time out - all results should be false
        Assert.assertFalse(execute());
        Assert.assertFalse(execute());// return from cache #1

        Assert.assertFalse(execute());// return from cache #2

        Thread.sleep(500);// timeout on command is set to 200ms

        HystrixCommandTest.RequestCacheNullPointerExceptionCase command4 = new HystrixCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        Boolean value = command4.execute();// return from cache #3

        Assert.assertFalse(value);
        HystrixCommandTest.RequestCacheNullPointerExceptionCase command5 = new HystrixCommandTest.RequestCacheNullPointerExceptionCase(circuitBreaker);
        Future<Boolean> f = queue();// return from cache #4

        // the bug is that we're getting a null Future back, rather than a Future that returns false
        Assert.assertNotNull(f);
        Assert.assertFalse(f.get());
        Assert.assertTrue(isResponseFromFallback());
        Assert.assertTrue(isResponseTimedOut());
        Assert.assertFalse(isFailedExecution());
        Assert.assertFalse(isResponseShortCircuited());
        Assert.assertNotNull(getExecutionException());
        assertCommandExecutionEvents(command1, TIMEOUT, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(command2, TIMEOUT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command3, TIMEOUT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command4, TIMEOUT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        assertCommandExecutionEvents(command5, TIMEOUT, FALLBACK_SUCCESS, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(5);
    }

    @Test
    public void testRequestCacheOnTimeoutThrowsException() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.RequestCacheTimeoutWithoutFallback r1 = new HystrixCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            System.out.println(("r1 value: " + (execute())));
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        HystrixCommandTest.RequestCacheTimeoutWithoutFallback r2 = new HystrixCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            execute();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            // what we want
        }
        HystrixCommandTest.RequestCacheTimeoutWithoutFallback r3 = new HystrixCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        Future<Boolean> f3 = queue();
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

        HystrixCommandTest.RequestCacheTimeoutWithoutFallback r4 = new HystrixCommandTest.RequestCacheTimeoutWithoutFallback(circuitBreaker);
        try {
            execute();
            // we should have thrown an exception
            Assert.fail("expected a timeout");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseTimedOut());
            Assert.assertFalse(isResponseFromFallback());
            // what we want
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
        HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback r1 = new HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("r1: " + (execute())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            Assert.assertTrue(isResponseRejected());
            // what we want
        }
        HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback r2 = new HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("r2: " + (execute())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            // e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            // what we want
        }
        HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback r3 = new HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("f3: " + (r3.queue().get())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            // e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            // what we want
        }
        // let the command finish (only 1 should actually be blocked on this due to the response cache)
        completionLatch.countDown();
        // then another after the command has completed
        HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback r4 = new HystrixCommandTest.RequestCacheThreadRejectionWithoutFallback(circuitBreaker, completionLatch);
        try {
            System.out.println(("r4: " + (execute())));
            // we should have thrown an exception
            Assert.fail("expected a rejection");
        } catch (HystrixRuntimeException e) {
            // e.printStackTrace();
            Assert.assertTrue(isResponseRejected());
            Assert.assertFalse(isResponseFromFallback());
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
    public void testBasicExecutionWorksWithoutRequestVariable() throws Exception {
        /* force the RequestVariable to not be initialized */
        HystrixRequestContext.setContextOnCurrentThread(null);
        TestHystrixCommand<Boolean> command = new HystrixCommandTest.SuccessfulTestCommand();
        Assert.assertEquals(true, command.execute());
        TestHystrixCommand<Boolean> command2 = new HystrixCommandTest.SuccessfulTestCommand();
        Assert.assertEquals(true, command2.queue().get());
    }

    /**
     * Test that if we try and execute a command with a cacheKey without initializing RequestVariable that it gives an error.
     */
    @Test(expected = HystrixRuntimeException.class)
    public void testCacheKeyExecutionRequiresRequestVariable() throws Exception {
        /* force the RequestVariable to not be initialized */
        HystrixRequestContext.setContextOnCurrentThread(null);
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.SuccessfulCacheableCommand command = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "one");
        Assert.assertEquals("one", command.execute());
        HystrixCommandTest.SuccessfulCacheableCommand command2 = new HystrixCommandTest.SuccessfulCacheableCommand<String>(circuitBreaker, true, "two");
        Assert.assertEquals("two", command2.queue().get());
    }

    /**
     * Test that a BadRequestException can be thrown and not count towards errors and bypasses fallback.
     */
    @Test
    public void testBadRequestExceptionViaExecuteInThread() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.BadRequestCommand command1 = null;
        try {
            command1 = new HystrixCommandTest.BadRequestCommand(circuitBreaker, ExecutionIsolationStrategy.THREAD);
            execute();
            Assert.fail(("we expect to receive a " + (HystrixBadRequestException.class.getSimpleName())));
        } catch (HystrixBadRequestException e) {
            // success
            e.printStackTrace();
        }
        assertCommandExecutionEvents(command1, BAD_REQUEST);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test that a BadRequestException can be thrown and not count towards errors and bypasses fallback.
     */
    @Test
    public void testBadRequestExceptionViaQueueInThread() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.BadRequestCommand command1 = null;
        try {
            command1 = new HystrixCommandTest.BadRequestCommand(circuitBreaker, ExecutionIsolationStrategy.THREAD);
            command1.queue().get();
            Assert.fail(("we expect to receive a " + (HystrixBadRequestException.class.getSimpleName())));
        } catch (ExecutionException e) {
            e.printStackTrace();
            if ((e.getCause()) instanceof HystrixBadRequestException) {
                // success
            } else {
                Assert.fail(((("We expect a " + (HystrixBadRequestException.class.getSimpleName())) + " but got a ") + (e.getClass().getSimpleName())));
            }
        }
        assertCommandExecutionEvents(command1, BAD_REQUEST);
        Assert.assertNotNull(getExecutionException());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test that BadRequestException behavior works the same on a cached response.
     */
    @Test
    public void testBadRequestExceptionViaQueueInThreadOnResponseFromCache() throws Exception {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        // execute once to cache the value
        HystrixCommandTest.BadRequestCommand command1 = null;
        try {
            command1 = new HystrixCommandTest.BadRequestCommand(circuitBreaker, ExecutionIsolationStrategy.THREAD);
            execute();
        } catch (Throwable e) {
            // ignore
        }
        HystrixCommandTest.BadRequestCommand command2 = null;
        try {
            command2 = new HystrixCommandTest.BadRequestCommand(circuitBreaker, ExecutionIsolationStrategy.THREAD);
            command2.queue().get();
            Assert.fail(("we expect to receive a " + (HystrixBadRequestException.class.getSimpleName())));
        } catch (ExecutionException e) {
            e.printStackTrace();
            if ((e.getCause()) instanceof HystrixBadRequestException) {
                // success
            } else {
                Assert.fail(((("We expect a " + (HystrixBadRequestException.class.getSimpleName())) + " but got a ") + (e.getClass().getSimpleName())));
            }
        }
        assertCommandExecutionEvents(command1, BAD_REQUEST);
        assertCommandExecutionEvents(command2, BAD_REQUEST, RESPONSE_FROM_CACHE);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    /**
     * Test that a BadRequestException can be thrown and not count towards errors and bypasses fallback.
     */
    @Test
    public void testBadRequestExceptionViaExecuteInSemaphore() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.BadRequestCommand command1 = new HystrixCommandTest.BadRequestCommand(circuitBreaker, ExecutionIsolationStrategy.SEMAPHORE);
        try {
            execute();
            Assert.fail(("we expect to receive a " + (HystrixBadRequestException.class.getSimpleName())));
        } catch (HystrixBadRequestException e) {
            // success
            e.printStackTrace();
        }
        assertCommandExecutionEvents(command1, BAD_REQUEST);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test a checked Exception being thrown
     */
    @Test
    public void testCheckedExceptionViaExecute() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.CommandWithCheckedException command = new HystrixCommandTest.CommandWithCheckedException(circuitBreaker);
        try {
            execute();
            Assert.fail(("we expect to receive a " + (Exception.class.getSimpleName())));
        } catch (Exception e) {
            Assert.assertEquals("simulated checked exception message", e.getCause().getMessage());
        }
        Assert.assertEquals("simulated checked exception message", getFailedExecutionException().getMessage());
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(getExecutionException());
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
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
        HystrixCommandTest.CommandWithCheckedException command = new HystrixCommandTest.CommandWithCheckedException(circuitBreaker);
        final AtomicReference<Throwable> t = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            command.observe().subscribe(new rx.Observer<Boolean>() {
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
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    /**
     * Test an Exception implementing NotWrappedByHystrix being thrown
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testNotWrappedExceptionViaObserve() throws InterruptedException {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.CommandWithNotWrappedByHystrixException command = new HystrixCommandTest.CommandWithNotWrappedByHystrixException(circuitBreaker);
        final AtomicReference<Throwable> t = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            command.observe().subscribe(new rx.Observer<Boolean>() {
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
        Assert.assertTrue(((t.get()) instanceof NotWrappedByHystrixTestException));
        Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(getExecutionException());
        Assert.assertTrue(((getExecutionException()) instanceof NotWrappedByHystrixTestException));
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testSemaphoreExecutionWithTimeout() {
        TestHystrixCommand<Boolean> cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), false);
        System.out.println("Starting command");
        long timeMillis = System.currentTimeMillis();
        try {
            cmd.execute();
            Assert.fail("Should throw");
        } catch (Throwable t) {
            Assert.assertNotNull(cmd.getExecutionException());
            System.out.println(("Unsuccessful Execution took : " + ((System.currentTimeMillis()) - timeMillis)));
            assertCommandExecutionEvents(cmd, TIMEOUT, FALLBACK_MISSING);
            Assert.assertEquals(0, cmd.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(1);
        }
    }

    /**
     * Test a recoverable java.lang.Error being thrown with no fallback
     */
    @Test
    public void testRecoverableErrorWithNoFallbackThrowsError() {
        TestHystrixCommand<Integer> command = getRecoverableErrorCommand(THREAD, UNIMPLEMENTED);
        try {
            command.execute();
            Assert.fail(("we expect to receive a " + (Error.class.getSimpleName())));
        } catch (Exception e) {
            // the actual error is an extra cause level deep because Hystrix needs to wrap Throwable/Error as it's public
            // methods only support Exception and it's not a strong enough reason to break backwards compatibility and jump to version 2.x
            // so HystrixRuntimeException -> wrapper Exception -> actual Error
            Assert.assertEquals("Execution ERROR for TestHystrixCommand", e.getCause().getCause().getMessage());
        }
        Assert.assertEquals("Execution ERROR for TestHystrixCommand", command.getFailedExecutionException().getCause().getMessage());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testRecoverableErrorMaskedByFallbackButLogged() {
        TestHystrixCommand<Integer> command = getRecoverableErrorCommand(THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
        Assert.assertEquals(HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE, command.execute());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE, FALLBACK_SUCCESS);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testUnrecoverableErrorThrownWithNoFallback() {
        TestHystrixCommand<Integer> command = getUnrecoverableErrorCommand(THREAD, UNIMPLEMENTED);
        try {
            command.execute();
            Assert.fail(("we expect to receive a " + (Error.class.getSimpleName())));
        } catch (Exception e) {
            // the actual error is an extra cause level deep because Hystrix needs to wrap Throwable/Error as it's public
            // methods only support Exception and it's not a strong enough reason to break backwards compatibility and jump to version 2.x
            // so HystrixRuntimeException -> wrapper Exception -> actual Error
            Assert.assertEquals("Unrecoverable Error for TestHystrixCommand", e.getCause().getCause().getMessage());
        }
        Assert.assertEquals("Unrecoverable Error for TestHystrixCommand", command.getFailedExecutionException().getCause().getMessage());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    // even though fallback is implemented, that logic never fires, as this is an unrecoverable error and should be directly propagated to the caller
    @Test
    public void testUnrecoverableErrorThrownWithFallback() {
        TestHystrixCommand<Integer> command = getUnrecoverableErrorCommand(THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
        try {
            command.execute();
            Assert.fail(("we expect to receive a " + (Error.class.getSimpleName())));
        } catch (Exception e) {
            // the actual error is an extra cause level deep because Hystrix needs to wrap Throwable/Error as it's public
            // methods only support Exception and it's not a strong enough reason to break backwards compatibility and jump to version 2.x
            // so HystrixRuntimeException -> wrapper Exception -> actual Error
            Assert.assertEquals("Unrecoverable Error for TestHystrixCommand", e.getCause().getCause().getMessage());
        }
        Assert.assertEquals("Unrecoverable Error for TestHystrixCommand", command.getFailedExecutionException().getCause().getMessage());
        Assert.assertTrue(((command.getExecutionTimeInMilliseconds()) > (-1)));
        Assert.assertTrue(command.isFailedExecution());
        assertCommandExecutionEvents(command, FAILURE);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    static class EventCommand extends HystrixCommand {
        public EventCommand() {
            super(Setter.withGroupKey(Factory.asKey("eventGroup")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withFallbackIsolationSemaphoreMaxConcurrentRequests(3)));
        }

        @Override
        protected String run() throws Exception {
            System.out.println(((Thread.currentThread().getName()) + " : In run()"));
            throw new RuntimeException("run_exception");
        }

        @Override
        public String getFallback() {
            try {
                System.out.println((((Thread.currentThread().getName()) + " : In fallback => ") + (getExecutionEvents())));
                Thread.sleep(30000L);
            } catch (InterruptedException e) {
                System.out.println(((Thread.currentThread().getName()) + " : Interruption occurred"));
            }
            System.out.println(((Thread.currentThread().getName()) + " : CMD Success Result"));
            return "fallback";
        }
    }

    @Test
    public void testNonBlockingCommandQueueFiresTimeout() throws Exception {
        // see https://github.com/Netflix/Hystrix/issues/514
        final TestHystrixCommand<Integer> cmd = getCommand(THREAD, SUCCESS, 200, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 50);
        new Thread() {
            @Override
            public void run() {
                cmd.queue();
            }
        }.start();
        Thread.sleep(200);
        // timeout should occur in 50ms, and underlying thread should run for 500ms
        // therefore, after 200ms, the command should have finished with a fallback on timeout
        Assert.assertTrue(cmd.isExecutionComplete());
        Assert.assertTrue(cmd.isResponseTimedOut());
        Assert.assertEquals(0, cmd.metrics.getCurrentConcurrentExecutionCount());
    }

    /**
     * Test a command execution that fails but has a fallback.
     */
    @Test
    public void testExecutionFailureWithFallbackImplementedButDisabled() {
        TestHystrixCommand<Boolean> commandEnabled = new HystrixCommandTest.KnownFailureTestCommandWithFallback(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true);
        try {
            Assert.assertEquals(false, commandEnabled.execute());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("We should have received a response from the fallback.");
        }
        TestHystrixCommand<Boolean> commandDisabled = new HystrixCommandTest.KnownFailureTestCommandWithFallback(new HystrixCircuitBreakerTest.TestCircuitBreaker(), false);
        try {
            Assert.assertEquals(false, commandDisabled.execute());
            Assert.fail("expect exception thrown");
        } catch (Exception e) {
            // expected
        }
        Assert.assertEquals("we failed with a simulated issue", commandDisabled.getFailedExecutionException().getMessage());
        Assert.assertTrue(commandDisabled.isFailedExecution());
        assertCommandExecutionEvents(commandEnabled, FAILURE, FALLBACK_SUCCESS);
        assertCommandExecutionEvents(commandDisabled, FAILURE);
        Assert.assertNotNull(commandDisabled.getExecutionException());
        Assert.assertEquals(0, commandDisabled.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(2);
    }

    @Test
    public void testExecutionTimeoutValue() {
        HystrixCommand.Setter properties = Setter.withGroupKey(Factory.asKey("TestKey")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(50));
        HystrixCommand<String> command = new HystrixCommand<String>(properties) {
            @Override
            protected String run() throws Exception {
                Thread.sleep(3000);
                // should never reach here
                return "hello";
            }

            @Override
            protected String getFallback() {
                if (isResponseTimedOut()) {
                    return "timed-out";
                } else {
                    return "abc";
                }
            }
        };
        String value = command.execute();
        Assert.assertTrue(command.isResponseTimedOut());
        Assert.assertEquals("expected fallback value", "timed-out", value);
    }

    /**
     * See https://github.com/Netflix/Hystrix/issues/212
     */
    @Test
    public void testObservableTimeoutNoFallbackThreadContext() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        final AtomicReference<Thread> onErrorThread = new AtomicReference<Thread>();
        final AtomicBoolean isRequestContextInitialized = new AtomicBoolean();
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 200, UNIMPLEMENTED, 50);
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
        assertCommandExecutionEvents(command, TIMEOUT, FALLBACK_MISSING);
        Assert.assertNotNull(command.getExecutionException());
        Assert.assertEquals(0, command.getBuilder().metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testExceptionConvertedToBadRequestExceptionInExecutionHookBypassesCircuitBreaker() {
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        HystrixCommandTest.ExceptionToBadRequestByExecutionHookCommand command = new HystrixCommandTest.ExceptionToBadRequestByExecutionHookCommand(circuitBreaker, ExecutionIsolationStrategy.THREAD);
        try {
            execute();
            Assert.fail(("we expect to receive a " + (HystrixBadRequestException.class.getSimpleName())));
        } catch (HystrixBadRequestException e) {
            // success
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(((("We expect a " + (HystrixBadRequestException.class.getSimpleName())) + " but got a ") + (e.getClass().getSimpleName())));
        }
        assertCommandExecutionEvents(command, BAD_REQUEST);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        assertSaneHystrixRequestLog(1);
    }

    @Test
    public void testInterruptFutureOnTimeout() throws InterruptedException, ExecutionException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true);
        // when
        Future<Boolean> f = queue();
        // then
        Thread.sleep(500);
        Assert.assertTrue(cmd.hasBeenInterrupted());
    }

    @Test
    public void testInterruptObserveOnTimeout() throws InterruptedException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true);
        // when
        cmd.observe().subscribe();
        // then
        Thread.sleep(500);
        Assert.assertTrue(cmd.hasBeenInterrupted());
    }

    @Test
    public void testInterruptToObservableOnTimeout() throws InterruptedException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true);
        // when
        toObservable().subscribe();
        // then
        Thread.sleep(500);
        Assert.assertTrue(cmd.hasBeenInterrupted());
    }

    @Test
    public void testDoNotInterruptFutureOnTimeoutIfPropertySaysNotTo() throws InterruptedException, ExecutionException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), false);
        // when
        Future<Boolean> f = queue();
        // then
        Thread.sleep(500);
        Assert.assertFalse(cmd.hasBeenInterrupted());
    }

    @Test
    public void testDoNotInterruptObserveOnTimeoutIfPropertySaysNotTo() throws InterruptedException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), false);
        // when
        cmd.observe().subscribe();
        // then
        Thread.sleep(500);
        Assert.assertFalse(cmd.hasBeenInterrupted());
    }

    @Test
    public void testDoNotInterruptToObservableOnTimeoutIfPropertySaysNotTo() throws InterruptedException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), false);
        // when
        toObservable().subscribe();
        // then
        Thread.sleep(500);
        Assert.assertFalse(cmd.hasBeenInterrupted());
    }

    @Test
    public void testCancelFutureWithInterruptionWhenPropertySaysNotTo() throws InterruptedException, ExecutionException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true, false, 1000);
        // when
        Future<Boolean> f = queue();
        Thread.sleep(500);
        f.cancel(true);
        Thread.sleep(500);
        // then
        try {
            f.get();
            Assert.fail("Should have thrown a CancellationException");
        } catch (CancellationException e) {
            Assert.assertFalse(cmd.hasBeenInterrupted());
        }
    }

    @Test
    public void testCancelFutureWithInterruption() throws InterruptedException, ExecutionException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true, true, 1000);
        // when
        Future<Boolean> f = queue();
        Thread.sleep(500);
        f.cancel(true);
        Thread.sleep(500);
        // then
        try {
            f.get();
            Assert.fail("Should have thrown a CancellationException");
        } catch (CancellationException e) {
            Assert.assertTrue(cmd.hasBeenInterrupted());
        }
    }

    @Test
    public void testCancelFutureWithoutInterruption() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        HystrixCommandTest.InterruptibleCommand cmd = new HystrixCommandTest.InterruptibleCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker(), true, true, 1000);
        // when
        Future<Boolean> f = queue();
        Thread.sleep(500);
        f.cancel(false);
        Thread.sleep(500);
        // then
        try {
            f.get();
            Assert.fail("Should have thrown a CancellationException");
        } catch (CancellationException e) {
            Assert.assertFalse(cmd.hasBeenInterrupted());
        }
    }

    @Test
    public void testChainedCommand() {
        class SubCommand extends TestHystrixCommand<Integer> {
            public SubCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
                super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            }

            @Override
            protected Integer run() throws Exception {
                return 2;
            }
        }
        class PrimaryCommand extends TestHystrixCommand<Integer> {
            public PrimaryCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
                super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            }

            @Override
            protected Integer run() throws Exception {
                throw new RuntimeException("primary failure");
            }

            @Override
            protected Integer getFallback() {
                SubCommand subCmd = new SubCommand(new HystrixCircuitBreakerTest.TestCircuitBreaker());
                return subCmd.execute();
            }
        }
        Assert.assertTrue((2 == (execute())));
    }

    @Test
    public void testSlowFallback() {
        class PrimaryCommand extends TestHystrixCommand<Integer> {
            public PrimaryCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
                super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            }

            @Override
            protected Integer run() throws Exception {
                throw new RuntimeException("primary failure");
            }

            @Override
            protected Integer getFallback() {
                try {
                    Thread.sleep(1500);
                    return 1;
                } catch (InterruptedException ie) {
                    System.out.println("Caught Interrupted Exception");
                    ie.printStackTrace();
                }
                return -1;
            }
        }
        Assert.assertTrue((1 == (execute())));
    }

    @Test
    public void testSemaphoreThreadSafety() {
        final int NUM_PERMITS = 1;
        final TryableSemaphoreActual s = new TryableSemaphoreActual(HystrixProperty.Factory.asProperty(NUM_PERMITS));
        final int NUM_THREADS = 10;
        ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
        final int NUM_TRIALS = 100;
        for (int t = 0; t < NUM_TRIALS; t++) {
            System.out.println(("TRIAL : " + t));
            final AtomicInteger numAcquired = new AtomicInteger(0);
            final CountDownLatch latch = new CountDownLatch(NUM_THREADS);
            for (int i = 0; i < NUM_THREADS; i++) {
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        boolean acquired = s.tryAcquire();
                        if (acquired) {
                            try {
                                numAcquired.incrementAndGet();
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            } finally {
                                s.release();
                            }
                        }
                        latch.countDown();
                    }
                });
            }
            try {
                Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
            } catch (InterruptedException ex) {
                Assert.fail(ex.getMessage());
            }
            Assert.assertEquals("Number acquired should be equal to the number of permits", NUM_PERMITS, numAcquired.get());
            Assert.assertEquals("Semaphore should always get released back to 0", 0, s.getNumberOfPermitsUsed());
        }
    }

    @Test
    public void testCancelledTasksInQueueGetRemoved() throws Exception {
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("Cancellation-A");
        HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
        CommonHystrixCommandTests.SingleThreadedPoolWithQueue pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(10, 1);
        HystrixCommandTest.TestCommandRejection command1 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_NOT_IMPLEMENTED);
        HystrixCommandTest.TestCommandRejection command2 = new HystrixCommandTest.TestCommandRejection(key, circuitBreaker, pool, 500, 600, HystrixCommandTest.TestCommandRejection.FALLBACK_NOT_IMPLEMENTED);
        // this should go through the queue and into the thread pool
        Future<Boolean> poolFiller = queue();
        // this command will stay in the queue until the thread pool is empty
        Observable<Boolean> cmdInQueue = observe();
        Subscription s = cmdInQueue.subscribe();
        Assert.assertEquals(1, pool.queue.size());
        s.unsubscribe();
        Assert.assertEquals(0, pool.queue.size());
        // make sure we wait for the command to finish so the state is clean for next test
        poolFiller.get();
        assertCommandExecutionEvents(command1, SUCCESS);
        assertCommandExecutionEvents(command2, CANCELLED);
        Assert.assertEquals(0, circuitBreaker.metrics.getCurrentConcurrentExecutionCount());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        assertSaneHystrixRequestLog(2);
    }

    @Test
    public void testOnRunStartHookThrowsSemaphoreIsolated() {
        final AtomicBoolean exceptionEncountered = new AtomicBoolean(false);
        final AtomicBoolean onThreadStartInvoked = new AtomicBoolean(false);
        final AtomicBoolean onThreadCompleteInvoked = new AtomicBoolean(false);
        final AtomicBoolean executionAttempted = new AtomicBoolean(false);
        class FailureInjectionHook extends HystrixCommandExecutionHook {
            @Override
            public <T> void onExecutionStart(HystrixInvokable<T> commandInstance) {
                throw new HystrixRuntimeException(FailureType.COMMAND_EXCEPTION, commandInstance.getClass(), "Injected Failure", null, null);
            }

            @Override
            public <T> void onThreadStart(HystrixInvokable<T> commandInstance) {
                onThreadStartInvoked.set(true);
                super.onThreadStart(commandInstance);
            }

            @Override
            public <T> void onThreadComplete(HystrixInvokable<T> commandInstance) {
                onThreadCompleteInvoked.set(true);
                super.onThreadComplete(commandInstance);
            }
        }
        final FailureInjectionHook failureInjectionHook = new FailureInjectionHook();
        class FailureInjectedCommand extends TestHystrixCommand<Integer> {
            public FailureInjectedCommand(ExecutionIsolationStrategy isolationStrategy) {
                super(TestHystrixCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationStrategy)), failureInjectionHook);
            }

            @Override
            protected Integer run() throws Exception {
                executionAttempted.set(true);
                return 3;
            }
        }
        TestHystrixCommand<Integer> semaphoreCmd = new FailureInjectedCommand(ExecutionIsolationStrategy.SEMAPHORE);
        try {
            int result = semaphoreCmd.execute();
            System.out.println(("RESULT : " + result));
        } catch (Throwable ex) {
            ex.printStackTrace();
            exceptionEncountered.set(true);
        }
        Assert.assertTrue(exceptionEncountered.get());
        Assert.assertFalse(onThreadStartInvoked.get());
        Assert.assertFalse(onThreadCompleteInvoked.get());
        Assert.assertFalse(executionAttempted.get());
        Assert.assertEquals(0, semaphoreCmd.metrics.getCurrentConcurrentExecutionCount());
    }

    @Test
    public void testOnRunStartHookThrowsThreadIsolated() {
        final AtomicBoolean exceptionEncountered = new AtomicBoolean(false);
        final AtomicBoolean onThreadStartInvoked = new AtomicBoolean(false);
        final AtomicBoolean onThreadCompleteInvoked = new AtomicBoolean(false);
        final AtomicBoolean executionAttempted = new AtomicBoolean(false);
        class FailureInjectionHook extends HystrixCommandExecutionHook {
            @Override
            public <T> void onExecutionStart(HystrixInvokable<T> commandInstance) {
                throw new HystrixRuntimeException(FailureType.COMMAND_EXCEPTION, commandInstance.getClass(), "Injected Failure", null, null);
            }

            @Override
            public <T> void onThreadStart(HystrixInvokable<T> commandInstance) {
                onThreadStartInvoked.set(true);
                super.onThreadStart(commandInstance);
            }

            @Override
            public <T> void onThreadComplete(HystrixInvokable<T> commandInstance) {
                onThreadCompleteInvoked.set(true);
                super.onThreadComplete(commandInstance);
            }
        }
        final FailureInjectionHook failureInjectionHook = new FailureInjectionHook();
        class FailureInjectedCommand extends TestHystrixCommand<Integer> {
            public FailureInjectedCommand(ExecutionIsolationStrategy isolationStrategy) {
                super(TestHystrixCommand.testPropsBuilder(new HystrixCircuitBreakerTest.TestCircuitBreaker()).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationStrategy)), failureInjectionHook);
            }

            @Override
            protected Integer run() throws Exception {
                executionAttempted.set(true);
                return 3;
            }
        }
        TestHystrixCommand<Integer> threadCmd = new FailureInjectedCommand(ExecutionIsolationStrategy.THREAD);
        try {
            int result = threadCmd.execute();
            System.out.println(("RESULT : " + result));
        } catch (Throwable ex) {
            ex.printStackTrace();
            exceptionEncountered.set(true);
        }
        Assert.assertTrue(exceptionEncountered.get());
        Assert.assertTrue(onThreadStartInvoked.get());
        Assert.assertTrue(onThreadCompleteInvoked.get());
        Assert.assertFalse(executionAttempted.get());
        Assert.assertEquals(0, threadCmd.metrics.getCurrentConcurrentExecutionCount());
    }

    @Test
    public void testEarlyUnsubscribeDuringExecutionViaToObservable() {
        class AsyncCommand extends HystrixCommand<Boolean> {
            public AsyncCommand() {
                super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            }

            @Override
            protected Boolean run() {
                try {
                    Thread.sleep(500);
                    return true;
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        HystrixCommand<Boolean> cmd = new AsyncCommand();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Boolean> o = cmd.toObservable();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("OnUnsubscribe");
                latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("OnCompleted");
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
            Thread.sleep(10);
            s.unsubscribe();
            Assert.assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
            System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
            Assert.assertEquals("Number of execution semaphores in use", 0, cmd.getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use", 0, cmd.getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(cmd.isExecutionComplete());
            Assert.assertEquals(null, cmd.getFailedExecutionException());
            Assert.assertNull(cmd.getExecutionException());
            System.out.println(("Execution time : " + (cmd.getExecutionTimeInMilliseconds())));
            Assert.assertTrue(((cmd.getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertFalse(cmd.isSuccessfulExecution());
            assertCommandExecutionEvents(cmd, CANCELLED);
            Assert.assertEquals(0, cmd.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testEarlyUnsubscribeDuringExecutionViaObserve() {
        class AsyncCommand extends HystrixCommand<Boolean> {
            public AsyncCommand() {
                super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            }

            @Override
            protected Boolean run() {
                try {
                    Thread.sleep(500);
                    return true;
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        HystrixCommand<Boolean> cmd = new AsyncCommand();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Boolean> o = cmd.observe();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("OnUnsubscribe");
                latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("OnCompleted");
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
            Thread.sleep(10);
            s.unsubscribe();
            Assert.assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
            System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
            Assert.assertEquals("Number of execution semaphores in use", 0, cmd.getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use", 0, cmd.getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(cmd.isExecutionComplete());
            Assert.assertEquals(null, cmd.getFailedExecutionException());
            Assert.assertNull(cmd.getExecutionException());
            Assert.assertTrue(((cmd.getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertFalse(cmd.isSuccessfulExecution());
            assertCommandExecutionEvents(cmd, CANCELLED);
            Assert.assertEquals(0, cmd.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testEarlyUnsubscribeDuringFallback() {
        class AsyncCommand extends HystrixCommand<Boolean> {
            public AsyncCommand() {
                super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            }

            @Override
            protected Boolean run() {
                throw new RuntimeException("run failure");
            }

            @Override
            protected Boolean getFallback() {
                try {
                    Thread.sleep(500);
                    return false;
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        HystrixCommand<Boolean> cmd = new AsyncCommand();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Boolean> o = cmd.toObservable();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("OnUnsubscribe");
                latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
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
            Assert.assertEquals(0, cmd.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertFalse(cmd.isExecutionComplete());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testRequestThenCacheHitAndCacheHitUnsubscribed() {
        HystrixCommandTest.AsyncCacheableCommand original = new HystrixCommandTest.AsyncCacheableCommand("foo");
        HystrixCommandTest.AsyncCacheableCommand fromCache = new HystrixCommandTest.AsyncCacheableCommand("foo");
        final AtomicReference<Boolean> originalValue = new AtomicReference<Boolean>(null);
        final AtomicReference<Boolean> fromCacheValue = new AtomicReference<Boolean>(null);
        final CountDownLatch originalLatch = new CountDownLatch(1);
        final CountDownLatch fromCacheLatch = new CountDownLatch(1);
        Observable<Boolean> originalObservable = original.toObservable();
        Observable<Boolean> fromCacheObservable = fromCache.toObservable();
        Subscription originalSubscription = originalObservable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original Unsubscribe"));
                originalLatch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnCompleted"));
                originalLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnError : ") + e));
                originalLatch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnNext : ") + b));
                originalValue.set(b);
            }
        });
        Subscription fromCacheSubscription = fromCacheObservable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " FromCache Unsubscribe"));
                fromCacheLatch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " FromCache OnCompleted"));
                fromCacheLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " FromCache OnError : ") + e));
                fromCacheLatch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " FromCache OnNext : ") + b));
                fromCacheValue.set(b);
            }
        });
        try {
            fromCacheSubscription.unsubscribe();
            Assert.assertTrue(fromCacheLatch.await(600, TimeUnit.MILLISECONDS));
            Assert.assertTrue(originalLatch.await(600, TimeUnit.MILLISECONDS));
            Assert.assertEquals("Number of execution semaphores in use (original)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (original)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertTrue(isExecutionComplete());
            Assert.assertTrue(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertTrue(isSuccessfulExecution());
            assertCommandExecutionEvents(original, SUCCESS);
            Assert.assertTrue(originalValue.get());
            Assert.assertEquals(0, original.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertEquals("Number of execution semaphores in use (fromCache)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (fromCache)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            assertCommandExecutionEvents(fromCache, RESPONSE_FROM_CACHE, CANCELLED);
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            Assert.assertEquals(0, fromCache.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertFalse(original.isCancelled());// underlying work

            System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
            assertSaneHystrixRequestLog(2);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testRequestThenCacheHitAndOriginalUnsubscribed() {
        HystrixCommandTest.AsyncCacheableCommand original = new HystrixCommandTest.AsyncCacheableCommand("foo");
        HystrixCommandTest.AsyncCacheableCommand fromCache = new HystrixCommandTest.AsyncCacheableCommand("foo");
        final AtomicReference<Boolean> originalValue = new AtomicReference<Boolean>(null);
        final AtomicReference<Boolean> fromCacheValue = new AtomicReference<Boolean>(null);
        final CountDownLatch originalLatch = new CountDownLatch(1);
        final CountDownLatch fromCacheLatch = new CountDownLatch(1);
        Observable<Boolean> originalObservable = original.toObservable();
        Observable<Boolean> fromCacheObservable = fromCache.toObservable();
        Subscription originalSubscription = originalObservable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original Unsubscribe"));
                originalLatch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnCompleted"));
                originalLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnError : ") + e));
                originalLatch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnNext : ") + b));
                originalValue.set(b);
            }
        });
        Subscription fromCacheSubscription = fromCacheObservable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache Unsubscribe"));
                fromCacheLatch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache OnCompleted"));
                fromCacheLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache OnError : ") + e));
                fromCacheLatch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache OnNext : ") + b));
                fromCacheValue.set(b);
            }
        });
        try {
            Thread.sleep(10);
            originalSubscription.unsubscribe();
            Assert.assertTrue(originalLatch.await(600, TimeUnit.MILLISECONDS));
            Assert.assertTrue(fromCacheLatch.await(600, TimeUnit.MILLISECONDS));
            Assert.assertEquals("Number of execution semaphores in use (original)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (original)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertTrue(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            assertCommandExecutionEvents(original, CANCELLED);
            Assert.assertNull(originalValue.get());
            Assert.assertEquals(0, original.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertEquals("Number of execution semaphores in use (fromCache)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (fromCache)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertTrue(isExecutionComplete());
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            assertCommandExecutionEvents(fromCache, SUCCESS, RESPONSE_FROM_CACHE);
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
            Assert.assertTrue(isSuccessfulExecution());
            Assert.assertEquals(0, fromCache.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertFalse(original.isCancelled());// underlying work

            System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
            assertSaneHystrixRequestLog(2);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testRequestThenTwoCacheHitsOriginalAndOneCacheHitUnsubscribed() {
        HystrixCommandTest.AsyncCacheableCommand original = new HystrixCommandTest.AsyncCacheableCommand("foo");
        HystrixCommandTest.AsyncCacheableCommand fromCache1 = new HystrixCommandTest.AsyncCacheableCommand("foo");
        HystrixCommandTest.AsyncCacheableCommand fromCache2 = new HystrixCommandTest.AsyncCacheableCommand("foo");
        final AtomicReference<Boolean> originalValue = new AtomicReference<Boolean>(null);
        final AtomicReference<Boolean> fromCache1Value = new AtomicReference<Boolean>(null);
        final AtomicReference<Boolean> fromCache2Value = new AtomicReference<Boolean>(null);
        final CountDownLatch originalLatch = new CountDownLatch(1);
        final CountDownLatch fromCache1Latch = new CountDownLatch(1);
        final CountDownLatch fromCache2Latch = new CountDownLatch(1);
        Observable<Boolean> originalObservable = original.toObservable();
        Observable<Boolean> fromCache1Observable = fromCache1.toObservable();
        Observable<Boolean> fromCache2Observable = fromCache2.toObservable();
        Subscription originalSubscription = originalObservable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original Unsubscribe"));
                originalLatch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnCompleted"));
                originalLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnError : ") + e));
                originalLatch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnNext : ") + b));
                originalValue.set(b);
            }
        });
        Subscription fromCache1Subscription = fromCache1Observable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 Unsubscribe"));
                fromCache1Latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 OnCompleted"));
                fromCache1Latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 OnError : ") + e));
                fromCache1Latch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 OnNext : ") + b));
                fromCache1Value.set(b);
            }
        });
        Subscription fromCache2Subscription = fromCache2Observable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 Unsubscribe"));
                fromCache2Latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 OnCompleted"));
                fromCache2Latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 OnError : ") + e));
                fromCache2Latch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 OnNext : ") + b));
                fromCache2Value.set(b);
            }
        });
        try {
            Thread.sleep(10);
            originalSubscription.unsubscribe();
            // fromCache1Subscription.unsubscribe();
            fromCache2Subscription.unsubscribe();
            Assert.assertTrue(originalLatch.await(600, TimeUnit.MILLISECONDS));
            Assert.assertTrue(fromCache1Latch.await(600, TimeUnit.MILLISECONDS));
            Assert.assertTrue(fromCache2Latch.await(600, TimeUnit.MILLISECONDS));
            System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
            Assert.assertEquals("Number of execution semaphores in use (original)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (original)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertTrue(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            assertCommandExecutionEvents(original, CANCELLED);
            Assert.assertNull(originalValue.get());
            Assert.assertFalse(original.isCancelled());// underlying work

            Assert.assertEquals(0, original.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertEquals("Number of execution semaphores in use (fromCache1)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (fromCache1)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertTrue(isExecutionComplete());
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            assertCommandExecutionEvents(fromCache1, SUCCESS, RESPONSE_FROM_CACHE);
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
            Assert.assertTrue(isSuccessfulExecution());
            Assert.assertTrue(fromCache1Value.get());
            Assert.assertEquals(0, fromCache1.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertEquals("Number of execution semaphores in use (fromCache2)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (fromCache2)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            assertCommandExecutionEvents(fromCache2, RESPONSE_FROM_CACHE, CANCELLED);
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            Assert.assertNull(fromCache2Value.get());
            Assert.assertEquals(0, fromCache2.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(3);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testRequestThenTwoCacheHitsAllUnsubscribed() {
        HystrixCommandTest.AsyncCacheableCommand original = new HystrixCommandTest.AsyncCacheableCommand("foo");
        HystrixCommandTest.AsyncCacheableCommand fromCache1 = new HystrixCommandTest.AsyncCacheableCommand("foo");
        HystrixCommandTest.AsyncCacheableCommand fromCache2 = new HystrixCommandTest.AsyncCacheableCommand("foo");
        final CountDownLatch originalLatch = new CountDownLatch(1);
        final CountDownLatch fromCache1Latch = new CountDownLatch(1);
        final CountDownLatch fromCache2Latch = new CountDownLatch(1);
        Observable<Boolean> originalObservable = original.toObservable();
        Observable<Boolean> fromCache1Observable = fromCache1.toObservable();
        Observable<Boolean> fromCache2Observable = fromCache2.toObservable();
        Subscription originalSubscription = originalObservable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original Unsubscribe"));
                originalLatch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnCompleted"));
                originalLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnError : ") + e));
                originalLatch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.Original OnNext : ") + b));
            }
        });
        Subscription fromCache1Subscription = fromCache1Observable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 Unsubscribe"));
                fromCache1Latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 OnCompleted"));
                fromCache1Latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 OnError : ") + e));
                fromCache1Latch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache1 OnNext : ") + b));
            }
        });
        Subscription fromCache2Subscription = fromCache2Observable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 Unsubscribe"));
                fromCache2Latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 OnCompleted"));
                fromCache2Latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 OnError : ") + e));
                fromCache2Latch.countDown();
            }

            @Override
            public void onNext(Boolean b) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " Test.FromCache2 OnNext : ") + b));
            }
        });
        try {
            Thread.sleep(10);
            originalSubscription.unsubscribe();
            fromCache1Subscription.unsubscribe();
            fromCache2Subscription.unsubscribe();
            Assert.assertTrue(originalLatch.await(200, TimeUnit.MILLISECONDS));
            Assert.assertTrue(fromCache1Latch.await(200, TimeUnit.MILLISECONDS));
            Assert.assertTrue(fromCache2Latch.await(200, TimeUnit.MILLISECONDS));
            System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
            Assert.assertEquals("Number of execution semaphores in use (original)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (original)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertTrue(isExecutedInThread());
            System.out.println(("FEE : " + (getFailedExecutionException())));
            if ((getFailedExecutionException()) != null) {
                getFailedExecutionException().printStackTrace();
            }
            Assert.assertNull(getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) > (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            assertCommandExecutionEvents(original, CANCELLED);
            // assertTrue(original.isCancelled());   //underlying work  This doesn't work yet
            Assert.assertEquals(0, original.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertEquals("Number of execution semaphores in use (fromCache1)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (fromCache1)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            assertCommandExecutionEvents(fromCache1, RESPONSE_FROM_CACHE, CANCELLED);
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            Assert.assertEquals(0, fromCache1.metrics.getCurrentConcurrentExecutionCount());
            Assert.assertEquals("Number of execution semaphores in use (fromCache2)", 0, getExecutionSemaphore().getNumberOfPermitsUsed());
            Assert.assertEquals("Number of fallback semaphores in use (fromCache2)", 0, getFallbackSemaphore().getNumberOfPermitsUsed());
            Assert.assertFalse(isExecutionComplete());
            Assert.assertFalse(isExecutedInThread());
            Assert.assertEquals(null, getFailedExecutionException());
            Assert.assertNull(getExecutionException());
            assertCommandExecutionEvents(fromCache2, RESPONSE_FROM_CACHE, CANCELLED);
            Assert.assertTrue(((getExecutionTimeInMilliseconds()) == (-1)));
            Assert.assertFalse(isSuccessfulExecution());
            Assert.assertEquals(0, fromCache2.metrics.getCurrentConcurrentExecutionCount());
            assertSaneHystrixRequestLog(3);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Some RxJava operators like take(n), zip receive data in an onNext from upstream and immediately unsubscribe.
     * When upstream is a HystrixCommand, Hystrix may get that unsubscribe before it gets to its onCompleted.
     * This should still be marked as a HystrixEventType.SUCCESS.
     */
    @Test
    public void testUnsubscribingDownstreamOperatorStillResultsInSuccessEventType() throws InterruptedException {
        HystrixCommand<Integer> cmd = getCommand(THREAD, SUCCESS, 100, UNIMPLEMENTED);
        Observable<Integer> o = cmd.toObservable().doOnNext(new rx.functions.Action1<Integer>() {
            @Override
            public void call(Integer i) {
                System.out.println((((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " CMD OnNext : ") + i));
            }
        }).doOnError(new rx.functions.Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println((((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " CMD OnError : ") + throwable));
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " CMD OnCompleted"));
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " CMD OnSubscribe"));
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " CMD OnUnsubscribe"));
            }
        }).take(1).observeOn(Schedulers.io()).map(new rx.functions.Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer i) {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " : Doing some more computation in the onNext!!"));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return i;
            }
        });
        final CountDownLatch latch = new CountDownLatch(1);
        o.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " : OnSubscribe"));
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " : OnUnsubscribe"));
            }
        }).subscribe(new rx.Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " : OnCompleted"));
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " : OnError : ") + e));
                latch.countDown();
            }

            @Override
            public void onNext(Integer i) {
                System.out.println((((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " : OnNext : ") + i));
            }
        });
        latch.await(1000, TimeUnit.MILLISECONDS);
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertTrue(cmd.isExecutedInThread());
        assertCommandExecutionEvents(cmd, SUCCESS);
    }

    @Test
    public void testUnsubscribeBeforeSubscribe() throws Exception {
        // this may happen in Observable chain, so Hystrix should make sure that command never executes/allocates in this situation
        Observable<String> error = Observable.error(new RuntimeException("foo"));
        HystrixCommand<Integer> cmd = getCommand(THREAD, SUCCESS, 100);
        Observable<Integer> cmdResult = cmd.toObservable().doOnNext(new rx.functions.Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnNext : ") + integer));
            }
        }).doOnError(new rx.functions.Action1<Throwable>() {
            @Override
            public void call(Throwable ex) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnError : ") + ex));
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnCompleted"));
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnSubscribe"));
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnUnsubscribe"));
            }
        });
        // the zip operator will subscribe to each observable.  there is a race between the error of the first
        // zipped observable terminating the zip and the subscription to the command's observable
        Observable<String> zipped = Observable.zip(error, cmdResult, new rx.functions.Func2<String, Integer, String>() {
            @Override
            public String call(String s, Integer integer) {
                return s + integer;
            }
        });
        final CountDownLatch latch = new CountDownLatch(1);
        zipped.subscribe(new rx.Subscriber<String>() {
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
            public void onNext(String s) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " OnNext : ") + s));
            }
        });
        latch.await(1000, TimeUnit.MILLISECONDS);
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
    }

    @Test
    public void testRxRetry() throws Exception {
        // see https://github.com/Netflix/Hystrix/issues/1100
        // Since each command instance is single-use, the expectation is that applying the .retry() operator
        // results in only a single execution and propagation out of that error
        HystrixCommand<Integer> cmd = getLatentCommand(THREAD, FAILURE, 300, UNIMPLEMENTED, 100);
        final CountDownLatch latch = new CountDownLatch(1);
        System.out.println(((System.currentTimeMillis()) + " : Starting"));
        Observable<Integer> o = cmd.toObservable().retry(2);
        System.out.println((((System.currentTimeMillis()) + " Created retried command : ") + o));
        o.subscribe(new rx.Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnCompleted"));
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnError : ") + e));
                latch.countDown();
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnNext : ") + integer));
            }
        });
        latch.await(1000, TimeUnit.MILLISECONDS);
        System.out.println((((System.currentTimeMillis()) + " ReqLog : ") + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
    }

    /**
     * ********************** THREAD-ISOLATED Execution Hook Tests **************************************
     */
    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: NO
     * Execution Result: SUCCESS
     */
    @Test
    public void testExecutionHookThreadSuccess() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, SUCCESS);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(1, 0, 1));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionEmit - !onRunSuccess - !onComplete - onEmit - onExecutionSuccess - onThreadComplete - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    @Test
    public void testExecutionHookEarlyUnsubscribe() {
        System.out.println("Running command.observe(), awaiting terminal state of Observable, then running assertions...");
        final CountDownLatch latch = new CountDownLatch(1);
        TestHystrixCommand<Integer> command = getCommand(THREAD, SUCCESS, 1000);
        Observable<Integer> o = command.observe();
        Subscription s = o.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnUnsubscribe"));
                latch.countDown();
            }
        }).subscribe(new rx.Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnCompleted"));
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnError : ") + e));
                latch.countDown();
            }

            @Override
            public void onNext(Integer i) {
                System.out.println((((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " : OnNext : ") + i));
            }
        });
        try {
            Thread.sleep(15);
            s.unsubscribe();
            latch.await(3, TimeUnit.SECONDS);
            TestableExecutionHook hook = command.getBuilder().executionHook;
            Assert.assertTrue(hook.commandEmissionsMatch(0, 0, 0));
            Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
            Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
            Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onUnsubscribe - onThreadComplete - ", hook.executionSequence.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: NO
     * Execution Result: synchronous HystrixBadRequestException
     */
    @Test
    public void testExecutionHookThreadBadRequestException() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, BAD_REQUEST);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(HystrixBadRequestException.class, hook.getCommandException().getClass());
                Assert.assertEquals(HystrixBadRequestException.class, hook.getExecutionException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: NO
     * Execution Result: synchronous HystrixRuntimeException
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadExceptionNoFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, FAILURE, 0, UNIMPLEMENTED);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: NO
     * Execution Result: synchronous HystrixRuntimeException
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadExceptionSuccessfulFallback() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, FAILURE, 0, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: NO
     * Execution Result: synchronous HystrixRuntimeException
     * Fallback: HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadExceptionUnsuccessfulFallback() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, FAILURE, 0, AbstractTestHystrixCommand.FallbackResult.FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onThreadComplete - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: YES
     * Execution Result: SUCCESS (but timeout prior)
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadTimeoutNoFallbackRunSuccess() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, SUCCESS, 500, UNIMPLEMENTED, 200);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(TimeoutException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                System.out.println(("RequestLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onThreadComplete - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: YES
     * Execution Result: SUCCESS (but timeout prior)
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadTimeoutSuccessfulFallbackRunSuccess() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 200);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                System.out.println(("RequestLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onThreadComplete - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: YES
     * Execution Result: SUCCESS (but timeout prior)
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadTimeoutUnsuccessfulFallbackRunSuccess() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, 200);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(TimeoutException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onThreadComplete - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: YES
     * Execution Result: HystrixRuntimeException (but timeout prior)
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadTimeoutNoFallbackRunFailure() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, FAILURE, 500, UNIMPLEMENTED, 200);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(TimeoutException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onThreadComplete - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: YES
     * Execution Result: HystrixRuntimeException (but timeout prior)
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadTimeoutSuccessfulFallbackRunFailure() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, FAILURE, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, 200);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onThreadComplete - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : NO
     * Thread Pool Queue full?: NO
     * Timeout: YES
     * Execution Result: HystrixRuntimeException (but timeout prior)
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadTimeoutUnsuccessfulFallbackRunFailure() {
        assertHooksOnFailure(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(THREAD, FAILURE, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, 200);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(TimeoutException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onThreadStart - !onRunStart - onExecutionStart - onThreadComplete - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : YES
     * Thread Pool Queue full?: YES
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadPoolQueueFullNoFallback() {
        assertHooksOnFailFast(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
                HystrixThreadPool pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(1);
                try {
                    // fill the pool
                    getLatentCommand(THREAD, SUCCESS, 500, UNIMPLEMENTED, circuitBreaker, pool, 600).observe();
                    // fill the queue
                    getLatentCommand(THREAD, SUCCESS, 500, UNIMPLEMENTED, circuitBreaker, pool, 600).observe();
                } catch (Exception e) {
                    // ignore
                }
                return getLatentCommand(THREAD, SUCCESS, 500, UNIMPLEMENTED, circuitBreaker, pool, 600);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RejectedExecutionException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : YES
     * Thread Pool Queue full?: YES
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadPoolQueueFullSuccessfulFallback() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
                HystrixThreadPool pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(1);
                try {
                    // fill the pool
                    getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker, pool, 600).observe();
                    // fill the queue
                    getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker, pool, 600).observe();
                } catch (Exception e) {
                    // ignore
                }
                return getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker, pool, 600);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals("onStart - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : YES
     * Thread Pool Queue full?: YES
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadPoolQueueFullUnsuccessfulFallback() {
        assertHooksOnFailFast(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
                HystrixThreadPool pool = new CommonHystrixCommandTests.SingleThreadedPoolWithQueue(1);
                try {
                    // fill the pool
                    getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, circuitBreaker, pool, 600).observe();
                    // fill the queue
                    getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, circuitBreaker, pool, 600).observe();
                } catch (Exception e) {
                    // ignore
                }
                return getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, circuitBreaker, pool, 600);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RejectedExecutionException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : YES
     * Thread Pool Queue full?: N/A
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadPoolFullNoFallback() {
        assertHooksOnFailFast(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
                HystrixThreadPool pool = new CommonHystrixCommandTests.SingleThreadedPoolWithNoQueue();
                try {
                    // fill the pool
                    getLatentCommand(THREAD, SUCCESS, 500, UNIMPLEMENTED, circuitBreaker, pool, 600).observe();
                } catch (Exception e) {
                    // ignore
                }
                return getLatentCommand(THREAD, SUCCESS, 500, UNIMPLEMENTED, circuitBreaker, pool, 600);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RejectedExecutionException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : YES
     * Thread Pool Queue full?: N/A
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadPoolFullSuccessfulFallback() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
                HystrixThreadPool pool = new CommonHystrixCommandTests.SingleThreadedPoolWithNoQueue();
                try {
                    // fill the pool
                    getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker, pool, 600).observe();
                } catch (Exception e) {
                    // ignore
                }
                return getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, circuitBreaker, pool, 600);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertEquals("onStart - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: THREAD
     * Thread Pool full? : YES
     * Thread Pool Queue full?: N/A
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadPoolFullUnsuccessfulFallback() {
        assertHooksOnFailFast(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker();
                HystrixThreadPool pool = new CommonHystrixCommandTests.SingleThreadedPoolWithNoQueue();
                try {
                    // fill the pool
                    getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, circuitBreaker, pool, 600).observe();
                } catch (Exception e) {
                    // ignore
                }
                return getLatentCommand(THREAD, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, circuitBreaker, pool, 600);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RejectedExecutionException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : YES
     * Thread/semaphore: THREAD
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookThreadShortCircuitNoFallback() {
        assertHooksOnFailFast(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCircuitOpenCommand(THREAD, UNIMPLEMENTED);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : YES
     * Thread/semaphore: THREAD
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookThreadShortCircuitSuccessfulFallback() {
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCircuitOpenCommand(THREAD, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals("onStart - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : YES
     * Thread/semaphore: THREAD
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookThreadShortCircuitUnsuccessfulFallback() {
        assertHooksOnFailFast(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker = new HystrixCircuitBreakerTest.TestCircuitBreaker().setForceShortCircuit(true);
                return getCircuitOpenCommand(THREAD, AbstractTestHystrixCommand.FallbackResult.FAILURE);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Request-cache? : YES
     */
    @Test
    public void testExecutionHookResponseFromCache() {
        final HystrixCommandKey key = HystrixCommandKey.Factory.asKey("Hook-Cache");
        getCommand(key, THREAD, SUCCESS, 0, UNIMPLEMENTED, 0, new HystrixCircuitBreakerTest.TestCircuitBreaker(), null, 100, YES, 42, 10, 10).observe();
        assertHooksOnSuccess(new rx.functions.Func0<TestHystrixCommand<Integer>>() {
            @Override
            public TestHystrixCommand<Integer> call() {
                return getCommand(key, THREAD, SUCCESS, 0, UNIMPLEMENTED, 0, new HystrixCircuitBreakerTest.TestCircuitBreaker(), null, 100, YES, 42, 10, 10);
            }
        }, new rx.functions.Action1<TestHystrixCommand<Integer>>() {
            @Override
            public void call(TestHystrixCommand<Integer> command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 0, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals("onCacheHit - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * ********************** END THREAD-ISOLATED Execution Hook Tests **************************************
     */
    /* ******************************************************************************** */
    /* ******************************************************************************** */
    /* private HystrixCommand class implementations for unit testing */
    /* ******************************************************************************** */
    /* ******************************************************************************** */
    static AtomicInteger uniqueNameCounter = new AtomicInteger(1);

    private static class FlexibleTestHystrixCommand {
        public static Integer EXECUTE_VALUE = 1;

        public static Integer FALLBACK_VALUE = 11;

        public static HystrixCommandTest.AbstractFlexibleTestHystrixCommand from(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, AbstractTestHystrixCommand.FallbackResult fallbackResult, int fallbackLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            if (fallbackResult.equals(UNIMPLEMENTED)) {
                return new HystrixCommandTest.FlexibleTestHystrixCommandNoFallback(commandKey, isolationStrategy, executionResult, executionLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
            } else {
                return new HystrixCommandTest.FlexibleTestHystrixCommandWithFallback(commandKey, isolationStrategy, executionResult, executionLatency, fallbackResult, fallbackLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
            }
        }
    }

    private static class AbstractFlexibleTestHystrixCommand extends TestHystrixCommand<Integer> {
        protected final AbstractTestHystrixCommand.ExecutionResult executionResult;

        protected final int executionLatency;

        protected final AbstractTestHystrixCommand.CacheEnabled cacheEnabled;

        protected final Object value;

        AbstractFlexibleTestHystrixCommand(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            super(TestHystrixCommand.testPropsBuilder(circuitBreaker).setCommandKey(commandKey).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setThreadPool(threadPool).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationStrategy).withExecutionTimeoutInMilliseconds(timeout).withCircuitBreakerEnabled((!circuitBreakerDisabled))).setExecutionSemaphore(executionSemaphore).setFallbackSemaphore(fallbackSemaphore));
            this.executionResult = executionResult;
            this.executionLatency = executionLatency;
            this.cacheEnabled = cacheEnabled;
            this.value = value;
        }

        @Override
        protected Integer run() throws Exception {
            System.out.println(((((System.currentTimeMillis()) + " : ") + (Thread.currentThread().getName())) + " starting the run() method"));
            addLatency(executionLatency);
            if ((executionResult) == (SUCCESS)) {
                return HystrixCommandTest.FlexibleTestHystrixCommand.EXECUTE_VALUE;
            } else
                if ((executionResult) == (FAILURE)) {
                    throw new RuntimeException("Execution Failure for TestHystrixCommand");
                } else
                    if ((executionResult) == (NOT_WRAPPED_FAILURE)) {
                        throw new NotWrappedByHystrixTestRuntimeException();
                    } else
                        if ((executionResult) == (HYSTRIX_FAILURE)) {
                            throw new HystrixRuntimeException(FailureType.COMMAND_EXCEPTION, HystrixCommandTest.AbstractFlexibleTestHystrixCommand.class, "Execution Hystrix Failure for TestHystrixCommand", new RuntimeException("Execution Failure for TestHystrixCommand"), new RuntimeException("Fallback Failure for TestHystrixCommand"));
                        } else
                            if ((executionResult) == (RECOVERABLE_ERROR)) {
                                throw new Error("Execution ERROR for TestHystrixCommand");
                            } else
                                if ((executionResult) == (UNRECOVERABLE_ERROR)) {
                                    throw new StackOverflowError("Unrecoverable Error for TestHystrixCommand");
                                } else
                                    if ((executionResult) == (BAD_REQUEST)) {
                                        throw new HystrixBadRequestException("Execution BadRequestException for TestHystrixCommand");
                                    } else
                                        if ((executionResult) == (BAD_REQUEST_NOT_WRAPPED)) {
                                            throw new HystrixBadRequestException("Execution BadRequestException for TestHystrixCommand", new NotWrappedByHystrixTestRuntimeException());
                                        } else {
                                            throw new RuntimeException(("You passed in a executionResult enum that can't be represented in HystrixCommand: " + (executionResult)));
                                        }







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

    private static class FlexibleTestHystrixCommandWithFallback extends HystrixCommandTest.AbstractFlexibleTestHystrixCommand {
        protected final AbstractTestHystrixCommand.FallbackResult fallbackResult;

        protected final int fallbackLatency;

        FlexibleTestHystrixCommandWithFallback(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, AbstractTestHystrixCommand.FallbackResult fallbackResult, int fallbackLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            super(commandKey, isolationStrategy, executionResult, executionLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
            this.fallbackResult = fallbackResult;
            this.fallbackLatency = fallbackLatency;
        }

        @Override
        protected Integer getFallback() {
            addLatency(fallbackLatency);
            if ((fallbackResult) == (AbstractTestHystrixCommand.FallbackResult.SUCCESS)) {
                return HystrixCommandTest.FlexibleTestHystrixCommand.FALLBACK_VALUE;
            } else
                if ((fallbackResult) == (AbstractTestHystrixCommand.FallbackResult.FAILURE)) {
                    throw new RuntimeException("Fallback Failure for TestHystrixCommand");
                } else
                    if ((fallbackResult) == (UNIMPLEMENTED)) {
                        return super.getFallback();
                    } else {
                        throw new RuntimeException(("You passed in a fallbackResult enum that can't be represented in HystrixCommand: " + (fallbackResult)));
                    }


        }
    }

    private static class FlexibleTestHystrixCommandNoFallback extends HystrixCommandTest.AbstractFlexibleTestHystrixCommand {
        FlexibleTestHystrixCommandNoFallback(HystrixCommandKey commandKey, ExecutionIsolationStrategy isolationStrategy, AbstractTestHystrixCommand.ExecutionResult executionResult, int executionLatency, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int timeout, AbstractTestHystrixCommand.CacheEnabled cacheEnabled, Object value, TryableSemaphore executionSemaphore, TryableSemaphore fallbackSemaphore, boolean circuitBreakerDisabled) {
            super(commandKey, isolationStrategy, executionResult, executionLatency, circuitBreaker, threadPool, timeout, cacheEnabled, value, executionSemaphore, fallbackSemaphore, circuitBreakerDisabled);
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class SuccessfulTestCommand extends TestHystrixCommand<Boolean> {
        public SuccessfulTestCommand() {
            this(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter());
        }

        public SuccessfulTestCommand(HystrixCommandProperties.Setter properties) {
            super(TestHystrixCommand.testPropsBuilder().setCommandPropertiesDefaults(properties));
        }

        @Override
        protected Boolean run() {
            return true;
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class DynamicOwnerTestCommand extends TestHystrixCommand<Boolean> {
        public DynamicOwnerTestCommand(HystrixCommandGroupKey owner) {
            super(TestHystrixCommand.testPropsBuilder().setOwner(owner));
        }

        @Override
        protected Boolean run() {
            System.out.println("successfully executed");
            return true;
        }
    }

    /**
     * Successful execution - no fallback implementation.
     */
    private static class DynamicOwnerAndKeyTestCommand extends TestHystrixCommand<Boolean> {
        public DynamicOwnerAndKeyTestCommand(HystrixCommandGroupKey owner, HystrixCommandKey key) {
            super(TestHystrixCommand.testPropsBuilder().setOwner(owner).setCommandKey(key).setCircuitBreaker(null).setMetrics(null));
            // we specifically are NOT passing in a circuit breaker here so we test that it creates a new one correctly based on the dynamic key
        }

        @Override
        protected Boolean run() {
            System.out.println("successfully executed");
            return true;
        }
    }

    /**
     * Failed execution with known exception (HystrixException) - no fallback implementation.
     */
    private static class KnownFailureTestCommandWithoutFallback extends TestHystrixCommand<Boolean> {
        private KnownFailureTestCommandWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
        }

        @Override
        protected Boolean run() {
            System.out.println(("*** simulated failed execution *** ==> " + (Thread.currentThread())));
            throw new RuntimeException("we failed with a simulated issue");
        }
    }

    /**
     * Failed execution - fallback implementation successfully returns value.
     */
    private static class KnownFailureTestCommandWithFallback extends TestHystrixCommand<Boolean> {
        public KnownFailureTestCommandWithFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
        }

        public KnownFailureTestCommandWithFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean fallbackEnabled) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withFallbackEnabled(fallbackEnabled)));
        }

        @Override
        protected Boolean run() {
            System.out.println("*** simulated failed execution ***");
            throw new RuntimeException("we failed with a simulated issue");
        }

        @Override
        protected Boolean getFallback() {
            return false;
        }
    }

    /**
     * A Command implementation that supports caching.
     */
    private static class SuccessfulCacheableCommand<T> extends TestHystrixCommand<T> {
        private final boolean cacheEnabled;

        private volatile boolean executed = false;

        private final T value;

        public SuccessfulCacheableCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean cacheEnabled, T value) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.value = value;
            this.cacheEnabled = cacheEnabled;
        }

        @Override
        protected T run() {
            executed = true;
            System.out.println("successfully executed");
            return value;
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
    private static class SuccessfulCacheableCommandViaSemaphore extends TestHystrixCommand<String> {
        private final boolean cacheEnabled;

        private volatile boolean executed = false;

        private final String value;

        public SuccessfulCacheableCommandViaSemaphore(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean cacheEnabled, String value) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE)));
            this.value = value;
            this.cacheEnabled = cacheEnabled;
        }

        @Override
        protected String run() {
            executed = true;
            System.out.println("successfully executed");
            return value;
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
    private static class SlowCacheableCommand extends TestHystrixCommand<String> {
        private final String value;

        private final int duration;

        private volatile boolean executed = false;

        public SlowCacheableCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, String value, int duration) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
            this.value = value;
            this.duration = duration;
        }

        @Override
        protected String run() {
            executed = true;
            try {
                Thread.sleep(duration);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("successfully executed");
            return value;
        }

        @Override
        public String getCacheKey() {
            return value;
        }
    }

    /**
     * This has a ThreadPool that has a single thread and queueSize of 1.
     */
    private static class TestCommandRejection extends TestHystrixCommand<Boolean> {
        private static final int FALLBACK_NOT_IMPLEMENTED = 1;

        private static final int FALLBACK_SUCCESS = 2;

        private static final int FALLBACK_FAILURE = 3;

        private final int fallbackBehavior;

        private final int sleepTime;

        private TestCommandRejection(HystrixCommandKey key, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int sleepTime, int timeout, int fallbackBehavior) {
            super(TestHystrixCommand.testPropsBuilder().setCommandKey(key).setThreadPool(threadPool).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(timeout)));
            this.fallbackBehavior = fallbackBehavior;
            this.sleepTime = sleepTime;
        }

        @Override
        protected Boolean run() {
            System.out.println(">>> TestCommandRejection running");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected Boolean getFallback() {
            if ((fallbackBehavior) == (HystrixCommandTest.TestCommandRejection.FALLBACK_SUCCESS)) {
                return false;
            } else
                if ((fallbackBehavior) == (HystrixCommandTest.TestCommandRejection.FALLBACK_FAILURE)) {
                    throw new RuntimeException("failed on fallback");
                } else {
                    // FALLBACK_NOT_IMPLEMENTED
                    return super.getFallback();
                }

        }
    }

    /**
     * Command that receives a custom thread-pool, sleepTime, timeout
     */
    private static class CommandWithCustomThreadPool extends TestHystrixCommand<Boolean> {
        public boolean didExecute = false;

        private final int sleepTime;

        private CommandWithCustomThreadPool(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, HystrixThreadPool threadPool, int sleepTime, HystrixCommandProperties.Setter properties) {
            super(TestHystrixCommand.testPropsBuilder().setThreadPool(threadPool).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(properties));
            this.sleepTime = sleepTime;
        }

        @Override
        protected Boolean run() {
            System.out.println(("**** Executing CommandWithCustomThreadPool. Execution => " + (sleepTime)));
            didExecute = true;
            try {
                Thread.sleep(sleepTime);
                System.out.println("Woke up");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * The run() will fail and getFallback() take a long time.
     */
    private static class TestSemaphoreCommandWithSlowFallback extends TestHystrixCommand<Boolean> {
        private final long fallbackSleep;

        private TestSemaphoreCommandWithSlowFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, int fallbackSemaphoreExecutionCount, long fallbackSleep) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackSemaphoreExecutionCount).withExecutionIsolationThreadInterruptOnTimeout(false)));
            this.fallbackSleep = fallbackSleep;
        }

        @Override
        protected Boolean run() {
            throw new RuntimeException("run fails");
        }

        @Override
        protected Boolean getFallback() {
            try {
                Thread.sleep(fallbackSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private static class NoRequestCacheTimeoutWithoutFallback extends TestHystrixCommand<Boolean> {
        public NoRequestCacheTimeoutWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(200).withCircuitBreakerEnabled(false)));
            // we want it to timeout
        }

        @Override
        protected Boolean run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println((">>>> Sleep Interrupted: " + (e.getMessage())));
                // e.printStackTrace();
            }
            return true;
        }

        @Override
        public String getCacheKey() {
            return null;
        }
    }

    /**
     * The run() will take time. Configurable fallback implementation.
     */
    private static class TestSemaphoreCommand extends TestHystrixCommand<Boolean> {
        private final long executionSleep;

        private static final int RESULT_SUCCESS = 1;

        private static final int RESULT_FAILURE = 2;

        private static final int RESULT_BAD_REQUEST_EXCEPTION = 3;

        private final int resultBehavior;

        private static final int FALLBACK_SUCCESS = 10;

        private static final int FALLBACK_NOT_IMPLEMENTED = 11;

        private static final int FALLBACK_FAILURE = 12;

        private final int fallbackBehavior;

        private TestSemaphoreCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, int executionSemaphoreCount, long executionSleep, int resultBehavior, int fallbackBehavior) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionIsolationSemaphoreMaxConcurrentRequests(executionSemaphoreCount)));
            this.executionSleep = executionSleep;
            this.resultBehavior = resultBehavior;
            this.fallbackBehavior = fallbackBehavior;
        }

        private TestSemaphoreCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, TryableSemaphore semaphore, long executionSleep, int resultBehavior, int fallbackBehavior) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE)).setExecutionSemaphore(semaphore));
            this.executionSleep = executionSleep;
            this.resultBehavior = resultBehavior;
            this.fallbackBehavior = fallbackBehavior;
        }

        @Override
        protected Boolean run() {
            try {
                Thread.sleep(executionSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ((resultBehavior) == (HystrixCommandTest.TestSemaphoreCommand.RESULT_SUCCESS)) {
                return true;
            } else
                if ((resultBehavior) == (HystrixCommandTest.TestSemaphoreCommand.RESULT_FAILURE)) {
                    throw new RuntimeException("TestSemaphoreCommand failure");
                } else
                    if ((resultBehavior) == (HystrixCommandTest.TestSemaphoreCommand.RESULT_BAD_REQUEST_EXCEPTION)) {
                        throw new HystrixBadRequestException("TestSemaphoreCommand BadRequestException");
                    } else {
                        throw new IllegalStateException("Didn't use a proper enum for result behavior");
                    }


        }

        @Override
        protected Boolean getFallback() {
            if ((fallbackBehavior) == (HystrixCommandTest.TestSemaphoreCommand.FALLBACK_SUCCESS)) {
                return false;
            } else
                if ((fallbackBehavior) == (HystrixCommandTest.TestSemaphoreCommand.FALLBACK_FAILURE)) {
                    throw new RuntimeException("fallback failure");
                } else {
                    // FALLBACK_NOT_IMPLEMENTED
                    return super.getFallback();
                }

        }
    }

    /**
     * Semaphore based command that allows caller to use latches to know when it has started and signal when it
     * would like the command to finish
     */
    private static class LatchedSemaphoreCommand extends TestHystrixCommand<Boolean> {
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
         * 		this command calls {@link java.util.concurrent.CountDownLatch#countDown()} immediately
         * 		upon running
         * @param waitLatch
         * 		this command calls {@link java.util.concurrent.CountDownLatch#await()} once it starts
         * 		to run. The caller can use the latch to signal the command to finish
         */
        private LatchedSemaphoreCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, TryableSemaphore semaphore, CountDownLatch startLatch, CountDownLatch waitLatch) {
            this("Latched", circuitBreaker, semaphore, startLatch, waitLatch);
        }

        private LatchedSemaphoreCommand(String commandName, HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, TryableSemaphore semaphore, CountDownLatch startLatch, CountDownLatch waitLatch) {
            super(TestHystrixCommand.testPropsBuilder().setCommandKey(HystrixCommandKey.Factory.asKey(commandName)).setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withCircuitBreakerEnabled(false)).setExecutionSemaphore(semaphore));
            this.startLatch = startLatch;
            this.waitLatch = waitLatch;
        }

        @Override
        protected Boolean run() {
            // signals caller that run has started
            this.startLatch.countDown();
            try {
                // waits for caller to countDown latch
                this.waitLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    /**
     * The run() will take time. Contains fallback.
     */
    private static class TestSemaphoreCommandWithFallback extends TestHystrixCommand<Boolean> {
        private final long executionSleep;

        private final Boolean fallback;

        private TestSemaphoreCommandWithFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, int executionSemaphoreCount, long executionSleep, Boolean fallback) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(SEMAPHORE).withExecutionIsolationSemaphoreMaxConcurrentRequests(executionSemaphoreCount)));
            this.executionSleep = executionSleep;
            this.fallback = fallback;
        }

        @Override
        protected Boolean run() {
            try {
                Thread.sleep(executionSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected Boolean getFallback() {
            return fallback;
        }
    }

    private static class RequestCacheNullPointerExceptionCase extends TestHystrixCommand<Boolean> {
        public RequestCacheNullPointerExceptionCase(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(200)));
            // we want it to timeout
        }

        @Override
        protected Boolean run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected Boolean getFallback() {
            return false;
        }

        @Override
        public String getCacheKey() {
            return "A";
        }
    }

    private static class RequestCacheTimeoutWithoutFallback extends TestHystrixCommand<Boolean> {
        public RequestCacheTimeoutWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(200).withCircuitBreakerEnabled(false)));
            // we want it to timeout
        }

        @Override
        protected Boolean run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println((">>>> Sleep Interrupted: " + (e.getMessage())));
                // e.printStackTrace();
            }
            return true;
        }

        @Override
        public String getCacheKey() {
            return "A";
        }
    }

    private static class RequestCacheThreadRejectionWithoutFallback extends TestHystrixCommand<Boolean> {
        final CountDownLatch completionLatch;

        public RequestCacheThreadRejectionWithoutFallback(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, CountDownLatch completionLatch) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setThreadPool(new HystrixThreadPool() {
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
        protected Boolean run() {
            try {
                if (completionLatch.await(1000, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("timed out waiting on completionLatch");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        @Override
        public String getCacheKey() {
            return "A";
        }
    }

    private static class BadRequestCommand extends TestHystrixCommand<Boolean> {
        public BadRequestCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, ExecutionIsolationStrategy isolationType) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationType)).setMetrics(circuitBreaker.metrics));
        }

        @Override
        protected Boolean run() {
            throw new HystrixBadRequestException("Message to developer that they passed in bad data or something like that.");
        }

        @Override
        protected Boolean getFallback() {
            return false;
        }

        @Override
        protected String getCacheKey() {
            return "one";
        }
    }

    private static class AsyncCacheableCommand extends HystrixCommand<Boolean> {
        private final String arg;

        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        public AsyncCacheableCommand(String arg) {
            super(Setter.withGroupKey(Factory.asKey("ASYNC")));
            this.arg = arg;
        }

        @Override
        protected Boolean run() {
            try {
                Thread.sleep(500);
                return true;
            } catch (InterruptedException ex) {
                cancelled.set(true);
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected String getCacheKey() {
            return arg;
        }

        public boolean isCancelled() {
            return cancelled.get();
        }
    }

    private static class BusinessException extends Exception {
        public BusinessException(String msg) {
            super(msg);
        }
    }

    private static class ExceptionToBadRequestByExecutionHookCommand extends TestHystrixCommand<Boolean> {
        public ExceptionToBadRequestByExecutionHookCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, ExecutionIsolationStrategy isolationType) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationStrategy(isolationType)).setMetrics(circuitBreaker.metrics).setExecutionHook(new TestableExecutionHook() {
                @Override
                public <T> Exception onRunError(HystrixInvokable<T> commandInstance, Exception e) {
                    super.onRunError(commandInstance, e);
                    return new HystrixBadRequestException("autoconverted exception", e);
                }
            }));
        }

        @Override
        protected Boolean run() throws HystrixCommandTest.BusinessException {
            throw new HystrixCommandTest.BusinessException("invalid input by the user");
        }

        @Override
        protected String getCacheKey() {
            return "nein";
        }
    }

    private static class CommandWithCheckedException extends TestHystrixCommand<Boolean> {
        public CommandWithCheckedException(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
        }

        @Override
        protected Boolean run() throws Exception {
            throw new IOException("simulated checked exception message");
        }
    }

    private static class CommandWithNotWrappedByHystrixException extends TestHystrixCommand<Boolean> {
        public CommandWithNotWrappedByHystrixException(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics));
        }

        @Override
        protected Boolean run() throws Exception {
            throw new NotWrappedByHystrixTestException();
        }
    }

    private static class InterruptibleCommand extends TestHystrixCommand<Boolean> {
        public InterruptibleCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean shouldInterrupt, boolean shouldInterruptOnCancel, int timeoutInMillis) {
            super(TestHystrixCommand.testPropsBuilder().setCircuitBreaker(circuitBreaker).setMetrics(circuitBreaker.metrics).setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionIsolationThreadInterruptOnFutureCancel(shouldInterruptOnCancel).withExecutionIsolationThreadInterruptOnTimeout(shouldInterrupt).withExecutionTimeoutInMilliseconds(timeoutInMillis)));
        }

        public InterruptibleCommand(HystrixCircuitBreakerTest.TestCircuitBreaker circuitBreaker, boolean shouldInterrupt) {
            this(circuitBreaker, shouldInterrupt, false, 100);
        }

        private volatile boolean hasBeenInterrupted;

        public boolean hasBeenInterrupted() {
            return hasBeenInterrupted;
        }

        @Override
        protected Boolean run() throws Exception {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted!");
                e.printStackTrace();
                hasBeenInterrupted = true;
            }
            return hasBeenInterrupted;
        }
    }

    private static class CommandWithDisabledTimeout extends TestHystrixCommand<Boolean> {
        private final int latency;

        public CommandWithDisabledTimeout(int timeout, int latency) {
            super(TestHystrixCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(timeout).withExecutionTimeoutEnabled(false)));
            this.latency = latency;
        }

        @Override
        protected Boolean run() throws Exception {
            try {
                Thread.sleep(latency);
                return true;
            } catch (InterruptedException ex) {
                return false;
            }
        }

        @Override
        protected Boolean getFallback() {
            return false;
        }
    }
}

