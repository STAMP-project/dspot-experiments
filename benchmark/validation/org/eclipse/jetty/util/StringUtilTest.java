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


public class StringUtilTest {
    @Test
    @SuppressWarnings("ReferenceEquality")
    public void testAsciiToLowerCase() {
        String lc = "\u0690bc def 1\u06903";
        Assertions.assertEquals(StringUtil.asciiToLowerCase("\u0690Bc DeF 1\u06903"), lc);
        Assertions.assertTrue(((StringUtil.asciiToLowerCase(lc)) == lc));
    }

    @Test
    public void testStartsWithIgnoreCase() {
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690b\u0690defg", "\u0690b\u0690"));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690bcdefg", "\u0690bc"));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690bcdefg", "\u0690Bc"));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690Bcdefg", "\u0690bc"));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690Bcdefg", "\u0690Bc"));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690bcdefg", ""));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690bcdefg", null));
        Assertions.assertTrue(StringUtil.startsWithIgnoreCase("\u0690bcdefg", "\u0690bcdefg"));
        Assertions.assertFalse(StringUtil.startsWithIgnoreCase(null, "xyz"));
        Assertions.assertFalse(StringUtil.startsWithIgnoreCase("\u0690bcdefg", "xyz"));
        Assertions.assertFalse(StringUtil.startsWithIgnoreCase("\u0690", "xyz"));
    }

    @Test
    public void testEndsWithIgnoreCase() {
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcd\u0690f\u0690", "\u0690f\u0690"));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdefg", "efg"));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdefg", "eFg"));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdeFg", "efg"));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdeFg", "eFg"));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdefg", ""));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdefg", null));
        Assertions.assertTrue(StringUtil.endsWithIgnoreCase("\u0690bcdefg", "\u0690bcdefg"));
        Assertions.assertFalse(StringUtil.endsWithIgnoreCase(null, "xyz"));
        Assertions.assertFalse(StringUtil.endsWithIgnoreCase("\u0690bcdefg", "xyz"));
        Assertions.assertFalse(StringUtil.endsWithIgnoreCase("\u0690", "xyz"));
    }

    @Test
    public void testIndexFrom() {
        Assertions.assertEquals(StringUtil.indexFrom("\u0690bcd", "xyz"), (-1));
        Assertions.assertEquals(StringUtil.indexFrom("\u0690bcd", "\u0690bcz"), 0);
        Assertions.assertEquals(StringUtil.indexFrom("\u0690bcd", "bcz"), 1);
        Assertions.assertEquals(StringUtil.indexFrom("\u0690bcd", "dxy"), 3);
    }

    @Test
    @SuppressWarnings("ReferenceEquality")
    public void testReplace() {
        String s = "\u0690bc \u0690bc \u0690bc";
        Assertions.assertEquals(StringUtil.replace(s, "\u0690bc", "xyz"), "xyz xyz xyz");
        Assertions.assertTrue(((StringUtil.replace(s, "xyz", "pqy")) == s));
        s = " \u0690bc ";
        Assertions.assertEquals(StringUtil.replace(s, "\u0690bc", "xyz"), " xyz ");
    }

    @Test
    @SuppressWarnings("ReferenceEquality")
    public void testUnquote() {
        String uq = " not quoted ";
        Assertions.assertTrue(((StringUtil.unquote(uq)) == uq));
        Assertions.assertEquals(StringUtil.unquote("' quoted string '"), " quoted string ");
        Assertions.assertEquals(StringUtil.unquote("\" quoted string \""), " quoted string ");
        Assertions.assertEquals(StringUtil.unquote("\' quoted\"string \'"), " quoted\"string ");
        Assertions.assertEquals(StringUtil.unquote("\" quoted\'string \""), " quoted'string ");
    }

    @Test
    @SuppressWarnings("ReferenceEquality")
    public void testNonNull() {
        String nn = "non empty string";
        Assertions.assertTrue((nn == (StringUtil.nonNull(nn))));
        Assertions.assertEquals("", StringUtil.nonNull(null));
    }

    /* Test for boolean equals(String, char[], int, int) */
    @Test
    public void testEqualsStringcharArrayintint() {
        Assertions.assertTrue(StringUtil.equals("\u0690bc", new char[]{ 'x', '\u0690', 'b', 'c', 'z' }, 1, 3));
        Assertions.assertFalse(StringUtil.equals("axc", new char[]{ 'x', 'a', 'b', 'c', 'z' }, 1, 3));
    }

    @Test
    public void testAppend() {
        StringBuilder buf = new StringBuilder();
        buf.append('a');
        StringUtil.append(buf, "abc", 1, 1);
        StringUtil.append(buf, ((byte) (12)), 16);
        StringUtil.append(buf, ((byte) (16)), 16);
        StringUtil.append(buf, ((byte) (-1)), 16);
        StringUtil.append(buf, ((byte) (-16)), 16);
        Assertions.assertEquals("ab0c10fff0", buf.toString());
    }

    @Test
    @Deprecated
    public void testSidConversion() throws Exception {
        String sid4 = "S-1-4-21-3623811015-3361044348-30300820";
        String sid5 = "S-1-5-21-3623811015-3361044348-30300820-1013";
        String sid6 = "S-1-6-21-3623811015-3361044348-30300820-1013-23445";
        String sid12 = "S-1-12-21-3623811015-3361044348-30300820-1013-23445-21-3623811015-3361044348-30300820-1013-23445";
        byte[] sid4Bytes = StringUtil.sidStringToBytes(sid4);
        byte[] sid5Bytes = StringUtil.sidStringToBytes(sid5);
        byte[] sid6Bytes = StringUtil.sidStringToBytes(sid6);
        byte[] sid12Bytes = StringUtil.sidStringToBytes(sid12);
        Assertions.assertEquals(sid4, StringUtil.sidBytesToString(sid4Bytes));
        Assertions.assertEquals(sid5, StringUtil.sidBytesToString(sid5Bytes));
        Assertions.assertEquals(sid6, StringUtil.sidBytesToString(sid6Bytes));
        Assertions.assertEquals(sid12, StringUtil.sidBytesToString(sid12Bytes));
    }

    @Test
    public void testHasControlCharacter() {
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("\r\n"), Matchers.is(0));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("\t"), Matchers.is(0));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars(";\n"), Matchers.is(1));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("abc\fz"), Matchers.is(3));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("z\b"), Matchers.is(1));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars(":\u001c"), Matchers.is(1));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars(null), Matchers.is((-1)));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars(""), Matchers.is((-1)));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("   "), Matchers.is((-1)));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("a"), Matchers.is((-1)));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("."), Matchers.is((-1)));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars(";"), Matchers.is((-1)));
        MatcherAssert.assertThat(StringUtil.indexOfControlChars("Euro is \u20ac"), Matchers.is((-1)));
    }

    @Test
    public void testIsBlank() {
        Assertions.assertTrue(StringUtil.isBlank(null));
        Assertions.assertTrue(StringUtil.isBlank(""));
        Assertions.assertTrue(StringUtil.isBlank("\r\n"));
        Assertions.assertTrue(StringUtil.isBlank("\t"));
        Assertions.assertTrue(StringUtil.isBlank("   "));
        Assertions.assertFalse(StringUtil.isBlank("a"));
        Assertions.assertFalse(StringUtil.isBlank("  a"));
        Assertions.assertFalse(StringUtil.isBlank("a  "));
        Assertions.assertFalse(StringUtil.isBlank("."));
        Assertions.assertFalse(StringUtil.isBlank(";\n"));
    }

    @Test
    public void testIsNotBlank() {
        Assertions.assertFalse(StringUtil.isNotBlank(null));
        Assertions.assertFalse(StringUtil.isNotBlank(""));
        Assertions.assertFalse(StringUtil.isNotBlank("\r\n"));
        Assertions.assertFalse(StringUtil.isNotBlank("\t"));
        Assertions.assertFalse(StringUtil.isNotBlank("   "));
        Assertions.assertTrue(StringUtil.isNotBlank("a"));
        Assertions.assertTrue(StringUtil.isNotBlank("  a"));
        Assertions.assertTrue(StringUtil.isNotBlank("a  "));
        Assertions.assertTrue(StringUtil.isNotBlank("."));
        Assertions.assertTrue(StringUtil.isNotBlank(";\n"));
    }

    @Test
    public void testSanitizeHTML() {
        Assertions.assertEquals(null, StringUtil.sanitizeXmlString(null));
        Assertions.assertEquals("", StringUtil.sanitizeXmlString(""));
        Assertions.assertEquals("&lt;&amp;&gt;", StringUtil.sanitizeXmlString("<&>"));
        Assertions.assertEquals("Hello &lt;Cruel&gt; World", StringUtil.sanitizeXmlString("Hello <Cruel> World"));
        Assertions.assertEquals("Hello ? World", StringUtil.sanitizeXmlString("Hello \u0000 World"));
    }

    @Test
    public void testSplit() {
        MatcherAssert.assertThat(StringUtil.csvSplit(null), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtil.csvSplit(null), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtil.csvSplit(""), Matchers.emptyArray());
        MatcherAssert.assertThat(StringUtil.csvSplit(" \t\n"), Matchers.emptyArray());
        MatcherAssert.assertThat(StringUtil.csvSplit("aaa"), Matchers.arrayContaining("aaa"));
        MatcherAssert.assertThat(StringUtil.csvSplit(" \taaa\n"), Matchers.arrayContaining("aaa"));
        MatcherAssert.assertThat(StringUtil.csvSplit(" \ta\n"), Matchers.arrayContaining("a"));
        MatcherAssert.assertThat(StringUtil.csvSplit(" \t\u1234\n"), Matchers.arrayContaining("\u1234"));
        MatcherAssert.assertThat(StringUtil.csvSplit("aaa,bbb,ccc"), Matchers.arrayContaining("aaa", "bbb", "ccc"));
        MatcherAssert.assertThat(StringUtil.csvSplit("aaa,,ccc"), Matchers.arrayContaining("aaa", "", "ccc"));
        MatcherAssert.assertThat(StringUtil.csvSplit(",b b,"), Matchers.arrayContaining("", "b b"));
        MatcherAssert.assertThat(StringUtil.csvSplit(",,bbb,,"), Matchers.arrayContaining("", "", "bbb", ""));
        MatcherAssert.assertThat(StringUtil.csvSplit(" aaa, bbb, ccc"), Matchers.arrayContaining("aaa", "bbb", "ccc"));
        MatcherAssert.assertThat(StringUtil.csvSplit("aaa,\t,ccc"), Matchers.arrayContaining("aaa", "", "ccc"));
        MatcherAssert.assertThat(StringUtil.csvSplit("  ,  b b  ,   "), Matchers.arrayContaining("", "b b"));
        MatcherAssert.assertThat(StringUtil.csvSplit(" ,\n,bbb, , "), Matchers.arrayContaining("", "", "bbb", ""));
        MatcherAssert.assertThat(StringUtil.csvSplit("\"aaa\", \" b,\\\"\",\"\""), Matchers.arrayContaining("aaa", " b,\"", ""));
    }
}

