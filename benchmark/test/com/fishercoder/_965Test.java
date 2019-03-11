package com.fishercoder;


import _965.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _965Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _965Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 1, 1, 1, 1, null, 1));
        Assert.assertEquals(true, _965Test.solution1.isUnivalTree(_965Test.root));
    }

    @Test
    public void test2() {
        _965Test.root = TreeUtils.constructBinaryTree(Arrays.asList(2, 2, 2, 5, 2));
        Assert.assertEquals(false, _965Test.solution1.isUnivalTree(_965Test.root));
    }
}

