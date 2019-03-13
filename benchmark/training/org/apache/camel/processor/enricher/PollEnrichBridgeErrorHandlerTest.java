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
package org.apache.camel.processor.enricher;


import Exchange.EXCEPTION_CAUGHT;
import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PollingConsumerPollStrategy;
import org.junit.Assert;
import org.junit.Test;


public class PollEnrichBridgeErrorHandlerTest extends ContextTestSupport {
    private PollEnrichBridgeErrorHandlerTest.MyPollingStrategy myPoll = new PollEnrichBridgeErrorHandlerTest.MyPollingStrategy();

    @Test
    public void testPollEnrichBridgeErrorHandler() throws Exception {
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("seda:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals((1 + 3), myPoll.getCounter());
        Exception caught = getMockEndpoint("mock:dead").getExchanges().get(0).getProperty(EXCEPTION_CAUGHT, Exception.class);
        Assert.assertNotNull(caught);
        Assert.assertTrue(caught.getMessage().startsWith("Error during poll"));
        Assert.assertEquals("Something went wrong", caught.getCause().getCause().getMessage());
    }

    private class MyPollingStrategy implements PollingConsumerPollStrategy {
        private int counter;

        @Override
        public boolean begin(Consumer consumer, Endpoint endpoint) {
            (counter)++;
            throw new IllegalArgumentException("Something went wrong");
        }

        @Override
        public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
            // noop
        }

        @Override
        public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception cause) throws Exception {
            return false;
        }

        public int getCounter() {
            return counter;
        }
    }
}

