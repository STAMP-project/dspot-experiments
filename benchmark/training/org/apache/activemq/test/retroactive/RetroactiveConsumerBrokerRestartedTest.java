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
package org.apache.activemq.test.retroactive;


import DeliveryMode.PERSISTENT;
import javax.jms.Session.AUTO_ACKNOWLEDGE;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RetroactiveConsumerBrokerRestartedTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(RetroactiveConsumerBrokerRestartedTest.class);

    private static final String ACTIVEMQ_BROKER_URI = "tcp://localhost:62626";

    private BrokerService broker;

    Connection connection;

    public void testFixedCountSubscriptionRecoveryPolicy() throws Exception {
        connection.start();
        // Create the durable sub.
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Ensure that consumer will receive messages sent before it was created
        Topic topicSub = session.createTopic("TestTopic?consumer.retroactive=true");
        Topic topic = session.createTopic("TestTopic");
        TopicSubscriber sub1 = session.createDurableSubscriber(topicSub, "sub1");
        // Produce a message
        MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(PERSISTENT);
        // Make sure it works when the durable sub is active.
        producer.send(session.createTextMessage("Msg:1"));
        producer.send(session.createTextMessage("Msg:2"));
        producer.send(session.createTextMessage("Msg:3"));
        restartBroker();
        connection = getConnection();
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        producer = session.createProducer(topic);
        producer.setDeliveryMode(PERSISTENT);
        producer.send(session.createTextMessage("Msg:4"));
        // Recreate the subscriber to check if it will be able to recover the messages
        sub1 = session.createDurableSubscriber(topicSub, "sub1");
        // Try to get the messages
        assertTextMessageEquals("Msg:1", sub1.receive(1000));
        assertTextMessageEquals("Msg:2", sub1.receive(1000));
        assertTextMessageEquals("Msg:3", sub1.receive(1000));
        assertTextMessageEquals("Msg:4", sub1.receive(1000));
    }
}

