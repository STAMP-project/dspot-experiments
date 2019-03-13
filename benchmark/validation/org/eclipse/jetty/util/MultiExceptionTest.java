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


import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MultiExceptionTest {
    @Test
    public void testEmpty() throws Exception {
        MultiException me = new MultiException();
        Assertions.assertEquals(0, me.size());
        me.ifExceptionThrow();
        me.ifExceptionThrowMulti();
        me.ifExceptionThrowRuntime();
        me.ifExceptionThrowSuppressed();
        Assertions.assertEquals(0, me.getStackTrace().length, "Stack trace should not be filled out");
    }

    @Test
    public void testOne() throws Exception {
        MultiException me = new MultiException();
        IOException io = new IOException("one");
        me.add(io);
        Assertions.assertEquals(1, me.size());
        // TODO: convert to assertThrows chain
        try {
            me.ifExceptionThrow();
            Assertions.assertTrue(false);
        } catch (IOException e) {
            Assertions.assertTrue((e == io));
        }
        try {
            me.ifExceptionThrowMulti();
            Assertions.assertTrue(false);
        } catch (MultiException e) {
            Assertions.assertTrue((e instanceof MultiException));
        }
        try {
            me.ifExceptionThrowRuntime();
            Assertions.assertTrue(false);
        } catch (RuntimeException e) {
            Assertions.assertTrue(((e.getCause()) == io));
        }
        try {
            me.ifExceptionThrowSuppressed();
            Assertions.assertTrue(false);
        } catch (IOException e) {
            Assertions.assertTrue((e == io));
        }
        me = new MultiException();
        RuntimeException run = new RuntimeException("one");
        me.add(run);
        try {
            me.ifExceptionThrowRuntime();
            Assertions.assertTrue(false);
        } catch (RuntimeException e) {
            Assertions.assertTrue((run == e));
        }
        Assertions.assertEquals(0, me.getStackTrace().length, "Stack trace should not be filled out");
    }

    @Test
    public void testTwo() throws Exception {
        MultiException me = multiExceptionWithIoRt();
        try {
            me.ifExceptionThrow();
            Assertions.assertTrue(false);
        } catch (MultiException e) {
            Assertions.assertTrue((e instanceof MultiException));
            Assertions.assertTrue(((e.getStackTrace().length) > 0));
        }
        me = multiExceptionWithIoRt();
        try {
            me.ifExceptionThrowMulti();
            Assertions.assertTrue(false);
        } catch (MultiException e) {
            Assertions.assertTrue((e instanceof MultiException));
            Assertions.assertTrue(((e.getStackTrace().length) > 0));
        }
        me = multiExceptionWithIoRt();
        try {
            me.ifExceptionThrowRuntime();
            Assertions.assertTrue(false);
        } catch (RuntimeException e) {
            Assertions.assertTrue(((e.getCause()) instanceof MultiException));
            Assertions.assertTrue(((e.getStackTrace().length) > 0));
        }
        me = multiExceptionWithRtIo();
        try {
            me.ifExceptionThrowRuntime();
            Assertions.assertTrue(false);
        } catch (RuntimeException e) {
            MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(MultiException.class));
            Assertions.assertTrue(((e.getStackTrace().length) > 0));
        }
        me = multiExceptionWithRtIo();
        try {
            me.ifExceptionThrowSuppressed();
            Assertions.assertTrue(false);
        } catch (RuntimeException e) {
            MatcherAssert.assertThat(e.getCause(), Matchers.is(Matchers.nullValue()));
            Assertions.assertEquals(1, e.getSuppressed().length, 1);
            Assertions.assertEquals(IOException.class, e.getSuppressed()[0].getClass());
        }
    }

    @Test
    public void testCause() throws Exception {
        MultiException me = new MultiException();
        IOException io = new IOException("one");
        RuntimeException run = new RuntimeException("two");
        me.add(io);
        me.add(run);
        try {
            me.ifExceptionThrow();
        } catch (MultiException e) {
            Assertions.assertEquals(io, e.getCause());
            Assertions.assertEquals(2, e.size());
        }
    }
}

