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
package io.atomix.core.multiset;


import CollectionEvent.Type.ADD;
import CollectionEvent.Type.REMOVE;
import io.atomix.core.AbstractPrimitiveTest;
import io.atomix.core.collection.CollectionEvent;
import io.atomix.core.collection.CollectionEventListener;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Distributed multiset test.
 */
public class DistributedMultisetTest extends AbstractPrimitiveTest {
    @Test
    public void testMultisetOperations() throws Exception {
        DistributedMultiset<String> multiset = atomix().<String>multisetBuilder("test-multiset").withProtocol(protocol()).build();
        Assert.assertEquals(0, multiset.size());
        Assert.assertTrue(multiset.isEmpty());
        Assert.assertFalse(multiset.contains("foo"));
        Assert.assertTrue(multiset.add("foo"));
        Assert.assertTrue(multiset.contains("foo"));
        Assert.assertTrue(multiset.add("foo"));
        Assert.assertTrue(multiset.contains("foo"));
        Assert.assertEquals(2, multiset.size());
        Assert.assertFalse(multiset.isEmpty());
        Assert.assertTrue(multiset.remove("foo"));
        Assert.assertEquals(1, multiset.size());
        Assert.assertEquals(1, multiset.count("foo"));
        Assert.assertEquals(1, multiset.setCount("foo", 2));
        Assert.assertEquals(2, multiset.size());
        Assert.assertEquals(2, multiset.count("foo"));
        Assert.assertFalse(multiset.setCount("foo", 1, 2));
        Assert.assertTrue(multiset.setCount("foo", 2, 3));
        Assert.assertEquals(3, multiset.add("foo", 3));
        Assert.assertEquals(6, multiset.count("foo"));
        Assert.assertEquals(0, multiset.add("bar", 3));
        Assert.assertEquals(3, multiset.count("bar"));
        Assert.assertTrue(multiset.setCount("baz", 0, 1));
        Assert.assertEquals(1, multiset.count("baz"));
        Assert.assertEquals(3, multiset.remove("bar", 3));
        Assert.assertEquals(0, multiset.count("bar"));
        Assert.assertFalse(multiset.contains("bar"));
        Assert.assertEquals(6, multiset.remove("foo", 2));
        Assert.assertEquals(4, multiset.count("foo"));
    }

    @Test
    public void testMultisetViews() throws Exception {
        DistributedMultiset<String> multiset = atomix().<String>multisetBuilder("test-multiset-views").withProtocol(protocol()).build();
        multiset.setCount("foo", 1);
        multiset.setCount("bar", 2);
        multiset.setCount("baz", 3);
        Assert.assertEquals(3, multiset.entrySet().size());
        Assert.assertEquals(3, multiset.elementSet().size());
        Assert.assertEquals(3, multiset.entrySet().stream().count());
        Assert.assertEquals(3, multiset.elementSet().stream().count());
    }

    @Test
    public void testEventListeners() throws Exception {
        DistributedMultiset<String> multiset = atomix().<String>multisetBuilder("test-multiset-listeners").withProtocol(protocol()).build();
        DistributedMultisetTest.TestQueueEventListener listener = new DistributedMultisetTest.TestQueueEventListener();
        CollectionEvent<String> event;
        multiset.addListener(listener);
        Assert.assertTrue(multiset.add("foo"));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertTrue(multiset.addAll(Arrays.asList("foo", "bar", "baz")));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals(2, multiset.count("foo"));
        Assert.assertEquals(2, multiset.setCount("foo", 3));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertEquals(3, multiset.count("foo"));
        Assert.assertEquals(3, multiset.setCount("foo", 2));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertTrue(multiset.setCount("foo", 2, 3));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertTrue(multiset.setCount("foo", 3, 2));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertEquals(2, multiset.add("foo", 2));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertEquals(4, multiset.remove("foo", 2));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertFalse(listener.eventReceived());
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

