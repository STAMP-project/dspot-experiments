package org.springframework.web.util;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.NestedExceptionUtils;


public class NestedServletExceptionTests {
    @Test
    public void testNestedServletExceptionString() {
        NestedServletException exception = new NestedServletException("foo");
        Assert.assertEquals("foo", exception.getMessage());
    }

    @Test
    public void testNestedServletExceptionStringThrowable() {
        Throwable cause = new RuntimeException();
        NestedServletException exception = new NestedServletException("foo", cause);
        Assert.assertEquals(NestedExceptionUtils.buildMessage("foo", cause), exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testNestedServletExceptionStringNullThrowable() {
        // This can happen if someone is sloppy with Throwable causes...
        NestedServletException exception = new NestedServletException("foo", null);
        Assert.assertEquals("foo", exception.getMessage());
    }
}

