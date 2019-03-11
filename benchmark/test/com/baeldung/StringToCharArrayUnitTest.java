package com.baeldung;


import org.junit.Assert;
import org.junit.Test;


public class StringToCharArrayUnitTest {
    @Test
    public void givenString_whenCallingStringToCharArray_shouldConvertToCharArray() {
        String givenString = "characters";
        char[] result = givenString.toCharArray();
        char[] expectedCharArray = new char[]{ 'c', 'h', 'a', 'r', 'a', 'c', 't', 'e', 'r', 's' };
        Assert.assertArrayEquals(expectedCharArray, result);
    }
}

