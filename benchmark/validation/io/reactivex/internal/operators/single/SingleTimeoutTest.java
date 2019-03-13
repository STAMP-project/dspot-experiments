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
package io.reactivex.internal.operators.single;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Action;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class SingleTimeoutTest {
    @Test
    public void shouldUnsubscribeFromUnderlyingSubscriptionOnDispose() {
        final PublishSubject<String> subject = PublishSubject.create();
        final TestScheduler scheduler = new TestScheduler();
        final TestObserver<String> observer = subject.single("").timeout(100, TimeUnit.MILLISECONDS, scheduler).test();
        Assert.assertTrue(subject.hasObservers());
        observer.dispose();
        Assert.assertFalse(subject.hasObservers());
    }

    @Test
    public void otherErrors() {
        Single.never().timeout(1, TimeUnit.MILLISECONDS, Single.error(new TestException())).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void mainSuccess() {
        Single.just(1).timeout(1, TimeUnit.DAYS).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void mainError() {
        Single.error(new TestException()).timeout(1, TimeUnit.DAYS).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void disposeWhenFallback() {
        TestScheduler sch = new TestScheduler();
        SingleSubject<Integer> subj = SingleSubject.create();
        subj.timeout(1, TimeUnit.SECONDS, sch, Single.just(1)).test(true).assertEmpty();
        Assert.assertFalse(subj.hasObservers());
    }

    @Test
    public void isDisposed() {
        TestHelper.checkDisposed(SingleSubject.create().timeout(1, TimeUnit.DAYS));
    }

    @Test
    public void fallbackDispose() {
        TestScheduler sch = new TestScheduler();
        SingleSubject<Integer> subj = SingleSubject.create();
        SingleSubject<Integer> fallback = SingleSubject.create();
        TestObserver<Integer> to = subj.timeout(1, TimeUnit.SECONDS, sch, fallback).test();
        Assert.assertFalse(fallback.hasObservers());
        sch.advanceTimeBy(1, TimeUnit.SECONDS);
        Assert.assertFalse(subj.hasObservers());
        Assert.assertTrue(fallback.hasObservers());
        to.cancel();
        Assert.assertFalse(fallback.hasObservers());
    }

    @Test
    public void normalSuccessDoesntDisposeMain() {
        final int[] calls = new int[]{ 0 };
        Single.just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (calls[0])++;
            }
        }).timeout(1, TimeUnit.DAYS).test().assertResult(1);
        Assert.assertEquals(0, calls[0]);
    }

    @Test
    public void successTimeoutRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final SingleSubject<Integer> subj = SingleSubject.create();
            SingleSubject<Integer> fallback = SingleSubject.create();
            final TestScheduler sch = new TestScheduler();
            TestObserver<Integer> to = subj.timeout(1, TimeUnit.MILLISECONDS, sch, fallback).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    subj.onSuccess(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sch.advanceTimeBy(1, TimeUnit.MILLISECONDS);
                }
            };
            TestHelper.race(r1, r2);
            if (!(fallback.hasObservers())) {
                to.assertResult(1);
            } else {
                to.assertEmpty();
            }
        }
    }

    @Test
    public void errorTimeoutRace() {
        final TestException ex = new TestException();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                final SingleSubject<Integer> subj = SingleSubject.create();
                SingleSubject<Integer> fallback = SingleSubject.create();
                final TestScheduler sch = new TestScheduler();
                TestObserver<Integer> to = subj.timeout(1, TimeUnit.MILLISECONDS, sch, fallback).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        subj.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        sch.advanceTimeBy(1, TimeUnit.MILLISECONDS);
                    }
                };
                TestHelper.race(r1, r2);
                if (!(fallback.hasObservers())) {
                    to.assertFailure(TestException.class);
                } else {
                    to.assertEmpty();
                }
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mainTimedOut() {
        Single.never().timeout(1, TimeUnit.NANOSECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(1, TimeUnit.NANOSECONDS));
    }
}

