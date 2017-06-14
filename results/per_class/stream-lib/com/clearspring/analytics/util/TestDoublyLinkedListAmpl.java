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

    /* amplification of com.clearspring.analytics.util.TestDoublyLinkedList#testConcurrentModification */
    @org.junit.Test(expected = java.util.ConcurrentModificationException.class)
    public void testConcurrentModification_literalMutation24482() {
        com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
        // AssertGenerator replace invocation
        com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_literalMutation24482__3 = list.add(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24482__3).getNext());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24482__3).getValue(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24482__3).getPrev());
        list.add(2);
        list.add(3);
        for (int i : list) {
            if (i == 2) {
                list.add(4);
            }
        }
    }

    /* amplification of com.clearspring.analytics.util.TestDoublyLinkedList#testConcurrentModification */
    @org.junit.Test(timeout = 10000)
    public void testConcurrentModification_literalMutation24490_cf24952_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = 0;
            com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
            list.add(1);
            list.add(2);
            // AssertGenerator replace invocation
            com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_literalMutation24490__5 = list.add(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24490__5).getNext();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.util.DoublyLinkedList vc_11466 = (com.clearspring.analytics.util.DoublyLinkedList)null;
            // StatementAdderMethod cloned existing statement
            vc_11466.first();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24490__5).getValue();
            for (int i : list) {
                if (i == 2) {
                    list.add(4);
                }
            }
            org.junit.Assert.fail("testConcurrentModification_literalMutation24490_cf24952 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.util.TestDoublyLinkedList#testConcurrentModification */
    @org.junit.Test(timeout = 10000)
    public void testConcurrentModification_add24478_failAssert1_literalMutation25648() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
            // AssertGenerator replace invocation
            com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_add24478_failAssert1_literalMutation25648__5 = list.add(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_add24478_failAssert1_literalMutation25648__5).getPrev());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_add24478_failAssert1_literalMutation25648__5).getValue(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_add24478_failAssert1_literalMutation25648__5).getNext());
            // AssertGenerator replace invocation
            com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_add24478_failAssert1_literalMutation25648__7 = // MethodCallAdder
list.add(2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_add24478_failAssert1_literalMutation25648__7).getValue(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_add24478_failAssert1_literalMutation25648__7).getNext());
            list.add(2);
            list.add(3);
            for (int i : list) {
                if (i == 2) {
                    list.add(4);
                }
            }
            org.junit.Assert.fail("testConcurrentModification_add24478 should have thrown ConcurrentModificationException");
        } catch (java.util.ConcurrentModificationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.util.TestDoublyLinkedList#testConcurrentModification */
    @org.junit.Test(timeout = 10000)
    public void testConcurrentModification_literalMutation24492_cf25167_failAssert63_literalMutation28380() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = 6;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_1, 6);
            com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
            // AssertGenerator replace invocation
            com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_literalMutation24492_cf25167_failAssert63_literalMutation28380__7 = list.add(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24492_cf25167_failAssert63_literalMutation28380__7).getPrev());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24492_cf25167_failAssert63_literalMutation28380__7).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24492_cf25167_failAssert63_literalMutation28380__7).getValue(), 0);
            list.add(2);
            // AssertGenerator replace invocation
            com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_literalMutation24492__5 = list.add(6);
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24492__5).getNext();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_7_0);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.util.DoublyLinkedList vc_11578 = (com.clearspring.analytics.util.DoublyLinkedList)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_11578);
            // StatementAdderMethod cloned existing statement
            vc_11578.iterator();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24492__5).getValue();
            for (int i : list) {
                if (i == 2) {
                    list.add(4);
                }
            }
            org.junit.Assert.fail("testConcurrentModification_literalMutation24492_cf25167 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.util.TestDoublyLinkedList#testConcurrentModification */
    @org.junit.Test(timeout = 10000)
    public void testConcurrentModification_literalMutation24490_cf24961_failAssert15_literalMutation27663() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_1, 0);
            com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer> list = new com.clearspring.analytics.util.DoublyLinkedList<java.lang.Integer>();
            list.add(1);
            list.add(2);
            // AssertGenerator replace invocation
            com.clearspring.analytics.util.ListNode2<java.lang.Integer> o_testConcurrentModification_literalMutation24490__5 = list.add(0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24490__5).getNext();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_7_0);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.util.DoublyLinkedList vc_11472 = (com.clearspring.analytics.util.DoublyLinkedList)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_11472);
            // StatementAdderMethod cloned existing statement
            vc_11472.isEmpty();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((com.clearspring.analytics.util.ListNode2)o_testConcurrentModification_literalMutation24490__5).getValue();
            for (int i : list) {
                if (i == 2) {
                    list.add(0);
                }
            }
            org.junit.Assert.fail("testConcurrentModification_literalMutation24490_cf24961 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

