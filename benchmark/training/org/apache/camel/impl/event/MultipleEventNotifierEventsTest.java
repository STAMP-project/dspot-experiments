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
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class MultipleEventNotifierEventsTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    private static List<CamelEvent> events2 = new ArrayList<>();

    @Test
    public void testExchangeDone() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(14, MultipleEventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, MultipleEventNotifierEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, MultipleEventNotifierEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, MultipleEventNotifierEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, MultipleEventNotifierEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, MultipleEventNotifierEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, MultipleEventNotifierEventsTest.events.get(5));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events.get(6));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, MultipleEventNotifierEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events.get(8));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events.get(9));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events.get(10));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events.get(11));
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, MultipleEventNotifierEventsTest.events.get(12));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events.get(13));
        Assert.assertEquals(8, MultipleEventNotifierEventsTest.events2.size());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events2.get(0));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, MultipleEventNotifierEventsTest.events2.get(1));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events2.get(2));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events2.get(3));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events2.get(4));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events2.get(5));
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, MultipleEventNotifierEventsTest.events2.get(6));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events2.get(7));
        context.stop();
        Assert.assertEquals(20, MultipleEventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStoppingEvent.class, MultipleEventNotifierEventsTest.events.get(14));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, MultipleEventNotifierEventsTest.events.get(15));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, MultipleEventNotifierEventsTest.events.get(16));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, MultipleEventNotifierEventsTest.events.get(17));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, MultipleEventNotifierEventsTest.events.get(18));
        TestSupport.assertIsInstanceOf(CamelContextStoppedEvent.class, MultipleEventNotifierEventsTest.events.get(19));
        Assert.assertEquals(8, MultipleEventNotifierEventsTest.events2.size());
    }

    @Test
    public void testExchangeFailed() throws Exception {
        try {
            template.sendBody("direct:fail", "Hello World");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            // expected
            TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
        }
        Assert.assertEquals(10, MultipleEventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, MultipleEventNotifierEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, MultipleEventNotifierEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, MultipleEventNotifierEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, MultipleEventNotifierEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, MultipleEventNotifierEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, MultipleEventNotifierEventsTest.events.get(5));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events.get(6));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, MultipleEventNotifierEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeFailedEvent.class, MultipleEventNotifierEventsTest.events.get(8));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events.get(9));
        Assert.assertEquals(4, MultipleEventNotifierEventsTest.events2.size());
        context.stop();
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, MultipleEventNotifierEventsTest.events2.get(0));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, MultipleEventNotifierEventsTest.events2.get(1));
        TestSupport.assertIsInstanceOf(ExchangeFailedEvent.class, MultipleEventNotifierEventsTest.events2.get(2));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, MultipleEventNotifierEventsTest.events2.get(3));
        Assert.assertEquals(16, MultipleEventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStoppingEvent.class, MultipleEventNotifierEventsTest.events.get(10));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, MultipleEventNotifierEventsTest.events.get(11));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, MultipleEventNotifierEventsTest.events.get(12));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, MultipleEventNotifierEventsTest.events.get(13));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, MultipleEventNotifierEventsTest.events.get(14));
        TestSupport.assertIsInstanceOf(CamelContextStoppedEvent.class, MultipleEventNotifierEventsTest.events.get(15));
        Assert.assertEquals(4, MultipleEventNotifierEventsTest.events2.size());
    }
}

