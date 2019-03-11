package com.fishercoder;


import _445.Solution2;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import com.fishercoder.solutions._445;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/13/17.
 */
public class _445Test {
    private static _445 test;

    private static Solution2 solution2;

    @Test
    public void test1() {
        ListNode l1 = LinkedListUtils.contructLinkedList(new int[]{ 7, 2, 4, 3 });
        ListNode l2 = LinkedListUtils.contructLinkedList(new int[]{ 5, 6, 4 });
        ListNode expected = LinkedListUtils.contructLinkedList(new int[]{ 7, 8, 0, 7 });
        Assert.assertEquals(expected, _445Test.test.addTwoNumbers(l1, l2));
    }

    @Test
    public void test2() {
        ListNode l1 = LinkedListUtils.contructLinkedList(new int[]{ 7, 2, 4, 3 });
        ListNode l2 = LinkedListUtils.contructLinkedList(new int[]{ 5, 6, 4 });
        ListNode expected = LinkedListUtils.contructLinkedList(new int[]{ 7, 8, 0, 7 });
        Assert.assertEquals(expected, _445Test.solution2.addTwoNumbers(l1, l2));
    }

    @Test
    public void test3() {
        ListNode l1 = LinkedListUtils.contructLinkedList(new int[]{ 5 });
        ListNode l2 = LinkedListUtils.contructLinkedList(new int[]{ 5 });
        ListNode expected = LinkedListUtils.contructLinkedList(new int[]{ 1, 0 });
        Assert.assertEquals(expected, _445Test.solution2.addTwoNumbers(l1, l2));
    }
}

