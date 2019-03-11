package com.fishercoder;


import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import com.fishercoder.solutions._25;
import org.junit.Assert;
import org.junit.Test;


public class _25Test {
    private static _25 test;

    private static ListNode actual;

    private static ListNode expected;

    private static ListNode head;

    @Test
    public void test1() {
        _25Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 4, 5 });
        _25Test.actual = _25Test.test.reverseKGroup(_25Test.head, 2);
        _25Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 2, 1, 4, 3, 5 });
        Assert.assertEquals(_25Test.actual, _25Test.expected);
    }
}

