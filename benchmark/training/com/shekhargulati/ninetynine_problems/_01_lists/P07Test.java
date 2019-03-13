package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P07Test {
    @Test
    public void shouldFlattenAListOfList() throws Exception {
        List<String> flatten = P07.flatten(Arrays.asList("a", Arrays.asList("b", Arrays.asList("c", "d")), "e"), String.class);
        Assert.assertThat(flatten, hasSize(5));
        Assert.assertThat(flatten, CoreMatchers.hasItems("a", "b", "c", "d", "e"));
    }

    @Test
    public void shouldFlattenDeepNestedLists() throws Exception {
        List<String> flatten = P07.flatten(Arrays.asList("a", Arrays.asList("b", Arrays.asList("c", Arrays.asList("d", "e", Arrays.asList("f", "g"))), "h")), String.class);
        Assert.assertThat(flatten, hasSize(8));
        Assert.assertThat(flatten, CoreMatchers.hasItems("a", "b", "c", "d", "e", "f", "g", "h"));
    }

    @Test
    public void shouldReturnEmptyListWhenTryingToFlattenAnEmptyList() throws Exception {
        List<String> flatten = P07.flatten(Collections.emptyList(), String.class);
        Assert.assertTrue(flatten.isEmpty());
    }

    @Test
    public void shouldReturnSameListWhenListHasNoNestedList() throws Exception {
        List<String> flatten = P07.flatten(Arrays.asList("a", "b", "c", "d", "e"), String.class);
        Assert.assertThat(flatten, hasSize(5));
        Assert.assertThat(flatten, CoreMatchers.hasItems("a", "b", "c", "d", "e"));
    }

    @Test
    public void shouldFlattenAListOfList_stream() throws Exception {
        List<String> flatten = P07.flatten_stream(Arrays.asList("a", Arrays.asList("b", Arrays.asList("c", "d")), "e"), String.class);
        Assert.assertThat(flatten, hasSize(5));
        Assert.assertThat(flatten, CoreMatchers.hasItems("a", "b", "c", "d", "e"));
    }

    @Test
    public void shouldFlattenDeepNestedLists_stream() throws Exception {
        List<String> flatten = P07.flatten_stream(Arrays.asList("a", Arrays.asList("b", Arrays.asList("c", Arrays.asList("d", "e", Arrays.asList("f", "g"))), "h")), String.class);
        Assert.assertThat(flatten, hasSize(8));
        Assert.assertThat(flatten, CoreMatchers.hasItems("a", "b", "c", "d", "e", "f", "g", "h"));
    }
}

