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
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableAllTest {
    @Test
    public void testAllObservable() {
        Observable<String> obs = Observable.just("one", "two", "six");
        Observer<Boolean> observer = mockObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toObservable().subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onNext(true);
        Mockito.verify(observer).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testNotAllObservable() {
        Observable<String> obs = Observable.just("one", "two", "three", "six");
        Observer<Boolean> observer = mockObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toObservable().subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onNext(false);
        Mockito.verify(observer).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testEmptyObservable() {
        Observable<String> obs = Observable.empty();
        Observer<Boolean> observer = mockObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toObservable().subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onNext(true);
        Mockito.verify(observer).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testErrorObservable() {
        Throwable error = new Throwable();
        Observable<String> obs = Observable.error(error);
        Observer<Boolean> observer = mockObserver();
        obs.all(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return (s.length()) == 3;
            }
        }).toObservable().subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer).onError(error);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testFollowingFirstObservable() {
        Observable<Integer> o = Observable.fromArray(1, 3, 5, 6);
        Observable<Boolean> allOdd = o.all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                return (i % 2) == 1;
            }
        }).toObservable();
        Assert.assertFalse(allOdd.blockingFirst());
    }

    @Test(timeout = 5000)
    public void testIssue1935NoUnsubscribeDownstreamObservable() {
        Observable<Integer> source = Observable.just(1).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return false;
            }
        }).toObservable().flatMap(new Function<Boolean, Observable<Integer>>() {
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
        Observable.just("Boo!").all(new Predicate<String>() {
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
    public void testAll() {
        Observable<String> obs = Observable.just("one", "two", "six");
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
        Observable<String> obs = Observable.just("one", "two", "three", "six");
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
        Observable<String> obs = Observable.empty();
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
        Observable<String> obs = Observable.error(error);
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
        Observable<Integer> o = Observable.fromArray(1, 3, 5, 6);
        Single<Boolean> allOdd = o.all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                return (i % 2) == 1;
            }
        });
        Assert.assertFalse(allOdd.blockingGet());
    }

    @Test(timeout = 5000)
    public void testIssue1935NoUnsubscribeDownstream() {
        Observable<Integer> source = Observable.just(1).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return false;
            }
        }).flatMapObservable(new Function<Boolean, Observable<Integer>>() {
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
        Observable.just("Boo!").all(new Predicate<String>() {
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
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).all(Functions.alwaysTrue()).toObservable());
        TestHelper.checkDisposed(Observable.just(1).all(Functions.alwaysTrue()));
    }

    @Test
    public void predicateThrowsObservable() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.all(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    throw new TestException();
                }
            }).toObservable().test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void predicateThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            all(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    throw new TestException();
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

