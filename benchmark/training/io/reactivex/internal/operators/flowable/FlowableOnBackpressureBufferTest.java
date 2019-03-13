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
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.Flowable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class FlowableOnBackpressureBufferTest {
    @Test
    public void testNoBackpressureSupport() {
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        // this will be ignored
        ts.request(100);
        // we take 500 so it unsubscribes
        FlowableOnBackpressureBufferTest.infinite.take(500).subscribe(ts);
        // it completely ignores the `request(100)` and we get 500
        Assert.assertEquals(500, ts.values().size());
        ts.assertNoErrors();
    }

    @Test(timeout = 2000)
    public void testFixBackpressureWithBuffer() throws InterruptedException {
        final CountDownLatch l1 = new CountDownLatch(100);
        final CountDownLatch l2 = new CountDownLatch(150);
        TestSubscriber<Long> ts = new TestSubscriber<Long>(new DefaultSubscriber<Long>() {
            @Override
            protected void onStart() {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Long t) {
                l1.countDown();
                l2.countDown();
            }
        }, 0L);
        // this will be ignored
        ts.request(100);
        // we take 500 so it unsubscribes
        FlowableOnBackpressureBufferTest.infinite.subscribeOn(Schedulers.computation()).onBackpressureBuffer().take(500).subscribe(ts);
        // it completely ignores the `request(100)` and we get 500
        l1.await();
        Assert.assertEquals(100, ts.values().size());
        ts.request(50);
        l2.await();
        Assert.assertEquals(150, ts.values().size());
        ts.request(350);
        ts.awaitTerminalEvent();
        Assert.assertEquals(500, ts.values().size());
        ts.assertNoErrors();
        Assert.assertEquals(0, ts.values().get(0).intValue());
        Assert.assertEquals(499, ts.values().get(499).intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFixBackpressureBufferNegativeCapacity() throws InterruptedException {
        Flowable.empty().onBackpressureBuffer((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFixBackpressureBufferZeroCapacity() throws InterruptedException {
        Flowable.empty().onBackpressureBuffer(0);
    }

    @Test
    public void testFixBackpressureBoundedBuffer() throws InterruptedException {
        final CountDownLatch l1 = new CountDownLatch(100);
        final CountDownLatch backpressureCallback = new CountDownLatch(1);
        TestSubscriber<Long> ts = new TestSubscriber<Long>(new DefaultSubscriber<Long>() {
            @Override
            protected void onStart() {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Long t) {
                l1.countDown();
            }
        }, 0L);
        ts.request(100);
        /* .take(1000) */
        FlowableOnBackpressureBufferTest.infinite.subscribeOn(Schedulers.computation()).onBackpressureBuffer(500, new Action() {
            @Override
            public void run() {
                backpressureCallback.countDown();
            }
        }).subscribe(ts);
        l1.await();
        ts.request(50);
        Assert.assertTrue(backpressureCallback.await(500, TimeUnit.MILLISECONDS));
        ts.awaitTerminalEvent(1, TimeUnit.SECONDS);
        ts.assertError(MissingBackpressureException.class);
        int size = ts.values().size();
        Assert.assertTrue((size <= 150));// will get up to 50 more

        Assert.assertTrue(((ts.values().get((size - 1))) == (size - 1)));
    }

    static final Flowable<Long> infinite = Flowable.unsafeCreate(new Publisher<Long>() {
        @Override
        public void subscribe(Subscriber<? super Long> s) {
            BooleanSubscription bs = new BooleanSubscription();
            s.onSubscribe(bs);
            long i = 0;
            while (!(bs.isCancelled())) {
                s.onNext((i++));
            } 
        }
    });

    private static final Action THROWS_NON_FATAL = new Action() {
        @Override
        public void run() {
            throw new RuntimeException();
        }
    };

    @Test
    public void nonFatalExceptionThrownByOnOverflowIsNotReportedByUpstream() {
        final AtomicBoolean errorOccurred = new AtomicBoolean(false);
        TestSubscriber<Long> ts = TestSubscriber.create(0);
        FlowableOnBackpressureBufferTest.infinite.subscribeOn(Schedulers.computation()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                errorOccurred.set(true);
            }
        }).onBackpressureBuffer(1, FlowableOnBackpressureBufferTest.THROWS_NON_FATAL).subscribe(ts);
        ts.awaitTerminalEvent();
        Assert.assertFalse(errorOccurred.get());
    }

    @Test
    public void maxSize() {
        TestSubscriber<Integer> ts = TestSubscriber.create(0);
        Flowable.range(1, 10).onBackpressureBuffer(1).subscribe(ts);
        ts.assertNoValues();
        ts.assertError(MissingBackpressureException.class);
        ts.assertNotComplete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fixBackpressureBufferNegativeCapacity() throws InterruptedException {
        Flowable.empty().onBackpressureBuffer((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fixBackpressureBufferZeroCapacity() throws InterruptedException {
        Flowable.empty().onBackpressureBuffer(0);
    }

    @Test(expected = NullPointerException.class)
    public void fixBackpressureBufferNullStrategy() throws InterruptedException {
        Flowable.empty().onBackpressureBuffer(10, new Action() {
            @Override
            public void run() {
            }
        }, null);
    }

    @Test
    public void noDelayError() {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).onBackpressureBuffer(false).test(0L).assertFailure(TestException.class);
    }

    @Test
    public void delayError() {
        TestSubscriber<Integer> ts = Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).onBackpressureBuffer(true).test(0L).assertEmpty();
        ts.request(1);
        ts.assertFailure(TestException.class, 1);
    }

    @Test
    public void delayErrorBuffer() {
        TestSubscriber<Integer> ts = Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).onBackpressureBuffer(16, true).test(0L).assertEmpty();
        ts.request(1);
        ts.assertFailure(TestException.class, 1);
    }

    @Test
    public void fusedNormal() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 10).onBackpressureBuffer().subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void fusedError() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.<Integer>error(new TestException()).onBackpressureBuffer().subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertFailure(TestException.class);
    }

    @Test
    public void fusedPreconsume() throws Exception {
        TestSubscriber<Integer> ts = Flowable.range(1, (1000 * 1000)).onBackpressureBuffer().observeOn(Schedulers.single()).test(0L);
        ts.assertEmpty();
        Thread.sleep(100);
        ts.request((1000 * 1000));
        ts.awaitDone(5, TimeUnit.SECONDS).assertValueCount((1000 * 1000)).assertNoErrors().assertComplete();
    }

    @Test
    public void emptyDelayError() {
        Flowable.empty().onBackpressureBuffer(true).test().assertResult();
    }

    @Test
    public void fusionRejected() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(SYNC);
        Flowable.<Integer>never().onBackpressureBuffer().subscribe(ts);
        SubscriberFusion.assertFusion(ts, NONE).assertEmpty();
    }
}

