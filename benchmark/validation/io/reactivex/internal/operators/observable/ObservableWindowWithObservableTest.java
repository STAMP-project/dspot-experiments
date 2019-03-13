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


import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;

import static io.reactivex.TestHelper.RACE_LONG_LOOPS;
import static io.reactivex.TestHelper.assertError;
import static io.reactivex.TestHelper.assertUndeliverable;
import static io.reactivex.TestHelper.checkDisposed;
import static io.reactivex.TestHelper.compositeList;
import static io.reactivex.TestHelper.race;
import static io.reactivex.TestHelper.trackPluginErrors;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


public class ObservableWindowWithObservableTest {
    @Test
    public void testWindowViaObservableNormal1() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> boundary = PublishSubject.create();
        final Observer<Object> o = mockObserver();
        final List<Observer<Object>> values = new java.util.ArrayList<Observer<Object>>();
        Observer<Observable<Integer>> wo = new DefaultObserver<Observable<Integer>>() {
            @Override
            public void onNext(Observable<Integer> args) {
                final Observer<Object> mo = TestHelper.mockObserver();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onComplete() {
                o.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        int n = 30;
        for (int i = 0; i < n; i++) {
            source.onNext(i);
            if (((i % 3) == 2) && (i < (n - 1))) {
                boundary.onNext((i / 3));
            }
        }
        source.onComplete();
        org.mockito.Mockito.verify(o, org.mockito.Mockito.never()).onError(any(Throwable.class));
        Assert.assertEquals((n / 3), values.size());
        int j = 0;
        for (Observer<Object> mo : values) {
            org.mockito.Mockito.verify(mo, org.mockito.Mockito.never()).onError(any(Throwable.class));
            for (int i = 0; i < 3; i++) {
                verify(mo).onNext((j + i));
            }
            verify(mo).onComplete();
            j += 3;
        }
        verify(o).onComplete();
    }

    @Test
    public void testWindowViaObservableBoundaryCompletes() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> boundary = PublishSubject.create();
        final Observer<Object> o = mockObserver();
        final List<Observer<Object>> values = new java.util.ArrayList<Observer<Object>>();
        Observer<Observable<Integer>> wo = new DefaultObserver<Observable<Integer>>() {
            @Override
            public void onNext(Observable<Integer> args) {
                final Observer<Object> mo = TestHelper.mockObserver();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onComplete() {
                o.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        int n = 30;
        for (int i = 0; i < n; i++) {
            source.onNext(i);
            if (((i % 3) == 2) && (i < (n - 1))) {
                boundary.onNext((i / 3));
            }
        }
        boundary.onComplete();
        Assert.assertEquals((n / 3), values.size());
        int j = 0;
        for (Observer<Object> mo : values) {
            for (int i = 0; i < 3; i++) {
                verify(mo).onNext((j + i));
            }
            verify(mo).onComplete();
            org.mockito.Mockito.verify(mo, org.mockito.Mockito.never()).onError(any(Throwable.class));
            j += 3;
        }
        verify(o).onComplete();
        org.mockito.Mockito.verify(o, org.mockito.Mockito.never()).onError(any(Throwable.class));
    }

    @Test
    public void testWindowViaObservableBoundaryThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> boundary = PublishSubject.create();
        final Observer<Object> o = mockObserver();
        final List<Observer<Object>> values = new java.util.ArrayList<Observer<Object>>();
        Observer<Observable<Integer>> wo = new DefaultObserver<Observable<Integer>>() {
            @Override
            public void onNext(Observable<Integer> args) {
                final Observer<Object> mo = TestHelper.mockObserver();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onComplete() {
                o.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        boundary.onError(new TestException());
        Assert.assertEquals(1, values.size());
        Observer<Object> mo = values.get(0);
        verify(mo).onNext(0);
        verify(mo).onNext(1);
        verify(mo).onNext(2);
        verify(mo).onError(org.mockito.ArgumentMatchers.any(TestException.class));
        org.mockito.Mockito.verify(o, org.mockito.Mockito.never()).onComplete();
        verify(o).onError(org.mockito.ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testWindowViaObservableSourceThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> boundary = PublishSubject.create();
        final Observer<Object> o = mockObserver();
        final List<Observer<Object>> values = new java.util.ArrayList<Observer<Object>>();
        Observer<Observable<Integer>> wo = new DefaultObserver<Observable<Integer>>() {
            @Override
            public void onNext(Observable<Integer> args) {
                final Observer<Object> mo = TestHelper.mockObserver();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onComplete() {
                o.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        source.onError(new TestException());
        Assert.assertEquals(1, values.size());
        Observer<Object> mo = values.get(0);
        verify(mo).onNext(0);
        verify(mo).onNext(1);
        verify(mo).onNext(2);
        verify(mo).onError(org.mockito.ArgumentMatchers.any(TestException.class));
        org.mockito.Mockito.verify(o, org.mockito.Mockito.never()).onComplete();
        verify(o).onError(org.mockito.ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testWindowNoDuplication() {
        final PublishSubject<Integer> source = PublishSubject.create();
        final TestObserver<Integer> tow = new TestObserver<Integer>() {
            boolean once;

            @Override
            public void onNext(Integer t) {
                if (!(once)) {
                    once = true;
                    source.onNext(2);
                }
                super.onNext(t);
            }
        };
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>() {
            @Override
            public void onNext(Observable<Integer> t) {
                t.subscribe(tow);
                super.onNext(t);
            }
        };
        source.window(new java.util.concurrent.Callable<Observable<Object>>() {
            @Override
            public Observable<Object> call() {
                return io.reactivex.Observable.never();
            }
        }).subscribe(to);
        source.onNext(1);
        source.onComplete();
        to.assertValueCount(1);
        tow.assertValues(1, 2);
    }

    @Test
    public void testWindowViaObservableNoUnsubscribe() {
        Observable<Integer> source = range(1, 10);
        java.util.concurrent.Callable<Observable<String>> boundary = new java.util.concurrent.Callable<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return io.reactivex.Observable.empty();
            }
        };
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(boundary).subscribe(to);
        // 2.0.2 - not anymore
        // assertTrue("Not cancelled!", ts.isCancelled());
        to.assertComplete();
    }

    @Test
    public void testBoundaryUnsubscribedOnMainCompletion() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> boundary = PublishSubject.create();
        java.util.concurrent.Callable<Observable<Integer>> boundaryFunc = new java.util.concurrent.Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return boundary;
            }
        };
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(boundaryFunc).subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(boundary.hasObservers());
        source.onComplete();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(boundary.hasObservers());
        to.assertComplete();
        to.assertNoErrors();
        to.assertValueCount(1);
    }

    @Test
    public void testMainUnsubscribedOnBoundaryCompletion() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> boundary = PublishSubject.create();
        java.util.concurrent.Callable<Observable<Integer>> boundaryFunc = new java.util.concurrent.Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return boundary;
            }
        };
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(boundaryFunc).subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(boundary.hasObservers());
        boundary.onComplete();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(boundary.hasObservers());
        to.assertComplete();
        to.assertNoErrors();
        to.assertValueCount(1);
    }

    @Test
    public void testChildUnsubscribed() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> boundary = PublishSubject.create();
        java.util.concurrent.Callable<Observable<Integer>> boundaryFunc = new java.util.concurrent.Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return boundary;
            }
        };
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(boundaryFunc).subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(boundary.hasObservers());
        to.dispose();
        Assert.assertTrue(source.hasObservers());
        Assert.assertFalse(boundary.hasObservers());
        to.values().get(0).test(true);
        Assert.assertFalse(source.hasObservers());
        to.assertNotComplete();
        to.assertNoErrors();
        to.assertValueCount(1);
    }

    @Test
    public void newBoundaryCalledAfterWindowClosed() {
        final AtomicInteger calls = new AtomicInteger();
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> boundary = PublishSubject.create();
        java.util.concurrent.Callable<Observable<Integer>> boundaryFunc = new java.util.concurrent.Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                calls.getAndIncrement();
                return boundary;
            }
        };
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(boundaryFunc).subscribe(to);
        source.onNext(1);
        boundary.onNext(1);
        Assert.assertTrue(boundary.hasObservers());
        source.onNext(2);
        boundary.onNext(2);
        Assert.assertTrue(boundary.hasObservers());
        source.onNext(3);
        boundary.onNext(3);
        Assert.assertTrue(boundary.hasObservers());
        source.onNext(4);
        source.onComplete();
        to.assertNoErrors();
        to.assertValueCount(4);
        to.assertComplete();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(boundary.hasObservers());
    }

    @Test
    public void boundaryDispose() {
        checkDisposed(never().window(never()));
    }

    @Test
    public void boundaryDispose2() {
        checkDisposed(never().window(Functions.justCallable(never())));
    }

    @Test
    public void boundaryOnError() {
        TestObserver<Object> to = error(new TestException()).window(never()).flatMap(Functions.<Observable<Object>>identity(), true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = compositeList(to.errors().get(0));
        assertError(errors, 0, TestException.class);
    }

    @Test
    public void mainError() {
        error(new TestException()).window(Functions.justCallable(never())).test().assertError(TestException.class);
    }

    @Test
    public void innerBadSource() {
        io.reactivex.TestHelper.checkBadSourceObservable(new io.reactivex.functions.Function<Observable<Integer>, Object>() {
            @Override
            public Object apply(Observable<Integer> o) throws Exception {
                return io.reactivex.Observable.just(1).window(o).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, ((Object[]) (null)));
        io.reactivex.TestHelper.checkBadSourceObservable(new io.reactivex.functions.Function<Observable<Integer>, Object>() {
            @Override
            public Object apply(Observable<Integer> o) throws Exception {
                return io.reactivex.Observable.just(1).window(Functions.justCallable(o)).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, ((Object[]) (null)));
    }

    @Test
    public void reentrant() {
        final Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(BehaviorSubject.createDefault(1)).flatMap(new io.reactivex.functions.Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void reentrantCallable() {
        final Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(new java.util.concurrent.Callable<Observable<Integer>>() {
            boolean once;

            @Override
            public Observable<Integer> call() throws Exception {
                if (!(once)) {
                    once = true;
                    return BehaviorSubject.createDefault(1);
                }
                return io.reactivex.Observable.never();
            }
        }).flatMap(new io.reactivex.functions.Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void badSource() {
        io.reactivex.TestHelper.checkBadSourceObservable(new io.reactivex.functions.Function<Observable<Object>, Object>() {
            @Override
            public Object apply(Observable<Object> o) throws Exception {
                return o.window(io.reactivex.Observable.never()).flatMap(new Function<Observable<Object>, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(Observable<Object> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, 1);
    }

    @Test
    public void badSourceCallable() {
        io.reactivex.TestHelper.checkBadSourceObservable(new io.reactivex.functions.Function<Observable<Object>, Object>() {
            @Override
            public Object apply(Observable<Object> o) throws Exception {
                return o.window(Functions.justCallable(io.reactivex.Observable.never())).flatMap(new Function<Observable<Object>, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(Observable<Object> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, 1);
    }

    @Test
    public void boundaryError() {
        BehaviorSubject.createDefault(1).window(Functions.justCallable(error(new TestException()))).test().assertValueCount(1).assertNotComplete().assertError(TestException.class);
    }

    @Test
    public void boundaryCallableCrashOnCall2() {
        BehaviorSubject.createDefault(1).window(new java.util.concurrent.Callable<Observable<Integer>>() {
            int calls;

            @Override
            public Observable<Integer> call() throws Exception {
                if ((++(calls)) == 2) {
                    throw new TestException();
                }
                return io.reactivex.Observable.just(1);
            }
        }).test().assertError(TestException.class).assertNotComplete();
    }

    @Test
    public void oneWindow() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Observable<Integer>> to = BehaviorSubject.createDefault(1).window(Functions.justCallable(ps)).take(1).test();
        ps.onNext(1);
        to.assertValueCount(1).assertNoErrors().assertComplete();
    }

    @Test
    public void boundaryDirectDoubleOnSubscribe() {
        io.reactivex.TestHelper.checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, Observable<Observable<Object>>>() {
            @Override
            public Observable<Observable<Object>> apply(Observable<Object> f) throws Exception {
                return f.window(io.reactivex.Observable.never()).takeLast(1);
            }
        });
    }

    @Test
    public void upstreamDisposedWhenOutputsDisposed() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> boundary = PublishSubject.create();
        TestObserver<Integer> to = source.window(boundary).take(1).flatMap(new io.reactivex.functions.Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> w) throws Exception {
                return w.take(1);
            }
        }).test();
        source.onNext(1);
        Assert.assertFalse("source not disposed", source.hasObservers());
        Assert.assertFalse("boundary not disposed", boundary.hasObservers());
        to.assertResult(1);
    }

    @Test
    public void mainAndBoundaryBothError() {
        List<Throwable> errors = trackPluginErrors();
        try {
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            TestObserver<Observable<Object>> to = error(new TestException("main")).window(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    ref.set(observer);
                }
            }).test();
            to.assertValueCount(1).assertError(TestException.class).assertErrorMessage("main").assertNotComplete();
            ref.get().onError(new TestException("inner"));
            io.reactivex.TestHelper.assertUndeliverable(errors, 0, TestException.class, "inner");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mainCompleteBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = trackPluginErrors();
            try {
                final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
                final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
                TestObserver<Observable<Object>> to = new Observable<Object>() {
                    @Override
                    protected void subscribeActual(Observer<? extends Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        refMain.set(observer);
                    }
                }.window(new Observable<Object>() {
                    @Override
                    protected void subscribeActual(Observer<? extends Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        ref.set(observer);
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        refMain.get().onComplete();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onError(ex);
                    }
                };
                race(r1, r2);
                to.assertValueCount(1).assertTerminated();
                if (!(errors.isEmpty())) {
                    assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void mainNextBoundaryNextRace() {
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            TestObserver<Observable<Object>> to = new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    refMain.set(observer);
                }
            }.window(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    ref.set(observer);
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    refMain.get().onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ref.get().onNext(1);
                }
            };
            race(r1, r2);
            to.assertValueCount(2).assertNotComplete().assertNoErrors();
        }
    }

    @Test
    public void takeOneAnotherBoundary() {
        final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
        final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
        TestObserver<Observable<Object>> to = new Observable<Object>() {
            @Override
            protected void subscribeActual(Observer<? extends Object> observer) {
                observer.onSubscribe(Disposables.empty());
                refMain.set(observer);
            }
        }.window(new Observable<Object>() {
            @Override
            protected void subscribeActual(Observer<? extends Object> observer) {
                observer.onSubscribe(Disposables.empty());
                ref.set(observer);
            }
        }).test();
        to.assertValueCount(1).assertNotTerminated().cancel();
        ref.get().onNext(1);
        to.assertValueCount(1).assertNotTerminated();
    }

    @Test
    public void disposeMainBoundaryCompleteRace() {
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            final TestObserver<Observable<Object>> to = new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    refMain.set(observer);
                }
            }.window(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    final AtomicInteger counter = new AtomicInteger();
                    observer.onSubscribe(new Disposable() {
                        @Override
                        public void dispose() {
                            // about a microsecond
                            for (int i = 0; i < 100; i++) {
                                counter.incrementAndGet();
                            }
                        }

                        @Override
                        public boolean isDisposed() {
                            return false;
                        }
                    });
                    ref.set(observer);
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    Observer<Object> o = ref.get();
                    o.onNext(1);
                    o.onComplete();
                }
            };
            race(r1, r2);
        }
    }

    @Test
    public void disposeMainBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            final TestObserver<Observable<Object>> to = new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    refMain.set(observer);
                }
            }.window(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    final AtomicInteger counter = new AtomicInteger();
                    observer.onSubscribe(new Disposable() {
                        @Override
                        public void dispose() {
                            // about a microsecond
                            for (int i = 0; i < 100; i++) {
                                counter.incrementAndGet();
                            }
                        }

                        @Override
                        public boolean isDisposed() {
                            return false;
                        }
                    });
                    ref.set(observer);
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    Observer<Object> o = ref.get();
                    o.onNext(1);
                    o.onError(ex);
                }
            };
            race(r1, r2);
        }
    }

    @Test
    public void boundarySupplierDoubleOnSubscribe() {
        io.reactivex.TestHelper.checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, Observable<Observable<Object>>>() {
            @Override
            public Observable<Observable<Object>> apply(Observable<Object> f) throws Exception {
                return f.window(Functions.justCallable(io.reactivex.Observable.never())).takeLast(1);
            }
        });
    }

    @Test
    public void selectorUpstreamDisposedWhenOutputsDisposed() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> boundary = PublishSubject.create();
        TestObserver<Integer> to = source.window(Functions.justCallable(boundary)).take(1).flatMap(new io.reactivex.functions.Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> w) throws Exception {
                return w.take(1);
            }
        }).test();
        source.onNext(1);
        Assert.assertFalse("source not disposed", source.hasObservers());
        Assert.assertFalse("boundary not disposed", boundary.hasObservers());
        to.assertResult(1);
    }

    @Test
    public void supplierMainAndBoundaryBothError() {
        List<Throwable> errors = trackPluginErrors();
        try {
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            TestObserver<Observable<Object>> to = error(new TestException("main")).window(Functions.justCallable(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    ref.set(observer);
                }
            })).test();
            to.assertValueCount(1).assertError(TestException.class).assertErrorMessage("main").assertNotComplete();
            ref.get().onError(new TestException("inner"));
            io.reactivex.TestHelper.assertUndeliverable(errors, 0, TestException.class, "inner");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void supplierMainCompleteBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = trackPluginErrors();
            try {
                final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
                final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
                TestObserver<Observable<Object>> to = new Observable<Object>() {
                    @Override
                    protected void subscribeActual(Observer<? extends Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        refMain.set(observer);
                    }
                }.window(Functions.justCallable(new Observable<Object>() {
                    @Override
                    protected void subscribeActual(Observer<? extends Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        ref.set(observer);
                    }
                })).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        refMain.get().onComplete();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onError(ex);
                    }
                };
                race(r1, r2);
                to.assertValueCount(1).assertTerminated();
                if (!(errors.isEmpty())) {
                    assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void supplierMainNextBoundaryNextRace() {
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            TestObserver<Observable<Object>> to = new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    refMain.set(observer);
                }
            }.window(Functions.justCallable(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    ref.set(observer);
                }
            })).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    refMain.get().onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ref.get().onNext(1);
                }
            };
            race(r1, r2);
            to.assertValueCount(2).assertNotComplete().assertNoErrors();
        }
    }

    @Test
    public void supplierTakeOneAnotherBoundary() {
        final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
        final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
        TestObserver<Observable<Object>> to = new Observable<Object>() {
            @Override
            protected void subscribeActual(Observer<? extends Object> observer) {
                observer.onSubscribe(Disposables.empty());
                refMain.set(observer);
            }
        }.window(Functions.justCallable(new Observable<Object>() {
            @Override
            protected void subscribeActual(Observer<? extends Object> observer) {
                observer.onSubscribe(Disposables.empty());
                ref.set(observer);
            }
        })).test();
        to.assertValueCount(1).assertNotTerminated().cancel();
        ref.get().onNext(1);
        to.assertValueCount(1).assertNotTerminated();
    }

    @Test
    public void supplierDisposeMainBoundaryCompleteRace() {
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
            final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
            final TestObserver<Observable<Object>> to = new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    observer.onSubscribe(Disposables.empty());
                    refMain.set(observer);
                }
            }.window(Functions.justCallable(new Observable<Object>() {
                @Override
                protected void subscribeActual(Observer<? extends Object> observer) {
                    final AtomicInteger counter = new AtomicInteger();
                    observer.onSubscribe(new Disposable() {
                        @Override
                        public void dispose() {
                            // about a microsecond
                            for (int i = 0; i < 100; i++) {
                                counter.incrementAndGet();
                            }
                        }

                        @Override
                        public boolean isDisposed() {
                            return false;
                        }
                    });
                    ref.set(observer);
                }
            })).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    Observer<Object> o = ref.get();
                    o.onNext(1);
                    o.onComplete();
                }
            };
            race(r1, r2);
        }
    }

    @Test
    public void supplierDisposeMainBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = trackPluginErrors();
            try {
                final AtomicReference<Observer<? super Object>> refMain = new AtomicReference<Observer<? super Object>>();
                final AtomicReference<Observer<? super Object>> ref = new AtomicReference<Observer<? super Object>>();
                final TestObserver<Observable<Object>> to = new Observable<Object>() {
                    @Override
                    protected void subscribeActual(Observer<? extends Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        refMain.set(observer);
                    }
                }.window(new java.util.concurrent.Callable<ObservableSource<Object>>() {
                    int count;

                    @Override
                    public io.reactivex.ObservableSource<Object> call() throws Exception {
                        if ((++(count)) > 1) {
                            return Observable.never();
                        }
                        return new Observable<Object>() {
                            @Override
                            protected void subscribeActual(Observer<? extends Object> observer) {
                                final AtomicInteger counter = new AtomicInteger();
                                observer.onSubscribe(new Disposable() {
                                    @Override
                                    public void dispose() {
                                        // about a microsecond
                                        for (int i = 0; i < 100; i++) {
                                            counter.incrementAndGet();
                                        }
                                    }

                                    @Override
                                    public boolean isDisposed() {
                                        return false;
                                    }
                                });
                                ref.set(observer);
                            }
                        };
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        to.cancel();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        Observer<Object> o = ref.get();
                        o.onNext(1);
                        o.onError(ex);
                    }
                };
                race(r1, r2);
                if (!(errors.isEmpty())) {
                    assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }
}

