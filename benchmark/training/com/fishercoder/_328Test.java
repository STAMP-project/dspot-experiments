package com.fishercoder;


import _328.Solution1;
import com.fishercoder.common.classes.ListNode;
import org.junit.Assert;
import org.junit.Test;


public class _328Test {
    private static Solution1 solution1;

    private static ListNode expected;

    private static ListNode node;

    @Test
    public void test1() {
        _328Test.node = new ListNode(1);
        _328Test.node.next = new ListNode(2);
        _328Test.node.next.next = new ListNode(3);
        _328Test.node.next.next.next = new ListNode(4);
        _328Test.node.next.next.next.next = new ListNode(5);
        _328Test.expected = new ListNode(1);
        _328Test.expected.next = new ListNode(3);
        _328Test.expected.next.next = new ListNode(5);
        _328Test.expected.next.next.next = new ListNode(2);
        _328Test.expected.next.next.next.next = new ListNode(4);
        Assert.assertEquals(_328Test.expected, _328Test.solution1.oddEvenList(_328Test.node));
    }
}

