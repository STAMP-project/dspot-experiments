package com.tokenautocomplete;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TagTokenizerTest {
    private TagTokenizer tokenizer;

    @Test
    public void testBasicTagDetection() {
        String test = "@bears #tokens";
        List<Range> ranges = tokenizer.findTokenRanges(test, 0, test.length());
        Assert.assertEquals(Arrays.asList(new Range(0, 6), new Range(7, 14)), ranges);
        Assert.assertEquals("@bears", test.substring(ranges.get(0).start, ranges.get(0).end));
        Assert.assertEquals("#tokens", test.substring(ranges.get(1).start, ranges.get(1).end));
    }

    @Test
    public void testSequentialTagDetection() {
        String test = "@@bears#tokens#";
        Assert.assertEquals(Arrays.asList(new Range(1, 7), new Range(7, 14)), tokenizer.findTokenRanges(test, 0, test.length()));
    }

    @Test
    public void testNonTokenInput() {
        String test = "This is some input with @names and #hash #tags inside";
        Assert.assertEquals(Arrays.asList(new Range(24, 30), new Range(35, 40), new Range(41, 46)), tokenizer.findTokenRanges(test, 0, test.length()));
    }

    @Test
    public void testMissingTokenContentInput() {
        String test = "@token       @ asdm      @asjdfhajks      sdfasdf";
        Assert.assertEquals(Arrays.asList(new Range(0, 6), new Range(25, 36)), tokenizer.findTokenRanges(test, 0, test.length()));
    }
}

