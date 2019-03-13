/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.thread;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ReservedThreadExecutorTest {
    private static final int SIZE = 2;

    private static final Runnable NOOP = new Runnable() {
        @Override
        public void run() {
        }

        @Override
        public String toString() {
            return "NOOP!";
        }
    };

    private ReservedThreadExecutorTest.TestExecutor _executor;

    private ReservedThreadExecutor _reservedExecutor;

    @Test
    public void testStarted() {
        // Reserved threads are lazily started.
        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(0));
    }

    @Test
    public void testPending() throws Exception {
        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(0));
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            _reservedExecutor.tryExecute(ReservedThreadExecutorTest.NOOP);

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(ReservedThreadExecutorTest.SIZE));
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            _executor.startThread();

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(0));
        waitForAllAvailable();
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            MatcherAssert.assertThat(_reservedExecutor.tryExecute(new ReservedThreadExecutorTest.Task()), Matchers.is(true));

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(1));
        MatcherAssert.assertThat(_reservedExecutor.getAvailable(), Matchers.is(0));
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            MatcherAssert.assertThat(_reservedExecutor.tryExecute(ReservedThreadExecutorTest.NOOP), Matchers.is(false));

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(ReservedThreadExecutorTest.SIZE));
        MatcherAssert.assertThat(_reservedExecutor.getAvailable(), Matchers.is(0));
    }

    @Test
    public void testExecuted() throws Exception {
        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(0));
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            _reservedExecutor.tryExecute(ReservedThreadExecutorTest.NOOP);

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(ReservedThreadExecutorTest.SIZE));
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            _executor.startThread();

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(0));
        waitForAllAvailable();
        ReservedThreadExecutorTest.Task[] tasks = new ReservedThreadExecutorTest.Task[ReservedThreadExecutorTest.SIZE];
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++) {
            tasks[i] = new ReservedThreadExecutorTest.Task();
            MatcherAssert.assertThat(_reservedExecutor.tryExecute(tasks[i]), Matchers.is(true));
        }
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            tasks[i]._ran.await(10, TimeUnit.SECONDS);

        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(1));
        ReservedThreadExecutorTest.Task extra = new ReservedThreadExecutorTest.Task();
        MatcherAssert.assertThat(_reservedExecutor.tryExecute(extra), Matchers.is(false));
        MatcherAssert.assertThat(_executor._queue.size(), Matchers.is(2));
        Thread.sleep(500);
        MatcherAssert.assertThat(extra._ran.getCount(), Matchers.is(1L));
        for (int i = 0; i < (ReservedThreadExecutorTest.SIZE); i++)
            tasks[i]._complete.countDown();

        waitForAllAvailable();
    }

    @Test
    public void testShrink() throws Exception {
        final long IDLE = 1000;
        _reservedExecutor.stop();
        _reservedExecutor.setIdleTimeout(IDLE, TimeUnit.MILLISECONDS);
        _reservedExecutor.start();
        MatcherAssert.assertThat(_reservedExecutor.getAvailable(), Matchers.is(0));
        MatcherAssert.assertThat(_reservedExecutor.tryExecute(ReservedThreadExecutorTest.NOOP), Matchers.is(false));
        MatcherAssert.assertThat(_reservedExecutor.tryExecute(ReservedThreadExecutorTest.NOOP), Matchers.is(false));
        _executor.startThread();
        _executor.startThread();
        waitForAvailable(2);
        int available = _reservedExecutor.getAvailable();
        MatcherAssert.assertThat(available, Matchers.is(2));
        Thread.sleep(((5 * IDLE) / 2));
        MatcherAssert.assertThat(_reservedExecutor.getAvailable(), Matchers.is(0));
    }

    private static class TestExecutor implements Executor {
        private final Deque<Runnable> _queue = new ArrayDeque<>();

        @Override
        public void execute(Runnable task) {
            _queue.addLast(task);
        }

        public void startThread() {
            Runnable task = _queue.pollFirst();
            if (task != null)
                new Thread(task).start();

        }
    }

    private static class Task implements Runnable {
        private CountDownLatch _ran = new CountDownLatch(1);

        private CountDownLatch _complete = new CountDownLatch(1);

        @Override
        public void run() {
            _ran.countDown();
            try {
                _complete.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

