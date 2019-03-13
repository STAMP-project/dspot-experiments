package com.fishercoder;


import _538.BSTSolution;
import _538.GenericSolution;
import com.fishercoder.common.classes.TreeNode;
import junit.framework.Assert;
import org.junit.Test;


public class _538Test {
    private static GenericSolution genericSolution;

    private static BSTSolution bstSolution;

    private static TreeNode expectedRoot;

    private static TreeNode root;

    @Test
    public void test1() {
        _538Test.root = new TreeNode(5);
        _538Test.root.left = new TreeNode(2);
        _538Test.root.right = new TreeNode(13);
        _538Test.expectedRoot = new TreeNode(18);
        _538Test.expectedRoot.left = new TreeNode(20);
        _538Test.expectedRoot.right = new TreeNode(13);
        Assert.assertEquals(_538Test.expectedRoot.toString(), _538Test.genericSolution.convertBST(_538Test.root).toString());
        Assert.assertEquals(_538Test.expectedRoot.toString(), _538Test.bstSolution.convertBST(_538Test.root).toString());
    }

    @Test
    public void test2() {
        _538Test.root = null;
        _538Test.expectedRoot = null;
        Assert.assertEquals(_538Test.expectedRoot, _538Test.genericSolution.convertBST(_538Test.root));
        Assert.assertEquals(_538Test.expectedRoot, _538Test.bstSolution.convertBST(_538Test.root));
    }
}

