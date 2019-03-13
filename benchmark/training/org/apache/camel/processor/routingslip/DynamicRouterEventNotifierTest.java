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
package org.apache.camel.processor.routingslip;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.ExchangeSendingEvent;
import org.apache.camel.spi.CamelEvent.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.junit.Assert;
import org.junit.Test;


public class DynamicRouterEventNotifierTest extends ContextTestSupport {
    private DynamicRouterEventNotifierTest.MyEventNotifier notifier = new DynamicRouterEventNotifierTest.MyEventNotifier();

    @Test
    public void testDynamicRouterEventNotifier() throws Exception {
        getMockEndpoint("mock:x").expectedMessageCount(1);
        getMockEndpoint("mock:y").expectedMessageCount(1);
        getMockEndpoint("mock:z").expectedMessageCount(1);
        getMockEndpoint("mock:end").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("Should have 5 sending events", 5, notifier.getSending());
        Assert.assertEquals("Should have 5 sent events", 5, notifier.getSent());
    }

    private final class MyEventNotifier extends EventNotifierSupport {
        private int sending;

        private int sent;

        @Override
        public void notify(CamelEvent event) throws Exception {
            if (event instanceof ExchangeSendingEvent) {
                log.info("Sending: {}", event);
                (sending)++;
            } else {
                (sent)++;
            }
        }

        @Override
        public boolean isEnabled(CamelEvent event) {
            return (event instanceof ExchangeSendingEvent) || (event instanceof ExchangeSentEvent);
        }

        public int getSending() {
            return sending;
        }

        public int getSent() {
            return sent;
        }
    }
}

