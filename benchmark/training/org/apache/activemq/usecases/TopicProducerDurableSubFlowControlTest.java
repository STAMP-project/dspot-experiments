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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import javax.management.ObjectName;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.DurableSubscriptionViewMBean;
import org.apache.activemq.util.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopicProducerDurableSubFlowControlTest extends TestCase implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(TopicProducerDurableSubFlowControlTest.class);

    private static final String brokerName = "testBroker";

    private static final String brokerUrl = "vm://" + (TopicProducerDurableSubFlowControlTest.brokerName);

    protected static final int destinationMemLimit = 2097152;// 2MB


    private static final AtomicLong produced = new AtomicLong();

    private static final AtomicLong consumed = new AtomicLong();

    private static final int numMessagesToSend = 10000;

    private BrokerService broker;

    public void testTopicProducerFlowControl() throws Exception {
        // Create the connection factory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(TopicProducerDurableSubFlowControlTest.brokerUrl);
        connectionFactory.setAlwaysSyncSend(true);
        // Start the test destination listener
        Connection c = connectionFactory.createConnection();
        c.setClientID("cliId1");
        c.start();
        Session listenerSession = c.createSession(false, 1);
        TopicSubscriber durable = listenerSession.createDurableSubscriber(createDestination(), "DurableSub-0");
        durable.close();
        durable = listenerSession.createDurableSubscriber(createDestination(), "DurableSub-1");
        durable.setMessageListener(this);
        // Start producing the test messages
        final Session session = connectionFactory.createConnection().createSession(false, AUTO_ACKNOWLEDGE);
        final MessageProducer producer = session.createProducer(createDestination());
        final Thread producingThread = new Thread("Producing Thread") {
            public void run() {
                try {
                    for (long i = 0; i < (TopicProducerDurableSubFlowControlTest.numMessagesToSend); i++) {
                        producer.send(session.createTextMessage("test"));
                        long count = TopicProducerDurableSubFlowControlTest.produced.incrementAndGet();
                        if ((count % 10000) == 0) {
                            TopicProducerDurableSubFlowControlTest.LOG.info((("Produced " + count) + " messages"));
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        producer.close();
                        session.close();
                    } catch (Exception e) {
                    }
                }
            }
        };
        producingThread.start();
        ArrayList<ObjectName> subON = new ArrayList<>();
        final ArrayList<DurableSubscriptionViewMBean> subViews = new ArrayList<>();
        subON.addAll(Arrays.asList(broker.getAdminView().getInactiveDurableTopicSubscribers()));
        subON.addAll(Arrays.asList(broker.getAdminView().getDurableTopicSubscribers()));
        TestCase.assertTrue("have a sub", (!(subON.isEmpty())));
        for (ObjectName subName : subON) {
            subViews.add(((DurableSubscriptionViewMBean) (broker.getManagementContext().newProxyInstance(subName, DurableSubscriptionViewMBean.class, true))));
        }
        TopicProducerDurableSubFlowControlTest.LOG.info("Wait for producer to stop");
        TestCase.assertTrue("producer thread is done", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                for (DurableSubscriptionViewMBean sub : subViews) {
                    TopicProducerDurableSubFlowControlTest.LOG.info(("name: " + (sub.getSubscriptionName())));
                    TopicProducerDurableSubFlowControlTest.LOG.info(("cursor size: " + (sub.cursorSize())));
                    TopicProducerDurableSubFlowControlTest.LOG.info(("mem usage: " + (sub.getCursorMemoryUsage())));
                    TopicProducerDurableSubFlowControlTest.LOG.info(("mem % usage: " + (sub.getCursorPercentUsage())));
                }
                return !(producingThread.isAlive());
            }
        }, ((10 * 60) * 1000)));
        for (DurableSubscriptionViewMBean sub : subViews) {
            TopicProducerDurableSubFlowControlTest.LOG.info(("name: " + (sub.getSubscriptionName())));
            TopicProducerDurableSubFlowControlTest.LOG.info(("cursor size: " + (sub.cursorSize())));
            TopicProducerDurableSubFlowControlTest.LOG.info(("mem usage: " + (sub.getCursorMemoryUsage())));
            TopicProducerDurableSubFlowControlTest.LOG.info(("mem % usage: " + (sub.getCursorPercentUsage())));
            if ((sub.cursorSize()) > 0) {
                TestCase.assertTrue("Has a decent usage", ((sub.getCursorPercentUsage()) > 5));
            }
        }
    }
}

