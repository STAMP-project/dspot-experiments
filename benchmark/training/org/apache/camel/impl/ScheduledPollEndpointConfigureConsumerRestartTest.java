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
package org.apache.camel.impl;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.ScheduledPollConsumer;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ScheduledPollEndpointConfigureConsumerRestartTest extends ContextTestSupport {
    private ScheduledPollEndpointConfigureConsumerRestartTest.MyEndpoint my;

    private Map<String, Object> props = new HashMap<>();

    @Test
    public void testRestart() throws Exception {
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        Assert.assertEquals("Hello", getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody());
        Assert.assertEquals(123, getMockEndpoint("mock:result").getExchanges().get(0).getIn().getHeader("foo"));
        // restart route
        resetMocks();
        context.getRouteController().stopRoute("foo");
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);
        // start route
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("Hello", getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody());
        Assert.assertEquals(123, getMockEndpoint("mock:result").getExchanges().get(0).getIn().getHeader("foo"));
    }

    private static class MyEndpoint extends ScheduledPollEndpoint {
        @Override
        public Producer createProducer() throws Exception {
            return null;
        }

        @Override
        public Consumer createConsumer(Processor processor) throws Exception {
            ScheduledPollEndpointConfigureConsumerRestartTest.MyConsumer answer = new ScheduledPollEndpointConfigureConsumerRestartTest.MyConsumer(this, processor);
            configureConsumer(answer);
            return answer;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        protected String createEndpointUri() {
            return "myendpoint:foo";
        }
    }

    public static final class MyConsumer extends ScheduledPollConsumer {
        private int foo;

        private String bar;

        public MyConsumer(Endpoint endpoint, Processor processor) {
            super(endpoint, processor);
        }

        public int getFoo() {
            return foo;
        }

        public void setFoo(int foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        @Override
        protected int poll() throws Exception {
            Exchange exchange = new org.apache.camel.support.DefaultExchange(getEndpoint());
            exchange.getIn().setBody(bar);
            exchange.getIn().setHeader("foo", foo);
            getProcessor().process(exchange);
            return 1;
        }
    }
}

