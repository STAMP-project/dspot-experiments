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
package org.apache.activemq.network;


import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class ForceDurableNetworkBridgeTest extends DynamicNetworkTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(ForceDurableNetworkBridgeTest.class);

    protected String testTopicName2 = "include.nonforced.bar";

    protected String staticTopic = "include.static.bar";

    protected String staticTopic2 = "include.static.nonforced.bar";

    public static enum FLOW {

        FORWARD,
        REVERSE;}

    private BrokerService broker1;

    private BrokerService broker2;

    private Session session1;

    private final ForceDurableNetworkBridgeTest.FLOW flow;

    public ForceDurableNetworkBridgeTest(final ForceDurableNetworkBridgeTest.FLOW flow) {
        this.flow = flow;
    }

    @Test
    public void testForceDurableSubscriptionStatic() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(staticTopic);
        assertNCDurableSubsCount(broker2, topic, 1);
        assertConsumersCount(broker2, topic, 1);
        // Static so consumers stick around
        assertNCDurableSubsCount(broker2, topic, 1);
        assertConsumersCount(broker2, topic, 1);
    }

    @Test
    public void testConsumerNotForceDurableSubscriptionStatic() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(staticTopic2);
        assertConsumersCount(broker2, topic, 1);
        assertNCDurableSubsCount(broker2, topic, 0);
    }

    @Test
    public void testConsumerNotForceDurableSubscription() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName2);
        MessageConsumer sub1 = session1.createConsumer(topic);
        assertConsumersCount(broker2, topic, 1);
        assertNCDurableSubsCount(broker2, topic, 0);
        sub1.close();
        assertNCDurableSubsCount(broker2, topic, 0);
        assertConsumersCount(broker2, topic, 0);
    }

    @Test
    public void testConsumerNotForceDurableWithAnotherDurable() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName2);
        TopicSubscriber durSub = session1.createDurableSubscriber(topic, subName);
        session1.createConsumer(topic);
        // 1 consumer because of conduit
        // 1 durable sub
        assertConsumersCount(broker2, topic, 1);
        assertNCDurableSubsCount(broker2, topic, 1);
        // Remove the sub
        durSub.close();
        Thread.sleep(1000);
        removeSubscription(broker1, topic, subName);
        // The durable should be gone even though there is a consumer left
        // since we are not forcing durable subs
        assertNCDurableSubsCount(broker2, topic, 0);
        // consumers count ends up being 0 here, even though there is a non-durable consumer left,
        // because the durable sub is destroyed and it is a conduit subscription
        // this is another good reason to want to enable forcing of durables
        assertConsumersCount(broker2, topic, 0);
    }

    @Test
    public void testForceDurableSubscription() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName);
        MessageConsumer sub1 = session1.createConsumer(topic);
        assertNCDurableSubsCount(broker2, topic, 1);
        assertConsumersCount(broker2, topic, 1);
        sub1.close();
        assertNCDurableSubsCount(broker2, topic, 0);
        assertConsumersCount(broker2, topic, 0);
    }

    @Test
    public void testForceDurableMultiSubscriptions() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName);
        MessageConsumer sub1 = session1.createConsumer(topic);
        MessageConsumer sub2 = session1.createConsumer(topic);
        MessageConsumer sub3 = session1.createConsumer(topic);
        assertNCDurableSubsCount(broker2, topic, 1);
        assertConsumersCount(broker2, topic, 1);
        sub1.close();
        sub2.close();
        assertNCDurableSubsCount(broker2, topic, 1);
        assertConsumersCount(broker2, topic, 1);
        sub3.close();
        assertNCDurableSubsCount(broker2, topic, 0);
        assertConsumersCount(broker2, topic, 0);
    }

    @Test
    public void testForceDurableSubWithDurableCreatedFirst() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName);
        TopicSubscriber durSub = session1.createDurableSubscriber(topic, subName);
        durSub.close();
        assertNCDurableSubsCount(broker2, topic, 1);
        MessageConsumer sub1 = session1.createConsumer(topic);
        Thread.sleep(1000);
        assertNCDurableSubsCount(broker2, topic, 1);
        sub1.close();
        Thread.sleep(1000);
        assertNCDurableSubsCount(broker2, topic, 1);
        removeSubscription(broker1, topic, subName);
        assertNCDurableSubsCount(broker2, topic, 0);
    }

    @Test
    public void testForceDurableSubWithNonDurableCreatedFirst() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName);
        MessageConsumer sub1 = session1.createConsumer(topic);
        assertNCDurableSubsCount(broker2, topic, 1);
        TopicSubscriber durSub = session1.createDurableSubscriber(topic, subName);
        durSub.close();
        Thread.sleep(1000);
        assertNCDurableSubsCount(broker2, topic, 1);
        removeSubscription(broker1, topic, subName);
        Thread.sleep(1000);
        assertConsumersCount(broker2, topic, 1);
        assertNCDurableSubsCount(broker2, topic, 1);
        sub1.close();
        assertNCDurableSubsCount(broker2, topic, 0);
    }

    @Test
    public void testDurableSticksAroundOnConsumerClose() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic(testTopicName);
        // Create the normal consumer first
        MessageConsumer sub1 = session1.createConsumer(topic);
        assertNCDurableSubsCount(broker2, topic, 1);
        TopicSubscriber durSub = session1.createDurableSubscriber(topic, subName);
        durSub.close();
        sub1.close();
        Thread.sleep(1000);
        // Both consumer and durable are closed but the durable should stick around
        assertConsumersCount(broker2, topic, 1);
        assertNCDurableSubsCount(broker2, topic, 1);
        removeSubscription(broker1, topic, subName);
        assertConsumersCount(broker2, topic, 0);
        assertNCDurableSubsCount(broker2, topic, 0);
    }
}

