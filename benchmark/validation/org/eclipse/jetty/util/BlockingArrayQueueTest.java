/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.util.HashSet;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;


public class BlockingArrayQueueTest {
    @Test
    public void testWrap() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(3);
        Assertions.assertEquals(0, queue.size());
        for (int i = 0; i < (queue.getMaxCapacity()); i++) {
            queue.offer("one");
            Assertions.assertEquals(1, queue.size());
            queue.offer("two");
            Assertions.assertEquals(2, queue.size());
            queue.offer("three");
            Assertions.assertEquals(3, queue.size());
            Assertions.assertEquals("one", queue.get(0));
            Assertions.assertEquals("two", queue.get(1));
            Assertions.assertEquals("three", queue.get(2));
            Assertions.assertEquals("[one, two, three]", queue.toString());
            Assertions.assertEquals("one", queue.poll());
            Assertions.assertEquals(2, queue.size());
            Assertions.assertEquals("two", queue.poll());
            Assertions.assertEquals(1, queue.size());
            Assertions.assertEquals("three", queue.poll());
            Assertions.assertEquals(0, queue.size());
            queue.offer("xxx");
            Assertions.assertEquals(1, queue.size());
            Assertions.assertEquals("xxx", queue.poll());
            Assertions.assertEquals(0, queue.size());
        }
    }

    @Test
    public void testRemove() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(3, 3);
        queue.add("0");
        queue.add("x");
        for (int i = 1; i < 100; i++) {
            queue.add(("" + i));
            queue.add("x");
            queue.remove(((queue.size()) - 3));
            queue.set(((queue.size()) - 3), ((queue.get(((queue.size()) - 3))) + "!"));
        }
        for (int i = 0; i < 99; i++)
            Assertions.assertEquals((i + "!"), queue.get(i));

    }

    @Test
    public void testLimit() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(1, 0, 1);
        String element = "0";
        Assertions.assertTrue(queue.add(element));
        Assertions.assertFalse(queue.offer("1"));
        Assertions.assertEquals(element, queue.poll());
        Assertions.assertTrue(queue.add(element));
    }

    @Test
    public void testGrow() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(3, 2);
        Assertions.assertEquals(3, queue.getCapacity());
        queue.add("a");
        queue.add("a");
        Assertions.assertEquals(2, queue.size());
        Assertions.assertEquals(3, queue.getCapacity());
        queue.add("a");
        queue.add("a");
        Assertions.assertEquals(4, queue.size());
        Assertions.assertEquals(5, queue.getCapacity());
        int s = 5;
        int c = 5;
        queue.add("a");
        for (int t = 0; t < 100; t++) {
            Assertions.assertEquals(s, queue.size());
            Assertions.assertEquals(c, queue.getCapacity());
            for (int i = queue.size(); (i--) > 0;)
                queue.poll();

            Assertions.assertEquals(0, queue.size());
            Assertions.assertEquals(c, queue.getCapacity());
            for (int i = queue.getCapacity(); (i--) > 0;)
                queue.add("a");

            queue.add("a");
            Assertions.assertEquals((s + 1), queue.size());
            Assertions.assertEquals((c + 2), queue.getCapacity());
            queue.poll();
            queue.add("a");
            queue.add("a");
            Assertions.assertEquals((s + 2), queue.size());
            Assertions.assertEquals((c + 2), queue.getCapacity());
            s += 2;
            c += 2;
        }
    }

    // TODO: SLOW, needs review
    @Test
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testTake() throws Exception {
        final String[] data = new String[4];
        final BlockingArrayQueue<String> queue = new BlockingArrayQueue();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    data[0] = queue.take();
                    data[1] = queue.take();
                    Thread.sleep(1000);
                    data[2] = queue.take();
                    data[3] = queue.poll(100, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assertions.fail("Should not had failed");
                }
            }
        };
        thread.start();
        Thread.sleep(1000);
        queue.offer("zero");
        queue.offer("one");
        queue.offer("two");
        thread.join();
        Assertions.assertEquals("zero", data[0]);
        Assertions.assertEquals("one", data[1]);
        Assertions.assertEquals("two", data[2]);
        Assertions.assertEquals(null, data[3]);
    }

    // TODO: SLOW, needs review
    @Test
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testConcurrentAccess() throws Exception {
        final int THREADS = 50;
        final int LOOPS = 1000;
        final BlockingArrayQueue<Integer> queue = new BlockingArrayQueue((1 + (THREADS * LOOPS)));
        final ConcurrentLinkedQueue<Integer> produced = new ConcurrentLinkedQueue<>();
        final ConcurrentLinkedQueue<Integer> consumed = new ConcurrentLinkedQueue<>();
        final AtomicBoolean running = new AtomicBoolean(true);
        // start consumers
        final CyclicBarrier barrier0 = new CyclicBarrier((THREADS + 1));
        for (int i = 0; i < THREADS; i++) {
            new Thread() {
                @Override
                public void run() {
                    final Random random = new Random();
                    setPriority(((getPriority()) - 1));
                    try {
                        while (running.get()) {
                            int r = 1 + (random.nextInt(10));
                            if ((r % 2) == 0) {
                                Integer msg = queue.poll();
                                if (msg == null) {
                                    Thread.sleep((1 + (random.nextInt(10))));
                                    continue;
                                }
                                consumed.add(msg);
                            } else {
                                Integer msg = queue.poll(r, TimeUnit.MILLISECONDS);
                                if (msg != null)
                                    consumed.add(msg);

                            }
                        } 
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            barrier0.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        // start producers
        final CyclicBarrier barrier1 = new CyclicBarrier((THREADS + 1));
        for (int i = 0; i < THREADS; i++) {
            final int id = i;
            new Thread() {
                @Override
                public void run() {
                    final Random random = new Random();
                    try {
                        for (int j = 0; j < LOOPS; j++) {
                            Integer msg = random.nextInt();
                            produced.add(msg);
                            if (!(queue.offer(msg)))
                                throw new Exception(((id + " FULL! ") + (queue.size())));

                            Thread.sleep((1 + (random.nextInt(10))));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            barrier1.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        barrier1.await();
        int size = queue.size();
        int last = size - 1;
        while ((size > 0) && (size != last)) {
            last = size;
            Thread.sleep(500);
            size = queue.size();
        } 
        running.set(false);
        barrier0.await();
        HashSet<Integer> prodSet = new HashSet<>(produced);
        HashSet<Integer> consSet = new HashSet<>(consumed);
        Assertions.assertEquals(prodSet, consSet);
    }

    @Test
    public void testRemoveObjectFromEmptyQueue() {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(4, 0, 4);
        Assertions.assertFalse(queue.remove("SOMETHING"));
    }

    @Test
    public void testRemoveObjectWithWrappedTail() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(6);
        // Wrap the tail
        for (int i = 0; i < (queue.getMaxCapacity()); ++i)
            queue.offer(("" + i));

        // Advance the head
        queue.poll();
        // Remove from the middle
        Assertions.assertTrue(queue.remove("2"));
        // Advance the tail
        Assertions.assertTrue(queue.offer("A"));
        Assertions.assertTrue(queue.offer("B"));
        queue.poll();
        // Remove from the middle
        Assertions.assertTrue(queue.remove("3"));
    }

    @Test
    public void testRemoveObject() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(4, 0, 4);
        String element1 = "A";
        Assertions.assertTrue(queue.offer(element1));
        Assertions.assertTrue(queue.remove(element1));
        for (int i = 0; i < ((queue.getMaxCapacity()) - 1); ++i) {
            queue.offer(("" + i));
            queue.poll();
        }
        String element2 = "B";
        Assertions.assertTrue(queue.offer(element2));
        Assertions.assertTrue(queue.offer(element1));
        Assertions.assertTrue(queue.remove(element1));
        Assertions.assertFalse(queue.remove("NOT_PRESENT"));
        Assertions.assertTrue(queue.remove(element2));
        Assertions.assertFalse(queue.remove("NOT_PRESENT"));
        queue.clear();
        for (int i = 0; i < (queue.getMaxCapacity()); ++i)
            queue.offer(("" + i));

        Assertions.assertTrue(queue.remove(("" + ((queue.getMaxCapacity()) - 1))));
    }

    @Test
    public void testRemoveWithMaxCapacityOne() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(1);
        String element = "A";
        Assertions.assertTrue(queue.offer(element));
        Assertions.assertTrue(queue.remove(element));
        Assertions.assertTrue(queue.offer(element));
        Assertions.assertEquals(element, queue.remove(0));
    }

    @Test
    public void testIteratorWithModification() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(4, 0, 4);
        int count = (queue.getMaxCapacity()) - 1;
        for (int i = 0; i < count; ++i)
            queue.offer(("" + i));

        int sum = 0;
        for (String element : queue) {
            ++sum;
            // Concurrent modification, must not change the iterator
            queue.remove(element);
        }
        Assertions.assertEquals(count, sum);
        Assertions.assertTrue(queue.isEmpty());
    }

    @Test
    public void testListIterator() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(4, 0, 4);
        String element1 = "A";
        String element2 = "B";
        queue.offer(element1);
        queue.offer(element2);
        ListIterator<String> iterator = queue.listIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
        String element = iterator.next();
        Assertions.assertEquals(element1, element);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.hasPrevious());
        element = iterator.next();
        Assertions.assertEquals(element2, element);
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertTrue(iterator.hasPrevious());
        element = iterator.previous();
        Assertions.assertEquals(element2, element);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.hasPrevious());
        element = iterator.previous();
        Assertions.assertEquals(element1, element);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
    }

    @Test
    public void testListIteratorWithWrappedHead() throws Exception {
        BlockingArrayQueue<String> queue = new BlockingArrayQueue(4, 0, 4);
        // This sequence of offers and polls wraps the head around the array
        queue.offer("0");
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        queue.poll();
        queue.poll();
        String element1 = queue.get(0);
        String element2 = queue.get(1);
        ListIterator<String> iterator = queue.listIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
        String element = iterator.next();
        Assertions.assertEquals(element1, element);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.hasPrevious());
        element = iterator.next();
        Assertions.assertEquals(element2, element);
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertTrue(iterator.hasPrevious());
        element = iterator.previous();
        Assertions.assertEquals(element2, element);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.hasPrevious());
        element = iterator.previous();
        Assertions.assertEquals(element1, element);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
    }
}

