package com.fishercoder;


import _669.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _669Test {
    private static Solution1 solution1;

    private static TreeNode root;

    private static TreeNode expected;

    @Test
    public void test1() {
        _669Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 0, 2));
        _669Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(1, null, 2));
        Assert.assertEquals(_669Test.expected, _669Test.solution1.trimBST(_669Test.root, 1, 2));
    }

    @Test
    public void test2() {
        _669Test.root = TreeUtils.constructBinaryTree(Arrays.asList(3, 0, 4, null, 2, null, null, 1));
        _669Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(3, 2, null, 1));
        Assert.assertEquals(_669Test.expected, _669Test.solution1.trimBST(_669Test.root, 1, 3));
    }
}

