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
package org.apache.activemq.store.kahadb;


import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Destination;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KahaDBFastEnqueueTest {
    private static final Logger LOG = LoggerFactory.getLogger(KahaDBFastEnqueueTest.class);

    private BrokerService broker;

    private ActiveMQConnectionFactory connectionFactory;

    KahaDBPersistenceAdapter kahaDBPersistenceAdapter;

    private final Destination destination = new ActiveMQQueue("Test");

    private final String payloadString = new String(new byte[6 * 1024]);

    private final boolean useBytesMessage = true;

    private final int parallelProducer = 20;

    private final Vector<Exception> exceptions = new Vector<Exception>();

    long toSend = 10000;

    // use with:
    // -Xmx4g -Dorg.apache.kahadb.journal.appender.WRITE_STAT_WINDOW=10000 -Dorg.apache.kahadb.journal.CALLER_BUFFER_APPENDER=true
    @Test
    public void testPublishNoConsumer() throws Exception {
        startBroker(true, 10);
        final AtomicLong sharedCount = new AtomicLong(toSend);
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < (parallelProducer); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        publishMessages(sharedCount, 0);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.MINUTES);
        Assert.assertTrue("Producers done in time", executorService.isTerminated());
        Assert.assertTrue(("No exceptions: " + (exceptions)), exceptions.isEmpty());
        long totalSent = (toSend) * (payloadString.length());
        double duration = (System.currentTimeMillis()) - start;
        stopBroker();
        KahaDBFastEnqueueTest.LOG.info((("Duration:                " + duration) + "ms"));
        KahaDBFastEnqueueTest.LOG.info((("Rate:                       " + (((toSend) * 1000) / duration)) + "m/s"));
        KahaDBFastEnqueueTest.LOG.info(("Total send:             " + totalSent));
        KahaDBFastEnqueueTest.LOG.info(("Total journal write: " + (kahaDBPersistenceAdapter.getStore().getJournal().length())));
        KahaDBFastEnqueueTest.LOG.info(("Total index size " + (kahaDBPersistenceAdapter.getStore().getPageFile().getDiskSize())));
        KahaDBFastEnqueueTest.LOG.info(("Total store size: " + (kahaDBPersistenceAdapter.size())));
        KahaDBFastEnqueueTest.LOG.info((("Journal writes %:    " + (((kahaDBPersistenceAdapter.getStore().getJournal().length()) / ((double) (totalSent))) * 100)) + "%"));
        restartBroker(0, 1200000);
        consumeMessages(toSend);
    }

    @Test
    public void testPublishNoConsumerNoCheckpoint() throws Exception {
        toSend = 100;
        startBroker(true, 0);
        final AtomicLong sharedCount = new AtomicLong(toSend);
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < (parallelProducer); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        publishMessages(sharedCount, 0);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.MINUTES);
        Assert.assertTrue("Producers done in time", executorService.isTerminated());
        Assert.assertTrue(("No exceptions: " + (exceptions)), exceptions.isEmpty());
        long totalSent = (toSend) * (payloadString.length());
        broker.getAdminView().gc();
        double duration = (System.currentTimeMillis()) - start;
        stopBroker();
        KahaDBFastEnqueueTest.LOG.info((("Duration:                " + duration) + "ms"));
        KahaDBFastEnqueueTest.LOG.info((("Rate:                       " + (((toSend) * 1000) / duration)) + "m/s"));
        KahaDBFastEnqueueTest.LOG.info(("Total send:             " + totalSent));
        KahaDBFastEnqueueTest.LOG.info(("Total journal write: " + (kahaDBPersistenceAdapter.getStore().getJournal().length())));
        KahaDBFastEnqueueTest.LOG.info(("Total index size " + (kahaDBPersistenceAdapter.getStore().getPageFile().getDiskSize())));
        KahaDBFastEnqueueTest.LOG.info(("Total store size: " + (kahaDBPersistenceAdapter.size())));
        KahaDBFastEnqueueTest.LOG.info((("Journal writes %:    " + (((kahaDBPersistenceAdapter.getStore().getJournal().length()) / ((double) (totalSent))) * 100)) + "%"));
        restartBroker(0, 0);
        consumeMessages(toSend);
    }

    final double sampleRate = 100000;

    @Test
    public void testRollover() throws Exception {
        byte flip = 1;
        for (long i = 0; i < (Short.MAX_VALUE); i++) {
            Assert.assertEquals(("0 @:" + i), 0, (flip ^= 1));
            Assert.assertEquals(("1 @:" + i), 1, (flip ^= 1));
        }
    }
}

