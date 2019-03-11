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


import javax.jms.Queue;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ6387Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6387Test.class);

    private final String QUEUE_NAME = "testQueue";

    private final String TOPIC_NAME = "testTopic";

    private final String SUBSCRIPTION_NAME = "subscriberId";

    private final String CLIENT_ID = "client1";

    private final int MSG_COUNT = 150;

    private ActiveMQConnectionFactory connectionFactory;

    private BrokerService brokerService;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testQueueMessagesKeptAfterDelivery() throws Exception {
        createDurableSubscription();
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        sendBytesMessage(Queue.class);
        logBrokerMemoryUsage(Queue.class);
        Assert.assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
        receiveMessages(Queue.class);
        Assert.assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
        logBrokerMemoryUsage(Queue.class);
        Assert.assertEquals(0, getCurrentMemoryUsage(Queue.class));
    }

    @Test
    public void testQueueMessagesKeptAfterPurge() throws Exception {
        createDurableSubscription();
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        sendBytesMessage(Queue.class);
        logBrokerMemoryUsage(Queue.class);
        Assert.assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
        getProxyToQueue(QUEUE_NAME).purge();
        Assert.assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
        logBrokerMemoryUsage(Queue.class);
        Assert.assertEquals(0, getCurrentMemoryUsage(Queue.class));
    }

    @Test
    public void testDurableTopicSubscriptionMessagesKeptAfterDelivery() throws Exception {
        createDurableSubscription();
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        sendBytesMessage(Topic.class);
        logBrokerMemoryUsage(Topic.class);
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        receiveMessages(Topic.class);
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        logBrokerMemoryUsage(Topic.class);
        Assert.assertEquals(0, getCurrentMemoryUsage(Topic.class));
    }

    @Test
    public void testDurableTopicSubscriptionMessagesKeptAfterUnsubscribe() throws Exception {
        createDurableSubscription();
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        sendBytesMessage(Topic.class);
        logBrokerMemoryUsage(Topic.class);
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        unsubscribeDurableSubscription();
        Assert.assertEquals(0, brokerService.getAdminView().getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerService.getAdminView().getInactiveDurableTopicSubscribers().length);
        logBrokerMemoryUsage(Topic.class);
        Assert.assertEquals(0, getCurrentMemoryUsage(Topic.class));
    }
}

