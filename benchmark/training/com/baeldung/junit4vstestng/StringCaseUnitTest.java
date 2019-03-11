package com.baeldung.junit4vstestng;


import org.junit.Assert;
import org.junit.Test;


public class StringCaseUnitTest {
    private static String data;

    @Test
    public void givenString_whenAllCaps_thenCorrect() {
        Assert.assertEquals(StringCaseUnitTest.data.toUpperCase(), StringCaseUnitTest.data);
    }
}

