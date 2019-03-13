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
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SelectorAwareVTThatDropsMessagesWhenNoConsumer {
    protected static final Logger LOG = LoggerFactory.getLogger(SelectorAwareVTThatDropsMessagesWhenNoConsumer.class);

    private static final String QUEUE_NAME = "TestQ";

    private static final String CONSUMER_QUEUE = "Consumer.Orders.VirtualOrders." + (SelectorAwareVTThatDropsMessagesWhenNoConsumer.QUEUE_NAME);

    private static final String PRODUCER_DESTINATION_NAME = "VirtualOrders." + (SelectorAwareVTThatDropsMessagesWhenNoConsumer.QUEUE_NAME);

    final AtomicInteger receivedCount = new AtomicInteger(0);

    private BrokerService broker;

    @Test(timeout = 60 * 1000)
    public void verifyNoDispatchDuringDisconnect() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue consumerQueue = session.createQueue(SelectorAwareVTThatDropsMessagesWhenNoConsumer.CONSUMER_QUEUE);
        MessageListener listenerA = new SelectorAwareVTThatDropsMessagesWhenNoConsumer.CountingListener(receivedCount);
        MessageConsumer consumerA = session.createConsumer(consumerQueue);
        consumerA.setMessageListener(listenerA);
        Destination producerDestination = session.createTopic(SelectorAwareVTThatDropsMessagesWhenNoConsumer.PRODUCER_DESTINATION_NAME);
        MessageProducer producer = session.createProducer(producerDestination);
        TextMessage message = session.createTextMessage("bla");
        producer.send(message);
        producer.send(message);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (receivedCount.get()) == 2;
            }
        });
        consumerA.close();
        producer.send(message);
        producer.send(message);
        Assert.assertEquals(2, receivedCount.get());
        SelectorAwareVTThatDropsMessagesWhenNoConsumer.LOG.debug("Restarting consumerA");
        consumerA = session.createConsumer(consumerQueue);
        consumerA.setMessageListener(listenerA);
        producer.send(message);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (receivedCount.get()) == 3;
            }
        });
        Assert.assertEquals(3, receivedCount.get());
        connection.close();
    }

    class CountingListener implements MessageListener {
        AtomicInteger counter;

        public CountingListener(AtomicInteger counter) {
            this.counter = counter;
        }

        @Override
        public void onMessage(Message message) {
            counter.incrementAndGet();
        }
    }
}

