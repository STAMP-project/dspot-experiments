package com.fishercoder;


import _993.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class _993Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _993Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, 4));
        TreeUtils.printBinaryTree(_993Test.root);
        Assert.assertEquals(false, _993Test.solution1.isCousins(_993Test.root, 4, 3));
    }

    @Test
    public void test2() {
        _993Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, null, 4, null, 5));
        TreeUtils.printBinaryTree(_993Test.root);
        Assert.assertEquals(true, _993Test.solution1.isCousins(_993Test.root, 5, 4));
    }

    @Test
    public void test3() {
        _993Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, null, 4));
        TreeUtils.printBinaryTree(_993Test.root);
        Assert.assertEquals(false, _993Test.solution1.isCousins(_993Test.root, 2, 3));
    }
}

