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
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicSubscriber;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class AMQ6254Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6254Test.class);

    private static final String KAHADB = "KahaDB";

    private static final String LEVELDB = "LevelDB";

    private BrokerService brokerService;

    private String topicA = "alphabet.a";

    private String topicB = "alphabet.b";

    private String persistenceAdapterName;

    private boolean pluginsEnabled;

    public AMQ6254Test(String persistenceAdapterName, boolean pluginsEnabled) {
        this.persistenceAdapterName = persistenceAdapterName;
        this.pluginsEnabled = pluginsEnabled;
    }

    @Test(timeout = 60000)
    public void testReactivateKeepaliveSubscription() throws Exception {
        // Create wild card durable subscription
        Connection connection = createConnection();
        connection.setClientID("cliID");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = session.createDurableSubscriber(session.createTopic("alphabet.>"), "alphabet.>");
        // Send message on Topic A
        connection = createConnection();
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createTopic(topicA));
        producer.send(session.createTextMessage("Hello A"));
        // Verify that message is received
        TextMessage message = ((TextMessage) (subscriber.receive(2000)));
        Assert.assertNotNull("Message not received.", message);
        Assert.assertEquals("Hello A", message.getText());
        Assert.assertTrue("Should have only one consumer", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToTopic(topicA).getConsumerCount()) == 1;
            }
        }));
        subscriber.close();
        Assert.assertTrue("Should have one message consumed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToTopic(topicA).getDequeueCount()) == 1;
            }
        }));
        connection.close();
        Assert.assertTrue("Should have only one destination", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Destination destA = getDestination(topicA);
                return (destA.getDestinationStatistics().getConsumers().getCount()) == 1;
            }
        }));
        Assert.assertTrue("Should have only one inactive subscription", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getInactiveDurableTopicSubscribers().length) == 1;
            }
        }));
        // Restart broker
        brokerService.stop();
        brokerService.waitUntilStopped();
        AMQ6254Test.LOG.info("Broker stopped");
        brokerService = createBroker(false);
        brokerService.start();
        brokerService.waitUntilStarted();
        AMQ6254Test.LOG.info("Broker restarted");
        // Recreate wild card durable subscription
        connection = createConnection();
        connection.setClientID("cliID");
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        subscriber = session.createDurableSubscriber(session.createTopic("alphabet.>"), "alphabet.>");
        // Send message on Topic B
        connection = createConnection();
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        producer = session.createProducer(session.createTopic(topicA));
        producer.send(session.createTextMessage("Hello Again A"));
        // Verify both messages are received
        message = ((TextMessage) (subscriber.receive(2000)));
        Assert.assertNotNull("Message not received.", message);
        Assert.assertEquals("Hello Again A", message.getText());
        // Verify that we still have a single subscription
        Assert.assertTrue("Should have only one destination", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Destination destA = getDestination(topicA);
                return (destA.getDestinationStatistics().getConsumers().getCount()) == 1;
            }
        }));
        subscriber.close();
        connection.close();
    }
}

