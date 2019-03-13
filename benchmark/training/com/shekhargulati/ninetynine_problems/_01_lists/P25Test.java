package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class P25Test {
    @Test
    public void shouldGenerateRandomPermutationOfElementsOfAList() throws Exception {
        List<String> permutation = P25.randomPermutation(Stream.of("a", "b", "c", "d", "e", "f").collect(Collectors.toList()));
        System.out.println(permutation);
        Assert.assertThat(permutation, hasSize(6));
        Assert.assertThat(permutation, containsInAnyOrder("a", "b", "c", "d", "e", "f"));
    }
}

