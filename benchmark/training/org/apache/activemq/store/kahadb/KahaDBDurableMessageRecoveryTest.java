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
package org.apache.activemq.store.kahadb;


import ActiveMQSession.AUTO_ACKNOWLEDGE;
import ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE;
import java.io.File;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicSubscriber;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class KahaDBDurableMessageRecoveryTest {
    @Rule
    public TemporaryFolder dataFileDir = new TemporaryFolder(new File("target"));

    private BrokerService broker;

    private URI brokerConnectURI;

    private boolean recoverIndex;

    private boolean enableSubscriptionStats;

    /**
     *
     *
     * @param deleteIndex
     * 		
     */
    public KahaDBDurableMessageRecoveryTest(boolean recoverIndex, boolean enableSubscriptionStats) {
        super();
        this.recoverIndex = recoverIndex;
        this.enableSubscriptionStats = enableSubscriptionStats;
    }

    /**
     * Test that on broker restart a durable topic subscription will recover all
     * messages before the "last ack" in KahaDB which could happen if using
     * individual acknowledge mode and skipping messages
     */
    @Test
    public void durableRecoveryIndividualAcknowledge() throws Exception {
        String testTopic = "test.topic";
        Session session = getSession(INDIVIDUAL_ACKNOWLEDGE);
        ActiveMQTopic topic = ((ActiveMQTopic) (session.createTopic(testTopic)));
        MessageProducer producer = session.createProducer(topic);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, "sub1");
        for (int i = 1; i <= 10; i++) {
            producer.send(session.createTextMessage(("msg: " + i)));
        }
        producer.close();
        Assert.assertTrue(Wait.waitFor(() -> 10 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        // Receive only the 5th message using individual ack mode
        for (int i = 1; i <= 10; i++) {
            TextMessage received = ((TextMessage) (subscriber.receive(1000)));
            Assert.assertNotNull(received);
            if (i == 5) {
                received.acknowledge();
            }
        }
        // Verify there are 9 messages left still and restart broker
        Assert.assertTrue(Wait.waitFor(() -> 9 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        subscriber.close();
        restartBroker(recoverIndex);
        // Verify 9 messages exist in store on startup
        Assert.assertTrue(Wait.waitFor(() -> 9 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        // Recreate subscriber and try and receive the other 9 messages
        session = getSession(AUTO_ACKNOWLEDGE);
        subscriber = session.createDurableSubscriber(topic, "sub1");
        for (int i = 1; i <= 4; i++) {
            TextMessage received = ((TextMessage) (subscriber.receive(1000)));
            Assert.assertNotNull(received);
            Assert.assertEquals(("msg: " + i), received.getText());
        }
        for (int i = 6; i <= 10; i++) {
            TextMessage received = ((TextMessage) (subscriber.receive(1000)));
            Assert.assertNotNull(received);
            Assert.assertEquals(("msg: " + i), received.getText());
        }
        subscriber.close();
        Assert.assertTrue(Wait.waitFor(() -> 0 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
    }

    @Test
    public void multipleDurableRecoveryIndividualAcknowledge() throws Exception {
        String testTopic = "test.topic";
        Session session = getSession(INDIVIDUAL_ACKNOWLEDGE);
        ActiveMQTopic topic = ((ActiveMQTopic) (session.createTopic(testTopic)));
        MessageProducer producer = session.createProducer(topic);
        TopicSubscriber subscriber1 = session.createDurableSubscriber(topic, "sub1");
        TopicSubscriber subscriber2 = session.createDurableSubscriber(topic, "sub2");
        for (int i = 1; i <= 10; i++) {
            producer.send(session.createTextMessage(("msg: " + i)));
        }
        producer.close();
        Assert.assertTrue(Wait.waitFor(() -> 10 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        Assert.assertTrue(Wait.waitFor(() -> 10 == (getPendingMessageCount(topic, "clientId1", "sub2")), 3000, 500));
        // Receive 2 messages using individual ack mode only on first sub
        for (int i = 1; i <= 10; i++) {
            TextMessage received = ((TextMessage) (subscriber1.receive(1000)));
            Assert.assertNotNull(received);
            if ((i == 3) || (i == 7)) {
                received.acknowledge();
            }
        }
        // Verify there are 8 messages left still and restart broker
        Assert.assertTrue(Wait.waitFor(() -> 8 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        Assert.assertTrue(Wait.waitFor(() -> 10 == (getPendingMessageCount(topic, "clientId1", "sub2")), 3000, 500));
        // Verify the pending size is less for sub1
        final long sub1PendingSizeBeforeRestart = getPendingMessageSize(topic, "clientId1", "sub1");
        final long sub2PendingSizeBeforeRestart = getPendingMessageSize(topic, "clientId1", "sub2");
        Assert.assertTrue((sub1PendingSizeBeforeRestart > 0));
        Assert.assertTrue((sub2PendingSizeBeforeRestart > 0));
        Assert.assertTrue((sub1PendingSizeBeforeRestart < sub2PendingSizeBeforeRestart));
        subscriber1.close();
        subscriber2.close();
        restartBroker(recoverIndex);
        // Verify 8 messages exist in store on startup on sub 1 and 10 on sub 2
        Assert.assertTrue(Wait.waitFor(() -> 8 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        Assert.assertTrue(Wait.waitFor(() -> 10 == (getPendingMessageCount(topic, "clientId1", "sub2")), 3000, 500));
        // Verify the pending size is less for sub1
        Assert.assertEquals(sub1PendingSizeBeforeRestart, getPendingMessageSize(topic, "clientId1", "sub1"));
        Assert.assertEquals(sub2PendingSizeBeforeRestart, getPendingMessageSize(topic, "clientId1", "sub2"));
        // Recreate subscriber and try and receive the other 8 messages
        session = getSession(AUTO_ACKNOWLEDGE);
        subscriber1 = session.createDurableSubscriber(topic, "sub1");
        subscriber2 = session.createDurableSubscriber(topic, "sub2");
        for (int i = 1; i <= 2; i++) {
            TextMessage received = ((TextMessage) (subscriber1.receive(1000)));
            Assert.assertNotNull(received);
            Assert.assertEquals(("msg: " + i), received.getText());
        }
        for (int i = 4; i <= 6; i++) {
            TextMessage received = ((TextMessage) (subscriber1.receive(1000)));
            Assert.assertNotNull(received);
            Assert.assertEquals(("msg: " + i), received.getText());
        }
        for (int i = 8; i <= 10; i++) {
            TextMessage received = ((TextMessage) (subscriber1.receive(1000)));
            Assert.assertNotNull(received);
            Assert.assertEquals(("msg: " + i), received.getText());
        }
        // Make sure sub 2 gets all 10
        for (int i = 1; i <= 10; i++) {
            TextMessage received = ((TextMessage) (subscriber2.receive(1000)));
            Assert.assertNotNull(received);
            Assert.assertEquals(("msg: " + i), received.getText());
        }
        subscriber1.close();
        subscriber2.close();
        Assert.assertTrue(Wait.waitFor(() -> 0 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        Assert.assertTrue(Wait.waitFor(() -> 0 == (getPendingMessageCount(topic, "clientId1", "sub2")), 3000, 500));
    }

    @Test
    public void multipleDurableTestRecoverSubscription() throws Exception {
        String testTopic = "test.topic";
        Session session = getSession(INDIVIDUAL_ACKNOWLEDGE);
        ActiveMQTopic topic = ((ActiveMQTopic) (session.createTopic(testTopic)));
        MessageProducer producer = session.createProducer(topic);
        TopicSubscriber subscriber1 = session.createDurableSubscriber(topic, "sub1");
        TopicSubscriber subscriber2 = session.createDurableSubscriber(topic, "sub2");
        for (int i = 1; i <= 10; i++) {
            producer.send(session.createTextMessage(("msg: " + i)));
        }
        producer.close();
        // Receive 2 messages using individual ack mode only on first sub
        for (int i = 1; i <= 10; i++) {
            TextMessage received = ((TextMessage) (subscriber1.receive(1000)));
            Assert.assertNotNull(received);
            if ((i == 3) || (i == 7)) {
                received.acknowledge();
            }
        }
        // Verify there are 8 messages left on sub 1 and 10 on sub2 and restart
        Assert.assertTrue(Wait.waitFor(() -> 8 == (getPendingMessageCount(topic, "clientId1", "sub1")), 3000, 500));
        Assert.assertTrue(Wait.waitFor(() -> 10 == (getPendingMessageCount(topic, "clientId1", "sub2")), 3000, 500));
        subscriber1.close();
        subscriber2.close();
        restartBroker(recoverIndex);
        // Manually recover subscription and verify proper messages are loaded
        final Topic brokerTopic = ((Topic) (broker.getDestination(topic)));
        final TopicMessageStore store = ((TopicMessageStore) (brokerTopic.getMessageStore()));
        final AtomicInteger sub1Recovered = new AtomicInteger();
        final AtomicInteger sub2Recovered = new AtomicInteger();
        store.recoverSubscription("clientId1", "sub1", new MessageRecoveryListener() {
            @Override
            public boolean recoverMessageReference(MessageId ref) throws Exception {
                return false;
            }

            @Override
            public boolean recoverMessage(Message message) throws Exception {
                TextMessage textMessage = ((TextMessage) (message));
                if ((textMessage.getText().equals(("msg: " + 3))) || (textMessage.getText().equals(("msg: " + 7)))) {
                    throw new IllegalStateException(("Got wrong message: " + (textMessage.getText())));
                }
                sub1Recovered.incrementAndGet();
                return true;
            }

            @Override
            public boolean isDuplicate(MessageId ref) {
                return false;
            }

            @Override
            public boolean hasSpace() {
                return true;
            }
        });
        store.recoverSubscription("clientId1", "sub2", new MessageRecoveryListener() {
            @Override
            public boolean recoverMessageReference(MessageId ref) throws Exception {
                return false;
            }

            @Override
            public boolean recoverMessage(Message message) throws Exception {
                sub2Recovered.incrementAndGet();
                return true;
            }

            @Override
            public boolean isDuplicate(MessageId ref) {
                return false;
            }

            @Override
            public boolean hasSpace() {
                return true;
            }
        });
        // Verify proper number of messages are recovered
        Assert.assertEquals(8, sub1Recovered.get());
        Assert.assertEquals(10, sub2Recovered.get());
    }
}

