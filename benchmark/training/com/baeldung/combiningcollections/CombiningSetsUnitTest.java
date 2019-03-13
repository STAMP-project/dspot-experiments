package com.baeldung.combiningcollections;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CombiningSetsUnitTest {
    private static final Set<Object> first = new HashSet<Object>(Arrays.asList(new Object[]{ "One", "Two", "Three" }));

    private static final Set<Object> second = new HashSet<Object>(Arrays.asList(new Object[]{ "Four", "Five", "Six" }));

    private static final Set<Object> expected = new HashSet<Object>(Arrays.asList(new Object[]{ "One", "Two", "Three", "Four", "Five", "Six" }));

    @Test
    public void givenTwoSets_whenUsingNativeJava_thenArraysCombined() {
        Assert.assertThat(CombiningSets.usingNativeJava(CombiningSetsUnitTest.first, CombiningSetsUnitTest.second), CoreMatchers.is(CombiningSetsUnitTest.expected));
    }

    @Test
    public void givenTwoSets_whenUsingObjectStreams_thenArraysCombined() {
        Assert.assertThat(CombiningSets.usingJava8ObjectStream(CombiningSetsUnitTest.first, CombiningSetsUnitTest.second), CoreMatchers.is(CombiningSetsUnitTest.expected));
    }

    @Test
    public void givenTwoSets_whenUsingFlatMaps_thenArraysCombined() {
        Assert.assertThat(CombiningSets.usingJava8FlatMaps(CombiningSetsUnitTest.first, CombiningSetsUnitTest.second), CoreMatchers.is(CombiningSetsUnitTest.expected));
    }

    @Test
    public void givenTwoSets_whenUsingApacheCommons_thenArraysCombined() {
        Assert.assertThat(CombiningSets.usingApacheCommons(CombiningSetsUnitTest.first, CombiningSetsUnitTest.second), CoreMatchers.is(CombiningSetsUnitTest.expected));
    }

    @Test
    public void givenTwoSets_whenUsingGuava_thenArraysCombined() {
        Assert.assertThat(CombiningSets.usingGuava(CombiningSetsUnitTest.first, CombiningSetsUnitTest.second), CoreMatchers.is(CombiningSetsUnitTest.expected));
    }
}

