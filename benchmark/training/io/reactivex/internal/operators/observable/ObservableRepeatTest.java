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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableRepeatTest {
    @Test(timeout = 2000)
    public void testRepetition() {
        int num = 10;
        final AtomicInteger count = new AtomicInteger();
        int value = Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(final Observer<? super Integer> o) {
                o.onNext(count.incrementAndGet());
                o.onComplete();
            }
        }).repeat().subscribeOn(Schedulers.computation()).take(num).blockingLast();
        Assert.assertEquals(num, value);
    }

    @Test(timeout = 2000)
    public void testRepeatTake() {
        Observable<Integer> xs = Observable.just(1, 2);
        Object[] ys = xs.repeat().subscribeOn(Schedulers.newThread()).take(4).toList().blockingGet().toArray();
        Assert.assertArrayEquals(new Object[]{ 1, 2, 1, 2 }, ys);
    }

    @Test(timeout = 20000)
    public void testNoStackOverFlow() {
        just(1).repeat().subscribeOn(Schedulers.newThread()).take(100000).blockingLast();
    }

    @Test
    public void testRepeatTakeWithSubscribeOn() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Observable<Integer> oi = unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> sub) {
                sub.onSubscribe(Disposables.empty());
                counter.incrementAndGet();
                sub.onNext(1);
                sub.onNext(2);
                sub.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
        Object[] ys = oi.repeat().subscribeOn(Schedulers.newThread()).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return t1;
            }
        }).take(4).toList().blockingGet().toArray();
        Assert.assertEquals(2, counter.get());
        Assert.assertArrayEquals(new Object[]{ 1, 2, 1, 2 }, ys);
    }

    @Test(timeout = 2000)
    public void testRepeatAndTake() {
        Observer<Object> o = mockObserver();
        just(1).repeat().take(10).subscribe(o);
        Mockito.verify(o, Mockito.times(10)).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 2000)
    public void testRepeatLimited() {
        Observer<Object> o = mockObserver();
        just(1).repeat(10).subscribe(o);
        Mockito.verify(o, Mockito.times(10)).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 2000)
    public void testRepeatError() {
        Observer<Object> o = mockObserver();
        error(new TestException()).repeat(10).subscribe(o);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test(timeout = 2000)
    public void testRepeatZero() {
        Observer<Object> o = mockObserver();
        just(1).repeat(0).subscribe(o);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 2000)
    public void testRepeatOne() {
        Observer<Object> o = mockObserver();
        just(1).repeat(1).subscribe(o);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.times(1)).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    /**
     * Issue #2587.
     */
    @Test
    public void testRepeatAndDistinctUnbounded() {
        Observable<Integer> src = fromIterable(Arrays.asList(1, 2, 3, 4, 5)).take(3).repeat(3).distinct();
        TestObserver<Integer> to = new TestObserver<Integer>();
        src.subscribe(to);
        to.assertNoErrors();
        to.assertTerminated();
        to.assertValues(1, 2, 3);
    }

    /**
     * Issue #2844: wrong target of request.
     */
    @Test(timeout = 3000)
    public void testRepeatRetarget() {
        final List<Integer> concatBase = new ArrayList<Integer>();
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.just(1, 2).repeat(5).concatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer x) {
                System.out.println(("testRepeatRetarget -> " + x));
                concatBase.add(x);
                return io.reactivex.Observable.<Integer>empty().delay(200, TimeUnit.MILLISECONDS);
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        to.assertNoValues();
        Assert.assertEquals(Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2, 1, 2), concatBase);
    }

    @Test
    public void repeatUntil() {
        just(1).repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return false;
            }
        }).take(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void repeatLongPredicateInvalid() {
        try {
            just(1).repeat((-99));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("times >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void repeatUntilError() {
        error(new TestException()).repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return true;
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void repeatUntilFalse() {
        just(1).repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return true;
            }
        }).test().assertResult(1);
    }

    @Test
    public void repeatUntilSupplierCrash() {
        just(1).repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void shouldDisposeInnerObservable() {
        final PublishSubject<Object> subject = PublishSubject.create();
        final Disposable disposable = Observable.just("Leak").repeatWhen(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> completions) throws Exception {
                return completions.switchMap(new Function<Object, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(Object ignore) throws Exception {
                        return subject;
                    }
                });
            }
        }).subscribe();
        Assert.assertTrue(subject.hasObservers());
        disposable.dispose();
        Assert.assertFalse(subject.hasObservers());
    }

    @Test
    public void testRepeatWhen() {
        error(new TestException()).repeatWhen(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> v) throws Exception {
                return v.delay(10, TimeUnit.SECONDS);
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void whenTake() {
        range(1, 3).repeatWhen(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> handler) throws Exception {
                return handler.take(2);
            }
        }).test().assertResult(1, 2, 3, 1, 2, 3);
    }

    @Test
    public void handlerError() {
        range(1, 3).repeatWhen(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> v) throws Exception {
                return v.map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object w) throws Exception {
                        throw new TestException();
                    }
                });
            }
        }).test().assertFailure(TestException.class, 1, 2, 3);
    }

    @Test
    public void noCancelPreviousRepeat() {
        final AtomicInteger counter = new AtomicInteger();
        Observable<Integer> source = just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.repeat(5).test().assertResult(1, 1, 1, 1, 1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRepeatUntil() {
        final AtomicInteger counter = new AtomicInteger();
        Observable<Integer> source = just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        final AtomicInteger times = new AtomicInteger();
        source.repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return (times.getAndIncrement()) == 4;
            }
        }).test().assertResult(1, 1, 1, 1, 1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRepeatWhen() {
        final AtomicInteger counter = new AtomicInteger();
        Observable<Integer> source = just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        final AtomicInteger times = new AtomicInteger();
        source.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> e) throws Exception {
                return e.takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(Object v) throws Exception {
                        return (times.getAndIncrement()) < 4;
                    }
                });
            }
        }).test().assertResult(1, 1, 1, 1, 1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void repeatFloodNoSubscriptionError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final PublishSubject<Integer> source = PublishSubject.create();
            final PublishSubject<Integer> signaller = PublishSubject.create();
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                TestObserver<Integer> to = source.take(1).repeatWhen(new Function<Observable<Object>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Observable<Object> v) throws Exception {
                        return signaller;
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                            source.onNext(1);
                        }
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                            signaller.onNext(1);
                        }
                    }
                };
                TestHelper.race(r1, r2);
                to.dispose();
            }
            if (!(errors.isEmpty())) {
                for (Throwable e : errors) {
                    e.printStackTrace();
                }
                Assert.fail((errors + ""));
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

