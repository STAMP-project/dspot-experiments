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
package org.apache.camel.component.atomix.client.queue;


import AtomixClientConstants.RESOURCE_ACTION;
import AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT;
import AtomixClientConstants.RESOURCE_VALUE;
import AtomixQueue.Action.ADD;
import AtomixQueue.Action.CLEAR;
import AtomixQueue.Action.CONTAINS;
import AtomixQueue.Action.IS_EMPTY;
import AtomixQueue.Action.OFFER;
import AtomixQueue.Action.PEEK;
import AtomixQueue.Action.POLL;
import AtomixQueue.Action.REMOVE;
import AtomixQueue.Action.SIZE;
import io.atomix.collections.DistributedQueue;
import java.util.UUID;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.junit.Test;


public class AtomixQueueProducerTest extends AtomixClientTestSupport {
    private static final String QUEUE_NAME = UUID.randomUUID().toString();

    private DistributedQueue<Object> queue;

    @EndpointInject(uri = "direct:start")
    private FluentProducerTemplate fluent;

    // ************************************
    // Test
    // ************************************
    @Test
    public void testAdd() throws Exception {
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, ADD).withBody(val1).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(queue.contains(val1).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, ADD).withBody(val2).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(queue.contains(val2).join());
    }

    @Test
    public void testOfferPeekAndPoll() throws Exception {
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, OFFER).withBody(val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(queue.contains(val).join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, PEEK).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(queue.contains(val).join());
        assertEquals(val, result.getBody());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, POLL).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(queue.contains(val).join());
        assertEquals(val, result.getBody());
    }

    @Test
    public void testSizeClearIsEmpty() throws Exception {
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(0, result.getBody(Integer.class).intValue());
        assertEquals(queue.size().join(), result.getBody(Integer.class));
        queue.add(val1).join();
        queue.add(val2).join();
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SIZE).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(queue.size().join(), result.getBody(Integer.class));
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, IS_EMPTY).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertFalse(queue.isEmpty().join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CLEAR).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(0, queue.size().join().intValue());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, IS_EMPTY).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertTrue(queue.isEmpty().join());
    }

    @Test
    public void testContains() throws Exception {
        final String val = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CONTAINS).withBody(val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(result.getBody(Boolean.class));
        assertFalse(queue.contains(val).join());
        queue.add(val);
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, CONTAINS).withBody(val).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertTrue(result.getBody(Boolean.class));
        assertTrue(queue.contains(val).join());
    }

    @Test
    public void testRemove() throws Exception {
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        queue.add(val1).join();
        queue.add(val2).join();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, REMOVE).withHeader(RESOURCE_VALUE, val1).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertFalse(queue.contains(val1).join());
        assertTrue(queue.contains(val2).join());
    }
}

