package com.fishercoder;


import _876.Solution1;
import com.fishercoder.common.classes.ListNode;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _876Test {
    private static Solution1 solution1;

    private static ListNode head;

    private static ListNode middle;

    @Test
    public void test1() {
        _876Test.head = ListNode.createSinglyLinkedList(Arrays.asList(1, 2, 3, 4, 5));
        _876Test.middle = _876Test.solution1.middleNode(_876Test.head);
        Assert.assertEquals(_876Test.middle, ListNode.createSinglyLinkedList(Arrays.asList(3, 4, 5)));
    }

    @Test
    public void test2() {
        _876Test.head = ListNode.createSinglyLinkedList(Arrays.asList(1, 2, 3, 4, 5, 6));
        _876Test.middle = _876Test.solution1.middleNode(_876Test.head);
        Assert.assertEquals(_876Test.middle, ListNode.createSinglyLinkedList(Arrays.asList(4, 5, 6)));
    }
}

