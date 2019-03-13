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


import DeliveryMode.NON_PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2764Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2764Test.class);

    @Rule
    public TestName name = new TestName();

    private BrokerService brokerOne;

    private BrokerService brokerTwo;

    private Destination destination;

    private final ArrayList<Connection> connections = new ArrayList<Connection>();

    @Test(timeout = 60000)
    public void testInactivityMonitor() throws Exception {
        startBrokerTwo();
        brokerTwo.waitUntilStarted();
        startBrokerOne();
        brokerOne.waitUntilStarted();
        ActiveMQConnectionFactory secondProducerConnectionFactory = createBrokerTwoHttpConnectionFactory();
        ActiveMQConnectionFactory consumerConnectionFactory = createBrokerOneHttpConnectionFactory();
        MessageConsumer consumer = createConsumer(consumerConnectionFactory);
        AtomicInteger counter = createConsumerCounter(consumerConnectionFactory);
        waitForConsumerToArrive(counter);
        Connection connection = secondProducerConnectionFactory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(NON_PERSISTENT);
        final int expectedMessagesReceived = 1000;
        for (int i = 1; i <= expectedMessagesReceived; i++) {
            Message message = session.createMessage();
            producer.send(message);
            if ((i % 200) == 0) {
                AMQ2764Test.LOG.info(("sent message " + i));
            }
        }
        for (int i = 1; i <= expectedMessagesReceived; i++) {
            Message message = consumer.receive(2000);
            if (message == null) {
                Assert.fail("Didn't receive a message");
            }
            if ((i % 200) == 0) {
                AMQ2764Test.LOG.info(("received message " + i));
            }
        }
    }

    @Test(timeout = 60000)
    public void testBrokerRestart() throws Exception {
        startBrokerTwo();
        brokerTwo.waitUntilStarted();
        startBrokerOne();
        brokerOne.waitUntilStarted();
        ActiveMQConnectionFactory producerConnectionFactory = createBrokerOneConnectionFactory();
        ActiveMQConnectionFactory secondProducerConnectionFactory = createBrokerTwoConnectionFactory();
        ActiveMQConnectionFactory consumerConnectionFactory = createBrokerOneConnectionFactory();
        MessageConsumer consumer = createConsumer(consumerConnectionFactory);
        AtomicInteger counter = createConsumerCounter(consumerConnectionFactory);
        waitForConsumerToArrive(counter);
        final int expectedMessagesReceived = 25;
        int actualMessagesReceived = doSendMessage(expectedMessagesReceived, consumer, producerConnectionFactory);
        Assert.assertEquals("Didn't receive the right amount of messages directly connected", expectedMessagesReceived, actualMessagesReceived);
        Assert.assertNull("Had extra messages", consumer.receiveNoWait());
        actualMessagesReceived = doSendMessage(expectedMessagesReceived, consumer, secondProducerConnectionFactory);
        Assert.assertEquals("Didn't receive the right amount of messages via network", expectedMessagesReceived, actualMessagesReceived);
        Assert.assertNull("Had extra messages", consumer.receiveNoWait());
        AMQ2764Test.LOG.info("Stopping broker one");
        stopBrokerOne();
        TimeUnit.SECONDS.sleep(1);
        AMQ2764Test.LOG.info("Restarting broker");
        startBrokerOne();
        consumer = createConsumer(consumerConnectionFactory);
        counter = createConsumerCounter(consumerConnectionFactory);
        waitForConsumerToArrive(counter);
        actualMessagesReceived = doSendMessage(expectedMessagesReceived, consumer, secondProducerConnectionFactory);
        Assert.assertEquals("Didn't receive the right amount of messages via network after restart", expectedMessagesReceived, actualMessagesReceived);
        Assert.assertNull("Had extra messages", consumer.receiveNoWait());
        stopBrokerOne();
        stopBrokerTwo();
    }
}

