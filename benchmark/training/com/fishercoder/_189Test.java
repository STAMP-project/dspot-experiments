package com.fishercoder;


import _189.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _189Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _189Test.nums = new int[]{ 1, 2, 3 };
        _189Test.solution1.rotate(_189Test.nums, 1);
        CommonUtils.printArray(_189Test.nums);
    }
}

