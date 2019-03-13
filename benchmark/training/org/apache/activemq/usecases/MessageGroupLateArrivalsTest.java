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


import Session.CLIENT_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(BlockJUnit4ClassRunner.class)
public class MessageGroupLateArrivalsTest {
    public static final Logger log = LoggerFactory.getLogger(MessageGroupLateArrivalsTest.class);

    protected Connection connection;

    protected Session session;

    protected MessageProducer producer;

    protected Destination destination;

    BrokerService broker;

    protected TransportConnector connector;

    protected HashMap<String, Integer> messageCount = new HashMap<String, Integer>();

    protected HashMap<String, Set<String>> messageGroups = new HashMap<String, Set<String>>();

    @Test(timeout = 30 * 1000)
    public void testConsumersLateToThePartyGetSomeNewGroups() throws Exception {
        final int perBatch = 3;
        int[] counters = new int[]{ perBatch, perBatch, perBatch };
        CountDownLatch startSignal = new CountDownLatch(0);
        CountDownLatch doneSignal = new CountDownLatch(3);
        CountDownLatch worker1Started = new CountDownLatch(1);
        CountDownLatch worker2Started = new CountDownLatch(1);
        CountDownLatch worker3Started = new CountDownLatch(1);
        messageCount.put("worker1", 0);
        messageGroups.put("worker1", new HashSet<String>());
        MessageGroupLateArrivalsTest.Worker worker1 = new MessageGroupLateArrivalsTest.Worker(connection, destination, "worker1", startSignal, doneSignal, counters, messageCount, messageGroups, worker1Started);
        messageCount.put("worker2", 0);
        messageGroups.put("worker2", new HashSet<String>());
        MessageGroupLateArrivalsTest.Worker worker2 = new MessageGroupLateArrivalsTest.Worker(connection, destination, "worker2", startSignal, doneSignal, counters, messageCount, messageGroups, worker2Started);
        messageCount.put("worker3", 0);
        messageGroups.put("worker3", new HashSet<String>());
        MessageGroupLateArrivalsTest.Worker worker3 = new MessageGroupLateArrivalsTest.Worker(connection, destination, "worker3", startSignal, doneSignal, counters, messageCount, messageGroups, worker3Started);
        new Thread(worker1).start();
        new Thread(worker2).start();
        worker1Started.await();
        worker2Started.await();
        for (int i = 0; i < perBatch; i++) {
            Message msga = session.createTextMessage("hello a");
            msga.setStringProperty("JMSXGroupID", "A");
            producer.send(msga);
            Message msgb = session.createTextMessage("hello b");
            msgb.setStringProperty("JMSXGroupID", "B");
            producer.send(msgb);
        }
        // ensure this chap, late to the party gets a new group
        new Thread(worker3).start();
        // wait for presence before new group
        worker3Started.await();
        for (int i = 0; i < perBatch; i++) {
            Message msgc = session.createTextMessage("hello c");
            msgc.setStringProperty("JMSXGroupID", "C");
            producer.send(msgc);
        }
        doneSignal.await();
        List<String> workers = new ArrayList<String>(messageCount.keySet());
        Collections.sort(workers);
        for (String worker : workers) {
            MessageGroupLateArrivalsTest.log.info(((((("worker " + worker) + " received ") + (messageCount.get(worker))) + " messages from groups ") + (messageGroups.get(worker))));
        }
        for (String worker : workers) {
            Assert.assertEquals(((((("worker " + worker) + " received ") + (messageCount.get(worker))) + " messages from groups ") + (messageGroups.get(worker))), perBatch, messageCount.get(worker).intValue());
            Assert.assertEquals(((((("worker " + worker) + " received ") + (messageCount.get(worker))) + " messages from groups ") + (messageGroups.get(worker))), 1, messageGroups.get(worker).size());
        }
    }

    @Test(timeout = 30 * 1000)
    public void testConsumerLateToBigPartyGetsNewGroup() throws Exception {
        final int perBatch = 2;
        int[] counters = new int[]{ perBatch, perBatch, perBatch };
        CountDownLatch startSignal = new CountDownLatch(0);
        CountDownLatch doneSignal = new CountDownLatch(2);
        CountDownLatch worker1Started = new CountDownLatch(1);
        CountDownLatch worker2Started = new CountDownLatch(1);
        messageCount.put("worker1", 0);
        messageGroups.put("worker1", new HashSet<String>());
        MessageGroupLateArrivalsTest.Worker worker1 = new MessageGroupLateArrivalsTest.Worker(connection, destination, "worker1", startSignal, doneSignal, counters, messageCount, messageGroups, worker1Started);
        messageCount.put("worker2", 0);
        messageGroups.put("worker2", new HashSet<String>());
        MessageGroupLateArrivalsTest.Worker worker2 = new MessageGroupLateArrivalsTest.Worker(connection, destination, "worker2", startSignal, doneSignal, counters, messageCount, messageGroups, worker2Started);
        new Thread(worker1).start();
        for (int i = 0; i < perBatch; i++) {
            Message msga = session.createTextMessage("hello c");
            msga.setStringProperty("JMSXGroupID", "A");
            producer.send(msga);
            Message msgb = session.createTextMessage("hello b");
            msgb.setStringProperty("JMSXGroupID", "B");
            producer.send(msgb);
        }
        // ensure this chap, late to the party gets a new group
        new Thread(worker2).start();
        // wait for presence before new group
        worker2Started.await();
        for (int i = 0; i < perBatch; i++) {
            Message msgc = session.createTextMessage("hello a");
            msgc.setStringProperty("JMSXGroupID", "C");
            producer.send(msgc);
        }
        doneSignal.await();
        MessageGroupLateArrivalsTest.log.info(((("worker1  received " + (messageCount.get("worker1"))) + " messages from groups ") + (messageGroups.get("worker1"))));
        Assert.assertEquals(((("worker1 received " + (messageCount.get("worker1"))) + " messages from groups ") + (messageGroups.get("worker1"))), (2 * perBatch), messageCount.get("worker1").intValue());
        Assert.assertEquals(((("worker1 received " + (messageCount.get("worker1"))) + " messages from groups ") + (messageGroups.get("worker1"))), 2, messageGroups.get("worker1").size());
        MessageGroupLateArrivalsTest.log.info(((("worker2  received " + (messageCount.get("worker2"))) + " messages from groups ") + (messageGroups.get("worker2"))));
        Assert.assertEquals(((("worker2 received " + (messageCount.get("worker2"))) + " messages from groups ") + (messageGroups.get("worker2"))), (2 * perBatch), messageCount.get("worker1").intValue());
        Assert.assertEquals(((("worker2 received " + (messageCount.get("worker2"))) + " messages from groups ") + (messageGroups.get("worker2"))), 1, messageGroups.get("worker2").size());
    }

    private static final class Worker implements Runnable {
        private Connection connection = null;

        private Destination queueName = null;

        private String workerName = null;

        private CountDownLatch startSignal = null;

        private CountDownLatch doneSignal = null;

        private CountDownLatch workerStarted = null;

        private int[] counters = null;

        private final HashMap<String, Integer> messageCount;

        private final HashMap<String, Set<String>> messageGroups;

        private Worker(Connection connection, Destination queueName, String workerName, CountDownLatch startSignal, CountDownLatch doneSignal, int[] counters, HashMap<String, Integer> messageCount, HashMap<String, Set<String>> messageGroups, CountDownLatch workerStarted) {
            this.connection = connection;
            this.queueName = queueName;
            this.workerName = workerName;
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.counters = counters;
            this.messageCount = messageCount;
            this.messageGroups = messageGroups;
            this.workerStarted = workerStarted;
        }

        private void update(String group) {
            int msgCount = messageCount.get(workerName);
            messageCount.put(workerName, (msgCount + 1));
            Set<String> groups = messageGroups.get(workerName);
            groups.add(group);
            messageGroups.put(workerName, groups);
        }

        @Override
        public void run() {
            try {
                startSignal.await();
                MessageGroupLateArrivalsTest.log.info(workerName);
                Session sess = connection.createSession(false, CLIENT_ACKNOWLEDGE);
                MessageConsumer consumer = sess.createConsumer(queueName);
                workerStarted.countDown();
                while (true) {
                    if ((((counters[0]) == 0) && ((counters[1]) == 0)) && ((counters[2]) == 0)) {
                        doneSignal.countDown();
                        MessageGroupLateArrivalsTest.log.info(((workerName) + " done..."));
                        break;
                    }
                    Message msg = consumer.receive(500);
                    if (msg == null)
                        continue;

                    msg.acknowledge();
                    String group = msg.getStringProperty("JMSXGroupID");
                    msg.getBooleanProperty("JMSXGroupFirstForConsumer");
                    if ("A".equals(group)) {
                        --(counters[0]);
                        update(group);
                    } else
                        if ("B".equals(group)) {
                            --(counters[1]);
                            update(group);
                        } else
                            if ("C".equals(group)) {
                                --(counters[2]);
                                update(group);
                            } else {
                                MessageGroupLateArrivalsTest.log.warn(((workerName) + ", unknown group"));
                            }


                    if ((((counters[0]) != 0) || ((counters[1]) != 0)) || ((counters[2]) != 0)) {
                        msg.acknowledge();
                    }
                } 
                consumer.close();
                sess.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

