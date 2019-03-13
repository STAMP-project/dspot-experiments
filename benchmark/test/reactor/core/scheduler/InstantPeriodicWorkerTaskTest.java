/**
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.scheduler;


import Disposable.Composite;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.test.util.RaceTestUtils;


public class InstantPeriodicWorkerTaskTest {
    private static final RuntimeException exception = new RuntimeException();

    private static final Runnable errorRunnable = () -> {
        throw InstantPeriodicWorkerTaskTest.exception;
    };

    private static final Runnable emptyRunnable = () -> {
    };

    @Test
    public void taskCrash() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composite = Disposables.composite();
        List<Throwable> throwables = InstantPeriodicWorkerTaskTest.prepareErrorHook();
        try {
            InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composite);
            assertThat(task.call()).isNull();
            assertThat(throwables).containsOnly(InstantPeriodicWorkerTaskTest.exception);
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void dispose() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
            assertThat(task.isDisposed()).isFalse();
            task.dispose();
            assertThat(task.isDisposed()).isTrue();
            task.dispose();
            assertThat(task.isDisposed()).isTrue();
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void dispose2() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
            task.setFirst(new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null));
            task.setRest(new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null));
            assertThat(task.isDisposed()).isFalse();
            task.dispose();
            assertThat(task.isDisposed()).isTrue();
            task.dispose();
            assertThat(task.isDisposed()).isTrue();
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void dispose2CurrentThread() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
            task.thread = Thread.currentThread();
            task.setFirst(new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null));
            task.setRest(new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null));
            assertThat(task.isDisposed()).isFalse();
            task.dispose();
            assertThat(task.isDisposed()).isTrue();
            assertThat(composit.size()).isEqualTo(0);
            task.dispose();
            assertThat(task.isDisposed()).isTrue();
            assertThat(composit.size()).isEqualTo(0);
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void dispose3() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
            task.dispose();
            FutureTask<Void> f1 = new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null);
            task.setFirst(f1);
            assertThat(f1.isCancelled()).isTrue();
            FutureTask<Void> f2 = new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null);
            task.setRest(f2);
            assertThat(f2.isCancelled()).isTrue();
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void disposeOnCurrentThread() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
            task.thread = Thread.currentThread();
            task.dispose();
            FutureTask<Void> f1 = new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null);
            task.setFirst(f1);
            assertThat(f1.isCancelled()).isTrue();
            FutureTask<Void> f2 = new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null);
            task.setRest(f2);
            assertThat(f2.isCancelled()).isTrue();
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void firstCancelRace() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            for (int i = 0; i < 10000; i++) {
                final InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
                final FutureTask<Void> f1 = new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null);
                Runnable r1 = () -> task.setFirst(f1);
                Runnable r2 = task::dispose;
                RaceTestUtils.race(r1, r2);
                Assert.assertTrue(f1.isCancelled());
                Assert.assertTrue(task.isDisposed());
            }
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }

    @Test
    public void restCancelRace() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Disposable.Composite composit = Disposables.composite();
        try {
            for (int i = 0; i < 10000; i++) {
                final InstantPeriodicWorkerTask task = new InstantPeriodicWorkerTask(InstantPeriodicWorkerTaskTest.errorRunnable, exec, composit);
                final FutureTask<Void> f1 = new FutureTask<Void>(InstantPeriodicWorkerTaskTest.emptyRunnable, null);
                Runnable r1 = () -> task.setRest(f1);
                Runnable r2 = task::dispose;
                RaceTestUtils.race(r1, r2);
                Assert.assertTrue(f1.isCancelled());
                Assert.assertTrue(task.isDisposed());
            }
        } finally {
            exec.shutdownNow();
            Schedulers.resetOnHandleError();
        }
    }
}

