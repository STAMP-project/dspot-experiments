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
package org.apache.hadoop.hbase.util;


import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestBoundedPriorityBlockingQueue {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBoundedPriorityBlockingQueue.class);

    private static final int CAPACITY = 16;

    static class TestObject {
        private final int priority;

        private final int seqId;

        public TestObject(final int priority, final int seqId) {
            this.priority = priority;
            this.seqId = seqId;
        }

        public int getSeqId() {
            return this.seqId;
        }

        public int getPriority() {
            return this.priority;
        }
    }

    static class TestObjectComparator implements Comparator<TestBoundedPriorityBlockingQueue.TestObject> {
        public TestObjectComparator() {
        }

        @Override
        public int compare(TestBoundedPriorityBlockingQueue.TestObject a, TestBoundedPriorityBlockingQueue.TestObject b) {
            return (a.getPriority()) - (b.getPriority());
        }
    }

    private BoundedPriorityBlockingQueue<TestBoundedPriorityBlockingQueue.TestObject> queue;

    @Test
    public void tesAppend() throws Exception {
        // Push
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            Assert.assertTrue(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(i, i)));
            Assert.assertEquals(i, queue.size());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.remainingCapacity());
        }
        Assert.assertFalse(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(0, (-1)), 5, TimeUnit.MILLISECONDS));
        // Pop
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            TestBoundedPriorityBlockingQueue.TestObject obj = queue.poll();
            Assert.assertEquals(i, obj.getSeqId());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.size());
            Assert.assertEquals(i, queue.remainingCapacity());
        }
        Assert.assertEquals(null, queue.poll());
    }

    @Test
    public void tesAppendSamePriority() throws Exception {
        // Push
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            Assert.assertTrue(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(0, i)));
            Assert.assertEquals(i, queue.size());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.remainingCapacity());
        }
        Assert.assertFalse(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(0, (-1)), 5, TimeUnit.MILLISECONDS));
        // Pop
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            TestBoundedPriorityBlockingQueue.TestObject obj = queue.poll();
            Assert.assertEquals(i, obj.getSeqId());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.size());
            Assert.assertEquals(i, queue.remainingCapacity());
        }
        Assert.assertEquals(null, queue.poll());
    }

    @Test
    public void testPrepend() throws Exception {
        // Push
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            Assert.assertTrue(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), i)));
            Assert.assertEquals(i, queue.size());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.remainingCapacity());
        }
        // Pop
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            TestBoundedPriorityBlockingQueue.TestObject obj = queue.poll();
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - (i - 1)), obj.getSeqId());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.size());
            Assert.assertEquals(i, queue.remainingCapacity());
        }
        Assert.assertEquals(null, queue.poll());
    }

    @Test
    public void testInsert() throws Exception {
        // Push
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); i += 2) {
            Assert.assertTrue(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(i, i)));
            Assert.assertEquals(((1 + i) / 2), queue.size());
        }
        for (int i = 2; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); i += 2) {
            Assert.assertTrue(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(i, i)));
            Assert.assertEquals((((TestBoundedPriorityBlockingQueue.CAPACITY) / 2) + (i / 2)), queue.size());
        }
        Assert.assertFalse(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(0, (-1)), 5, TimeUnit.MILLISECONDS));
        // Pop
        for (int i = 1; i <= (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            TestBoundedPriorityBlockingQueue.TestObject obj = queue.poll();
            Assert.assertEquals(i, obj.getSeqId());
            Assert.assertEquals(((TestBoundedPriorityBlockingQueue.CAPACITY) - i), queue.size());
            Assert.assertEquals(i, queue.remainingCapacity());
        }
        Assert.assertEquals(null, queue.poll());
    }

    @Test
    public void testFifoSamePriority() throws Exception {
        Assert.assertTrue(((TestBoundedPriorityBlockingQueue.CAPACITY) >= 6));
        for (int i = 0; i < 6; ++i) {
            Assert.assertTrue(queue.offer(new TestBoundedPriorityBlockingQueue.TestObject(((1 + (i % 2)) * 10), i)));
        }
        for (int i = 0; i < 6; i += 2) {
            TestBoundedPriorityBlockingQueue.TestObject obj = queue.poll();
            Assert.assertEquals(10, obj.getPriority());
            Assert.assertEquals(i, obj.getSeqId());
        }
        for (int i = 1; i < 6; i += 2) {
            TestBoundedPriorityBlockingQueue.TestObject obj = queue.poll();
            Assert.assertEquals(20, obj.getPriority());
            Assert.assertEquals(i, obj.getSeqId());
        }
        Assert.assertEquals(null, queue.poll());
    }

    @Test
    public void testPoll() {
        Assert.assertNull(queue.poll());
        PriorityQueue<TestBoundedPriorityBlockingQueue.TestObject> testList = new PriorityQueue<>(TestBoundedPriorityBlockingQueue.CAPACITY, new TestBoundedPriorityBlockingQueue.TestObjectComparator());
        for (int i = 0; i < (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            TestBoundedPriorityBlockingQueue.TestObject obj = new TestBoundedPriorityBlockingQueue.TestObject(i, i);
            testList.add(obj);
            queue.offer(obj);
        }
        for (int i = 0; i < (TestBoundedPriorityBlockingQueue.CAPACITY); ++i) {
            Assert.assertEquals(testList.poll(), queue.poll());
        }
        Assert.assertNull(null, queue.poll());
    }

    @Test
    public void testPollInExecutor() throws InterruptedException {
        final TestBoundedPriorityBlockingQueue.TestObject testObj = new TestBoundedPriorityBlockingQueue.TestObject(0, 0);
        final CyclicBarrier threadsStarted = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Assert.assertNull(queue.poll(1000, TimeUnit.MILLISECONDS));
                    threadsStarted.await();
                    Assert.assertSame(testObj, queue.poll(1000, TimeUnit.MILLISECONDS));
                    Assert.assertTrue(queue.isEmpty());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    threadsStarted.await();
                    queue.offer(testObj);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        executor.shutdown();
        Assert.assertTrue(executor.awaitTermination(8000, TimeUnit.MILLISECONDS));
    }
}

