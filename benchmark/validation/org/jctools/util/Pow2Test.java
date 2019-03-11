package org.jctools.util;


import org.junit.Assert;
import org.junit.Test;


public class Pow2Test {
    static final int MAX_POSITIVE_POW2 = 1 << 30;

    @Test
    public void testAlign() {
        Assert.assertEquals(4, Pow2.align(2, 4));
        Assert.assertEquals(4, Pow2.align(4, 4));
    }

    @Test
    public void testRound() {
        Assert.assertEquals(4, Pow2.roundToPowerOfTwo(4));
        Assert.assertEquals(4, Pow2.roundToPowerOfTwo(3));
        Assert.assertEquals(1, Pow2.roundToPowerOfTwo(0));
        Assert.assertEquals(Pow2Test.MAX_POSITIVE_POW2, Pow2.roundToPowerOfTwo(Pow2Test.MAX_POSITIVE_POW2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxRoundException() {
        Pow2.roundToPowerOfTwo(((Pow2Test.MAX_POSITIVE_POW2) + 1));
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRoundException() {
        Pow2.roundToPowerOfTwo((-1));
        Assert.fail();
    }
}

