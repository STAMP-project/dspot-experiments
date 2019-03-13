/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class EqualsTest extends TestBase {
    @Test
    public void shouldBeEqual() {
        Assert.assertEquals(new Equals(null), new Equals(null));
        Assert.assertEquals(new Equals(new Integer(2)), new Equals(new Integer(2)));
        Assert.assertFalse(new Equals(null).equals(null));
        Assert.assertFalse(new Equals(null).equals("Test"));
        Assert.assertEquals(1, new Equals(null).hashCode());
    }

    @Test
    public void shouldArraysBeEqual() {
        Assert.assertTrue(new Equals(new int[]{ 1, 2 }).matches(new int[]{ 1, 2 }));
        Assert.assertFalse(new Equals(new Object[]{ "1" }).matches(new Object[]{ "1.0" }));
    }

    @Test
    public void shouldDescribeWithExtraTypeInfo() throws Exception {
        String descStr = new Equals(100).toStringWithType();
        Assert.assertEquals("(Integer) 100", descStr);
    }

    @Test
    public void shouldDescribeWithExtraTypeInfoOfLong() throws Exception {
        String descStr = new Equals(100L).toStringWithType();
        Assert.assertEquals("(Long) 100L", descStr);
    }

    @Test
    public void shouldDescribeWithTypeOfString() throws Exception {
        String descStr = new Equals("x").toStringWithType();
        Assert.assertEquals("(String) \"x\"", descStr);
    }

    @Test
    public void shouldAppendQuotingForString() {
        String descStr = new Equals("str").toString();
        Assert.assertEquals("\"str\"", descStr);
    }

    @Test
    public void shouldAppendQuotingForChar() {
        String descStr = new Equals('s').toString();
        Assert.assertEquals("'s'", descStr);
    }

    @Test
    public void shouldDescribeUsingToString() {
        String descStr = new Equals(100).toString();
        Assert.assertEquals("100", descStr);
    }

    @Test
    public void shouldDescribeNull() {
        String descStr = new Equals(null).toString();
        Assert.assertEquals("null", descStr);
    }

    @Test
    public void shouldMatchTypes() throws Exception {
        // when
        ContainsExtraTypeInfo equals = new Equals(10);
        // then
        Assert.assertTrue(equals.typeMatches(10));
        Assert.assertFalse(equals.typeMatches(10L));
    }

    @Test
    public void shouldMatchTypesSafelyWhenActualIsNull() throws Exception {
        // when
        ContainsExtraTypeInfo equals = new Equals(null);
        // then
        Assert.assertFalse(equals.typeMatches(10));
    }

    @Test
    public void shouldMatchTypesSafelyWhenGivenIsNull() throws Exception {
        // when
        ContainsExtraTypeInfo equals = new Equals(10);
        // then
        Assert.assertFalse(equals.typeMatches(null));
    }
}

