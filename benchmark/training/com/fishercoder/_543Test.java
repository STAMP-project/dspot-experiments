package com.fishercoder;


import _543.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;


public class _543Test {
    private static Solution1 solution1;

    private static TreeNode root;

    @Test
    public void test1() {
        _543Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, 4, 5));
        TreeUtils.printBinaryTree(_543Test.root);
        TestCase.assertEquals(3, _543Test.solution1.diameterOfBinaryTree(_543Test.root));
    }
}

