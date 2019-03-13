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
package com.hazelcast.executor;


import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CompletableFutureTest extends HazelcastTestSupport {
    private static final RuntimeException THROW_TEST_EXCEPTION = new RuntimeException("Test exception");

    private static final RuntimeException NO_EXCEPTION = null;

    private ExecutionService executionService;

    private CountDownLatch inExecutionLatch;

    private CountDownLatch startLogicLatch;

    private CountDownLatch executedLogic;

    private CountDownLatch callbacksDoneLatch;

    private AtomicReference<Object> reference1;

    private AtomicReference<Object> reference2;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void preregisterCallback() {
        ICompletableFuture<String> f = submitAwaitingTask(expectedNumberOfCallbacks(1), CompletableFutureTest.NO_EXCEPTION);
        f.andThen(storeTaskResponseToReference(reference1));
        releaseAwaitingTask();
        assertCallbacksExecutedEventually();
        Assert.assertEquals("success", reference1.get());
    }

    @Test
    public void preregisterTwoCallbacks() {
        ICompletableFuture<String> f = submitAwaitingTask(expectedNumberOfCallbacks(2), CompletableFutureTest.NO_EXCEPTION);
        f.andThen(storeTaskResponseToReference(reference1));
        f.andThen(storeTaskResponseToReference(reference2));
        releaseAwaitingTask();
        assertCallbacksExecutedEventually();
        Assert.assertEquals("success", reference1.get());
        Assert.assertEquals("success", reference2.get());
    }

    @Test
    public void preregisterTwoCallbacks_taskThrowsException() {
        ICompletableFuture<String> f = submitAwaitingTask(expectedNumberOfCallbacks(2), CompletableFutureTest.THROW_TEST_EXCEPTION);
        f.andThen(storeTaskResponseToReference(reference1));
        f.andThen(storeTaskResponseToReference(reference2));
        releaseAwaitingTask();
        assertCallbacksExecutedEventually();
        CompletableFutureTest.assertTestExceptionThrown(reference1, reference2);
    }

    // https://github.com/hazelcast/hazelcast/issues/6020
    @Test
    public void postregisterCallback() {
        ICompletableFuture<String> f = submitAwaitingTask(expectedNumberOfCallbacks(1), CompletableFutureTest.NO_EXCEPTION);
        releaseAwaitingTask();
        assertTaskFinishedEventually(f);
        f.andThen(storeTaskResponseToReference(reference1));
        assertCallbacksExecutedEventually();
        Assert.assertEquals("success", reference1.get());
    }

    @Test
    public void postregisterTwoCallbacks() {
        ICompletableFuture<String> f = submitAwaitingTask(expectedNumberOfCallbacks(2), CompletableFutureTest.NO_EXCEPTION);
        releaseAwaitingTask();
        assertTaskFinishedEventually(f);
        f.andThen(storeTaskResponseToReference(reference1));
        f.andThen(storeTaskResponseToReference(reference2));
        assertCallbacksExecutedEventually();
        Assert.assertEquals("success", reference1.get());
        Assert.assertEquals("success", reference2.get());
    }

    @Test
    public void postregisterTwoCallbacks_taskThrowsException() {
        ICompletableFuture<String> f = submitAwaitingTask(expectedNumberOfCallbacks(2), CompletableFutureTest.THROW_TEST_EXCEPTION);
        releaseAwaitingTask();
        assertTaskFinishedEventually(f);
        f.andThen(storeTaskResponseToReference(reference1));
        f.andThen(storeTaskResponseToReference(reference2));
        assertCallbacksExecutedEventually();
        CompletableFutureTest.assertTestExceptionThrown(reference1, reference2);
    }

    @Test(timeout = 60000)
    public void get_taskThrowsException() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.THROW_TEST_EXCEPTION);
        submitReleasingTask(100);
        expected.expect(ExecutionException.class);
        f.get();
    }

    @Test(timeout = 60000)
    public void getWithTimeout_taskThrowsException() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.THROW_TEST_EXCEPTION);
        submitReleasingTask(200);
        expected.expect(ExecutionException.class);
        f.get(30000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 60000)
    public void getWithTimeout_finishesWithinTime() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        submitReleasingTask(200);
        String result = f.get(30000, TimeUnit.MILLISECONDS);
        Assert.assertEquals("success", result);
    }

    @Test(timeout = 60000)
    public void getWithTimeout_timesOut() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        expected.expect(TimeoutException.class);
        f.get(1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void singleCancellation_beforeDone_succeeds() {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        assertTaskInExecution();
        boolean cancelResult = f.cancel(false);
        Assert.assertTrue("Task cancellation succeeded should succeed", cancelResult);
    }

    @Test
    public void doubleCancellation_beforeDone_firstSucceeds_secondFails() {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        assertTaskInExecution();// but never released to execute logic

        boolean firstCancelResult = f.cancel(false);
        boolean secondCancelResult = f.cancel(false);
        Assert.assertTrue("First task cancellation should succeed", firstCancelResult);
        Assert.assertFalse("Second task cancellation should failed", secondCancelResult);
    }

    @Test
    public void cancellation_afterDone_taskNotCancelled_flagsSetCorrectly() throws Exception {
        final ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        assertTaskInExecution();
        releaseAwaitingTask();
        assertTaskExecutedItsLogic();
        assertTaskFinishedEventually(f);
        boolean firstCancelResult = f.cancel(false);
        boolean secondCancelResult = f.cancel(false);
        Assert.assertFalse("Cancellation should not succeed after task is done", firstCancelResult);
        Assert.assertFalse("Cancellation should not succeed after task is done", secondCancelResult);
        Assert.assertFalse("Task should NOT be cancelled", f.isCancelled());
        Assert.assertEquals("success", f.get());
    }

    @Test
    public void noCancellation_afterDone_flagsSetCorrectly() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        assertTaskInExecution();
        releaseAwaitingTask();
        assertTaskExecutedItsLogic();
        assertTaskFinishedEventually(f);
        Assert.assertTrue("Task should be done", f.isDone());
        Assert.assertFalse("Task should NOT be cancelled", f.isCancelled());
        Assert.assertEquals("success", f.get());
    }

    @Test(timeout = 60000)
    public void cancelAndGet_taskCancelled_withoutInterruption_logicExecuted() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        assertTaskInExecution();
        boolean cancelResult = f.cancel(false);
        releaseAwaitingTask();
        assertTaskExecutedItsLogic();// cancellation came, when task already awaiting, so logic executed

        assertTaskFinishedEventually(f);
        Assert.assertTrue("Task cancellation should succeed", cancelResult);
        Assert.assertTrue("Task should be done", f.isDone());
        Assert.assertTrue("Task should be cancelled", f.isCancelled());
        expected.expect(CancellationException.class);
        f.get();
    }

    @Test(timeout = 60000)
    public void cancelAndGet_taskCancelled_withInterruption_noLogicExecuted() throws Exception {
        ICompletableFuture<String> f = submitAwaitingTaskNoCallbacks(CompletableFutureTest.NO_EXCEPTION);
        assertTaskInExecution();
        boolean cancelResult = f.cancel(true);
        assertTaskInterruptedAndDidNotExecuteItsLogic();
        assertTaskFinishedEventually(f);// task did not have to be releases - interruption was enough

        Assert.assertTrue("Task cancellation should succeed", cancelResult);
        Assert.assertTrue("Task should be done", f.isDone());
        Assert.assertTrue("Task should be cancelled", f.isCancelled());
        expected.expect(CancellationException.class);
        f.get();
    }
}

