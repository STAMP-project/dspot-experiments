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
import Session.CLIENT_ACKNOWLEDGE;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test behavior of senders when broker side producer flow control kicks in.
 */
public class AMQ5712Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ5712Test.class);

    @Rule
    public TestName name = new TestName();

    private BrokerService brokerService;

    private Connection connection;

    @Test(timeout = 120000)
    public void test() throws Exception {
        connection = createConnection();
        connection.start();
        final int MSG_COUNT = 100;
        final Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        final Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);
        producer.setDeliveryMode(NON_PERSISTENT);
        final QueueViewMBean queueView = getProxyToQueue(name.getMethodName());
        byte[] payload = new byte[65535];
        Arrays.fill(payload, ((byte) (255)));
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicInteger counter = new AtomicInteger();
        Thread purge = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!(done.await(5, TimeUnit.SECONDS))) {
                        if (((queueView.getBlockedSends()) > 0) && ((queueView.getQueueSize()) > 0)) {
                            long queueSize = queueView.getQueueSize();
                            AMQ5712Test.LOG.info("Queue send blocked at {} messages", queueSize);
                            MessageConsumer consumer = session.createConsumer(queue);
                            for (int i = 0; i < queueSize; i++) {
                                Message message = consumer.receive(60000);
                                if (message != null) {
                                    counter.incrementAndGet();
                                    message.acknowledge();
                                } else {
                                    AMQ5712Test.LOG.warn("Got null message when none as expected.");
                                }
                            }
                            consumer.close();
                        }
                    } 
                } catch (Exception ex) {
                }
            }
        });
        purge.start();
        for (int i = 0; i < MSG_COUNT; i++) {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(payload);
            producer.send(message);
            AMQ5712Test.LOG.info("sent message: {}", i);
        }
        done.countDown();
        purge.join(60000);
        if (purge.isAlive()) {
            Assert.fail("Consumer thread should have read initial batch and completed.");
        }
        // wait for processed acked messages
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getDequeueCount()) == (counter.get());
            }
        }));
        long remainingQueued = queueView.getQueueSize();
        AMQ5712Test.LOG.info("Remaining messages to consume: {}", remainingQueued);
        Assert.assertEquals(remainingQueued, (MSG_COUNT - (counter.get())));
        MessageConsumer consumer = session.createConsumer(queue);
        for (int i = counter.get(); i < MSG_COUNT; i++) {
            Message message = consumer.receive(5000);
            Assert.assertNotNull("Should not get null message", consumer);
            counter.incrementAndGet();
            message.acknowledge();
            AMQ5712Test.LOG.info("Read message: {}", i);
        }
        Assert.assertEquals("Should consume all messages", MSG_COUNT, counter.get());
    }
}

