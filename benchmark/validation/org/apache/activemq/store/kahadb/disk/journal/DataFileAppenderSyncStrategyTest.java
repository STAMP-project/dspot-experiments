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
package org.apache.activemq.store.kahadb.disk.journal;


import JournalDiskSyncStrategy.ALWAYS;
import JournalDiskSyncStrategy.NEVER;
import JournalDiskSyncStrategy.PERIODIC;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;


public class DataFileAppenderSyncStrategyTest {
    @Rule
    public TemporaryFolder dataFileDir = new TemporaryFolder(new File("target"));

    @Rule
    public Timeout globalTimeout = new Timeout(10, TimeUnit.SECONDS);

    private KahaDBStore store;

    private int defaultJournalLength = 10 * 1024;

    @Test
    public void testPeriodicSync() throws Exception {
        store = configureStore(PERIODIC);
        store.start();
        final Journal journal = store.getJournal();
        DataFileAppender appender = ((DataFileAppender) (journal.appender));
        Assert.assertTrue(appender.periodicSync);
    }

    @Test
    public void testAlwaysSync() throws Exception {
        store = configureStore(ALWAYS);
        store.start();
        final Journal journal = store.getJournal();
        DataFileAppender appender = ((DataFileAppender) (journal.appender));
        Assert.assertFalse(appender.periodicSync);
    }

    @Test
    public void testNeverSync() throws Exception {
        store = configureStore(NEVER);
        store.start();
        final Journal journal = store.getJournal();
        DataFileAppender appender = ((DataFileAppender) (journal.appender));
        Assert.assertFalse(appender.periodicSync);
    }
}

