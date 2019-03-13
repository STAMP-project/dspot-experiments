package com.baeldung.combiningcollections;


import org.junit.Assert;
import org.junit.Test;


public class CombiningArraysUnitTest {
    private static final String[] first = new String[]{ "One", "Two", "Three" };

    private static final String[] second = new String[]{ "Four", "Five", "Six" };

    private static final String[] expected = new String[]{ "One", "Two", "Three", "Four", "Five", "Six" };

    @Test
    public void givenTwoArrays_whenUsingNativeJava_thenArraysCombined() {
        Assert.assertArrayEquals(CombiningArraysUnitTest.expected, CombiningArrays.usingNativeJava(CombiningArraysUnitTest.first, CombiningArraysUnitTest.second));
    }

    @Test
    public void givenTwoArrays_whenUsingObjectStreams_thenArraysCombined() {
        Assert.assertArrayEquals(CombiningArraysUnitTest.expected, CombiningArrays.usingJava8ObjectStream(CombiningArraysUnitTest.first, CombiningArraysUnitTest.second));
    }

    @Test
    public void givenTwoArrays_whenUsingFlatMaps_thenArraysCombined() {
        Assert.assertArrayEquals(CombiningArraysUnitTest.expected, CombiningArrays.usingJava8FlatMaps(CombiningArraysUnitTest.first, CombiningArraysUnitTest.second));
    }

    @Test
    public void givenTwoArrays_whenUsingApacheCommons_thenArraysCombined() {
        Assert.assertArrayEquals(CombiningArraysUnitTest.expected, CombiningArrays.usingApacheCommons(CombiningArraysUnitTest.first, CombiningArraysUnitTest.second));
    }

    @Test
    public void givenTwoArrays_whenUsingGuava_thenArraysCombined() {
        Assert.assertArrayEquals(CombiningArraysUnitTest.expected, CombiningArrays.usingGuava(CombiningArraysUnitTest.first, CombiningArraysUnitTest.second));
    }
}

