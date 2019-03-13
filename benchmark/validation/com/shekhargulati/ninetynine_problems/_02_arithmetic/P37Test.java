package com.shekhargulati.ninetynine_problems._02_arithmetic;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P37Test {
    @Test
    public void gcdOf36And63Is9() throws Exception {
        int gcd = P37.gcd(36, 63);
        Assert.assertThat(gcd, CoreMatchers.equalTo(9));
    }
}

