package com.fishercoder;


import _83.Solution1;
import _83.Solution2;
import com.fishercoder.common.classes.ListNode;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/18/17.
 */
public class _83Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static ListNode head;

    private static ListNode expected;

    @Test
    public void test1() {
        _83Test.head = ListNode.createSinglyLinkedList(Arrays.asList(1, 1, 2, 3, 3));
        _83Test.expected = ListNode.createSinglyLinkedList(Arrays.asList(1, 2, 3));
        Assert.assertEquals(_83Test.expected, _83Test.solution1.deleteDuplicates(_83Test.head));
    }

    @Test
    public void test2() {
        _83Test.head = ListNode.createSinglyLinkedList(Arrays.asList(1, 1, 2, 3, 3));
        _83Test.expected = ListNode.createSinglyLinkedList(Arrays.asList(1, 2, 3));
        Assert.assertEquals(_83Test.expected, _83Test.solution2.deleteDuplicates(_83Test.head));
    }
}

