package com.baeldung.prime;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class PrimeGeneratorUnitTest {
    @Test
    public void whenBruteForced_returnsSuccessfully() {
        final List<Integer> primeNumbers = primeNumbersBruteForce(20);
        Assert.assertEquals(Arrays.asList(new Integer[]{ 2, 3, 5, 7, 11, 13, 17, 19 }), primeNumbers);
    }

    @Test
    public void whenOptimized_returnsSuccessfully() {
        final List<Integer> primeNumbers = primeNumbersTill(20);
        Assert.assertEquals(Arrays.asList(new Integer[]{ 2, 3, 5, 7, 11, 13, 17, 19 }), primeNumbers);
    }

    @Test
    public void whenSieveOfEratosthenes_returnsSuccessfully() {
        final List<Integer> primeNumbers = sieveOfEratosthenes(20);
        Assert.assertEquals(Arrays.asList(new Integer[]{ 2, 3, 5, 7, 11, 13, 17, 19 }), primeNumbers);
    }
}

