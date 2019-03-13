package com.baeldung.string.interview;


import org.junit.Assert;
import org.junit.Test;


public class StringCountOccurrencesUnitTest {
    @Test
    public void givenString_whenCountingFrequencyOfChar_thenCountCorrect() {
        Assert.assertEquals(3, countOccurrences("united states", 't'));
    }
}

