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
package org.apache.camel.processor.async;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.ExchangeSendingEvent;
import org.apache.camel.spi.CamelEvent.ExchangeSentEvent;
import org.junit.Assert;
import org.junit.Test;


public class AsyncEndpointEventNotifierSendingTest extends ContextTestSupport {
    private final List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testAsyncEndpointEventNotifier() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");
        String reply = template.requestBody("direct:start", "Hello Camel", String.class);
        Assert.assertEquals("Bye Camel", reply);
        assertMockEndpointsSatisfied();
        Assert.assertEquals(8, events.size());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, events.get(0));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, events.get(1));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, events.get(2));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, events.get(3));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, events.get(4));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, events.get(5));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, events.get(6));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, events.get(7));
    }
}

