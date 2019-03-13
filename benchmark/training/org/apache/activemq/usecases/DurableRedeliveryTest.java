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
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for AMQ-7071
 * Test to show prefetched messages will be marked as redelivered if connection terminated improperly
 * and the lastDeliveredSequenceId is unknown
 */
public class DurableRedeliveryTest {
    static final Logger LOG = LoggerFactory.getLogger(DurableRedeliveryTest.class);

    BrokerService broker = null;

    String topicName = "testTopic";

    @Test
    public void testRedeliveryFlagAfterConnectionKill() throws Exception {
        ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(broker.getTransportConnectors().get(0).getPublishableConnectString());
        ActiveMQConnection producerConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        ActiveMQConnection durableConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        durableConnection.setClientID("clientId");
        producerConnection.start();
        durableConnection.start();
        Session session = durableConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createDurableSubscriber(topic, "sub1");
        populateDestination(1, topic, producerConnection);
        producerConnection.close();
        Wait.waitFor(() -> (broker.getBroker().getClients().length) == 1);
        // Close the connection on the broker side (not the client) so the delivered status of the
        // prefetched message will be unknown which should now trigger the previously dispatched message
        // to be marked as redelivered
        TransportConnector connector = broker.getTransportConnectors().get(0);
        TransportConnection connection = connector.getConnections().stream().findFirst().get();
        connection.stop();
        Wait.waitFor(() -> (broker.getBroker().getClients().length) == 0);
        // Reconnect and consume the message
        durableConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        durableConnection.setClientID("clientId");
        durableConnection.start();
        session = durableConnection.createSession(false, AUTO_ACKNOWLEDGE);
        topic = session.createTopic(topicName);
        consumer = session.createDurableSubscriber(topic, "sub1");
        Message msg = consumer.receive(2000);
        DurableRedeliveryTest.LOG.info(("got: " + msg));
        Assert.assertNotNull("got the message", msg);
        Assert.assertTrue("got the message has redelivered flag", msg.getJMSRedelivered());
        producerConnection.close();
        durableConnection.close();
    }
}

