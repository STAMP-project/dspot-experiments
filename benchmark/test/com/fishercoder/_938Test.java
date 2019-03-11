package com.fishercoder;


import _938.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _938Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _938Test.root = TreeUtils.constructBinaryTree(Arrays.asList(10, 5, 15, 3, 7, null, 18));
        TreeUtils.printBinaryTree(_938Test.root);
        Assert.assertEquals(32, _938Test.solution1.rangeSumBST(_938Test.root, 7, 15));
    }

    @Test
    public void test2() {
        _938Test.root = TreeUtils.constructBinaryTree(Arrays.asList(10, 5, 15, 3, 7, 13, 18, 1, null, 6));
        TreeUtils.printBinaryTree(_938Test.root);
        Assert.assertEquals(23, _938Test.solution1.rangeSumBST(_938Test.root, 6, 10));
    }
}

