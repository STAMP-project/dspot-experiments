package com.fishercoder;


import _549.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class _549Test {
    private static Solution1 solution1;

    private static int expected;

    private static int actual;

    private static TreeNode root;

    @Test
    public void test1() {
        _549Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3));
        _549Test.actual = _549Test.solution1.longestConsecutive(_549Test.root);
        _549Test.expected = 2;
        Assert.assertEquals(_549Test.expected, _549Test.actual);
    }

    @Test
    public void test2() {
        _549Test.root = TreeUtils.constructBinaryTree(Arrays.asList(2, 1, 3));
        _549Test.actual = _549Test.solution1.longestConsecutive(_549Test.root);
        _549Test.expected = 3;
        Assert.assertEquals(_549Test.expected, _549Test.actual);
    }

    @Test
    public void test3() {
        _549Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1));
        _549Test.actual = _549Test.solution1.longestConsecutive(_549Test.root);
        _549Test.expected = 1;
        Assert.assertEquals(_549Test.expected, _549Test.actual);
    }

    @Test
    public void test4() {
        _549Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, null, 3, null, 4));
        TreeUtils.printBinaryTree(_549Test.root);
        _549Test.actual = _549Test.solution1.longestConsecutive(_549Test.root);
        _549Test.expected = 4;
        Assert.assertEquals(_549Test.expected, _549Test.actual);
    }
}

