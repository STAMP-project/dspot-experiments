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


import ImmediateThinScheduler.INSTANCE;
import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.Observer;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableSwitchTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    private Observer<String> observer;

    @Test
    public void testSwitchWhenOuterCompleteBeforeInner() {
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> outerObserver) {
                outerObserver.onSubscribe(Disposables.empty());
                publishNext(outerObserver, 50, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 70, "one");
                        publishNext(innerObserver, 100, "two");
                        publishCompleted(innerObserver, 200);
                    }
                }));
                publishCompleted(outerObserver, 60);
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(350, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(2)).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testSwitchWhenInnerCompleteBeforeOuter() {
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> outerObserver) {
                outerObserver.onSubscribe(Disposables.empty());
                publishNext(outerObserver, 10, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 0, "one");
                        publishNext(innerObserver, 10, "two");
                        publishCompleted(innerObserver, 20);
                    }
                }));
                publishNext(outerObserver, 100, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 0, "three");
                        publishNext(innerObserver, 10, "four");
                        publishCompleted(innerObserver, 20);
                    }
                }));
                publishCompleted(outerObserver, 200);
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
        onComplete();
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        scheduler.advanceTimeTo(250, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testSwitchWithComplete() {
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> outerObserver) {
                outerObserver.onSubscribe(Disposables.empty());
                publishNext(outerObserver, 50, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(final Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 60, "one");
                        publishNext(innerObserver, 100, "two");
                    }
                }));
                publishNext(outerObserver, 200, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(final Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 0, "three");
                        publishNext(innerObserver, 100, "four");
                    }
                }));
                publishCompleted(outerObserver, 250);
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(175, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(225, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(350, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSwitchWithError() {
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> outerObserver) {
                outerObserver.onSubscribe(Disposables.empty());
                publishNext(outerObserver, 50, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(final Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 50, "one");
                        publishNext(innerObserver, 100, "two");
                    }
                }));
                publishNext(outerObserver, 200, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 0, "three");
                        publishNext(innerObserver, 100, "four");
                    }
                }));
                publishError(outerObserver, 250, new TestException());
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(175, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(225, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(350, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testSwitchWithSubsequenceComplete() {
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> outerObserver) {
                outerObserver.onSubscribe(Disposables.empty());
                publishNext(outerObserver, 50, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 50, "one");
                        publishNext(innerObserver, 100, "two");
                    }
                }));
                publishNext(outerObserver, 130, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishCompleted(innerObserver, 0);
                    }
                }));
                publishNext(outerObserver, 150, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 50, "three");
                    }
                }));
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(250, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSwitchWithSubsequenceError() {
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> observer) {
                observer.onSubscribe(Disposables.empty());
                publishNext(observer, 50, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> observer) {
                        observer.onSubscribe(Disposables.empty());
                        publishNext(observer, 50, "one");
                        publishNext(observer, 100, "two");
                    }
                }));
                publishNext(observer, 130, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> observer) {
                        observer.onSubscribe(Disposables.empty());
                        publishError(observer, 0, new TestException());
                    }
                }));
                publishNext(observer, 150, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> observer) {
                        observer.onSubscribe(Disposables.empty());
                        publishNext(observer, 50, "three");
                    }
                }));
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(250, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext("three");
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testSwitchIssue737() {
        // https://github.com/ReactiveX/RxJava/issues/737
        Observable<Observable<String>> source = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? super Observable<String>> outerObserver) {
                outerObserver.onSubscribe(Disposables.empty());
                publishNext(outerObserver, 0, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 10, "1-one");
                        publishNext(innerObserver, 20, "1-two");
                        // The following events will be ignored
                        publishNext(innerObserver, 30, "1-three");
                        publishCompleted(innerObserver, 40);
                    }
                }));
                publishNext(outerObserver, 25, Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> innerObserver) {
                        innerObserver.onSubscribe(Disposables.empty());
                        publishNext(innerObserver, 10, "2-one");
                        publishNext(innerObserver, 20, "2-two");
                        publishNext(innerObserver, 30, "2-three");
                        publishCompleted(innerObserver, 40);
                    }
                }));
                publishCompleted(outerObserver, 30);
            }
        });
        Observable<String> sampled = Observable.switchOnNext(source);
        sampled.subscribe(observer);
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("1-one");
        inOrder.verify(observer, Mockito.times(1)).onNext("1-two");
        inOrder.verify(observer, Mockito.times(1)).onNext("2-one");
        inOrder.verify(observer, Mockito.times(1)).onNext("2-two");
        inOrder.verify(observer, Mockito.times(1)).onNext("2-three");
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testUnsubscribe() {
        final AtomicBoolean isUnsubscribed = new AtomicBoolean();
        Observable.switchOnNext(Observable.unsafeCreate(new ObservableSource<Observable<Integer>>() {
            @Override
            public void subscribe(final Observer<? super Observable<Integer>> observer) {
                Disposable bs = Disposables.empty();
                observer.onSubscribe(bs);
                observer.onNext(Observable.just(1));
                isUnsubscribed.set(bs.isDisposed());
            }
        })).take(1).subscribe();
        Assert.assertTrue("Switch doesn't propagate 'unsubscribe'", isUnsubscribed.get());
    }

    /**
     * The upstream producer hijacked the switch producer stopping the requests aimed at the inner observables.
     */
    @Test
    public void testIssue2654() {
        Observable<String> oneItem = Observable.just("Hello").mergeWith(Observable.<String>never());
        Observable<String> src = oneItem.switchMap(new Function<String, Observable<String>>() {
            @Override
            public io.reactivex.Observable<String> apply(final String s) {
                return Observable.just(s).mergeWith(Observable.interval(10, TimeUnit.MILLISECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long i) {
                        return (s + " ") + i;
                    }
                })).take(250);
            }
        }).share();
        TestObserver<String> to = new TestObserver<String>() {
            @Override
            public void onNext(String t) {
                super.onNext(t);
                if ((valueCount()) == 250) {
                    onComplete();
                    dispose();
                }
            }
        };
        src.subscribe(to);
        to.awaitTerminalEvent(10, TimeUnit.SECONDS);
        System.out.println(("> testIssue2654: " + (to.valueCount())));
        to.assertTerminated();
        to.assertNoErrors();
        Assert.assertEquals(250, to.valueCount());
    }

    @Test
    public void delayErrors() {
        PublishSubject<ObservableSource<Integer>> source = PublishSubject.create();
        TestObserver<Integer> to = source.switchMapDelayError(Functions.<ObservableSource<Integer>>identity()).test();
        to.assertNoValues().assertNoErrors().assertNotComplete();
        source.onNext(Observable.just(1));
        source.onNext(Observable.<Integer>error(new TestException("Forced failure 1")));
        source.onNext(Observable.just(2, 3, 4));
        source.onNext(Observable.<Integer>error(new TestException("Forced failure 2")));
        source.onNext(Observable.just(5));
        source.onError(new TestException("Forced failure 3"));
        to.assertValues(1, 2, 3, 4, 5).assertNotComplete().assertError(CompositeException.class);
        List<Throwable> errors = ExceptionHelper.flatten(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Forced failure 1");
        TestHelper.assertError(errors, 1, TestException.class, "Forced failure 2");
        TestHelper.assertError(errors, 2, TestException.class, "Forced failure 3");
    }

    @Test
    public void switchOnNextDelayError() {
        PublishSubject<Observable<Integer>> ps = PublishSubject.create();
        TestObserver<Integer> to = Observable.switchOnNextDelayError(ps).test();
        ps.onNext(Observable.just(1));
        ps.onNext(Observable.range(2, 4));
        ps.onComplete();
        to.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void switchOnNextDelayErrorWithError() {
        PublishSubject<Observable<Integer>> ps = PublishSubject.create();
        TestObserver<Integer> to = Observable.switchOnNextDelayError(ps).test();
        ps.onNext(Observable.just(1));
        ps.onNext(Observable.<Integer>error(new TestException()));
        ps.onNext(Observable.range(2, 4));
        ps.onComplete();
        to.assertFailure(TestException.class, 1, 2, 3, 4, 5);
    }

    @Test
    public void switchOnNextDelayErrorBufferSize() {
        PublishSubject<Observable<Integer>> ps = PublishSubject.create();
        TestObserver<Integer> to = Observable.switchOnNextDelayError(ps, 2).test();
        ps.onNext(Observable.just(1));
        ps.onNext(Observable.range(2, 4));
        ps.onComplete();
        to.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void switchMapDelayErrorEmptySource() {
        Assert.assertSame(Observable.empty(), Observable.<Object>empty().switchMapDelayError(new Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return Observable.just(1);
            }
        }, 16));
    }

    @Test
    public void switchMapDelayErrorJustSource() {
        Observable.just(0).switchMapDelayError(new Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return Observable.just(1);
            }
        }, 16).test().assertResult(1);
    }

    @Test
    public void switchMapErrorEmptySource() {
        Assert.assertSame(Observable.empty(), Observable.<Object>empty().switchMap(new Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return Observable.just(1);
            }
        }, 16));
    }

    @Test
    public void switchMapJustSource() {
        Observable.just(0).switchMap(new Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return Observable.just(1);
            }
        }, 16).test().assertResult(1);
    }

    @Test
    public void switchMapInnerCancelled() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = Observable.just(1).switchMap(Functions.justFunction(ps)).test();
        Assert.assertTrue(ps.hasObservers());
        to.cancel();
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void switchMapSingleJustSource() {
        Observable.just(0).switchMapSingle(new Function<Object, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Object v) throws Exception {
                return Single.just(1);
            }
        }).test().assertResult(1);
    }

    @Test
    public void switchMapSingleMapperReturnsNull() {
        Observable.just(0).switchMapSingle(new Function<Object, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Object v) throws Exception {
                return null;
            }
        }).test().assertError(NullPointerException.class);
    }

    @Test(expected = NullPointerException.class)
    public void switchMapSingleMapperIsNull() {
        Observable.just(0).switchMapSingle(null);
    }

    @Test
    public void switchMapSingleFunctionDoesntReturnSingle() {
        Observable.just(0).switchMapSingle(new Function<Object, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Object v) throws Exception {
                return new SingleSource<Integer>() {
                    @Override
                    public void subscribe(SingleObserver<? super Integer> observer) {
                        observer.onSubscribe(Disposables.empty());
                        observer.onSuccess(1);
                    }
                };
            }
        }).test().assertResult(1);
    }

    @Test
    public void switchMapSingleDelayErrorJustSource() {
        final AtomicBoolean completed = new AtomicBoolean();
        Observable.just(0, 1).switchMapSingleDelayError(new Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Integer v) throws Exception {
                if (v == 0) {
                    return Single.error(new RuntimeException());
                } else {
                    return Single.just(1).doOnSuccess(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer n) throws Exception {
                            completed.set(true);
                        }
                    });
                }
            }
        }).test().assertValue(1).assertError(RuntimeException.class);
        Assert.assertTrue(completed.get());
    }

    @Test
    public void scalarMap() {
        Observable.switchOnNext(Observable.just(Observable.just(1))).test().assertResult(1);
    }

    @Test
    public void scalarMapDelayError() {
        Observable.switchOnNextDelayError(Observable.just(Observable.just(1))).test().assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.switchOnNext(Observable.just(Observable.just(1)).hide()));
    }

    @Test
    public void nextSourceErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishSubject<Integer> ps1 = PublishSubject.create();
                final PublishSubject<Integer> ps2 = PublishSubject.create();
                ps1.switchMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                        if (v == 1) {
                            return ps2;
                        }
                        return Observable.never();
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onNext(2);
                    }
                };
                final TestException ex = new TestException();
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                for (Throwable e : errors) {
                    Assert.assertTrue(e.toString(), (e instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void outerInnerErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishSubject<Integer> ps1 = PublishSubject.create();
                final PublishSubject<Integer> ps2 = PublishSubject.create();
                ps1.switchMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                        if (v == 1) {
                            return ps2;
                        }
                        return Observable.never();
                    }
                }).test();
                ps1.onNext(1);
                final TestException ex1 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onError(ex1);
                    }
                };
                final TestException ex2 = new TestException();
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                for (Throwable e : errors) {
                    Assert.assertTrue(e.getCause().toString(), ((e.getCause()) instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void nextCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps1 = PublishSubject.create();
            final TestObserver<Integer> to = ps1.switchMap(new Function<Integer, ObservableSource<Integer>>() {
                @Override
                public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                    return Observable.never();
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps1.onNext(2);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void mapperThrows() {
        Observable.just(1).hide().switchMap(new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void badMainSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onComplete();
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.switchMap(Functions.justFunction(Observable.never())).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void emptyInner() {
        Observable.range(1, 5).switchMap(Functions.justFunction(Observable.empty())).test().assertResult();
    }

    @Test
    public void justInner() {
        Observable.range(1, 5).switchMap(Functions.justFunction(Observable.just(1))).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void badInnerSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.just(1).hide().switchMap(Functions.justFunction(new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException());
                    observer.onComplete();
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            })).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void innerCompletesReentrant() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                ps.onComplete();
            }
        };
        Observable.just(1).hide().switchMap(Functions.justFunction(ps)).subscribe(to);
        ps.onNext(1);
        to.assertResult(1);
    }

    @Test
    public void innerErrorsReentrant() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                ps.onError(new TestException());
            }
        };
        Observable.just(1).hide().switchMap(Functions.justFunction(ps)).subscribe(to);
        ps.onNext(1);
        to.assertFailure(TestException.class, 1);
    }

    @Test
    public void innerDisposedOnMainError() {
        final PublishSubject<Integer> main = PublishSubject.create();
        final PublishSubject<Integer> inner = PublishSubject.create();
        TestObserver<Integer> to = main.switchMap(Functions.justFunction(inner)).test();
        Assert.assertTrue(main.hasObservers());
        main.onNext(1);
        Assert.assertTrue(inner.hasObservers());
        main.onError(new TestException());
        Assert.assertFalse(inner.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void outerInnerErrorRaceIgnoreDispose() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final AtomicReference<Observer<? super Integer>> obs1 = new AtomicReference<Observer<? super Integer>>();
                final Observable<Integer> ps1 = new Observable<Integer>() {
                    @Override
                    protected void subscribeActual(Observer<? super Integer> observer) {
                        obs1.set(observer);
                    }
                };
                final AtomicReference<Observer<? super Integer>> obs2 = new AtomicReference<Observer<? super Integer>>();
                final Observable<Integer> ps2 = new Observable<Integer>() {
                    @Override
                    protected void subscribeActual(Observer<? super Integer> observer) {
                        obs2.set(observer);
                    }
                };
                ps1.switchMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                        if (v == 1) {
                            return ps2;
                        }
                        return Observable.never();
                    }
                }).test();
                obs1.get().onSubscribe(Disposables.empty());
                obs1.get().onNext(1);
                obs2.get().onSubscribe(Disposables.empty());
                final TestException ex1 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        obs1.get().onError(ex1);
                    }
                };
                final TestException ex2 = new TestException();
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        obs2.get().onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                for (Throwable e : errors) {
                    Assert.assertTrue(e.toString(), ((e.getCause()) instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void asyncFused() {
        Observable.just(1).hide().switchMap(Functions.justFunction(Observable.range(1, 5).observeOn(INSTANCE))).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void syncFusedMaybe() {
        Observable.range(1, 5).hide().switchMap(Functions.justFunction(Maybe.just(1).toObservable())).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void syncFusedSingle() {
        Observable.range(1, 5).hide().switchMap(Functions.justFunction(Single.just(1).toObservable())).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void syncFusedCompletable() {
        Observable.range(1, 5).hide().switchMap(Functions.justFunction(Completable.complete().toObservable())).test().assertResult();
    }

    @Test
    public void asyncFusedRejecting() {
        Observable.just(1).hide().switchMap(Functions.justFunction(rejectObservableFusion())).test().assertEmpty();
    }

    @Test
    public void asyncFusedPollCrash() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.switchMap(Functions.justFunction(Observable.range(1, 5).observeOn(INSTANCE).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(TestHelper.<Integer>observableStripBoundary()))).test();
        to.assertEmpty();
        ps.onNext(1);
        to.assertFailure(TestException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void asyncFusedPollCrashDelayError() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.switchMapDelayError(Functions.justFunction(Observable.range(1, 5).observeOn(INSTANCE).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(TestHelper.<Integer>observableStripBoundary()))).test();
        to.assertEmpty();
        ps.onNext(1);
        Assert.assertTrue(ps.hasObservers());
        to.assertEmpty();
        ps.onComplete();
        to.assertFailure(TestException.class);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void fusedBoundary() {
        String thread = Thread.currentThread().getName();
        Observable.range(1, 10000).switchMap(new Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                return Observable.just(2).hide().observeOn(Schedulers.single()).map(new Function<Integer, Object>() {
                    @Override
                    public Object apply(Integer w) throws Exception {
                        return Thread.currentThread().getName();
                    }
                });
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertNever(thread).assertNoErrors().assertComplete();
    }
}

