/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.util;


import com.hazelcast.spi.impl.AbstractCompletableFuture;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionTimedOutException;
import com.hazelcast.util.FutureUtil.ExceptionHandler;
import com.hazelcast.util.executor.CompletedFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static FutureUtil.RETHROW_TRANSACTION_EXCEPTION;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FutureUtilTest extends HazelcastTestSupport {
    @Test
    public void test_waitWithDeadline_first_wait_second_finished() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(new FutureUtilTest.SimpleTask(waitLock)));
        }
        FutureUtil.waitWithDeadline(futures, 10, TimeUnit.SECONDS, FutureUtil.logAllExceptions(Level.WARNING));
    }

    @Test
    public void test_waitWithDeadline_first_finished_second_wait() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(new FutureUtilTest.SimpleTask(waitLock)));
        }
        FutureUtil.waitWithDeadline(futures, 10, TimeUnit.SECONDS, FutureUtil.logAllExceptions(Level.WARNING));
    }

    @Test
    public void test_returnWithDeadline_first_wait_second_finished() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(new FutureUtilTest.SimpleCallable(waitLock, (i + 1))));
        }
        Collection<Integer> result = FutureUtil.returnWithDeadline(futures, 10, TimeUnit.SECONDS, FutureUtil.logAllExceptions(Level.WARNING));
        Assert.assertEquals(2, result.size());
        Integer[] array = result.toArray(new Integer[0]);
        Assert.assertEquals(1, ((int) (array[0])));
        Assert.assertEquals(2, ((int) (array[1])));
    }

    @Test
    public void test_returnWithDeadline_first_finished_second_wait() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(new FutureUtilTest.SimpleCallable(waitLock, (i + 1))));
        }
        Collection<Integer> result = FutureUtil.returnWithDeadline(futures, 10, TimeUnit.SECONDS, FutureUtil.logAllExceptions(Level.WARNING));
        Assert.assertEquals(2, result.size());
        Integer[] array = result.toArray(new Integer[0]);
        Assert.assertEquals(1, ((int) (array[0])));
        Assert.assertEquals(2, ((int) (array[1])));
    }

    @Test(expected = TimeoutException.class)
    public void test_returnWithDeadline_timeout_exception() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
        for (int i = 0; i < 2; i++) {
            Future<Integer> submit = ((Future<Integer>) (executorService.submit(new FutureUtilTest.TimeoutingTask(waitLock))));
            futures.add(submit);
        }
        FutureUtil.returnWithDeadline(futures, 1, TimeUnit.SECONDS, new ExceptionHandler() {
            @Override
            public void handleException(Throwable throwable) {
                if (throwable instanceof TimeoutException) {
                    ExceptionUtil.sneakyThrow(throwable);
                }
                throw ExceptionUtil.rethrow(throwable);
            }
        });
    }

    @Test
    public void test_waitWithDeadline_failing_second() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(new FutureUtilTest.FailingCallable(waitLock)));
        }
        FutureUtilTest.ExceptionCollector exceptionHandler = new FutureUtilTest.ExceptionCollector();
        FutureUtil.waitWithDeadline(futures, 5, TimeUnit.SECONDS, exceptionHandler);
        Assert.assertEquals(1, exceptionHandler.throwables.size());
        Throwable throwable = exceptionHandler.throwables.iterator().next();
        Assert.assertTrue((throwable instanceof ExecutionException));
        Assert.assertTrue(((throwable.getCause()) instanceof FutureUtilTest.SpecialRuntimeException));
    }

    @Test
    public void test_returnWithDeadline_failing_second() {
        AtomicBoolean waitLock = new AtomicBoolean(true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(new FutureUtilTest.FailingCallable(waitLock)));
        }
        FutureUtilTest.ExceptionCollector exceptionHandler = new FutureUtilTest.ExceptionCollector();
        FutureUtil.returnWithDeadline(futures, 5, TimeUnit.SECONDS, exceptionHandler);
        Assert.assertEquals(1, exceptionHandler.throwables.size());
        Throwable throwable = exceptionHandler.throwables.iterator().next();
        Assert.assertTrue((throwable instanceof ExecutionException));
        Assert.assertTrue(((throwable.getCause()) instanceof FutureUtilTest.SpecialRuntimeException));
    }

    @Test(expected = TransactionTimedOutException.class)
    public void testTransactionTimedOutExceptionHandler() {
        final ExceptionHandler exceptionHandler = RETHROW_TRANSACTION_EXCEPTION;
        final Throwable throwable = new TimeoutException();
        exceptionHandler.handleException(throwable);
    }

    @Test
    public void testAllDone_whenAllFuturesCompleted() {
        Collection<Future> futures = Arrays.asList(((Future) (new CompletedFuture(null, null, null))));
        Assert.assertTrue(FutureUtil.allDone(futures));
        futures = Arrays.asList(((Future) (new FutureUtilTest.UncancellableFuture())));
        Assert.assertFalse(FutureUtil.allDone(futures));
    }

    @Test(expected = InterruptedException.class)
    public void testgetAllDoneThrowsException_whenSomeFutureHasException() throws Exception {
        InterruptedException exception = new InterruptedException();
        Collection<Future> futures = Arrays.asList(((Future) (new CompletedFuture(null, exception, null))));
        FutureUtil.checkAllDone(futures);
    }

    @Test
    public void testGetAllDone_whenSomeFuturesAreCompleted() {
        Future completedFuture = new CompletedFuture(null, null, null);
        Collection<Future> futures = Arrays.asList(new FutureUtilTest.UncancellableFuture(), completedFuture, new FutureUtilTest.UncancellableFuture());
        Assert.assertEquals(1, FutureUtil.getAllDone(futures).size());
        Assert.assertEquals(completedFuture, FutureUtil.getAllDone(futures).get(0));
    }

    private static final class ExceptionCollector implements ExceptionHandler {
        private final List<Throwable> throwables = new ArrayList<Throwable>();

        @Override
        public void handleException(Throwable throwable) {
            throwables.add(throwable);
        }
    }

    private static final class TimeoutingTask implements Runnable {
        private final AtomicBoolean waitLock;

        private TimeoutingTask(AtomicBoolean waitLock) {
            this.waitLock = waitLock;
        }

        @Override
        public void run() {
            if (waitLock.compareAndSet(true, false)) {
                HazelcastTestSupport.sleepSeconds(10);
            }
        }
    }

    private static final class SimpleTask implements Runnable {
        private final AtomicBoolean waitLock;

        private SimpleTask(AtomicBoolean waitLock) {
            this.waitLock = waitLock;
        }

        @Override
        public void run() {
            if (waitLock.compareAndSet(true, false)) {
                HazelcastTestSupport.sleepSeconds(1);
            }
        }
    }

    private static final class SimpleCallable implements Callable<Integer> {
        private final AtomicBoolean waitLock;

        private final int index;

        private SimpleCallable(AtomicBoolean waitLock, int index) {
            this.waitLock = waitLock;
            this.index = index;
        }

        @Override
        public Integer call() {
            if (waitLock.compareAndSet(true, false)) {
                HazelcastTestSupport.sleepSeconds(2);
            }
            return index;
        }
    }

    private static final class FailingCallable implements Callable<Integer> {
        private final AtomicBoolean waitLock;

        private FailingCallable(AtomicBoolean waitLock) {
            this.waitLock = waitLock;
        }

        @Override
        public Integer call() {
            if (waitLock.compareAndSet(true, false)) {
                HazelcastTestSupport.sleepSeconds(1);
                return 1;
            }
            throw new FutureUtilTest.SpecialRuntimeException("foo");
        }
    }

    private static class SpecialRuntimeException extends RuntimeException {
        private SpecialRuntimeException(String message) {
            super(message);
        }
    }

    private static class UncancellableFuture<V> extends AbstractCompletableFuture<V> {
        public UncancellableFuture() {
            super(((Executor) (null)), null);
        }

        @Override
        protected boolean shouldCancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public V get(long timeout, TimeUnit unit) {
            return null;
        }
    }
}

