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
package com.hazelcast.collection.impl.queue;


import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IQueue;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public abstract class QueueAbstractTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected IAtomicLong atomicLong;

    private IQueue<String> queue;

    private QueueConfig queueConfig;

    // ================ offer ==============================
    @Test
    public void testOffer() throws Exception {
        int count = 100;
        for (int i = 0; i < count; i++) {
            queue.offer(("item" + i));
        }
        Assert.assertEquals(100, queue.size());
    }

    @Test
    public void testOffer_whenFull() {
        for (int i = 0; i < (queueConfig.getMaxSize()); i++) {
            queue.offer(("item" + i));
        }
        boolean accepted = queue.offer("rejected");
        Assert.assertFalse(accepted);
        Assert.assertEquals(queueConfig.getMaxSize(), queue.size());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testOffer_whenNullArgument() {
        try {
            queue.offer(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testOfferWithTimeout() {
        QueueAbstractTest.OfferThread offerThread = new QueueAbstractTest.OfferThread(queue);
        for (int i = 0; i < (queueConfig.getMaxSize()); i++) {
            queue.offer(("item" + i));
        }
        Assert.assertFalse(queue.offer("rejected"));
        offerThread.start();
        queue.poll();
        HazelcastTestSupport.assertSizeEventually(queueConfig.getMaxSize(), queue);
        HazelcastTestSupport.assertContains(queue, "waiting");
    }

    // ================ poll ==============================
    @Test
    public void testPoll() {
        int count = 100;
        for (int i = 0; i < count; i++) {
            queue.offer(("item" + i));
        }
        queue.poll();
        queue.poll();
        queue.poll();
        queue.poll();
        Assert.assertEquals(96, queue.size());
    }

    @Test
    public void testPoll_whenQueueEmpty() {
        Assert.assertNull(queue.poll());
    }

    // ================ poll with timeout ==============================
    @Test
    public void testPollWithTimeout() throws Exception {
        QueueAbstractTest.PollThread pollThread = new QueueAbstractTest.PollThread(queue);
        pollThread.start();
        queue.offer("offer");
        queue.offer("remain");
        HazelcastTestSupport.assertSizeEventually(1, queue);
        HazelcastTestSupport.assertContains(queue, "remain");
    }

    // ================ remove ==============================
    @Test
    public void testRemove() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        Assert.assertTrue(queue.remove("item4"));
        Assert.assertEquals(queue.size(), 9);
    }

    @Test
    public void testRemove_whenElementNotExists() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        Assert.assertFalse(queue.remove("item13"));
        Assert.assertEquals(10, queue.size());
    }

    @Test
    public void testRemove_whenQueueEmpty() {
        Assert.assertFalse(queue.remove("not in Queue"));
    }

    @Test
    public void testRemove_whenArgNull() {
        queue.add("foo");
        try {
            queue.remove(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(1, queue.size());
    }

    // ================ drainTo ==============================
    @Test
    public void testDrainTo() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>(10);
        Assert.assertEquals(10, queue.drainTo(list));
        Assert.assertEquals(10, list.size());
        Assert.assertEquals("item0", list.get(0));
        Assert.assertEquals("item5", list.get(5));
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testDrainTo_whenQueueEmpty() {
        List<String> list = new ArrayList<String>();
        Assert.assertEquals(0, queue.drainTo(list));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testDrainTo_whenCollectionNull() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        try {
            queue.drainTo(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(10, queue.size());
    }

    @Test
    public void testDrainToWithMaxElement() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>(10);
        queue.drainTo(list, 4);
        Assert.assertEquals(4, list.size());
        HazelcastTestSupport.assertContains(list, "item3");
        Assert.assertEquals(6, queue.size());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testDrainToWithMaxElement_whenCollectionNull() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        try {
            queue.drainTo(null, 4);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(10, queue.size());
    }

    @Test
    public void testDrainToWithMaxElement_whenMaxArgNegative() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>();
        Assert.assertEquals(10, queue.drainTo(list, (-4)));
        Assert.assertEquals(0, queue.size());
    }

    // ================ contains ==============================
    @Test
    public void testContains_whenExists() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        HazelcastTestSupport.assertContains(queue, "item4");
        HazelcastTestSupport.assertContains(queue, "item8");
    }

    @Test
    public void testContains_whenNotExists() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        HazelcastTestSupport.assertNotContains(queue, "item10");
        HazelcastTestSupport.assertNotContains(queue, "item19");
    }

    // ================ containsAll ==============================
    @Test
    public void testAddAll_whenCollectionContainsNull() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>();
        list.add("item10");
        list.add(null);
        try {
            queue.addAll(list);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
    }

    @Test
    public void testContainsAll_whenExists() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        HazelcastTestSupport.assertContainsAll(queue, list);
    }

    @Test
    public void testContainsAll_whenNoneExists() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>();
        list.add("item10");
        list.add("item11");
        list.add("item12");
        HazelcastTestSupport.assertNotContainsAll(queue, list);
    }

    @Test
    public void testContainsAll_whenSomeExists() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>();
        list.add("item1");
        list.add("item2");
        list.add("item14");
        list.add("item13");
        HazelcastTestSupport.assertNotContainsAll(queue, list);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void testContainsAll_whenNull() {
        queue.containsAll(null);
    }

    // ================ addAll ==============================
    @Test
    public void testAddAll() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.add(("item" + i));
        }
        Assert.assertTrue(queue.addAll(list));
        Assert.assertEquals(queue.size(), 10);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testAddAll_whenNullCollection() {
        try {
            queue.addAll(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testAddAll_whenEmptyCollection() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        Assert.assertEquals(10, queue.size());
        Assert.assertTrue(queue.addAll(Collections.<String>emptyList()));
        Assert.assertEquals(10, queue.size());
    }

    @Test
    public void testAddAll_whenDuplicateItems() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        List<String> list = new ArrayList<String>();
        list.add("item3");
        HazelcastTestSupport.assertContains(queue, "item3");
        queue.addAll(list);
        Assert.assertEquals(11, queue.size());
    }

    // ================ retainAll ==============================
    @Test
    public void testRetainAll() {
        queue.add("item3");
        queue.add("item4");
        queue.add("item5");
        List<String> arrayList = new ArrayList<String>();
        arrayList.add("item3");
        arrayList.add("item4");
        arrayList.add("item31");
        Assert.assertTrue(queue.retainAll(arrayList));
        Assert.assertEquals(queue.size(), 2);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testRetainAll_whenCollectionNull() {
        queue.add("item3");
        queue.add("item4");
        queue.add("item5");
        try {
            queue.retainAll(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(3, queue.size());
    }

    @Test
    public void testRetainAll_whenCollectionEmpty() {
        queue.add("item3");
        queue.add("item4");
        queue.add("item5");
        Assert.assertTrue(queue.retainAll(Collections.emptyList()));
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testRetainAll_whenCollectionContainsNull() {
        queue.add("item3");
        queue.add("item4");
        queue.add("item5");
        List<String> list = new ArrayList<String>();
        list.add(null);
        Assert.assertTrue(queue.retainAll(list));
        Assert.assertEquals(0, queue.size());
    }

    // ================ removeAll ==============================
    @Test
    public void testRemoveAll() {
        queue.add("item3");
        queue.add("item4");
        queue.add("item5");
        List<String> arrayList = new ArrayList<String>();
        arrayList.add("item3");
        arrayList.add("item4");
        arrayList.add("item5");
        Assert.assertTrue(queue.removeAll(arrayList));
        Assert.assertEquals(queue.size(), 0);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void testRemoveAll_whenCollectionNull() {
        queue.removeAll(null);
    }

    @Test
    public void testRemoveAll_whenCollectionEmpty() {
        queue.add("item3");
        queue.add("item4");
        queue.add("item5");
        Assert.assertFalse(queue.removeAll(Collections.<String>emptyList()));
        Assert.assertEquals(3, queue.size());
    }

    // ================ toArray ==============================
    @Test
    public void testToArray() {
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        Object[] array = queue.toArray();
        for (int i = 0; i < (array.length); i++) {
            Object o = array[i];
            Assert.assertEquals(o, ("item" + (i++)));
        }
        String[] arr = new String[5];
        arr = queue.toArray(arr);
        Assert.assertEquals(arr.length, 10);
        for (int i = 0; i < (arr.length); i++) {
            Object o = arr[i];
            Assert.assertEquals(o, ("item" + (i++)));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testQueueRemoveFromIterator() {
        queue.add("one");
        Iterator<String> iterator = queue.iterator();
        iterator.next();
        iterator.remove();
    }

    private static class OfferThread extends Thread {
        IQueue<String> queue;

        OfferThread(IQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                queue.offer("waiting", 1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PollThread extends Thread {
        IQueue queue;

        PollThread(IQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                queue.poll(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

