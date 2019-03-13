/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.websocket;


import org.apache.nifi.processor.Processor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestWebSocketMessageRouters {
    @Test
    public void testRegisterProcessor() throws Exception {
        final String endpointId = "endpoint-id";
        final WebSocketMessageRouters routers = new WebSocketMessageRouters();
        try {
            routers.getRouterOrFail(endpointId);
            Assert.fail("Should fail because no route exists with the endpointId.");
        } catch (WebSocketConfigurationException e) {
        }
        final Processor processor1 = Mockito.mock(Processor.class);
        Mockito.when(processor1.getIdentifier()).thenReturn("processor-1");
        Assert.assertFalse(routers.isProcessorRegistered(endpointId, processor1));
        routers.registerProcessor(endpointId, processor1);
        Assert.assertNotNull(routers.getRouterOrFail(endpointId));
        Assert.assertTrue(routers.isProcessorRegistered(endpointId, processor1));
        routers.deregisterProcessor(endpointId, processor1);
        Assert.assertFalse(routers.isProcessorRegistered(endpointId, processor1));
    }
}

