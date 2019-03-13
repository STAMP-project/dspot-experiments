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
package io.reactivex.internal.schedulers;


import SchedulerWhen.SUBSCRIBED;
import io.reactivex.Flowable;
import io.reactivex.Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class SchedulerWhenTest {
    @Test
    public void testAsyncMaxConcurrent() {
        TestScheduler tSched = new TestScheduler();
        SchedulerWhen sched = maxConcurrentScheduler(tSched);
        TestSubscriber<Long> tSub = TestSubscriber.create();
        asyncWork(sched).subscribe(tSub);
        tSub.assertValueCount(0);
        tSched.advanceTimeBy(0, TimeUnit.SECONDS);
        tSub.assertValueCount(0);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(2);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(4);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(5);
        tSub.assertComplete();
        sched.dispose();
    }

    @Test
    public void testAsyncDelaySubscription() {
        final TestScheduler tSched = new TestScheduler();
        SchedulerWhen sched = throttleScheduler(tSched);
        TestSubscriber<Long> tSub = TestSubscriber.create();
        asyncWork(sched).subscribe(tSub);
        tSub.assertValueCount(0);
        tSched.advanceTimeBy(0, TimeUnit.SECONDS);
        tSub.assertValueCount(0);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(1);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(1);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(2);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(2);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(3);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(3);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(4);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(4);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(5);
        tSub.assertComplete();
        sched.dispose();
    }

    @Test
    public void testSyncMaxConcurrent() {
        TestScheduler tSched = new TestScheduler();
        SchedulerWhen sched = maxConcurrentScheduler(tSched);
        TestSubscriber<Long> tSub = TestSubscriber.create();
        syncWork(sched).subscribe(tSub);
        tSub.assertValueCount(0);
        tSched.advanceTimeBy(0, TimeUnit.SECONDS);
        // since all the work is synchronous nothing is blocked and its all done
        tSub.assertValueCount(5);
        tSub.assertComplete();
        sched.dispose();
    }

    @Test
    public void testSyncDelaySubscription() {
        final TestScheduler tSched = new TestScheduler();
        SchedulerWhen sched = throttleScheduler(tSched);
        TestSubscriber<Long> tSub = TestSubscriber.create();
        syncWork(sched).subscribe(tSub);
        tSub.assertValueCount(0);
        tSched.advanceTimeBy(0, TimeUnit.SECONDS);
        tSub.assertValueCount(1);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(2);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(3);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(4);
        tSched.advanceTimeBy(1, TimeUnit.SECONDS);
        tSub.assertValueCount(5);
        tSub.assertComplete();
        sched.dispose();
    }

    @Test(timeout = 1000)
    public void testRaceConditions() {
        Scheduler comp = Schedulers.computation();
        Scheduler limited = comp.when(new io.reactivex.functions.Function<Flowable.Flowable<Flowable.Flowable<Completable>>, Completable>() {
            @Override
            public io.reactivex.Completable apply(Flowable.Flowable<Flowable.Flowable<Completable>> t) {
                return Completable.merge(Flowable.Flowable.merge(t, 10));
            }
        });
        merge(just(just(1).subscribeOn(limited).observeOn(comp)).repeat(1000)).blockingSubscribe();
    }

    @Test
    public void subscribedDisposable() {
        SUBSCRIBED.dispose();
        Assert.assertFalse(SUBSCRIBED.isDisposed());
    }

    @Test(expected = TestException.class)
    public void combineCrashInConstructor() {
        new SchedulerWhen(new io.reactivex.functions.Function<Flowable.Flowable<Flowable.Flowable<Completable>>, Completable>() {
            @Override
            public io.reactivex.Completable apply(Flowable.Flowable<Flowable.Flowable<Completable>> v) throws Exception {
                throw new TestException();
            }
        }, Schedulers.single());
    }

    @Test
    public void disposed() {
        SchedulerWhen sw = new SchedulerWhen(new io.reactivex.functions.Function<Flowable.Flowable<Flowable.Flowable<Completable>>, Completable>() {
            @Override
            public io.reactivex.Completable apply(Flowable.Flowable<Flowable.Flowable<Completable>> v) throws Exception {
                return Completable.never();
            }
        }, Schedulers.single());
        Assert.assertFalse(sw.isDisposed());
        sw.dispose();
        Assert.assertTrue(sw.isDisposed());
    }

    @Test
    public void scheduledActiondisposedSetRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final ScheduledAction sa = new ScheduledAction() {
                private static final long serialVersionUID = -672980251643733156L;

                @Override
                protected Disposable callActual(Worker actualWorker, CompletableObserver actionCompletable) {
                    return Disposables.empty();
                }
            };
            Assert.assertFalse(sa.isDisposed());
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    sa.dispose();
                }
            };
            TestHelper.race(r1, r1);
            Assert.assertTrue(sa.isDisposed());
        }
    }

    @Test
    public void scheduledActionStates() {
        final AtomicInteger count = new AtomicInteger();
        ScheduledAction sa = new ScheduledAction() {
            private static final long serialVersionUID = -672980251643733156L;

            @Override
            protected Disposable callActual(Worker actualWorker, CompletableObserver actionCompletable) {
                count.incrementAndGet();
                return Disposables.empty();
            }
        };
        Assert.assertFalse(sa.isDisposed());
        sa.dispose();
        Assert.assertTrue(sa.isDisposed());
        sa.dispose();
        Assert.assertTrue(sa.isDisposed());
        // should not run when disposed
        sa.call(Schedulers.single().createWorker(), null);
        Assert.assertEquals(0, count.get());
        // should not run when already scheduled
        sa.set(Disposables.empty());
        sa.call(Schedulers.single().createWorker(), null);
        Assert.assertEquals(0, count.get());
        // disposed while in call
        sa = new ScheduledAction() {
            private static final long serialVersionUID = -672980251643733156L;

            @Override
            protected Disposable callActual(Worker actualWorker, CompletableObserver actionCompletable) {
                count.incrementAndGet();
                dispose();
                return Disposables.empty();
            }
        };
        sa.call(Schedulers.single().createWorker(), null);
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void onCompleteActionRunCrash() {
        final AtomicInteger count = new AtomicInteger();
        OnCompletedAction a = new OnCompletedAction(new Runnable() {
            @Override
            public void run() {
                throw new TestException();
            }
        }, new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                count.incrementAndGet();
            }

            @Override
            public void onError(Throwable e) {
                count.decrementAndGet();
                e.printStackTrace();
            }
        });
        try {
            a.run();
            Assert.fail("Should have thrown");
        } catch (TestException expected) {
        }
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void queueWorkerDispose() {
        QueueWorker qw = new QueueWorker(PublishProcessor.<ScheduledAction>create(), Schedulers.single().createWorker());
        Assert.assertFalse(qw.isDisposed());
        qw.dispose();
        Assert.assertTrue(qw.isDisposed());
        qw.dispose();
        Assert.assertTrue(qw.isDisposed());
    }
}

