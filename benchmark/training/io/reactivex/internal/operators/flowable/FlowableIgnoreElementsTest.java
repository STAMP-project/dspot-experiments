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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;


public class FlowableIgnoreElementsTest {
    @Test
    public void testWithEmptyFlowable() {
        Assert.assertTrue(Flowable.empty().ignoreElements().toFlowable().isEmpty().blockingGet());
    }

    @Test
    public void testWithNonEmptyFlowable() {
        Assert.assertTrue(Flowable.just(1, 2, 3).ignoreElements().toFlowable().isEmpty().blockingGet());
    }

    @Test
    public void testUpstreamIsProcessedButIgnoredFlowable() {
        final int num = 10;
        final AtomicInteger upstreamCount = new AtomicInteger();
        long count = Flowable.range(1, num).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                upstreamCount.incrementAndGet();
            }
        }).ignoreElements().toFlowable().count().blockingGet();
        Assert.assertEquals(num, upstreamCount.get());
        Assert.assertEquals(0, count);
    }

    @Test
    public void testCompletedOkFlowable() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Flowable.range(1, 10).ignoreElements().toFlowable().subscribe(ts);
        ts.assertNoErrors();
        ts.assertNoValues();
        ts.assertTerminated();
    }

    @Test
    public void testErrorReceivedFlowable() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        TestException ex = new TestException("boo");
        Flowable.error(ex).ignoreElements().toFlowable().subscribe(ts);
        ts.assertNoValues();
        ts.assertTerminated();
        ts.assertError(TestException.class);
        ts.assertErrorMessage("boo");
    }

    @Test
    public void testUnsubscribesFromUpstreamFlowable() {
        final AtomicBoolean unsub = new AtomicBoolean();
        Flowable.range(1, 10).concatWith(Flowable.<Integer>never()).doOnCancel(new Action() {
            @Override
            public void run() {
                unsub.set(true);
            }
        }).ignoreElements().toFlowable().subscribe().dispose();
        Assert.assertTrue(unsub.get());
    }

    @Test(timeout = 10000)
    public void testDoesNotHangAndProcessesAllUsingBackpressureFlowable() {
        final AtomicInteger upstreamCount = new AtomicInteger();
        final AtomicInteger count = new AtomicInteger(0);
        int num = 10;
        // 
        // 
        // 
        // 
        Flowable.range(1, num).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                upstreamCount.incrementAndGet();
            }
        }).ignoreElements().<Integer>toFlowable().doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                upstreamCount.incrementAndGet();
            }
        }).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
                count.incrementAndGet();
            }
        });
        Assert.assertEquals(num, upstreamCount.get());
        Assert.assertEquals(0, count.get());
    }

    @Test
    public void testWithEmpty() {
        Assert.assertNull(Flowable.empty().ignoreElements().blockingGet());
    }

    @Test
    public void testWithNonEmpty() {
        Assert.assertNull(Flowable.just(1, 2, 3).ignoreElements().blockingGet());
    }

    @Test
    public void testUpstreamIsProcessedButIgnored() {
        final int num = 10;
        final AtomicInteger upstreamCount = new AtomicInteger();
        Object count = Flowable.range(1, num).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                upstreamCount.incrementAndGet();
            }
        }).ignoreElements().blockingGet();
        Assert.assertEquals(num, upstreamCount.get());
        Assert.assertNull(count);
    }

    @Test
    public void testCompletedOk() {
        TestObserver<Object> to = new TestObserver<Object>();
        Flowable.range(1, 10).ignoreElements().subscribe(to);
        to.assertNoErrors();
        to.assertNoValues();
        to.assertTerminated();
    }

    @Test
    public void testErrorReceived() {
        TestObserver<Object> to = new TestObserver<Object>();
        TestException ex = new TestException("boo");
        Flowable.error(ex).ignoreElements().subscribe(to);
        to.assertNoValues();
        to.assertTerminated();
        to.assertError(TestException.class);
        to.assertErrorMessage("boo");
    }

    @Test
    public void testUnsubscribesFromUpstream() {
        final AtomicBoolean unsub = new AtomicBoolean();
        Flowable.range(1, 10).concatWith(Flowable.<Integer>never()).doOnCancel(new Action() {
            @Override
            public void run() {
                unsub.set(true);
            }
        }).ignoreElements().subscribe().dispose();
        Assert.assertTrue(unsub.get());
    }

    @Test(timeout = 10000)
    public void testDoesNotHangAndProcessesAllUsingBackpressure() {
        final AtomicInteger upstreamCount = new AtomicInteger();
        final AtomicInteger count = new AtomicInteger(0);
        int num = 10;
        // 
        // 
        // 
        Flowable.range(1, num).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                upstreamCount.incrementAndGet();
            }
        }).ignoreElements().subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
        Assert.assertEquals(num, upstreamCount.get());
        Assert.assertEquals(0, count.get());
    }

    @Test
    public void cancel() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.ignoreElements().<Integer>toFlowable().test();
        Assert.assertTrue(pp.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void fused() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.just(1).hide().ignoreElements().<Integer>toFlowable().subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult();
    }

    @Test
    public void fusedAPICalls() {
        Flowable.just(1).hide().ignoreElements().<Integer>toFlowable().subscribe(new FlowableSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                @SuppressWarnings("unchecked")
                QueueSubscription<Integer> qs = ((QueueSubscription<Integer>) (s));
                try {
                    Assert.assertNull(qs.poll());
                } catch (Exception ex) {
                    throw new AssertionError(ex);
                }
                Assert.assertTrue(qs.isEmpty());
                qs.clear();
                Assert.assertTrue(qs.isEmpty());
                try {
                    Assert.assertNull(qs.poll());
                } catch (Exception ex) {
                    throw new AssertionError(ex);
                }
                try {
                    qs.offer(1);
                    Assert.fail("Should have thrown!");
                } catch (UnsupportedOperationException ex) {
                    // expected
                }
                try {
                    qs.offer(1, 2);
                    Assert.fail("Should have thrown!");
                } catch (UnsupportedOperationException ex) {
                    // expected
                }
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
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).ignoreElements());
        TestHelper.checkDisposed(Flowable.just(1).ignoreElements().toFlowable());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.ignoreElements().toFlowable();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToCompletable(new Function<Flowable<Object>, Completable>() {
            @Override
            public io.reactivex.Completable apply(Flowable<Object> f) throws Exception {
                return f.ignoreElements();
            }
        });
    }
}

