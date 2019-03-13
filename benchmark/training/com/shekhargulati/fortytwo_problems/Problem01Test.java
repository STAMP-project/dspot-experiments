package com.shekhargulati.fortytwo_problems;


import java.util.AbstractMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class Problem01Test {
    @Test
    public void shouldCreatePairsByMinOfMaximumSumOfPairs() throws Exception {
        List<AbstractMap.SimpleEntry<Integer, Integer>> pairs = Problem01.pairs(new int[]{ 1, 9, 3, 5 });
        Assert.assertThat(pairs, contains(new AbstractMap.SimpleEntry(1, 9), new AbstractMap.SimpleEntry(3, 5)));
    }
}

