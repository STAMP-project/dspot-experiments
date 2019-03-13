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


import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the basic functionality of the {@link LifoSetQueue}.
 */
public class SetQueueTest {
    @Test
    public void testSizeAddPollAndPeek() {
        try {
            Queue<Integer> queue = new SetQueue<Integer>();
            // empty queue
            Assert.assertEquals(0, queue.size());
            Assert.assertNull(queue.poll());
            Assert.assertNull(queue.peek());
            // add some elements
            Assert.assertTrue(queue.add(1));
            Assert.assertTrue(queue.offer(2));
            Assert.assertTrue(queue.offer(3));
            Assert.assertEquals(3, queue.size());
            Assert.assertEquals(1, queue.peek().intValue());
            // prevent duplicates. note that the methods return true, because no capacity constraint is violated
            Assert.assertTrue(queue.add(1));
            Assert.assertTrue(queue.offer(1));
            Assert.assertTrue(queue.add(3));
            Assert.assertTrue(queue.offer(3));
            Assert.assertTrue(queue.add(2));
            Assert.assertTrue(queue.offer(2));
            Assert.assertEquals(3, queue.size());
            // peek and poll some elements
            Assert.assertEquals(1, queue.peek().intValue());
            Assert.assertEquals(3, queue.size());
            Assert.assertEquals(1, queue.poll().intValue());
            Assert.assertEquals(2, queue.size());
            Assert.assertEquals(2, queue.peek().intValue());
            Assert.assertEquals(2, queue.size());
            Assert.assertEquals(2, queue.poll().intValue());
            Assert.assertEquals(1, queue.size());
            Assert.assertEquals(3, queue.peek().intValue());
            Assert.assertEquals(1, queue.size());
            Assert.assertEquals(3, queue.poll().intValue());
            Assert.assertEquals(0, queue.size());
            Assert.assertTrue(queue.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " : ") + (e.getMessage())));
        }
    }

    @Test
    public void testOrder() {
        try {
            Queue<Integer> queue = new SetQueue<Integer>();
            queue.add(1);
            queue.add(2);
            queue.add(3);
            Assert.assertEquals(1, queue.peek().intValue());
            queue.add(4);
            Assert.assertEquals(1, queue.peek().intValue());
            queue.remove(2);
            Assert.assertEquals(1, queue.peek().intValue());
            queue.remove(4);
            Assert.assertEquals(1, queue.peek().intValue());
            queue.remove(2);
            Assert.assertEquals(1, queue.poll().intValue());
            Assert.assertEquals(3, queue.poll().intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + " : ") + (e.getMessage())));
        }
    }
}

