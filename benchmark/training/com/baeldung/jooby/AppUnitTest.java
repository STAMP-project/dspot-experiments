package com.baeldung.jooby;


import org.junit.Assert;
import org.junit.Test;


public class AppUnitTest {
    @Test
    public void given_defaultUrl_with_mockrouter_expect_fixedString() throws Throwable {
        String result = get("/");
        Assert.assertEquals("Hello World!", result);
    }
}

