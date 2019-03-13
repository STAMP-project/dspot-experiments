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
package io.reactivex.internal.operators.completable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Action;
import io.reactivex.internal.operators.completable.CompletableTimeout.TimeOutObserver;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class CompletableTimeoutTest {
    @Test
    public void timeoutException() throws Exception {
        Completable.never().timeout(100, TimeUnit.MILLISECONDS, Schedulers.io()).test().awaitDone(5, TimeUnit.SECONDS).assertFailureAndMessage(TimeoutException.class, ExceptionHelper.timeoutMessage(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void timeoutContinueOther() throws Exception {
        final int[] call = new int[]{ 0 };
        Completable other = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                (call[0])++;
            }
        });
        Completable.never().timeout(100, TimeUnit.MILLISECONDS, Schedulers.io(), other).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
        Assert.assertEquals(1, call[0]);
    }

    @Test
    public void shouldUnsubscribeFromUnderlyingSubscriptionOnDispose() {
        final PublishSubject<String> subject = PublishSubject.create();
        final TestScheduler scheduler = new TestScheduler();
        final TestObserver<Void> observer = subject.ignoreElements().timeout(100, TimeUnit.MILLISECONDS, scheduler).test();
        Assert.assertTrue(subject.hasObservers());
        observer.dispose();
        Assert.assertFalse(subject.hasObservers());
    }

    @Test
    public void otherErrors() {
        Completable.never().timeout(1, TimeUnit.MILLISECONDS, Completable.error(new TestException())).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void mainSuccess() {
        Completable.complete().timeout(1, TimeUnit.DAYS).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }

    @Test
    public void mainError() {
        Completable.error(new TestException()).timeout(1, TimeUnit.DAYS).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void errorTimeoutRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final TestScheduler scheduler = new TestScheduler();
                final PublishSubject<Integer> ps = PublishSubject.create();
                TestObserver<Void> to = ps.ignoreElements().timeout(1, TimeUnit.MILLISECONDS, scheduler, Completable.complete()).test();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        scheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
                    }
                };
                TestHelper.race(r1, r2);
                to.assertTerminated();
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void ambRace() {
        TestObserver<Void> to = new TestObserver<Void>();
        to.onSubscribe(Disposables.empty());
        CompositeDisposable cd = new CompositeDisposable();
        AtomicBoolean once = new AtomicBoolean();
        TimeOutObserver a = new TimeOutObserver(cd, once, to);
        a.onComplete();
        a.onComplete();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            a.onError(new TestException());
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

