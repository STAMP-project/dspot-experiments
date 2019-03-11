package com.fishercoder;


import _671.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _671Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _671Test.root = TreeUtils.constructBinaryTree(Arrays.asList(2, 2, 5, null, null, 5, 7));
        Assert.assertEquals(5, _671Test.solution1.findSecondMinimumValue(_671Test.root));
    }

    @Test
    public void test2() {
        _671Test.root = TreeUtils.constructBinaryTree(Arrays.asList(2, 2, 2));
        Assert.assertEquals((-1), _671Test.solution1.findSecondMinimumValue(_671Test.root));
    }
}

