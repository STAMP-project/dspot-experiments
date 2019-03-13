package com.fishercoder;


import _700.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _700Test {
    private static Solution1 solution1;

    private static TreeNode root;

    private static TreeNode expected;

    @Test
    public void test1() {
        _700Test.root = TreeUtils.constructBinaryTree(List.of(List, 4, 2, 7, 1, 3));
        _700Test.expected = TreeUtils.constructBinaryTree(List.of(List, 2, 1, 3));
        Assert.assertEquals(_700Test.expected, _700Test.solution1.searchBST(_700Test.root, 2));
    }
}

