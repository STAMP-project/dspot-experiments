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
package org.apache.activemq.transport.amqp;


import Session.AUTO_ACKNOWLEDGE;
import java.util.Date;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.transport.amqp.joram.ActiveMQAdmin;
import org.junit.Assert;
import org.junit.Test;


public class AMQ4563Test extends AmqpTestSupport {
    public static final String KAHADB_DIRECTORY = "./target/activemq-data/kahadb-amq4563";

    @Test(timeout = 60000)
    public void testMessagesAreAckedAMQProducer() throws Exception {
        int messagesSent = 3;
        ActiveMQAdmin.enableJMSFrameTracing();
        Assert.assertTrue(brokerService.isPersistent());
        Connection connection = createAMQConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = null;
        for (int i = 0; i < messagesSent; i++) {
            message = session.createTextMessage();
            String messageText = (("Hello " + i) + " sent at ") + (new Date().toString());
            message.setText(messageText);
            AmqpTestSupport.LOG.debug(">>>> Sent [{}]", messageText);
            producer.send(message);
        }
        // After the first restart we should get all messages sent above
        restartBroker(connection, session);
        int messagesReceived = readAllMessages(name.getMethodName());
        Assert.assertEquals(messagesSent, messagesReceived);
        // This time there should be no messages on this queue
        restartBroker(connection, session);
        QueueViewMBean queueView = getProxyToQueue(name.getMethodName());
        Assert.assertEquals(0, queueView.getQueueSize());
    }

    @Test(timeout = 60000)
    public void testSelectingOnAMQPMessageID() throws Exception {
        ActiveMQAdmin.enableJMSFrameTracing();
        Assert.assertTrue(brokerService.isPersistent());
        Connection connection = JMSClientContext.INSTANCE.createConnection(amqpURI);
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer p = session.createProducer(queue);
        TextMessage message = session.createTextMessage();
        String messageText = "Hello sent at " + (new Date().toString());
        message.setText(messageText);
        p.send(message);
        // Restart broker.
        restartBroker(connection, session);
        String selector = ("JMSMessageID = '" + (message.getJMSMessageID())) + "'";
        AmqpTestSupport.LOG.info("Using selector: {}", selector);
        int messagesReceived = readAllMessages(name.getMethodName(), selector);
        Assert.assertEquals(1, messagesReceived);
    }

    @Test(timeout = 60000)
    public void testSelectingOnActiveMQMessageID() throws Exception {
        ActiveMQAdmin.enableJMSFrameTracing();
        Assert.assertTrue(brokerService.isPersistent());
        Connection connection = createAMQConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageProducer p = session.createProducer(destination);
        TextMessage message = session.createTextMessage();
        String messageText = "Hello sent at " + (new Date().toString());
        message.setText(messageText);
        p.send(message);
        // Restart broker.
        restartBroker(connection, session);
        String selector = ("JMSMessageID = '" + (message.getJMSMessageID())) + "'";
        AmqpTestSupport.LOG.info("Using selector: {}", selector);
        int messagesReceived = readAllMessages(name.getMethodName(), selector);
        Assert.assertEquals(1, messagesReceived);
    }

    @Test(timeout = 60000)
    public void testMessagesAreAckedAMQPProducer() throws Exception {
        int messagesSent = 3;
        ActiveMQAdmin.enableJMSFrameTracing();
        Assert.assertTrue(brokerService.isPersistent());
        Connection connection = JMSClientContext.INSTANCE.createConnection(amqpURI);
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer p = session.createProducer(queue);
        TextMessage message = null;
        for (int i = 0; i < messagesSent; i++) {
            message = session.createTextMessage();
            String messageText = (("Hello " + i) + " sent at ") + (new Date().toString());
            message.setText(messageText);
            AmqpTestSupport.LOG.debug(">>>> Sent [{}]", messageText);
            p.send(message);
        }
        // After the first restart we should get all messages sent above
        restartBroker(connection, session);
        int messagesReceived = readAllMessages(name.getMethodName());
        Assert.assertEquals(messagesSent, messagesReceived);
        // This time there should be no messages on this queue
        restartBroker(connection, session);
        QueueViewMBean queueView = getProxyToQueue(name.getMethodName());
        Assert.assertEquals(0, queueView.getQueueSize());
    }
}

