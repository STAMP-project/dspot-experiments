/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.rocketmq.jms.integration;


import CommonConstant.CONSUMERID;
import CommonConstant.CONSUME_THREAD_NUMS;
import CommonConstant.INSTANCE_NAME;
import CommonConstant.NAMESERVER;
import CommonConstant.PRODUCERID;
import CommonConstant.SEND_TIMEOUT_MILLIS;
import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.jms.JmsTestListener;
import org.apache.rocketmq.jms.JmsTestUtil;
import org.apache.rocketmq.jms.domain.JmsBaseConnectionFactory;
import org.junit.Assert;
import org.junit.Test;


public class JmsClientIT extends IntegrationTestBase {
    @Test
    public void testConfigInURI() throws Exception {
        JmsBaseConnectionFactory connectionFactory = new JmsBaseConnectionFactory(new URI(String.format("rocketmq://xxx?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s", PRODUCERID, IntegrationTestBase.producerId, CONSUMERID, IntegrationTestBase.consumerId, NAMESERVER, IntegrationTestBase.nameServer, CONSUME_THREAD_NUMS, IntegrationTestBase.consumeThreadNums, SEND_TIMEOUT_MILLIS, (10 * 1000), INSTANCE_NAME, "JMS_TEST")));
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        connection.start();
        try {
            Destination destination = session.createTopic((((IntegrationTestBase.topic) + ":") + (IntegrationTestBase.messageType)));
            session.createConsumer(destination);
            session.createProducer(destination);
            DefaultMQPushConsumer rmqPushConsumer = ((DefaultMQPushConsumer) (JmsTestUtil.getRMQPushConsumerExt(IntegrationTestBase.consumerId).getConsumer()));
            Assert.assertNotNull(rmqPushConsumer);
            Assert.assertEquals(IntegrationTestBase.consumerId, rmqPushConsumer.getConsumerGroup());
            Assert.assertEquals("JMS_TEST", rmqPushConsumer.getInstanceName());
            Assert.assertEquals(IntegrationTestBase.consumeThreadNums, rmqPushConsumer.getConsumeThreadMax());
            Assert.assertEquals(IntegrationTestBase.consumeThreadNums, rmqPushConsumer.getConsumeThreadMin());
            Assert.assertEquals(IntegrationTestBase.nameServer, rmqPushConsumer.getNamesrvAddr());
            DefaultMQProducer mqProducer = ((DefaultMQProducer) (JmsTestUtil.getMQProducer(IntegrationTestBase.producerId)));
            Assert.assertNotNull(mqProducer);
            Assert.assertEquals(IntegrationTestBase.producerId, mqProducer.getProducerGroup());
            Assert.assertEquals("JMS_TEST", mqProducer.getInstanceName());
            Assert.assertEquals((10 * 1000), mqProducer.getSendMsgTimeout());
            Assert.assertEquals(IntegrationTestBase.nameServer, mqProducer.getNamesrvAddr());
            Thread.sleep(2000);
        } finally {
            connection.close();
        }
    }

    @Test
    public void testProducerAndConsume_TwoConsumer() throws Exception {
        Connection connection = createConnection(IntegrationTestBase.producerId, IntegrationTestBase.consumerId);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destinationA = session.createTopic("TopicA");
        Destination destinationB = session.createTopic("TopicB");
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        JmsTestListener listenerA = new JmsTestListener(10, countDownLatch);
        JmsTestListener listenerB = new JmsTestListener(10, countDownLatch);
        try {
            // two consumers
            MessageConsumer messageConsumerA = session.createConsumer(destinationA);
            messageConsumerA.setMessageListener(listenerA);
            MessageConsumer messageConsumerB = session.createConsumer(destinationB);
            messageConsumerB.setMessageListener(listenerB);
            // producer
            MessageProducer messageProducer = session.createProducer(destinationA);
            connection.start();
            for (int i = 0; i < 10; i++) {
                TextMessage message = session.createTextMessage(((IntegrationTestBase.text) + i));
                Assert.assertNull(message.getJMSMessageID());
                messageProducer.send(message);
                Assert.assertNotNull(message.getJMSMessageID());
            }
            for (int i = 0; i < 10; i++) {
                TextMessage message = session.createTextMessage(((IntegrationTestBase.text) + i));
                Assert.assertNull(message.getJMSMessageID());
                messageProducer.send(destinationB, message);
                Assert.assertNotNull(message.getJMSMessageID());
            }
            if (countDownLatch.await(30, TimeUnit.SECONDS)) {
                Thread.sleep(2000);
            }
            Assert.assertEquals(10, listenerA.getConsumedNum());
            Assert.assertEquals(10, listenerB.getConsumedNum());
        } finally {
            // Close the connection
            connection.close();
        }
    }

    @Test
    public void testProducerAndConsume_TagFilter() throws Exception {
        Connection connection = createConnection(IntegrationTestBase.producerId, IntegrationTestBase.consumerId);
        Connection anotherConnection = createConnection(IntegrationTestBase.producerId, ((IntegrationTestBase.consumerId) + "other"));
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Session anotherSession = anotherConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destinationA = session.createTopic("topic:tagA");
        Destination destinationB = session.createTopic("topic:tagB");
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        JmsTestListener listenerForTagA = new JmsTestListener(10, countDownLatch);
        JmsTestListener listenerForAll = new JmsTestListener(40, countDownLatch);
        try {
            session.createConsumer(destinationA).setMessageListener(listenerForTagA);
            anotherSession.createConsumer(session.createTopic("topic")).setMessageListener(listenerForAll);
            // producer
            MessageProducer messageProducer = session.createProducer(destinationA);
            connection.start();
            anotherConnection.start();
            for (int i = 0; i < 20; i++) {
                TextMessage message = session.createTextMessage(((IntegrationTestBase.text) + i));
                Assert.assertNull(message.getJMSMessageID());
                messageProducer.send(message);
                Assert.assertNotNull(message.getJMSMessageID());
            }
            for (int i = 0; i < 20; i++) {
                TextMessage message = session.createTextMessage(((IntegrationTestBase.text) + i));
                Assert.assertNull(message.getJMSMessageID());
                messageProducer.send(destinationB, message);
                Assert.assertNotNull(message.getJMSMessageID());
            }
            if (countDownLatch.await(30, TimeUnit.SECONDS)) {
                Thread.sleep(2000);
            }
            Assert.assertEquals(20, listenerForTagA.getConsumedNum());
            Assert.assertEquals(40, listenerForAll.getConsumedNum());
        } finally {
            // Close the connection
            connection.close();
            anotherConnection.close();
        }
    }
}

