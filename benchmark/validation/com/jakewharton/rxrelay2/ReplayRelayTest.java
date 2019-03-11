/**
 * Copyright 2016 Netflix, Inc.
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
package com.jakewharton.rxrelay2;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


public class ReplayRelayTest {
    @Test
    public void testSubscriptionLeak() {
        ReplayRelay<Object> subject = ReplayRelay.create();
        Disposable s = subject.subscribe();
        Assert.assertEquals(1, subject.observerCount());
        s.dispose();
        Assert.assertEquals(0, subject.observerCount());
    }

    @Test(timeout = 1000)
    public void testUnsubscriptionCase() {
        ReplayRelay<String> src = ReplayRelay.create();
        for (int i = 0; i < 10; i++) {
            final Observer<Object> o = mockObserver();
            InOrder inOrder = Mockito.inOrder(o);
            String v = "" + i;
            src.accept(v);
            System.out.printf("Turn: %d%n", i);
            src.firstElement().toObservable().flatMap(new io.reactivex.functions.Function<String, Observable<String>>() {
                @Override
                public Observable<String> apply(String t1) {
                    return Observable.just(((t1 + ", ") + t1));
                }
            }).subscribe(new io.reactivex.observers.DefaultObserver<String>() {
                @Override
                public void onNext(String t) {
                    System.out.println(t);
                    o.onNext(t);
                }

                @Override
                public void onError(Throwable e) {
                    o.onError(e);
                }

                @Override
                public void onComplete() {
                    o.onComplete();
                }
            });
            inOrder.verify(o).onNext("0, 0");
            inOrder.verify(o).onComplete();
            Mockito.verify(o, Mockito.never()).onError(any(Throwable.class));
        }
    }

    @Test
    public void testReplayTimestampedDirectly() {
        TestScheduler scheduler = new TestScheduler();
        ReplayRelay<Integer> source = ReplayRelay.createWithTime(1, TimeUnit.SECONDS, scheduler);
        source.accept(1);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Observer<Integer> o = mockObserver();
        source.subscribe(o);
        source.accept(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.accept(3);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // source.onComplete();
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Mockito.verify(o, Mockito.never()).onError(any(Throwable.class));
        Mockito.verify(o, Mockito.never()).onNext(1);
        Mockito.verify(o).onNext(2);
        Mockito.verify(o).onNext(3);
        // verify(o).onComplete();
    }

    @Test
    public void testSizeAndHasAnyValueUnbounded() {
        ReplayRelay<Object> rs = ReplayRelay.create();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        rs.accept(1);
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.accept(1);
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueEffectivelyUnbounded() {
        ReplayRelay<Object> rs = ReplayRelay.createUnbounded();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        rs.accept(1);
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.accept(1);
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueSizeBounded() {
        ReplayRelay<Object> rs = ReplayRelay.createWithSize(1);
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        for (int i = 0; i < 1000; i++) {
            rs.accept(i);
            Assert.assertEquals(1, rs.size());
            Assert.assertTrue(rs.hasValue());
        }
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueTimeBounded() {
        TestScheduler ts = new TestScheduler();
        ReplayRelay<Object> rs = ReplayRelay.createWithTime(1, TimeUnit.SECONDS, ts);
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        for (int i = 0; i < 1000; i++) {
            rs.accept(i);
            Assert.assertEquals(1, rs.size());
            Assert.assertTrue(rs.hasValue());
            ts.advanceTimeBy(2, TimeUnit.SECONDS);
            Assert.assertEquals(0, rs.size());
            Assert.assertFalse(rs.hasValue());
        }
    }

    @Test
    public void testGetValues() {
        ReplayRelay<Object> rs = ReplayRelay.create();
        Object[] expected = new Object[10];
        for (int i = 0; i < (expected.length); i++) {
            expected[i] = i;
            rs.accept(i);
            Assert.assertArrayEquals(Arrays.copyOf(expected, (i + 1)), rs.getValues());
        }
        Assert.assertArrayEquals(expected, rs.getValues());
    }

    @Test
    public void testGetValuesUnbounded() {
        ReplayRelay<Object> rs = ReplayRelay.createUnbounded();
        Object[] expected = new Object[10];
        for (int i = 0; i < (expected.length); i++) {
            expected[i] = i;
            rs.accept(i);
            Assert.assertArrayEquals(Arrays.copyOf(expected, (i + 1)), rs.getValues());
        }
        Assert.assertArrayEquals(expected, rs.getValues());
    }

    @Test
    public void createWithSizeInvalidCapacity() {
        try {
            ReplayRelay.createWithSize((-99));
            Assert.fail("Didn't throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("maxSize > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void createWithTimeAndSizeInvalidCapacity() {
        try {
            ReplayRelay.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.computation(), (-99));
            Assert.fail("Didn't throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("maxSize > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void hasSubscribers() {
        ReplayRelay<Integer> rp = ReplayRelay.create();
        Assert.assertFalse(rp.hasObservers());
        TestObserver<Integer> ts = rp.test();
        Assert.assertTrue(rp.hasObservers());
        ts.cancel();
        Assert.assertFalse(rp.hasObservers());
    }

    @Test
    public void peekStateUnbounded() {
        ReplayRelay<Integer> rp = ReplayRelay.create();
        rp.accept(1);
        Assert.assertEquals(((Integer) (1)), rp.getValue());
        Assert.assertEquals(1, rp.getValues()[0]);
    }

    @Test
    public void peekStateTimeAndSize() {
        ReplayRelay<Integer> rp = ReplayRelay.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.computation(), 1);
        rp.accept(1);
        Assert.assertEquals(((Integer) (1)), rp.getValue());
        Assert.assertEquals(1, rp.getValues()[0]);
        rp.accept(2);
        Assert.assertEquals(((Integer) (2)), rp.getValue());
        Assert.assertEquals(2, rp.getValues()[0]);
        Assert.assertEquals(((Integer) (2)), rp.getValues(new Integer[0])[0]);
        Assert.assertEquals(((Integer) (2)), rp.getValues(new Integer[1])[0]);
        Integer[] a = new Integer[2];
        Assert.assertEquals(((Integer) (2)), rp.getValues(a)[0]);
        Assert.assertNull(a[1]);
    }

    @Test
    public void onNextNull() {
        final ReplayRelay<Object> s = ReplayRelay.create();
        try {
            s.accept(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("value == null", e.getMessage());
        }
    }

    @Test
    public void capacityHint() {
        ReplayRelay<Integer> rp = ReplayRelay.create(8);
        for (int i = 0; i < 15; i++) {
            rp.accept(i);
        }
        rp.test().assertValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
    }

    @Test
    public void subscribeCancelRace() {
        for (int i = 0; i < 500; i++) {
            final TestObserver<Integer> ts = new TestObserver<Integer>();
            final ReplayRelay<Integer> rp = ReplayRelay.create();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    rp.subscribe(ts);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2, Schedulers.single());
        }
    }

    @Test
    public void subscribeRace() {
        for (int i = 0; i < 500; i++) {
            final ReplayRelay<Integer> rp = ReplayRelay.create();
            Runnable r1 = new Runnable() {
                @Override
                @SuppressWarnings("CheckReturnValue")
                public void run() {
                    rp.test();
                }
            };
            TestHelper.race(r1, r1, Schedulers.single());
        }
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cancelUpfront() {
        ReplayRelay<Integer> rp = ReplayRelay.create();
        rp.test();
        rp.test();
        TestObserver<Integer> ts = rp.test(true);
        Assert.assertEquals(2, rp.observerCount());
        ts.assertEmpty();
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < 500; i++) {
            final ReplayRelay<Integer> rp = ReplayRelay.create();
            final TestObserver<Integer> ts1 = rp.test();
            final TestObserver<Integer> ts2 = rp.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts1.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts2.cancel();
                }
            };
            TestHelper.race(r1, r2, Schedulers.single());
            Assert.assertFalse(rp.hasObservers());
        }
    }

    @Test
    public void timedSkipOld() {
        TestScheduler scheduler = new TestScheduler();
        ReplayRelay<Integer> rp = ReplayRelay.createWithTimeAndSize(1, TimeUnit.SECONDS, scheduler, 2);
        rp.accept(1);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        rp.test().assertEmpty();
    }

    @Test
    public void takeSizeAndTime() {
        TestScheduler scheduler = new TestScheduler();
        ReplayRelay<Integer> rp = ReplayRelay.createWithTimeAndSize(1, TimeUnit.SECONDS, scheduler, 2);
        rp.accept(1);
        rp.accept(2);
        rp.accept(3);
        rp.take(1).test().assertResult(2);
    }

    @Test
    public void takeSize() {
        ReplayRelay<Integer> rp = ReplayRelay.createWithSize(2);
        rp.accept(1);
        rp.accept(2);
        rp.accept(3);
        rp.take(1).test().assertResult(2);
    }

    @Test
    public void reentrantDrain() {
        TestScheduler scheduler = new TestScheduler();
        final ReplayRelay<Integer> rp = ReplayRelay.createWithTimeAndSize(1, TimeUnit.SECONDS, scheduler, 2);
        TestObserver<Integer> ts = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t == 1) {
                    rp.accept(2);
                }
                super.onNext(t);
            }
        };
        rp.subscribe(ts);
        rp.accept(1);
        ts.assertValues(1, 2);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(ReplayRelay.create());
        TestHelper.checkDisposed(ReplayRelay.createUnbounded());
        TestHelper.checkDisposed(ReplayRelay.createWithSize(10));
        TestHelper.checkDisposed(ReplayRelay.createWithTimeAndSize(1, TimeUnit.SECONDS, Schedulers.single(), 10));
    }
}

