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
package org.eclipse.jetty.util.component;


import AbstractLifeCycle.AbstractLifeCycleListener;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LifeCycleListenerTest {
    static Exception cause = new Exception("expected test exception");

    @Test
    public void testStart() throws Exception {
        LifeCycleListenerTest.TestLifeCycle lifecycle = new LifeCycleListenerTest.TestLifeCycle();
        LifeCycleListenerTest.TestListener listener = new LifeCycleListenerTest.TestListener();
        addLifeCycleListener(listener);
        lifecycle.setCause(LifeCycleListenerTest.cause);
        try (StacklessLogging stackless = new StacklessLogging(AbstractLifeCycle.class)) {
            start();
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertEquals(LifeCycleListenerTest.cause, e);
            Assertions.assertEquals(LifeCycleListenerTest.cause, listener.getCause());
        }
        lifecycle.setCause(null);
        start();
        // check that the starting event has been thrown
        Assertions.assertTrue(listener.starting, "The staring event didn't occur");
        // check that the started event has been thrown
        Assertions.assertTrue(listener.started, "The started event didn't occur");
        // check that the starting event occurs before the started event
        Assertions.assertTrue(((listener.startingTime) <= (listener.startedTime)), "The starting event must occur before the started event");
        // check that the lifecycle's state is started
        Assertions.assertTrue(isStarted(), "The lifecycle state is not started");
    }

    @Test
    public void testStop() throws Exception {
        LifeCycleListenerTest.TestLifeCycle lifecycle = new LifeCycleListenerTest.TestLifeCycle();
        LifeCycleListenerTest.TestListener listener = new LifeCycleListenerTest.TestListener();
        addLifeCycleListener(listener);
        // need to set the state to something other than stopped or stopping or
        // else
        // stop() will return without doing anything
        start();
        lifecycle.setCause(LifeCycleListenerTest.cause);
        try (StacklessLogging stackless = new StacklessLogging(AbstractLifeCycle.class)) {
            stop();
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertEquals(LifeCycleListenerTest.cause, e);
            Assertions.assertEquals(LifeCycleListenerTest.cause, listener.getCause());
        }
        lifecycle.setCause(null);
        stop();
        // check that the stopping event has been thrown
        Assertions.assertTrue(listener.stopping, "The stopping event didn't occur");
        // check that the stopped event has been thrown
        Assertions.assertTrue(listener.stopped, "The stopped event didn't occur");
        // check that the stopping event occurs before the stopped event
        Assertions.assertTrue(((listener.stoppingTime) <= (listener.stoppedTime)), "The stopping event must occur before the stopped event");
        // System.out.println("STOPING TIME : " + listener.stoppingTime + " : " + listener.stoppedTime);
        // check that the lifecycle's state is stopped
        Assertions.assertTrue(isStopped(), "The lifecycle state is not stooped");
    }

    @Test
    public void testRemoveLifecycleListener() throws Exception {
        LifeCycleListenerTest.TestLifeCycle lifecycle = new LifeCycleListenerTest.TestLifeCycle();
        LifeCycleListenerTest.TestListener listener = new LifeCycleListenerTest.TestListener();
        addLifeCycleListener(listener);
        start();
        Assertions.assertTrue(listener.starting, "The starting event didn't occur");
        removeLifeCycleListener(listener);
        stop();
        Assertions.assertFalse(listener.stopping, "The stopping event occurred");
    }

    private static class TestLifeCycle extends AbstractLifeCycle {
        Exception cause;

        private TestLifeCycle() {
        }

        @Override
        protected void doStart() throws Exception {
            if ((cause) != null)
                throw cause;

            super.doStart();
        }

        @Override
        protected void doStop() throws Exception {
            if ((cause) != null)
                throw cause;

            super.doStop();
        }

        public void setCause(Exception e) {
            cause = e;
        }
    }

    private class TestListener extends AbstractLifeCycle.AbstractLifeCycleListener {
        @SuppressWarnings("unused")
        private boolean failure = false;

        private boolean started = false;

        private boolean starting = false;

        private boolean stopped = false;

        private boolean stopping = false;

        private long startedTime;

        private long startingTime;

        private long stoppedTime;

        private long stoppingTime;

        private Throwable cause = null;

        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
            failure = true;
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }

        public void lifeCycleStarted(LifeCycle event) {
            started = true;
            startedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        }

        public void lifeCycleStarting(LifeCycle event) {
            starting = true;
            startingTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            // need to sleep to make sure the starting and started times are not
            // the same
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void lifeCycleStopped(LifeCycle event) {
            stopped = true;
            stoppedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        }

        public void lifeCycleStopping(LifeCycle event) {
            stopping = true;
            stoppingTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            // need to sleep to make sure the stopping and stopped times are not
            // the same
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

