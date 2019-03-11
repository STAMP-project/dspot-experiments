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
import Session.CLIENT_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQTopicSubscriber;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3678Test implements MessageListener {
    private static Logger LOG = LoggerFactory.getLogger(AMQ3678Test.class);

    private BrokerService broker;

    private String connectionURI;

    private final AtomicInteger messagesSent = new AtomicInteger(0);

    private final AtomicInteger messagesReceived = new AtomicInteger(0);

    private final ActiveMQTopic destination = new ActiveMQTopic("XYZ");

    private final CountDownLatch latch = new CountDownLatch(2);

    private final int deliveryMode = DeliveryMode.NON_PERSISTENT;

    @Test(timeout = 60000)
    public void countConsumers() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionURI);
        factory.setAlwaysSyncSend(true);
        factory.setDispatchAsync(false);
        final Connection producerConnection = factory.createConnection();
        producerConnection.start();
        final Connection consumerConnection = factory.createConnection();
        consumerConnection.setClientID("subscriber1");
        Session consumerMQSession = consumerConnection.createSession(false, CLIENT_ACKNOWLEDGE);
        ActiveMQTopicSubscriber activeConsumer = ((ActiveMQTopicSubscriber) (consumerMQSession.createDurableSubscriber(destination, "myTopic?consumer.prefetchSize=1")));
        activeConsumer.setMessageListener(this);
        consumerConnection.start();
        final Session producerSession = producerConnection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageProducer producer = producerSession.createProducer(destination);
        producer.setDeliveryMode(deliveryMode);
        Thread t = new Thread(new Runnable() {
            private boolean done = false;

            @Override
            public void run() {
                while (!(done)) {
                    if ((messagesSent.get()) == 50) {
                        try {
                            broker.getAdminView().removeTopic(destination.getTopicName());
                        } catch (Exception e1) {
                            Assert.fail(("Unable to remove destination:" + (destination.getPhysicalName())));
                        }
                    }
                    try {
                        producer.send(producerSession.createTextMessage());
                        int val = messagesSent.incrementAndGet();
                        AMQ3678Test.LOG.trace((("sent message (" + val) + ")"));
                        if (val == 100) {
                            done = true;
                            latch.countDown();
                            producer.close();
                            producerSession.close();
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } 
            }
        });
        t.start();
        try {
            if (!(latch.await(10, TimeUnit.SECONDS))) {
                Assert.fail("did not receive all the messages");
            }
        } catch (InterruptedException e) {
            Assert.fail("did not receive all the messages, exception waiting for latch");
        }
    }
}

