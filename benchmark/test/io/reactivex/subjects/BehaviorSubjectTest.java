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


import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject.BehaviorDisposable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class BehaviorSubjectTest extends SubjectTest<Integer> {
    private final Throwable testException = new Throwable();

    @Test
    public void testThatSubscriberReceivesDefaultValueAndSubsequentEvents() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(testException);
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testThatSubscriberReceivesLatestAndThenSubsequentEvents() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        subject.onNext("one");
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("two");
        subject.onNext("three");
        Mockito.verify(observer, Mockito.never()).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(testException);
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testSubscribeThenOnComplete() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSubscribeToCompletedOnlyEmitsOnComplete() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        subject.onNext("one");
        subject.onComplete();
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext("default");
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSubscribeToErrorOnlyEmitsOnError() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        subject.onNext("one");
        RuntimeException re = new RuntimeException("test error");
        subject.onError(re);
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext("default");
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onError(re);
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testCompletedStopsEmittingData() {
        BehaviorSubject<Integer> channel = BehaviorSubject.createDefault(2013);
        Observer<Object> observerA = mockObserver();
        Observer<Object> observerB = mockObserver();
        Observer<Object> observerC = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(observerA);
        channel.subscribe(to);
        channel.subscribe(observerB);
        InOrder inOrderA = Mockito.inOrder(observerA);
        InOrder inOrderB = Mockito.inOrder(observerB);
        InOrder inOrderC = Mockito.inOrder(observerC);
        inOrderA.verify(observerA).onNext(2013);
        inOrderB.verify(observerB).onNext(2013);
        channel.onNext(42);
        inOrderA.verify(observerA).onNext(42);
        inOrderB.verify(observerB).onNext(42);
        to.dispose();
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
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onError(testException);
        subject.onNext("two");
        subject.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onError(testException);
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testCompletedAfterErrorIsNotSent2() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onError(testException);
        subject.onNext("two");
        subject.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onError(testException);
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onComplete();
        Observer<Object> o2 = mockObserver();
        subject.subscribe(o2);
        Mockito.verify(o2, Mockito.times(1)).onError(testException);
        Mockito.verify(o2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o2, Mockito.never()).onComplete();
    }

    @Test
    public void testCompletedAfterErrorIsNotSent3() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("default");
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onComplete();
        subject.onNext("two");
        subject.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Observer<Object> o2 = mockObserver();
        subject.subscribe(o2);
        Mockito.verify(o2, Mockito.times(1)).onComplete();
        Mockito.verify(o2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 1000)
    public void testUnsubscriptionCase() {
        BehaviorSubject<String> src = BehaviorSubject.createDefault("null");// FIXME was plain null which is not allowed

        for (int i = 0; i < 10; i++) {
            final Observer<Object> o = mockObserver();
            InOrder inOrder = Mockito.inOrder(o);
            String v = "" + i;
            src.onNext(v);
            System.out.printf("Turn: %d%n", i);
            src.firstElement().toObservable().flatMap(new io.reactivex.functions.Function<String, Observable<String>>() {
                @Override
                public io.reactivex.Observable<String> apply(String t1) {
                    return Observable.just(((t1 + ", ") + t1));
                }
            }).subscribe(new DefaultObserver<String>() {
                @Override
                public void onNext(String t) {
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
            inOrder.verify(o).onNext(((v + ", ") + v));
            inOrder.verify(o).onComplete();
            Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testStartEmpty() {
        BehaviorSubject<Integer> source = BehaviorSubject.create();
        final Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.subscribe(o);
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder.verify(o, Mockito.never()).onComplete();
        source.onNext(1);
        source.onComplete();
        source.onNext(2);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testStartEmptyThenAddOne() {
        BehaviorSubject<Integer> source = BehaviorSubject.create();
        final Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.onNext(1);
        source.subscribe(o);
        inOrder.verify(o).onNext(1);
        source.onComplete();
        source.onNext(2);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testStartEmptyCompleteWithOne() {
        BehaviorSubject<Integer> source = BehaviorSubject.create();
        final Observer<Object> o = mockObserver();
        source.onNext(1);
        source.onComplete();
        source.onNext(2);
        source.subscribe(o);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void testTakeOneSubscriber() {
        BehaviorSubject<Integer> source = BehaviorSubject.createDefault(1);
        final Observer<Object> o = mockObserver();
        source.take(1).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Assert.assertEquals(0, source.subscriberCount());
        Assert.assertFalse(source.hasObservers());
    }

    // FIXME RS subscribers are not allowed to throw
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery() {
    // BehaviorSubject<String> ps = BehaviorSubject.create();
    // 
    // ps.subscribe();
    // TestObserver<String> to = new TestObserver<T>();
    // ps.subscribe(to);
    // 
    // try {
    // ps.onError(new RuntimeException("an exception"));
    // fail("expect OnErrorNotImplementedException");
    // } catch (OnErrorNotImplementedException e) {
    // // ignore
    // }
    // // even though the onError above throws we should still receive it on the other subscriber
    // assertEquals(1, to.getOnErrorEvents().size());
    // }
    // FIXME RS subscribers are not allowed to throw
    // /**
    // * This one has multiple failures so should get a CompositeException
    // */
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery2() {
    // BehaviorSubject<String> ps = BehaviorSubject.create();
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
    public void testEmissionSubscriptionRace() throws Exception {
        Scheduler s = Schedulers.io();
        Scheduler.Worker worker = Schedulers.io().createWorker();
        try {
            for (int i = 0; i < 50000; i++) {
                if ((i % 1000) == 0) {
                    System.out.println(i);
                }
                final BehaviorSubject<Object> rs = BehaviorSubject.create();
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
                rs.subscribeOn(s).observeOn(Schedulers.io()).subscribe(new DefaultObserver<Object>() {
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
                    System.out.println(rs.hasObservers());
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
        BehaviorSubject<Object> as = BehaviorSubject.create();
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
        BehaviorSubject<Object> as = BehaviorSubject.createDefault(((Object) (1)));
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
        BehaviorSubject<Object> as = BehaviorSubject.create();
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
        BehaviorSubject<Object> as = BehaviorSubject.create();
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
        BehaviorSubject<Object> p = BehaviorSubject.create();
        Assert.assertFalse(p.hasObservers());
        p.test(true).assertEmpty();
        Assert.assertFalse(p.hasObservers());
    }

    @Test
    public void onSubscribe() {
        BehaviorSubject<Object> p = BehaviorSubject.create();
        Disposable bs = Disposables.empty();
        p.onSubscribe(bs);
        Assert.assertFalse(bs.isDisposed());
        p.onComplete();
        bs = Disposables.empty();
        p.onSubscribe(bs);
        Assert.assertTrue(bs.isDisposed());
    }

    @Test
    public void onErrorAfterComplete() {
        BehaviorSubject<Object> p = BehaviorSubject.create();
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
        BehaviorSubject<Object> p = BehaviorSubject.create();
        TestObserver<Object> to = p.test();
        p.test(true).assertEmpty();
        p.onNext(1);
        p.onComplete();
        to.assertResult(1);
    }

    @Test
    public void addRemoveRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorSubject<Object> p = BehaviorSubject.create();
            final TestObserver<Object> to = p.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    p.test();
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void subscribeOnNextRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorSubject<Object> p = BehaviorSubject.createDefault(((Object) (1)));
            final TestObserver[] to = new io.reactivex.TestObserver[]{ null };
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to[0] = p.test();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onNext(2);
                }
            };
            TestHelper.race(r1, r2);
            if ((to[0].valueCount()) == 1) {
                to[0].assertValue(2).assertNoErrors().assertNotComplete();
            } else {
                to[0].assertValues(1, 2).assertNoErrors().assertNotComplete();
            }
        }
    }

    @Test
    public void innerDisposed() {
        BehaviorSubject.create().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Assert.assertFalse(d.isDisposed());
                d.dispose();
                Assert.assertTrue(d.isDisposed());
            }

            @Override
            public void onNext(Object value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void completeSubscribeRace() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorSubject<Object> p = BehaviorSubject.create();
            final TestObserver<Object> to = new TestObserver<Object>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    p.subscribe(to);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onComplete();
                }
            };
            TestHelper.race(r1, r2);
            to.assertResult();
        }
    }

    @Test
    public void errorSubscribeRace() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final BehaviorSubject<Object> p = BehaviorSubject.create();
            final TestObserver<Object> to = new TestObserver<Object>();
            final TestException ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    p.subscribe(to);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onError(ex);
                }
            };
            TestHelper.race(r1, r2);
            to.assertFailure(TestException.class);
        }
    }

    @Test
    public void behaviorDisposableDisposeState() {
        BehaviorSubject<Integer> bs = BehaviorSubject.create();
        bs.onNext(1);
        TestObserver<Integer> to = new TestObserver<Integer>();
        BehaviorDisposable<Integer> bd = new BehaviorDisposable<Integer>(to, bs);
        to.onSubscribe(bd);
        Assert.assertFalse(bd.isDisposed());
        bd.dispose();
        Assert.assertTrue(bd.isDisposed());
        bd.dispose();
        Assert.assertTrue(bd.isDisposed());
        Assert.assertTrue(bd.test(2));
        bd.emitFirst();
        to.assertEmpty();
        bd.emitNext(2, 0);
    }

    @Test
    public void emitFirstDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            BehaviorSubject<Integer> bs = BehaviorSubject.create();
            bs.onNext(1);
            TestObserver<Integer> to = new TestObserver<Integer>();
            final BehaviorDisposable<Integer> bd = new BehaviorDisposable<Integer>(to, bs);
            to.onSubscribe(bd);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    bd.emitFirst();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    bd.dispose();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void emitNextDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            BehaviorSubject<Integer> bs = BehaviorSubject.create();
            bs.onNext(1);
            TestObserver<Integer> to = new TestObserver<Integer>();
            final BehaviorDisposable<Integer> bd = new BehaviorDisposable<Integer>(to, bs);
            to.onSubscribe(bd);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    bd.emitNext(2, 0);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    bd.dispose();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void emittingEmitNext() {
        BehaviorSubject<Integer> bs = BehaviorSubject.create();
        bs.onNext(1);
        TestObserver<Integer> to = new TestObserver<Integer>();
        final BehaviorDisposable<Integer> bd = new BehaviorDisposable<Integer>(to, bs);
        to.onSubscribe(bd);
        bd.emitting = true;
        bd.emitNext(2, 1);
        bd.emitNext(3, 2);
        Assert.assertNotNull(bd.queue);
    }
}

