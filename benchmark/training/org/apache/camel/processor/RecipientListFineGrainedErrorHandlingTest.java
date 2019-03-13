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


import Exchange.FAILURE_ENDPOINT;
import Exchange.REDELIVERED;
import Exchange.REDELIVERY_COUNTER;
import Exchange.TO_ENDPOINT;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Assert;
import org.junit.Test;


public class RecipientListFineGrainedErrorHandlingTest extends ContextTestSupport {
    private static int counter;

    private static int tries;

    @Test
    public void testRecipientListOk() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class).redeliveryDelay(0).maximumRedeliveries(2);
                from("direct:start").to("mock:a").recipientList(TestSupport.header("foo")).stopOnException();
            }
        });
        context.start();
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        getMockEndpoint("mock:baz").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", "mock:foo,mock:bar,mock:baz");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRecipientListErrorAggregate() throws Exception {
        RecipientListFineGrainedErrorHandlingTest.counter = 0;
        RecipientListFineGrainedErrorHandlingTest.tries = 0;
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class).redeliveryDelay(0).maximumRedeliveries(3).end().to("mock:a").recipientList(TestSupport.header("foo")).aggregationStrategy(new RecipientListFineGrainedErrorHandlingTest.MyAggregationStrategy()).parallelProcessing();
            }
        });
        context.start();
        getMockEndpoint("mock:a").expectedMessageCount(1);
        // can be 0 or 1 depending whether the task was executed or not (we run parallel)
        getMockEndpoint("mock:foo").expectedMinimumMessageCount(0);
        getMockEndpoint("mock:bar").expectedMinimumMessageCount(0);
        getMockEndpoint("mock:baz").expectedMinimumMessageCount(0);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", "mock:foo,mock:bar,bean:fail,mock:baz");
        assertMockEndpointsSatisfied();
        // bean is invoked 4 times
        Assert.assertEquals(4, RecipientListFineGrainedErrorHandlingTest.counter);
        // of which 3 of them is retries
        Assert.assertEquals(3, RecipientListFineGrainedErrorHandlingTest.tries);
    }

    @Test
    public void testRecipientListError() throws Exception {
        RecipientListFineGrainedErrorHandlingTest.counter = 0;
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class).redeliveryDelay(0).maximumRedeliveries(2);
                from("direct:start").to("mock:a").recipientList(TestSupport.header("foo")).stopOnException();
            }
        });
        context.start();
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        getMockEndpoint("mock:baz").expectedMessageCount(0);
        try {
            template.sendBodyAndHeader("direct:start", "Hello World", "foo", "mock:foo,mock:bar,bean:fail,mock:baz");
            Assert.fail("Should throw exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals(3, RecipientListFineGrainedErrorHandlingTest.counter);
    }

    @Test
    public void testRecipientListAsBeanError() throws Exception {
        RecipientListFineGrainedErrorHandlingTest.counter = 0;
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);
                onException(Exception.class).redeliveryDelay(0).maximumRedeliveries(2);
                from("direct:start").to("mock:a").bean(RecipientListFineGrainedErrorHandlingTest.MyRecipientBean.class);
            }
        });
        context.start();
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        getMockEndpoint("mock:baz").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should throw exception");
        } catch (CamelExecutionException e) {
            // expected
            TestSupport.assertIsInstanceOf(CamelExchangeException.class, e.getCause());
            TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause().getCause());
            Assert.assertEquals("Damn", e.getCause().getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals(3, RecipientListFineGrainedErrorHandlingTest.counter);
    }

    public static class MyAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            // check whether we have attempted redelivery
            Boolean redelivered = newExchange.getIn().getHeader(REDELIVERED, Boolean.class);
            if ((redelivered != null) && redelivered) {
                // extract the number of times we tried
                RecipientListFineGrainedErrorHandlingTest.tries = newExchange.getIn().getHeader(REDELIVERY_COUNTER, Integer.class);
                // this is the endpoint that failed
                Assert.assertEquals("bean://fail", newExchange.getProperty(FAILURE_ENDPOINT, String.class));
            }
            // just let it pass through
            return newExchange;
        }
    }

    public static class MyRecipientBean {
        @RecipientList(stopOnException = true)
        public String sendSomewhere(Exchange exchange) {
            return "mock:foo,mock:bar,bean:fail,mock:baz";
        }
    }

    public static class MyFailBean {
        public String doSomething(Exchange exchange) throws Exception {
            (RecipientListFineGrainedErrorHandlingTest.counter)++;
            Assert.assertEquals("bean://fail", exchange.getProperty(TO_ENDPOINT, String.class));
            throw new IllegalArgumentException("Damn");
        }
    }
}

