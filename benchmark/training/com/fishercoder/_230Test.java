package com.fishercoder;


import _230.Solution1;
import _230.Solution2;
import com.fishercoder.common.classes.TreeNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/19/17.
 */
public class _230Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode root;

    private static int k;

    @Test
    public void test1() {
        _230Test.root = new TreeNode(1);
        _230Test.k = 1;
        Assert.assertEquals(1, _230Test.solution1.kthSmallest(_230Test.root, _230Test.k));
        Assert.assertEquals(1, _230Test.solution2.kthSmallest(_230Test.root, _230Test.k));
    }

    @Test
    public void test2() {
        _230Test.root = new TreeNode(2);
        _230Test.root.left = new TreeNode(1);
        _230Test.k = 1;
        Assert.assertEquals(1, _230Test.solution1.kthSmallest(_230Test.root, _230Test.k));
        Assert.assertEquals(1, _230Test.solution2.kthSmallest(_230Test.root, _230Test.k));
    }
}

