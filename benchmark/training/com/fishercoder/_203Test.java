package com.fishercoder;


import _203.Solution1;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import org.junit.Assert;
import org.junit.Test;


public class _203Test {
    private static Solution1 solution1;

    private static ListNode head;

    private static ListNode expected;

    @Test
    public void test1() {
        _203Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 6, 3, 4, 5, 6 });
        _203Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 4, 5 });
        Assert.assertEquals(_203Test.expected, _203Test.solution1.removeElements(_203Test.head, 6));
    }
}

