package com.baeldung.string;


import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class StringHelperUnitTest {
    public static final String TEST_STRING = "abcdef";

    public static final String NULL_STRING = null;

    public static final String EMPTY_STRING = "";

    public static final String ONE_CHAR_STRING = "a";

    public static final String WHITE_SPACE_AT_THE_END_STRING = "abc ";

    public static final String NEW_LINE_AT_THE_END_STRING = "abc\n";

    public static final String MULTIPLE_LINES_STRING = "abc\ndef";

    @Test
    public void givenTestString_whenSubstring_thenGetStingWithoutLastChar() {
        Assert.assertEquals("abcde", StringHelper.removeLastChar(StringHelperUnitTest.TEST_STRING));
        Assert.assertEquals("abcde", StringUtils.substring(StringHelperUnitTest.TEST_STRING, 0, ((StringHelperUnitTest.TEST_STRING.length()) - 1)));
        Assert.assertEquals("abcde", StringUtils.chop(StringHelperUnitTest.TEST_STRING));
        Assert.assertEquals("abcde", StringHelperUnitTest.TEST_STRING.replaceAll(".$", ""));
        Assert.assertEquals("abcde", StringHelper.removeLastCharRegex(StringHelperUnitTest.TEST_STRING));
        Assert.assertEquals("abcde", StringHelper.removeLastCharOptional(StringHelperUnitTest.TEST_STRING));
        Assert.assertEquals("abcde", StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.TEST_STRING));
    }

    @Test
    public void givenNullString_whenSubstring_thenGetNullString() {
        Assert.assertEquals(StringHelperUnitTest.NULL_STRING, StringHelper.removeLastChar(StringHelperUnitTest.NULL_STRING));
        Assert.assertEquals(StringHelperUnitTest.NULL_STRING, StringUtils.chop(StringHelperUnitTest.NULL_STRING));
        Assert.assertEquals(StringHelperUnitTest.NULL_STRING, StringHelper.removeLastCharRegex(StringHelperUnitTest.NULL_STRING));
        Assert.assertEquals(StringHelperUnitTest.NULL_STRING, StringHelper.removeLastCharOptional(StringHelperUnitTest.NULL_STRING));
        Assert.assertEquals(StringHelperUnitTest.NULL_STRING, StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.NULL_STRING));
    }

    @Test
    public void givenEmptyString_whenSubstring_thenGetEmptyString() {
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastChar(StringHelperUnitTest.EMPTY_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringUtils.substring(StringHelperUnitTest.EMPTY_STRING, 0, ((StringHelperUnitTest.EMPTY_STRING.length()) - 1)));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringUtils.chop(StringHelperUnitTest.EMPTY_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelperUnitTest.EMPTY_STRING.replaceAll(".$", ""));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastCharRegex(StringHelperUnitTest.EMPTY_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastCharOptional(StringHelperUnitTest.EMPTY_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.EMPTY_STRING));
    }

    @Test
    public void givenOneCharString_whenSubstring_thenGetEmptyString() {
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastChar(StringHelperUnitTest.ONE_CHAR_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringUtils.substring(StringHelperUnitTest.ONE_CHAR_STRING, 0, ((StringHelperUnitTest.ONE_CHAR_STRING.length()) - 1)));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringUtils.chop(StringHelperUnitTest.ONE_CHAR_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelperUnitTest.ONE_CHAR_STRING.replaceAll(".$", ""));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastCharRegex(StringHelperUnitTest.ONE_CHAR_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastCharOptional(StringHelperUnitTest.ONE_CHAR_STRING));
        Assert.assertEquals(StringHelperUnitTest.EMPTY_STRING, StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.ONE_CHAR_STRING));
    }

    @Test
    public void givenStringWithWhiteSpaceAtTheEnd_whenSubstring_thenGetStringWithoutWhiteSpaceAtTheEnd() {
        Assert.assertEquals("abc", StringHelper.removeLastChar(StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING));
        Assert.assertEquals("abc", StringUtils.substring(StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING, 0, ((StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING.length()) - 1)));
        Assert.assertEquals("abc", StringUtils.chop(StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING));
        Assert.assertEquals("abc", StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING.replaceAll(".$", ""));
        Assert.assertEquals("abc", StringHelper.removeLastCharRegex(StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING));
        Assert.assertEquals("abc", StringHelper.removeLastCharOptional(StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING));
        Assert.assertEquals("abc", StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.WHITE_SPACE_AT_THE_END_STRING));
    }

    @Test
    public void givenStringWithNewLineAtTheEnd_whenSubstring_thenGetStringWithoutNewLine() {
        Assert.assertEquals("abc", StringHelper.removeLastChar(StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING));
        Assert.assertEquals("abc", StringUtils.substring(StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING, 0, ((StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING.length()) - 1)));
        Assert.assertEquals("abc", StringUtils.chop(StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING));
        Assert.assertNotEquals("abc", StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING.replaceAll(".$", ""));
        Assert.assertNotEquals("abc", StringHelper.removeLastCharRegex(StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING));
        Assert.assertEquals("abc", StringHelper.removeLastCharOptional(StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING));
        Assert.assertNotEquals("abc", StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.NEW_LINE_AT_THE_END_STRING));
    }

    @Test
    public void givenMultiLineString_whenSubstring_thenGetStringWithoutNewLine() {
        Assert.assertEquals("abc\nde", StringHelper.removeLastChar(StringHelperUnitTest.MULTIPLE_LINES_STRING));
        Assert.assertEquals("abc\nde", StringUtils.substring(StringHelperUnitTest.MULTIPLE_LINES_STRING, 0, ((StringHelperUnitTest.MULTIPLE_LINES_STRING.length()) - 1)));
        Assert.assertEquals("abc\nde", StringUtils.chop(StringHelperUnitTest.MULTIPLE_LINES_STRING));
        Assert.assertEquals("abc\nde", StringHelperUnitTest.MULTIPLE_LINES_STRING.replaceAll(".$", ""));
        Assert.assertEquals("abc\nde", StringHelper.removeLastCharRegex(StringHelperUnitTest.MULTIPLE_LINES_STRING));
        Assert.assertEquals("abc\nde", StringHelper.removeLastCharOptional(StringHelperUnitTest.MULTIPLE_LINES_STRING));
        Assert.assertEquals("abc\nde", StringHelper.removeLastCharRegexOptional(StringHelperUnitTest.MULTIPLE_LINES_STRING));
    }
}

