package com.shekhargulati.ninetynine_problems._02_arithmetic;


import java.util.List;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


public class P32Test {
    @Test
    public void shouldFindPrimeFactorsOf315() throws Exception {
        List<Integer> primeFactors = P32.primeFactors(315);
        Assert.assertThat(primeFactors, IsCollectionContaining.hasItems(3, 3, 5, 7));
    }

    @Test
    public void shouldFindPrimeFactorsOf33() throws Exception {
        List<Integer> primeFactors = P32.primeFactors(33);
        Assert.assertThat(primeFactors, IsCollectionContaining.hasItems(3, 11));
    }
}

