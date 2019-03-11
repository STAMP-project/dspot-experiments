package com.fishercoder;


import _101.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _101Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _101Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 2, 3, 4, 4, 3));
        Assert.assertEquals(true, _101Test.solution1.isSymmetric(_101Test.root));
    }

    @Test
    public void test2() {
        _101Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 2, null, 3, null, 3));
        Assert.assertEquals(false, _101Test.solution1.isSymmetric(_101Test.root));
    }
}

