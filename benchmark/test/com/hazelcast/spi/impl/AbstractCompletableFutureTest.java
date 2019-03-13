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
package com.hazelcast.spi.impl;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractCompletableFutureTest extends HazelcastTestSupport {
    private static final Object RESULT = "foobar";

    private static final String EXCEPTION_MESSAGE = "You screwed buddy!";

    private static final Exception EXCEPTION = new RuntimeException(AbstractCompletableFutureTest.EXCEPTION_MESSAGE);

    private ILogger logger;

    private NodeEngineImpl nodeEngine;

    private Executor executor;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void future_notExecuted_notDoneNotCancelled() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        Assert.assertFalse("New future should not be done", isDone());
        Assert.assertFalse("New future should not be cancelled", isCancelled());
    }

    @Test
    public void future_notExecuted_callbackRegistered_notDoneNotCancelled() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.andThen(Mockito.mock(ExecutionCallback.class));
        Assert.assertFalse("New future should not be done", isDone());
        Assert.assertFalse("New future should not be cancelled", isCancelled());
    }

    @Test
    public void future_ordinaryResultSet_doneNotCancelled() {
        future_resultSet_doneNotCancelled(AbstractCompletableFutureTest.RESULT);
    }

    @Test
    public void future_nullResultSet_doneNotCancelled() {
        future_resultSet_doneNotCancelled(null);
    }

    @Test
    public void future_exceptionResultSet_doneNotCancelled() {
        future_resultSet_doneNotCancelled(AbstractCompletableFutureTest.EXCEPTION);
    }

    @Test
    public void future_resultNotSet_cancelled() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        boolean cancelled = cancel(false);
        Assert.assertTrue(cancelled);
        Assert.assertTrue("Cancelled future should be done", isDone());
        Assert.assertTrue("Cancelled future should be cancelled", isCancelled());
    }

    @Test
    public void future_resultSetAndCancelled() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        setResult(AbstractCompletableFutureTest.RESULT);
        boolean cancelled = cancel(false);
        Assert.assertFalse(cancelled);
        Assert.assertTrue("Done future should be done", isDone());
        Assert.assertFalse("Done future should not be cancelled even if cancelled executed", isCancelled());
    }

    @Test
    public void future_cancelledAndResultSet() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        boolean cancelled = cancel(false);
        setResult(AbstractCompletableFutureTest.RESULT);
        Assert.assertTrue(cancelled);
        Assert.assertTrue("Cancelled future should be done", isDone());
        Assert.assertTrue("Cancelled future should be cancelled", isCancelled());
        Assert.assertNull("Internal result should be null", getResult());
    }

    @Test(expected = CancellationException.class)
    public void get_cancelledFuture_exceptionThrown() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.cancel(false);
        get();
    }

    @Test(expected = CancellationException.class)
    public void getWithTimeout_cancelledFuture_exceptionThrown() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.cancel(false);
        future.get(10, TimeUnit.MILLISECONDS);
    }

    @Test
    public void get_ordinaryResultSet_returnsResult() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        setResult(AbstractCompletableFutureTest.RESULT);
        Object result = future.get();
        Assert.assertSame(AbstractCompletableFutureTest.RESULT, result);
    }

    @Test
    public void getWithTimeout_ordinaryResultSet_returnsResult() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        setResult(AbstractCompletableFutureTest.RESULT);
        Object result = future.get(10, TimeUnit.MILLISECONDS);
        Assert.assertSame(AbstractCompletableFutureTest.RESULT, result);
    }

    @Test
    public void get_exceptionResultSet_exceptionThrown() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.setResult(AbstractCompletableFutureTest.EXCEPTION);
        expected.expect(AbstractCompletableFutureTest.EXCEPTION.getClass());
        expected.expectMessage(AbstractCompletableFutureTest.EXCEPTION_MESSAGE);
        get();
    }

    @Test
    public void getWithTimeout_exceptionResultSet_exceptionThrown_noTimeout() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.setResult(AbstractCompletableFutureTest.EXCEPTION);
        expected.expect(AbstractCompletableFutureTest.EXCEPTION.getClass());
        expected.expectMessage(AbstractCompletableFutureTest.EXCEPTION_MESSAGE);
        future.get(1, TimeUnit.NANOSECONDS);
    }

    @Test
    public void get_nullResultSet_returnsResult() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.setResult(null);
        Object result = future.get();
        Assert.assertNull(result);
    }

    @Test
    public void getWithTimeout_nullResultSet_returnsResult() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.setResult(null);
        Object result = future.get(10, TimeUnit.MILLISECONDS);
        Assert.assertNull(result);
    }

    @Test(expected = TimeoutException.class, timeout = 120000)
    public void getWithTimeout_resultNotSet_timesOut() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.get(10, TimeUnit.MILLISECONDS);
    }

    @Test(expected = TimeoutException.class, timeout = 60000)
    public void getWithTimeout_zeroTimeout_resultNotSet_timesOut() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.get(0, TimeUnit.MILLISECONDS);
    }

    @Test(expected = TimeoutException.class, timeout = 60000)
    public void getWithTimeout_negativeTimeout_resultNotSet_timesOut() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.get((-1), TimeUnit.MILLISECONDS);
    }

    @Test(expected = TimeoutException.class, timeout = 60000)
    public void getWithTimeout_lowerThanOneMilliTimeout_resultNotSet_timesOut() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.get(1, TimeUnit.NANOSECONDS);
    }

    @Test
    public void getWithTimeout_threadInterrupted_exceptionThrown() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        Thread.currentThread().interrupt();
        expected.expect(InterruptedException.class);
        future.get(100, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 60000)
    public void getWithTimeout_waited_notifiedOnSet() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        submitSetResultAfterTimeInMillis(future, AbstractCompletableFutureTest.RESULT, 200);
        Object result = future.get(30000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(AbstractCompletableFutureTest.RESULT, result);
    }

    @Test(timeout = 60000)
    public void getWithTimeout_waited_waited_notifiedOnCancel() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        submitCancelAfterTimeInMillis(future, 200);
        expected.expect(CancellationException.class);
        future.get(30000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void setResult_ordinaryResultSet_futureDone() {
        setResult_resultSet_futureDone(AbstractCompletableFutureTest.RESULT);
    }

    @Test
    public void setResult_exceptionResultSet_futureDone() {
        setResult_resultSet_futureDone(AbstractCompletableFutureTest.EXCEPTION);
    }

    @Test
    public void setResult_nullResultSet_futureDone() {
        setResult_resultSet_futureDone(null);
    }

    @Test
    public void setResult_whenResultAlreadySet_secondResultDiscarded() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        Object initialResult = "firstresult";
        Object secondResult = "secondresult";
        setResult(initialResult);
        setResult(secondResult);
        Assert.assertSame(initialResult, get());
    }

    @Test
    public void setResult_whenPendingCallback_nullResult() {
        setResult_whenPendingCallback_callbacksExecutedCorrectly(null);
    }

    @Test
    public void setResult_whenPendingCallback_ordinaryResult() {
        setResult_whenPendingCallback_callbacksExecutedCorrectly("foo");
    }

    @Test
    public void setResult_whenPendingCallback_exceptionResult() {
        setResult_whenPendingCallback_callbacksExecutedCorrectly(new Exception());
    }

    @Test
    public void getResult_whenInitialState() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        Object result = future.getResult();
        Assert.assertNull("Internal result should be null initially", result);
    }

    @Test
    public void getResult_whenPendingCallback() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.andThen(Mockito.mock(ExecutionCallback.class));
        Object result = future.getResult();
        Assert.assertNull("Internal result should be null initially", result);
    }

    @Test
    public void getResult_whenNullResult() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.setResult(null);
        Object result = future.getResult();
        Assert.assertNull("Internal result should be null when set to null", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void andThen_whenNullCallback_exceptionThrown() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        future.andThen(null, executor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void andThen_whenNullExecutor_exceptionThrown() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        andThen(Mockito.mock(ExecutionCallback.class), null);
    }

    @Test
    public void andThen_whenInitialState() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback, executor);
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void andThen_whenCancelled() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.cancel(false);
        future.andThen(callback, executor);
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void andThen_whenPendingCallback() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        ExecutionCallback callback1 = Mockito.mock(ExecutionCallback.class);
        ExecutionCallback callback2 = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback1, executor);
        future.andThen(callback2, executor);
        Mockito.verifyZeroInteractions(callback1);
        Mockito.verifyZeroInteractions(callback2);
    }

    @Test
    public void andThen_whenPendingCallback_andCancelled() {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        ExecutionCallback callback1 = Mockito.mock(ExecutionCallback.class);
        ExecutionCallback callback2 = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback1, executor);
        future.cancel(false);
        future.andThen(callback2, executor);
        Mockito.verifyZeroInteractions(callback1);
        Mockito.verifyZeroInteractions(callback2);
    }

    @Test
    public void andThen_whenResultAvailable() throws Exception {
        AbstractCompletableFutureTest.TestFutureImpl future = new AbstractCompletableFutureTest.TestFutureImpl(nodeEngine, logger);
        final Object result = "result";
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        setResult(result);
        future.andThen(callback, executor);
        Assert.assertSame(result, get());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Mockito.verify(callback).onResponse(result);
            }
        });
    }

    private class TestFutureImpl extends AbstractCompletableFuture<Object> {
        protected TestFutureImpl(NodeEngine nodeEngine, ILogger logger) {
            super(nodeEngine, logger);
        }
    }
}

