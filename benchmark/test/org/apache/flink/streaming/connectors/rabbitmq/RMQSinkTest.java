/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.rabbitmq;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Collections;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.api.functions.sink.SinkContextUtil;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for the {@link RMQSink}.
 */
public class RMQSinkTest {
    private static final String QUEUE_NAME = "queue";

    private static final String EXCHANGE = "exchange";

    private static final String ROUTING_KEY = "application.component.error";

    private static final String EXPIRATION = "10000";

    private static final String MESSAGE_STR = "msg";

    private static final byte[] MESSAGE = new byte[1];

    private static BasicProperties props = new AMQP.BasicProperties.Builder().headers(Collections.singletonMap("Test", "My Value")).expiration(RMQSinkTest.EXPIRATION).build();

    private RMQConnectionConfig rmqConnectionConfig;

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Channel channel;

    private SerializationSchema<String> serializationSchema;

    private RMQSinkTest.DummyPublishOptions publishOptions;

    private RMQSinkTest.DummyReturnHandler returnListener;

    @Test
    public void openCallDeclaresQueueInStandardMode() throws Exception {
        createRMQSink();
        Mockito.verify(channel).queueDeclare(RMQSinkTest.QUEUE_NAME, false, false, false, null);
    }

    @Test
    public void openCallDontDeclaresQueueInWithOptionsMode() throws Exception {
        createRMQSinkWithOptions(false, false);
        Mockito.verify(channel, Mockito.never()).queueDeclare(null, false, false, false, null);
    }

    @Test
    public void throwExceptionIfChannelIsNull() throws Exception {
        Mockito.when(connection.createChannel()).thenReturn(null);
        try {
            createRMQSink();
        } catch (RuntimeException ex) {
            Assert.assertEquals("None of RabbitMQ channels are available", ex.getMessage());
        }
    }

    @Test
    public void invokePublishBytesToQueue() throws Exception {
        RMQSink<String> rmqSink = createRMQSink();
        rmqSink.invoke(RMQSinkTest.MESSAGE_STR, SinkContextUtil.forTimestamp(0));
        Mockito.verify(serializationSchema).serialize(RMQSinkTest.MESSAGE_STR);
        Mockito.verify(channel).basicPublish("", RMQSinkTest.QUEUE_NAME, null, RMQSinkTest.MESSAGE);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionDuringPublishingIsNotIgnored() throws Exception {
        RMQSink<String> rmqSink = createRMQSink();
        Mockito.doThrow(IOException.class).when(channel).basicPublish("", RMQSinkTest.QUEUE_NAME, null, RMQSinkTest.MESSAGE);
        rmqSink.invoke("msg", SinkContextUtil.forTimestamp(0));
    }

    @Test
    public void exceptionDuringPublishingIsIgnoredIfLogFailuresOnly() throws Exception {
        RMQSink<String> rmqSink = createRMQSink();
        rmqSink.setLogFailuresOnly(true);
        Mockito.doThrow(IOException.class).when(channel).basicPublish("", RMQSinkTest.QUEUE_NAME, null, RMQSinkTest.MESSAGE);
        rmqSink.invoke("msg", SinkContextUtil.forTimestamp(0));
    }

    @Test
    public void closeAllResources() throws Exception {
        RMQSink<String> rmqSink = createRMQSink();
        rmqSink.close();
        Mockito.verify(channel).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void invokePublishBytesToQueueWithOptions() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptions(false, false);
        rmqSink.invoke(RMQSinkTest.MESSAGE_STR, SinkContextUtil.forTimestamp(0));
        Mockito.verify(serializationSchema).serialize(RMQSinkTest.MESSAGE_STR);
        Mockito.verify(channel).basicPublish(RMQSinkTest.EXCHANGE, RMQSinkTest.ROUTING_KEY, false, false, publishOptions.computeProperties(""), RMQSinkTest.MESSAGE);
    }

    @Test(expected = IllegalStateException.class)
    public void invokePublishBytesToQueueWithOptionsMandatory() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptions(true, false);
        rmqSink.invoke(RMQSinkTest.MESSAGE_STR, SinkContextUtil.forTimestamp(0));
    }

    @Test(expected = IllegalStateException.class)
    public void invokePublishBytesToQueueWithOptionsImmediate() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptions(false, true);
        rmqSink.invoke(RMQSinkTest.MESSAGE_STR, SinkContextUtil.forTimestamp(0));
    }

    @Test
    public void invokePublishBytesToQueueWithOptionsMandatoryReturnHandler() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptionsAndReturnHandler(true, false);
        rmqSink.invoke(RMQSinkTest.MESSAGE_STR, SinkContextUtil.forTimestamp(0));
        Mockito.verify(serializationSchema).serialize(RMQSinkTest.MESSAGE_STR);
        Mockito.verify(channel).basicPublish(RMQSinkTest.EXCHANGE, RMQSinkTest.ROUTING_KEY, true, false, publishOptions.computeProperties(""), RMQSinkTest.MESSAGE);
    }

    @Test
    public void invokePublishBytesToQueueWithOptionsImmediateReturnHandler() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptionsAndReturnHandler(false, true);
        rmqSink.invoke(RMQSinkTest.MESSAGE_STR, SinkContextUtil.forTimestamp(0));
        Mockito.verify(serializationSchema).serialize(RMQSinkTest.MESSAGE_STR);
        Mockito.verify(channel).basicPublish(RMQSinkTest.EXCHANGE, RMQSinkTest.ROUTING_KEY, false, true, publishOptions.computeProperties(""), RMQSinkTest.MESSAGE);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionDuringWithOptionsPublishingIsNotIgnored() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptions(false, false);
        Mockito.doThrow(IOException.class).when(channel).basicPublish(RMQSinkTest.EXCHANGE, RMQSinkTest.ROUTING_KEY, false, false, publishOptions.computeProperties(""), RMQSinkTest.MESSAGE);
        rmqSink.invoke("msg", SinkContextUtil.forTimestamp(0));
    }

    @Test
    public void exceptionDuringWithOptionsPublishingIsIgnoredIfLogFailuresOnly() throws Exception {
        RMQSink<String> rmqSink = createRMQSinkWithOptions(false, false);
        rmqSink.setLogFailuresOnly(true);
        Mockito.doThrow(IOException.class).when(channel).basicPublish(RMQSinkTest.EXCHANGE, RMQSinkTest.ROUTING_KEY, false, false, publishOptions.computeProperties(""), RMQSinkTest.MESSAGE);
        rmqSink.invoke("msg", SinkContextUtil.forTimestamp(0));
    }

    private class DummyPublishOptions implements RMQSinkPublishOptions<String> {
        private static final long serialVersionUID = 1L;

        private boolean mandatory = false;

        private boolean immediate = false;

        public DummyPublishOptions(boolean mandatory, boolean immediate) {
            this.mandatory = mandatory;
            this.immediate = immediate;
        }

        @Override
        public String computeRoutingKey(String a) {
            return RMQSinkTest.ROUTING_KEY;
        }

        @Override
        public BasicProperties computeProperties(String a) {
            return RMQSinkTest.props;
        }

        @Override
        public String computeExchange(String a) {
            return RMQSinkTest.EXCHANGE;
        }

        @Override
        public boolean computeMandatory(String a) {
            return mandatory;
        }

        @Override
        public boolean computeImmediate(String a) {
            return immediate;
        }
    }

    private class DummyReturnHandler implements SerializableReturnListener {
        private static final long serialVersionUID = 1L;

        @Override
        public void handleReturn(final int replyCode, final String replyText, final String exchange, final String routingKey, final BasicProperties properties, final byte[] body) {
        }
    }

    private class DummySerializationSchema implements SerializationSchema<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public byte[] serialize(String element) {
            return RMQSinkTest.MESSAGE;
        }
    }
}

