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
package io.reactivex.schedulers;


import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.internal.util.ExceptionHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TestSchedulerTest {
    // mocking is unchecked, unfortunately
    @SuppressWarnings("unchecked")
    @Test
    public final void testPeriodicScheduling() throws Exception {
        final Function<Long, Void> calledOp = Mockito.mock(Function.class);
        final TestScheduler scheduler = new TestScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            inner.schedulePeriodically(new Runnable() {
                @Override
                public void run() {
                    System.out.println(scheduler.now(TimeUnit.MILLISECONDS));
                    try {
                        calledOp.apply(scheduler.now(TimeUnit.MILLISECONDS));
                    } catch (Throwable ex) {
                        ExceptionHelper.wrapOrThrow(ex);
                    }
                }
            }, 1, 2, TimeUnit.SECONDS);
            Mockito.verify(calledOp, Mockito.never()).apply(ArgumentMatchers.anyLong());
            InOrder inOrder = Mockito.inOrder(calledOp);
            scheduler.advanceTimeBy(999L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.never()).apply(ArgumentMatchers.anyLong());
            scheduler.advanceTimeBy(1L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).apply(1000L);
            scheduler.advanceTimeBy(1999L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.never()).apply(3000L);
            scheduler.advanceTimeBy(1L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).apply(3000L);
            scheduler.advanceTimeBy(5L, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).apply(5000L);
            inOrder.verify(calledOp, Mockito.times(1)).apply(7000L);
            inner.dispose();
            scheduler.advanceTimeBy(11L, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.never()).apply(ArgumentMatchers.anyLong());
        } finally {
            inner.dispose();
        }
    }

    // mocking is unchecked, unfortunately
    @SuppressWarnings("unchecked")
    @Test
    public final void testPeriodicSchedulingUnsubscription() throws Exception {
        final Function<Long, Void> calledOp = Mockito.mock(Function.class);
        final TestScheduler scheduler = new TestScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final Disposable subscription = inner.schedulePeriodically(new Runnable() {
                @Override
                public void run() {
                    System.out.println(scheduler.now(TimeUnit.MILLISECONDS));
                    try {
                        calledOp.apply(scheduler.now(TimeUnit.MILLISECONDS));
                    } catch (Throwable ex) {
                        ExceptionHelper.wrapOrThrow(ex);
                    }
                }
            }, 1, 2, TimeUnit.SECONDS);
            Mockito.verify(calledOp, Mockito.never()).apply(ArgumentMatchers.anyLong());
            InOrder inOrder = Mockito.inOrder(calledOp);
            scheduler.advanceTimeBy(999L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.never()).apply(ArgumentMatchers.anyLong());
            scheduler.advanceTimeBy(1L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).apply(1000L);
            scheduler.advanceTimeBy(1999L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.never()).apply(3000L);
            scheduler.advanceTimeBy(1L, TimeUnit.MILLISECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).apply(3000L);
            scheduler.advanceTimeBy(5L, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).apply(5000L);
            inOrder.verify(calledOp, Mockito.times(1)).apply(7000L);
            subscription.dispose();
            scheduler.advanceTimeBy(11L, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.never()).apply(ArgumentMatchers.anyLong());
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testImmediateUnsubscribes() {
        TestScheduler s = new TestScheduler();
        final Scheduler.Worker inner = s.createWorker();
        final AtomicInteger counter = new AtomicInteger(0);
        try {
            inner.schedule(new Runnable() {
                @Override
                public void run() {
                    counter.incrementAndGet();
                    System.out.println(("counter: " + (counter.get())));
                    inner.schedule(this);
                }
            });
            inner.dispose();
            Assert.assertEquals(0, counter.get());
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testImmediateUnsubscribes2() {
        TestScheduler s = new TestScheduler();
        final Scheduler.Worker inner = s.createWorker();
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            final Disposable subscription = inner.schedule(new Runnable() {
                @Override
                public void run() {
                    counter.incrementAndGet();
                    System.out.println(("counter: " + (counter.get())));
                    inner.schedule(this);
                }
            });
            subscription.dispose();
            Assert.assertEquals(0, counter.get());
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testNestedSchedule() {
        final TestScheduler scheduler = new TestScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final Runnable calledOp = Mockito.mock(Runnable.class);
            Flowable<Object> poller;
            poller = Flowable.unsafeCreate(new Publisher<Object>() {
                @Override
                public void subscribe(final Subscriber<? super Object> aSubscriber) {
                    final BooleanSubscription bs = new BooleanSubscription();
                    aSubscriber.onSubscribe(bs);
                    inner.schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (!(bs.isCancelled())) {
                                calledOp.run();
                                inner.schedule(this, 5, TimeUnit.SECONDS);
                            }
                        }
                    });
                }
            });
            InOrder inOrder = Mockito.inOrder(calledOp);
            Disposable sub;
            sub = poller.subscribe();
            scheduler.advanceTimeTo(6, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.times(2)).run();
            sub.dispose();
            scheduler.advanceTimeTo(11, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.never()).run();
            sub = poller.subscribe();
            scheduler.advanceTimeTo(12, TimeUnit.SECONDS);
            inOrder.verify(calledOp, Mockito.times(1)).run();
        } finally {
            inner.dispose();
        }
    }

    @Test
    public void timedRunnableToString() {
        TimedRunnable r = new TimedRunnable(((TestWorker) (new TestScheduler().createWorker())), 5, new Runnable() {
            @Override
            public void run() {
                // deliberately no-op
            }

            @Override
            public String toString() {
                return "Runnable";
            }
        }, 1);
        Assert.assertEquals("TimedRunnable(time = 5, run = Runnable)", r.toString());
    }

    @Test
    public void workerDisposed() {
        TestScheduler scheduler = new TestScheduler();
        Worker w = scheduler.createWorker();
        w.dispose();
        Assert.assertTrue(w.isDisposed());
    }

    @Test
    public void constructorTimeSetsTime() {
        TestScheduler ts = new TestScheduler(5, TimeUnit.SECONDS);
        Assert.assertEquals(5, ts.now(TimeUnit.SECONDS));
        Assert.assertEquals(5000, ts.now(TimeUnit.MILLISECONDS));
    }
}

