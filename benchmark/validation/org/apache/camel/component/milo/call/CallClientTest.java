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
package org.apache.camel.component.milo.call;


import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.milo.AbstractMiloServerTest;
import org.apache.camel.component.milo.NodeIds;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for calling from the client side
 */
public class CallClientTest extends AbstractMiloServerTest {
    private static final String DIRECT_START_1 = "direct:start1";

    private static final String MILO_CLIENT_BASE_C1 = "milo-client:tcp://localhost:@@port@@";

    private static final String MILO_CLIENT_ITEM_C1_1 = (((((CallClientTest.MILO_CLIENT_BASE_C1) + "?node=") + (NodeIds.nodeValue(MockNamespace.URI, MockNamespace.FOLDER_ID))) + "&method=") + (NodeIds.nodeValue(MockNamespace.URI, "id1"))) + "&overrideHost=true";

    @Produce(uri = CallClientTest.DIRECT_START_1)
    protected ProducerTemplate producer1;

    private OpcUaServer server;

    private MockCall.Call1 call1;

    @Test
    public void testCall1() throws Exception {
        // call
        CallClientTest.doCall(this.producer1, "foo");
        CallClientTest.doCall(this.producer1, "bar");
        // assert
        Assert.assertArrayEquals(new Object[]{ "foo", "bar" }, this.call1.calls.toArray());
    }
}

