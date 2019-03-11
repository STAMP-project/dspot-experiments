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


import AtomicMapEvent.Type.INSERT;
import AtomicMapEvent.Type.REMOVE;
import AtomicMapEvent.Type.UPDATE;
import CommitStatus.FAILURE;
import CommitStatus.SUCCESS;
import Isolation.READ_COMMITTED;
import Isolation.REPEATABLE_READS;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.atomix.core.AbstractPrimitiveTest;
import io.atomix.core.transaction.Transaction;
import io.atomix.core.transaction.TransactionalMap;
import io.atomix.utils.time.Versioned;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AtomicMap}.
 */
public class AtomicMapTest extends AbstractPrimitiveTest {
    /**
     * Tests null values.
     */
    @Test
    public void testNullValues() throws Throwable {
        final String fooValue = "Hello foo!";
        final String barValue = "Hello bar!";
        AtomicMap<String, String> map = atomix().<String, String>atomicMapBuilder("testNullValues").withProtocol(protocol()).withNullValues().build();
        Assert.assertNull(map.get("foo"));
        Assert.assertNull(map.put("foo", null));
        Versioned<String> value = map.put("foo", fooValue);
        Assert.assertNotNull(value);
        Assert.assertNull(value.value());
        value = map.get("foo");
        Assert.assertNotNull(value);
        Assert.assertEquals(fooValue, value.value());
        Assert.assertTrue(map.replace("foo", fooValue, null));
        value = map.get("foo");
        Assert.assertNotNull(value);
        Assert.assertNull(value.value());
        Assert.assertFalse(map.replace("foo", fooValue, barValue));
        Assert.assertTrue(map.replace("foo", null, barValue));
        value = map.get("foo");
        Assert.assertNotNull(value);
        Assert.assertEquals(barValue, value.value());
    }

    @Test
    public void testBasicMapOperations() throws Throwable {
        final String fooValue = "Hello foo!";
        final String barValue = "Hello bar!";
        AtomicMap<String, String> map = atomix().<String, String>atomicMapBuilder("testBasicMapOperationMap").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertNull(map.put("foo", fooValue));
        Assert.assertEquals(1, map.size());
        Assert.assertFalse(map.isEmpty());
        Versioned<String> value = map.putIfAbsent("foo", "Hello foo again!");
        Assert.assertNotNull(value);
        Assert.assertEquals(fooValue, value.value());
        Map<String, Versioned<String>> values = map.getAllPresent(Collections.singleton("foo"));
        Assert.assertNotNull(values);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(fooValue, values.get("foo").value());
        Assert.assertNull(map.putIfAbsent("bar", barValue));
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(2, map.keySet().size());
        Assert.assertTrue(map.keySet().containsAll(Sets.newHashSet("foo", "bar")));
        Assert.assertEquals(2, map.values().size());
        List<String> rawValues = map.values().stream().map(( v) -> v.value()).collect(Collectors.toList());
        Assert.assertTrue(rawValues.contains("Hello foo!"));
        Assert.assertTrue(rawValues.contains("Hello bar!"));
        Assert.assertEquals(2, map.entrySet().size());
        Assert.assertEquals(fooValue, map.get("foo").value());
        Assert.assertEquals(fooValue, map.remove("foo").value());
        Assert.assertFalse(map.containsKey("foo"));
        Assert.assertNull(map.get("foo"));
        value = map.get("bar");
        Assert.assertNotNull(value);
        Assert.assertEquals(barValue, value.value());
        Assert.assertTrue(map.containsKey("bar"));
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsValue(barValue));
        Assert.assertFalse(map.containsValue(fooValue));
        value = map.replace("bar", "Goodbye bar!");
        Assert.assertNotNull(value);
        Assert.assertEquals(barValue, value.value());
        Assert.assertNull(map.replace("foo", "Goodbye foo!"));
        Assert.assertFalse(map.replace("foo", "Goodbye foo!", fooValue));
        Assert.assertTrue(map.replace("bar", "Goodbye bar!", barValue));
        Assert.assertFalse(map.replace("bar", "Goodbye bar!", barValue));
        value = map.get("bar");
        Assert.assertTrue(map.replace("bar", value.version(), "Goodbye bar!"));
        Assert.assertFalse(map.replace("bar", value.version(), barValue));
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertNull(map.put("foo", "Hello foo!", Duration.ofSeconds(3)));
        Thread.sleep(1000);
        Assert.assertEquals("Hello foo!", map.get("foo").value());
        Thread.sleep(5000);
        Assert.assertNull(map.get("foo"));
        Assert.assertNull(map.put("bar", "Hello bar!"));
        Assert.assertEquals("Hello bar!", map.put("bar", "Goodbye bar!", Duration.ofMillis(100)).value());
        Assert.assertEquals("Goodbye bar!", map.get("bar").value());
        Thread.sleep(5000);
        Assert.assertNull(map.get("bar"));
        Assert.assertNull(map.putIfAbsent("baz", "Hello baz!", Duration.ofMillis(100)));
        Assert.assertNotNull(map.get("baz"));
        Thread.sleep(5000);
        Assert.assertNull(map.get("baz"));
    }

    @Test
    public void testMapComputeOperations() throws Throwable {
        final String value1 = "value1";
        final String value2 = "value2";
        final String value3 = "value3";
        AtomicMap<String, String> map = atomix().<String, String>atomicMapBuilder("testMapComputeOperationsMap").withProtocol(protocol()).build();
        Assert.assertEquals(value1, map.computeIfAbsent("foo", ( k) -> value1).value());
        Assert.assertEquals(value1, map.computeIfAbsent("foo", ( k) -> value2).value());
        Assert.assertNull(map.computeIfPresent("bar", ( k, v) -> value2));
        Assert.assertEquals(value3, map.computeIfPresent("foo", ( k, v) -> value3).value());
        Assert.assertNull(map.computeIfPresent("foo", ( k, v) -> null));
        Assert.assertEquals(value1, map.computeIf("foo", ( v) -> v == null, ( k, v) -> value1).value());
        Assert.assertEquals(value2, map.compute("foo", ( k, v) -> value2).value());
    }

    @Test
    public void testMapListeners() throws Throwable {
        final String value1 = "value1";
        final String value2 = "value2";
        final String value3 = "value3";
        AtomicMap<String, String> map = atomix().<String, String>atomicMapBuilder("testMapListenerMap").withProtocol(protocol()).build();
        AtomicMapTest.TestAtomicMapEventListener listener = new AtomicMapTest.TestAtomicMapEventListener();
        // add listener; insert new value into map and verify an INSERT event is received.
        map.addListener(listener);
        map.put("foo", value1);
        AtomicMapEvent<String, String> event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals(value1, event.newValue().value());
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
        Assert.assertEquals(value3, event.newValue().value());
        // perform a non-state changing operation and verify no events are received.
        map.putIfAbsent("foo", value1);
        Assert.assertFalse(listener.eventReceived());
        // verify REMOVE events are received correctly.
        map.remove("foo");
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals(value3, event.oldValue().value());
        // verify compute methods also generate events.
        map.computeIf("foo", ( v) -> v == null, ( k, v) -> value1);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals(value1, event.newValue().value());
        map.compute("foo", ( k, v) -> value2);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(UPDATE, event.type());
        Assert.assertEquals(value2, event.newValue().value());
        map.computeIf("foo", ( v) -> Objects.equals(v, value2), ( k, v) -> null);
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals(value2, event.oldValue().value());
        map.put("bar", "expire", Duration.ofSeconds(1));
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(INSERT, event.type());
        Assert.assertEquals("expire", event.newValue().value());
        event = listener.event();
        Assert.assertNotNull(event);
        Assert.assertEquals(REMOVE, event.type());
        Assert.assertEquals("expire", event.oldValue().value());
        map.removeListener(listener);
    }

    @Test
    public void testMapViews() throws Exception {
        AtomicMap<String, String> map = atomix().<String, String>atomicMapBuilder("testMapViews").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.keySet().isEmpty());
        Assert.assertTrue(map.entrySet().isEmpty());
        Assert.assertTrue(map.values().isEmpty());
        Assert.assertEquals(0, map.keySet().stream().count());
        Assert.assertEquals(0, map.entrySet().stream().count());
        Assert.assertEquals(0, map.values().stream().count());
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
        Assert.assertTrue(map.values().contains(new Versioned(one, 0)));
        Assert.assertTrue(map.entrySet().contains(Maps.immutableEntry(one, new Versioned(one, 0))));
        Assert.assertTrue(map.keySet().containsAll(Arrays.asList(one, two, three, four)));
        Assert.assertTrue(map.keySet().remove(one));
        Assert.assertFalse(map.keySet().contains(one));
        Assert.assertFalse(map.containsKey(one));
        Assert.assertTrue(map.entrySet().remove(Maps.immutableEntry(two, map.get(two))));
        Assert.assertFalse(map.keySet().contains(two));
        Assert.assertFalse(map.containsKey(two));
        Assert.assertTrue(map.entrySet().remove(Maps.immutableEntry(three, new Versioned(three, 0))));
        Assert.assertFalse(map.keySet().contains(three));
        Assert.assertFalse(map.containsKey(three));
        Assert.assertFalse(map.entrySet().remove(Maps.immutableEntry(four, new Versioned(four, 1))));
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
        Assert.assertEquals(97, map.values().toArray(new Versioned[97]).length);
        Iterator<String> iterator = map.keySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            i += 1;
            map.put(String.valueOf((100 * i)), String.valueOf((100 * i)));
        } 
        Assert.assertEquals(String.valueOf(100), map.get(String.valueOf(100)).value());
    }

    @Test
    public void testTransaction() throws Throwable {
        Transaction transaction1 = atomix().transactionBuilder().withIsolation(READ_COMMITTED).build();
        transaction1.begin();
        TransactionalMap<String, String> map1 = transaction1.<String, String>mapBuilder("test-transactional-map").withProtocol(protocol()).build();
        Transaction transaction2 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction2.begin();
        TransactionalMap<String, String> map2 = transaction2.<String, String>mapBuilder("test-transactional-map").withProtocol(protocol()).build();
        Assert.assertNull(map1.get("foo"));
        Assert.assertFalse(map1.containsKey("foo"));
        Assert.assertNull(map2.get("foo"));
        Assert.assertFalse(map2.containsKey("foo"));
        map1.put("foo", "bar");
        map1.put("bar", "baz");
        Assert.assertNull(map1.get("foo"));
        Assert.assertEquals(SUCCESS, transaction1.commit());
        Assert.assertNull(map2.get("foo"));
        Assert.assertEquals("baz", map2.get("bar"));
        map2.put("foo", "bar");
        Assert.assertEquals("bar", map2.get("foo"));
        map2.remove("foo");
        Assert.assertFalse(map2.containsKey("foo"));
        map2.put("foo", "baz");
        Assert.assertEquals(FAILURE, transaction2.commit());
        Transaction transaction3 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction3.begin();
        TransactionalMap<String, String> map3 = transaction3.<String, String>mapBuilder("test-transactional-map").withProtocol(protocol()).build();
        Assert.assertEquals("bar", map3.get("foo"));
        map3.put("foo", "baz");
        Assert.assertEquals("baz", map3.get("foo"));
        Assert.assertEquals(SUCCESS, transaction3.commit());
        AtomicMap<String, String> map = atomix().<String, String>atomicMapBuilder("test-transactional-map").withProtocol(protocol()).build();
        Assert.assertEquals("baz", map.get("foo").value());
        Assert.assertEquals("baz", map.get("bar").value());
        Map<String, Versioned<String>> result = map.getAllPresent(Collections.singleton("foo"));
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.size()) == 1));
        Assert.assertEquals("baz", result.get("foo").value());
    }

    private static class TestAtomicMapEventListener implements AtomicMapEventListener<String, String> {
        private final BlockingQueue<AtomicMapEvent<String, String>> queue = new LinkedBlockingQueue<>();

        @Override
        public void event(AtomicMapEvent<String, String> event) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public boolean eventReceived() {
            return !(queue.isEmpty());
        }

        public AtomicMapEvent<String, String> event() throws InterruptedException {
            return queue.take();
        }
    }
}

