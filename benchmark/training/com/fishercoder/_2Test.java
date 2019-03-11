package com.fishercoder;


import _2.Solution1;
import _2.Solution2;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import org.junit.Assert;
import org.junit.Test;


public class _2Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static ListNode l1;

    private static ListNode l2;

    private static ListNode expected;

    @Test
    public void test1() {
        _2Test.l1 = LinkedListUtils.contructLinkedList(new int[]{ 2, 4, 3 });
        _2Test.l2 = LinkedListUtils.contructLinkedList(new int[]{ 5, 6, 4 });
        _2Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 7, 0, 8 });
        Assert.assertEquals(_2Test.expected, _2Test.solution2.addTwoNumbers(_2Test.l1, _2Test.l2));
        Assert.assertEquals(_2Test.expected, _2Test.solution1.addTwoNumbers(_2Test.l1, _2Test.l2));
    }

    @Test
    public void test2() {
        _2Test.l1 = LinkedListUtils.contructLinkedList(new int[]{ 1, 8 });
        _2Test.l2 = LinkedListUtils.contructLinkedList(new int[]{ 0 });
        _2Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 1, 8 });
        Assert.assertEquals(_2Test.expected, _2Test.solution1.addTwoNumbers(_2Test.l1, _2Test.l2));
        Assert.assertEquals(_2Test.expected, _2Test.solution2.addTwoNumbers(_2Test.l1, _2Test.l2));
    }

    @Test
    public void test3() {
        _2Test.l1 = LinkedListUtils.contructLinkedList(new int[]{ 5 });
        _2Test.l2 = LinkedListUtils.contructLinkedList(new int[]{ 5 });
        _2Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 0, 1 });
        Assert.assertEquals(_2Test.expected, _2Test.solution1.addTwoNumbers(_2Test.l1, _2Test.l2));
        Assert.assertEquals(_2Test.expected, _2Test.solution2.addTwoNumbers(_2Test.l1, _2Test.l2));
    }
}

