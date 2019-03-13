/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.concurrent;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class BackgroundInitializerTest {
    /**
     * Tests whether initialize() is invoked.
     */
    @Test
    public void testInitialize() throws ConcurrentException {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        init.start();
        checkInitialize(init);
    }

    /**
     * Tries to obtain the executor before start(). It should not have been
     * initialized yet.
     */
    @Test
    public void testGetActiveExecutorBeforeStart() {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        Assertions.assertNull(init.getActiveExecutor(), "Got an executor");
    }

    /**
     * Tests whether an external executor is correctly detected.
     */
    @Test
    public void testGetActiveExecutorExternal() throws InterruptedException, ConcurrentException {
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl(exec);
            init.start();
            Assertions.assertSame(exec, init.getActiveExecutor(), "Wrong executor");
            checkInitialize(init);
        } finally {
            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests getActiveExecutor() for a temporary executor.
     */
    @Test
    public void testGetActiveExecutorTemp() throws ConcurrentException {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        init.start();
        Assertions.assertNotNull(init.getActiveExecutor(), "No active executor");
        checkInitialize(init);
    }

    /**
     * Tests the execution of the background task if a temporary executor has to
     * be created.
     */
    @Test
    public void testInitializeTempExecutor() throws ConcurrentException {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        Assertions.assertTrue(init.start(), "Wrong result of start()");
        checkInitialize(init);
        Assertions.assertTrue(init.getActiveExecutor().isShutdown(), "Executor not shutdown");
    }

    /**
     * Tests whether an external executor can be set using the
     * setExternalExecutor() method.
     */
    @Test
    public void testSetExternalExecutor() throws ConcurrentException {
        final ExecutorService exec = Executors.newCachedThreadPool();
        try {
            final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
            init.setExternalExecutor(exec);
            Assertions.assertEquals(exec, init.getExternalExecutor(), "Wrong executor service");
            Assertions.assertTrue(init.start(), "Wrong result of start()");
            Assertions.assertSame(exec, init.getActiveExecutor(), "Wrong active executor");
            checkInitialize(init);
            Assertions.assertFalse(exec.isShutdown(), "Executor was shutdown");
        } finally {
            exec.shutdown();
        }
    }

    /**
     * Tests that setting an executor after start() causes an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		because the test implementation may throw it
     */
    @Test
    public void testSetExternalExecutorAfterStart() throws InterruptedException, ConcurrentException {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        init.start();
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            Assertions.assertThrows(IllegalStateException.class, () -> init.setExternalExecutor(exec));
            init.get();
        } finally {
            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests invoking start() multiple times. Only the first invocation should
     * have an effect.
     */
    @Test
    public void testStartMultipleTimes() throws ConcurrentException {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        Assertions.assertTrue(init.start(), "Wrong result for start()");
        for (int i = 0; i < 10; i++) {
            Assertions.assertFalse(init.start(), "Could start again");
        }
        checkInitialize(init);
    }

    /**
     * Tests calling get() before start(). This should cause an exception.
     */
    @Test
    public void testGetBeforeStart() {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        Assertions.assertThrows(IllegalStateException.class, init::get);
    }

    /**
     * Tests the get() method if background processing causes a runtime
     * exception.
     */
    @Test
    public void testGetRuntimeException() {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        final RuntimeException rex = new RuntimeException();
        init.ex = rex;
        init.start();
        Exception ex = Assertions.assertThrows(Exception.class, init::get);
        Assertions.assertEquals(rex, ex, "Runtime exception not thrown");
    }

    /**
     * Tests the get() method if background processing causes a checked
     * exception.
     */
    @Test
    public void testGetCheckedException() {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        final Exception ex = new Exception();
        init.ex = ex;
        init.start();
        ConcurrentException cex = Assertions.assertThrows(ConcurrentException.class, init::get);
        Assertions.assertEquals(ex, cex.getCause(), "Exception not thrown");
    }

    /**
     * Tests the get() method if waiting for the initialization is interrupted.
     *
     * @throws java.lang.InterruptedException
     * 		because we're making use of Java's concurrent API
     */
    @Test
    public void testGetInterruptedException() throws InterruptedException {
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl(exec);
        final CountDownLatch latch1 = new CountDownLatch(1);
        init.shouldSleep = true;
        init.start();
        final AtomicReference<InterruptedException> iex = new AtomicReference<>();
        final Thread getThread = new Thread() {
            @Override
            public void run() {
                try {
                    init.get();
                } catch (final ConcurrentException cex) {
                    if ((cex.getCause()) instanceof InterruptedException) {
                        iex.set(((InterruptedException) (cex.getCause())));
                    }
                } finally {
                    Assertions.assertTrue(isInterrupted(), "Thread not interrupted");
                    latch1.countDown();
                }
            }
        };
        getThread.start();
        getThread.interrupt();
        latch1.await();
        exec.shutdownNow();
        exec.awaitTermination(1, TimeUnit.SECONDS);
        Assertions.assertNotNull(iex.get(), "No interrupted exception");
    }

    /**
     * Tests isStarted() before start() was called.
     */
    @Test
    public void testIsStartedFalse() {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        Assertions.assertFalse(init.isStarted(), "Already started");
    }

    /**
     * Tests isStarted() after start().
     */
    @Test
    public void testIsStartedTrue() {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        init.start();
        Assertions.assertTrue(init.isStarted(), "Not started");
    }

    /**
     * Tests isStarted() after the background task has finished.
     */
    @Test
    public void testIsStartedAfterGet() throws ConcurrentException {
        final BackgroundInitializerTest.BackgroundInitializerTestImpl init = new BackgroundInitializerTest.BackgroundInitializerTestImpl();
        init.start();
        checkInitialize(init);
        Assertions.assertTrue(init.isStarted(), "Not started");
    }

    /**
     * A concrete implementation of BackgroundInitializer. It also overloads
     * some methods that simplify testing.
     */
    private static class BackgroundInitializerTestImpl extends BackgroundInitializer<Integer> {
        /**
         * An exception to be thrown by initialize().
         */
        Exception ex;

        /**
         * A flag whether the background task should sleep a while.
         */
        boolean shouldSleep;

        /**
         * The number of invocations of initialize().
         */
        volatile int initializeCalls;

        BackgroundInitializerTestImpl() {
            super();
        }

        BackgroundInitializerTestImpl(final ExecutorService exec) {
            super(exec);
        }

        /**
         * Records this invocation. Optionally throws an exception or sleeps a
         * while.
         *
         * @throws Exception
         * 		in case of an error
         */
        @Override
        protected Integer initialize() throws Exception {
            if ((ex) != null) {
                throw ex;
            }
            if (shouldSleep) {
                Thread.sleep(60000L);
            }
            return Integer.valueOf((++(initializeCalls)));
        }
    }
}

