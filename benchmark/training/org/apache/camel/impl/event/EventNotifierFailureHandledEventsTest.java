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
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.SendProcessor;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class EventNotifierFailureHandledEventsTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testExchangeDeadLetterChannel() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:dead"));
                from("direct:start").throwException(new IllegalArgumentException("Damn"));
            }
        });
        context.start();
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(12, EventNotifierFailureHandledEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierFailureHandledEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierFailureHandledEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierFailureHandledEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierFailureHandledEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierFailureHandledEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierFailureHandledEventsTest.events.get(5));
        ExchangeFailureHandlingEvent e0 = TestSupport.assertIsInstanceOf(ExchangeFailureHandlingEvent.class, EventNotifierFailureHandledEventsTest.events.get(6));
        Assert.assertEquals("should be DLC", true, e0.isDeadLetterChannel());
        Assert.assertEquals("mock://dead", e0.getDeadLetterUri());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierFailureHandledEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierFailureHandledEventsTest.events.get(8));
        ExchangeFailureHandledEvent e = TestSupport.assertIsInstanceOf(ExchangeFailureHandledEvent.class, EventNotifierFailureHandledEventsTest.events.get(9));
        Assert.assertEquals("should be DLC", true, e.isDeadLetterChannel());
        Assert.assertTrue("should be marked as failure handled", e.isHandled());
        Assert.assertFalse("should not be continued", e.isContinued());
        Processor fh = e.getFailureHandler();
        if (fh.getClass().getName().endsWith("ProcessorToReactiveProcessorBridge")) {
            fh = getProcessor();
        }
        SendProcessor send = TestSupport.assertIsInstanceOf(SendProcessor.class, fh);
        Assert.assertEquals("mock://dead", send.getDestination().getEndpointUri());
        Assert.assertEquals("mock://dead", e.getDeadLetterUri());
        // dead letter channel will mark the exchange as completed
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, EventNotifierFailureHandledEventsTest.events.get(10));
        // and the last event should be the direct:start
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierFailureHandledEventsTest.events.get(11));
        ExchangeSentEvent sent = ((ExchangeSentEvent) (EventNotifierFailureHandledEventsTest.events.get(11)));
        Assert.assertEquals("direct://start", sent.getEndpoint().getEndpointUri());
    }

    @Test
    public void testExchangeOnException() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(IllegalArgumentException.class).handled(true).to("mock:dead");
                from("direct:start").throwException(new IllegalArgumentException("Damn"));
            }
        });
        context.start();
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(12, EventNotifierFailureHandledEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierFailureHandledEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierFailureHandledEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierFailureHandledEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierFailureHandledEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierFailureHandledEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierFailureHandledEventsTest.events.get(5));
        ExchangeFailureHandlingEvent e0 = TestSupport.assertIsInstanceOf(ExchangeFailureHandlingEvent.class, EventNotifierFailureHandledEventsTest.events.get(6));
        Assert.assertEquals("should NOT be DLC", false, e0.isDeadLetterChannel());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierFailureHandledEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierFailureHandledEventsTest.events.get(8));
        ExchangeFailureHandledEvent e = TestSupport.assertIsInstanceOf(ExchangeFailureHandledEvent.class, EventNotifierFailureHandledEventsTest.events.get(9));
        Assert.assertEquals("should NOT be DLC", false, e.isDeadLetterChannel());
        Assert.assertTrue("should be marked as failure handled", e.isHandled());
        Assert.assertFalse("should not be continued", e.isContinued());
        // onException will handle the exception
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, EventNotifierFailureHandledEventsTest.events.get(10));
        // and the last event should be the direct:start
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierFailureHandledEventsTest.events.get(11));
        ExchangeSentEvent sent = ((ExchangeSentEvent) (EventNotifierFailureHandledEventsTest.events.get(11)));
        Assert.assertEquals("direct://start", sent.getEndpoint().getEndpointUri());
    }

    @Test
    public void testExchangeDoTryDoCatch() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").doTry().throwException(new IllegalArgumentException("Damn")).doCatch(IllegalArgumentException.class).to("mock:dead").end();
            }
        });
        context.start();
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(12, EventNotifierFailureHandledEventsTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierFailureHandledEventsTest.events.get(0));
        TestSupport.assertIsInstanceOf(RouteAddedEvent.class, EventNotifierFailureHandledEventsTest.events.get(1));
        TestSupport.assertIsInstanceOf(RouteStartedEvent.class, EventNotifierFailureHandledEventsTest.events.get(2));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierFailureHandledEventsTest.events.get(3));
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierFailureHandledEventsTest.events.get(4));
        TestSupport.assertIsInstanceOf(ExchangeCreatedEvent.class, EventNotifierFailureHandledEventsTest.events.get(5));
        ExchangeFailureHandlingEvent e0 = TestSupport.assertIsInstanceOf(ExchangeFailureHandlingEvent.class, EventNotifierFailureHandledEventsTest.events.get(6));
        Assert.assertEquals("should NOT be DLC", false, e0.isDeadLetterChannel());
        TestSupport.assertIsInstanceOf(ExchangeSendingEvent.class, EventNotifierFailureHandledEventsTest.events.get(7));
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierFailureHandledEventsTest.events.get(8));
        ExchangeFailureHandledEvent e = TestSupport.assertIsInstanceOf(ExchangeFailureHandledEvent.class, EventNotifierFailureHandledEventsTest.events.get(9));
        Assert.assertEquals("should NOT be DLC", false, e.isDeadLetterChannel());
        Assert.assertFalse("should not be marked as failure handled as it was continued instead", e.isHandled());
        Assert.assertTrue("should be continued", e.isContinued());
        // onException will handle the exception
        TestSupport.assertIsInstanceOf(ExchangeCompletedEvent.class, EventNotifierFailureHandledEventsTest.events.get(10));
        // and the last event should be the direct:start
        TestSupport.assertIsInstanceOf(ExchangeSentEvent.class, EventNotifierFailureHandledEventsTest.events.get(11));
        ExchangeSentEvent sent = ((ExchangeSentEvent) (EventNotifierFailureHandledEventsTest.events.get(11)));
        Assert.assertEquals("direct://start", sent.getEndpoint().getEndpointUri());
    }
}

