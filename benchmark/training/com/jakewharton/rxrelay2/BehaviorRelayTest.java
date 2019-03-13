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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class BehaviorRelayTest {
    @Test
    public void testThatSubscriberReceivesDefaultValueAndSubsequentEvents() {
        BehaviorRelay<String> subject = BehaviorRelay.createDefault("default");
        Observer<String> observer = TestHelper.mockObserver();
        subject.subscribe(observer);
        subject.accept("one");
        subject.accept("two");
        subject.accept("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testThatSubscriberReceivesLatestAndThenSubsequentEvents() {
        BehaviorRelay<String> subject = BehaviorRelay.createDefault("default");
        subject.accept("one");
        Observer<String> observer = TestHelper.mockObserver();
        subject.subscribe(observer);
        subject.accept("two");
        subject.accept("three");
        Mockito.verify(observer, Mockito.never()).onNext("default");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test(timeout = 1000)
    public void testUnsubscriptionCase() {
        BehaviorRelay<String> src = BehaviorRelay.createDefault("null");// FIXME was plain null which is not allowed

        for (int i = 0; i < 10; i++) {
            final Observer<Object> o = TestHelper.mockObserver();
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
    public void testTakeOneSubscriber() {
        BehaviorRelay<Integer> source = BehaviorRelay.createDefault(1);
        final Observer<Object> o = TestHelper.mockObserver();
        source.take(1).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Assert.assertEquals(0, source.subscriberCount());
        Assert.assertFalse(source.hasObservers());
    }

    @Test
    public void testCurrentStateMethodsNormalEmptyStart() {
        BehaviorRelay<Object> as = BehaviorRelay.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertNull(as.getValue());
        as.accept(1);
        Assert.assertTrue(as.hasValue());
        Assert.assertEquals(1, as.getValue());
    }

    @Test
    public void testCurrentStateMethodsNormalSomeStart() {
        BehaviorRelay<Object> as = BehaviorRelay.createDefault(((Object) (1)));
        Assert.assertTrue(as.hasValue());
        Assert.assertEquals(1, as.getValue());
        as.accept(2);
        Assert.assertTrue(as.hasValue());
        Assert.assertEquals(2, as.getValue());
    }

    @Test
    public void onNextNull() {
        final BehaviorRelay<Object> s = BehaviorRelay.create();
        try {
            s.accept(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("value == null", e.getMessage());
        }
    }

    @Test
    public void cancelOnArrival() {
        BehaviorRelay<Object> p = BehaviorRelay.create();
        Assert.assertFalse(p.hasObservers());
        p.test(true).assertEmpty();
        Assert.assertFalse(p.hasObservers());
    }

    @Test
    public void addRemoveRace() {
        for (int i = 0; i < 500; i++) {
            final BehaviorRelay<Object> p = BehaviorRelay.create();
            final TestObserver<Object> ts = p.test();
            Runnable r1 = new Runnable() {
                @Override
                @SuppressWarnings("CheckReturnValue")
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
            TestHelper.race(r1, r2, Schedulers.single());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void subscribeOnNextRace() {
        for (int i = 0; i < 500; i++) {
            final BehaviorRelay<Object> p = BehaviorRelay.createDefault(((Object) (1)));
            final TestObserver[] ts = new TestObserver[]{ null };
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts[0] = p.test();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.accept(2);
                }
            };
            TestHelper.race(r1, r2, Schedulers.single());
            if ((ts[0].valueCount()) == 1) {
                ts[0].assertValue(2).assertNoErrors().assertNotComplete();
            } else {
                ts[0].assertValues(1, 2).assertNoErrors().assertNotComplete();
            }
        }
    }

    @Test
    public void innerDisposed() {
        BehaviorRelay.create().subscribe(new Observer<Object>() {
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
}

