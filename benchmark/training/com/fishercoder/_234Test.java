package com.fishercoder;


import _234.Solution1;
import _234.Solution2;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import junit.framework.TestCase;
import org.junit.Test;


public class _234Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static ListNode head;

    @Test
    public void test1() {
        _234Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 2, 1 });
        TestCase.assertEquals(true, _234Test.solution1.isPalindrome(_234Test.head));
        _234Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 2, 1 });
        TestCase.assertEquals(true, _234Test.solution2.isPalindrome(_234Test.head));
    }

    @Test
    public void test2() {
        _234Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 2, 1 });
        TestCase.assertEquals(true, _234Test.solution1.isPalindrome(_234Test.head));
        _234Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 2, 1 });
        TestCase.assertEquals(true, _234Test.solution2.isPalindrome(_234Test.head));
    }
}

