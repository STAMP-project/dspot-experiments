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


import Session.AUTO_ACKNOWLEDGE;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NonBlockingConsumerRedeliveryTest {
    private static final Logger LOG = LoggerFactory.getLogger(NonBlockingConsumerRedeliveryTest.class);

    private final String destinationName = "Destination";

    private final int MSG_COUNT = 100;

    private BrokerService broker;

    private String connectionUri;

    private ActiveMQConnectionFactory connectionFactory;

    @Test
    public void testMessageDeleiveredWhenNonBlockingEnabled() throws Exception {
        final LinkedHashSet<Message> received = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> beforeRollback = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> afterRollback = new LinkedHashSet<Message>();
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(destinationName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.add(message);
            }
        });
        sendMessages();
        session.commit();
        connection.start();
        Assert.assertTrue((("Pre-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        beforeRollback.addAll(received);
        received.clear();
        session.rollback();
        Assert.assertTrue((("Post-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages since rollback."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        afterRollback.addAll(received);
        received.clear();
        Assert.assertEquals(beforeRollback.size(), afterRollback.size());
        Assert.assertEquals(beforeRollback, afterRollback);
        session.commit();
    }

    @Test
    public void testMessageDeleiveredInCorrectOrder() throws Exception {
        final LinkedHashSet<Message> received = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> beforeRollback = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> afterRollback = new LinkedHashSet<Message>();
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(destinationName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.add(message);
            }
        });
        sendMessages();
        session.commit();
        connection.start();
        Assert.assertTrue((("Pre-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        beforeRollback.addAll(received);
        received.clear();
        session.rollback();
        Assert.assertTrue((("Post-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages since rollback."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        afterRollback.addAll(received);
        received.clear();
        Assert.assertEquals(beforeRollback.size(), afterRollback.size());
        Assert.assertEquals(beforeRollback, afterRollback);
        Iterator<Message> after = afterRollback.iterator();
        Iterator<Message> before = beforeRollback.iterator();
        while ((before.hasNext()) && (after.hasNext())) {
            TextMessage original = ((TextMessage) (before.next()));
            TextMessage rolledBack = ((TextMessage) (after.next()));
            int originalInt = Integer.parseInt(original.getText());
            int rolledbackInt = Integer.parseInt(rolledBack.getText());
            Assert.assertEquals(originalInt, rolledbackInt);
        } 
        session.commit();
    }

    @Test
    public void testMessageDeleiveryDoesntStop() throws Exception {
        final LinkedHashSet<Message> received = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> beforeRollback = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> afterRollback = new LinkedHashSet<Message>();
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(destinationName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.add(message);
            }
        });
        sendMessages();
        connection.start();
        Assert.assertTrue((("Pre-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        beforeRollback.addAll(received);
        received.clear();
        session.rollback();
        sendMessages();
        Assert.assertTrue((("Post-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages since rollback."));
                return (received.size()) == ((MSG_COUNT) * 2);
            }
        }));
        afterRollback.addAll(received);
        received.clear();
        Assert.assertEquals(((beforeRollback.size()) * 2), afterRollback.size());
        session.commit();
    }

    @Test
    public void testNonBlockingMessageDeleiveryIsDelayed() throws Exception {
        final LinkedHashSet<Message> received = new LinkedHashSet<Message>();
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.getRedeliveryPolicy().setInitialRedeliveryDelay(TimeUnit.SECONDS.toMillis(6));
        Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(destinationName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.add(message);
            }
        });
        sendMessages();
        connection.start();
        Assert.assertTrue((("Pre-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        received.clear();
        session.rollback();
        Assert.assertFalse("Delayed redelivery test not expecting any messages yet.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (received.size()) > 0;
            }
        }, TimeUnit.SECONDS.toMillis(4)));
        session.commit();
        session.close();
    }

    @Test
    public void testNonBlockingMessageDeleiveryWithRollbacks() throws Exception {
        final LinkedHashSet<Message> received = new LinkedHashSet<Message>();
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        final Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(destinationName);
        final MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.add(message);
            }
        });
        sendMessages();
        connection.start();
        Assert.assertTrue((("Pre-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        received.clear();
        consumer.setMessageListener(new MessageListener() {
            int count = 0;

            @Override
            public void onMessage(Message message) {
                if ((++(count)) > 10) {
                    try {
                        session.rollback();
                        NonBlockingConsumerRedeliveryTest.LOG.info("Rolling back session.");
                        count = 0;
                    } catch (JMSException e) {
                        NonBlockingConsumerRedeliveryTest.LOG.warn(("Caught an unexcepted exception: " + (e.getMessage())));
                    }
                } else {
                    received.add(message);
                    try {
                        session.commit();
                    } catch (JMSException e) {
                        NonBlockingConsumerRedeliveryTest.LOG.warn(("Caught an unexcepted exception: " + (e.getMessage())));
                    }
                }
            }
        });
        session.rollback();
        Assert.assertTrue((("Post-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages since rollback."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        Assert.assertEquals(MSG_COUNT, received.size());
        session.commit();
    }

    @Test
    public void testNonBlockingMessageDeleiveryWithAllRolledBack() throws Exception {
        final LinkedHashSet<Message> received = new LinkedHashSet<Message>();
        final LinkedHashSet<Message> dlqed = new LinkedHashSet<Message>();
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.getRedeliveryPolicy().setMaximumRedeliveries(5);
        final Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(destinationName);
        final Destination dlq = session.createQueue("ActiveMQ.DLQ");
        final MessageConsumer consumer = session.createConsumer(destination);
        final MessageConsumer dlqConsumer = session.createConsumer(dlq);
        dlqConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                dlqed.add(message);
            }
        });
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.add(message);
            }
        });
        sendMessages();
        connection.start();
        Assert.assertTrue((("Pre-Rollback expects to receive: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (received.size())) + " messages."));
                return (received.size()) == (MSG_COUNT);
            }
        }));
        session.rollback();
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    session.rollback();
                } catch (JMSException e) {
                    NonBlockingConsumerRedeliveryTest.LOG.warn(("Caught an unexcepted exception: " + (e.getMessage())));
                }
            }
        });
        Assert.assertTrue((("Post-Rollback expects to DLQ: " + (MSG_COUNT)) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                NonBlockingConsumerRedeliveryTest.LOG.info((("Consumer has received " + (dlqed.size())) + " messages in DLQ."));
                return (dlqed.size()) == (MSG_COUNT);
            }
        }));
        session.commit();
    }
}

