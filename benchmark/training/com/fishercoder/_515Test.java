package com.fishercoder;


import _515.Solution1;
import _515.Solution2;
import com.fishercoder.common.classes.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _515Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static List<Integer> expected;

    private static List<Integer> actual;

    private static TreeNode root;

    @Test
    public void test1() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(3);
        root.right = new TreeNode(2);
        _515Test.expected = Arrays.asList(1, 3);
        _515Test.actual = _515Test.solution1.largestValues(root);
        Assert.assertEquals(_515Test.expected, _515Test.actual);
        _515Test.actual = _515Test.solution2.largestValues(root);
        Assert.assertEquals(_515Test.expected, _515Test.actual);
    }

    @Test
    public void test2() {
        _515Test.expected = new ArrayList<>();
        _515Test.actual = _515Test.solution1.largestValues(null);
        Assert.assertEquals(_515Test.expected, _515Test.actual);
        _515Test.actual = _515Test.solution2.largestValues(null);
        Assert.assertEquals(_515Test.expected, _515Test.actual);
    }
}

