package com.fishercoder;


import _654.Solution1;
import _654.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _654Test {
    private static int[] nums;

    private static TreeNode expected;

    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        _654Test.nums = new int[]{ 3, 2, 1, 6, 0, 5 };
        _654Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(6, 3, 5, null, 2, 0, null, null, 1));
        Assert.assertEquals(_654Test.expected, _654Test.solution1.constructMaximumBinaryTree(_654Test.nums));
        Assert.assertEquals(_654Test.expected, _654Test.solution2.constructMaximumBinaryTree(_654Test.nums));
    }
}

