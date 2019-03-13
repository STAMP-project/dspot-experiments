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
package io.atomix.core.set;


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
 * Distributed set test.
 */
public class DistributedSetTest extends AbstractPrimitiveTest {
    @Test
    public void testSetOperations() throws Exception {
        DistributedSet<String> set = atomix().<String>setBuilder("test-set").withProtocol(protocol()).build();
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        Assert.assertFalse(set.contains("foo"));
        Assert.assertTrue(set.add("foo"));
        Assert.assertTrue(set.contains("foo"));
        Assert.assertFalse(set.add("foo"));
        Assert.assertTrue(set.contains("foo"));
        Assert.assertEquals(1, set.size());
        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.add("bar"));
        Assert.assertTrue(set.remove("foo"));
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.remove("bar"));
        Assert.assertTrue(set.isEmpty());
        Assert.assertFalse(set.remove("bar"));
        Assert.assertTrue(set.add("foo"));
        Assert.assertTrue(set.add("bar"));
        Assert.assertEquals(2, set.size());
        Assert.assertFalse(set.isEmpty());
        set.clear();
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testEventListeners() throws Exception {
        DistributedSet<String> set = atomix().<String>setBuilder("test-set-listeners").withProtocol(protocol()).build();
        DistributedSetTest.TestSetEventListener listener = new DistributedSetTest.TestSetEventListener();
        CollectionEvent<String> event;
        set.addListener(listener);
        Assert.assertTrue(set.add("foo"));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertTrue(set.add("bar"));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("bar", event.element());
        Assert.assertTrue(set.addAll(Arrays.asList("foo", "bar", "baz")));
        event = listener.event();
        Assert.assertEquals(ADD, event.type());
        Assert.assertEquals("baz", event.element());
        Assert.assertFalse(listener.eventReceived());
        Assert.assertTrue(set.remove("foo"));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("foo", event.element());
        Assert.assertTrue(set.removeAll(Arrays.asList("foo", "bar", "baz")));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertTrue(((event.element().equals("bar")) || (event.element().equals("baz"))));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertTrue(((event.element().equals("bar")) || (event.element().equals("baz"))));
    }

    private static class TestSetEventListener implements CollectionEventListener<String> {
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

