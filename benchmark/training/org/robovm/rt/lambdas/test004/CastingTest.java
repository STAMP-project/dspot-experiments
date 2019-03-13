package org.robovm.rt.lambdas.test004;


import org.junit.Assert;
import org.junit.Test;


public class CastingTest {
    @Test
    public void test004() {
        Assert.assertEquals(10, ((I) (() -> 10)).add());
    }
}

