package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.solutions._563;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/23/17.
 */
public class _563Test {
    private static _563 test;

    private static int expected;

    private static int actual;

    private static TreeNode root;

    @Test
    public void test1() {
        _563Test.root = new TreeNode(1);
        _563Test.root.left = new TreeNode(2);
        _563Test.root.right = new TreeNode(3);
        _563Test.expected = 1;
        _563Test.actual = _563Test.test.findTilt(_563Test.root);
        Assert.assertEquals(_563Test.expected, _563Test.actual);
    }
}

