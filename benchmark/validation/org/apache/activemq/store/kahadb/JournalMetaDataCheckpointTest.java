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


import MessageDatabase.Metadata;
import java.io.IOException;
import javax.jms.Destination;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JournalMetaDataCheckpointTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalMetaDataCheckpointTest.class);

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private final String payload = new String(new byte[1024]);

    private BrokerService broker = null;

    private final Destination destination = new ActiveMQQueue("Test");

    private KahaDBPersistenceAdapter adapter;

    @Test
    public void testRecoveryOnDeleteFailureMetaDataOk() throws Exception {
        startBroker();
        int sent = produceMessagesToConsumeMultipleDataFiles(50);
        int numFilesAfterSend = getNumberOfJournalFiles();
        JournalMetaDataCheckpointTest.LOG.info("Sent {}, Num journal files: {} ", sent, numFilesAfterSend);
        Assert.assertTrue(("more than x files: " + numFilesAfterSend), (numFilesAfterSend > 4));
        int received = tryConsume(destination, (sent / 2));
        Assert.assertEquals("all message received", (sent / 2), received);
        int numFilesAfterRestart = getNumberOfJournalFiles();
        JournalMetaDataCheckpointTest.LOG.info(("Num journal files before gc: " + numFilesAfterRestart));
        // force gc
        getStore().checkpoint(true);
        int numFilesAfterGC = getNumberOfJournalFiles();
        Assert.assertEquals("all message received", (sent / 2), received);
        JournalMetaDataCheckpointTest.LOG.info(("Num journal files after restart nd gc: " + numFilesAfterGC));
        Assert.assertTrue("Gc has happened", (numFilesAfterGC < numFilesAfterRestart));
        // verify metadata is correct on disk
        final MessageDatabase[] fromDiskMetaData = new MessageDatabase.Metadata[1];
        final KahaDBStore messageStore = ((KahaDBPersistenceAdapter) (broker.getPersistenceAdapter())).getStore();
        // need to avoid cache and in-progress writes of existing pageFile
        PageFile fromDiskPageFile = new PageFile(messageStore.getIndexDirectory(), "db");
        fromDiskPageFile.setEnablePageCaching(false);
        fromDiskPageFile.setEnableRecoveryFile(false);
        fromDiskPageFile.load();
        fromDiskPageFile.tx().execute(new Transaction.Closure<IOException>() {
            @Override
            public void execute(Transaction tx) throws IOException {
                Page<MessageDatabase.Metadata> page = tx.load(0, messageStore.metadataMarshaller);
                fromDiskMetaData[0] = page.get();
            }
        });
        Assert.assertEquals("location is uptodate", messageStore.getMetadata().ackMessageFileMapLocation, fromDiskMetaData[0].ackMessageFileMapLocation);
    }
}

