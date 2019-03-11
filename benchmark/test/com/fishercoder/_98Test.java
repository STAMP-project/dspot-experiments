package com.fishercoder;


import _98.Solution1;
import com.fishercoder.common.classes.TreeNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/17/17.
 */
public class _98Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _98Test.root = new TreeNode(2);
        _98Test.root.left = new TreeNode(1);
        _98Test.root.right = new TreeNode(3);
        Assert.assertEquals(true, _98Test.solution1.isValidBST(_98Test.root));
    }

    @Test
    public void test2() {
        _98Test.root = new TreeNode(0);
        Assert.assertEquals(true, _98Test.solution1.isValidBST(_98Test.root));
    }

    @Test
    public void test3() {
        _98Test.root = new TreeNode(1);
        _98Test.root.left = new TreeNode(1);
        Assert.assertEquals(false, _98Test.solution1.isValidBST(_98Test.root));
    }
}

