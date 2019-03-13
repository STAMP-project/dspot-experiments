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
package org.apache.activemq.usecases;


import Session.SESSION_TRANSACTED;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* Test plan:
Producer: publish messages into a queue, with 10 message groups, closing the group with seq=-1 on message 5 and message 10
Consumers: 2 consumers created after all messages are sent

Expected: for each group, messages 1-5 are handled by one consumer and messages 6-10 are handled by the other consumer.  Messages
1 and 6 have the JMSXGroupFirstForConsumer property set to true.
 */
public class MessageGroupCloseTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(MessageGroupNewConsumerTest.class);

    private Connection connection;

    // Released after all messages are created
    private CountDownLatch latchMessagesCreated = new CountDownLatch(1);

    private int messagesSent;

    private int messagesRecvd1;

    private int messagesRecvd2;

    private int messageGroupCount;

    private int errorCountFirstForConsumer;

    private int errorCountWrongConsumerClose;

    private int errorCountDuplicateClose;

    // groupID, count
    private HashMap<String, Integer> messageGroups1 = new HashMap<String, Integer>();

    private HashMap<String, Integer> messageGroups2 = new HashMap<String, Integer>();

    private HashSet<String> closedGroups1 = new HashSet<String>();

    private HashSet<String> closedGroups2 = new HashSet<String>();

    // with the prefetch too high, this bug is not realized
    private static final String connStr = "vm://localhost?broker.persistent=false&broker.useJmx=false&jms.prefetchPolicy.all=1";

    public void testNewConsumer() throws InterruptedException, JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MessageGroupCloseTest.connStr);
        connection = factory.createConnection();
        connection.start();
        final String queueName = this.getClass().getSimpleName();
        final Thread producerThread = new Thread() {
            public void run() {
                try {
                    Session session = connection.createSession(true, SESSION_TRANSACTED);
                    Queue queue = session.createQueue(queueName);
                    MessageProducer prod = session.createProducer(queue);
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            int seq = j + 1;
                            if (((j + 1) % 5) == 0) {
                                seq = -1;
                            }
                            Message message = generateMessage(session, Integer.toString(i), seq);
                            prod.send(message);
                            session.commit();
                            (messagesSent)++;
                            MessageGroupCloseTest.LOG.info(((("Sent message: group=" + i) + ", seq=") + seq));
                            // Thread.sleep(20);
                        }
                        if ((i % 100) == 0) {
                            MessageGroupCloseTest.LOG.info(("Sent messages: group=" + i));
                        }
                        setMessageGroupCount(((getMessageGroupCount()) + 1));
                    }
                    MessageGroupCloseTest.LOG.info(((messagesSent) + " messages sent"));
                    latchMessagesCreated.countDown();
                    prod.close();
                    session.close();
                } catch (Exception e) {
                    MessageGroupCloseTest.LOG.error("Producer failed", e);
                }
            }
        };
        final Thread consumerThread1 = new Thread() {
            public void run() {
                try {
                    latchMessagesCreated.await();
                    MessageGroupCloseTest.LOG.info("starting consumer1");
                    Session session = connection.createSession(true, SESSION_TRANSACTED);
                    Queue queue = session.createQueue(queueName);
                    MessageConsumer con1 = session.createConsumer(queue);
                    while (true) {
                        Message message = con1.receive(5000);
                        if (message == null)
                            break;

                        MessageGroupCloseTest.LOG.info(("Con1: got message " + (formatMessage(message))));
                        checkMessage(message, "Con1", messageGroups1, closedGroups1);
                        session.commit();
                        (messagesRecvd1)++;
                        if (((messagesRecvd1) % 100) == 0) {
                            MessageGroupCloseTest.LOG.info(("Con1: got messages count=" + (messagesRecvd1)));
                        }
                        // Thread.sleep(50);
                    } 
                    MessageGroupCloseTest.LOG.info(("Con1: total messages=" + (messagesRecvd1)));
                    MessageGroupCloseTest.LOG.info(("Con1: total message groups=" + (messageGroups1.size())));
                    con1.close();
                    session.close();
                } catch (Exception e) {
                    MessageGroupCloseTest.LOG.error("Consumer 1 failed", e);
                }
            }
        };
        final Thread consumerThread2 = new Thread() {
            public void run() {
                try {
                    latchMessagesCreated.await();
                    MessageGroupCloseTest.LOG.info("starting consumer2");
                    Session session = connection.createSession(true, SESSION_TRANSACTED);
                    Queue queue = session.createQueue(queueName);
                    MessageConsumer con2 = session.createConsumer(queue);
                    while (true) {
                        Message message = con2.receive(5000);
                        if (message == null) {
                            break;
                        }
                        MessageGroupCloseTest.LOG.info(("Con2: got message " + (formatMessage(message))));
                        checkMessage(message, "Con2", messageGroups2, closedGroups2);
                        session.commit();
                        (messagesRecvd2)++;
                        if (((messagesRecvd2) % 100) == 0) {
                            MessageGroupCloseTest.LOG.info(("Con2: got messages count=" + (messagesRecvd2)));
                        }
                        // Thread.sleep(50);
                    } 
                    con2.close();
                    session.close();
                    MessageGroupCloseTest.LOG.info(("Con2: total messages=" + (messagesRecvd2)));
                    MessageGroupCloseTest.LOG.info(("Con2: total message groups=" + (messageGroups2.size())));
                } catch (Exception e) {
                    MessageGroupCloseTest.LOG.error("Consumer 2 failed", e);
                }
            }
        };
        consumerThread2.start();
        consumerThread1.start();
        producerThread.start();
        // wait for threads to finish
        producerThread.join();
        consumerThread1.join();
        consumerThread2.join();
        connection.close();
        // check results
        TestCase.assertEquals("consumers should get all the messages", messagesSent, ((messagesRecvd1) + (messagesRecvd2)));
        TestCase.assertEquals("not all message groups closed for consumer 1", messageGroups1.size(), closedGroups1.size());
        TestCase.assertEquals("not all message groups closed for consumer 2", messageGroups2.size(), closedGroups2.size());
        TestCase.assertTrue("producer failed to send any messages", ((messagesSent) > 0));
        TestCase.assertEquals("JMSXGroupFirstForConsumer not set", 0, errorCountFirstForConsumer);
        TestCase.assertEquals("wrong consumer got close message", 0, errorCountWrongConsumerClose);
        TestCase.assertEquals("consumer got duplicate close message", 0, errorCountDuplicateClose);
    }
}

