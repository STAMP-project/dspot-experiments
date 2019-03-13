package com.bumptech.glide.request;


import DataSource.DATA_DISK_CACHE;
import RequestFutureTarget.Waiter;
import com.bumptech.glide.request.target.SizeReadyCallback;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class RequestFutureTargetTest {
    private int width;

    private int height;

    private RequestFutureTarget<Object> future;

    private Request request;

    private Waiter waiter;

    @Test
    public void testCallsSizeReadyCallbackOnGetSize() {
        SizeReadyCallback cb = Mockito.mock(SizeReadyCallback.class);
        future.getSize(cb);
        Mockito.verify(cb).onSizeReady(ArgumentMatchers.eq(width), ArgumentMatchers.eq(height));
    }

    @Test
    public void testReturnsFalseForDoneBeforeDone() {
        Assert.assertFalse(future.isDone());
    }

    @Test
    public void testReturnsTrueFromIsDoneIfDone() {
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(new Object(), null, future, DATA_DISK_CACHE, true);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void testReturnsFalseForIsCancelledBeforeCancelled() {
        Assert.assertFalse(future.isCancelled());
    }

    @Test
    public void testReturnsTrueFromCancelIfNotYetDone() {
        Assert.assertTrue(future.cancel(false));
    }

    @Test
    public void cancel_withMayInterruptIfRunningTrueAndNotFinishedRequest_clearsFuture() {
        future.cancel(true);
        Mockito.verify(request).clear();
    }

    @Test
    public void cancel_withInterruptFalseAndNotFinishedRequest_doesNotClearFuture() {
        future.cancel(false);
        Mockito.verify(request, Mockito.never()).clear();
    }

    @Test
    public void testDoesNotRepeatedlyClearRequestIfCancelledRepeatedly() {
        future.cancel(true);
        future.cancel(true);
        Mockito.verify(request, Mockito.times(1)).clear();
    }

    @Test
    public void testDoesNotClearRequestIfCancelledAfterDone() {
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(new Object(), null, future, DATA_DISK_CACHE, true);
        future.cancel(true);
        Mockito.verify(request, Mockito.never()).clear();
    }

    @Test
    public void testReturnsTrueFromDoneIfCancelled() {
        future.cancel(true);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void testReturnsFalseFromIsCancelledIfCancelledAfterDone() {
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(new Object(), null, future, DATA_DISK_CACHE, true);
        future.cancel(true);
        Assert.assertFalse(future.isCancelled());
    }

    @Test
    public void testReturnsTrueFromCancelIfCancelled() {
        future.cancel(true);
        Assert.assertTrue(future.isCancelled());
    }

    @Test
    public void testReturnsFalseFromCancelIfDone() {
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(new Object(), null, future, DATA_DISK_CACHE, true);
        Assert.assertFalse(future.cancel(true));
    }

    @Test
    public void testReturnsResourceOnGetIfAlreadyDone() throws InterruptedException, ExecutionException {
        Object expected = new Object();
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(expected, null, future, DATA_DISK_CACHE, true);
        Assert.assertEquals(expected, future.get());
    }

    @Test
    public void testReturnsResourceOnGetWithTimeoutIfAlreadyDone() throws InterruptedException, ExecutionException, TimeoutException {
        Object expected = new Object();
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(expected, null, future, DATA_DISK_CACHE, true);
        Assert.assertEquals(expected, future.get(1, TimeUnit.MILLISECONDS));
    }

    @Test(expected = CancellationException.class)
    public void testThrowsCancellationExceptionIfCancelledBeforeGet() throws InterruptedException, ExecutionException {
        future.cancel(true);
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void testThrowsCancellationExceptionIfCancelledBeforeGetWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        future.cancel(true);
        future.get(100, TimeUnit.MILLISECONDS);
    }

    @Test(expected = ExecutionException.class)
    public void testThrowsExecutionExceptionOnGetIfExceptionBeforeGet() throws InterruptedException, ExecutionException {
        /* e= */
        /* model= */
        /* isFirstResource= */
        future.onLoadFailed(null, null, future, true);
        future.get();
    }

    @Test(expected = ExecutionException.class)
    public void testThrowsExecutionExceptionOnGetIfExceptionWithNullValueBeforeGet() throws InterruptedException, ExecutionException, TimeoutException {
        /* e= */
        /* model= */
        /* isFirstResource= */
        future.onLoadFailed(null, null, future, true);
        future.get(100, TimeUnit.MILLISECONDS);
    }

    @Test(expected = ExecutionException.class)
    public void testThrowsExecutionExceptionOnGetIfExceptionBeforeGetWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        /* e= */
        /* model= */
        /* isFirstResource= */
        future.onLoadFailed(null, null, future, true);
        future.get(100, TimeUnit.MILLISECONDS);
    }

    @Test(expected = TimeoutException.class)
    public void testThrowsTimeoutExceptionOnGetIfFailedToReceiveResourceInTime() throws InterruptedException, ExecutionException, TimeoutException {
        future.get(1, TimeUnit.MILLISECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionIfGetCalledOnMainThread() throws InterruptedException, ExecutionException {
        future = new RequestFutureTarget(width, height, true, waiter);
        future.get();
    }

    @Test
    public void testGetSucceedsOnMainThreadIfDone() throws InterruptedException, ExecutionException {
        future = new RequestFutureTarget(width, height, true, waiter);
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(new Object(), null, future, DATA_DISK_CACHE, true);
        future.get();
    }

    @Test(expected = InterruptedException.class)
    public void testThrowsInterruptedExceptionIfThreadInterruptedWhenDoneWaiting() throws InterruptedException, ExecutionException {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                Thread.currentThread().interrupt();
                return null;
            }
        }).when(waiter).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.anyLong());
        future.get();
    }

    @Test(expected = ExecutionException.class)
    public void testThrowsExecutionExceptionIfLoadFailsWhileWaiting() throws InterruptedException, ExecutionException {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                /* e= */
                /* model= */
                /* isFirstResource= */
                future.onLoadFailed(null, null, future, true);
                return null;
            }
        }).when(waiter).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.anyLong());
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void testThrowsCancellationExceptionIfCancelledWhileWaiting() throws InterruptedException, ExecutionException {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                future.cancel(false);
                return null;
            }
        }).when(waiter).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.anyLong());
        future.get();
    }

    @Test(expected = TimeoutException.class)
    public void testThrowsTimeoutExceptionIfFinishesWaitingWithTimeoutAndDoesNotReceiveResult() throws InterruptedException, ExecutionException, TimeoutException {
        future.get(1, TimeUnit.MILLISECONDS);
    }

    @Test(expected = AssertionError.class)
    public void testThrowsAssertionErrorIfFinishesWaitingWithoutTimeoutAndDoesNotReceiveResult() throws InterruptedException, ExecutionException {
        future.get();
    }

    @Test
    public void testNotifiesAllWhenLoadFails() {
        /* e= */
        /* model= */
        /* isFirstResource= */
        future.onLoadFailed(null, null, future, true);
        Mockito.verify(waiter).notifyAll(ArgumentMatchers.eq(future));
    }

    @Test
    public void testNotifiesAllWhenResourceReady() {
        /* resource= */
        /* model= */
        /* target= */
        /* isFirstResource */
        future.onResourceReady(new Object(), null, future, DATA_DISK_CACHE, true);
        Mockito.verify(waiter).notifyAll(ArgumentMatchers.eq(future));
    }

    @Test
    public void testNotifiesAllOnCancelIfNotCancelled() {
        future.cancel(false);
        Mockito.verify(waiter).notifyAll(ArgumentMatchers.eq(future));
    }

    @Test
    public void testDoesNotNotifyAllOnSecondCancel() {
        future.cancel(true);
        Mockito.verify(waiter).notifyAll(ArgumentMatchers.eq(future));
        future.cancel(true);
        Mockito.verify(waiter, Mockito.times(1)).notifyAll(ArgumentMatchers.eq(future));
    }

    @Test
    public void testReturnsResourceIfReceivedWhileWaiting() throws InterruptedException, ExecutionException {
        final Object expected = new Object();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                /* resource= */
                /* model= */
                /* target= */
                /* isFirstResource */
                future.onResourceReady(expected, null, future, DATA_DISK_CACHE, true);
                return null;
            }
        }).when(waiter).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.anyLong());
        Assert.assertEquals(expected, future.get());
    }

    @Test
    public void testWaitsForeverIfNoTimeoutSet() throws InterruptedException {
        try {
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (AssertionError e) {
            // Expected.
        }
        Mockito.verify(waiter).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.eq(0L));
    }

    @Test
    public void testWaitsForGivenTimeoutMillisIfTimeoutSet() throws InterruptedException {
        long timeout = 2;
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            // Expected.
        }
        Mockito.verify(waiter, Mockito.atLeastOnce()).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.eq(timeout));
    }

    @Test
    public void testConvertsOtherTimeUnitsToMillisForWaiter() throws InterruptedException {
        long timeoutMicros = 1000;
        try {
            future.get(timeoutMicros, TimeUnit.MICROSECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            // Expected.
        }
        Mockito.verify(waiter, Mockito.atLeastOnce()).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.eq(TimeUnit.MICROSECONDS.toMillis(timeoutMicros)));
    }

    @Test
    public void testDoesNotWaitIfGivenTimeOutEqualToZero() throws InterruptedException {
        try {
            future.get(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            // Expected.
        }
        Mockito.verify(waiter, Mockito.never()).waitForTimeout(ArgumentMatchers.eq(future), ArgumentMatchers.anyLong());
    }
}

