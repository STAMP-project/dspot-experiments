package com.fishercoder;


import _769.Solution1;
import _769.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _769Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] arr;

    @Test
    public void test1() {
        _769Test.arr = new int[]{ 4, 3, 2, 1, 0 };
        Assert.assertEquals(1, _769Test.solution1.maxChunksToSorted(_769Test.arr));
        Assert.assertEquals(1, _769Test.solution2.maxChunksToSorted(_769Test.arr));
    }

    @Test
    public void test2() {
        _769Test.arr = new int[]{ 1, 0, 2, 3, 4 };
        Assert.assertEquals(4, _769Test.solution1.maxChunksToSorted(_769Test.arr));
        Assert.assertEquals(4, _769Test.solution2.maxChunksToSorted(_769Test.arr));
    }
}

