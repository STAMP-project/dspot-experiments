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
 * Unit tests for {@link AtomicNavigableMapProxy}.
 */
public class DistributedNavigableMapTest extends AbstractPrimitiveTest {
    private final String four = "hello";

    private final String three = "goodbye";

    private final String two = "foo";

    private final String one = "bar";

    private final String spare = "spare";

    private final List<String> all = Lists.newArrayList(one, two, three, four);

    /**
     * Tests of the functionality associated with the {@link DistributedNavigableMap} interface except transactions and
     * listeners.
     */
    @Test
    public void testBasicMapOperations() throws Throwable {
        // Throughout the test there are isEmpty queries, these are intended to
        // make sure that the previous section has been cleaned up, they serve
        // the secondary purpose of testing isEmpty but that is not their
        // primary purpose.
        DistributedNavigableMap<String, String> map = atomix().<String, String>navigableMapBuilder("basicTestMap").withProtocol(protocol()).build();
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
        all.forEach(( key) -> Assert.assertFalse(map.containsKey(key)));
        all.forEach(( value) -> Assert.assertFalse(map.containsValue(value)));
        all.forEach(( key) -> Assert.assertNull(map.get(key)));
        all.forEach(( key) -> Assert.assertNull(map.getOrDefault(key, null)));
        all.forEach(( key) -> Assert.assertEquals("bar", map.getOrDefault(key, "bar")));
        all.forEach(( key) -> Assert.assertNull(map.put(key, all.get(all.indexOf(key)))));
        all.forEach(( key) -> Assert.assertTrue(map.containsKey(key)));
        all.forEach(( value) -> Assert.assertTrue(map.containsValue(value)));
        all.forEach(( key) -> Assert.assertEquals(all.get(all.indexOf(key)), map.get(key)));
        all.forEach(( key) -> Assert.assertEquals(all.get(all.indexOf(key)), map.getOrDefault(key, null)));
        all.forEach(( key) -> Assert.assertEquals(all.get(all.indexOf(key)), map.computeIfAbsent(key, ( v) -> all.get(all.indexOf(key)))));
        Assert.assertEquals(4, map.size());
        Assert.assertFalse(map.isEmpty());
        all.forEach(( key) -> Assert.assertNull(map.computeIfPresent(key, ( k, v) -> null)));
        Assert.assertTrue(map.isEmpty());
        all.forEach(( key) -> Assert.assertEquals(all.get(all.indexOf(key)), map.compute(key, ( k, v) -> all.get(all.indexOf(key)))));
        Assert.assertEquals(4, map.size());
        Assert.assertFalse(map.isEmpty());
        all.forEach(( key) -> Assert.assertEquals(all.get(all.indexOf(key)), map.remove(key)));
        Assert.assertTrue(map.isEmpty());
        all.forEach(( key) -> Assert.assertNull(map.put(key, all.get(all.indexOf(key)))));
        // Test various collections of keys, values and entries
        Assert.assertTrue(stringArrayCollectionIsEqual(map.keySet(), all));
        Assert.assertTrue(stringArrayCollectionIsEqual(map.values(), all));
        map.forEach(( key, value) -> {
            assertTrue(all.contains(key));
            assertEquals(value, all.get(all.indexOf(key)));
        });
        map.clear();
        Assert.assertTrue(map.isEmpty());
        all.forEach(( key) -> Assert.assertNull(map.putIfAbsent(key, all.get(all.indexOf(key)))));
        all.forEach(( key) -> Assert.assertEquals(all.get(all.indexOf(key)), map.putIfAbsent(key, null)));
        all.forEach(( key) -> Assert.assertFalse(map.remove(key, spare)));
        all.forEach(( key) -> Assert.assertTrue(map.remove(key, all.get(all.indexOf(key)))));
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void mapListenerTests() throws Throwable {
        final String value1 = "value1";
        final String value2 = "value2";
        final String value3 = "value3";
        DistributedNavigableMap<String, String> map = atomix().<String, String>navigableMapBuilder("treeMapListenerTestMap").withProtocol(protocol()).build();
        DistributedNavigableMapTest.TestMapEventListener listener = new DistributedNavigableMapTest.TestMapEventListener();
        // add listener; insert new value into map and verify an INSERT event
        // is received.
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
        // add the listener back and verify UPDATE events are received
        // correctly
        map.addListener(listener);
        map.put("foo", value3);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(UPDATE, event.type());
        Assert.assertEquals(value3, event.newValue());
        // perform a non-state changing operation and verify no events are
        // received.
        map.putIfAbsent("foo", value1);
        Assert.assertFalse(listener.eventReceived());
        // verify REMOVE events are received correctly.
        map.remove("foo");
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals(value3, event.oldValue());
        map.compute("foo", ( k, v) -> value2);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals(value2, event.newValue());
        map.removeListener(listener);
    }

    @Test
    public void treeMapFunctionsTest() throws Throwable {
        DistributedNavigableMap<String, String> map = atomix().<String, String>navigableMapBuilder("treeMapFunctionTestMap").withProtocol(protocol()).build();
        // Tests on empty map
        Assert.assertNull(map.firstKey());
        Assert.assertNull(map.lastKey());
        Assert.assertNull(map.ceilingEntry(one));
        Assert.assertNull(map.floorEntry(one));
        Assert.assertNull(map.higherEntry(one));
        Assert.assertNull(map.lowerEntry(one));
        Assert.assertNull(map.firstEntry());
        Assert.assertNull(map.lastEntry());
        Assert.assertNull(map.lowerKey(one));
        Assert.assertNull(map.higherKey(one));
        Assert.assertNull(map.floorKey(one));
        Assert.assertNull(map.ceilingKey(one));
        Assert.assertEquals(0, map.size());
        all.forEach(( key) -> Assert.assertNull(map.put(key, key)));
        Assert.assertEquals(4, map.size());
        Assert.assertEquals(one, map.firstKey());
        Assert.assertEquals(four, map.lastKey());
        Assert.assertEquals(one, map.ceilingEntry(one).getKey());
        Assert.assertEquals(one, map.ceilingEntry(one).getValue());
        Assert.assertEquals(two, map.ceilingEntry(((one) + "a")).getKey());
        Assert.assertEquals(two, map.ceilingEntry(((one) + "a")).getValue());
        Assert.assertNull(map.ceilingEntry(((four) + "a")));
        Assert.assertEquals(two, map.floorEntry(two).getKey());
        Assert.assertEquals(two, map.floorEntry(two).getValue());
        Assert.assertEquals(one, map.floorEntry(two.substring(0, 2)).getKey());
        Assert.assertEquals(one, map.floorEntry(two.substring(0, 2)).getValue());
        Assert.assertNull(map.floorEntry(one.substring(0, 1)));
        Assert.assertEquals(three, map.higherEntry(two).getKey());
        Assert.assertEquals(three, map.higherEntry(two).getValue());
        Assert.assertNull(map.higherEntry(four));
        Assert.assertEquals(three, map.lowerEntry(four).getKey());
        Assert.assertEquals(three, map.lowerEntry(four).getValue());
        Assert.assertNull(map.lowerEntry(one));
        Assert.assertEquals(one, map.firstEntry().getKey());
        Assert.assertEquals(one, map.firstEntry().getValue());
        Assert.assertEquals(four, map.lastEntry().getKey());
        Assert.assertEquals(four, map.lastEntry().getValue());
        all.forEach(( key) -> Assert.assertEquals(key, map.put(key, key)));
        Assert.assertNull(map.lowerKey(one));
        Assert.assertEquals(two, map.lowerKey(three));
        Assert.assertEquals(three, map.floorKey(three));
        Assert.assertNull(map.floorKey(one.substring(0, 1)));
        Assert.assertEquals(two, map.ceilingKey(two));
        Assert.assertNull(map.ceilingKey(((four) + "a")));
        Assert.assertEquals(four, map.higherKey(three));
        Assert.assertNull(map.higherEntry(four));
    }

    @Test
    public void testTreeMapViews() throws Throwable {
        DistributedNavigableMap<String, String> map = atomix().<String, String>navigableMapBuilder("testTreeMapViews").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.keySet().isEmpty());
        Assert.assertTrue(map.entrySet().isEmpty());
        Assert.assertTrue(map.values().isEmpty());
        for (char a = 'a'; a <= 'z'; a++) {
            map.put(String.valueOf(a), String.valueOf(a));
        }
        Assert.assertFalse(map.isEmpty());
        Assert.assertFalse(map.keySet().isEmpty());
        Assert.assertFalse(map.entrySet().isEmpty());
        Assert.assertFalse(map.values().isEmpty());
        Assert.assertEquals(26, map.keySet().stream().count());
        Assert.assertEquals(26, map.entrySet().stream().count());
        Assert.assertEquals(26, map.values().stream().count());
        String a = String.valueOf('a');
        String b = String.valueOf('b');
        String c = String.valueOf('c');
        String d = String.valueOf('d');
        Assert.assertTrue(map.keySet().contains(a));
        Assert.assertTrue(map.values().contains(a));
        Assert.assertTrue(map.entrySet().contains(Maps.immutableEntry(a, a)));
        Assert.assertTrue(map.keySet().containsAll(Arrays.asList(a, b, c, d)));
        Assert.assertTrue(map.keySet().remove(a));
        Assert.assertFalse(map.keySet().contains(a));
        Assert.assertFalse(map.containsKey(a));
        Assert.assertEquals(b, map.firstKey());
        Assert.assertTrue(map.entrySet().remove(Maps.immutableEntry(b, map.get(b))));
        Assert.assertFalse(map.keySet().contains(b));
        Assert.assertFalse(map.containsKey(b));
        Assert.assertEquals(c, map.firstKey());
        Assert.assertTrue(map.entrySet().remove(Maps.immutableEntry(c, c)));
        Assert.assertFalse(map.keySet().contains(c));
        Assert.assertFalse(map.containsKey(c));
        Assert.assertEquals(d, map.firstKey());
        Assert.assertFalse(map.entrySet().remove(Maps.immutableEntry(d, c)));
        Assert.assertTrue(map.keySet().contains(d));
        Assert.assertTrue(map.containsKey(d));
        Assert.assertEquals(d, map.firstKey());
        Assert.assertEquals(23, map.size());
        Assert.assertEquals(23, map.keySet().size());
        Assert.assertEquals(23, map.entrySet().size());
        Assert.assertEquals(23, map.values().size());
        Assert.assertEquals(23, map.keySet().toArray().length);
        Assert.assertEquals(23, map.entrySet().toArray().length);
        Assert.assertEquals(23, map.values().toArray().length);
        Assert.assertEquals(23, map.keySet().toArray(new String[23]).length);
        Assert.assertEquals(23, map.entrySet().toArray(new Map.Entry[23]).length);
        Assert.assertEquals(23, map.values().toArray(new String[23]).length);
        Iterator<String> iterator = map.keySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            i += 1;
            map.put(String.valueOf((26 + i)), String.valueOf((26 + i)));
        } 
        Assert.assertEquals(String.valueOf(27), map.get(String.valueOf(27)));
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

