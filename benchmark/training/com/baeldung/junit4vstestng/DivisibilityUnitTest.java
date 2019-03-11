package com.baeldung.junit4vstestng;


import org.junit.Assert;
import org.junit.Test;


public class DivisibilityUnitTest {
    private static int number;

    @Test
    public void givenNumber_whenDivisibleByTwo_thenCorrect() {
        Assert.assertEquals(((DivisibilityUnitTest.number) % 2), 0);
    }
}

