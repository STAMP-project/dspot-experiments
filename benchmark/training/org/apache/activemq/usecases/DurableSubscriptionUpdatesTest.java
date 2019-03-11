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
import java.net.URI;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the durable sub updates when the offline sub is reactivated with new values.
 */
public class DurableSubscriptionUpdatesTest {
    private static final Logger LOG = LoggerFactory.getLogger(DurableSubscriptionUpdatesTest.class);

    private final int MSG_COUNT = 5;

    private BrokerService brokerService;

    private URI connectionUri;

    private String clientId;

    private String subscriptionName;

    private String topicName;

    private TopicConnection connection;

    @Rule
    public TestName name = new TestName();

    @Test(timeout = 60000)
    public void testSelectorChange() throws Exception {
        connection = createConnection();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        // Create a Durable Topic Subscription with noLocal set to true.
        TopicSubscriber durableSubscriber = session.createDurableSubscriber(topic, subscriptionName, "JMSPriority > 8", false);
        // Public first set, only the non durable sub should get these.
        publishToTopic(session, topic, 9);
        publishToTopic(session, topic, 8);
        // Standard subscriber should receive them
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = durableSubscriber.receive(2000);
            Assert.assertNotNull(message);
            Assert.assertEquals(9, message.getJMSPriority());
        }
        // Subscriber should not receive the others.
        {
            Message message = durableSubscriber.receive(500);
            Assert.assertNull(message);
        }
        // Public second set for testing durable sub changed.
        publishToTopic(session, topic, 9);
        Assert.assertEquals(1, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Durable now goes inactive.
        durableSubscriber.close();
        Assert.assertTrue("Should have no durables.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 0;
            }
        }));
        Assert.assertTrue("Should have an inactive sub.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getInactiveDurableTopicSubscribers().length) == 1;
            }
        }));
        DurableSubscriptionUpdatesTest.LOG.debug("Testing that updated selector subscription does get any messages.");
        // Recreate a Durable Topic Subscription with noLocal set to false.
        durableSubscriber = session.createDurableSubscriber(topic, subscriptionName, "JMSPriority > 7", false);
        Assert.assertEquals(1, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Durable subscription should not receive them as the subscriptions should
        // have been removed and recreated to update the noLocal flag.
        {
            Message message = durableSubscriber.receive(500);
            Assert.assertNull(message);
        }
        // Public third set which should get queued for the durable sub with noLocal=false
        publishToTopic(session, topic, 8);
        // Durable subscriber should receive them
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = durableSubscriber.receive(5000);
            Assert.assertNotNull("Should get messages now", message);
            Assert.assertEquals(8, message.getJMSPriority());
        }
    }

    @Test(timeout = 60000)
    public void testResubscribeWithNewNoLocalValueNoBrokerRestart() throws Exception {
        connection = createConnection();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        // Create a Durable Topic Subscription with noLocal set to true.
        TopicSubscriber durableSubscriber = session.createDurableSubscriber(topic, subscriptionName, null, true);
        // Create a Durable Topic Subscription with noLocal set to true.
        TopicSubscriber nonDurableSubscriber = session.createSubscriber(topic);
        // Public first set, only the non durable sub should get these.
        publishToTopic(session, topic);
        DurableSubscriptionUpdatesTest.LOG.debug("Testing that noLocal=true subscription doesn't get any messages.");
        // Standard subscriber should receive them
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = nonDurableSubscriber.receive(2000);
            Assert.assertNotNull(message);
        }
        // Durable noLocal=true subscription should not receive them
        {
            Message message = durableSubscriber.receive(500);
            Assert.assertNull(message);
        }
        // Public second set for testing durable sub changed.
        publishToTopic(session, topic);
        Assert.assertEquals(1, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Durable now goes inactive.
        durableSubscriber.close();
        Assert.assertTrue("Should have no durables.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 0;
            }
        }));
        Assert.assertTrue("Should have an inactive sub.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getInactiveDurableTopicSubscribers().length) == 1;
            }
        }));
        DurableSubscriptionUpdatesTest.LOG.debug("Testing that updated noLocal=false subscription does get any messages.");
        // Recreate a Durable Topic Subscription with noLocal set to false.
        durableSubscriber = session.createDurableSubscriber(topic, subscriptionName, null, false);
        Assert.assertEquals(1, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Durable noLocal=false subscription should not receive them as the subscriptions should
        // have been removed and recreated to update the noLocal flag.
        {
            Message message = durableSubscriber.receive(500);
            Assert.assertNull(message);
        }
        // Public third set which should get queued for the durable sub with noLocal=false
        publishToTopic(session, topic);
        // Durable subscriber should receive them
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = durableSubscriber.receive(5000);
            Assert.assertNotNull("Should get local messages now", message);
        }
    }

    @Test(timeout = 60000)
    public void testDurableResubscribeWithNewNoLocalValueWithBrokerRestart() throws Exception {
        connection = createConnection();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        // Create a Durable Topic Subscription with noLocal set to true.
        TopicSubscriber durableSubscriber = session.createDurableSubscriber(topic, subscriptionName, null, true);
        // Create a Durable Topic Subscription with noLocal set to true.
        TopicSubscriber nonDurableSubscriber = session.createSubscriber(topic);
        // Public first set, only the non durable sub should get these.
        publishToTopic(session, topic);
        DurableSubscriptionUpdatesTest.LOG.debug("Testing that noLocal=true subscription doesn't get any messages.");
        // Standard subscriber should receive them
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = nonDurableSubscriber.receive(2000);
            Assert.assertNotNull(message);
        }
        // Durable noLocal=true subscription should not receive them
        {
            Message message = durableSubscriber.receive(500);
            Assert.assertNull(message);
        }
        // Public second set for testing durable sub changed.
        publishToTopic(session, topic);
        Assert.assertEquals(1, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Durable now goes inactive.
        durableSubscriber.close();
        Assert.assertTrue("Should have no durables.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 0;
            }
        }));
        Assert.assertTrue("Should have an inactive sub.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getInactiveDurableTopicSubscribers().length) == 1;
            }
        }));
        DurableSubscriptionUpdatesTest.LOG.debug("Testing that updated noLocal=false subscription does get any messages.");
        connection.close();
        restartBroker();
        connection = createConnection();
        session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        // The previous subscription should be restored as an offline subscription.
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Recreate a Durable Topic Subscription with noLocal set to false.
        durableSubscriber = session.createDurableSubscriber(topic, subscriptionName, null, false);
        Assert.assertEquals(1, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        // Durable noLocal=false subscription should not receive them as the subscriptions should
        // have been removed and recreated to update the noLocal flag.
        {
            Message message = durableSubscriber.receive(500);
            Assert.assertNull(message);
        }
        // Public third set which should get queued for the durable sub with noLocal=false
        publishToTopic(session, topic);
        // Durable subscriber should receive them
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = durableSubscriber.receive(2000);
            Assert.assertNotNull("Should get local messages now", message);
        }
    }
}

