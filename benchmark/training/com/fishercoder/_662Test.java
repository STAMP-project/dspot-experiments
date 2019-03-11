package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import com.fishercoder.solutions._662;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;


public class _662Test {
    private static _662 test;

    private static TreeNode root;

    private static int expected;

    @Test
    public void test1() {
        _662Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 3, 2, 5, 3, null, 9));
        _662Test.expected = 4;
        TestCase.assertEquals(_662Test.expected, _662Test.test.widthOfBinaryTree(_662Test.root));
    }

    @Test
    public void test2() {
        _662Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 3, null, 5, 3));
        _662Test.expected = 2;
        TestCase.assertEquals(_662Test.expected, _662Test.test.widthOfBinaryTree(_662Test.root));
    }

    @Test
    public void test3() {
        _662Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 3, 2, 5));
        _662Test.expected = 2;
        TestCase.assertEquals(_662Test.expected, _662Test.test.widthOfBinaryTree(_662Test.root));
    }

    @Test
    public void test5() {
        _662Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1));
        _662Test.expected = 1;
        TestCase.assertEquals(_662Test.expected, _662Test.test.widthOfBinaryTree(_662Test.root));
    }
}

