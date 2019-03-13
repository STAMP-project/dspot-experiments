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
package org.apache.camel.component.rabbitmq;


import AMQP.BasicProperties;
import RabbitMQConstants.EXCHANGE_NAME;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RuntimeCamelException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RabbitMQProducerIntTest extends AbstractRabbitMQIntTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducerIntTest.class);

    private static final String EXCHANGE = "ex1";

    private static final String ROUTE = "route1";

    private static final String CUSTOM_HEADER = "CustomHeader";

    private static final String BASIC_URI_FORMAT = "rabbitmq:localhost:5672/%s?routingKey=%s&username=cameltest&password=cameltest&skipQueueDeclare=true";

    private static final String BASIC_URI = String.format(RabbitMQProducerIntTest.BASIC_URI_FORMAT, RabbitMQProducerIntTest.EXCHANGE, RabbitMQProducerIntTest.ROUTE);

    private static final String ALLOW_NULL_HEADERS = (RabbitMQProducerIntTest.BASIC_URI) + "&allowNullHeaders=true";

    private static final String PUBLISHER_ACKNOWLEDGES_URI = (RabbitMQProducerIntTest.BASIC_URI) + "&mandatory=true&publisherAcknowledgements=true";

    private static final String PUBLISHER_ACKNOWLEDGES_BAD_ROUTE_URI = (String.format(RabbitMQProducerIntTest.BASIC_URI_FORMAT, RabbitMQProducerIntTest.EXCHANGE, "route2")) + "&publisherAcknowledgements=true";

    private static final String GUARANTEED_DELIVERY_URI = (RabbitMQProducerIntTest.BASIC_URI) + "&mandatory=true&guaranteedDeliveries=true";

    private static final String GUARANTEED_DELIVERY_BAD_ROUTE_NOT_MANDATORY_URI = (String.format(RabbitMQProducerIntTest.BASIC_URI_FORMAT, RabbitMQProducerIntTest.EXCHANGE, "route2")) + "&guaranteedDeliveries=true";

    private static final String GUARANTEED_DELIVERY_BAD_ROUTE_URI = (String.format(RabbitMQProducerIntTest.BASIC_URI_FORMAT, RabbitMQProducerIntTest.EXCHANGE, "route2")) + "&mandatory=true&guaranteedDeliveries=true";

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Produce(uri = "direct:start-allow-null-headers")
    protected ProducerTemplate templateAllowNullHeaders;

    @Produce(uri = "direct:start-with-confirms")
    protected ProducerTemplate templateWithConfirms;

    @Produce(uri = "direct:start-with-confirms-bad-route")
    protected ProducerTemplate templateWithConfirmsAndBadRoute;

    @Produce(uri = "direct:start-with-guaranteed-delivery")
    protected ProducerTemplate templateWithGuranteedDelivery;

    @Produce(uri = "direct:start-with-guaranteed-delivery-bad-route")
    protected ProducerTemplate templateWithGuranteedDeliveryAndBadRoute;

    @Produce(uri = "direct:start-with-guaranteed-delivery-bad-route-but-not-mandatory")
    protected ProducerTemplate templateWithGuranteedDeliveryBadRouteButNotMandatory;

    private Connection connection;

    private Channel channel;

    @Test
    public void producedMessageIsReceived() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received));
        template.sendBodyAndHeader("new message", EXCHANGE_NAME, "ex1");
        assertThatBodiesReceivedIn(received, "new message");
    }

    @Test
    public void producedMessageWithNotNullHeaders() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        final Map<String, Object> receivedHeaders = new HashMap<String, Object>();
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(EXCHANGE_NAME, RabbitMQProducerIntTest.EXCHANGE);
        headers.put(RabbitMQProducerIntTest.CUSTOM_HEADER, RabbitMQProducerIntTest.CUSTOM_HEADER.toLowerCase());
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received, receivedHeaders));
        template.sendBodyAndHeaders("new message", headers);
        assertThatBodiesAndHeadersReceivedIn(receivedHeaders, headers, received, "new message");
    }

    @Test
    public void producedMessageAllowNullHeaders() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        final Map<String, Object> receivedHeaders = new HashMap<String, Object>();
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(EXCHANGE_NAME, null);
        headers.put(RabbitMQProducerIntTest.CUSTOM_HEADER, null);
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received, receivedHeaders));
        templateAllowNullHeaders.sendBodyAndHeaders("new message", headers);
        assertThatBodiesAndHeadersReceivedIn(receivedHeaders, headers, received, "new message");
    }

    @Test
    public void producedMessageIsReceivedWhenPublisherAcknowledgementsAreEnabled() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received));
        templateWithConfirms.sendBodyAndHeader("publisher ack message", EXCHANGE_NAME, "ex1");
        assertThatBodiesReceivedIn(received, "publisher ack message");
    }

    @Test
    public void producedMessageIsReceivedWhenPublisherAcknowledgementsAreEnabledAndBadRoutingKeyIsUsed() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received));
        templateWithConfirmsAndBadRoute.sendBody("publisher ack message");
        assertThatBodiesReceivedIn(received);
    }

    @Test
    public void shouldSuccessfullyProduceMessageWhenGuaranteedDeliveryIsActivatedAndMessageIsMarkedAsMandatory() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received));
        templateWithGuranteedDelivery.sendBodyAndHeader("publisher ack message", EXCHANGE_NAME, "ex1");
        assertThatBodiesReceivedIn(received, "publisher ack message");
    }

    @Test(expected = RuntimeCamelException.class)
    public void shouldFailIfMessageIsMarkedAsMandatoryAndGuaranteedDeliveryIsActiveButNoQueueIsBound() {
        templateWithGuranteedDeliveryAndBadRoute.sendBody("publish with ack and return message");
    }

    @Test
    public void shouldSuccessfullyProduceMessageWhenGuaranteedDeliveryIsActivatedOnABadRouteButMessageIsNotMandatory() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        channel.basicConsume("sammyq", true, new RabbitMQProducerIntTest.ArrayPopulatingConsumer(received));
        templateWithGuranteedDeliveryBadRouteButNotMandatory.sendBodyAndHeader("publisher ack message", EXCHANGE_NAME, "ex1");
        assertThatBodiesReceivedIn(received);
    }

    private class ArrayPopulatingConsumer extends DefaultConsumer {
        private final List<String> received;

        private final Map<String, Object> receivedHeaders;

        ArrayPopulatingConsumer(final List<String> received) {
            super(RabbitMQProducerIntTest.this.channel);
            this.received = received;
            receivedHeaders = new HashMap<String, Object>();
        }

        ArrayPopulatingConsumer(final List<String> received, Map<String, Object> receivedHeaders) {
            super(RabbitMQProducerIntTest.this.channel);
            this.received = received;
            this.receivedHeaders = receivedHeaders;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            RabbitMQProducerIntTest.LOGGER.info("AMQP.BasicProperties: {}", properties);
            receivedHeaders.putAll(properties.getHeaders());
            received.add(new String(body));
        }
    }
}

