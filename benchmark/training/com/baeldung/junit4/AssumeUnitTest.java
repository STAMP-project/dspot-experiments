package com.baeldung.junit4;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class AssumeUnitTest {
    @Test
    public void trueAssumption() {
        Assume.assumeTrue("5 is greater the 1", (5 > 1));
        Assert.assertEquals((5 + 2), 7);
    }

    @Test
    public void falseAssumption() {
        Assume.assumeFalse("5 is less then 1", (5 < 1));
        Assert.assertEquals((5 + 2), 7);
    }
}

