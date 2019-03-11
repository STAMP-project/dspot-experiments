package com.fishercoder;


import _235.Solution1;
import _235.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _235Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode root;

    private static TreeNode p;

    private static TreeNode q;

    @Test
    public void test1() {
        _235Test.root = TreeUtils.constructBinaryTree(Arrays.asList(6, 2, 8, 0, 4, 7, 9, 3, 5));
        TreeUtils.printBinaryTree(_235Test.root);
        _235Test.p = TreeUtils.constructBinaryTree(Arrays.asList(2, 0, 4, 3, 5));
        TreeUtils.printBinaryTree(_235Test.p);
        _235Test.q = TreeUtils.constructBinaryTree(Arrays.asList(8, 7, 9));
        TreeUtils.printBinaryTree(_235Test.q);
        Assert.assertEquals(_235Test.root, _235Test.solution1.lowestCommonAncestor(_235Test.root, _235Test.p, _235Test.q));
        Assert.assertEquals(_235Test.root, _235Test.solution2.lowestCommonAncestor(_235Test.root, _235Test.p, _235Test.q));
    }

    @Test
    public void test2() {
        _235Test.root = TreeUtils.constructBinaryTree(Arrays.asList(6, 2, 8, 0, 4, 7, 9, 3, 5));
        TreeUtils.printBinaryTree(_235Test.root);
        _235Test.p = TreeUtils.constructBinaryTree(Arrays.asList(2, 0, 4, 3, 5));
        TreeUtils.printBinaryTree(_235Test.p);
        _235Test.q = TreeUtils.constructBinaryTree(Arrays.asList(4));
        TreeUtils.printBinaryTree(_235Test.q);
        Assert.assertEquals(_235Test.p, _235Test.solution1.lowestCommonAncestor(_235Test.root, _235Test.p, _235Test.q));
        Assert.assertEquals(_235Test.p, _235Test.solution2.lowestCommonAncestor(_235Test.root, _235Test.p, _235Test.q));
    }
}

