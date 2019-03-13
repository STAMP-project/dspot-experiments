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


import AbstractDirectTask.FINISHED;
import io.reactivex.TestHelper;
import io.reactivex.internal.functions.Functions;
import java.util.concurrent.FutureTask;
import org.junit.Assert;
import org.junit.Test;


public class AbstractDirectTaskTest {
    @Test
    public void cancelSetFuture() {
        AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
            private static final long serialVersionUID = 208585707945686116L;
        };
        final Boolean[] interrupted = new Boolean[]{ null };
        Assert.assertFalse(task.isDisposed());
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                interrupted[0] = mayInterruptIfRunning;
                return super.cancel(mayInterruptIfRunning);
            }
        };
        task.setFuture(ft);
        Assert.assertTrue(interrupted[0]);
        Assert.assertTrue(task.isDisposed());
    }

    @Test
    public void cancelSetFutureCurrentThread() {
        AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
            private static final long serialVersionUID = 208585707945686116L;
        };
        final Boolean[] interrupted = new Boolean[]{ null };
        Assert.assertFalse(task.isDisposed());
        task.runner = Thread.currentThread();
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                interrupted[0] = mayInterruptIfRunning;
                return super.cancel(mayInterruptIfRunning);
            }
        };
        task.setFuture(ft);
        Assert.assertFalse(interrupted[0]);
        Assert.assertTrue(task.isDisposed());
    }

    @Test
    public void setFutureCancel() {
        AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
            private static final long serialVersionUID = 208585707945686116L;
        };
        final Boolean[] interrupted = new Boolean[]{ null };
        FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                interrupted[0] = mayInterruptIfRunning;
                return super.cancel(mayInterruptIfRunning);
            }
        };
        Assert.assertFalse(task.isDisposed());
        task.setFuture(ft);
        Assert.assertFalse(task.isDisposed());
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        Assert.assertTrue(interrupted[0]);
    }

    @Test
    public void setFutureCancelSameThread() {
        AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
            private static final long serialVersionUID = 208585707945686116L;
        };
        final Boolean[] interrupted = new Boolean[]{ null };
        FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                interrupted[0] = mayInterruptIfRunning;
                return super.cancel(mayInterruptIfRunning);
            }
        };
        Assert.assertFalse(task.isDisposed());
        task.setFuture(ft);
        task.runner = Thread.currentThread();
        Assert.assertFalse(task.isDisposed());
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        Assert.assertFalse(interrupted[0]);
    }

    @Test
    public void finished() {
        AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
            private static final long serialVersionUID = 208585707945686116L;
        };
        final Boolean[] interrupted = new Boolean[]{ null };
        FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                interrupted[0] = mayInterruptIfRunning;
                return super.cancel(mayInterruptIfRunning);
            }
        };
        task.set(FINISHED);
        task.setFuture(ft);
        Assert.assertTrue(task.isDisposed());
        Assert.assertNull(interrupted[0]);
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        Assert.assertNull(interrupted[0]);
    }

    @Test
    public void finishedCancel() {
        AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
            private static final long serialVersionUID = 208585707945686116L;
        };
        final Boolean[] interrupted = new Boolean[]{ null };
        FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                interrupted[0] = mayInterruptIfRunning;
                return super.cancel(mayInterruptIfRunning);
            }
        };
        task.set(FINISHED);
        Assert.assertTrue(task.isDisposed());
        task.dispose();
        Assert.assertTrue(task.isDisposed());
        task.setFuture(ft);
        Assert.assertTrue(task.isDisposed());
        Assert.assertNull(interrupted[0]);
        Assert.assertTrue(task.isDisposed());
        Assert.assertNull(interrupted[0]);
    }

    @Test
    public void disposeSetFutureRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final AbstractDirectTask task = new AbstractDirectTask(Functions.EMPTY_RUNNABLE) {
                private static final long serialVersionUID = 208585707945686116L;
            };
            final Boolean[] interrupted = new Boolean[]{ null };
            final FutureTask<Void> ft = new FutureTask<Void>(Functions.EMPTY_RUNNABLE, null) {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    interrupted[0] = mayInterruptIfRunning;
                    return super.cancel(mayInterruptIfRunning);
                }
            };
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    task.dispose();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    task.setFuture(ft);
                }
            };
            TestHelper.race(r1, r2);
        }
    }
}

