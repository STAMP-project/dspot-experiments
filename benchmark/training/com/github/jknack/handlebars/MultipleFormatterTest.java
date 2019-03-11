package com.github.jknack.handlebars;


import java.io.IOException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class MultipleFormatterTest extends AbstractTest {
    static final long now = System.currentTimeMillis();

    @Test
    public void testDateFormatter() throws IOException {
        Template t = compile("time is {{this}}");
        Assert.assertEquals(("time is " + (MultipleFormatterTest.now)), t.apply(new Date(MultipleFormatterTest.now)));
    }

    @Test
    public void testIntegerFormatter() throws IOException {
        Template t = compile("Hex-Value is {{this}}");
        Assert.assertEquals("Hex-Value is 10", t.apply(16));
    }
}

