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


import Functions.EMPTY_RUNNABLE;
import Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.schedulers.TrampolineScheduler;
import io.reactivex.subscribers.DefaultSubscriber;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Base tests for all schedulers including Immediate/Current.
 */
public abstract class AbstractSchedulerTests {
    @Test
    public void testNestedActions() throws InterruptedException {
        Scheduler scheduler = getScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final Runnable firstStepStart = Mockito.mock(Runnable.class);
            final Runnable firstStepEnd = Mockito.mock(Runnable.class);
            final Runnable secondStepStart = Mockito.mock(Runnable.class);
            final Runnable secondStepEnd = Mockito.mock(Runnable.class);
            final Runnable thirdStepStart = Mockito.mock(Runnable.class);
            final Runnable thirdStepEnd = Mockito.mock(Runnable.class);
            final Runnable firstAction = new Runnable() {
                @Override
                public void run() {
                    firstStepStart.run();
                    firstStepEnd.run();
                    latch.countDown();
                }
            };
            final Runnable secondAction = new Runnable() {
                @Override
                public void run() {
                    secondStepStart.run();
                    inner.schedule(firstAction);
                    secondStepEnd.run();
                }
            };
            final Runnable thirdAction = new Runnable() {
                @Override
                public void run() {
                    thirdStepStart.run();
                    inner.schedule(secondAction);
                    thirdStepEnd.run();
                }
            };
            InOrder inOrder = Mockito.inOrder(firstStepStart, firstStepEnd, secondStepStart, secondStepEnd, thirdStepStart, thirdStepEnd);
            inner.schedule(thirdAction);
            latch.await();
            inOrder.verify(thirdStepStart, Mockito.times(1)).run();
            inOrder.verify(thirdStepEnd, Mockito.times(1)).run();
            inOrder.verify(secondStepStart, Mockito.times(1)).run();
            inOrder.verify(secondStepEnd, Mockito.times(1)).run();
            inOrder.verify(firstStepStart, Mockito.times(1)).run();
            inOrder.verify(firstStepEnd, Mockito.times(1)).run();
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testNestedScheduling() {
        Flowable<Integer> ids = Flowable.fromIterable(Arrays.asList(1, 2)).subscribeOn(getScheduler());
        Flowable<String> m = ids.flatMap(new Function<Integer, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(Integer id) {
                return Flowable.fromIterable(Arrays.asList(("a-" + id), ("b-" + id))).subscribeOn(getScheduler()).map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return "names=>" + s;
                    }
                });
            }
        });
        List<String> strings = m.toList().blockingGet();
        Assert.assertEquals(4, strings.size());
        // because flatMap does a merge there is no guarantee of order
        Assert.assertTrue(strings.contains("names=>a-1"));
        Assert.assertTrue(strings.contains("names=>a-2"));
        Assert.assertTrue(strings.contains("names=>b-1"));
        Assert.assertTrue(strings.contains("names=>b-2"));
    }

    /**
     * The order of execution is nondeterministic.
     *
     * @throws InterruptedException
     * 		if the await is interrupted
     */
    @SuppressWarnings("rawtypes")
    @Test
    public final void testSequenceOfActions() throws InterruptedException {
        final Scheduler scheduler = getScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            final Runnable first = Mockito.mock(Runnable.class);
            final Runnable second = Mockito.mock(Runnable.class);
            // make it wait until both the first and second are called
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    try {
                        return invocation.getMock();
                    } finally {
                        latch.countDown();
                    }
                }
            }).when(first).run();
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    try {
                        return invocation.getMock();
                    } finally {
                        latch.countDown();
                    }
                }
            }).when(second).run();
            inner.schedule(first);
            inner.schedule(second);
            latch.await();
            Mockito.verify(first, Mockito.times(1)).run();
            Mockito.verify(second, Mockito.times(1)).run();
        } finally {
            inner.dispose();
        }
    }

    @Test
    public void testSequenceOfDelayedActions() throws InterruptedException {
        Scheduler scheduler = getScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final Runnable first = Mockito.mock(Runnable.class);
            final Runnable second = Mockito.mock(Runnable.class);
            inner.schedule(new Runnable() {
                @Override
                public void run() {
                    inner.schedule(first, 30, TimeUnit.MILLISECONDS);
                    inner.schedule(second, 10, TimeUnit.MILLISECONDS);
                    inner.schedule(new Runnable() {
                        @Override
                        public void run() {
                            latch.countDown();
                        }
                    }, 40, TimeUnit.MILLISECONDS);
                }
            });
            latch.await();
            InOrder inOrder = Mockito.inOrder(first, second);
            inOrder.verify(second, Mockito.times(1)).run();
            inOrder.verify(first, Mockito.times(1)).run();
        } finally {
            inner.dispose();
        }
    }

    @Test
    public void testMixOfDelayedAndNonDelayedActions() throws InterruptedException {
        Scheduler scheduler = getScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final Runnable first = Mockito.mock(Runnable.class);
            final Runnable second = Mockito.mock(Runnable.class);
            final Runnable third = Mockito.mock(Runnable.class);
            final Runnable fourth = Mockito.mock(Runnable.class);
            inner.schedule(new Runnable() {
                @Override
                public void run() {
                    inner.schedule(first);
                    inner.schedule(second, 300, TimeUnit.MILLISECONDS);
                    inner.schedule(third, 100, TimeUnit.MILLISECONDS);
                    inner.schedule(fourth);
                    inner.schedule(new Runnable() {
                        @Override
                        public void run() {
                            latch.countDown();
                        }
                    }, 400, TimeUnit.MILLISECONDS);
                }
            });
            latch.await();
            InOrder inOrder = Mockito.inOrder(first, second, third, fourth);
            inOrder.verify(first, Mockito.times(1)).run();
            inOrder.verify(fourth, Mockito.times(1)).run();
            inOrder.verify(third, Mockito.times(1)).run();
            inOrder.verify(second, Mockito.times(1)).run();
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testRecursiveExecution() throws InterruptedException {
        final Scheduler scheduler = getScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final AtomicInteger i = new AtomicInteger();
            final CountDownLatch latch = new CountDownLatch(1);
            inner.schedule(new Runnable() {
                @Override
                public void run() {
                    if ((i.incrementAndGet()) < 100) {
                        inner.schedule(this);
                    } else {
                        latch.countDown();
                    }
                }
            });
            latch.await();
            Assert.assertEquals(100, i.get());
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testRecursiveExecutionWithDelayTime() throws InterruptedException {
        Scheduler scheduler = getScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();
        try {
            final AtomicInteger i = new AtomicInteger();
            final CountDownLatch latch = new CountDownLatch(1);
            inner.schedule(new Runnable() {
                int state;

                @Override
                public void run() {
                    i.set(state);
                    if (((state)++) < 100) {
                        inner.schedule(this, 1, TimeUnit.MILLISECONDS);
                    } else {
                        latch.countDown();
                    }
                }
            });
            latch.await();
            Assert.assertEquals(100, i.get());
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testRecursiveSchedulerInObservable() {
        Flowable<Integer> obs = Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(final Subscriber<? super Integer> subscriber) {
                final Scheduler.Worker inner = getScheduler().createWorker();
                AsyncSubscription as = new AsyncSubscription();
                subscriber.onSubscribe(as);
                as.setResource(inner);
                inner.schedule(new Runnable() {
                    int i;

                    @Override
                    public void run() {
                        if ((i) > 42) {
                            try {
                                subscriber.onComplete();
                            } finally {
                                inner.dispose();
                            }
                            return;
                        }
                        subscriber.onNext(((i)++));
                        inner.schedule(this);
                    }
                });
            }
        });
        final AtomicInteger lastValue = new AtomicInteger();
        obs.blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) {
                System.out.println(("Value: " + v));
                lastValue.set(v);
            }
        });
        Assert.assertEquals(42, lastValue.get());
    }

    @Test
    public final void testConcurrentOnNextFailsValidation() throws InterruptedException {
        final int count = 10;
        final CountDownLatch latch = new CountDownLatch(count);
        Flowable<String> f = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(final Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                for (int i = 0; i < count; i++) {
                    final int v = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onNext(("v: " + v));
                            latch.countDown();
                        }
                    }).start();
                }
            }
        });
        AbstractSchedulerTests.ConcurrentObserverValidator<String> observer = new AbstractSchedulerTests.ConcurrentObserverValidator<String>();
        // this should call onNext concurrently
        f.subscribe(observer);
        if (!(observer.completed.await(3000, TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out");
        }
        if ((observer.error.get()) == null) {
            Assert.fail("We expected error messages due to concurrency");
        }
    }

    @Test
    public final void testObserveOn() throws InterruptedException {
        final Scheduler scheduler = getScheduler();
        Flowable<String> f = Flowable.fromArray("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten");
        AbstractSchedulerTests.ConcurrentObserverValidator<String> observer = new AbstractSchedulerTests.ConcurrentObserverValidator<String>();
        f.observeOn(scheduler).subscribe(observer);
        if (!(observer.completed.await(3000, TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out");
        }
        if ((observer.error.get()) != null) {
            observer.error.get().printStackTrace();
            Assert.fail(("Error: " + (observer.error.get().getMessage())));
        }
    }

    @Test
    public final void testSubscribeOnNestedConcurrency() throws InterruptedException {
        final Scheduler scheduler = getScheduler();
        Flowable<String> f = Flowable.fromArray("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten").flatMap(new Function<String, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final String v) {
                return Flowable.unsafeCreate(new Publisher<String>() {
                    @Override
                    public void subscribe(Subscriber<? super String> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        subscriber.onNext(("value_after_map-" + v));
                        subscriber.onComplete();
                    }
                }).subscribeOn(scheduler);
            }
        });
        AbstractSchedulerTests.ConcurrentObserverValidator<String> observer = new AbstractSchedulerTests.ConcurrentObserverValidator<String>();
        f.subscribe(observer);
        if (!(observer.completed.await(3000, TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out");
        }
        if ((observer.error.get()) != null) {
            observer.error.get().printStackTrace();
            Assert.fail(("Error: " + (observer.error.get().getMessage())));
        }
    }

    /**
     * Used to determine if onNext is being invoked concurrently.
     *
     * @param <T>
     * 		
     */
    private static class ConcurrentObserverValidator<T> extends DefaultSubscriber<T> {
        final AtomicInteger concurrentCounter = new AtomicInteger();

        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();

        final CountDownLatch completed = new CountDownLatch(1);

        @Override
        public void onComplete() {
            completed.countDown();
        }

        @Override
        public void onError(Throwable e) {
            error.set(e);
            completed.countDown();
        }

        @Override
        public void onNext(T args) {
            int count = concurrentCounter.incrementAndGet();
            System.out.println(("ConcurrentObserverValidator.onNext: " + args));
            if (count > 1) {
                onError(new RuntimeException("we should not have concurrent execution of onNext"));
            }
            try {
                try {
                    // take some time so other onNext calls could pile up (I haven't yet thought of a way to do this without sleeping)
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // ignore
                }
            } finally {
                concurrentCounter.decrementAndGet();
            }
        }
    }

    @Test
    public void scheduleDirect() throws Exception {
        Scheduler s = getScheduler();
        final CountDownLatch cdl = new CountDownLatch(1);
        s.scheduleDirect(new Runnable() {
            @Override
            public void run() {
                cdl.countDown();
            }
        });
        Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void scheduleDirectDelayed() throws Exception {
        Scheduler s = getScheduler();
        final CountDownLatch cdl = new CountDownLatch(1);
        s.scheduleDirect(new Runnable() {
            @Override
            public void run() {
                cdl.countDown();
            }
        }, 50, TimeUnit.MILLISECONDS);
        Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
    }

    @Test(timeout = 7000)
    public void scheduleDirectPeriodic() throws Exception {
        Scheduler s = getScheduler();
        if (s instanceof TrampolineScheduler) {
            // can't properly stop a trampolined periodic task
            return;
        }
        final CountDownLatch cdl = new CountDownLatch(5);
        Disposable d = s.schedulePeriodicallyDirect(new Runnable() {
            @Override
            public void run() {
                cdl.countDown();
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
        try {
            Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
        } finally {
            d.dispose();
        }
        Assert.assertTrue(d.isDisposed());
    }

    @Test(timeout = 10000)
    public void schedulePeriodicallyDirectZeroPeriod() throws Exception {
        Scheduler s = getScheduler();
        if (s instanceof TrampolineScheduler) {
            // can't properly stop a trampolined periodic task
            return;
        }
        for (int initial = 0; initial < 2; initial++) {
            final CountDownLatch cdl = new CountDownLatch(1);
            final SequentialDisposable sd = new SequentialDisposable();
            try {
                sd.replace(s.schedulePeriodicallyDirect(new Runnable() {
                    int count;

                    @Override
                    public void run() {
                        if ((++(count)) == 10) {
                            sd.dispose();
                            cdl.countDown();
                        }
                    }
                }, initial, 0, TimeUnit.MILLISECONDS));
                Assert.assertTrue(("" + initial), cdl.await(5, TimeUnit.SECONDS));
            } finally {
                sd.dispose();
            }
        }
    }

    @Test(timeout = 10000)
    public void schedulePeriodicallyZeroPeriod() throws Exception {
        Scheduler s = getScheduler();
        if (s instanceof TrampolineScheduler) {
            // can't properly stop a trampolined periodic task
            return;
        }
        for (int initial = 0; initial < 2; initial++) {
            final CountDownLatch cdl = new CountDownLatch(1);
            final SequentialDisposable sd = new SequentialDisposable();
            Scheduler.Worker w = s.createWorker();
            try {
                sd.replace(w.schedulePeriodically(new Runnable() {
                    int count;

                    @Override
                    public void run() {
                        if ((++(count)) == 10) {
                            sd.dispose();
                            cdl.countDown();
                        }
                    }
                }, initial, 0, TimeUnit.MILLISECONDS));
                Assert.assertTrue(("" + initial), cdl.await(5, TimeUnit.SECONDS));
            } finally {
                sd.dispose();
                w.dispose();
            }
        }
    }

    @Test(timeout = 6000)
    public void scheduleDirectDecoratesRunnable() throws InterruptedException {
        assertRunnableDecorated(new Runnable() {
            @Override
            public void run() {
                getScheduler().scheduleDirect(EMPTY_RUNNABLE);
            }
        });
    }

    @Test(timeout = 6000)
    public void scheduleDirectWithDelayDecoratesRunnable() throws InterruptedException {
        assertRunnableDecorated(new Runnable() {
            @Override
            public void run() {
                getScheduler().scheduleDirect(EMPTY_RUNNABLE, 1, TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test(timeout = 6000)
    public void schedulePeriodicallyDirectDecoratesRunnable() throws InterruptedException {
        final Scheduler scheduler = getScheduler();
        if (scheduler instanceof TrampolineScheduler) {
            // Can't properly stop a trampolined periodic task.
            return;
        }
        final AtomicReference<Disposable> disposable = new AtomicReference<Disposable>();
        try {
            assertRunnableDecorated(new Runnable() {
                @Override
                public void run() {
                    disposable.set(scheduler.schedulePeriodicallyDirect(EMPTY_RUNNABLE, 1, 10000, TimeUnit.MILLISECONDS));
                }
            });
        } finally {
            disposable.get().dispose();
        }
    }

    @Test(timeout = 5000)
    public void unwrapDefaultPeriodicTask() throws InterruptedException {
        Scheduler s = getScheduler();
        if (s instanceof TrampolineScheduler) {
            // TrampolineScheduler always return EmptyDisposable
            return;
        }
        final CountDownLatch cdl = new CountDownLatch(1);
        Runnable countDownRunnable = new Runnable() {
            @Override
            public void run() {
                cdl.countDown();
            }
        };
        Disposable disposable = s.schedulePeriodicallyDirect(countDownRunnable, 100, 100, TimeUnit.MILLISECONDS);
        SchedulerRunnableIntrospection wrapper = ((SchedulerRunnableIntrospection) (disposable));
        Assert.assertSame(countDownRunnable, wrapper.getWrappedRunnable());
        Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
        disposable.dispose();
    }

    @Test
    public void unwrapScheduleDirectTask() {
        Scheduler scheduler = getScheduler();
        if (scheduler instanceof TrampolineScheduler) {
            // TrampolineScheduler always return EmptyDisposable
            return;
        }
        final CountDownLatch cdl = new CountDownLatch(1);
        Runnable countDownRunnable = new Runnable() {
            @Override
            public void run() {
                cdl.countDown();
            }
        };
        Disposable disposable = scheduler.scheduleDirect(countDownRunnable, 100, TimeUnit.MILLISECONDS);
        SchedulerRunnableIntrospection wrapper = ((SchedulerRunnableIntrospection) (disposable));
        Assert.assertSame(countDownRunnable, wrapper.getWrappedRunnable());
        disposable.dispose();
    }

    @Test
    public void scheduleDirectNullRunnable() {
        try {
            getScheduler().scheduleDirect(null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("run is null", npe.getMessage());
        }
    }

    @Test
    public void scheduleDirectWithDelayNullRunnable() {
        try {
            getScheduler().scheduleDirect(null, 10, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("run is null", npe.getMessage());
        }
    }

    @Test
    public void schedulePeriodicallyDirectNullRunnable() {
        try {
            getScheduler().schedulePeriodicallyDirect(null, 5, 10, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("run is null", npe.getMessage());
        }
    }
}

