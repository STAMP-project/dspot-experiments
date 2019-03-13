/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.core.queue;


import CollectionEvent.Type.ADD;
import CollectionEvent.Type.REMOVE;
import io.atomix.core.AbstractPrimitiveTest;
import io.atomix.core.collection.CollectionEvent;
import io.atomix.core.collection.CollectionEventListener;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Distributed queue test.
 */
public class DistributedQueueTest extends AbstractPrimitiveTest {
    @Test
    public void testQueueOperations() throws Exception {
        DistributedQueue<String> queue = atomix().<String>queueBuilder("test-queue").withProtocol(protocol()).build();
        Assert.assertEquals(0, queue.size());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertFalse(queue.contains("foo"));
        Assert.assertTrue(queue.add("foo"));
        Assert.assertTrue(queue.contains("foo"));
        Assert.assertTrue(queue.add("foo"));
        Assert.assertTrue(queue.contains("foo"));
        Assert.assertEquals(2, queue.size());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.remove("foo"));
        Assert.assertEquals(1, queue.size());
        Assert.assertEquals("foo", queue.remove());
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testEventListeners() throws Exception {
        DistributedQueue<String> queue = atomix().<String>queueBuilder("test-queue-listeners").withProtocol(protocol()).build();
        DistributedQueueTest.TestQueueEventListener listener = new DistributedQueueTest.TestQueueEventListener();
        CollectionEvent<String> event;
        queue.addListener(listener);
        Assert.assertTrue(queue.add("foo"));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertTrue(queue.offer("bar"));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("bar", event.element());
        Assert.assertTrue(queue.addAll(Arrays.asList("foo", "bar", "baz")));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("bar", event.element());
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("baz", event.element());
        Assert.assertEquals("foo", queue.remove());
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertEquals("bar", queue.poll());
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("bar", event.element());
        Assert.assertEquals("foo", queue.element());
        Assert.assertFalse(listener.eventReceived());
        Assert.assertEquals("foo", queue.peek());
        Assert.assertFalse(listener.eventReceived());
        Assert.assertTrue(queue.removeAll(Arrays.asList("foo", "bar", "baz")));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("bar", event.element());
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("baz", event.element());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(0, queue.size());
        Assert.assertNull(queue.peek());
        try {
            queue.element();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
    }

    private static class TestQueueEventListener implements CollectionEventListener<String> {
        private final BlockingQueue<CollectionEvent<String>> queue = new LinkedBlockingQueue<>();

        @Override
        public void event(CollectionEvent<String> event) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public boolean eventReceived() {
            return !(queue.isEmpty());
        }

        public CollectionEvent<String> event() throws InterruptedException {
            return queue.poll(10, TimeUnit.SECONDS);
        }
    }
}

