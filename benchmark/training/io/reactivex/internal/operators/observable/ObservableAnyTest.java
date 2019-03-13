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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableAnyTest {
    @Test
    public void testAnyWithTwoItemsObservable() {
        Observable<Integer> w = Observable.just(1, 2);
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.times(1)).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testIsEmptyWithTwoItemsObservable() {
        Observable<Integer> w = Observable.just(1, 2);
        Observable<Boolean> observable = w.isEmpty().toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testAnyWithOneItemObservable() {
        Observable<Integer> w = Observable.just(1);
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.times(1)).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testIsEmptyWithOneItemObservable() {
        Observable<Integer> w = Observable.just(1);
        Observable<Boolean> observable = w.isEmpty().toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testAnyWithEmptyObservable() {
        Observable<Integer> w = Observable.empty();
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testIsEmptyWithEmptyObservable() {
        Observable<Integer> w = Observable.empty();
        Observable<Boolean> observable = w.isEmpty().toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(true);
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testAnyWithPredicate1Observable() {
        Observable<Integer> w = Observable.just(1, 2, 3);
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 2;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.times(1)).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testExists1Observable() {
        Observable<Integer> w = Observable.just(1, 2, 3);
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 2;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.times(1)).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testAnyWithPredicate2Observable() {
        Observable<Integer> w = Observable.just(1, 2, 3);
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 1;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testAnyWithEmptyAndPredicateObservable() {
        // If the source is empty, always output false.
        Observable<Integer> w = Observable.empty();
        Observable<Boolean> observable = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t) {
                return true;
            }
        }).toObservable();
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testWithFollowingFirstObservable() {
        Observable<Integer> o = Observable.fromArray(1, 3, 5, 6);
        Observable<Boolean> anyEven = o.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                return (i % 2) == 0;
            }
        }).toObservable();
        Assert.assertTrue(anyEven.blockingFirst());
    }

    @Test(timeout = 5000)
    public void testIssue1935NoUnsubscribeDownstreamObservable() {
        Observable<Integer> source = Observable.just(1).isEmpty().toObservable().flatMap(new Function<Boolean, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Boolean t1) {
                return Observable.just(2).delay(500, TimeUnit.MILLISECONDS);
            }
        });
        Assert.assertEquals(((Object) (2)), source.blockingFirst());
    }

    @Test
    public void testPredicateThrowsExceptionAndValueInCauseMessageObservable() {
        TestObserver<Boolean> to = new TestObserver<Boolean>();
        final IllegalArgumentException ex = new IllegalArgumentException();
        Observable.just("Boo!").any(new Predicate<String>() {
            @Override
            public boolean test(String v) {
                throw ex;
            }
        }).subscribe(to);
        to.assertTerminated();
        to.assertNoValues();
        to.assertNotComplete();
        to.assertError(ex);
        // FIXME value as last cause?
        // assertTrue(ex.getCause().getMessage().contains("Boo!"));
    }

    @Test
    public void testAnyWithTwoItems() {
        Observable<Integer> w = Observable.just(1, 2);
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onSuccess(false);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testIsEmptyWithTwoItems() {
        Observable<Integer> w = Observable.just(1, 2);
        Single<Boolean> single = w.isEmpty();
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testAnyWithOneItem() {
        Observable<Integer> w = Observable.just(1);
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onSuccess(false);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testIsEmptyWithOneItem() {
        Observable<Integer> w = Observable.just(1);
        Single<Boolean> single = w.isEmpty();
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testAnyWithEmpty() {
        Observable<Integer> w = Observable.empty();
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testIsEmptyWithEmpty() {
        Observable<Integer> w = Observable.empty();
        Single<Boolean> single = w.isEmpty();
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testAnyWithPredicate1() {
        Observable<Integer> w = Observable.just(1, 2, 3);
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 2;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onSuccess(false);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testExists1() {
        Observable<Integer> w = Observable.just(1, 2, 3);
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 2;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onSuccess(false);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testAnyWithPredicate2() {
        Observable<Integer> w = Observable.just(1, 2, 3);
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 1;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testAnyWithEmptyAndPredicate() {
        // If the source is empty, always output false.
        Observable<Integer> w = Observable.empty();
        Single<Boolean> single = w.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t) {
                return true;
            }
        });
        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testWithFollowingFirst() {
        Observable<Integer> o = Observable.fromArray(1, 3, 5, 6);
        Single<Boolean> anyEven = o.any(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                return (i % 2) == 0;
            }
        });
        Assert.assertTrue(anyEven.blockingGet());
    }

    @Test(timeout = 5000)
    public void testIssue1935NoUnsubscribeDownstream() {
        Observable<Integer> source = Observable.just(1).isEmpty().flatMapObservable(new Function<Boolean, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Boolean t1) {
                return Observable.just(2).delay(500, TimeUnit.MILLISECONDS);
            }
        });
        Assert.assertEquals(((Object) (2)), source.blockingFirst());
    }

    @Test
    public void testPredicateThrowsExceptionAndValueInCauseMessage() {
        TestObserver<Boolean> to = new TestObserver<Boolean>();
        final IllegalArgumentException ex = new IllegalArgumentException();
        Observable.just("Boo!").any(new Predicate<String>() {
            @Override
            public boolean test(String v) {
                throw ex;
            }
        }).subscribe(to);
        to.assertTerminated();
        to.assertNoValues();
        to.assertNotComplete();
        to.assertError(ex);
        // FIXME value as last cause?
        // assertTrue(ex.getCause().getMessage().contains("Boo!"));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).any(Functions.alwaysTrue()).toObservable());
        TestHelper.checkDisposed(Observable.just(1).any(Functions.alwaysTrue()));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Boolean>>() {
            @Override
            public io.reactivex.ObservableSource<Boolean> apply(Observable<Object> o) throws Exception {
                return o.any(Functions.alwaysTrue()).toObservable();
            }
        });
        checkDoubleOnSubscribeObservableToSingle(new Function<Observable<Object>, SingleSource<Boolean>>() {
            @Override
            public io.reactivex.SingleSource<Boolean> apply(Observable<Object> o) throws Exception {
                return o.any(Functions.alwaysTrue());
            }
        });
    }

    @Test
    public void predicateThrowsSuppressOthers() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new IOException());
                    observer.onComplete();
                }
            }.any(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    throw new TestException();
                }
            }).toObservable().test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSourceSingle() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onNext(1);
                    observer.onError(new TestException("Second"));
                    observer.onComplete();
                }
            }.any(Functions.alwaysTrue()).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

