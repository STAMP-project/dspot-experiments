package com.fishercoder;


import _897.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Test;


public class _897Test {
    private static Solution1 solution1;

    private static TreeNode root;

    private static TreeNode actual;

    @Test
    public void test1() {
        _897Test.root = TreeUtils.constructBinaryTree(Arrays.asList(5, 3, 6, 2, 4, null, 8, 1, null, null, null, 7, 9));
        TreeUtils.printBinaryTree(_897Test.root);
        _897Test.actual = _897Test.solution1.increasingBST(_897Test.root);
        TreeUtils.printBinaryTree(_897Test.actual);
    }
}

