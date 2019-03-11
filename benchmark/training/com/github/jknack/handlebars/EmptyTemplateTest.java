package com.github.jknack.handlebars;


import Template.EMPTY;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;


public class EmptyTemplateTest {
    @Test
    public void text() {
        Assert.assertEquals("", EMPTY.text());
    }

    @Test
    public void apply() throws IOException {
        Assert.assertEquals("", EMPTY.apply(((Object) (null))));
        Assert.assertEquals("", EMPTY.apply(((Context) (null))));
    }

    @Test
    public void applyWithWriter() throws IOException {
        EMPTY.apply(((Object) (null)), null);
        EMPTY.apply(((Context) (null)), null);
    }

    @Test
    public void toJs() throws IOException {
        Assert.assertEquals("", EMPTY.toJavaScript());
    }

    @Test
    public void typeSafeTemplate() throws IOException {
        TypeSafeTemplate<Object> ts = EMPTY.as();
        Assert.assertNotNull(ts);
        Assert.assertEquals("", ts.apply(null));
        StringWriter writer = new StringWriter();
        ts.apply(null, writer);
        Assert.assertEquals("", writer.toString());
    }

    @Test
    public void collect() throws IOException {
        Assert.assertNotNull(EMPTY.collect());
        Assert.assertEquals(0, EMPTY.collect().size());
    }
}

