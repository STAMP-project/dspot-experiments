package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.LinkedList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P01Test {
    @Test
    public void shouldFindLastElementFromAListOfAlphabets() throws Exception {
        Assert.assertThat(P01.last(Arrays.asList("a", "b", "c", "d")), CoreMatchers.is(CoreMatchers.equalTo("d")));
    }

    @Test
    public void shouldFindLastElementFromALinkedListOfAlphabets() throws Exception {
        LinkedList<String> alphabets = CollectionUtils.linkedList("a", "b", "c", "d");
        Assert.assertThat(P01.last(alphabets), CoreMatchers.is(CoreMatchers.equalTo("d")));
    }

    @Test
    public void shouldFindLastElementFromAListOfAlphabetsUsingRecursion() throws Exception {
        Assert.assertThat(P01.lastRecursive(Arrays.asList("a", "b", "c", "d")), CoreMatchers.is(CoreMatchers.equalTo("d")));
    }
}

