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
package org.apache.activemq.broker.scheduler;


import ScheduledMessage.AMQ_SCHEDULED_DELAY;
import Session.SESSION_TRANSACTED;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Using the broker's scheduler and setting reduceMemoryFootprint="true" causes
 * message properties to be lost.
 */
public class ReduceMemoryFootprintTest {
    private static final Logger LOG = LoggerFactory.getLogger(ReduceMemoryFootprintTest.class);

    private static final String TEST_AMQ_BROKER_URI = "tcp://localhost:0";

    private static final String TEST_QUEUE_NAME = "Reduce.Memory.Footprint.Test";

    private static final String PROP_NAME = "prop_name";

    private static final String PROP_VALUE = "test-value";

    private String connectionURI;

    private BrokerService broker;

    @Test(timeout = 60000)
    public void testPropertyLostNonScheduled() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionURI);
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = session.createProducer(new ActiveMQQueue(ReduceMemoryFootprintTest.TEST_QUEUE_NAME));
        connection.start();
        String messageText = createMessageText();
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        // Try with non-scheduled
        message.setStringProperty(ReduceMemoryFootprintTest.PROP_NAME, ReduceMemoryFootprintTest.PROP_VALUE);
        message.setText(messageText);
        producer.send(message);
        session.commit();
        ReduceMemoryFootprintTest.LOG.info("Attempting to receive non-scheduled message");
        Message receivedMessage = consumeMessages(connection);
        Assert.assertNotNull(receivedMessage);
        Assert.assertEquals("property should match", ReduceMemoryFootprintTest.PROP_VALUE, receivedMessage.getStringProperty(ReduceMemoryFootprintTest.PROP_NAME));
        connection.close();
    }

    @Test(timeout = 60000)
    public void testPropertyLostScheduled() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionURI);
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = session.createProducer(new ActiveMQQueue(ReduceMemoryFootprintTest.TEST_QUEUE_NAME));
        connection.start();
        String messageText = createMessageText();
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        // Try with scheduled
        message.setStringProperty(ReduceMemoryFootprintTest.PROP_NAME, ReduceMemoryFootprintTest.PROP_VALUE);
        message.setLongProperty(AMQ_SCHEDULED_DELAY, 1000);
        message.setText(messageText);
        producer.send(message);
        session.commit();
        ReduceMemoryFootprintTest.LOG.info("Attempting to receive scheduled message");
        Message receivedMessage = consumeMessages(connection);
        Assert.assertNotNull(receivedMessage);
        Assert.assertEquals("property should match", ReduceMemoryFootprintTest.PROP_VALUE, receivedMessage.getStringProperty(ReduceMemoryFootprintTest.PROP_NAME));
        connection.close();
    }
}

