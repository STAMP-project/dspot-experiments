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


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcDurableSubDupTest {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcDurableSubDupTest.class);

    final int prefetchVal = 150;

    String urlOptions = "jms.watchTopicAdvisories=false";

    String url = null;

    String queueName = "topicTest?consumer.prefetchSize=" + (prefetchVal);

    String xmlMessage = "<Example 01234567890123456789012345678901234567890123456789 MessageText>";

    String selector = "";

    String clntVersion = "87";

    String clntId = "timsClntId345" + (clntVersion);

    String subscriptionName = "subscriptionName-y" + (clntVersion);

    SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");

    final int TO_RECEIVE = 5000;

    BrokerService broker = null;

    Vector<Throwable> exceptions = new Vector<Throwable>();

    final int MAX_MESSAGES = 100000;

    int[] dupChecker = new int[MAX_MESSAGES];

    @Test
    public void testNoDupsOnSlowConsumerReconnect() throws Exception {
        JdbcDurableSubDupTest.JmsConsumerDup consumer = new JdbcDurableSubDupTest.JmsConsumerDup();
        consumer.done.set(true);
        consumer.run();
        consumer.done.set(false);
        JdbcDurableSubDupTest.LOG.info("serial production then consumption");
        JdbcDurableSubDupTest.JmsProvider provider = new JdbcDurableSubDupTest.JmsProvider();
        provider.run();
        consumer.run();
        Assert.assertTrue(("no exceptions: " + (exceptions)), exceptions.isEmpty());
        for (int i = 0; i < (TO_RECEIVE); i++) {
            Assert.assertTrue(("got message " + i), ((dupChecker[i]) == 1));
        }
    }

    @Test
    public void testNoDupsOnSlowConsumerLargePriorityGapReconnect() throws Exception {
        JdbcDurableSubDupTest.JmsConsumerDup consumer = new JdbcDurableSubDupTest.JmsConsumerDup();
        consumer.done.set(true);
        consumer.run();
        consumer.done.set(false);
        JdbcDurableSubDupTest.JmsProvider provider = new JdbcDurableSubDupTest.JmsProvider();
        provider.priorityModulator = 2500;
        provider.run();
        consumer.run();
        Assert.assertTrue(("no exceptions: " + (exceptions)), exceptions.isEmpty());
        for (int i = 0; i < (TO_RECEIVE); i++) {
            Assert.assertTrue(("got message " + i), ((dupChecker[i]) == 1));
        }
    }

    class JmsConsumerDup implements MessageListener {
        long count = 0;

        AtomicBoolean done = new AtomicBoolean(false);

        public void run() {
            Connection connection = null;
            Session session;
            Topic topic;
            ActiveMQConnectionFactory factory;
            MessageConsumer consumer;
            factory = new ActiveMQConnectionFactory(url);
            try {
                connection = factory.createConnection("MyUsername", "MyPassword");
                connection.setClientID(clntId);
                connection.start();
                session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                topic = session.createTopic(queueName);
                consumer = session.createDurableSubscriber(topic, subscriptionName, selector, false);
                consumer.setMessageListener(this);
                JdbcDurableSubDupTest.LOG.info("Waiting for messages...");
                while (!(done.get())) {
                    TimeUnit.SECONDS.sleep(5);
                    if (((count) == (TO_RECEIVE)) || (!(exceptions.isEmpty()))) {
                        done.set(true);
                    }
                } 
            } catch (Exception e) {
                JdbcDurableSubDupTest.LOG.error("caught", e);
                exceptions.add(e);
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    try {
                        JdbcDurableSubDupTest.LOG.info((("consumer done (" + (exceptions.isEmpty())) + "), closing connection"));
                        connection.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onMessage(Message message) {
            ++(count);
            try {
                Thread.sleep(0L);
            } catch (InterruptedException e) {
            }
            try {
                TextMessage m = ((TextMessage) (message));
                if (((count) % 100) == 0) {
                    JdbcDurableSubDupTest.LOG.info(((((((((((("Rcvd Msg #-" + (count)) + " ") + (m.getText())) + " Sent->") + (dtf.format(new Date(m.getJMSTimestamp())))) + " Recv->") + (dtf.format(new Date()))) + " Expr->") + (dtf.format(new Date(m.getJMSExpiration())))) + ", mid: ") + (m.getJMSMessageID())));
                }
                int i = m.getIntProperty("SeqNo");
                // check for duplicate messages
                if (i < (MAX_MESSAGES)) {
                    if ((dupChecker[i]) == 1) {
                        JdbcDurableSubDupTest.LOG.error(((("Duplicate message received at count: " + (count)) + ", id: ") + (m.getJMSMessageID())));
                        exceptions.add(new RuntimeException(("Got Duplicate at: " + (m.getJMSMessageID()))));
                    } else {
                        dupChecker[i] = 1;
                    }
                }
            } catch (JMSException e) {
                JdbcDurableSubDupTest.LOG.error("caught ", e);
                exceptions.add(e);
            }
        }
    }

    class JmsProvider implements Runnable {
        int priorityModulator = 10;

        @Override
        public void run() {
            Connection connection;
            Session session;
            Topic topic;
            ActiveMQConnectionFactory factory;
            MessageProducer messageProducer;
            long timeToLive = 0L;
            TextMessage message = null;
            factory = new ActiveMQConnectionFactory(url);
            try {
                connection = factory.createConnection("MyUserName", "MyPassword");
                session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                topic = session.createTopic(queueName);
                messageProducer = session.createProducer(topic);
                messageProducer.setPriority(3);
                messageProducer.setTimeToLive(timeToLive);
                messageProducer.setDeliveryMode(PERSISTENT);
                int msgSeqNo = 0;
                int NUM_MSGS = 1000;
                int NUM_GROUPS = (TO_RECEIVE) / NUM_MSGS;
                for (int n = 0; n < NUM_GROUPS; n++) {
                    message = session.createTextMessage();
                    for (int i = 0; i < NUM_MSGS; i++) {
                        int priority = 0;
                        if ((priorityModulator) <= 10) {
                            priority = msgSeqNo % (priorityModulator);
                        } else {
                            priority = (msgSeqNo >= (priorityModulator)) ? 9 : 0;
                        }
                        message.setText(((((xmlMessage) + msgSeqNo) + "-") + priority));
                        message.setJMSPriority(priority);
                        message.setIntProperty("SeqNo", msgSeqNo);
                        if ((i > 0) && ((i % 100) == 0)) {
                            JdbcDurableSubDupTest.LOG.info(("Sending message: " + (message.getText())));
                        }
                        messageProducer.send(message, PERSISTENT, message.getJMSPriority(), timeToLive);
                        msgSeqNo++;
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        exceptions.add(e);
                    }
                }
            } catch (JMSException e) {
                JdbcDurableSubDupTest.LOG.error("caught ", e);
                e.printStackTrace();
                exceptions.add(e);
            }
        }
    }
}

