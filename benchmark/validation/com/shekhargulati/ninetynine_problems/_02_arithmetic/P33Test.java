package com.shekhargulati.ninetynine_problems._02_arithmetic;


import java.util.AbstractMap;
import java.util.List;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


public class P33Test {
    @Test
    public void shouldFindPrimeFactorsOf315() throws Exception {
        List<AbstractMap.SimpleEntry<Integer, Integer>> primeFactors = P33.primeFactorsMult(315);
        Assert.assertThat(primeFactors, IsCollectionContaining.hasItems(new AbstractMap.SimpleEntry<>(3, 2), new AbstractMap.SimpleEntry<>(5, 1), new AbstractMap.SimpleEntry<>(7, 1)));
    }

    @Test
    public void shouldFindPrimeFactorsOf33() throws Exception {
        List<AbstractMap.SimpleEntry<Integer, Integer>> primeFactors = P33.primeFactorsMult(33);
        Assert.assertThat(primeFactors, IsCollectionContaining.hasItems(new AbstractMap.SimpleEntry<>(3, 1), new AbstractMap.SimpleEntry<>(11, 1)));
    }
}

