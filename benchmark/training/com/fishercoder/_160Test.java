package com.fishercoder;


import _160.Solution1;
import _160.Solution2;
import _160.Solution3;
import com.fishercoder.common.classes.ListNode;
import org.junit.Assert;
import org.junit.Test;


public class _160Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static ListNode headA;

    private static ListNode headB;

    private static ListNode expected;

    @Test
    public void test3() {
        _160Test.headA = new ListNode(3);
        _160Test.headB = new ListNode(2);
        _160Test.headB.next = new ListNode(3);
        _160Test.expected = new ListNode(3);
        Assert.assertEquals(_160Test.expected, _160Test.solution3.getIntersectionNode(_160Test.headA, _160Test.headB));
    }
}

