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
package org.apache.camel.impl.event;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class EventNotifierExchangeSentTest extends ContextTestSupport {
    protected static List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testExchangeSent() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(8, EventNotifierExchangeSentTest.events.size());
        ExchangeSendingEvent e0 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(0));
        ExchangeSendingEvent e1 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(1));
        ExchangeSentEvent e2 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(2));
        ExchangeSendingEvent e3 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(3));
        ExchangeSentEvent e4 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(4));
        ExchangeSendingEvent e5 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(5));
        ExchangeSentEvent e6 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(6));
        ExchangeSentEvent e7 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(7));
        Assert.assertEquals("direct://start", e0.getEndpoint().getEndpointUri());
        Assert.assertEquals("log://foo", e1.getEndpoint().getEndpointUri());
        Assert.assertEquals("log://foo", e2.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://bar", e3.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://bar", e4.getEndpoint().getEndpointUri());
        long time = e4.getTimeTaken();
        Assert.assertTrue(("Should take about 0.5 sec, was: " + time), (time > 400));
        Assert.assertEquals("mock://result", e5.getEndpoint().getEndpointUri());
        Assert.assertEquals("mock://result", e6.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://start", e7.getEndpoint().getEndpointUri());
        time = e7.getTimeTaken();
        Assert.assertTrue(("Should take about 0.5 sec, was: " + time), (time > 400));
    }

    @Test
    public void testExchangeSentRecipient() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:foo", "Hello World", "foo", "direct:cool,direct:start");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(oneExchangeDone.matchesMockWaitTime());
        Assert.assertEquals(12, EventNotifierExchangeSentTest.events.size());
        ExchangeSendingEvent e0 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(0));
        ExchangeSendingEvent e1 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(1));
        ExchangeSentEvent e2 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(2));
        ExchangeSendingEvent e3 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(3));
        ExchangeSendingEvent e4 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(4));
        ExchangeSentEvent e5 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(5));
        ExchangeSendingEvent e6 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(6));
        ExchangeSentEvent e7 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(7));
        ExchangeSendingEvent e8 = TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierExchangeSentTest.events.get(8));
        ExchangeSentEvent e9 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(9));
        ExchangeSentEvent e10 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(10));
        ExchangeSentEvent e11 = TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierExchangeSentTest.events.get(11));
        Assert.assertEquals("direct://foo", e0.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://cool", e1.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://cool", e2.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://start", e3.getEndpoint().getEndpointUri());
        Assert.assertEquals("log://foo", e4.getEndpoint().getEndpointUri());
        Assert.assertEquals("log://foo", e5.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://bar", e6.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://bar", e7.getEndpoint().getEndpointUri());
        Assert.assertEquals("mock://result", e8.getEndpoint().getEndpointUri());
        Assert.assertEquals("mock://result", e9.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://start", e10.getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://foo", e11.getEndpoint().getEndpointUri());
    }

    @Test
    public void testExchangeWireTap() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:tap", "Hello World");
        assertMockEndpointsSatisfied();
        // give it time to complete
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(6, EventNotifierExchangeSentTest.events.size()));
        // we should find log:foo which we tapped
        // which runs async so they can be in random order
        boolean found = false;
        boolean found2 = false;
        for (CamelEvent event : EventNotifierExchangeSentTest.events) {
            if (event instanceof ExchangeSendingEvent) {
                ExchangeSendingEvent sending = ((ExchangeSendingEvent) (event));
                String uri = sending.getEndpoint().getEndpointUri();
                if ("log://foo".equals(uri)) {
                    found = true;
                }
            } else
                if (event instanceof ExchangeSentEvent) {
                    ExchangeSentEvent sent = ((ExchangeSentEvent) (event));
                    String uri = sent.getEndpoint().getEndpointUri();
                    if ("log://foo".equals(uri)) {
                        found2 = true;
                    }
                }

        }
        Assert.assertTrue("We should find log:foo being wire tapped", found);
        Assert.assertTrue("We should find log:foo being wire tapped", found2);
    }
}

