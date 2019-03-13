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
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4899Test {
    protected static final Logger LOG = LoggerFactory.getLogger(AMQ4899Test.class);

    private static final String QUEUE_NAME = "AMQ4899TestQueue";

    private static final String CONSUMER_QUEUE = "Consumer.Orders.VirtualOrders." + (AMQ4899Test.QUEUE_NAME);

    private static final String PRODUCER_DESTINATION_NAME = "VirtualOrders." + (AMQ4899Test.QUEUE_NAME);

    private static final Integer MESSAGE_LIMIT = 20;

    public static final String CONSUMER_A_SELECTOR = "Order < " + 10;

    public static String CONSUMER_B_SELECTOR = "Order >= " + 10;

    private final CountDownLatch consumersStarted = new CountDownLatch(2);

    private final CountDownLatch consumerAtoConsumeCount = new CountDownLatch(10);

    private final CountDownLatch consumerBtoConsumeCount = new CountDownLatch(10);

    private BrokerService broker;

    @Test(timeout = 60 * 1000)
    public void testVirtualTopicMultipleSelectors() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue consumerQueue = session.createQueue(AMQ4899Test.CONSUMER_QUEUE);
        MessageListener listenerA = new AMQ4899Listener("A", consumersStarted, consumerAtoConsumeCount);
        MessageConsumer consumerA = session.createConsumer(consumerQueue, AMQ4899Test.CONSUMER_A_SELECTOR);
        consumerA.setMessageListener(listenerA);
        MessageListener listenerB = new AMQ4899Listener("B", consumersStarted, consumerBtoConsumeCount);
        MessageConsumer consumerB = session.createConsumer(consumerQueue, AMQ4899Test.CONSUMER_B_SELECTOR);
        consumerB.setMessageListener(listenerB);
        consumersStarted.await(10, TimeUnit.SECONDS);
        Assert.assertEquals("Not all consumers started in time", 0, consumersStarted.getCount());
        Destination producerDestination = session.createTopic(AMQ4899Test.PRODUCER_DESTINATION_NAME);
        MessageProducer producer = session.createProducer(producerDestination);
        int messageIndex = 0;
        for (int i = 0; i < (AMQ4899Test.MESSAGE_LIMIT); i++) {
            if (i == 3) {
                AMQ4899Test.LOG.debug("Stopping consumerA");
                consumerA.close();
            }
            if (i == 14) {
                AMQ4899Test.LOG.debug("Stopping consumer B");
                consumerB.close();
            }
            String messageText = (("hello " + (messageIndex++)) + " sent at ") + (new Date().toString());
            TextMessage message = session.createTextMessage(messageText);
            message.setIntProperty("Order", i);
            AMQ4899Test.LOG.debug("Sending message [{}]", messageText);
            producer.send(message);
        }
        // restart consumerA
        AMQ4899Test.LOG.debug("Restarting consumerA");
        consumerA = session.createConsumer(consumerQueue, AMQ4899Test.CONSUMER_A_SELECTOR);
        consumerA.setMessageListener(listenerA);
        // restart consumerB
        AMQ4899Test.LOG.debug("restarting consumerB");
        consumerB = session.createConsumer(consumerQueue, AMQ4899Test.CONSUMER_B_SELECTOR);
        consumerB.setMessageListener(listenerB);
        consumerAtoConsumeCount.await(5, TimeUnit.SECONDS);
        consumerBtoConsumeCount.await(5, TimeUnit.SECONDS);
        AMQ4899Test.LOG.debug("Unconsumed messages for consumerA {} consumerB {}", consumerAtoConsumeCount.getCount(), consumerBtoConsumeCount.getCount());
        Assert.assertEquals("Consumer A did not consume all messages", 0, consumerAtoConsumeCount.getCount());
        Assert.assertEquals("Consumer B did not consume all messages", 0, consumerBtoConsumeCount.getCount());
        connection.close();
    }
}

