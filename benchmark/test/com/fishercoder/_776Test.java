package com.fishercoder;


import _776.Solution1;
import _776.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _776Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode root;

    private static TreeNode small;

    private static TreeNode big;

    @Test
    public void test1() {
        _776Test.root = TreeUtils.constructBinaryTree(Arrays.asList(4, 2, 6, 1, 3, 5, 7));
        _776Test.small = TreeUtils.constructBinaryTree(Arrays.asList(2, 1));
        _776Test.big = TreeUtils.constructBinaryTree(Arrays.asList(4, 3, 6, null, null, 5, 7));
        Assert.assertArrayEquals(new TreeNode[]{ _776Test.small, _776Test.big }, _776Test.solution1.splitBST(_776Test.root, 2));
    }

    @Test
    public void test2() {
        _776Test.root = TreeUtils.constructBinaryTree(Arrays.asList(4, 2, 6, 1, 3, 5, 7));
        _776Test.small = TreeUtils.constructBinaryTree(Arrays.asList(2, 1));
        _776Test.big = TreeUtils.constructBinaryTree(Arrays.asList(4, 3, 6, null, null, 5, 7));
        Assert.assertArrayEquals(new TreeNode[]{ _776Test.small, _776Test.big }, _776Test.solution2.splitBST(_776Test.root, 2));
    }
}

