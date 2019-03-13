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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ObservableWindowWithTimeTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    @Test
    public void testTimedAndCount() {
        final List<String> list = new ArrayList<String>();
        final List<List<String>> lists = new ArrayList<List<String>>();
        Observable<String> source = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                push(observer, "one", 10);
                push(observer, "two", 90);
                push(observer, "three", 110);
                push(observer, "four", 190);
                push(observer, "five", 210);
                complete(observer, 250);
            }
        });
        Observable<Observable<String>> windowed = source.window(100, TimeUnit.MILLISECONDS, scheduler, 2);
        windowed.subscribe(observeWindow(list, lists));
        scheduler.advanceTimeTo(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, lists.size());
        Assert.assertEquals(lists.get(0), list("one", "two"));
        scheduler.advanceTimeTo(200, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, lists.size());
        Assert.assertEquals(lists.get(1), list("three", "four"));
        scheduler.advanceTimeTo(300, TimeUnit.MILLISECONDS);
        Assert.assertEquals(3, lists.size());
        Assert.assertEquals(lists.get(2), list("five"));
    }

    @Test
    public void testTimed() {
        final List<String> list = new ArrayList<String>();
        final List<List<String>> lists = new ArrayList<List<String>>();
        Observable<String> source = unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                push(observer, "one", 98);
                push(observer, "two", 99);
                push(observer, "three", 99);// FIXME happens after the window is open

                push(observer, "four", 101);
                push(observer, "five", 102);
                complete(observer, 150);
            }
        });
        Observable<Observable<String>> windowed = source.window(100, TimeUnit.MILLISECONDS, scheduler);
        windowed.subscribe(observeWindow(list, lists));
        scheduler.advanceTimeTo(101, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, lists.size());
        Assert.assertEquals(lists.get(0), list("one", "two", "three"));
        scheduler.advanceTimeTo(201, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, lists.size());
        Assert.assertEquals(lists.get(1), list("four", "five"));
    }

    @Test
    public void testExactWindowSize() {
        Observable<Observable<Integer>> source = range(1, 10).window(1, TimeUnit.MINUTES, scheduler, 3);
        final List<Integer> list = new ArrayList<Integer>();
        final List<List<Integer>> lists = new ArrayList<List<Integer>>();
        source.subscribe(observeWindow(list, lists));
        Assert.assertEquals(4, lists.size());
        Assert.assertEquals(3, lists.get(0).size());
        Assert.assertEquals(Arrays.asList(1, 2, 3), lists.get(0));
        Assert.assertEquals(3, lists.get(1).size());
        Assert.assertEquals(Arrays.asList(4, 5, 6), lists.get(1));
        Assert.assertEquals(3, lists.get(2).size());
        Assert.assertEquals(Arrays.asList(7, 8, 9), lists.get(2));
        Assert.assertEquals(1, lists.get(3).size());
        Assert.assertEquals(Arrays.asList(10), lists.get(3));
    }

    @Test
    public void testTakeFlatMapCompletes() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final AtomicInteger wip = new AtomicInteger();
        final int indicator = 999999999;
        window(300, TimeUnit.MILLISECONDS).take(10).doOnComplete(new Action() {
            @Override
            public void run() {
                System.out.println("Main done!");
            }
        }).flatMap(new Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Observable<Integer> w) {
                return w.startWith(indicator).doOnComplete(new Action() {
                    @Override
                    public void run() {
                        System.out.println(("inner done: " + (wip.incrementAndGet())));
                    }
                });
            }
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer pv) {
                System.out.println(pv);
            }
        }).subscribe(to);
        to.awaitTerminalEvent(5, TimeUnit.SECONDS);
        to.assertComplete();
        Assert.assertTrue(((to.valueCount()) != 0));
    }

    @Test
    public void timespanTimeskipDefaultScheduler() {
        just(1).window(1, 1, TimeUnit.MINUTES).flatMap(Functions.<Observable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timespanTimeskipCustomScheduler() {
        just(1).window(1, 1, TimeUnit.MINUTES, Schedulers.io()).flatMap(Functions.<Observable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timespanTimeskipCustomSchedulerBufferSize() {
        range(1, 10).window(1, 1, TimeUnit.MINUTES, Schedulers.io(), 2).flatMap(Functions.<Observable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void timespanDefaultSchedulerSize() {
        range(1, 10).window(1, TimeUnit.MINUTES, 20).flatMap(Functions.<Observable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void timespanDefaultSchedulerSizeRestart() {
        range(1, 10).window(1, TimeUnit.MINUTES, 20, true).flatMap(Functions.<Observable<Integer>>identity(), true).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void invalidSpan() {
        try {
            just(1).window((-99), 1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown!");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("timespan > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void timeskipJustOverlap() {
        just(1).window(2, 1, TimeUnit.MINUTES, Schedulers.single()).flatMap(Functions.<Observable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timeskipJustSkip() {
        just(1).window(1, 2, TimeUnit.MINUTES, Schedulers.single()).flatMap(Functions.<Observable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timeskipSkipping() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.window(1, 2, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Observable<Integer>>identity()).test();
        ps.onNext(1);
        ps.onNext(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ps.onNext(3);
        ps.onNext(4);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ps.onNext(5);
        ps.onNext(6);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ps.onNext(7);
        ps.onComplete();
        to.assertResult(1, 2, 5, 6);
    }

    @Test
    public void timeskipOverlapping() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.window(2, 1, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Observable<Integer>>identity()).test();
        ps.onNext(1);
        ps.onNext(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ps.onNext(3);
        ps.onNext(4);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ps.onNext(5);
        ps.onNext(6);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ps.onNext(7);
        ps.onComplete();
        to.assertResult(1, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7);
    }

    @Test
    public void exactOnError() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.window(1, 1, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Observable<Integer>>identity()).test();
        ps.onError(new TestException());
        to.assertFailure(TestException.class);
    }

    @Test
    public void overlappingOnError() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.window(2, 1, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Observable<Integer>>identity()).test();
        ps.onError(new TestException());
        to.assertFailure(TestException.class);
    }

    @Test
    public void skipOnError() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.window(1, 2, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Observable<Integer>>identity()).test();
        ps.onError(new TestException());
        to.assertFailure(TestException.class);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(window(1, TimeUnit.DAYS, Schedulers.single()));
        TestHelper.checkDisposed(range(1, 5).window(2, 1, TimeUnit.DAYS, Schedulers.single()));
        TestHelper.checkDisposed(range(1, 5).window(1, 2, TimeUnit.DAYS, Schedulers.single()));
        TestHelper.checkDisposed(never().window(1, TimeUnit.DAYS, Schedulers.single(), 2, true));
    }

    @Test
    public void restartTimer() {
        range(1, 5).window(1, TimeUnit.DAYS, Schedulers.single(), 2, true).flatMap(Functions.<Observable<Integer>>identity()).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void exactBoundaryError() {
        error(new TestException()).window(1, TimeUnit.DAYS, Schedulers.single(), 2, true).test().assertSubscribed().assertError(TestException.class).assertNotComplete();
    }

    @Test
    public void restartTimerMany() {
        intervalRange(1, 1000, 1, 1, TimeUnit.MILLISECONDS).window(1, TimeUnit.MILLISECONDS, Schedulers.single(), 2, true).flatMap(Functions.<Observable<Long>>identity()).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(500).assertNoErrors().assertComplete();
    }

    @Test
    public void exactUnboundedReentrant() {
        TestScheduler scheduler = new TestScheduler();
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
        ps.window(1, TimeUnit.MILLISECONDS, scheduler).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void exactBoundedReentrant() {
        TestScheduler scheduler = new TestScheduler();
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
        ps.window(1, TimeUnit.MILLISECONDS, scheduler, 10, true).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void exactBoundedReentrant2() {
        TestScheduler scheduler = new TestScheduler();
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
        ps.window(1, TimeUnit.MILLISECONDS, scheduler, 2, true).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void skipReentrant() {
        TestScheduler scheduler = new TestScheduler();
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
        ps.window(1, 2, TimeUnit.MILLISECONDS, scheduler).flatMap(new Function<Observable<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Observable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(to);
        ps.onNext(1);
        to.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void sizeTimeTimeout() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 100).test().assertValueCount(1);
        scheduler.advanceTimeBy(5, TimeUnit.MILLISECONDS);
        to.assertValueCount(2).assertNoErrors().assertNotComplete();
        to.values().get(0).test().assertResult();
    }

    @Test
    public void periodicWindowCompletion() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, Long.MAX_VALUE, false).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionRestartTimer() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, Long.MAX_VALUE, true).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionBounded() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 5, false).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionRestartTimerBounded() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 5, true).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionRestartTimerBoundedSomeData() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 2, true).test();
        ps.onNext(1);
        ps.onNext(2);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValueCount(22).assertNoErrors().assertNotComplete();
    }

    @Test
    public void countRestartsOnTimeTick() {
        TestScheduler scheduler = new TestScheduler();
        Subject<Integer> ps = PublishSubject.<Integer>create();
        TestObserver<Observable<Integer>> to = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 5, true).test();
        // window #1
        ps.onNext(1);
        ps.onNext(2);
        scheduler.advanceTimeBy(5, TimeUnit.MILLISECONDS);
        // window #2
        ps.onNext(3);
        ps.onNext(4);
        ps.onNext(5);
        ps.onNext(6);
        to.assertValueCount(2).assertNoErrors().assertNotComplete();
    }
}

