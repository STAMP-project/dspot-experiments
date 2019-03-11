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
package org.apache.activemq.broker;


import Session.CLIENT_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.TestSupport;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.transport.failover.FailoverTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.activemq.TestSupport.PersistenceAdapterChoice.KahaDB;


@RunWith(Parameterized.class)
public class RedeliveryRestartTest extends TestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(RedeliveryRestartTest.class);

    ActiveMQConnection connection;

    BrokerService broker = null;

    String queueName = "redeliveryRestartQ";

    @Parameterized.Parameter
    public TestSupport.PersistenceAdapterChoice persistenceAdapterChoice = KahaDB;

    @Test
    public void testValidateRedeliveryFlagAfterRestartNoTx() throws Exception {
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory((("failover:(" + (broker.getTransportConnectors().get(0).getPublishableConnectString())) + ")?jms.prefetchPolicy.all=0"));
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        populateDestination(10, destination, connection);
        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage msg = null;
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (consumer.receive(20000)));
            RedeliveryRestartTest.LOG.info(("not redelivered? got: " + msg));
            assertNotNull("got the message", msg);
            assertEquals("first delivery", 1, msg.getLongProperty("JMSXDeliveryCount"));
            assertEquals("not a redelivery", false, msg.getJMSRedelivered());
        }
        consumer.close();
        restartBroker();
        // make failover aware of the restarted auto assigned port
        connection.getTransport().narrow(FailoverTransport.class).add(true, broker.getTransportConnectors().get(0).getPublishableConnectString());
        consumer = session.createConsumer(destination);
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (consumer.receive(4000)));
            RedeliveryRestartTest.LOG.info(("redelivered? got: " + msg));
            assertNotNull("got the message again", msg);
            assertEquals("re delivery flag", true, msg.getJMSRedelivered());
            assertEquals("redelivery count survives restart", 2, msg.getLongProperty("JMSXDeliveryCount"));
            msg.acknowledge();
        }
        // consume the rest that were not redeliveries
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (consumer.receive(20000)));
            RedeliveryRestartTest.LOG.info(("not redelivered? got: " + msg));
            assertNotNull("got the message", msg);
            assertEquals("not a redelivery", false, msg.getJMSRedelivered());
            assertEquals("first delivery", 1, msg.getLongProperty("JMSXDeliveryCount"));
            msg.acknowledge();
        }
        connection.close();
    }

    @Test
    public void testDurableSubRedeliveryFlagAfterRestartNotSupported() throws Exception {
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory((("failover:(" + (broker.getTransportConnectors().get(0).getPublishableConnectString())) + ")?jms.prefetchPolicy.all=0"));
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.setClientID("id");
        connection.start();
        Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        ActiveMQTopic destination = new ActiveMQTopic(queueName);
        TopicSubscriber durableSub = session.createDurableSubscriber(destination, "id");
        populateDestination(10, destination, connection);
        TextMessage msg = null;
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (durableSub.receive(20000)));
            RedeliveryRestartTest.LOG.info(("not redelivered? got: " + msg));
            assertNotNull("got the message", msg);
            assertEquals("first delivery", 1, msg.getLongProperty("JMSXDeliveryCount"));
            assertEquals("not a redelivery", false, msg.getJMSRedelivered());
        }
        durableSub.close();
        restartBroker();
        // make failover aware of the restarted auto assigned port
        connection.getTransport().narrow(FailoverTransport.class).add(true, broker.getTransportConnectors().get(0).getPublishableConnectString());
        durableSub = session.createDurableSubscriber(destination, "id");
        for (int i = 0; i < 10; i++) {
            msg = ((TextMessage) (durableSub.receive(4000)));
            RedeliveryRestartTest.LOG.info(("redelivered? got: " + msg));
            assertNotNull("got the message again", msg);
            assertEquals("no reDelivery flag", false, msg.getJMSRedelivered());
            msg.acknowledge();
        }
        connection.close();
    }

    @Test
    public void testValidateRedeliveryFlagAfterRestart() throws Exception {
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory((("failover:(" + (broker.getTransportConnectors().get(0).getPublishableConnectString())) + ")?jms.prefetchPolicy.all=0"));
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Destination destination = session.createQueue(queueName);
        populateDestination(10, destination, connection);
        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage msg = null;
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (consumer.receive(20000)));
            RedeliveryRestartTest.LOG.info(("not redelivered? got: " + msg));
            assertNotNull("got the message", msg);
            assertEquals("first delivery", 1, msg.getLongProperty("JMSXDeliveryCount"));
            assertEquals("not a redelivery", false, msg.getJMSRedelivered());
        }
        session.rollback();
        consumer.close();
        restartBroker();
        // make failover aware of the restarted auto assigned port
        connection.getTransport().narrow(FailoverTransport.class).add(true, broker.getTransportConnectors().get(0).getPublishableConnectString());
        consumer = session.createConsumer(destination);
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (consumer.receive(4000)));
            RedeliveryRestartTest.LOG.info(("redelivered? got: " + msg));
            assertNotNull("got the message again", msg);
            assertEquals("redelivery count survives restart", 2, msg.getLongProperty("JMSXDeliveryCount"));
            assertEquals("re delivery flag", true, msg.getJMSRedelivered());
        }
        session.commit();
        // consume the rest that were not redeliveries
        for (int i = 0; i < 5; i++) {
            msg = ((TextMessage) (consumer.receive(20000)));
            RedeliveryRestartTest.LOG.info(("not redelivered? got: " + msg));
            assertNotNull("got the message", msg);
            assertEquals("first delivery", 1, msg.getLongProperty("JMSXDeliveryCount"));
            assertEquals("not a redelivery", false, msg.getJMSRedelivered());
        }
        session.commit();
        connection.close();
    }

    @Test
    public void testValidateRedeliveryFlagAfterRecovery() throws Exception {
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(((broker.getTransportConnectors().get(0).getPublishableConnectString()) + "?jms.prefetchPolicy.all=0"));
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Destination destination = session.createQueue(queueName);
        populateDestination(1, destination, connection);
        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage msg = ((TextMessage) (consumer.receive(5000)));
        RedeliveryRestartTest.LOG.info(("got: " + msg));
        assertNotNull("got the message", msg);
        assertEquals("first delivery", 1, msg.getLongProperty("JMSXDeliveryCount"));
        assertEquals("not a redelivery", false, msg.getJMSRedelivered());
        stopBrokerWithStoreFailure(broker, persistenceAdapterChoice);
        broker = createRestartedBroker();
        broker.start();
        connection.close();
        connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(broker.getTransportConnectors().get(0).getPublishableConnectString());
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        session = connection.createSession(true, SESSION_TRANSACTED);
        consumer = session.createConsumer(destination);
        msg = ((TextMessage) (consumer.receive(10000)));
        assertNotNull("got the message again", msg);
        assertEquals("redelivery count survives restart", 2, msg.getLongProperty("JMSXDeliveryCount"));
        assertEquals("re delivery flag", true, msg.getJMSRedelivered());
        session.commit();
        connection.close();
    }
}

