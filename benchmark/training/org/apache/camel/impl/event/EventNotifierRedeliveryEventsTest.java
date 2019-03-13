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
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class EventNotifierRedeliveryEventsTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testExchangeRedeliverySync() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:dead").maximumRedeliveries(4).redeliveryDelay(0));
                from("direct:start").throwException(new IllegalArgumentException("Damn"));
            }
        });
        context.start();
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(oneExchangeDone.matchesMockWaitTime());
        Assert.assertEquals(12, EventNotifierRedeliveryEventsTest.events.size());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierRedeliveryEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierRedeliveryEventsTest.events.get(1));
        ExchangeRedeliveryEvent e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(2));
        Assert.assertEquals(1, e.getAttempt());
        e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(3));
        Assert.assertEquals(2, e.getAttempt());
        e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(4));
        Assert.assertEquals(3, e.getAttempt());
        e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(5));
        Assert.assertEquals(4, e.getAttempt());
        TestSupport.assertIsInstanceOf(ExchangeFailureHandlingEvent.class, EventNotifierRedeliveryEventsTest.events.get(6));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierRedeliveryEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierRedeliveryEventsTest.events.get(8));
        TestSupport.assertIsInstanceOf(ExchangeFailureHandledEvent.class, EventNotifierRedeliveryEventsTest.events.get(9));
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, EventNotifierRedeliveryEventsTest.events.get(10));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierRedeliveryEventsTest.events.get(11));
    }

    @Test
    public void testExchangeRedeliveryAsync() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:dead").maximumRedeliveries(4).asyncDelayedRedelivery().redeliveryDelay(10));
                from("direct:start").throwException(new IllegalArgumentException("Damn"));
            }
        });
        context.start();
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(oneExchangeDone.matchesMockWaitTime());
        Assert.assertEquals(12, EventNotifierRedeliveryEventsTest.events.size());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierRedeliveryEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierRedeliveryEventsTest.events.get(1));
        ExchangeRedeliveryEvent e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(2));
        Assert.assertEquals(1, e.getAttempt());
        e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(3));
        Assert.assertEquals(2, e.getAttempt());
        e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(4));
        Assert.assertEquals(3, e.getAttempt());
        e = TestSupport.assertIsInstanceOf(ExchangeRedeliveryEvent.class, EventNotifierRedeliveryEventsTest.events.get(5));
        Assert.assertEquals(4, e.getAttempt());
        // since its async the ordering of the rest can be different depending per OS and timing
    }
}

