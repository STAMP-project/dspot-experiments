package com.insightfullogic.java8.examples.chapter8;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SingleResponsibilityPrincipleTest {
    private final PrimeCounter primeCounter;

    public SingleResponsibilityPrincipleTest(PrimeCounter primeCounter) {
        this.primeCounter = primeCounter;
    }

    @Test
    public void countsPrimesTo10() {
        Assert.assertEquals(5, primeCounter.countPrimes(10));
        Assert.assertEquals(9, primeCounter.countPrimes(20));
    }

    @Test
    public void countsPrimesTo20() {
        Assert.assertEquals(9, primeCounter.countPrimes(20));
    }

    @Test
    public void countsPrimesTo30() {
        Assert.assertEquals(11, primeCounter.countPrimes(30));
    }
}

