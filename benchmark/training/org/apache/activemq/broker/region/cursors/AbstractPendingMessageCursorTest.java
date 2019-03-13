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
import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.org.apache.activemq.broker.region.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.AbstractStoreStatTestSupport;
import org.apache.activemq.util.SubscriptionKey;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test checks that KahaDB properly sets the new storeMessageSize statistic.
 *
 * AMQ-5748
 */
public abstract class AbstractPendingMessageCursorTest extends AbstractStoreStatTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractPendingMessageCursorTest.class);

    protected BrokerService broker;

    protected URI brokerConnectURI;

    protected String defaultQueueName = "test.queue";

    protected String defaultTopicName = "test.topic";

    protected static int maxMessageSize = 1000;

    protected final boolean prioritizedMessages;

    protected boolean enableSubscriptionStatistics;

    @Rule
    public Timeout globalTimeout = new Timeout(60, TimeUnit.SECONDS);

    /**
     *
     *
     * @param prioritizedMessages
     * 		
     */
    public AbstractPendingMessageCursorTest(final boolean prioritizedMessages) {
        super();
        this.prioritizedMessages = prioritizedMessages;
    }

    @Test
    public void testQueueMessageSize() throws Exception {
        // doesn't apply to queues, only run once
        Assume.assumeFalse(enableSubscriptionStatistics);
        AtomicLong publishedMessageSize = new AtomicLong();
        org.apache.activemq.broker.region.Queue dest = publishTestQueueMessages(200, publishedMessageSize);
        verifyPendingStats(dest, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
    }

    @Test
    public void testQueueBrowserMessageSize() throws Exception {
        // doesn't apply to queues, only run once
        Assume.assumeFalse(enableSubscriptionStatistics);
        AtomicLong publishedMessageSize = new AtomicLong();
        org.apache.activemq.broker.region.Queue dest = publishTestQueueMessages(200, publishedMessageSize);
        browseTestQueueMessages(dest.getName());
        verifyPendingStats(dest, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
    }

    @Test
    public void testQueueMessageSizeNonPersistent() throws Exception {
        // doesn't apply to queues, only run once
        Assume.assumeFalse(enableSubscriptionStatistics);
        AtomicLong publishedMessageSize = new AtomicLong();
        org.apache.activemq.broker.region.Queue dest = publishTestQueueMessages(200, NON_PERSISTENT, publishedMessageSize);
        verifyPendingStats(dest, 200, publishedMessageSize.get());
    }

    @Test
    public void testQueueMessageSizePersistentAndNonPersistent() throws Exception {
        // doesn't apply to queues, only run once
        Assume.assumeFalse(enableSubscriptionStatistics);
        AtomicLong publishedNonPersistentMessageSize = new AtomicLong();
        AtomicLong publishedMessageSize = new AtomicLong();
        org.apache.activemq.broker.region.Queue dest = publishTestQueueMessages(100, PERSISTENT, publishedMessageSize);
        dest = publishTestQueueMessages(100, NON_PERSISTENT, publishedNonPersistentMessageSize);
        verifyPendingStats(dest, 200, ((publishedMessageSize.get()) + (publishedNonPersistentMessageSize.get())));
        verifyStoreStats(dest, 100, publishedMessageSize.get());
    }

    @Test
    public void testQueueMessageSizeAfterConsumption() throws Exception {
        // doesn't apply to queues, only run once
        Assume.assumeFalse(enableSubscriptionStatistics);
        AtomicLong publishedMessageSize = new AtomicLong();
        org.apache.activemq.broker.region.Queue dest = publishTestQueueMessages(200, publishedMessageSize);
        verifyPendingStats(dest, 200, publishedMessageSize.get());
        consumeTestQueueMessages();
        verifyPendingStats(dest, 0, 0);
        verifyStoreStats(dest, 0, 0);
    }

    @Test
    public void testQueueMessageSizeAfterConsumptionNonPersistent() throws Exception {
        // doesn't apply to queues, only run once
        Assume.assumeFalse(enableSubscriptionStatistics);
        AtomicLong publishedMessageSize = new AtomicLong();
        org.apache.activemq.broker.region.Queue dest = publishTestQueueMessages(200, NON_PERSISTENT, publishedMessageSize);
        verifyPendingStats(dest, 200, publishedMessageSize.get());
        consumeTestQueueMessages();
        verifyPendingStats(dest, 0, 0);
        verifyStoreStats(dest, 0, 0);
    }

    @Test
    public void testTopicMessageSize() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(new ActiveMQTopic(this.defaultTopicName));
        org.apache.activemq.broker.region.Topic dest = publishTestTopicMessages(200, publishedMessageSize);
        // verify the count and size - there is a prefetch of 100 so only 100 are pending and 100
        // are dispatched because we have an active consumer online
        // verify that the size is greater than 100 messages times the minimum size of 100
        verifyPendingStats(dest, 100, (100 * 100));
        // consume all messages
        consumeTestMessages(consumer, 200);
        // All messages should now be gone
        verifyPendingStats(dest, 0, 0);
        connection.close();
    }

    @Test
    public void testTopicNonPersistentMessageSize() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(new ActiveMQTopic(this.defaultTopicName));
        org.apache.activemq.broker.region.Topic dest = publishTestTopicMessages(200, NON_PERSISTENT, publishedMessageSize);
        // verify the count and size - there is a prefetch of 100 so only 100 are pending and 100
        // are dispatched because we have an active consumer online
        // verify the size is at least as big as 100 messages times the minimum of 100 size
        verifyPendingStats(dest, 100, (100 * 100));
        // consume all messages
        consumeTestMessages(consumer, 200);
        // All messages should now be gone
        verifyPendingStats(dest, 0, 0);
        connection.close();
    }

    @Test
    public void testTopicPersistentAndNonPersistentMessageSize() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(new ActiveMQTopic(this.defaultTopicName));
        org.apache.activemq.broker.region.Topic dest = publishTestTopicMessages(100, NON_PERSISTENT, publishedMessageSize);
        dest = publishTestTopicMessages(100, PERSISTENT, publishedMessageSize);
        // verify the count and size - there is a prefetch of 100 so only 100 are pending and 100
        // are dispatched because we have an active consumer online
        // verify the size is at least as big as 100 messages times the minimum of 100 size
        verifyPendingStats(dest, 100, (100 * 100));
        // consume all messages
        consumeTestMessages(consumer, 200);
        // All messages should now be gone
        verifyPendingStats(dest, 0, 0);
        connection.close();
    }

    @Test
    public void testMessageSizeOneDurable() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        org.apache.activemq.broker.region.Topic dest = publishTestMessagesDurable(connection, new String[]{ "sub1" }, 200, publishedMessageSize, PERSISTENT);
        // verify the count and size - durable is offline so all 200 should be pending since none are in prefetch
        verifyPendingStats(dest, subKey, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
        // should be equal in this case
        Assert.assertEquals(dest.getDurableTopicSubs().get(subKey).getPendingMessageSize(), dest.getMessageStore().getMessageStoreStatistics().getMessageSize().getTotalSize());
        // consume all messages
        consumeDurableTestMessages(connection, "sub1", 200, publishedMessageSize);
        // All messages should now be gone
        verifyPendingStats(dest, subKey, 0, 0);
        verifyStoreStats(dest, 0, 0);
        connection.close();
    }

    @Test
    public void testMessageSizeOneDurablePartialConsumption() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        org.apache.activemq.broker.region.Topic dest = publishTestMessagesDurable(connection, new String[]{ "sub1" }, 200, publishedMessageSize, PERSISTENT);
        // verify the count and size - durable is offline so all 200 should be pending since none are in prefetch
        verifyPendingStats(dest, subKey, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
        // consume all messages
        consumeDurableTestMessages(connection, "sub1", 50, publishedMessageSize);
        // 150 should be left
        verifyPendingStats(dest, subKey, 150, publishedMessageSize.get());
        verifyStoreStats(dest, 150, publishedMessageSize.get());
        connection.close();
    }

    @Test
    public void testMessageSizeTwoDurables() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        org.apache.activemq.broker.region.Topic dest = publishTestMessagesDurable(connection, new String[]{ "sub1", "sub2" }, 200, publishedMessageSize, PERSISTENT);
        // verify the count and size
        SubscriptionKey subKey = new SubscriptionKey("clientId", "sub1");
        verifyPendingStats(dest, subKey, 200, publishedMessageSize.get());
        // consume messages just for sub1
        consumeDurableTestMessages(connection, "sub1", 200, publishedMessageSize);
        // There is still a durable that hasn't consumed so the messages should exist
        SubscriptionKey subKey2 = new SubscriptionKey("clientId", "sub2");
        verifyPendingStats(dest, subKey, 0, 0);
        verifyPendingStats(dest, subKey2, 200, publishedMessageSize.get());
        verifyStoreStats(dest, 200, publishedMessageSize.get());
        connection.stop();
    }

    protected static interface MessageSizeCalculator {
        long getMessageSize() throws Exception;
    }
}

