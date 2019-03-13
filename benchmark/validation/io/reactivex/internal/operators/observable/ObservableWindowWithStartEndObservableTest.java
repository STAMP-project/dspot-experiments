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


import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class ObservableWindowWithStartEndObservableTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    @Test
    public void testObservableBasedOpenerAndCloser() {
        final List<String> list = new ArrayList<String>();
        final List<List<String>> lists = new ArrayList<List<String>>();
        Observable<String> source = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> innerObserver) {
                innerObserver.onSubscribe(Disposables.empty());
                push(innerObserver, "one", 10);
                push(innerObserver, "two", 60);
                push(innerObserver, "three", 110);
                push(innerObserver, "four", 160);
                push(innerObserver, "five", 210);
                complete(innerObserver, 500);
            }
        });
        Observable<Object> openings = Observable.unsafeCreate(new ObservableSource<Object>() {
            @Override
            public void subscribe(Observer<? super Object> innerObserver) {
                innerObserver.onSubscribe(Disposables.empty());
                push(innerObserver, new Object(), 50);
                push(innerObserver, new Object(), 200);
                complete(innerObserver, 250);
            }
        });
        Function<Object, Observable<Object>> closer = new Function<Object, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Object opening) {
                return io.reactivex.Observable.unsafeCreate(new ObservableSource<Object>() {
                    @Override
                    public void subscribe(Observer<? extends Object> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        push(innerObserver, new Object(), 100);
                        complete(innerObserver, 101);
                    }
                });
            }
        };
        Observable<Observable<String>> windowed = source.window(openings, closer);
        windowed.subscribe(observeWindow(list, lists));
        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, lists.size());
        Assert.assertEquals(lists.get(0), list("two", "three"));
        Assert.assertEquals(lists.get(1), list("five"));
    }

    @Test
    public void testObservableBasedCloser() {
        final List<String> list = new ArrayList<String>();
        final List<List<String>> lists = new ArrayList<List<String>>();
        Observable<String> source = unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> innerObserver) {
                innerObserver.onSubscribe(Disposables.empty());
                push(innerObserver, "one", 10);
                push(innerObserver, "two", 60);
                push(innerObserver, "three", 110);
                push(innerObserver, "four", 160);
                push(innerObserver, "five", 210);
                complete(innerObserver, 250);
            }
        });
        Callable<Observable<Object>> closer = new Callable<Observable<Object>>() {
            int calls;

            @Override
            public Observable<Object> call() {
                return io.reactivex.Observable.unsafeCreate(new ObservableSource<Object>() {
                    @Override
                    public void subscribe(Observer<? extends Object> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        int c = (calls)++;
                        if (c == 0) {
                            push(innerObserver, new Object(), 100);
                        } else
                            if (c == 1) {
                                push(innerObserver, new Object(), 100);
                            } else {
                                complete(innerObserver, 101);
                            }

                    }
                });
            }
        };
        Observable<Observable<String>> windowed = source.window(closer);
        windowed.subscribe(observeWindow(list, lists));
        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(3, lists.size());
        Assert.assertEquals(lists.get(0), list("one", "two"));
        Assert.assertEquals(lists.get(1), list("three", "four"));
        Assert.assertEquals(lists.get(2), list("five"));
    }

    @Test
    public void testNoUnsubscribeAndNoLeak() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> open = PublishSubject.create();
        final PublishSubject<Integer> close = PublishSubject.create();
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(open, new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t) {
                return close;
            }
        }).subscribe(to);
        open.onNext(1);
        source.onNext(1);
        Assert.assertTrue(open.hasObservers());
        Assert.assertTrue(close.hasObservers());
        close.onNext(1);
        Assert.assertFalse(close.hasObservers());
        source.onComplete();
        to.assertComplete();
        to.assertNoErrors();
        to.assertValueCount(1);
        // 2.0.2 - not anymore
        // assertTrue("Not cancelled!", ts.isCancelled());
        Assert.assertFalse(open.hasObservers());
        Assert.assertFalse(close.hasObservers());
    }

    @Test
    public void testUnsubscribeAll() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> open = PublishSubject.create();
        final PublishSubject<Integer> close = PublishSubject.create();
        TestObserver<Observable<Integer>> to = new TestObserver<Observable<Integer>>();
        source.window(open, new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t) {
                return close;
            }
        }).subscribe(to);
        open.onNext(1);
        Assert.assertTrue(open.hasObservers());
        Assert.assertTrue(close.hasObservers());
        to.dispose();
        // Disposing the outer sequence stops the opening of new windows
        Assert.assertFalse(open.hasObservers());
        // FIXME subject has subscribers because of the open window
        Assert.assertTrue(close.hasObservers());
    }

    @Test
    public void boundarySelectorNormal() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> start = PublishSubject.create();
        final PublishSubject<Integer> end = PublishSubject.create();
        TestObserver<Integer> to = source.window(start, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return end;
            }
        }).flatMap(Functions.<Observable<Integer>>identity()).test();
        start.onNext(0);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        start.onNext(1);
        source.onNext(5);
        source.onNext(6);
        end.onNext(1);
        start.onNext(2);
        TestHelper.emit(source, 7, 8);
        to.assertResult(1, 2, 3, 4, 5, 5, 6, 6, 7, 8);
    }

    @Test
    public void startError() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> start = PublishSubject.create();
        final PublishSubject<Integer> end = PublishSubject.create();
        TestObserver<Integer> to = source.window(start, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return end;
            }
        }).flatMap(Functions.<Observable<Integer>>identity()).test();
        start.onError(new TestException());
        to.assertFailure(TestException.class);
        Assert.assertFalse("Source has observers!", source.hasObservers());
        Assert.assertFalse("Start has observers!", start.hasObservers());
        Assert.assertFalse("End has observers!", end.hasObservers());
    }

    @Test
    public void endError() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> start = PublishSubject.create();
        final PublishSubject<Integer> end = PublishSubject.create();
        TestObserver<Integer> to = source.window(start, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return end;
            }
        }).flatMap(Functions.<Observable<Integer>>identity()).test();
        start.onNext(1);
        end.onError(new TestException());
        to.assertFailure(TestException.class);
        Assert.assertFalse("Source has observers!", source.hasObservers());
        Assert.assertFalse("Start has observers!", start.hasObservers());
        Assert.assertFalse("End has observers!", end.hasObservers());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(just(1).window(just(2), Functions.justFunction(never())));
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
        ps.window(BehaviorSubject.createDefault(1), Functions.justFunction(never())).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void badSourceCallable() {
        TestHelper.checkBadSourceObservable(new Function<Observable<Object>, Object>() {
            @Override
            public Object apply(Observable<Object> o) throws Exception {
                return o.window(io.reactivex.Observable.just(1), Functions.justFunction(io.reactivex.Observable.never()));
            }
        }, false, 1, 1, ((Object[]) (null)));
    }

    @Test
    public void windowCloseIngoresCancel() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            BehaviorSubject.createDefault(1).window(BehaviorSubject.createDefault(1), new Function<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> apply(Integer f) throws Exception {
                    return new Observable<Integer>() {
                        @Override
                        protected void subscribeActual(Observer<? extends Integer> observer) {
                            observer.onSubscribe(Disposables.empty());
                            observer.onNext(1);
                            observer.onNext(2);
                            observer.onError(new TestException());
                        }
                    };
                }
            }).test().assertValueCount(1).assertNoErrors().assertNotComplete();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mainAndBoundaryDisposeOnNoWindows() {
        AtomicBoolean mainDisposed = new AtomicBoolean();
        AtomicBoolean openDisposed = new AtomicBoolean();
        final AtomicBoolean closeDisposed = new AtomicBoolean();
        observableDisposed(mainDisposed).window(observableDisposed(openDisposed), new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return observableDisposed(closeDisposed);
            }
        }).test().assertSubscribed().assertNoErrors().assertNotComplete().dispose();
        Assert.assertTrue(mainDisposed.get());
        Assert.assertTrue(openDisposed.get());
        Assert.assertTrue(closeDisposed.get());
    }
}

