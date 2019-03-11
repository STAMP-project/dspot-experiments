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
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;


/**
 *
 *
 * @author chirino
 */
public class KahaDBTest extends TestCase {
    public void testIgnoreMissingJournalfilesOptionSetFalse() throws Exception {
        KahaDBStore kaha = createStore(true);
        kaha.setJournalMaxFileLength((1024 * 100));
        TestCase.assertFalse(kaha.isIgnoreMissingJournalfiles());
        BrokerService broker = createBroker(kaha);
        sendMessages(1000);
        broker.stop();
        // Delete some journal files..
        assertExistsAndDelete(new File(kaha.getDirectory(), "db-4.log"));
        assertExistsAndDelete(new File(kaha.getDirectory(), "db-8.log"));
        kaha = createStore(false);
        kaha.setJournalMaxFileLength((1024 * 100));
        TestCase.assertFalse(kaha.isIgnoreMissingJournalfiles());
        try {
            broker = createBroker(kaha);
            TestCase.fail("expected IOException");
        } catch (IOException e) {
            TestCase.assertTrue(e.getMessage().startsWith("Detected missing/corrupt journal files"));
        }
    }

    public void testIgnoreMissingJournalfilesOptionSetTrue() throws Exception {
        KahaDBStore kaha = createStore(true);
        kaha.setJournalMaxFileLength((1024 * 100));
        TestCase.assertFalse(kaha.isIgnoreMissingJournalfiles());
        BrokerService broker = createBroker(kaha);
        sendMessages(1000);
        broker.stop();
        // Delete some journal files..
        assertExistsAndDelete(new File(kaha.getDirectory(), "db-4.log"));
        assertExistsAndDelete(new File(kaha.getDirectory(), "db-8.log"));
        kaha = createStore(false);
        kaha.setIgnoreMissingJournalfiles(true);
        kaha.setJournalMaxFileLength((1024 * 100));
        broker = createBroker(kaha);
        // We know we won't get all the messages but we should get most of them.
        int count = receiveMessages();
        TestCase.assertTrue((count > 800));
        TestCase.assertTrue((count < 1000));
        broker.stop();
    }

    public void testCheckCorruptionNotIgnored() throws Exception {
        KahaDBStore kaha = createStore(true);
        TestCase.assertTrue(kaha.isChecksumJournalFiles());
        TestCase.assertFalse(kaha.isCheckForCorruptJournalFiles());
        kaha.setJournalMaxFileLength((1024 * 100));
        kaha.setChecksumJournalFiles(true);
        BrokerService broker = createBroker(kaha);
        sendMessages(1000);
        broker.stop();
        // Modify/Corrupt some journal files..
        assertExistsAndCorrupt(new File(kaha.getDirectory(), "db-4.log"));
        assertExistsAndCorrupt(new File(kaha.getDirectory(), "db-8.log"));
        kaha = createStore(false);
        kaha.setJournalMaxFileLength((1024 * 100));
        kaha.setChecksumJournalFiles(true);
        kaha.setCheckForCorruptJournalFiles(true);
        TestCase.assertFalse(kaha.isIgnoreMissingJournalfiles());
        try {
            broker = createBroker(kaha);
            TestCase.fail("expected IOException");
        } catch (IOException e) {
            TestCase.assertTrue(e.getMessage().startsWith("Detected missing/corrupt journal files"));
        }
    }

    public void testMigrationOnNewDefaultForChecksumJournalFiles() throws Exception {
        KahaDBStore kaha = createStore(true);
        kaha.setChecksumJournalFiles(false);
        TestCase.assertFalse(kaha.isChecksumJournalFiles());
        TestCase.assertFalse(kaha.isCheckForCorruptJournalFiles());
        kaha.setJournalMaxFileLength((1024 * 100));
        BrokerService broker = createBroker(kaha);
        sendMessages(1000);
        broker.stop();
        kaha = createStore(false);
        kaha.setJournalMaxFileLength((1024 * 100));
        kaha.setCheckForCorruptJournalFiles(true);
        TestCase.assertFalse(kaha.isIgnoreMissingJournalfiles());
        createBroker(kaha);
        TestCase.assertEquals(1000, receiveMessages());
    }

    public void testCheckCorruptionIgnored() throws Exception {
        KahaDBStore kaha = createStore(true);
        kaha.setJournalMaxFileLength((1024 * 100));
        BrokerService broker = createBroker(kaha);
        sendMessages(1000);
        broker.stop();
        // Delete some journal files..
        assertExistsAndCorrupt(new File(kaha.getDirectory(), "db-4.log"));
        assertExistsAndCorrupt(new File(kaha.getDirectory(), "db-8.log"));
        kaha = createStore(false);
        kaha.setIgnoreMissingJournalfiles(true);
        kaha.setJournalMaxFileLength((1024 * 100));
        kaha.setCheckForCorruptJournalFiles(true);
        broker = createBroker(kaha);
        // We know we won't get all the messages but we should get most of them.
        int count = receiveMessages();
        TestCase.assertTrue(("Expected to received a min # of messages.. Got: " + count), (count > 990));
        TestCase.assertTrue((count < 1000));
        broker.stop();
    }

    public void testNoReplayOnStopStart() throws Exception {
        KahaDBStore kaha = createStore(true);
        BrokerService broker = createBroker(kaha);
        sendMessages(100);
        broker.stop();
        broker.waitUntilStopped();
        kaha = createStore(false);
        kaha.setCheckForCorruptJournalFiles(true);
        final AtomicBoolean didSomeRecovery = new AtomicBoolean(false);
        DefaultTestAppender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if (((event.getLevel()) == (Level.INFO)) && (event.getRenderedMessage().contains("Recovering from the journal @"))) {
                    didSomeRecovery.set(true);
                }
            }
        };
        Logger.getRootLogger().addAppender(appender);
        broker = createBroker(kaha);
        int count = receiveMessages();
        TestCase.assertEquals("Expected to received all messages.", count, 100);
        broker.stop();
        Logger.getRootLogger().removeAppender(appender);
        TestCase.assertFalse("Did not replay any records from the journal", didSomeRecovery.get());
    }
}

