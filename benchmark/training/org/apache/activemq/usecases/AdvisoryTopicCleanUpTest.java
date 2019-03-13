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


import Message.DEFAULT_DELIVERY_MODE;
import Message.DEFAULT_PRIORITY;
import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AdvisoryTopicCleanUpTest {
    private static final Logger LOG = LoggerFactory.getLogger(AdvisoryTopicCleanUpTest.class);

    private BrokerService broker;

    private String connectionUri;

    @Test
    public void testAdvisoryTopic() throws Exception {
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQDestination queue = ((ActiveMQDestination) (session.createQueue("AdvisoryTopicCleanUpTestQueue")));
        MessageProducer prod = session.createProducer(queue);
        Message message = session.createMessage();
        prod.send(message);
        message = session.createMessage();
        prod.send(message);
        message = session.createMessage();
        prod.send(message, DEFAULT_DELIVERY_MODE, DEFAULT_PRIORITY, 1000);
        connection.close();
        connection = createConnection();
        session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);
        message = consumer.receive((60 * 1000));
        message.acknowledge();
        connection.close();
        connection = null;
        for (int i = 0; i < 2; i++) {
            connection = createConnection();
            session = connection.createSession(true, CLIENT_ACKNOWLEDGE);
            consumer = session.createConsumer(queue);
            message = consumer.receive((60 * 1000));
            session.rollback();
            connection.close();
            connection = null;
        }
        Thread.sleep((2 * 1000));
        connection = createConnection();
        session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        consumer = session.createConsumer(queue);
        message = consumer.receive(1000);
        if (message != null)
            message.acknowledge();

        connection.close();
        connection = null;
        TimeUnit.SECONDS.sleep(1);
        ActiveMQDestination[] dests = broker.getRegionBroker().getDestinations();
        for (ActiveMQDestination destination : dests) {
            String name = destination.getPhysicalName();
            if (name.contains(queue.getPhysicalName())) {
                AdvisoryTopicCleanUpTest.LOG.info(("Destination on Broker before removing the Queue: " + name));
            }
        }
        dests = broker.getRegionBroker().getDestinations();
        if (dests == null) {
            Assert.fail(("Should have Destination for: " + (queue.getPhysicalName())));
        }
        broker.getAdminView().removeQueue(queue.getPhysicalName());
        dests = broker.getRegionBroker().getDestinations();
        if (dests != null) {
            for (ActiveMQDestination destination : dests) {
                String name = destination.getPhysicalName();
                AdvisoryTopicCleanUpTest.LOG.info(("Destination on broker after removing the Queue: " + name));
                Assert.assertFalse(("Advisory topic should not exist. " + name), ((name.startsWith("ActiveMQ.Advisory")) && (name.contains(queue.getPhysicalName()))));
            }
        }
    }
}

