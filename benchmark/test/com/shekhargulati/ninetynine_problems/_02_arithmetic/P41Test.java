package com.shekhargulati.ninetynine_problems._02_arithmetic;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P41Test {
    @Test
    public void shouldCalculatePhiOf10090UsingP39() throws Exception {
        long p = P39.phi(10090);
        Assert.assertThat(p, CoreMatchers.equalTo(4032L));
    }

    @Test
    public void shouldCalculatePhiOf10090UsingP40() throws Exception {
        long p = P40.phi(10090);
        Assert.assertThat(p, CoreMatchers.equalTo(4032L));
    }
}

