package com.fishercoder;


import _105.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class _105Test {
    private static Solution1 solution1;

    private static TreeNode expected;

    private static TreeNode actual;

    private static int[] preorder;

    private static int[] inorder;

    @Test
    public void test1() {
        _105Test.preorder = new int[]{ 1, 2, 3 };
        _105Test.inorder = new int[]{ 2, 1, 3 };
        _105Test.actual = _105Test.solution1.buildTree(_105Test.preorder, _105Test.inorder);
        _105Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3));
        Assert.assertEquals(_105Test.expected, _105Test.actual);
    }

    @Test
    public void test2() {
        _105Test.preorder = new int[]{ 1, 2, 4, 5, 3 };
        _105Test.inorder = new int[]{ 4, 2, 5, 1, 3 };
        _105Test.actual = _105Test.solution1.buildTree(_105Test.preorder, _105Test.inorder);
        _105Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, 4, 5));
        Assert.assertEquals(_105Test.expected, _105Test.actual);
    }
}

