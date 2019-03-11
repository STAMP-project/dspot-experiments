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


import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JournalFdRecoveryTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalFdRecoveryTest.class);

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private String payload;

    private ActiveMQConnectionFactory cf = null;

    private BrokerService broker = null;

    private final Destination destination = new ActiveMQQueue("Test");

    private String connectionUri;

    private KahaDBPersistenceAdapter adapter;

    public byte fill = Byte.valueOf("3");

    private int maxJournalSizeBytes;

    @Test
    public void testStopOnPageInIOError() throws Exception {
        startBroker();
        int sent = produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        JournalFdRecoveryTest.LOG.info(("Num journal files: " + numFiles));
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 4));
        File dataDir = broker.getPersistenceAdapter().getDirectory();
        for (int i = 2; i < 4; i++) {
            whackDataFile(dataDir, i);
        }
        final CountDownLatch gotShutdown = new CountDownLatch(1);
        broker.addShutdownHook(new Runnable() {
            @Override
            public void run() {
                gotShutdown.countDown();
            }
        });
        int received = tryConsume(destination, sent);
        Assert.assertNotEquals("not all message received", sent, received);
        Assert.assertTrue("broker got shutdown on page in error", gotShutdown.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testRecoveryAfterCorruption() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        JournalFdRecoveryTest.LOG.info(("Num journal files: " + numFiles));
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 4));
        File dataDir = broker.getPersistenceAdapter().getDirectory();
        if ((broker) != null) {
            broker.stop();
            broker.waitUntilStopped();
        }
        long afterStop = totalOpenFileDescriptorCount(broker);
        whackIndex(dataDir);
        JournalFdRecoveryTest.LOG.info(("Num Open files with broker stopped: " + afterStop));
        doStartBroker(false);
        JournalFdRecoveryTest.LOG.info(("Journal read pool: " + (adapter.getStore().getJournal().getAccessorPool().size())));
        Assert.assertEquals("one entry in the pool on start", 1, adapter.getStore().getJournal().getAccessorPool().size());
        long afterRecovery = totalOpenFileDescriptorCount(broker);
        JournalFdRecoveryTest.LOG.info(("Num Open files with broker recovered: " + afterRecovery));
    }

    @Test
    public void testRecoveryWithMissingMssagesWithValidAcks() throws Exception {
        doCreateBroker(true);
        adapter.setCheckpointInterval(50000);
        adapter.setCleanupInterval(50000);
        broker.start();
        int toSend = 50;
        produceMessagesToConsumeMultipleDataFiles(toSend);
        int numFiles = getNumberOfJournalFiles();
        JournalFdRecoveryTest.LOG.info(("Num files: " + numFiles));
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 5));
        Assert.assertEquals("Drain", 30, tryConsume(destination, 30));
        JournalFdRecoveryTest.LOG.info(("Num files after stopped: " + (getNumberOfJournalFiles())));
        File dataDir = broker.getPersistenceAdapter().getDirectory();
        broker.stop();
        broker.waitUntilStopped();
        whackDataFile(dataDir, 4);
        whackIndex(dataDir);
        doStartBroker(false);
        JournalFdRecoveryTest.LOG.info(("Num files after restarted: " + (getNumberOfJournalFiles())));
        Assert.assertEquals("Empty?", 18, tryConsume(destination, 20));
        Assert.assertEquals("no queue size ", 0L, getDestinationStatistics().getMessages().getCount());
    }

    @Test
    public void testRecoveryCheckSpeedSmallMessages() throws Exception {
        maxJournalSizeBytes = Journal.DEFAULT_MAX_FILE_LENGTH;
        doCreateBroker(true);
        broker.start();
        int toSend = 20000;
        payload = new String(new byte[100]);
        produceMessagesToConsumeMultipleDataFiles(toSend);
        broker.stop();
        broker.waitUntilStopped();
        Instant b = Instant.now();
        doStartBroker(false);
        Instant e = Instant.now();
        Duration timeElapsed = Duration.between(b, e);
        JournalFdRecoveryTest.LOG.info(("Elapsed: " + timeElapsed));
    }
}

