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
package org.apache.camel.component.hipchat;


import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import HipchatApiConstants.API_MESSAGE;
import HipchatApiConstants.API_MESSAGE_COLOR;
import HipchatApiConstants.API_MESSAGE_FORMAT;
import HipchatApiConstants.API_MESSAGE_NOTIFY;
import HipchatConstants.MESSAGE_BACKGROUND_COLOR;
import HipchatConstants.MESSAGE_FORMAT;
import HipchatConstants.TO_ROOM;
import HipchatConstants.TO_ROOM_RESPONSE_STATUS;
import HipchatConstants.TO_USER;
import HipchatConstants.TO_USER_RESPONSE_STATUS;
import HipchatConstants.TRIGGER_NOTIFY;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.StatusLine;
import org.junit.Test;


public class HipchatComponentProducerTest extends CamelTestSupport {
    @EndpointInject(uri = "direct:start")
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    private HipchatComponentProducerTest.PostCallback callback = new HipchatComponentProducerTest.PostCallback();

    @Test
    public void sendInOnly() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(TO_ROOM, "CamelUnitTest");
                exchange.getIn().setHeader(TO_USER, "CamelUnitTestUser");
                exchange.getIn().setBody("This is my unit test message.");
            }
        });
        assertMockEndpointsSatisfied();
        assertCommonResultExchange(result.getExchanges().get(0));
        assertNullExchangeHeader(result.getExchanges().get(0));
        assertResponseMessage(exchange.getIn());
    }

    @Test
    public void sendInOut() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = template.send("direct:start", InOut, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(TO_ROOM, "CamelUnitTest");
                exchange.getIn().setHeader(TO_USER, "CamelUnitTestUser");
                exchange.getIn().setHeader(MESSAGE_BACKGROUND_COLOR, "CamelUnitTestBkColor");
                exchange.getIn().setHeader(MESSAGE_FORMAT, "CamelUnitTestFormat");
                exchange.getIn().setHeader(TRIGGER_NOTIFY, "CamelUnitTestNotify");
                exchange.getIn().setBody("This is my unit test message.");
            }
        });
        assertMockEndpointsSatisfied();
        assertCommonResultExchange(result.getExchanges().get(0));
        assertRemainingResultExchange(result.getExchanges().get(0));
        assertResponseMessage(exchange.getIn());
    }

    @Test
    public void sendInOutRoomOnly() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = template.send("direct:start", InOut, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(TO_ROOM, "CamelUnitTest");
                exchange.getIn().setHeader(MESSAGE_BACKGROUND_COLOR, "CamelUnitTestBkColor");
                exchange.getIn().setHeader(MESSAGE_FORMAT, "CamelUnitTestFormat");
                exchange.getIn().setHeader(TRIGGER_NOTIFY, "CamelUnitTestNotify");
                exchange.getIn().setBody("This is my unit test message.");
            }
        });
        assertMockEndpointsSatisfied();
        Exchange resultExchange = result.getExchanges().get(0);
        assertIsInstanceOf(String.class, resultExchange.getIn().getBody());
        assertEquals("This is my unit test message.", resultExchange.getIn().getBody(String.class));
        assertEquals("CamelUnitTest", resultExchange.getIn().getHeader(TO_ROOM));
        assertNull(resultExchange.getIn().getHeader(TO_USER));
        assertNull(resultExchange.getIn().getHeader(TO_USER_RESPONSE_STATUS));
        assertNotNull(resultExchange.getIn().getHeader(TO_ROOM_RESPONSE_STATUS));
        assertRemainingResultExchange(result.getExchanges().get(0));
        assertEquals(204, exchange.getIn().getHeader(TO_ROOM_RESPONSE_STATUS, StatusLine.class).getStatusCode());
        assertNotNull(callback);
        assertNotNull(callback.called);
        assertEquals("This is my unit test message.", callback.called.get(API_MESSAGE));
        assertEquals("CamelUnitTestBkColor", callback.called.get(API_MESSAGE_COLOR));
        assertEquals("CamelUnitTestFormat", callback.called.get(API_MESSAGE_FORMAT));
        assertEquals("CamelUnitTestNotify", callback.called.get(API_MESSAGE_NOTIFY));
    }

    @Test
    public void sendInOutUserOnly() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = template.send("direct:start", InOut, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(TO_USER, "CamelUnitTest");
                exchange.getIn().setHeader(MESSAGE_BACKGROUND_COLOR, "CamelUnitTestBkColor");
                exchange.getIn().setHeader(MESSAGE_FORMAT, "CamelUnitTestFormat");
                exchange.getIn().setHeader(TRIGGER_NOTIFY, "CamelUnitTestNotify");
                exchange.getIn().setBody("This is my unit test message.");
            }
        });
        assertMockEndpointsSatisfied();
        Exchange resultExchange = result.getExchanges().get(0);
        assertIsInstanceOf(String.class, resultExchange.getIn().getBody());
        assertEquals("This is my unit test message.", resultExchange.getIn().getBody(String.class));
        assertEquals("CamelUnitTest", resultExchange.getIn().getHeader(TO_USER));
        assertNull(resultExchange.getIn().getHeader(TO_ROOM));
        assertNull(resultExchange.getIn().getHeader(TO_ROOM_RESPONSE_STATUS));
        assertNotNull(resultExchange.getIn().getHeader(TO_USER_RESPONSE_STATUS));
        assertRemainingResultExchange(result.getExchanges().get(0));
        assertEquals(204, exchange.getIn().getHeader(TO_USER_RESPONSE_STATUS, StatusLine.class).getStatusCode());
        assertNotNull(callback);
        assertNotNull(callback.called);
        assertEquals("This is my unit test message.", callback.called.get(API_MESSAGE));
        assertNull(callback.called.get(API_MESSAGE_COLOR));
        assertEquals("CamelUnitTestFormat", callback.called.get(API_MESSAGE_FORMAT));
        assertEquals("CamelUnitTestNotify", callback.called.get(API_MESSAGE_NOTIFY));
    }

    public static class PostCallback {
        public Map<String, String> called;

        public void call(Map<String, String> postParam) {
            this.called = postParam;
        }
    }
}

