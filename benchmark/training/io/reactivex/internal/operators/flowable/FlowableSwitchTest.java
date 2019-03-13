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
import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.TestScheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static java.util.Arrays.asList;


public class FlowableSwitchTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    private Subscriber<String> subscriber;

    @Test
    public void testSwitchWhenOuterCompleteBeforeInner() {
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 50, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 70, "one");
                        publishNext(subscriber, 100, "two");
                        publishCompleted(subscriber, 200);
                    }
                }));
                publishCompleted(subscriber, 60);
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(350, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(2)).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testSwitchWhenInnerCompleteBeforeOuter() {
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 10, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 0, "one");
                        publishNext(subscriber, 10, "two");
                        publishCompleted(subscriber, 20);
                    }
                }));
                publishNext(subscriber, 100, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 0, "three");
                        publishNext(subscriber, 10, "four");
                        publishCompleted(subscriber, 20);
                    }
                }));
                publishCompleted(subscriber, 200);
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
        onComplete();
        inOrder.verify(subscriber, Mockito.times(1)).onNext("one");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("two");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("three");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("four");
        scheduler.advanceTimeTo(250, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testSwitchWithComplete() {
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 50, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(final Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 60, "one");
                        publishNext(subscriber, 100, "two");
                    }
                }));
                publishNext(subscriber, 200, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(final Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 0, "three");
                        publishNext(subscriber, 100, "four");
                    }
                }));
                publishCompleted(subscriber, 250);
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(175, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("two");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(225, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("three");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(350, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("four");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSwitchWithError() {
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 50, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(final Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 50, "one");
                        publishNext(subscriber, 100, "two");
                    }
                }));
                publishNext(subscriber, 200, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 0, "three");
                        publishNext(subscriber, 100, "four");
                    }
                }));
                publishError(subscriber, 250, new TestException());
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(175, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("two");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(225, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("three");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(350, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testSwitchWithSubsequenceComplete() {
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 50, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 50, "one");
                        publishNext(subscriber, 100, "two");
                    }
                }));
                publishNext(subscriber, 130, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishCompleted(subscriber, 0);
                    }
                }));
                publishNext(subscriber, 150, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 50, "three");
                    }
                }));
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(250, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("three");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSwitchWithSubsequenceError() {
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 50, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 50, "one");
                        publishNext(subscriber, 100, "two");
                    }
                }));
                publishNext(subscriber, 130, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishError(subscriber, 0, new TestException());
                    }
                }));
                publishNext(subscriber, 150, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 50, "three");
                    }
                }));
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(90, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(125, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("one");
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(250, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext("three");
        onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testSwitchIssue737() {
        // https://github.com/ReactiveX/RxJava/issues/737
        Flowable<Flowable<String>> source = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 0, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 10, "1-one");
                        publishNext(subscriber, 20, "1-two");
                        // The following events will be ignored
                        publishNext(subscriber, 30, "1-three");
                        publishCompleted(subscriber, 40);
                    }
                }));
                publishNext(subscriber, 25, Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        publishNext(subscriber, 10, "2-one");
                        publishNext(subscriber, 20, "2-two");
                        publishNext(subscriber, 30, "2-three");
                        publishCompleted(subscriber, 40);
                    }
                }));
                publishCompleted(subscriber, 30);
            }
        });
        Flowable<String> sampled = Flowable.switchOnNext(source);
        sampled.subscribe(subscriber);
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("1-one");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("1-two");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("2-one");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("2-two");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("2-three");
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBackpressure() {
        PublishProcessor<String> o1 = PublishProcessor.create();
        PublishProcessor<String> o2 = PublishProcessor.create();
        PublishProcessor<String> o3 = PublishProcessor.create();
        PublishProcessor<PublishProcessor<String>> o = PublishProcessor.create();
        publishNext(o, 0, o1);
        publishNext(o, 5, o2);
        publishNext(o, 10, o3);
        publishCompleted(o, 15);
        for (int i = 0; i < 10; i++) {
            publishNext(o1, (i * 5), ("a" + (i + 1)));
            publishNext(o2, (5 + (i * 5)), ("b" + (i + 1)));
            publishNext(o3, (10 + (i * 5)), ("c" + (i + 1)));
        }
        publishCompleted(o1, 45);
        publishCompleted(o2, 50);
        publishCompleted(o3, 55);
        final TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        Flowable.switchOnNext(o).subscribe(new DefaultSubscriber<String>() {
            private int requested;

            @Override
            public void onStart() {
                requested = 3;
                request(3);
                testSubscriber.onSubscribe(new BooleanSubscription());
            }

            @Override
            public void onComplete() {
                testSubscriber.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                testSubscriber.onError(e);
            }

            @Override
            public void onNext(String s) {
                testSubscriber.onNext(s);
                (requested)--;
                if ((requested) == 0) {
                    requested = 3;
                    request(3);
                }
            }
        });
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testSubscriber.assertValues("a1", "b1", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10");
        testSubscriber.assertNoErrors();
        testSubscriber.assertTerminated();
    }

    @Test
    public void testUnsubscribe() {
        final AtomicBoolean isUnsubscribed = new AtomicBoolean();
        Flowable.switchOnNext(Flowable.unsafeCreate(new Publisher<Flowable<Integer>>() {
            @Override
            public void subscribe(final Subscriber<? super Flowable<Integer>> subscriber) {
                BooleanSubscription bs = new BooleanSubscription();
                subscriber.onSubscribe(bs);
                subscriber.onNext(Flowable.just(1));
                isUnsubscribed.set(bs.isCancelled());
            }
        })).take(1).subscribe();
        Assert.assertTrue("Switch doesn't propagate 'unsubscribe'", isUnsubscribed.get());
    }

    /**
     * The upstream producer hijacked the switch producer stopping the requests aimed at the inner observables.
     */
    @Test
    public void testIssue2654() {
        Flowable<String> oneItem = Flowable.just("Hello").mergeWith(Flowable.<String>never());
        Flowable<String> src = oneItem.switchMap(new Function<String, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final String s) {
                return Flowable.just(s).mergeWith(Flowable.interval(10, TimeUnit.MILLISECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long i) {
                        return (s + " ") + i;
                    }
                })).take(250);
            }
        }).share();
        TestSubscriber<String> ts = new TestSubscriber<String>() {
            @Override
            public void onNext(String t) {
                super.onNext(t);
                if ((valueCount()) == 250) {
                    onComplete();
                    dispose();
                }
            }
        };
        src.subscribe(ts);
        ts.awaitTerminalEvent(10, TimeUnit.SECONDS);
        System.out.println(("> testIssue2654: " + (ts.valueCount())));
        ts.assertTerminated();
        ts.assertNoErrors();
        Assert.assertEquals(250, ts.valueCount());
    }

    @Test(timeout = 10000)
    public void testInitialRequestsAreAdditive() {
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        Flowable.switchOnNext(Flowable.interval(100, TimeUnit.MILLISECONDS).map(new Function<Long, Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> apply(Long t) {
                return Flowable.just(1L, 2L, 3L);
            }
        }).take(3)).subscribe(ts);
        ts.request(((Long.MAX_VALUE) - 100));
        ts.request(1);
        ts.awaitTerminalEvent();
    }

    @Test(timeout = 10000)
    public void testInitialRequestsDontOverflow() {
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        Flowable.switchOnNext(Flowable.interval(100, TimeUnit.MILLISECONDS).map(new Function<Long, Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> apply(Long t) {
                return Flowable.fromIterable(asList(1L, 2L, 3L)).hide();
            }
        }).take(3)).subscribe(ts);
        ts.request(((Long.MAX_VALUE) - 1));
        ts.request(2);
        ts.awaitTerminalEvent();
        Assert.assertTrue(((ts.valueCount()) > 0));
    }

    @Test(timeout = 10000)
    public void testSecondaryRequestsDontOverflow() throws InterruptedException {
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        Flowable.switchOnNext(Flowable.interval(100, TimeUnit.MILLISECONDS).map(new Function<Long, Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> apply(Long t) {
                return Flowable.fromIterable(asList(1L, 2L, 3L)).hide();
            }
        }).take(3)).subscribe(ts);
        ts.request(1);
        // we will miss two of the first observable
        Thread.sleep(250);
        ts.request(((Long.MAX_VALUE) - 1));
        ts.request(((Long.MAX_VALUE) - 1));
        ts.awaitTerminalEvent();
        ts.assertValueCount(7);
    }

    @Test
    public void delayErrors() {
        PublishProcessor<Publisher<Integer>> source = PublishProcessor.create();
        TestSubscriber<Integer> ts = source.switchMapDelayError(Functions.<Publisher<Integer>>identity()).test();
        ts.assertNoValues().assertNoErrors().assertNotComplete();
        source.onNext(Flowable.just(1));
        source.onNext(Flowable.<Integer>error(new TestException("Forced failure 1")));
        source.onNext(Flowable.just(2, 3, 4));
        source.onNext(Flowable.<Integer>error(new TestException("Forced failure 2")));
        source.onNext(Flowable.just(5));
        source.onError(new TestException("Forced failure 3"));
        ts.assertValues(1, 2, 3, 4, 5).assertNotComplete().assertError(CompositeException.class);
        List<Throwable> errors = ExceptionHelper.flatten(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Forced failure 1");
        TestHelper.assertError(errors, 1, TestException.class, "Forced failure 2");
        TestHelper.assertError(errors, 2, TestException.class, "Forced failure 3");
    }

    @Test
    public void switchOnNextPrefetch() {
        final List<Integer> list = new ArrayList<Integer>();
        Flowable<Integer> source = Flowable.range(1, 10).hide().doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        });
        Flowable.switchOnNext(Flowable.just(source).hide(), 2).test(1);
        Assert.assertEquals(asList(1, 2, 3), list);
    }

    @Test
    public void switchOnNextDelayError() {
        final List<Integer> list = new ArrayList<Integer>();
        Flowable<Integer> source = Flowable.range(1, 10).hide().doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        });
        Flowable.switchOnNextDelayError(Flowable.just(source).hide()).test(1);
        Assert.assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), list);
    }

    @Test
    public void switchOnNextDelayErrorPrefetch() {
        final List<Integer> list = new ArrayList<Integer>();
        Flowable<Integer> source = Flowable.range(1, 10).hide().doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        });
        Flowable.switchOnNextDelayError(Flowable.just(source).hide(), 2).test(1);
        Assert.assertEquals(asList(1, 2, 3), list);
    }

    @Test
    public void switchOnNextDelayErrorWithError() {
        PublishProcessor<Flowable<Integer>> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.switchOnNextDelayError(pp).test();
        pp.onNext(Flowable.just(1));
        pp.onNext(Flowable.<Integer>error(new TestException()));
        pp.onNext(Flowable.range(2, 4));
        pp.onComplete();
        ts.assertFailure(TestException.class, 1, 2, 3, 4, 5);
    }

    @Test
    public void switchOnNextDelayErrorBufferSize() {
        PublishProcessor<Flowable<Integer>> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.switchOnNextDelayError(pp, 2).test();
        pp.onNext(Flowable.just(1));
        pp.onNext(Flowable.range(2, 4));
        pp.onComplete();
        ts.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void switchMapDelayErrorEmptySource() {
        Assert.assertSame(Flowable.empty(), Flowable.<Object>empty().switchMapDelayError(new Function<Object, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Object v) throws Exception {
                return Flowable.just(1);
            }
        }, 16));
    }

    @Test
    public void switchMapDelayErrorJustSource() {
        Flowable.just(0).switchMapDelayError(new Function<Object, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Object v) throws Exception {
                return Flowable.just(1);
            }
        }, 16).test().assertResult(1);
    }

    @Test
    public void switchMapErrorEmptySource() {
        Assert.assertSame(Flowable.empty(), Flowable.<Object>empty().switchMap(new Function<Object, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Object v) throws Exception {
                return Flowable.just(1);
            }
        }, 16));
    }

    @Test
    public void switchMapJustSource() {
        Flowable.just(0).switchMap(new Function<Object, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Object v) throws Exception {
                return Flowable.just(1);
            }
        }, 16).test().assertResult(1);
    }

    @Test
    public void switchMapInnerCancelled() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.just(1).switchMap(Functions.justFunction(pp)).test();
        Assert.assertTrue(pp.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.switchOnNext(Flowable.just(Flowable.just(1)).hide()));
    }

    @Test
    public void nextSourceErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Integer> pp1 = PublishProcessor.create();
                final PublishProcessor<Integer> pp2 = PublishProcessor.create();
                pp1.switchMap(new Function<Integer, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                        if (v == 1) {
                            return pp2;
                        }
                        return Flowable.never();
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp1.onNext(2);
                    }
                };
                final TestException ex = new TestException();
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        pp2.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                for (Throwable e : errors) {
                    Assert.assertTrue(e.toString(), (e instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void outerInnerErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Integer> pp1 = PublishProcessor.create();
                final PublishProcessor<Integer> pp2 = PublishProcessor.create();
                pp1.switchMap(new Function<Integer, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                        if (v == 1) {
                            return pp2;
                        }
                        return Flowable.never();
                    }
                }).test();
                final TestException ex1 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp1.onError(ex1);
                    }
                };
                final TestException ex2 = new TestException();
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        pp2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                for (Throwable e : errors) {
                    Assert.assertTrue(e.toString(), (e instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void nextCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp1 = PublishProcessor.create();
            final TestSubscriber<Integer> ts = pp1.switchMap(new Function<Integer, Flowable<Integer>>() {
                @Override
                public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                    return Flowable.never();
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp1.onNext(2);
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

    @Test
    public void mapperThrows() {
        Flowable.just(1).hide().switchMap(new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void badMainSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onComplete();
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                }
            }.switchMap(Functions.justFunction(Flowable.never())).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void emptyInner() {
        Flowable.range(1, 5).switchMap(Functions.justFunction(Flowable.empty())).test().assertResult();
    }

    @Test
    public void justInner() {
        Flowable.range(1, 5).switchMap(Functions.justFunction(Flowable.just(1))).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void badInnerSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.just(1).hide().switchMap(Functions.justFunction(new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                }
            })).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void innerCompletesReentrant() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                pp.onComplete();
            }
        };
        Flowable.just(1).hide().switchMap(Functions.justFunction(pp)).subscribe(ts);
        pp.onNext(1);
        ts.assertResult(1);
    }

    @Test
    public void innerErrorsReentrant() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                pp.onError(new TestException());
            }
        };
        Flowable.just(1).hide().switchMap(Functions.justFunction(pp)).subscribe(ts);
        pp.onNext(1);
        ts.assertFailure(TestException.class, 1);
    }

    @Test
    public void scalarMap() {
        Flowable.switchOnNext(Flowable.just(Flowable.just(1))).test().assertResult(1);
    }

    @Test
    public void scalarMapDelayError() {
        Flowable.switchOnNextDelayError(Flowable.just(Flowable.just(1))).test().assertResult(1);
    }

    @Test
    public void scalarXMap() {
        Flowable.fromCallable(Functions.justCallable(1)).switchMap(Functions.justFunction(Flowable.just(1))).test().assertResult(1);
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceFlowable(new Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.switchMap(Functions.justFunction(Flowable.just(1)));
            }
        }, false, 1, 1, 1);
    }

    @Test
    public void innerOverflow() {
        Flowable.just(1).hide().switchMap(Functions.justFunction(new Flowable<Integer>() {
            @Override
            protected void subscribeActual(Subscriber<? super Integer> s) {
                s.onSubscribe(new BooleanSubscription());
                for (int i = 0; i < 10; i++) {
                    s.onNext(i);
                }
            }
        }), 8).test(1L).assertFailure(MissingBackpressureException.class, 0);
    }

    @Test
    public void drainCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            Flowable.just(1).hide().switchMap(Functions.justFunction(pp)).subscribe(ts);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    pp.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void fusedInnerCrash() {
        Flowable.just(1).hide().switchMap(Functions.justFunction(Flowable.just(1).map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(TestHelper.<Object>flowableStripBoundary()))).test().assertFailure(TestException.class);
    }

    @Test
    public void innerCancelledOnMainError() {
        final PublishProcessor<Integer> main = PublishProcessor.create();
        final PublishProcessor<Integer> inner = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.switchMap(Functions.justFunction(inner)).test();
        Assert.assertTrue(main.hasSubscribers());
        main.onNext(1);
        Assert.assertTrue(inner.hasSubscribers());
        main.onError(new TestException());
        Assert.assertFalse(inner.hasSubscribers());
        ts.assertFailure(TestException.class);
    }

    @Test
    public void fusedBoundary() {
        String thread = Thread.currentThread().getName();
        Flowable.range(1, 10000).switchMap(new Function<Integer, Flowable<? extends Object>>() {
            @Override
            public io.reactivex.Flowable<? extends Object> apply(Integer v) throws Exception {
                return Flowable.just(2).hide().observeOn(Schedulers.single()).map(new Function<Integer, Object>() {
                    @Override
                    public Object apply(Integer w) throws Exception {
                        return Thread.currentThread().getName();
                    }
                });
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertNever(thread).assertNoErrors().assertComplete();
    }
}

