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
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.TestScheduler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableThrottleFirstTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    private Subscriber<String> subscriber;

    @Test
    public void testThrottlingWithCompleted() {
        Flowable<String> source = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                publishNext(subscriber, 100, "one");// publish as it's first

                publishNext(subscriber, 300, "two");// skip as it's last within the first 400

                publishNext(subscriber, 900, "three");// publish

                publishNext(subscriber, 905, "four");// skip

                publishCompleted(subscriber, 1000);// Should be published as soon as the timeout expires.

            }
        });
        Flowable<String> sampled = source.throttleFirst(400, TimeUnit.MILLISECONDS, scheduler);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("one");
        inOrder.verify(subscriber, Mockito.times(0)).onNext("two");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("three");
        inOrder.verify(subscriber, Mockito.times(0)).onNext("four");
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testThrottlingWithError() {
        Flowable<String> source = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                Exception error = new TestException();
                publishNext(subscriber, 100, "one");// Should be published since it is first

                publishNext(subscriber, 200, "two");// Should be skipped since onError will arrive before the timeout expires

                publishError(subscriber, 300, error);// Should be published as soon as the timeout expires.

            }
        });
        Flowable<String> sampled = source.throttleFirst(400, TimeUnit.MILLISECONDS, scheduler);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(400, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber).onNext("one");
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testThrottle() {
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        TestScheduler s = new TestScheduler();
        PublishProcessor<Integer> o = PublishProcessor.create();
        o.throttleFirst(500, TimeUnit.MILLISECONDS, s).subscribe(subscriber);
        // send events with simulated time increments
        s.advanceTimeTo(0, TimeUnit.MILLISECONDS);
        o.onNext(1);// deliver

        o.onNext(2);// skip

        s.advanceTimeTo(501, TimeUnit.MILLISECONDS);
        o.onNext(3);// deliver

        s.advanceTimeTo(600, TimeUnit.MILLISECONDS);
        o.onNext(4);// skip

        s.advanceTimeTo(700, TimeUnit.MILLISECONDS);
        o.onNext(5);// skip

        o.onNext(6);// skip

        s.advanceTimeTo(1001, TimeUnit.MILLISECONDS);
        o.onNext(7);// deliver

        s.advanceTimeTo(1501, TimeUnit.MILLISECONDS);
        o.onComplete();
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onNext(7);
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void throttleFirstDefaultScheduler() {
        throttleFirst(100, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(throttleFirst(1, TimeUnit.DAYS));
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            throttleFirst(1, TimeUnit.DAYS).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void backpressureNoRequest() {
        throttleFirst(1, TimeUnit.MINUTES).test(0L).assertFailure(MissingBackpressureException.class);
    }
}

