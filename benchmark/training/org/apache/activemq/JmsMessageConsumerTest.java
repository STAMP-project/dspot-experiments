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


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class JmsMessageConsumerTest {
    private BrokerService brokerService;

    private String brokerURI;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testSyncReceiveWithExpirationChecks() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURI);
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);
        producer.setTimeToLive(TimeUnit.SECONDS.toMillis(2));
        connection.start();
        producer.send(session.createTextMessage("test"));
        // Allow message to expire in the prefetch buffer
        TimeUnit.SECONDS.sleep(4);
        Assert.assertNull(consumer.receive(1000));
        connection.close();
    }

    @Test
    public void testSyncReceiveWithIgnoreExpirationChecks() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURI);
        factory.setConsumerExpiryCheckEnabled(false);
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);
        producer.setTimeToLive(TimeUnit.SECONDS.toMillis(2));
        connection.start();
        producer.send(session.createTextMessage("test"));
        // Allow message to expire in the prefetch buffer
        TimeUnit.SECONDS.sleep(4);
        Assert.assertNotNull(consumer.receive(1000));
        connection.close();
    }

    @Test
    public void testAsyncReceiveWithExpirationChecks() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURI);
        final CountDownLatch received = new CountDownLatch(1);
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.countDown();
            }
        });
        MessageProducer producer = session.createProducer(destination);
        producer.setTimeToLive(TimeUnit.SECONDS.toMillis(2));
        producer.send(session.createTextMessage("test"));
        // Allow message to expire in the prefetch buffer
        TimeUnit.SECONDS.sleep(4);
        connection.start();
        Assert.assertFalse(received.await(1, TimeUnit.SECONDS));
        connection.close();
    }

    @Test
    public void testAsyncReceiveWithoutExpirationChecks() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURI);
        factory.setConsumerExpiryCheckEnabled(false);
        final CountDownLatch received = new CountDownLatch(1);
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                received.countDown();
            }
        });
        MessageProducer producer = session.createProducer(destination);
        producer.setTimeToLive(TimeUnit.SECONDS.toMillis(2));
        producer.send(session.createTextMessage("test"));
        // Allow message to expire in the prefetch buffer
        TimeUnit.SECONDS.sleep(4);
        connection.start();
        Assert.assertTrue(received.await(5, TimeUnit.SECONDS));
        connection.close();
    }
}

