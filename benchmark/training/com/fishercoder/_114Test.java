package com.fishercoder;


import _114.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Test;


public class _114Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _114Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 5, 3, 4, null, 6));
        TreeUtils.printBinaryTree(_114Test.root);
        _114Test.solution1.flatten(_114Test.root);
        TreeUtils.printBinaryTree(_114Test.root);
    }
}

