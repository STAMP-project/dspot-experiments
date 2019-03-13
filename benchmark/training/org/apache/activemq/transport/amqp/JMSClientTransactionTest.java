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


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Message.DEFAULT_PRIORITY;
import javax.jms.Message.DEFAULT_TIME_TO_LIVE;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for transaction behaviors using the JMS client.
 */
public class JMSClientTransactionTest extends JMSClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSClientTransactionTest.class);

    private final int MSG_COUNT = 1000;

    @Test(timeout = 60000)
    public void testProduceOneConsumeOneInTx() throws Exception {
        connection = createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Destination queue = session.createQueue(getTestName());
        MessageProducer messageProducer = session.createProducer(queue);
        messageProducer.send(session.createMessage());
        session.rollback();
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(0, queueView.getQueueSize());
        messageProducer.send(session.createMessage());
        session.commit();
        Assert.assertEquals(1, queueView.getQueueSize());
        MessageConsumer messageConsumer = session.createConsumer(queue);
        Assert.assertNotNull(messageConsumer.receive(5000));
        session.rollback();
        Assert.assertEquals(1, queueView.getQueueSize());
        Assert.assertNotNull(messageConsumer.receive(5000));
        session.commit();
        Assert.assertEquals(0, queueView.getQueueSize());
    }

    @Test(timeout = 60000)
    public void testSingleConsumedMessagePerTxCase() throws Exception {
        connection = createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = session.createQueue(getTestName());
        MessageProducer messageProducer = session.createProducer(queue);
        for (int i = 0; i < (MSG_COUNT); i++) {
            TextMessage message = session.createTextMessage();
            message.setText(("test" + i));
            messageProducer.send(message, PERSISTENT, DEFAULT_PRIORITY, DEFAULT_TIME_TO_LIVE);
        }
        session.close();
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(1000, queueView.getQueueSize());
        int counter = 0;
        session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        do {
            TextMessage message = ((TextMessage) (messageConsumer.receive(1000)));
            if (message != null) {
                counter++;
                JMSClientTransactionTest.LOG.info("Message n. {} with content '{}' has been recieved.", counter, message.getText());
                session.commit();
                JMSClientTransactionTest.LOG.info("Transaction has been committed.");
            }
        } while (counter < (MSG_COUNT) );
        Assert.assertEquals(0, queueView.getQueueSize());
        session.close();
    }

    @Test(timeout = 60000)
    public void testConsumeAllMessagesInSingleTxCase() throws Exception {
        connection = createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination queue = session.createQueue(getTestName());
        MessageProducer messageProducer = session.createProducer(queue);
        for (int i = 0; i < (MSG_COUNT); i++) {
            TextMessage message = session.createTextMessage();
            message.setText(("test" + i));
            messageProducer.send(message, PERSISTENT, DEFAULT_PRIORITY, DEFAULT_TIME_TO_LIVE);
        }
        session.close();
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(1000, queueView.getQueueSize());
        int counter = 0;
        session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        do {
            TextMessage message = ((TextMessage) (messageConsumer.receive(1000)));
            if (message != null) {
                counter++;
                JMSClientTransactionTest.LOG.info("Message n. {} with content '{}' has been recieved.", counter, message.getText());
            }
        } while (counter < (MSG_COUNT) );
        JMSClientTransactionTest.LOG.info("Transaction has been committed.");
        session.commit();
        Assert.assertEquals(0, queueView.getQueueSize());
        session.close();
    }

    @Test(timeout = 60000)
    public void testQueueTXRollbackAndCommit() throws Exception {
        final int MSG_COUNT = 3;
        connection = createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue destination = session.createQueue(getDestinationName());
        MessageProducer producer = session.createProducer(destination);
        MessageConsumer consumer = session.createConsumer(destination);
        for (int i = 1; i <= MSG_COUNT; i++) {
            JMSClientTransactionTest.LOG.info("Sending message: {} to rollback", i);
            TextMessage message = session.createTextMessage(("Rolled back Message: " + i));
            message.setIntProperty("MessageSequence", i);
            producer.send(message);
        }
        session.rollback();
        Assert.assertEquals(0, getProxyToQueue(getDestinationName()).getQueueSize());
        for (int i = 1; i <= MSG_COUNT; i++) {
            JMSClientTransactionTest.LOG.info("Sending message: {} to commit", i);
            TextMessage message = session.createTextMessage(("Commit Message: " + i));
            message.setIntProperty("MessageSequence", i);
            producer.send(message);
        }
        session.commit();
        Assert.assertEquals(MSG_COUNT, getProxyToQueue(getDestinationName()).getQueueSize());
        SubscriptionViewMBean subscription = getProxyToQueueSubscriber(getDestinationName());
        Assert.assertNotNull(subscription);
        for (int i = 1; i <= MSG_COUNT; i++) {
            JMSClientTransactionTest.LOG.info("Trying to receive message: {}", i);
            TextMessage message = ((TextMessage) (consumer.receive(1000)));
            Assert.assertNotNull((("Message " + i) + " should be available"), message);
            Assert.assertEquals(("Should get message: " + i), i, message.getIntProperty("MessageSequence"));
        }
        session.commit();
    }

    @Test(timeout = 60000)
    public void testQueueTXRollbackAndCommitAsyncConsumer() throws Exception {
        final int MSG_COUNT = 3;
        final AtomicInteger counter = new AtomicInteger();
        connection = createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue destination = session.createQueue(getDestinationName());
        MessageProducer producer = session.createProducer(destination);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    JMSClientTransactionTest.LOG.info("Received Message {}", message.getJMSMessageID());
                } catch (JMSException e) {
                }
                counter.incrementAndGet();
            }
        });
        int msgIndex = 0;
        for (int i = 1; i <= MSG_COUNT; i++) {
            JMSClientTransactionTest.LOG.info("Sending message: {} to rollback", (msgIndex++));
            TextMessage message = session.createTextMessage(("Rolled back Message: " + msgIndex));
            message.setIntProperty("MessageSequence", msgIndex);
            producer.send(message);
        }
        JMSClientTransactionTest.LOG.info("ROLLBACK of sent message here:");
        session.rollback();
        Assert.assertEquals(0, getProxyToQueue(getDestinationName()).getQueueSize());
        for (int i = 1; i <= MSG_COUNT; i++) {
            JMSClientTransactionTest.LOG.info("Sending message: {} to commit", (msgIndex++));
            TextMessage message = session.createTextMessage(("Commit Message: " + msgIndex));
            message.setIntProperty("MessageSequence", msgIndex);
            producer.send(message);
        }
        JMSClientTransactionTest.LOG.info("COMMIT of sent message here:");
        session.commit();
        Assert.assertEquals(MSG_COUNT, getProxyToQueue(getDestinationName()).getQueueSize());
        SubscriptionViewMBean subscription = getProxyToQueueSubscriber(getDestinationName());
        Assert.assertNotNull(subscription);
        Assert.assertTrue((("Should read all " + MSG_COUNT) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (counter.get()) == MSG_COUNT;
            }
        }));
        JMSClientTransactionTest.LOG.info("COMMIT of first received batch here:");
        session.commit();
        for (int i = 1; i <= MSG_COUNT; i++) {
            JMSClientTransactionTest.LOG.info("Sending message: {} to commit", (msgIndex++));
            TextMessage message = session.createTextMessage(("Commit Message: " + msgIndex));
            message.setIntProperty("MessageSequence", msgIndex);
            producer.send(message);
        }
        JMSClientTransactionTest.LOG.info("COMMIT of next sent message batch here:");
        session.commit();
        JMSClientTransactionTest.LOG.info("WAITING -> for next three messages to arrive:");
        Assert.assertTrue((("Should read all " + MSG_COUNT) + " messages."), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                JMSClientTransactionTest.LOG.info("Read {} messages so far", counter.get());
                return (counter.get()) == (MSG_COUNT * 2);
            }
        }));
    }

    @Test
    public void testMessageOrderAfterRollback() throws Exception {
        sendMessages(5);
        int counter = 0;
        while ((counter++) < 10) {
            connection = createConnection();
            connection.start();
            Session session = connection.createSession(true, (-1));
            Queue queue = session.createQueue(getDestinationName());
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(5000);
            Assert.assertNotNull(message);
            Assert.assertTrue((message instanceof TextMessage));
            int sequenceID = message.getIntProperty("sequenceID");
            Assert.assertEquals(0, sequenceID);
            JMSClientTransactionTest.LOG.info("Read message = {}", getText());
            session.rollback();
            session.close();
            connection.close();
        } 
    }
}

