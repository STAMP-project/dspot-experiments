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
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ObservableCacheTest {
    @Test
    public void testColdReplayNoBackpressure() {
        ObservableCache<Integer> source = new ObservableCache<Integer>(range(0, 1000), 16);
        Assert.assertFalse("Source is connected!", source.isConnected());
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.subscribe(to);
        Assert.assertTrue("Source is not connected!", source.isConnected());
        Assert.assertFalse("Subscribers retained!", source.hasObservers());
        to.assertNoErrors();
        to.assertTerminated();
        List<Integer> onNextEvents = to.values();
        Assert.assertEquals(1000, onNextEvents.size());
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(((Integer) (i)), onNextEvents.get(i));
        }
    }

    @Test
    public void testCache() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Observable<String> o = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(final Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        System.out.println("published Observable being executed");
                        observer.onNext("one");
                        observer.onComplete();
                    }
                }).start();
            }
        }).cache();
        // we then expect the following 2 subscriptions to get that same value
        final CountDownLatch latch = new CountDownLatch(2);
        // subscribe once
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                System.out.println(("v: " + v));
                latch.countDown();
            }
        });
        // subscribe again
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                System.out.println(("v: " + v));
                latch.countDown();
            }
        });
        if (!(latch.await(1000, TimeUnit.MILLISECONDS))) {
            Assert.fail("subscriptions did not receive values");
        }
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testUnsubscribeSource() throws Exception {
        Action unsubscribe = Mockito.mock(Action.class);
        Observable<Integer> o = just(1).doOnDispose(unsubscribe).cache();
        o.subscribe();
        o.subscribe();
        o.subscribe();
        Mockito.verify(unsubscribe, Mockito.never()).run();
    }

    @Test
    public void testTake() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        ObservableCache<Integer> cached = new ObservableCache<Integer>(range(1, 1000), 16);
        cached.take(10).subscribe(to);
        to.assertNoErrors();
        to.assertComplete();
        to.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // ts.assertUnsubscribed(); // FIXME no longer valid
        Assert.assertFalse(cached.hasObservers());
    }

    @Test
    public void testAsync() {
        Observable<Integer> source = range(1, 10000);
        for (int i = 0; i < 100; i++) {
            TestObserver<Integer> to1 = new TestObserver<Integer>();
            ObservableCache<Integer> cached = new ObservableCache<Integer>(source, 16);
            cached.observeOn(Schedulers.computation()).subscribe(to1);
            to1.awaitTerminalEvent(2, TimeUnit.SECONDS);
            to1.assertNoErrors();
            to1.assertComplete();
            Assert.assertEquals(10000, to1.values().size());
            TestObserver<Integer> to2 = new TestObserver<Integer>();
            cached.observeOn(Schedulers.computation()).subscribe(to2);
            to2.awaitTerminalEvent(2, TimeUnit.SECONDS);
            to2.assertNoErrors();
            to2.assertComplete();
            Assert.assertEquals(10000, to2.values().size());
        }
    }

    @Test
    public void testAsyncComeAndGo() {
        Observable<Long> source = interval(1, 1, TimeUnit.MILLISECONDS).take(1000).subscribeOn(Schedulers.io());
        ObservableCache<Long> cached = new ObservableCache<Long>(source, 16);
        Observable<Long> output = cached.observeOn(Schedulers.computation());
        List<TestObserver<Long>> list = new ArrayList<TestObserver<Long>>(100);
        for (int i = 0; i < 100; i++) {
            TestObserver<Long> to = new TestObserver<Long>();
            list.add(to);
            output.skip((i * 10)).take(10).subscribe(to);
        }
        List<Long> expected = new ArrayList<Long>();
        for (int i = 0; i < 10; i++) {
            expected.add(((long) (i - 10)));
        }
        int j = 0;
        for (TestObserver<Long> to : list) {
            to.awaitTerminalEvent(3, TimeUnit.SECONDS);
            to.assertNoErrors();
            to.assertComplete();
            for (int i = j * 10; i < ((j * 10) + 10); i++) {
                expected.set((i - (j * 10)), ((long) (i)));
            }
            to.assertValueSequence(expected);
            j++;
        }
    }

    @Test
    public void testNoMissingBackpressureException() {
        final int m = (4 * 1000) * 1000;
        Observable<Integer> firehose = unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> t) {
                t.onSubscribe(Disposables.empty());
                for (int i = 0; i < m; i++) {
                    t.onNext(i);
                }
                t.onComplete();
            }
        });
        TestObserver<Integer> to = new TestObserver<Integer>();
        firehose.cache().observeOn(Schedulers.computation()).takeLast(100).subscribe(to);
        to.awaitTerminalEvent(3, TimeUnit.SECONDS);
        to.assertNoErrors();
        to.assertComplete();
        Assert.assertEquals(100, to.values().size());
    }

    @Test
    public void testValuesAndThenError() {
        Observable<Integer> source = range(1, 10).concatWith(<Integer>error(new TestException())).cache();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.subscribe(to);
        to.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        to.assertNotComplete();
        to.assertError(TestException.class);
        TestObserver<Integer> to2 = new TestObserver<Integer>();
        source.subscribe(to2);
        to2.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        to2.assertNotComplete();
        to2.assertError(TestException.class);
    }

    @Test
    public void observers() {
        PublishSubject<Integer> ps = PublishSubject.create();
        ObservableCache<Integer> cache = ((ObservableCache<Integer>) (range(1, 5).concatWith(ps).cache()));
        Assert.assertFalse(cache.hasObservers());
        Assert.assertEquals(0, cache.cachedEventCount());
        TestObserver<Integer> to = cache.test();
        Assert.assertTrue(cache.hasObservers());
        Assert.assertEquals(5, cache.cachedEventCount());
        ps.onComplete();
        to.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void disposeOnArrival() {
        range(1, 5).cache().test(true).assertEmpty();
    }

    @Test
    public void disposeOnArrival2() {
        Observable<Integer> o = PublishSubject.<Integer>create().cache();
        o.test();
        o.test(true).assertEmpty();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(range(1, 5).cache());
    }

    @Test
    public void take() {
        Observable<Integer> cache = range(1, 5).cache();
        cache.take(2).test().assertResult(1, 2);
        cache.take(3).test().assertResult(1, 2, 3);
    }

    @Test
    public void subscribeEmitRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.<Integer>create();
            final Observable<Integer> cache = ps.cache();
            cache.test();
            final TestObserver<Integer> to = new TestObserver<Integer>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    cache.subscribe(to);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 500; j++) {
                        ps.onNext(j);
                    }
                    ps.onComplete();
                }
            };
            TestHelper.race(r1, r2);
            to.awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(500).assertComplete().assertNoErrors();
        }
    }

    @Test
    public void cancelledUpFront() {
        final AtomicInteger call = new AtomicInteger();
        Observable<Object> f = fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return call.incrementAndGet();
            }
        }).concatWith(never()).cache();
        f.test().assertValuesOnly(1);
        f.test(true).assertEmpty();
        Assert.assertEquals(1, call.get());
    }
}

