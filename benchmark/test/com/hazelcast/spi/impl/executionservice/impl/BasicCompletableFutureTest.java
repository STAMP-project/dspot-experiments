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
package com.hazelcast.spi.impl.executionservice.impl;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class BasicCompletableFutureTest {
    private static final String DELEGATE_RESULT = "DELEGATE_RESULT";

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private FutureTask<String> delegateFuture;

    private boolean delegateThrowException;

    private BasicCompletableFuture<String> outerFuture;

    @Test
    public void cancel_delegate_bothCancelled() {
        delegateFuture.cancel(false);
        Assert.assertTrue(delegateFuture.isCancelled());
        Assert.assertTrue(outerFuture.isCancelled());
    }

    @Test
    public void cancel_delegate_getOnDelegate() throws Exception {
        delegateFuture.cancel(false);
        expected.expect(CancellationException.class);
        delegateFuture.get();
    }

    @Test
    public void cancel_delegate_getOnOuter() throws Exception {
        delegateFuture.cancel(false);
        expected.expect(CancellationException.class);
        outerFuture.get();
    }

    @Test
    public void cancel_outer_bothCancelled() {
        outerFuture.cancel(false);
        Assert.assertTrue(delegateFuture.isCancelled());
        Assert.assertTrue(outerFuture.isCancelled());
    }

    @Test
    public void cancel_outer_getOnDelegate() throws Exception {
        outerFuture.cancel(false);
        expected.expect(CancellationException.class);
        delegateFuture.get();
    }

    @Test
    public void cancel_outer_getOnOuter() throws Exception {
        outerFuture.cancel(false);
        expected.expect(CancellationException.class);
        outerFuture.get();
    }

    @Test
    public void completeDelegate_bothDone_delegateAskedFirst() {
        delegateFuture.run();
        Assert.assertTrue(delegateFuture.isDone());
        Assert.assertTrue(outerFuture.isDone());
    }

    @Test
    public void completeDelegate_bothDone_outerAskedFirst() {
        delegateFuture.run();
        Assert.assertTrue(outerFuture.isDone());
        Assert.assertTrue(delegateFuture.isDone());
    }

    @Test
    public void completeDelegate_getWithTimeout_delegateAsked() throws Exception {
        delegateFuture.run();
        Assert.assertEquals(BasicCompletableFutureTest.DELEGATE_RESULT, delegateFuture.get(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void completeDelegate_getWithTimeout_outerAsked() throws Exception {
        delegateFuture.run();
        Assert.assertEquals(BasicCompletableFutureTest.DELEGATE_RESULT, outerFuture.get(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void completeDelegate_successfully_callbacksNeverRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateFuture.run();
        outerFuture.andThen(callback);
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_withException_callbacksNeverRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateThrowException = true;
        delegateFuture.run();
        outerFuture.andThen(callback);
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_successfully_callbackAfterGet_invokeIsDoneOnOuter_callbacksRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateFuture.run();
        outerFuture.isDone();
        outerFuture.andThen(callback);
        Mockito.verify(callback, Mockito.times(1)).onResponse(ArgumentMatchers.any(String.class));
        Mockito.verify(callback, Mockito.times(0)).onFailure(ArgumentMatchers.any(Throwable.class));
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_successfully_callbackAfterGet_invokeGetOnOuter_callbacksRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateFuture.run();
        outerFuture.get();
        outerFuture.andThen(callback);
        Mockito.verify(callback, Mockito.times(1)).onResponse(ArgumentMatchers.any(String.class));
        Mockito.verify(callback, Mockito.times(0)).onFailure(ArgumentMatchers.any(Throwable.class));
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_successfully_callbackBeforeGet_invokeIsDoneOnOuter_callbacksRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateFuture.run();
        outerFuture.andThen(callback);
        outerFuture.isDone();
        Mockito.verify(callback, Mockito.times(1)).onResponse(ArgumentMatchers.any(String.class));
        Mockito.verify(callback, Mockito.times(0)).onFailure(ArgumentMatchers.any(Throwable.class));
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_successfully_callbackBeforeGet_invokeGetOnOuter_callbacksRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateFuture.run();
        outerFuture.andThen(callback);
        outerFuture.get();
        Mockito.verify(callback, Mockito.times(1)).onResponse(ArgumentMatchers.any(String.class));
        Mockito.verify(callback, Mockito.times(0)).onFailure(ArgumentMatchers.any(Throwable.class));
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_withException_callbackBeforeGet_invokeIsDoneOnOuter_callbacksRun() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateThrowException = true;
        delegateFuture.run();
        outerFuture.andThen(callback);
        outerFuture.isDone();
        Mockito.verify(callback, Mockito.times(0)).onResponse(ArgumentMatchers.any(String.class));
        Mockito.verify(callback, Mockito.times(1)).onFailure(ArgumentMatchers.any(Throwable.class));
        Mockito.verifyZeroInteractions(callback);
    }

    @Test
    public void completeDelegate_withException_callbackBeforeGet_invokeGetOnOuter_callbacksNeverReached() throws Exception {
        ExecutionCallback<String> callback = BasicCompletableFutureTest.getStringExecutionCallback();
        delegateThrowException = true;
        delegateFuture.run();
        outerFuture.andThen(callback);
        try {
            outerFuture.get();
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertEquals("Exception in execution", t.getCause().getMessage());
        }
        Mockito.verify(callback, Mockito.times(0)).onResponse(ArgumentMatchers.any(String.class));
        Mockito.verify(callback, Mockito.times(1)).onFailure(ArgumentMatchers.any(Throwable.class));
        Mockito.verifyZeroInteractions(callback);
    }

    private static class TestCurrentThreadExecutor extends ThreadPoolExecutor implements ManagedExecutorService {
        public TestCurrentThreadExecutor() {
            super(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        }

        public String getName() {
            return "hz:test:current:thread:executor";
        }

        public int getQueueSize() {
            return Integer.MAX_VALUE;
        }

        public int getRemainingQueueCapacity() {
            return Integer.MAX_VALUE;
        }

        public void execute(Runnable runnable) {
            // run in current thread
            try {
                runnable.run();
            } catch (Exception ex) {
                ExceptionUtil.sneakyThrow(new ExecutionException(ex));
            }
        }
    }
}

