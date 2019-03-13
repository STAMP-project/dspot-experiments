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


import AdvisorySupport.MSG_PROPERTY_CONSUMER_ID;
import AdvisorySupport.MSG_PROPERTY_DISCARDED_COUNT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.FilePendingSubscriberMessageStoragePolicy;
import org.apache.activemq.broker.region.policy.VMPendingSubscriberMessageStoragePolicy;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageEvictionTest {
    static final Logger LOG = LoggerFactory.getLogger(MessageEvictionTest.class);

    private BrokerService broker;

    private ConnectionFactory connectionFactory;

    Connection connection;

    private Session session;

    private Topic destination;

    private final String destinationName = "verifyEvection";

    protected int numMessages = 2000;

    protected String payload = new String(new byte[1024 * 2]);

    @Test
    public void testMessageEvictionMemoryUsageFileCursor() throws Exception {
        setUp(new FilePendingSubscriberMessageStoragePolicy());
        doTestMessageEvictionMemoryUsage();
    }

    @Test
    public void testMessageEvictionMemoryUsageVmCursor() throws Exception {
        setUp(new VMPendingSubscriberMessageStoragePolicy());
        doTestMessageEvictionMemoryUsage();
    }

    @Test
    public void testMessageEvictionDiscardedAdvisory() throws Exception {
        setUp(new VMPendingSubscriberMessageStoragePolicy());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final CountDownLatch consumerRegistered = new CountDownLatch(1);
        final CountDownLatch gotAdvisory = new CountDownLatch(1);
        final CountDownLatch advisoryIsGood = new CountDownLatch(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ActiveMQTopic discardedAdvisoryDestination = AdvisorySupport.getMessageDiscardedAdvisoryTopic(destination);
                    // use separate session rather than asyncDispatch on consumer session
                    // as we want consumer session to block
                    Session advisorySession = connection.createSession(false, AUTO_ACKNOWLEDGE);
                    final MessageConsumer consumer = advisorySession.createConsumer(discardedAdvisoryDestination);
                    consumer.setMessageListener(new MessageListener() {
                        int advisoriesReceived = 0;

                        @Override
                        public void onMessage(Message message) {
                            try {
                                MessageEvictionTest.LOG.info(("advisory:" + message));
                                ActiveMQMessage activeMQMessage = ((ActiveMQMessage) (message));
                                Assert.assertNotNull(activeMQMessage.getStringProperty(MSG_PROPERTY_CONSUMER_ID));
                                Assert.assertEquals((++(advisoriesReceived)), activeMQMessage.getIntProperty(MSG_PROPERTY_DISCARDED_COUNT));
                                message.acknowledge();
                                advisoryIsGood.countDown();
                            } catch (JMSException e) {
                                e.printStackTrace();
                                Assert.fail(e.toString());
                            } finally {
                                gotAdvisory.countDown();
                            }
                        }
                    });
                    consumerRegistered.countDown();
                    gotAdvisory.await(120, TimeUnit.SECONDS);
                    consumer.close();
                    advisorySession.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.toString());
                }
            }
        });
        Assert.assertTrue("we have an advisory consumer", consumerRegistered.await(60, TimeUnit.SECONDS));
        doTestMessageEvictionMemoryUsage();
        Assert.assertTrue("got an advisory for discarded", gotAdvisory.await(0, TimeUnit.SECONDS));
        Assert.assertTrue("advisory is good", advisoryIsGood.await(0, TimeUnit.SECONDS));
    }
}

