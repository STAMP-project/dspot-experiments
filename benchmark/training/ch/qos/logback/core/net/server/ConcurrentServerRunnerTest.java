/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.net.server;


import ch.qos.logback.core.net.mock.MockContext;
import ch.qos.logback.core.net.server.test.MockServerListener;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentServerRunnerTest {
    private static final int DELAY = 10000;

    private static final int SHORT_DELAY = 10;

    private MockContext context = new MockContext();

    private MockServerListener<MockClient> listener = new MockServerListener<MockClient>();

    private ExecutorService executor = Executors.newCachedThreadPool();

    private ConcurrentServerRunnerTest.InstrumentedConcurrentServerRunner runner = new ConcurrentServerRunnerTest.InstrumentedConcurrentServerRunner(listener, executor);

    @Test
    public void testStartStop() throws Exception {
        Assert.assertFalse(isRunning());
        executor.execute(runner);
        Assert.assertTrue(runner.awaitRunState(true, ConcurrentServerRunnerTest.DELAY));
        int retries = (ConcurrentServerRunnerTest.DELAY) / (ConcurrentServerRunnerTest.SHORT_DELAY);
        synchronized(listener) {
            while (((retries--) > 0) && ((listener.getWaiter()) == null)) {
                listener.wait(ConcurrentServerRunnerTest.SHORT_DELAY);
            } 
        }
        Assert.assertNotNull(listener.getWaiter());
        stop();
        Assert.assertTrue(listener.isClosed());
        Assert.assertFalse(runner.awaitRunState(false, ConcurrentServerRunnerTest.DELAY));
    }

    @Test
    public void testRunOneClient() throws Exception {
        executor.execute(runner);
        MockClient client = new MockClient();
        listener.addClient(client);
        int retries = (ConcurrentServerRunnerTest.DELAY) / (ConcurrentServerRunnerTest.SHORT_DELAY);
        synchronized(client) {
            while (((retries--) > 0) && (!(client.isRunning()))) {
                client.wait(ConcurrentServerRunnerTest.SHORT_DELAY);
            } 
        }
        Assert.assertTrue(runner.awaitRunState(true, ConcurrentServerRunnerTest.DELAY));
        client.close();
        stop();
    }

    @Test
    public void testRunManyClients() throws Exception {
        executor.execute(runner);
        int count = 10;
        while ((count--) > 0) {
            MockClient client = new MockClient();
            listener.addClient(client);
            int retries = (ConcurrentServerRunnerTest.DELAY) / (ConcurrentServerRunnerTest.SHORT_DELAY);
            synchronized(client) {
                while (((retries--) > 0) && (!(client.isRunning()))) {
                    client.wait(ConcurrentServerRunnerTest.SHORT_DELAY);
                } 
            }
            Assert.assertTrue(runner.awaitRunState(true, ConcurrentServerRunnerTest.DELAY));
        } 
        stop();
    }

    @Test
    public void testRunClientAndVisit() throws Exception {
        executor.execute(runner);
        MockClient client = new MockClient();
        listener.addClient(client);
        int retries = (ConcurrentServerRunnerTest.DELAY) / (ConcurrentServerRunnerTest.SHORT_DELAY);
        synchronized(client) {
            while (((retries--) > 0) && (!(client.isRunning()))) {
                client.wait(ConcurrentServerRunnerTest.SHORT_DELAY);
            } 
        }
        Assert.assertTrue(runner.awaitRunState(true, ConcurrentServerRunnerTest.DELAY));
        MockClientVisitor visitor = new MockClientVisitor();
        accept(visitor);
        Assert.assertSame(client, visitor.getLastVisited());
        stop();
    }

    static class InstrumentedConcurrentServerRunner extends ConcurrentServerRunner<MockClient> {
        private final Lock lock = new ReentrantLock();

        private final Condition runningCondition = lock.newCondition();

        public InstrumentedConcurrentServerRunner(ServerListener<MockClient> listener, Executor executor) {
            super(listener, executor);
        }

        @Override
        protected boolean configureClient(MockClient client) {
            return true;
        }

        @Override
        protected void setRunning(boolean running) {
            lock.lock();
            try {
                super.setRunning(running);
                runningCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public boolean awaitRunState(boolean state, long delay) throws InterruptedException {
            lock.lock();
            try {
                while ((isRunning()) != state) {
                    runningCondition.await(delay, TimeUnit.MILLISECONDS);
                } 
                return isRunning();
            } finally {
                lock.unlock();
            }
        }
    }
}

