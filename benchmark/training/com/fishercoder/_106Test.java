package com.fishercoder;


import _106.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class _106Test {
    private static Solution1 solution1;

    private static TreeNode expected;

    private static TreeNode actual;

    private static int[] inorder;

    private static int[] postorder;

    @Test
    public void test1() {
        /**
         * it should be a tree like this:
         *    3
         *   /
         *  1
         *   \
         *   2
         */
        _106Test.postorder = new int[]{ 2, 1, 3 };
        _106Test.inorder = new int[]{ 1, 2, 3 };
        _106Test.actual = _106Test.solution1.buildTree(_106Test.inorder, _106Test.postorder);
        _106Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(3, 1, null, null, 2));
        Assert.assertEquals(_106Test.expected, _106Test.actual);
    }

    @Test
    public void test2() {
        /**
         * it should be a tree like this:
         *    3
         *   /
         *  1
         *   \
         *   5
         *  /
         * 2
         *  \
         *  4
         */
        _106Test.postorder = new int[]{ 4, 2, 5, 1, 3 };
        _106Test.inorder = new int[]{ 1, 2, 4, 5, 3 };
        _106Test.actual = _106Test.solution1.buildTree(_106Test.inorder, _106Test.postorder);
        _106Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(3, 1, null, null, 5, 2, null, null, 4));
        Assert.assertEquals(_106Test.expected, _106Test.actual);
    }

    @Test
    public void test3() {
        /**
         * it should be a tree like this:
         *    2
         *   /
         *  1
         */
        _106Test.inorder = new int[]{ 1, 2 };
        _106Test.postorder = new int[]{ 1, 2 };
        _106Test.actual = _106Test.solution1.buildTree(_106Test.inorder, _106Test.postorder);
        _106Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(2, 1));
        Assert.assertEquals(_106Test.expected, _106Test.actual);
    }
}

