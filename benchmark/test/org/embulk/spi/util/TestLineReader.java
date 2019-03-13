package org.embulk.spi.util;


import LineDelimiter.CR;
import LineDelimiter.CRLF;
import LineDelimiter.LF;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestLineReader {
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithoutDelimiter() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\rtest2\ntest3\r\ntest4", null, 256);
        Assert.assertEquals(Arrays.asList("test1", "test2", "test3", "test4"), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterCR() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\rtest2\ntest3\r\ntest4", CR, 256);
        Assert.assertEquals(Arrays.asList("test1", "test2\ntest3\r\ntest4"), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterLF() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\rtest2\ntest3\r\ntest4", LF, 256);
        Assert.assertEquals(Arrays.asList("test1\rtest2", "test3\r\ntest4"), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterCRLF() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\rtest2\ntest3\r\ntest4", CRLF, 256);
        Assert.assertEquals(Arrays.asList("test1\rtest2\ntest3", "test4"), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterAndSmallBuffer() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\rtest2\ntest3\r\ntest4", CR, 1);
        Assert.assertEquals(Arrays.asList("test1", "test2\ntest3\r\ntest4"), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterCRWithEmptyLine() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\r\rtest2\r", CR, 256);
        Assert.assertEquals(Arrays.asList("test1", "", "test2", ""), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterLFWithEmptyLine() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\n\ntest2\n", LF, 256);
        Assert.assertEquals(Arrays.asList("test1", "", "test2", ""), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterCRLFWithEmptyLine() throws IOException {
        List<String> lines = TestLineReader.readLines("test1\r\n\r\ntest2\r\n", CRLF, 256);
        Assert.assertEquals(Arrays.asList("test1", "", "test2", ""), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithoutDelimiterAndEmptyString() throws IOException {
        List<String> lines = TestLineReader.readLines("", null, 256);
        Assert.assertEquals(Collections.emptyList(), lines);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testReadLineWithDelimiterAndEmptyString() throws IOException {
        List<String> lines = TestLineReader.readLines("", CR, 256);
        Assert.assertEquals(Collections.emptyList(), lines);
    }
}

