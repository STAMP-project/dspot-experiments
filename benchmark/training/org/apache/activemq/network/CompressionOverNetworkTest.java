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
package org.apache.activemq.network;


import DeliveryMode.NON_PERSISTENT;
import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public class CompressionOverNetworkTest {
    protected static final int RECEIVE_TIMEOUT_MILLS = 10000;

    protected static final int MESSAGE_COUNT = 10;

    private static final Logger LOG = LoggerFactory.getLogger(CompressionOverNetworkTest.class);

    protected AbstractApplicationContext context;

    protected Connection localConnection;

    protected Connection remoteConnection;

    protected BrokerService localBroker;

    protected BrokerService remoteBroker;

    protected Session localSession;

    protected Session remoteSession;

    protected ActiveMQDestination included;

    @Test
    public void testCompressedOverCompressedNetwork() throws Exception {
        ActiveMQConnection localAmqConnection = ((ActiveMQConnection) (localConnection));
        localAmqConnection.setUseCompression(true);
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        StringBuilder payload = new StringBuilder("test-");
        for (int i = 0; i < 100; ++i) {
            payload.append(UUID.randomUUID().toString());
        }
        Message test = localSession.createTextMessage(payload.toString());
        producer.send(test);
        Message msg = consumer1.receive(CompressionOverNetworkTest.RECEIVE_TIMEOUT_MILLS);
        Assert.assertNotNull(msg);
        ActiveMQTextMessage message = ((ActiveMQTextMessage) (msg));
        Assert.assertTrue(message.isCompressed());
        Assert.assertEquals(payload.toString(), message.getText());
    }

    @Test
    public void testTextMessageCompression() throws Exception {
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        StringBuilder payload = new StringBuilder("test-");
        for (int i = 0; i < 100; ++i) {
            payload.append(UUID.randomUUID().toString());
        }
        Message test = localSession.createTextMessage(payload.toString());
        producer.send(test);
        Message msg = consumer1.receive(CompressionOverNetworkTest.RECEIVE_TIMEOUT_MILLS);
        Assert.assertNotNull(msg);
        ActiveMQTextMessage message = ((ActiveMQTextMessage) (msg));
        Assert.assertTrue(message.isCompressed());
        Assert.assertEquals(payload.toString(), message.getText());
    }

    @Test
    public void testBytesMessageCompression() throws Exception {
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        StringBuilder payload = new StringBuilder("test-");
        for (int i = 0; i < 100; ++i) {
            payload.append(UUID.randomUUID().toString());
        }
        byte[] bytes = payload.toString().getBytes("UTF-8");
        BytesMessage test = localSession.createBytesMessage();
        test.writeBytes(bytes);
        producer.send(test);
        Message msg = consumer1.receive(CompressionOverNetworkTest.RECEIVE_TIMEOUT_MILLS);
        Assert.assertNotNull(msg);
        ActiveMQBytesMessage message = ((ActiveMQBytesMessage) (msg));
        Assert.assertTrue(message.isCompressed());
        Assert.assertTrue(((message.getContent().getLength()) < (bytes.length)));
        byte[] result = new byte[bytes.length];
        Assert.assertEquals(bytes.length, message.readBytes(result));
        Assert.assertEquals((-1), message.readBytes(result));
        for (int i = 0; i < (bytes.length); ++i) {
            Assert.assertEquals(bytes[i], result[i]);
        }
    }

    @Test
    public void testStreamMessageCompression() throws Exception {
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        StreamMessage test = localSession.createStreamMessage();
        for (int i = 0; i < 100; ++i) {
            test.writeString(("test string: " + i));
        }
        producer.send(test);
        Message msg = consumer1.receive(CompressionOverNetworkTest.RECEIVE_TIMEOUT_MILLS);
        Assert.assertNotNull(msg);
        ActiveMQStreamMessage message = ((ActiveMQStreamMessage) (msg));
        Assert.assertTrue(message.isCompressed());
        for (int i = 0; i < 100; ++i) {
            Assert.assertEquals(("test string: " + i), message.readString());
        }
    }

    @Test
    public void testMapMessageCompression() throws Exception {
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        MapMessage test = localSession.createMapMessage();
        for (int i = 0; i < 100; ++i) {
            test.setString(Integer.toString(i), ("test string: " + i));
        }
        producer.send(test);
        Message msg = consumer1.receive(CompressionOverNetworkTest.RECEIVE_TIMEOUT_MILLS);
        Assert.assertNotNull(msg);
        ActiveMQMapMessage message = ((ActiveMQMapMessage) (msg));
        Assert.assertTrue(message.isCompressed());
        for (int i = 0; i < 100; ++i) {
            Assert.assertEquals(("test string: " + i), message.getString(Integer.toString(i)));
        }
    }

    @Test
    public void testObjectMessageCompression() throws Exception {
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        StringBuilder payload = new StringBuilder("test-");
        for (int i = 0; i < 100; ++i) {
            payload.append(UUID.randomUUID().toString());
        }
        Message test = localSession.createObjectMessage(payload.toString());
        producer.send(test);
        Message msg = consumer1.receive(CompressionOverNetworkTest.RECEIVE_TIMEOUT_MILLS);
        Assert.assertNotNull(msg);
        ActiveMQObjectMessage message = ((ActiveMQObjectMessage) (msg));
        Assert.assertTrue(message.isCompressed());
        Assert.assertEquals(payload.toString(), message.getObject());
    }
}

