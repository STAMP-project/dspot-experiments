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
package org.apache.hadoop.metrics2.impl;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the half-blocking metrics sink queue
 */
public class TestSinkQueue {
    private static final Logger LOG = LoggerFactory.getLogger(TestSinkQueue.class);

    /**
     * Test common use case
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCommon() throws Exception {
        final SinkQueue.SinkQueue<Integer> q = new SinkQueue.SinkQueue<Integer>(2);
        q.enqueue(1);
        Assert.assertEquals("queue front", 1, ((int) (q.front())));
        Assert.assertEquals("queue back", 1, ((int) (q.back())));
        Assert.assertEquals("element", 1, ((int) (q.dequeue())));
        Assert.assertTrue("should enqueue", q.enqueue(2));
        q.consume(new Consumer<Integer>() {
            @Override
            public void consume(Integer e) {
                Assert.assertEquals("element", 2, ((int) (e)));
            }
        });
        Assert.assertTrue("should enqueue", q.enqueue(3));
        Assert.assertEquals("element", 3, ((int) (q.dequeue())));
        Assert.assertEquals("queue size", 0, q.size());
        Assert.assertEquals("queue front", null, q.front());
        Assert.assertEquals("queue back", null, q.back());
    }

    /**
     * Test blocking when queue is empty
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEmptyBlocking() throws Exception {
        testEmptyBlocking(0);
        testEmptyBlocking(100);
    }

    /**
     * Test nonblocking enqueue when queue is full
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFull() throws Exception {
        final SinkQueue.SinkQueue<Integer> q = new SinkQueue.SinkQueue<Integer>(1);
        q.enqueue(1);
        Assert.assertTrue("should drop", (!(q.enqueue(2))));
        Assert.assertEquals("element", 1, ((int) (q.dequeue())));
        q.enqueue(3);
        q.consume(new Consumer<Integer>() {
            @Override
            public void consume(Integer e) {
                Assert.assertEquals("element", 3, ((int) (e)));
            }
        });
        Assert.assertEquals("queue size", 0, q.size());
    }

    /**
     * Test the consumeAll method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConsumeAll() throws Exception {
        final int capacity = 64;// arbitrary

        final SinkQueue.SinkQueue<Integer> q = new SinkQueue.SinkQueue<Integer>(capacity);
        for (int i = 0; i < capacity; ++i) {
            Assert.assertTrue("should enqueue", q.enqueue(i));
        }
        Assert.assertTrue("should not enqueue", (!(q.enqueue(capacity))));
        final Runnable trigger = Mockito.mock(Runnable.class);
        q.consumeAll(new Consumer<Integer>() {
            private int expected = 0;

            @Override
            public void consume(Integer e) {
                Assert.assertEquals("element", ((expected)++), ((int) (e)));
                trigger.run();
            }
        });
        Mockito.verify(trigger, Mockito.times(capacity)).run();
    }

    /**
     * Test the consumer throwing exceptions
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConsumerException() throws Exception {
        final SinkQueue.SinkQueue<Integer> q = new SinkQueue.SinkQueue<Integer>(1);
        final RuntimeException ex = new RuntimeException("expected");
        q.enqueue(1);
        try {
            q.consume(new Consumer<Integer>() {
                @Override
                public void consume(Integer e) {
                    throw ex;
                }
            });
        } catch (Exception expected) {
            Assert.assertSame("consumer exception", ex, expected);
        }
        // The queue should be in consistent state after exception
        Assert.assertEquals("queue size", 1, q.size());
        Assert.assertEquals("element", 1, ((int) (q.dequeue())));
    }

    /**
     * Test the clear method
     */
    @Test
    public void testClear() {
        final SinkQueue.SinkQueue<Integer> q = new SinkQueue.SinkQueue<Integer>(128);
        for (int i = 0; i < ((q.capacity()) + 97); ++i) {
            q.enqueue(i);
        }
        Assert.assertEquals("queue size", q.capacity(), q.size());
        q.clear();
        Assert.assertEquals("queue size", 0, q.size());
    }

    /**
     * Test consumers that take their time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHangingConsumer() throws Exception {
        SinkQueue.SinkQueue<Integer> q = newSleepingConsumerQueue(2, 1, 2);
        Assert.assertEquals("queue back", 2, ((int) (q.back())));
        Assert.assertTrue("should drop", (!(q.enqueue(3))));// should not block

        Assert.assertEquals("queue size", 2, q.size());
        Assert.assertEquals("queue head", 1, ((int) (q.front())));
        Assert.assertEquals("queue back", 2, ((int) (q.back())));
    }

    /**
     * Test concurrent consumer access, which is illegal
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConcurrentConsumers() throws Exception {
        final SinkQueue.SinkQueue<Integer> q = newSleepingConsumerQueue(2, 1);
        Assert.assertTrue("should enqueue", q.enqueue(2));
        Assert.assertEquals("queue back", 2, ((int) (q.back())));
        Assert.assertTrue("should drop", (!(q.enqueue(3))));// should not block

        shouldThrowCME(new TestSinkQueue.Fun() {
            @Override
            public void run() {
                q.clear();
            }
        });
        shouldThrowCME(new TestSinkQueue.Fun() {
            @Override
            public void run() throws Exception {
                q.consume(null);
            }
        });
        shouldThrowCME(new TestSinkQueue.Fun() {
            @Override
            public void run() throws Exception {
                q.consumeAll(null);
            }
        });
        shouldThrowCME(new TestSinkQueue.Fun() {
            @Override
            public void run() throws Exception {
                q.dequeue();
            }
        });
        // The queue should still be in consistent state after all the exceptions
        Assert.assertEquals("queue size", 2, q.size());
        Assert.assertEquals("queue front", 1, ((int) (q.front())));
        Assert.assertEquals("queue back", 2, ((int) (q.back())));
    }

    static interface Fun {
        void run() throws Exception;
    }
}

