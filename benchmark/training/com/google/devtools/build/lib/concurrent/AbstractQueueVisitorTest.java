/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.concurrent;


import ErrorClassification.CRITICAL;
import ErrorClassification.NOT_CRITICAL;
import ErrorClassifier.DEFAULT;
import com.google.common.util.concurrent.SettableFuture;
import com.google.devtools.build.lib.concurrent.ErrorClassifier.ErrorClassification;
import com.google.devtools.build.lib.testutil.TestThread;
import com.google.devtools.build.lib.testutil.TestUtils;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ErrorClassifier.DEFAULT;
import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;


/**
 * Tests for AbstractQueueVisitor.
 */
@RunWith(JUnit4.class)
public class AbstractQueueVisitorTest {
    private static final RuntimeException THROWABLE = new RuntimeException();

    @Test
    public void simpleCounter() throws Exception {
        AbstractQueueVisitorTest.CountingQueueVisitor counter = new AbstractQueueVisitorTest.CountingQueueVisitor();
        counter.enqueue();
        /* interruptWorkers= */
        awaitQuiescence(false);
        assertThat(counter.getCount()).isSameAs(10);
    }

    @Test
    public void externalDep() throws Exception {
        SettableFuture<Object> future = SettableFuture.create();
        AbstractQueueVisitor counter = /* parallelism= */
        /* keepAliveTime= */
        /* failFastOnException= */
        new AbstractQueueVisitor(2, 3L, TimeUnit.SECONDS, true, "FOO-BAR", DEFAULT);
        counter.dependOnFuture(future);
        new Thread(() -> {
            try {
                Thread.sleep(5);
                future.set(new Object());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        /* interruptWorkers= */
        counter.awaitQuiescence(false);
    }

    @Test
    public void callerOwnedPool() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        assertThat(executor.getActiveCount()).isSameAs(0);
        AbstractQueueVisitorTest.CountingQueueVisitor counter = new AbstractQueueVisitorTest.CountingQueueVisitor(executor);
        counter.enqueue();
        /* interruptWorkers= */
        awaitQuiescence(false);
        assertThat(counter.getCount()).isSameAs(10);
        executor.shutdown();
        assertThat(executor.awaitTermination(TestUtils.WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void doubleCounter() throws Exception {
        AbstractQueueVisitorTest.CountingQueueVisitor counter = new AbstractQueueVisitorTest.CountingQueueVisitor();
        counter.enqueue();
        counter.enqueue();
        /* interruptWorkers= */
        awaitQuiescence(false);
        assertThat(counter.getCount()).isSameAs(10);
    }

    @Test
    public void exceptionFromWorkerThread() {
        final RuntimeException myException = new IllegalStateException();
        AbstractQueueVisitorTest.ConcreteQueueVisitor visitor = new AbstractQueueVisitorTest.ConcreteQueueVisitor();
        visitor.execute(new Runnable() {
            @Override
            public void run() {
                throw myException;
            }
        });
        try {
            // The exception from the worker thread should be
            // re-thrown from the main thread.
            /* interruptWorkers= */
            awaitQuiescence(false);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isSameAs(myException);
        }
    }

    // Regression test for "AbstractQueueVisitor loses track of jobs if thread allocation fails".
    @Test
    public void threadPoolThrowsSometimes() throws Exception {
        // In certain cases (for example, if the address space is almost entirely consumed by a huge
        // JVM heap), thread allocation can fail with an OutOfMemoryError. If the queue visitor
        // does not handle this gracefully, we lose track of tasks and hang the visitor indefinitely.
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()) {
            private final AtomicLong count = new AtomicLong();

            @Override
            public void execute(Runnable command) {
                long count = this.count.incrementAndGet();
                if (count == 6) {
                    throw new Error("Could not create thread (fakeout)");
                }
                super.execute(command);
            }
        };
        AbstractQueueVisitorTest.CountingQueueVisitor counter = new AbstractQueueVisitorTest.CountingQueueVisitor(executor);
        counter.enqueue();
        try {
            /* interruptWorkers= */
            awaitQuiescence(false);
            Assert.fail();
        } catch (Error expected) {
            assertThat(expected).hasMessage("Could not create thread (fakeout)");
        }
        assertThat(counter.getCount()).isSameAs(5);
        executor.shutdown();
        assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
    }

    // Regression test to make sure that AbstractQueueVisitor doesn't swallow unchecked exceptions if
    // it is interrupted concurrently with the unchecked exception being thrown.
    @Test
    public void interruptAndThrownIsInterruptedAndThrown() throws Exception {
        final AbstractQueueVisitorTest.ConcreteQueueVisitor visitor = new AbstractQueueVisitorTest.ConcreteQueueVisitor();
        // Use a latch to make sure the thread gets a chance to start.
        final CountDownLatch threadStarted = new CountDownLatch(1);
        visitor.execute(new Runnable() {
            @Override
            public void run() {
                threadStarted.countDown();
                assertThat(awaitUninterruptibly(getInterruptionLatchForTestingOnly(), 2, TimeUnit.SECONDS)).isTrue();
                throw AbstractQueueVisitorTest.THROWABLE;
            }
        });
        assertThat(threadStarted.await(TestUtils.WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        // Interrupt will not be processed until work starts.
        Thread.currentThread().interrupt();
        try {
            /* interruptWorkers= */
            awaitQuiescence(true);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isEqualTo(AbstractQueueVisitorTest.THROWABLE);
            assertThat(Thread.interrupted()).isTrue();
        }
    }

    @Test
    public void interruptionWithoutInterruptingWorkers() throws Exception {
        final Thread mainThread = Thread.currentThread();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final boolean[] workerThreadCompleted = new boolean[]{ false };
        final AbstractQueueVisitorTest.ConcreteQueueVisitor visitor = new AbstractQueueVisitorTest.ConcreteQueueVisitor();
        visitor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    latch1.countDown();
                    latch2.await();
                    workerThreadCompleted[0] = true;
                } catch (InterruptedException e) {
                    // Do not set workerThreadCompleted to true
                }
            }
        });
        TestThread interrupterThread = new TestThread() {
            @Override
            public void runTest() throws Exception {
                latch1.await();
                mainThread.interrupt();
                assertThat(getInterruptionLatchForTestingOnly().await(TestUtils.WAIT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)).isTrue();
                latch2.countDown();
            }
        };
        interrupterThread.start();
        try {
            /* interruptWorkers= */
            awaitQuiescence(false);
            Assert.fail();
        } catch (InterruptedException e) {
            // Expected.
        }
        interrupterThread.joinAndAssertState(400);
        assertThat(workerThreadCompleted[0]).isTrue();
    }

    @Test
    public void interruptionWithInterruptingWorkers() throws Exception {
        AbstractQueueVisitorTest.assertInterruptWorkers(null);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        AbstractQueueVisitorTest.assertInterruptWorkers(executor);
        executor.shutdown();
        executor.awaitTermination(TestUtils.WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    public void failFast() throws Exception {
        // In failFast mode, we only run actions queued before the exception.
        AbstractQueueVisitorTest.assertFailFast(null, true, false, "a", "b");
        // In !failFast mode, we complete all queued actions.
        AbstractQueueVisitorTest.assertFailFast(null, false, false, "a", "b", "1", "2");
        // Now check fail-fast on interrupt:
        AbstractQueueVisitorTest.assertFailFast(null, false, true, "a", "b");
    }

    @Test
    public void failFastNoShutdown() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        // In failFast mode, we only run actions queued before the exception.
        AbstractQueueVisitorTest.assertFailFast(executor, true, false, "a", "b");
        // In !failFast mode, we complete all queued actions.
        AbstractQueueVisitorTest.assertFailFast(executor, false, false, "a", "b", "1", "2");
        // Now check fail-fast on interrupt:
        AbstractQueueVisitorTest.assertFailFast(executor, false, true, "a", "b");
        executor.shutdown();
        assertThat(executor.awaitTermination(TestUtils.WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void jobIsInterruptedWhenOtherFails() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        final AbstractQueueVisitor visitor = AbstractQueueVisitorTest.createQueueVisitorWithConstantErrorClassification(executor, CRITICAL);
        final CountDownLatch latch1 = new CountDownLatch(1);
        final AtomicBoolean wasInterrupted = new AtomicBoolean(false);
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                latch1.countDown();
                try {
                    // Interruption is expected during a sleep. There is no sense in fail or assert call
                    // because exception is going to be swallowed inside AbstractQueueVisitor.
                    // We are using wasInterrupted flag to assert in the end of test.
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    wasInterrupted.set(true);
                }
            }
        };
        visitor.execute(r1);
        latch1.await();
        visitor.execute(AbstractQueueVisitorTest.throwingRunnable());
        CountDownLatch exnLatch = visitor.getExceptionLatchForTestingOnly();
        try {
            /* interruptWorkers= */
            visitor.awaitQuiescence(true);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isSameAs(AbstractQueueVisitorTest.THROWABLE);
        }
        assertThat(wasInterrupted.get()).isTrue();
        assertThat(executor.isShutdown()).isTrue();
        assertThat(exnLatch.await(0, TimeUnit.MILLISECONDS)).isTrue();
    }

    @Test
    public void javaErrorConsideredCriticalNoMatterWhat() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        final Error error = new Error("bad!");
        AbstractQueueVisitor visitor = AbstractQueueVisitorTest.createQueueVisitorWithConstantErrorClassification(executor, NOT_CRITICAL);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean sleepFinished = new AtomicBoolean(false);
        final AtomicBoolean sleepInterrupted = new AtomicBoolean(false);
        Runnable errorRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await(TestUtils.WAIT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
                } catch (InterruptedException expected) {
                    // Should only happen if the test itself is interrupted.
                }
                throw error;
            }
        };
        Runnable sleepRunnable = new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    Thread.sleep(TestUtils.WAIT_TIMEOUT_MILLISECONDS);
                    sleepFinished.set(true);
                } catch (InterruptedException unexpected) {
                    sleepInterrupted.set(true);
                }
            }
        };
        CountDownLatch exnLatch = visitor.getExceptionLatchForTestingOnly();
        visitor.execute(errorRunnable);
        visitor.execute(sleepRunnable);
        Error thrownError = null;
        // Interrupt workers on a critical error. That way we can test that visitor.work doesn't wait
        // for all workers to finish if one of them already had a critical error.
        try {
            /* interruptWorkers= */
            visitor.awaitQuiescence(true);
        } catch (Error e) {
            thrownError = e;
        }
        assertThat(sleepInterrupted.get()).isTrue();
        assertThat(sleepFinished.get()).isFalse();
        assertThat(thrownError).isEqualTo(error);
        assertThat(exnLatch.await(0, TimeUnit.MILLISECONDS)).isTrue();
    }

    private static class ClassifiedException extends RuntimeException {
        private final ErrorClassification classification;

        private ClassifiedException(ErrorClassification classification) {
            this.classification = classification;
        }
    }

    @Test
    public void mostSevereErrorPropagated() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        final AbstractQueueVisitorTest.ClassifiedException criticalException = new AbstractQueueVisitorTest.ClassifiedException(ErrorClassification.CRITICAL);
        final AbstractQueueVisitorTest.ClassifiedException criticalAndLogException = new AbstractQueueVisitorTest.ClassifiedException(ErrorClassification.CRITICAL_AND_LOG);
        final ErrorClassifier errorClassifier = new ErrorClassifier() {
            @Override
            protected ErrorClassification classifyException(Exception e) {
                return e instanceof AbstractQueueVisitorTest.ClassifiedException ? ((AbstractQueueVisitorTest.ClassifiedException) (e)).classification : ErrorClassification.NOT_CRITICAL;
            }
        };
        AbstractQueueVisitor visitor = /* shutdownOnCompletion= */
        /* failFastOnException= */
        new AbstractQueueVisitor(executor, true, false, errorClassifier);
        final CountDownLatch exnLatch = visitor.getExceptionLatchForTestingOnly();
        Runnable criticalExceptionRunnable = new Runnable() {
            @Override
            public void run() {
                throw criticalException;
            }
        };
        Runnable criticalAndLogExceptionRunnable = new Runnable() {
            @Override
            public void run() {
                // Wait for the critical exception to be thrown. There's a benign race between our 'await'
                // call completing because the exception latch was counted down, and our thread being
                // interrupted by AbstractQueueVisitor because the critical error was encountered. This is
                // completely fine; all that matters is that we have a chance to throw our error _after_
                // the previous one was thrown by the other Runnable.
                try {
                    exnLatch.await();
                } catch (InterruptedException e) {
                    // Ignored.
                }
                throw criticalAndLogException;
            }
        };
        visitor.execute(criticalExceptionRunnable);
        visitor.execute(criticalAndLogExceptionRunnable);
        AbstractQueueVisitorTest.ClassifiedException exn = null;
        try {
            /* interruptWorkers= */
            visitor.awaitQuiescence(true);
        } catch (AbstractQueueVisitorTest.ClassifiedException e) {
            exn = e;
        }
        assertThat(exn).isEqualTo(criticalAndLogException);
    }

    private static class CountingQueueVisitor extends AbstractQueueVisitor {
        private static final String THREAD_NAME = "BlazeTest CountingQueueVisitor";

        private int theInt = 0;

        private final Object lock = new Object();

        public CountingQueueVisitor() {
            /* parallelism= */
            /* keepAliveTime= */
            /* failFastOnException= */
            super(5, 3L, TimeUnit.SECONDS, false, AbstractQueueVisitorTest.CountingQueueVisitor.THREAD_NAME, DEFAULT);
        }

        CountingQueueVisitor(ThreadPoolExecutor executor) {
            super(executor, false, true, DEFAULT);
        }

        public void enqueue() {
            execute(new Runnable() {
                @Override
                public void run() {
                    synchronized(lock) {
                        if ((theInt) < 10) {
                            (theInt)++;
                            enqueue();
                        }
                    }
                }
            });
        }

        public int getCount() {
            return theInt;
        }
    }

    private static class ConcreteQueueVisitor extends AbstractQueueVisitor {
        private static final String THREAD_NAME = "BlazeTest ConcreteQueueVisitor";

        ConcreteQueueVisitor() {
            /* failFastOnException= */
            super(5, 3L, TimeUnit.SECONDS, false, AbstractQueueVisitorTest.ConcreteQueueVisitor.THREAD_NAME, DEFAULT);
        }

        ConcreteQueueVisitor(boolean failFast) {
            super(5, 3L, TimeUnit.SECONDS, failFast, AbstractQueueVisitorTest.ConcreteQueueVisitor.THREAD_NAME, DEFAULT);
        }

        ConcreteQueueVisitor(ThreadPoolExecutor executor, boolean failFast) {
            /* shutdownOnCompletion= */
            super(executor, false, failFast, DEFAULT);
        }
    }
}

