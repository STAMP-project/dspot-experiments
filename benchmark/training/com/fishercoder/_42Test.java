package com.fishercoder;


import _42.Solution1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/13/17.
 */
public class _42Test {
    private static Solution1 solution1;

    private static int[] height;

    @Test
    public void test1() {
        _42Test.height = new int[]{ 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 };
        Assert.assertEquals(6, _42Test.solution1.trap(_42Test.height));
    }
}

