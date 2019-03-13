/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.util.concurrent;


import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.RequireAssertEnabled;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractConcurrentArrayQueueTest extends HazelcastTestSupport {
    // must be a power of two to work
    static final int CAPACITY = 1 << 2;

    AbstractConcurrentArrayQueue<Integer> queue;

    private List<Integer> emptyList = Collections.emptyList();

    @Test
    public void testAddedCount() {
        Assert.assertEquals(0, queue.addedCount());
        queue.offer(1);
        Assert.assertEquals(1, queue.addedCount());
    }

    @Test
    public void testRemovedCount() {
        queue.offer(1);
        queue.peek();
        Assert.assertEquals(0, queue.removedCount());
        queue.poll();
        Assert.assertEquals(1, queue.removedCount());
    }

    @Test
    public void testCapacity() {
        Assert.assertEquals(AbstractConcurrentArrayQueueTest.CAPACITY, queue.capacity());
    }

    @Test
    public void testRemainingCapacity() {
        Assert.assertEquals(AbstractConcurrentArrayQueueTest.CAPACITY, queue.remainingCapacity());
        queue.offer(1);
        Assert.assertEquals(((AbstractConcurrentArrayQueueTest.CAPACITY) - 1), queue.remainingCapacity());
    }

    @Test
    public void testRemove() {
        queue.offer(23);
        Assert.assertEquals(23, ((int) (queue.remove())));
        Assert.assertEquals(0, queue.size());
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemove_whenEmpty() {
        queue.remove();
    }

    @Test
    public void testElement() {
        queue.offer(23);
        Assert.assertEquals(23, ((int) (queue.element())));
        Assert.assertEquals(1, queue.size());
    }

    @Test(expected = NoSuchElementException.class)
    public void testElement_whenEmpty() {
        queue.element();
    }

    @Test
    public void testIsEmpty_whenEmpty() {
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testIsEmpty_whenNotEmpty() {
        queue.offer(1);
        Assert.assertFalse(queue.isEmpty());
    }

    @Test
    public void testContains_whenContains() {
        queue.offer(23);
        HazelcastTestSupport.assertContains(queue, 23);
    }

    @Test
    public void testContains_whenNotContains() {
        HazelcastTestSupport.assertNotContains(queue, 42);
    }

    @Test
    public void testContains_whenNull() {
        HazelcastTestSupport.assertNotContains(queue, null);
    }

    @Test
    public void testContainsAll_whenContainsAll() {
        queue.offer(1);
        queue.offer(23);
        queue.offer(42);
        queue.offer(95);
        HazelcastTestSupport.assertContainsAll(queue, Arrays.asList(23, 42));
    }

    @Test
    public void testContainsAll_whenNotContainsAll() {
        queue.offer(1);
        queue.offer(95);
        HazelcastTestSupport.assertNotContainsAll(queue, Arrays.asList(23, 42));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIterator() {
        queue.iterator();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToArray() {
        queue.toArray();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToArray_withArray() {
        queue.toArray(new Integer[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove_withObject() {
        queue.remove(1);
    }

    @Test
    public void testAddAll() {
        queue.addAll(Arrays.asList(23, 42));
        Assert.assertEquals(2, queue.size());
        HazelcastTestSupport.assertContains(queue, 23);
        HazelcastTestSupport.assertContains(queue, 42);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddAll_whenOverCapacity_thenThrowException() {
        queue.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll() {
        queue.removeAll(emptyList);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRetainAll() {
        queue.retainAll(emptyList);
    }

    @Test
    public void testClear() {
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.clear();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testOffer() {
        Assert.assertTrue(queue.offer(1));
    }

    @Test
    public void testOffer_whenQueueIsFull_thenReject() {
        for (int i = 0; i < (AbstractConcurrentArrayQueueTest.CAPACITY); i++) {
            queue.offer(i);
        }
        Assert.assertFalse(queue.offer(23));
    }

    @Test
    public void testOffer_whenArrayQueueWasCompletelyFilled_thenUpdateHeadCache() {
        for (int i = 0; i < (AbstractConcurrentArrayQueueTest.CAPACITY); i++) {
            queue.offer(i);
        }
        queue.poll();
        Assert.assertTrue(queue.offer(23));
    }

    @RequireAssertEnabled
    @Test(expected = AssertionError.class)
    public void testOffer_whenNull_thenAssert() {
        queue.offer(null);
    }

    @Test
    public void testPoll() {
        queue.offer(23);
        queue.offer(42);
        int result1 = queue.poll();
        int result2 = queue.poll();
        Assert.assertEquals(23, result1);
        Assert.assertEquals(42, result2);
    }

    @Test
    public void testDrain() {
        for (int i = 0; i < (AbstractConcurrentArrayQueueTest.CAPACITY); i++) {
            queue.offer(i);
        }
        queue.drain(new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                Assert.assertNotNull(integer);
                return true;
            }
        });
    }

    @Test
    public void testDrainTo() {
        testDrainTo(3, 3);
    }

    @Test
    public void testDrainTo_whenLimitIsLargerThanQueue_thenDrainAllElements() {
        testDrainTo(((AbstractConcurrentArrayQueueTest.CAPACITY) + 1), AbstractConcurrentArrayQueueTest.CAPACITY);
    }

    @Test
    public void testDrainTo_whenLimitIsZero_thenDoNothing() {
        testDrainTo(0, 0);
    }

    @Test
    public void testDrainTo_whenLimitIsNegative_thenDoNothing() {
        testDrainTo((-1), 0);
    }
}

