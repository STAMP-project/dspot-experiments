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
import com.google.common.collect.Sets;
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
 * Distributed tree set test.
 */
public class DistributedNavigableSetTest extends AbstractPrimitiveTest {
    @Test
    public void testSetOperations() throws Exception {
        DistributedNavigableSet<String> set = atomix().<String>navigableSetBuilder("test-set").withProtocol(protocol()).build();
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
    }

    @Test
    public void testEventListeners() throws Exception {
        DistributedNavigableSet<String> set = atomix().<String>navigableSetBuilder("test-set-listeners").withProtocol(protocol()).build();
        DistributedNavigableSetTest.TestSetEventListener listener = new DistributedNavigableSetTest.TestSetEventListener();
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

    @Test
    public void testTreeSetOperations() throws Throwable {
        DistributedNavigableSet<String> set = atomix().<String>navigableSetBuilder("testTreeSetOperations").withProtocol(protocol()).build();
        try {
            set.first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            set.last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            set.subSet("a", false, "z", false).first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            set.subSet("a", false, "z", false).last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        Assert.assertNull(set.pollFirst());
        Assert.assertNull(set.pollLast());
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.subSet("a", true, "b", true).size());
        Assert.assertTrue(set.subSet("a", true, "b", true).isEmpty());
        Assert.assertEquals(0, set.headSet("a").size());
        Assert.assertTrue(set.headSet("a").isEmpty());
        Assert.assertEquals(0, set.tailSet("b").size());
        Assert.assertTrue(set.tailSet("b").isEmpty());
        for (char letter = 'a'; letter <= 'z'; letter++) {
            set.add(String.valueOf(letter));
        }
        Assert.assertEquals("a", set.first());
        Assert.assertEquals("z", set.last());
        Assert.assertEquals("a", set.pollFirst());
        Assert.assertEquals("z", set.pollLast());
        Assert.assertEquals("b", set.first());
        Assert.assertEquals("y", set.last());
        try {
            set.subSet("A", false, "Z", false).first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            set.subSet("A", false, "Z", false).last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            set.subSet("a", true, "b", false).first();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            set.subSet("a", true, "b", false).last();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        Assert.assertEquals("d", set.subSet("c", false, "x", false).subSet("c", true, "x", true).first());
        Assert.assertEquals("w", set.subSet("c", false, "x", false).subSet("c", true, "x", true).last());
        Assert.assertEquals("y", set.headSet("y", true).last());
        Assert.assertEquals("x", set.headSet("y", false).last());
        Assert.assertEquals("y", set.headSet("y", true).subSet("a", true, "z", false).last());
        Assert.assertEquals("b", set.tailSet("b", true).first());
        Assert.assertEquals("c", set.tailSet("b", false).first());
        Assert.assertEquals("b", set.tailSet("b", true).subSet("a", false, "z", true).first());
        Assert.assertEquals("b", set.higher("a"));
        Assert.assertEquals("c", set.higher("b"));
        Assert.assertEquals("y", set.lower("z"));
        Assert.assertEquals("x", set.lower("y"));
        Assert.assertEquals("b", set.ceiling("a"));
        Assert.assertEquals("b", set.ceiling("b"));
        Assert.assertEquals("y", set.floor("z"));
        Assert.assertEquals("y", set.floor("y"));
        Assert.assertEquals("c", set.subSet("c", true, "x", true).higher("b"));
        Assert.assertEquals("d", set.subSet("c", true, "x", true).higher("c"));
        Assert.assertEquals("x", set.subSet("c", true, "x", true).lower("y"));
        Assert.assertEquals("w", set.subSet("c", true, "x", true).lower("x"));
        Assert.assertEquals("d", set.subSet("c", false, "x", false).higher("b"));
        Assert.assertEquals("d", set.subSet("c", false, "x", false).higher("c"));
        Assert.assertEquals("e", set.subSet("c", false, "x", false).higher("d"));
        Assert.assertEquals("w", set.subSet("c", false, "x", false).lower("y"));
        Assert.assertEquals("w", set.subSet("c", false, "x", false).lower("x"));
        Assert.assertEquals("v", set.subSet("c", false, "x", false).lower("w"));
        Assert.assertEquals("c", set.subSet("c", true, "x", true).ceiling("b"));
        Assert.assertEquals("c", set.subSet("c", true, "x", true).ceiling("c"));
        Assert.assertEquals("x", set.subSet("c", true, "x", true).floor("y"));
        Assert.assertEquals("x", set.subSet("c", true, "x", true).floor("x"));
        Assert.assertEquals("d", set.subSet("c", false, "x", false).ceiling("b"));
        Assert.assertEquals("d", set.subSet("c", false, "x", false).ceiling("c"));
        Assert.assertEquals("d", set.subSet("c", false, "x", false).ceiling("d"));
        Assert.assertEquals("w", set.subSet("c", false, "x", false).floor("y"));
        Assert.assertEquals("w", set.subSet("c", false, "x", false).floor("x"));
        Assert.assertEquals("w", set.subSet("c", false, "x", false).floor("w"));
    }

    @Test
    public void testSubSets() throws Throwable {
        DistributedNavigableSet<String> set = atomix().<String>navigableSetBuilder("testSubSets").withProtocol(protocol()).build();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            set.add(String.valueOf(letter));
        }
        Assert.assertEquals("a", set.first());
        Assert.assertEquals("a", set.pollFirst());
        Assert.assertEquals("b", set.descendingSet().last());
        Assert.assertEquals("b", set.descendingSet().pollLast());
        Assert.assertEquals("z", set.last());
        Assert.assertEquals("z", set.pollLast());
        Assert.assertEquals("y", set.descendingSet().first());
        Assert.assertEquals("y", set.descendingSet().pollFirst());
        Assert.assertEquals("d", set.subSet("d", true, "w", false).first());
        Assert.assertEquals("e", set.subSet("d", false, "w", false).first());
        Assert.assertEquals("d", set.tailSet("d", true).first());
        Assert.assertEquals("e", set.tailSet("d", false).first());
        Assert.assertEquals("w", set.headSet("w", true).descendingSet().first());
        Assert.assertEquals("v", set.headSet("w", false).descendingSet().first());
        Assert.assertEquals("w", set.subSet("d", false, "w", true).last());
        Assert.assertEquals("v", set.subSet("d", false, "w", false).last());
        Assert.assertEquals("w", set.headSet("w", true).last());
        Assert.assertEquals("v", set.headSet("w", false).last());
        Assert.assertEquals("d", set.tailSet("d", true).descendingSet().last());
        Assert.assertEquals("e", set.tailSet("d", false).descendingSet().last());
        Assert.assertEquals("w", set.subSet("d", false, "w", true).descendingSet().first());
        Assert.assertEquals("v", set.subSet("d", false, "w", false).descendingSet().first());
        Assert.assertEquals(20, set.subSet("d", true, "w", true).size());
        Assert.assertEquals(19, set.subSet("d", true, "w", false).size());
        Assert.assertEquals(19, set.subSet("d", false, "w", true).size());
        Assert.assertEquals(18, set.subSet("d", false, "w", false).size());
        Assert.assertEquals(20, set.subSet("d", true, "w", true).stream().count());
        Assert.assertEquals(19, set.subSet("d", true, "w", false).stream().count());
        Assert.assertEquals(19, set.subSet("d", false, "w", true).stream().count());
        Assert.assertEquals(18, set.subSet("d", false, "w", false).stream().count());
        Assert.assertEquals("d", set.subSet("d", true, "w", true).stream().findFirst().get());
        Assert.assertEquals("d", set.subSet("d", true, "w", false).stream().findFirst().get());
        Assert.assertEquals("e", set.subSet("d", false, "w", true).stream().findFirst().get());
        Assert.assertEquals("e", set.subSet("d", false, "w", false).stream().findFirst().get());
        Assert.assertEquals("w", set.subSet("d", true, "w", true).descendingSet().stream().findFirst().get());
        Assert.assertEquals("v", set.subSet("d", true, "w", false).descendingSet().stream().findFirst().get());
        Assert.assertEquals("w", set.subSet("d", false, "w", true).descendingSet().stream().findFirst().get());
        Assert.assertEquals("v", set.subSet("d", false, "w", false).descendingSet().stream().findFirst().get());
        Assert.assertEquals("d", set.subSet("d", true, "w", true).iterator().next());
        Assert.assertEquals("w", set.subSet("d", true, "w", true).descendingIterator().next());
        Assert.assertEquals("w", set.subSet("d", true, "w", true).descendingSet().iterator().next());
        Assert.assertEquals("e", set.subSet("d", false, "w", true).iterator().next());
        Assert.assertEquals("e", set.subSet("d", false, "w", true).descendingSet().descendingIterator().next());
        Assert.assertEquals("w", set.subSet("d", false, "w", true).descendingIterator().next());
        Assert.assertEquals("w", set.subSet("d", false, "w", true).descendingSet().iterator().next());
        Assert.assertEquals("d", set.subSet("d", true, "w", false).iterator().next());
        Assert.assertEquals("d", set.subSet("d", true, "w", false).descendingSet().descendingIterator().next());
        Assert.assertEquals("v", set.subSet("d", true, "w", false).descendingIterator().next());
        Assert.assertEquals("v", set.subSet("d", true, "w", false).descendingSet().iterator().next());
        Assert.assertEquals("e", set.subSet("d", false, "w", false).iterator().next());
        Assert.assertEquals("e", set.subSet("d", false, "w", false).descendingSet().descendingIterator().next());
        Assert.assertEquals("v", set.subSet("d", false, "w", false).descendingIterator().next());
        Assert.assertEquals("v", set.subSet("d", false, "w", false).descendingSet().iterator().next());
        Assert.assertEquals("d", set.subSet("d", true, "w", true).headSet("m", true).iterator().next());
        Assert.assertEquals("m", set.subSet("d", true, "w", true).headSet("m", true).descendingIterator().next());
        Assert.assertEquals("d", set.subSet("d", true, "w", true).headSet("m", false).iterator().next());
        Assert.assertEquals("l", set.subSet("d", true, "w", true).headSet("m", false).descendingIterator().next());
        Assert.assertEquals("m", set.subSet("d", true, "w", true).tailSet("m", true).iterator().next());
        Assert.assertEquals("w", set.subSet("d", true, "w", true).tailSet("m", true).descendingIterator().next());
        Assert.assertEquals("n", set.subSet("d", true, "w", true).tailSet("m", false).iterator().next());
        Assert.assertEquals("w", set.subSet("d", true, "w", true).tailSet("m", false).descendingIterator().next());
        Assert.assertEquals(18, set.subSet("d", true, "w", true).subSet("e", true, "v", true).subSet("d", true, "w", true).size());
        Assert.assertEquals("x", set.tailSet("d", true).descendingIterator().next());
        Assert.assertEquals("x", set.tailSet("d", true).descendingSet().iterator().next());
        Assert.assertEquals("c", set.headSet("w", true).iterator().next());
        Assert.assertEquals("c", set.headSet("w", true).descendingSet().descendingSet().iterator().next());
        set.headSet("e", false).clear();
        Assert.assertEquals("e", set.first());
        Assert.assertEquals(20, set.size());
        set.headSet("g", true).clear();
        Assert.assertEquals("h", set.first());
        Assert.assertEquals(17, set.size());
        set.tailSet("t", false).clear();
        Assert.assertEquals("t", set.last());
        Assert.assertEquals(13, set.size());
        set.tailSet("o", true).clear();
        Assert.assertEquals("n", set.last());
        Assert.assertEquals(7, set.size());
        set.subSet("k", false, "n", false).clear();
        Assert.assertEquals(5, set.size());
        Assert.assertEquals(Sets.newHashSet("h", "i", "j", "k", "n"), Sets.newHashSet(set));
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

