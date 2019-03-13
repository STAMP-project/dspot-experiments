package com.shekhargulati.ninetynine_problems._02_arithmetic;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P39Test {
    @Test
    public void shouldSayPhiOf10Is4() throws Exception {
        long phi = P39.phi(10);
        Assert.assertThat(phi, CoreMatchers.equalTo(4L));
    }
}

