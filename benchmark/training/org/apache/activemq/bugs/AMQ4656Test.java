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


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.management.ObjectName;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.DurableSubscriptionViewMBean;
import org.apache.activemq.broker.region.policy.PendingDurableSubscriberMessageStoragePolicy;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class AMQ4656Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ4656Test.class);

    private static BrokerService brokerService;

    private static String BROKER_ADDRESS = "tcp://localhost:0";

    private String connectionUri;

    @Parameterized.Parameter
    public PendingDurableSubscriberMessageStoragePolicy pendingDurableSubPolicy;

    @Test(timeout = 90000)
    public void testDurableConsumerEnqueueCountWithZeroPrefetch() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(getClass().getName());
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic("DurableTopic");
        MessageConsumer consumer = session.createDurableSubscriber(((Topic) (destination)), "EnqueueSub");
        final BrokerViewMBean brokerView = AMQ4656Test.brokerService.getAdminView();
        ObjectName subName = brokerView.getDurableTopicSubscribers()[0];
        final DurableSubscriptionViewMBean sub = ((DurableSubscriptionViewMBean) (AMQ4656Test.brokerService.getManagementContext().newProxyInstance(subName, DurableSubscriptionViewMBean.class, true)));
        Assert.assertEquals(0, sub.getEnqueueCounter());
        Assert.assertEquals(0, sub.getDequeueCounter());
        Assert.assertEquals(0, sub.getPendingQueueSize());
        Assert.assertEquals(0, sub.getDispatchedCounter());
        Assert.assertEquals(0, sub.getDispatchedQueueSize());
        consumer.close();
        MessageProducer producer = session.createProducer(destination);
        for (int i = 0; i < 20; i++) {
            producer.send(session.createMessage());
        }
        producer.close();
        consumer = session.createDurableSubscriber(((Topic) (destination)), "EnqueueSub");
        Assert.assertTrue("Should be an Active Subscription", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerView.getDurableTopicSubscribers().length) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(25)));
        Assert.assertTrue("Should all be dispatched", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (sub.getDispatchedCounter()) == 20;
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(25)));
        Assert.assertEquals(20, sub.getEnqueueCounter());
        Assert.assertEquals(0, sub.getDequeueCounter());
        Assert.assertEquals(0, sub.getPendingQueueSize());
        Assert.assertEquals(20, sub.getDispatchedCounter());
        Assert.assertEquals(20, sub.getDispatchedQueueSize());
        AMQ4656Test.LOG.info("Pending Queue Size with no receives: {}", sub.getPendingQueueSize());
        Assert.assertNotNull(consumer.receive(1000));
        Assert.assertNotNull(consumer.receive(1000));
        consumer.close();
        AMQ4656Test.LOG.info("Pending Queue Size with two receives: {}", sub.getPendingQueueSize());
        Assert.assertTrue("Should be an Active Subscription", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerView.getInactiveDurableTopicSubscribers().length) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(25)));
        final DurableSubscriptionViewMBean inactive = ((DurableSubscriptionViewMBean) (AMQ4656Test.brokerService.getManagementContext().newProxyInstance(subName, DurableSubscriptionViewMBean.class, true)));
        Assert.assertTrue("Should all be dispatched", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (inactive.getDequeueCounter()) == 2;
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(25)));
        Assert.assertEquals(20, inactive.getEnqueueCounter());
        Assert.assertEquals(2, inactive.getDequeueCounter());
        Assert.assertEquals(18, inactive.getPendingQueueSize());
        Assert.assertEquals(20, inactive.getDispatchedCounter());
        Assert.assertEquals(0, inactive.getDispatchedQueueSize());
        session.close();
        connection.close();
    }
}

