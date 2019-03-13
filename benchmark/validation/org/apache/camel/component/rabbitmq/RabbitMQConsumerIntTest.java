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
import RabbitMQConstants.DELIVERY_MODE;
import RabbitMQConstants.REPLY_TO;
import RabbitMQConstants.TIMESTAMP;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class RabbitMQConsumerIntTest extends AbstractRabbitMQIntTest {
    private static final String EXCHANGE = "ex1";

    private static final String HEADERS_EXCHANGE = "ex8";

    private static final String QUEUE = "q1";

    private static final String MSG = "hello world";

    @EndpointInject(uri = ("rabbitmq:localhost:5672/" + (RabbitMQConsumerIntTest.EXCHANGE)) + "?username=cameltest&password=cameltest")
    private Endpoint from;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint to;

    @EndpointInject(uri = ((("rabbitmq:localhost:5672/" + (RabbitMQConsumerIntTest.HEADERS_EXCHANGE)) + "?username=cameltest&password=cameltest&exchangeType=headers&queue=") + (RabbitMQConsumerIntTest.QUEUE)) + "&args=#args")
    private Endpoint headersExchangeWithQueue;

    @EndpointInject(uri = "rabbitmq:localhost:5672/" + ("ex7" + "?username=cameltest&password=cameltest&exchangeType=headers&autoDelete=false&durable=true&queue=q7&arg.binding.fizz=buzz"))
    private Endpoint headersExchangeWithQueueDefiniedInline;

    @Test
    public void sentMessageIsReceived() throws IOException, InterruptedException, TimeoutException {
        to.expectedMessageCount(1);
        to.expectedHeaderReceived(REPLY_TO, "myReply");
        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.replyTo("myReply");
        Channel channel = connection().createChannel();
        channel.basicPublish(RabbitMQConsumerIntTest.EXCHANGE, "", properties.build(), RabbitMQConsumerIntTest.MSG.getBytes());
        to.assertIsSatisfied();
    }

    @Test
    public void sentMessageIsDeliveryModeSet() throws IOException, InterruptedException, TimeoutException {
        to.expectedMessageCount(1);
        to.expectedHeaderReceived(DELIVERY_MODE, 1);
        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.deliveryMode(1);
        Channel channel = connection().createChannel();
        channel.basicPublish(RabbitMQConsumerIntTest.EXCHANGE, "", properties.build(), RabbitMQConsumerIntTest.MSG.getBytes());
        to.assertIsSatisfied();
    }

    @Test
    public void sentMessageWithTimestampIsReceived() throws IOException, InterruptedException, TimeoutException {
        Date timestamp = currentTimestampWithoutMillis();
        to.expectedMessageCount(1);
        to.expectedHeaderReceived(TIMESTAMP, timestamp);
        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.timestamp(timestamp);
        Channel channel = connection().createChannel();
        channel.basicPublish(RabbitMQConsumerIntTest.EXCHANGE, "", properties.build(), RabbitMQConsumerIntTest.MSG.getBytes());
        to.assertIsSatisfied();
    }

    /**
     * Tests the proper rabbit binding arguments are in place when the headersExchangeWithQueue is created.
     * Should only receive messages with the header [foo=bar]
     */
    @Test
    public void sentMessageIsReceivedWithHeadersRouting() throws IOException, InterruptedException, TimeoutException {
        // should only be one message that makes it through because only
        // one has the correct header set
        to.expectedMessageCount(1);
        Channel channel = connection().createChannel();
        channel.basicPublish(RabbitMQConsumerIntTest.HEADERS_EXCHANGE, "", propertiesWithHeader("foo", "bar"), RabbitMQConsumerIntTest.MSG.getBytes());
        channel.basicPublish(RabbitMQConsumerIntTest.HEADERS_EXCHANGE, "", null, RabbitMQConsumerIntTest.MSG.getBytes());
        channel.basicPublish(RabbitMQConsumerIntTest.HEADERS_EXCHANGE, "", propertiesWithHeader("foo", "bra"), RabbitMQConsumerIntTest.MSG.getBytes());
        to.assertIsSatisfied();
    }

    @Test
    public void sentMessageIsReceivedWithHeadersRoutingMultiValueMapBindings() throws Exception {
        to.expectedMessageCount(3);
        Channel channel = connection().createChannel();
        channel.basicPublish("ex7", "", propertiesWithHeader("fizz", "buzz"), RabbitMQConsumerIntTest.MSG.getBytes());
        channel.basicPublish("ex7", "", propertiesWithHeader("fizz", "buzz"), RabbitMQConsumerIntTest.MSG.getBytes());
        channel.basicPublish("ex7", "", propertiesWithHeader("fizz", "buzz"), RabbitMQConsumerIntTest.MSG.getBytes());
        channel.basicPublish("ex7", "", propertiesWithHeader("fizz", "nope"), RabbitMQConsumerIntTest.MSG.getBytes());
        to.assertIsSatisfied();
    }
}

