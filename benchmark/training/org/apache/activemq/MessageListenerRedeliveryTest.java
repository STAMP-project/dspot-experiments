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


import ActiveMQMessage.DLQ_DELIVERY_FAILURE_CAUSE_PROPERTY;
import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageListenerRedeliveryTest {
    private static final Logger LOG = LoggerFactory.getLogger(MessageListenerRedeliveryTest.class);

    @Rule
    public TestName name = new TestName();

    private Connection connection;

    private class TestMessageListener implements MessageListener {
        public int counter;

        private final Session session;

        public TestMessageListener(Session session) {
            this.session = session;
        }

        @Override
        public void onMessage(Message message) {
            try {
                MessageListenerRedeliveryTest.LOG.info(("Message Received: " + message));
                (counter)++;
                if ((counter) <= 4) {
                    MessageListenerRedeliveryTest.LOG.info("Message Rollback.");
                    session.rollback();
                } else {
                    MessageListenerRedeliveryTest.LOG.info("Message Commit.");
                    message.acknowledge();
                    session.commit();
                }
            } catch (JMSException e) {
                MessageListenerRedeliveryTest.LOG.error("Error when rolling back transaction");
            }
        }
    }

    @Test(timeout = 60000)
    public void testQueueRollbackConsumerListener() throws JMSException {
        connection.start();
        Session session = connection.createSession(true, CLIENT_ACKNOWLEDGE);
        Queue queue = session.createQueue(("queue-" + (getTestName())));
        MessageProducer producer = createProducer(session, queue);
        Message message = createTextMessage(session);
        producer.send(message);
        session.commit();
        MessageConsumer consumer = session.createConsumer(queue);
        ActiveMQMessageConsumer mc = ((ActiveMQMessageConsumer) (consumer));
        mc.setRedeliveryPolicy(getRedeliveryPolicy());
        MessageListenerRedeliveryTest.TestMessageListener listener = new MessageListenerRedeliveryTest.TestMessageListener(session);
        consumer.setMessageListener(listener);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        // first try.. should get 2 since there is no delay on the
        // first redeliver..
        Assert.assertEquals(2, listener.counter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        // 2nd redeliver (redelivery after 1 sec)
        Assert.assertEquals(3, listener.counter);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        // 3rd redeliver (redelivery after 2 seconds) - it should give up after
        // that
        Assert.assertEquals(4, listener.counter);
        // create new message
        producer.send(createTextMessage(session));
        session.commit();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        // it should be committed, so no redelivery
        Assert.assertEquals(5, listener.counter);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        // no redelivery, counter should still be 4
        Assert.assertEquals(5, listener.counter);
        session.close();
    }

    @Test(timeout = 60000)
    public void testQueueRollbackSessionListener() throws JMSException {
        connection.start();
        Session session = connection.createSession(true, CLIENT_ACKNOWLEDGE);
        Queue queue = session.createQueue(("queue-" + (getTestName())));
        MessageProducer producer = createProducer(session, queue);
        Message message = createTextMessage(session);
        producer.send(message);
        session.commit();
        MessageConsumer consumer = session.createConsumer(queue);
        ActiveMQMessageConsumer mc = ((ActiveMQMessageConsumer) (consumer));
        mc.setRedeliveryPolicy(getRedeliveryPolicy());
        MessageListenerRedeliveryTest.TestMessageListener listener = new MessageListenerRedeliveryTest.TestMessageListener(session);
        consumer.setMessageListener(listener);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        // first try
        Assert.assertEquals(2, listener.counter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        // second try (redelivery after 1 sec)
        Assert.assertEquals(3, listener.counter);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        // third try (redelivery after 2 seconds) - it should give up after that
        Assert.assertEquals(4, listener.counter);
        // create new message
        producer.send(createTextMessage(session));
        session.commit();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
        // it should be committed, so no redelivery
        Assert.assertEquals(5, listener.counter);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // ignore
        }
        // no redelivery, counter should still be 4
        Assert.assertEquals(5, listener.counter);
        session.close();
    }

    @Test(timeout = 60000)
    public void testQueueSessionListenerExceptionRetry() throws Exception {
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(("queue-" + (getTestName())));
        MessageProducer producer = createProducer(session, queue);
        Message message = createTextMessage(session, "1");
        producer.send(message);
        message = createTextMessage(session, "2");
        producer.send(message);
        MessageConsumer consumer = session.createConsumer(queue);
        final CountDownLatch gotMessage = new CountDownLatch(2);
        final AtomicInteger count = new AtomicInteger(0);
        final int maxDeliveries = getRedeliveryPolicy().getMaximumRedeliveries();
        final ArrayList<String> received = new ArrayList<String>();
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                MessageListenerRedeliveryTest.LOG.info(("Message Received: " + message));
                try {
                    received.add(getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                    Assert.fail(e.toString());
                }
                if ((count.incrementAndGet()) < maxDeliveries) {
                    throw new RuntimeException(((getTestName()) + " force a redelivery"));
                }
                // new blood
                count.set(0);
                gotMessage.countDown();
            }
        });
        Assert.assertTrue("got message before retry expiry", gotMessage.await(20, TimeUnit.SECONDS));
        for (int i = 0; i < maxDeliveries; i++) {
            Assert.assertEquals(("got first redelivered: " + i), "1", received.get(i));
        }
        for (int i = maxDeliveries; i < (maxDeliveries * 2); i++) {
            Assert.assertEquals(("got first redelivered: " + i), "2", received.get(i));
        }
        session.close();
    }

    @Test(timeout = 60000)
    public void testQueueSessionListenerExceptionDlq() throws Exception {
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(("queue-" + (getTestName())));
        MessageProducer producer = createProducer(session, queue);
        Message message = createTextMessage(session);
        producer.send(message);
        final Message[] dlqMessage = new Message[1];
        ActiveMQDestination dlqDestination = new ActiveMQQueue("ActiveMQ.DLQ");
        MessageConsumer dlqConsumer = session.createConsumer(dlqDestination);
        final CountDownLatch gotDlqMessage = new CountDownLatch(1);
        dlqConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                MessageListenerRedeliveryTest.LOG.info(("DLQ Message Received: " + message));
                dlqMessage[0] = message;
                gotDlqMessage.countDown();
            }
        });
        MessageConsumer consumer = session.createConsumer(queue);
        final int maxDeliveries = getRedeliveryPolicy().getMaximumRedeliveries();
        final CountDownLatch gotMessage = new CountDownLatch(maxDeliveries);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                MessageListenerRedeliveryTest.LOG.info(("Message Received: " + message));
                gotMessage.countDown();
                throw new RuntimeException(((getTestName()) + " force a redelivery"));
            }
        });
        Assert.assertTrue("got message before retry expiry", gotMessage.await(20, TimeUnit.SECONDS));
        // check DLQ
        Assert.assertTrue("got dlq message", gotDlqMessage.await(20, TimeUnit.SECONDS));
        // check DLQ message cause is captured
        message = dlqMessage[0];
        Assert.assertNotNull("dlq message captured", message);
        String cause = message.getStringProperty(DLQ_DELIVERY_FAILURE_CAUSE_PROPERTY);
        MessageListenerRedeliveryTest.LOG.info("DLQ'd message cause reported as: {}", cause);
        Assert.assertTrue("cause 'cause' exception is remembered", cause.contains("RuntimeException"));
        Assert.assertTrue("is correct exception", cause.contains(getTestName()));
        Assert.assertTrue("cause exception is remembered", cause.contains("Throwable"));
        Assert.assertTrue("cause policy is remembered", cause.contains("RedeliveryPolicy"));
        Assert.assertTrue("cause redelivered count is remembered", cause.contains((("[" + (maxDeliveries + 1)) + "]")));
        session.close();
    }

    @Test(timeout = 60000)
    public void testTransactedQueueSessionListenerExceptionDlq() throws Exception {
        connection.start();
        final Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue queue = session.createQueue(("queue-" + (getTestName())));
        MessageProducer producer = createProducer(session, queue);
        Message message = createTextMessage(session);
        producer.send(message);
        session.commit();
        final Message[] dlqMessage = new Message[1];
        ActiveMQDestination dlqDestination = new ActiveMQQueue("ActiveMQ.DLQ");
        MessageConsumer dlqConsumer = session.createConsumer(dlqDestination);
        final CountDownLatch gotDlqMessage = new CountDownLatch(1);
        dlqConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                MessageListenerRedeliveryTest.LOG.info(("DLQ Message Received: " + message));
                dlqMessage[0] = message;
                gotDlqMessage.countDown();
            }
        });
        MessageConsumer consumer = session.createConsumer(queue);
        final int maxDeliveries = getRedeliveryPolicy().getMaximumRedeliveries();
        final CountDownLatch gotMessage = new CountDownLatch(maxDeliveries);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                MessageListenerRedeliveryTest.LOG.info(("Message Received: " + message));
                gotMessage.countDown();
                try {
                    session.rollback();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException(((getTestName()) + " force a redelivery"));
            }
        });
        Assert.assertTrue("got message before retry expiry", gotMessage.await(20, TimeUnit.SECONDS));
        // check DLQ
        Assert.assertTrue("got dlq message", gotDlqMessage.await(20, TimeUnit.SECONDS));
        // check DLQ message cause is captured
        message = dlqMessage[0];
        Assert.assertNotNull("dlq message captured", message);
        String cause = message.getStringProperty(DLQ_DELIVERY_FAILURE_CAUSE_PROPERTY);
        MessageListenerRedeliveryTest.LOG.info("DLQ'd message cause reported as: {}", cause);
        Assert.assertTrue("cause 'cause' exception is remembered", cause.contains("RuntimeException"));
        Assert.assertTrue("is correct exception", cause.contains(getTestName()));
        Assert.assertTrue("cause exception is remembered", cause.contains("Throwable"));
        Assert.assertTrue("cause policy is remembered", cause.contains("RedeliveryPolicy"));
        session.close();
    }
}

