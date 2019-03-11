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
package org.apache.activemq.broker.policy;


import Session.CLIENT_ACKNOWLEDGE;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.IllegalStateException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.JmsMultipleClientsTestSupport;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.jmx.AbortSlowConsumerStrategyViewMBean;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.util.MessageIdList;
import org.apache.activemq.util.SocketProxy;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbortSlowConsumer0Test extends AbortSlowConsumerBase {
    private static final Logger LOG = LoggerFactory.getLogger(AbortSlowConsumer0Test.class);

    public AbortSlowConsumer0Test() {
        this.topic = true;
    }

    @Test
    public void testRegularConsumerIsNotAborted() throws Exception {
        startConsumers(destination);
        for (Connection c : connections) {
            c.setExceptionListener(this);
        }
        startProducers(destination, 100);
        allMessagesList.waitForMessagesToArrive(10);
        allMessagesList.assertAtLeastMessagesReceived(10);
    }

    @Test
    public void testSlowConsumerIsAbortedViaJmx() throws Exception {
        underTest.setMaxSlowDuration((60 * 1000));// so jmx does the abort

        startConsumers(withPrefetch(2, destination));
        Map.Entry<MessageConsumer, MessageIdList> consumertoAbort = consumers.entrySet().iterator().next();
        consumertoAbort.getValue().setProcessingDelay((8 * 1000));
        for (Connection c : connections) {
            c.setExceptionListener(this);
        }
        startProducers(destination, 100);
        consumertoAbort.getValue().assertMessagesReceived(1);
        ActiveMQDestination amqDest = ((ActiveMQDestination) (destination));
        ObjectName destinationViewMBean = new ObjectName((((("org.apache.activemq:destinationType=" + (amqDest.isTopic() ? "Topic" : "Queue")) + ",destinationName=") + (amqDest.getPhysicalName())) + ",type=Broker,brokerName=localhost"));
        DestinationViewMBean queue = ((DestinationViewMBean) (broker.getManagementContext().newProxyInstance(destinationViewMBean, DestinationViewMBean.class, true)));
        ObjectName slowConsumerPolicyMBeanName = queue.getSlowConsumerStrategy();
        Assert.assertNotNull(slowConsumerPolicyMBeanName);
        AbortSlowConsumerStrategyViewMBean abortPolicy = ((AbortSlowConsumerStrategyViewMBean) (broker.getManagementContext().newProxyInstance(slowConsumerPolicyMBeanName, AbortSlowConsumerStrategyViewMBean.class, true)));
        TimeUnit.SECONDS.sleep(3);
        TabularData slowOnes = abortPolicy.getSlowConsumers();
        Assert.assertEquals("one slow consumers", 1, slowOnes.size());
        AbortSlowConsumer0Test.LOG.info(("slow ones:" + slowOnes));
        CompositeData slowOne = ((CompositeData) (slowOnes.values().iterator().next()));
        AbortSlowConsumer0Test.LOG.info(("Slow one: " + slowOne));
        Assert.assertTrue("we have an object name", ((slowOne.get("subscription")) instanceof ObjectName));
        abortPolicy.abortConsumer(((ObjectName) (slowOne.get("subscription"))));
        consumertoAbort.getValue().assertAtMostMessagesReceived(1);
        slowOnes = abortPolicy.getSlowConsumers();
        Assert.assertEquals("no slow consumers left", 0, slowOnes.size());
        // verify mbean gone with destination
        if (topic) {
            broker.getAdminView().removeTopic(amqDest.getPhysicalName());
        } else {
            broker.getAdminView().removeQueue(amqDest.getPhysicalName());
        }
        try {
            abortPolicy.getSlowConsumers();
            Assert.fail("expect not found post destination removal");
        } catch (UndeclaredThrowableException expected) {
            Assert.assertTrue(("correct exception: " + (expected.getCause())), ((expected.getCause()) instanceof InstanceNotFoundException));
        }
    }

    @Test
    public void testOnlyOneSlowConsumerIsAborted() throws Exception {
        consumerCount = 10;
        startConsumers(destination);
        Map.Entry<MessageConsumer, MessageIdList> consumertoAbort = consumers.entrySet().iterator().next();
        consumertoAbort.getValue().setProcessingDelay((8 * 1000));
        for (Connection c : connections) {
            c.setExceptionListener(this);
        }
        startProducers(destination, 100);
        allMessagesList.waitForMessagesToArrive(99);
        allMessagesList.assertAtLeastMessagesReceived(99);
        consumertoAbort.getValue().assertMessagesReceived(1);
        TimeUnit.SECONDS.sleep(5);
        consumertoAbort.getValue().assertAtMostMessagesReceived(1);
    }

    @Test
    public void testAbortAlreadyClosingConsumers() throws Exception {
        consumerCount = 1;
        startConsumers(withPrefetch(2, destination));
        for (MessageIdList list : consumers.values()) {
            list.setProcessingDelay((6 * 1000));
        }
        for (Connection c : connections) {
            c.setExceptionListener(this);
        }
        startProducers(destination, 100);
        allMessagesList.waitForMessagesToArrive(consumerCount);
        for (MessageConsumer consumer : consumers.keySet()) {
            AbortSlowConsumer0Test.LOG.info(("closing consumer: " + consumer));
            // / will block waiting for on message till 6secs expire
            consumer.close();
        }
    }

    @Test
    public void testAbortConsumerOnDeadConnection() throws Exception {
        TransportConnector transportConnector = broker.addConnector("tcp://0.0.0.0:0");
        transportConnector.setBrokerService(broker);
        transportConnector.setTaskRunnerFactory(broker.getTaskRunnerFactory());
        transportConnector.start();
        SocketProxy socketProxy = new SocketProxy(transportConnector.getPublishableConnectURI());
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(socketProxy.getUrl());
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setAll(4);
        connectionFactory.setPrefetchPolicy(prefetchPolicy);
        Connection c = connectionFactory.createConnection();
        connections.add(c);
        c.start();
        Session session = c.createSession(false, CLIENT_ACKNOWLEDGE);
        final ActiveMQMessageConsumer messageconsumer = ((ActiveMQMessageConsumer) (session.createConsumer(destination)));
        startProducers(destination, 10);
        messageconsumer.receive(4000).acknowledge();
        Assert.assertNotNull(messageconsumer.receive(4000));
        Assert.assertNotNull(messageconsumer.receive(4000));
        Assert.assertNotNull(messageconsumer.receive(4000));
        // close control command won't get through
        socketProxy.pause();
        ActiveMQDestination amqDest = ((ActiveMQDestination) (destination));
        ObjectName destinationViewMBean = new ObjectName((((("org.apache.activemq:destinationType=" + (amqDest.isTopic() ? "Topic" : "Queue")) + ",destinationName=") + (amqDest.getPhysicalName())) + ",type=Broker,brokerName=localhost"));
        final DestinationViewMBean destView = ((DestinationViewMBean) (broker.getManagementContext().newProxyInstance(destinationViewMBean, DestinationViewMBean.class, true)));
        Assert.assertTrue("Consumer gone from broker view", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                AbortSlowConsumer0Test.LOG.info("DestView {} comsumerCount {}", destView, destView.getConsumerCount());
                return 0 == (destView.getConsumerCount());
            }
        }));
        socketProxy.goOn();
        Assert.assertTrue("consumer was closed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                boolean closed = false;
                try {
                    messageconsumer.receive(400);
                } catch (javax.jms expected) {
                    closed = expected.toString().contains("closed");
                }
                return closed;
            }
        }));
    }
}

