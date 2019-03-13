package com.shekhargulati.ninetynine_problems._02_arithmetic;


import org.junit.Assert;
import org.junit.Test;


public class P31Test {
    @Test
    public void shouldSay7IsAPrimeNumber() throws Exception {
        boolean prime = P31.isPrime(7);
        Assert.assertTrue(prime);
    }

    @Test
    public void shouldSay10IsNotAPrimeNumber() throws Exception {
        boolean prime = P31.isPrime(10);
        Assert.assertFalse(prime);
    }
}

