/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.reactivex.internal.operators.observable;


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ObservableFromCallableTest {
    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotInvokeFuncUntilSubscription() throws Exception {
        Callable<Object> func = Mockito.mock(Callable.class);
        Mockito.when(func.call()).thenReturn(new Object());
        Observable<Object> fromCallableObservable = Observable.fromCallable(func);
        Mockito.verifyZeroInteractions(func);
        fromCallableObservable.subscribe();
        Mockito.verify(func).call();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCallOnNextAndOnCompleted() throws Exception {
        Callable<String> func = Mockito.mock(Callable.class);
        Mockito.when(func.call()).thenReturn("test_value");
        Observable<String> fromCallableObservable = Observable.fromCallable(func);
        Observer<Object> observer = mockObserver();
        fromCallableObservable.subscribe(observer);
        Mockito.verify(observer).onNext("test_value");
        Mockito.verify(observer).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCallOnError() throws Exception {
        Callable<Object> func = Mockito.mock(Callable.class);
        Throwable throwable = new IllegalStateException("Test exception");
        Mockito.when(func.call()).thenThrow(throwable);
        Observable<Object> fromCallableObservable = Observable.fromCallable(func);
        Observer<Object> observer = mockObserver();
        fromCallableObservable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer).onError(throwable);
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
        Observable<String> fromCallableObservable = Observable.fromCallable(func);
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
    public void shouldAllowToThrowCheckedException() {
        final Exception checkedException = new Exception("test exception");
        Observable<Object> fromCallableObservable = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw checkedException;
            }
        });
        Observer<Object> observer = mockObserver();
        fromCallableObservable.subscribe(observer);
        Mockito.verify(observer).onSubscribe(ArgumentMatchers.any(Disposable.class));
        Mockito.verify(observer).onError(checkedException);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void fusedFlatMapExecution() {
        final int[] calls = new int[]{ 0 };
        Observable.just(1).flatMap(new io.reactivex.functions.Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                return Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return ++(calls[0]);
                    }
                });
            }
        }).test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @Test
    public void fusedFlatMapExecutionHidden() {
        final int[] calls = new int[]{ 0 };
        Observable.just(1).hide().flatMap(new io.reactivex.functions.Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                return Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return ++(calls[0]);
                    }
                });
            }
        }).test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @Test
    public void fusedFlatMapNull() {
        Observable.just(1).flatMap(new io.reactivex.functions.Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                return Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return null;
                    }
                });
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void fusedFlatMapNullHidden() {
        Observable.just(1).hide().flatMap(new io.reactivex.functions.Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                return Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return null;
                    }
                });
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void disposedOnArrival() {
        final int[] count = new int[]{ 0 };
        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                (count[0])++;
                return 1;
            }
        }).test(true).assertEmpty();
        Assert.assertEquals(0, count[0]);
    }

    @Test
    public void disposedOnCall() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                to.cancel();
                return 1;
            }
        }).subscribe(to);
        to.assertEmpty();
    }

    @Test
    public void disposedOnCallThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final TestObserver<Integer> to = new TestObserver<Integer>();
            Observable.fromCallable(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    to.cancel();
                    throw new TestException();
                }
            }).subscribe(to);
            to.assertEmpty();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void take() {
        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }).take(1).test().assertResult(1);
    }
}

