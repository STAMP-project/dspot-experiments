package com.baeldung.combiningcollections;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CombiningListsUnitTest {
    private static final List<Object> first = Arrays.asList(new Object[]{ "One", "Two", "Three" });

    private static final List<Object> second = Arrays.asList(new Object[]{ "Four", "Five", "Six" });

    private static final List<Object> expected = Arrays.asList(new Object[]{ "One", "Two", "Three", "Four", "Five", "Six" });

    @Test
    public void givenTwoLists_whenUsingNativeJava_thenArraysCombined() {
        Assert.assertThat(CombiningLists.usingNativeJava(CombiningListsUnitTest.first, CombiningListsUnitTest.second), CoreMatchers.is(CombiningListsUnitTest.expected));
    }

    @Test
    public void givenTwoLists_whenUsingObjectStreams_thenArraysCombined() {
        Assert.assertThat(CombiningLists.usingJava8ObjectStream(CombiningListsUnitTest.first, CombiningListsUnitTest.second), CoreMatchers.is(CombiningListsUnitTest.expected));
    }

    @Test
    public void givenTwoLists_whenUsingFlatMaps_thenArraysCombined() {
        Assert.assertThat(CombiningLists.usingJava8FlatMaps(CombiningListsUnitTest.first, CombiningListsUnitTest.second), CoreMatchers.is(CombiningListsUnitTest.expected));
    }

    @Test
    public void givenTwoLists_whenUsingApacheCommons_thenArraysCombined() {
        Assert.assertThat(CombiningLists.usingApacheCommons(CombiningListsUnitTest.first, CombiningListsUnitTest.second), CoreMatchers.is(CombiningListsUnitTest.expected));
    }

    @Test
    public void givenTwoLists_whenUsingGuava_thenArraysCombined() {
        Assert.assertThat(CombiningLists.usingGuava(CombiningListsUnitTest.first, CombiningListsUnitTest.second), CoreMatchers.is(CombiningListsUnitTest.expected));
    }
}

