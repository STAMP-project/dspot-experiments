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


import Session.AUTO_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class MessageGroupReconnectDistributionTest {
    public static final Logger LOG = LoggerFactory.getLogger(MessageGroupReconnectDistributionTest.class);

    final Random random = new Random();

    protected Connection connection;

    protected Session session;

    protected MessageProducer producer;

    protected ActiveMQQueue destination = new ActiveMQQueue("GroupQ");

    protected TransportConnector connector;

    ActiveMQConnectionFactory connFactory;

    BrokerService broker;

    int numMessages = 10000;

    int groupSize = 10;

    int batchSize = 20;

    @Parameterized.Parameter(0)
    public int numConsumers = 4;

    @Parameterized.Parameter(1)
    public boolean consumerPriority = true;

    @Test(timeout = (5 * 60) * 1000)
    public void testReconnect() throws Exception {
        final AtomicLong totalConsumed = new AtomicLong(0);
        ExecutorService executorService = Executors.newFixedThreadPool(numConsumers);
        final ArrayList<AtomicLong> consumedCounters = new ArrayList<AtomicLong>(numConsumers);
        final ArrayList<AtomicLong> batchCounters = new ArrayList<AtomicLong>(numConsumers);
        for (int i = 0; i < (numConsumers); i++) {
            consumedCounters.add(new AtomicLong(0L));
            batchCounters.add(new AtomicLong(0L));
            final int id = i;
            executorService.submit(new Runnable() {
                int getBatchSize() {
                    return (id + 1) * (batchSize);
                }

                @Override
                public void run() {
                    try {
                        Session connectionSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
                        int batchSize = getBatchSize();
                        MessageConsumer messageConsumer = connectionSession.createConsumer(destWithPrefetch(destination));
                        Message message;
                        AtomicLong consumed = consumedCounters.get(id);
                        AtomicLong batches = batchCounters.get(id);
                        MessageGroupReconnectDistributionTest.LOG.info(((((((("Consumer: " + id) + ", batchSize:") + batchSize) + ", totalConsumed:") + (totalConsumed.get())) + ", consumed:") + (consumed.get())));
                        while ((totalConsumed.get()) < (numMessages)) {
                            message = messageConsumer.receive(10000);
                            if (message == null) {
                                MessageGroupReconnectDistributionTest.LOG.info(((((((("Consumer: " + id) + ", batchSize:") + batchSize) + ", null message (totalConsumed:") + (totalConsumed.get())) + ") consumed:") + (consumed.get())));
                                messageConsumer.close();
                                if ((totalConsumed.get()) == (numMessages)) {
                                    break;
                                } else {
                                    batchSize = getBatchSize();
                                    messageConsumer = connectionSession.createConsumer(destWithPrefetch(destination));
                                    batches.incrementAndGet();
                                    continue;
                                }
                            }
                            consumed.incrementAndGet();
                            totalConsumed.incrementAndGet();
                            if (((consumed.get()) > 0) && (((consumed.intValue()) % batchSize) == 0)) {
                                messageConsumer.close();
                                batchSize = getBatchSize();
                                messageConsumer = connectionSession.createConsumer(destWithPrefetch(destination));
                                batches.incrementAndGet();
                            }
                        } 
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            TimeUnit.MILLISECONDS.sleep(200);
        }
        TimeUnit.SECONDS.sleep(1);
        produceMessages(numMessages);
        executorService.shutdown();
        Assert.assertTrue("threads done on time", executorService.awaitTermination(10, TimeUnit.MINUTES));
        Assert.assertEquals("All consumed", numMessages, totalConsumed.intValue());
        MessageGroupReconnectDistributionTest.LOG.info(("Distribution: " + consumedCounters));
        MessageGroupReconnectDistributionTest.LOG.info(("Batches: " + batchCounters));
        double max = (consumedCounters.get(0).longValue()) * 1.5;
        double min = (consumedCounters.get(0).longValue()) * 0.5;
        for (AtomicLong l : consumedCounters) {
            Assert.assertTrue(((("Even +/- 50% distribution on consumed:" + consumedCounters) + ", outlier:") + (l.get())), (((l.longValue()) < max) && ((l.longValue()) > min)));
        }
    }
}

