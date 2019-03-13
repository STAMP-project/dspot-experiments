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


public class EventNotifierEventsTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testExchangeDone() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(14, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierEventsTest.events.get(5));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierEventsTest.events.get(9));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierEventsTest.events.get(11));
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, EventNotifierEventsTest.events.get(12));
        // this is the sent using the produce template to start the test
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierEventsTest.events.get(13));
        context.stop();
        Assert.assertEquals(20, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStoppingEvent.class, EventNotifierEventsTest.events.get(14));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, EventNotifierEventsTest.events.get(15));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, EventNotifierEventsTest.events.get(16));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, EventNotifierEventsTest.events.get(17));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, EventNotifierEventsTest.events.get(18));
        TestSupport.assertIsInstanceOf(CamelContextStoppedEvent.class, EventNotifierEventsTest.events.get(19));
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
        Assert.assertEquals(10, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierEventsTest.events.get(5));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierEventsTest.events.get(6));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeFailedEvent.class, EventNotifierEventsTest.events.get(8));
        // this is the sent using the produce template to start the test
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierEventsTest.events.get(9));
        context.stop();
        Assert.assertEquals(16, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStoppingEvent.class, EventNotifierEventsTest.events.get(10));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, EventNotifierEventsTest.events.get(11));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, EventNotifierEventsTest.events.get(12));
        TestSupport.assertIsInstanceOf(RouteStoppedEvent.class, EventNotifierEventsTest.events.get(13));
        TestSupport.assertIsInstanceOf(RouteRemovedEvent.class, EventNotifierEventsTest.events.get(14));
        TestSupport.assertIsInstanceOf(CamelContextStoppedEvent.class, EventNotifierEventsTest.events.get(15));
    }

    @Test
    public void testSuspendResume() throws Exception {
        Assert.assertEquals(6, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierEventsTest.events.get(5));
        context.suspend();
        Assert.assertEquals(8, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextSuspendingEvent.class, EventNotifierEventsTest.events.get(6));
        // notice direct component is not suspended (as they are internal)
        TestSupport.assertIsInstanceOf(CamelContextSuspendedEvent.class, EventNotifierEventsTest.events.get(7));
        context.resume();
        Assert.assertEquals(10, EventNotifierEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextResumingEvent.class, EventNotifierEventsTest.events.get(8));
        TestSupport.assertIsInstanceOf(CamelContextResumedEvent.class, EventNotifierEventsTest.events.get(9));
    }
}

