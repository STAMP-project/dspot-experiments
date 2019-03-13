package com.fishercoder;


import _283.Solution1;
import _283.Solution2;
import _283.Solution3;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _283Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static int[] nums;

    @Test
    public void test1() {
        _283Test.nums = new int[]{ 0, 1, 0, 3, 12 };
        _283Test.solution1.moveZeroes(_283Test.nums);
        CommonUtils.printArray(_283Test.nums);
    }

    @Test
    public void test2() {
        _283Test.nums = new int[]{ 0, 1, 0, 3, 12 };
        _283Test.solution2.moveZeroes(_283Test.nums);
        CommonUtils.printArray(_283Test.nums);
    }

    @Test
    public void test3() {
        _283Test.nums = new int[]{ 0, 1, 0, 3, 12 };
        _283Test.solution3.moveZeroes(_283Test.nums);
        CommonUtils.printArray(_283Test.nums);
    }
}

