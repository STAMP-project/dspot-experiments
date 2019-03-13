/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.kafka.internals;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ClosableBlockingQueue}.
 */
public class ClosableBlockingQueueTest {
    // ------------------------------------------------------------------------
    // single-threaded unit tests
    // ------------------------------------------------------------------------
    @Test
    public void testCreateQueueHashCodeEquals() {
        try {
            ClosableBlockingQueue<String> queue1 = new ClosableBlockingQueue();
            ClosableBlockingQueue<String> queue2 = new ClosableBlockingQueue(22);
            Assert.assertTrue(queue1.isOpen());
            Assert.assertTrue(queue2.isOpen());
            Assert.assertTrue(queue1.isEmpty());
            Assert.assertTrue(queue2.isEmpty());
            Assert.assertEquals(0, queue1.size());
            Assert.assertEquals(0, queue2.size());
            Assert.assertTrue(((queue1.hashCode()) == (queue2.hashCode())));
            // noinspection EqualsWithItself
            Assert.assertTrue(queue1.equals(queue1));
            // noinspection EqualsWithItself
            Assert.assertTrue(queue2.equals(queue2));
            Assert.assertTrue(queue1.equals(queue2));
            Assert.assertNotNull(queue1.toString());
            Assert.assertNotNull(queue2.toString());
            List<String> elements = new ArrayList<>();
            elements.add("a");
            elements.add("b");
            elements.add("c");
            ClosableBlockingQueue<String> queue3 = new ClosableBlockingQueue(elements);
            ClosableBlockingQueue<String> queue4 = new ClosableBlockingQueue(Arrays.asList("a", "b", "c"));
            Assert.assertTrue(queue3.isOpen());
            Assert.assertTrue(queue4.isOpen());
            Assert.assertFalse(queue3.isEmpty());
            Assert.assertFalse(queue4.isEmpty());
            Assert.assertEquals(3, queue3.size());
            Assert.assertEquals(3, queue4.size());
            Assert.assertTrue(((queue3.hashCode()) == (queue4.hashCode())));
            // noinspection EqualsWithItself
            Assert.assertTrue(queue3.equals(queue3));
            // noinspection EqualsWithItself
            Assert.assertTrue(queue4.equals(queue4));
            Assert.assertTrue(queue3.equals(queue4));
            Assert.assertNotNull(queue3.toString());
            Assert.assertNotNull(queue4.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCloseEmptyQueue() {
        try {
            ClosableBlockingQueue<String> queue = new ClosableBlockingQueue();
            Assert.assertTrue(queue.isOpen());
            Assert.assertTrue(queue.close());
            Assert.assertFalse(queue.isOpen());
            Assert.assertFalse(queue.addIfOpen("element"));
            Assert.assertTrue(queue.isEmpty());
            try {
                queue.add("some element");
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCloseNonEmptyQueue() {
        try {
            ClosableBlockingQueue<Integer> queue = new ClosableBlockingQueue(Arrays.asList(1, 2, 3));
            Assert.assertTrue(queue.isOpen());
            Assert.assertFalse(queue.close());
            Assert.assertFalse(queue.close());
            queue.poll();
            Assert.assertFalse(queue.close());
            Assert.assertFalse(queue.close());
            queue.pollBatch();
            Assert.assertTrue(queue.close());
            Assert.assertFalse(queue.isOpen());
            Assert.assertFalse(queue.addIfOpen(42));
            Assert.assertTrue(queue.isEmpty());
            try {
                queue.add(99);
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPeekAndPoll() {
        try {
            ClosableBlockingQueue<String> queue = new ClosableBlockingQueue();
            Assert.assertNull(queue.peek());
            Assert.assertNull(queue.peek());
            Assert.assertNull(queue.poll());
            Assert.assertNull(queue.poll());
            Assert.assertEquals(0, queue.size());
            queue.add("a");
            queue.add("b");
            queue.add("c");
            Assert.assertEquals(3, queue.size());
            Assert.assertEquals("a", queue.peek());
            Assert.assertEquals("a", queue.peek());
            Assert.assertEquals("a", queue.peek());
            Assert.assertEquals(3, queue.size());
            Assert.assertEquals("a", queue.poll());
            Assert.assertEquals("b", queue.poll());
            Assert.assertEquals(1, queue.size());
            Assert.assertEquals("c", queue.peek());
            Assert.assertEquals("c", queue.peek());
            Assert.assertEquals("c", queue.poll());
            Assert.assertEquals(0, queue.size());
            Assert.assertNull(queue.poll());
            Assert.assertNull(queue.peek());
            Assert.assertNull(queue.peek());
            Assert.assertTrue(queue.close());
            try {
                queue.peek();
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
            try {
                queue.poll();
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPollBatch() {
        try {
            ClosableBlockingQueue<String> queue = new ClosableBlockingQueue();
            Assert.assertNull(queue.pollBatch());
            queue.add("a");
            queue.add("b");
            Assert.assertEquals(Arrays.asList("a", "b"), queue.pollBatch());
            Assert.assertNull(queue.pollBatch());
            queue.add("c");
            Assert.assertEquals(Collections.singletonList("c"), queue.pollBatch());
            Assert.assertNull(queue.pollBatch());
            Assert.assertTrue(queue.close());
            try {
                queue.pollBatch();
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetElementBlocking() {
        try {
            ClosableBlockingQueue<String> queue = new ClosableBlockingQueue();
            Assert.assertNull(queue.getElementBlocking(1));
            Assert.assertNull(queue.getElementBlocking(3));
            Assert.assertNull(queue.getElementBlocking(2));
            Assert.assertEquals(0, queue.size());
            queue.add("a");
            queue.add("b");
            queue.add("c");
            queue.add("d");
            queue.add("e");
            queue.add("f");
            Assert.assertEquals(6, queue.size());
            Assert.assertEquals("a", queue.getElementBlocking(99));
            Assert.assertEquals("b", queue.getElementBlocking());
            Assert.assertEquals(4, queue.size());
            Assert.assertEquals("c", queue.getElementBlocking(0));
            Assert.assertEquals("d", queue.getElementBlocking(1000000));
            Assert.assertEquals("e", queue.getElementBlocking());
            Assert.assertEquals("f", queue.getElementBlocking(1786598));
            Assert.assertEquals(0, queue.size());
            Assert.assertNull(queue.getElementBlocking(1));
            Assert.assertNull(queue.getElementBlocking(3));
            Assert.assertNull(queue.getElementBlocking(2));
            Assert.assertTrue(queue.close());
            try {
                queue.getElementBlocking();
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
            try {
                queue.getElementBlocking(1000000000L);
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetBatchBlocking() {
        try {
            ClosableBlockingQueue<String> queue = new ClosableBlockingQueue();
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(1));
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(3));
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(2));
            queue.add("a");
            queue.add("b");
            Assert.assertEquals(Arrays.asList("a", "b"), queue.getBatchBlocking(900000009));
            queue.add("c");
            queue.add("d");
            Assert.assertEquals(Arrays.asList("c", "d"), queue.getBatchBlocking());
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(2));
            queue.add("e");
            Assert.assertEquals(Collections.singletonList("e"), queue.getBatchBlocking(0));
            queue.add("f");
            Assert.assertEquals(Collections.singletonList("f"), queue.getBatchBlocking(1000000000));
            Assert.assertEquals(0, queue.size());
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(1));
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(3));
            Assert.assertEquals(Collections.emptyList(), queue.getBatchBlocking(2));
            Assert.assertTrue(queue.close());
            try {
                queue.getBatchBlocking();
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
            try {
                queue.getBatchBlocking(1000000000L);
                Assert.fail("should cause an exception");
            } catch (IllegalStateException ignored) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    // multi-threaded tests
    // ------------------------------------------------------------------------
    @Test
    public void notifyOnClose() {
        try {
            final long oneYear = (((365L * 24) * 60) * 60) * 1000;
            // test "getBatchBlocking()"
            final ClosableBlockingQueue<String> queue1 = new ClosableBlockingQueue();
            ClosableBlockingQueueTest.QueueCall call1 = new ClosableBlockingQueueTest.QueueCall() {
                @Override
                public void call() throws Exception {
                    queue1.getBatchBlocking();
                }
            };
            ClosableBlockingQueueTest.testCallExitsOnClose(call1, queue1);
            // test "getBatchBlocking()"
            final ClosableBlockingQueue<String> queue2 = new ClosableBlockingQueue();
            ClosableBlockingQueueTest.QueueCall call2 = new ClosableBlockingQueueTest.QueueCall() {
                @Override
                public void call() throws Exception {
                    queue2.getBatchBlocking(oneYear);
                }
            };
            ClosableBlockingQueueTest.testCallExitsOnClose(call2, queue2);
            // test "getBatchBlocking()"
            final ClosableBlockingQueue<String> queue3 = new ClosableBlockingQueue();
            ClosableBlockingQueueTest.QueueCall call3 = new ClosableBlockingQueueTest.QueueCall() {
                @Override
                public void call() throws Exception {
                    queue3.getElementBlocking();
                }
            };
            ClosableBlockingQueueTest.testCallExitsOnClose(call3, queue3);
            // test "getBatchBlocking()"
            final ClosableBlockingQueue<String> queue4 = new ClosableBlockingQueue();
            ClosableBlockingQueueTest.QueueCall call4 = new ClosableBlockingQueueTest.QueueCall() {
                @Override
                public void call() throws Exception {
                    queue4.getElementBlocking(oneYear);
                }
            };
            ClosableBlockingQueueTest.testCallExitsOnClose(call4, queue4);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testMultiThreadedAddGet() {
        try {
            final ClosableBlockingQueue<Integer> queue = new ClosableBlockingQueue();
            final AtomicReference<Throwable> pushErrorRef = new AtomicReference<>();
            final AtomicReference<Throwable> pollErrorRef = new AtomicReference<>();
            final int numElements = 2000;
            Thread pusher = new Thread("pusher") {
                @Override
                public void run() {
                    try {
                        final Random rnd = new Random();
                        for (int i = 0; i < numElements; i++) {
                            queue.add(i);
                            // sleep a bit, sometimes
                            int sleepTime = rnd.nextInt(3);
                            if (sleepTime > 1) {
                                Thread.sleep(sleepTime);
                            }
                        }
                        while (true) {
                            if (queue.close()) {
                                break;
                            } else {
                                Thread.sleep(5);
                            }
                        } 
                    } catch (Throwable t) {
                        pushErrorRef.set(t);
                    }
                }
            };
            pusher.start();
            Thread poller = new Thread("poller") {
                @SuppressWarnings("InfiniteLoopStatement")
                @Override
                public void run() {
                    try {
                        int count = 0;
                        try {
                            final Random rnd = new Random();
                            int nextExpected = 0;
                            while (true) {
                                int getMethod = count % 7;
                                switch (getMethod) {
                                    case 0 :
                                        {
                                            Integer next = queue.getElementBlocking(1);
                                            if (next != null) {
                                                Assert.assertEquals(nextExpected, next.intValue());
                                                nextExpected++;
                                                count++;
                                            }
                                            break;
                                        }
                                    case 1 :
                                        {
                                            List<Integer> nextList = queue.getBatchBlocking();
                                            for (Integer next : nextList) {
                                                Assert.assertNotNull(next);
                                                Assert.assertEquals(nextExpected, next.intValue());
                                                nextExpected++;
                                                count++;
                                            }
                                            break;
                                        }
                                    case 2 :
                                        {
                                            List<Integer> nextList = queue.getBatchBlocking(1);
                                            if (nextList != null) {
                                                for (Integer next : nextList) {
                                                    Assert.assertNotNull(next);
                                                    Assert.assertEquals(nextExpected, next.intValue());
                                                    nextExpected++;
                                                    count++;
                                                }
                                            }
                                            break;
                                        }
                                    case 3 :
                                        {
                                            Integer next = queue.poll();
                                            if (next != null) {
                                                Assert.assertEquals(nextExpected, next.intValue());
                                                nextExpected++;
                                                count++;
                                            }
                                            break;
                                        }
                                    case 4 :
                                        {
                                            List<Integer> nextList = queue.pollBatch();
                                            if (nextList != null) {
                                                for (Integer next : nextList) {
                                                    Assert.assertNotNull(next);
                                                    Assert.assertEquals(nextExpected, next.intValue());
                                                    nextExpected++;
                                                    count++;
                                                }
                                            }
                                            break;
                                        }
                                    default :
                                        {
                                            Integer next = queue.getElementBlocking();
                                            Assert.assertNotNull(next);
                                            Assert.assertEquals(nextExpected, next.intValue());
                                            nextExpected++;
                                            count++;
                                        }
                                }
                                // sleep a bit, sometimes
                                int sleepTime = rnd.nextInt(3);
                                if (sleepTime > 1) {
                                    Thread.sleep(sleepTime);
                                }
                            } 
                        } catch (IllegalStateException e) {
                            // we get this once the queue is closed
                            Assert.assertEquals(numElements, count);
                        }
                    } catch (Throwable t) {
                        pollErrorRef.set(t);
                    }
                }
            };
            poller.start();
            pusher.join();
            poller.join();
            if ((pushErrorRef.get()) != null) {
                Throwable t = pushErrorRef.get();
                t.printStackTrace();
                Assert.fail(("Error in pusher: " + (t.getMessage())));
            }
            if ((pollErrorRef.get()) != null) {
                Throwable t = pollErrorRef.get();
                t.printStackTrace();
                Assert.fail(("Error in poller: " + (t.getMessage())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private interface QueueCall {
        void call() throws Exception;
    }
}

