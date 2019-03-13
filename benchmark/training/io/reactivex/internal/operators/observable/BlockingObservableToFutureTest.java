/**
 * Copyright (c) 2016-present, RxJava Contributors.
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
package io.reactivex.internal.operators.observable;


import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class BlockingObservableToFutureTest {
    @Test
    public void testToFuture() throws InterruptedException, ExecutionException {
        Observable<String> obs = Observable.just("one");
        Future<String> f = obs.toFuture();
        Assert.assertEquals("one", f.get());
    }

    @Test
    public void testToFutureList() throws InterruptedException, ExecutionException {
        Observable<String> obs = Observable.just("one", "two", "three");
        Future<List<String>> f = obs.toList().toFuture();
        Assert.assertEquals("one", f.get().get(0));
        Assert.assertEquals("two", f.get().get(1));
        Assert.assertEquals("three", f.get().get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testExceptionWithMoreThanOneElement() throws Throwable {
        Observable<String> obs = just("one", "two");
        Future<String> f = obs.toFuture();
        try {
            // we expect an exception since there are more than 1 element
            f.get();
            Assert.fail("Should have thrown!");
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testToFutureWithException() {
        Observable<String> obs = unsafeCreate(new io.reactivex.ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onNext("one");
                observer.onError(new TestException());
            }
        });
        Future<String> f = obs.toFuture();
        try {
            f.get();
            Assert.fail("expected exception");
        } catch (Throwable e) {
            Assert.assertEquals(TestException.class, e.getCause().getClass());
        }
    }

    @Test(expected = CancellationException.class)
    public void testGetAfterCancel() throws Exception {
        Observable<String> obs = never();
        Future<String> f = obs.toFuture();
        boolean cancelled = f.cancel(true);
        Assert.assertTrue(cancelled);// because OperationNeverComplete never does

        f.get();// Future.get() docs require this to throw

    }

    @Test(expected = CancellationException.class)
    public void testGetWithTimeoutAfterCancel() throws Exception {
        Observable<String> obs = never();
        Future<String> f = obs.toFuture();
        boolean cancelled = f.cancel(true);
        Assert.assertTrue(cancelled);// because OperationNeverComplete never does

        f.get(Long.MAX_VALUE, TimeUnit.NANOSECONDS);// Future.get() docs require this to throw

    }

    @Test(expected = NoSuchElementException.class)
    public void testGetWithEmptyFlowable() throws Throwable {
        Observable<String> obs = empty();
        Future<String> f = obs.toFuture();
        try {
            f.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}

