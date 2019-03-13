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
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ObservableFlatMapMaybeTest {
    @Test
    public void normal() {
        range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalEmpty() {
        range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.empty();
            }
        }).test().assertResult();
    }

    @Test
    public void normalDelayError() {
        range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }, true).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalAsync() {
        range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v).subscribeOn(Schedulers.computation());
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).assertNoErrors().assertComplete();
    }

    @Test
    public void mapperThrowsObservable() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        to.assertFailure(TestException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void mapperReturnsNullObservable() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return null;
            }
        }).test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        to.assertFailure(NullPointerException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void normalDelayErrorAll() {
        TestObserver<Integer> to = range(1, 10).concatWith(<Integer>error(new TestException())).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.error(new TestException());
            }
        }, true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        for (int i = 0; i < 11; i++) {
            TestHelper.assertError(errors, i, TestException.class);
        }
    }

    @Test
    public void takeAsync() {
        range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v).subscribeOn(Schedulers.computation());
            }
        }).take(2).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(2).assertValueSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).assertNoErrors().assertComplete();
    }

    @Test
    public void take() {
        range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }).take(2).test().assertResult(1, 2);
    }

    @Test
    public void middleError() {
        fromArray(new String[]{ "1", "a", "2" }).flatMapMaybe(new io.reactivex.functions.Function<String, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(final String s) throws NumberFormatException {
                // return Single.just(Integer.valueOf(s)); //This works
                return Maybe.fromCallable(new Callable<Integer>() {
                    @Override
                    public Integer call() throws NumberFormatException {
                        return Integer.valueOf(s);
                    }
                });
            }
        }).test().assertFailure(NumberFormatException.class, 1);
    }

    @Test
    public void asyncFlatten() {
        range(1, 1000).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(1).subscribeOn(Schedulers.computation());
            }
        }).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(500).assertNoErrors().assertComplete();
    }

    @Test
    public void asyncFlattenNone() {
        range(1, 1000).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.<Integer>empty().subscribeOn(Schedulers.computation());
            }
        }).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }

    @Test
    public void successError() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = range(1, 2).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                if (v == 2) {
                    return ps.singleElement();
                }
                return Maybe.error(new TestException());
            }
        }, true).test();
        ps.onNext(1);
        ps.onComplete();
        to.assertFailure(TestException.class, 1);
    }

    @Test
    public void completeError() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = range(1, 2).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                if (v == 2) {
                    return ps.singleElement();
                }
                return Maybe.error(new TestException());
            }
        }, true).test();
        ps.onComplete();
        to.assertFailure(TestException.class);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishSubject.<Integer>create().flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.<Integer>empty();
            }
        }));
    }

    @Test
    public void innerSuccessCompletesAfterMain() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = just(1).flatMapMaybe(Functions.justFunction(ps.singleElement())).test();
        ps.onNext(2);
        ps.onComplete();
        to.assertResult(2);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Object> f) throws Exception {
                return f.flatMapMaybe(Functions.justFunction(Maybe.just(2)));
            }
        });
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onError(new TestException("Second"));
                }
            }.flatMapMaybe(Functions.justFunction(Maybe.just(2))).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badInnerSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            just(1).flatMapMaybe(Functions.justFunction(new Maybe<Integer>() {
                @Override
                protected void subscribeActual(MaybeObserver<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onError(new TestException("Second"));
                }
            })).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void emissionQueueTrigger() {
        final PublishSubject<Integer> ps1 = PublishSubject.create();
        final PublishSubject<Integer> ps2 = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps2.onNext(2);
                    ps2.onComplete();
                }
            }
        };
        Observable.just(ps1, ps2).flatMapMaybe(new io.reactivex.functions.Function<PublishSubject<Integer>, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(PublishSubject<Integer> v) throws Exception {
                return v.singleElement();
            }
        }).subscribe(to);
        ps1.onNext(1);
        ps1.onComplete();
        to.assertResult(1, 2);
    }

    @Test
    public void emissionQueueTrigger2() {
        final PublishSubject<Integer> ps1 = PublishSubject.create();
        final PublishSubject<Integer> ps2 = PublishSubject.create();
        final PublishSubject<Integer> ps3 = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps2.onNext(2);
                    ps2.onComplete();
                }
            }
        };
        Observable.just(ps1, ps2, ps3).flatMapMaybe(new io.reactivex.functions.Function<PublishSubject<Integer>, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(PublishSubject<Integer> v) throws Exception {
                return v.singleElement();
            }
        }).subscribe(to);
        ps1.onNext(1);
        ps1.onComplete();
        ps3.onComplete();
        to.assertResult(1, 2);
    }

    @Test
    public void disposeInner() {
        final TestObserver<Object> to = new TestObserver<Object>();
        just(1).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Object>>() {
            @Override
            public io.reactivex.MaybeSource<Object> apply(Integer v) throws Exception {
                return new Maybe<Object>() {
                    @Override
                    protected void subscribeActual(MaybeObserver<? super Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        Assert.assertFalse(isDisposed());
                        to.dispose();
                        Assert.assertTrue(isDisposed());
                    }
                };
            }
        }).subscribe(to);
        to.assertEmpty();
    }
}

