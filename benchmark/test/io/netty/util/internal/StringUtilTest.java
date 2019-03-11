/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import EmptyArrays.EMPTY_BYTES;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void ensureNewlineExists() {
        Assert.assertNotNull(StringUtil.NEWLINE);
    }

    @Test
    public void testToHexString() {
        Assert.assertThat(StringUtil.toHexString(new byte[]{ 0 }), CoreMatchers.is("0"));
        Assert.assertThat(StringUtil.toHexString(new byte[]{ 1 }), CoreMatchers.is("1"));
        Assert.assertThat(StringUtil.toHexString(new byte[]{ 0, 0 }), CoreMatchers.is("0"));
        Assert.assertThat(StringUtil.toHexString(new byte[]{ 1, 0 }), CoreMatchers.is("100"));
        Assert.assertThat(StringUtil.toHexString(EMPTY_BYTES), CoreMatchers.is(""));
    }

    @Test
    public void testToHexStringPadded() {
        Assert.assertThat(StringUtil.toHexStringPadded(new byte[]{ 0 }), CoreMatchers.is("00"));
        Assert.assertThat(StringUtil.toHexStringPadded(new byte[]{ 1 }), CoreMatchers.is("01"));
        Assert.assertThat(StringUtil.toHexStringPadded(new byte[]{ 0, 0 }), CoreMatchers.is("0000"));
        Assert.assertThat(StringUtil.toHexStringPadded(new byte[]{ 1, 0 }), CoreMatchers.is("0100"));
        Assert.assertThat(StringUtil.toHexStringPadded(EMPTY_BYTES), CoreMatchers.is(""));
    }

    @Test
    public void splitSimple() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, "foo:bar".split(":"));
    }

    @Test
    public void splitWithTrailingDelimiter() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, "foo,bar,".split(","));
    }

    @Test
    public void splitWithTrailingDelimiters() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, "foo!bar!!".split("!"));
    }

    @Test
    public void splitWithTrailingDelimitersDot() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, "foo.bar..".split("\\."));
    }

    @Test
    public void splitWithTrailingDelimitersEq() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, "foo=bar==".split("="));
    }

    @Test
    public void splitWithTrailingDelimitersSpace() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, "foo bar  ".split(" "));
    }

    @Test
    public void splitWithConsecutiveDelimiters() {
        Assert.assertArrayEquals(new String[]{ "foo", "", "bar" }, "foo$$bar".split("\\$"));
    }

    @Test
    public void splitWithDelimiterAtBeginning() {
        Assert.assertArrayEquals(new String[]{ "", "foo", "bar" }, "#foo#bar".split("#"));
    }

    @Test
    public void splitMaxPart() {
        Assert.assertArrayEquals(new String[]{ "foo", "bar:bar2" }, "foo:bar:bar2".split(":", 2));
        Assert.assertArrayEquals(new String[]{ "foo", "bar", "bar2" }, "foo:bar:bar2".split(":", 3));
    }

    @Test
    public void substringAfterTest() {
        Assert.assertEquals("bar:bar2", StringUtil.substringAfter("foo:bar:bar2", ':'));
    }

    @Test
    public void commonSuffixOfLengthTest() {
        // negative length suffixes are never common
        StringUtilTest.checkNotCommonSuffix("abc", "abc", (-1));
        // null has no suffix
        StringUtilTest.checkNotCommonSuffix("abc", null, 0);
        StringUtilTest.checkNotCommonSuffix(null, null, 0);
        // any non-null string has 0-length suffix
        StringUtilTest.checkCommonSuffix("abc", "xx", 0);
        StringUtilTest.checkCommonSuffix("abc", "abc", 0);
        StringUtilTest.checkCommonSuffix("abc", "abc", 1);
        StringUtilTest.checkCommonSuffix("abc", "abc", 2);
        StringUtilTest.checkCommonSuffix("abc", "abc", 3);
        StringUtilTest.checkNotCommonSuffix("abc", "abc", 4);
        StringUtilTest.checkCommonSuffix("abcd", "cd", 1);
        StringUtilTest.checkCommonSuffix("abcd", "cd", 2);
        StringUtilTest.checkNotCommonSuffix("abcd", "cd", 3);
        StringUtilTest.checkCommonSuffix("abcd", "axcd", 1);
        StringUtilTest.checkCommonSuffix("abcd", "axcd", 2);
        StringUtilTest.checkNotCommonSuffix("abcd", "axcd", 3);
        StringUtilTest.checkNotCommonSuffix("abcx", "abcy", 1);
    }

    @Test(expected = NullPointerException.class)
    public void escapeCsvNull() {
        StringUtil.escapeCsv(null);
    }

    @Test
    public void escapeCsvEmpty() {
        CharSequence value = "";
        CharSequence expected = value;
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvUnquoted() {
        CharSequence value = "something";
        CharSequence expected = value;
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvAlreadyQuoted() {
        CharSequence value = "\"something\"";
        CharSequence expected = "\"something\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithQuote() {
        CharSequence value = "s\"";
        CharSequence expected = "\"s\"\"\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithQuoteInMiddle() {
        CharSequence value = "some text\"and more text";
        CharSequence expected = "\"some text\"\"and more text\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithQuoteInMiddleAlreadyQuoted() {
        CharSequence value = "\"some text\"and more text\"";
        CharSequence expected = "\"some text\"\"and more text\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithQuotedWords() {
        CharSequence value = "\"foo\"\"goo\"";
        CharSequence expected = "\"foo\"\"goo\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithAlreadyEscapedQuote() {
        CharSequence value = "foo\"\"goo";
        CharSequence expected = "foo\"\"goo";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvEndingWithQuote() {
        CharSequence value = "some\"";
        CharSequence expected = "\"some\"\"\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithSingleQuote() {
        CharSequence value = "\"";
        CharSequence expected = "\"\"\"\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithSingleQuoteAndCharacter() {
        CharSequence value = "\"f";
        CharSequence expected = "\"\"\"f\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvAlreadyEscapedQuote() {
        CharSequence value = "\"some\"\"";
        CharSequence expected = "\"some\"\"\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvQuoted() {
        CharSequence value = "\"foo,goo\"";
        CharSequence expected = value;
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithLineFeed() {
        CharSequence value = "some text\n more text";
        CharSequence expected = "\"some text\n more text\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithSingleLineFeedCharacter() {
        CharSequence value = "\n";
        CharSequence expected = "\"\n\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithMultipleLineFeedCharacter() {
        CharSequence value = "\n\n";
        CharSequence expected = "\"\n\n\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithQuotedAndLineFeedCharacter() {
        CharSequence value = " \" \n ";
        CharSequence expected = "\" \"\" \n \"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithLineFeedAtEnd() {
        CharSequence value = "testing\n";
        CharSequence expected = "\"testing\n\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithComma() {
        CharSequence value = "test,ing";
        CharSequence expected = "\"test,ing\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithSingleComma() {
        CharSequence value = ",";
        CharSequence expected = "\",\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithSingleCarriageReturn() {
        CharSequence value = "\r";
        CharSequence expected = "\"\r\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithMultipleCarriageReturn() {
        CharSequence value = "\r\r";
        CharSequence expected = "\"\r\r\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithCarriageReturn() {
        CharSequence value = "some text\r more text";
        CharSequence expected = "\"some text\r more text\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithQuotedAndCarriageReturnCharacter() {
        CharSequence value = "\"\r";
        CharSequence expected = "\"\"\"\r\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithCarriageReturnAtEnd() {
        CharSequence value = "testing\r";
        CharSequence expected = "\"testing\r\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithCRLFCharacter() {
        CharSequence value = "\r\n";
        CharSequence expected = "\"\r\n\"";
        StringUtilTest.escapeCsv(value, expected);
    }

    @Test
    public void escapeCsvWithTrimming() {
        Assert.assertSame("", StringUtil.escapeCsv("", true));
        Assert.assertSame("ab", StringUtil.escapeCsv("ab", true));
        StringUtilTest.escapeCsvWithTrimming("", "");
        StringUtilTest.escapeCsvWithTrimming(" \t ", "");
        StringUtilTest.escapeCsvWithTrimming("ab", "ab");
        StringUtilTest.escapeCsvWithTrimming("a b", "a b");
        StringUtilTest.escapeCsvWithTrimming(" \ta \tb", "a \tb");
        StringUtilTest.escapeCsvWithTrimming("a \tb \t", "a \tb");
        StringUtilTest.escapeCsvWithTrimming("\t a \tb \t", "a \tb");
        StringUtilTest.escapeCsvWithTrimming("\"\t a b \"", "\"\t a b \"");
        StringUtilTest.escapeCsvWithTrimming(" \"\t a b \"\t", "\"\t a b \"");
        StringUtilTest.escapeCsvWithTrimming(" testing\t\n ", "\"testing\t\n\"");
        StringUtilTest.escapeCsvWithTrimming("\ttest,ing ", "\"test,ing\"");
    }

    @Test
    public void escapeCsvGarbageFree() {
        // 'StringUtil#escapeCsv()' should return same string object if string didn't changing.
        Assert.assertSame("1", StringUtil.escapeCsv("1", true));
        Assert.assertSame(" 123 ", StringUtil.escapeCsv(" 123 ", false));
        Assert.assertSame("\" 123 \"", StringUtil.escapeCsv("\" 123 \"", true));
        Assert.assertSame("\"\"", StringUtil.escapeCsv("\"\"", true));
        Assert.assertSame("123 \"\"", StringUtil.escapeCsv("123 \"\"", true));
        Assert.assertSame("123\"\"321", StringUtil.escapeCsv("123\"\"321", true));
        Assert.assertSame("\"123\"\"321\"", StringUtil.escapeCsv("\"123\"\"321\"", true));
    }

    @Test
    public void testUnescapeCsv() {
        Assert.assertEquals("", StringUtil.unescapeCsv(""));
        Assert.assertEquals("\"", StringUtil.unescapeCsv("\"\"\"\""));
        Assert.assertEquals("\"\"", StringUtil.unescapeCsv("\"\"\"\"\"\""));
        Assert.assertEquals("\"\"\"", StringUtil.unescapeCsv("\"\"\"\"\"\"\"\""));
        Assert.assertEquals("\"netty\"", StringUtil.unescapeCsv("\"\"\"netty\"\"\""));
        Assert.assertEquals("netty", StringUtil.unescapeCsv("netty"));
        Assert.assertEquals("netty", StringUtil.unescapeCsv("\"netty\""));
        Assert.assertEquals("\r", StringUtil.unescapeCsv("\"\r\""));
        Assert.assertEquals("\n", StringUtil.unescapeCsv("\"\n\""));
        Assert.assertEquals("hello,netty", StringUtil.unescapeCsv("\"hello,netty\""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvWithSingleQuote() {
        StringUtil.unescapeCsv("\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvWithOddQuote() {
        StringUtil.unescapeCsv("\"\"\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvWithCRAndWithoutQuote() {
        StringUtil.unescapeCsv("\r");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvWithLFAndWithoutQuote() {
        StringUtil.unescapeCsv("\n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvWithCommaAndWithoutQuote() {
        StringUtil.unescapeCsv(",");
    }

    @Test
    public void escapeCsvAndUnEscapeCsv() {
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("netty");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("hello,netty");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("hello,\"netty\"");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("\"");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv(",");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("\r");
        StringUtilTest.assertEscapeCsvAndUnEscapeCsv("\n");
    }

    @Test
    public void testUnescapeCsvFields() {
        Assert.assertEquals(Arrays.asList(""), StringUtil.unescapeCsvFields(""));
        Assert.assertEquals(Arrays.asList("", ""), StringUtil.unescapeCsvFields(","));
        Assert.assertEquals(Arrays.asList("a", ""), StringUtil.unescapeCsvFields("a,"));
        Assert.assertEquals(Arrays.asList("", "a"), StringUtil.unescapeCsvFields(",a"));
        Assert.assertEquals(Arrays.asList("\""), StringUtil.unescapeCsvFields("\"\"\"\""));
        Assert.assertEquals(Arrays.asList("\"", "\""), StringUtil.unescapeCsvFields("\"\"\"\",\"\"\"\""));
        Assert.assertEquals(Arrays.asList("netty"), StringUtil.unescapeCsvFields("netty"));
        Assert.assertEquals(Arrays.asList("hello", "netty"), StringUtil.unescapeCsvFields("hello,netty"));
        Assert.assertEquals(Arrays.asList("hello,netty"), StringUtil.unescapeCsvFields("\"hello,netty\""));
        Assert.assertEquals(Arrays.asList("hello", "netty"), StringUtil.unescapeCsvFields("\"hello\",\"netty\""));
        Assert.assertEquals(Arrays.asList("a\"b", "c\"d"), StringUtil.unescapeCsvFields("\"a\"\"b\",\"c\"\"d\""));
        Assert.assertEquals(Arrays.asList("a\rb", "c\nd"), StringUtil.unescapeCsvFields("\"a\rb\",\"c\nd\""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvFieldsWithCRWithoutQuote() {
        StringUtil.unescapeCsvFields("a,\r");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvFieldsWithLFWithoutQuote() {
        StringUtil.unescapeCsvFields("a,\r");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvFieldsWithQuote() {
        StringUtil.unescapeCsvFields("a,\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvFieldsWithQuote2() {
        StringUtil.unescapeCsvFields("\",a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unescapeCsvFieldsWithQuote3() {
        StringUtil.unescapeCsvFields("a\"b,a");
    }

    @Test
    public void testSimpleClassName() throws Exception {
        StringUtilTest.testSimpleClassName(String.class);
    }

    @Test
    public void testSimpleInnerClassName() throws Exception {
        StringUtilTest.testSimpleClassName(StringUtilTest.TestClass.class);
    }

    private static final class TestClass {}

    @Test
    public void testEndsWith() {
        Assert.assertFalse(StringUtil.endsWith("", 'u'));
        Assert.assertTrue(StringUtil.endsWith("u", 'u'));
        Assert.assertTrue(StringUtil.endsWith("-u", 'u'));
        Assert.assertFalse(StringUtil.endsWith("-", 'u'));
        Assert.assertFalse(StringUtil.endsWith("u-", 'u'));
    }

    @Test
    public void trimOws() {
        Assert.assertSame("", StringUtil.trimOws(""));
        Assert.assertEquals("", StringUtil.trimOws(" \t "));
        Assert.assertSame("a", StringUtil.trimOws("a"));
        Assert.assertEquals("a", StringUtil.trimOws(" a"));
        Assert.assertEquals("a", StringUtil.trimOws("a "));
        Assert.assertEquals("a", StringUtil.trimOws(" a "));
        Assert.assertSame("abc", StringUtil.trimOws("abc"));
        Assert.assertEquals("abc", StringUtil.trimOws("\tabc"));
        Assert.assertEquals("abc", StringUtil.trimOws("abc\t"));
        Assert.assertEquals("abc", StringUtil.trimOws("\tabc\t"));
        Assert.assertSame("a\t b", StringUtil.trimOws("a\t b"));
        Assert.assertEquals("", StringUtil.trimOws("\t ").toString());
        Assert.assertEquals("a b", StringUtil.trimOws("\ta b \t").toString());
    }
}

