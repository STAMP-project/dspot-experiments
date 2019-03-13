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
import java.util.concurrent.CountDownLatch;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* Test plan:
Producer: publish messages into a queue, with three message groups
Consumer1: created before any messages are created
Consumer2: created after consumer1 has processed one message from each message group

All three groups are handled by to consumer1, so consumer2 should not get any messages.
See bug AMQ-2016: Message grouping fails when consumers are added
 */
public class MessageGroupNewConsumerTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(MessageGroupNewConsumerTest.class);

    private Connection connection;

    // Released after the messages are created
    private CountDownLatch latchMessagesCreated = new CountDownLatch(1);

    // Released after one message from each group is consumed
    private CountDownLatch latchGroupsAcquired = new CountDownLatch(1);

    private static final String[] groupNames = new String[]{ "GrA", "GrB", "GrC" };

    private int messagesSent;

    private int messagesRecvd1;

    private int messagesRecvd2;

    // with the prefetch too high, this bug is not realized
    private static final String connStr = "vm://localhost?broker.persistent=false&broker.useJmx=false&jms.prefetchPolicy.all=1";

    public void testNewConsumer() throws InterruptedException, JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MessageGroupNewConsumerTest.connStr);
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
                        for (String group : MessageGroupNewConsumerTest.groupNames) {
                            Message message = generateMessage(session, group, (i + 1));
                            prod.send(message);
                            session.commit();
                            (messagesSent)++;
                        }
                        MessageGroupNewConsumerTest.LOG.info(("Sent message seq " + (i + 1)));
                        if (i == 0) {
                            latchMessagesCreated.countDown();
                        }
                        if (i == 2) {
                            MessageGroupNewConsumerTest.LOG.info("Prod: Waiting for groups");
                            latchGroupsAcquired.await();
                        }
                        Thread.sleep(20);
                    }
                    MessageGroupNewConsumerTest.LOG.info(((messagesSent) + " messages sent"));
                    prod.close();
                    session.close();
                } catch (Exception e) {
                    MessageGroupNewConsumerTest.LOG.error("Producer failed", e);
                }
            }
        };
        final Thread consumerThread1 = new Thread() {
            public void run() {
                try {
                    Session session = connection.createSession(true, SESSION_TRANSACTED);
                    Queue queue = session.createQueue(queueName);
                    MessageConsumer con1 = session.createConsumer(queue);
                    latchMessagesCreated.await();
                    while (true) {
                        Message message = con1.receive(1000);
                        if (message == null)
                            break;

                        MessageGroupNewConsumerTest.LOG.info(("Con1 got message " + (formatMessage(message))));
                        session.commit();
                        (messagesRecvd1)++;
                        // since we get the messages in order, the first few messages will be one from each group
                        // after we get one from each group, start the other consumer
                        if ((messagesRecvd1) == (MessageGroupNewConsumerTest.groupNames.length)) {
                            MessageGroupNewConsumerTest.LOG.info("All groups acquired");
                            latchGroupsAcquired.countDown();
                            Thread.sleep(1000);
                        }
                        Thread.sleep(50);
                    } 
                    MessageGroupNewConsumerTest.LOG.info(((messagesRecvd1) + " messages received by consumer1"));
                    con1.close();
                    session.close();
                } catch (Exception e) {
                    MessageGroupNewConsumerTest.LOG.error("Consumer 1 failed", e);
                }
            }
        };
        final Thread consumerThread2 = new Thread() {
            public void run() {
                try {
                    latchGroupsAcquired.await();
                    while (consumerThread1.isAlive()) {
                        MessageGroupNewConsumerTest.LOG.info("(re)starting consumer2");
                        Session session = connection.createSession(true, SESSION_TRANSACTED);
                        Queue queue = session.createQueue(queueName);
                        MessageConsumer con2 = session.createConsumer(queue);
                        while (true) {
                            Message message = con2.receive(500);
                            if (message == null)
                                break;

                            MessageGroupNewConsumerTest.LOG.info(("Con2 got message       " + (formatMessage(message))));
                            session.commit();
                            (messagesRecvd2)++;
                            Thread.sleep(50);
                        } 
                        con2.close();
                        session.close();
                    } 
                    MessageGroupNewConsumerTest.LOG.info(((messagesRecvd2) + " messages received by consumer2"));
                } catch (Exception e) {
                    MessageGroupNewConsumerTest.LOG.error("Consumer 2 failed", e);
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
        TestCase.assertEquals("consumer 2 should not get any messages", 0, messagesRecvd2);
        TestCase.assertEquals("consumer 1 should get all the messages", messagesSent, messagesRecvd1);
        TestCase.assertTrue("producer failed to send any messages", ((messagesSent) > 0));
    }
}

