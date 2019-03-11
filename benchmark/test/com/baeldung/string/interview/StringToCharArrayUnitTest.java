package com.baeldung.string.interview;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class StringToCharArrayUnitTest {
    @Test
    public void whenConvertingStringToCharArray_thenConversionSuccessful() {
        String beforeConvStr = "hello";
        char[] afterConvCharArr = new char[]{ 'h', 'e', 'l', 'l', 'o' };
        Assert.assertEquals(Arrays.equals(beforeConvStr.toCharArray(), afterConvCharArr), true);
    }
}

