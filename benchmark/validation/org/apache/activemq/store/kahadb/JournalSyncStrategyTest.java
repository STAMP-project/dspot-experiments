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


import JournalDiskSyncStrategy.ALWAYS;
import JournalDiskSyncStrategy.NEVER;
import JournalDiskSyncStrategy.PERIODIC;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;


public class JournalSyncStrategyTest {
    @Rule
    public TemporaryFolder dataFileDir = new TemporaryFolder(new File("target"));

    @Rule
    public Timeout globalTimeout = new Timeout(10, TimeUnit.SECONDS);

    private KahaDBStore store;

    private int defaultJournalLength = 10 * 1024;

    @Test
    public void testPeriodicSync() throws Exception {
        store = configureStore(PERIODIC);
        store.setJournalDiskSyncInterval(800);
        store.start();
        final Journal journal = store.getJournal();
        Assert.assertTrue(journal.isJournalDiskSyncPeriodic());
        Assert.assertFalse(store.isEnableJournalDiskSyncs());
        Assert.assertEquals(store.getJournalDiskSyncStrategy(), PERIODIC.name());
        Assert.assertEquals(store.getJournalDiskSyncStrategyEnum(), PERIODIC);
        Assert.assertEquals(store.getJournal().getJournalDiskSyncStrategy(), PERIODIC);
        Assert.assertEquals(store.getJournalDiskSyncInterval(), 800);
        Location l = store.lastAsyncJournalUpdate.get();
        // write a message to the store
        MessageStore messageStore = store.createQueueMessageStore(new ActiveMQQueue("test"));
        writeMessage(messageStore, 1);
        // make sure message write causes the lastAsyncJournalUpdate to be set with a new value
        Assert.assertFalse(store.lastAsyncJournalUpdate.get().equals(l));
    }

    @Test
    public void testAlwaysSync() throws Exception {
        store = configureStore(ALWAYS);
        store.start();
        Assert.assertFalse(store.getJournal().isJournalDiskSyncPeriodic());
        Assert.assertTrue(store.isEnableJournalDiskSyncs());
        Assert.assertEquals(store.getJournalDiskSyncStrategy(), ALWAYS.name());
        Assert.assertEquals(store.getJournalDiskSyncStrategyEnum(), ALWAYS);
        Assert.assertEquals(store.getJournal().getJournalDiskSyncStrategy(), ALWAYS);
        MessageStore messageStore = store.createQueueMessageStore(new ActiveMQQueue("test"));
        writeMessage(messageStore, 1);
        Assert.assertNull(store.lastAsyncJournalUpdate.get());
    }

    @Test
    public void testNeverSync() throws Exception {
        store = configureStore(NEVER);
        store.start();
        Assert.assertFalse(store.getJournal().isJournalDiskSyncPeriodic());
        Assert.assertFalse(store.isEnableJournalDiskSyncs());
        Assert.assertEquals(store.getJournalDiskSyncStrategy(), NEVER.name());
        Assert.assertEquals(store.getJournalDiskSyncStrategyEnum(), NEVER);
        Assert.assertEquals(store.getJournal().getJournalDiskSyncStrategy(), NEVER);
        MessageStore messageStore = store.createQueueMessageStore(new ActiveMQQueue("test"));
        writeMessage(messageStore, 1);
        Assert.assertNull(store.lastAsyncJournalUpdate.get());
    }
}

