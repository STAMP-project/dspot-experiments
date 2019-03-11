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
package ch.qos.logback.core.boolex;


import ch.qos.logback.core.Context;
import junit.framework.TestCase;


public class MatcherTest extends TestCase {
    Context context;

    Matcher matcher;

    public void testFullRegion() throws Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        TestCase.assertTrue(matcher.matches("test"));
        TestCase.assertTrue(matcher.matches("xxxxtest"));
        TestCase.assertTrue(matcher.matches("testxxxx"));
        TestCase.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    public void testPartRegion() throws Exception {
        matcher.setRegex("test");
        matcher.start();
        TestCase.assertTrue(matcher.matches("test"));
        TestCase.assertTrue(matcher.matches("xxxxtest"));
        TestCase.assertTrue(matcher.matches("testxxxx"));
        TestCase.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    public void testCaseInsensitive() throws Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        TestCase.assertTrue(matcher.matches("TEST"));
        TestCase.assertTrue(matcher.matches("tEst"));
        TestCase.assertTrue(matcher.matches("tESt"));
        TestCase.assertTrue(matcher.matches("TesT"));
    }

    public void testCaseSensitive() throws Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        TestCase.assertFalse(matcher.matches("TEST"));
        TestCase.assertFalse(matcher.matches("tEst"));
        TestCase.assertFalse(matcher.matches("tESt"));
        TestCase.assertFalse(matcher.matches("TesT"));
    }
}

