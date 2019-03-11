package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.solutions._652;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 7/30/17.
 */
public class _652Test {
    private static _652 test;

    private static List<TreeNode> expected;

    private static TreeNode root;

    @Test
    public void test1() {
        _652Test.root = new TreeNode(1);
        _652Test.root.left = new TreeNode(2);
        _652Test.root.left.left = new TreeNode(4);
        _652Test.root.right = new TreeNode(3);
        _652Test.root.right.left = new TreeNode(2);
        _652Test.root.right.left.left = new TreeNode(4);
        _652Test.root.right.right = new TreeNode(4);
        TreeNode tree1 = new TreeNode(2);
        tree1.left = new TreeNode(4);
        TreeNode tree2 = new TreeNode(4);
        _652Test.expected = new ArrayList(Arrays.asList(tree2, tree1));
        Assert.assertEquals(_652Test.expected, _652Test.test.findDuplicateSubtrees(_652Test.root));
    }

    @Test
    public void test2() {
        _652Test.expected = new ArrayList();
        Assert.assertEquals(_652Test.expected, _652Test.test.findDuplicateSubtrees(_652Test.root));
    }

    @Test
    public void test3() {
        _652Test.root = new TreeNode(2);
        _652Test.root.left = new TreeNode(1);
        _652Test.root.right = new TreeNode(1);
        TreeNode tree1 = new TreeNode(1);
        _652Test.expected = new ArrayList(Arrays.asList(tree1));
        Assert.assertEquals(_652Test.expected, _652Test.test.findDuplicateSubtrees(_652Test.root));
    }
}

