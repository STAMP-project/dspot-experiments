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
package org.apache.activemq.camel.component.broker;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Message;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;


public class BrokerComponentXMLConfigTest {
    protected static final String CONF_ROOT = "src/test/resources/org/apache/activemq/camel/component/broker/";

    protected static final String TOPIC_NAME = "test.broker.component.topic";

    protected static final String QUEUE_NAME = "test.broker.component.queue";

    protected static final String ROUTE_QUEUE_NAME = "test.broker.component.route";

    protected static final String DIVERTED_QUEUE_NAME = "test.broker.component.ProcessLater";

    protected static final int DIVERT_COUNT = 100;

    protected BrokerService brokerService;

    protected ActiveMQConnectionFactory factory;

    protected Connection producerConnection;

    protected Connection consumerConnection;

    protected Session consumerSession;

    protected Session producerSession;

    protected int messageCount = 1000;

    protected int timeOutInSeconds = 10;

    @Test
    public void testReRouteAll() throws Exception {
        final ActiveMQQueue queue = new ActiveMQQueue(BrokerComponentXMLConfigTest.QUEUE_NAME);
        Topic topic = consumerSession.createTopic(BrokerComponentXMLConfigTest.TOPIC_NAME);
        final CountDownLatch latch = new CountDownLatch(messageCount);
        MessageConsumer consumer = consumerSession.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    Assert.assertEquals(9, message.getJMSPriority());
                    latch.countDown();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        MessageProducer producer = producerSession.createProducer(topic);
        for (int i = 0; i < (messageCount); i++) {
            Message message = producerSession.createTextMessage(("test: " + i));
            producer.send(message);
        }
        latch.await(timeOutInSeconds, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void testRouteWithDestinationLimit() throws Exception {
        final ActiveMQQueue routeQueue = new ActiveMQQueue(BrokerComponentXMLConfigTest.ROUTE_QUEUE_NAME);
        final CountDownLatch routeLatch = new CountDownLatch(BrokerComponentXMLConfigTest.DIVERT_COUNT);
        MessageConsumer messageConsumer = consumerSession.createConsumer(routeQueue);
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    routeLatch.countDown();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        final CountDownLatch divertLatch = new CountDownLatch(((messageCount) - (BrokerComponentXMLConfigTest.DIVERT_COUNT)));
        MessageConsumer divertConsumer = consumerSession.createConsumer(new ActiveMQQueue(BrokerComponentXMLConfigTest.DIVERTED_QUEUE_NAME));
        divertConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    divertLatch.countDown();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        MessageProducer producer = producerSession.createProducer(routeQueue);
        for (int i = 0; i < (messageCount); i++) {
            Message message = producerSession.createTextMessage(("test: " + i));
            producer.send(message);
        }
        routeLatch.await(timeOutInSeconds, TimeUnit.SECONDS);
        divertLatch.await(timeOutInSeconds, TimeUnit.SECONDS);
        Assert.assertEquals(0, routeLatch.getCount());
        Assert.assertEquals(0, divertLatch.getCount());
    }

    @Test
    public void testPreserveOriginalHeaders() throws Exception {
        final ActiveMQQueue queue = new ActiveMQQueue(BrokerComponentXMLConfigTest.QUEUE_NAME);
        Topic topic = consumerSession.createTopic(BrokerComponentXMLConfigTest.TOPIC_NAME);
        final CountDownLatch latch = new CountDownLatch(messageCount);
        MessageConsumer consumer = consumerSession.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    Assert.assertEquals("321", message.getStringProperty("JMSXGroupID"));
                    Assert.assertEquals("custom", message.getStringProperty("CustomHeader"));
                    latch.countDown();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        MessageProducer producer = producerSession.createProducer(topic);
        for (int i = 0; i < (messageCount); i++) {
            Message message = producerSession.createTextMessage(("test: " + i));
            message.setStringProperty("JMSXGroupID", "123");
            producer.send(message);
        }
        latch.await(timeOutInSeconds, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }
}

