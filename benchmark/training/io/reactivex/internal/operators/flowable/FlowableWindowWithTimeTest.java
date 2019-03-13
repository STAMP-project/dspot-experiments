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
package io.reactivex.internal.operators.flowable;


import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.MissingBackpressureException;
import io.reactivex.processors.TestScheduler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class FlowableWindowWithTimeTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    @Test
    public void testTimedAndCount() {
        final List<String> list = new ArrayList<String>();
        final List<List<String>> lists = new ArrayList<List<String>>();
        Flowable<String> source = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                push(subscriber, "one", 10);
                push(subscriber, "two", 90);
                push(subscriber, "three", 110);
                push(subscriber, "four", 190);
                push(subscriber, "five", 210);
                complete(subscriber, 250);
            }
        });
        Flowable<Flowable<String>> windowed = source.window(100, TimeUnit.MILLISECONDS, scheduler, 2);
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
        Flowable<String> source = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                push(subscriber, "one", 98);
                push(subscriber, "two", 99);
                push(subscriber, "three", 99);// FIXME happens after the window is open

                push(subscriber, "four", 101);
                push(subscriber, "five", 102);
                complete(subscriber, 150);
            }
        });
        Flowable<Flowable<String>> windowed = source.window(100, TimeUnit.MILLISECONDS, scheduler);
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
        Flowable<Flowable<Integer>> source = Flowable.range(1, 10).window(1, TimeUnit.MINUTES, scheduler, 3);
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
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final AtomicInteger wip = new AtomicInteger();
        final int indicator = 999999999;
        FlowableWindowWithSizeTest.hotStream().window(300, TimeUnit.MILLISECONDS).take(10).doOnComplete(new Action() {
            @Override
            public void run() {
                System.out.println("Main done!");
            }
        }).flatMap(new Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> w) {
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
        }).subscribe(ts);
        ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
        ts.assertComplete();
        Assert.assertTrue(((ts.valueCount()) != 0));
    }

    @Test
    public void timespanTimeskipCustomSchedulerBufferSize() {
        Flowable.range(1, 10).window(1, 1, TimeUnit.MINUTES, Schedulers.io(), 2).flatMap(Functions.<Flowable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void timespanDefaultSchedulerSize() {
        Flowable.range(1, 10).window(1, TimeUnit.MINUTES, 20).flatMap(Functions.<Flowable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void timespanDefaultSchedulerSizeRestart() {
        Flowable.range(1, 10).window(1, TimeUnit.MINUTES, 20, true).flatMap(Functions.<Flowable<Integer>>identity(), true).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void invalidSpan() {
        try {
            Flowable.just(1).window((-99), 1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown!");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("timespan > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void timespanTimeskipDefaultScheduler() {
        Flowable.just(1).window(1, 1, TimeUnit.MINUTES).flatMap(Functions.<Flowable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timespanTimeskipCustomScheduler() {
        Flowable.just(1).window(1, 1, TimeUnit.MINUTES, Schedulers.io()).flatMap(Functions.<Flowable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timeskipJustOverlap() {
        Flowable.just(1).window(2, 1, TimeUnit.MINUTES, Schedulers.single()).flatMap(Functions.<Flowable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timeskipJustSkip() {
        Flowable.just(1).window(1, 2, TimeUnit.MINUTES, Schedulers.single()).flatMap(Functions.<Flowable<Integer>>identity()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void timeskipSkipping() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.window(1, 2, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Flowable<Integer>>identity()).test();
        pp.onNext(1);
        pp.onNext(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        pp.onNext(3);
        pp.onNext(4);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        pp.onNext(5);
        pp.onNext(6);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        pp.onNext(7);
        pp.onComplete();
        ts.assertResult(1, 2, 5, 6);
    }

    @Test
    public void timeskipOverlapping() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.window(2, 1, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Flowable<Integer>>identity()).test();
        pp.onNext(1);
        pp.onNext(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        pp.onNext(3);
        pp.onNext(4);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        pp.onNext(5);
        pp.onNext(6);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        pp.onNext(7);
        pp.onComplete();
        ts.assertResult(1, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7);
    }

    @Test
    public void exactOnError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestScheduler scheduler = new TestScheduler();
            PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.window(1, 1, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Flowable<Integer>>identity()).test();
            pp.onError(new TestException());
            ts.assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void overlappingOnError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestScheduler scheduler = new TestScheduler();
            PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.window(2, 1, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Flowable<Integer>>identity()).test();
            pp.onError(new TestException());
            ts.assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void skipOnError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestScheduler scheduler = new TestScheduler();
            PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.window(1, 2, TimeUnit.SECONDS, scheduler).flatMap(Functions.<Flowable<Integer>>identity()).test();
            pp.onError(new TestException());
            ts.assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void exactBackpressure() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, 1, TimeUnit.SECONDS, scheduler).test(0L);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ts.assertFailure(MissingBackpressureException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skipBackpressure() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, 2, TimeUnit.SECONDS, scheduler).test(0L);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ts.assertFailure(MissingBackpressureException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void overlapBackpressure() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(2, 1, TimeUnit.SECONDS, scheduler).test(0L);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        ts.assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void exactBackpressure2() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, 1, TimeUnit.SECONDS, scheduler).test(1L);
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        ts.assertError(MissingBackpressureException.class);
    }

    @Test
    public void skipBackpressure2() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, 2, TimeUnit.SECONDS, scheduler).test(1L);
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        ts.assertError(MissingBackpressureException.class);
    }

    @Test
    public void overlapBackpressure2() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestScheduler scheduler = new TestScheduler();
            PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Flowable<Integer>> ts = pp.window(2, 1, TimeUnit.SECONDS, scheduler).test(1L);
            scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
            ts.assertError(MissingBackpressureException.class);
            TestHelper.assertError(errors, 0, MissingBackpressureException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.range(1, 5).window(1, TimeUnit.DAYS, Schedulers.single()).onBackpressureDrop());
        TestHelper.checkDisposed(Flowable.range(1, 5).window(2, 1, TimeUnit.DAYS, Schedulers.single()).onBackpressureDrop());
        TestHelper.checkDisposed(Flowable.range(1, 5).window(1, 2, TimeUnit.DAYS, Schedulers.single()).onBackpressureDrop());
        TestHelper.checkDisposed(Flowable.never().window(1, TimeUnit.DAYS, Schedulers.single(), 2, true).onBackpressureDrop());
    }

    @Test
    public void restartTimer() {
        Flowable.range(1, 5).window(1, TimeUnit.DAYS, Schedulers.single(), 2, true).flatMap(Functions.<Flowable<Integer>>identity()).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void exactBoundaryError() {
        Flowable.error(new TestException()).window(1, TimeUnit.DAYS, Schedulers.single(), 2, true).test().assertSubscribed().assertError(TestException.class).assertNotComplete();
    }

    @Test
    public void restartTimerMany() throws Exception {
        final AtomicBoolean cancel1 = new AtomicBoolean();
        Flowable.intervalRange(1, 1000, 1, 1, TimeUnit.MILLISECONDS).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                cancel1.set(true);
            }
        }).window(1, TimeUnit.MILLISECONDS, Schedulers.single(), 2, true).flatMap(Functions.<Flowable<Long>>identity()).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(500).assertNoErrors().assertComplete();
        int timeout = 20;
        while (((timeout--) > 0) && (!(cancel1.get()))) {
            Thread.sleep(100);
        } 
        Assert.assertTrue("intervalRange was not cancelled!", cancel1.get());
    }

    @Test
    public void exactUnboundedReentrant() {
        TestScheduler scheduler = new TestScheduler();
        final FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(1, TimeUnit.MILLISECONDS, scheduler).flatMap(new Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(ts);
        ps.onNext(1);
        ts.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void exactBoundedReentrant() {
        TestScheduler scheduler = new TestScheduler();
        final FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(1, TimeUnit.MILLISECONDS, scheduler, 10, true).flatMap(new Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(ts);
        ps.onNext(1);
        ts.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void exactBoundedReentrant2() {
        TestScheduler scheduler = new TestScheduler();
        final FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(1, TimeUnit.MILLISECONDS, scheduler, 2, true).flatMap(new Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(ts);
        ps.onNext(1);
        ts.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void skipReentrant() {
        TestScheduler scheduler = new TestScheduler();
        final FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(1, 2, TimeUnit.MILLISECONDS, scheduler).flatMap(new Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(ts);
        ps.onNext(1);
        ts.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void sizeTimeTimeout() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(5, TimeUnit.MILLISECONDS, scheduler, 100).test().assertValueCount(1);
        scheduler.advanceTimeBy(5, TimeUnit.MILLISECONDS);
        ts.assertValueCount(2).assertNoErrors().assertNotComplete();
        ts.values().get(0).test().assertResult();
    }

    @Test
    public void periodicWindowCompletion() {
        TestScheduler scheduler = new TestScheduler();
        FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = ps.window(5, TimeUnit.MILLISECONDS, scheduler, Long.MAX_VALUE, false).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        ts.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionRestartTimer() {
        TestScheduler scheduler = new TestScheduler();
        FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = ps.window(5, TimeUnit.MILLISECONDS, scheduler, Long.MAX_VALUE, true).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        ts.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionBounded() {
        TestScheduler scheduler = new TestScheduler();
        FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 5, false).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        ts.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionRestartTimerBounded() {
        TestScheduler scheduler = new TestScheduler();
        FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 5, true).test();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        ts.assertValueCount(21).assertNoErrors().assertNotComplete();
    }

    @Test
    public void periodicWindowCompletionRestartTimerBoundedSomeData() {
        TestScheduler scheduler = new TestScheduler();
        FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 2, true).test();
        ps.onNext(1);
        ps.onNext(2);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        ts.assertValueCount(22).assertNoErrors().assertNotComplete();
    }

    @Test
    public void countRestartsOnTimeTick() {
        TestScheduler scheduler = new TestScheduler();
        FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Flowable<Integer>> ts = ps.window(5, TimeUnit.MILLISECONDS, scheduler, 5, true).test();
        // window #1
        ps.onNext(1);
        ps.onNext(2);
        scheduler.advanceTimeBy(5, TimeUnit.MILLISECONDS);
        // window #2
        ps.onNext(3);
        ps.onNext(4);
        ps.onNext(5);
        ps.onNext(6);
        ts.assertValueCount(2).assertNoErrors().assertNotComplete();
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Publisher<Flowable<Object>>>() {
            @Override
            public io.reactivex.Publisher<Flowable<Object>> apply(Flowable<Object> f) throws Exception {
                return f.window(1, TimeUnit.SECONDS, 1).takeLast(0);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void firstWindowMissingBackpressure() {
        Flowable.never().window(1, TimeUnit.SECONDS, 1).test(0L).assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void nextWindowMissingBackpressure() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, TimeUnit.SECONDS, 1).test(1L);
        pp.onNext(1);
        ts.assertValueCount(1).assertError(MissingBackpressureException.class).assertNotComplete();
    }

    @Test
    public void cancelUpfront() {
        Flowable.never().window(1, TimeUnit.SECONDS, 1).test(0L, true).assertEmpty();
    }

    @Test
    public void nextWindowMissingBackpressureDrainOnSize() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, TimeUnit.MINUTES, 1).subscribeWith(new TestSubscriber<Flowable<Integer>>(2) {
            int calls;

            @Override
            public void onNext(Flowable<Integer> t) {
                super.onNext(t);
                if ((++(calls)) == 2) {
                    pp.onNext(2);
                }
            }
        });
        pp.onNext(1);
        ts.assertValueCount(2).assertError(MissingBackpressureException.class).assertNotComplete();
    }

    @Test
    public void nextWindowMissingBackpressureDrainOnTime() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        final TestScheduler sch = new TestScheduler();
        TestSubscriber<Flowable<Integer>> ts = pp.window(1, TimeUnit.MILLISECONDS, sch, 10).test(1);
        sch.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        ts.assertValueCount(1).assertError(MissingBackpressureException.class).assertNotComplete();
    }
}

