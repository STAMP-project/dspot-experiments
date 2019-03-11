package com.baeldung.algorithms.primechecker;


import org.junit.Assert;
import org.junit.Test;


public class PrimeCheckerUnitTest {
    private final BigIntegerPrimeChecker primeChecker = new BigIntegerPrimeChecker();

    @Test
    public void whenCheckIsPrime_thenTrue() {
        Assert.assertTrue(primeChecker.isPrime(13L));
        Assert.assertTrue(primeChecker.isPrime(1009L));
        Assert.assertTrue(primeChecker.isPrime(74207281L));
    }

    @Test
    public void whenCheckIsPrime_thenFalse() {
        Assert.assertTrue((!(primeChecker.isPrime(50L))));
        Assert.assertTrue((!(primeChecker.isPrime(1001L))));
        Assert.assertTrue((!(primeChecker.isPrime(74207282L))));
    }

    private final BruteForcePrimeChecker bfPrimeChecker = new BruteForcePrimeChecker();

    @Test
    public void whenBFCheckIsPrime_thenTrue() {
        Assert.assertTrue(bfPrimeChecker.isPrime(13));
        Assert.assertTrue(bfPrimeChecker.isPrime(1009));
    }

    @Test
    public void whenBFCheckIsPrime_thenFalse() {
        Assert.assertFalse(bfPrimeChecker.isPrime(50));
        Assert.assertFalse(bfPrimeChecker.isPrime(1001));
    }

    private final OptimisedPrimeChecker optimisedPrimeChecker = new OptimisedPrimeChecker();

    @Test
    public void whenOptCheckIsPrime_thenTrue() {
        Assert.assertTrue(optimisedPrimeChecker.isPrime(13));
        Assert.assertTrue(optimisedPrimeChecker.isPrime(1009));
    }

    @Test
    public void whenOptCheckIsPrime_thenFalse() {
        Assert.assertFalse(optimisedPrimeChecker.isPrime(50));
        Assert.assertFalse(optimisedPrimeChecker.isPrime(1001));
    }

    private final PrimesPrimeChecker primesPrimeChecker = new PrimesPrimeChecker();

    @Test
    public void whenPrimesCheckIsPrime_thenTrue() {
        Assert.assertTrue(primesPrimeChecker.isPrime(13));
        Assert.assertTrue(primesPrimeChecker.isPrime(1009));
    }

    @Test
    public void whenPrimesCheckIsPrime_thenFalse() {
        Assert.assertFalse(primesPrimeChecker.isPrime(50));
        Assert.assertFalse(primesPrimeChecker.isPrime(1001));
    }
}

