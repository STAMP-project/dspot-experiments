/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util;


import org.junit.Assert;
import org.junit.Test;


public class LinkedListTest {
    LinkedList list = null;

    LinkedListNode node1 = null;

    LinkedListNode node2 = null;

    LinkedListNode node3 = null;

    private static class AbstractBaseLinkedListNodeMock extends AbstractBaseLinkedListNode<LinkedListTest.AbstractBaseLinkedListNodeMock> {}

    /* Test method for 'org.kie.util.LinkedList.add(LinkedListNode)' */
    @Test
    public void testAdd() {
        this.list.add(this.node1);
        Assert.assertNull("Node1 previous should be null", this.node1.getPrevious());
        Assert.assertNull("Node1 next should be null", this.node1.getNext());
        Assert.assertSame("First node should be node1", this.list.getFirst(), this.node1);
        Assert.assertSame("Last node should be node1", this.list.getLast(), this.node1);
        this.list.add(this.node2);
        Assert.assertSame("node1 next should be node2", this.node1.getNext(), this.node2);
        Assert.assertSame("node2 previous should be node1", this.node2.getPrevious(), this.node1);
        Assert.assertSame("First node should be node1", this.list.getFirst(), this.node1);
        Assert.assertSame("Last node should be node2", this.list.getLast(), this.node2);
        this.list.add(this.node3);
        Assert.assertSame("node2 next should be node3", this.node2.getNext(), this.node3);
        Assert.assertSame("node3 previous should be node2", this.node3.getPrevious(), this.node2);
        Assert.assertEquals("LinkedList should have 3 nodes", this.list.size(), 3);
        Assert.assertSame("First node should be node1", this.list.getFirst(), this.node1);
        Assert.assertSame("Last node should be node3", this.list.getLast(), this.node3);
    }

    /* Test method for 'org.kie.util.LinkedList.remove(LinkedListNode)' */
    @Test
    public void testRemove() {
        this.list.add(this.node1);
        this.list.add(this.node2);
        this.list.add(this.node3);
        Assert.assertSame("Node2 previous should be node1", this.node2.getPrevious(), this.node1);
        Assert.assertSame("Node2 next should be node3", this.node2.getNext(), this.node3);
        this.list.remove(this.node2);
        Assert.assertNull("Node2 previous should be null", this.node2.getPrevious());
        Assert.assertNull("Node2 next should be null", this.node2.getNext());
        Assert.assertNull("Node1 previous should be null", this.node1.getPrevious());
        Assert.assertSame("Node1 next should be node3", this.node1.getNext(), this.node3);
        this.list.remove(this.node1);
        Assert.assertNull("Node1 previous should be null", this.node1.getPrevious());
        Assert.assertNull("Node1 next should be null", this.node1.getNext());
        Assert.assertNull("Node3 previous should be null", this.node3.getPrevious());
        Assert.assertNull("Node3 next should be null", this.node3.getNext());
        this.list.remove(this.node3);
        Assert.assertNull("Node3 previous should be null", this.node3.getPrevious());
        Assert.assertNull("Node3 next should be null", this.node3.getNext());
    }

    /* Test method for 'org.kie.util.LinkedList.getFirst()' */
    @Test
    public void testGetFirst() {
        Assert.assertNull("Empty list should return null on getFirst()", this.list.getFirst());
        this.list.add(this.node1);
        Assert.assertSame("List should return node1 on getFirst()", this.list.getFirst(), this.node1);
        this.list.add(this.node2);
        Assert.assertSame("List should return node1 on getFirst()", this.list.getFirst(), this.node1);
        this.list.add(this.node3);
        Assert.assertSame("List should return node1 on getFirst()", this.list.getFirst(), this.node1);
    }

    /* Test method for 'org.kie.util.LinkedList.getLast()' */
    @Test
    public void testGetLast() {
        Assert.assertNull("Empty list should return null on getLast()", this.list.getLast());
        this.list.add(this.node1);
        Assert.assertSame("List should return node1 on getLast()", this.list.getLast(), this.node1);
        this.list.add(this.node2);
        Assert.assertSame("List should return node2 on getLast()", this.list.getLast(), this.node2);
        this.list.add(this.node3);
        Assert.assertSame("List should return node3 on getLast()", this.list.getLast(), this.node3);
    }

    /* Test method for 'org.kie.util.LinkedList.removeFirst()' */
    @Test
    public void testRemoveFirst() {
        this.list.add(this.node1);
        this.list.add(this.node2);
        this.list.add(this.node3);
        Assert.assertSame("List should return node1 on getFirst()", this.list.getFirst(), this.node1);
        this.list.removeFirst();
        Assert.assertSame("List should return node2 on getFirst()", this.list.getFirst(), this.node2);
        this.list.removeFirst();
        Assert.assertSame("List should return node3 on getFirst()", this.list.getFirst(), this.node3);
        this.list.removeFirst();
        Assert.assertNull("Empty list should return null on getFirst()", this.list.getFirst());
    }

    /* Test method for 'org.kie.util.LinkedList.removeLast()' */
    @Test
    public void testRemoveLast() {
        this.list.add(this.node1);
        this.list.add(this.node2);
        this.list.add(this.node3);
        Assert.assertSame("List should return node1 on getLast()", this.list.getLast(), this.node3);
        this.list.removeLast();
        Assert.assertSame("List should return node2 on getLast()", this.list.getLast(), this.node2);
        this.list.removeLast();
        Assert.assertSame("List should return node3 on getLast()", this.list.getLast(), this.node1);
        this.list.removeLast();
        Assert.assertNull("Empty list should return null on getLast()", this.list.getLast());
    }

    /* Test method for 'org.kie.util.LinkedList.isEmpty()' */
    @Test
    public void testIsEmpty() {
        Assert.assertTrue("Empty list should return true on isEmpty()", this.list.isEmpty());
        this.list.add(this.node1);
        Assert.assertFalse("Not empty list should return false on isEmpty()", this.list.isEmpty());
    }

    /* Test method for 'org.kie.util.LinkedList.clear()' */
    @Test
    public void testClear() {
        this.list.add(this.node1);
        this.list.add(this.node2);
        this.list.add(this.node3);
        Assert.assertEquals("List size should be 3", this.list.size(), 3);
        this.list.clear();
        Assert.assertEquals("Empty list should have size 0", this.list.size(), 0);
    }

    /* Test method for 'org.kie.util.LinkedList.size()' */
    @Test
    public void testSize() {
        this.list.add(this.node1);
        Assert.assertEquals("LinkedList should have 1 node", this.list.size(), 1);
        this.list.add(this.node2);
        Assert.assertEquals("LinkedList should have 2 nodes", this.list.size(), 2);
        this.list.add(this.node3);
        Assert.assertEquals("LinkedList should have 3 nodes", this.list.size(), 3);
    }

    @Test
    public void testInsertAfter() {
        try {
            this.list.insertAfter(null, this.node1);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Assert.fail("Should NOT raise NPE!");
        }
    }
}

