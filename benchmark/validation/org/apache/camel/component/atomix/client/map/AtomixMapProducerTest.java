/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.atomix.client.map;


import AtomixClientConstants.RESOURCE_ACTION;
import AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT;
import AtomixClientConstants.RESOURCE_KEY;
import AtomixClientConstants.RESOURCE_OLD_VALUE;
import AtomixClientConstants.RESOURCE_TTL;
import AtomixClientConstants.RESOURCE_VALUE;
import AtomixMap.Action.CLEAR;
import AtomixMap.Action.CONTAINS_KEY;
import AtomixMap.Action.CONTAINS_VALUE;
import AtomixMap.Action.ENTRY_SET;
import AtomixMap.Action.GET;
import AtomixMap.Action.IS_EMPTY;
import AtomixMap.Action.PUT;
import AtomixMap.Action.PUT_IF_ABSENT;
import AtomixMap.Action.REMOVE;
import AtomixMap.Action.REPLACE;
import AtomixMap.Action.SIZE;
import AtomixMap.Action.VALUES;
import io.atomix.collections.DistributedMap;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.hamcrest.core.Is;
import org.junit.Test;


public class AtomixMapProducerTest extends AtomixClientTestSupport {
    private static final String MAP_NAME = UUID.randomUUID().toString();

    private DistributedMap<Object, Object> map;

    @EndpointInject(uri = "direct:start")
    private FluentProducerTemplate fluent;

    // ************************************
    // Test
    // ************************************
    @Test
    public void testPut() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT).withHeader(RESOURCE_KEY, key).withBody(val).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val, result.getBody());
        assertEquals(val, map.get(key).join());
    }

    @Test
    public void testPutWithTTL() throws Exception {
        final String key1 = context().getUuidGenerator().generateUuid();
        final String key2 = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT).withHeader(RESOURCE_KEY, key1).withHeader(RESOURCE_TTL, "1s").withBody(val).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val, result.getBody());
        assertEquals(val, map.get(key1).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT).withHeader(RESOURCE_KEY, key2).withHeader(RESOURCE_TTL, "250").withBody(val).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val, result.getBody());
        assertEquals(val, map.get(key2).join());
        CountDownLatch latch = new CountDownLatch(2);
        map.onRemove(key1, ( e) -> latch.countDown());
        map.onRemove(key2, ( e) -> latch.countDown());
        latch.await();
        assertFalse(map.containsKey(key1).join());
        assertFalse(map.containsKey(key2).join());
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT_IF_ABSENT).withHeader(RESOURCE_KEY, key).withBody(val1).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val1, result.getBody());
        assertEquals(val1, map.get(key).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT_IF_ABSENT).withHeader(RESOURCE_KEY, key).withBody(val2).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val1, result.getBody());
        assertEquals(val1, map.get(key).join());
    }

    @Test
    public void testGet() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, GET).withHeader(RESOURCE_KEY, key).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(map.containsKey(key).join());
        map.put(key, val).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, GET).withHeader(RESOURCE_KEY, key).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val, result.getBody(String.class));
        assertTrue(map.containsKey(key).join());
    }

    @Test
    public void testSizeClearIsEmpty() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(0, result.getBody(Integer.class).intValue());
        assertEquals(map.size().join(), result.getBody(Integer.class));
        map.put(key, val).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(1, result.getBody(Integer.class).intValue());
        assertEquals(map.size().join(), result.getBody(Integer.class));
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, IS_EMPTY).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertFalse(map.isEmpty().join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CLEAR).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(0, map.size().join().intValue());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, IS_EMPTY).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertTrue(map.isEmpty().join());
    }

    @Test
    public void testContainsKey() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CONTAINS_KEY).withHeader(RESOURCE_KEY, key).withBody(val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertFalse(map.containsKey(key).join());
        map.put(key, val).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CONTAINS_KEY).withHeader(RESOURCE_KEY, key).withBody(val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertTrue(map.containsKey(key).join());
    }

    @Test
    public void testContainsValue() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CONTAINS_VALUE).withHeader(RESOURCE_VALUE, val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertFalse(map.containsKey(key).join());
        map.put(key, val).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CONTAINS_VALUE).withHeader(RESOURCE_VALUE, val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertTrue(map.containsValue(val).join());
    }

    @Test
    public void testRemove() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        map.put(key, val).join();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE).withHeader(RESOURCE_KEY, key).withHeader(RESOURCE_VALUE, context().getUuidGenerator().generateUuid()).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertTrue(map.containsKey(key).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE).withHeader(RESOURCE_KEY, key).withHeader(RESOURCE_VALUE, val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertFalse(map.containsKey(key).join());
        map.put(key, val).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE).withHeader(RESOURCE_KEY, key).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val, result.getBody(String.class));
        assertFalse(map.containsKey(key).join());
    }

    @Test
    public void testReplace() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String oldVal = context().getUuidGenerator().generateUuid();
        final String newVal = context().getUuidGenerator().generateUuid();
        map.put(key, oldVal).join();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REPLACE).withHeader(RESOURCE_KEY, key).withHeader(RESOURCE_OLD_VALUE, context().getUuidGenerator().generateUuid()).withBody(newVal).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertEquals(oldVal, map.get(key).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REPLACE).withHeader(RESOURCE_KEY, key).withHeader(RESOURCE_OLD_VALUE, oldVal).withBody(newVal).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertEquals(newVal, map.get(key).join());
        map.put(key, oldVal).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REPLACE).withHeader(RESOURCE_KEY, key).withBody(newVal).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(oldVal, result.getBody(String.class));
        assertEquals(newVal, map.get(key).join());
    }

    @Test
    public void testValues() throws Exception {
        map.put(context().getUuidGenerator().generateUuid(), context().getUuidGenerator().generateUuid()).join();
        map.put(context().getUuidGenerator().generateUuid(), context().getUuidGenerator().generateUuid()).join();
        map.put(context().getUuidGenerator().generateUuid(), context().getUuidGenerator().generateUuid()).join();
        Message result = fluent.clearAll().withHeader(RESOURCE_ACTION, VALUES).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertThat(map.values().join(), Is.is(result.getBody(Collection.class)));
    }

    @Test
    public void testEntrySet() throws Exception {
        map.put(context().getUuidGenerator().generateUuid(), context().getUuidGenerator().generateUuid()).join();
        map.put(context().getUuidGenerator().generateUuid(), context().getUuidGenerator().generateUuid()).join();
        map.put(context().getUuidGenerator().generateUuid(), context().getUuidGenerator().generateUuid()).join();
        Message result = fluent.clearAll().withHeader(RESOURCE_ACTION, ENTRY_SET).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(map.entrySet().join().size(), result.getBody(Set.class).size());
    }
}

