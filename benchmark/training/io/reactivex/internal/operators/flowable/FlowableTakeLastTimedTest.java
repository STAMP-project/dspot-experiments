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
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableTakeLastTimedTest {
    @Test(expected = IndexOutOfBoundsException.class)
    public void testTakeLastTimedWithNegativeCount() {
        Flowable.just("one").takeLast((-1), 1, TimeUnit.SECONDS);
    }

    @Test
    public void takeLastTimed() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Object> source = PublishProcessor.create();
        // FIXME time unit now matters!
        Flowable<Object> result = source.takeLast(1000, TimeUnit.MILLISECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        source.onNext(1);// T: 0ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(2);// T: 250ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(3);// T: 500ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(4);// T: 750ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(5);// T: 1000ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onComplete();// T: 1250ms

        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(3);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(4);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(5);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void takeLastTimedDelayCompletion() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Object> source = PublishProcessor.create();
        // FIXME time unit now matters
        Flowable<Object> result = source.takeLast(1000, TimeUnit.MILLISECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        source.onNext(1);// T: 0ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(2);// T: 250ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(3);// T: 500ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(4);// T: 750ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(5);// T: 1000ms

        scheduler.advanceTimeBy(1250, TimeUnit.MILLISECONDS);
        source.onComplete();// T: 2250ms

        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void takeLastTimedWithCapacity() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Object> source = PublishProcessor.create();
        // FIXME time unit now matters!
        Flowable<Object> result = source.takeLast(2, 1000, TimeUnit.MILLISECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        source.onNext(1);// T: 0ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(2);// T: 250ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(3);// T: 500ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(4);// T: 750ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(5);// T: 1000ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onComplete();// T: 1250ms

        inOrder.verify(subscriber, Mockito.times(1)).onNext(4);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(5);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void takeLastTimedThrowingSource() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Object> source = PublishProcessor.create();
        Flowable<Object> result = source.takeLast(1, TimeUnit.SECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        source.onNext(1);// T: 0ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(2);// T: 250ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(3);// T: 500ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(4);// T: 750ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(5);// T: 1000ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onError(new TestException());// T: 1250ms

        inOrder.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void takeLastTimedWithZeroCapacity() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Object> source = PublishProcessor.create();
        Flowable<Object> result = source.takeLast(0, 1, TimeUnit.SECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        result.subscribe(subscriber);
        source.onNext(1);// T: 0ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(2);// T: 250ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(3);// T: 500ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(4);// T: 750ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onNext(5);// T: 1000ms

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS);
        source.onComplete();// T: 1250ms

        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testContinuousDelivery() {
        TestScheduler scheduler = new TestScheduler();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        PublishProcessor<Integer> pp = PublishProcessor.create();
        pp.takeLast(1000, TimeUnit.MILLISECONDS, scheduler).subscribe(ts);
        pp.onNext(1);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        pp.onNext(2);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        pp.onNext(3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        pp.onNext(4);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        pp.onComplete();
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValue(3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        ts.request(1);
        ts.assertValues(3, 4);
        ts.assertComplete();
        ts.assertNoErrors();
    }

    @Test
    public void takeLastTimeAndSize() {
        Flowable.just(1, 2).takeLast(1, 1, TimeUnit.MINUTES).test().assertResult(2);
    }

    @Test
    public void takeLastTime() {
        Flowable.just(1, 2).takeLast(1, TimeUnit.MINUTES).test().assertResult(1, 2);
    }

    @Test
    public void takeLastTimeDelayError() {
        Flowable.just(1, 2).concatWith(Flowable.<Integer>error(new TestException())).takeLast(1, TimeUnit.MINUTES, true).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void takeLastTimeDelayErrorCustomScheduler() {
        Flowable.just(1, 2).concatWith(Flowable.<Integer>error(new TestException())).takeLast(1, TimeUnit.MINUTES, Schedulers.io(), true).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishProcessor.create().takeLast(1, TimeUnit.MINUTES));
    }

    @Test
    public void observeOn() {
        Observable.range(1, 1000).takeLast(1, TimeUnit.DAYS).take(500).observeOn(Schedulers.single(), true, 1).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(500).assertNoErrors().assertComplete();
    }

    @Test
    public void cancelCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestSubscriber<Integer> ts = pp.takeLast(1, TimeUnit.DAYS).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
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
    public void emptyDelayError() {
        Flowable.empty().takeLast(1, TimeUnit.DAYS, true).test().assertResult();
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Publisher<Object>>() {
            @Override
            public io.reactivex.Publisher<Object> apply(Flowable<Object> f) throws Exception {
                return f.takeLast(1, TimeUnit.SECONDS);
            }
        });
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(PublishProcessor.create().takeLast(1, TimeUnit.SECONDS));
    }
}

