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
package org.apache.camel.component.atomix.client.value;


import AtomixClientConstants.RESOURCE_ACTION;
import AtomixClientConstants.RESOURCE_ACTION_HAS_RESULT;
import AtomixClientConstants.RESOURCE_OLD_VALUE;
import AtomixValue.Action.COMPARE_AND_SET;
import AtomixValue.Action.GET;
import AtomixValue.Action.GET_AND_SET;
import AtomixValue.Action.SET;
import io.atomix.variables.DistributedValue;
import java.util.UUID;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.junit.Test;


public class AtomixValueProducerTest extends AtomixClientTestSupport {
    private static final String VALUE_NAME = UUID.randomUUID().toString();

    private DistributedValue<Object> value;

    @EndpointInject(uri = "direct:start")
    private FluentProducerTemplate fluent;

    // ************************************
    // Test
    // ************************************
    @Test
    public void test() throws Exception {
        final String val1 = context().getUuidGenerator().generateUuid();
        final String val2 = context().getUuidGenerator().generateUuid();
        Message result;
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, SET).withBody(val1).request(Message.class);
        assertFalse(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val1, value.get().join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, COMPARE_AND_SET).withHeader(RESOURCE_OLD_VALUE, val1).withBody(val2).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val2, value.get().join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, GET).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val2, result.getBody());
        assertEquals(val2, value.get().join());
        result = fluent.clearAll().withHeader(RESOURCE_ACTION, GET_AND_SET).withBody(val1).request(Message.class);
        assertTrue(result.getHeader(RESOURCE_ACTION_HAS_RESULT, Boolean.class));
        assertEquals(val2, result.getBody());
        assertEquals(val1, value.get().join());
    }
}

