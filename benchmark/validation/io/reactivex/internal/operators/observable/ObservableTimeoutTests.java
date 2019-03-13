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
package io.reactivex.internal.operators.observable;


import io.reactivex.Observable;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableTimeoutTests {
    private PublishSubject<String> underlyingSubject;

    private TestScheduler testScheduler;

    private Observable<String> withTimeout;

    private static final long TIMEOUT = 3;

    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    @Test
    public void shouldNotTimeoutIfOnNextWithinTimeout() {
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        withTimeout.subscribe(to);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        Mockito.verify(observer).onNext("One");
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        to.dispose();
    }

    @Test
    public void shouldNotTimeoutIfSecondOnNextWithinTimeout() {
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        withTimeout.subscribe(to);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("Two");
        Mockito.verify(observer).onNext("Two");
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        to.dispose();
    }

    @Test
    public void shouldTimeoutIfOnNextNotWithinTimeout() {
        TestObserver<String> observer = new TestObserver<String>();
        withTimeout.subscribe(observer);
        testScheduler.advanceTimeBy(((ObservableTimeoutTests.TIMEOUT) + 1), TimeUnit.SECONDS);
        observer.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(ObservableTimeoutTests.TIMEOUT, ObservableTimeoutTests.TIME_UNIT));
    }

    @Test
    public void shouldTimeoutIfSecondOnNextNotWithinTimeout() {
        TestObserver<String> observer = new TestObserver<String>();
        withTimeout.subscribe(observer);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        observer.assertValue("One");
        testScheduler.advanceTimeBy(((ObservableTimeoutTests.TIMEOUT) + 1), TimeUnit.SECONDS);
        observer.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(ObservableTimeoutTests.TIMEOUT, ObservableTimeoutTests.TIME_UNIT), "One");
    }

    @Test
    public void shouldCompleteIfUnderlyingComletes() {
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        withTimeout.subscribe(observer);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onComplete();
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(observer).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        to.dispose();
    }

    @Test
    public void shouldErrorIfUnderlyingErrors() {
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        withTimeout.subscribe(observer);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onError(new UnsupportedOperationException());
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        Mockito.verify(observer).onError(ArgumentMatchers.any(UnsupportedOperationException.class));
        to.dispose();
    }

    @Test
    public void shouldSwitchToOtherIfOnNextNotWithinTimeout() {
        Observable<String> other = Observable.just("a", "b", "c");
        Observable<String> source = underlyingSubject.timeout(ObservableTimeoutTests.TIMEOUT, ObservableTimeoutTests.TIME_UNIT, testScheduler, other);
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        source.subscribe(to);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onNext("Two");
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("One");
        inOrder.verify(observer, Mockito.times(1)).onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("c");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        to.dispose();
    }

    @Test
    public void shouldSwitchToOtherIfOnErrorNotWithinTimeout() {
        Observable<String> other = Observable.just("a", "b", "c");
        Observable<String> source = underlyingSubject.timeout(ObservableTimeoutTests.TIMEOUT, ObservableTimeoutTests.TIME_UNIT, testScheduler, other);
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        source.subscribe(to);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onError(new UnsupportedOperationException());
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("One");
        inOrder.verify(observer, Mockito.times(1)).onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("c");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        to.dispose();
    }

    @Test
    public void shouldSwitchToOtherIfOnCompletedNotWithinTimeout() {
        Observable<String> other = Observable.just("a", "b", "c");
        Observable<String> source = underlyingSubject.timeout(ObservableTimeoutTests.TIMEOUT, ObservableTimeoutTests.TIME_UNIT, testScheduler, other);
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        source.subscribe(to);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onComplete();
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("One");
        inOrder.verify(observer, Mockito.times(1)).onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("c");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        to.dispose();
    }

    @Test
    public void shouldSwitchToOtherAndCanBeUnsubscribedIfOnNextNotWithinTimeout() {
        PublishSubject<String> other = PublishSubject.create();
        Observable<String> source = underlyingSubject.timeout(ObservableTimeoutTests.TIMEOUT, ObservableTimeoutTests.TIME_UNIT, testScheduler, other);
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        source.subscribe(to);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        underlyingSubject.onNext("One");
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
        underlyingSubject.onNext("Two");
        other.onNext("a");
        other.onNext("b");
        to.dispose();
        // The following messages should not be delivered.
        other.onNext("c");
        other.onNext("d");
        other.onComplete();
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("One");
        inOrder.verify(observer, Mockito.times(1)).onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("b");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldTimeoutIfSynchronizedObservableEmitFirstOnNextNotWithinTimeout() throws InterruptedException {
        final CountDownLatch exit = new CountDownLatch(1);
        final CountDownLatch timeoutSetuped = new CountDownLatch(1);
        final TestObserver<String> observer = new TestObserver<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Observable.unsafeCreate(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> observer) {
                        observer.onSubscribe(Disposables.empty());
                        try {
                            timeoutSetuped.countDown();
                            exit.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        observer.onNext("a");
                        observer.onComplete();
                    }
                }).timeout(1, TimeUnit.SECONDS, testScheduler).subscribe(observer);
            }
        }).start();
        timeoutSetuped.await();
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        observer.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(1, TimeUnit.SECONDS));
        exit.countDown();// exit the thread

    }

    @Test
    public void shouldUnsubscribeFromUnderlyingSubscriptionOnTimeout() throws InterruptedException {
        // From https://github.com/ReactiveX/RxJava/pull/951
        final Disposable upstream = Mockito.mock(io.reactivex.disposables.Disposable.class);
        Observable<String> never = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(upstream);
            }
        });
        TestScheduler testScheduler = new TestScheduler();
        Observable<String> observableWithTimeout = never.timeout(1000, TimeUnit.MILLISECONDS, testScheduler);
        TestObserver<String> observer = new TestObserver<String>();
        observableWithTimeout.subscribe(observer);
        testScheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        observer.assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(1000, TimeUnit.MILLISECONDS));
        Mockito.verify(upstream, Mockito.times(1)).dispose();
    }

    @Test
    public void shouldUnsubscribeFromUnderlyingSubscriptionOnDispose() {
        final PublishSubject<String> subject = PublishSubject.create();
        final TestScheduler scheduler = new TestScheduler();
        final TestObserver<String> observer = subject.timeout(100, TimeUnit.MILLISECONDS, scheduler).test();
        Assert.assertTrue(subject.hasObservers());
        observer.dispose();
        Assert.assertFalse(subject.hasObservers());
    }

    @Test
    public void timedAndOther() {
        timeout(100, TimeUnit.MILLISECONDS, Observable.just(1)).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(timeout(1, TimeUnit.DAYS));
        TestHelper.checkDisposed(timeout(1, TimeUnit.DAYS, Observable.just(1)));
    }

    @Test
    public void timedErrorOther() {
        timeout(1, TimeUnit.DAYS, Observable.just(1)).test().assertFailure(TestException.class);
    }

    @Test
    public void timedError() {
        timeout(1, TimeUnit.DAYS).test().assertFailure(TestException.class);
    }

    @Test
    public void timedEmptyOther() {
        timeout(1, TimeUnit.DAYS, Observable.just(1)).test().assertResult();
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
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onComplete();
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.timeout(1, TimeUnit.DAYS, Observable.just(3)).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void timedTake() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.timeout(1, TimeUnit.DAYS).take(1).test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        Assert.assertFalse(ps.hasObservers());
        to.assertResult(1);
    }

    @Test
    public void timedFallbackTake() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.timeout(1, TimeUnit.DAYS, Observable.just(2)).take(1).test();
        Assert.assertTrue(ps.hasObservers());
        ps.onNext(1);
        Assert.assertFalse(ps.hasObservers());
        to.assertResult(1);
    }

    @Test
    public void fallbackErrors() {
        timeout(1, TimeUnit.MILLISECONDS, Observable.error(new TestException())).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void onNextOnTimeoutRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestScheduler sch = new TestScheduler();
            final PublishSubject<Integer> ps = PublishSubject.create();
            TestObserver<Integer> to = ps.timeout(1, TimeUnit.SECONDS, sch).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sch.advanceTimeBy(1, TimeUnit.SECONDS);
                }
            };
            TestHelper.race(r1, r2);
            if ((to.valueCount()) != 0) {
                if ((to.errorCount()) != 0) {
                    to.assertFailure(TimeoutException.class, 1);
                    to.assertErrorMessage(ExceptionHelper.timeoutMessage(1, TimeUnit.SECONDS));
                } else {
                    to.assertValuesOnly(1);
                }
            } else {
                to.assertFailure(TimeoutException.class);
                to.assertErrorMessage(ExceptionHelper.timeoutMessage(1, TimeUnit.SECONDS));
            }
        }
    }

    @Test
    public void onNextOnTimeoutRaceFallback() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestScheduler sch = new TestScheduler();
            final PublishSubject<Integer> ps = PublishSubject.create();
            TestObserver<Integer> to = ps.timeout(1, TimeUnit.SECONDS, sch, Observable.just(2)).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sch.advanceTimeBy(1, TimeUnit.SECONDS);
                }
            };
            TestHelper.race(r1, r2);
            if (to.isTerminated()) {
                int c = to.valueCount();
                if (c == 1) {
                    int v = to.values().get(0);
                    Assert.assertTrue(("" + v), ((v == 1) || (v == 2)));
                } else {
                    to.assertResult(1, 2);
                }
            } else {
                to.assertValuesOnly(1);
            }
        }
    }
}

