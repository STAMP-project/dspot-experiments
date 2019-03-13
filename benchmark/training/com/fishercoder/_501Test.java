package com.fishercoder;


import _501.Solution1;
import _501.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/28/17.
 */
public class _501Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] expected;

    private static int[] actual;

    private static TreeNode treeNode;

    @Test
    public void test1() {
        _501Test.treeNode = new TreeNode(1);
        _501Test.treeNode.right = new TreeNode(2);
        _501Test.treeNode.right.left = new TreeNode(2);
        _501Test.expected = new int[]{ 2 };
        CommonUtils.printArray(_501Test.expected);
        CommonUtils.printArray(_501Test.actual);
        _501Test.actual = _501Test.solution1.findMode(_501Test.treeNode);
        Assert.assertArrayEquals(_501Test.expected, _501Test.actual);
        _501Test.actual = _501Test.solution2.findMode(_501Test.treeNode);
        Assert.assertArrayEquals(_501Test.expected, _501Test.actual);
    }
}

