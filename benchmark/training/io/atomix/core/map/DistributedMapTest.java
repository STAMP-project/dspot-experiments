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
package io.atomix.core.map;


import MapEvent.Type.INSERT;
import MapEvent.Type.REMOVE;
import MapEvent.Type.UPDATE;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.atomix.core.AbstractPrimitiveTest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link DistributedMap}.
 */
public class DistributedMapTest extends AbstractPrimitiveTest {
    /**
     * Tests null values.
     */
    @Test
    public void testNullValues() throws Throwable {
        final String fooValue = "Hello foo!";
        final String barValue = "Hello bar!";
        DistributedMap<String, String> map = atomix().<String, String>mapBuilder("testNullValues").withProtocol(protocol()).withNullValues().build();
        Assert.assertNull(map.get("foo"));
        Assert.assertNull(map.put("foo", null));
        Assert.assertNull(map.put("foo", fooValue));
        Assert.assertEquals(fooValue, map.get("foo"));
        Assert.assertTrue(map.replace("foo", fooValue, null));
        Assert.assertNull(map.get("foo"));
        Assert.assertFalse(map.replace("foo", fooValue, barValue));
        Assert.assertTrue(map.replace("foo", null, barValue));
        Assert.assertEquals(barValue, map.get("foo"));
    }

    @Test
    public void testBasicMapOperations() throws Throwable {
        final String fooValue = "Hello foo!";
        final String barValue = "Hello bar!";
        DistributedMap<String, String> map = atomix().<String, String>mapBuilder("testBasicMapOperationMap").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertNull(map.put("foo", fooValue));
        Assert.assertTrue(((map.size()) == 1));
        Assert.assertFalse(map.isEmpty());
        Assert.assertEquals(fooValue, map.putIfAbsent("foo", "Hello foo again!"));
        Assert.assertNull(map.putIfAbsent("bar", barValue));
        Assert.assertTrue(((map.size()) == 2));
        Assert.assertTrue(((map.keySet().size()) == 2));
        Assert.assertTrue(map.keySet().containsAll(Sets.newHashSet("foo", "bar")));
        Assert.assertTrue(((map.values().size()) == 2));
        List<String> rawValues = map.values().stream().collect(Collectors.toList());
        Assert.assertTrue(rawValues.contains("Hello foo!"));
        Assert.assertTrue(rawValues.contains("Hello bar!"));
        Assert.assertTrue(((map.entrySet().size()) == 2));
        Assert.assertEquals(fooValue, map.get("foo"));
        Assert.assertEquals(fooValue, map.remove("foo"));
        Assert.assertFalse(map.containsKey("foo"));
        Assert.assertNull(map.get("foo"));
        Assert.assertEquals(barValue, map.get("bar"));
        Assert.assertTrue(map.containsKey("bar"));
        Assert.assertTrue(((map.size()) == 1));
        Assert.assertTrue(map.containsValue(barValue));
        Assert.assertFalse(map.containsValue(fooValue));
        Assert.assertEquals(barValue, map.replace("bar", "Goodbye bar!"));
        Assert.assertNull(map.replace("foo", "Goodbye foo!"));
        Assert.assertFalse(map.replace("foo", "Goodbye foo!", fooValue));
        Assert.assertTrue(map.replace("bar", "Goodbye bar!", barValue));
        Assert.assertFalse(map.replace("bar", "Goodbye bar!", barValue));
        map.clear();
        Assert.assertTrue(((map.size()) == 0));
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testMapComputeOperations() throws Throwable {
        final String value1 = "value1";
        final String value2 = "value2";
        final String value3 = "value3";
        DistributedMap<String, String> map = atomix().<String, String>mapBuilder("testMapComputeOperationsMap").withProtocol(protocol()).build();
        Assert.assertEquals(value1, map.computeIfAbsent("foo", ( k) -> value1));
        Assert.assertEquals(value1, map.computeIfAbsent("foo", ( k) -> value2));
        Assert.assertNull(map.computeIfPresent("bar", ( k, v) -> value2));
        Assert.assertEquals(value3, map.computeIfPresent("foo", ( k, v) -> value3));
        Assert.assertNull(map.computeIfPresent("foo", ( k, v) -> null));
        Assert.assertEquals(value2, map.compute("foo", ( k, v) -> value2));
    }

    @Test
    public void testMapListeners() throws Throwable {
        final String value1 = "value1";
        final String value2 = "value2";
        final String value3 = "value3";
        DistributedMap<String, String> map = atomix().<String, String>mapBuilder("testMapListenerMap").withProtocol(protocol()).build();
        DistributedMapTest.TestMapEventListener listener = new DistributedMapTest.TestMapEventListener();
        // add listener; insert new value into map and verify an INSERT event is received.
        map.addListener(listener);
        map.put("foo", value1);
        MapEvent<String, String> event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals(value1, event.newValue());
        // remove listener and verify listener is not notified.
        map.removeListener(listener);
        map.put("foo", value2);
        Assert.assertFalse(listener.eventReceived());
        // add the listener back and verify UPDATE events are received correctly
        map.addListener(listener);
        map.put("foo", value3);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(UPDATE, event.type());
        Assert.assertEquals(value3, event.newValue());
        // perform a non-state changing operation and verify no events are received.
        map.putIfAbsent("foo", value1);
        Assert.assertFalse(listener.eventReceived());
        // verify REMOVE events are received correctly.
        map.remove("foo");
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals(value3, event.oldValue());
        // verify compute methods also generate events.
        map.computeIfAbsent("foo", ( k) -> value1);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals(value1, event.newValue());
        map.compute("foo", ( k, v) -> value2);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(UPDATE, event.type());
        Assert.assertEquals(value2, event.newValue());
        map.computeIfPresent("foo", ( k, v) -> null);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals(value2, event.oldValue());
        map.removeListener(listener);
    }

    @Test
    public void testMapViews() throws Exception {
        DistributedMap<String, String> map = atomix().<String, String>mapBuilder("testMapViews").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.keySet().isEmpty());
        Assert.assertTrue(map.entrySet().isEmpty());
        Assert.assertTrue(map.values().isEmpty());
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
        Assert.assertFalse(map.isEmpty());
        Assert.assertFalse(map.keySet().isEmpty());
        Assert.assertFalse(map.entrySet().isEmpty());
        Assert.assertFalse(map.values().isEmpty());
        Assert.assertEquals(100, map.keySet().stream().count());
        Assert.assertEquals(100, map.entrySet().stream().count());
        Assert.assertEquals(100, map.values().stream().count());
        String one = String.valueOf(1);
        String two = String.valueOf(2);
        String three = String.valueOf(3);
        String four = String.valueOf(4);
        Assert.assertTrue(map.keySet().contains(one));
        Assert.assertTrue(map.values().contains(one));
        Assert.assertTrue(map.entrySet().contains(Maps.immutableEntry(one, one)));
        Assert.assertTrue(map.keySet().containsAll(Arrays.asList(one, two, three, four)));
        Assert.assertTrue(map.keySet().remove(one));
        Assert.assertFalse(map.keySet().contains(one));
        Assert.assertFalse(map.containsKey(one));
        Assert.assertTrue(map.entrySet().remove(Maps.immutableEntry(two, map.get(two))));
        Assert.assertFalse(map.keySet().contains(two));
        Assert.assertFalse(map.containsKey(two));
        Assert.assertTrue(map.entrySet().remove(Maps.immutableEntry(three, three)));
        Assert.assertFalse(map.keySet().contains(three));
        Assert.assertFalse(map.containsKey(three));
        Assert.assertFalse(map.entrySet().remove(Maps.immutableEntry(four, three)));
        Assert.assertTrue(map.keySet().contains(four));
        Assert.assertTrue(map.containsKey(four));
        Assert.assertEquals(97, map.size());
        Assert.assertEquals(97, map.keySet().size());
        Assert.assertEquals(97, map.entrySet().size());
        Assert.assertEquals(97, map.values().size());
        Assert.assertEquals(97, map.keySet().toArray().length);
        Assert.assertEquals(97, map.entrySet().toArray().length);
        Assert.assertEquals(97, map.values().toArray().length);
        Assert.assertEquals(97, map.keySet().toArray(new String[97]).length);
        Assert.assertEquals(97, map.entrySet().toArray(new Map.Entry[97]).length);
        Assert.assertEquals(97, map.values().toArray(new String[97]).length);
        Iterator<String> iterator = map.keySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            i += 1;
            map.put(String.valueOf((100 * i)), String.valueOf((100 * i)));
        } 
        Assert.assertEquals(String.valueOf(100), map.get(String.valueOf(100)));
    }

    /**
     * Tests a map with complex types.
     */
    @Test
    public void testComplexTypes() throws Throwable {
        DistributedMap<DistributedMapTest.Key, Pair<String, Integer>> map = atomix().<DistributedMapTest.Key, Pair<String, Integer>>mapBuilder("testComplexTypes").withProtocol(protocol()).build();
        map.put(new DistributedMapTest.Key("foo"), Pair.of("foo", 1));
        Assert.assertEquals("foo", map.get(new DistributedMapTest.Key("foo")).getLeft());
        Assert.assertEquals(Integer.valueOf(1), map.get(new DistributedMapTest.Key("foo")).getRight());
    }

    /**
     * Tests a map with complex types.
     */
    @Test
    public void testRequiredComplexTypes() throws Throwable {
        DistributedMap<DistributedMapTest.Key, Pair<String, Integer>> map = atomix().<DistributedMapTest.Key, Pair<String, Integer>>mapBuilder("testComplexTypes").withProtocol(protocol()).withRegistrationRequired().withKeyType(DistributedMapTest.Key.class).withValueType(ImmutablePair.class).build();
        map.put(new DistributedMapTest.Key("foo"), Pair.of("foo", 1));
        Assert.assertEquals("foo", map.get(new DistributedMapTest.Key("foo")).getLeft());
        Assert.assertEquals(Integer.valueOf(1), map.get(new DistributedMapTest.Key("foo")).getRight());
    }

    private static class Key {
        String value;

        Key(String value) {
            this.value = value;
        }
    }

    private static class TestMapEventListener implements MapEventListener<String, String> {
        private final BlockingQueue<MapEvent<String, String>> queue = new LinkedBlockingQueue<>();

        @Override
        public void event(MapEvent<String, String> event) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public boolean eventReceived() {
            return !(queue.isEmpty());
        }

        public MapEvent<String, String> event() throws InterruptedException {
            return queue.poll(10, TimeUnit.SECONDS);
        }
    }
}

