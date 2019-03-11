package com.fishercoder;


import _617.Solution1;
import _617.Solution2;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by stevesun on 6/10/17.
 */
public class _617Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static TreeNode t1;

    private static TreeNode t2;

    private static TreeNode actual;

    private static TreeNode expected;

    @Test
    public void test1() {
        _617Test.t1 = TreeUtils.constructBinaryTree(Arrays.asList(1, 3, 2, 5));
        _617Test.t2 = TreeUtils.constructBinaryTree(Arrays.asList(2, 1, 3, null, 4, null, 7));
        _617Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(3, 4, 5, 5, 4, null, 7));
        _617Test.actual = _617Test.solution1.mergeTrees(_617Test.t1, _617Test.t2);
        TestCase.assertEquals(_617Test.expected, _617Test.actual);
    }

    @Test
    public void test2() {
        _617Test.t1 = TreeUtils.constructBinaryTree(Arrays.asList(1, 3, 2, 5));
        _617Test.t2 = TreeUtils.constructBinaryTree(Arrays.asList(2, 1, 3, null, 4, null, 7));
        _617Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(3, 4, 5, 5, 4, null, 7));
        _617Test.actual = _617Test.solution2.mergeTrees(_617Test.t1, _617Test.t2);
        TestCase.assertEquals(_617Test.expected, _617Test.actual);
    }
}

