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
package org.apache.activemq;


import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsRollbackRedeliveryTest {
    @Rule
    public TestName testName = new TestName();

    protected static final Logger LOG = LoggerFactory.getLogger(JmsRollbackRedeliveryTest.class);

    final int nbMessages = 10;

    final String destinationName = "Destination";

    final String brokerUrl = "vm://localhost?create=false";

    boolean consumerClose = true;

    boolean rollback = true;

    BrokerService broker;

    @Test
    public void testRedelivery() throws Exception {
        doTestRedelivery(brokerUrl, false);
    }

    @Test
    public void testRedeliveryWithInterleavedProducer() throws Exception {
        doTestRedelivery(brokerUrl, true);
    }

    @Test
    public void testRedeliveryWithPrefetch0() throws Exception {
        doTestRedelivery(((brokerUrl) + "?jms.prefetchPolicy.queuePrefetch=0"), true);
    }

    @Test
    public void testRedeliveryWithPrefetch1() throws Exception {
        doTestRedelivery(((brokerUrl) + "?jms.prefetchPolicy.queuePrefetch=1"), true);
    }

    @Test
    public void testRedeliveryOnSingleConsumer() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        populateDestinationWithInterleavedProducer(nbMessages, destinationName, connection);
        // Consume messages and rollback transactions
        {
            AtomicInteger received = new AtomicInteger();
            Map<String, Boolean> rolledback = new ConcurrentHashMap<String, Boolean>();
            Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(destinationName);
            MessageConsumer consumer = session.createConsumer(destination);
            while ((received.get()) < (nbMessages)) {
                TextMessage msg = ((TextMessage) (consumer.receive(6000000)));
                if (msg != null) {
                    if ((msg != null) && ((rolledback.put(msg.getText(), Boolean.TRUE)) != null)) {
                        JmsRollbackRedeliveryTest.LOG.info(((((("Received message " + (msg.getText())) + " (") + (received.getAndIncrement())) + ")") + (msg.getJMSMessageID())));
                        Assert.assertTrue(msg.getJMSRedelivered());
                        session.commit();
                    } else {
                        JmsRollbackRedeliveryTest.LOG.info(((("Rollback message " + (msg.getText())) + " id: ") + (msg.getJMSMessageID())));
                        session.rollback();
                    }
                }
            } 
            consumer.close();
            session.close();
        }
    }

    @Test
    public void testRedeliveryOnSingleSession() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        populateDestination(nbMessages, destinationName, connection);
        // Consume messages and rollback transactions
        {
            AtomicInteger received = new AtomicInteger();
            Map<String, Boolean> rolledback = new ConcurrentHashMap<String, Boolean>();
            Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(destinationName);
            while ((received.get()) < (nbMessages)) {
                MessageConsumer consumer = session.createConsumer(destination);
                TextMessage msg = ((TextMessage) (consumer.receive(6000000)));
                if (msg != null) {
                    if ((msg != null) && ((rolledback.put(msg.getText(), Boolean.TRUE)) != null)) {
                        JmsRollbackRedeliveryTest.LOG.info(((((("Received message " + (msg.getText())) + " (") + (received.getAndIncrement())) + ")") + (msg.getJMSMessageID())));
                        Assert.assertTrue(msg.getJMSRedelivered());
                        session.commit();
                    } else {
                        JmsRollbackRedeliveryTest.LOG.info(((("Rollback message " + (msg.getText())) + " id: ") + (msg.getJMSMessageID())));
                        session.rollback();
                    }
                }
                consumer.close();
            } 
            session.close();
        }
    }

    // AMQ-1593
    @Test
    public void testValidateRedeliveryCountOnRollback() throws Exception {
        final int numMessages = 1;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        populateDestination(numMessages, destinationName, connection);
        {
            AtomicInteger received = new AtomicInteger();
            final int maxRetries = new RedeliveryPolicy().getMaximumRedeliveries();
            while ((received.get()) < maxRetries) {
                Session session = connection.createSession(true, SESSION_TRANSACTED);
                Destination destination = session.createQueue(destinationName);
                MessageConsumer consumer = session.createConsumer(destination);
                TextMessage msg = ((TextMessage) (consumer.receive(1000)));
                if (msg != null) {
                    JmsRollbackRedeliveryTest.LOG.info(((((("Received message " + (msg.getText())) + " (") + (received.getAndIncrement())) + ")") + (msg.getJMSMessageID())));
                    Assert.assertEquals("redelivery property matches deliveries", received.get(), msg.getLongProperty("JMSXDeliveryCount"));
                    session.rollback();
                }
                session.close();
            } 
            consumeMessage(connection, (maxRetries + 1));
        }
    }

    // AMQ-1593
    @Test
    public void testValidateRedeliveryCountOnRollbackWithPrefetch0() throws Exception {
        final int numMessages = 1;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(((brokerUrl) + "?jms.prefetchPolicy.queuePrefetch=0"));
        Connection connection = connectionFactory.createConnection();
        connection.start();
        populateDestination(numMessages, destinationName, connection);
        {
            AtomicInteger received = new AtomicInteger();
            final int maxRetries = new RedeliveryPolicy().getMaximumRedeliveries();
            while ((received.get()) < maxRetries) {
                Session session = connection.createSession(true, SESSION_TRANSACTED);
                Destination destination = session.createQueue(destinationName);
                MessageConsumer consumer = session.createConsumer(destination);
                TextMessage msg = ((TextMessage) (consumer.receive(1000)));
                if (msg != null) {
                    JmsRollbackRedeliveryTest.LOG.info(((((("Received message " + (msg.getText())) + " (") + (received.getAndIncrement())) + ")") + (msg.getJMSMessageID())));
                    Assert.assertEquals("redelivery property matches deliveries", received.get(), msg.getLongProperty("JMSXDeliveryCount"));
                    session.rollback();
                }
                session.close();
            } 
            consumeMessage(connection, (maxRetries + 1));
        }
    }

    @Test
    public void testRedeliveryPropertyWithNoRollback() throws Exception {
        final int numMessages = 1;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        populateDestination(numMessages, destinationName, connection);
        connection.close();
        {
            AtomicInteger received = new AtomicInteger();
            final int maxRetries = new RedeliveryPolicy().getMaximumRedeliveries();
            while ((received.get()) < maxRetries) {
                connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(true, SESSION_TRANSACTED);
                Destination destination = session.createQueue(destinationName);
                MessageConsumer consumer = session.createConsumer(destination);
                TextMessage msg = ((TextMessage) (consumer.receive(2000)));
                if (msg != null) {
                    JmsRollbackRedeliveryTest.LOG.info(((((("Received message " + (msg.getText())) + " (") + (received.getAndIncrement())) + ")") + (msg.getJMSMessageID())));
                    Assert.assertEquals("redelivery property matches deliveries", received.get(), msg.getLongProperty("JMSXDeliveryCount"));
                }
                session.close();
                connection.close();
            } 
            connection = connectionFactory.createConnection();
            connection.start();
            consumeMessage(connection, (maxRetries + 1));
        }
    }
}

