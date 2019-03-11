package com.fishercoder;


import _449.Solution1;
import _449.Solution2;
import _449.Solution3;
import com.fishercoder.common.classes.TreeNode;
import junit.framework.Assert;
import org.junit.Test;


public class _449Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static TreeNode expectedRoot;

    @Test
    public void test1() {
        _449Test.expectedRoot = new TreeNode(3);
        _449Test.expectedRoot.left = new TreeNode(1);
        _449Test.expectedRoot.right = new TreeNode(4);
        _449Test.expectedRoot.left.right = new TreeNode(2);
        Assert.assertEquals(_449Test.expectedRoot.toString(), _449Test.solution1.deserialize(_449Test.solution1.serialize(_449Test.expectedRoot)).toString());
        Assert.assertEquals(_449Test.expectedRoot.toString(), _449Test.solution2.deserialize(_449Test.solution2.serialize(_449Test.expectedRoot)).toString());
        Assert.assertEquals(_449Test.expectedRoot.toString(), _449Test.solution3.deserialize(_449Test.solution3.serialize(_449Test.expectedRoot)).toString());
    }

    @Test
    public void test2() {
        _449Test.expectedRoot = new TreeNode(2);
        _449Test.expectedRoot.left = new TreeNode(1);
        _449Test.expectedRoot.right = new TreeNode(3);
        Assert.assertEquals(_449Test.expectedRoot.toString(), _449Test.solution1.deserialize(_449Test.solution1.serialize(_449Test.expectedRoot)).toString());
        Assert.assertEquals(_449Test.expectedRoot.toString(), _449Test.solution2.deserialize(_449Test.solution2.serialize(_449Test.expectedRoot)).toString());
        Assert.assertEquals(_449Test.expectedRoot.toString(), _449Test.solution3.deserialize(_449Test.solution3.serialize(_449Test.expectedRoot)).toString());
    }
}

