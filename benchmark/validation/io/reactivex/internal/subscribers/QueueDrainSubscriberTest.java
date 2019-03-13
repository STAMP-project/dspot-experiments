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
package io.reactivex.internal.subscribers;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class QueueDrainSubscriberTest {
    @Test
    public void unorderedFastPathNoRequest() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createUnordered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.onNext(1);
        ts.assertFailure(MissingBackpressureException.class);
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void orderedFastPathNoRequest() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createOrdered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.onNext(1);
        ts.assertFailure(MissingBackpressureException.class);
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void acceptBadRequest() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createUnordered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        Assert.assertTrue(qd.accept(ts, 0));
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            qd.requested((-1));
            TestHelper.assertError(errors, 0, IllegalArgumentException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void unorderedFastPathRequest1() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createUnordered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.requested(1);
        qd.onNext(1);
        ts.assertValuesOnly(1);
    }

    @Test
    public void orderedFastPathRequest1() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createOrdered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.requested(1);
        qd.onNext(1);
        ts.assertValuesOnly(1);
    }

    @Test
    public void unorderedSlowPath() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createUnordered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.enter();
        qd.onNext(1);
        ts.assertEmpty();
    }

    @Test
    public void orderedSlowPath() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createOrdered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.enter();
        qd.onNext(1);
        ts.assertEmpty();
    }

    @Test
    public void orderedSlowPathNonEmptyQueue() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createOrdered(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.queue.offer(0);
        qd.requested(2);
        qd.onNext(1);
        ts.assertValuesOnly(0, 1);
    }

    @Test
    public void unorderedOnNextRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
            Disposable d = Disposables.empty();
            final QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createUnordered(ts, d);
            ts.onSubscribe(new BooleanSubscription());
            qd.requested(Long.MAX_VALUE);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    qd.onNext(1);
                }
            };
            TestHelper.race(r1, r1);
            ts.assertValuesOnly(1, 1);
        }
    }

    @Test
    public void orderedOnNextRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
            Disposable d = Disposables.empty();
            final QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createOrdered(ts, d);
            ts.onSubscribe(new BooleanSubscription());
            qd.requested(Long.MAX_VALUE);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    qd.onNext(1);
                }
            };
            TestHelper.race(r1, r1);
            ts.assertValuesOnly(1, 1);
        }
    }

    @Test
    public void unorderedFastPathReject() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createUnorderedReject(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.requested(1);
        qd.onNext(1);
        ts.assertValuesOnly(1);
        Assert.assertEquals(1, qd.requested());
    }

    @Test
    public void orderedFastPathReject() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1);
        Disposable d = Disposables.empty();
        QueueDrainSubscriber<Integer, Integer, Integer> qd = QueueDrainSubscriberTest.createOrderedReject(ts, d);
        ts.onSubscribe(new BooleanSubscription());
        qd.requested(1);
        qd.onNext(1);
        ts.assertValuesOnly(1);
        Assert.assertEquals(1, qd.requested());
    }
}

