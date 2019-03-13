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


import AMQP.BasicProperties.Builder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.junit.Test;


/**
 * Integration test to check if requested direct reply messages are received
 */
public class RabbitMQConsumerIntTestReplyTo extends AbstractRabbitMQIntTest {
    protected static final String QUEUE = "amq.rabbitmq.reply-to";

    private static final String EXCHANGE = "ex_reply";

    private static final String ROUTING_KEY = "testreply";

    private static final String REQUEST = "Knock! Knock!";

    private static final String REPLY = "Hello world";

    protected Channel channel;

    @EndpointInject(uri = (("rabbitmq:localhost:5672/" + (RabbitMQConsumerIntTestReplyTo.EXCHANGE)) + "?username=cameltest&password=cameltest&routingKey=") + (RabbitMQConsumerIntTestReplyTo.ROUTING_KEY))
    private Endpoint from;

    private Connection connection;

    @Test
    public void replyMessageIsReceived() throws IOException, InterruptedException, TimeoutException {
        final List<String> received = new ArrayList<>();
        AMQP.BasicProperties.Builder prop = new AMQP.BasicProperties.Builder();
        prop.replyTo(RabbitMQConsumerIntTestReplyTo.QUEUE);
        channel.basicConsume(RabbitMQConsumerIntTestReplyTo.QUEUE, true, new RabbitMQConsumerIntTestReplyTo.ArrayPopulatingConsumer(received));
        channel.basicPublish(RabbitMQConsumerIntTestReplyTo.EXCHANGE, RabbitMQConsumerIntTestReplyTo.ROUTING_KEY, prop.build(), RabbitMQConsumerIntTestReplyTo.REQUEST.getBytes());
        assertThatBodiesReceivedIn(received, RabbitMQConsumerIntTestReplyTo.REPLY);
    }

    private class ArrayPopulatingConsumer extends DefaultConsumer {
        private final List<String> received;

        ArrayPopulatingConsumer(final List<String> received) {
            super(RabbitMQConsumerIntTestReplyTo.this.channel);
            this.received = received;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            received.add(new String(body));
        }
    }
}

