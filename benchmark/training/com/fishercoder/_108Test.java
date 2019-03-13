package com.fishercoder;


import _108.Solution1;
import com.fishercoder.common.utils.TreeUtils;
import org.junit.Test;


public class _108Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _108Test.nums = new int[]{ 1, 2, 3 };
        TreeUtils.printBinaryTree(_108Test.solution1.sortedArrayToBST(_108Test.nums));
    }

    @Test
    public void test2() {
        _108Test.nums = new int[]{  };
        TreeUtils.printBinaryTree(_108Test.solution1.sortedArrayToBST(_108Test.nums));
    }
}

