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


import EmptyDisposable.INSTANCE;
import Functions.EMPTY_RUNNABLE;
import io.reactivex.Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ExecutorSchedulerInterruptibleTest extends AbstractSchedulerConcurrencyTests {
    static final Executor executor = Executors.newFixedThreadPool(2, new RxThreadFactory("TestCustomPool"));

    @Test
    public final void testHandledErrorIsNotDeliveredToThreadHandler() throws InterruptedException {
        SchedulerTestHelper.testHandledErrorIsNotDeliveredToThreadHandler(getScheduler());
    }

    @Test(timeout = 60000)
    public void testCancelledTaskRetention() throws InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Scheduler s = Schedulers.from(exec, true);
        try {
            Scheduler.Worker w = s.createWorker();
            try {
                ExecutorSchedulerInterruptibleTest.testCancelledRetention(w, false);
            } finally {
                w.dispose();
            }
            w = s.createWorker();
            try {
                ExecutorSchedulerInterruptibleTest.testCancelledRetention(w, true);
            } finally {
                w.dispose();
            }
        } finally {
            exec.shutdownNow();
        }
    }

    /**
     * A simple executor which queues tasks and executes them one-by-one if executeOne() is called.
     */
    static final class TestExecutor implements Executor {
        final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();

        @Override
        public void execute(Runnable command) {
            queue.offer(command);
        }

        public void executeOne() {
            Runnable r = queue.poll();
            if (r != null) {
                r.run();
            }
        }

        public void executeAll() {
            Runnable r;
            while ((r = queue.poll()) != null) {
                r.run();
            } 
        }
    }

    @Test
    public void testCancelledTasksDontRun() {
        final AtomicInteger calls = new AtomicInteger();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                calls.getAndIncrement();
            }
        };
        ExecutorSchedulerInterruptibleTest.TestExecutor exec = new ExecutorSchedulerInterruptibleTest.TestExecutor();
        Scheduler custom = Schedulers.from(exec, true);
        Worker w = custom.createWorker();
        try {
            Disposable d1 = w.schedule(task);
            Disposable d2 = w.schedule(task);
            Disposable d3 = w.schedule(task);
            d1.dispose();
            d2.dispose();
            d3.dispose();
            exec.executeAll();
            Assert.assertEquals(0, calls.get());
        } finally {
            w.dispose();
        }
    }

    @Test
    public void testCancelledWorkerDoesntRunTasks() {
        final AtomicInteger calls = new AtomicInteger();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                calls.getAndIncrement();
            }
        };
        ExecutorSchedulerInterruptibleTest.TestExecutor exec = new ExecutorSchedulerInterruptibleTest.TestExecutor();
        Scheduler custom = Schedulers.from(exec, true);
        Worker w = custom.createWorker();
        try {
            w.schedule(task);
            w.schedule(task);
            w.schedule(task);
        } finally {
            w.dispose();
        }
        exec.executeAll();
        Assert.assertEquals(0, calls.get());
    }

    // FIXME the internal structure changed and these can't be tested
    // 
    // @Test
    // public void testNoTimedTaskAfterScheduleRetention() throws InterruptedException {
    // Executor e = new Executor() {
    // @Override
    // public void execute(Runnable command) {
    // command.run();
    // }
    // };
    // ExecutorWorker w = (ExecutorWorker)Schedulers.from(e, true).createWorker();
    // 
    // w.schedule(Functions.emptyRunnable(), 50, TimeUnit.MILLISECONDS);
    // 
    // assertTrue(w.tasks.hasSubscriptions());
    // 
    // Thread.sleep(150);
    // 
    // assertFalse(w.tasks.hasSubscriptions());
    // }
    // 
    // @Test
    // public void testNoTimedTaskPartRetention() {
    // Executor e = new Executor() {
    // @Override
    // public void execute(Runnable command) {
    // 
    // }
    // };
    // ExecutorWorker w = (ExecutorWorker)Schedulers.from(e, true).createWorker();
    // 
    // Disposable task = w.schedule(Functions.emptyRunnable(), 1, TimeUnit.DAYS);
    // 
    // assertTrue(w.tasks.hasSubscriptions());
    // 
    // task.dispose();
    // 
    // assertFalse(w.tasks.hasSubscriptions());
    // }
    // 
    // @Test
    // public void testNoPeriodicTimedTaskPartRetention() throws InterruptedException {
    // Executor e = new Executor() {
    // @Override
    // public void execute(Runnable command) {
    // command.run();
    // }
    // };
    // ExecutorWorker w = (ExecutorWorker)Schedulers.from(e, true).createWorker();
    // 
    // final CountDownLatch cdl = new CountDownLatch(1);
    // final Runnable action = new Runnable() {
    // @Override
    // public void run() {
    // cdl.countDown();
    // }
    // };
    // 
    // Disposable task = w.schedulePeriodically(action, 0, 1, TimeUnit.DAYS);
    // 
    // assertTrue(w.tasks.hasSubscriptions());
    // 
    // cdl.await();
    // 
    // task.dispose();
    // 
    // assertFalse(w.tasks.hasSubscriptions());
    // }
    @Test
    public void plainExecutor() throws Exception {
        Scheduler s = Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable r) {
                r.run();
            }
        }, true);
        final CountDownLatch cdl = new CountDownLatch(5);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                cdl.countDown();
            }
        };
        s.scheduleDirect(r);
        s.scheduleDirect(r, 50, TimeUnit.MILLISECONDS);
        Disposable d = s.schedulePeriodicallyDirect(r, 10, 10, TimeUnit.MILLISECONDS);
        try {
            Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
        } finally {
            d.dispose();
        }
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void rejectingExecutor() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.shutdown();
        Scheduler s = Schedulers.from(exec, true);
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Assert.assertSame(INSTANCE, s.scheduleDirect(EMPTY_RUNNABLE));
            Assert.assertSame(INSTANCE, s.scheduleDirect(EMPTY_RUNNABLE, 10, TimeUnit.MILLISECONDS));
            Assert.assertSame(INSTANCE, s.schedulePeriodicallyDirect(EMPTY_RUNNABLE, 10, 10, TimeUnit.MILLISECONDS));
            TestHelper.assertUndeliverable(errors, 0, RejectedExecutionException.class);
            TestHelper.assertUndeliverable(errors, 1, RejectedExecutionException.class);
            TestHelper.assertUndeliverable(errors, 2, RejectedExecutionException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void rejectingExecutorWorker() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.shutdown();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Worker s = Schedulers.from(exec, true).createWorker();
            Assert.assertSame(INSTANCE, s.schedule(EMPTY_RUNNABLE));
            s = Schedulers.from(exec, true).createWorker();
            Assert.assertSame(INSTANCE, s.schedule(EMPTY_RUNNABLE, 10, TimeUnit.MILLISECONDS));
            s = Schedulers.from(exec, true).createWorker();
            Assert.assertSame(INSTANCE, s.schedulePeriodically(EMPTY_RUNNABLE, 10, 10, TimeUnit.MILLISECONDS));
            TestHelper.assertUndeliverable(errors, 0, RejectedExecutionException.class);
            TestHelper.assertUndeliverable(errors, 1, RejectedExecutionException.class);
            TestHelper.assertUndeliverable(errors, 2, RejectedExecutionException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void reuseScheduledExecutor() throws Exception {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        try {
            Scheduler s = Schedulers.from(exec, true);
            final CountDownLatch cdl = new CountDownLatch(8);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    cdl.countDown();
                }
            };
            s.scheduleDirect(r);
            s.scheduleDirect(r, 10, TimeUnit.MILLISECONDS);
            Disposable d = s.schedulePeriodicallyDirect(r, 10, 10, TimeUnit.MILLISECONDS);
            try {
                Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
            } finally {
                d.dispose();
            }
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void reuseScheduledExecutorAsWorker() throws Exception {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        Worker s = Schedulers.from(exec, true).createWorker();
        Assert.assertFalse(s.isDisposed());
        try {
            final CountDownLatch cdl = new CountDownLatch(8);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    cdl.countDown();
                }
            };
            s.schedule(r);
            s.schedule(r, 10, TimeUnit.MILLISECONDS);
            Disposable d = s.schedulePeriodically(r, 10, 10, TimeUnit.MILLISECONDS);
            try {
                Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
            } finally {
                d.dispose();
            }
        } finally {
            s.dispose();
            exec.shutdown();
        }
        Assert.assertTrue(s.isDisposed());
    }

    @Test
    public void disposeRace() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        final Scheduler s = Schedulers.from(exec, true);
        try {
            for (int i = 0; i < 500; i++) {
                final Worker w = s.createWorker();
                final AtomicInteger c = new AtomicInteger(2);
                w.schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.decrementAndGet();
                        while ((c.get()) != 0) {
                        } 
                    }
                });
                c.decrementAndGet();
                while ((c.get()) != 0) {
                } 
                w.dispose();
            }
        } finally {
            exec.shutdownNow();
        }
    }

    @Test
    public void runnableDisposed() {
        final Scheduler s = Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable r) {
                r.run();
            }
        }, true);
        Disposable d = s.scheduleDirect(EMPTY_RUNNABLE);
        Assert.assertTrue(d.isDisposed());
    }

    @Test(timeout = 1000)
    public void runnableDisposedAsync() throws Exception {
        final Scheduler s = Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable r) {
                new Thread(r).start();
            }
        }, true);
        Disposable d = s.scheduleDirect(EMPTY_RUNNABLE);
        while (!(d.isDisposed())) {
            Thread.sleep(1);
        } 
    }

    @Test(timeout = 1000)
    public void runnableDisposedAsync2() throws Exception {
        final Scheduler s = Schedulers.from(ExecutorSchedulerInterruptibleTest.executor, true);
        Disposable d = s.scheduleDirect(EMPTY_RUNNABLE);
        while (!(d.isDisposed())) {
            Thread.sleep(1);
        } 
    }

    @Test(timeout = 1000)
    public void runnableDisposedAsyncCrash() throws Exception {
        final Scheduler s = Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable r) {
                new Thread(r).start();
            }
        }, true);
        Disposable d = s.scheduleDirect(new Runnable() {
            @Override
            public void run() {
                throw new IllegalStateException();
            }
        });
        while (!(d.isDisposed())) {
            Thread.sleep(1);
        } 
    }

    @Test(timeout = 1000)
    public void runnableDisposedAsyncTimed() throws Exception {
        final Scheduler s = Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable r) {
                new Thread(r).start();
            }
        }, true);
        Disposable d = s.scheduleDirect(EMPTY_RUNNABLE, 1, TimeUnit.MILLISECONDS);
        while (!(d.isDisposed())) {
            Thread.sleep(1);
        } 
    }

    @Test(timeout = 1000)
    public void runnableDisposedAsyncTimed2() throws Exception {
        ExecutorService executorScheduler = Executors.newScheduledThreadPool(1, new RxThreadFactory("TestCustomPoolTimed"));
        try {
            final Scheduler s = Schedulers.from(executorScheduler, true);
            Disposable d = s.scheduleDirect(EMPTY_RUNNABLE, 1, TimeUnit.MILLISECONDS);
            while (!(d.isDisposed())) {
                Thread.sleep(1);
            } 
        } finally {
            executorScheduler.shutdownNow();
        }
    }

    @Test
    public void unwrapScheduleDirectTaskAfterDispose() {
        Scheduler scheduler = getScheduler();
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
        Assert.assertSame(EMPTY_RUNNABLE, wrapper.getWrappedRunnable());
    }

    @Test(timeout = 10000)
    public void interruptibleDirectTask() throws Exception {
        Scheduler scheduler = getScheduler();
        final AtomicInteger sync = new AtomicInteger(2);
        final AtomicBoolean isInterrupted = new AtomicBoolean();
        Disposable d = scheduler.scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if ((sync.decrementAndGet()) != 0) {
                    while ((sync.get()) != 0) {
                    } 
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    isInterrupted.set(true);
                }
            }
        });
        if ((sync.decrementAndGet()) != 0) {
            while ((sync.get()) != 0) {
            } 
        }
        Thread.sleep(500);
        d.dispose();
        int i = 20;
        while (((i--) > 0) && (!(isInterrupted.get()))) {
            Thread.sleep(50);
        } 
        Assert.assertTrue("Interruption did not propagate", isInterrupted.get());
    }

    @Test(timeout = 10000)
    public void interruptibleWorkerTask() throws Exception {
        Scheduler scheduler = getScheduler();
        Worker worker = scheduler.createWorker();
        try {
            final AtomicInteger sync = new AtomicInteger(2);
            final AtomicBoolean isInterrupted = new AtomicBoolean();
            Disposable d = worker.schedule(new Runnable() {
                @Override
                public void run() {
                    if ((sync.decrementAndGet()) != 0) {
                        while ((sync.get()) != 0) {
                        } 
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        isInterrupted.set(true);
                    }
                }
            });
            if ((sync.decrementAndGet()) != 0) {
                while ((sync.get()) != 0) {
                } 
            }
            Thread.sleep(500);
            d.dispose();
            int i = 20;
            while (((i--) > 0) && (!(isInterrupted.get()))) {
                Thread.sleep(50);
            } 
            Assert.assertTrue("Interruption did not propagate", isInterrupted.get());
        } finally {
            worker.dispose();
        }
    }
}

