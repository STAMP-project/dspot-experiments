/**
 * Copyright (C) 2011 Clearspring Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.clearspring.analytics.util;


public class TestDoublyLinkedListAmpl {
    @org.junit.Test
    public void testDoublyLinkedList() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        assertIsEmpty(list);
    }

    @org.junit.Test
    public void testAdd() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        list.add(1);
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(1, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 }, list.toArray());
        list.add(2);
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(2, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 }, list.toArray());
        list.add(3);
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(3, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 , 3 }, list.toArray());
        org.junit.Assert.assertEquals(new java.lang.Integer(1), list.first());
    }

    @org.junit.Test
    public void testAddNode() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        list.add(new com.clearspring.analytics.util.ListNode2<java.lang.Integer>(1));
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(1, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 }, list.toArray());
        list.add(new com.clearspring.analytics.util.ListNode2<java.lang.Integer>(2));
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(2, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 }, list.toArray());
        list.add(new com.clearspring.analytics.util.ListNode2<java.lang.Integer>(3));
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(3, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 , 3 }, list.toArray());
        org.junit.Assert.assertEquals(new java.lang.Integer(1), list.first());
    }

    @org.junit.Test
    public void testAddAfter() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        list.add(1);
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> node2 = list.add(2);
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> node4 = list.add(4);
        list.addAfter(node2, 3);
        org.junit.Assert.assertEquals(4, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 , 3 , 4 }, list.toArray());
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> node5 = list.addAfter(node4, 5);
        org.junit.Assert.assertEquals(5, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 , 3 , 4 , 5 }, list.toArray());
        org.junit.Assert.assertEquals(new java.lang.Integer(5), list.last());
        org.junit.Assert.assertEquals(node5, list.head());
    }

    @org.junit.Test
    public void testRemove() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> node1 = list.add(1);
        list.remove(node1);
        node1 = list.add(1);
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> node2 = list.add(2);
        list.remove(node1);
        org.junit.Assert.assertEquals(1, list.size());
        org.junit.Assert.assertEquals(new java.lang.Integer(2), list.first());
        org.junit.Assert.assertEquals(node2, list.head());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 2 }, list.toArray());
        list.remove(node2);
        assertIsEmpty(list);
        node1 = list.add(1);
        node2 = list.add(2);
        list.remove(node2);
        org.junit.Assert.assertEquals(1, list.size());
        org.junit.Assert.assertEquals(new java.lang.Integer(1), list.first());
        org.junit.Assert.assertEquals(node1, list.head());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 }, list.toArray());
        node2 = list.add(2);
        list.add(3);
        org.junit.Assert.assertEquals(3, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 2 , 3 }, list.toArray());
        list.remove(node2);
        org.junit.Assert.assertEquals(2, list.size());
        org.junit.Assert.assertEquals(node1, list.tail());
        org.junit.Assert.assertEquals(new java.lang.Integer(3), list.last());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 , 3 }, list.toArray());
    }

    private <T> void assertIsEmpty(com.clearspring.analytics.util.DoublyLinkedList<T> list) {
        org.junit.Assert.assertNull(list.tail());
        org.junit.Assert.assertNull(list.head());
        org.junit.Assert.assertNull(list.first());
        org.junit.Assert.assertNull(list.last());
        org.junit.Assert.assertTrue(list.isEmpty());
        org.junit.Assert.assertEquals(0, list.size());
        for (T i : list) {
            org.junit.Assert.fail((("What is this: " + i) + " ?"));
        }
    }

    @org.junit.Test(expected = java.util.ConcurrentModificationException.class)
    public void testConcurrentModification() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification__3 = list.add(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__3).getValue(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__3).getPrev());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__3).getNext());
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification__4 = list.add(2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__4).getValue(), 2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__4).getNext());
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification__5 = list.add(3);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__5).getNext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__5).getValue(), 3);
        for (int i : list) {
            if (i == 2) {
                // AssertGenerator replace invocation
                com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification__11 = list.add(4);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__11).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification__11).getValue(), 4);
            }
        }
    }

    @org.junit.Test
    public void testEnqueue() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testEnqueue__3 = list.enqueue(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__3).getNext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__3).getPrev());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__3).getValue(), 1);
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(1, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 1 }, list.toArray());
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testEnqueue__10 = list.enqueue(2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__10).getValue(), 2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__10).getPrev());
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(2, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 2 , 1 }, list.toArray());
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testEnqueue__17 = list.enqueue(3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__17).getValue(), 3);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testEnqueue__17).getPrev());
        org.junit.Assert.assertFalse(list.isEmpty());
        org.junit.Assert.assertEquals(3, list.size());
        org.junit.Assert.assertArrayEquals(new java.lang.Integer[]{ 3 , 2 , 1 }, list.toArray());
        org.junit.Assert.assertEquals(new java.lang.Integer(3), list.first());
        org.junit.Assert.assertEquals(new java.lang.Integer(1), list.last());
    }
}

