package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import com.fishercoder.solutions._663;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _663Test {
    private static _663 test;

    private static TreeNode root;

    private static boolean expected;

    @Test
    public void test1() {
        _663Test.root = TreeUtils.constructBinaryTree(Arrays.asList(5, 10, 10, null, null, 2, 3));
        TreeUtils.printBinaryTree(_663Test.root);
        _663Test.expected = true;
        Assert.assertEquals(_663Test.expected, _663Test.test.checkEqualTree(_663Test.root));
    }

    @Test
    public void test2() {
        _663Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 10, null, null, 2, 20));
        TreeUtils.printBinaryTree(_663Test.root);
        _663Test.expected = false;
        Assert.assertEquals(_663Test.expected, _663Test.test.checkEqualTree(_663Test.root));
    }

    @Test
    public void test3() {
        _663Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, null, 2, 2));
        TreeUtils.printBinaryTree(_663Test.root);
        _663Test.expected = false;
        Assert.assertEquals(_663Test.expected, _663Test.test.checkEqualTree(_663Test.root));
    }

    @Test
    public void test4() {
        _663Test.root = TreeUtils.constructBinaryTree(Arrays.asList(0));
        TreeUtils.printBinaryTree(_663Test.root);
        _663Test.expected = false;
        Assert.assertEquals(_663Test.expected, _663Test.test.checkEqualTree(_663Test.root));
    }
}

