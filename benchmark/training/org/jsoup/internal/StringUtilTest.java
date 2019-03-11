package org.jsoup.internal;


import java.util.Arrays;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void join() {
        Assert.assertEquals("", StringUtil.join(Arrays.asList(""), " "));
        Assert.assertEquals("one", StringUtil.join(Arrays.asList("one"), " "));
        Assert.assertEquals("one two three", StringUtil.join(Arrays.asList("one", "two", "three"), " "));
    }

    @Test
    public void padding() {
        Assert.assertEquals("", StringUtil.padding(0));
        Assert.assertEquals(" ", StringUtil.padding(1));
        Assert.assertEquals("  ", StringUtil.padding(2));
        Assert.assertEquals("               ", StringUtil.padding(15));
        Assert.assertEquals("                                             ", StringUtil.padding(45));
    }

    @Test
    public void paddingInACan() {
        String[] padding = StringUtil.padding;
        Assert.assertEquals(21, padding.length);
        for (int i = 0; i < (padding.length); i++) {
            Assert.assertEquals(i, padding[i].length());
        }
    }

    @Test
    public void isBlank() {
        Assert.assertTrue(StringUtil.isBlank(null));
        Assert.assertTrue(StringUtil.isBlank(""));
        Assert.assertTrue(StringUtil.isBlank("      "));
        Assert.assertTrue(StringUtil.isBlank("   \r\n  "));
        Assert.assertFalse(StringUtil.isBlank("hello"));
        Assert.assertFalse(StringUtil.isBlank("   hello   "));
    }

    @Test
    public void isNumeric() {
        Assert.assertFalse(StringUtil.isNumeric(null));
        Assert.assertFalse(StringUtil.isNumeric(" "));
        Assert.assertFalse(StringUtil.isNumeric("123 546"));
        Assert.assertFalse(StringUtil.isNumeric("hello"));
        Assert.assertFalse(StringUtil.isNumeric("123.334"));
        Assert.assertTrue(StringUtil.isNumeric("1"));
        Assert.assertTrue(StringUtil.isNumeric("1234"));
    }

    @Test
    public void isWhitespace() {
        Assert.assertTrue(StringUtil.isWhitespace('\t'));
        Assert.assertTrue(StringUtil.isWhitespace('\n'));
        Assert.assertTrue(StringUtil.isWhitespace('\r'));
        Assert.assertTrue(StringUtil.isWhitespace('\f'));
        Assert.assertTrue(StringUtil.isWhitespace(' '));
        Assert.assertFalse(StringUtil.isWhitespace('\u00a0'));
        Assert.assertFalse(StringUtil.isWhitespace('\u2000'));
        Assert.assertFalse(StringUtil.isWhitespace('\u3000'));
    }

    @Test
    public void normaliseWhiteSpace() {
        Assert.assertEquals(" ", normaliseWhitespace("    \r \n \r\n"));
        Assert.assertEquals(" hello there ", normaliseWhitespace("   hello   \r \n  there    \n"));
        Assert.assertEquals("hello", normaliseWhitespace("hello"));
        Assert.assertEquals("hello there", normaliseWhitespace("hello\nthere"));
    }

    @Test
    public void normaliseWhiteSpaceHandlesHighSurrogates() {
        String test71540chars = "\ud869\udeb2\u304b\u309a  1";
        String test71540charsExpectedSingleWhitespace = "\ud869\udeb2\u304b\u309a 1";
        Assert.assertEquals(test71540charsExpectedSingleWhitespace, normaliseWhitespace(test71540chars));
        String extractedText = Jsoup.parse(test71540chars).text();
        Assert.assertEquals(test71540charsExpectedSingleWhitespace, extractedText);
    }

    @Test
    public void resolvesRelativeUrls() {
        Assert.assertEquals("http://example.com/one/two?three", resolve("http://example.com", "./one/two?three"));
        Assert.assertEquals("http://example.com/one/two?three", resolve("http://example.com?one", "./one/two?three"));
        Assert.assertEquals("http://example.com/one/two?three#four", resolve("http://example.com", "./one/two?three#four"));
        Assert.assertEquals("https://example.com/one", resolve("http://example.com/", "https://example.com/one"));
        Assert.assertEquals("http://example.com/one/two.html", resolve("http://example.com/two/", "../one/two.html"));
        Assert.assertEquals("https://example2.com/one", resolve("https://example.com/", "//example2.com/one"));
        Assert.assertEquals("https://example.com:8080/one", resolve("https://example.com:8080", "./one"));
        Assert.assertEquals("https://example2.com/one", resolve("http://example.com/", "https://example2.com/one"));
        Assert.assertEquals("https://example.com/one", resolve("wrong", "https://example.com/one"));
        Assert.assertEquals("https://example.com/one", resolve("https://example.com/one", ""));
        Assert.assertEquals("", resolve("wrong", "also wrong"));
        Assert.assertEquals("ftp://example.com/one", resolve("ftp://example.com/two/", "../one"));
        Assert.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "./two.c"));
        Assert.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "two.c"));
    }
}

