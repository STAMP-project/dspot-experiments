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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RegexSetTest {
    @Test
    public void testEmpty() {
        RegexSet set = new RegexSet();
        Assertions.assertEquals(false, set.contains("foo"));
        Assertions.assertEquals(false, set.matches("foo"));
        Assertions.assertEquals(false, set.matches(""));
    }

    @Test
    public void testSimple() {
        RegexSet set = new RegexSet();
        set.add("foo.*");
        Assertions.assertEquals(true, set.contains("foo.*"));
        Assertions.assertEquals(true, set.matches("foo"));
        Assertions.assertEquals(true, set.matches("foobar"));
        Assertions.assertEquals(false, set.matches("bar"));
        Assertions.assertEquals(false, set.matches(""));
    }

    @Test
    public void testSimpleTerminated() {
        RegexSet set = new RegexSet();
        set.add("^foo.*$");
        Assertions.assertEquals(true, set.contains("^foo.*$"));
        Assertions.assertEquals(true, set.matches("foo"));
        Assertions.assertEquals(true, set.matches("foobar"));
        Assertions.assertEquals(false, set.matches("bar"));
        Assertions.assertEquals(false, set.matches(""));
    }

    @Test
    public void testCombined() {
        RegexSet set = new RegexSet();
        set.add("^foo.*$");
        set.add("bar");
        set.add("[a-z][0-9][a-z][0-9]");
        Assertions.assertEquals(true, set.contains("^foo.*$"));
        Assertions.assertEquals(true, set.matches("foo"));
        Assertions.assertEquals(true, set.matches("foobar"));
        Assertions.assertEquals(true, set.matches("bar"));
        Assertions.assertEquals(true, set.matches("c3p0"));
        Assertions.assertEquals(true, set.matches("r2d2"));
        Assertions.assertEquals(false, set.matches("wibble"));
        Assertions.assertEquals(false, set.matches("barfoo"));
        Assertions.assertEquals(false, set.matches("2b!b"));
        Assertions.assertEquals(false, set.matches(""));
    }
}

