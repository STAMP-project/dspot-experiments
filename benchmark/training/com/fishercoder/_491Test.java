package com.fishercoder;


import _491.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import java.util.List;
import org.junit.Test;


public class _491Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _491Test.nums = new int[]{ 4, 6, 7, 7 };
        List<List<Integer>> actual = _491Test.solution1.findSubsequences(_491Test.nums);
        CommonUtils.printListList(actual);
    }
}

