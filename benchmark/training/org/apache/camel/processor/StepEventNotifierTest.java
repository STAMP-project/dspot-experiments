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


import CamelEvent.StepCompletedEvent;
import CamelEvent.StepStartedEvent;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.junit.Assert;
import org.junit.Test;


public class StepEventNotifierTest extends ContextTestSupport {
    private StepEventNotifierTest.MyEventNotifier notifier = new StepEventNotifierTest.MyEventNotifier();

    @Test
    public void testStepEventNotifier() throws Exception {
        context.addService(notifier);
        context.getManagementStrategy().addEventNotifier(notifier);
        Assert.assertEquals(0, notifier.getEvents().size());
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(4, notifier.getEvents().size());
        TestSupport.assertIsInstanceOf(StepStartedEvent.class, notifier.getEvents().get(0));
        TestSupport.assertIsInstanceOf(StepCompletedEvent.class, notifier.getEvents().get(1));
        TestSupport.assertIsInstanceOf(StepStartedEvent.class, notifier.getEvents().get(2));
        TestSupport.assertIsInstanceOf(StepCompletedEvent.class, notifier.getEvents().get(3));
        Assert.assertEquals("foo", getStepId());
        Assert.assertEquals("foo", getStepId());
        Assert.assertEquals("bar", getStepId());
        Assert.assertEquals("bar", getStepId());
    }

    private class MyEventNotifier extends EventNotifierSupport {
        private final List<CamelEvent> events = new ArrayList<>();

        public MyEventNotifier() {
            setIgnoreCamelContextEvents(true);
            setIgnoreExchangeEvents(true);
            setIgnoreRouteEvents(true);
            setIgnoreServiceEvents(true);
            setIgnoreStepEvents(false);
        }

        @Override
        public void notify(CamelEvent event) throws Exception {
            events.add(event);
        }

        public List<CamelEvent> getEvents() {
            return events;
        }
    }
}

