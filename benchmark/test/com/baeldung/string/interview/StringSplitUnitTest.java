package com.baeldung.string.interview;


import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class StringSplitUnitTest {
    @Test
    public void givenCoreJava_whenSplittingStrings_thenSplitted() {
        String[] expected = new String[]{ "john", "peter", "mary" };
        String[] splitted = "john,peter,mary".split(",");
        Assert.assertArrayEquals(expected, splitted);
    }

    @Test
    public void givenApacheCommons_whenSplittingStrings_thenSplitted() {
        String[] expected = new String[]{ "john", "peter", "mary" };
        String[] splitted = StringUtils.split("john peter mary");
        Assert.assertArrayEquals(expected, splitted);
    }
}

