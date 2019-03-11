package com.fishercoder;


import _872.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _872Test {
    private static Solution1 solution1;

    private static TreeNode root1;

    private static TreeNode root2;

    @Test
    public void test1() {
        _872Test.root1 = TreeUtils.constructBinaryTree(Arrays.asList(3, 5, 6, 2, 7, 4, 1, 9, 8));
        _872Test.root2 = TreeUtils.constructBinaryTree(Arrays.asList(3, 5, 6, 2, 7, 4, 1, 9, 8));
        TreeUtils.printBinaryTree(_872Test.root1);
        TreeUtils.printBinaryTree(_872Test.root2);
        Assert.assertEquals(true, _872Test.solution1.leafSimilar(_872Test.root1, _872Test.root2));
    }

    @Test
    public void test2() {
        _872Test.root1 = TreeUtils.constructBinaryTree(Arrays.asList(18, 35, 22, null, 103, 43, 101, 58, null, 97));
        TreeUtils.printBinaryTree(_872Test.root1);
        _872Test.root2 = TreeUtils.constructBinaryTree(Arrays.asList(94, 102, 17, 122, null, null, 54, 58, 101, 97));
        TreeUtils.printBinaryTree(_872Test.root2);
        Assert.assertEquals(false, _872Test.solution1.leafSimilar(_872Test.root1, _872Test.root2));
    }
}

