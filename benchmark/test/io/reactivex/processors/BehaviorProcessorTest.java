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
package io.reactivex.processors;


import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.BehaviorProcessor.BehaviorSubscription;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class BehaviorProcessorTest extends FlowableProcessorTest<Object> {
    private final Throwable testException = new Throwable();

    @Test
    public void testThatSubscriberReceivesDefaultValueAndSubsequentEvents() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onNext("two");
        processor.onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("default");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(testException);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testThatSubscriberReceivesLatestAndThenSubsequentEvents() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        processor.onNext("one");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("two");
        processor.onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onNext("default");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(testException);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testSubscribeThenOnComplete() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("default");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSubscribeToCompletedOnlyEmitsOnComplete() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        processor.onNext("one");
        processor.onComplete();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext("default");
        Mockito.verify(subscriber, Mockito.never()).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSubscribeToErrorOnlyEmitsOnError() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        processor.onNext("one");
        RuntimeException re = new RuntimeException("test error");
        processor.onError(re);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext("default");
        Mockito.verify(subscriber, Mockito.never()).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onError(re);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testCompletedStopsEmittingData() {
        BehaviorProcessor<Integer> channel = BehaviorProcessor.createDefault(2013);
        Subscriber<Object> observerA = TestHelper.mockSubscriber();
        Subscriber<Object> observerB = TestHelper.mockSubscriber();
        Subscriber<Object> observerC = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(observerA);
        channel.subscribe(ts);
        channel.subscribe(observerB);
        InOrder inOrderA = Mockito.inOrder(observerA);
        InOrder inOrderB = Mockito.inOrder(observerB);
        InOrder inOrderC = Mockito.inOrder(observerC);
        inOrderA.verify(observerA).onNext(2013);
        inOrderB.verify(observerB).onNext(2013);
        channel.onNext(42);
        inOrderA.verify(observerA).onNext(42);
        inOrderB.verify(observerB).onNext(42);
        ts.dispose();
        inOrderA.verifyNoMoreInteractions();
        channel.onNext(4711);
        inOrderB.verify(observerB).onNext(4711);
        channel.onComplete();
        inOrderB.verify(observerB).onComplete();
        channel.subscribe(observerC);
        inOrderC.verify(observerC).onComplete();
        channel.onNext(13);
        inOrderB.verifyNoMoreInteractions();
        inOrderC.verifyNoMoreInteractions();
    }

    @Test
    public void testCompletedAfterErrorIsNotSent() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onError(testException);
        processor.onNext("two");
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("default");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onError(testException);
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testCompletedAfterErrorIsNotSent2() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onError(testException);
        processor.onNext("two");
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("default");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onError(testException);
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Subscriber<Object> subscriber2 = TestHelper.mockSubscriber();
        processor.subscribe(subscriber2);
        Mockito.verify(subscriber2, Mockito.times(1)).onError(testException);
        Mockito.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber2, Mockito.never()).onComplete();
    }

    @Test
    public void testCompletedAfterErrorIsNotSent3() {
        BehaviorProcessor<String> processor = BehaviorProcessor.createDefault("default");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onComplete();
        processor.onNext("two");
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("default");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Subscriber<Object> subscriber2 = TestHelper.mockSubscriber();
        processor.subscribe(subscriber2);
        Mockito.verify(subscriber2, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 1000)
    public void testUnsubscriptionCase() {
        BehaviorProcessor<String> src = BehaviorProcessor.createDefault("null");// FIXME was plain null which is not allowed

        for (int i = 0; i < 10; i++) {
            final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
            InOrder inOrder = Mockito.inOrder(subscriber);
            String v = "" + i;
            src.onNext(v);
            System.out.printf("Turn: %d%n", i);
            src.firstElement().toFlowable().flatMap(new io.reactivex.functions.Function<String, Flowable<String>>() {
                @Override
                public io.reactivex.Flowable<String> apply(String t1) {
                    return Flowable.just(((t1 + ", ") + t1));
                }
            }).subscribe(new DefaultSubscriber<String>() {
                @Override
                public void onNext(String t) {
                    subscriber.onNext(t);
                }

                @Override
                public void onError(Throwable e) {
                    subscriber.onError(e);
                }

                @Override
                public void onComplete() {
                    subscriber.onComplete();
                }
            });
            inOrder.verify(subscriber).onNext(((v + ", ") + v));
            inOrder.verify(subscriber).onComplete();
            Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testStartEmpty() {
        BehaviorProcessor<Integer> source = BehaviorProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.subscribe(subscriber);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        source.onNext(1);
        source.onComplete();
        source.onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testStartEmptyThenAddOne() {
        BehaviorProcessor<Integer> source = BehaviorProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.onNext(1);
        source.subscribe(subscriber);
        inOrder.verify(subscriber).onNext(1);
        source.onComplete();
        source.onNext(2);
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testStartEmptyCompleteWithOne() {
        BehaviorProcessor<Integer> source = BehaviorProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.onNext(1);
        source.onComplete();
        source.onNext(2);
        source.subscribe(subscriber);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void testTakeOneSubscriber() {
        BehaviorProcessor<Integer> source = BehaviorProcessor.createDefault(1);
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.take(1).subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Assert.assertEquals(0, source.subscriberCount());
        Assert.assertFalse(source.hasSubscribers());
    }

    // FIXME RS subscribers are not allowed to throw
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery() {
    // BehaviorProcessor<String> ps = BehaviorProcessor.create();
    // 
    // ps.subscribe();
    // TestSubscriber<String> ts = new TestSubscriber<String>();
    // ps.subscribe(ts);
    // 
    // try {
    // ps.onError(new RuntimeException("an exception"));
    // fail("expect OnErrorNotImplementedException");
    // } catch (OnErrorNotImplementedException e) {
    // // ignore
    // }
    // // even though the onError above throws we should still receive it on the other subscriber
    // assertEquals(1, ts.getOnErrorEvents().size());
    // }
    // FIXME RS subscribers are not allowed to throw
    // /**
    // * This one has multiple failures so should get a CompositeException
    // */
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery2() {
    // BehaviorProcessor<String> ps = BehaviorProcessor.create();
    // 
    // ps.subscribe();
    // ps.subscribe();
    // TestSubscriber<String> ts = new TestSubscriber<String>();
    // ps.subscribe(ts);
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
    // assertEquals(1, ts.getOnErrorEvents().size());
    // }
    @Test
    public void testEmissionSubscriptionRace() throws Exception {
        Scheduler s = Schedulers.io();
        Scheduler.Worker worker = Schedulers.io().createWorker();
        try {
            for (int i = 0; i < 50000; i++) {
                if ((i % 1000) == 0) {
                    System.out.println(i);
                }
                final BehaviorProcessor<Object> rs = BehaviorProcessor.create();
                final CountDownLatch finish = new CountDownLatch(1);
                final CountDownLatch start = new CountDownLatch(1);
                worker.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            start.await();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        rs.onNext(1);
                    }
                });
                final AtomicReference<Object> o = new AtomicReference<Object>();
                rs.subscribeOn(s).observeOn(Schedulers.io()).subscribe(new DefaultSubscriber<Object>() {
                    @Override
                    public void onComplete() {
                        o.set((-1));
                        finish.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        o.set(e);
                        finish.countDown();
                    }

                    @Override
                    public void onNext(Object t) {
                        o.set(t);
                        finish.countDown();
                    }
                });
                start.countDown();
                if (!(finish.await(5, TimeUnit.SECONDS))) {
                    System.out.println(o.get());
                    System.out.println(rs.hasSubscribers());
                    rs.onComplete();
                    Assert.fail(("Timeout @ " + i));
                    break;
                } else {
                    Assert.assertEquals(1, o.get());
                    worker.schedule(new Runnable() {
                        @Override
                        public void run() {
                            rs.onComplete();
                        }
                    });
                }
            }
        } finally {
            worker.dispose();
        }
    }

    @Test
    public void testCurrentStateMethodsNormalEmptyStart() {
        BehaviorProcessor<Object> as = BehaviorProcessor.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onNext(1);
        Assert.assertTrue(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertEquals(1, as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsNormalSomeStart() {
        BehaviorProcessor<Object> as = BehaviorProcessor.createDefault(((Object) (1)));
        Assert.assertTrue(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertEquals(1, as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onNext(2);
        Assert.assertTrue(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertEquals(2, as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsEmpty() {
        BehaviorProcessor<Object> as = BehaviorProcessor.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsError() {
        BehaviorProcessor<Object> as = BehaviorProcessor.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onError(new TestException());
        Assert.assertFalse(as.hasValue());
        Assert.assertTrue(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertTrue(((as.getThrowable()) instanceof TestException));
    }

    @Test
    public void cancelOnArrival() {
        BehaviorProcessor<Object> p = BehaviorProcessor.create();
        Assert.assertFalse(p.hasSubscribers());
        p.test(0L, true).assertEmpty();
        Assert.assertFalse(p.hasSubscribers());
    }

    @Test
    public void onSubscribe() {
        BehaviorProcessor<Object> p = BehaviorProcessor.create();
        BooleanSubscription bs = new BooleanSubscription();
        p.onSubscribe(bs);
        Assert.assertFalse(bs.isCancelled());
        p.onComplete();
        bs = new BooleanSubscription();
        p.onSubscribe(bs);
        Assert.assertTrue(bs.isCancelled());
    }

    @Test
    public void onErrorAfterComplete() {
        BehaviorProcessor<Object> p = BehaviorProcessor.create();
        p.onComplete();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            p.onError(new TestException());
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelOnArrival2() {
        BehaviorProcessor<Object> p = BehaviorProcessor.create();
        TestSubscriber<Object> ts = p.test();
        p.test(0L, true).assertEmpty();
        p.onNext(1);
        p.onComplete();
        ts.assertResult(1);
    }

    @Test
    public void addRemoveRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorProcessor<Object> p = BehaviorProcessor.create();
            final TestSubscriber<Object> ts = p.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    p.test();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void subscribeOnNextRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorProcessor<Object> p = BehaviorProcessor.createDefault(((Object) (1)));
            final TestSubscriber[] ts = new io.reactivex.TestSubscriber[]{ null };
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts[0] = p.test();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onNext(2);
                }
            };
            TestHelper.race(r1, r2);
            if ((ts[0].valueCount()) == 1) {
                ts[0].assertValue(2).assertNoErrors().assertNotComplete();
            } else {
                ts[0].assertValues(1, 2).assertNoErrors().assertNotComplete();
            }
        }
    }

    @Test
    public void firstBackpressured() {
        BehaviorProcessor<Object> p = BehaviorProcessor.createDefault(((Object) (1)));
        p.test(0L, false).assertFailure(MissingBackpressureException.class);
        Assert.assertFalse(p.hasSubscribers());
    }

    @Test
    public void offer() {
        BehaviorProcessor<Integer> pp = BehaviorProcessor.create();
        TestSubscriber<Integer> ts = pp.test(0);
        Assert.assertFalse(pp.offer(1));
        ts.request(1);
        Assert.assertTrue(pp.offer(1));
        Assert.assertFalse(pp.offer(2));
        ts.cancel();
        Assert.assertTrue(pp.offer(2));
        ts = pp.test(1);
        Assert.assertTrue(pp.offer(null));
        ts.assertFailure(NullPointerException.class, 2);
        Assert.assertTrue(pp.hasThrowable());
        Assert.assertTrue(pp.getThrowable().toString(), ((pp.getThrowable()) instanceof NullPointerException));
    }

    @Test
    public void offerAsync() throws Exception {
        final BehaviorProcessor<Integer> pp = BehaviorProcessor.create();
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                while (!(pp.hasSubscribers())) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        return;
                    }
                } 
                for (int i = 1; i <= 10; i++) {
                    while (!(pp.offer(i))) {
                    } 
                }
                pp.onComplete();
            }
        });
        Thread.sleep(1);
        pp.test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void completeSubscribeRace() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorProcessor<Object> p = BehaviorProcessor.create();
            final TestSubscriber<Object> ts = new TestSubscriber<Object>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    p.subscribe(ts);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onComplete();
                }
            };
            TestHelper.race(r1, r2);
            ts.assertResult();
        }
    }

    @Test
    public void errorSubscribeRace() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorProcessor<Object> p = BehaviorProcessor.create();
            final TestSubscriber<Object> ts = new TestSubscriber<Object>();
            final TestException ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    p.subscribe(ts);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onError(ex);
                }
            };
            TestHelper.race(r1, r2);
            ts.assertFailure(TestException.class);
        }
    }

    @Test(timeout = 10000)
    public void subscriberCancelOfferRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorProcessor<Integer> pp = BehaviorProcessor.create();
            final TestSubscriber<Integer> ts = pp.test(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        while (!(pp.offer(i))) {
                        } 
                    }
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2);
            if ((ts.valueCount()) > 0) {
                ts.assertValuesOnly(0);
            } else {
                ts.assertEmpty();
            }
        }
    }

    @Test
    public void behaviorDisposableDisposeState() {
        BehaviorProcessor<Integer> bp = BehaviorProcessor.create();
        bp.onNext(1);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        BehaviorSubscription<Integer> bs = new BehaviorSubscription<Integer>(ts, bp);
        ts.onSubscribe(bs);
        Assert.assertFalse(bs.cancelled);
        bs.cancel();
        Assert.assertTrue(bs.cancelled);
        bs.cancel();
        Assert.assertTrue(bs.cancelled);
        Assert.assertTrue(bs.test(2));
        bs.emitFirst();
        ts.assertEmpty();
        bs.emitNext(2, 0);
    }

    @Test
    public void emitFirstDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            BehaviorProcessor<Integer> bp = BehaviorProcessor.create();
            bp.onNext(1);
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final BehaviorSubscription<Integer> bs = new BehaviorSubscription<Integer>(ts, bp);
            ts.onSubscribe(bs);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    bs.emitFirst();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    bs.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void emitNextDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            BehaviorProcessor<Integer> bp = BehaviorProcessor.create();
            bp.onNext(1);
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final BehaviorSubscription<Integer> bs = new BehaviorSubscription<Integer>(ts, bp);
            ts.onSubscribe(bs);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    bs.emitNext(2, 0);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    bs.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void emittingEmitNext() {
        BehaviorProcessor<Integer> bp = BehaviorProcessor.create();
        bp.onNext(1);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final BehaviorSubscription<Integer> bs = new BehaviorSubscription<Integer>(ts, bp);
        ts.onSubscribe(bs);
        bs.emitting = true;
        bs.emitNext(2, 1);
        bs.emitNext(3, 2);
        Assert.assertNotNull(bs.queue);
    }

    @Test
    public void badRequest() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            BehaviorProcessor<Integer> bp = BehaviorProcessor.create();
            bp.onNext(1);
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final BehaviorSubscription<Integer> bs = new BehaviorSubscription<Integer>(ts, bp);
            ts.onSubscribe(bs);
            bs.request((-1));
            TestHelper.assertError(errors, 0, IllegalArgumentException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

