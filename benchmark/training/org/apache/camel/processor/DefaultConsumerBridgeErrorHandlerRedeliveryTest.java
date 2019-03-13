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
package org.apache.camel.processor;


import Exchange.EXCEPTION_CAUGHT;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DefaultConsumerBridgeErrorHandlerRedeliveryTest extends DefaultConsumerBridgeErrorHandlerTest {
    protected final AtomicInteger redeliverCounter = new AtomicInteger();

    @Test
    public void testDefaultConsumerBridgeErrorHandler() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World", "Hello World");
        getMockEndpoint("mock:dead").expectedBodiesReceived("Cannot process");
        latch.countDown();
        assertMockEndpointsSatisfied();
        // should not attempt redelivery as we must be exhausted when bridging the error handler
        Assert.assertEquals(0, redeliverCounter.get());
        Exception cause = getMockEndpoint("mock:dead").getReceivedExchanges().get(0).getProperty(EXCEPTION_CAUGHT, Exception.class);
        Assert.assertNotNull(cause);
        Assert.assertEquals("Simulated", cause.getMessage());
    }
}

