package com.insightfullogic.java8.examples.chapter5;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class EncounterOrderTest {
    @Test
    public void listToStream() {
        // BEGIN LIST_TO_STREAM
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        List<Integer> sameOrder = numbers.stream().collect(Collectors.toList());
        Assert.assertEquals(numbers, sameOrder);
        // END LIST_TO_STREAM
    }

    @Test
    public void hashSetToStreamSorted() {
        // BEGIN HASHSET_TO_STREAM_SORTED
        Set<Integer> numbers = new HashSet<>(Arrays.asList(4, 3, 2, 1));
        List<Integer> sameOrder = numbers.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), sameOrder);
        // END HASHSET_TO_STREAM_SORTED
    }

    @Test
    public void toStreamMapped() {
        // BEGIN TO_STREAM_MAPPED
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        List<Integer> stillOrdered = numbers.stream().map(( x) -> x + 1).collect(Collectors.toList());
        // Reliable encounter ordering
        Assert.assertEquals(Arrays.asList(2, 3, 4, 5), stillOrdered);
        Set<Integer> unordered = new HashSet<>(numbers);
        List<Integer> stillUnordered = unordered.stream().map(( x) -> x + 1).collect(Collectors.toList());
        // Can't assume encounter ordering
        MatcherAssert.assertThat(stillUnordered, Matchers.hasItem(2));
        MatcherAssert.assertThat(stillUnordered, Matchers.hasItem(3));
        MatcherAssert.assertThat(stillUnordered, Matchers.hasItem(4));
        MatcherAssert.assertThat(stillUnordered, Matchers.hasItem(5));
        // END TO_STREAM_MAPPED
    }
}

