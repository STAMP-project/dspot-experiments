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
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Destination;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.disk.journal.DataFile;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.RecoverableRandomAccessFile;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getRootLogger;


public class JournalCorruptionEofIndexRecoveryTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalCorruptionEofIndexRecoveryTest.class);

    private ActiveMQConnectionFactory cf = null;

    private BrokerService broker = null;

    private String connectionUri;

    private KahaDBPersistenceAdapter adapter;

    private boolean ignoreMissingJournalFiles = false;

    private int journalMaxBatchSize;

    private final Destination destination = new ActiveMQQueue("Test");

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private final String payload = new String(new byte[1024]);

    File brokerDataDir = null;

    @Test
    public void testNoRestartOnCorruptJournal() throws Exception {
        ignoreMissingJournalFiles = false;
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 2));
        corruptBatchEndEof(3);
        try {
            restartBroker(true);
            Assert.fail("Expect failure to start with corrupt journal");
        } catch (Exception expected) {
        }
    }

    @Test
    public void testRecoveryAfterCorruptionEof() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 2));
        corruptBatchEndEof(3);
        restartBroker(false);
        Assert.assertEquals("missing one message", 49, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 49, drainQueue(49));
    }

    @Test
    public void testRecoveryAfterCorruptionMetadataLocation() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 2));
        broker.getPersistenceAdapter().checkpoint(true);
        Location location = getStore().getMetadata().producerSequenceIdTrackerLocation;
        DataFile dataFile = getStore().getJournal().getFileMap().get(Integer.valueOf(location.getDataFileId()));
        RecoverableRandomAccessFile randomAccessFile = dataFile.openRandomAccessFile();
        randomAccessFile.seek(location.getOffset());
        randomAccessFile.writeInt(Integer.MAX_VALUE);
        randomAccessFile.getChannel().force(true);
        getStore().getJournal().close();
        try {
            broker.stop();
            broker.waitUntilStopped();
        } catch (Exception expected) {
        } finally {
            broker = null;
        }
        AtomicBoolean trappedExpectedLogMessage = new AtomicBoolean(false);
        DefaultTestAppender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((((event.getLevel()) == (Level.WARN)) && (event.getRenderedMessage().contains("Cannot recover message audit"))) && (event.getThrowableInformation().getThrowable().getLocalizedMessage().contains("Invalid location size"))) {
                    trappedExpectedLogMessage.set(true);
                }
            }
        };
        getRootLogger().addAppender(appender);
        try {
            restartBroker(false);
        } finally {
            getRootLogger().removeAppender(appender);
        }
        Assert.assertEquals("no missing message", 50, broker.getAdminView().getTotalMessageCount());
        Assert.assertTrue("Did replay records on invalid location size", trappedExpectedLogMessage.get());
    }

    @Test
    public void testRecoveryAfterCorruptionCheckSum() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(4);
        corruptBatchCheckSumSplash(1);
        restartBroker(true);
        Assert.assertEquals("missing one message", 3, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 3, drainQueue(4));
    }

    @Test
    public void testRecoveryAfterCorruptionCheckSumExistingIndex() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(4);
        corruptBatchCheckSumSplash(1);
        restartBroker(false);
        Assert.assertEquals("unnoticed", 4, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 0, drainQueue(4));
        // force recover index and loose one message
        restartBroker(false, true);
        Assert.assertEquals("missing one index recreation", 3, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 3, drainQueue(4));
    }

    @Test
    public void testRecoverIndex() throws Exception {
        startBroker();
        final int numToSend = 4;
        produceMessagesToConsumeMultipleDataFiles(numToSend);
        // force journal replay by whacking the index
        restartBroker(false, true);
        Assert.assertEquals("Drain", numToSend, drainQueue(numToSend));
    }

    @Test
    public void testRecoverIndexWithSmallBatch() throws Exception {
        journalMaxBatchSize = 2 * 1024;
        startBroker();
        final int numToSend = 4;
        produceMessagesToConsumeMultipleDataFiles(numToSend);
        // force journal replay by whacking the index
        restartBroker(false, true);
        Assert.assertEquals("Drain", numToSend, drainQueue(numToSend));
    }

    @Test
    public void testRecoveryAfterProducerAuditLocationCorrupt() throws Exception {
        doTestRecoveryAfterLocationCorrupt(false);
    }

    @Test
    public void testRecoveryAfterAckMapLocationCorrupt() throws Exception {
        doTestRecoveryAfterLocationCorrupt(true);
    }
}

