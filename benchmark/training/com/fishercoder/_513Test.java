package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.solutions._513;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/15/17.
 */
public class _513Test {
    private static _513 test;

    private static int expected;

    private static int actual;

    private static TreeNode root;

    @Test
    public void test1() {
        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);
        _513Test.expected = 1;
        _513Test.actual = _513Test.test.findBottomLeftValue(root);
        Assert.assertEquals(_513Test.expected, _513Test.actual);
    }

    @Test
    public void test2() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(6);
        root.right.left.left = new TreeNode(7);
        _513Test.expected = 7;
        _513Test.actual = _513Test.test.findBottomLeftValue(root);
        Assert.assertEquals(_513Test.expected, _513Test.actual);
    }
}

