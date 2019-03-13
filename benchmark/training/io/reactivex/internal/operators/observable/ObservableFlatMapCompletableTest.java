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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ObservableFlatMapCompletableTest {
    @Test
    public void normalObservable() {
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }).toObservable().test().assertResult();
    }

    @Test
    public void mapperThrowsObservable() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).<Integer>toObservable().test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        to.assertFailure(TestException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void mapperReturnsNullObservable() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return null;
            }
        }).<Integer>toObservable().test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        to.assertFailure(NullPointerException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void normalDelayErrorObservable() {
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }, true).toObservable().test().assertResult();
    }

    @Test
    public void normalAsyncObservable() {
        Observable.range(1, 1000).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Observable.range(1, 100).subscribeOn(Schedulers.computation()).ignoreElements();
            }
        }).toObservable().test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }

    @Test
    public void normalDelayErrorAllObservable() {
        TestObserver<Integer> to = Observable.range(1, 10).concatWith(Observable.<Integer>error(new TestException())).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.error(new TestException());
            }
        }, true).<Integer>toObservable().test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        for (int i = 0; i < 11; i++) {
            TestHelper.assertError(errors, i, TestException.class);
        }
    }

    @Test
    public void normalDelayInnerErrorAllObservable() {
        TestObserver<Integer> to = Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.error(new TestException());
            }
        }, true).<Integer>toObservable().test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        for (int i = 0; i < 10; i++) {
            TestHelper.assertError(errors, i, TestException.class);
        }
    }

    @Test
    public void normalNonDelayErrorOuterObservable() {
        Observable.range(1, 10).concatWith(Observable.<Integer>error(new TestException())).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }, false).toObservable().test().assertFailure(TestException.class);
    }

    @Test
    public void fusedObservable() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }).<Integer>toObservable().subscribe(to);
        to.assertOf(ObserverFusion.<Integer>assertFuseable()).assertOf(ObserverFusion.<Integer>assertFusionMode(ASYNC)).assertResult();
    }

    @Test
    public void disposedObservable() {
        TestHelper.checkDisposed(Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }).toObservable());
    }

    @Test
    public void normal() {
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }).test().assertResult();
    }

    @Test
    public void mapperThrows() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Void> to = ps.flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        to.assertFailure(TestException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void mapperReturnsNull() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Void> to = ps.flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return null;
            }
        }).test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        to.assertFailure(NullPointerException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void normalDelayError() {
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }, true).test().assertResult();
    }

    @Test
    public void normalAsync() {
        Observable.range(1, 1000).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Observable.range(1, 100).subscribeOn(Schedulers.computation()).ignoreElements();
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }

    @Test
    public void normalDelayErrorAll() {
        TestObserver<Void> to = Observable.range(1, 10).concatWith(Observable.<Integer>error(new TestException())).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.error(new TestException());
            }
        }, true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        for (int i = 0; i < 11; i++) {
            TestHelper.assertError(errors, i, TestException.class);
        }
    }

    @Test
    public void normalDelayInnerErrorAll() {
        TestObserver<Void> to = Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.error(new TestException());
            }
        }, true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        for (int i = 0; i < 10; i++) {
            TestHelper.assertError(errors, i, TestException.class);
        }
    }

    @Test
    public void normalNonDelayErrorOuter() {
        Observable.range(1, 10).concatWith(Observable.<Integer>error(new TestException())).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }, false).test().assertFailure(TestException.class);
    }

    @Test
    public void fused() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }).<Integer>toObservable().subscribe(to);
        to.assertOf(ObserverFusion.<Integer>assertFuseable()).assertOf(ObserverFusion.<Integer>assertFusionMode(ASYNC)).assertResult();
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }));
    }

    @Test
    public void innerObserver() {
        Observable.range(1, 3).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return new Completable() {
                    @Override
                    protected void subscribeActual(CompletableObserver observer) {
                        observer.onSubscribe(Disposables.empty());
                        Assert.assertFalse(isDisposed());
                        dispose();
                        Assert.assertTrue(isDisposed());
                    }
                };
            }
        }).test();
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceObservable(new io.reactivex.functions.Function<Observable<Integer>, Object>() {
            @Override
            public Object apply(Observable<Integer> o) throws Exception {
                return o.flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
                    @Override
                    public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                        return Completable.complete();
                    }
                });
            }
        }, false, 1, null);
    }

    @Test
    public void fusedInternalsObservable() {
        Observable.range(1, 10).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.complete();
            }
        }).toObservable().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                QueueDisposable<?> qd = ((QueueDisposable<?>) (d));
                try {
                    Assert.assertNull(qd.poll());
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
                Assert.assertTrue(qd.isEmpty());
                qd.clear();
            }

            @Override
            public void onNext(Object t) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void innerObserverObservable() {
        Observable.range(1, 3).flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return new Completable() {
                    @Override
                    protected void subscribeActual(CompletableObserver observer) {
                        observer.onSubscribe(Disposables.empty());
                        Assert.assertFalse(isDisposed());
                        dispose();
                        Assert.assertTrue(isDisposed());
                    }
                };
            }
        }).toObservable().test();
    }

    @Test
    public void badSourceObservable() {
        checkBadSourceObservable(new io.reactivex.functions.Function<Observable<Integer>, Object>() {
            @Override
            public Object apply(Observable<Integer> o) throws Exception {
                return o.flatMapCompletable(new io.reactivex.functions.Function<Integer, CompletableSource>() {
                    @Override
                    public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                        return Completable.complete();
                    }
                }).toObservable();
            }
        }, false, 1, null);
    }
}

