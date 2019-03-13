package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class P26Test {
    @Test
    public void shouldFindAllCombinationsOfSize2FromAListWithSize3() throws Exception {
        List<String> input = Stream.of("a", "b", "c").collect(Collectors.toList());
        List<List<String>> combinations = P26.combinations(input, 2);
        Assert.assertThat(combinations, hasSize(3));
    }

    @Test
    public void shouldFindAllCombinationsOfSize3FromAListWithSize6() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d", "e", "f").collect(Collectors.toList());
        List<List<String>> combinations = P26.combinations(input, 3);
        Assert.assertThat(combinations, hasSize(20));
    }

    @Test
    public void shouldFindAllCombinationsOfSize4FromAListWithSize6() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d", "e", "f").collect(Collectors.toList());
        List<List<String>> combinations = P26.combinations(input, 4);
        Assert.assertThat(combinations, hasSize(15));
    }
}

