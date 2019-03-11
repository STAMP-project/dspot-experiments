package com.github.jknack.handlebars;


import java.io.IOException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class FormatterTest extends AbstractTest {
    static final long now = System.currentTimeMillis();

    @Test
    public void useFormatterTwice() throws IOException {
        Template t = compile("time is {{this}}/{{this}}");
        Assert.assertEquals(((("time is " + (FormatterTest.now)) + "/") + (FormatterTest.now)), t.apply(new Date(FormatterTest.now)));
    }

    @Test
    public void testFormatterWithoutMatch() throws IOException {
        Template t = compile("string is {{this}}");
        Assert.assertEquals("string is testvalue", t.apply("testvalue"));
    }

    @Test
    public void useTemplateTwice() throws IOException {
        Template t = compile("time is {{this}}");
        Assert.assertEquals(("time is " + (FormatterTest.now)), t.apply(new Date(FormatterTest.now)));
        Assert.assertEquals(("time is " + (FormatterTest.now)), t.apply(new Date(FormatterTest.now)));
    }
}

