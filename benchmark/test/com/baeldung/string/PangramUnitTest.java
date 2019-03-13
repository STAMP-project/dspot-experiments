package com.baeldung.string;


import org.junit.Assert;
import org.junit.Test;


public class PangramUnitTest {
    @Test
    public void givenValidString_isPangram_shouldReturnSuccess() {
        String input = "Two driven jocks help fax my big quiz";
        Assert.assertTrue(Pangram.isPangram(input));
        Assert.assertTrue(Pangram.isPangramWithStreams(input));
    }

    @Test
    public void givenNullString_isPangram_shouldReturnFailure() {
        String input = null;
        Assert.assertFalse(Pangram.isPangram(input));
        Assert.assertFalse(Pangram.isPangramWithStreams(input));
        Assert.assertFalse(Pangram.isPerfectPangram(input));
    }

    @Test
    public void givenPerfectPangramString_isPerfectPangram_shouldReturnSuccess() {
        String input = "abcdefghijklmNoPqrStuVwxyz";
        Assert.assertTrue(Pangram.isPerfectPangram(input));
    }

    @Test
    public void givenNonPangramString_isPangram_shouldReturnFailure() {
        String input = "invalid pangram";
        Assert.assertFalse(Pangram.isPangram(input));
        Assert.assertFalse(Pangram.isPangramWithStreams(input));
    }

    @Test
    public void givenPangram_isPerfectPangram_shouldReturnFailure() {
        String input = "Two driven jocks help fax my big quiz";
        Assert.assertFalse(Pangram.isPerfectPangram(input));
    }
}

