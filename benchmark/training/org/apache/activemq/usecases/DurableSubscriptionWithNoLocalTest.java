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
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for spec compliance for durable subscriptions that change the noLocal flag.
 */
@RunWith(Parameterized.class)
public class DurableSubscriptionWithNoLocalTest {
    private static final Logger LOG = LoggerFactory.getLogger(DurableSubscriptionWithNoLocalTest.class);

    private final int MSG_COUNT = 10;

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    @Rule
    public TestName name = new TestName();

    private BrokerService brokerService;

    private String connectionUri;

    private ActiveMQConnectionFactory factory;

    private final boolean keepDurableSubsActive;

    public DurableSubscriptionWithNoLocalTest(final boolean keepDurableSubsActive) {
        this.keepDurableSubsActive = keepDurableSubsActive;
    }

    /**
     * Make sure that NoLocal works for connection started/stopped
     *
     * @throws JMSException
     * 		
     */
    @Test(timeout = 60000)
    public void testNoLocalStillWorkWithConnectionRestart() throws Exception {
        ActiveMQConnection connection = null;
        try {
            connection = ((ActiveMQConnection) (factory.createConnection()));
            connection.setClientID("test-client");
            connection.start();
            test(connection, "test message 1");
            connection.stop();
            connection.start();
            test(connection, "test message 2");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Make sure that NoLocal works for multiple connections to the same subscription
     *
     * @throws JMSException
     * 		
     */
    @Test(timeout = 60000)
    public void testNoLocalStillWorksNewConnection() throws Exception {
        ActiveMQConnection connection = null;
        try {
            connection = ((ActiveMQConnection) (factory.createConnection()));
            connection.setClientID("test-client");
            connection.start();
            test(connection, "test message 1");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        try {
            connection = ((ActiveMQConnection) (factory.createConnection()));
            connection.setClientID("test-client");
            connection.start();
            test(connection, "test message 2");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Make sure that NoLocal works after restart
     *
     * @throws JMSException
     * 		
     */
    @Test(timeout = 60000)
    public void testNoLocalStillWorksRestartBroker() throws Exception {
        ActiveMQConnection connection = null;
        try {
            connection = ((ActiveMQConnection) (factory.createConnection()));
            connection.setClientID("test-client");
            connection.start();
            test(connection, "test message 1");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        tearDown();
        createBroker(false);
        try {
            connection = ((ActiveMQConnection) (factory.createConnection()));
            connection.setClientID("test-client");
            connection.start();
            test(connection, "test message 2");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Test(timeout = 60000)
    public void testDurableSubWithNoLocalChange() throws Exception {
        TopicConnection connection = factory.createTopicConnection();
        connection.setClientID(getClientId());
        connection.start();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(getDestinationName());
        TopicPublisher publisher = session.createPublisher(topic);
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal = true");
        TopicSubscriber subscriber = session.createSubscriber(topic);
        TopicSubscriber durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, true);
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from non-durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(subscriber.receive(500));
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=true) subscriber");
        Assert.assertNull(durableSub.receive(500));
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.debug("Close DurableSubscriber with noLocal=true");
        durableSub.close();
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal=false");
        durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, false);
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from reconnected (noLocal=false) subscription");
        Assert.assertNull(durableSub.receive(500));
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=false) durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(durableSub.receive(500));
        }
        // Should be empty now
        Assert.assertNull(durableSub.receive(100));
    }

    @Test(timeout = 60000)
    public void testInvertedDurableSubWithNoLocalChange() throws Exception {
        TopicConnection connection = factory.createTopicConnection();
        connection.setClientID(getClientId());
        connection.start();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(getDestinationName());
        TopicPublisher publisher = session.createPublisher(topic);
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal = true");
        TopicSubscriber subscriber = session.createSubscriber(topic);
        TopicSubscriber durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, false);
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from non-durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(subscriber.receive(500));
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=false) durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(durableSub.receive(500));
        }
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.debug("Close DurableSubscriber with noLocal=true");
        durableSub.close();
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal=false");
        durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, true);
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from reconnected (noLocal=true) subscription");
        Assert.assertNull(durableSub.receive(500));
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from reconnected (noLocal=true) subscription");
        Assert.assertNull(durableSub.receive(500));
        // Should be empty now
        Assert.assertNull(durableSub.receive(100));
    }

    @Test(timeout = 60000)
    public void testDurableSubWithNoLocalChangeAfterRestart() throws Exception {
        TopicConnection connection = factory.createTopicConnection();
        connection.setClientID(getClientId());
        connection.start();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(getDestinationName());
        TopicPublisher publisher = session.createPublisher(topic);
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal = true");
        TopicSubscriber subscriber = session.createSubscriber(topic);
        TopicSubscriber durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, true);
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from non-durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(subscriber.receive(500));
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=true) subscriber");
        Assert.assertNull(durableSub.receive(500));
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        tearDown();
        createBroker(false);
        connection = factory.createTopicConnection();
        connection.setClientID(getClientId());
        connection.start();
        session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        topic = session.createTopic(getDestinationName());
        publisher = session.createPublisher(topic);
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal=false");
        durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, false);
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from reconnected (noLocal=false) subscription");
        Assert.assertNull(durableSub.receive(500));
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=false) durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(durableSub.receive(500));
        }
        // Should be empty now
        Assert.assertNull(durableSub.receive(100));
    }

    @Test(timeout = 60000)
    public void testInvertedDurableSubWithNoLocalChangeAfterRestart() throws Exception {
        TopicConnection connection = factory.createTopicConnection();
        connection.setClientID(getClientId());
        connection.start();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(getDestinationName());
        TopicPublisher publisher = session.createPublisher(topic);
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal = true");
        TopicSubscriber subscriber = session.createSubscriber(topic);
        TopicSubscriber durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, false);
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from non-durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(subscriber.receive(500));
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=false) durable subscriber");
        for (int i = 0; i < (MSG_COUNT); i++) {
            Assert.assertNotNull(durableSub.receive(500));
        }
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        tearDown();
        createBroker(false);
        connection = factory.createTopicConnection();
        connection.setClientID(getClientId());
        connection.start();
        session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        topic = session.createTopic(getDestinationName());
        publisher = session.createPublisher(topic);
        DurableSubscriptionWithNoLocalTest.LOG.debug("Create DurableSubscriber with noLocal=true");
        durableSub = session.createDurableSubscriber(topic, getSubscriptionName(), null, true);
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=true) subscriber");
        Assert.assertNull(durableSub.receive(500));
        DurableSubscriptionWithNoLocalTest.LOG.debug((("Sending " + (MSG_COUNT)) + " messages to topic"));
        for (int i = 0; i < (MSG_COUNT); i++) {
            publisher.publish(session.createMessage());
        }
        DurableSubscriptionWithNoLocalTest.LOG.info("Attempting to receive messages from (noLocal=true) subscriber");
        Assert.assertNull(durableSub.receive(500));
        // Should be empty now
        Assert.assertNull(durableSub.receive(100));
    }
}

