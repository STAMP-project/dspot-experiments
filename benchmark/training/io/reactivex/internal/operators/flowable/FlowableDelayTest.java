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


import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.processors.PublishProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableDelayTest {
    private Subscriber<Long> subscriber;

    private Subscriber<Long> subscriber2;

    private TestScheduler scheduler;

    @Test
    public void testDelay() {
        Flowable<Long> source = Flowable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Flowable<Long> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(0L);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2400L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1L);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3400L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2L);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testLongDelay() {
        Flowable<Long> source = Flowable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Flowable<Long> delayed = source.delay(5L, TimeUnit.SECONDS, scheduler);
        delayed.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(5999L, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(6000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(0L);
        scheduler.advanceTimeTo(6999L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(7000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1L);
        scheduler.advanceTimeTo(7999L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(8000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2L);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithError() {
        Flowable<Long> source = Flowable.interval(1L, TimeUnit.SECONDS, scheduler).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long value) {
                if (value == 1L) {
                    throw new RuntimeException("error!");
                }
                return value;
            }
        });
        Flowable<Long> delayed = source.delay(1L, TimeUnit.SECONDS, scheduler);
        delayed.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(1999L, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        scheduler.advanceTimeTo(5000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithMultipleSubscriptions() {
        Flowable<Long> source = Flowable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Flowable<Long> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed.subscribe(subscriber);
        delayed.subscribe(subscriber2);
        InOrder inOrder = Mockito.inOrder(subscriber);
        InOrder inOrder2 = Mockito.inOrder(subscriber2);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(0L);
        inOrder2.verify(subscriber2, Mockito.times(1)).onNext(0L);
        scheduler.advanceTimeTo(2499L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder2.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1L);
        inOrder2.verify(subscriber2, Mockito.times(1)).onNext(1L);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber2, Mockito.never()).onComplete();
        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2L);
        inOrder2.verify(subscriber2, Mockito.times(1)).onNext(2L);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder2.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder2.verify(subscriber2, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelaySubscription() {
        Flowable<Integer> result = Flowable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(3);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelaySubscriptionCancelBeforeTime() {
        Flowable<Integer> result = Flowable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        result.subscribe(ts);
        ts.dispose();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithFlowableNormal1() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final List<PublishProcessor<Integer>> delays = new ArrayList<PublishProcessor<Integer>>();
        final int n = 10;
        for (int i = 0; i < n; i++) {
            PublishProcessor<Integer> delay = PublishProcessor.create();
            delays.add(delay);
        }
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delays.get(t1);
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delayFunc).subscribe(subscriber);
        for (int i = 0; i < n; i++) {
            source.onNext(i);
            delays.get(i).onNext(i);
            inOrder.verify(subscriber).onNext(i);
        }
        source.onComplete();
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithFlowableSingleSend1() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delayFunc).subscribe(subscriber);
        source.onNext(1);
        delay.onNext(1);
        delay.onNext(2);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithFlowableSourceThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delayFunc).subscribe(subscriber);
        source.onNext(1);
        source.onError(new TestException());
        delay.onNext(1);
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableDelayFunctionThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                throw new TestException();
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delayFunc).subscribe(subscriber);
        source.onNext(1);
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableDelayThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delayFunc).subscribe(subscriber);
        source.onNext(1);
        delay.onError(new TestException());
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableSubscriptionNormal() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delay, delayFunc).subscribe(subscriber);
        source.onNext(1);
        delay.onNext(1);
        source.onNext(2);
        delay.onNext(2);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableSubscriptionFunctionThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Callable<Flowable<Integer>> subFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                throw new TestException();
            }
        };
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(Flowable.defer(subFunc), delayFunc).subscribe(subscriber);
        source.onNext(1);
        delay.onNext(1);
        source.onNext(2);
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableSubscriptionThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Callable<Flowable<Integer>> subFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                return delay;
            }
        };
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(Flowable.defer(subFunc), delayFunc).subscribe(subscriber);
        source.onNext(1);
        delay.onError(new TestException());
        source.onNext(2);
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableEmptyDelayer() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return Flowable.empty();
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(delayFunc).subscribe(subscriber);
        source.onNext(1);
        source.onComplete();
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithFlowableSubscriptionRunCompletion() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> sdelay = PublishProcessor.create();
        final PublishProcessor<Integer> delay = PublishProcessor.create();
        Callable<Flowable<Integer>> subFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                return sdelay;
            }
        };
        Function<Integer, Flowable<Integer>> delayFunc = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.delay(Flowable.defer(subFunc), delayFunc).subscribe(subscriber);
        source.onNext(1);
        sdelay.onComplete();
        source.onNext(2);
        delay.onNext(2);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithFlowableAsTimed() {
        Flowable<Long> source = Flowable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        final Flowable<Long> delayer = Flowable.timer(500L, TimeUnit.MILLISECONDS, scheduler);
        Function<Long, Flowable<Long>> delayFunc = new Function<Long, Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> apply(Long t1) {
                return delayer;
            }
        };
        Flowable<Long> delayed = source.delay(delayFunc);
        delayed.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(0L);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2400L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1L);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3400L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2L);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithFlowableReorder() {
        int n = 3;
        PublishProcessor<Integer> source = PublishProcessor.create();
        final List<PublishProcessor<Integer>> subjects = new ArrayList<PublishProcessor<Integer>>();
        for (int i = 0; i < n; i++) {
            subjects.add(PublishProcessor.<Integer>create());
        }
        Flowable<Integer> result = source.delay(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return subjects.get(t1);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        for (int i = 0; i < n; i++) {
            source.onNext(i);
        }
        source.onComplete();
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        for (int i = n - 1; i >= 0; i--) {
            subjects.get(i).onComplete();
            inOrder.verify(subscriber).onNext(i);
        }
        inOrder.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayEmitsEverything() {
        Flowable<Integer> source = Flowable.range(1, 5);
        Flowable<Integer> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed = delayed.doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> t1) {
                System.out.println(t1);
            }
        });
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        delayed.subscribe(ts);
        // all will be delivered after 500ms since range does not delay between them
        scheduler.advanceTimeBy(500L, TimeUnit.MILLISECONDS);
        ts.assertValues(1, 2, 3, 4, 5);
    }

    @Test
    public void testBackpressureWithTimedDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, ((Flowable.bufferSize()) * 2)).delay(100, TimeUnit.MILLISECONDS).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), ts.valueCount());
    }

    @Test
    public void testBackpressureWithSubscriptionTimedDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, ((Flowable.bufferSize()) * 2)).delaySubscription(100, TimeUnit.MILLISECONDS).delay(100, TimeUnit.MILLISECONDS).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), ts.valueCount());
    }

    @Test
    public void testBackpressureWithSelectorDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, ((Flowable.bufferSize()) * 2)).delay(new Function<Integer, Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> apply(Integer i) {
                return Flowable.timer(100, TimeUnit.MILLISECONDS);
            }
        }).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), ts.valueCount());
    }

    @Test
    public void testBackpressureWithSelectorDelayAndSubscriptionDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, ((Flowable.bufferSize()) * 2)).delay(Flowable.defer(new Callable<Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> call() {
                return Flowable.timer(500, TimeUnit.MILLISECONDS);
            }
        }), new Function<Integer, Flowable<Long>>() {
            @Override
            public io.reactivex.Flowable<Long> apply(Integer i) {
                return Flowable.timer(100, TimeUnit.MILLISECONDS);
            }
        }).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), ts.valueCount());
    }

    @Test
    public void testErrorRunsBeforeOnNext() {
        TestScheduler test = new TestScheduler();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        pp.delay(1, TimeUnit.SECONDS, test).subscribe(ts);
        pp.onNext(1);
        test.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        pp.onError(new TestException());
        test.advanceTimeBy(1, TimeUnit.SECONDS);
        ts.assertNoValues();
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @Test
    public void testDelaySupplierSimple() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        Flowable<Integer> source = Flowable.range(1, 5);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.delaySubscription(Flowable.defer(new Callable<Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> call() {
                return pp;
            }
        })).subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertNotComplete();
        pp.onNext(1);
        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertComplete();
        ts.assertNoErrors();
    }

    @Test
    public void testDelaySupplierCompletes() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        Flowable<Integer> source = Flowable.range(1, 5);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.delaySubscription(Flowable.defer(new Callable<Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> call() {
                return pp;
            }
        })).subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertNotComplete();
        // FIXME should this complete the source instead of consuming it?
        pp.onComplete();
        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertComplete();
        ts.assertNoErrors();
    }

    @Test
    public void testDelaySupplierErrors() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        Flowable<Integer> source = Flowable.range(1, 5);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.delaySubscription(Flowable.defer(new Callable<Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> call() {
                return pp;
            }
        })).subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertNotComplete();
        pp.onError(new TestException());
        ts.assertNoValues();
        ts.assertNotComplete();
        ts.assertError(TestException.class);
    }

    @Test
    public void delayAndTakeUntilNeverSubscribeToSource() {
        PublishProcessor<Integer> delayUntil = PublishProcessor.create();
        PublishProcessor<Integer> interrupt = PublishProcessor.create();
        final AtomicBoolean subscribed = new AtomicBoolean(false);
        Flowable.just(1).doOnSubscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                subscribed.set(true);
            }
        }).delaySubscription(delayUntil).takeUntil(interrupt).subscribe();
        interrupt.onNext(9000);
        delayUntil.onNext(1);
        Assert.assertFalse(subscribed.get());
    }

    @Test
    public void delayWithTimeDelayError() throws Exception {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).delay(100, TimeUnit.MILLISECONDS, true).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class, 1);
    }

    @Test
    public void testDelaySubscriptionDisposeBeforeTime() {
        Flowable<Integer> result = Flowable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        result.subscribe(ts);
        ts.dispose();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testOnErrorCalledOnScheduler() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Thread> thread = new AtomicReference<Thread>();
        Flowable.<String>error(new Exception()).delay(0, TimeUnit.MILLISECONDS, Schedulers.newThread()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                thread.set(Thread.currentThread());
                latch.countDown();
            }
        }).onErrorResumeNext(Flowable.<String>empty()).subscribe();
        latch.await();
        Assert.assertNotEquals(Thread.currentThread(), thread.get());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().delay(1, TimeUnit.SECONDS));
        TestHelper.checkDisposed(PublishProcessor.create().delay(Functions.justFunction(Flowable.never())));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.delay(1, TimeUnit.SECONDS);
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.delay(Functions.justFunction(Flowable.never()));
            }
        });
    }

    @Test
    public void onCompleteFinal() {
        TestScheduler scheduler = new TestScheduler();
        Flowable.empty().delay(1, TimeUnit.MILLISECONDS, scheduler).subscribe(new DisposableSubscriber<Object>() {
            @Override
            public void onNext(Object value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                throw new TestException();
            }
        });
        try {
            scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown");
        } catch (TestException ex) {
            // expected
        }
    }

    @Test
    public void onErrorFinal() {
        TestScheduler scheduler = new TestScheduler();
        Flowable.error(new TestException()).delay(1, TimeUnit.MILLISECONDS, scheduler).subscribe(new DisposableSubscriber<Object>() {
            @Override
            public void onNext(Object value) {
            }

            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }

            @Override
            public void onComplete() {
            }
        });
        try {
            scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown");
        } catch (TestException ex) {
            // expected
        }
    }

    @Test
    public void itemDelayReturnsNull() {
        Flowable.just(1).delay(new Function<Integer, Publisher<Object>>() {
            @Override
            public io.reactivex.Publisher<Object> apply(Integer t) throws Exception {
                return null;
            }
        }).test().assertFailureAndMessage(NullPointerException.class, "The itemDelay returned a null Publisher");
    }
}

