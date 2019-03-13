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


import ScheduledRunnable.ASYNC_DISPOSED;
import ScheduledRunnable.DONE;
import ScheduledRunnable.FUTURE_INDEX;
import ScheduledRunnable.SYNC_DISPOSED;
import ScheduledRunnable.THREAD_INDEX;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ScheduledRunnableTest {
    @Test
    public void dispose() {
        CompositeDisposable set = new CompositeDisposable();
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
        set.add(run);
        Assert.assertFalse(run.isDisposed());
        set.dispose();
        Assert.assertTrue(run.isDisposed());
    }

    @Test
    public void disposeRun() {
        CompositeDisposable set = new CompositeDisposable();
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
        set.add(run);
        Assert.assertFalse(run.isDisposed());
        run.dispose();
        run.dispose();
        Assert.assertTrue(run.isDisposed());
    }

    @Test
    public void setFutureCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            CompositeDisposable set = new CompositeDisposable();
            final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
            set.add(run);
            final FutureTask<Object> ft = new FutureTask<Object>(Functions.EMPTY_RUNNABLE, 0);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    run.setFuture(ft);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    run.dispose();
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertEquals(0, set.size());
        }
    }

    @Test
    public void setFutureRunRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            CompositeDisposable set = new CompositeDisposable();
            final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
            set.add(run);
            final FutureTask<Object> ft = new FutureTask<Object>(Functions.EMPTY_RUNNABLE, 0);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    run.setFuture(ft);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    run.run();
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertEquals(0, set.size());
        }
    }

    @Test
    public void disposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            CompositeDisposable set = new CompositeDisposable();
            final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
            set.add(run);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    run.dispose();
                }
            };
            TestHelper.race(r1, r1);
            Assert.assertEquals(0, set.size());
        }
    }

    @Test
    public void runDispose() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            CompositeDisposable set = new CompositeDisposable();
            final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
            set.add(run);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    run.call();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    run.dispose();
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertEquals(0, set.size());
        }
    }

    @Test
    public void pluginCrash() {
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                throw new TestException("Second");
            }
        });
        CompositeDisposable set = new CompositeDisposable();
        final ScheduledRunnable run = new ScheduledRunnable(new Runnable() {
            @Override
            public void run() {
                throw new TestException("First");
            }
        }, set);
        set.add(run);
        try {
            run.run();
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Second", ex.getMessage());
        } finally {
            Thread.currentThread().setUncaughtExceptionHandler(null);
        }
        Assert.assertTrue(run.isDisposed());
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void crashReported() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            CompositeDisposable set = new CompositeDisposable();
            final ScheduledRunnable run = new ScheduledRunnable(new Runnable() {
                @Override
                public void run() {
                    throw new TestException("First");
                }
            }, set);
            set.add(run);
            run.run();
            Assert.assertTrue(run.isDisposed());
            Assert.assertEquals(0, set.size());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "First");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void withoutParentDisposed() {
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.dispose();
        run.call();
    }

    @Test
    public void withParentDisposed() {
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, new CompositeDisposable());
        run.dispose();
        run.call();
    }

    @Test
    public void withFutureDisposed() {
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.setFuture(new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null));
        run.dispose();
        run.call();
    }

    @Test
    public void withFutureDisposed2() {
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.dispose();
        run.setFuture(new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null));
        run.call();
    }

    @Test
    public void withFutureDisposed3() {
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.dispose();
        run.set(2, Thread.currentThread());
        run.setFuture(new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null));
        run.call();
    }

    @Test
    public void runFuture() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            CompositeDisposable set = new CompositeDisposable();
            final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
            set.add(run);
            final FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    run.call();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    run.setFuture(ft);
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void syncWorkerCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final CompositeDisposable set = new CompositeDisposable();
            final AtomicBoolean interrupted = new AtomicBoolean();
            final AtomicInteger sync = new AtomicInteger(2);
            final AtomicInteger syncb = new AtomicInteger(2);
            Runnable r0 = new Runnable() {
                @Override
                public void run() {
                    set.dispose();
                    if ((sync.decrementAndGet()) != 0) {
                        while ((sync.get()) != 0) {
                        } 
                    }
                    if ((syncb.decrementAndGet()) != 0) {
                        while ((syncb.get()) != 0) {
                        } 
                    }
                    for (int j = 0; j < 1000; j++) {
                        if (Thread.currentThread().isInterrupted()) {
                            interrupted.set(true);
                            break;
                        }
                    }
                }
            };
            final ScheduledRunnable run = new ScheduledRunnable(r0, set);
            set.add(run);
            final FutureTask<Void> ft = new FutureTask<Void>(run, null);
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    if ((sync.decrementAndGet()) != 0) {
                        while ((sync.get()) != 0) {
                        } 
                    }
                    run.setFuture(ft);
                    if ((syncb.decrementAndGet()) != 0) {
                        while ((syncb.get()) != 0) {
                        } 
                    }
                }
            };
            TestHelper.race(ft, r2);
            Assert.assertFalse("The task was interrupted", interrupted.get());
        }
    }

    @Test
    public void disposeAfterRun() {
        final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.run();
        Assert.assertEquals(DONE, run.get(FUTURE_INDEX));
        run.dispose();
        Assert.assertEquals(DONE, run.get(FUTURE_INDEX));
    }

    @Test
    public void syncDisposeIdempotent() {
        final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.set(THREAD_INDEX, Thread.currentThread());
        run.dispose();
        Assert.assertEquals(SYNC_DISPOSED, run.get(FUTURE_INDEX));
        run.dispose();
        Assert.assertEquals(SYNC_DISPOSED, run.get(FUTURE_INDEX));
        run.run();
        Assert.assertEquals(SYNC_DISPOSED, run.get(FUTURE_INDEX));
    }

    @Test
    public void asyncDisposeIdempotent() {
        final ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        run.dispose();
        Assert.assertEquals(ASYNC_DISPOSED, run.get(FUTURE_INDEX));
        run.dispose();
        Assert.assertEquals(ASYNC_DISPOSED, run.get(FUTURE_INDEX));
        run.run();
        Assert.assertEquals(ASYNC_DISPOSED, run.get(FUTURE_INDEX));
    }

    @Test
    public void noParentIsDisposed() {
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, null);
        Assert.assertFalse(run.isDisposed());
        run.run();
        Assert.assertTrue(run.isDisposed());
    }

    @Test
    public void withParentIsDisposed() {
        CompositeDisposable set = new CompositeDisposable();
        ScheduledRunnable run = new ScheduledRunnable(Functions.EMPTY_RUNNABLE, set);
        set.add(run);
        Assert.assertFalse(run.isDisposed());
        run.run();
        Assert.assertTrue(run.isDisposed());
        Assert.assertFalse(set.remove(run));
    }
}

