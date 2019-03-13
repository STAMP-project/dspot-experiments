package com.richpath.util;


import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void testGetDimenFromString() throws Exception {
        Assert.assertEquals(22.0, Utils.getDimenFromString("22dip"), 0);
        Assert.assertEquals(1.5F, Utils.getDimenFromString("1.5dp"), 0);
        Assert.assertEquals(0.7F, Utils.getDimenFromString("0.7sp"), 0);
        Assert.assertEquals(2.0F, Utils.getDimenFromString("2in"), 0);
        Assert.assertEquals(22.0F, Utils.getDimenFromString("22px"), 0);
    }
}

