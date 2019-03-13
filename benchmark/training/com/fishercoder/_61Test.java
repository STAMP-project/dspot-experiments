package com.fishercoder;


import _61.Solution1;
import com.fishercoder.common.classes.ListNode;
import junit.framework.Assert;
import org.junit.Test;


public class _61Test {
    private static Solution1 solution1;

    private static ListNode expected;

    private static ListNode actual;

    private static ListNode head;

    private static int k;

    @Test
    public void test1() {
        _61Test.k = 2;
        _61Test.expected = new ListNode(4);
        _61Test.expected.next = new ListNode(5);
        _61Test.expected.next.next = new ListNode(1);
        _61Test.expected.next.next.next = new ListNode(2);
        _61Test.expected.next.next.next.next = new ListNode(3);
        _61Test.head = new ListNode(1);
        _61Test.head.next = new ListNode(2);
        _61Test.head.next.next = new ListNode(3);
        _61Test.head.next.next.next = new ListNode(4);
        _61Test.head.next.next.next.next = new ListNode(5);
        _61Test.actual = _61Test.solution1.rotateRight(_61Test.head, _61Test.k);
        Assert.assertEquals(_61Test.expected, _61Test.actual);
    }
}

