package com.fishercoder;


import _104.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _104Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _104Test.root = TreeUtils.constructBinaryTree(Arrays.asList(3, 9, 20, null, null, 15, 7));
        Assert.assertEquals(3, _104Test.solution1.maxDepth(_104Test.root));
    }
}

