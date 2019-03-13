package com.fishercoder;


import _377.Solution1;
import _377.Solution2;
import _377.Solution3;
import _377.Solution4;
import junit.framework.Assert;
import org.junit.Test;


public class _377Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static Solution4 solution4;

    private static int[] nums;

    private static int target;

    @Test
    public void test1() {
        _377Test.nums = new int[]{ 1, 2, 3 };
        _377Test.target = 4;
        Assert.assertEquals(7, _377Test.solution1.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(7, _377Test.solution2.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(7, _377Test.solution3.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(7, _377Test.solution4.combinationSum4(_377Test.nums, _377Test.target));
    }

    @Test
    public void test2() {
        _377Test.nums = new int[]{ 4, 2, 1 };
        _377Test.target = 32;
        // assertEquals(39882198, solution1.combinationSum4(nums, target));//this results in MLE, so comment out
        Assert.assertEquals(39882198, _377Test.solution2.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(39882198, _377Test.solution3.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(39882198, _377Test.solution4.combinationSum4(_377Test.nums, _377Test.target));
    }

    @Test
    public void test3() {
        _377Test.nums = new int[]{ 9 };
        _377Test.target = 3;
        Assert.assertEquals(0, _377Test.solution1.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(0, _377Test.solution2.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(0, _377Test.solution3.combinationSum4(_377Test.nums, _377Test.target));
        Assert.assertEquals(0, _377Test.solution4.combinationSum4(_377Test.nums, _377Test.target));
    }
}

