/**
 * Copyright 2018 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util.concurrent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ArrayBlockingQueueTest {
    @Test
    public void constructed() {
        List<Integer> list = Arrays.asList(2, 3, 5);
        try {
            new ArrayBlockingQueue<>(2, false, list);
            Assert.fail("IAE expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new ArrayBlockingQueue<>(0);
            Assert.fail("IAE expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new ArrayBlockingQueue<>(1, false, null);
            Assert.fail("IAE expected");
        } catch (NullPointerException e) {
            // Expected
        }
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5, false, list);
        Assert.assertEquals(3, queue.size());
        Assert.assertEquals(2, queue.remainingCapacity());
        Assert.assertEquals(2, queue.poll().intValue());
        Assert.assertEquals(3, queue.poll().intValue());
        Assert.assertEquals(5, queue.poll().intValue());
        Assert.assertNull(queue.poll());
    }

    @Test
    public void singleThread() {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        Assert.assertEquals(1, queue.poll().intValue());
        queue.add(4);
        Assert.assertEquals(2, queue.poll().intValue());
        Assert.assertEquals(3, queue.poll().intValue());
        Assert.assertEquals(4, queue.poll().intValue());
        Assert.assertNull(queue.poll());
        queue.add(5);
        Assert.assertEquals(5, queue.poll().intValue());
    }

    @Test
    public void blockingAddition() throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
        Assert.assertTrue(queue.offer(1));
        Assert.assertFalse(queue.offer(2));
        new Thread(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                // Do nothing
            }
            queue.poll();
        }).start();
        long start = System.currentTimeMillis();
        queue.put(3);
        long end = System.currentTimeMillis();
        Assert.assertTrue((((start + 50) < end) && ((start + 250) > end)));
        Assert.assertEquals(3, queue.remove().intValue());
    }

    @Test
    public void blockingRemoval() throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        Assert.assertNull(queue.poll());
        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // Do nothing
            }
            queue.add(1);
            queue.add(2);
        }).start();
        long start = System.currentTimeMillis();
        int a = queue.take();
        long end = System.currentTimeMillis();
        int b = queue.take();
        Assert.assertTrue((((start + 100) < end) && ((start + 500) > end)));
        Assert.assertEquals(1, a);
        Assert.assertEquals(2, b);
    }

    @Test
    public void shiftQueueSize() {
        Assert.assertEquals(6, shiftQueue().size());
    }

    @Test
    public void remove() {
        BlockingQueue<Integer> queue = simpleQueue();
        Assert.assertTrue(queue.remove(2));
        Assert.assertEquals(2, queue.size());
        Assert.assertEquals(1, queue.poll().intValue());
        Assert.assertEquals(3, queue.poll().intValue());
        Assert.assertNull(queue.poll());
        queue = shiftQueue();
        Assert.assertTrue(queue.remove(1));
        Assert.assertEquals(5, queue.size());
        Assert.assertEquals(0, queue.poll().intValue());
        Assert.assertEquals(2, queue.poll().intValue());
        Assert.assertEquals(3, queue.poll().intValue());
        Assert.assertEquals(4, queue.poll().intValue());
        Assert.assertEquals(5, queue.poll().intValue());
        Assert.assertNull(queue.poll());
        queue = shiftQueue();
        Assert.assertTrue(queue.remove(4));
        Assert.assertEquals(5, queue.size());
        Assert.assertEquals(0, queue.poll().intValue());
        Assert.assertEquals(1, queue.poll().intValue());
        Assert.assertEquals(2, queue.poll().intValue());
        Assert.assertEquals(3, queue.poll().intValue());
        Assert.assertEquals(5, queue.poll().intValue());
        Assert.assertNull(queue.poll());
    }

    @Test
    public void dumpsToArray() {
        BlockingQueue<Integer> queue = simpleQueue();
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3 }, queue.toArray());
        queue = shiftQueue();
        Assert.assertArrayEquals(new Integer[]{ 0, 1, 2, 3, 4, 5 }, queue.toArray());
    }

    @Test
    public void dumpsToTypedArray() {
        BlockingQueue<Integer> queue = simpleQueue();
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3 }, queue.toArray(new Integer[3]));
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3 }, queue.toArray(new Integer[1]));
        Integer[] array = new Integer[]{ 10, 11, 12, 13, 14, 15 };
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3, null, 14, 15 }, queue.toArray(array));
    }

    @Test
    public void drains() {
        BlockingQueue<Integer> queue = simpleQueue();
        List<Integer> target = new ArrayList<>();
        Assert.assertEquals(2, queue.drainTo(target, 2));
        Assert.assertEquals(Arrays.asList(1, 2), target);
        queue = simpleQueue();
        target.clear();
        Assert.assertEquals(3, queue.drainTo(target, 4));
        Assert.assertEquals(Arrays.asList(1, 2, 3), target);
        queue = shiftQueue();
        target.clear();
        Assert.assertEquals(2, queue.drainTo(target, 2));
        Assert.assertEquals(Arrays.asList(0, 1), target);
        queue = shiftQueue();
        target.clear();
        Assert.assertEquals(4, queue.drainTo(target, 4));
        Assert.assertEquals(Arrays.asList(0, 1, 2, 3), target);
    }

    @Test
    public void iterator() {
        BlockingQueue<Integer> queue = simpleQueue();
        Assert.assertEquals(Arrays.asList(1, 2, 3), new ArrayList<>(queue));
        queue = shiftQueue();
        Assert.assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), new ArrayList<>(queue));
    }
}

