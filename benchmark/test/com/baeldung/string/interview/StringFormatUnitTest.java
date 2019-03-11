package com.baeldung.string.interview;


import org.junit.Assert;
import org.junit.Test;


public class StringFormatUnitTest {
    @Test
    public void givenString_whenUsingStringFormat_thenStringFormatted() {
        String title = "Baeldung";
        String formatted = String.format("Title is %s", title);
        Assert.assertEquals(formatted, "Title is Baeldung");
    }
}

