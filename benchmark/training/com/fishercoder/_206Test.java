package com.fishercoder;


import _206.Solution1;
import _206.Solution2;
import com.fishercoder.common.classes.ListNode;
import com.fishercoder.common.utils.LinkedListUtils;
import junit.framework.TestCase;
import org.junit.Test;


public class _206Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static ListNode head;

    @Test
    public void test1() {
        _206Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3 });
        TestCase.assertEquals(LinkedListUtils.contructLinkedList(new int[]{ 3, 2, 1 }), _206Test.solution1.reverseList(_206Test.head));
        _206Test.head = LinkedListUtils.contructLinkedList(new int[]{ 1, 2, 3 });
        TestCase.assertEquals(LinkedListUtils.contructLinkedList(new int[]{ 3, 2, 1 }), _206Test.solution2.reverseList(_206Test.head));
    }
}

