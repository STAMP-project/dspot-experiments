package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import com.fishercoder.solutions._545;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _545Test {
    private static _545 test;

    private static TreeNode root;

    private static List<Integer> expected;

    @Test
    public void test1() {
        _545Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, null, 4, 5, null, 6, 7));
        TreeUtils.printBinaryTree(_545Test.root);
        _545Test.expected = Arrays.asList(1, 2, 4, 6, 7, 5, 3);
        Assert.assertEquals(_545Test.expected, _545Test.test.boundaryOfBinaryTree(_545Test.root));
    }
}

