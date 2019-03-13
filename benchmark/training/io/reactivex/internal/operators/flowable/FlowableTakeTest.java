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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class FlowableTakeTest {
    @Test
    public void testTake1() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Flowable<String> take = w.take(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTake2() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Flowable<String> take = w.take(1);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeWithError() {
        Flowable.fromIterable(Arrays.asList(1, 2, 3)).take(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                throw new IllegalArgumentException("some error");
            }
        }).blockingSingle();
    }

    @Test
    public void testTakeWithErrorHappeningInOnNext() {
        Flowable<Integer> w = Flowable.fromIterable(Arrays.asList(1, 2, 3)).take(2).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                throw new IllegalArgumentException("some error");
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        w.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(IllegalArgumentException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testTakeWithErrorHappeningInTheLastOnNext() {
        Flowable<Integer> w = Flowable.fromIterable(Arrays.asList(1, 2, 3)).take(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                throw new IllegalArgumentException("some error");
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        w.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(IllegalArgumentException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testTakeDoesntLeakErrors() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable<String> source = Flowable.unsafeCreate(new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onNext("one");
                    subscriber.onError(new Throwable("test failed"));
                }
            });
            Subscriber<String> subscriber = TestHelper.mockSubscriber();
            source.take(1).subscribe(subscriber);
            Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.notNull())));
            Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
            // even though onError is called we take(1) so shouldn't see it
            Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
            Mockito.verify(subscriber, Mockito.times(1)).onComplete();
            Mockito.verifyNoMoreInteractions(subscriber);
            TestHelper.assertUndeliverable(errors, 0, Throwable.class, "test failed");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testUnsubscribeAfterTake() {
        FlowableTakeTest.TestFlowableFunc f = new FlowableTakeTest.TestFlowableFunc("one", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(f);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> take = w.take(1);
        take.subscribe(subscriber);
        // wait for the Flowable to complete
        try {
            f.t.join();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        System.out.println("TestFlowable thread finished");
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.notNull())));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        // FIXME no longer assertable
        // verify(s, times(1)).unsubscribe();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test(timeout = 2000)
    public void testUnsubscribeFromSynchronousInfiniteFlowable() {
        final AtomicLong count = new AtomicLong();
        FlowableTakeTest.INFINITE_OBSERVABLE.take(10).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long l) {
                count.set(l);
            }
        });
        Assert.assertEquals(10, count.get());
    }

    @Test(timeout = 2000)
    public void testMultiTake() {
        final AtomicInteger count = new AtomicInteger();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                BooleanSubscription bs = new BooleanSubscription();
                s.onSubscribe(bs);
                for (int i = 0; !(bs.isCancelled()); i++) {
                    System.out.println(("Emit: " + i));
                    count.incrementAndGet();
                    s.onNext(i);
                }
            }
        }).take(100).take(1).blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                System.out.println(("Receive: " + t1));
            }
        });
        Assert.assertEquals(1, count.get());
    }

    static class TestFlowableFunc implements Publisher<String> {
        final String[] values;

        Thread t;

        TestFlowableFunc(String... values) {
            this.values = values;
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            System.out.println("TestFlowable subscribed to ...");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestFlowable thread");
                        for (String s : values) {
                            System.out.println(("TestFlowable onNext: " + s));
                            subscriber.onNext(s);
                        }
                        subscriber.onComplete();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println("starting TestFlowable thread");
            t.start();
            System.out.println("done starting TestFlowable thread");
        }
    }

    private static io.reactivex.Flowable<Long> INFINITE_OBSERVABLE = Flowable.unsafeCreate(new Publisher<Long>() {
        @Override
        public void subscribe(Subscriber<? super Long> op) {
            BooleanSubscription bs = new BooleanSubscription();
            op.onSubscribe(bs);
            long l = 1;
            while (!(bs.isCancelled())) {
                op.onNext((l++));
            } 
            op.onComplete();
        }
    });

    @Test(timeout = 2000)
    public void testTakeObserveOn() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        FlowableTakeTest.INFINITE_OBSERVABLE.onBackpressureDrop().observeOn(io.reactivex.schedulers.Schedulers.newThread()).take(1).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Mockito.verify(subscriber).onNext(1L);
        Mockito.verify(subscriber, Mockito.never()).onNext(2L);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testProducerRequestThroughTake() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(3);
        final AtomicLong requested = new AtomicLong();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        requested.set(n);
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        }).take(3).subscribe(ts);
        Assert.assertEquals(Long.MAX_VALUE, requested.get());
    }

    @Test
    public void testProducerRequestThroughTakeIsModified() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(3);
        final AtomicLong requested = new AtomicLong();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        requested.set(n);
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        }).take(1).subscribe(ts);
        // FIXME take triggers fast path if downstream requests more than the limit
        Assert.assertEquals(Long.MAX_VALUE, requested.get());
    }

    @Test
    public void testInterrupt() throws InterruptedException {
        final AtomicReference<Object> exception = new AtomicReference<Object>();
        final CountDownLatch latch = new CountDownLatch(1);
        Flowable.just(1).subscribeOn(io.reactivex.schedulers.Schedulers.computation()).take(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    exception.set(e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });
        latch.await();
        Assert.assertNull(exception.get());
    }

    @Test
    public void testDoesntRequestMoreThanNeededFromUpstream() throws InterruptedException {
        final AtomicLong requests = new AtomicLong();
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        // 
        // 
        // 
        Flowable.interval(100, MILLISECONDS).doOnRequest(new LongConsumer() {
            @Override
            public void accept(long n) {
                System.out.println(n);
                requests.addAndGet(n);
            }
        }).take(2).subscribe(ts);
        Thread.sleep(50);
        ts.request(1);
        ts.request(1);
        ts.request(1);
        ts.awaitTerminalEvent();
        ts.assertComplete();
        ts.assertNoErrors();
        Assert.assertEquals(3, requests.get());
    }

    @Test
    public void takeFinalValueThrows() {
        Flowable<Integer> source = Flowable.just(1).take(1);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                throw new TestException();
            }
        };
        source.safeSubscribe(ts);
        ts.assertNoValues();
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @Test
    public void testReentrantTake() {
        final PublishProcessor<Integer> source = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.take(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) {
                source.onNext(2);
            }
        }).subscribe(ts);
        source.onNext(1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void takeNegative() {
        try {
            Flowable.just(1).take((-99));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("count >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void takeZero() {
        Flowable.just(1).take(0).test().assertResult();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().take(2));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.take(2);
            }
        });
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(Flowable.never().take(1));
    }

    @Test
    public void requestRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final TestSubscriber<Integer> ts = Flowable.range(1, 2).take(2).test(0L);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.request(1);
                }
            };
            TestHelper.race(r1, r1);
            ts.assertResult(1, 2);
        }
    }

    @Test
    public void errorAfterLimitReached() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.error(new TestException()).take(0).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

