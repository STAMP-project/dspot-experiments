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
package org.apache.activemq.broker.region.cursors;


import DeliveryMode.NON_PERSISTENT;
import DeliveryMode.PERSISTENT;
import TopicSession.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.org.apache.activemq.broker.region.Topic;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.MessageStoreSubscriptionStatistics;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.util.SubscriptionKey;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test checks that pending message metrics work properly with KahaDB
 *
 * AMQ-5923, AMQ-6375
 */
@RunWith(Parameterized.class)
public class KahaDBPendingMessageCursorTest extends AbstractPendingMessageCursorTest {
    protected static final Logger LOG = LoggerFactory.getLogger(KahaDBPendingMessageCursorTest.class);

    @Rule
    public TemporaryFolder dataFileDir = new TemporaryFolder(new File("target"));

    /**
     *
     *
     * @param prioritizedMessages
     * 		
     */
    public KahaDBPendingMessageCursorTest(final boolean prioritizedMessages, final boolean enableSubscriptionStatistics) {
        super(prioritizedMessages);
        this.enableSubscriptionStatistics = enableSubscriptionStatistics;
    }

    /**
     * Test that the the counter restores size and works after restart and more
     * messages are published
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDurableMessageSizeAfterRestartAndPublish() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Topic topic = publishTestMessagesDurable(connection, new String[]{ "sub1" }, 200, publishedMessageSize, PERSISTENT);
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        // verify the count and size
        verifyPendingStats(topic, subKey, 200, publishedMessageSize.get());
        verifyStoreStats(topic, 200, publishedMessageSize.get());
        // should be equal in this case
        long beforeRestartSize = topic.getDurableTopicSubs().get(subKey).getPendingMessageSize();
        Assert.assertEquals(beforeRestartSize, topic.getMessageStore().getMessageStoreStatistics().getMessageSize().getTotalSize());
        // stop, restart broker and publish more messages
        stopBroker();
        this.setUpBroker(false);
        // verify that after restart the size is the same as before restart on recovery
        topic = ((Topic) (getBroker().getDestination(new ActiveMQTopic(defaultTopicName))));
        Assert.assertEquals(beforeRestartSize, topic.getDurableTopicSubs().get(subKey).getPendingMessageSize());
        connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        topic = publishTestMessagesDurable(connection, new String[]{ "sub1" }, 200, publishedMessageSize, PERSISTENT);
        // verify the count and size
        verifyPendingStats(topic, subKey, 400, publishedMessageSize.get());
        verifyStoreStats(topic, 400, publishedMessageSize.get());
    }

    @Test
    public void testMessageSizeTwoDurablesPartialConsumption() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        SubscriptionKey subKey2 = new SubscriptionKey("clientId", "sub2");
        org.apache.activemq.broker.region.Topic dest = publishTestMessagesDurable(connection, new String[]{ "sub1", "sub2" }, 200, publishedMessageSize, PERSISTENT);
        // verify the count and size - durable is offline so all 200 should be pending since none are in prefetch
        verifyPendingStats(dest, subKey, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
        // consume all messages
        consumeDurableTestMessages(connection, "sub1", 50, publishedMessageSize);
        // 150 should be left
        verifyPendingStats(dest, subKey, 150, publishedMessageSize.get());
        // 200 should be left
        verifyPendingStats(dest, subKey2, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
        connection.close();
    }

    /**
     * Test that the the counter restores size and works after restart and more
     * messages are published
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonPersistentDurableMessageSize() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Topic topic = publishTestMessagesDurable(connection, new String[]{ "sub1" }, 200, publishedMessageSize, NON_PERSISTENT);
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        // verify the count and size
        verifyPendingStats(topic, subKey, 200, publishedMessageSize.get());
        verifyStoreStats(topic, 0, 0);
    }

    /**
     * Test that the subscription counters are properly set when enabled
     * and not set when disabled
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnabledSubscriptionStatistics() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        SubscriptionKey subKey2 = new SubscriptionKey("clientId", "sub2");
        org.apache.activemq.broker.region.Topic dest = publishTestMessagesDurable(connection, new String[]{ "sub1", "sub2" }, 200, publishedMessageSize, PERSISTENT);
        TopicMessageStore store = ((TopicMessageStore) (dest.getMessageStore()));
        MessageStoreSubscriptionStatistics stats = store.getMessageStoreSubStatistics();
        if (enableSubscriptionStatistics) {
            Assert.assertTrue(((stats.getMessageCount(subKey.toString()).getCount()) == 200));
            Assert.assertTrue(((stats.getMessageSize(subKey.toString()).getTotalSize()) > 0));
            Assert.assertTrue(((stats.getMessageCount(subKey2.toString()).getCount()) == 200));
            Assert.assertTrue(((stats.getMessageSize(subKey2.toString()).getTotalSize()) > 0));
            Assert.assertEquals(stats.getMessageCount().getCount(), ((stats.getMessageCount(subKey.toString()).getCount()) + (stats.getMessageSize(subKey.toString()).getCount())));
            Assert.assertEquals(stats.getMessageSize().getTotalSize(), ((stats.getMessageSize(subKey.toString()).getTotalSize()) + (stats.getMessageSize(subKey2.toString()).getTotalSize())));
            // Delete second subscription and verify stats are updated accordingly
            store.deleteSubscription(subKey2.getClientId(), subKey2.getSubscriptionName());
            Assert.assertEquals(stats.getMessageCount().getCount(), stats.getMessageCount(subKey.toString()).getCount());
            Assert.assertEquals(stats.getMessageSize().getTotalSize(), stats.getMessageSize(subKey.toString()).getTotalSize());
            Assert.assertTrue(((stats.getMessageCount(subKey2.toString()).getCount()) == 0));
            Assert.assertTrue(((stats.getMessageSize(subKey2.toString()).getTotalSize()) == 0));
        } else {
            Assert.assertTrue(((stats.getMessageCount(subKey.toString()).getCount()) == 0));
            Assert.assertTrue(((stats.getMessageSize(subKey.toString()).getTotalSize()) == 0));
            Assert.assertTrue(((stats.getMessageCount(subKey2.toString()).getCount()) == 0));
            Assert.assertTrue(((stats.getMessageSize(subKey2.toString()).getTotalSize()) == 0));
            Assert.assertEquals(0, stats.getMessageCount().getCount());
            Assert.assertEquals(0, stats.getMessageSize().getTotalSize());
        }
    }

    @Test
    public void testUpdateMessageSubSize() throws Exception {
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        javax.jms.Topic dest = session.createTopic(defaultTopicName);
        session.createDurableSubscriber(dest, "sub1");
        session.createDurableSubscriber(dest, "sub2");
        MessageProducer prod = session.createProducer(dest);
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText("SmallMessage");
        prod.send(message);
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        SubscriptionKey subKey2 = new SubscriptionKey("clientId", "sub1");
        final Topic topic = ((Topic) (getBroker().getDestination(new ActiveMQTopic(defaultTopicName))));
        final DurableTopicSubscription sub = topic.getDurableTopicSubs().get(subKey);
        final DurableTopicSubscription sub2 = topic.getDurableTopicSubs().get(subKey2);
        long sizeBeforeUpdate = sub.getPendingMessageSize();
        message = ((ActiveMQTextMessage) (topic.getMessageStore().getMessage(message.getMessageId())));
        message.setText("LargerMessageLargerMessage");
        // update the message
        topic.getMessageStore().updateMessage(message);
        // should be at least 10 bytes bigger and match the store size
        Assert.assertTrue(((sub.getPendingMessageSize()) > (sizeBeforeUpdate + 10)));
        Assert.assertEquals(sub.getPendingMessageSize(), topic.getMessageStore().getMessageSize());
        Assert.assertEquals(sub.getPendingMessageSize(), sub2.getPendingMessageSize());
    }

    @Test
    public void testUpdateMessageSubSizeAfterConsume() throws Exception {
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        javax.jms.Topic dest = session.createTopic(defaultTopicName);
        session.createDurableSubscriber(dest, "sub1");
        TopicSubscriber subscriber2 = session.createDurableSubscriber(dest, "sub2");
        MessageProducer prod = session.createProducer(dest);
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText("SmallMessage");
        ActiveMQTextMessage message2 = new ActiveMQTextMessage();
        message2.setText("SmallMessage2");
        prod.send(message);
        prod.send(message2);
        // Receive first message for sub 2 and wait for stats to update
        subscriber2.receive();
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        SubscriptionKey subKey2 = new SubscriptionKey("clientId", "sub2");
        final Topic topic = ((Topic) (getBroker().getDestination(new ActiveMQTopic(defaultTopicName))));
        final DurableTopicSubscription sub = topic.getDurableTopicSubs().get(subKey);
        final DurableTopicSubscription sub2 = topic.getDurableTopicSubs().get(subKey2);
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (sub.getPendingMessageSize()) > (sub2.getPendingMessageSize());
            }
        });
        long sizeBeforeUpdate = sub.getPendingMessageSize();
        long sizeBeforeUpdate2 = sub2.getPendingMessageSize();
        // update message 2
        message = ((ActiveMQTextMessage) (topic.getMessageStore().getMessage(message.getMessageId())));
        message.setText("LargerMessageLargerMessage");
        // update the message
        topic.getMessageStore().updateMessage(message);
        // should be at least 10 bytes bigger and match the store size
        Assert.assertTrue(((sub.getPendingMessageSize()) > (sizeBeforeUpdate + 10)));
        Assert.assertEquals(sub.getPendingMessageSize(), topic.getMessageStore().getMessageSize());
        // Sub2 only has 1 message so should be less than sub, verify that the update message
        // didn't update the stats of sub2 and sub1 should be over twice as large since the
        // updated message is bigger
        Assert.assertTrue(((sub.getPendingMessageSize()) > (2 * (sub2.getPendingMessageSize()))));
        Assert.assertEquals(sizeBeforeUpdate2, sub2.getPendingMessageSize());
    }
}

