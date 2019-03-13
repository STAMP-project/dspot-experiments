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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class FlowableFromCallableTest {
    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotInvokeFuncUntilSubscription() throws Exception {
        Callable<Object> func = Mockito.mock(Callable.class);
        Mockito.when(func.call()).thenReturn(new Object());
        Flowable<Object> fromCallableFlowable = Flowable.fromCallable(func);
        Mockito.verifyZeroInteractions(func);
        fromCallableFlowable.subscribe();
        Mockito.verify(func).call();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCallOnNextAndOnCompleted() throws Exception {
        Callable<String> func = Mockito.mock(Callable.class);
        Mockito.when(func.call()).thenReturn("test_value");
        Flowable<String> fromCallableFlowable = Flowable.fromCallable(func);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        fromCallableFlowable.subscribe(subscriber);
        Mockito.verify(subscriber).onNext("test_value");
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCallOnError() throws Exception {
        Callable<Object> func = Mockito.mock(Callable.class);
        Throwable throwable = new IllegalStateException("Test exception");
        Mockito.when(func.call()).thenThrow(throwable);
        Flowable<Object> fromCallableFlowable = Flowable.fromCallable(func);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        fromCallableFlowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber).onError(throwable);
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
        Flowable<String> fromCallableFlowable = Flowable.fromCallable(func);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> outer = new TestSubscriber<String>(subscriber);
        fromCallableFlowable.subscribeOn(Schedulers.computation()).subscribe(outer);
        // Wait until func will be invoked
        observerLatch.await();
        // Unsubscribing before emission
        outer.cancel();
        // Emitting result
        funcLatch.countDown();
        // func must be invoked
        Mockito.verify(func).call();
        // Observer must not be notified at all
        Mockito.verify(subscriber).onSubscribe(ArgumentMatchers.any(Subscription.class));
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void shouldAllowToThrowCheckedException() {
        final Exception checkedException = new Exception("test exception");
        Flowable<Object> fromCallableFlowable = Flowable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw checkedException;
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        fromCallableFlowable.subscribe(subscriber);
        Mockito.verify(subscriber).onSubscribe(ArgumentMatchers.any(Subscription.class));
        Mockito.verify(subscriber).onError(checkedException);
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void fusedFlatMapExecution() {
        final int[] calls = new int[]{ 0 };
        Flowable.just(1).flatMap(new io.reactivex.functions.Function<Integer, Publisher<? extends Object>>() {
            @Override
            public io.reactivex.Publisher<? extends Object> apply(Integer v) throws Exception {
                return Flowable.fromCallable(new Callable<Object>() {
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
        Flowable.just(1).hide().flatMap(new io.reactivex.functions.Function<Integer, Publisher<? extends Object>>() {
            @Override
            public io.reactivex.Publisher<? extends Object> apply(Integer v) throws Exception {
                return Flowable.fromCallable(new Callable<Object>() {
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
        Flowable.just(1).flatMap(new io.reactivex.functions.Function<Integer, Publisher<? extends Object>>() {
            @Override
            public io.reactivex.Publisher<? extends Object> apply(Integer v) throws Exception {
                return Flowable.fromCallable(new Callable<Object>() {
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
        Flowable.just(1).hide().flatMap(new io.reactivex.functions.Function<Integer, Publisher<? extends Object>>() {
            @Override
            public io.reactivex.Publisher<? extends Object> apply(Integer v) throws Exception {
                return Flowable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return null;
                    }
                });
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test(timeout = 5000)
    public void undeliverableUponCancellation() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            Flowable.fromCallable(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    ts.cancel();
                    throw new TestException();
                }
            }).subscribe(ts);
            ts.assertEmpty();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

