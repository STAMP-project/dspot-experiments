package com.baeldung.guava;


import org.junit.Assert;
import org.junit.Test;


public class CharMatcherUnitTest {
    @Test
    public void whenMatchingLetterOrString_ShouldReturnTrueForCorrectString() throws Exception {
        String inputString = "someString789";
        boolean result = javaLetterOrDigit().matchesAllOf(inputString);
        Assert.assertTrue(result);
    }

    @Test
    public void whenCollapsingString_ShouldReturnStringWithDashesInsteadOfWhitespaces() throws Exception {
        String inputPhoneNumber = "8 123 456 123";
        String result = whitespace().collapseFrom(inputPhoneNumber, '-');
        Assert.assertEquals("8-123-456-123", result);
    }

    @Test
    public void whenCountingDigitsInString_ShouldReturnActualCountOfDigits() throws Exception {
        String inputPhoneNumber = "8 123 456 123";
        int result = digit().countIn(inputPhoneNumber);
        Assert.assertEquals(10, result);
    }
}

