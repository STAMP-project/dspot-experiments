package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.solutions._450;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 5/29/17.
 */
public class _450Test {
    private static _450 test;

    private static TreeNode expected;

    private static TreeNode actual;

    private static TreeNode root;

    @Test
    public void test1() {
        _450Test.root = new TreeNode(5);
        _450Test.root.left = new TreeNode(3);
        _450Test.root.left.left = new TreeNode(2);
        _450Test.root.left.right = new TreeNode(4);
        _450Test.root.right = new TreeNode(6);
        _450Test.root.right.right = new TreeNode(7);
        _450Test.expected = new TreeNode(5);
        _450Test.expected.left = new TreeNode(4);
        _450Test.expected.right = new TreeNode(6);
        _450Test.expected.left.left = new TreeNode(2);
        _450Test.expected.right.right = new TreeNode(7);
        Assert.assertEquals(_450Test.expected, _450Test.test.deleteNode(_450Test.root, 3));
    }
}

