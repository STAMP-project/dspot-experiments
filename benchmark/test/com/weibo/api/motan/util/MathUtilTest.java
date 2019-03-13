package com.weibo.api.motan.util;


import org.junit.Assert;
import org.junit.Test;


public class MathUtilTest {
    @Test
    public void parseInt() {
        Assert.assertTrue(((MathUtil.parseInt("-1", 0)) == (-1)));
        Assert.assertTrue(((MathUtil.parseInt("0", (-1))) == 0));
        Assert.assertTrue(((MathUtil.parseInt("1", 0)) == 1));
        Assert.assertTrue(((MathUtil.parseInt("", 0)) == 0));
        Assert.assertTrue(((MathUtil.parseInt(null, 0)) == 0));
        Assert.assertTrue(((MathUtil.parseInt("Invalid Int String", 0)) == 0));
    }

    @Test
    public void parseLong() {
        Assert.assertTrue(((MathUtil.parseLong("-1", 0)) == (-1)));
        Assert.assertTrue(((MathUtil.parseLong("0", (-1))) == 0));
        Assert.assertTrue(((MathUtil.parseLong("1", 0)) == 1));
        Assert.assertTrue(((MathUtil.parseLong("", 0)) == 0));
        Assert.assertTrue(((MathUtil.parseLong(null, 0)) == 0));
        Assert.assertTrue(((MathUtil.parseLong("Invalid Int String", 0)) == 0));
    }

    @Test
    public void getNonNegative() {
        Assert.assertTrue(((MathUtil.getNonNegative(Integer.MIN_VALUE)) == 0));
        Assert.assertTrue(((MathUtil.getNonNegative((-1))) > 0));
        Assert.assertTrue(((MathUtil.getNonNegative(0)) == 0));
        Assert.assertTrue(((MathUtil.getNonNegative(1)) == 1));
        Assert.assertTrue(((MathUtil.getNonNegative(Integer.MAX_VALUE)) == (Integer.MAX_VALUE)));
    }
}

