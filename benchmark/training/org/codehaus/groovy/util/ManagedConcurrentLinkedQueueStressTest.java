/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.util;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import org.apache.groovy.stress.util.GCUtils;
import org.apache.groovy.stress.util.ThreadUtils;
import org.junit.Assert;
import org.junit.Test;


public class ManagedConcurrentLinkedQueueStressTest {
    static final int ENTRY_COUNT = 8196;

    static final ReferenceBundle bundle = ReferenceBundle.getWeakBundle();

    ManagedConcurrentLinkedQueue<Object> queue = new ManagedConcurrentLinkedQueue<Object>(ManagedConcurrentLinkedQueueStressTest.bundle);

    @Test
    public void testQueueRemovesCollectedEntries() {
        // Keep a hardref so we can test get later
        List<Object> elements = populate();
        Assert.assertEquals("should contain all entries", ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT, queue.values().size());
        Object o = elements.get(((ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT) / 2));
        Assert.assertTrue("should contain an element", queue.values().contains(o));
        o = null;
        elements.remove(0);
        GCUtils.gc();
        Assert.assertEquals("should have one less element", ((ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT) - 1), queue.values().size());
        elements.clear();
        GCUtils.gc();
        // Add an entries to force ReferenceManager.removeStaleEntries
        Object last = new Object();
        queue.add(last);
        Assert.assertEquals("should only contain last added", 1, queue.values().size());
    }

    @Test
    public void testQueueRemovesCollectedEntriesOnIteration() {
        List<Object> elements = populate();
        Assert.assertEquals("should contain all entries", ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT, queue.values().size());
        elements.clear();
        GCUtils.gc();
        Assert.assertFalse("Iterator should remove collected elements", queue.iterator().hasNext());
    }

    @Test
    public void testQueueIterationManyThreadsWithRemove() throws Exception {
        List<Object> elements = populate();
        Assert.assertEquals("should contain all entries", ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT, queue.values().size());
        multipleIterateAndRemove(8, ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT);
    }

    @Test
    public void testQueueIterationManyThreadsWithRemoveWithGC() throws Exception {
        List<Object> elements = populate();
        Assert.assertEquals("should contain all entries", ManagedConcurrentLinkedQueueStressTest.ENTRY_COUNT, queue.values().size());
        // Remove some refs so GC will work in order to test multiple iterating threads
        // removing collected references
        int i = 0;
        for (Iterator<Object> itr = elements.iterator(); itr.hasNext();) {
            itr.next();
            if (((i++) % 8) == 0) {
                itr.remove();
            }
        }
        GCUtils.gc();
        multipleIterateAndRemove(8, elements.size());
    }

    @Test
    public void testQueueRemoveCalledByMultipleThreadsOnSameElement() throws Exception {
        final Object value1 = new Object();
        final Object value2 = new Object();
        queue.add(value1);
        queue.add(value2);
        final int threadCount = 8;
        final CyclicBarrier barrier = new CyclicBarrier((threadCount + 1));
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Iterator<Object> itr = queue.iterator();
                    Object o = itr.next();
                    Assert.assertEquals(value1, o);
                    ThreadUtils.await(barrier);
                    itr.remove();
                    ThreadUtils.await(barrier);
                }
            });
            t.setDaemon(true);
            t.start();
        }
        ThreadUtils.await(barrier);// start

        barrier.await(1L, TimeUnit.MINUTES);
        Iterator<Object> itr = queue.iterator();
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(value2, itr.next());
        Assert.assertFalse(itr.hasNext());
    }
}

