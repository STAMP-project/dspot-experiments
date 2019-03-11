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


import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.rocketmq.jms.JmsTestUtil;
import org.apache.rocketmq.jms.domain.JmsBaseConnectionFactory;
import org.apache.rocketmq.jms.domain.RMQPushConsumerExt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsConsumerIT extends IntegrationTestBase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageListener listener = new MessageListener() {
        @Override
        public void onMessage(Message message) {
            try {
                Assert.assertNotNull(message);
                Assert.assertNotNull(message.getJMSMessageID());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Test
    public void testStartIdempotency() throws Exception {
        JmsBaseConnectionFactory connectionFactory = new JmsBaseConnectionFactory(new URI(((("rocketmq://xxx?consumerId=" + (IntegrationTestBase.consumerId)) + "&nameServer=") + (IntegrationTestBase.nameServer))));
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        JmsTestUtil.checkConsumerState(IntegrationTestBase.consumerId, true, false);
        try {
            Destination destination = session.createTopic((((IntegrationTestBase.topic) + ":") + (IntegrationTestBase.messageType)));
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(listener);
            JmsTestUtil.checkConsumerState(IntegrationTestBase.consumerId, false, false);
            startConsumer();
            JmsTestUtil.checkConsumerState(IntegrationTestBase.consumerId, false, true);
            Destination destination1 = session.createTopic((((IntegrationTestBase.topic2) + ":") + (IntegrationTestBase.messageType)));
            MessageConsumer consumer1 = session.createConsumer(destination1);
            consumer1.setMessageListener(listener);
            startConsumer();
            JmsTestUtil.checkConsumerState(IntegrationTestBase.consumerId, false, true);
            // the start is idempotent
            connection.start();
            connection.start();
            Thread.sleep(5000);
        } finally {
            connection.close();
        }
    }

    @Test
    public void testReferenceCount() throws Exception {
        JmsBaseConnectionFactory connectionFactory = new JmsBaseConnectionFactory(new URI(((("rocketmq://xxx?consumerId=" + (IntegrationTestBase.consumerId)) + "&nameServer=") + (IntegrationTestBase.nameServer))));
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        connection.start();
        try {
            Destination destination = session.createTopic((((IntegrationTestBase.topic) + ":") + (IntegrationTestBase.messageType)));
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(listener);
            RMQPushConsumerExt rmqPushConsumerExt = JmsTestUtil.getRMQPushConsumerExt(IntegrationTestBase.consumerId);
            Assert.assertNotNull(rmqPushConsumerExt);
            Assert.assertEquals(1, rmqPushConsumerExt.getReferenceCount());
            MessageConsumer consumer2 = session.createConsumer(destination);
            Assert.assertEquals(2, rmqPushConsumerExt.getReferenceCount());
            MessageConsumer consumer3 = session.createConsumer(session.createTopic((((IntegrationTestBase.topic) + ":") + (IntegrationTestBase.messageType))));
            Assert.assertEquals(3, rmqPushConsumerExt.getReferenceCount());
            session.close();
            Assert.assertEquals(0, rmqPushConsumerExt.getReferenceCount());
            Assert.assertEquals(false, rmqPushConsumerExt.isStarted());
            Assert.assertNull(JmsTestUtil.getRMQPushConsumerExt(IntegrationTestBase.consumerId));
            Thread.sleep(5000);
        } finally {
            connection.close();
        }
    }
}

