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


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class IncludeExcludeTest {
    @Test
    public void testEmpty() {
        IncludeExclude<String> ie = new IncludeExclude();
        MatcherAssert.assertThat("Empty IncludeExclude", ie.size(), Matchers.is(0));
        MatcherAssert.assertThat("Matches 'foo'", ie.test("foo"), Matchers.is(true));
    }

    @Test
    public void testIncludeOnly() {
        IncludeExclude<String> ie = new IncludeExclude();
        ie.include("foo");
        ie.include("bar");
        MatcherAssert.assertThat("IncludeExclude.size", ie.size(), Matchers.is(2));
        Assertions.assertEquals(false, ie.test(""));
        Assertions.assertEquals(true, ie.test("foo"));
        Assertions.assertEquals(true, ie.test("bar"));
        Assertions.assertEquals(false, ie.test("foobar"));
    }

    @Test
    public void testExcludeOnly() {
        IncludeExclude<String> ie = new IncludeExclude();
        ie.exclude("foo");
        ie.exclude("bar");
        Assertions.assertEquals(2, ie.size());
        Assertions.assertEquals(false, ie.test("foo"));
        Assertions.assertEquals(false, ie.test("bar"));
        Assertions.assertEquals(true, ie.test(""));
        Assertions.assertEquals(true, ie.test("foobar"));
        Assertions.assertEquals(true, ie.test("wibble"));
    }

    @Test
    public void testIncludeExclude() {
        IncludeExclude<String> ie = new IncludeExclude();
        ie.include("foo");
        ie.include("bar");
        ie.exclude("bar");
        ie.exclude("xxx");
        Assertions.assertEquals(4, ie.size());
        Assertions.assertEquals(true, ie.test("foo"));
        Assertions.assertEquals(false, ie.test("bar"));
        Assertions.assertEquals(false, ie.test(""));
        Assertions.assertEquals(false, ie.test("foobar"));
        Assertions.assertEquals(false, ie.test("xxx"));
    }

    @Test
    public void testEmptyRegex() {
        IncludeExclude<String> ie = new IncludeExclude(RegexSet.class);
        Assertions.assertEquals(0, ie.size());
        Assertions.assertEquals(true, ie.test("foo"));
    }

    @Test
    public void testIncludeRegex() {
        IncludeExclude<String> ie = new IncludeExclude(RegexSet.class);
        ie.include("f..");
        ie.include("b((ar)|(oo))");
        Assertions.assertEquals(2, ie.size());
        Assertions.assertEquals(false, ie.test(""));
        Assertions.assertEquals(true, ie.test("foo"));
        Assertions.assertEquals(true, ie.test("far"));
        Assertions.assertEquals(true, ie.test("bar"));
        Assertions.assertEquals(true, ie.test("boo"));
        Assertions.assertEquals(false, ie.test("foobar"));
        Assertions.assertEquals(false, ie.test("xxx"));
    }

    @Test
    public void testExcludeRegex() {
        IncludeExclude<String> ie = new IncludeExclude(RegexSet.class);
        ie.exclude("f..");
        ie.exclude("b((ar)|(oo))");
        Assertions.assertEquals(2, ie.size());
        Assertions.assertEquals(false, ie.test("foo"));
        Assertions.assertEquals(false, ie.test("far"));
        Assertions.assertEquals(false, ie.test("bar"));
        Assertions.assertEquals(false, ie.test("boo"));
        Assertions.assertEquals(true, ie.test(""));
        Assertions.assertEquals(true, ie.test("foobar"));
        Assertions.assertEquals(true, ie.test("xxx"));
    }

    @Test
    public void testIncludeExcludeRegex() {
        IncludeExclude<String> ie = new IncludeExclude(RegexSet.class);
        ie.include(".*[aeiou].*");
        ie.include("[AEIOU].*");
        ie.exclude("f..");
        ie.exclude("b((ar)|(oo))");
        Assertions.assertEquals(4, ie.size());
        Assertions.assertEquals(false, ie.test("foo"));
        Assertions.assertEquals(false, ie.test("far"));
        Assertions.assertEquals(false, ie.test("bar"));
        Assertions.assertEquals(false, ie.test("boo"));
        Assertions.assertEquals(false, ie.test(""));
        Assertions.assertEquals(false, ie.test("xxx"));
        Assertions.assertEquals(true, ie.test("foobar"));
        Assertions.assertEquals(true, ie.test("Ant"));
    }
}

