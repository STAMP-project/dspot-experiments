package com.github.jknack.handlebars;


import org.junit.Assert;
import org.junit.Test;


public class HandlebarsExceptionTest {
    @Test
    public void withCause() {
        Exception cause = new NullPointerException();
        Assert.assertEquals(cause, new HandlebarsException(cause).getCause());
    }

    @Test
    public void withMessageCause() {
        Exception cause = new NullPointerException();
        String message = "message";
        HandlebarsException ex = new HandlebarsException(message, cause);
        Assert.assertEquals(cause, ex.getCause());
        Assert.assertEquals(message, ex.getMessage());
    }

    @Test
    public void withErrorCause() {
        Exception cause = new NullPointerException();
        HandlebarsError error = createMock(HandlebarsError.class);
        HandlebarsException ex = new HandlebarsException(error, cause);
        Assert.assertEquals(cause, ex.getCause());
        Assert.assertEquals(error, ex.getError());
    }

    @Test
    public void withError() {
        HandlebarsError error = createMock(HandlebarsError.class);
        HandlebarsException ex = new HandlebarsException(error);
        Assert.assertEquals(error, ex.getError());
    }
}

