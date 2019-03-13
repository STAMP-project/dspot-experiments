package com.fishercoder;


import _145.Solution1;
import _145.Solution2;
import com.fishercoder.common.classes.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _145Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode root;

    private static List<Integer> expected;

    @Test
    public void test1() {
        _145Test.root = new TreeNode(1);
        _145Test.root.left = new TreeNode(2);
        _145Test.root.right = new TreeNode(3);
        _145Test.expected = new ArrayList<>(Arrays.asList(2, 3, 1));
        Assert.assertEquals(_145Test.expected, _145Test.solution1.postorderTraversal(_145Test.root));
        Assert.assertEquals(_145Test.expected, _145Test.solution2.postorderTraversal(_145Test.root));
    }
}

