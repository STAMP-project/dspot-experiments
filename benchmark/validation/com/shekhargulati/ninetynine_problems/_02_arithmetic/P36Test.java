package com.shekhargulati.ninetynine_problems._02_arithmetic;


import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


public class P36Test {
    @Test
    public void shouldProduceAListOfGoldbachCompositions() throws Exception {
        List<AbstractMap.SimpleEntry<Integer, List<Integer>>> compositions = P36.goldbach_list(IntStream.rangeClosed(9, 20));
        Assert.assertThat(compositions, hasSize(6));
        Assert.assertThat(compositions, IsCollectionContaining.hasItems(new AbstractMap.SimpleEntry<>(10, Arrays.asList(3, 7)), new AbstractMap.SimpleEntry<>(12, Arrays.asList(5, 7)), new AbstractMap.SimpleEntry<>(14, Arrays.asList(3, 11)), new AbstractMap.SimpleEntry<>(16, Arrays.asList(3, 13)), new AbstractMap.SimpleEntry<>(18, Arrays.asList(5, 13)), new AbstractMap.SimpleEntry<>(20, Arrays.asList(3, 17))));
    }

    @Test
    public void shouldProduceAListOfGoldbachCompositionsWhereBothPrimeNumbersAreGreaterThan50() throws Exception {
        List<AbstractMap.SimpleEntry<Integer, List<Integer>>> compositions = P36.goldbach_list1(IntStream.rangeClosed(1, 2000), 50);
        Assert.assertThat(compositions, hasSize(4));
        Assert.assertThat(compositions, IsCollectionContaining.hasItems(new AbstractMap.SimpleEntry<>(992, Arrays.asList(73, 919)), new AbstractMap.SimpleEntry<>(1382, Arrays.asList(61, 1321)), new AbstractMap.SimpleEntry<>(1856, Arrays.asList(67, 1789)), new AbstractMap.SimpleEntry<>(1928, Arrays.asList(61, 1867))));
    }
}

