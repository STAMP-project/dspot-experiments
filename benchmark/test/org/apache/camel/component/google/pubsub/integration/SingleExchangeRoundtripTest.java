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
package org.apache.camel.component.google.pubsub.integration;


import GooglePubsubConstants.ACK_ID;
import GooglePubsubConstants.ATTRIBUTES;
import GooglePubsubConstants.MESSAGE_ID;
import GooglePubsubConstants.PUBLISH_TIME;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.google.pubsub.PubsubTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class SingleExchangeRoundtripTest extends PubsubTestSupport {
    private static final String TOPIC_NAME = "singleSend";

    private static final String SUBSCRIPTION_NAME = "singleReceive";

    @EndpointInject(uri = "direct:from")
    private Endpoint directIn;

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (SingleExchangeRoundtripTest.TOPIC_NAME))
    private Endpoint pubsubTopic;

    @EndpointInject(uri = "mock:sendResult")
    private MockEndpoint sendResult;

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (SingleExchangeRoundtripTest.SUBSCRIPTION_NAME))
    private Endpoint pubsubSubscription;

    @EndpointInject(uri = "mock:receiveResult")
    private MockEndpoint receiveResult;

    @Produce(uri = "direct:from")
    private ProducerTemplate producer;

    @Test
    public void testSingleMessageSend() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        String attributeKey = "ATTRIBUTE-TEST-KEY";
        String attributeValue = "ATTRIBUTE-TEST-VALUE";
        Map<String, String> attributes = new HashMap<>();
        attributes.put(attributeKey, attributeValue);
        exchange.getIn().setBody(("Single  : " + (exchange.getExchangeId())));
        exchange.getIn().setHeader(ATTRIBUTES, attributes);
        receiveResult.expectedMessageCount(1);
        receiveResult.expectedBodiesReceivedInAnyOrder(exchange.getIn().getBody());
        producer.send(exchange);
        List<Exchange> sentExchanges = sendResult.getExchanges();
        assertEquals("Sent exchanges", 1, sentExchanges.size());
        Exchange sentExchange = sentExchanges.get(0);
        assertEquals("Sent ID", exchange.getIn().getHeader(MESSAGE_ID), sentExchange.getIn().getHeader(MESSAGE_ID));
        receiveResult.assertIsSatisfied(5000);
        List<Exchange> receivedExchanges = receiveResult.getExchanges();
        assertNotNull("Received exchanges", receivedExchanges);
        Exchange receivedExchange = receivedExchanges.get(0);
        assertNotNull("PUBSUB Message ID Property", receivedExchange.getIn().getHeader(MESSAGE_ID));
        assertNotNull("PUBSUB Ack ID Property", receivedExchange.getIn().getHeader(ACK_ID));
        assertNotNull("PUBSUB Published Time", receivedExchange.getIn().getHeader(PUBLISH_TIME));
        assertEquals("PUBSUB Header Attribute", attributeValue, ((Map) (receivedExchange.getIn().getHeader(ATTRIBUTES))).get(attributeKey));
        assertEquals(sentExchange.getIn().getHeader(MESSAGE_ID), receivedExchange.getIn().getHeader(MESSAGE_ID));
    }
}

