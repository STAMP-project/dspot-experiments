/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
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
package io.reactivex.internal.schedulers;


import EmptyDisposable.INSTANCE;
import Functions.EMPTY_RUNNABLE;
import io.reactivex.Scheduler.Worker;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TrampolineSchedulerInternalTest {
    @Test
    public void scheduleDirectInterrupt() {
        Thread.currentThread().interrupt();
        final int[] calls = new int[]{ 0 };
        Assert.assertSame(INSTANCE, Schedulers.trampoline().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                (calls[0])++;
            }
        }, 1, TimeUnit.SECONDS));
        Assert.assertTrue(Thread.interrupted());
        Assert.assertEquals(0, calls[0]);
    }

    @Test
    public void dispose() {
        Worker w = Schedulers.trampoline().createWorker();
        Assert.assertFalse(w.isDisposed());
        w.dispose();
        Assert.assertTrue(w.isDisposed());
        Assert.assertEquals(INSTANCE, w.schedule(EMPTY_RUNNABLE));
    }

    @Test
    public void reentrantScheduleDispose() {
        final Worker w = Schedulers.trampoline().createWorker();
        try {
            final int[] calls = new int[]{ 0, 0 };
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    (calls[0])++;
                    w.schedule(new Runnable() {
                        @Override
                        public void run() {
                            (calls[1])++;
                        }
                    }).dispose();
                }
            });
            Assert.assertEquals(1, calls[0]);
            Assert.assertEquals(0, calls[1]);
        } finally {
            w.dispose();
        }
    }

    @Test
    public void reentrantScheduleShutdown() {
        final Worker w = Schedulers.trampoline().createWorker();
        try {
            final int[] calls = new int[]{ 0, 0 };
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    (calls[0])++;
                    w.schedule(new Runnable() {
                        @Override
                        public void run() {
                            (calls[1])++;
                        }
                    }, 1, TimeUnit.MILLISECONDS);
                    w.dispose();
                }
            });
            Assert.assertEquals(1, calls[0]);
            Assert.assertEquals(0, calls[1]);
        } finally {
            w.dispose();
        }
    }

    @Test
    public void reentrantScheduleShutdown2() {
        final Worker w = Schedulers.trampoline().createWorker();
        try {
            final int[] calls = new int[]{ 0, 0 };
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    (calls[0])++;
                    w.dispose();
                    Assert.assertSame(INSTANCE, w.schedule(new Runnable() {
                        @Override
                        public void run() {
                            (calls[1])++;
                        }
                    }, 1, TimeUnit.MILLISECONDS));
                }
            });
            Assert.assertEquals(1, calls[0]);
            Assert.assertEquals(0, calls[1]);
        } finally {
            w.dispose();
        }
    }

    @Test(timeout = 5000)
    public void reentrantScheduleInterrupt() {
        final Worker w = Schedulers.trampoline().createWorker();
        try {
            final int[] calls = new int[]{ 0 };
            Thread.currentThread().interrupt();
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    (calls[0])++;
                }
            }, 1, TimeUnit.DAYS);
            Assert.assertTrue(Thread.interrupted());
            Assert.assertEquals(0, calls[0]);
        } finally {
            w.dispose();
        }
    }

    @Test
    public void sleepingRunnableDisposedOnRun() {
        TrampolineWorker w = new TrampolineWorker();
        Runnable r = Mockito.mock(Runnable.class);
        SleepingRunnable run = new SleepingRunnable(r, w, 0);
        w.dispose();
        run.run();
        Mockito.verify(r, Mockito.never()).run();
    }

    @Test
    public void sleepingRunnableNoDelayRun() {
        TrampolineWorker w = new TrampolineWorker();
        Runnable r = Mockito.mock(Runnable.class);
        SleepingRunnable run = new SleepingRunnable(r, w, 0);
        run.run();
        Mockito.verify(r).run();
    }

    @Test
    public void sleepingRunnableDisposedOnDelayedRun() {
        final TrampolineWorker w = new TrampolineWorker();
        Runnable r = Mockito.mock(Runnable.class);
        SleepingRunnable run = new SleepingRunnable(r, w, ((System.currentTimeMillis()) + 200));
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                w.dispose();
            }
        }, 100, TimeUnit.MILLISECONDS);
        run.run();
        Mockito.verify(r, Mockito.never()).run();
    }
}

