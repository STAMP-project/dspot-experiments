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


import java.util.Arrays;
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


public class BodyTypesTest extends PubsubTestSupport {
    private static final String TOPIC_NAME = "typesSend";

    private static final String SUBSCRIPTION_NAME = "TypesReceive";

    @EndpointInject(uri = "direct:from")
    private Endpoint directIn;

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (BodyTypesTest.TOPIC_NAME))
    private Endpoint pubsubTopic;

    @EndpointInject(uri = "mock:sendResult")
    private MockEndpoint sendResult;

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (BodyTypesTest.SUBSCRIPTION_NAME))
    private Endpoint pubsubSubscription;

    @EndpointInject(uri = "mock:receiveResult")
    private MockEndpoint receiveResult;

    @Produce(uri = "direct:from")
    private ProducerTemplate producer;

    @Test
    public void byteArray() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        byte[] body = new byte[]{ 1, 2, 3 };
        exchange.getIn().setBody(body);
        receiveResult.expectedMessageCount(1);
        producer.send(exchange);
        List<Exchange> sentExchanges = sendResult.getExchanges();
        assertEquals("Sent exchanges", 1, sentExchanges.size());
        Exchange sentExchange = sentExchanges.get(0);
        assertTrue("Sent body type is byte[]", ((sentExchange.getIn().getBody()) instanceof byte[]));
        assertTrue("Sent body type is the one sent", ((sentExchange.getIn().getBody()) == body));
        receiveResult.assertIsSatisfied(5000);
        List<Exchange> receivedExchanges = receiveResult.getExchanges();
        assertNotNull("Received exchanges", receivedExchanges);
        Exchange receivedExchange = receivedExchanges.get(0);
        assertTrue("Received body is of byte[] type", ((receivedExchange.getIn().getBody()) instanceof byte[]));
        assertTrue("Received body equals sent", Arrays.equals(body, ((byte[]) (receivedExchange.getIn().getBody()))));
    }

    @Test
    public void objectSerialised() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Map<String, String> body = new HashMap<>();
        body.put("KEY", "VALUE1212");
        exchange.getIn().setBody(body);
        receiveResult.expectedMessageCount(1);
        producer.send(exchange);
        List<Exchange> sentExchanges = sendResult.getExchanges();
        assertEquals("Sent exchanges", 1, sentExchanges.size());
        Exchange sentExchange = sentExchanges.get(0);
        assertTrue("Sent body type is byte[]", ((sentExchange.getIn().getBody()) instanceof Map));
        receiveResult.assertIsSatisfied(5000);
        List<Exchange> receivedExchanges = receiveResult.getExchanges();
        assertNotNull("Received exchanges", receivedExchanges);
        Exchange receivedExchange = receivedExchanges.get(0);
        assertTrue("Received body is of byte[] type", ((receivedExchange.getIn().getBody()) instanceof byte[]));
        Object bodyReceived = BodyTypesTest.deserialize(((byte[]) (receivedExchange.getIn().getBody())));
        assertTrue("Received body is a Map ", ((Map) (bodyReceived)).get("KEY").equals("VALUE1212"));
    }
}

