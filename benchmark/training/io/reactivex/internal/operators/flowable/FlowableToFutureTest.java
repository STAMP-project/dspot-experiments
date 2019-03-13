/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableToFutureTest {
    @Test
    public void testSuccess() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        Object value = new Object();
        Mockito.when(future.get()).thenReturn(value);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        Flowable.fromFuture(future).subscribe(ts);
        ts.dispose();
        Mockito.verify(subscriber, Mockito.times(1)).onNext(value);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(future, Mockito.never()).cancel(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testSuccessOperatesOnSuppliedScheduler() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        Object value = new Object();
        Mockito.when(future.get()).thenReturn(value);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestScheduler scheduler = new TestScheduler();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        Flowable.fromFuture(future, scheduler).subscribe(ts);
        Mockito.verify(subscriber, Mockito.never()).onNext(value);
        scheduler.triggerActions();
        Mockito.verify(subscriber, Mockito.times(1)).onNext(value);
    }

    @Test
    public void testFailure() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        RuntimeException e = new RuntimeException();
        Mockito.when(future.get()).thenThrow(e);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        Flowable.fromFuture(future).subscribe(ts);
        ts.dispose();
        Mockito.verify(subscriber, Mockito.never()).onNext(null);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onError(e);
        Mockito.verify(future, Mockito.never()).cancel(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCancelledBeforeSubscribe() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        CancellationException e = new CancellationException("unit test synthetic cancellation");
        Mockito.when(future.get()).thenThrow(e);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        ts.dispose();
        Flowable.fromFuture(future).subscribe(ts);
        ts.assertNoErrors();
        ts.assertNotComplete();
    }

    @Test
    public void testCancellationDuringFutureGet() throws Exception {
        Future<Object> future = new Future<Object>() {
            private AtomicBoolean isCancelled = new AtomicBoolean(false);

            private AtomicBoolean isDone = new AtomicBoolean(false);

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                isCancelled.compareAndSet(false, true);
                return true;
            }

            @Override
            public boolean isCancelled() {
                return isCancelled.get();
            }

            @Override
            public boolean isDone() {
                return (isCancelled()) || (isDone.get());
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                Thread.sleep(500);
                isDone.compareAndSet(false, true);
                return "foo";
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return get();
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        Flowable<Object> futureObservable = Flowable.fromFuture(future);
        futureObservable.subscribeOn(Schedulers.computation()).subscribe(ts);
        Thread.sleep(100);
        ts.dispose();
        ts.assertNoErrors();
        ts.assertNoValues();
        ts.assertNotComplete();
    }

    @Test
    public void backpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0);
        FutureTask<Integer> f = new FutureTask<Integer>(new Runnable() {
            @Override
            public void run() {
            }
        }, 1);
        f.run();
        Flowable.fromFuture(f).subscribe(ts);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void withTimeoutNoTimeout() {
        FutureTask<Integer> task = new FutureTask<Integer>(new Runnable() {
            @Override
            public void run() {
            }
        }, 1);
        task.run();
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.fromFuture(task, 1, TimeUnit.SECONDS).subscribe(ts);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void withTimeoutTimeout() {
        FutureTask<Integer> task = new FutureTask<Integer>(new Runnable() {
            @Override
            public void run() {
            }
        }, 1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.fromFuture(task, 10, TimeUnit.MILLISECONDS).subscribe(ts);
        ts.assertNoValues();
        ts.assertError(TimeoutException.class);
        ts.assertNotComplete();
    }

    @Test
    public void withTimeoutNoTimeoutScheduler() {
        FutureTask<Integer> task = new FutureTask<Integer>(new Runnable() {
            @Override
            public void run() {
            }
        }, 1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.fromFuture(task, Schedulers.computation()).subscribe(ts);
        task.run();
        ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertComplete();
    }
}

