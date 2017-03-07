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


public class AmplMatcherTest extends junit.framework.TestCase {
    ch.qos.logback.core.Context context;

    ch.qos.logback.core.boolex.Matcher matcher;

    public void setUp() throws java.lang.Exception {
        context = new ch.qos.logback.core.ContextBase();
        matcher = new ch.qos.logback.core.boolex.Matcher();
        matcher.setContext(context);
        matcher.setName("testMatcher");
        super.setUp();
    }

    public void tearDown() throws java.lang.Exception {
        matcher = null;
        super.tearDown();
    }

    public void testFullRegion() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    public void testPartRegion() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    public void testCaseInsensitive() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    public void testCaseSensitive() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf25() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_18 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_18);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_17 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_17).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_17).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_17).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_17).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_17.setCanonEq(vc_18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_17).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_17).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_17).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_17).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_17).getRegex());
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf47() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_34 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_34).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_34).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_34).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_34).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_34.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_34).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_34).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_34).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_34).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_34).isCanonEq());
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf16_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(false);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "tESt";
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_9 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_9.matches(String_vc_0);
            // MethodAssertGenerator build local variable
            Object o_16_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseInsensitive_cf16 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf49() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_36 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_36).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_36).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_36).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_36).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_36.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_36).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_36).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_36).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_36).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_36).isCaseSensitive());
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf9_cf227_failAssert43() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(false);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5).isStarted());
            // AssertGenerator replace invocation
            boolean o_testCaseInsensitive_cf9__12 = // StatementAdderMethod cloned existing statement
vc_5.isStarted();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testCaseInsensitive_cf9__12);
            // StatementAdderOnAssert create null value
            java.lang.String vc_121 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_120 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_120.matches(vc_121);
            // MethodAssertGenerator build local variable
            Object o_38_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseInsensitive_cf9_cf227 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf33_cf982() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_24 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_23 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_23.setName(vc_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // StatementAdderOnAssert create random local variable
        boolean vc_439 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_439);
        // StatementAdderMethod cloned existing statement
        vc_23.setUnicodeCase(vc_439);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf29_cf840() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_21 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_21);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_20 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_20.setCaseSensitive(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_385 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_385).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_385).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_385).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_385).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_385).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_385).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_385).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_385).isCaseSensitive());
        // AssertGenerator replace invocation
        java.lang.String o_testCaseInsensitive_cf29_cf840__52 = // StatementAdderMethod cloned existing statement
vc_385.getRegex();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testCaseInsensitive_cf29_cf840__52);
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf34_cf1074() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_3 = "tEst";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3, "tEst");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3, "tEst");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_23 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_23.setName(String_vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_23).getName(), "tEst");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_23).getName(), "tEst");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_478 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_478).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_478).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_478).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_478).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getName());
        // StatementAdderMethod cloned existing statement
        vc_478.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_478).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_478).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_478).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_478).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_478).getName());
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf29_cf853_cf6364() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_21 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_21);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_20 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_20.setCaseSensitive(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_20.setCaseSensitive(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_20.setUnicodeCase(vc_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf28_cf807_cf4632() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_2 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(boolean_vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(boolean_vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(boolean_vc_2);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_20 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_20.setCaseSensitive(boolean_vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_20.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_20).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_20).getStatusManager());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_1976 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_1976).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_1976).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_1976).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_1976).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_1976).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_1976).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_1976).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_1976).getRegex());
        // AssertGenerator replace invocation
        java.lang.String o_testCaseInsensitive_cf28_cf807_cf4632__104 = // StatementAdderMethod cloned existing statement
vc_1976.getRegex();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testCaseInsensitive_cf28_cf807_cf4632__104);
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf45_cf1567_cf8774() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("TEST"));
        junit.framework.Assert.assertTrue(matcher.matches("tEst"));
        junit.framework.Assert.assertTrue(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_32 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_32);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_32);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_32);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_31 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_31.setUnicodeCase(vc_32);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // StatementAdderOnAssert create random local variable
        boolean vc_687 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_687);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_687);
        // StatementAdderMethod cloned existing statement
        vc_31.setCaseSensitive(vc_687);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_31).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_31).isCaseSensitive());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_3744 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_3744).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_3744).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_3744).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_3744).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_3744).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_3744).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_3744).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_3744).isCaseSensitive());
        // AssertGenerator replace invocation
        boolean o_testCaseInsensitive_cf45_cf1567_cf8774__108 = // StatementAdderMethod cloned existing statement
vc_3744.isUnicodeCase();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testCaseInsensitive_cf45_cf1567_cf8774__108);
        junit.framework.Assert.assertTrue(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseInsensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseInsensitive_cf34_add994_cf10732_failAssert56() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(false);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_3 = "tEst";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_3, "tEst");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_3, "tEst");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_23 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_23.setName(String_vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_23).getName(), "tEst");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
            // StatementAdderMethod cloned existing statement
            vc_23.setName(String_vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_23).getName(), "tEst");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_23).getName(), "tEst");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_23).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_23).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_23).isUnicodeCase());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_4599 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            vc_23.matches(vc_4599);
            // MethodAssertGenerator build local variable
            Object o_107_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseInsensitive_cf34_add994_cf10732 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12012_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(true);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create null value
            java.lang.String vc_5116 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5115 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_5115.matches(vc_5116);
            // MethodAssertGenerator build local variable
            Object o_16_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseSensitive_cf12012 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12022() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_5124 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_5124);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5123 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_5123.setCanonEq(vc_5124);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isStarted());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12044() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5140 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5140).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5140).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5140).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5140).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_5140.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5140).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5140).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5140).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5140).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5140).isUnicodeCase());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12046() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5142 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5142).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5142).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5142).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5142).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_5142.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5142).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5142).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5142).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5142).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5142).getStatusManager());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12018() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5121 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5121).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5121).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5121).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5121).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5121).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5121).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5121).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5121).isUnicodeCase());
        // AssertGenerator replace invocation
        java.lang.String o_testCaseSensitive_cf12018__12 = // StatementAdderMethod cloned existing statement
vc_5121.getRegex();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testCaseSensitive_cf12018__12);
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12041() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_833 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(boolean_vc_833);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5137 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_5137.setUnicodeCase(boolean_vc_833);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getRegex());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12032_cf13149() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_5131 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5129 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_5129.setName(vc_5131);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_911 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(boolean_vc_911);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5618 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5618).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5618).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5618).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5618).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_5618.setUnicodeCase(boolean_vc_911);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5618).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5618).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5618).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5618).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5618).getStatusManager());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12022_cf12686() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_5124 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_5124);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_5124);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5123 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_5123.setCanonEq(vc_5124);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5123).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5123).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5123).isStarted());
        // StatementAdderOnAssert create null value
        java.lang.String vc_5426 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5426);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5425 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5425).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5425).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5425).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5425).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_5425.setName(vc_5426);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5425).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5425).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5425).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5425).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5425).isCaseSensitive());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12042_cf13597() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        boolean vc_5138 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_5138);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_5138);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5137 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_5137.setUnicodeCase(vc_5138);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5137).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5137).getRegex());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5806 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5806).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5806).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5806).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5806).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_5806.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5806).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5806).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5806).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5806).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5806).isUnicodeCase());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12025_cf12743_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(true);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create literal from method
            boolean boolean_vc_830 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(boolean_vc_830);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5126 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isUnicodeCase());
            // StatementAdderMethod cloned existing statement
            vc_5126.setCaseSensitive(boolean_vc_830);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isUnicodeCase());
            // StatementAdderOnAssert create null value
            java.lang.String vc_5449 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_5126.matches(vc_5449);
            // MethodAssertGenerator build local variable
            Object o_54_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseSensitive_cf12025_cf12743 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12032_cf13084_cf17517() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_5131 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5129 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_5129.setName(vc_5131);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5590 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5590).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5590).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5590).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5590).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5590).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5590).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5590).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5590).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5590).isCaseSensitive());
        // AssertGenerator replace invocation
        boolean o_testCaseSensitive_cf12032_cf13084__52 = // StatementAdderMethod cloned existing statement
vc_5590.isCaseSensitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testCaseSensitive_cf12032_cf13084__52);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_7436 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_7436).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_7436).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_7436).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_7436).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_7436.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_7436).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_7436).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_7436).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_7436).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_7436).getRegex());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12037_cf13330_cf18340() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_832 = "TEST";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_832, "TEST");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_832, "TEST");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_832, "TEST");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5133 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_5133.setRegex(String_vc_832);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex(), "TEST");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex(), "TEST");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex(), "TEST");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_5690 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5690, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5690, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5688 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_5688.setRegex(vc_5690);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5688).getRegex(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5688).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5688).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5688).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5688).getRegex(), "");
        // StatementAdderMethod cloned existing statement
        vc_5133.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5133).getRegex(), "TEST");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5133).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5133).isUnicodeCase());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12032_cf13087_cf13884() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_5131 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5131, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5129 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_5129.setName(vc_5131);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5592 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5592).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5592).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isUnicodeCase());
        // AssertGenerator replace invocation
        boolean o_testCaseSensitive_cf12032_cf13087__52 = // StatementAdderMethod cloned existing statement
vc_5592.isStarted();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testCaseSensitive_cf12032_cf13087__52);
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_961 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(boolean_vc_961);
        // StatementAdderMethod cloned existing statement
        vc_5592.setCanonEq(boolean_vc_961);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5592).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5592).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5592).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5592).isUnicodeCase());
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12025_cf12771_cf13982_failAssert39() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(true);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create literal from method
            boolean boolean_vc_830 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(boolean_vc_830);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(boolean_vc_830);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5126 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isUnicodeCase());
            // StatementAdderMethod cloned existing statement
            vc_5126.setCaseSensitive(boolean_vc_830);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5126).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5126).isUnicodeCase());
            // StatementAdderOnAssert create literal from method
            boolean boolean_vc_884 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(boolean_vc_884);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5459 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5459).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5459).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5459).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5459).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_5459.setCaseSensitive(boolean_vc_884);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5459).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5459).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5459).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5459).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5459).getStatusManager());
            // StatementAdderOnAssert create null value
            java.lang.String vc_5981 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.boolex.Matcher vc_5979 = (ch.qos.logback.core.boolex.Matcher)null;
            // StatementAdderMethod cloned existing statement
            vc_5979.setName(vc_5981);
            // MethodAssertGenerator build local variable
            Object o_130_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseSensitive_cf12025_cf12771_cf13982 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12031_cf13026_cf18029_failAssert61() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.setCaseSensitive(true);
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_4_0 = matcher.matches("TEST");
            // MethodAssertGenerator build local variable
            Object o_6_0 = matcher.matches("tEst");
            // MethodAssertGenerator build local variable
            Object o_8_0 = matcher.matches("tESt");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_831 = "TesT";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_831, "TesT");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_831, "TesT");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5129 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
            // StatementAdderMethod cloned existing statement
            vc_5129.setName(String_vc_831);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "TesT");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName(), "TesT");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_5565 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5565).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5565).getContext());
            // AssertGenerator replace invocation
            java.lang.String o_testCaseSensitive_cf12031_cf13026__52 = // StatementAdderMethod cloned existing statement
vc_5565.getRegex();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testCaseSensitive_cf12031_cf13026__52);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7633 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_7631 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_7631.matches(vc_7633);
            // MethodAssertGenerator build local variable
            Object o_112_0 = matcher.matches("TesT");
            org.junit.Assert.fail("testCaseSensitive_cf12031_cf13026_cf18029 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void testCaseSensitive_cf12030_cf12917_cf14463() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();
        junit.framework.Assert.assertFalse(matcher.matches("TEST"));
        junit.framework.Assert.assertFalse(matcher.matches("tEst"));
        junit.framework.Assert.assertFalse(matcher.matches("tESt"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_5130 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5130);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5130);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5130);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_5129 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_5129.setName(vc_5130);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_5129).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_5129).isCanonEq());
        // AssertGenerator replace invocation
        boolean o_testCaseSensitive_cf12030_cf12917__50 = // StatementAdderMethod cloned existing statement
vc_5129.isStarted();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testCaseSensitive_cf12030_cf12917__50);
        // AssertGenerator replace invocation
        boolean o_testCaseSensitive_cf12030_cf12917_cf14463__88 = // StatementAdderMethod cloned existing statement
vc_5129.isCanonEq();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testCaseSensitive_cf12030_cf12917_cf14463__88);
        junit.framework.Assert.assertFalse(matcher.matches("TesT"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21797_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex(".*test.*");
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_3_0 = matcher.matches("test");
            // MethodAssertGenerator build local variable
            Object o_5_0 = matcher.matches("xxxxtest");
            // MethodAssertGenerator build local variable
            Object o_7_0 = matcher.matches("testxxxx");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1488 = "xxxxtestxxxx";
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_9185 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_9185.matches(String_vc_1488);
            // MethodAssertGenerator build local variable
            Object o_15_0 = matcher.matches("xxxxtestxxxx");
            org.junit.Assert.fail("testFullRegion_cf21797 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21824() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9212 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9212).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9212).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9212).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9212).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getName());
        // StatementAdderMethod cloned existing statement
        vc_9212.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9212).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9212).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9212).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9212).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9212).getName());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21822() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9210 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9210).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9210).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9210).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9210).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_9210.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9210).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9210).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9210).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9210).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9210).isStarted());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21810() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_9200 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9200);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9199 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_9199.setName(vc_9200);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCaseSensitive());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21804() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        boolean vc_9194 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_9194);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9193 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_9193.setCanonEq(vc_9194);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21816_cf22729() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_9204 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9204);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9204);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9203 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_9203.setRegex(vc_9204);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // StatementAdderOnAssert create random local variable
        boolean vc_9652 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_9652);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9651 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9651).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9651).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9651).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9651).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_9651.setUnicodeCase(vc_9652);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9651).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9651).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9651).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9651).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9651).getRegex());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21818_cf22881() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_9205 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_9205, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_9205, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9203 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_9203.setRegex(vc_9205);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9203).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9203).getRegex(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9203).isStarted());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9728 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9728).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9728).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9728).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9728).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_9728.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9728).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9728).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9728).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9728).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9728).getStatusManager());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21810_cf22449_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex(".*test.*");
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_3_0 = matcher.matches("test");
            // MethodAssertGenerator build local variable
            Object o_5_0 = matcher.matches("xxxxtest");
            // MethodAssertGenerator build local variable
            Object o_7_0 = matcher.matches("testxxxx");
            // StatementAdderOnAssert create null value
            java.lang.String vc_9200 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9200);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_9199 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCaseSensitive());
            // StatementAdderMethod cloned existing statement
            vc_9199.setName(vc_9200);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9199).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9199).isCaseSensitive());
            // StatementAdderMethod cloned existing statement
            vc_9199.matches(vc_9200);
            // MethodAssertGenerator build local variable
            Object o_51_0 = matcher.matches("xxxxtestxxxx");
            org.junit.Assert.fail("testFullRegion_cf21810_cf22449 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21806_cf22414() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        boolean vc_9197 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_9197);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_9197);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9196 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_9196.setCaseSensitive(vc_9197);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9508 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9508).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9508).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9508).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9508).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_9508.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9508).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9508).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9508).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9508).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9508).isCaseSensitive());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21806_cf22407() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        boolean vc_9197 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_9197);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_9197);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9196 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_9196.setCaseSensitive(vc_9197);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_1520 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(boolean_vc_1520);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9503 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9503).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9503).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9503).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9503).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_9503.setUnicodeCase(boolean_vc_1520);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9503).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9503).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9503).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9503).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9503).isCanonEq());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21800_cf22167_cf28911() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9189 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9189).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9189).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9189).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9189).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9189).isCanonEq());
        // AssertGenerator replace invocation
        java.lang.String o_testFullRegion_cf21800__11 = // StatementAdderMethod cloned existing statement
vc_9189.getName();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testFullRegion_cf21800__11);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1505 = ".*test.*";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1505, ".*test.*");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1505, ".*test.*");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9388 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_9388.setRegex(String_vc_1505);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9388).getRegex(), ".*test.*");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9388).getRegex(), ".*test.*");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9388).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9388).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9388).isStarted());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_12544 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_12544).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_12544).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_12544).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_12544).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_12544).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_12544).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_12544).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_12544).getContext());
        // AssertGenerator replace invocation
        boolean o_testFullRegion_cf21800_cf22167_cf28911__89 = // StatementAdderMethod cloned existing statement
vc_12544.isCanonEq();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFullRegion_cf21800_cf22167_cf28911__89);
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21804_cf22313_cf26551() throws java.lang.Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        boolean vc_9194 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_9194);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_9194);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_9194);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9193 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // StatementAdderMethod cloned existing statement
        vc_9193.setCanonEq(vc_9194);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9193).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9193).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9193).isCaseSensitive());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_9464 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_9464, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_9464, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_9462 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_9462.setRegex(vc_9464);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9462).getRegex(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9462).getRegex(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isUnicodeCase());
        // StatementAdderMethod cloned existing statement
        vc_9462.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_9462).getRegex(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9462).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9462).isUnicodeCase());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testFullRegion */
    @org.junit.Test(timeout = 10000)
    public void testFullRegion_cf21806_cf22339_cf27334_failAssert77() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex(".*test.*");
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_3_0 = matcher.matches("test");
            // MethodAssertGenerator build local variable
            Object o_5_0 = matcher.matches("xxxxtest");
            // MethodAssertGenerator build local variable
            Object o_7_0 = matcher.matches("testxxxx");
            // StatementAdderOnAssert create random local variable
            boolean vc_9197 = false;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(vc_9197);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(vc_9197);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_9196 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
            // StatementAdderMethod cloned existing statement
            vc_9196.setCaseSensitive(vc_9197);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9196).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9196).isCanonEq());
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_9473 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_9473).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9473).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9473).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9473).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9473).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9473).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_9473).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_9473).getContext());
            // AssertGenerator replace invocation
            boolean o_testFullRegion_cf21806_cf22339__51 = // StatementAdderMethod cloned existing statement
vc_9473.isCanonEq();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testFullRegion_cf21806_cf22339__51);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1761 = "test";
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_11812 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_11812.matches(String_vc_1761);
            // MethodAssertGenerator build local variable
            Object o_111_0 = matcher.matches("xxxxtestxxxx");
            org.junit.Assert.fail("testFullRegion_cf21806_cf22339_cf27334 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31152() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13578 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13578).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13578).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13578).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13578).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_13578.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13578).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13578).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13578).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13578).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13578).isStarted());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31125_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_3_0 = matcher.matches("test");
            // MethodAssertGenerator build local variable
            Object o_5_0 = matcher.matches("xxxxtest");
            // MethodAssertGenerator build local variable
            Object o_7_0 = matcher.matches("testxxxx");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1956 = "test";
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_13551 = new ch.qos.logback.core.boolex.Matcher();
            // StatementAdderMethod cloned existing statement
            vc_13551.matches(String_vc_1956);
            // MethodAssertGenerator build local variable
            Object o_15_0 = matcher.matches("xxxxtestxxxx");
            org.junit.Assert.fail("testPartRegion_cf31125 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31150() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13576 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13576).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13576).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13576).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13576).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getContext());
        // StatementAdderMethod cloned existing statement
        vc_13576.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13576).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13576).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13576).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13576).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13576).getContext());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31128() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13555 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13555).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13555).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13555).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13555).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13555).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13555).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13555).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13555).isCaseSensitive());
        // AssertGenerator replace invocation
        java.lang.String o_testPartRegion_cf31128__11 = // StatementAdderMethod cloned existing statement
vc_13555.getName();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testPartRegion_cf31128__11);
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31130_cf31539() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13557 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13557).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13557).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getName());
        // AssertGenerator replace invocation
        java.lang.String o_testPartRegion_cf31130__11 = // StatementAdderMethod cloned existing statement
vc_13557.getRegex();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testPartRegion_cf31130__11);
        // StatementAdderOnAssert create random local variable
        boolean vc_13782 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_13782);
        // StatementAdderMethod cloned existing statement
        vc_13557.setCanonEq(vc_13782);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13557).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13557).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13557).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13557).getName());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31138_cf31777_failAssert33() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_3_0 = matcher.matches("test");
            // MethodAssertGenerator build local variable
            Object o_5_0 = matcher.matches("xxxxtest");
            // MethodAssertGenerator build local variable
            Object o_7_0 = matcher.matches("testxxxx");
            // StatementAdderOnAssert create null value
            java.lang.String vc_13566 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13566);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_13565 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
            // StatementAdderMethod cloned existing statement
            vc_13565.setName(vc_13566);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
            // StatementAdderMethod cloned existing statement
            vc_13565.matches(vc_13566);
            // MethodAssertGenerator build local variable
            Object o_51_0 = matcher.matches("xxxxtestxxxx");
            org.junit.Assert.fail("testPartRegion_cf31138_cf31777 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31132_cf31647() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        boolean vc_13560 = false;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_13560);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(vc_13560);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13559 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_13559.setCanonEq(vc_13560);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getStatusManager());
        // StatementAdderOnAssert create random local variable
        boolean vc_13833 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_13833);
        // StatementAdderMethod cloned existing statement
        vc_13559.setUnicodeCase(vc_13833);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13559).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13559).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13559).getStatusManager());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31144_cf32039_cf39924() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_13570 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13570);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13570);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13570);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13569 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_13569.setRegex(vc_13570);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13569).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13569).isCanonEq());
        // StatementAdderOnAssert create null value
        java.lang.String vc_14010 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14010);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14010);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_14009 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_14009.setName(vc_14010);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isStarted());
        // StatementAdderMethod cloned existing statement
        vc_14009.start();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_14009).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_14009).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_14009).isStarted());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31138_cf31818_cf37538() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_13566 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13566);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13566);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13566);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13565 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // StatementAdderMethod cloned existing statement
        vc_13565.setName(vc_13566);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1991 = "testxxxx";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1991, "testxxxx");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1991, "testxxxx");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13902 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getRegex());
        // StatementAdderMethod cloned existing statement
        vc_13902.setRegex(String_vc_1991);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13902).getRegex(), "testxxxx");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13902).getRegex(), "testxxxx");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_2264 = "test";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_2264, "test");
        // StatementAdderMethod cloned existing statement
        vc_13902.setRegex(String_vc_2264);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13902).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13902).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13902).getRegex(), "test");
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31140_cf31916_cf38852_failAssert69() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            matcher.setRegex("test");
            matcher.start();
            // MethodAssertGenerator build local variable
            Object o_3_0 = matcher.matches("test");
            // MethodAssertGenerator build local variable
            Object o_5_0 = matcher.matches("xxxxtest");
            // MethodAssertGenerator build local variable
            Object o_7_0 = matcher.matches("testxxxx");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13567 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_13567, "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_13567, "");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.boolex.Matcher vc_13565 = new ch.qos.logback.core.boolex.Matcher();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
            // StatementAdderMethod cloned existing statement
            vc_13565.setName(vc_13567);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
            // AssertGenerator replace invocation
            boolean o_testPartRegion_cf31140_cf31916__49 = // StatementAdderMethod cloned existing statement
vc_13565.isStarted();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPartRegion_cf31140_cf31916__49);
            // StatementAdderOnAssert create null value
            java.lang.String vc_17141 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_13565.matches(vc_17141);
            // MethodAssertGenerator build local variable
            Object o_91_0 = matcher.matches("xxxxtestxxxx");
            org.junit.Assert.fail("testPartRegion_cf31140_cf31916_cf38852 should have thrown EvaluationException");
        } catch (ch.qos.logback.core.boolex.EvaluationException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31120_cf31392_cf33718() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13549 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator replace invocation
        boolean o_testPartRegion_cf31120__11 = // StatementAdderMethod cloned existing statement
vc_13549.isUnicodeCase();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testPartRegion_cf31120__11);
        // StatementAdderOnAssert create null value
        java.lang.String vc_13714 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13714);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13714);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13713 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getContext());
        // StatementAdderMethod cloned existing statement
        vc_13713.setName(vc_13714);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getContext());
        // StatementAdderOnAssert create random local variable
        boolean vc_14795 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_14795);
        // StatementAdderMethod cloned existing statement
        vc_13713.setUnicodeCase(vc_14795);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13713).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13713).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13713).getContext());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31140_cf31911_cf36676() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_13567 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13567, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13567, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13567, "");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13565 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // StatementAdderMethod cloned existing statement
        vc_13565.setName(vc_13567);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13565).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13565).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.boolex.Matcher)vc_13565).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13565).getContext());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13950 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13950).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13950).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13950).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13950).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13950).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13950).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13950).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13950).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13950).isUnicodeCase());
        // AssertGenerator replace invocation
        boolean o_testPartRegion_cf31140_cf31911__51 = // StatementAdderMethod cloned existing statement
vc_13950.isCanonEq();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testPartRegion_cf31140_cf31911__51);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_16094 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_16094).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_16094).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_16094).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_16094).isCanonEq());
        // StatementAdderMethod cloned existing statement
        vc_16094.stop();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_16094).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_16094).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_16094).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_16094).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_16094).isCanonEq());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    /* amplification of ch.qos.logback.core.boolex.MatcherTest#testPartRegion */
    @org.junit.Test(timeout = 10000)
    public void testPartRegion_cf31120_cf31398_cf32671() throws java.lang.Exception {
        matcher.setRegex("test");
        matcher.start();
        junit.framework.Assert.assertTrue(matcher.matches("test"));
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtest"));
        junit.framework.Assert.assertTrue(matcher.matches("testxxxx"));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.boolex.Matcher vc_13549 = new ch.qos.logback.core.boolex.Matcher();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator replace invocation
        boolean o_testPartRegion_cf31120__11 = // StatementAdderMethod cloned existing statement
vc_13549.isUnicodeCase();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testPartRegion_cf31120__11);
        // StatementAdderOnAssert create null value
        java.lang.String vc_13718 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13718);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13718);
        // StatementAdderMethod cloned existing statement
        vc_13549.setRegex(vc_13718);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        // StatementAdderOnAssert create random local variable
        boolean vc_14300 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_14300);
        // StatementAdderMethod cloned existing statement
        vc_13549.setCanonEq(vc_14300);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCanonEq());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isUnicodeCase());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.boolex.Matcher)vc_13549).isCaseSensitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.boolex.Matcher)vc_13549).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.boolex.Matcher)vc_13549).getRegex());
        junit.framework.Assert.assertTrue(matcher.matches("xxxxtestxxxx"));
    }
}

