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
package io.reactivex.internal.operators.observable;


import io.reactivex.observers.TestObserver;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableToFutureTest {
    @Test
    public void testSuccess() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        Object value = new Object();
        Mockito.when(future.get()).thenReturn(value);
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        Observable.fromFuture(future).subscribe(to);
        to.dispose();
        Mockito.verify(o, Mockito.times(1)).onNext(value);
        Mockito.verify(o, Mockito.times(1)).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(future, Mockito.never()).cancel(true);
    }

    @Test
    public void testSuccessOperatesOnSuppliedScheduler() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        Object value = new Object();
        Mockito.when(future.get()).thenReturn(value);
        Observer<Object> o = mockObserver();
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Object> to = new TestObserver<Object>(o);
        Observable.fromFuture(future, scheduler).subscribe(to);
        Mockito.verify(o, Mockito.never()).onNext(value);
        scheduler.triggerActions();
        Mockito.verify(o, Mockito.times(1)).onNext(value);
    }

    @Test
    public void testFailure() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        RuntimeException e = new RuntimeException();
        Mockito.when(future.get()).thenThrow(e);
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        Observable.fromFuture(future).subscribe(to);
        to.dispose();
        Mockito.verify(o, Mockito.never()).onNext(null);
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o, Mockito.times(1)).onError(e);
        Mockito.verify(future, Mockito.never()).cancel(true);
    }

    @Test
    public void testCancelledBeforeSubscribe() throws Exception {
        @SuppressWarnings("unchecked")
        Future<Object> future = Mockito.mock(Future.class);
        CancellationException e = new CancellationException("unit test synthetic cancellation");
        Mockito.when(future.get()).thenThrow(e);
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        to.dispose();
        Observable.fromFuture(future).subscribe(to);
        to.assertNoErrors();
        to.assertNotComplete();
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
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        Observable<Object> futureObservable = Observable.fromFuture(future);
        futureObservable.subscribeOn(Schedulers.computation()).subscribe(to);
        Thread.sleep(100);
        to.dispose();
        to.assertNoErrors();
        to.assertNoValues();
        to.assertNotComplete();
    }
}

