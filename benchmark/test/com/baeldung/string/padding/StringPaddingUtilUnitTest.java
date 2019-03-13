package com.baeldung.string.padding;


import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class StringPaddingUtilUnitTest {
    String inputString = "123456";

    String expectedPaddedStringSpaces = "    123456";

    String expectedPaddedStringZeros = "0000123456";

    int minPaddedStringLength = 10;

    @Test
    public void givenString_whenPaddingWithSpaces_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringSpaces, StringPaddingUtil.padLeftSpaces(inputString, minPaddedStringLength));
    }

    @Test
    public void givenString_whenPaddingWithSpacesUsingSubstring_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringSpaces, StringPaddingUtil.padLeft(inputString, minPaddedStringLength));
    }

    @Test
    public void givenString_whenPaddingWithZeros_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringZeros, StringPaddingUtil.padLeftZeros(inputString, minPaddedStringLength));
    }

    @Test
    public void givenString_whenPaddingWithSpacesUsingStringUtils_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringSpaces, StringUtils.leftPad(inputString, minPaddedStringLength));
    }

    @Test
    public void givenString_whenPaddingWithZerosUsingStringUtils_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringZeros, StringUtils.leftPad(inputString, minPaddedStringLength, "0"));
    }

    @Test
    public void givenString_whenPaddingWithSpacesUsingGuavaStrings_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringSpaces, Strings.padStart(inputString, minPaddedStringLength, ' '));
    }

    @Test
    public void givenString_whenPaddingWithZerosUsingGuavaStrings_thenStringPaddedMatches() {
        Assert.assertEquals(expectedPaddedStringZeros, Strings.padStart(inputString, minPaddedStringLength, '0'));
    }
}

