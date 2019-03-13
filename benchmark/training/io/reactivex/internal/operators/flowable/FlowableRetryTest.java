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
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class FlowableRetryTest {
    @Test
    public void iterativeBackoff() {
        Subscriber<String> consumer = TestHelper.mockSubscriber();
        Flowable<String> producer = Flowable.unsafeCreate(new Publisher<String>() {
            private AtomicInteger count = new AtomicInteger(4);

            long last = System.currentTimeMillis();

            @Override
            public void subscribe(Subscriber<? super String> t1) {
                t1.onSubscribe(new BooleanSubscription());
                System.out.println((((count.get()) + " @ ") + (String.valueOf(((last) - (System.currentTimeMillis()))))));
                last = System.currentTimeMillis();
                if ((count.getAndDecrement()) == 0) {
                    t1.onNext("hello");
                    t1.onComplete();
                } else {
                    t1.onError(new RuntimeException());
                }
            }
        });
        TestSubscriber<String> ts = new TestSubscriber<String>(consumer);
        producer.retryWhen(new Function<Flowable<? extends Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<? extends Throwable> attempts) {
                // Worker w = Schedulers.computation().createWorker();
                return attempts.map(new Function<Throwable, FlowableRetryTest.Tuple>() {
                    @Override
                    public FlowableRetryTest.Tuple apply(Throwable n) {
                        return new FlowableRetryTest.Tuple(new Long(1), n);
                    }
                }).scan(new BiFunction<FlowableRetryTest.Tuple, FlowableRetryTest.Tuple, FlowableRetryTest.Tuple>() {
                    @Override
                    public FlowableRetryTest.Tuple apply(FlowableRetryTest.Tuple t, FlowableRetryTest.Tuple n) {
                        return new FlowableRetryTest.Tuple(((t.count) + (n.count)), n.n);
                    }
                }).flatMap(new Function<FlowableRetryTest.Tuple, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(FlowableRetryTest.Tuple t) {
                        System.out.println(("Retry # " + (t.count)));
                        return (t.count) > 20 ? Flowable.<Object>error(t.n) : Flowable.timer(((t.count) * 1L), MILLISECONDS).cast(Object.class);
                    }
                });
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        InOrder inOrder = Mockito.inOrder(consumer);
        inOrder.verify(consumer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(consumer, Mockito.times(1)).onNext("hello");
        inOrder.verify(consumer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    public static class Tuple {
        Long count;

        Throwable n;

        Tuple(Long c, Throwable n) {
            count = c;
            this.n = n;
        }
    }

    @Test
    public void testRetryIndefinitely() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        int numRetries = 20;
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numRetries));
        origin.retry().subscribe(new TestSubscriber<String>(subscriber));
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times((numRetries + 1))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(subscriber, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSchedulingNotificationHandler() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        int numRetries = 2;
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numRetries));
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        origin.retryWhen(new Function<Flowable<? extends Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<? extends Throwable> t1) {
                return t1.observeOn(io.reactivex.schedulers.Schedulers.computation()).map(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable t1) {
                        return 1;
                    }
                }).startWith(1).cast(Object.class);
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) {
                e.printStackTrace();
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times((1 + numRetries))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(subscriber, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnNextFromNotificationHandler() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        int numRetries = 2;
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numRetries));
        origin.retryWhen(new Function<Flowable<? extends Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<? extends Throwable> t1) {
                return t1.map(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable t1) {
                        return 0;
                    }
                }).startWith(0).cast(Object.class);
            }
        }).subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times((numRetries + 1))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(subscriber, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnCompletedFromNotificationHandler() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(1));
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        origin.retryWhen(new Function<Flowable<? extends Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<? extends Throwable> t1) {
                return Flowable.empty();
            }
        }).subscribe(ts);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.notNull())));
        inOrder.verify(subscriber, Mockito.never()).onNext("beginningEveryTime");
        inOrder.verify(subscriber, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Exception.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnErrorFromNotificationHandler() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(2));
        origin.retryWhen(new Function<Flowable<? extends Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<? extends Throwable> t1) {
                return Flowable.error(new RuntimeException());
            }
        }).subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.notNull())));
        inOrder.verify(subscriber, Mockito.never()).onNext("beginningEveryTime");
        inOrder.verify(subscriber, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSingleSubscriptionOnFirst() throws Exception {
        final AtomicInteger inc = new AtomicInteger(0);
        Publisher<Integer> onSubscribe = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                final int emit = inc.incrementAndGet();
                subscriber.onNext(emit);
                subscriber.onComplete();
            }
        };
        int first = Flowable.unsafeCreate(onSubscribe).retryWhen(new Function<Flowable<? extends Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<? extends Throwable> attempt) {
                return attempt.zipWith(Flowable.just(1), new BiFunction<Throwable, Integer, Object>() {
                    @Override
                    public Object apply(Throwable o, Integer integer) {
                        return 0;
                    }
                });
            }
        }).blockingFirst();
        Assert.assertEquals("Observer did not receive the expected output", 1, first);
        Assert.assertEquals("Subscribe was not called once", 1, inc.get());
    }

    @Test
    public void testOriginFails() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(1));
        origin.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("beginningEveryTime");
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        inOrder.verify(subscriber, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testRetryFail() {
        int numRetries = 1;
        int numFailures = 2;
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numFailures));
        origin.retry(numRetries).subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 2 attempts (first time fail, second time (1st retry) fail)
        inOrder.verify(subscriber, Mockito.times((1 + numRetries))).onNext("beginningEveryTime");
        // should only retry once, fail again and emit onError
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        // no success
        inOrder.verify(subscriber, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRetrySuccess() {
        int numFailures = 1;
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numFailures));
        origin.retry(3).subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times((1 + numFailures))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(subscriber, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testInfiniteRetry() {
        int numFailures = 20;
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numFailures));
        origin.retry().subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times((1 + numFailures))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(subscriber, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    /* Checks in a simple and synchronous way that retry resubscribes
    after error. This test fails against 0.16.1-0.17.4, hangs on 0.17.5 and
    passes in 0.17.6 thanks to fix for issue #1027.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRetrySubscribesAgainAfterError() throws Exception {
        // record emitted values with this action
        Consumer<Integer> record = Mockito.mock(io.reactivex.Consumer.class);
        InOrder inOrder = Mockito.inOrder(record);
        // always throw an exception with this action
        Consumer<Integer> throwException = Mockito.mock(io.reactivex.Consumer.class);
        Mockito.doThrow(new RuntimeException()).when(throwException).accept(Mockito.anyInt());
        // create a retrying Flowable based on a PublishProcessor
        PublishProcessor<Integer> processor = PublishProcessor.create();
        // subscribe and ignore
        // retry on error
        // throw a RuntimeException
        // record item
        processor.doOnNext(record).doOnNext(throwException).retry().subscribe();
        inOrder.verifyNoMoreInteractions();
        processor.onNext(1);
        inOrder.verify(record).accept(1);
        processor.onNext(2);
        inOrder.verify(record).accept(2);
        processor.onNext(3);
        inOrder.verify(record).accept(3);
        inOrder.verifyNoMoreInteractions();
    }

    public static class FuncWithErrors implements Publisher<String> {
        private final int numFailures;

        private final AtomicInteger count = new AtomicInteger(0);

        FuncWithErrors(int count) {
            this.numFailures = count;
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new Subscription() {
                final AtomicLong req = new AtomicLong();

                // 0 = not set, 1 = fast path, 2 = backpressure
                final AtomicInteger path = new AtomicInteger(0);

                volatile boolean done;

                @Override
                public void request(long n) {
                    if ((n == (Long.MAX_VALUE)) && (path.compareAndSet(0, 1))) {
                        subscriber.onNext("beginningEveryTime");
                        int i = count.getAndIncrement();
                        if (i < (numFailures)) {
                            subscriber.onError(new RuntimeException(("forced failure: " + (i + 1))));
                        } else {
                            subscriber.onNext("onSuccessOnly");
                            subscriber.onComplete();
                        }
                        return;
                    }
                    if ((((n > 0) && ((req.getAndAdd(n)) == 0)) && (((path.get()) == 2) || (path.compareAndSet(0, 2)))) && (!(done))) {
                        int i = count.getAndIncrement();
                        if (i < (numFailures)) {
                            subscriber.onNext("beginningEveryTime");
                            subscriber.onError(new RuntimeException(("forced failure: " + (i + 1))));
                            done = true;
                        } else {
                            do {
                                if (i == (numFailures)) {
                                    subscriber.onNext("beginningEveryTime");
                                } else
                                    if (i > (numFailures)) {
                                        subscriber.onNext("onSuccessOnly");
                                        subscriber.onComplete();
                                        done = true;
                                        break;
                                    }

                                i = count.getAndIncrement();
                            } while ((req.decrementAndGet()) > 0 );
                        }
                    }
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
                }
            });
        }
    }

    @Test
    public void testUnsubscribeFromRetry() {
        PublishProcessor<Integer> processor = PublishProcessor.create();
        final AtomicInteger count = new AtomicInteger(0);
        Disposable sub = processor.retry().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer n) {
                count.incrementAndGet();
            }
        });
        processor.onNext(1);
        sub.dispose();
        processor.onNext(2);
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void testRetryAllowsSubscriptionAfterAllSubscriptionsUnsubscribed() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        Publisher<String> onSubscribe = new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                subsCount.incrementAndGet();
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                    }

                    @Override
                    public void cancel() {
                        subsCount.decrementAndGet();
                    }
                });
            }
        };
        Flowable<String> stream = Flowable.unsafeCreate(onSubscribe);
        Flowable<String> streamWithRetry = stream.retry();
        Disposable sub = streamWithRetry.subscribe();
        Assert.assertEquals(1, subsCount.get());
        sub.dispose();
        Assert.assertEquals(0, subsCount.get());
        streamWithRetry.subscribe();
        Assert.assertEquals(1, subsCount.get());
    }

    @Test
    public void testSourceFlowableCallsUnsubscribe() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        final TestSubscriber<String> ts = new TestSubscriber<String>();
        Publisher<String> onSubscribe = new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                BooleanSubscription bs = new BooleanSubscription();
                // if isUnsubscribed is true that means we have a bug such as
                // https://github.com/ReactiveX/RxJava/issues/1024
                if (!(bs.isCancelled())) {
                    subsCount.incrementAndGet();
                    s.onError(new RuntimeException("failed"));
                    // it unsubscribes the child directly
                    // this simulates various error/completion scenarios that could occur
                    // or just a source that proactively triggers cleanup
                    // FIXME can't unsubscribe child
                    // s.unsubscribe();
                    bs.cancel();
                } else {
                    s.onError(new RuntimeException());
                }
            }
        };
        Flowable.unsafeCreate(onSubscribe).retry(3).subscribe(ts);
        Assert.assertEquals(4, subsCount.get());// 1 + 3 retries

    }

    @Test
    public void testSourceFlowableRetry1() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        final TestSubscriber<String> ts = new TestSubscriber<String>();
        Publisher<String> onSubscribe = new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                s.onSubscribe(new BooleanSubscription());
                subsCount.incrementAndGet();
                s.onError(new RuntimeException("failed"));
            }
        };
        Flowable.unsafeCreate(onSubscribe).retry(1).subscribe(ts);
        Assert.assertEquals(2, subsCount.get());
    }

    @Test
    public void testSourceFlowableRetry0() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        final TestSubscriber<String> ts = new TestSubscriber<String>();
        Publisher<String> onSubscribe = new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                s.onSubscribe(new BooleanSubscription());
                subsCount.incrementAndGet();
                s.onError(new RuntimeException("failed"));
            }
        };
        Flowable.unsafeCreate(onSubscribe).retry(0).subscribe(ts);
        Assert.assertEquals(1, subsCount.get());
    }

    static final class SlowFlowable implements Publisher<Long> {
        final AtomicInteger efforts = new AtomicInteger(0);

        final AtomicInteger active = new AtomicInteger(0);

        final AtomicInteger maxActive = new AtomicInteger(0);

        final AtomicInteger nextBeforeFailure;

        final String context;

        private final int emitDelay;

        SlowFlowable(int emitDelay, int countNext, String context) {
            this.emitDelay = emitDelay;
            this.nextBeforeFailure = new AtomicInteger(countNext);
            this.context = context;
        }

        @Override
        public void subscribe(final Subscriber<? super Long> subscriber) {
            final AtomicBoolean terminate = new AtomicBoolean(false);
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void cancel() {
                    terminate.set(true);
                    active.decrementAndGet();
                }
            });
            efforts.getAndIncrement();
            active.getAndIncrement();
            maxActive.set(Math.max(active.get(), maxActive.get()));
            final Thread thread = new Thread(context) {
                @Override
                public void run() {
                    long nr = 0;
                    try {
                        while (!(terminate.get())) {
                            Thread.sleep(emitDelay);
                            if ((nextBeforeFailure.getAndDecrement()) > 0) {
                                subscriber.onNext((nr++));
                            } else {
                                active.decrementAndGet();
                                subscriber.onError(new RuntimeException("expected-failed"));
                                break;
                            }
                        } 
                    } catch (InterruptedException t) {
                    }
                }
            };
            thread.start();
        }
    }

    /**
     * Observer for listener on seperate thread.
     */
    static final class AsyncSubscriber<T> extends DefaultSubscriber<T> {
        protected CountDownLatch latch = new CountDownLatch(1);

        protected io.reactivex.Subscriber<T> target;

        /**
         * Wrap existing Observer.
         *
         * @param target
         * 		the target subscriber
         */
        AsyncSubscriber(Subscriber<T> target) {
            this.target = target;
        }

        /**
         * Wait.
         */
        public void await() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Assert.fail("Test interrupted");
            }
        }

        // Observer implementation
        @Override
        public void onComplete() {
            target.onComplete();
            latch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            target.onError(t);
            latch.countDown();
        }

        @Override
        public void onNext(T v) {
            target.onNext(v);
        }
    }

    @Test(timeout = 10000)
    public void testUnsubscribeAfterError() {
        Subscriber<Long> subscriber = TestHelper.mockSubscriber();
        // Flowable that always fails after 100ms
        FlowableRetryTest.SlowFlowable so = new FlowableRetryTest.SlowFlowable(100, 0, "testUnsubscribeAfterError");
        Flowable<Long> f = Flowable.unsafeCreate(so).retry(5);
        FlowableRetryTest.AsyncSubscriber<Long> async = new FlowableRetryTest.AsyncSubscriber<Long>(subscriber);
        f.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(subscriber);
        // Should fail once
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, so.efforts.get());
        Assert.assertEquals("Only 1 active subscription", 1, so.maxActive.get());
    }

    // (timeout = 10000)
    @Test
    public void testTimeoutWithRetry() {
        Subscriber<Long> subscriber = TestHelper.mockSubscriber();
        // Flowable that sends every 100ms (timeout fails instead)
        FlowableRetryTest.SlowFlowable sf = new FlowableRetryTest.SlowFlowable(100, 10, "testTimeoutWithRetry");
        Flowable<Long> f = Flowable.unsafeCreate(sf).timeout(80, MILLISECONDS).retry(5);
        FlowableRetryTest.AsyncSubscriber<Long> async = new FlowableRetryTest.AsyncSubscriber<Long>(subscriber);
        f.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(subscriber);
        // Should fail once
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, sf.efforts.get());
    }

    // (timeout = 15000)
    @Test
    public void testRetryWithBackpressure() throws InterruptedException {
        final int NUM_LOOPS = 1;
        for (int j = 0; j < NUM_LOOPS; j++) {
            final int numRetries = (Flowable.bufferSize()) * 2;
            for (int i = 0; i < 400; i++) {
                Subscriber<String> subscriber = TestHelper.mockSubscriber();
                Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numRetries));
                TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
                origin.retry().observeOn(io.reactivex.schedulers.Schedulers.computation()).subscribe(ts);
                ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
                InOrder inOrder = Mockito.inOrder(subscriber);
                // should have no errors
                Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
                // should show numRetries attempts
                inOrder.verify(subscriber, Mockito.times((numRetries + 1))).onNext("beginningEveryTime");
                // should have a single success
                inOrder.verify(subscriber, Mockito.times(1)).onNext("onSuccessOnly");
                // should have a single successful onComplete
                inOrder.verify(subscriber, Mockito.times(1)).onComplete();
                inOrder.verifyNoMoreInteractions();
            }
        }
    }

    // (timeout = 15000)
    @Test
    public void testRetryWithBackpressureParallel() throws InterruptedException {
        final int NUM_LOOPS = 1;
        final int numRetries = (Flowable.bufferSize()) * 2;
        int ncpu = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(Math.max((ncpu / 2), 2));
        try {
            for (int r = 0; r < NUM_LOOPS; r++) {
                if ((r % 10) == 0) {
                    System.out.println(("testRetryWithBackpressureParallelLoop -> " + r));
                }
                final AtomicInteger timeouts = new AtomicInteger();
                final Map<Integer, List<String>> data = new ConcurrentHashMap<Integer, List<String>>();
                int m = 5000;
                final CountDownLatch cdl = new CountDownLatch(m);
                for (int i = 0; i < m; i++) {
                    final int j = i;
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            final AtomicInteger nexts = new AtomicInteger();
                            try {
                                Flowable<String> origin = Flowable.unsafeCreate(new FlowableRetryTest.FuncWithErrors(numRetries));
                                TestSubscriber<String> ts = new TestSubscriber<String>();
                                origin.retry().observeOn(io.reactivex.schedulers.Schedulers.computation()).subscribe(ts);
                                ts.awaitTerminalEvent(2500, MILLISECONDS);
                                List<String> onNextEvents = new ArrayList<String>(ts.values());
                                if ((onNextEvents.size()) != (numRetries + 2)) {
                                    for (Throwable t : ts.errors()) {
                                        onNextEvents.add(t.toString());
                                    }
                                    for (long err = ts.completions(); err != 0; err--) {
                                        onNextEvents.add("onComplete");
                                    }
                                    data.put(j, onNextEvents);
                                }
                            } catch (Throwable t) {
                                timeouts.incrementAndGet();
                                System.out.println(((((j + " | ") + (cdl.getCount())) + " !!! ") + (nexts.get())));
                            }
                            cdl.countDown();
                        }
                    });
                }
                cdl.await();
                Assert.assertEquals(0, timeouts.get());
                if ((data.size()) > 0) {
                    Assert.fail(("Data content mismatch: " + (FlowableRetryTest.allSequenceFrequency(data))));
                }
            }
        } finally {
            exec.shutdown();
        }
    }

    // (timeout = 3000)
    @Test
    public void testIssue1900() throws InterruptedException {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        final int NUM_MSG = 1034;
        final AtomicInteger count = new AtomicInteger();
        Flowable<String> origin = Flowable.range(0, NUM_MSG).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t1) {
                return "msg: " + (count.incrementAndGet());
            }
        });
        origin.retry().groupBy(new Function<String, String>() {
            @Override
            public String apply(String t1) {
                return t1;
            }
        }).flatMap(new Function<io.reactivex.flowables.GroupedFlowable<String, String>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(io.reactivex.flowables.GroupedFlowable<String, String> t1) {
                return t1.take(1);
            }
        }).subscribe(new TestSubscriber<String>(subscriber));
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times(NUM_MSG)).onNext(ArgumentMatchers.any(String.class));
        // // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        // inOrder.verify(observer, times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    // (timeout = 3000)
    @Test
    public void testIssue1900SourceNotSupportingBackpressure() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        final int NUM_MSG = 1034;
        final AtomicInteger count = new AtomicInteger();
        Flowable<String> origin = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                for (int i = 0; i < NUM_MSG; i++) {
                    subscriber.onNext(("msg:" + (count.incrementAndGet())));
                }
                subscriber.onComplete();
            }
        });
        origin.retry().groupBy(new Function<String, String>() {
            @Override
            public String apply(String t1) {
                return t1;
            }
        }).flatMap(new Function<io.reactivex.flowables.GroupedFlowable<String, String>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(io.reactivex.flowables.GroupedFlowable<String, String> t1) {
                return t1.take(1);
            }
        }).subscribe(new TestSubscriber<String>(subscriber));
        InOrder inOrder = Mockito.inOrder(subscriber);
        // should show 3 attempts
        inOrder.verify(subscriber, Mockito.times(NUM_MSG)).onNext(ArgumentMatchers.any(String.class));
        // // should have no errors
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        // inOrder.verify(observer, times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void retryWhenDefaultScheduler() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).retryWhen(((Function) (new Function<Flowable, Flowable>() {
            @Override
            public io.reactivex.Flowable apply(Flowable f) {
                return f.take(2);
            }
        }))).subscribe(ts);
        ts.assertValues(1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void retryWhenTrampolineScheduler() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).subscribeOn(io.reactivex.schedulers.Schedulers.trampoline()).retryWhen(((Function) (new Function<Flowable, Flowable>() {
            @Override
            public io.reactivex.Flowable apply(Flowable f) {
                return f.take(2);
            }
        }))).subscribe(ts);
        ts.assertValues(1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void retryPredicate() {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable v) throws Exception {
                return true;
            }
        }).take(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void retryLongPredicateInvalid() {
        try {
            Flowable.just(1).retry((-99), new Predicate<Throwable>() {
                @Override
                public boolean test(Throwable e) throws Exception {
                    return true;
                }
            });
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("times >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void retryUntil() {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return false;
            }
        }).take(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void shouldDisposeInnerFlowable() {
        final PublishProcessor<Object> processor = PublishProcessor.create();
        final Disposable disposable = Flowable.error(new RuntimeException("Leak")).retryWhen(new Function<Flowable<Throwable>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Throwable> errors) throws Exception {
                return errors.switchMap(new Function<Throwable, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Throwable ignore) throws Exception {
                        return processor;
                    }
                });
            }
        }).subscribe();
        Assert.assertTrue(processor.hasSubscribers());
        disposable.dispose();
        Assert.assertFalse(processor.hasSubscribers());
    }

    @Test
    public void noCancelPreviousRetry() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Flowable<Integer> source = Flowable.defer(new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return Flowable.error(new TestException());
                }
                return Flowable.just(1);
            }
        }).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retry(5).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRetryWhile() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Flowable<Integer> source = Flowable.defer(new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return Flowable.error(new TestException());
                }
                return Flowable.just(1);
            }
        }).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retry(5, io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRetryWhile2() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Flowable<Integer> source = Flowable.defer(new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return Flowable.error(new TestException());
                }
                return Flowable.just(1);
            }
        }).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer a, Throwable b) throws Exception {
                return a < 5;
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRetryUntil() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Flowable<Integer> source = Flowable.defer(new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return Flowable.error(new TestException());
                }
                return Flowable.just(1);
            }
        }).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return false;
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRepeatWhen() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Flowable<Integer> source = Flowable.defer(new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if ((times.get()) < 4) {
                    return Flowable.error(new TestException());
                }
                return Flowable.just(1);
            }
        }).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retryWhen(new Function<Flowable<Throwable>, Flowable<?>>() {
            @Override
            public io.reactivex.Flowable<?> apply(Flowable<Throwable> e) throws Exception {
                return e.takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(Object v) throws Exception {
                        return (times.getAndIncrement()) < 4;
                    }
                });
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRepeatWhen2() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Flowable<Integer> source = Flowable.<Integer>error(new TestException()).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retryWhen(new Function<Flowable<Throwable>, Flowable<?>>() {
            @Override
            public io.reactivex.Flowable<?> apply(Flowable<Throwable> e) throws Exception {
                return e.takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(Object v) throws Exception {
                        return (times.getAndIncrement()) < 4;
                    }
                });
            }
        }).test().assertResult();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void repeatFloodNoSubscriptionError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        final TestException error = new TestException();
        try {
            final PublishProcessor<Integer> source = PublishProcessor.create();
            final PublishProcessor<Integer> signaller = PublishProcessor.create();
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                TestSubscriber<Integer> ts = source.take(1).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer v) throws Exception {
                        throw error;
                    }
                }).retryWhen(new Function<Flowable<Throwable>, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Flowable<Throwable> v) throws Exception {
                        return signaller;
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                            source.onNext(1);
                        }
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                            signaller.offer(1);
                        }
                    }
                };
                TestHelper.race(r1, r2);
                ts.dispose();
            }
            if (!(errors.isEmpty())) {
                for (Throwable e : errors) {
                    e.printStackTrace();
                }
                Assert.fail((errors + ""));
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

