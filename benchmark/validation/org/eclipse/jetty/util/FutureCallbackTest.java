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
package org.eclipse.jetty.util;


import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FutureCallbackTest {
    @Test
    public void testNotDone() {
        FutureCallback fcb = new FutureCallback();
        Assertions.assertFalse(fcb.isDone());
        Assertions.assertFalse(fcb.isCancelled());
    }

    @Test
    public void testGetNotDone() throws Exception {
        FutureCallback fcb = new FutureCallback();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        try {
            fcb.get(500, TimeUnit.MILLISECONDS);
            Assertions.fail("Expected a TimeoutException");
        } catch (TimeoutException e) {
        }
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.greaterThan(50L));
    }

    @Test
    public void testDone() throws Exception {
        FutureCallback fcb = new FutureCallback();
        fcb.succeeded();
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertFalse(fcb.isCancelled());
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        Assertions.assertEquals(null, fcb.get());
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThan(500L));
    }

    @Test
    public void testGetDone() throws Exception {
        final FutureCallback fcb = new FutureCallback();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fcb.succeeded();
            }
        }).start();
        latch.await();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        Assertions.assertEquals(null, fcb.get(10000, TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.greaterThan(10L));
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThan(1000L));
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertFalse(fcb.isCancelled());
    }

    @Test
    public void testFailed() throws Exception {
        FutureCallback fcb = new FutureCallback();
        Exception ex = new Exception("FAILED");
        fcb.failed(ex);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertFalse(fcb.isCancelled());
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        try {
            fcb.get();
            Assertions.fail("Expected an ExecutionException");
        } catch (ExecutionException ee) {
            Assertions.assertEquals(ex, ee.getCause());
        }
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThan(100L));
    }

    @Test
    public void testGetFailed() throws Exception {
        final FutureCallback fcb = new FutureCallback();
        final Exception ex = new Exception("FAILED");
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fcb.failed(ex);
            }
        }).start();
        latch.await();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        try {
            fcb.get(10000, TimeUnit.MILLISECONDS);
            Assertions.fail("Expected an ExecutionException");
        } catch (ExecutionException ee) {
            Assertions.assertEquals(ex, ee.getCause());
        }
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.greaterThan(10L));
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThan(5000L));
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertFalse(fcb.isCancelled());
    }

    @Test
    public void testCancelled() throws Exception {
        FutureCallback fcb = new FutureCallback();
        fcb.cancel(true);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertTrue(fcb.isCancelled());
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        try {
            fcb.get();
            Assertions.fail("Expected a CancellationException");
        } catch (CancellationException e) {
            MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(CancellationException.class));
        }
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThan(100L));
    }

    @Test
    public void testGetCancelled() throws Exception {
        final FutureCallback fcb = new FutureCallback();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fcb.cancel(true);
            }
        }).start();
        latch.await();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        try {
            fcb.get(10000, TimeUnit.MILLISECONDS);
            Assertions.fail("Expected a CancellationException");
        } catch (CancellationException e) {
            MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(CancellationException.class));
        }
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.greaterThan(10L));
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThan(1000L));
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertTrue(fcb.isCancelled());
    }
}

