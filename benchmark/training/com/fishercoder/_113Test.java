package com.fishercoder;


import _113.Solution1;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _113Test {
    private static Solution1 solution1;

    private static TreeNode root;

    private static int sum;

    private static List<List<Integer>> expected;

    @Test
    public void test1() {
        _113Test.sum = 22;
        _113Test.root = TreeUtils.constructBinaryTree(Arrays.asList(5, 4, 8, 11, null, 13, 4, 7, 2, null, null, 5, 1));
        TreeUtils.printBinaryTree(_113Test.root);
        _113Test.expected = new ArrayList<>();
        _113Test.expected.add(Arrays.asList(5, 4, 11, 2));
        _113Test.expected.add(Arrays.asList(5, 8, 4, 5));
        Assert.assertEquals(_113Test.expected, _113Test.solution1.pathSum(_113Test.root, _113Test.sum));
    }
}

