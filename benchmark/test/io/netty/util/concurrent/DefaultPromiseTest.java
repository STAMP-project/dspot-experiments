/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;


import ImmediateEventExecutor.INSTANCE;
import io.netty.util.Signal;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ImmediateEventExecutor.INSTANCE;


@SuppressWarnings("unchecked")
public class DefaultPromiseTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromiseTest.class);

    private static int stackOverflowDepth;

    @Test
    public void testCancelDoesNotScheduleWhenNoListeners() {
        EventExecutor executor = Mockito.mock(EventExecutor.class);
        Mockito.when(executor.inEventLoop()).thenReturn(false);
        Promise<Void> promise = new DefaultPromise<Void>(executor);
        promise.cancel(false);
        Mockito.verify(executor, Mockito.never()).execute(Mockito.any(Runnable.class));
        Assert.assertTrue(promise.isCancelled());
    }

    @Test
    public void testSuccessDoesNotScheduleWhenNoListeners() {
        EventExecutor executor = Mockito.mock(EventExecutor.class);
        Mockito.when(executor.inEventLoop()).thenReturn(false);
        Object value = new Object();
        Promise<Object> promise = new DefaultPromise<Object>(executor);
        promise.setSuccess(value);
        Mockito.verify(executor, Mockito.never()).execute(Mockito.any(Runnable.class));
        Assert.assertSame(value, promise.getNow());
    }

    @Test
    public void testFailureDoesNotScheduleWhenNoListeners() {
        EventExecutor executor = Mockito.mock(EventExecutor.class);
        Mockito.when(executor.inEventLoop()).thenReturn(false);
        Exception cause = new Exception();
        Promise<Void> promise = new DefaultPromise<Void>(executor);
        promise.setFailure(cause);
        Mockito.verify(executor, Mockito.never()).execute(Mockito.any(Runnable.class));
        Assert.assertSame(cause, promise.cause());
    }

    @Test(expected = CancellationException.class)
    public void testCancellationExceptionIsThrownWhenBlockingGet() throws InterruptedException, ExecutionException {
        final Promise<Void> promise = new DefaultPromise<Void>(INSTANCE);
        promise.cancel(false);
        promise.get();
    }

    @Test(expected = CancellationException.class)
    public void testCancellationExceptionIsThrownWhenBlockingGetWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final Promise<Void> promise = new DefaultPromise<Void>(INSTANCE);
        promise.cancel(false);
        promise.get(1, TimeUnit.SECONDS);
    }

    @Test
    public void testStackOverflowWithImmediateEventExecutorA() throws Exception {
        DefaultPromiseTest.testStackOverFlowChainedFuturesA(DefaultPromiseTest.stackOverflowTestDepth(), INSTANCE, true);
        DefaultPromiseTest.testStackOverFlowChainedFuturesA(DefaultPromiseTest.stackOverflowTestDepth(), INSTANCE, false);
    }

    @Test
    public void testNoStackOverflowWithDefaultEventExecutorA() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            EventExecutor executor = new DefaultEventExecutor(executorService);
            try {
                DefaultPromiseTest.testStackOverFlowChainedFuturesA(DefaultPromiseTest.stackOverflowTestDepth(), executor, true);
                DefaultPromiseTest.testStackOverFlowChainedFuturesA(DefaultPromiseTest.stackOverflowTestDepth(), executor, false);
            } finally {
                executor.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
            }
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testNoStackOverflowWithImmediateEventExecutorB() throws Exception {
        DefaultPromiseTest.testStackOverFlowChainedFuturesB(DefaultPromiseTest.stackOverflowTestDepth(), INSTANCE, true);
        DefaultPromiseTest.testStackOverFlowChainedFuturesB(DefaultPromiseTest.stackOverflowTestDepth(), INSTANCE, false);
    }

    @Test
    public void testNoStackOverflowWithDefaultEventExecutorB() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            EventExecutor executor = new DefaultEventExecutor(executorService);
            try {
                DefaultPromiseTest.testStackOverFlowChainedFuturesB(DefaultPromiseTest.stackOverflowTestDepth(), executor, true);
                DefaultPromiseTest.testStackOverFlowChainedFuturesB(DefaultPromiseTest.stackOverflowTestDepth(), executor, false);
            } finally {
                executor.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
            }
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testListenerNotifyOrder() throws Exception {
        EventExecutor executor = new DefaultPromiseTest.TestEventExecutor();
        try {
            final BlockingQueue<FutureListener<Void>> listeners = new LinkedBlockingQueue<FutureListener<Void>>();
            int runs = 100000;
            for (int i = 0; i < runs; i++) {
                final Promise<Void> promise = new DefaultPromise<Void>(executor);
                final FutureListener<Void> listener1 = new FutureListener<Void>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        listeners.add(this);
                    }
                };
                final FutureListener<Void> listener2 = new FutureListener<Void>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        listeners.add(this);
                    }
                };
                final FutureListener<Void> listener4 = new FutureListener<Void>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        listeners.add(this);
                    }
                };
                final FutureListener<Void> listener3 = new FutureListener<Void>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        listeners.add(this);
                        future.addListener(listener4);
                    }
                };
                GlobalEventExecutor.INSTANCE.execute(new Runnable() {
                    @Override
                    public void run() {
                        promise.setSuccess(null);
                    }
                });
                promise.addListener(listener1).addListener(listener2).addListener(listener3);
                Assert.assertSame(((("Fail 1 during run " + i) + " / ") + runs), listener1, listeners.take());
                Assert.assertSame(((("Fail 2 during run " + i) + " / ") + runs), listener2, listeners.take());
                Assert.assertSame(((("Fail 3 during run " + i) + " / ") + runs), listener3, listeners.take());
                Assert.assertSame(((("Fail 4 during run " + i) + " / ") + runs), listener4, listeners.take());
                Assert.assertTrue(((("Fail during run " + i) + " / ") + runs), listeners.isEmpty());
            }
        } finally {
            executor.shutdownGracefully(0, 0, TimeUnit.SECONDS).sync();
        }
    }

    @Test
    public void testListenerNotifyLater() throws Exception {
        // Testing first execution path in DefaultPromise
        DefaultPromiseTest.testListenerNotifyLater(1);
        // Testing second execution path in DefaultPromise
        DefaultPromiseTest.testListenerNotifyLater(2);
    }

    @Test(timeout = 2000)
    public void testPromiseListenerAddWhenCompleteFailure() throws Exception {
        DefaultPromiseTest.testPromiseListenerAddWhenComplete(DefaultPromiseTest.fakeException());
    }

    @Test(timeout = 2000)
    public void testPromiseListenerAddWhenCompleteSuccess() throws Exception {
        DefaultPromiseTest.testPromiseListenerAddWhenComplete(null);
    }

    @Test(timeout = 2000)
    public void testLateListenerIsOrderedCorrectlySuccess() throws InterruptedException {
        DefaultPromiseTest.testLateListenerIsOrderedCorrectly(null);
    }

    @Test(timeout = 2000)
    public void testLateListenerIsOrderedCorrectlyFailure() throws InterruptedException {
        DefaultPromiseTest.testLateListenerIsOrderedCorrectly(DefaultPromiseTest.fakeException());
    }

    @Test
    public void testSignalRace() {
        final long wait = TimeUnit.NANOSECONDS.convert(10, TimeUnit.SECONDS);
        EventExecutor executor = null;
        try {
            executor = new DefaultPromiseTest.TestEventExecutor();
            final int numberOfAttempts = 4096;
            final Map<Thread, DefaultPromise<Void>> promises = new HashMap<Thread, DefaultPromise<Void>>();
            for (int i = 0; i < numberOfAttempts; i++) {
                final DefaultPromise<Void> promise = new DefaultPromise<Void>(executor);
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        promise.setSuccess(null);
                    }
                });
                promises.put(thread, promise);
            }
            for (final Map.Entry<Thread, DefaultPromise<Void>> promise : promises.entrySet()) {
                promise.getKey().start();
                final long start = System.nanoTime();
                promise.getValue().awaitUninterruptibly(wait, TimeUnit.NANOSECONDS);
                Assert.assertThat(((System.nanoTime()) - start), Matchers.lessThan(wait));
            }
        } finally {
            if (executor != null) {
                executor.shutdownGracefully();
            }
        }
    }

    @Test
    public void signalUncancellableCompletionValue() {
        final Promise<Signal> promise = new DefaultPromise<Signal>(INSTANCE);
        promise.setSuccess(Signal.valueOf(DefaultPromise.class, "UNCANCELLABLE"));
        Assert.assertTrue(promise.isDone());
        Assert.assertTrue(promise.isSuccess());
    }

    @Test
    public void signalSuccessCompletionValue() {
        final Promise<Signal> promise = new DefaultPromise<Signal>(INSTANCE);
        promise.setSuccess(Signal.valueOf(DefaultPromise.class, "SUCCESS"));
        Assert.assertTrue(promise.isDone());
        Assert.assertTrue(promise.isSuccess());
    }

    @Test
    public void setUncancellableGetNow() {
        final Promise<String> promise = new DefaultPromise<String>(INSTANCE);
        Assert.assertNull(promise.getNow());
        Assert.assertTrue(promise.setUncancellable());
        Assert.assertNull(promise.getNow());
        Assert.assertFalse(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        promise.setSuccess("success");
        Assert.assertTrue(promise.isDone());
        Assert.assertTrue(promise.isSuccess());
        Assert.assertEquals("success", promise.getNow());
    }

    private static final class TestEventExecutor extends SingleThreadEventExecutor {
        TestEventExecutor() {
            super(null, Executors.defaultThreadFactory(), true);
        }

        @Override
        protected void run() {
            for (; ;) {
                Runnable task = takeTask();
                if (task != null) {
                    task.run();
                    updateLastExecutionTime();
                }
                if (confirmShutdown()) {
                    break;
                }
            }
        }
    }
}

