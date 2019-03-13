/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.jobmanager.scheduler;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the basic functionality of the {@link LifoSetQueue}.
 */
public class LifoSetQueueTest {
    @Test
    public void testSizeAddPollAndPeek() {
        try {
            LifoSetQueue<Integer> queue = new LifoSetQueue<Integer>();
            // empty queue
            Assert.assertEquals(0, queue.size());
            Assert.assertNull(queue.poll());
            Assert.assertNull(queue.peek());
            // add some elements
            Assert.assertTrue(queue.add(1));
            Assert.assertTrue(queue.offer(2));
            Assert.assertTrue(queue.offer(3));
            Assert.assertEquals(3, queue.size());
            Assert.assertEquals(3, queue.peek().intValue());
            // prevent duplicates. note that the methods return true, because no capacity constraint is violated
            Assert.assertTrue(queue.add(1));
            Assert.assertTrue(queue.offer(1));
            Assert.assertTrue(queue.add(3));
            Assert.assertTrue(queue.offer(3));
            Assert.assertTrue(queue.add(2));
            Assert.assertTrue(queue.offer(2));
            Assert.assertEquals(3, queue.size());
            // peek and poll some elements
            Assert.assertEquals(3, queue.peek().intValue());
            Assert.assertEquals(3, queue.size());
            Assert.assertEquals(3, queue.poll().intValue());
            Assert.assertEquals(2, queue.size());
            Assert.assertEquals(2, queue.peek().intValue());
            Assert.assertEquals(2, queue.size());
            Assert.assertEquals(2, queue.poll().intValue());
            Assert.assertEquals(1, queue.size());
            Assert.assertEquals(1, queue.peek().intValue());
            Assert.assertEquals(1, queue.size());
            Assert.assertEquals(1, queue.poll().intValue());
            Assert.assertEquals(0, queue.size());
            Assert.assertTrue(queue.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " : ") + (e.getMessage())));
        }
    }

    /**
     * Remove is tricky, because it goes through the iterator and calls remove() on the iterator.
     */
    @Test
    public void testRemove() {
        try {
            LifoSetQueue<String> queue = new LifoSetQueue<String>();
            queue.add("1");
            queue.add("2");
            queue.add("3");
            queue.add("4");
            queue.add("5");
            queue.add("6");
            queue.add("7");
            Assert.assertEquals(7, queue.size());
            Assert.assertEquals("7", queue.peek());
            // remove non-existing
            Assert.assertFalse(queue.remove("8"));
            // remove the last
            Assert.assertTrue(queue.remove("7"));
            // remove the first
            Assert.assertTrue(queue.remove("1"));
            // remove in the middle
            Assert.assertTrue(queue.remove("3"));
            Assert.assertEquals(4, queue.size());
            // check that we can re-add the removed elements
            Assert.assertTrue(queue.add("1"));
            Assert.assertTrue(queue.add("7"));
            Assert.assertTrue(queue.add("3"));
            Assert.assertEquals(7, queue.size());
            // check the order
            Assert.assertEquals("3", queue.poll());
            Assert.assertEquals("7", queue.poll());
            Assert.assertEquals("1", queue.poll());
            Assert.assertEquals("6", queue.poll());
            Assert.assertEquals("5", queue.poll());
            Assert.assertEquals("4", queue.poll());
            Assert.assertEquals("2", queue.poll());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " : ") + (e.getMessage())));
        }
    }
}

