package com.shekhargulati.ninetynine_problems._02_arithmetic;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P40Test {
    @Test
    public void phiOf10Is4() throws Exception {
        int p = P40.phi(10);
        Assert.assertThat(p, CoreMatchers.equalTo(4));
    }

    @Test
    public void phiOf99Is60() throws Exception {
        int p = P40.phi(99);
        Assert.assertThat(p, CoreMatchers.equalTo(60));
    }
}

