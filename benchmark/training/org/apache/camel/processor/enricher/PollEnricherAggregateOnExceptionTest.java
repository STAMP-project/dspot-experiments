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


import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class PollEnricherAggregateOnExceptionTest extends ContextTestSupport {
    @Test
    public void testEnrichTrueOk() throws Exception {
        template.sendBody("seda:foo", "Hello World");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEnrichTrueKaboom() throws Exception {
        template.send("seda:foo", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new IllegalArgumentException("I cannot do this"));
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("I cannot do this");
        template.sendBody("direct:start", "Kaboom");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEnrichFalseOk() throws Exception {
        template.sendBody("seda:foo", "Hello World");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBody("direct:start2", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEnrichFalseKaboom() throws Exception {
        template.send("seda:foo", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new IllegalArgumentException("I cannot do this"));
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        try {
            template.sendBody("direct:start2", "Kaboom");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("I cannot do this", e.getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
    }

    private class MyAggregationStrategy implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if ((newExchange.getException()) != null) {
                oldExchange.getIn().setBody(newExchange.getException().getMessage());
                return oldExchange;
            }
            // replace body
            oldExchange.getIn().setBody(newExchange.getIn().getBody());
            return oldExchange;
        }
    }
}

