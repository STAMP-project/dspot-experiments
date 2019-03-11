/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.util.ArrayList;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueueZeroPrefetchLazyDispatchPriorityTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueueZeroPrefetchLazyDispatchPriorityTest.class);

    private final byte[] PAYLOAD = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    private final int ITERATIONS = 6;

    private BrokerService broker;

    @Test(timeout = 120000)
    public void testPriorityMessages() throws Exception {
        for (int i = 0; i < (ITERATIONS); i++) {
            // send 4 message priority MEDIUM
            produceMessages(4, 4, "TestQ");
            // send 1 message priority HIGH
            produceMessages(1, 5, "TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("On iteration {}", i);
            Thread.sleep(500);
            // consume messages
            ArrayList<Message> consumeList = consumeMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info(("Consumed list " + (consumeList.size())));
            // compare lists
            Assert.assertEquals("message 1 should be priority high", 5, consumeList.get(0).getJMSPriority());
            Assert.assertEquals("message 2 should be priority medium", 4, consumeList.get(1).getJMSPriority());
            Assert.assertEquals("message 3 should be priority medium", 4, consumeList.get(2).getJMSPriority());
            Assert.assertEquals("message 4 should be priority medium", 4, consumeList.get(3).getJMSPriority());
            Assert.assertEquals("message 5 should be priority medium", 4, consumeList.get(4).getJMSPriority());
        }
    }

    @Test(timeout = 120000)
    public void testPriorityMessagesMoreThanPageSize() throws Exception {
        final int numToSend = 450;
        for (int i = 0; i < (ITERATIONS); i++) {
            produceMessages((numToSend - 1), 4, "TestQ");
            // ensure we get expiry processing
            Thread.sleep(700);
            // send 1 message priority HIGH
            produceMessages(1, 5, "TestQ");
            Thread.sleep(500);
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("On iteration {}", i);
            // consume messages
            ArrayList<Message> consumeList = consumeMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("Consumed list {}", consumeList.size());
            // compare lists
            Assert.assertFalse("Consumed list should not be empty", consumeList.isEmpty());
            Assert.assertEquals("message 1 should be priority high", 5, consumeList.get(0).getJMSPriority());
            for (int j = 1; j < (numToSend - 1); j++) {
                Assert.assertEquals((("message " + j) + " should be priority medium"), 4, consumeList.get(j).getJMSPriority());
            }
        }
    }

    @Test(timeout = 120000)
    public void testLongLivedPriorityConsumer() throws Exception {
        final int numToSend = 150;
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(broker.getTransportConnectorByScheme("tcp").getPublishableConnectString());
        Connection connection = connectionFactory.createConnection();
        try {
            Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(new ActiveMQQueue("TestQ"));
            connection.start();
            for (int i = 0; i < (ITERATIONS); i++) {
                produceMessages((numToSend - 1), 4, "TestQ");
                // send 1 message priority HIGH
                produceMessages(1, 5, "TestQ");
                Message message = consumer.receive(4000);
                Assert.assertEquals("message should be priority high", 5, message.getJMSPriority());
            }
        } finally {
            connection.close();
        }
        ArrayList<Message> consumeList = consumeMessages("TestQ");
        QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("Consumed list {}", consumeList.size());
        for (Message message : consumeList) {
            Assert.assertEquals("should be priority medium", 4, message.getJMSPriority());
        }
    }

    @Test(timeout = 120000)
    public void testPriorityMessagesWithJmsBrowser() throws Exception {
        final int numToSend = 250;
        for (int i = 0; i < (ITERATIONS); i++) {
            produceMessages((numToSend - 1), 4, "TestQ");
            ArrayList<Message> browsed = browseMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("Browsed: {}", browsed.size());
            // send 1 message priority HIGH
            produceMessages(1, 5, "TestQ");
            Thread.sleep(500);
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("On iteration {}", i);
            Message message = consumeOneMessage("TestQ");
            Assert.assertNotNull(message);
            Assert.assertEquals(5, message.getJMSPriority());
            // consume messages
            ArrayList<Message> consumeList = consumeMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("Consumed list {}", consumeList.size());
            // compare lists
            // assertEquals("Iteration: " + i
            // +", message 1 should be priority high", 5,
            // consumeList.get(0).getJMSPriority());
            for (int j = 1; j < (numToSend - 1); j++) {
                Assert.assertEquals((((("Iteration: " + i) + ", message ") + j) + " should be priority medium"), 4, consumeList.get(j).getJMSPriority());
            }
        }
    }

    @Test(timeout = 120000)
    public void testJmsBrowserGetsPagedIn() throws Exception {
        final int numToSend = 10;
        for (int i = 0; i < (ITERATIONS); i++) {
            produceMessages(numToSend, 4, "TestQ");
            ArrayList<Message> browsed = browseMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("Browsed: {}", browsed.size());
            Assert.assertEquals(0, browsed.size());
            Message message = consumeOneMessage("TestQ", CLIENT_ACKNOWLEDGE);
            Assert.assertNotNull(message);
            browsed = browseMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info("Browsed: {}", browsed.size());
            Assert.assertEquals("see only the paged in for pull", 1, browsed.size());
            // consume messages
            ArrayList<Message> consumeList = consumeMessages("TestQ");
            QueueZeroPrefetchLazyDispatchPriorityTest.LOG.info(("Consumed list " + (consumeList.size())));
            Assert.assertEquals(numToSend, consumeList.size());
        }
    }
}

