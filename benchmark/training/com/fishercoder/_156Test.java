package com.fishercoder;


import _156.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _156Test {
    private static Solution1 solution1;

    private static TreeNode root;

    private static TreeNode expected;

    @Test
    public void test1() {
        _156Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, 4, 5));
        _156Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(4, 5, 2, null, null, 3, 1));
        Assert.assertEquals(_156Test.expected, _156Test.solution1.upsideDownBinaryTree(_156Test.root));
    }
}

