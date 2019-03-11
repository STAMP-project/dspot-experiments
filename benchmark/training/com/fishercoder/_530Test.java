package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.solutions._530;
import junit.framework.Assert;
import org.junit.Test;


public class _530Test {
    private static _530 test;

    private static int expected;

    private static int actual;

    private static TreeNode root;

    @Test
    public void test1() {
        _530Test.root = new TreeNode(1);
        _530Test.root.right = new TreeNode(3);
        _530Test.root.right.left = new TreeNode(2);
        _530Test.expected = 1;
        _530Test.actual = _530Test.test.getMinimumDifference(_530Test.root);
        Assert.assertEquals(_530Test.expected, _530Test.actual);
    }

    @Test
    public void test2() {
        _530Test.root = new TreeNode(1);
        _530Test.root.right = new TreeNode(5);
        _530Test.root.right.left = new TreeNode(3);
        _530Test.expected = 2;
        _530Test.actual = _530Test.test.getMinimumDifference(_530Test.root);
        Assert.assertEquals(_530Test.expected, _530Test.actual);
    }

    // [543,384,652,null,445,null,699]
    @Test
    public void test3() {
        _530Test.root = new TreeNode(543);
        _530Test.root.left = new TreeNode(384);
        _530Test.root.right = new TreeNode(652);
        _530Test.root.left.right = new TreeNode(445);
        _530Test.root.right.right = new TreeNode(699);
        _530Test.expected = 47;
        _530Test.actual = _530Test.test.getMinimumDifference(_530Test.root);
        Assert.assertEquals(_530Test.expected, _530Test.actual);
    }
}

