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
package org.apache.activemq.bugs;


import DeliveryMode.NON_PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test validates that the AMQ consumer blocks on redelivery of a message,
 * through all redeliveries, until the message is either successfully consumed
 * or sent to the DLQ.
 */
public class AMQ1853Test {
    private static BrokerService broker;

    private static final Logger LOG = LoggerFactory.getLogger(AMQ1853Test.class);

    static final String jmsConnectionURI = "failover:(vm://localhost)";

    // Virtual Topic that the test publishes 10 messages to
    private static final String queueFail = "Queue.BlockingConsumer.QueueFail";

    // Number of messages
    private final int producerMessages = 5;

    private final int totalNumberMessages = (producerMessages) * 2;

    private final int maxRedeliveries = 2;

    private final int redeliveryDelay = 1000;

    private Map<String, AtomicInteger> messageList = null;

    @Test
    public void testConsumerMessagesAreNotOrdered() throws Exception {
        AMQ1853Test.TestConsumer consumerAllFail = null;
        messageList = new Hashtable<String, AtomicInteger>();
        try {
            // The first 2 consumers will rollback, ultimately causing messages to land on the DLQ
            AMQ1853Test.TestProducer producerAllFail = new AMQ1853Test.TestProducer(AMQ1853Test.queueFail);
            AMQ1853Test.thread(producerAllFail, false);
            consumerAllFail = new AMQ1853Test.TestConsumer(AMQ1853Test.queueFail, true);
            AMQ1853Test.thread(consumerAllFail, false);
            // Give the consumers a second to start
            Thread.sleep(1000);
            AMQ1853Test.thread(producerAllFail, false);
            // Give the consumers a second to start
            Thread.sleep(1000);
            producerAllFail.getLatch().await();
            AMQ1853Test.LOG.info(("producer successful, count = " + (producerAllFail.getLatch().getCount())));
            AMQ1853Test.LOG.info(("final message list size =  " + (messageList.size())));
            Assert.assertTrue(((("message list size =  " + (messageList.size())) + " exptected:") + (totalNumberMessages)), Wait.waitFor(new Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (totalNumberMessages) == (messageList.size());
                }
            }));
            consumerAllFail.getLatch().await();
            AMQ1853Test.LOG.info(("consumerAllFail successful, count = " + (consumerAllFail.getLatch().getCount())));
            Iterator<String> keys = messageList.keySet().iterator();
            for (AtomicInteger counter : messageList.values()) {
                String message = keys.next();
                AMQ1853Test.LOG.info(((("final count for message " + message) + " counter =  ") + (counter.get())));
                Assert.assertTrue(((("for message " + message) + " counter =  ") + (counter.get())), ((counter.get()) == ((maxRedeliveries) + 1)));
            }
            Assert.assertFalse(consumerAllFail.messageReceiptIsOrdered());
        } finally {
            if (consumerAllFail != null) {
                consumerAllFail.setStop(true);
            }
        }
    }

    private class TestProducer implements Runnable {
        private CountDownLatch latch = null;

        private String destinationName = null;

        public TestProducer(String destinationName) {
            this.destinationName = destinationName;
            // We run the producer 2 times
            latch = new CountDownLatch(totalNumberMessages);
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public void run() {
            ActiveMQConnectionFactory connectionFactory = null;
            ActiveMQConnection connection = null;
            ActiveMQSession session = null;
            Destination destination = null;
            try {
                AMQ1853Test.LOG.info((("Started TestProducer for destination (" + (destinationName)) + ")"));
                connectionFactory = new ActiveMQConnectionFactory(AMQ1853Test.jmsConnectionURI);
                connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
                connection.setCopyMessageOnSend(false);
                connection.start();
                session = ((ActiveMQSession) (connection.createSession(false, AUTO_ACKNOWLEDGE)));
                destination = session.createQueue(this.destinationName);
                // Create a MessageProducer from the Session to the Topic or Queue
                ActiveMQMessageProducer producer = ((ActiveMQMessageProducer) (session.createProducer(destination)));
                producer.setDeliveryMode(NON_PERSISTENT);
                for (int i = 0; i < (producerMessages); i++) {
                    TextMessage message = ((TextMessage) (session.createTextMessage()));
                    message.setLongProperty("TestTime", System.currentTimeMillis());
                    try {
                        producer.send(message);
                        AMQ1853Test.LOG.info((((("Producer (" + (destinationName)) + ")\n") + (message.getJMSMessageID())) + " = sent messageId\n"));
                        latch.countDown();
                        AMQ1853Test.LOG.info((" Latch count  " + (latch.getCount())));
                        AMQ1853Test.LOG.info(("Producer message list size = " + (messageList.keySet().size())));
                        messageList.put(message.getJMSMessageID(), new AtomicInteger(0));
                        AMQ1853Test.LOG.info(("Producer message list size = " + (messageList.keySet().size())));
                    } catch (Exception deeperException) {
                        AMQ1853Test.LOG.info(((("Producer for destination (" + (destinationName)) + ") Caught: ") + deeperException));
                    }
                    Thread.sleep(1000);
                }
                AMQ1853Test.LOG.info((("Finished TestProducer for destination (" + (destinationName)) + ")"));
            } catch (Exception e) {
                AMQ1853Test.LOG.error(((("Terminating TestProducer(" + (destinationName)) + ")Caught: ") + e));
            } finally {
                try {
                    if (session != null) {
                        session.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    AMQ1853Test.LOG.error(((("Closing connection/session (" + (destinationName)) + ")Caught: ") + e));
                }
            }
        }
    }

    private class TestConsumer implements Runnable , ExceptionListener , MessageListener {
        private CountDownLatch latch = null;

        private int receivedMessageCounter = 0;

        private boolean bFakeFail = false;

        String destinationName = null;

        boolean bMessageReceiptIsOrdered = true;

        boolean bStop = false;

        String previousMessageId = null;

        private ActiveMQConnectionFactory connectionFactory = null;

        private ActiveMQConnection connection = null;

        private Session session = null;

        private MessageConsumer consumer = null;

        public TestConsumer(String destinationName, boolean bFakeFail) {
            this.bFakeFail = bFakeFail;
            latch = new CountDownLatch(((totalNumberMessages) * (this.bFakeFail ? (maxRedeliveries) + 1 : 1)));
            this.destinationName = destinationName;
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public boolean messageReceiptIsOrdered() {
            return bMessageReceiptIsOrdered;
        }

        public void run() {
            try {
                AMQ1853Test.LOG.info((("Started TestConsumer for destination (" + (destinationName)) + ")"));
                connectionFactory = new ActiveMQConnectionFactory(AMQ1853Test.jmsConnectionURI);
                connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
                connection.setNonBlockingRedelivery(true);
                session = connection.createSession(true, SESSION_TRANSACTED);
                RedeliveryPolicy policy = connection.getRedeliveryPolicy();
                policy.setInitialRedeliveryDelay(redeliveryDelay);
                policy.setBackOffMultiplier((-1));
                policy.setRedeliveryDelay(redeliveryDelay);
                policy.setMaximumRedeliveryDelay((-1));
                policy.setUseExponentialBackOff(false);
                policy.setMaximumRedeliveries(maxRedeliveries);
                connection.setExceptionListener(this);
                Destination destination = session.createQueue(destinationName);
                consumer = session.createConsumer(destination);
                consumer.setMessageListener(this);
                connection.start();
                while (!(bStop)) {
                    Thread.sleep(100);
                } 
                AMQ1853Test.LOG.info(((((("Finished TestConsumer for destination name (" + (destinationName)) + ") remaining ") + (this.latch.getCount())) + " messages ") + (this.toString())));
            } catch (Exception e) {
                AMQ1853Test.LOG.error(((("Consumer (" + (destinationName)) + ") Caught: ") + e));
            } finally {
                try {
                    if ((consumer) != null) {
                        consumer.close();
                    }
                    if ((session) != null) {
                        session.close();
                    }
                    if ((connection) != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    AMQ1853Test.LOG.error(((("Closing connection/session (" + (destinationName)) + ")Caught: ") + e));
                }
            }
        }

        public synchronized void onException(JMSException ex) {
            AMQ1853Test.LOG.error((("Consumer for destination, (" + (destinationName)) + "), JMS Exception occured.  Shutting down client."));
        }

        public synchronized void setStop(boolean bStop) {
            this.bStop = bStop;
        }

        public synchronized void onMessage(Message message) {
            (receivedMessageCounter)++;
            latch.countDown();
            AMQ1853Test.LOG.info(((((("Consumer for destination (" + (destinationName)) + ") latch countdown: ") + (latch.getCount())) + " :: Number messages received ") + (this.receivedMessageCounter)));
            try {
                if (((receivedMessageCounter) % ((maxRedeliveries) + 1)) == 1) {
                    previousMessageId = message.getJMSMessageID();
                }
                if (bMessageReceiptIsOrdered) {
                    bMessageReceiptIsOrdered = previousMessageId.trim().equals(message.getJMSMessageID());
                }
                final String jmsMessageId = message.getJMSMessageID();
                Assert.assertTrue("Did not find expected ", Wait.waitFor(new Wait.Condition() {
                    @Override
                    public boolean isSatisified() throws Exception {
                        return messageList.containsKey(jmsMessageId);
                    }
                }));
                AtomicInteger counter = messageList.get(jmsMessageId);
                counter.incrementAndGet();
                AMQ1853Test.LOG.info(((((((((((((("Consumer for destination (" + (destinationName)) + ")\n") + (message.getJMSMessageID())) + " = currentMessageId\n") + (previousMessageId)) + " = previousMessageId\n") + (bMessageReceiptIsOrdered)) + "= bMessageReceiptIsOrdered\n") + ">>LATENCY ") + ((System.currentTimeMillis()) - (message.getLongProperty("TestTime")))) + "\n") + "message counter = ") + (counter.get())));
                if (!(bFakeFail)) {
                    AMQ1853Test.LOG.debug(((("Consumer on destination " + (destinationName)) + " committing JMS Session for message: ") + (message.toString())));
                    session.commit();
                } else {
                    AMQ1853Test.LOG.debug(((("Consumer on destination " + (destinationName)) + " rolling back JMS Session for message: ") + (message.toString())));
                    session.rollback();// rolls back all the consumed messages on the session to

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                AMQ1853Test.LOG.error((("Error reading JMS Message from destination " + (destinationName)) + "."));
            }
        }
    }
}

