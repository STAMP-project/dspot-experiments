package com.fishercoder;


import _82.Solution1;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import org.junit.Assert;
import org.junit.Test;


public class _82Test {
    private static Solution1 solution1;

    private static ListNode head;

    private static ListNode expected;

    @Test
    public void test1() {
        _82Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 3, 4, 4, 5 });
        _82Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 5 });
        Assert.assertEquals(_82Test.expected, _82Test.solution1.deleteDuplicates(_82Test.head));
    }

    @Test
    public void test2() {
        _82Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 1, 1, 2, 3 });
        _82Test.expected = LinkedListUtils.contructLinkedList(new int[]{ 2, 3 });
        Assert.assertEquals(_82Test.expected, _82Test.solution1.deleteDuplicates(_82Test.head));
    }
}

