package com.baeldung.combiningcollections;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CombiningMapsUnitTest {
    private static final Map<String, String> first = new HashMap<>();

    private static final Map<String, String> second = new HashMap<>();

    private static Map<String, String> expected = new HashMap<>();

    static {
        CombiningMapsUnitTest.first.put("one", "first String");
        CombiningMapsUnitTest.first.put("two", "second String");
        CombiningMapsUnitTest.second.put("three", "third String");
        CombiningMapsUnitTest.second.put("four", "fourth String");
        CombiningMapsUnitTest.expected.put("one", "first String");
        CombiningMapsUnitTest.expected.put("two", "second String");
        CombiningMapsUnitTest.expected.put("three", "third String");
        CombiningMapsUnitTest.expected.put("four", "fourth String");
    }

    @Test
    public void givenTwoMaps_whenUsingNativeJava_thenMapsCombined() {
        Assert.assertThat(CombiningMaps.usingPlainJava(CombiningMapsUnitTest.first, CombiningMapsUnitTest.second), CoreMatchers.is(CombiningMapsUnitTest.expected));
    }

    @Test
    public void givenTwoMaps_whenUsingForEach_thenMapsCombined() {
        Assert.assertThat(CombiningMaps.usingJava8ForEach(CombiningMapsUnitTest.first, CombiningMapsUnitTest.second), CoreMatchers.is(CombiningMapsUnitTest.expected));
    }

    @Test
    public void givenTwoMaps_whenUsingFlatMaps_thenMapsCombined() {
        Assert.assertThat(CombiningMaps.usingJava8FlatMaps(CombiningMapsUnitTest.first, CombiningMapsUnitTest.second), CoreMatchers.is(CombiningMapsUnitTest.expected));
    }

    @Test
    public void givenTwoMaps_whenUsingApacheCommons_thenMapsCombined() {
        Assert.assertThat(CombiningMaps.usingApacheCommons(CombiningMapsUnitTest.first, CombiningMapsUnitTest.second), CoreMatchers.is(CombiningMapsUnitTest.expected));
    }

    @Test
    public void givenTwoMaps_whenUsingGuava_thenMapsCombined() {
        Assert.assertThat(CombiningMaps.usingGuava(CombiningMapsUnitTest.first, CombiningMapsUnitTest.second), CoreMatchers.is(CombiningMapsUnitTest.expected));
    }
}

