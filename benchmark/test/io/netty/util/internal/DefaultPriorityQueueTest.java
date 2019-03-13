/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DefaultPriorityQueueTest {
    @Test
    public void testPoll() {
        PriorityQueue<DefaultPriorityQueueTest.TestElement> queue = new DefaultPriorityQueue<DefaultPriorityQueueTest.TestElement>(DefaultPriorityQueueTest.TestElementComparator.INSTANCE, 0);
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        DefaultPriorityQueueTest.TestElement a = new DefaultPriorityQueueTest.TestElement(5);
        DefaultPriorityQueueTest.TestElement b = new DefaultPriorityQueueTest.TestElement(10);
        DefaultPriorityQueueTest.TestElement c = new DefaultPriorityQueueTest.TestElement(2);
        DefaultPriorityQueueTest.TestElement d = new DefaultPriorityQueueTest.TestElement(7);
        DefaultPriorityQueueTest.TestElement e = new DefaultPriorityQueueTest.TestElement(6);
        DefaultPriorityQueueTest.assertOffer(queue, a);
        DefaultPriorityQueueTest.assertOffer(queue, b);
        DefaultPriorityQueueTest.assertOffer(queue, c);
        DefaultPriorityQueueTest.assertOffer(queue, d);
        // Remove the first element
        Assert.assertSame(c, queue.peek());
        Assert.assertSame(c, queue.poll());
        Assert.assertEquals(3, queue.size());
        // Test that offering another element preserves the priority queue semantics.
        DefaultPriorityQueueTest.assertOffer(queue, e);
        Assert.assertEquals(4, queue.size());
        Assert.assertSame(a, queue.peek());
        Assert.assertSame(a, queue.poll());
        Assert.assertEquals(3, queue.size());
        // Keep removing the remaining elements
        Assert.assertSame(e, queue.peek());
        Assert.assertSame(e, queue.poll());
        Assert.assertEquals(2, queue.size());
        Assert.assertSame(d, queue.peek());
        Assert.assertSame(d, queue.poll());
        Assert.assertEquals(1, queue.size());
        Assert.assertSame(b, queue.peek());
        Assert.assertSame(b, queue.poll());
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
    }

    @Test
    public void testClear() {
        PriorityQueue<DefaultPriorityQueueTest.TestElement> queue = new DefaultPriorityQueue<DefaultPriorityQueueTest.TestElement>(DefaultPriorityQueueTest.TestElementComparator.INSTANCE, 0);
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        DefaultPriorityQueueTest.TestElement a = new DefaultPriorityQueueTest.TestElement(5);
        DefaultPriorityQueueTest.TestElement b = new DefaultPriorityQueueTest.TestElement(10);
        DefaultPriorityQueueTest.TestElement c = new DefaultPriorityQueueTest.TestElement(2);
        DefaultPriorityQueueTest.TestElement d = new DefaultPriorityQueueTest.TestElement(6);
        DefaultPriorityQueueTest.assertOffer(queue, a);
        DefaultPriorityQueueTest.assertOffer(queue, b);
        DefaultPriorityQueueTest.assertOffer(queue, c);
        DefaultPriorityQueueTest.assertOffer(queue, d);
        queue.clear();
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        // Test that elements can be re-inserted after the clear operation
        DefaultPriorityQueueTest.assertOffer(queue, a);
        Assert.assertSame(a, queue.peek());
        DefaultPriorityQueueTest.assertOffer(queue, b);
        Assert.assertSame(a, queue.peek());
        DefaultPriorityQueueTest.assertOffer(queue, c);
        Assert.assertSame(c, queue.peek());
        DefaultPriorityQueueTest.assertOffer(queue, d);
        Assert.assertSame(c, queue.peek());
    }

    @Test
    public void testClearIgnoringIndexes() {
        PriorityQueue<DefaultPriorityQueueTest.TestElement> queue = new DefaultPriorityQueue<DefaultPriorityQueueTest.TestElement>(DefaultPriorityQueueTest.TestElementComparator.INSTANCE, 0);
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        DefaultPriorityQueueTest.TestElement a = new DefaultPriorityQueueTest.TestElement(5);
        DefaultPriorityQueueTest.TestElement b = new DefaultPriorityQueueTest.TestElement(10);
        DefaultPriorityQueueTest.TestElement c = new DefaultPriorityQueueTest.TestElement(2);
        DefaultPriorityQueueTest.TestElement d = new DefaultPriorityQueueTest.TestElement(6);
        DefaultPriorityQueueTest.TestElement e = new DefaultPriorityQueueTest.TestElement(11);
        DefaultPriorityQueueTest.assertOffer(queue, a);
        DefaultPriorityQueueTest.assertOffer(queue, b);
        DefaultPriorityQueueTest.assertOffer(queue, c);
        DefaultPriorityQueueTest.assertOffer(queue, d);
        queue.clearIgnoringIndexes();
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        // Elements cannot be re-inserted but new ones can.
        try {
            queue.offer(a);
            Assert.fail();
        } catch (IllegalArgumentException t) {
            // expected
        }
        DefaultPriorityQueueTest.assertOffer(queue, e);
        Assert.assertSame(e, queue.peek());
    }

    @Test
    public void testRemoval() {
        DefaultPriorityQueueTest.testRemoval(false);
    }

    @Test
    public void testRemovalTyped() {
        DefaultPriorityQueueTest.testRemoval(true);
    }

    @Test
    public void testZeroInitialSize() {
        PriorityQueue<DefaultPriorityQueueTest.TestElement> queue = new DefaultPriorityQueue<DefaultPriorityQueueTest.TestElement>(DefaultPriorityQueueTest.TestElementComparator.INSTANCE, 0);
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        DefaultPriorityQueueTest.TestElement e = new DefaultPriorityQueueTest.TestElement(1);
        DefaultPriorityQueueTest.assertOffer(queue, e);
        Assert.assertSame(e, queue.peek());
        Assert.assertEquals(1, queue.size());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertSame(e, queue.poll());
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
    }

    @Test
    public void testPriorityChange() {
        PriorityQueue<DefaultPriorityQueueTest.TestElement> queue = new DefaultPriorityQueue<DefaultPriorityQueueTest.TestElement>(DefaultPriorityQueueTest.TestElementComparator.INSTANCE, 0);
        DefaultPriorityQueueTest.assertEmptyQueue(queue);
        DefaultPriorityQueueTest.TestElement a = new DefaultPriorityQueueTest.TestElement(10);
        DefaultPriorityQueueTest.TestElement b = new DefaultPriorityQueueTest.TestElement(20);
        DefaultPriorityQueueTest.TestElement c = new DefaultPriorityQueueTest.TestElement(30);
        DefaultPriorityQueueTest.TestElement d = new DefaultPriorityQueueTest.TestElement(25);
        DefaultPriorityQueueTest.TestElement e = new DefaultPriorityQueueTest.TestElement(23);
        DefaultPriorityQueueTest.TestElement f = new DefaultPriorityQueueTest.TestElement(15);
        queue.add(a);
        queue.add(b);
        queue.add(c);
        queue.add(d);
        queue.add(e);
        queue.add(f);
        e.value = 35;
        queue.priorityChanged(e);
        a.value = 40;
        queue.priorityChanged(a);
        a.value = 31;
        queue.priorityChanged(a);
        d.value = 10;
        queue.priorityChanged(d);
        f.value = 5;
        queue.priorityChanged(f);
        List<DefaultPriorityQueueTest.TestElement> expectedOrderList = new ArrayList<DefaultPriorityQueueTest.TestElement>(queue.size());
        expectedOrderList.addAll(Arrays.asList(a, b, c, d, e, f));
        Collections.sort(expectedOrderList, DefaultPriorityQueueTest.TestElementComparator.INSTANCE);
        Assert.assertEquals(expectedOrderList.size(), queue.size());
        Assert.assertEquals(expectedOrderList.isEmpty(), queue.isEmpty());
        Iterator<DefaultPriorityQueueTest.TestElement> itr = expectedOrderList.iterator();
        while (itr.hasNext()) {
            DefaultPriorityQueueTest.TestElement next = itr.next();
            DefaultPriorityQueueTest.TestElement poll = queue.poll();
            Assert.assertEquals(next, poll);
            itr.remove();
            Assert.assertEquals(expectedOrderList.size(), queue.size());
            Assert.assertEquals(expectedOrderList.isEmpty(), queue.isEmpty());
        } 
    }

    private static final class TestElementComparator implements Serializable , Comparator<DefaultPriorityQueueTest.TestElement> {
        private static final long serialVersionUID = 7930368853384760103L;

        static final DefaultPriorityQueueTest.TestElementComparator INSTANCE = new DefaultPriorityQueueTest.TestElementComparator();

        private TestElementComparator() {
        }

        @Override
        public int compare(DefaultPriorityQueueTest.TestElement o1, DefaultPriorityQueueTest.TestElement o2) {
            return (o1.value) - (o2.value);
        }
    }

    private static final class TestElement implements PriorityQueueNode {
        int value;

        private int priorityQueueIndex = INDEX_NOT_IN_QUEUE;

        TestElement(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof DefaultPriorityQueueTest.TestElement) && ((((DefaultPriorityQueueTest.TestElement) (o)).value) == (value));
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public int priorityQueueIndex(DefaultPriorityQueue queue) {
            return priorityQueueIndex;
        }

        @Override
        public void priorityQueueIndex(DefaultPriorityQueue queue, int i) {
            priorityQueueIndex = i;
        }
    }
}

