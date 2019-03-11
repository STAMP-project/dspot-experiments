package com.fishercoder;


import _508.Solution1;
import _508.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _508Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] expected;

    private static int[] actual;

    private static TreeNode root;

    @Test
    public void test1() {
        _508Test.root = TreeUtils.constructBinaryTree(Arrays.asList(5, 2, (-3)));
        _508Test.expected = new int[]{ 2, -3, 4 };
        /**
         * Since order does NOT matter, so I'll sort them and then compare
         */
        Arrays.sort(_508Test.expected);
        _508Test.actual = _508Test.solution1.findFrequentTreeSum(_508Test.root);
        Arrays.sort(_508Test.actual);
        Assert.assertArrayEquals(_508Test.expected, _508Test.actual);
        _508Test.actual = _508Test.solution2.findFrequentTreeSum(_508Test.root);
        Arrays.sort(_508Test.actual);
        Assert.assertArrayEquals(_508Test.expected, _508Test.actual);
    }

    @Test
    public void test2() {
        _508Test.root = TreeUtils.constructBinaryTree(Arrays.asList(5, 2, (-5)));
        _508Test.expected = new int[]{ 2 };
        _508Test.actual = _508Test.solution1.findFrequentTreeSum(_508Test.root);
        Assert.assertArrayEquals(_508Test.expected, _508Test.actual);
        _508Test.actual = _508Test.solution2.findFrequentTreeSum(_508Test.root);
        Assert.assertArrayEquals(_508Test.expected, _508Test.actual);
    }

    @Test
    public void test3() {
        _508Test.root = TreeUtils.constructBinaryTree(Arrays.asList(3, 1, 5, 0, 2, 4, 6, null, null, null, 3));
        TreeUtils.printBinaryTree(_508Test.root);
        _508Test.expected = new int[]{ 6 };
        _508Test.actual = _508Test.solution1.findFrequentTreeSum(_508Test.root);
        Assert.assertArrayEquals(_508Test.expected, _508Test.actual);
        _508Test.actual = _508Test.solution2.findFrequentTreeSum(_508Test.root);
        Assert.assertArrayEquals(_508Test.expected, _508Test.actual);
    }
}

