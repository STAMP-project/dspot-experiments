package com.fishercoder;


import _109.Solution1;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.classes.TreeNode;
import com.fishercoder.common.utils.LinkedListUtils;
import com.fishercoder.common.utils.TreeUtils;
import java.util.Arrays;
import org.junit.Test;


public class _109Test {
    private static Solution1 solution1;

    private static ListNode head;

    private static TreeNode expected;

    @Test
    public void test1() {
        _109Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 4, 5 });
        _109Test.expected = TreeUtils.constructBinaryTree(Arrays.asList(3, 1, 4, null, 2, null, 5));
        /**
         * as long as it's a height-balanced tree, it's good for this problem requirement
         */
        TreeUtils.printBinaryTree(_109Test.solution1.sortedListToBST(_109Test.head));
    }
}

