package com.fishercoder;


import _572.Solution1;
import com.fishercoder.common.classes.TreeNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/6/17.
 */
public class _572Test {
    private static Solution1 solution1;

    private static boolean expected;

    private static boolean actual;

    @Test
    public void test1() {
        TreeNode s = new TreeNode(3);
        s.left = new TreeNode(4);
        s.right = new TreeNode(5);
        s.left.left = new TreeNode(1);
        s.left.right = new TreeNode(2);
        s.left.right.left = new TreeNode(0);
        TreeNode t = new TreeNode(4);
        t.left = new TreeNode(1);
        t.right = new TreeNode(2);
        _572Test.expected = false;
        _572Test.actual = _572Test.solution1.isSubtree(s, t);
        Assert.assertEquals(_572Test.expected, _572Test.actual);
    }

    @Test
    public void test2() {
        TreeNode s = new TreeNode(3);
        s.left = new TreeNode(4);
        s.right = new TreeNode(5);
        s.left.left = new TreeNode(1);
        s.left.right = new TreeNode(2);
        TreeNode t = new TreeNode(4);
        t.left = new TreeNode(1);
        t.right = new TreeNode(2);
        _572Test.expected = true;
        _572Test.actual = _572Test.solution1.isSubtree(s, t);
        Assert.assertEquals(_572Test.expected, _572Test.actual);
    }
}

