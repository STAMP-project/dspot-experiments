package com.fishercoder;


import _725.Solution1;
import _725.Solution2;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import org.junit.Test;


public class _725Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static ListNode root;

    private static int k;

    private static ListNode[] actual;

    @Test
    public void test1() {
        _725Test.root = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3 });
        _725Test.k = 5;
        _725Test.actual = _725Test.solution1.splitListToParts(_725Test.root, _725Test.k);
        for (ListNode head : _725Test.actual) {
            ListNode.printList(head);
        }
    }

    @Test
    public void test2() {
        _725Test.root = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        _725Test.k = 3;
        _725Test.actual = _725Test.solution1.splitListToParts(_725Test.root, _725Test.k);
        for (ListNode head : _725Test.actual) {
            ListNode.printList(head);
        }
    }

    @Test
    public void test3() {
        _725Test.root = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3 });
        _725Test.k = 5;
        _725Test.actual = _725Test.solution2.splitListToParts(_725Test.root, _725Test.k);
        for (ListNode head : _725Test.actual) {
            ListNode.printList(head);
        }
    }

    @Test
    public void test4() {
        _725Test.root = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        _725Test.k = 3;
        _725Test.actual = _725Test.solution2.splitListToParts(_725Test.root, _725Test.k);
        for (ListNode head : _725Test.actual) {
            ListNode.printList(head);
        }
    }
}

