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


import AtomixClientConstants.EVENT_TYPE;
import DistributedValue.Events.CHANGE;
import io.atomix.variables.DistributedValue;
import java.util.UUID;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class AtomixValueConsumerTest extends AtomixClientTestSupport {
    private static final String VALUE_NAME = UUID.randomUUID().toString();

    private DistributedValue<Object> value;

    // ************************************
    // Test
    // ************************************
    @Test
    public void testEvents() throws Exception {
        String val1 = context().getUuidGenerator().generateUuid();
        String val2 = context().getUuidGenerator().generateUuid();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        mock.message(0).body().isEqualTo(val1);
        mock.message(0).header(EVENT_TYPE).isEqualTo(CHANGE);
        mock.message(1).body().isEqualTo(val2);
        mock.message(1).header(EVENT_TYPE).isEqualTo(CHANGE);
        value.set(val1).join();
        value.compareAndSet(val1, val2).join();
        mock.assertIsSatisfied();
    }
}

