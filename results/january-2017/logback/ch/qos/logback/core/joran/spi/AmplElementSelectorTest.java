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


package ch.qos.logback.core.joran.spi;


/**
 * Test pattern manipulation code.
 *
 * @author Ceki Gulcu
 */
public class AmplElementSelectorTest {
    @org.junit.Test
    public void test1() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    @org.junit.Test
    public void testSuffix() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    @org.junit.Test
    public void test2() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    @org.junit.Test
    public void test3() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    @org.junit.Test
    public void test4() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    @org.junit.Test
    public void test5() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    @org.junit.Test
    public void test6() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    // test tail matching
    @org.junit.Test
    public void testTailMatch() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test prefix matching
    @org.junit.Test
    public void testPrefixMatch() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf36() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_18 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18);
        // AssertGenerator replace invocation
        int o_test1_cf36__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf36__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf25() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_11 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate().equals(vc_11));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_11).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1249006609 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1249006609, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).duplicate()).duplicate().equals(vc_11));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_11).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).duplicate().equals(vc_11));
        // AssertGenerator add assertion
        java.util.ArrayList collection_72561234 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_72561234, ((ch.qos.logback.core.joran.spi.ElementPath)vc_11).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_535870348 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_535870348, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11).duplicate()).duplicate()).peekLast());
        // AssertGenerator replace invocation
        boolean o_test1_cf25__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf25__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf13() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test1_cf13__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf13__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf24() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_10 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // AssertGenerator replace invocation
        boolean o_test1_cf24__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf24__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf31() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_15 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_709189159 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_709189159, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_524730536 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_524730536, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).duplicate().equals(vc_15));
        // AssertGenerator add assertion
        java.util.ArrayList collection_643294074 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_643294074, ((ch.qos.logback.core.joran.spi.ElementPath)vc_15).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_15).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).duplicate()).duplicate().equals(vc_15));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate().equals(vc_15));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_15).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_15).duplicate()).duplicate()).peekLast());
        // AssertGenerator replace invocation
        int o_test1_cf31__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf31__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf30() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_14 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator replace invocation
        int o_test1_cf30__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf30__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf41() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // AssertGenerator replace invocation
        int o_test1_cf41__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf41__7, 97);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf37() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_19 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).duplicate()).duplicate().equals(vc_19));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_19).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_19).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).duplicate().equals(vc_19));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate().equals(vc_19));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1054486546 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1054486546, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1760381217 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1760381217, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_829010075 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_829010075, ((ch.qos.logback.core.joran.spi.ElementPath)vc_19).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_19).duplicate()).duplicate()).peekLast());
        // AssertGenerator replace invocation
        int o_test1_cf37__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf37__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf12_cf89_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2);
            // AssertGenerator replace invocation
            boolean o_test1_cf12__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_2);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test1_cf12__9);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_33 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_30 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_30.isContainedIn(vc_33);
            // MethodAssertGenerator build local variable
            Object o_21_0 = p.get(0);
            org.junit.Assert.fail("test1_cf12_cf89 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf26_cf612() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_10 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_9 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1691965529 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1691965529, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_394412756 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_394412756, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2132696421 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2132696421, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_29175323 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_29175323, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1154731922 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1154731922, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2101000825 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2101000825, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9).peekLast());
        // AssertGenerator replace invocation
        boolean o_test1_cf26__11 = // StatementAdderMethod cloned existing statement
vc_9.isContainedIn(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf26__11);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_186 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_186);
        // AssertGenerator replace invocation
        boolean o_test1_cf26_cf612__37 = // StatementAdderMethod cloned existing statement
vc_9.isContainedIn(vc_186);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf26_cf612__37);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf19_cf423_failAssert38() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_7 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_224292301 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_224292301, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_891291837 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_891291837, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).duplicate().equals(vc_7));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate().equals(vc_7));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate().equals(vc_7));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1380034668 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1380034668, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).size(), 0);
            // AssertGenerator replace invocation
            boolean o_test1_cf19__9 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_7);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test1_cf19__9);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_112 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_110 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_110.equals(vc_112);
            // MethodAssertGenerator build local variable
            Object o_43_0 = p.get(0);
            org.junit.Assert.fail("test1_cf19_cf423 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf19_cf450() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1089874499 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1089874499, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_133123235 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_133123235, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        java.util.ArrayList collection_482623244 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_482623244, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_224292301 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_224292301, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_891291837 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_891291837, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1380034668 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1380034668, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).size(), 0);
        // AssertGenerator replace invocation
        boolean o_test1_cf19__9 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf19__9);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_124 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_124);
        // AssertGenerator replace invocation
        int o_test1_cf19_cf450__39 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_124);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf19_cf450__39, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf24_cf522() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_10 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // AssertGenerator replace invocation
        boolean o_test1_cf24__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf24__9);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_150 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_150);
        // AssertGenerator replace invocation
        int o_test1_cf24_cf522__17 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_150);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf24_cf522__17, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf41_cf1001_cf2684() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // AssertGenerator replace invocation
        int o_test1_cf41__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf41__7, 97);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_340 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_340);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_340);
        // AssertGenerator replace invocation
        boolean o_test1_cf41_cf1001__13 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_340);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf41_cf1001__13);
        // AssertGenerator replace invocation
        int o_test1_cf41_cf1001_cf2684__19 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf41_cf1001_cf2684__19, 97);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf13_cf210_failAssert53_add2131() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 1);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "a");
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_3 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_test1_cf13__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test1_cf13__9);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_63 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_63).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1746705630 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1746705630, ((ch.qos.logback.core.joran.spi.ElementPath)vc_63).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_888003080 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_888003080, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_63).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).duplicate().equals(vc_63));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).duplicate()).duplicate().equals(vc_63));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate().equals(vc_63));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1537759807 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1537759807, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_63).duplicate()).duplicate()).getCopyOfPartList());;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_60 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_60);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_60.getTailMatchLength(vc_63);
            // StatementAdderMethod cloned existing statement
            vc_60.getTailMatchLength(vc_63);
            // MethodAssertGenerator build local variable
            Object o_19_0 = p.get(0);
            org.junit.Assert.fail("test1_cf13_cf210 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf38_cf948_cf2851_failAssert62() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_18 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_18);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_18);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_17 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1547278377 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1547278377, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1076073966 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1076073966, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1828808659 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1828808659, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_400788519 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_400788519, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1457064144 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1457064144, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1773893812 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1773893812, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_17).duplicate()).peekLast());
            // AssertGenerator replace invocation
            int o_test1_cf38__11 = // StatementAdderMethod cloned existing statement
vc_17.getTailMatchLength(vc_18);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test1_cf38__11, 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_318 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_318);
            // AssertGenerator replace invocation
            boolean o_test1_cf38_cf948__37 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_318);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test1_cf38_cf948__37);
            // StatementAdderMethod cloned existing statement
            vc_17.fullPathMatch(vc_18);
            // MethodAssertGenerator build local variable
            Object o_65_0 = p.get(0);
            org.junit.Assert.fail("test1_cf38_cf948_cf2851 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf41_cf1014_cf1895_failAssert49() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator replace invocation
            int o_test1_cf41__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test1_cf41__7, 97);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_349 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate().equals(vc_349));
            // AssertGenerator add assertion
            java.util.ArrayList collection_81915820 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_81915820, ((ch.qos.logback.core.joran.spi.ElementPath)vc_349).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).duplicate()).duplicate().equals(vc_349));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).duplicate().equals(vc_349));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_349).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1681441635 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1681441635, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1292034975 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1292034975, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_349).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_349).size(), 0);
            // AssertGenerator replace invocation
            int o_test1_cf41_cf1014__13 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_349);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test1_cf41_cf1014__13, 0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_601 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_598 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_598.fullPathMatch(vc_601);
            // MethodAssertGenerator build local variable
            Object o_47_0 = p.get(0);
            org.junit.Assert.fail("test1_cf41_cf1014_cf1895 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf30_cf659_cf1478_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_14 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_14);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_14);
            // AssertGenerator replace invocation
            int o_test1_cf30__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_14);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test1_cf30__9, 0);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_201 = new java.lang.Object();
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_199 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1585043158 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1585043158, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1760570681 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1760570681, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_218732810 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_218732810, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_199).getCopyOfPartList());;
            // AssertGenerator replace invocation
            boolean o_test1_cf30_cf659__19 = // StatementAdderMethod cloned existing statement
vc_199.equals(vc_201);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test1_cf30_cf659__19);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_487 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_484 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_484.equals(vc_487);
            // MethodAssertGenerator build local variable
            Object o_49_0 = p.get(0);
            org.junit.Assert.fail("test1_cf30_cf659_cf1478 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf41_cf1008_cf2622_failAssert72() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator replace invocation
            int o_test1_cf41__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test1_cf41__7, 97);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_345 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate().equals(vc_345));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1271577446 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1271577446, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).duplicate()).duplicate().equals(vc_345));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).duplicate().equals(vc_345));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1424920426 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1424920426, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_345).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_949520772 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_949520772, ((ch.qos.logback.core.joran.spi.ElementPath)vc_345).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_345).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_345).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_test1_cf41_cf1008__13 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_345);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test1_cf41_cf1008__13, 0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_811 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_808 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_808.getTailMatchLength(vc_811);
            // MethodAssertGenerator build local variable
            Object o_47_0 = p.get(0);
            org.junit.Assert.fail("test1_cf41_cf1008_cf2622 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf12_cf61_cf1848() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_test1_cf12__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf12__9);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_24 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // AssertGenerator replace invocation
        boolean o_test1_cf12_cf61__17 = // StatementAdderMethod cloned existing statement
p.equals(vc_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf12_cf61__17);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_586 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_586);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_585 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1861661987 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1861661987, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_298059444 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_298059444, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_399349430 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_399349430, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_585).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_test1_cf12_cf61_cf1848__29 = // StatementAdderMethod cloned existing statement
vc_585.getPrefixMatchLength(vc_586);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf12_cf61_cf1848__29, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf19_cf425_cf1325() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_605918780 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_605918780, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_213367357 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_213367357, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        java.util.ArrayList collection_721729830 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_721729830, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_577588717 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_577588717, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2015515001 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2015515001, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        java.util.ArrayList collection_285204708 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_285204708, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_224292301 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_224292301, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_891291837 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_891291837, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate().equals(vc_7));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1380034668 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1380034668, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7).size(), 0);
        // AssertGenerator replace invocation
        boolean o_test1_cf19__9 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf19__9);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_112 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_112);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_112);
        // AssertGenerator replace invocation
        boolean o_test1_cf19_cf425__39 = // StatementAdderMethod cloned existing statement
p.equals(vc_112);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf19_cf425__39);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_443 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test1_cf19_cf425_cf1325__71 = // StatementAdderMethod cloned existing statement
vc_7.equals(vc_443);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf19_cf425_cf1325__71);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test1 */
    @org.junit.Test(timeout = 10000)
    public void test1_cf12_cf135_cf1700() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_test1_cf12__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test1_cf12__9);
        // AssertGenerator replace invocation
        int o_test1_cf12_cf135__15 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf12_cf135__15, 97);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_546 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_546);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_545 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1335915896 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1335915896, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1392306316 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1392306316, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_292812216 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_292812216, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_545).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_test1_cf12_cf135_cf1700__25 = // StatementAdderMethod cloned existing statement
vc_545.getTailMatchLength(vc_546);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test1_cf12_cf135_cf1700__25, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2921() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_917 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_804461977 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_804461977, ((ch.qos.logback.core.joran.spi.ElementPath)vc_917).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1199947312 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1199947312, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_38200169 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_38200169, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).size(), 0);
        // AssertGenerator replace invocation
        int o_test2_cf2921__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_917);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2921__11, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2920() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_916 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_916);
        // AssertGenerator replace invocation
        int o_test2_cf2920__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_916);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2920__11, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2931() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // AssertGenerator replace invocation
        int o_test2_cf2931__9 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2931__9, 3);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2914() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_912 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_912);
        // AssertGenerator replace invocation
        boolean o_test2_cf2914__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_912);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2914__11);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2902() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_904 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_904);
        // AssertGenerator replace invocation
        boolean o_test2_cf2902__11 = // StatementAdderMethod cloned existing statement
p.equals(vc_904);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2902__11);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2927() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_921 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).duplicate().equals(vc_921));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate().equals(vc_921));
        // AssertGenerator add assertion
        java.util.ArrayList collection_424641312 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_424641312, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_78830032 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_78830032, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate().equals(vc_921));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1762863210 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1762863210, ((ch.qos.logback.core.joran.spi.ElementPath)vc_921).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test2_cf2927__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_921);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2927__11, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2926() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_920 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_920);
        // AssertGenerator replace invocation
        int o_test2_cf2926__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_920);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2926__11, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2915() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_913 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1030108535 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1030108535, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1547181240 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1547181240, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_888172635 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_888172635, ((ch.qos.logback.core.joran.spi.ElementPath)vc_913).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).peekLast());
        // AssertGenerator replace invocation
        boolean o_test2_cf2915__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_913);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2915__11);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2932_cf4049() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_923 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1861623951 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1861623951, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1895188525 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1895188525, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1659273588 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1659273588, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1789719613 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1789719613, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1049800048 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1049800048, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1792988583 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1792988583, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_test2_cf2932__11 = // StatementAdderMethod cloned existing statement
vc_923.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2932__11, 0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1256 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1256);
        // AssertGenerator replace invocation
        boolean o_test2_cf2932_cf4049__35 = // StatementAdderMethod cloned existing statement
vc_923.equals(vc_1256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2932_cf4049__35);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2932_cf4067() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_923 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1364642120 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1364642120, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_993749257 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_993749257, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_103468884 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_103468884, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1789719613 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1789719613, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1049800048 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1049800048, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1792988583 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1792988583, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_923).duplicate()).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_test2_cf2932__11 = // StatementAdderMethod cloned existing statement
vc_923.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2932__11, 0);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1268 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1268);
        // AssertGenerator replace invocation
        int o_test2_cf2932_cf4067__35 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1268);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2932_cf4067__35, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2921_cf3724() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_917 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1960518336 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1960518336, ((ch.qos.logback.core.joran.spi.ElementPath)vc_917).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_505120856 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_505120856, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_182061049 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_182061049, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_804461977 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_804461977, ((ch.qos.logback.core.joran.spi.ElementPath)vc_917).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1199947312 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1199947312, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_38200169 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_38200169, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).size(), 0);
        // AssertGenerator replace invocation
        int o_test2_cf2921__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_917);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2921__11, 0);
        // AssertGenerator replace invocation
        int o_test2_cf2921_cf3724__39 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_917);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2921_cf3724__39, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2922_cf3770_failAssert83() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_916 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_916);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_915 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1193828931 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1193828931, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_322904347 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_322904347, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1318059226 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1318059226, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_915).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_test2_cf2922__13 = // StatementAdderMethod cloned existing statement
vc_915.getPrefixMatchLength(vc_916);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test2_cf2922__13, 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_1158 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_1156 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_1156.getPrefixMatchLength(vc_1158);
            // MethodAssertGenerator build local variable
            Object o_43_0 = p.get(1);
            org.junit.Assert.fail("test2_cf2922_cf3770 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2915_cf3501() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_913 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1049527556 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1049527556, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        java.util.ArrayList collection_467700960 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_467700960, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_909037414 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_909037414, ((ch.qos.logback.core.joran.spi.ElementPath)vc_913).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1030108535 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1030108535, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1547181240 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1547181240, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).duplicate().equals(vc_913));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_913).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_888172635 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_888172635, ((ch.qos.logback.core.joran.spi.ElementPath)vc_913).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_913).peekLast());
        // AssertGenerator replace invocation
        boolean o_test2_cf2915__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_913);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2915__11);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1059 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test2_cf2915_cf3501__41 = // StatementAdderMethod cloned existing statement
vc_913.equals(vc_1059);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2915_cf3501__41);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2909_cf3379_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_909 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_226140425 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_226140425, ((ch.qos.logback.core.joran.spi.ElementPath)vc_909).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate().equals(vc_909));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1268809652 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1268809652, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).duplicate()).duplicate().equals(vc_909));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_909).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1453903925 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1453903925, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_909).duplicate()).duplicate().equals(vc_909));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_909).peekLast());
            // AssertGenerator replace invocation
            boolean o_test2_cf2909__11 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_909);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test2_cf2909__11);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_1023 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_1020 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_1020.isContainedIn(vc_1023);
            // MethodAssertGenerator build local variable
            Object o_45_0 = p.get(1);
            org.junit.Assert.fail("test2_cf2909_cf3379 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2914_cf3478() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_912 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_912);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_912);
        // AssertGenerator replace invocation
        boolean o_test2_cf2914__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_912);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2914__11);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1052 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1052);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_1051 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_947121708 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_947121708, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_879466366 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_879466366, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_343620588 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_343620588, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).duplicate()).peekLast());
        // AssertGenerator replace invocation
        int o_test2_cf2914_cf3478__21 = // StatementAdderMethod cloned existing statement
vc_1051.getTailMatchLength(vc_1052);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2914_cf3478__21, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2914_cf3478_cf5596_failAssert45() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_912 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_912);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_912);
            // AssertGenerator replace invocation
            boolean o_test2_cf2914__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_912);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test2_cf2914__11);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_1052 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1052);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_1051 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_947121708 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_947121708, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_879466366 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_879466366, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_343620588 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_343620588, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1051).duplicate()).duplicate()).peekLast());
            // AssertGenerator replace invocation
            int o_test2_cf2914_cf3478__21 = // StatementAdderMethod cloned existing statement
vc_1051.getTailMatchLength(vc_1052);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test2_cf2914_cf3478__21, 0);
            // StatementAdderMethod cloned existing statement
            vc_1051.fullPathMatch(vc_1052);
            // MethodAssertGenerator build local variable
            Object o_49_0 = p.get(1);
            org.junit.Assert.fail("test2_cf2914_cf3478_cf5596 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2927_cf3878_cf4130_failAssert66() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_921 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).duplicate().equals(vc_921));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate().equals(vc_921));
            // AssertGenerator add assertion
            java.util.ArrayList collection_767293884 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_767293884, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_937095776 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_937095776, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate().equals(vc_921));
            // AssertGenerator add assertion
            java.util.ArrayList collection_169491282 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_169491282, ((ch.qos.logback.core.joran.spi.ElementPath)vc_921).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).duplicate().equals(vc_921));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate().equals(vc_921));
            // AssertGenerator add assertion
            java.util.ArrayList collection_424641312 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_424641312, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_78830032 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_78830032, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_921).duplicate().equals(vc_921));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1762863210 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1762863210, ((ch.qos.logback.core.joran.spi.ElementPath)vc_921).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_test2_cf2927__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_921);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test2_cf2927__11, 0);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1190 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1190);
            // AssertGenerator replace invocation
            boolean o_test2_cf2927_cf3878__41 = // StatementAdderMethod cloned existing statement
vc_921.equals(vc_1190);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test2_cf2927_cf3878__41);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_1287 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_1284 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_1284.isContainedIn(vc_1287);
            // MethodAssertGenerator build local variable
            Object o_77_0 = p.get(1);
            org.junit.Assert.fail("test2_cf2927_cf3878_cf4130 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2921_cf3724_cf4587() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_917 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1840900265 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1840900265, ((ch.qos.logback.core.joran.spi.ElementPath)vc_917).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1486637593 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1486637593, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_2130651540 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2130651540, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1960518336 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1960518336, ((ch.qos.logback.core.joran.spi.ElementPath)vc_917).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_505120856 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_505120856, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_182061049 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_182061049, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_804461977 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_804461977, ((ch.qos.logback.core.joran.spi.ElementPath)vc_917).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1199947312 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1199947312, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate().equals(vc_917));
        // AssertGenerator add assertion
        java.util.ArrayList collection_38200169 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_38200169, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_917).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_917).size(), 0);
        // AssertGenerator replace invocation
        int o_test2_cf2921__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_917);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2921__11, 0);
        // AssertGenerator replace invocation
        int o_test2_cf2921_cf3724__39 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_917);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2921_cf3724__39, 0);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1422 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1422);
        // AssertGenerator replace invocation
        int o_test2_cf2921_cf3724_cf4587__69 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1422);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2921_cf3724_cf4587__69, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2914_cf3465_failAssert5_literalMutation4207() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("ah/b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1741131754 = new java.util.ArrayList<Object>();
	collection_1741131754.add("ah");
	collection_1741131754.add("b");
	org.junit.Assert.assertEquals(collection_1741131754, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_317260682 = new java.util.ArrayList<Object>();
	collection_317260682.add("ah");
	collection_317260682.add("b");
	org.junit.Assert.assertEquals(collection_317260682, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1453907708 = new java.util.ArrayList<Object>();
	collection_1453907708.add("ah");
	collection_1453907708.add("b");
	org.junit.Assert.assertEquals(collection_1453907708, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "b");
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, "ah");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_912 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_912);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_912);
            // AssertGenerator replace invocation
            boolean o_test2_cf2914__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_912);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test2_cf2914__11);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_1049 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_930113354 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_930113354, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1163576197 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1163576197, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).duplicate().equals(vc_1049));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate().equals(vc_1049));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1389373382 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1389373382, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1049).duplicate()).duplicate()).duplicate().equals(vc_1049));
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_1046 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1046);
            // StatementAdderMethod cloned existing statement
            vc_1046.getPrefixMatchLength(vc_1049);
            // MethodAssertGenerator build local variable
            Object o_23_0 = p.get(1);
            org.junit.Assert.fail("test2_cf2914_cf3465 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test2 */
    @org.junit.Test(timeout = 10000)
    public void test2_cf2931_cf4005_cf5887() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("b", p.peekLast());
        org.junit.Assert.assertEquals("a", p.get(0));
        // AssertGenerator replace invocation
        int o_test2_cf2931__9 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test2_cf2931__9, 3);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1235 = new java.lang.Object();
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_1233 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_875559530 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_875559530, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1262773060 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1262773060, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_234243028 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_234243028, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1319531119 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1319531119, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_443165859 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_443165859, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_82802162 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_82802162, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1233).duplicate()).duplicate()).peekLast());
        // AssertGenerator replace invocation
        boolean o_test2_cf2931_cf4005__17 = // StatementAdderMethod cloned existing statement
vc_1233.equals(vc_1235);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2931_cf4005__17);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1718 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1718);
        // AssertGenerator replace invocation
        boolean o_test2_cf2931_cf4005_cf5887__41 = // StatementAdderMethod cloned existing statement
vc_1235.equals(vc_1718);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test2_cf2931_cf4005_cf5887__41);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6111() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1775 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_869949883 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_869949883, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1417387286 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1417387286, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2022126209 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2022126209, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test3_cf6111__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1775);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6111__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6110() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1774 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1774);
        // AssertGenerator replace invocation
        int o_test3_cf6110__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1774);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6110__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6121() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // AssertGenerator replace invocation
        int o_test3_cf6121__9 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6121__9, -1426773145);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6099() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1767 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate().equals(vc_1767));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_407721202 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_407721202, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).duplicate().equals(vc_1767));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2005012935 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2005012935, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_2073867260 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2073867260, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1767).duplicate()).duplicate()).duplicate().equals(vc_1767));
        // AssertGenerator replace invocation
        boolean o_test3_cf6099__11 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_1767);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6099__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6117() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1779 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).duplicate().equals(vc_1779));
        // AssertGenerator add assertion
        java.util.ArrayList collection_731339391 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_731339391, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).duplicate()).duplicate().equals(vc_1779));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1165591008 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1165591008, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_852136212 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_852136212, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1779).duplicate().equals(vc_1779));
        // AssertGenerator replace invocation
        int o_test3_cf6117__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_1779);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6117__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6116() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1778 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1778);
        // AssertGenerator replace invocation
        int o_test3_cf6116__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_1778);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6116__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6105() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1771 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1622308208 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1622308208, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2101265782 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2101265782, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        java.util.ArrayList collection_988920293 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_988920293, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator replace invocation
        boolean o_test3_cf6105__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_1771);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6105__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6104() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1770 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1770);
        // AssertGenerator replace invocation
        boolean o_test3_cf6104__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_1770);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6104__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6092() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1762 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1762);
        // AssertGenerator replace invocation
        boolean o_test3_cf6092__11 = // StatementAdderMethod cloned existing statement
p.equals(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6092__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6118() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1778 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1778);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_1777 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_200952297 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_200952297, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1338501431 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1338501431, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1876314047 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1876314047, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_1777).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test3_cf6118__13 = // StatementAdderMethod cloned existing statement
vc_1777.getTailMatchLength(vc_1778);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6118__13, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6111_cf6982() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1775 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1803735050 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1803735050, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1892140555 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1892140555, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1823805246 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1823805246, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_869949883 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_869949883, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1417387286 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1417387286, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2022126209 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2022126209, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test3_cf6111__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1775);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6111__11, 0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1982 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1982);
        // AssertGenerator replace invocation
        boolean o_test3_cf6111_cf6982__41 = // StatementAdderMethod cloned existing statement
vc_1775.equals(vc_1982);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6111_cf6982__41);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6121_cf7347() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // AssertGenerator replace invocation
        int o_test3_cf6121__9 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6121__9, -1426773145);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2092 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2092);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_2091 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1473013976 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1473013976, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_515233754 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_515233754, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_579959164 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_579959164, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2091).duplicate()).peekLast());
        // AssertGenerator replace invocation
        boolean o_test3_cf6121_cf7347__17 = // StatementAdderMethod cloned existing statement
vc_2091.equals(vc_2092);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6121_cf7347__17);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6121_cf7357() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // AssertGenerator replace invocation
        int o_test3_cf6121__9 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6121__9, -1426773145);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2100 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2100);
        // AssertGenerator replace invocation
        boolean o_test3_cf6121_cf7357__15 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_2100);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6121_cf7357__15);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6110_cf6954() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1774 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1774);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1774);
        // AssertGenerator replace invocation
        int o_test3_cf6110__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1774);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6110__11, 0);
        // AssertGenerator replace invocation
        int o_test3_cf6110_cf6954__17 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6110_cf6954__17, -1426773145);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6104_cf6712_cf8867_failAssert79() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(1);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_1770 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1770);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1770);
            // AssertGenerator replace invocation
            boolean o_test3_cf6104__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_1770);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test3_cf6104__11);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_1903 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate().equals(vc_1903));
            // AssertGenerator add assertion
            java.util.ArrayList collection_112851174 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_112851174, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).duplicate().equals(vc_1903));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1109069646 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1109069646, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_534831432 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_534831432, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1903).duplicate()).duplicate()).duplicate().equals(vc_1903));
            // AssertGenerator replace invocation
            boolean o_test3_cf6104_cf6712__19 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_1903);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test3_cf6104_cf6712__19);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_2475 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_2472 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_2472.isContainedIn(vc_2475);
            // MethodAssertGenerator build local variable
            Object o_55_0 = p.get(2);
            org.junit.Assert.fail("test3_cf6104_cf6712_cf8867 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6105_cf6788_cf9381() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1771 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_500220960 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_500220960, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1608367566 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1608367566, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1910365648 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1910365648, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1706692635 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1706692635, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1599314587 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1599314587, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1474694009 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1474694009, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1622308208 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1622308208, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2101265782 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2101265782, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator add assertion
        java.util.ArrayList collection_988920293 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_988920293, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1771).duplicate()).duplicate()).duplicate().equals(vc_1771));
        // AssertGenerator replace invocation
        boolean o_test3_cf6105__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_1771);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6105__11);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1928 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1928);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1928);
        // AssertGenerator replace invocation
        int o_test3_cf6105_cf6788__41 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1928);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6105_cf6788__41, 0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2576 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2576);
        // AssertGenerator replace invocation
        boolean o_test3_cf6105_cf6788_cf9381__73 = // StatementAdderMethod cloned existing statement
vc_1771.equals(vc_2576);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6105_cf6788_cf9381__73);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6092_cf6175_cf7793() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1762 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1762);
        // AssertGenerator replace invocation
        boolean o_test3_cf6092__11 = // StatementAdderMethod cloned existing statement
p.equals(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6092__11);
        // AssertGenerator replace invocation
        boolean o_test3_cf6092_cf6175__17 = // StatementAdderMethod cloned existing statement
p.equals(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6092_cf6175__17);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2218 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2218);
        // AssertGenerator replace invocation
        int o_test3_cf6092_cf6175_cf7793__25 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_2218);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6092_cf6175_cf7793__25, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6104_cf6729_cf7507() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_1770 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1770);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1770);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1770);
        // AssertGenerator replace invocation
        boolean o_test3_cf6104__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_1770);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6104__11);
        // AssertGenerator replace invocation
        int o_test3_cf6104_cf6729__17 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_1770);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6104_cf6729__17, 0);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2153 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).duplicate().equals(vc_2153));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1634568695 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1634568695, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate().equals(vc_2153));
        // AssertGenerator add assertion
        java.util.ArrayList collection_191439179 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_191439179, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_870344597 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_870344597, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).duplicate()).duplicate()).duplicate().equals(vc_2153));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2153).size(), 0);
        // AssertGenerator replace invocation
        int o_test3_cf6104_cf6729_cf7507__25 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_2153);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6104_cf6729_cf7507__25, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6111_cf6982_cf8681() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_1775 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_158030999 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_158030999, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        java.util.ArrayList collection_125637004 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_125637004, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_153302867 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_153302867, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1803735050 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1803735050, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1892140555 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1892140555, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1823805246 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1823805246, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_869949883 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_869949883, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate().equals(vc_1775));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1417387286 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1417387286, ((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2022126209 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2022126209, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_1775).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test3_cf6111__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1775);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test3_cf6111__11, 0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1982 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1982);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1982);
        // AssertGenerator replace invocation
        boolean o_test3_cf6111_cf6982__41 = // StatementAdderMethod cloned existing statement
vc_1775.equals(vc_1982);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6111_cf6982__41);
        // AssertGenerator replace invocation
        boolean o_test3_cf6111_cf6982_cf8681__71 = // StatementAdderMethod cloned existing statement
vc_1775.equals(vc_1982);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test3_cf6111_cf6982_cf8681__71);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test3 */
    @org.junit.Test(timeout = 10000)
    public void test3_cf6116_cf7158_cf8261_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a123/b1234/cvvsdf");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(1);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_1778 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1778);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1778);
            // AssertGenerator replace invocation
            int o_test3_cf6116__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_1778);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test3_cf6116__11, 0);
            // AssertGenerator replace invocation
            int o_test3_cf6116_cf7158__17 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_1778);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test3_cf6116_cf7158__17, 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_2348 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_2348.getTailMatchLength(vc_1778);
            // MethodAssertGenerator build local variable
            Object o_27_0 = p.get(2);
            org.junit.Assert.fail("test3_cf6116_cf7158_cf8261 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9642() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2654 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator replace invocation
        int o_test4_cf9642__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9642__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9653() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // AssertGenerator replace invocation
        int o_test4_cf9653__9 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9653__9, -1426773145);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9631() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2647 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).duplicate()).duplicate().equals(vc_2647));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate().equals(vc_2647));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).duplicate().equals(vc_2647));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_157209971 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_157209971, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_495510482 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_495510482, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_752223569 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_752223569, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2647).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        boolean o_test4_cf9631__11 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_2647);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9631__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9643() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2655 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1458953485 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1458953485, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1110939394 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1110939394, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1551486355 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1551486355, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate().equals(vc_2655));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).duplicate().equals(vc_2655));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2655).duplicate()).duplicate()).duplicate().equals(vc_2655));
        // AssertGenerator replace invocation
        int o_test4_cf9643__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_2655);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9643__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9648() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2658 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2658);
        // AssertGenerator replace invocation
        int o_test4_cf9648__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_2658);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9648__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9637() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2651 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        java.util.ArrayList collection_269980 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_269980, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1181417656 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1181417656, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1407533817 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1407533817, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_test4_cf9637__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_2651);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9637__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9649() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2659 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate().equals(vc_2659));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_36637384 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_36637384, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_102643694 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_102643694, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1432872171 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1432872171, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).duplicate().equals(vc_2659));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2659).duplicate()).duplicate()).duplicate().equals(vc_2659));
        // AssertGenerator replace invocation
        int o_test4_cf9649__11 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_2659);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9649__11, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9624() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2642 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2642);
        // AssertGenerator replace invocation
        boolean o_test4_cf9624__11 = // StatementAdderMethod cloned existing statement
p.equals(vc_2642);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9624__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9636() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2650 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2650);
        // AssertGenerator replace invocation
        boolean o_test4_cf9636__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_2650);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9636__11);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9624_cf9751() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2642 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2642);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2642);
        // AssertGenerator replace invocation
        boolean o_test4_cf9624__11 = // StatementAdderMethod cloned existing statement
p.equals(vc_2642);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9624__11);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2676 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2676);
        // AssertGenerator replace invocation
        int o_test4_cf9624_cf9751__19 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_2676);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9624_cf9751__19, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9637_cf10330() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2651 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1364740424 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1364740424, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_46324909 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_46324909, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1021280785 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1021280785, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        java.util.ArrayList collection_269980 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_269980, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1181417656 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1181417656, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).duplicate().equals(vc_2651));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1407533817 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1407533817, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_test4_cf9637__11 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_2651);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9637__11);
        // AssertGenerator replace invocation
        int o_test4_cf9637_cf10330__39 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_2651);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9637_cf10330__39, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9635_failAssert31_literalMutation10198() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234ccvvsdf");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "b1234ccvvsdf");
            // AssertGenerator add assertion
            java.util.ArrayList collection_2078864630 = new java.util.ArrayList<Object>();
	collection_2078864630.add("a123");
	collection_2078864630.add("b1234ccvvsdf");
	org.junit.Assert.assertEquals(collection_2078864630, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "b1234ccvvsdf");
            // AssertGenerator add assertion
            java.util.ArrayList collection_92241131 = new java.util.ArrayList<Object>();
	collection_92241131.add("a123");
	collection_92241131.add("b1234ccvvsdf");
	org.junit.Assert.assertEquals(collection_92241131, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "b1234ccvvsdf");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1687479946 = new java.util.ArrayList<Object>();
	collection_1687479946.add("a123");
	collection_1687479946.add("b1234ccvvsdf");
	org.junit.Assert.assertEquals(collection_1687479946, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "a123");
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, "b1234ccvvsdf");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_2651 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate().equals(vc_2651));
            // AssertGenerator add assertion
            java.util.ArrayList collection_2122242327 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2122242327, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_147068555 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_147068555, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate().equals(vc_2651));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).duplicate().equals(vc_2651));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1947372468 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1947372468, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2651).duplicate()).duplicate()).getCopyOfPartList());;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_2648 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2648);
            // StatementAdderMethod cloned existing statement
            vc_2648.isContainedIn(vc_2651);
            // MethodAssertGenerator build local variable
            Object o_15_0 = p.get(2);
            org.junit.Assert.fail("test4_cf9635 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9644_cf10589_failAssert52() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(1);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_2654 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2654);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_2653 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1888227141 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1888227141, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_762867617 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_762867617, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_945587600 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_945587600, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2653).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_test4_cf9644__13 = // StatementAdderMethod cloned existing statement
vc_2653.getPrefixMatchLength(vc_2654);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test4_cf9644__13, 0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_2889 = new ch.qos.logback.core.joran.spi.ElementPath();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_2886 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_2886.fullPathMatch(vc_2889);
            // MethodAssertGenerator build local variable
            Object o_43_0 = p.get(2);
            org.junit.Assert.fail("test4_cf9644_cf10589 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9650_cf10819_cf12273_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(1);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_2658 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2658);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2658);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_2657 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_2084159872 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2084159872, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1841941228 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1841941228, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1510330203 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1510330203, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_489406074 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_489406074, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_271301724 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_271301724, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_372916380 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_372916380, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_2657).duplicate()).duplicate()).peekLast());
            // AssertGenerator replace invocation
            int o_test4_cf9650__13 = // StatementAdderMethod cloned existing statement
vc_2657.getTailMatchLength(vc_2658);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test4_cf9650__13, 0);
            // AssertGenerator replace invocation
            boolean o_test4_cf9650_cf10819__37 = // StatementAdderMethod cloned existing statement
vc_2657.isContainedIn(vc_2658);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test4_cf9650_cf10819__37);
            // StatementAdderMethod cloned existing statement
            vc_2658.hashCode();
            // MethodAssertGenerator build local variable
            Object o_63_0 = p.get(2);
            org.junit.Assert.fail("test4_cf9650_cf10819_cf12273 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9642_cf10462_cf11273() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2654 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator replace invocation
        int o_test4_cf9642__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9642__11, 0);
        // AssertGenerator replace invocation
        boolean o_test4_cf9642_cf10462__17 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9642_cf10462__17);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3090 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3090);
        // AssertGenerator replace invocation
        boolean o_test4_cf9642_cf10462_cf11273__25 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_3090);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9642_cf10462_cf11273__25);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9642_cf10463_cf11521() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_2654 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2654);
        // AssertGenerator replace invocation
        int o_test4_cf9642__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_2654);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9642__11, 0);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_2849 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).duplicate().equals(vc_2849));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1183168730 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1183168730, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1083272105 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1083272105, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1962358000 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1962358000, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate().equals(vc_2849));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate().equals(vc_2849));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).duplicate().equals(vc_2849));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1324264698 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1324264698, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_699533290 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_699533290, ((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1761527837 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1761527837, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate().equals(vc_2849));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate().equals(vc_2849));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_2849).duplicate()).duplicate()).peekLast());
        // AssertGenerator replace invocation
        boolean o_test4_cf9642_cf10463__19 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_2849);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9642_cf10463__19);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3149 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test4_cf9642_cf10463_cf11521__51 = // StatementAdderMethod cloned existing statement
vc_2849.equals(vc_3149);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9642_cf10463_cf11521__51);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9625_cf9811_cf12069() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
        org.junit.Assert.assertEquals(3, p.size());
        org.junit.Assert.assertEquals("a123", p.get(0));
        org.junit.Assert.assertEquals("b1234", p.get(1));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_2643 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test4_cf9625__11 = // StatementAdderMethod cloned existing statement
p.equals(vc_2643);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test4_cf9625__11);
        // AssertGenerator replace invocation
        boolean o_test4_cf9625_cf9811__15 = // StatementAdderMethod cloned existing statement
vc_2643.equals(vc_2643);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_test4_cf9625_cf9811__15);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_3277 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1247941474 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1247941474, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_35376173 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_35376173, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1479399163 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1479399163, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3277).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_test4_cf9625_cf9811_cf12069__21 = // StatementAdderMethod cloned existing statement
vc_3277.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test4_cf9625_cf9811_cf12069__21, 0);
        org.junit.Assert.assertEquals("cvvsdf", p.get(2));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test4 */
    @org.junit.Test(timeout = 10000)
    public void test4_cf9642_cf10486_cf13304_failAssert77() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a123/b1234/cvvsdf");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = p.get(1);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_2654 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2654);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2654);
            // AssertGenerator replace invocation
            int o_test4_cf9642__11 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_2654);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test4_cf9642__11, 0);
            // AssertGenerator replace invocation
            int o_test4_cf9642_cf10486__17 = // StatementAdderMethod cloned existing statement
p.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test4_cf9642_cf10486__17, -1426773145);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_3526 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // StatementAdderMethod cloned existing statement
            p.fullPathMatch(vc_3526);
            // MethodAssertGenerator build local variable
            Object o_27_0 = p.get(2);
            org.junit.Assert.fail("test4_cf9642_cf10486_cf13304 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13689() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_3623 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1016297914 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1016297914, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).duplicate().equals(vc_3623));
        // AssertGenerator add assertion
        java.util.ArrayList collection_13750506 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_13750506, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).duplicate()).duplicate().equals(vc_3623));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate().equals(vc_3623));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2048310034 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2048310034, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3623).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_test5_cf13689__7 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_3623);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13689__7, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13700() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_3629 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1446338408 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1446338408, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1783953068 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1783953068, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_510026671 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_510026671, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3629).duplicate()).peekLast());
        // AssertGenerator replace invocation
        int o_test5_cf13700__7 = // StatementAdderMethod cloned existing statement
vc_3629.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13700__7, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13688() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3622 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3622);
        // AssertGenerator replace invocation
        int o_test5_cf13688__7 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_3622);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13688__7, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13699() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // AssertGenerator replace invocation
        int o_test5_cf13699__5 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13699__5, 97);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13677() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_3615 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1927424456 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1927424456, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        java.util.ArrayList collection_220953446 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_220953446, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_731518306 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_731518306, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate().equals(vc_3615));
        // AssertGenerator replace invocation
        boolean o_test5_cf13677__7 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_3615);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13677__7);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13694() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3626 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3626);
        // AssertGenerator replace invocation
        int o_test5_cf13694__7 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3626);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13694__7, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13683() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_3619 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate().equals(vc_3619));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate().equals(vc_3619));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).duplicate().equals(vc_3619));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1712754736 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1712754736, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_459701128 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_459701128, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1331956476 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1331956476, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).size(), 0);
        // AssertGenerator replace invocation
        boolean o_test5_cf13683__7 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_3619);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13683__7);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13671() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3611 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test5_cf13671__7 = // StatementAdderMethod cloned existing statement
p.equals(vc_3611);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13671__7);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13682() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3618 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3618);
        // AssertGenerator replace invocation
        boolean o_test5_cf13682__7 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_3618);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13682__7);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13695() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_3627 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1432101549 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1432101549, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_582315225 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_582315225, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        java.util.ArrayList collection_807559318 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_807559318, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate().equals(vc_3627));
        // AssertGenerator replace invocation
        int o_test5_cf13695__7 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3627);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13695__7, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13671_cf13917() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3611 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test5_cf13671__7 = // StatementAdderMethod cloned existing statement
p.equals(vc_3611);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13671__7);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3692 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3692);
        // AssertGenerator replace invocation
        int o_test5_cf13671_cf13917__13 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3692);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13671_cf13917__13, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13671_cf13909() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3611 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test5_cf13671__7 = // StatementAdderMethod cloned existing statement
p.equals(vc_3611);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13671__7);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3688 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3688);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_3687 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1102369464 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1102369464, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_2028794668 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2028794668, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_80193040 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_80193040, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3687).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test5_cf13671_cf13909__15 = // StatementAdderMethod cloned existing statement
vc_3687.getPrefixMatchLength(vc_3688);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13671_cf13909__15, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13695_cf14626() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_3627 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1963570824 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1963570824, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_707555251 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_707555251, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        java.util.ArrayList collection_821125370 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_821125370, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1432101549 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1432101549, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_582315225 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_582315225, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        java.util.ArrayList collection_807559318 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_807559318, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate()).duplicate().equals(vc_3627));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3627).duplicate()).duplicate().equals(vc_3627));
        // AssertGenerator replace invocation
        int o_test5_cf13695__7 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3627);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13695__7, 0);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3919 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test5_cf13695_cf14626__37 = // StatementAdderMethod cloned existing statement
vc_3627.equals(vc_3919);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13695_cf14626__37);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13681_failAssert16_literalMutation14199() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_2127431773 = new java.util.ArrayList<Object>();
	collection_2127431773.add("  ");
	org.junit.Assert.assertEquals(collection_2127431773, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1189770142 = new java.util.ArrayList<Object>();
	collection_1189770142.add("  ");
	org.junit.Assert.assertEquals(collection_1189770142, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_454399184 = new java.util.ArrayList<Object>();
	collection_454399184.add("  ");
	org.junit.Assert.assertEquals(collection_454399184, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 1);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_3619 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate().equals(vc_3619));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate().equals(vc_3619));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).duplicate().equals(vc_3619));
            // AssertGenerator add assertion
            java.util.ArrayList collection_128355154 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_128355154, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1931360636 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1931360636, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_379200531 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_379200531, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3619).size(), 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_3616 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3616);
            // StatementAdderMethod cloned existing statement
            vc_3616.isContainedIn(vc_3619);
            // MethodAssertGenerator build local variable
            Object o_11_0 = p.get(0);
            org.junit.Assert.fail("test5_cf13681 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13696_literalMutation14669() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_837334413 = new java.util.ArrayList<Object>();
	collection_837334413.add("a");
	org.junit.Assert.assertEquals(collection_837334413, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_389239421 = new java.util.ArrayList<Object>();
	collection_389239421.add("a");
	org.junit.Assert.assertEquals(collection_389239421, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_437407638 = new java.util.ArrayList<Object>();
	collection_437407638.add("a");
	org.junit.Assert.assertEquals(collection_437407638, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3626 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3626);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3626);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_3625 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1851549786 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1851549786, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1434280505 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1434280505, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_228484531 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_228484531, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1543450486 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1543450486, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1365675834 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1365675834, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1707840984 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1707840984, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_test5_cf13696__9 = // StatementAdderMethod cloned existing statement
vc_3625.getTailMatchLength(vc_3626);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13696__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_literalMutation13660_cf13734_cf15299() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("/a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1044803908 = new java.util.ArrayList<Object>();
	collection_1044803908.add("a");
	org.junit.Assert.assertEquals(collection_1044803908, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1530431771 = new java.util.ArrayList<Object>();
	collection_1530431771.add("a");
	org.junit.Assert.assertEquals(collection_1530431771, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1585838338 = new java.util.ArrayList<Object>();
	collection_1585838338.add("a");
	org.junit.Assert.assertEquals(collection_1585838338, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1681749576 = new java.util.ArrayList<Object>();
	collection_1681749576.add("a");
	org.junit.Assert.assertEquals(collection_1681749576, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_2004869486 = new java.util.ArrayList<Object>();
	collection_2004869486.add("a");
	org.junit.Assert.assertEquals(collection_2004869486, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1638376806 = new java.util.ArrayList<Object>();
	collection_1638376806.add("a");
	org.junit.Assert.assertEquals(collection_1638376806, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1034979067 = new java.util.ArrayList<Object>();
	collection_1034979067.add("a");
	org.junit.Assert.assertEquals(collection_1034979067, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_195476700 = new java.util.ArrayList<Object>();
	collection_195476700.add("a");
	org.junit.Assert.assertEquals(collection_195476700, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "a");
        // AssertGenerator add assertion
        java.util.ArrayList collection_874599307 = new java.util.ArrayList<Object>();
	collection_874599307.add("a");
	org.junit.Assert.assertEquals(collection_874599307, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_3648 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3648);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3648);
        // AssertGenerator replace invocation
        int o_test5_literalMutation13660_cf13734__25 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3648);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_literalMutation13660_cf13734__25, 0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4116 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4116);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_4115 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_173536715 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_173536715, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1534759320 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1534759320, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1806361614 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1806361614, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4115).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_test5_literalMutation13660_cf13734_cf15299__53 = // StatementAdderMethod cloned existing statement
vc_4115.equals(vc_4116);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_literalMutation13660_cf13734_cf15299__53);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13677_cf14143_cf17361() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
        org.junit.Assert.assertEquals(1, p.size());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_3615 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1139730763 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1139730763, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        java.util.ArrayList collection_660926533 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_660926533, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1511110297 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1511110297, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1416057897 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1416057897, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1531599916 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1531599916, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1877161926 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1877161926, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1927424456 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1927424456, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        java.util.ArrayList collection_220953446 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_220953446, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_731518306 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_731518306, ((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).duplicate().equals(vc_3615));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_3615).duplicate().equals(vc_3615));
        // AssertGenerator replace invocation
        boolean o_test5_cf13677__7 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_3615);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13677__7);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3742 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3742);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3742);
        // AssertGenerator replace invocation
        boolean o_test5_cf13677_cf14143__37 = // StatementAdderMethod cloned existing statement
p.equals(vc_3742);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test5_cf13677_cf14143__37);
        // AssertGenerator replace invocation
        int o_test5_cf13677_cf14143_cf17361__67 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3615);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test5_cf13677_cf14143_cf17361__67, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13696_cf14680_cf16581_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_3626 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3626);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3626);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_3625 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1778762811 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1778762811, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_196366803 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_196366803, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_430768973 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_430768973, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1543450486 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1543450486, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1365675834 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1365675834, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1707840984 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1707840984, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_3625).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_test5_cf13696__9 = // StatementAdderMethod cloned existing statement
vc_3625.getTailMatchLength(vc_3626);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test5_cf13696__9, 0);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3940 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3940);
            // AssertGenerator replace invocation
            boolean o_test5_cf13696_cf14680__35 = // StatementAdderMethod cloned existing statement
p.equals(vc_3940);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test5_cf13696_cf14680__35);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_4464 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_4464.hashCode();
            // MethodAssertGenerator build local variable
            Object o_65_0 = p.get(0);
            org.junit.Assert.fail("test5_cf13696_cf14680_cf16581 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test5 */
    @org.junit.Test(timeout = 10000)
    public void test5_cf13670_cf13827_cf17222_failAssert32() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3610 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3610);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3610);
            // AssertGenerator replace invocation
            boolean o_test5_cf13670__7 = // StatementAdderMethod cloned existing statement
p.equals(vc_3610);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test5_cf13670__7);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_3670 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3670);
            // AssertGenerator replace invocation
            int o_test5_cf13670_cf13827__15 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_3670);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test5_cf13670_cf13827__15, 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_4630 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_4628 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_4628.isContainedIn(vc_4630);
            // MethodAssertGenerator build local variable
            Object o_29_0 = p.get(0);
            org.junit.Assert.fail("test5_cf13670_cf13827_cf17222 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17509() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // AssertGenerator replace invocation
        int o_test6_cf17509__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17509__7, 3);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17493() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_4697 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate().equals(vc_4697));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).duplicate()).duplicate().equals(vc_4697));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).duplicate().equals(vc_4697));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1216438340 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1216438340, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1051952816 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1051952816, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1539217208 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1539217208, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4697).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_test6_cf17493__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_4697);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test6_cf17493__9);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17481() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_4689 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_test6_cf17481__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_4689);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test6_cf17481__9);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17492() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4696 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4696);
        // AssertGenerator replace invocation
        boolean o_test6_cf17492__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_4696);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test6_cf17492__9);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17506() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4704 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_4703 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1638225240 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1638225240, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1169901385 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1169901385, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_2053117419 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2053117419, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4703).size(), 0);
        // AssertGenerator replace invocation
        int o_test6_cf17506__11 = // StatementAdderMethod cloned existing statement
vc_4703.getTailMatchLength(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17506__11, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17499() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_4701 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate().equals(vc_4701));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_309976181 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_309976181, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate().equals(vc_4701));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).duplicate().equals(vc_4701));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1721543585 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1721543585, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_155037805 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_155037805, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).size(), 0);
        // AssertGenerator replace invocation
        int o_test6_cf17499__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_4701);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17499__9, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17498() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4700 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4700);
        // AssertGenerator replace invocation
        int o_test6_cf17498__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_4700);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17498__9, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17505() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_4705 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).duplicate().equals(vc_4705));
        // AssertGenerator add assertion
        java.util.ArrayList collection_874928146 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_874928146, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).duplicate()).duplicate().equals(vc_4705));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1835475387 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1835475387, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1958685725 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1958685725, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4705).duplicate().equals(vc_4705));
        // AssertGenerator replace invocation
        int o_test6_cf17505__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4705);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17505__9, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17504() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4704 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // AssertGenerator replace invocation
        int o_test6_cf17504__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17504__9, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17504_cf18433() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4704 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // AssertGenerator replace invocation
        int o_test6_cf17504__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17504__9, 0);
        // AssertGenerator replace invocation
        boolean o_test6_cf17504_cf18433__15 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test6_cf17504_cf18433__15);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17509_cf18593() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // AssertGenerator replace invocation
        int o_test6_cf17509__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17509__7, 3);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5040 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5040);
        // AssertGenerator replace invocation
        boolean o_test6_cf17509_cf18593__13 = // StatementAdderMethod cloned existing statement
p.equals(vc_5040);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test6_cf17509_cf18593__13);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17487_cf17979_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_4693 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).duplicate().equals(vc_4693));
            // AssertGenerator add assertion
            java.util.ArrayList collection_24589034 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_24589034, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).duplicate()).duplicate().equals(vc_4693));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate().equals(vc_4693));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1235864220 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1235864220, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1139607373 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1139607373, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4693).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            boolean o_test6_cf17487__9 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_4693);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test6_cf17487__9);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_4824 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_4822 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_4822.fullPathMatch(vc_4824);
            // MethodAssertGenerator build local variable
            Object o_43_0 = p.get(1);
            org.junit.Assert.fail("test6_cf17487_cf17979 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17498_cf18267() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4700 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4700);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4700);
        // AssertGenerator replace invocation
        int o_test6_cf17498__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_4700);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17498__9, 0);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4924 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4924);
        // AssertGenerator replace invocation
        int o_test6_cf17498_cf18267__17 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4924);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17498_cf18267__17, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17504_cf18433_cf19732() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
        org.junit.Assert.assertEquals(2, p.size());
        org.junit.Assert.assertEquals("a", p.get(0));
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_4704 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4704);
        // AssertGenerator replace invocation
        int o_test6_cf17504__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17504__9, 0);
        // AssertGenerator replace invocation
        boolean o_test6_cf17504_cf18433__15 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_test6_cf17504_cf18433__15);
        // AssertGenerator replace invocation
        int o_test6_cf17504_cf18433_cf19732__21 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4704);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_test6_cf17504_cf18433_cf19732__21, 0);
        org.junit.Assert.assertEquals("b", p.get(1));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17510_cf18636_failAssert19_literalMutation18957() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a}//b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1118142626 = new java.util.ArrayList<Object>();
	collection_1118142626.add("a}");
	collection_1118142626.add("b");
	org.junit.Assert.assertEquals(collection_1118142626, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_218004233 = new java.util.ArrayList<Object>();
	collection_218004233.add("a}");
	collection_218004233.add("b");
	org.junit.Assert.assertEquals(collection_218004233, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_937308874 = new java.util.ArrayList<Object>();
	collection_937308874.add("a}");
	collection_937308874.add("b");
	org.junit.Assert.assertEquals(collection_937308874, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "a}");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_4707 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1259846128 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1259846128, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_274941597 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_274941597, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_943113024 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_943113024, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1981543346 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1981543346, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1818264262 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1818264262, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_686120661 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_686120661, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_4707).size(), 0);
            // AssertGenerator replace invocation
            int o_test6_cf17510__9 = // StatementAdderMethod cloned existing statement
vc_4707.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test6_cf17510__9, 0);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5062 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5062);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_5060 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5060);
            // StatementAdderMethod cloned existing statement
            vc_5060.equals(vc_5062);
            // MethodAssertGenerator build local variable
            Object o_37_0 = p.get(1);
            org.junit.Assert.fail("test6_cf17510_cf18636 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17498_cf18274_cf19125_failAssert57() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_4700 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4700);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4700);
            // AssertGenerator replace invocation
            int o_test6_cf17498__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_4700);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test6_cf17498__9, 0);
            // AssertGenerator replace invocation
            int o_test6_cf17498_cf18274__15 = // StatementAdderMethod cloned existing statement
p.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test6_cf17498_cf18274__15, 3);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5194 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_5192 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_5192.equals(vc_5194);
            // MethodAssertGenerator build local variable
            Object o_27_0 = p.get(1);
            org.junit.Assert.fail("test6_cf17498_cf18274_cf19125 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17497_failAssert20_add18213_add18763() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "a");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "a");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_4701 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate().equals(vc_4701));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_63522447 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_63522447, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate().equals(vc_4701));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).duplicate().equals(vc_4701));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1322496266 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1322496266, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_754662677 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_754662677, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate().equals(vc_4701));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1077383227 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1077383227, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate().equals(vc_4701));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).duplicate().equals(vc_4701));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1047518697 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1047518697, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_129726431 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_129726431, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4701).size(), 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_4698 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4698);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4698);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_4698.getPrefixMatchLength(vc_4701);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_4698.getPrefixMatchLength(vc_4701);
            // StatementAdderMethod cloned existing statement
            vc_4698.getPrefixMatchLength(vc_4701);
            // MethodAssertGenerator build local variable
            Object o_13_0 = p.get(1);
            org.junit.Assert.fail("test6_cf17497 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#test6 */
    @org.junit.Test(timeout = 10000)
    public void test6_cf17504_cf18434_cf20645_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("//a//b");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.get(0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_4704 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4704);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4704);
            // AssertGenerator replace invocation
            int o_test6_cf17504__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_4704);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_test6_cf17504__9, 0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_4983 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1169072261 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1169072261, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_649965429 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_649965429, ((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1674355064 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1674355064, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).duplicate()).duplicate().equals(vc_4983));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).duplicate().equals(vc_4983));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).duplicate().equals(vc_4983));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_4983).size(), 0);
            // AssertGenerator replace invocation
            boolean o_test6_cf17504_cf18434__17 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_4983);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_test6_cf17504_cf18434__17);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5590 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_5588 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_5588.equals(vc_5590);
            // MethodAssertGenerator build local variable
            Object o_53_0 = p.get(1);
            org.junit.Assert.fail("test6_cf17504_cf18434_cf20645 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20938() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_5647 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1722571548 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1722571548, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).duplicate()).duplicate().equals(vc_5647));
            // AssertGenerator add assertion
            java.util.ArrayList collection_654714992 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_654714992, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).duplicate().equals(vc_5647));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1807975809 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1807975809, ((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate().equals(vc_5647));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_5647).peekLast());
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20938__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getPrefixMatchLength(vc_5647);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20938__50, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20927() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_5642 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5642);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20927__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(vc_5642);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20927__50);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20928() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20928__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20928__48);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20952() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20952__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20952__48, 42);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20931() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_5641 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1019753464 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1019753464, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1825207998 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1825207998, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_751156432 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_751156432, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5641).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20931__50 = // StatementAdderMethod cloned existing statement
vc_5641.isContainedIn(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testPrefixMatch_cf20931__50);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20911() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5634 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5634);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20911__50 = // StatementAdderMethod cloned existing statement
p.equals(vc_5634);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20911__50);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20945() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_5650 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5650);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20945__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_5650);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20945__50, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20914() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_5635 = new java.lang.Object();
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_5633 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1220876419 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1220876419, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_195052755 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_195052755, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1710941916 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1710941916, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5633).size(), 0);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20914__52 = // StatementAdderMethod cloned existing statement
vc_5633.equals(vc_5635);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20914__52);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20936() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_5646 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5646);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20936__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getPrefixMatchLength(vc_5646);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20936__50, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20947() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_5651 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_182531148 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_182531148, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).duplicate()).duplicate().equals(vc_5651));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_541034823 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_541034823, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).duplicate().equals(vc_5651));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate().equals(vc_5651));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_38030973 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_38030973, ((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_5651).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20947__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_5651);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20947__50, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_literalMutation20904_cf23786() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1563031062 = new java.util.ArrayList<Object>();
	collection_1563031062.add("  ");
	org.junit.Assert.assertEquals(collection_1563031062, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_204737686 = new java.util.ArrayList<Object>();
	collection_204737686.add("  ");
	org.junit.Assert.assertEquals(collection_204737686, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1393387319 = new java.util.ArrayList<Object>();
	collection_1393387319.add("  ");
	org.junit.Assert.assertEquals(collection_1393387319, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_2094242529 = new java.util.ArrayList<Object>();
	collection_2094242529.add("  ");
	org.junit.Assert.assertEquals(collection_2094242529, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1936971045 = new java.util.ArrayList<Object>();
	collection_1936971045.add("  ");
	org.junit.Assert.assertEquals(collection_1936971045, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1526471319 = new java.util.ArrayList<Object>();
	collection_1526471319.add("  ");
	org.junit.Assert.assertEquals(collection_1526471319, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_6131 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).duplicate().equals(vc_6131));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_898011375 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_898011375, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate().equals(vc_6131));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate().equals(vc_6131));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_947631304 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_947631304, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_409349211 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_409349211, ((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).peekLast());
            // AssertGenerator replace invocation
            int o_testPrefixMatch_literalMutation20904_cf23786__68 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getPrefixMatchLength(vc_6131);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_literalMutation20904_cf23786__68, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20952_cf27802() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20952__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20952__48, 42);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_6603 = new java.lang.Object();
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_6601 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_495743672 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_495743672, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_2110787284 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2110787284, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_837551532 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_837551532, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_6601).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20952_cf27802__56 = // StatementAdderMethod cloned existing statement
vc_6601.equals(vc_6603);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20952_cf27802__56);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20928_cf25557_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_34_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
                // AssertGenerator replace invocation
                boolean o_testPrefixMatch_cf20928__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_testPrefixMatch_cf20928__48);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_6311 = new ch.qos.logback.core.joran.spi.ElementPath();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_6308 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_6308.getTailMatchLength(vc_6311);
                // MethodAssertGenerator build local variable
                Object o_58_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            org.junit.Assert.fail("testPrefixMatch_cf20928_cf25557 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20940_literalMutation26690() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("//*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_104734 = new java.util.ArrayList<Object>();
	collection_104734.add("*");
	org.junit.Assert.assertEquals(collection_104734, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1838166895 = new java.util.ArrayList<Object>();
	collection_1838166895.add("*");
	org.junit.Assert.assertEquals(collection_1838166895, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_931407320 = new java.util.ArrayList<Object>();
	collection_931407320.add("*");
	org.junit.Assert.assertEquals(collection_931407320, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_5645 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_305441084 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_305441084, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_408576883 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_408576883, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_10614399 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_10614399, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_411626084 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_411626084, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_336168201 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_336168201, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_213678756 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_213678756, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).size(), 0);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20940__50 = // StatementAdderMethod cloned existing statement
vc_5645.getPrefixMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20940__50, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20939_literalMutation26554() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_5646 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5646);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5646);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_5645 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1184771145 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1184771145, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1013149895 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1013149895, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1793587107 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1793587107, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1521372456 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1521372456, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1515615622 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1515615622, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_695363290 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_695363290, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5645).size(), 0);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20939__52 = // StatementAdderMethod cloned existing statement
vc_5645.getPrefixMatchLength(vc_5646);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20939__52, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20949_literalMutation27523() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/F/*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_763089658 = new java.util.ArrayList<Object>();
	collection_763089658.add("F");
	collection_763089658.add("*");
	org.junit.Assert.assertEquals(collection_763089658, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1339869148 = new java.util.ArrayList<Object>();
	collection_1339869148.add("F");
	collection_1339869148.add("*");
	org.junit.Assert.assertEquals(collection_1339869148, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_359064749 = new java.util.ArrayList<Object>();
	collection_359064749.add("F");
	collection_359064749.add("*");
	org.junit.Assert.assertEquals(collection_359064749, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_5649 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_490340925 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_490340925, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1440604552 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1440604552, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1639810330 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1639810330, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1469228868 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1469228868, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1802334729 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1802334729, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_467883733 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_467883733, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5649).size(), 0);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20949__50 = // StatementAdderMethod cloned existing statement
vc_5649.getTailMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20949__50, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20922_literalMutation24994_cf32117() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_5637 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_2046632629 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2046632629, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_2094548344 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2094548344, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_334408268 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_334408268, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_288304452 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_288304452, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1048519129 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1048519129, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_899271602 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_899271602, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1017778146 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1017778146, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_95863385 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_95863385, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_453013495 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_453013495, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_5637).getCopyOfPartList());;
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20922__50 = // StatementAdderMethod cloned existing statement
vc_5637.fullPathMatch(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20922__50);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20922_literalMutation24994_cf32117__91 = // StatementAdderMethod cloned existing statement
vc_5637.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20922_literalMutation24994_cf32117__91, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20946_cf27222_cf30051() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20946__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20946__48, 0);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20946_cf27222__52 = // StatementAdderMethod cloned existing statement
ruleElementSelector.fullPathMatch(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20946_cf27222__52);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_6874 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6874);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20946_cf27222_cf30051__58 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(vc_6874);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20946_cf27222_cf30051__58);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20928_cf25564_failAssert65_literalMutation35626() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getPrefixMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_6_0, 0);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getPrefixMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_13_0, 0);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ruleElementSelector.getPrefixMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_20_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ruleElementSelector.getPrefixMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_27_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/AA/b");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
                // AssertGenerator add assertion
                java.util.ArrayList collection_230105585 = new java.util.ArrayList<Object>();
	collection_230105585.add("AA");
	collection_230105585.add("b");
	org.junit.Assert.assertEquals(collection_230105585, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "b");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "b");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1092805648 = new java.util.ArrayList<Object>();
	collection_1092805648.add("AA");
	collection_1092805648.add("b");
	org.junit.Assert.assertEquals(collection_1092805648, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
                // AssertGenerator add assertion
                java.util.ArrayList collection_2098333573 = new java.util.ArrayList<Object>();
	collection_2098333573.add("AA");
	collection_2098333573.add("b");
	org.junit.Assert.assertEquals(collection_2098333573, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_34_0 = ruleElementSelector.getPrefixMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_0, 0);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ruleElementSelector.getPrefixMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_41_0, 2);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
                // AssertGenerator replace invocation
                boolean o_testPrefixMatch_cf20928__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_testPrefixMatch_cf20928__48);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_6312 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_6312);
                // StatementAdderMethod cloned existing statement
                vc_6312.hashCode();
                // MethodAssertGenerator build local variable
                Object o_56_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            org.junit.Assert.fail("testPrefixMatch_cf20928_cf25564 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_literalMutation20904_cf23786_cf36179_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_34_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1563031062 = new java.util.ArrayList<Object>();
	collection_1563031062.add("  ");
	org.junit.Assert.assertEquals(collection_1563031062, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_204737686 = new java.util.ArrayList<Object>();
	collection_204737686.add("  ");
	org.junit.Assert.assertEquals(collection_204737686, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1393387319 = new java.util.ArrayList<Object>();
	collection_1393387319.add("  ");
	org.junit.Assert.assertEquals(collection_1393387319, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_2094242529 = new java.util.ArrayList<Object>();
	collection_2094242529.add("  ");
	org.junit.Assert.assertEquals(collection_2094242529, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1936971045 = new java.util.ArrayList<Object>();
	collection_1936971045.add("  ");
	org.junit.Assert.assertEquals(collection_1936971045, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1526471319 = new java.util.ArrayList<Object>();
	collection_1526471319.add("  ");
	org.junit.Assert.assertEquals(collection_1526471319, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_6131 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).duplicate().equals(vc_6131));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_898011375 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_898011375, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate().equals(vc_6131));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate().equals(vc_6131));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_947631304 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_947631304, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_409349211 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_409349211, ((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_6131).duplicate()).duplicate()).peekLast());
                // AssertGenerator replace invocation
                int o_testPrefixMatch_literalMutation20904_cf23786__68 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getPrefixMatchLength(vc_6131);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testPrefixMatch_literalMutation20904_cf23786__68, 0);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_7561 = new ch.qos.logback.core.joran.spi.ElementPath();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_7558 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_7558.getPrefixMatchLength(vc_7561);
                // MethodAssertGenerator build local variable
                Object o_120_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            org.junit.Assert.fail("testPrefixMatch_literalMutation20904_cf23786_cf36179 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_literalMutation20829_cf21726_cf37066_failAssert85() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_2067459955 = new java.util.ArrayList<Object>();
	collection_2067459955.add("  ");
	org.junit.Assert.assertEquals(collection_2067459955, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1277814597 = new java.util.ArrayList<Object>();
	collection_1277814597.add("  ");
	org.junit.Assert.assertEquals(collection_1277814597, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1786178113 = new java.util.ArrayList<Object>();
	collection_1786178113.add("  ");
	org.junit.Assert.assertEquals(collection_1786178113, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_429972674 = new java.util.ArrayList<Object>();
	collection_429972674.add("  ");
	org.junit.Assert.assertEquals(collection_429972674, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_397596600 = new java.util.ArrayList<Object>();
	collection_397596600.add("  ");
	org.junit.Assert.assertEquals(collection_397596600, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1350532872 = new java.util.ArrayList<Object>();
	collection_1350532872.add("  ");
	org.junit.Assert.assertEquals(collection_1350532872, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
                // MethodAssertGenerator build local variable
                Object o_42_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
                // MethodAssertGenerator build local variable
                Object o_49_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_56_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
                // MethodAssertGenerator build local variable
                Object o_63_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
                // MethodAssertGenerator build local variable
                Object o_70_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
                // MethodAssertGenerator build local variable
                Object o_77_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
                // AssertGenerator replace invocation
                int o_testPrefixMatch_literalMutation20829_cf21726__66 = // StatementAdderMethod cloned existing statement
ruleElementSelector.hashCode();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testPrefixMatch_literalMutation20829_cf21726__66, 42);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementPath vc_7618 = (ch.qos.logback.core.joran.spi.ElementPath)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_7616 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_7616.fullPathMatch(vc_7618);
                // MethodAssertGenerator build local variable
                Object o_94_0 = ruleElementSelector.getPrefixMatchLength(p);
            }
            org.junit.Assert.fail("testPrefixMatch_literalMutation20829_cf21726_cf37066 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_literalMutation20829_literalMutation21614_cf34691() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1713718154 = new java.util.ArrayList<Object>();
	collection_1713718154.add("  ");
	org.junit.Assert.assertEquals(collection_1713718154, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1691037771 = new java.util.ArrayList<Object>();
	collection_1691037771.add("  ");
	org.junit.Assert.assertEquals(collection_1691037771, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_698471784 = new java.util.ArrayList<Object>();
	collection_698471784.add("  ");
	org.junit.Assert.assertEquals(collection_698471784, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_551165246 = new java.util.ArrayList<Object>();
	collection_551165246.add("  ");
	org.junit.Assert.assertEquals(collection_551165246, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1148743687 = new java.util.ArrayList<Object>();
	collection_1148743687.add("  ");
	org.junit.Assert.assertEquals(collection_1148743687, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1582586023 = new java.util.ArrayList<Object>();
	collection_1582586023.add("  ");
	org.junit.Assert.assertEquals(collection_1582586023, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_429972674 = new java.util.ArrayList<Object>();
	collection_429972674.add("  ");
	org.junit.Assert.assertEquals(collection_429972674, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_397596600 = new java.util.ArrayList<Object>();
	collection_397596600.add("  ");
	org.junit.Assert.assertEquals(collection_397596600, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1350532872 = new java.util.ArrayList<Object>();
	collection_1350532872.add("  ");
	org.junit.Assert.assertEquals(collection_1350532872, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_491311984 = new java.util.ArrayList<Object>();
	collection_491311984.add("  ");
	org.junit.Assert.assertEquals(collection_491311984, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1994638881 = new java.util.ArrayList<Object>();
	collection_1994638881.add("  ");
	org.junit.Assert.assertEquals(collection_1994638881, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_2021336928 = new java.util.ArrayList<Object>();
	collection_2021336928.add("  ");
	org.junit.Assert.assertEquals(collection_2021336928, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1692699539 = new java.util.ArrayList<Object>();
	collection_1692699539.add("  ");
	org.junit.Assert.assertEquals(collection_1692699539, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_910938276 = new java.util.ArrayList<Object>();
	collection_910938276.add("  ");
	org.junit.Assert.assertEquals(collection_910938276, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_2031906516 = new java.util.ArrayList<Object>();
	collection_2031906516.add("  ");
	org.junit.Assert.assertEquals(collection_2031906516, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_7362 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7362);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_7361 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1800839247 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1800839247, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_300560124 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_300560124, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1766758952 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1766758952, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7361).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_testPrefixMatch_literalMutation20829_literalMutation21614_cf34691__106 = // StatementAdderMethod cloned existing statement
vc_7361.getPrefixMatchLength(vc_7362);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_literalMutation20829_literalMutation21614_cf34691__106, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_cf20946_cf27222_cf30036() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // AssertGenerator replace invocation
            int o_testPrefixMatch_cf20946__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_cf20946__48, 0);
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20946_cf27222__52 = // StatementAdderMethod cloned existing statement
ruleElementSelector.fullPathMatch(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20946_cf27222__52);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_6867 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_cf20946_cf27222_cf30036__58 = // StatementAdderMethod cloned existing statement
ruleElementSelector.equals(vc_6867);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_cf20946_cf27222_cf30036__58);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_literalMutation20832_literalMutation22005_cf32984() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("6x/*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1514176025 = new java.util.ArrayList<Object>();
	collection_1514176025.add("6x");
	collection_1514176025.add("*");
	org.junit.Assert.assertEquals(collection_1514176025, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1354681794 = new java.util.ArrayList<Object>();
	collection_1354681794.add("6x");
	collection_1354681794.add("*");
	org.junit.Assert.assertEquals(collection_1354681794, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_404256938 = new java.util.ArrayList<Object>();
	collection_404256938.add("6x");
	collection_404256938.add("*");
	org.junit.Assert.assertEquals(collection_404256938, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_461262432 = new java.util.ArrayList<Object>();
	collection_461262432.add("6x");
	collection_461262432.add("*");
	org.junit.Assert.assertEquals(collection_461262432, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_2120698547 = new java.util.ArrayList<Object>();
	collection_2120698547.add("6x");
	collection_2120698547.add("*");
	org.junit.Assert.assertEquals(collection_2120698547, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1999284447 = new java.util.ArrayList<Object>();
	collection_1999284447.add("6x");
	collection_1999284447.add("*");
	org.junit.Assert.assertEquals(collection_1999284447, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_662286390 = new java.util.ArrayList<Object>();
	collection_662286390.add("6x");
	collection_662286390.add("*");
	org.junit.Assert.assertEquals(collection_662286390, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_152627534 = new java.util.ArrayList<Object>();
	collection_152627534.add("6x");
	collection_152627534.add("*");
	org.junit.Assert.assertEquals(collection_152627534, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1790613050 = new java.util.ArrayList<Object>();
	collection_1790613050.add("6x");
	collection_1790613050.add("*");
	org.junit.Assert.assertEquals(collection_1790613050, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("Lx/*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1440349613 = new java.util.ArrayList<Object>();
	collection_1440349613.add("Lx");
	collection_1440349613.add("*");
	org.junit.Assert.assertEquals(collection_1440349613, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1587364228 = new java.util.ArrayList<Object>();
	collection_1587364228.add("Lx");
	collection_1587364228.add("*");
	org.junit.Assert.assertEquals(collection_1587364228, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1936465557 = new java.util.ArrayList<Object>();
	collection_1936465557.add("Lx");
	collection_1936465557.add("*");
	org.junit.Assert.assertEquals(collection_1936465557, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1938549143 = new java.util.ArrayList<Object>();
	collection_1938549143.add("Lx");
	collection_1938549143.add("*");
	org.junit.Assert.assertEquals(collection_1938549143, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1890595932 = new java.util.ArrayList<Object>();
	collection_1890595932.add("Lx");
	collection_1890595932.add("*");
	org.junit.Assert.assertEquals(collection_1890595932, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_83980645 = new java.util.ArrayList<Object>();
	collection_83980645.add("Lx");
	collection_83980645.add("*");
	org.junit.Assert.assertEquals(collection_83980645, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_7190 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7190);
            // AssertGenerator replace invocation
            int o_testPrefixMatch_literalMutation20832_literalMutation22005_cf32984__104 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_7190);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testPrefixMatch_literalMutation20832_literalMutation22005_cf32984__104, 0);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    // test prefix matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testPrefixMatch */
    @org.junit.Test(timeout = 10000)
    public void testPrefixMatch_literalMutation20841_literalMutation22777_cf34802() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x/*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/x*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1678062421 = new java.util.ArrayList<Object>();
	collection_1678062421.add("x*");
	org.junit.Assert.assertEquals(collection_1678062421, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "x*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "x*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1741659655 = new java.util.ArrayList<Object>();
	collection_1741659655.add("x*");
	org.junit.Assert.assertEquals(collection_1741659655, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1020033566 = new java.util.ArrayList<Object>();
	collection_1020033566.add("x*");
	org.junit.Assert.assertEquals(collection_1020033566, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "x*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1619959212 = new java.util.ArrayList<Object>();
	collection_1619959212.add("x*");
	org.junit.Assert.assertEquals(collection_1619959212, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "x*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "x*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_996234541 = new java.util.ArrayList<Object>();
	collection_996234541.add("x*");
	org.junit.Assert.assertEquals(collection_996234541, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1602341550 = new java.util.ArrayList<Object>();
	collection_1602341550.add("x*");
	org.junit.Assert.assertEquals(collection_1602341550, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "x*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_891968418 = new java.util.ArrayList<Object>();
	collection_891968418.add("x*");
	org.junit.Assert.assertEquals(collection_891968418, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "x*");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "x*");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1845651943 = new java.util.ArrayList<Object>();
	collection_1845651943.add("x*");
	org.junit.Assert.assertEquals(collection_1845651943, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1020404751 = new java.util.ArrayList<Object>();
	collection_1020404751.add("x*");
	org.junit.Assert.assertEquals(collection_1020404751, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "x*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/A/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/*");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/a/b/*");
            org.junit.Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("/*");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_7377 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1467964120 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1467964120, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_718296791 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_718296791, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate().equals(vc_7377));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_359415133 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_359415133, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).duplicate()).duplicate().equals(vc_7377));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7377).duplicate()).duplicate().equals(vc_7377));
            // AssertGenerator replace invocation
            boolean o_testPrefixMatch_literalMutation20841_literalMutation22777_cf34802__87 = // StatementAdderMethod cloned existing statement
ruleElementSelector.fullPathMatch(vc_7377);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testPrefixMatch_literalMutation20841_literalMutation22777_cf34802__87);
            org.junit.Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38724() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7803 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        java.util.ArrayList collection_518839708 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_518839708, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        java.util.ArrayList collection_112560593 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_112560593, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_210069341 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_210069341, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_testSuffix_cf38724__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_7803);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38724__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38723() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7802 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7802);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38723__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_7802);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38723__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38734() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // AssertGenerator replace invocation
        int o_testSuffix_cf38734__7 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38734__7, 97);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38712() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7795 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_490113935 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_490113935, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_527637571 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_527637571, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1674720516 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1674720516, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).duplicate().equals(vc_7795));
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38712__9 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_7795);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38712__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38730() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7807 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_378350552 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_378350552, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1979289328 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1979289328, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate().equals(vc_7807));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).duplicate()).duplicate().equals(vc_7807));
        // AssertGenerator add assertion
        java.util.ArrayList collection_534945931 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_534945931, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).duplicate()).duplicate().equals(vc_7807));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7807).size(), 0);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38730__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_7807);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38730__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38708() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_7791 = new java.lang.Object();
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_7789 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1127238417 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1127238417, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1946064650 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1946064650, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1711151754 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1711151754, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).peekLast());
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38708__11 = // StatementAdderMethod cloned existing statement
vc_7789.equals(vc_7791);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38708__11);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38706() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_7791 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38706__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_7791);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38706__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38717() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7798 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7798);
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38717__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38717__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38729() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7806 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38729__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38729__9, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38718() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7799 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).duplicate()).duplicate().equals(vc_7799));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate().equals(vc_7799));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1650435476 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1650435476, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_739073632 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_739073632, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).duplicate().equals(vc_7799));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_210575632 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_210575632, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7799).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38718__9 = // StatementAdderMethod cloned existing statement
p.isContainedIn(vc_7799);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38718__9);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38719_cf39315() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7798 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7798);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_7797 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1289909245 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1289909245, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1907616788 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1907616788, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1730915148 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1730915148, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_228648510 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_228648510, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1835422030 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1835422030, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1158218562 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1158218562, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38719__11 = // StatementAdderMethod cloned existing statement
vc_7797.isContainedIn(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38719__11);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38719_cf39315__35 = // StatementAdderMethod cloned existing statement
vc_7797.getPrefixMatchLength(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38719_cf39315__35, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38719_cf39324() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7798 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7798);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_7797 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1978745258 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1978745258, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_875146547 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_875146547, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_286971407 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_286971407, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_228648510 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_228648510, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1835422030 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1835422030, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1158218562 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1158218562, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7797).duplicate()).getCopyOfPartList());;
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38719__11 = // StatementAdderMethod cloned existing statement
vc_7797.isContainedIn(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38719__11);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38719_cf39324__35 = // StatementAdderMethod cloned existing statement
vc_7797.getTailMatchLength(vc_7798);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38719_cf39324__35, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38712_cf39144() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7795 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1393254598 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1393254598, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_568903273 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_568903273, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_769429179 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_769429179, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_490113935 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_490113935, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_527637571 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_527637571, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate().equals(vc_7795));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1674720516 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1674720516, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7795).duplicate()).duplicate()).duplicate().equals(vc_7795));
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38712__9 = // StatementAdderMethod cloned existing statement
p.fullPathMatch(vc_7795);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38712__9);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38712_cf39144__37 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_7795);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38712_cf39144__37, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38724_cf39438() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementPath vc_7803 = new ch.qos.logback.core.joran.spi.ElementPath();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        java.util.ArrayList collection_990266933 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_990266933, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        java.util.ArrayList collection_1272935121 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1272935121, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1240504297 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1240504297, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        java.util.ArrayList collection_518839708 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_518839708, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).duplicate().equals(vc_7803));
        // AssertGenerator add assertion
        java.util.ArrayList collection_112560593 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_112560593, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_210069341 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_210069341, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).getCopyOfPartList());;
        // AssertGenerator replace invocation
        int o_testSuffix_cf38724__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_7803);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38724__9, 0);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38724_cf39438__37 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_7803);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38724_cf39438__37, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38722_failAssert18_literalMutation39337() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("s");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "s");
            // AssertGenerator add assertion
            java.util.ArrayList collection_459400854 = new java.util.ArrayList<Object>();
	collection_459400854.add("s");
	org.junit.Assert.assertEquals(collection_459400854, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "s");
            // AssertGenerator add assertion
            java.util.ArrayList collection_706269666 = new java.util.ArrayList<Object>();
	collection_706269666.add("s");
	org.junit.Assert.assertEquals(collection_706269666, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "s");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1443644562 = new java.util.ArrayList<Object>();
	collection_1443644562.add("s");
	org.junit.Assert.assertEquals(collection_1443644562, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 1);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "s");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_7803 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate().equals(vc_7803));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate().equals(vc_7803));
            // AssertGenerator add assertion
            java.util.ArrayList collection_719434767 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_719434767, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).duplicate().equals(vc_7803));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1052938992 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1052938992, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1452233863 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1452233863, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7803).getCopyOfPartList());;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_7800 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7800);
            // StatementAdderMethod cloned existing statement
            vc_7800.getPrefixMatchLength(vc_7803);
            // MethodAssertGenerator build local variable
            Object o_13_0 = p.get(0);
            org.junit.Assert.fail("testSuffix_cf38722 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38707_cf38973_failAssert56() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_7790 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7790);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_7789 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_2129878396 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2129878396, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1633217163 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1633217163, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1622848461 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1622848461, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7789).peekLast());
            // AssertGenerator replace invocation
            boolean o_testSuffix_cf38707__11 = // StatementAdderMethod cloned existing statement
vc_7789.equals(vc_7790);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSuffix_cf38707__11);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_7868 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_7866 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // StatementAdderMethod cloned existing statement
            vc_7866.getPrefixMatchLength(vc_7868);
            // MethodAssertGenerator build local variable
            Object o_41_0 = p.get(0);
            org.junit.Assert.fail("testSuffix_cf38707_cf38973 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38731_cf39633_failAssert53_literalMutation40830() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1106912749 = new java.util.ArrayList<Object>();
	collection_1106912749.add("  ");
	org.junit.Assert.assertEquals(collection_1106912749, ((ch.qos.logback.core.joran.spi.ElementSelector)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1889225941 = new java.util.ArrayList<Object>();
	collection_1889225941.add("  ");
	org.junit.Assert.assertEquals(collection_1889225941, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).size(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)p).peekLast(), "  ");
            // AssertGenerator add assertion
            java.util.ArrayList collection_548474613 = new java.util.ArrayList<Object>();
	collection_548474613.add("  ");
	org.junit.Assert.assertEquals(collection_548474613, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)p).duplicate()).duplicate()).getCopyOfPartList());;
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 1);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "  ");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_7806 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7806);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7806);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_7805 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1217213483 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1217213483, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_120355626 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_120355626, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_15410183 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_15410183, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_690947696 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_690947696, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1271501801 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1271501801, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_467919042 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_467919042, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_testSuffix_cf38731__11 = // StatementAdderMethod cloned existing statement
vc_7805.getTailMatchLength(vc_7806);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSuffix_cf38731__11, 0);
            // StatementAdderMethod cloned existing statement
            vc_7805.fullPathMatch(vc_7806);
            // MethodAssertGenerator build local variable
            Object o_37_0 = p.get(0);
            org.junit.Assert.fail("testSuffix_cf38731_cf39633 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38729_cf39519_cf40160() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7806 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38729__9 = // StatementAdderMethod cloned existing statement
p.getTailMatchLength(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38729__9, 0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_8054 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_8054);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_8054);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_8053 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_18112482 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_18112482, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1753622680 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1753622680, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_787264372 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_787264372, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2060871798 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2060871798, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2039761884 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2039761884, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1254110216 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1254110216, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8053).duplicate()).peekLast());
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38729_cf39519__19 = // StatementAdderMethod cloned existing statement
vc_8053.equals(vc_8054);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38729_cf39519__19);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38729_cf39519_cf40160__45 = // StatementAdderMethod cloned existing statement
p.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38729_cf39519_cf40160__45, 97);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38731_cf39642_cf40931() {
        ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        org.junit.Assert.assertEquals(1, p.size());
        org.junit.Assert.assertEquals("a", p.peekLast());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_7806 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7806);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ElementSelector vc_7805 = new ch.qos.logback.core.joran.spi.ElementSelector();
        // AssertGenerator add assertion
        java.util.ArrayList collection_568909528 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_568909528, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1211133353 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1211133353, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_361048127 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_361048127, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1225418101 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1225418101, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_501547713 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_501547713, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1687239346 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1687239346, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_690947696 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_690947696, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1271501801 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1271501801, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_467919042 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_467919042, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38731__11 = // StatementAdderMethod cloned existing statement
vc_7805.getTailMatchLength(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38731__11, 0);
        // AssertGenerator replace invocation
        boolean o_testSuffix_cf38731_cf39642__35 = // StatementAdderMethod cloned existing statement
vc_7805.isContainedIn(vc_7806);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSuffix_cf38731_cf39642__35);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.ElementPath vc_8506 = (ch.qos.logback.core.joran.spi.ElementPath)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_8506);
        // AssertGenerator replace invocation
        int o_testSuffix_cf38731_cf39642_cf40931__61 = // StatementAdderMethod cloned existing statement
vc_7805.getPrefixMatchLength(vc_8506);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSuffix_cf38731_cf39642_cf40931__61, 0);
        org.junit.Assert.assertEquals("a", p.get(0));
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38731_cf39642_cf40917_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_7806 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7806);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7806);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_7805 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1225418101 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1225418101, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_501547713 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_501547713, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1687239346 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1687239346, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_690947696 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_690947696, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1271501801 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1271501801, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_467919042 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_467919042, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7805).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_testSuffix_cf38731__11 = // StatementAdderMethod cloned existing statement
vc_7805.getTailMatchLength(vc_7806);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSuffix_cf38731__11, 0);
            // AssertGenerator replace invocation
            boolean o_testSuffix_cf38731_cf39642__35 = // StatementAdderMethod cloned existing statement
vc_7805.isContainedIn(vc_7806);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSuffix_cf38731_cf39642__35);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8497 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // StatementAdderMethod cloned existing statement
            vc_8497.fullPathMatch(vc_7806);
            // MethodAssertGenerator build local variable
            Object o_63_0 = p.get(0);
            org.junit.Assert.fail("testSuffix_cf38731_cf39642_cf40917 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38723_cf39376_cf40256_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_7802 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7802);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7802);
            // AssertGenerator replace invocation
            int o_testSuffix_cf38723__9 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_7802);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSuffix_cf38723__9, 0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_8001 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).duplicate().equals(vc_8001));
            // AssertGenerator add assertion
            java.util.ArrayList collection_340541721 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_340541721, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_20102041 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_20102041, ((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).duplicate()).duplicate().equals(vc_8001));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate().equals(vc_8001));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_2138318701 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2138318701, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_8001).peekLast());
            // AssertGenerator replace invocation
            int o_testSuffix_cf38723_cf39376__17 = // StatementAdderMethod cloned existing statement
p.getPrefixMatchLength(vc_8001);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSuffix_cf38723_cf39376__17, 0);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_8297 = new java.lang.Object();
            // StatementAdderMethod cloned existing statement
            vc_7802.equals(vc_8297);
            // MethodAssertGenerator build local variable
            Object o_51_0 = p.get(0);
            org.junit.Assert.fail("testSuffix_cf38723_cf39376_cf40256 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf38706_cf38887_failAssert51_add40823() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.spi.ElementSelector p = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = p.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 1);
            // MethodAssertGenerator build local variable
            Object o_5_0 = p.peekLast();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "a");
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_7791 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testSuffix_cf38706__9 = // StatementAdderMethod cloned existing statement
p.equals(vc_7791);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSuffix_cf38706__9);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_7847 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1025070979 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1025070979, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).duplicate()).duplicate().equals(vc_7847));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate().equals(vc_7847));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1804139527 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1804139527, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_179609897 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_179609897, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).duplicate().equals(vc_7847));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7847).duplicate()).duplicate()).peekLast());
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementSelector vc_7844 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7844);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_7844.getPrefixMatchLength(vc_7847);
            // StatementAdderMethod cloned existing statement
            vc_7844.getPrefixMatchLength(vc_7847);
            // MethodAssertGenerator build local variable
            Object o_19_0 = p.get(0);
            org.junit.Assert.fail("testSuffix_cf38706_cf38887 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41683() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_8744 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8744);
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41683__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(vc_8744);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_cf41683__50);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41695() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_8748 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8748);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8747 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1094340665 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1094340665, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1962324635 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1962324635, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_296282844 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_296282844, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41695__52 = // StatementAdderMethod cloned existing statement
vc_8747.getPrefixMatchLength(vc_8748);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41695__52, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41684() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41684__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_cf41684__48);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41708() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41708__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41708__48, 43);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41703() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_8753 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1923393652 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1923393652, ((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1581721319 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1581721319, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate().equals(vc_8753));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).duplicate()).duplicate().equals(vc_8753));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1640938568 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1640938568, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).duplicate().equals(vc_8753));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8753).duplicate()).peekLast());
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41703__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_8753);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41703__50, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41668() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_8737 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41668__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.equals(vc_8737);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_cf41668__50);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41701() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_8752 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8752);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41701__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_8752);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41701__50, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41687() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8743 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1052573896 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1052573896, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1715691360 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1715691360, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_85612961 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_85612961, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41687__50 = // StatementAdderMethod cloned existing statement
vc_8743.isContainedIn(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testTailMatch_cf41687__50);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41696() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8747 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1389399509 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1389399509, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_903568232 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_903568232, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_378821526 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_378821526, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8747).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41696__50 = // StatementAdderMethod cloned existing statement
vc_8747.getPrefixMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41696__50, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41701_cf47277() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_8752 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8752);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8752);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41701__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_8752);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41701__50, 0);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_9550 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9550);
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41701_cf47277__58 = // StatementAdderMethod cloned existing statement
p.equals(vc_9550);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_cf41701_cf47277__58);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_literalMutation41589_cf42202() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/7/b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_37406945 = new java.util.ArrayList<Object>();
	collection_37406945.add("7");
	collection_37406945.add("b");
	org.junit.Assert.assertEquals(collection_37406945, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_333634776 = new java.util.ArrayList<Object>();
	collection_333634776.add("7");
	collection_333634776.add("b");
	org.junit.Assert.assertEquals(collection_333634776, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1529580125 = new java.util.ArrayList<Object>();
	collection_1529580125.add("7");
	collection_1529580125.add("b");
	org.junit.Assert.assertEquals(collection_1529580125, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1047729647 = new java.util.ArrayList<Object>();
	collection_1047729647.add("7");
	collection_1047729647.add("b");
	org.junit.Assert.assertEquals(collection_1047729647, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1590550799 = new java.util.ArrayList<Object>();
	collection_1590550799.add("7");
	collection_1590550799.add("b");
	org.junit.Assert.assertEquals(collection_1590550799, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_592766846 = new java.util.ArrayList<Object>();
	collection_592766846.add("7");
	collection_592766846.add("b");
	org.junit.Assert.assertEquals(collection_592766846, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8843 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1647273494 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1647273494, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1809548993 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1809548993, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_801876676 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_801876676, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8843).duplicate()).peekLast());
            // AssertGenerator replace invocation
            int o_testTailMatch_literalMutation41589_cf42202__74 = // StatementAdderMethod cloned existing statement
vc_8843.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_literalMutation41589_cf42202__74, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41691_failAssert64_literalMutation46719() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("3");
                // AssertGenerator add assertion
                java.util.ArrayList collection_2024805264 = new java.util.ArrayList<Object>();
	collection_2024805264.add("3");
	org.junit.Assert.assertEquals(collection_2024805264, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "3");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "3");
                // AssertGenerator add assertion
                java.util.ArrayList collection_553610113 = new java.util.ArrayList<Object>();
	collection_553610113.add("3");
	org.junit.Assert.assertEquals(collection_553610113, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1864328286 = new java.util.ArrayList<Object>();
	collection_1864328286.add("3");
	org.junit.Assert.assertEquals(collection_1864328286, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "3");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_6_0, 0);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_13_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_20_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_27_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_34_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_41_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_8749 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_208432205 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_208432205, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_376074518 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_376074518, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate().equals(vc_8749));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).duplicate().equals(vc_8749));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_1953294092 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1953294092, ((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).duplicate()).duplicate().equals(vc_8749));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_8749).size(), 0);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_8746 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8746);
                // StatementAdderMethod cloned existing statement
                vc_8746.getPrefixMatchLength(vc_8749);
                // MethodAssertGenerator build local variable
                Object o_54_0 = ruleElementSelector.getTailMatchLength(p);
            }
            org.junit.Assert.fail("testTailMatch_cf41691 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_literalMutation41589_cf42189() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/7/b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1470751794 = new java.util.ArrayList<Object>();
	collection_1470751794.add("7");
	collection_1470751794.add("b");
	org.junit.Assert.assertEquals(collection_1470751794, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_716948436 = new java.util.ArrayList<Object>();
	collection_716948436.add("7");
	collection_716948436.add("b");
	org.junit.Assert.assertEquals(collection_716948436, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1765921031 = new java.util.ArrayList<Object>();
	collection_1765921031.add("7");
	collection_1765921031.add("b");
	org.junit.Assert.assertEquals(collection_1765921031, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1047729647 = new java.util.ArrayList<Object>();
	collection_1047729647.add("7");
	collection_1047729647.add("b");
	org.junit.Assert.assertEquals(collection_1047729647, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1590550799 = new java.util.ArrayList<Object>();
	collection_1590550799.add("7");
	collection_1590550799.add("b");
	org.junit.Assert.assertEquals(collection_1590550799, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_592766846 = new java.util.ArrayList<Object>();
	collection_592766846.add("7");
	collection_592766846.add("b");
	org.junit.Assert.assertEquals(collection_592766846, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8835 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_2064335090 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2064335090, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1155967418 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1155967418, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_182375483 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_182375483, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8835).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_testTailMatch_literalMutation41589_cf42189__74 = // StatementAdderMethod cloned existing statement
vc_8835.getPrefixMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_literalMutation41589_cf42189__74, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41701_cf47306() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_8752 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8752);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8752);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41701__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_8752);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41701__50, 0);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_9561 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_826563607 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_826563607, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_663020954 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_663020954, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_421109541 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_421109541, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9561).size(), 0);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41701_cf47306__58 = // StatementAdderMethod cloned existing statement
vc_9561.getPrefixMatchLength(vc_8752);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41701_cf47306__58, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_literalMutation41634_cf43667() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("z/b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1064077917 = new java.util.ArrayList<Object>();
	collection_1064077917.add("z");
	collection_1064077917.add("b");
	org.junit.Assert.assertEquals(collection_1064077917, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_675321994 = new java.util.ArrayList<Object>();
	collection_675321994.add("z");
	collection_675321994.add("b");
	org.junit.Assert.assertEquals(collection_675321994, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_559172405 = new java.util.ArrayList<Object>();
	collection_559172405.add("z");
	collection_559172405.add("b");
	org.junit.Assert.assertEquals(collection_559172405, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_406459422 = new java.util.ArrayList<Object>();
	collection_406459422.add("z");
	collection_406459422.add("b");
	org.junit.Assert.assertEquals(collection_406459422, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_271051193 = new java.util.ArrayList<Object>();
	collection_271051193.add("z");
	collection_271051193.add("b");
	org.junit.Assert.assertEquals(collection_271051193, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_473328862 = new java.util.ArrayList<Object>();
	collection_473328862.add("z");
	collection_473328862.add("b");
	org.junit.Assert.assertEquals(collection_473328862, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_9096 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9096);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_9095 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1584331571 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1584331571, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_861462977 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_861462977, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1372746430 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1372746430, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9095).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            boolean o_testTailMatch_literalMutation41634_cf43667__70 = // StatementAdderMethod cloned existing statement
vc_9095.isContainedIn(vc_9096);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_literalMutation41634_cf43667__70);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41685_cf46327_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_34_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_8745 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).duplicate().equals(vc_8745));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).duplicate()).duplicate().equals(vc_8745));
                // AssertGenerator add assertion
                java.util.ArrayList collection_1165817631 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1165817631, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate().equals(vc_8745));
                // AssertGenerator add assertion
                java.util.ArrayList collection_730110862 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_730110862, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1977876665 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1977876665, ((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8745).duplicate()).peekLast());
                // AssertGenerator replace invocation
                boolean o_testTailMatch_cf41685__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(vc_8745);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_testTailMatch_cf41685__50);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_9453 = new ch.qos.logback.core.joran.spi.ElementPath();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_9450 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_9450.getPrefixMatchLength(vc_9453);
                // MethodAssertGenerator build local variable
                Object o_84_0 = ruleElementSelector.getTailMatchLength(p);
            }
            org.junit.Assert.fail("testTailMatch_cf41685_cf46327 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41667_cf44957_cf49156() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_8736 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8736);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8736);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8736);
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41667__50 = // StatementAdderMethod cloned existing statement
ruleElementSelector.equals(vc_8736);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_cf41667__50);
            // StatementAdderMethod cloned existing statement
            p.hashCode();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_9848 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9848);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_9847 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_509038683 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_509038683, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1030833985 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1030833985, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1713233110 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1713233110, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9847).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41667_cf44957_cf49156__64 = // StatementAdderMethod cloned existing statement
vc_9847.getPrefixMatchLength(vc_9848);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41667_cf44957_cf49156__64, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41709_literalMutation48008_literalMutation56549() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*3/a");
            // AssertGenerator add assertion
            java.util.ArrayList collection_198975493 = new java.util.ArrayList<Object>();
	collection_198975493.add("*3");
	collection_198975493.add("a");
	org.junit.Assert.assertEquals(collection_198975493, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "a");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "a");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1580812429 = new java.util.ArrayList<Object>();
	collection_1580812429.add("*3");
	collection_1580812429.add("a");
	org.junit.Assert.assertEquals(collection_1580812429, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_605616515 = new java.util.ArrayList<Object>();
	collection_605616515.add("*3");
	collection_605616515.add("a");
	org.junit.Assert.assertEquals(collection_605616515, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8755 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1633582287 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1633582287, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_1038489439 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1038489439, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1768403407 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1768403407, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_71333170 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_71333170, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_989713272 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_989713272, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1576901017 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1576901017, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_328072009 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_328072009, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).getCopyOfPartList());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_798247462 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_798247462, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1948079267 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1948079267, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8755).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41709__50 = // StatementAdderMethod cloned existing statement
vc_8755.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41709__50, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_literalMutation41634_cf43683_cf58532() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("z/b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_341208621 = new java.util.ArrayList<Object>();
	collection_341208621.add("z");
	collection_341208621.add("b");
	org.junit.Assert.assertEquals(collection_341208621, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_237367785 = new java.util.ArrayList<Object>();
	collection_237367785.add("z");
	collection_237367785.add("b");
	org.junit.Assert.assertEquals(collection_237367785, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1802321178 = new java.util.ArrayList<Object>();
	collection_1802321178.add("z");
	collection_1802321178.add("b");
	org.junit.Assert.assertEquals(collection_1802321178, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_944440826 = new java.util.ArrayList<Object>();
	collection_944440826.add("z");
	collection_944440826.add("b");
	org.junit.Assert.assertEquals(collection_944440826, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_979487241 = new java.util.ArrayList<Object>();
	collection_979487241.add("z");
	collection_979487241.add("b");
	org.junit.Assert.assertEquals(collection_979487241, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_284759646 = new java.util.ArrayList<Object>();
	collection_284759646.add("z");
	collection_284759646.add("b");
	org.junit.Assert.assertEquals(collection_284759646, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_406459422 = new java.util.ArrayList<Object>();
	collection_406459422.add("z");
	collection_406459422.add("b");
	org.junit.Assert.assertEquals(collection_406459422, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_271051193 = new java.util.ArrayList<Object>();
	collection_271051193.add("z");
	collection_271051193.add("b");
	org.junit.Assert.assertEquals(collection_271051193, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_473328862 = new java.util.ArrayList<Object>();
	collection_473328862.add("z");
	collection_473328862.add("b");
	org.junit.Assert.assertEquals(collection_473328862, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // AssertGenerator replace invocation
            int o_testTailMatch_literalMutation41634_cf43683__66 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_literalMutation41634_cf43683__66, 2);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_10993 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_669058835 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_669058835, ((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).duplicate().equals(vc_10993));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_480780724 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_480780724, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_806225965 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_806225965, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate()).duplicate()).duplicate().equals(vc_10993));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_10993).duplicate().equals(vc_10993));
            // AssertGenerator replace invocation
            int o_testTailMatch_literalMutation41634_cf43683_cf58532__90 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getPrefixMatchLength(vc_10993);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_literalMutation41634_cf43683_cf58532__90, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_literalMutation41600_cf42538_failAssert32_literalMutation52381() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_6_0, 0);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*I/a");
                // AssertGenerator add assertion
                java.util.ArrayList collection_961025459 = new java.util.ArrayList<Object>();
	collection_961025459.add("*I");
	collection_961025459.add("a");
	org.junit.Assert.assertEquals(collection_961025459, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "a");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "a");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1244768669 = new java.util.ArrayList<Object>();
	collection_1244768669.add("*I");
	collection_1244768669.add("a");
	org.junit.Assert.assertEquals(collection_1244768669, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1083261734 = new java.util.ArrayList<Object>();
	collection_1083261734.add("*I");
	collection_1083261734.add("a");
	org.junit.Assert.assertEquals(collection_1083261734, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "a");
                // AssertGenerator add assertion
                java.util.ArrayList collection_666789823 = new java.util.ArrayList<Object>();
	collection_666789823.add("*I");
	collection_666789823.add("a");
	org.junit.Assert.assertEquals(collection_666789823, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "a");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "a");
                // AssertGenerator add assertion
                java.util.ArrayList collection_68736730 = new java.util.ArrayList<Object>();
	collection_68736730.add("*I");
	collection_68736730.add("a");
	org.junit.Assert.assertEquals(collection_68736730, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1934995781 = new java.util.ArrayList<Object>();
	collection_1934995781.add("*I");
	collection_1934995781.add("a");
	org.junit.Assert.assertEquals(collection_1934995781, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "a");
                // MethodAssertGenerator build local variable
                Object o_31_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_31_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1293521315 = new java.util.ArrayList<Object>();
	collection_1293521315.add("  ");
	org.junit.Assert.assertEquals(collection_1293521315, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "  ");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1598455906 = new java.util.ArrayList<Object>();
	collection_1598455906.add("  ");
	org.junit.Assert.assertEquals(collection_1598455906, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 1);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1089332429 = new java.util.ArrayList<Object>();
	collection_1089332429.add("  ");
	org.junit.Assert.assertEquals(collection_1089332429, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "  ");
                // MethodAssertGenerator build local variable
                Object o_38_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_38_0, 0);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
                // MethodAssertGenerator build local variable
                Object o_45_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_45_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_52_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_52_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_59_0 = ruleElementSelector.getTailMatchLength(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_59_0, 1);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_8895 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).duplicate()).duplicate().equals(vc_8895));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1859284579 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1859284579, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_274655426 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_274655426, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).duplicate().equals(vc_8895));
                // AssertGenerator add assertion
                java.util.ArrayList collection_1417034781 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1417034781, ((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_8895).duplicate().equals(vc_8895));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_8892 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8892);
                // StatementAdderMethod cloned existing statement
                vc_8892.fullPathMatch(vc_8895);
                // MethodAssertGenerator build local variable
                Object o_72_0 = ruleElementSelector.getTailMatchLength(p);
            }
            org.junit.Assert.fail("testTailMatch_literalMutation41600_cf42538 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41686_literalMutation46392_literalMutation51439() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("r/b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1074053532 = new java.util.ArrayList<Object>();
	collection_1074053532.add("r");
	collection_1074053532.add("b");
	org.junit.Assert.assertEquals(collection_1074053532, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_355622396 = new java.util.ArrayList<Object>();
	collection_355622396.add("r");
	collection_355622396.add("b");
	org.junit.Assert.assertEquals(collection_355622396, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1995164544 = new java.util.ArrayList<Object>();
	collection_1995164544.add("r");
	collection_1995164544.add("b");
	org.junit.Assert.assertEquals(collection_1995164544, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1639989652 = new java.util.ArrayList<Object>();
	collection_1639989652.add("r");
	collection_1639989652.add("b");
	org.junit.Assert.assertEquals(collection_1639989652, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "b");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1719851364 = new java.util.ArrayList<Object>();
	collection_1719851364.add("r");
	collection_1719851364.add("b");
	org.junit.Assert.assertEquals(collection_1719851364, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1849225442 = new java.util.ArrayList<Object>();
	collection_1849225442.add("r");
	collection_1849225442.add("b");
	org.junit.Assert.assertEquals(collection_1849225442, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("6a/B");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_241870661 = new java.util.ArrayList<Object>();
	collection_241870661.add("6a");
	collection_241870661.add("B");
	org.junit.Assert.assertEquals(collection_241870661, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "B");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "B");
            // AssertGenerator add assertion
            java.util.ArrayList collection_349190928 = new java.util.ArrayList<Object>();
	collection_349190928.add("6a");
	collection_349190928.add("B");
	org.junit.Assert.assertEquals(collection_349190928, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
            // AssertGenerator add assertion
            java.util.ArrayList collection_714948413 = new java.util.ArrayList<Object>();
	collection_714948413.add("6a");
	collection_714948413.add("B");
	org.junit.Assert.assertEquals(collection_714948413, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.ElementPath vc_8744 = (ch.qos.logback.core.joran.spi.ElementPath)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8744);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8744);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8744);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementSelector vc_8743 = new ch.qos.logback.core.joran.spi.ElementSelector();
            // AssertGenerator add assertion
            java.util.ArrayList collection_356118322 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_356118322, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_882658549 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_882658549, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1512521980 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1512521980, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_171765717 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_171765717, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_832511052 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_832511052, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1568794342 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1568794342, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_815542721 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_815542721, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1544642568 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1544642568, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_2075724602 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2075724602, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).size(), 0);
            // AssertGenerator replace invocation
            boolean o_testTailMatch_cf41686__52 = // StatementAdderMethod cloned existing statement
vc_8743.isContainedIn(vc_8744);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testTailMatch_cf41686__52);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41687_literalMutation46492_cf50469_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("O/a");
                // AssertGenerator add assertion
                java.util.ArrayList collection_376933567 = new java.util.ArrayList<Object>();
	collection_376933567.add("O");
	collection_376933567.add("a");
	org.junit.Assert.assertEquals(collection_376933567, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).peekLast(), "a");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).peekLast(), "a");
                // AssertGenerator add assertion
                java.util.ArrayList collection_2109608851 = new java.util.ArrayList<Object>();
	collection_2109608851.add("O");
	collection_2109608851.add("a");
	org.junit.Assert.assertEquals(collection_2109608851, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).size(), 2);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1596337549 = new java.util.ArrayList<Object>();
	collection_1596337549.add("O");
	collection_1596337549.add("a");
	org.junit.Assert.assertEquals(collection_1596337549, ((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).duplicate()).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)ruleElementSelector).peekLast(), "a");
                // MethodAssertGenerator build local variable
                Object o_38_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
                // MethodAssertGenerator build local variable
                Object o_45_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_52_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_59_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b/c");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_8743 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                java.util.ArrayList collection_1825934307 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1825934307, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_1081339497 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1081339497, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_2033439713 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2033439713, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1052573896 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1052573896, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_1715691360 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1715691360, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_85612961 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_85612961, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8743).duplicate()).duplicate()).size(), 0);
                // AssertGenerator replace invocation
                boolean o_testTailMatch_cf41687__50 = // StatementAdderMethod cloned existing statement
vc_8743.isContainedIn(p);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testTailMatch_cf41687__50);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementPath vc_10002 = (ch.qos.logback.core.joran.spi.ElementPath)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_10000 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_10000.getPrefixMatchLength(vc_10002);
                // MethodAssertGenerator build local variable
                Object o_114_0 = ruleElementSelector.getTailMatchLength(p);
            }
            org.junit.Assert.fail("testTailMatch_cf41687_literalMutation46492_cf50469 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_cf41702_literalMutation47390_cf57620() {
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
            org.junit.Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
            org.junit.Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/]/b/c");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 3);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1821976947 = new java.util.ArrayList<Object>();
	collection_1821976947.add("]");
	collection_1821976947.add("b");
	collection_1821976947.add("c");
	org.junit.Assert.assertEquals(collection_1821976947, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "c");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 3);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "c");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1177926206 = new java.util.ArrayList<Object>();
	collection_1177926206.add("]");
	collection_1177926206.add("b");
	collection_1177926206.add("c");
	org.junit.Assert.assertEquals(collection_1177926206, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 3);
            // AssertGenerator add assertion
            java.util.ArrayList collection_2055384179 = new java.util.ArrayList<Object>();
	collection_2055384179.add("]");
	collection_2055384179.add("b");
	collection_2055384179.add("c");
	org.junit.Assert.assertEquals(collection_2055384179, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "c");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 3);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1932286258 = new java.util.ArrayList<Object>();
	collection_1932286258.add("]");
	collection_1932286258.add("b");
	collection_1932286258.add("c");
	org.junit.Assert.assertEquals(collection_1932286258, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "c");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 3);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "c");
            // AssertGenerator add assertion
            java.util.ArrayList collection_610242066 = new java.util.ArrayList<Object>();
	collection_610242066.add("]");
	collection_610242066.add("b");
	collection_610242066.add("c");
	org.junit.Assert.assertEquals(collection_610242066, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 3);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1612850120 = new java.util.ArrayList<Object>();
	collection_1612850120.add("]");
	collection_1612850120.add("b");
	collection_1612850120.add("c");
	org.junit.Assert.assertEquals(collection_1612850120, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "c");
            ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41702__48 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(p);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41702__48, 2);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.ElementPath vc_10887 = new ch.qos.logback.core.joran.spi.ElementPath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).size(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_110572523 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_110572523, ((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).duplicate().equals(vc_10887));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).size(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).duplicate()).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate().equals(vc_10887));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).duplicate()).duplicate().equals(vc_10887));
            // AssertGenerator add assertion
            java.util.ArrayList collection_299446463 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_299446463, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).duplicate()).getCopyOfPartList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).peekLast());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).peekLast());
            // AssertGenerator add assertion
            java.util.ArrayList collection_2068711400 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2068711400, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_10887).duplicate()).getCopyOfPartList());;
            // AssertGenerator replace invocation
            int o_testTailMatch_cf41702_literalMutation47390_cf57620__78 = // StatementAdderMethod cloned existing statement
ruleElementSelector.getTailMatchLength(vc_10887);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testTailMatch_cf41702_literalMutation47390_cf57620__78, 0);
            org.junit.Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test tail matching
    /* amplification of ch.qos.logback.core.joran.spi.ElementSelectorTest#testTailMatch */
    @org.junit.Test(timeout = 10000)
    public void testTailMatch_literalMutation41655_cf44408_cf54565_failAssert86() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*");
                // MethodAssertGenerator build local variable
                Object o_6_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/A");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/a");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/A");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/b");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_34_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("/a/B");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ruleElementSelector.getTailMatchLength(p);
            }
            {
                ch.qos.logback.core.joran.spi.ElementPath p = new ch.qos.logback.core.joran.spi.ElementPath("//b/c");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
                // AssertGenerator add assertion
                java.util.ArrayList collection_1892948559 = new java.util.ArrayList<Object>();
	collection_1892948559.add("b");
	collection_1892948559.add("c");
	org.junit.Assert.assertEquals(collection_1892948559, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "c");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "c");
                // AssertGenerator add assertion
                java.util.ArrayList collection_1072863812 = new java.util.ArrayList<Object>();
	collection_1072863812.add("b");
	collection_1072863812.add("c");
	org.junit.Assert.assertEquals(collection_1072863812, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
                // AssertGenerator add assertion
                java.util.ArrayList collection_444911481 = new java.util.ArrayList<Object>();
	collection_444911481.add("b");
	collection_444911481.add("c");
	org.junit.Assert.assertEquals(collection_444911481, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "c");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).duplicate().equals(p));
                // AssertGenerator add assertion
                java.util.ArrayList collection_243713336 = new java.util.ArrayList<Object>();
	collection_243713336.add("b");
	collection_243713336.add("c");
	org.junit.Assert.assertEquals(collection_243713336, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).peekLast(), "c");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).size(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).peekLast(), "c");
                // AssertGenerator add assertion
                java.util.ArrayList collection_784436018 = new java.util.ArrayList<Object>();
	collection_784436018.add("b");
	collection_784436018.add("c");
	org.junit.Assert.assertEquals(collection_784436018, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate().equals(p));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)p).size(), 2);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1779541833 = new java.util.ArrayList<Object>();
	collection_1779541833.add("b");
	collection_1779541833.add("c");
	org.junit.Assert.assertEquals(collection_1779541833, ((ch.qos.logback.core.joran.spi.ElementPath)p).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate().equals(p));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)p).duplicate()).duplicate()).peekLast(), "c");
                ch.qos.logback.core.joran.spi.ElementSelector ruleElementSelector = new ch.qos.logback.core.joran.spi.ElementSelector("*/b/c");
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementPath vc_9228 = (ch.qos.logback.core.joran.spi.ElementPath)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9228);
                // AssertGenerator replace invocation
                boolean o_testTailMatch_literalMutation41655_cf44408__74 = // StatementAdderMethod cloned existing statement
ruleElementSelector.isContainedIn(vc_9228);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_testTailMatch_literalMutation41655_cf44408__74);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_10527 = new ch.qos.logback.core.joran.spi.ElementPath();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_10524 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_10524.isContainedIn(vc_10527);
                // MethodAssertGenerator build local variable
                Object o_110_0 = ruleElementSelector.getTailMatchLength(p);
            }
            org.junit.Assert.fail("testTailMatch_literalMutation41655_cf44408_cf54565 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

