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
import AtomixMap.Action.PUT;
import io.atomix.collections.DistributedMap;
import java.util.UUID;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.junit.Test;


public class AtomixMapNodesProducerTest extends AtomixClientTestSupport {
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
}

