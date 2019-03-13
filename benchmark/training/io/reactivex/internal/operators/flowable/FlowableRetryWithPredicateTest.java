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
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableRetryWithPredicateTest {
    BiPredicate<Integer, Throwable> retryTwice = new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer t1, Throwable t2) {
            return t1 <= 2;
        }
    };

    BiPredicate<Integer, Throwable> retry5 = new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer t1, Throwable t2) {
            return t1 <= 5;
        }
    };

    BiPredicate<Integer, Throwable> retryOnTestException = new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer t1, Throwable t2) {
            return t2 instanceof IOException;
        }
    };

    @Test
    public void testWithNothingToRetry() {
        Flowable<Integer> source = Flowable.range(0, 3);
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.retry(retryTwice).subscribe(subscriber);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testRetryTwice() {
        Flowable<Integer> source = Flowable.unsafeCreate(new Publisher<Integer>() {
            int count;

            @Override
            public void subscribe(Subscriber<? super Integer> t1) {
                t1.onSubscribe(new BooleanSubscription());
                (count)++;
                t1.onNext(0);
                t1.onNext(1);
                if ((count) == 1) {
                    t1.onError(new TestException());
                    return;
                }
                t1.onNext(2);
                t1.onNext(3);
                t1.onComplete();
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.retry(retryTwice).subscribe(subscriber);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testRetryTwiceAndGiveUp() {
        Flowable<Integer> source = Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> t1) {
                t1.onSubscribe(new BooleanSubscription());
                t1.onNext(0);
                t1.onNext(1);
                t1.onError(new TestException());
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.retry(retryTwice).subscribe(subscriber);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testRetryOnSpecificException() {
        Flowable<Integer> source = Flowable.unsafeCreate(new Publisher<Integer>() {
            int count;

            @Override
            public void subscribe(Subscriber<? super Integer> t1) {
                t1.onSubscribe(new BooleanSubscription());
                (count)++;
                t1.onNext(0);
                t1.onNext(1);
                if ((count) == 1) {
                    t1.onError(new IOException());
                    return;
                }
                t1.onNext(2);
                t1.onNext(3);
                t1.onComplete();
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.retry(retryOnTestException).subscribe(subscriber);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testRetryOnSpecificExceptionAndNotOther() {
        final IOException ioe = new IOException();
        final TestException te = new TestException();
        Flowable<Integer> source = Flowable.unsafeCreate(new Publisher<Integer>() {
            int count;

            @Override
            public void subscribe(Subscriber<? super Integer> t1) {
                t1.onSubscribe(new BooleanSubscription());
                (count)++;
                t1.onNext(0);
                t1.onNext(1);
                if ((count) == 1) {
                    t1.onError(ioe);
                    return;
                }
                t1.onNext(2);
                t1.onNext(3);
                t1.onError(te);
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.retry(retryOnTestException).subscribe(subscriber);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(0);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onError(te);
        Mockito.verify(subscriber, Mockito.never()).onError(ioe);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testUnsubscribeFromRetry() {
        PublishProcessor<Integer> processor = PublishProcessor.create();
        final AtomicInteger count = new AtomicInteger(0);
        Disposable sub = processor.retry(retryTwice).subscribe(new Consumer<Integer>() {
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

    @Test(timeout = 10000)
    public void testUnsubscribeAfterError() {
        Subscriber<Long> subscriber = TestHelper.mockSubscriber();
        // Flowable that always fails after 100ms
        FlowableRetryTest.SlowFlowable so = new FlowableRetryTest.SlowFlowable(100, 0, "testUnsubscribeAfterError");
        Flowable<Long> f = Flowable.unsafeCreate(so).retry(retry5);
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

    @Test(timeout = 10000)
    public void testTimeoutWithRetry() {
        Subscriber<Long> subscriber = TestHelper.mockSubscriber();
        // Flowable that sends every 100ms (timeout fails instead)
        FlowableRetryTest.SlowFlowable so = new FlowableRetryTest.SlowFlowable(100, 10, "testTimeoutWithRetry");
        Flowable<Long> f = Flowable.unsafeCreate(so).timeout(80, TimeUnit.MILLISECONDS).retry(retry5);
        FlowableRetryTest.AsyncSubscriber<Long> async = new FlowableRetryTest.AsyncSubscriber<Long>(subscriber);
        f.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(subscriber);
        // Should fail once
        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(subscriber, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, so.efforts.get());
    }

    @Test
    public void testIssue2826() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final RuntimeException e = new RuntimeException("You shall not pass");
        final AtomicInteger c = new AtomicInteger();
        Flowable.just(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                c.incrementAndGet();
                throw e;
            }
        }).retry(retry5).subscribe(ts);
        ts.assertTerminated();
        Assert.assertEquals(6, c.get());
        Assert.assertEquals(Collections.singletonList(e), ts.errors());
    }

    @Test
    public void testJustAndRetry() throws Exception {
        final AtomicBoolean throwException = new AtomicBoolean(true);
        int value = Flowable.just(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                if (throwException.compareAndSet(true, false)) {
                    throw new TestException();
                }
                return t1;
            }
        }).retry(1).blockingSingle();
        Assert.assertEquals(1, value);
    }

    @Test
    public void testIssue3008RetryWithPredicate() {
        final List<Long> list = new CopyOnWriteArrayList<Long>();
        final AtomicBoolean isFirst = new AtomicBoolean(true);
        Flowable.<Long>just(1L, 2L, 3L).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long x) {
                System.out.println(("map " + x));
                if ((x == 2) && (isFirst.getAndSet(false))) {
                    throw new RuntimeException("retryable error");
                }
                return x;
            }
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer t1, Throwable t2) {
                return true;
            }
        }).forEach(new Consumer<Long>() {
            @Override
            public void accept(Long t) {
                System.out.println(t);
                list.add(t);
            }
        });
        Assert.assertEquals(Arrays.asList(1L, 1L, 2L, 3L), list);
    }

    @Test
    public void testIssue3008RetryInfinite() {
        final List<Long> list = new CopyOnWriteArrayList<Long>();
        final AtomicBoolean isFirst = new AtomicBoolean(true);
        Flowable.<Long>just(1L, 2L, 3L).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long x) {
                System.out.println(("map " + x));
                if ((x == 2) && (isFirst.getAndSet(false))) {
                    throw new RuntimeException("retryable error");
                }
                return x;
            }
        }).retry().forEach(new Consumer<Long>() {
            @Override
            public void accept(Long t) {
                System.out.println(t);
                list.add(t);
            }
        });
        Assert.assertEquals(Arrays.asList(1L, 1L, 2L, 3L), list);
    }

    @Test
    public void testBackpressure() {
        final List<Long> requests = new ArrayList<Long>();
        Flowable<Integer> source = Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).doOnRequest(new LongConsumer() {
            @Override
            public void accept(long t) {
                requests.add(t);
            }
        });
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(3L);
        source.retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer t1, Throwable t2) {
                return t1 < 4;// FIXME was 3 in 1.x for some reason

            }
        }).subscribe(ts);
        Assert.assertEquals(Arrays.asList(3L, 2L, 1L), requests);
        ts.assertValues(1, 1, 1);
        ts.assertNotComplete();
        ts.assertNoErrors();
    }

    @Test
    public void predicateThrows() {
        TestSubscriber<Object> ts = Flowable.error(new TestException("Outer")).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void dontRetry() {
        Flowable.error(new TestException("Outer")).retry(Functions.alwaysFalse()).test().assertFailureAndMessage(TestException.class, "Outer");
    }

    @Test
    public void retryDisposeRace() {
        final TestException ex = new TestException();
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                final PublishProcessor<Integer> pp = PublishProcessor.create();
                final TestSubscriber<Integer> ts = pp.retry(Functions.alwaysTrue()).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ts.cancel();
                    }
                };
                TestHelper.race(r1, r2);
                ts.assertEmpty();
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void bipredicateThrows() {
        TestSubscriber<Object> ts = Flowable.error(new TestException("Outer")).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer n, Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void retryBiPredicateDisposeRace() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                final PublishProcessor<Integer> pp = PublishProcessor.create();
                final TestSubscriber<Integer> ts = pp.retry(new BiPredicate<Object, Object>() {
                    @Override
                    public boolean test(Object t1, Object t2) throws Exception {
                        return true;
                    }
                }).test();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ts.cancel();
                    }
                };
                TestHelper.race(r1, r2);
                ts.assertEmpty();
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

