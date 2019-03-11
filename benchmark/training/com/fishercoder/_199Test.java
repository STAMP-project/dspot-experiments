package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import com.fishercoder.solutions._199;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _199Test {
    private static _199 test;

    private static TreeNode root;

    @Test
    public void test1() {
        _199Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, null, 3));
        Assert.assertEquals(Arrays.asList(1, 3), _199Test.test.rightSideView(_199Test.root));
    }
}

