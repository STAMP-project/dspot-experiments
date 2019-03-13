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
package com.hazelcast.internal.util.concurrent;


import MPSCQueue.BLOCKED;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.concurrent.BusySpinIdleStrategy;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static MPSCQueue.INITIAL_ARRAY_SIZE;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MPSCQueueTest extends HazelcastTestSupport {
    private MPSCQueue<String> queue;

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void setOwningThread_whenNull() {
        MPSCQueue queue = new MPSCQueue(new BusySpinIdleStrategy());
        queue.setConsumerThread(null);
    }

    // ============== poll ==========================================
    @Test
    public void poll() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.offer("1");
        queue.offer("2");
        Assert.assertEquals("1", queue.poll());
        Assert.assertEquals("2", queue.poll());
    }

    @Test
    public void poll_whenEmpty() {
        queue.setConsumerThread(Thread.currentThread());
        Assert.assertNull(queue.poll());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pollWithTimeout_thenUnsupportedOperation() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.poll(1, TimeUnit.SECONDS);
    }

    // ============== take ==========================================
    @Test
    public void take_whenItemAvailable() throws Exception {
        queue.setConsumerThread(Thread.currentThread());
        queue.offer("1");
        queue.offer("2");
        Assert.assertEquals("1", queue.take());
        Assert.assertEquals("2", queue.take());
    }

    @Test(expected = InterruptedException.class)
    public void take_whenInterruptedWhileWaiting() throws Exception {
        final Thread owningThread = Thread.currentThread();
        queue.setConsumerThread(owningThread);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepSeconds(3);
                owningThread.interrupt();
            }
        });
        queue.take();
    }

    @Test
    public void take_whenItemAvailableAfterSomeBlocking() throws Exception {
        queue.setConsumerThread(Thread.currentThread());
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepSeconds(3);
                queue.offer("1");
            }
        });
        Object item = queue.take();
        Assert.assertEquals("1", item);
    }

    /**
     * A test that verifies if the array is expanded.
     */
    @Test
    public void take_whenManyItems() throws Exception {
        queue.setConsumerThread(Thread.currentThread());
        int count = (INITIAL_ARRAY_SIZE) * 10;
        for (int k = 0; k < count; k++) {
            queue.add(("item" + k));
        }
        for (int k = 0; k < count; k++) {
            Assert.assertEquals(("item" + k), queue.take());
        }
        Assert.assertEquals(0, queue.size());
    }

    // ============= isEmpty ====================================
    @Test
    public void isEmpty_whenEmpty() {
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void isEmpty_whenSomeItemsOnPutStack() throws InterruptedException {
        queue.put("item1");
        Assert.assertFalse(queue.isEmpty());
        queue.put("item2");
        Assert.assertFalse(queue.isEmpty());
    }

    @Test
    public void isEmpty_whenSomeItemsOnTakeStack() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.put("item1");
        queue.put("item2");
        queue.put("item3");
        queue.take();
        Assert.assertFalse(queue.isEmpty());
    }

    @Test
    public void isEmpty_whenSomeItemsOnTakeStackAndSomeOnPutStack() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.put("item1");
        queue.put("item2");
        queue.put("item3");
        queue.take();
        queue.put("item4");
        queue.put("item5");
        Assert.assertFalse(queue.isEmpty());
    }

    // ============= size ====================================
    @Test
    public void size_whenEmpty() {
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void size_whenSomeItemsOnPutStack() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.put("item1");
        Assert.assertEquals(1, queue.size());
        queue.put("item2");
        Assert.assertEquals(2, queue.size());
    }

    @Test
    public void size_whenSomeItemsOnTakeStack() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.put("item1");
        queue.put("item2");
        queue.put("item3");
        queue.take();
        Assert.assertEquals(2, queue.size());
    }

    @Test
    public void size_whenSomeItemsOnTakeStackAndSomeOnPutStack() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.put("item1");
        queue.put("item2");
        queue.put("item3");
        queue.take();
        queue.put("item4");
        queue.put("item5");
        Assert.assertEquals(4, queue.size());
    }

    @Test
    public void size_whenTakeStackEmptyAgain() throws InterruptedException {
        queue.setConsumerThread(Thread.currentThread());
        queue.put("item1");
        queue.put("item2");
        queue.put("item3");
        queue.take();
        queue.take();
        queue.take();
        Assert.assertEquals(0, queue.size());
    }

    // ============= offer ====================================
    @Test
    public void offer_withTimeout() throws InterruptedException {
        Assert.assertTrue(queue.offer("item1", 1, TimeUnit.MINUTES));
        Assert.assertTrue(queue.offer("item2", 1, TimeUnit.MINUTES));
        Assert.assertTrue(queue.offer("item3", 3, TimeUnit.MINUTES));
        Assert.assertEquals(3, queue.size());
    }

    @Test
    public void offer_noTimeout() throws InterruptedException {
        Assert.assertTrue(queue.offer("item1"));
        Assert.assertTrue(queue.offer("item2"));
        Assert.assertTrue(queue.offer("item3"));
        Assert.assertEquals(3, queue.size());
    }

    // ============= drain ====================================
    @Test(expected = UnsupportedOperationException.class)
    public void drain() {
        queue.drainTo(new LinkedList<String>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void drainMaxItems() {
        queue.drainTo(new LinkedList<String>(), 10);
    }

    // ============= clear ====================================
    @Test
    public void clear_whenEmpty() {
        queue.setConsumerThread(Thread.currentThread());
        queue.clear();
        Assert.assertEquals(0, queue.size());
        Assert.assertSame(BLOCKED, queue.putStack.get());
    }

    @Test
    public void clear_whenThreadWaiting() {
        queue.setConsumerThread(Thread.currentThread());
        queue.putStack.set(BLOCKED);
        queue.clear();
        Assert.assertEquals(0, queue.size());
        Assert.assertSame(BLOCKED, queue.putStack.get());
    }

    @Test
    public void clear_whenItemsOnPutStack() {
        queue.setConsumerThread(Thread.currentThread());
        queue.offer("1");
        queue.offer("2");
        queue.clear();
        Assert.assertEquals(0, queue.size());
        Assert.assertSame(BLOCKED, queue.putStack.get());
    }

    @Test
    public void clear_whenItemsOnTakeStack() throws Exception {
        queue.setConsumerThread(Thread.currentThread());
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        // copies the putStack into the takeStack
        queue.take();
        queue.clear();
        // since 1 item was taken, 2 items are remaining
        Assert.assertEquals(2, queue.size());
        Assert.assertSame(BLOCKED, queue.putStack.get());
    }

    @Test
    public void clear_whenItemsOnBothStacks() throws Exception {
        queue.setConsumerThread(Thread.currentThread());
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        // copies the putStack into the takeStack
        queue.take();
        queue.offer("5");
        queue.offer("6");
        queue.clear();
        // since 1 item was taken, 2 items are remaining
        Assert.assertEquals(2, queue.size());
        Assert.assertSame(BLOCKED, queue.putStack.get());
    }

    // ============= misc ====================================
    @Test
    public void when_peek_then_getButNotRemove() {
        queue.offer("1");
        Assert.assertEquals("1", queue.peek());
        Assert.assertEquals("1", queue.peek());
    }

    @Test
    public void when_peekEmpty_then_Null() {
        Assert.assertNull(queue.peek());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iterator_whenCalled_thenUnsupportedOperationException() {
        queue.iterator();
    }

    @Test
    public void remainingCapacity() {
        Assert.assertEquals(Integer.MAX_VALUE, queue.remainingCapacity());
    }
}

