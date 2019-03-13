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
package io.reactivex.subjects;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ReplaySubjectTest extends SubjectTest<Integer> {
    private final Throwable testException = new Throwable();

    @Test
    public void testCompleted() {
        ReplaySubject<String> subject = ReplaySubject.create();
        Observer<String> o1 = mockObserver();
        subject.subscribe(o1);
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        subject.onComplete();
        subject.onNext("four");
        subject.onComplete();
        subject.onError(new Throwable());
        assertCompletedSubscriber(o1);
        // assert that subscribing a 2nd time gets the same data
        Observer<String> o2 = mockObserver();
        subject.subscribe(o2);
        assertCompletedSubscriber(o2);
    }

    @Test
    public void testCompletedStopsEmittingData() {
        ReplaySubject<Integer> channel = ReplaySubject.create();
        Observer<Object> observerA = mockObserver();
        Observer<Object> observerB = mockObserver();
        Observer<Object> observerC = mockObserver();
        Observer<Object> observerD = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(observerA);
        channel.subscribe(to);
        channel.subscribe(observerB);
        InOrder inOrderA = Mockito.inOrder(observerA);
        InOrder inOrderB = Mockito.inOrder(observerB);
        InOrder inOrderC = Mockito.inOrder(observerC);
        InOrder inOrderD = Mockito.inOrder(observerD);
        channel.onNext(42);
        // both A and B should have received 42 from before subscription
        inOrderA.verify(observerA).onNext(42);
        inOrderB.verify(observerB).onNext(42);
        to.dispose();
        // a should receive no more
        inOrderA.verifyNoMoreInteractions();
        channel.onNext(4711);
        // only be should receive 4711 at this point
        inOrderB.verify(observerB).onNext(4711);
        channel.onComplete();
        // B is subscribed so should receive onComplete
        inOrderB.verify(observerB).onComplete();
        channel.subscribe(observerC);
        // when C subscribes it should receive 42, 4711, onComplete
        inOrderC.verify(observerC).onNext(42);
        inOrderC.verify(observerC).onNext(4711);
        inOrderC.verify(observerC).onComplete();
        // if further events are propagated they should be ignored
        channel.onNext(13);
        channel.onNext(14);
        channel.onNext(15);
        channel.onError(new RuntimeException());
        // a new subscription should only receive what was emitted prior to terminal state onComplete
        channel.subscribe(observerD);
        inOrderD.verify(observerD).onNext(42);
        inOrderD.verify(observerD).onNext(4711);
        inOrderD.verify(observerD).onComplete();
        Mockito.verify(observerA).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verify(observerB).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verify(observerC).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verify(observerD).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verifyNoMoreInteractions(observerA);
        Mockito.verifyNoMoreInteractions(observerB);
        Mockito.verifyNoMoreInteractions(observerC);
        Mockito.verifyNoMoreInteractions(observerD);
    }

    @Test
    public void testCompletedAfterError() {
        ReplaySubject<String> subject = ReplaySubject.create();
        Observer<String> observer = mockObserver();
        subject.onNext("one");
        subject.onError(testException);
        subject.onNext("two");
        subject.onComplete();
        subject.onError(new RuntimeException());
        subject.subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onError(testException);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testError() {
        ReplaySubject<String> subject = ReplaySubject.create();
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        subject.onError(testException);
        subject.onNext("four");
        subject.onError(new Throwable());
        subject.onComplete();
        assertErrorSubscriber(observer);
        observer = TestHelper.mockObserver();
        subject.subscribe(observer);
        assertErrorSubscriber(observer);
    }

    @Test
    public void testSubscribeMidSequence() {
        ReplaySubject<String> subject = ReplaySubject.create();
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        assertObservedUntilTwo(observer);
        Observer<String> anotherSubscriber = mockObserver();
        subject.subscribe(anotherSubscriber);
        assertObservedUntilTwo(anotherSubscriber);
        subject.onNext("three");
        subject.onComplete();
        assertCompletedSubscriber(observer);
        assertCompletedSubscriber(anotherSubscriber);
    }

    @Test
    public void testUnsubscribeFirstSubscriber() {
        ReplaySubject<String> subject = ReplaySubject.create();
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        subject.subscribe(to);
        subject.onNext("one");
        subject.onNext("two");
        to.dispose();
        assertObservedUntilTwo(observer);
        Observer<String> anotherSubscriber = mockObserver();
        subject.subscribe(anotherSubscriber);
        assertObservedUntilTwo(anotherSubscriber);
        subject.onNext("three");
        subject.onComplete();
        assertObservedUntilTwo(observer);
        assertCompletedSubscriber(anotherSubscriber);
    }

    @Test(timeout = 2000)
    public void testNewSubscriberDoesntBlockExisting() throws InterruptedException {
        final AtomicReference<String> lastValueForSubscriber1 = new AtomicReference<String>();
        Observer<String> observer1 = new DefaultObserver<String>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String v) {
                System.out.println(("observer1: " + v));
                lastValueForSubscriber1.set(v);
            }
        };
        final AtomicReference<String> lastValueForSubscriber2 = new AtomicReference<String>();
        final CountDownLatch oneReceived = new CountDownLatch(1);
        final CountDownLatch makeSlow = new CountDownLatch(1);
        final CountDownLatch completed = new CountDownLatch(1);
        Observer<String> observer2 = new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                completed.countDown();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String v) {
                System.out.println(("observer2: " + v));
                if (v.equals("one")) {
                    oneReceived.countDown();
                } else {
                    try {
                        makeSlow.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastValueForSubscriber2.set(v);
                }
            }
        };
        ReplaySubject<String> subject = ReplaySubject.create();
        subject.subscribe(observer1);
        subject.onNext("one");
        Assert.assertEquals("one", lastValueForSubscriber1.get());
        subject.onNext("two");
        Assert.assertEquals("two", lastValueForSubscriber1.get());
        // use subscribeOn to make this async otherwise we deadlock as we are using CountDownLatches
        subject.subscribeOn(Schedulers.newThread()).subscribe(observer2);
        System.out.println("before waiting for one");
        // wait until observer2 starts having replay occur
        oneReceived.await();
        System.out.println("after waiting for one");
        subject.onNext("three");
        System.out.println("sent three");
        // if subscription blocked existing subscribers then 'makeSlow' would cause this to not be there yet
        Assert.assertEquals("three", lastValueForSubscriber1.get());
        System.out.println("about to send onComplete");
        subject.onComplete();
        System.out.println("completed subject");
        // release
        makeSlow.countDown();
        System.out.println("makeSlow released");
        completed.await();
        // all of them should be emitted with the last being "three"
        Assert.assertEquals("three", lastValueForSubscriber2.get());
    }

    @Test
    public void testSubscriptionLeak() {
        ReplaySubject<Object> subject = ReplaySubject.create();
        Disposable d = subject.subscribe();
        Assert.assertEquals(1, subject.observerCount());
        d.dispose();
        Assert.assertEquals(0, subject.observerCount());
    }

    @Test(timeout = 1000)
    public void testUnsubscriptionCase() {
        ReplaySubject<String> src = ReplaySubject.create();
        for (int i = 0; i < 10; i++) {
            final Observer<Object> o = mockObserver();
            InOrder inOrder = Mockito.inOrder(o);
            String v = "" + i;
            src.onNext(v);
            System.out.printf("Turn: %d%n", i);
            src.firstElement().toObservable().flatMap(new Function<String, Observable<String>>() {
                @Override
                public io.reactivex.Observable<String> apply(String t1) {
                    return Observable.just(((t1 + ", ") + t1));
                }
            }).subscribe(new DefaultObserver<String>() {
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
            Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testTerminateOnce() {
        ReplaySubject<Integer> source = ReplaySubject.create();
        source.onNext(1);
        source.onNext(2);
        source.onComplete();
        final Observer<Integer> o = mockObserver();
        source.subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
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
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onNext(2);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testReplay1AfterTermination() {
        ReplaySubject<Integer> source = ReplaySubject.createWithSize(1);
        source.onNext(1);
        source.onNext(2);
        source.onComplete();
        for (int i = 0; i < 1; i++) {
            Observer<Integer> o = mockObserver();
            source.subscribe(o);
            Mockito.verify(o, Mockito.never()).onNext(1);
            Mockito.verify(o).onNext(2);
            Mockito.verify(o).onComplete();
            Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testReplay1Directly() {
        ReplaySubject<Integer> source = ReplaySubject.createWithSize(1);
        Observer<Integer> o = mockObserver();
        source.onNext(1);
        source.onNext(2);
        source.subscribe(o);
        source.onNext(3);
        source.onComplete();
        Mockito.verify(o, Mockito.never()).onNext(1);
        Mockito.verify(o).onNext(2);
        Mockito.verify(o).onNext(3);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testReplayTimestampedAfterTermination() {
        TestScheduler scheduler = new TestScheduler();
        ReplaySubject<Integer> source = ReplaySubject.createWithTime(1, TimeUnit.SECONDS, scheduler);
        source.onNext(1);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(3);
        source.onComplete();
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Observer<Integer> o = mockObserver();
        source.subscribe(o);
        Mockito.verify(o, Mockito.never()).onNext(1);
        Mockito.verify(o, Mockito.never()).onNext(2);
        Mockito.verify(o, Mockito.never()).onNext(3);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testReplayTimestampedDirectly() {
        TestScheduler scheduler = new TestScheduler();
        ReplaySubject<Integer> source = ReplaySubject.createWithTime(1, TimeUnit.SECONDS, scheduler);
        source.onNext(1);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Observer<Integer> o = mockObserver();
        source.subscribe(o);
        source.onNext(2);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(3);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onComplete();
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o, Mockito.never()).onNext(1);
        Mockito.verify(o).onNext(2);
        Mockito.verify(o).onNext(3);
        Mockito.verify(o).onComplete();
    }

    // FIXME RS subscribers can't throw
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery() {
    // ReplaySubject<String> ps = ReplaySubject.create();
    // 
    // ps.subscribe();
    // TestObserver<String> to = new TestObserver<String>();
    // ps.subscribe(to);
    // 
    // try {
    // ps.onError(new RuntimeException("an exception"));
    // fail("expect OnErrorNotImplementedException");
    // } catch (OnErrorNotImplementedException e) {
    // // ignore
    // }
    // // even though the onError above throws we should still receive it on the other subscriber
    // assertEquals(1, to.errors().size());
    // }
    // FIXME RS subscribers can't throw
    // /**
    // * This one has multiple failures so should get a CompositeException
    // */
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery2() {
    // ReplaySubject<String> ps = ReplaySubject.create();
    // 
    // ps.subscribe();
    // ps.subscribe();
    // TestObserver<String> to = new TestObserver<String>();
    // ps.subscribe(to);
    // ps.subscribe();
    // ps.subscribe();
    // ps.subscribe();
    // 
    // try {
    // ps.onError(new RuntimeException("an exception"));
    // fail("expect OnErrorNotImplementedException");
    // } catch (CompositeException e) {
    // // we should have 5 of them
    // assertEquals(5, e.getExceptions().size());
    // }
    // // even though the onError above throws we should still receive it on the other subscriber
    // assertEquals(1, to.getOnErrorEvents().size());
    // }
    @Test
    public void testCurrentStateMethodsNormal() {
        ReplaySubject<Object> as = ReplaySubject.create();
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getThrowable());
        as.onNext(1);
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsEmpty() {
        ReplaySubject<Object> as = ReplaySubject.create();
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsError() {
        ReplaySubject<Object> as = ReplaySubject.create();
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getThrowable());
        as.onError(new TestException());
        Assert.assertTrue(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertTrue(((as.getThrowable()) instanceof TestException));
    }

    @Test
    public void testSizeAndHasAnyValueUnbounded() {
        ReplaySubject<Object> rs = ReplaySubject.create();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onComplete();
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueEffectivelyUnbounded() {
        ReplaySubject<Object> rs = ReplaySubject.createUnbounded();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onComplete();
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueUnboundedError() {
        ReplaySubject<Object> rs = ReplaySubject.create();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onError(new TestException());
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueEffectivelyUnboundedError() {
        ReplaySubject<Object> rs = ReplaySubject.createUnbounded();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onNext(1);
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
        rs.onError(new TestException());
        Assert.assertEquals(2, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueUnboundedEmptyError() {
        ReplaySubject<Object> rs = ReplaySubject.create();
        rs.onError(new TestException());
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueEffectivelyUnboundedEmptyError() {
        ReplaySubject<Object> rs = ReplaySubject.createUnbounded();
        rs.onError(new TestException());
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueUnboundedEmptyCompleted() {
        ReplaySubject<Object> rs = ReplaySubject.create();
        rs.onComplete();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueEffectivelyUnboundedEmptyCompleted() {
        ReplaySubject<Object> rs = ReplaySubject.createUnbounded();
        rs.onComplete();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueSizeBounded() {
        ReplaySubject<Object> rs = ReplaySubject.createWithSize(1);
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        for (int i = 0; i < 1000; i++) {
            rs.onNext(i);
            Assert.assertEquals(1, rs.size());
            Assert.assertTrue(rs.hasValue());
        }
        rs.onComplete();
        Assert.assertEquals(1, rs.size());
        Assert.assertTrue(rs.hasValue());
    }

    @Test
    public void testSizeAndHasAnyValueTimeBounded() {
        TestScheduler to = new TestScheduler();
        ReplaySubject<Object> rs = ReplaySubject.createWithTime(1, TimeUnit.SECONDS, to);
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
        for (int i = 0; i < 1000; i++) {
            rs.onNext(i);
            Assert.assertEquals(1, rs.size());
            Assert.assertTrue(rs.hasValue());
            to.advanceTimeBy(2, TimeUnit.SECONDS);
            Assert.assertEquals(0, rs.size());
            Assert.assertFalse(rs.hasValue());
        }
        rs.onComplete();
        Assert.assertEquals(0, rs.size());
        Assert.assertFalse(rs.hasValue());
    }

    @Test
    public void testGetValues() {
        ReplaySubject<Object> rs = ReplaySubject.create();
        Object[] expected = new Object[10];
        for (int i = 0; i < (expected.length); i++) {
            expected[i] = i;
            rs.onNext(i);
            Assert.assertArrayEquals(Arrays.copyOf(expected, (i + 1)), rs.getValues());
        }
        rs.onComplete();
        Assert.assertArrayEquals(expected, rs.getValues());
    }

    @Test
    public void testGetValuesUnbounded() {
        ReplaySubject<Object> rs = ReplaySubject.createUnbounded();
        Object[] expected = new Object[10];
        for (int i = 0; i < (expected.length); i++) {
            expected[i] = i;
            rs.onNext(i);
            Assert.assertArrayEquals(Arrays.copyOf(expected, (i + 1)), rs.getValues());
        }
        rs.onComplete();
        Assert.assertArrayEquals(expected, rs.getValues());
    }

    @Test
    public void createWithSizeInvalidCapacity() {
        try {
            ReplaySubject.createWithSize((-99));
            Assert.fail("Didn't throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("maxSize > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void createWithTimeAndSizeInvalidCapacity() {
        try {
            ReplaySubject.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.computation(), (-99));
            Assert.fail("Didn't throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("maxSize > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void hasSubscribers() {
        ReplaySubject<Integer> rp = ReplaySubject.create();
        Assert.assertFalse(rp.hasObservers());
        TestObserver<Integer> to = rp.test();
        Assert.assertTrue(rp.hasObservers());
        to.cancel();
        Assert.assertFalse(rp.hasObservers());
    }

    @Test
    public void peekStateUnbounded() {
        ReplaySubject<Integer> rp = ReplaySubject.create();
        rp.onNext(1);
        Assert.assertEquals(((Integer) (1)), rp.getValue());
        Assert.assertEquals(1, rp.getValues()[0]);
    }

    @Test
    public void peekStateTimeAndSize() {
        ReplaySubject<Integer> rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.computation(), 1);
        rp.onNext(1);
        Assert.assertEquals(((Integer) (1)), rp.getValue());
        Assert.assertEquals(1, rp.getValues()[0]);
        rp.onNext(2);
        Assert.assertEquals(((Integer) (2)), rp.getValue());
        Assert.assertEquals(2, rp.getValues()[0]);
        Assert.assertEquals(((Integer) (2)), rp.getValues(new Integer[0])[0]);
        Assert.assertEquals(((Integer) (2)), rp.getValues(new Integer[1])[0]);
        Integer[] a = new Integer[2];
        Assert.assertEquals(((Integer) (2)), rp.getValues(a)[0]);
        Assert.assertNull(a[1]);
    }

    @Test
    public void peekStateTimeAndSizeValue() {
        ReplaySubject<Integer> rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.computation(), 1);
        Assert.assertNull(rp.getValue());
        Assert.assertEquals(0, rp.getValues().length);
        Assert.assertNull(rp.getValues(new Integer[2])[0]);
        rp.onComplete();
        Assert.assertNull(rp.getValue());
        Assert.assertEquals(0, rp.getValues().length);
        Assert.assertNull(rp.getValues(new Integer[2])[0]);
        rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.computation(), 1);
        rp.onError(new TestException());
        Assert.assertNull(rp.getValue());
        Assert.assertEquals(0, rp.getValues().length);
        Assert.assertNull(rp.getValues(new Integer[2])[0]);
    }

    @Test
    public void peekStateTimeAndSizeValueExpired() {
        TestScheduler scheduler = new TestScheduler();
        ReplaySubject<Integer> rp = ReplaySubject.createWithTime(1, TimeUnit.DAYS, scheduler);
        Assert.assertNull(rp.getValue());
        Assert.assertNull(rp.getValues(new Integer[2])[0]);
        rp.onNext(2);
        Assert.assertEquals(((Integer) (2)), rp.getValue());
        Assert.assertEquals(2, rp.getValues()[0]);
        scheduler.advanceTimeBy(2, TimeUnit.DAYS);
        Assert.assertEquals(null, rp.getValue());
        Assert.assertEquals(0, rp.getValues().length);
        Assert.assertNull(rp.getValues(new Integer[2])[0]);
    }

    @Test
    public void capacityHint() {
        ReplaySubject<Integer> rp = ReplaySubject.create(8);
        for (int i = 0; i < 15; i++) {
            rp.onNext(i);
        }
        rp.onComplete();
        rp.test().assertResult(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
    }

    @Test
    public void subscribeCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestObserver<Integer> to = new TestObserver<Integer>();
            final ReplaySubject<Integer> rp = ReplaySubject.create();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    rp.subscribe(to);
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
    public void subscribeAfterDone() {
        ReplaySubject<Integer> rp = ReplaySubject.create();
        rp.onComplete();
        Disposable bs = Disposables.empty();
        rp.onSubscribe(bs);
        Assert.assertTrue(bs.isDisposed());
    }

    @Test
    public void subscribeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ReplaySubject<Integer> rp = ReplaySubject.create();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    rp.test();
                }
            };
            TestHelper.race(r1, r1);
        }
    }

    @Test
    public void cancelUpfront() {
        ReplaySubject<Integer> rp = ReplaySubject.create();
        rp.test();
        rp.test();
        TestObserver<Integer> to = rp.test(true);
        Assert.assertEquals(2, rp.observerCount());
        to.assertEmpty();
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ReplaySubject<Integer> rp = ReplaySubject.create();
            final TestObserver<Integer> to1 = rp.test();
            final TestObserver<Integer> to2 = rp.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to1.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to2.cancel();
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertFalse(rp.hasObservers());
        }
    }

    @Test
    public void sizeboundReplayError() {
        ReplaySubject<Integer> rp = ReplaySubject.createWithSize(2);
        rp.onNext(1);
        rp.onNext(2);
        rp.onNext(3);
        rp.onNext(4);
        rp.onError(new TestException());
        rp.test().assertFailure(TestException.class, 3, 4);
    }

    @Test
    public void sizeAndTimeBoundReplayError() {
        ReplaySubject<Integer> rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.DAYS, Schedulers.single(), 2);
        rp.onNext(1);
        rp.onNext(2);
        rp.onNext(3);
        rp.onNext(4);
        rp.onError(new TestException());
        rp.test().assertFailure(TestException.class, 3, 4);
    }

    @Test
    public void timedSkipOld() {
        TestScheduler scheduler = new TestScheduler();
        ReplaySubject<Integer> rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.SECONDS, scheduler, 2);
        rp.onNext(1);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        rp.test().assertEmpty();
    }

    @Test
    public void takeSizeAndTime() {
        TestScheduler scheduler = new TestScheduler();
        ReplaySubject<Integer> rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.SECONDS, scheduler, 2);
        rp.onNext(1);
        rp.onNext(2);
        rp.onNext(3);
        rp.take(1).test().assertResult(2);
    }

    @Test
    public void takeSize() {
        ReplaySubject<Integer> rp = ReplaySubject.createWithSize(2);
        rp.onNext(1);
        rp.onNext(2);
        rp.onNext(3);
        rp.take(1).test().assertResult(2);
    }

    @Test
    public void reentrantDrain() {
        TestScheduler scheduler = new TestScheduler();
        final ReplaySubject<Integer> rp = ReplaySubject.createWithTimeAndSize(1, TimeUnit.SECONDS, scheduler, 2);
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t == 1) {
                    rp.onNext(2);
                }
                super.onNext(t);
            }
        };
        rp.subscribe(to);
        rp.onNext(1);
        rp.onComplete();
        to.assertResult(1, 2);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(ReplaySubject.create());
        TestHelper.checkDisposed(ReplaySubject.createUnbounded());
        TestHelper.checkDisposed(ReplaySubject.createWithSize(10));
        TestHelper.checkDisposed(ReplaySubject.createWithTimeAndSize(1, TimeUnit.SECONDS, Schedulers.single(), 10));
    }

    @Test
    public void timedNoOutdatedData() {
        TestScheduler scheduler = new TestScheduler();
        ReplaySubject<Integer> source = ReplaySubject.createWithTime(2, TimeUnit.SECONDS, scheduler);
        source.onNext(1);
        source.onComplete();
        source.test().assertResult(1);
        source.test().assertResult(1);
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        source.test().assertResult();
    }

    @Test
    public void noHeadRetentionCompleteSize() {
        ReplaySubject<Integer> source = ReplaySubject.createWithSize(1);
        source.onNext(1);
        source.onNext(2);
        source.onComplete();
        SizeBoundReplayBuffer<Integer> buf = ((SizeBoundReplayBuffer<Integer>) (source.buffer));
        Assert.assertNull(buf.head.value);
        Object o = buf.head;
        source.cleanupBuffer();
        Assert.assertSame(o, buf.head);
    }

    @Test
    public void noHeadRetentionSize() {
        ReplaySubject<Integer> source = ReplaySubject.createWithSize(1);
        source.onNext(1);
        source.onNext(2);
        SizeBoundReplayBuffer<Integer> buf = ((SizeBoundReplayBuffer<Integer>) (source.buffer));
        Assert.assertNotNull(buf.head.value);
        source.cleanupBuffer();
        Assert.assertNull(buf.head.value);
        Object o = buf.head;
        source.cleanupBuffer();
        Assert.assertSame(o, buf.head);
    }

    @Test
    public void noHeadRetentionCompleteTime() {
        ReplaySubject<Integer> source = ReplaySubject.createWithTime(1, TimeUnit.MINUTES, Schedulers.computation());
        source.onNext(1);
        source.onNext(2);
        source.onComplete();
        SizeAndTimeBoundReplayBuffer<Integer> buf = ((SizeAndTimeBoundReplayBuffer<Integer>) (source.buffer));
        Assert.assertNull(buf.head.value);
        Object o = buf.head;
        source.cleanupBuffer();
        Assert.assertSame(o, buf.head);
    }

    @Test
    public void noHeadRetentionTime() {
        TestScheduler sch = new TestScheduler();
        ReplaySubject<Integer> source = ReplaySubject.createWithTime(1, TimeUnit.MILLISECONDS, sch);
        source.onNext(1);
        sch.advanceTimeBy(2, TimeUnit.MILLISECONDS);
        source.onNext(2);
        SizeAndTimeBoundReplayBuffer<Integer> buf = ((SizeAndTimeBoundReplayBuffer<Integer>) (source.buffer));
        Assert.assertNotNull(buf.head.value);
        source.cleanupBuffer();
        Assert.assertNull(buf.head.value);
        Object o = buf.head;
        source.cleanupBuffer();
        Assert.assertSame(o, buf.head);
    }

    @Test
    public void noBoundedRetentionViaThreadLocal() throws Exception {
        final ReplaySubject<byte[]> rs = ReplaySubject.createWithSize(1);
        Observable<byte[]> source = rs.take(1).concatMap(new Function<byte[], Observable<byte[]>>() {
            @Override
            public io.reactivex.Observable<byte[]> apply(byte[] v) throws Exception {
                return rs;
            }
        }).takeLast(1);
        System.out.println("Bounded Replay Leak check: Wait before GC");
        Thread.sleep(1000);
        System.out.println("Bounded Replay Leak check: GC");
        System.gc();
        Thread.sleep(500);
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memHeap = memoryMXBean.getHeapMemoryUsage();
        long initial = memHeap.getUsed();
        System.out.printf("Bounded Replay Leak check: Starting: %.3f MB%n", ((initial / 1024.0) / 1024.0));
        final AtomicLong after = new AtomicLong();
        source.subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] v) throws Exception {
                System.out.println("Bounded Replay Leak check: Wait before GC 2");
                Thread.sleep(1000);
                System.out.println("Bounded Replay Leak check:  GC 2");
                System.gc();
                Thread.sleep(500);
                after.set(memoryMXBean.getHeapMemoryUsage().getUsed());
            }
        });
        for (int i = 0; i < 200; i++) {
            rs.onNext(new byte[1024 * 1024]);
        }
        rs.onComplete();
        System.out.printf("Bounded Replay Leak check: After: %.3f MB%n", (((after.get()) / 1024.0) / 1024.0));
        if ((initial + ((100 * 1024) * 1024)) < (after.get())) {
            Assert.fail(((("Bounded Replay Leak check: Memory leak detected: " + ((initial / 1024.0) / 1024.0)) + " -> ") + (((after.get()) / 1024.0) / 1024.0)));
        }
    }
}

