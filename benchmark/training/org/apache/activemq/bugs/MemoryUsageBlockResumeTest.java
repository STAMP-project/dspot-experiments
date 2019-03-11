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
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(BlockJUnit4ClassRunner.class)
public class MemoryUsageBlockResumeTest extends TestSupport implements Thread.UncaughtExceptionHandler {
    public int deliveryMode = DeliveryMode.PERSISTENT;

    private static final Logger LOG = LoggerFactory.getLogger(MemoryUsageBlockResumeTest.class);

    private static byte[] buf = new byte[4 * 1024];

    private static byte[] bigBuf = new byte[48 * 1024];

    private BrokerService broker;

    AtomicInteger messagesSent = new AtomicInteger(0);

    AtomicInteger messagesConsumed = new AtomicInteger(0);

    protected long messageReceiveTimeout = 10000L;

    Destination destination = new ActiveMQQueue("FooTwo");

    Destination bigDestination = new ActiveMQQueue("FooTwoBig");

    private String connectionUri;

    private final Vector<Throwable> exceptions = new Vector<Throwable>();

    @Test(timeout = 60 * 1000)
    public void testBlockByOtherResumeNoException() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionUri);
        // ensure more than on message can be pending when full
        factory.setProducerWindowSize((48 * 1024));
        // ensure messages are spooled to disk for this consumer
        ActiveMQPrefetchPolicy prefetch = new ActiveMQPrefetchPolicy();
        prefetch.setTopicPrefetch(10);
        factory.setPrefetchPolicy(prefetch);
        Connection consumerConnection = factory.createConnection();
        consumerConnection.start();
        Session consumerSession = consumerConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = consumerSession.createConsumer(bigDestination);
        final Connection producerConnection = factory.createConnection();
        producerConnection.start();
        final int fillWithBigCount = 10;
        Session session = producerConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(null);
        producer.setDeliveryMode(deliveryMode);
        for (int idx = 0; idx < fillWithBigCount; ++idx) {
            Message message = session.createTextMessage(((new String(MemoryUsageBlockResumeTest.bigBuf)) + idx));
            producer.send(bigDestination, message);
            messagesSent.incrementAndGet();
            MemoryUsageBlockResumeTest.LOG.info(((("After big: " + idx) + ", System Memory Usage ") + (broker.getSystemUsage().getMemoryUsage().getPercentUsage())));
        }
        // will block on pfc
        final int toSend = 20;
        Thread producingThread = new Thread("Producing thread") {
            @Override
            public void run() {
                try {
                    Session session = producerConnection.createSession(false, AUTO_ACKNOWLEDGE);
                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(deliveryMode);
                    for (int idx = 0; idx < toSend; ++idx) {
                        Message message = session.createTextMessage(((new String(MemoryUsageBlockResumeTest.buf)) + idx));
                        producer.send(destination, message);
                        messagesSent.incrementAndGet();
                        MemoryUsageBlockResumeTest.LOG.info(((("After little:" + idx) + ", System Memory Usage ") + (broker.getSystemUsage().getMemoryUsage().getPercentUsage())));
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        };
        producingThread.start();
        Thread producingThreadTwo = new Thread("Producing thread") {
            @Override
            public void run() {
                try {
                    Session session = producerConnection.createSession(false, AUTO_ACKNOWLEDGE);
                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(deliveryMode);
                    for (int idx = 0; idx < toSend; ++idx) {
                        Message message = session.createTextMessage(((new String(MemoryUsageBlockResumeTest.buf)) + idx));
                        producer.send(destination, message);
                        messagesSent.incrementAndGet();
                        MemoryUsageBlockResumeTest.LOG.info(((("After little:" + idx) + ", System Memory Usage ") + (broker.getSystemUsage().getMemoryUsage().getPercentUsage())));
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        };
        producingThreadTwo.start();
        Assert.assertTrue("producer has sent x in a reasonable time", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                MemoryUsageBlockResumeTest.LOG.info(((("Checking for : X sent, System Memory Usage " + (broker.getSystemUsage().getMemoryUsage().getPercentUsage())) + ", sent:  ") + (messagesSent)));
                return (messagesSent.get()) > 20;
            }
        }));
        MemoryUsageBlockResumeTest.LOG.info("Consuming from big q to allow delivery to smaller q from pending");
        int count = 0;
        Message m = null;
        for (; count < 10; count++) {
            Assert.assertTrue(((m = consumer.receive(messageReceiveTimeout)) != null));
            MemoryUsageBlockResumeTest.LOG.info(((((("Recieved Message (" + count) + "):") + m) + ", System Memory Usage ") + (broker.getSystemUsage().getMemoryUsage().getPercentUsage())));
            messagesConsumed.incrementAndGet();
        }
        consumer.close();
        producingThread.join();
        producingThreadTwo.join();
        Assert.assertEquals(("Incorrect number of Messages Sent: " + (messagesSent.get())), messagesSent.get(), (fillWithBigCount + (toSend * 2)));
        // consume all little messages
        consumer = consumerSession.createConsumer(destination);
        for (count = 0; count < (toSend * 2); count++) {
            Assert.assertTrue(((m = consumer.receive(messageReceiveTimeout)) != null));
            MemoryUsageBlockResumeTest.LOG.info(((((("Recieved Message (" + count) + "):") + m) + ", System Memory Usage ") + (broker.getSystemUsage().getMemoryUsage().getPercentUsage())));
            messagesConsumed.incrementAndGet();
        }
        Assert.assertEquals(("Incorrect number of Messages consumed: " + (messagesConsumed.get())), messagesSent.get(), messagesConsumed.get());
        // assertTrue("no exceptions: " + exceptions, exceptions.isEmpty());
    }
}

