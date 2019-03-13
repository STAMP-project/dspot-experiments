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
package org.apache.activemq.broker.scheduler;


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.scheduler.JobSchedulerStoreImpl;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test scheduler store GC cleanup with redelivery plugin and rollbacks.
 */
public class JobSchedulerRedliveryPluginDLQStoreCleanupTest {
    static final Logger LOG = LoggerFactory.getLogger(JobSchedulerStoreCheckpointTest.class);

    private JobSchedulerStoreImpl store;

    private BrokerService brokerService;

    private ByteSequence payload;

    private String connectionURI;

    private ActiveMQConnectionFactory cf;

    @Test
    public void testProducerAndRollback() throws Exception {
        final Connection connection = cf.createConnection();
        final Session producerSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final Session consumerSession = connection.createSession(true, SESSION_TRANSACTED);
        final Queue queue = producerSession.createQueue("FOO.BAR");
        final MessageProducer producer = producerSession.createProducer(queue);
        final MessageConsumer consumer = consumerSession.createConsumer(queue);
        final CountDownLatch sentAll = new CountDownLatch(8);
        connection.start();
        producer.setDeliveryMode(PERSISTENT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    JobSchedulerRedliveryPluginDLQStoreCleanupTest.LOG.info("Rolling back incoming message");
                    consumerSession.rollback();
                } catch (JMSException e) {
                    JobSchedulerRedliveryPluginDLQStoreCleanupTest.LOG.warn("Failed to Rollback on incoming message");
                }
            }
        });
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    BytesMessage message = producerSession.createBytesMessage();
                    message.writeBytes(payload.data, payload.offset, payload.length);
                    producer.send(message);
                    JobSchedulerRedliveryPluginDLQStoreCleanupTest.LOG.info("Send next Message to Queue");
                    sentAll.countDown();
                } catch (JMSException e) {
                    JobSchedulerRedliveryPluginDLQStoreCleanupTest.LOG.warn("Send of message did not complete.");
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
        Assert.assertTrue("Should have sent all messages", sentAll.await(2, TimeUnit.MINUTES));
        executor.shutdownNow();
        Assert.assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));
        Assert.assertTrue("Should clean out the scheduler store", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumJournalFiles()) == 1;
            }
        }));
    }
}

