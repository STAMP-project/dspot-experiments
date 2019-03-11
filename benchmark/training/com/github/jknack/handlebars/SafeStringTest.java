package com.github.jknack.handlebars;


import org.junit.Assert;
import org.junit.Test;


public class SafeStringTest {
    @Test
    public void equals() {
        Assert.assertEquals(new Handlebars.SafeString("hello"), new Handlebars.SafeString("hello"));
    }

    @Test
    public void notEquals() {
        Assert.assertNotSame(new Handlebars.SafeString("hello"), new Handlebars.SafeString("hello!"));
    }

    @Test
    public void hashcode() {
        Assert.assertEquals(new Handlebars.SafeString("hello").hashCode(), new Handlebars.SafeString("hello").hashCode());
    }

    @Test
    public void length() {
        Assert.assertEquals(5, new Handlebars.SafeString("hello").length());
    }

    @Test
    public void charAt() {
        Assert.assertEquals('e', new Handlebars.SafeString("hello").charAt(1));
    }

    @Test
    public void substring() {
        Assert.assertEquals("el", new Handlebars.SafeString("hello").subSequence(1, 3));
    }
}

