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


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test class for {@link MultiBackgroundInitializer}.
 */
public class MultiBackgroundInitializerTest {
    /**
     * Constant for the names of the child initializers.
     */
    private static final String CHILD_INIT = "childInitializer";

    /**
     * The initializer to be tested.
     */
    private MultiBackgroundInitializer initializer;

    /**
     * Tests addInitializer() if a null name is passed in. This should cause an
     * exception.
     */
    @Test
    public void testAddInitializerNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> initializer.addInitializer(null, new MultiBackgroundInitializerTest.ChildBackgroundInitializer()));
    }

    /**
     * Tests addInitializer() if a null initializer is passed in. This should
     * cause an exception.
     */
    @Test
    public void testAddInitializerNullInit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, null));
    }

    /**
     * Tests the background processing if there are no child initializers.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeNoChildren() throws ConcurrentException {
        Assertions.assertTrue(initializer.start(), "Wrong result of start()");
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = initializer.get();
        Assertions.assertTrue(res.initializerNames().isEmpty(), "Got child initializers");
        Assertions.assertTrue(initializer.getActiveExecutor().isShutdown(), "Executor not shutdown");
    }

    /**
     * Tests background processing if a temporary executor is used.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeTempExec() throws ConcurrentException {
        checkInitialize();
        Assertions.assertTrue(initializer.getActiveExecutor().isShutdown(), "Executor not shutdown");
    }

    /**
     * Tests background processing if an external executor service is provided.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeExternalExec() throws InterruptedException, ConcurrentException {
        final ExecutorService exec = Executors.newCachedThreadPool();
        try {
            initializer = new MultiBackgroundInitializer(exec);
            checkInitialize();
            Assertions.assertEquals(exec, initializer.getActiveExecutor(), "Wrong executor");
            Assertions.assertFalse(exec.isShutdown(), "Executor was shutdown");
        } finally {
            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests the behavior of initialize() if a child initializer has a specific
     * executor service. Then this service should not be overridden.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeChildWithExecutor() throws InterruptedException, ConcurrentException {
        final String initExec = "childInitializerWithExecutor";
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            final MultiBackgroundInitializerTest.ChildBackgroundInitializer c1 = new MultiBackgroundInitializerTest.ChildBackgroundInitializer();
            final MultiBackgroundInitializerTest.ChildBackgroundInitializer c2 = new MultiBackgroundInitializerTest.ChildBackgroundInitializer();
            c2.setExternalExecutor(exec);
            initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, c1);
            initializer.addInitializer(initExec, c2);
            initializer.start();
            initializer.get();
            checkChild(c1, initializer.getActiveExecutor());
            checkChild(c2, exec);
        } finally {
            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Tries to add another child initializer after the start() method has been
     * called. This should not be allowed.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testAddInitializerAfterStart() throws ConcurrentException {
        initializer.start();
        Assertions.assertThrows(IllegalStateException.class, () -> initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, new MultiBackgroundInitializerTest.ChildBackgroundInitializer()), "Could add initializer after start()!");
        initializer.get();
    }

    /**
     * Tries to query an unknown child initializer from the results object. This
     * should cause an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testResultGetInitializerUnknown() throws ConcurrentException {
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = checkInitialize();
        Assertions.assertThrows(NoSuchElementException.class, () -> res.getInitializer("unknown"));
    }

    /**
     * Tries to query the results of an unknown child initializer from the
     * results object. This should cause an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testResultGetResultObjectUnknown() throws ConcurrentException {
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = checkInitialize();
        Assertions.assertThrows(NoSuchElementException.class, () -> res.getResultObject("unknown"));
    }

    /**
     * Tries to query the exception of an unknown child initializer from the
     * results object. This should cause an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testResultGetExceptionUnknown() throws ConcurrentException {
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = checkInitialize();
        Assertions.assertThrows(NoSuchElementException.class, () -> res.getException("unknown"));
    }

    /**
     * Tries to query the exception flag of an unknown child initializer from
     * the results object. This should cause an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testResultIsExceptionUnknown() throws ConcurrentException {
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = checkInitialize();
        Assertions.assertThrows(NoSuchElementException.class, () -> res.isException("unknown"));
    }

    /**
     * Tests that the set with the names of the initializers cannot be modified.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testResultInitializerNamesModify() throws ConcurrentException {
        checkInitialize();
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = initializer.get();
        final Iterator<String> it = res.initializerNames().iterator();
        it.next();
        Assertions.assertThrows(UnsupportedOperationException.class, it::remove);
    }

    /**
     * Tests the behavior of the initializer if one of the child initializers
     * throws a runtime exception.
     */
    @Test
    public void testInitializeRuntimeEx() {
        final MultiBackgroundInitializerTest.ChildBackgroundInitializer child = new MultiBackgroundInitializerTest.ChildBackgroundInitializer();
        child.ex = new RuntimeException();
        initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, child);
        initializer.start();
        Exception ex = Assertions.assertThrows(Exception.class, initializer::get);
        Assertions.assertEquals(child.ex, ex, "Wrong exception");
    }

    /**
     * Tests the behavior of the initializer if one of the child initializers
     * throws a checked exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeEx() throws ConcurrentException {
        final MultiBackgroundInitializerTest.ChildBackgroundInitializer child = new MultiBackgroundInitializerTest.ChildBackgroundInitializer();
        child.ex = new Exception();
        initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, child);
        initializer.start();
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = initializer.get();
        Assertions.assertTrue(res.isException(MultiBackgroundInitializerTest.CHILD_INIT), "No exception flag");
        Assertions.assertNull(res.getResultObject(MultiBackgroundInitializerTest.CHILD_INIT), "Got a results object");
        final ConcurrentException cex = res.getException(MultiBackgroundInitializerTest.CHILD_INIT);
        Assertions.assertEquals(child.ex, cex.getCause(), "Wrong cause");
    }

    /**
     * Tests the isSuccessful() method of the result object if no child
     * initializer has thrown an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeResultsIsSuccessfulTrue() throws ConcurrentException {
        final MultiBackgroundInitializerTest.ChildBackgroundInitializer child = new MultiBackgroundInitializerTest.ChildBackgroundInitializer();
        initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, child);
        initializer.start();
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = initializer.get();
        Assertions.assertTrue(res.isSuccessful(), "Wrong success flag");
    }

    /**
     * Tests the isSuccessful() method of the result object if at least one
     * child initializer has thrown an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeResultsIsSuccessfulFalse() throws ConcurrentException {
        final MultiBackgroundInitializerTest.ChildBackgroundInitializer child = new MultiBackgroundInitializerTest.ChildBackgroundInitializer();
        child.ex = new Exception();
        initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, child);
        initializer.start();
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = initializer.get();
        Assertions.assertFalse(res.isSuccessful(), "Wrong success flag");
    }

    /**
     * Tests whether MultiBackgroundInitializers can be combined in a nested
     * way.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException
     * 		so we don't have to catch it
     */
    @Test
    public void testInitializeNested() throws ConcurrentException {
        final String nameMulti = "multiChildInitializer";
        initializer.addInitializer(MultiBackgroundInitializerTest.CHILD_INIT, new MultiBackgroundInitializerTest.ChildBackgroundInitializer());
        final MultiBackgroundInitializer mi2 = new MultiBackgroundInitializer();
        final int count = 3;
        for (int i = 0; i < count; i++) {
            mi2.addInitializer(((MultiBackgroundInitializerTest.CHILD_INIT) + i), new MultiBackgroundInitializerTest.ChildBackgroundInitializer());
        }
        initializer.addInitializer(nameMulti, mi2);
        initializer.start();
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res = initializer.get();
        final ExecutorService exec = initializer.getActiveExecutor();
        checkChild(res.getInitializer(MultiBackgroundInitializerTest.CHILD_INIT), exec);
        final MultiBackgroundInitializer.MultiBackgroundInitializerResults res2 = ((MultiBackgroundInitializer.MultiBackgroundInitializerResults) (res.getResultObject(nameMulti)));
        Assertions.assertEquals(count, res2.initializerNames().size(), "Wrong number of initializers");
        for (int i = 0; i < count; i++) {
            checkChild(res2.getInitializer(((MultiBackgroundInitializerTest.CHILD_INIT) + i)), exec);
        }
        Assertions.assertTrue(exec.isShutdown(), "Executor not shutdown");
    }

    /**
     * A concrete implementation of {@code BackgroundInitializer} used for
     * defining background tasks for {@code MultiBackgroundInitializer}.
     */
    private static class ChildBackgroundInitializer extends BackgroundInitializer<Integer> {
        /**
         * Stores the current executor service.
         */
        volatile ExecutorService currentExecutor;

        /**
         * A counter for the invocations of initialize().
         */
        volatile int initializeCalls;

        /**
         * An exception to be thrown by initialize().
         */
        Exception ex;

        /**
         * Records this invocation. Optionally throws an exception.
         */
        @Override
        protected Integer initialize() throws Exception {
            currentExecutor = getActiveExecutor();
            (initializeCalls)++;
            if ((ex) != null) {
                throw ex;
            }
            return Integer.valueOf(initializeCalls);
        }
    }
}

