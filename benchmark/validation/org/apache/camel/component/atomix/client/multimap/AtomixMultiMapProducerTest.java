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
package org.apache.camel.component.atomix.client.multimap;


import AtomixClientConstants.RESOURCE_ACTION;
import AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT;
import AtomixClientConstants.RESOURCE_KEY;
import AtomixClientConstants.RESOURCE_VALUE;
import AtomixMultiMap.Action.CLEAR;
import AtomixMultiMap.Action.CONTAINS_KEY;
import AtomixMultiMap.Action.GET;
import AtomixMultiMap.Action.IS_EMPTY;
import AtomixMultiMap.Action.PUT;
import AtomixMultiMap.Action.REMOVE;
import AtomixMultiMap.Action.REMOVE_VALUE;
import AtomixMultiMap.Action.SIZE;
import io.atomix.collections.DistributedMultiMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.junit.Test;


public class AtomixMultiMapProducerTest extends AtomixClientTestSupport {
    private static final String MAP_NAME = UUID.randomUUID().toString();

    private DistributedMultiMap<Object, Object> map;

    @EndpointInject(uri = "direct:start")
    private FluentProducerTemplate fluent;

    // ************************************
    // Test
    // ************************************
    @Test
    public void testPut() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT).withHeader(RESOURCE_KEY, key).withBody(val1).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertEquals(Arrays.asList(val1), map.get(key).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PUT).withHeader(RESOURCE_KEY, key).withBody(val2).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertEquals(Arrays.asList(val1, val2), map.get(key).join());
    }

    @Test
    public void testGet() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, GET).withHeader(RESOURCE_KEY, key).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Collection.class).isEmpty());
        assertFalse(map.containsKey(key).join());
        map.put(key, val).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, GET).withHeader(RESOURCE_KEY, key).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(Arrays.asList(val), result.getBody(Collection.class));
        assertTrue(map.containsKey(key).join());
    }

    @Test
    public void testSizeClearIsEmpty() throws Exception {
        final String key1 = context().getUuidGenerator().generateUuid();
        final String key2 = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(0, result.getBody(Integer.class).intValue());
        assertEquals(map.size().join(), result.getBody(Integer.class));
        map.put(key1, context().getUuidGenerator().generateUuid()).join();
        map.put(key1, context().getUuidGenerator().generateUuid()).join();
        map.put(key2, context().getUuidGenerator().generateUuid()).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(map.size().join(), result.getBody(Integer.class));
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).withHeader(RESOURCE_KEY, key1).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(map.size(key1).join(), result.getBody(Integer.class));
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

    // @Test
    // public void testContainsValue() throws Exception {
    // final String key = context().getUuidGenerator().generateUuid();
    // final String val1 = context().getUuidGenerator().generateUuid();
    // final String val2 = context().getUuidGenerator().generateUuid();
    // 
    // Message result;
    // 
    // result = fluent.clearAll()
    // .withHeader(AtomixClientConstants.RESOURCE_ACTION, AtomixClientMultiMapAction.CONTAINS_VALUE)
    // .withHeader(AtomixClientConstants.RESOURCE_VALUE, val1)
    // .request(Message.class);
    // 
    // assertTrue(result.getHeader(AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT, Boolean.class));
    // assertFalse(result.getBody(Boolean.class));
    // assertFalse(map.containsValue(val1).join());
    // 
    // map.put(key, val1).join();
    // map.put(key, val2).join();
    // 
    // result = fluent.clearAll()
    // .withHeader(AtomixClientConstants.RESOURCE_ACTION, AtomixClientMultiMapAction.CONTAINS_VALUE)
    // .withHeader(AtomixClientConstants.RESOURCE_VALUE, val1)
    // .request(Message.class);
    // 
    // assertTrue(result.getHeader(AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT, Boolean.class));
    // assertTrue(result.getBody(Boolean.class));
    // assertTrue(map.containsValue(val1).join());
    // 
    // result = fluent.clearAll()
    // .withHeader(AtomixClientConstants.RESOURCE_ACTION, AtomixClientMultiMapAction.CONTAINS_VALUE)
    // .withHeader(AtomixClientConstants.RESOURCE_VALUE, val2)
    // .request(Message.class);
    // 
    // assertTrue(result.getHeader(AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT, Boolean.class));
    // assertTrue(result.getBody(Boolean.class));
    // assertTrue(map.containsValue(val2).join());
    // }
    // 
    // @Test
    // public void testContainsEntry() throws Exception {
    // final String key = context().getUuidGenerator().generateUuid();
    // final String val1 = context().getUuidGenerator().generateUuid();
    // final String val2 = context().getUuidGenerator().generateUuid();
    // map.put(key, val1).join();
    // map.put(key, val2).join();
    // 
    // Message result;
    // 
    // result = fluent.clearAll()
    // .withHeader(AtomixClientConstants.RESOURCE_ACTION, AtomixClientMultiMapAction.CONTAINS_ENTRY)
    // .withHeader(AtomixClientConstants.RESOURCE_KEY, key)
    // .withHeader(AtomixClientConstants.RESOURCE_VALUE, val1)
    // .request(Message.class);
    // 
    // assertTrue(result.getHeader(AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT, Boolean.class));
    // assertTrue(result.getBody(Boolean.class));
    // assertTrue(map.containsEntry(key, val1).join());
    // 
    // result = fluent.clearAll()
    // .withHeader(AtomixClientConstants.RESOURCE_ACTION, AtomixClientMultiMapAction.CONTAINS_ENTRY)
    // .withHeader(AtomixClientConstants.RESOURCE_KEY, key)
    // .withHeader(AtomixClientConstants.RESOURCE_VALUE, val2)
    // .request(Message.class);
    // 
    // assertTrue(result.getHeader(AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT, Boolean.class));
    // assertTrue(result.getBody(Boolean.class));
    // assertTrue(map.containsEntry(key, val2).join());
    // 
    // }
    @Test
    public void testRemove() throws Exception {
        final String key = context().getUuidGenerator().generateUuid();
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        final String val3 = context().getUuidGenerator().generateUuid();
        map.put(key, val1).join();
        map.put(key, val2).join();
        map.put(key, val3).join();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE).withHeader(RESOURCE_KEY, key).withHeader(RESOURCE_VALUE, val1).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(Arrays.asList(val2, val3), map.get(key).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE_VALUE).withHeader(RESOURCE_VALUE, val2).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(Arrays.asList(val3), map.get(key).join());
        assertTrue(map.containsKey(key).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE).withHeader(RESOURCE_KEY, key).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(map.containsKey(key).join());
    }
}

