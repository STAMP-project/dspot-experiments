package com.fishercoder;


import _606.Solution1;
import _606.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/4/17.
 */
public class _606Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode treeNode;

    @Test
    public void test1() {
        _606Test.treeNode = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, 4));
        Assert.assertEquals("1(2(4))(3)", _606Test.solution1.tree2str(_606Test.treeNode));
        Assert.assertEquals("1(2(4))(3)", _606Test.solution2.tree2str(_606Test.treeNode));
    }

    @Test
    public void test2() {
        _606Test.treeNode = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, null, 4));
        Assert.assertEquals("1(2()(4))(3)", _606Test.solution1.tree2str(_606Test.treeNode));
        Assert.assertEquals("1(2()(4))(3)", _606Test.solution2.tree2str(_606Test.treeNode));
    }

    @Test
    public void test3() {
        _606Test.treeNode = TreeUtils.constructBinaryTree(Arrays.asList(1, null, 2, null, 3));
        Assert.assertEquals("1()(2()(3))", _606Test.solution1.tree2str(_606Test.treeNode));
        Assert.assertEquals("1()(2()(3))", _606Test.solution2.tree2str(_606Test.treeNode));
    }
}

