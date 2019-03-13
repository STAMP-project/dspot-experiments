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
package com.hazelcast.map.impl.mapstore.writebehind;


import com.hazelcast.map.ReachedMaxSizeException;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WriteBehindQueueTest extends HazelcastTestSupport {
    @Test
    public void smoke() {
        final WriteBehindQueue<DelayedEntry> queue = createWBQ();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testAddEnd() {
        WriteBehindQueue<DelayedEntry> queue = createWBQ();
        addEnd(1000, queue);
        Assert.assertEquals(1000, queue.size());
    }

    @Test
    public void testAddFront() {
        WriteBehindQueue<DelayedEntry> queue = createWBQ();
        List<DelayedEntry> delayedEntries = createDelayedEntryList(1000);
        queue.addFirst(delayedEntries);
        Assert.assertEquals(1000, queue.size());
    }

    @Test(expected = ReachedMaxSizeException.class)
    public void testWBQMaxSizeException() {
        final WriteBehindQueue<DelayedEntry> queue = createBoundedWBQ();
        // put total 1001 items. Max allowed is 1000
        addEnd(1001, queue);
    }

    @Test(expected = ReachedMaxSizeException.class)
    public void testWBQMaxSizeException_withMultipleWBQ() {
        final AtomicInteger counter = new AtomicInteger(0);
        final WriteBehindQueue<DelayedEntry> queue1 = createBoundedWBQ(counter);
        final WriteBehindQueue<DelayedEntry> queue2 = createBoundedWBQ(counter);
        final WriteBehindQueue<DelayedEntry> queue3 = createBoundedWBQ(counter);
        final WriteBehindQueue<DelayedEntry> queue4 = createBoundedWBQ(counter);
        // put total 1001 items. Max allowed is 1000
        addEnd(10, queue1);
        addEnd(500, queue2);
        addEnd(400, queue3);
        addEnd(91, queue4);
    }

    @Test
    public void testWBQ_counter_is_zero() {
        final AtomicInteger counter = new AtomicInteger(0);
        final WriteBehindQueue<DelayedEntry> queue = createBoundedWBQ(counter);
        addEnd(1000, queue);
        queue.clear();
        Assert.assertEquals(0, counter.intValue());
    }

    @Test
    public void testOffer_thenRemove_thenOffer() {
        final WriteBehindQueue<DelayedEntry> queue = createWBQ();
        addEnd(1000, queue);
        queue.clear();
        addEnd(1000, queue);
        Assert.assertEquals(1000, queue.size());
    }

    @Test
    public void testCounter_offer_thenRemove() {
        final AtomicInteger counter = new AtomicInteger(0);
        final WriteBehindQueue<DelayedEntry> queue = createBoundedWBQ(counter);
        addEnd(1000, queue);
        queue.drainTo(new ArrayList<DelayedEntry>(1000));
        Assert.assertEquals(0, counter.intValue());
    }

    @Test
    public void testClear() {
        final WriteBehindQueue<DelayedEntry> queue = createWBQ();
        queue.clear();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testClearFull() {
        final WriteBehindQueue<DelayedEntry> queue = createWBQ();
        addEnd(1000, queue);
        queue.clear();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testRemoveAll() {
        final WriteBehindQueue<DelayedEntry> queue = createWBQ();
        addEnd(1000, queue);
        queue.clear();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testGet_onCoalescedWBQ_whenCount_smallerThanQueueSize() throws Exception {
        int queueSize = 100;
        int fetchNumberOfEntries = 10;
        WriteBehindQueue<DelayedEntry> wbq = createWBQ();
        testGetWithCount(wbq, queueSize, fetchNumberOfEntries);
    }

    @Test
    public void testGet_onBoundedWBQ_whenCount_smallerThanQueueSize() throws Exception {
        int queueSize = 100;
        int fetchNumberOfEntries = 10;
        WriteBehindQueue<DelayedEntry> wbq = createBoundedWBQ();
        testGetWithCount(wbq, queueSize, fetchNumberOfEntries);
    }

    @Test
    public void testGet_onCoalescedWBQ_whenCount_higherThanQueueSize() throws Exception {
        int queueSize = 100;
        int fetchNumberOfEntries = 10000;
        WriteBehindQueue<DelayedEntry> wbq = createWBQ();
        testGetWithCount(wbq, queueSize, fetchNumberOfEntries);
    }

    @Test
    public void testGet_onBoundedWBQ_whenCount_higherThanQueueSize() throws Exception {
        int queueSize = 100;
        int fetchNumberOfEntries = 10000;
        WriteBehindQueue<DelayedEntry> wbq = createBoundedWBQ();
        testGetWithCount(wbq, queueSize, fetchNumberOfEntries);
    }
}

