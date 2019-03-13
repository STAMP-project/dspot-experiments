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
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RedeliveryRecoveryTest {
    static final Logger LOG = LoggerFactory.getLogger(RedeliveryRecoveryTest.class);

    ActiveMQConnection connection;

    BrokerService broker = null;

    String queueName = "redeliveryRecoveryQ";

    @Test
    public void testValidateRedeliveryFlagAfterRestart() throws Exception {
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(((broker.getTransportConnectors().get(0).getPublishableConnectString()) + "?jms.prefetchPolicy.all=0"));
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        populateDestination(1, destination, connection);
        MessageConsumer consumer = session.createConsumer(destination);
        Message msg = consumer.receive(5000);
        RedeliveryRecoveryTest.LOG.info(("got: " + msg));
        Assert.assertNotNull("got the message", msg);
        Assert.assertFalse("got the message", msg.getJMSRedelivered());
        consumer.close();
        connection.close();
        restartBroker();
        connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(((broker.getTransportConnectors().get(0).getPublishableConnectString()) + "?jms.prefetchPolicy.all=0"));
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        destination = session.createQueue(queueName);
        consumer = session.createConsumer(destination);
        msg = consumer.receive(5000);
        RedeliveryRecoveryTest.LOG.info(("got: " + msg));
        Assert.assertNotNull("got the message", msg);
        Assert.assertTrue("got the message has redelivered flag", msg.getJMSRedelivered());
        connection.close();
    }
}

