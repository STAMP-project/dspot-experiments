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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.util.MessageIdList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class ConcurrentProducerDurableConsumerTest extends TestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentProducerDurableConsumerTest.class);

    private final int consumerCount = 5;

    BrokerService broker;

    protected List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());

    protected Map<MessageConsumer, ConcurrentProducerDurableConsumerTest.TimedMessageListener> consumers = new HashMap<MessageConsumer, ConcurrentProducerDurableConsumerTest.TimedMessageListener>();

    protected MessageIdList allMessagesList = new MessageIdList();

    private final int messageSize = 1024;

    private final TestSupport.PersistenceAdapterChoice persistenceAdapterChoice;

    public ConcurrentProducerDurableConsumerTest(TestSupport.PersistenceAdapterChoice choice) {
        this.persistenceAdapterChoice = choice;
    }

    @Test(timeout = 120000)
    public void testSendRateWithActivatingConsumers() throws Exception {
        final Destination destination = createDestination();
        final ConnectionFactory factory = createConnectionFactory();
        startInactiveConsumers(factory, destination);
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = createMessageProducer(session, destination);
        // preload the durable consumers
        double[] inactiveConsumerStats = produceMessages(destination, 500, 10, session, producer, null);
        ConcurrentProducerDurableConsumerTest.LOG.info(((((("With inactive consumers: ave: " + (inactiveConsumerStats[1])) + ", max: ") + (inactiveConsumerStats[0])) + ", multiplier: ") + ((inactiveConsumerStats[0]) / (inactiveConsumerStats[1]))));
        // periodically start a durable sub that has a backlog
        final int consumersToActivate = 5;
        final Object addConsumerSignal = new Object();
        Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, ("ActivateConsumer" + (this)));
            }
        }).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageConsumer consumer = null;
                    for (int i = 0; i < consumersToActivate; i++) {
                        ConcurrentProducerDurableConsumerTest.LOG.info("Waiting for add signal from producer...");
                        synchronized(addConsumerSignal) {
                            addConsumerSignal.wait(((30 * 60) * 1000));
                        }
                        ConcurrentProducerDurableConsumerTest.TimedMessageListener listener = new ConcurrentProducerDurableConsumerTest.TimedMessageListener();
                        consumer = createDurableSubscriber(factory.createConnection(), destination, ("consumer" + (i + 1)));
                        ConcurrentProducerDurableConsumerTest.LOG.info(("Created consumer " + consumer));
                        consumer.setMessageListener(listener);
                        consumers.put(consumer, listener);
                    }
                } catch (Exception e) {
                    ConcurrentProducerDurableConsumerTest.LOG.error("failed to start consumer", e);
                }
            }
        });
        double[] statsWithActive = produceMessages(destination, 500, 10, session, producer, addConsumerSignal);
        ConcurrentProducerDurableConsumerTest.LOG.info((((((" with concurrent activate, ave: " + (statsWithActive[1])) + ", max: ") + (statsWithActive[0])) + ", multiplier: ") + ((statsWithActive[0]) / (statsWithActive[1]))));
        while ((consumers.size()) < consumersToActivate) {
            TimeUnit.SECONDS.sleep(2);
        } 
        long timeToFirstAccumulator = 0;
        for (ConcurrentProducerDurableConsumerTest.TimedMessageListener listener : consumers.values()) {
            long time = listener.getFirstReceipt();
            timeToFirstAccumulator += time;
            ConcurrentProducerDurableConsumerTest.LOG.info(("Time to first " + time));
        }
        ConcurrentProducerDurableConsumerTest.LOG.info(("Ave time to first message =" + (timeToFirstAccumulator / (consumers.size()))));
        for (ConcurrentProducerDurableConsumerTest.TimedMessageListener listener : consumers.values()) {
            ConcurrentProducerDurableConsumerTest.LOG.info(((("Ave batch receipt time: " + (listener.waitForReceivedLimit(10000))) + " max receipt: ") + (listener.maxReceiptTime)));
        }
        // compare no active to active
        ConcurrentProducerDurableConsumerTest.LOG.info(((((("Ave send time with active: " + (statsWithActive[1])) + " as multiplier of ave with none active: ") + (inactiveConsumerStats[1])) + ", multiplier=") + ((statsWithActive[1]) / (inactiveConsumerStats[1]))));
        assertTrue(((((("Ave send time with active: " + (statsWithActive[1])) + " within reasonable multpler of ave with none active: ") + (inactiveConsumerStats[1])) + ", multiplier ") + ((statsWithActive[1]) / (inactiveConsumerStats[1]))), ((statsWithActive[1]) < (15 * (inactiveConsumerStats[1]))));
    }

    class TimedMessageListener implements MessageListener {
        final int batchSize = 1000;

        CountDownLatch firstReceiptLatch = new CountDownLatch(1);

        long mark = System.currentTimeMillis();

        long firstReceipt = 0L;

        long receiptAccumulator = 0;

        long batchReceiptAccumulator = 0;

        long maxReceiptTime = 0;

        AtomicLong count = new AtomicLong(0);

        Map<Integer, MessageIdList> messageLists = new ConcurrentHashMap<Integer, MessageIdList>(new HashMap<Integer, MessageIdList>());

        @Override
        public void onMessage(Message message) {
            final long current = System.currentTimeMillis();
            final long duration = current - (mark);
            receiptAccumulator += duration;
            int priority = 0;
            try {
                priority = message.getJMSPriority();
            } catch (JMSException ignored) {
            }
            if (!(messageLists.containsKey(priority))) {
                MessageIdList perPriorityList = new MessageIdList();
                perPriorityList.setParent(allMessagesList);
                messageLists.put(priority, perPriorityList);
            }
            messageLists.get(priority).onMessage(message);
            if ((count.incrementAndGet()) == 1) {
                firstReceipt = duration;
                firstReceiptLatch.countDown();
                ConcurrentProducerDurableConsumerTest.LOG.info((("First receipt in " + (firstReceipt)) + "ms"));
            } else
                if (((count.get()) % (batchSize)) == 0) {
                    ConcurrentProducerDurableConsumerTest.LOG.info((((((("Consumed " + (count.get())) + " in ") + (batchReceiptAccumulator)) + "ms") + ", priority:") + priority));
                    batchReceiptAccumulator = 0;
                }

            maxReceiptTime = Math.max(maxReceiptTime, duration);
            receiptAccumulator += duration;
            batchReceiptAccumulator += duration;
            mark = current;
        }

        long getMessageCount() {
            return count.get();
        }

        long getFirstReceipt() throws Exception {
            firstReceiptLatch.await(30, TimeUnit.SECONDS);
            return firstReceipt;
        }

        public long waitForReceivedLimit(long limit) throws Exception {
            final long expiry = (System.currentTimeMillis()) + ((30 * 60) * 1000);
            while ((count.get()) < limit) {
                if ((System.currentTimeMillis()) > expiry) {
                    throw new RuntimeException(("Expired waiting for X messages, " + limit));
                }
                TimeUnit.SECONDS.sleep(2);
                String missing = findFirstMissingMessage();
                if (missing != null) {
                    ConcurrentProducerDurableConsumerTest.LOG.info(("first missing = " + missing));
                    throw new RuntimeException(("We have a missing message. " + missing));
                }
            } 
            return (receiptAccumulator) / (limit / (batchSize));
        }

        private String findFirstMissingMessage() {
            MessageId current = new MessageId();
            for (MessageIdList priorityList : messageLists.values()) {
                MessageId previous = null;
                for (String id : priorityList.getMessageIds()) {
                    current.setValue(id);
                    if (previous == null) {
                        previous = current.copy();
                    } else {
                        if ((((current.getProducerSequenceId()) - 1) != (previous.getProducerSequenceId())) && (((current.getProducerSequenceId()) - 10) != (previous.getProducerSequenceId()))) {
                            return (("Missing next after: " + previous) + ", got: ") + current;
                        } else {
                            previous = current.copy();
                        }
                    }
                }
            }
            return null;
        }
    }
}

