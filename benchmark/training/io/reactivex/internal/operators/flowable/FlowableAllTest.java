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
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableAllTest {
    @Test
    public void testAll() {
        Flowable<String> obs = Flowable.just("one", "two", "six");
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onSuccess(true);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testNotAll() {
        Flowable<String> obs = Flowable.just("one", "two", "three", "six");
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onSuccess(false);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testEmpty() {
        Flowable<String> obs = Flowable.empty();
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onSuccess(true);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testError() {
        Throwable error = new Throwable();
        Flowable<String> obs = Flowable.error(error);
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onError(error);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testFollowingFirst() {
        Flowable<Integer> f = Flowable.fromArray(1, 3, 5, 6);
        Single<Boolean> allOdd = f.all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                return (i % 2) == 1;
            }
        });
        Assert.assertFalse(allOdd.blockingGet());
    }

    @Test(timeout = 5000)
    public void testIssue1935NoUnsubscribeDownstream() {
        Flowable<Integer> source = Flowable.just(1).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return false;
            }
        }).flatMapPublisher(new Function<Boolean, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Boolean t1) {
                return Flowable.just(2).delay(500, TimeUnit.MILLISECONDS);
            }
        });
        Assert.assertEquals(((Object) (2)), source.blockingFirst());
    }

    @Test
    public void testBackpressureIfOneRequestedOneShouldBeDelivered() {
        TestObserver<Boolean> to = new TestObserver<Boolean>();
        Flowable.empty().all(new Predicate<Object>() {
            @Override
            public boolean test(Object t) {
                return false;
            }
        }).subscribe(to);
        to.assertTerminated();
        to.assertNoErrors();
        to.assertComplete();
        to.assertValue(true);
    }

    @Test
    public void testPredicateThrowsExceptionAndValueInCauseMessage() {
        TestObserver<Boolean> to = new TestObserver<Boolean>();
        final IllegalArgumentException ex = new IllegalArgumentException();
        Flowable.just("Boo!").all(new Predicate<String>() {
            @Override
            public boolean test(String v) {
                throw ex;
            }
        }).subscribe(to);
        to.assertTerminated();
        to.assertNoValues();
        to.assertNotComplete();
        to.assertError(ex);
        // FIXME need to decide about adding the value that probably caused the crash in some way
        // assertTrue(ex.getCause().getMessage().contains("Boo!"));
    }

    @Test
    public void testAllFlowable() {
        Flowable<String> obs = Flowable.just("one", "two", "six");
        Subscriber<Boolean> subscriber = TestHelper.mockSubscriber();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toFlowable().subscribe(subscriber);
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber).onNext(true);
        Mockito.verify(subscriber).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testNotAllFlowable() {
        Flowable<String> obs = Flowable.just("one", "two", "three", "six");
        Subscriber<Boolean> subscriber = TestHelper.mockSubscriber();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toFlowable().subscribe(subscriber);
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber).onNext(false);
        Mockito.verify(subscriber).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testEmptyFlowable() {
        Flowable<String> obs = Flowable.empty();
        Subscriber<Boolean> subscriber = TestHelper.mockSubscriber();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toFlowable().subscribe(subscriber);
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber).onNext(true);
        Mockito.verify(subscriber).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testErrorFlowable() {
        Throwable error = new Throwable();
        Flowable<String> obs = Flowable.error(error);
        Subscriber<Boolean> subscriber = TestHelper.mockSubscriber();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toFlowable().subscribe(subscriber);
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber).onError(error);
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testFollowingFirstFlowable() {
        Flowable<Integer> f = Flowable.fromArray(1, 3, 5, 6);
        Flowable<Boolean> allOdd = f.all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                return (i % 2) == 1;
            }
        }).toFlowable();
        Assert.assertFalse(allOdd.blockingFirst());
    }

    @Test(timeout = 5000)
    public void testIssue1935NoUnsubscribeDownstreamFlowable() {
        Flowable<Integer> source = Flowable.just(1).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return false;
            }
        }).toFlowable().flatMap(new Function<Boolean, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Boolean t1) {
                return Flowable.just(2).delay(500, TimeUnit.MILLISECONDS);
            }
        });
        Assert.assertEquals(((Object) (2)), source.blockingFirst());
    }

    @Test
    public void testBackpressureIfNoneRequestedNoneShouldBeDeliveredFlowable() {
        TestSubscriber<Boolean> ts = new TestSubscriber<Boolean>(0L);
        Flowable.empty().all(new Predicate<Object>() {
            @Override
            public boolean test(Object t1) {
                return false;
            }
        }).toFlowable().subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertNotComplete();
    }

    @Test
    public void testBackpressureIfOneRequestedOneShouldBeDeliveredFlowable() {
        TestSubscriber<Boolean> ts = new TestSubscriber<Boolean>(1L);
        Flowable.empty().all(new Predicate<Object>() {
            @Override
            public boolean test(Object t) {
                return false;
            }
        }).toFlowable().subscribe(ts);
        ts.assertTerminated();
        ts.assertNoErrors();
        ts.assertComplete();
        ts.assertValue(true);
    }

    @Test
    public void testPredicateThrowsExceptionAndValueInCauseMessageFlowable() {
        TestSubscriber<Boolean> ts = new TestSubscriber<Boolean>();
        final IllegalArgumentException ex = new IllegalArgumentException();
        Flowable.just("Boo!").all(new Predicate<String>() {
            @Override
            public boolean test(String v) {
                throw ex;
            }
        }).toFlowable().subscribe(ts);
        ts.assertTerminated();
        ts.assertNoValues();
        ts.assertNotComplete();
        ts.assertError(ex);
        // FIXME need to decide about adding the value that probably caused the crash in some way
        // assertTrue(ex.getCause().getMessage().contains("Boo!"));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).all(Functions.alwaysTrue()).toFlowable());
        TestHelper.checkDisposed(Flowable.just(1).all(Functions.alwaysTrue()));
    }

    @Test
    public void predicateThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                }
            }.all(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    throw new TestException();
                }
            }).toFlowable().test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void predicateThrowsObservable() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            all(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    throw new TestException();
                }
            }).toFlowable().test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceFlowable(new Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.all(Functions.alwaysTrue());
            }
        }, false, 1, 1, true);
        TestHelper.checkBadSourceFlowable(new Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.all(Functions.alwaysTrue()).toFlowable();
            }
        }, false, 1, 1, true);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Publisher<Boolean>>() {
            @Override
            public io.reactivex.Publisher<Boolean> apply(Flowable<Object> f) throws Exception {
                return f.all(Functions.alwaysTrue()).toFlowable();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToSingle(new Function<Flowable<Object>, Single<Boolean>>() {
            @Override
            public io.reactivex.Single<Boolean> apply(Flowable<Object> f) throws Exception {
                return f.all(Functions.alwaysTrue());
            }
        });
    }
}

