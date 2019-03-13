/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.android.schedulers;


import android.os.Looper;
import android.os.Message;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.android.testutil.CountingRunnable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowMessageQueue;


@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 16)
public final class HandlerSchedulerTest {
    private Scheduler scheduler;

    private boolean async;

    public HandlerSchedulerTest(boolean async) {
        this.scheduler = new HandlerScheduler(new android.os.Handler(Looper.getMainLooper()), async);
        this.async = async;
    }

    @Test
    public void directScheduleOncePostsImmediately() {
        CountingRunnable counter = new CountingRunnable();
        scheduler.scheduleDirect(counter);
        runUiThreadTasks();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void directScheduleOnceWithNegativeDelayPostsImmediately() {
        CountingRunnable counter = new CountingRunnable();
        scheduler.scheduleDirect(counter, (-1), TimeUnit.MINUTES);
        runUiThreadTasks();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void directScheduleOnceUsesHook() {
        final CountingRunnable newCounter = new CountingRunnable();
        final AtomicReference<Runnable> runnableRef = new AtomicReference<>();
        RxJavaPlugins.setScheduleHandler(new io.reactivex.functions.Function<Runnable, Runnable>() {
            @Override
            public Runnable apply(Runnable runnable) {
                runnableRef.set(runnable);
                return newCounter;
            }
        });
        CountingRunnable counter = new CountingRunnable();
        scheduler.scheduleDirect(counter);
        // Verify our runnable was passed to the schedulers hook.
        Assert.assertSame(counter, runnableRef.get());
        runUiThreadTasks();
        // Verify the scheduled runnable was the one returned from the hook.
        Assert.assertEquals(1, newCounter.get());
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void directScheduleOnceDisposedDoesNotRun() {
        CountingRunnable counter = new CountingRunnable();
        Disposable disposable = scheduler.scheduleDirect(counter);
        disposable.dispose();
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void directScheduleOnceWithDelayPostsWithDelay() {
        CountingRunnable counter = new CountingRunnable();
        scheduler.scheduleDirect(counter, 1, TimeUnit.MINUTES);
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
        HandlerSchedulerTest.idleMainLooper(1, TimeUnit.MINUTES);
        runUiThreadTasks();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void directScheduleOnceWithDelayUsesHook() {
        final CountingRunnable newCounter = new CountingRunnable();
        final AtomicReference<Runnable> runnableRef = new AtomicReference<>();
        RxJavaPlugins.setScheduleHandler(new io.reactivex.functions.Function<Runnable, Runnable>() {
            @Override
            public Runnable apply(Runnable runnable) {
                runnableRef.set(runnable);
                return newCounter;
            }
        });
        CountingRunnable counter = new CountingRunnable();
        scheduler.scheduleDirect(counter, 1, TimeUnit.MINUTES);
        // Verify our runnable was passed to the schedulers hook.
        Assert.assertSame(counter, runnableRef.get());
        HandlerSchedulerTest.idleMainLooper(1, TimeUnit.MINUTES);
        runUiThreadTasks();
        // Verify the scheduled runnable was the one returned from the hook.
        Assert.assertEquals(1, newCounter.get());
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void directScheduleOnceWithDelayDisposedDoesNotRun() {
        CountingRunnable counter = new CountingRunnable();
        Disposable disposable = scheduler.scheduleDirect(counter, 1, TimeUnit.MINUTES);
        HandlerSchedulerTest.idleMainLooper(30, TimeUnit.SECONDS);
        disposable.dispose();
        HandlerSchedulerTest.idleMainLooper(30, TimeUnit.SECONDS);
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerScheduleOncePostsImmediately() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter);
        runUiThreadTasks();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void workerScheduleOnceWithNegativeDelayPostsImmediately() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter, (-1), TimeUnit.MINUTES);
        runUiThreadTasks();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void workerScheduleOnceUsesHook() {
        final CountingRunnable newCounter = new CountingRunnable();
        final AtomicReference<Runnable> runnableRef = new AtomicReference<>();
        RxJavaPlugins.setScheduleHandler(new io.reactivex.functions.Function<Runnable, Runnable>() {
            @Override
            public Runnable apply(Runnable runnable) {
                runnableRef.set(runnable);
                return newCounter;
            }
        });
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter);
        // Verify our runnable was passed to the schedulers hook.
        Assert.assertSame(counter, runnableRef.get());
        runUiThreadTasks();
        // Verify the scheduled runnable was the one returned from the hook.
        Assert.assertEquals(1, newCounter.get());
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerScheduleOnceDisposedDoesNotRun() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        Disposable disposable = worker.schedule(counter);
        disposable.dispose();
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerScheduleOnceWithDelayPostsWithDelay() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter, 1, TimeUnit.MINUTES);
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
        HandlerSchedulerTest.idleMainLooper(1, TimeUnit.MINUTES);
        runUiThreadTasks();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void workerScheduleOnceWithDelayUsesHook() {
        final CountingRunnable newCounter = new CountingRunnable();
        final AtomicReference<Runnable> runnableRef = new AtomicReference<>();
        RxJavaPlugins.setScheduleHandler(new io.reactivex.functions.Function<Runnable, Runnable>() {
            @Override
            public Runnable apply(Runnable runnable) {
                runnableRef.set(runnable);
                return newCounter;
            }
        });
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter, 1, TimeUnit.MINUTES);
        // Verify our runnable was passed to the schedulers hook.
        Assert.assertSame(counter, runnableRef.get());
        HandlerSchedulerTest.idleMainLooper(1, TimeUnit.MINUTES);
        runUiThreadTasks();
        // Verify the scheduled runnable was the one returned from the hook.
        Assert.assertEquals(1, newCounter.get());
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerScheduleOnceWithDelayDisposedDoesNotRun() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        Disposable disposable = worker.schedule(counter, 1, TimeUnit.MINUTES);
        HandlerSchedulerTest.idleMainLooper(30, TimeUnit.SECONDS);
        disposable.dispose();
        HandlerSchedulerTest.idleMainLooper(30, TimeUnit.SECONDS);
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerDisposableTracksDisposedState() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        Disposable disposable = worker.schedule(counter);
        Assert.assertFalse(disposable.isDisposed());
        disposable.dispose();
        Assert.assertTrue(disposable.isDisposed());
    }

    @Test
    public void workerUnsubscriptionDuringSchedulingCancelsScheduledAction() {
        final AtomicReference<Worker> workerRef = new AtomicReference<>();
        RxJavaPlugins.setScheduleHandler(new io.reactivex.functions.Function<Runnable, Runnable>() {
            @Override
            public Runnable apply(Runnable runnable) {
                // Purposefully unsubscribe in an asinine point after the normal unsubscribed check.
                workerRef.get().dispose();
                return runnable;
            }
        });
        Worker worker = scheduler.createWorker();
        workerRef.set(worker);
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter);
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerDisposeCancelsScheduled() {
        Worker worker = scheduler.createWorker();
        CountingRunnable counter = new CountingRunnable();
        worker.schedule(counter, 1, TimeUnit.MINUTES);
        worker.dispose();
        runUiThreadTasks();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void workerUnsubscriptionDoesNotAffectOtherWorkers() {
        Worker workerA = scheduler.createWorker();
        CountingRunnable counterA = new CountingRunnable();
        workerA.schedule(counterA, 1, TimeUnit.MINUTES);
        Worker workerB = scheduler.createWorker();
        CountingRunnable counterB = new CountingRunnable();
        workerB.schedule(counterB, 1, TimeUnit.MINUTES);
        workerA.dispose();
        runUiThreadTasksIncludingDelayedTasks();
        Assert.assertEquals(0, counterA.get());
        Assert.assertEquals(1, counterB.get());
    }

    @Test
    public void workerTracksDisposedState() {
        Worker worker = scheduler.createWorker();
        Assert.assertFalse(worker.isDisposed());
        worker.dispose();
        Assert.assertTrue(worker.isDisposed());
    }

    @Test
    public void disposedWorkerReturnsDisposedDisposables() {
        Worker worker = scheduler.createWorker();
        worker.dispose();
        Disposable disposable = worker.schedule(new CountingRunnable());
        Assert.assertTrue(disposable.isDisposed());
    }

    @Test
    public void throwingActionRoutedToRxJavaPlugins() {
        Consumer<? super Throwable> originalErrorHandler = RxJavaPlugins.getErrorHandler();
        try {
            final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
            RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwableRef.set(throwable);
                }
            });
            Worker worker = scheduler.createWorker();
            final NullPointerException npe = new NullPointerException();
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    throw npe;
                }
            };
            worker.schedule(action);
            runUiThreadTasks();
            Assert.assertSame(npe, throwableRef.get());
        } finally {
            RxJavaPlugins.setErrorHandler(originalErrorHandler);
        }
    }

    @Test
    public void directScheduleOnceInputValidation() {
        try {
            scheduler.scheduleDirect(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("run == null", e.getMessage());
        }
        try {
            scheduler.scheduleDirect(null, 1, TimeUnit.MINUTES);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("run == null", e.getMessage());
        }
        try {
            scheduler.scheduleDirect(new CountingRunnable(), 1, null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("unit == null", e.getMessage());
        }
    }

    @Test
    public void workerScheduleOnceInputValidation() {
        Worker worker = scheduler.createWorker();
        try {
            worker.schedule(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("run == null", e.getMessage());
        }
        try {
            worker.schedule(null, 1, TimeUnit.MINUTES);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("run == null", e.getMessage());
        }
        try {
            worker.schedule(new CountingRunnable(), 1, null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("unit == null", e.getMessage());
        }
    }

    @Test
    public void directScheduleSetAsync() {
        ShadowMessageQueue mainMessageQueue = shadowOf(Looper.getMainLooper().getQueue());
        scheduler.scheduleDirect(new Runnable() {
            @Override
            public void run() {
            }
        });
        Message message = mainMessageQueue.getHead();
        Assert.assertEquals(async, message.isAsynchronous());
    }

    @Test
    public void workerScheduleSetAsync() {
        ShadowMessageQueue mainMessageQueue = shadowOf(Looper.getMainLooper().getQueue());
        Worker worker = scheduler.createWorker();
        worker.schedule(new Runnable() {
            @Override
            public void run() {
            }
        });
        Message message = mainMessageQueue.getHead();
        Assert.assertEquals(async, message.isAsynchronous());
    }

    @Test
    public void workerSchedulePeriodicallySetAsync() {
        ShadowMessageQueue mainMessageQueue = shadowOf(Looper.getMainLooper().getQueue());
        Worker worker = scheduler.createWorker();
        worker.schedulePeriodically(new Runnable() {
            @Override
            public void run() {
            }
        }, 1, 1, TimeUnit.MINUTES);
        Message message = mainMessageQueue.getHead();
        Assert.assertEquals(async, message.isAsynchronous());
    }
}

