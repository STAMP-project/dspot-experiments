package com.clearspring.analytics.util;


import org.junit.Assert;


public class TestDoublyLinkedListAmpl {
    private <T> void assertIsEmpty(DoublyLinkedList<T> list) {
        Assert.assertNull(list.tail());
        Assert.assertNull(list.head());
        Assert.assertNull(list.first());
        Assert.assertNull(list.last());
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(0, list.size());
        for (T i : list) {
            Assert.fail((("What is this: " + i) + " ?"));
        }
    }
}

