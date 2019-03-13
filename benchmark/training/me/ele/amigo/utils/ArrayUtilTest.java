package me.ele.amigo.utils;


import org.junit.Assert;
import org.junit.Test;


public class ArrayUtilTest {
    Object[] nullArray = null;

    Object[] emptyArray = new Object[0];

    Object[] noneEmptyArray = new Object[10];

    @Test
    public void testIsEmpty() {
        Assert.assertEquals(false, ArrayUtil.isEmpty(noneEmptyArray));
        Assert.assertEquals(true, ArrayUtil.isEmpty(nullArray));
        Assert.assertEquals(true, ArrayUtil.isEmpty(emptyArray));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertEquals(false, ArrayUtil.isNotEmpty(nullArray));
        Assert.assertEquals(false, ArrayUtil.isNotEmpty(emptyArray));
        Assert.assertEquals(true, ArrayUtil.isNotEmpty(noneEmptyArray));
    }

    @Test
    public void testLength() {
        Assert.assertEquals(0, ArrayUtil.length(nullArray));
        Assert.assertEquals(0, ArrayUtil.length(emptyArray));
        Assert.assertEquals(10, ArrayUtil.length(noneEmptyArray));
    }
}

