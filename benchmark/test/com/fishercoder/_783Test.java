package com.fishercoder;


import _783.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _783Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _783Test.root = TreeUtils.constructBinaryTree(Arrays.asList(4, 2, 6, 1, 3, null, null));
        TreeUtils.printBinaryTree(_783Test.root);
        Assert.assertEquals(1, _783Test.solution1.minDiffInBST(_783Test.root));
    }
}

