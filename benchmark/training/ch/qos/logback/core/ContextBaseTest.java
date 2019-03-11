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
package ch.qos.logback.core;


import CoreConstants.CONTEXT_NAME_KEY;
import CoreConstants.DEFAULT_CONTEXT_NAME;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;


public class ContextBaseTest {
    private ContextBaseTest.InstrumentedLifeCycleManager lifeCycleManager = new ContextBaseTest.InstrumentedLifeCycleManager();

    private ContextBaseTest.InstrumentedContextBase context = new ContextBaseTest.InstrumentedContextBase(lifeCycleManager);

    @Test
    public void renameDefault() {
        context.setName(DEFAULT_CONTEXT_NAME);
        setName("hello");
    }

    @Test
    public void idempotentNameTest() {
        setName("hello");
        setName("hello");
    }

    @Test
    public void renameTest() {
        setName("hello");
        try {
            setName("x");
            Assert.fail("renaming is not allowed");
        } catch (IllegalStateException ise) {
        }
    }

    @Test
    public void resetTest() {
        setName("hello");
        putProperty("keyA", "valA");
        putObject("keyA", "valA");
        Assert.assertEquals("valA", getProperty("keyA"));
        Assert.assertEquals("valA", getObject("keyA"));
        MockLifeCycleComponent component = new MockLifeCycleComponent();
        register(component);
        Assert.assertSame(component, lifeCycleManager.getLastComponent());
        reset();
        Assert.assertNull(getProperty("keyA"));
        Assert.assertNull(getObject("keyA"));
        Assert.assertTrue(lifeCycleManager.isReset());
    }

    @Test
    public void contextNameProperty() {
        Assert.assertNull(context.getProperty(CONTEXT_NAME_KEY));
        String HELLO = "hello";
        setName(HELLO);
        Assert.assertEquals(HELLO, context.getProperty(CONTEXT_NAME_KEY));
        // good to have a raw reference to the "CONTEXT_NAME" as most clients would
        // not go through CoreConstants
        Assert.assertEquals(HELLO, getProperty("CONTEXT_NAME"));
    }

    private static class InstrumentedContextBase extends ContextBase {
        private final LifeCycleManager lifeCycleManager;

        public InstrumentedContextBase(LifeCycleManager lifeCycleManager) {
            this.lifeCycleManager = lifeCycleManager;
        }

        @Override
        protected LifeCycleManager getLifeCycleManager() {
            return lifeCycleManager;
        }
    }

    private static class InstrumentedLifeCycleManager extends LifeCycleManager {
        private LifeCycle lastComponent;

        private boolean reset;

        @Override
        public void register(LifeCycle component) {
            lastComponent = component;
            super.register(component);
        }

        @Override
        public void reset() {
            reset = true;
            reset();
        }

        public LifeCycle getLastComponent() {
            return lastComponent;
        }

        public boolean isReset() {
            return reset;
        }
    }

    @Test
    public void contextThreadpoolIsDaemonized() throws InterruptedException {
        ExecutorService execSvc = getExecutorService();
        final ArrayList<Thread> executingThreads = new ArrayList<Thread>();
        execSvc.execute(new Runnable() {
            @Override
            public void run() {
                synchronized(executingThreads) {
                    executingThreads.add(Thread.currentThread());
                    executingThreads.notifyAll();
                }
            }
        });
        synchronized(executingThreads) {
            while (executingThreads.isEmpty()) {
                executingThreads.wait();
            } 
        }
        Assert.assertTrue("executing thread should be a daemon thread.", executingThreads.get(0).isDaemon());
    }
}

