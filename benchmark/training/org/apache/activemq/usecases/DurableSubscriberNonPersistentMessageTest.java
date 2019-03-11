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


import DeliveryMode.NON_PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.Date;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DurableSubscriberNonPersistentMessageTest extends TestCase {
    private final Logger LOG = LoggerFactory.getLogger(DurableSubscriberNonPersistentMessageTest.class);

    private String brokerURL;

    private String consumerBrokerURL;

    int initialMaxMsgs = 10;

    int cleanupMsgCount = 10;

    int totalMsgCount = (initialMaxMsgs) + (cleanupMsgCount);

    int totalMsgReceived = 0;

    int sleep = 500;

    int reconnectSleep = 2000;

    int messageTimeout = 1000;

    int messageSize = 1024;

    // Note: If ttl is set 0, the default set by the broker will be used if any
    // setting a value greater than 0 will enable the producer to set the ttl on
    // the message
    long ttl = 0;

    static String clientId = "Jason";

    MBeanServer mbeanServer;

    BrokerService broker;

    /**
     * Create the test case
     *
     * @param testName
     * 		name of the test case
     */
    public DurableSubscriberNonPersistentMessageTest(String testName) {
        super(testName);
    }

    public void testDurableSubscriberNonPersistentMessage() {
        String interest = "TEST";
        LOG.info("Starting DurableSubscriberNonPersistentMessageTest");
        try {
            // create durable topic consumer and disconnect
            createConsumer(interest, 0);
            Thread.sleep(1000);
            // produce 15 messages to topic
            DurableSubscriberNonPersistentMessageTest.Producer producer = new DurableSubscriberNonPersistentMessageTest.Producer(brokerURL, interest, messageSize, ttl);
            producer.sendMessages(totalMsgCount);
            producer.close();
            LOG.info(((totalMsgCount) + " messages sent"));
            // durable topic consumer will consume 10 messages and disconnect
            createConsumer(interest, initialMaxMsgs);
            Thread.sleep(reconnectSleep);
            createConsumer(interest, cleanupMsgCount);
            String brokerVersion = ((String) (mbeanServer.getAttribute(new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker"), "BrokerVersion")));
            LOG.info(("Test run on: " + brokerVersion));
            final String theJmxObject = "org.apache.activemq:type=Broker,brokerName=localhost," + ("endpoint=Consumer,destinationType=Topic,destinationName=TEST,clientId=Jason," + "consumerId=Durable(Jason_MyDurableTopic)");
            TestCase.assertTrue("pendingQueueSize should be zero", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    Integer pendingQueueSize = ((Integer) (mbeanServer.getAttribute(new ObjectName(theJmxObject), "PendingQueueSize")));
                    LOG.info(("pendingQueueSize = " + pendingQueueSize));
                    return (pendingQueueSize.intValue()) == 0;
                }
            }));
            TestCase.assertTrue("cursorMemoryUsage should be zero", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    Long cursorMemoryUsage = ((Long) (mbeanServer.getAttribute(new ObjectName(theJmxObject), "CursorMemoryUsage")));
                    LOG.info(("cursorMemoryUsage = " + cursorMemoryUsage));
                    return (cursorMemoryUsage.longValue()) == 0L;
                }
            }));
            // Not sure what the behavior should be here, if the messages
            // expired the received count shouldn't equal total message count
            TestCase.assertTrue(((totalMsgReceived) == ((initialMaxMsgs) + (cleanupMsgCount))));
        } catch (Exception e) {
            LOG.error(("Exception Executing DurableSubscriberNonPersistentMessageTest: " + (getStackTrace(e))));
            TestCase.fail("Should not throw any exceptions");
        }
    }

    public class Producer {
        protected ConnectionFactory factory;

        protected transient Connection connection;

        protected transient Session session;

        protected transient MessageProducer producer;

        protected static final int messageSize = 1024;

        public Producer(String brokerURL, String interest, int messageSize, long ttl) throws JMSException {
            factory = new ActiveMQConnectionFactory(brokerURL);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            producer = session.createProducer(session.createTopic(interest));
            producer.setDeliveryMode(NON_PERSISTENT);
            if (ttl > 0) {
                producer.setTimeToLive(ttl);
            }
        }

        public void close() throws JMSException {
            if ((connection) != null) {
                connection.close();
            }
        }

        protected void sendMessage() throws JMSException {
            TextMessage textMessage = session.createTextMessage("test message");
            producer.send(textMessage);
        }

        protected void sendMessages(int count) throws JMSException {
            for (int i = 0; i < count; i++) {
                TextMessage textMessage = session.createTextMessage(createMessageText(i));
                producer.send(textMessage);
            }
        }

        private String createMessageText(int index) {
            StringBuffer buffer = new StringBuffer(DurableSubscriberNonPersistentMessageTest.Producer.messageSize);
            buffer.append(((("Message: " + index) + " sent at: ") + (new Date())));
            if ((buffer.length()) > (DurableSubscriberNonPersistentMessageTest.Producer.messageSize)) {
                return buffer.substring(0, DurableSubscriberNonPersistentMessageTest.Producer.messageSize);
            }
            for (int i = buffer.length(); i < (DurableSubscriberNonPersistentMessageTest.Producer.messageSize); i++) {
                buffer.append(' ');
            }
            return buffer.toString();
        }

        protected void commitTransaction() throws JMSException {
            session.commit();
        }
    }

    public class Consumer {
        private final ConnectionFactory factory;

        private final ActiveMQConnection connection;

        private final Session session;

        private final MessageConsumer messageConsumer;

        public Consumer(String brokerURL, String interest, String clientId) throws JMSException {
            factory = new ActiveMQConnectionFactory(brokerURL);
            connection = ((ActiveMQConnection) (factory.createConnection()));
            connection.setClientID(clientId);
            connection.start();
            connection.getPrefetchPolicy().setAll(15);
            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(interest);
            messageConsumer = session.createDurableSubscriber(((Topic) (destination)), "MyDurableTopic");
        }

        public void deleteAllMessages() throws JMSException {
            while ((getMessage(500)) != null) {
                // empty queue
            } 
        }

        public Message getMessage(int timeout) throws JMSException {
            return messageConsumer.receive(timeout);
        }

        public void close() throws JMSException {
            if ((messageConsumer) != null) {
                messageConsumer.close();
            }
            if ((session) != null) {
                session.close();
            }
            if ((connection) != null) {
                connection.close();
            }
        }

        public Session getSession() {
            return session;
        }
    }
}

