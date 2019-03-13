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


import io.reactivex.Flowable;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableTimeoutTests {
    private PublishProcessor<String> underlyingSubject;

    private TestScheduler testScheduler;

    private Flowable<String> withTimeout;

    private static final long TIMEOUT = 3;

    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    @Test
    public void shouldNotTimeoutIfOnNextWithinTimeout() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        withTimeout.subscribe(ts);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        Mockito.verify(subscriber).onNext("One");
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        ts.cancel();
    }

    @Test
    public void shouldNotTimeoutIfSecondOnNextWithinTimeout() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        withTimeout.subscribe(ts);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("Two");
        Mockito.verify(subscriber).onNext("Two");
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        ts.dispose();
    }

    @Test
    public void shouldTimeoutIfOnNextNotWithinTimeout() {
        TestSubscriber<String> subscriber = new TestSubscriber<String>();
        withTimeout.subscribe(subscriber);
        testScheduler.advanceTimeBy(((FlowableTimeoutTests.TIMEOUT) + 1), TimeUnit.SECONDS);
        subscriber.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(FlowableTimeoutTests.TIMEOUT, FlowableTimeoutTests.TIME_UNIT));
    }

    @Test
    public void shouldTimeoutIfSecondOnNextNotWithinTimeout() {
        TestSubscriber<String> subscriber = new TestSubscriber<String>();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        withTimeout.subscribe(subscriber);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        subscriber.assertValue("One");
        testScheduler.advanceTimeBy(((FlowableTimeoutTests.TIMEOUT) + 1), TimeUnit.SECONDS);
        subscriber.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(FlowableTimeoutTests.TIMEOUT, FlowableTimeoutTests.TIME_UNIT), "One");
        ts.dispose();
    }

    @Test
    public void shouldCompleteIfUnderlyingComletes() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        withTimeout.subscribe(subscriber);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onComplete();
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        ts.dispose();
    }

    @Test
    public void shouldErrorIfUnderlyingErrors() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        withTimeout.subscribe(subscriber);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onError(new UnsupportedOperationException());
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(UnsupportedOperationException.class));
        ts.dispose();
    }

    @Test
    public void shouldSwitchToOtherIfOnNextNotWithinTimeout() {
        Flowable<String> other = Flowable.just("a", "b", "c");
        Flowable<String> source = underlyingSubject.timeout(FlowableTimeoutTests.TIMEOUT, FlowableTimeoutTests.TIME_UNIT, testScheduler, other);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        source.subscribe(ts);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onNext("Two");
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("One");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("a");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("b");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("c");
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        ts.dispose();
    }

    @Test
    public void shouldSwitchToOtherIfOnErrorNotWithinTimeout() {
        Flowable<String> other = Flowable.just("a", "b", "c");
        Flowable<String> source = underlyingSubject.timeout(FlowableTimeoutTests.TIMEOUT, FlowableTimeoutTests.TIME_UNIT, testScheduler, other);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        source.subscribe(ts);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onError(new UnsupportedOperationException());
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("One");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("a");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("b");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("c");
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        ts.dispose();
    }

    @Test
    public void shouldSwitchToOtherIfOnCompletedNotWithinTimeout() {
        Flowable<String> other = Flowable.just("a", "b", "c");
        Flowable<String> source = underlyingSubject.timeout(FlowableTimeoutTests.TIMEOUT, FlowableTimeoutTests.TIME_UNIT, testScheduler, other);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        source.subscribe(ts);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onComplete();
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("One");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("a");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("b");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("c");
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        ts.dispose();
    }

    @Test
    public void shouldSwitchToOtherAndCanBeUnsubscribedIfOnNextNotWithinTimeout() {
        PublishProcessor<String> other = PublishProcessor.create();
        Flowable<String> source = underlyingSubject.timeout(FlowableTimeoutTests.TIMEOUT, FlowableTimeoutTests.TIME_UNIT, testScheduler, other);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        source.subscribe(ts);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onNext("Two");
        other.onNext("a");
        other.onNext("b");
        ts.dispose();
        // The following messages should not be delivered.
        other.onNext("c");
        other.onNext("d");
        other.onComplete();
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("One");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("a");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("b");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldTimeoutIfSynchronizedFlowableEmitFirstOnNextNotWithinTimeout() throws InterruptedException {
        final CountDownLatch exit = new CountDownLatch(1);
        final CountDownLatch timeoutSetuped = new CountDownLatch(1);
        final TestSubscriber<String> subscriber = new TestSubscriber<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        try {
                            timeoutSetuped.countDown();
                            exit.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        subscriber.onNext("a");
                        subscriber.onComplete();
                    }
                }).timeout(1, TimeUnit.SECONDS, testScheduler).subscribe(subscriber);
            }
        }).start();
        timeoutSetuped.await();
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        subscriber.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(1, TimeUnit.SECONDS));
        exit.countDown();// exit the thread

    }

    @Test
    public void shouldUnsubscribeFromUnderlyingSubscriptionOnTimeout() throws InterruptedException {
        // From https://github.com/ReactiveX/RxJava/pull/951
        final Subscription s = Mockito.mock(Subscription.class);
        Flowable<String> never = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(s);
            }
        });
        TestScheduler testScheduler = new TestScheduler();
        Flowable<String> observableWithTimeout = never.timeout(1000, TimeUnit.MILLISECONDS, testScheduler);
        TestSubscriber<String> subscriber = new TestSubscriber<String>();
        observableWithTimeout.subscribe(subscriber);
        testScheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        subscriber.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(1000, TimeUnit.MILLISECONDS));
        Mockito.verify(s, Mockito.times(1)).cancel();
    }

    @Test
    public void shouldUnsubscribeFromUnderlyingSubscriptionOnDispose() {
        final PublishProcessor<String> processor = PublishProcessor.create();
        final TestScheduler scheduler = new TestScheduler();
        final TestSubscriber<String> subscriber = processor.timeout(100, TimeUnit.MILLISECONDS, scheduler).test();
        Assert.assertTrue(processor.hasSubscribers());
        subscriber.dispose();
        Assert.assertFalse(processor.hasSubscribers());
    }

    @Test
    public void timedAndOther() {
        timeout(100, TimeUnit.MILLISECONDS, Flowable.just(1)).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(timeout(1, TimeUnit.DAYS));
        TestHelper.checkDisposed(timeout(1, TimeUnit.DAYS, Flowable.just(1)));
    }

    @Test
    public void timedErrorOther() {
        timeout(1, TimeUnit.DAYS, Flowable.just(1)).test().assertFailure(TestException.class);
    }

    @Test
    public void timedError() {
        timeout(1, TimeUnit.DAYS).test().assertFailure(TestException.class);
    }

    @Test
    public void timedEmptyOther() {
        timeout(1, TimeUnit.DAYS, Flowable.just(1)).test().assertResult();
    }

    @Test
    public void timedEmpty() {
        timeout(1, TimeUnit.DAYS).test().assertResult();
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            timeout(1, TimeUnit.DAYS).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSourceOther() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onNext(1);
                    subscriber.onComplete();
                    subscriber.onNext(2);
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                }
            }.timeout(1, TimeUnit.DAYS, Flowable.just(3)).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void timedTake() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.timeout(1, TimeUnit.DAYS).take(1).test();
        Assert.assertTrue(pp.hasSubscribers());
        pp.onNext(1);
        Assert.assertFalse(pp.hasSubscribers());
        ts.assertResult(1);
    }

    @Test
    public void timedFallbackTake() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.timeout(1, TimeUnit.DAYS, Flowable.just(2)).take(1).test();
        Assert.assertTrue(pp.hasSubscribers());
        pp.onNext(1);
        Assert.assertFalse(pp.hasSubscribers());
        ts.assertResult(1);
    }

    @Test
    public void fallbackErrors() {
        timeout(1, TimeUnit.MILLISECONDS, Flowable.error(new TestException())).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void onNextOnTimeoutRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestScheduler sch = new TestScheduler();
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.timeout(1, TimeUnit.SECONDS, sch).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sch.advanceTimeBy(1, TimeUnit.SECONDS);
                }
            };
            TestHelper.race(r1, r2);
            if ((ts.valueCount()) != 0) {
                if ((ts.errorCount()) != 0) {
                    ts.assertFailure(TimeoutException.class, 1);
                    ts.assertErrorMessage(ExceptionHelper.timeoutMessage(1, TimeUnit.SECONDS));
                } else {
                    ts.assertValuesOnly(1);
                }
            } else {
                ts.assertFailure(TimeoutException.class);
                ts.assertErrorMessage(ExceptionHelper.timeoutMessage(1, TimeUnit.SECONDS));
            }
        }
    }

    @Test
    public void onNextOnTimeoutRaceFallback() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestScheduler sch = new TestScheduler();
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.timeout(1, TimeUnit.SECONDS, sch, Flowable.just(2)).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sch.advanceTimeBy(1, TimeUnit.SECONDS);
                }
            };
            TestHelper.race(r1, r2);
            if (ts.isTerminated()) {
                int c = ts.valueCount();
                if (c == 1) {
                    int v = ts.values().get(0);
                    Assert.assertTrue(("" + v), ((v == 1) || (v == 2)));
                } else {
                    ts.assertResult(1, 2);
                }
            } else {
                ts.assertValuesOnly(1);
            }
        }
    }
}

