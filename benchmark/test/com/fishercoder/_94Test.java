package com.fishercoder;


import _94.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.CommonUtils;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class _94Test {
    private static Solution1 solution1;

    private static TreeNode root;

    private static List<Integer> inorder;

    @Test
    public void test1() {
        _94Test.root = TreeUtils.constructBinaryTree(Arrays.asList(3, 1, null, null, 5, 2, null, null, 4));
        _94Test.inorder = _94Test.solution1.inorderTraversal(_94Test.root);
        CommonUtils.printList(_94Test.inorder);
    }
}

