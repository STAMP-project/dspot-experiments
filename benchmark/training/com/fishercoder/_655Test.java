package com.fishercoder;


import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.CommonUtils;
import com.fishercoder.common.utils.TreeUtils;
import com.fishercoder.solutions._655;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _655Test {
    private static List<List<String>> expected;

    private static TreeNode root;

    private static _655 test;

    @Test
    public void test1() {
        _655Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2));
        _655Test.expected = new ArrayList<>(2);
        List<String> row1 = new ArrayList<>(3);
        row1.add("");
        row1.add("1");
        row1.add("");
        _655Test.expected.add(row1);
        List<String> row2 = new ArrayList<>(3);
        row2.add(0, "2");
        row2.add("");
        row2.add("");
        _655Test.expected.add(row2);
        CommonUtils.printListList(_655Test.expected);
        Assert.assertEquals(_655Test.expected, _655Test.test.printTree(_655Test.root));
    }

    @Test
    public void test2() {
        _655Test.root = TreeUtils.constructBinaryTree(Arrays.asList(1, 2, 3, null, 4));
        TreeUtils.printBinaryTree(_655Test.root);
        CommonUtils.printListList(_655Test.test.printTree(_655Test.root));
    }

    @Test
    public void test3() {
        _655Test.root = TreeUtils.constructBinaryTree(Arrays.asList(3, null, 30, 10, null, null, 15, null, 45));
        TreeUtils.printBinaryTree(_655Test.root);
        CommonUtils.printListList(_655Test.test.printTree(_655Test.root));
        System.out.println(_655Test.test.printTree(_655Test.root));
    }
}

