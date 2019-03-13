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
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


public class PublishSubjectTest extends SubjectTest<Integer> {
    @Test
    public void testCompleted() {
        PublishSubject<String> subject = PublishSubject.create();
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        subject.onComplete();
        Observer<String> anotherSubscriber = mockObserver();
        subject.subscribe(anotherSubscriber);
        subject.onNext("four");
        subject.onComplete();
        subject.onError(new Throwable());
        assertCompletedSubscriber(observer);
        // todo bug?            assertNeverSubscriber(anotherSubscriber);
    }

    @Test
    public void testCompletedStopsEmittingData() {
        PublishSubject<Object> channel = PublishSubject.create();
        Observer<Object> observerA = mockObserver();
        Observer<Object> observerB = mockObserver();
        Observer<Object> observerC = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(observerA);
        channel.subscribe(to);
        channel.subscribe(observerB);
        InOrder inOrderA = Mockito.inOrder(observerA);
        InOrder inOrderB = Mockito.inOrder(observerB);
        InOrder inOrderC = Mockito.inOrder(observerC);
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
    public void testError() {
        PublishSubject<String> subject = PublishSubject.create();
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        subject.onError(testException);
        Observer<String> anotherSubscriber = mockObserver();
        subject.subscribe(anotherSubscriber);
        subject.onNext("four");
        subject.onError(new Throwable());
        subject.onComplete();
        assertErrorSubscriber(observer);
        // todo bug?            assertNeverSubscriber(anotherSubscriber);
    }

    @Test
    public void testSubscribeMidSequence() {
        PublishSubject<String> subject = PublishSubject.create();
        Observer<String> observer = mockObserver();
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        assertObservedUntilTwo(observer);
        Observer<String> anotherSubscriber = mockObserver();
        subject.subscribe(anotherSubscriber);
        subject.onNext("three");
        subject.onComplete();
        assertCompletedSubscriber(observer);
        assertCompletedStartingWithThreeSubscriber(anotherSubscriber);
    }

    @Test
    public void testUnsubscribeFirstSubscriber() {
        PublishSubject<String> subject = PublishSubject.create();
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        subject.subscribe(to);
        subject.onNext("one");
        subject.onNext("two");
        to.dispose();
        assertObservedUntilTwo(observer);
        Observer<String> anotherSubscriber = mockObserver();
        subject.subscribe(anotherSubscriber);
        subject.onNext("three");
        subject.onComplete();
        assertObservedUntilTwo(observer);
        assertCompletedStartingWithThreeSubscriber(anotherSubscriber);
    }

    @Test
    public void testNestedSubscribe() {
        final PublishSubject<Integer> s = PublishSubject.create();
        final AtomicInteger countParent = new AtomicInteger();
        final AtomicInteger countChildren = new AtomicInteger();
        final AtomicInteger countTotal = new AtomicInteger();
        final ArrayList<String> list = new ArrayList<String>();
        s.flatMap(new Function<Integer, Observable<String>>() {
            @Override
            public io.reactivex.Observable<String> apply(final Integer v) {
                countParent.incrementAndGet();
                // then subscribe to subject again (it will not receive the previous value)
                return s.map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer v2) {
                        countChildren.incrementAndGet();
                        return (("Parent: " + v) + " Child: ") + v2;
                    }
                });
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                countTotal.incrementAndGet();
                list.add(v);
            }
        });
        for (int i = 0; i < 10; i++) {
            s.onNext(i);
        }
        s.onComplete();
        // System.out.println("countParent: " + countParent.get());
        // System.out.println("countChildren: " + countChildren.get());
        // System.out.println("countTotal: " + countTotal.get());
        // 9+8+7+6+5+4+3+2+1+0 == 45
        Assert.assertEquals(45, list.size());
    }

    /**
     * Should be able to unsubscribe all Subscribers, have it stop emitting, then subscribe new ones and it start emitting again.
     */
    @Test
    public void testReSubscribe() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        Observer<Integer> o1 = mockObserver();
        TestObserver<Integer> to = new TestObserver<Integer>(o1);
        ps.subscribe(to);
        // emit
        ps.onNext(1);
        // validate we got it
        InOrder inOrder1 = Mockito.inOrder(o1);
        inOrder1.verify(o1, Mockito.times(1)).onNext(1);
        inOrder1.verifyNoMoreInteractions();
        // unsubscribe
        to.dispose();
        // emit again but nothing will be there to receive it
        ps.onNext(2);
        Observer<Integer> o2 = mockObserver();
        TestObserver<Integer> to2 = new TestObserver<Integer>(o2);
        ps.subscribe(to2);
        // emit
        ps.onNext(3);
        // validate we got it
        InOrder inOrder2 = Mockito.inOrder(o2);
        inOrder2.verify(o2, Mockito.times(1)).onNext(3);
        inOrder2.verifyNoMoreInteractions();
        to2.dispose();
    }

    private final Throwable testException = new Throwable();

    @Test(timeout = 1000)
    public void testUnsubscriptionCase() {
        PublishSubject<String> src = PublishSubject.create();
        for (int i = 0; i < 10; i++) {
            final Observer<Object> o = mockObserver();
            InOrder inOrder = Mockito.inOrder(o);
            String v = "" + i;
            System.out.printf("Turn: %d%n", i);
            src.firstElement().toObservable().flatMap(new Function<String, Observable<String>>() {
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
            src.onNext(v);
            inOrder.verify(o).onNext(((v + ", ") + v));
            inOrder.verify(o).onComplete();
            Mockito.verify(o, Mockito.never()).onError(any(Throwable.class));
        }
    }

    // FIXME RS subscribers are not allowed to throw
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery() {
    // PublishSubject<String> ps = PublishSubject.create();
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
    // assertEquals(1, to.getOnErrorEvents().size());
    // }
    // FIXME RS subscribers are not allowed to throw
    // /**
    // * This one has multiple failures so should get a CompositeException
    // */
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery2() {
    // PublishSubject<String> ps = PublishSubject.create();
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
        PublishSubject<Object> as = PublishSubject.create();
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
        PublishSubject<Object> as = PublishSubject.create();
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
        PublishSubject<Object> as = PublishSubject.create();
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getThrowable());
        as.onError(new TestException());
        Assert.assertTrue(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertTrue(((as.getThrowable()) instanceof TestException));
    }

    @Test
    public void crossCancel() {
        final TestObserver<Integer> to1 = new TestObserver<Integer>();
        TestObserver<Integer> to2 = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                to1.cancel();
            }
        };
        PublishSubject<Integer> ps = PublishSubject.create();
        ps.subscribe(to2);
        ps.subscribe(to1);
        ps.onNext(1);
        to2.assertValue(1);
        to1.assertNoValues();
    }

    @Test
    public void crossCancelOnError() {
        final TestObserver<Integer> to1 = new TestObserver<Integer>();
        TestObserver<Integer> to2 = new TestObserver<Integer>() {
            @Override
            public void onError(Throwable t) {
                super.onError(t);
                to1.cancel();
            }
        };
        PublishSubject<Integer> ps = PublishSubject.create();
        ps.subscribe(to2);
        ps.subscribe(to1);
        ps.onError(new TestException());
        to2.assertError(TestException.class);
        to1.assertNoErrors();
    }

    @Test
    public void crossCancelOnComplete() {
        final TestObserver<Integer> to1 = new TestObserver<Integer>();
        TestObserver<Integer> to2 = new TestObserver<Integer>() {
            @Override
            public void onComplete() {
                super.onComplete();
                to1.cancel();
            }
        };
        PublishSubject<Integer> ps = PublishSubject.create();
        ps.subscribe(to2);
        ps.subscribe(to1);
        ps.onComplete();
        to2.assertComplete();
        to1.assertNotComplete();
    }

    @Test
    public void onSubscribeCancelsImmediately() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.test();
        ps.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                d.dispose();
                d.dispose();
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        to.cancel();
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void terminateRace() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            TestObserver<Integer> to = ps.test();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            TestHelper.race(task, task);
            to.awaitDone(5, TimeUnit.SECONDS).assertResult();
        }
    }

    @Test
    public void addRemoveRance() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Integer> to = ps.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.subscribe();
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
    public void addTerminateRance() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.subscribe();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void addCompleteRance() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Integer> to = new TestObserver<Integer>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.subscribe(to);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            TestHelper.race(r1, r2);
            to.awaitDone(5, TimeUnit.SECONDS).assertResult();
        }
    }

    @Test
    public void subscribeToAfterComplete() {
        PublishSubject<Integer> ps = PublishSubject.create();
        ps.onComplete();
        PublishSubject<Integer> ps2 = PublishSubject.create();
        ps2.subscribe(ps);
        Assert.assertFalse(ps2.hasObservers());
    }

    @Test
    public void subscribedTo() {
        PublishSubject<Integer> ps = PublishSubject.create();
        PublishSubject<Integer> ps2 = PublishSubject.create();
        ps.subscribe(ps2);
        TestObserver<Integer> to = ps2.test();
        ps.onNext(1);
        ps.onNext(2);
        ps.onComplete();
        to.assertResult(1, 2);
    }
}

