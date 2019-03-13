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
package org.apache.camel.component.smpp.integration;


import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import SmppConstants.COMMAND;
import SmppConstants.DELIVERED;
import SmppConstants.DONE_DATE;
import SmppConstants.ERROR;
import SmppConstants.FINAL_DATE;
import SmppConstants.ID;
import SmppConstants.MESSAGE_STATE;
import SmppConstants.MESSAGE_TYPE;
import SmppConstants.SENT_MESSAGE_COUNT;
import SmppConstants.SUBMITTED;
import SmppConstants.SUBMIT_DATE;
import SmppMessageType.DeliveryReceipt;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Spring based integration test for the smpp component. To run this test, ensure that
 * the SMSC is running on:
 * host:     localhost
 * port:     2775
 * user:     smppclient
 * password: password
 * <br/>
 * A SMSC for test is available here: http://www.seleniumsoftware.com/downloads.html
 */
@Ignore("Must be manually tested")
public class SmppComponentSpringIntegrationTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @EndpointInject(uri = "direct:start")
    private Endpoint start;

    @Test
    public void sendSubmitSMInOut() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = start.createExchange(InOut);
        exchange.getIn().setBody("Hello SMPP World!");
        template.send(start, exchange);
        assertMockEndpointsSatisfied();
        Exchange resultExchange = result.getExchanges().get(0);
        assertEquals(DeliveryReceipt.toString(), resultExchange.getIn().getHeader(MESSAGE_TYPE));
        assertEquals("Hello SMPP World!", resultExchange.getIn().getBody());
        assertNotNull(resultExchange.getIn().getHeader(ID));
        assertEquals(1, resultExchange.getIn().getHeader(SUBMITTED));
        assertEquals(1, resultExchange.getIn().getHeader(DELIVERED));
        assertNotNull(resultExchange.getIn().getHeader(DONE_DATE));
        assertNotNull(resultExchange.getIn().getHeader(SUBMIT_DATE));
        assertNull(resultExchange.getIn().getHeader(ERROR));
        assertNotNull(exchange.getOut().getHeader(ID));
        assertEquals(1, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
    }

    @Test
    public void sendSubmitSMInOnly() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = start.createExchange(InOnly);
        exchange.getIn().setBody("Hello SMPP World!");
        template.send(start, exchange);
        assertMockEndpointsSatisfied();
        Exchange resultExchange = result.getExchanges().get(0);
        assertEquals(DeliveryReceipt.toString(), resultExchange.getIn().getHeader(MESSAGE_TYPE));
        assertEquals("Hello SMPP World!", resultExchange.getIn().getBody());
        assertNotNull(resultExchange.getIn().getHeader(ID));
        assertEquals(1, resultExchange.getIn().getHeader(SUBMITTED));
        assertEquals(1, resultExchange.getIn().getHeader(DELIVERED));
        assertNotNull(resultExchange.getIn().getHeader(DONE_DATE));
        assertNotNull(resultExchange.getIn().getHeader(SUBMIT_DATE));
        assertNull(resultExchange.getIn().getHeader(ERROR));
        assertNotNull(exchange.getIn().getHeader(ID));
        assertEquals(1, exchange.getIn().getHeader(SENT_MESSAGE_COUNT));
    }

    @Test
    public void sendLongSubmitSM() throws Exception {
        result.expectedMessageCount(2);
        Exchange exchange = start.createExchange(InOnly);
        exchange.getIn().setBody(("Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! " + ("Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! " + "Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! ")));// 270 chars

        template.send(start, exchange);
        assertMockEndpointsSatisfied();
        assertEquals(DeliveryReceipt.toString(), result.getExchanges().get(0).getIn().getHeader(MESSAGE_TYPE));
        assertEquals(DeliveryReceipt.toString(), result.getExchanges().get(1).getIn().getHeader(MESSAGE_TYPE));
        assertNotNull(exchange.getIn().getHeader(ID));
        assertEquals(2, exchange.getIn().getHeader(SENT_MESSAGE_COUNT));
    }

    @Test
    public void sendCancelSM() throws Exception {
        Exchange exchange = start.createExchange(InOut);
        exchange.getIn().setHeader(COMMAND, "CancelSm");
        exchange.getIn().setHeader(ID, "1");
        template.send(start, exchange);
        assertEquals("1", exchange.getOut().getHeader(ID));
    }

    @Test
    public void sendQuerySM() throws Exception {
        Exchange exchange = start.createExchange(InOut);
        exchange.getIn().setHeader(COMMAND, "QuerySm");
        exchange.getIn().setHeader(ID, "1");
        template.send(start, exchange);
        assertEquals("1", exchange.getOut().getHeader(ID));
        assertEquals(((byte) (0)), exchange.getOut().getHeader(ERROR));
        assertNotNull(exchange.getOut().getHeader(FINAL_DATE));
        assertEquals("DELIVERED", exchange.getOut().getHeader(MESSAGE_STATE));
    }

    @Test
    public void sendReplaceSM() throws Exception {
        Exchange exchange = start.createExchange(InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setBody("Hello Camel World!");
        exchange.getIn().setHeader(ID, "1");
        template.send(start, exchange);
        assertEquals("1", exchange.getOut().getHeader(ID));
    }

    @Test
    public void sendDataSM() throws Exception {
        Exchange exchange = start.createExchange(InOut);
        exchange.getIn().setHeader(COMMAND, "DataSm");
        template.send(start, exchange);
        assertNotNull(exchange.getOut().getHeader(ID));
    }

    @Test
    public void sendSubmitMultiSM() throws Exception {
        Exchange exchange = start.createExchange(InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setBody(("Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! " + ("Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! " + "Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! Hello SMPP World! ")));// 270 chars

        template.send(start, exchange);
        assertNotNull(exchange.getOut().getHeader(ID));
        assertEquals(2, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
    }
}

