package com.fishercoder;


import _703.Solution1.KthLargest;
import junit.framework.Assert;
import org.junit.Test;


public class _703Test {
    private static KthLargest solution1;

    private static int[] A;

    @Test
    public void test1() {
        _703Test.solution1 = new KthLargest(3, new int[]{ 4, 5, 8, 2 });
        Assert.assertEquals(4, _703Test.solution1.add(3));
        Assert.assertEquals(5, _703Test.solution1.add(5));
        Assert.assertEquals(5, _703Test.solution1.add(10));
        Assert.assertEquals(8, _703Test.solution1.add(9));
        Assert.assertEquals(8, _703Test.solution1.add(4));
    }
}

