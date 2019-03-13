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
package org.apache.camel.component.ironmq;


import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import IronMQConstants.MESSAGE_ID;
import IronMQConstants.MESSAGE_RESERVATION_ID;
import IronMQConstants.MESSAGE_RESERVED_COUNT;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class IronMQComponentTest extends CamelTestSupport {
    private IronMQEndpoint endpoint;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @Test
    public void testIronMQ() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        template.sendBody("direct:start", "some payload");
        assertMockEndpointsSatisfied();
        Message in = mock.getExchanges().get(0).getIn();
        Assert.assertNotNull(in.getHeader(MESSAGE_ID));
        Assert.assertNotNull(in.getHeader(MESSAGE_RESERVATION_ID));
        Assert.assertNotNull(in.getHeader(MESSAGE_RESERVED_COUNT));
    }

    @Test
    public void sendInOnly() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("This is my message text.");
            }
        });
        assertMockEndpointsSatisfied();
        Exchange resultExchange = result.getExchanges().get(0);
        assertEquals("This is my message text.", resultExchange.getIn().getBody());
        assertNotNull(resultExchange.getIn().getHeader(MESSAGE_ID));
        assertEquals("This is my message text.", exchange.getIn().getBody());
        assertNotNull(exchange.getIn().getHeader(MESSAGE_ID));
    }

    @Test
    public void sendInOut() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = template.send("direct:start", InOut, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("This is my message text.");
            }
        });
        assertMockEndpointsSatisfied();
        Exchange resultExchange = result.getExchanges().get(0);
        assertEquals("This is my message text.", resultExchange.getIn().getBody());
        assertNotNull(resultExchange.getIn().getHeader(MESSAGE_ID));
        assertEquals("This is my message text.", exchange.getOut().getBody());
        assertNotNull(exchange.getOut().getHeader(MESSAGE_ID));
    }
}

