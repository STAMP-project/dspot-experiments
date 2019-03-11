/**
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.multimap;


import AtomicMultimapEvent.Type.INSERT;
import AtomicMultimapEvent.Type.REMOVE;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.atomix.core.AbstractPrimitiveTest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link AtomicMultimapProxy}.
 */
public class AtomicMultimapTest extends AbstractPrimitiveTest {
    private final String one = "hello";

    private final String two = "goodbye";

    private final String three = "foo";

    private final String four = "bar";

    private final List<String> all = Lists.newArrayList(one, two, three, four);

    /**
     * Test that size behaves correctly (This includes testing of the empty check).
     */
    @Test
    public void testSize() throws Throwable {
        AtomicMultimap<String, String> multimap = atomix().<String, String>atomicMultimapBuilder("testOneMap").withProtocol(protocol()).build();
        Assert.assertTrue(multimap.isEmpty());
        Assert.assertEquals(0, multimap.size());
        Assert.assertTrue(multimap.put(one, one));
        Assert.assertFalse(multimap.isEmpty());
        Assert.assertEquals(1, multimap.size());
        Assert.assertTrue(multimap.put(one, two));
        Assert.assertEquals(2, multimap.size());
        Assert.assertFalse(multimap.put(one, one));
        Assert.assertEquals(2, multimap.size());
        Assert.assertTrue(multimap.put(two, one));
        Assert.assertTrue(multimap.put(two, two));
        Assert.assertEquals(4, multimap.size());
        Assert.assertTrue(multimap.remove(one, one));
        Assert.assertEquals(3, multimap.size());
        Assert.assertFalse(multimap.remove(one, one));
        Assert.assertEquals(3, multimap.size());
        multimap.clear();
        Assert.assertEquals(0, multimap.size());
        Assert.assertTrue(multimap.isEmpty());
    }

    /**
     * Contains tests for value, key and entry.
     */
    @Test
    public void containsTest() throws Throwable {
        AtomicMultimap<String, String> multimap = atomix().<String, String>atomicMultimapBuilder("testTwoMap").withProtocol(protocol()).build();
        all.forEach(( key) -> Assert.assertTrue(multimap.putAll(key, all)));
        Assert.assertEquals(16, multimap.size());
        all.forEach(( value) -> Assert.assertTrue(multimap.containsValue(value)));
        all.forEach(( key) -> all.forEach(( value) -> Assert.assertTrue(multimap.containsEntry(key, value))));
        final String[] removedKey = new String[1];
        all.forEach(( value) -> {
            all.forEach(( key) -> {
                Assert.assertTrue(multimap.remove(key, value));
                Assert.assertFalse(multimap.containsEntry(key, value));
                removedKey[0] = key;
            });
        });
        Assert.assertFalse(multimap.containsKey(removedKey[0]));
        all.forEach(( value) -> Assert.assertFalse(multimap.containsValue(value)));
    }

    /**
     * Contains tests for put, putAll, remove, removeAll and replace.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void addAndRemoveTest() throws Exception {
        AtomicMultimap<String, String> multimap = atomix().<String, String>atomicMultimapBuilder("testThreeMap").withProtocol(protocol()).build();
        all.forEach(( key) -> all.forEach(( value) -> {
            Assert.assertTrue(multimap.put(key, value));
            Assert.assertFalse(multimap.put(key, value));
        }));
        all.forEach(( key) -> all.forEach(( value) -> {
            Assert.assertTrue(multimap.remove(key, value));
            Assert.assertFalse(multimap.remove(key, value));
        }));
        Assert.assertTrue(multimap.isEmpty());
        all.forEach(( key) -> {
            Assert.assertTrue(multimap.putAll(key, Lists.newArrayList(all.subList(0, 2))));
            Assert.assertFalse(multimap.putAll(key, Lists.newArrayList(all.subList(0, 2))));
            Assert.assertTrue(multimap.putAll(key, Lists.newArrayList(all.subList(2, 4))));
            Assert.assertFalse(multimap.putAll(key, Lists.newArrayList(all.subList(2, 4))));
        });
        multimap.clear();
        all.forEach(( key) -> Assert.assertTrue(multimap.putAll(key, all)));
        Assert.assertEquals(16, multimap.size());
        all.forEach(( key) -> {
            Assert.assertTrue(stringArrayCollectionIsEqual(all, multimap.removeAll(key).value()));
            Assert.assertNotEquals(all, multimap.removeAll(key));
        });
        Assert.assertTrue(multimap.isEmpty());
        all.forEach(( key) -> Assert.assertTrue(multimap.putAll(key, all)));
        Assert.assertEquals(16, multimap.size());
        all.forEach(( key) -> {
            Assert.assertTrue(stringArrayCollectionIsEqual(all, multimap.replaceValues(key, all).value()));
            Assert.assertTrue(stringArrayCollectionIsEqual(all, multimap.replaceValues(key, Lists.newArrayList()).value()));
            Assert.assertTrue(multimap.replaceValues(key, all).value().isEmpty());
        });
        Assert.assertEquals(16, multimap.size());
        all.forEach(( key) -> {
            Assert.assertTrue(multimap.remove(key, one));
            Assert.assertTrue(stringArrayCollectionIsEqual(Lists.newArrayList(two, three, four), multimap.replaceValues(key, Lists.newArrayList()).value()));
            Assert.assertTrue(multimap.replaceValues(key, all).value().isEmpty());
        });
    }

    /**
     * Tests the get, keySet, keys, values, and entries implementations as well as a trivial test of the asMap
     * functionality (throws error).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAccessors() throws Exception {
        AtomicMultimap<String, String> multimap = atomix().<String, String>atomicMultimapBuilder("testFourMap").withProtocol(protocol()).build();
        all.forEach(( key) -> Assert.assertTrue(multimap.putAll(key, all)));
        Assert.assertEquals(16, multimap.size());
        all.forEach(( key) -> Assert.assertTrue(stringArrayCollectionIsEqual(all, multimap.get(key).value())));
        multimap.clear();
        all.forEach(( key) -> Assert.assertTrue(multimap.get(key).value().isEmpty()));
    }

    @Test
    public void testMultimapEvents() throws Exception {
        AtomicMultimap<String, String> multimap1 = atomix().<String, String>atomicMultimapBuilder("testMultimapEvents").withProtocol(protocol()).build();
        AtomicMultimap<String, String> multimap2 = atomix().<String, String>atomicMultimapBuilder("testMultimapEvents").withProtocol(protocol()).build();
        AtomicMultimapTest.TestAtomicMultimapEventListener listener = new AtomicMultimapTest.TestAtomicMultimapEventListener();
        multimap2.addListener(listener);
        Assert.assertTrue(multimap1.isEmpty());
        Assert.assertTrue(multimap1.put("foo", "bar"));
        AtomicMultimapEvent event = listener.event();
        Assert.assertEquals(INSERT, event.type());
        Assert.assertNull(event.oldValue());
        Assert.assertNotNull(event.newValue());
        Assert.assertFalse(multimap1.put("foo", "bar"));
        Assert.assertFalse(listener.eventReceived());
        Assert.assertTrue(multimap1.remove("foo", "bar"));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertNotNull(event.oldValue());
        Assert.assertNull(event.newValue());
        Assert.assertTrue(multimap1.put("foo", "foo"));
        Assert.assertTrue(multimap1.put("foo", "bar"));
        Assert.assertTrue(multimap1.put("foo", "baz"));
        for (int i = 0; i < 3; i++) {
            event = listener.event();
            Assert.assertEquals(INSERT, event.type());
            Assert.assertEquals("foo", event.key());
        }
        Assert.assertEquals(3, multimap1.removeAll("foo").value().size());
        for (int i = 0; i < 3; i++) {
            event = listener.event();
            Assert.assertEquals(REMOVE, event.type());
            Assert.assertEquals("foo", event.key());
        }
        Assert.assertTrue(multimap1.put("bar", "foo"));
        Assert.assertTrue(multimap1.put("bar", "bar"));
        Assert.assertTrue(multimap1.put("bar", "baz"));
        for (int i = 0; i < 3; i++) {
            event = listener.event();
            Assert.assertEquals(INSERT, event.type());
            Assert.assertEquals("bar", event.key());
        }
        multimap1.replaceValues("bar", Arrays.asList("foo", "barbaz"));
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("bar", event.key());
        Assert.assertEquals("bar", event.oldValue());
        event = listener.event();
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("bar", event.key());
        Assert.assertEquals("baz", event.oldValue());
        event = listener.event();
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals("bar", event.key());
        Assert.assertEquals("barbaz", event.newValue());
    }

    @Test
    public void testMultimapViews() throws Exception {
        AtomicMultimap<String, String> map = atomix().<String, String>atomicMultimapBuilder("testMultimapViews").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.keySet().isEmpty());
        Assert.assertTrue(map.keys().isEmpty());
        Assert.assertTrue(map.entries().isEmpty());
        Assert.assertTrue(map.values().isEmpty());
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
        Assert.assertFalse(map.isEmpty());
        Assert.assertFalse(map.keySet().isEmpty());
        Assert.assertFalse(map.keys().isEmpty());
        Assert.assertFalse(map.entries().isEmpty());
        Assert.assertFalse(map.values().isEmpty());
        Assert.assertEquals(100, map.keySet().stream().count());
        Assert.assertEquals(100, map.keys().stream().count());
        Assert.assertEquals(100, map.entries().stream().count());
        Assert.assertEquals(100, map.values().stream().count());
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), String.valueOf((i + 1)));
        }
        Assert.assertEquals(100, map.keySet().size());
        Assert.assertEquals(200, map.keys().size());
        Assert.assertEquals(200, map.entries().size());
        Assert.assertEquals(200, map.values().size());
        String one = String.valueOf(1);
        String two = String.valueOf(2);
        String three = String.valueOf(3);
        String four = String.valueOf(4);
        Assert.assertTrue(map.keySet().contains(one));
        Assert.assertTrue(map.keys().contains(one));
        Assert.assertTrue(map.values().contains(one));
        Assert.assertTrue(map.entries().contains(Maps.immutableEntry(one, one)));
        Assert.assertTrue(map.keySet().containsAll(Arrays.asList(one, two, three, four)));
        Assert.assertTrue(map.keys().containsAll(Arrays.asList(one, two, three, four)));
        Assert.assertTrue(map.values().containsAll(Arrays.asList(one, two, three, four)));
        Assert.assertTrue(map.keySet().remove(one));
        Assert.assertFalse(map.keySet().contains(one));
        Assert.assertFalse(map.containsKey(one));
        Assert.assertTrue(map.keys().remove(two));
        Assert.assertFalse(map.keys().contains(two));
        Assert.assertFalse(map.containsKey(two));
        Assert.assertTrue(map.entries().remove(Maps.immutableEntry(three, three)));
        Assert.assertTrue(map.keySet().contains(three));
        Assert.assertTrue(map.containsKey(three));
        Assert.assertTrue(map.entries().remove(Maps.immutableEntry(three, four)));
        Assert.assertFalse(map.keySet().contains(three));
        Assert.assertFalse(map.containsKey(three));
        Assert.assertFalse(map.entries().remove(Maps.immutableEntry(four, three)));
        Assert.assertTrue(map.keySet().contains(four));
        Assert.assertTrue(map.containsKey(four));
        Assert.assertEquals(194, map.size());
        Assert.assertEquals(97, map.keySet().size());
        Assert.assertEquals(194, map.keys().size());
        Assert.assertEquals(194, map.entries().size());
        Assert.assertEquals(194, map.values().size());
        Assert.assertEquals(97, map.keySet().stream().count());
        Assert.assertEquals(194, map.keys().stream().count());
        Assert.assertEquals(194, map.entries().stream().count());
        Assert.assertEquals(194, map.values().stream().count());
        Assert.assertEquals(97, map.keySet().toArray().length);
        Assert.assertEquals(194, map.keys().toArray().length);
        Assert.assertEquals(194, map.entries().toArray().length);
        Assert.assertEquals(194, map.values().toArray().length);
        Assert.assertEquals(97, map.keySet().toArray(new String[97]).length);
        Assert.assertEquals(194, map.keys().toArray(new String[194]).length);
        Assert.assertEquals(194, map.entries().toArray(new Map.Entry[194]).length);
        Assert.assertEquals(194, map.values().toArray(new String[194]).length);
        Iterator<String> iterator = map.keySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            i += 1;
            map.put(String.valueOf((100 * i)), String.valueOf((100 * i)));
        } 
    }

    private static class TestAtomicMultimapEventListener implements AtomicMultimapEventListener<String, String> {
        private final BlockingQueue<AtomicMultimapEvent<String, String>> queue = new LinkedBlockingQueue<>();

        @Override
        public void event(AtomicMultimapEvent<String, String> event) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public boolean eventReceived() {
            return !(queue.isEmpty());
        }

        public AtomicMultimapEvent<String, String> event() throws InterruptedException {
            return queue.poll(10, TimeUnit.SECONDS);
        }
    }
}

