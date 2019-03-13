package com.shekhargulati.ninetynine_problems._02_arithmetic;


import org.junit.Assert;
import org.junit.Test;


public class P38Test {
    @Test
    public void shouldSay35And64IsCoprime() throws Exception {
        boolean coprime = P38.coprime(35, 64);
        Assert.assertTrue(coprime);
    }
}

