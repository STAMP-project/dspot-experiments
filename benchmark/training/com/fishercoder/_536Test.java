package com.fishercoder;


import _536.Solution1;
import _536.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class _536Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode expected;

    private static String s;

    @Test
    public void test1() {
        _536Test.s = "4(2(3)(1))(6(5))";
        _536Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(4, 2, 6, 3, 1, 5));
        Assert.assertEquals(_536Test.expected, _536Test.solution1.str2tree(_536Test.s));
        Assert.assertEquals(_536Test.expected, _536Test.solution2.str2tree(_536Test.s));
    }

    @Test
    public void test2() {
        _536Test.s = "51(232)(434)";
        _536Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(51, 232, 434));
        Assert.assertEquals(_536Test.expected, _536Test.solution1.str2tree(_536Test.s));
        Assert.assertEquals(_536Test.expected, _536Test.solution2.str2tree(_536Test.s));
    }
}

