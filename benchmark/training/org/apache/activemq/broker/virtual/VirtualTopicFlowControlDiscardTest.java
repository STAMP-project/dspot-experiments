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
package org.apache.activemq.broker.virtual;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class VirtualTopicFlowControlDiscardTest {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualTopicFlowControlDiscardTest.class);

    final String payload = new String(new byte[155]);

    int numConsumers = 2;

    int total = 500;

    @Parameterized.Parameter(0)
    public boolean concurrentSend;

    @Parameterized.Parameter(1)
    public boolean transactedSend;

    @Parameterized.Parameter(2)
    public boolean sendFailGlobal;

    @Parameterized.Parameter(3)
    public boolean persistentBroker;

    BrokerService brokerService;

    ConnectionFactory connectionFactory;

    @Test
    public void testFanoutWithResourceException() throws Exception {
        Connection connection1 = connectionFactory.createConnection();
        connection1.start();
        Session session = connection1.createSession(false, AUTO_ACKNOWLEDGE);
        for (int i = 0; i < (numConsumers); i++) {
            session.createConsumer(new ActiveMQQueue((("Consumer." + i) + ".VirtualTopic.TEST")));
        }
        Connection connection2 = connectionFactory.createConnection();
        connection2.start();
        Session producerSession = connection2.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = producerSession.createProducer(new ActiveMQTopic("VirtualTopic.TEST"));
        long start = System.currentTimeMillis();
        VirtualTopicFlowControlDiscardTest.LOG.info(("Starting producer: " + start));
        for (int i = 0; i < (total); i++) {
            producer.send(producerSession.createTextMessage(payload));
        }
        VirtualTopicFlowControlDiscardTest.LOG.info(("Done producer, duration: " + ((System.currentTimeMillis()) - start)));
        Destination destination = brokerService.getDestination(new ActiveMQQueue("Consumer.0.VirtualTopic.TEST"));
        VirtualTopicFlowControlDiscardTest.LOG.info(("Dest 0 size: " + (destination.getDestinationStatistics().getEnqueues().getCount())));
        Assert.assertTrue("did not get all", ((destination.getDestinationStatistics().getEnqueues().getCount()) < (total)));
        Assert.assertTrue("got all", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Destination dest = brokerService.getDestination(new ActiveMQQueue("Consumer.1.VirtualTopic.TEST"));
                VirtualTopicFlowControlDiscardTest.LOG.info(("Dest 1 size: " + (dest.getDestinationStatistics().getEnqueues().getCount())));
                return (total) == (dest.getDestinationStatistics().getEnqueues().getCount());
            }
        }));
        try {
            connection1.close();
        } catch (Exception ex) {
        }
        try {
            connection2.close();
        } catch (Exception ex) {
        }
    }
}

