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
package io.reactivex.internal.operators.completable;


import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class CompletableFromCallableTest {
    @Test(expected = NullPointerException.class)
    public void fromCallableNull() {
        Completable.fromCallable(null);
    }

    @Test
    public void fromCallable() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                atomicInteger.incrementAndGet();
                return null;
            }
        }).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromCallableTwice() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                atomicInteger.incrementAndGet();
                return null;
            }
        };
        Completable.fromCallable(callable).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
        Completable.fromCallable(callable).test().assertResult();
        Assert.assertEquals(2, atomicInteger.get());
    }

    @Test
    public void fromCallableInvokesLazy() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Completable completable = Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                atomicInteger.incrementAndGet();
                return null;
            }
        });
        Assert.assertEquals(0, atomicInteger.get());
        completable.test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromCallableThrows() {
        Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new UnsupportedOperationException();
            }
        }).test().assertFailure(UnsupportedOperationException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotDeliverResultIfSubscriberUnsubscribedBeforeEmission() throws Exception {
        Callable<String> func = Mockito.mock(Callable.class);
        final CountDownLatch funcLatch = new CountDownLatch(1);
        final CountDownLatch observerLatch = new CountDownLatch(1);
        Mockito.when(func.call()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                observerLatch.countDown();
                try {
                    funcLatch.await();
                } catch (InterruptedException e) {
                    // It's okay, unsubscription causes Thread interruption
                    // Restoring interruption status of the Thread
                    Thread.currentThread().interrupt();
                }
                return "should_not_be_delivered";
            }
        });
        Completable fromCallableObservable = Completable.fromCallable(func);
        Observer<Object> observer = mockObserver();
        TestObserver<String> outer = new TestObserver<String>(observer);
        fromCallableObservable.subscribeOn(Schedulers.computation()).subscribe(outer);
        // Wait until func will be invoked
        observerLatch.await();
        // Unsubscribing before emission
        outer.cancel();
        // Emitting result
        funcLatch.countDown();
        // func must be invoked
        Mockito.verify(func).call();
        // Observer must not be notified at all
        Mockito.verify(observer).onSubscribe(ArgumentMatchers.any(Disposable.class));
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void fromActionErrorsDisposed() {
        final AtomicInteger calls = new AtomicInteger();
        Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                calls.incrementAndGet();
                throw new TestException();
            }
        }).test(true).assertEmpty();
        Assert.assertEquals(1, calls.get());
    }
}

