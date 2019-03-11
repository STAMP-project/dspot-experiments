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


import ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ5893Test {
    private static Logger LOG = LoggerFactory.getLogger(AMQ5893Test.class);

    private final int MSG_COUNT = 20;

    private BrokerService brokerService;

    private Connection connection;

    private String brokerURI;

    private CountDownLatch done;

    @Rule
    public TestName name = new TestName();

    @Test(timeout = 60000)
    public void tesIndividualAcksWithClosedConsumerAndAuditAsync() throws Exception {
        produceSomeMessages(MSG_COUNT);
        QueueViewMBean queueView = getProxyToQueue(getDestinationName());
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        connection = createConnection();
        Session session = connection.createSession(false, INDIVIDUAL_ACKNOWLEDGE);
        Queue queue = session.createQueue(getDestinationName());
        MessageConsumer consumer = session.createConsumer(queue);
        connection.start();
        // Consume all messages with no ACK
        done = new CountDownLatch(MSG_COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                AMQ5893Test.LOG.debug("Received message: {}", message);
                done.countDown();
            }
        });
        done.await(15, TimeUnit.SECONDS);
        consumer.close();
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        // Consumer the same batch again.
        consumer = session.createConsumer(queue);
        done = new CountDownLatch(MSG_COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                AMQ5893Test.LOG.debug("Received message: {}", message);
                done.countDown();
            }
        });
        done.await(15, TimeUnit.SECONDS);
        consumer.close();
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
    }

    @Test(timeout = 60000)
    public void tesIndividualAcksWithClosedConsumerAndAuditSync() throws Exception {
        produceSomeMessages(MSG_COUNT);
        QueueViewMBean queueView = getProxyToQueue(getDestinationName());
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        connection = createConnection();
        Session session = connection.createSession(false, INDIVIDUAL_ACKNOWLEDGE);
        Queue queue = session.createQueue(getDestinationName());
        MessageConsumer consumer = session.createConsumer(queue);
        connection.start();
        // Consume all messages with no ACK
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = consumer.receive(1000);
            Assert.assertNotNull(message);
            AMQ5893Test.LOG.debug("Received message: {}", message);
        }
        consumer.close();
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        // Consumer the same batch again.
        consumer = session.createConsumer(queue);
        for (int i = 0; i < (MSG_COUNT); ++i) {
            Message message = consumer.receive(1000);
            Assert.assertNotNull(message);
            AMQ5893Test.LOG.debug("Received message: {}", message);
        }
        consumer.close();
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
    }
}

