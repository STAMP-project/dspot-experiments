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
package org.apache.camel.component.milo.server;


import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.junit.Test;


/**
 * Unit tests for milo server component without using an actual connection
 */
public class ServerLocalTest extends CamelTestSupport {
    private static final String MILO_ITEM_1 = "milo-server:myitem1";

    private static final String MOCK_TEST = "mock:test";

    @EndpointInject(uri = ServerLocalTest.MOCK_TEST)
    protected MockEndpoint testEndpoint;

    @Test
    public void testAcceptVariantString() {
        sendBody(ServerLocalTest.MILO_ITEM_1, new Variant("Foo"));
    }

    @Test
    public void testAcceptVariantDouble() {
        sendBody(ServerLocalTest.MILO_ITEM_1, new Variant(0.0));
    }

    @Test
    public void testAcceptString() {
        sendBody(ServerLocalTest.MILO_ITEM_1, "Foo");
    }

    @Test
    public void testAcceptDouble() {
        sendBody(ServerLocalTest.MILO_ITEM_1, 0.0);
    }

    @Test
    public void testAcceptDataValueString() {
        sendBody(ServerLocalTest.MILO_ITEM_1, new org.eclipse.milo.opcua.stack.core.types.builtin.DataValue(new Variant("Foo")));
    }

    @Test
    public void testAcceptDataValueDouble() {
        sendBody(ServerLocalTest.MILO_ITEM_1, new org.eclipse.milo.opcua.stack.core.types.builtin.DataValue(new Variant(0.0)));
    }
}

