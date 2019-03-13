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


import io.reactivex.Completable;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.CompletableSubject;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class CompletableDelaySubscriptionTest {
    @Test
    public void normal() {
        final AtomicInteger counter = new AtomicInteger();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        }).delaySubscription(100, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void error() {
        final AtomicInteger counter = new AtomicInteger();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
                throw new TestException();
            }
        }).delaySubscription(100, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void disposeBeforeTime() {
        TestScheduler scheduler = new TestScheduler();
        final AtomicInteger counter = new AtomicInteger();
        Completable result = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        }).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        TestObserver<Void> to = result.test();
        to.assertEmpty();
        scheduler.advanceTimeBy(90, TimeUnit.MILLISECONDS);
        to.dispose();
        scheduler.advanceTimeBy(15, TimeUnit.MILLISECONDS);
        to.assertEmpty();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void timestep() {
        TestScheduler scheduler = new TestScheduler();
        final AtomicInteger counter = new AtomicInteger();
        Completable result = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        }).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        TestObserver<Void> to = result.test();
        scheduler.advanceTimeBy(90, TimeUnit.MILLISECONDS);
        to.assertEmpty();
        scheduler.advanceTimeBy(15, TimeUnit.MILLISECONDS);
        to.assertResult();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void timestepError() {
        TestScheduler scheduler = new TestScheduler();
        final AtomicInteger counter = new AtomicInteger();
        Completable result = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
                throw new TestException();
            }
        }).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        TestObserver<Void> to = result.test();
        scheduler.advanceTimeBy(90, TimeUnit.MILLISECONDS);
        to.assertEmpty();
        scheduler.advanceTimeBy(15, TimeUnit.MILLISECONDS);
        to.assertFailure(TestException.class);
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void disposeMain() {
        CompletableSubject cs = CompletableSubject.create();
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Void> to = cs.delaySubscription(1, TimeUnit.SECONDS, scheduler).test();
        Assert.assertFalse(cs.hasObservers());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Assert.assertTrue(cs.hasObservers());
        to.dispose();
        Assert.assertFalse(cs.hasObservers());
    }
}

