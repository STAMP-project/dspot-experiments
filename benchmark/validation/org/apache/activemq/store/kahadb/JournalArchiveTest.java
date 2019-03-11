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
import java.security.Permission;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Destination;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JournalArchiveTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalArchiveTest.class);

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private final String payload = new String(new byte[1024]);

    private BrokerService broker = null;

    private final Destination destination = new ActiveMQQueue("Test");

    private KahaDBPersistenceAdapter adapter;

    @Test
    public void testRecoveryOnArchiveFailure() throws Exception {
        final AtomicInteger atomicInteger = new AtomicInteger();
        System.setSecurityManager(new SecurityManager() {
            public void checkPermission(Permission perm) {
            }

            public void checkPermission(Permission perm, Object context) {
            }

            public void checkWrite(String file) {
                if ((file.contains(Journal.DEFAULT_ARCHIVE_DIRECTORY)) && ((atomicInteger.incrementAndGet()) > 4)) {
                    throw new SecurityException(("No Perms to write to archive times:" + (atomicInteger.get())));
                }
            }
        });
        startBroker();
        int sent = produceMessagesToConsumeMultipleDataFiles(50);
        int numFilesAfterSend = getNumberOfJournalFiles();
        JournalArchiveTest.LOG.info(("Num journal files: " + numFilesAfterSend));
        Assert.assertTrue(("more than x files: " + numFilesAfterSend), (numFilesAfterSend > 4));
        final CountDownLatch gotShutdown = new CountDownLatch(1);
        broker.addShutdownHook(new Runnable() {
            @Override
            public void run() {
                gotShutdown.countDown();
            }
        });
        int received = tryConsume(destination, sent);
        Assert.assertEquals("all message received", sent, received);
        Assert.assertTrue("broker got shutdown on page in error", gotShutdown.await(10, TimeUnit.SECONDS));
        // no restrictions
        System.setSecurityManager(null);
        int numFilesAfterRestart = 0;
        try {
            // ensure we can restart after failure to archive
            doStartBroker(false);
            numFilesAfterRestart = getNumberOfJournalFiles();
            JournalArchiveTest.LOG.info(("Num journal files before gc: " + numFilesAfterRestart));
            // force gc
            getStore().checkpoint(true);
        } catch (Exception error) {
            JournalArchiveTest.LOG.error("Failed to restart!", error);
            Assert.fail("Failed to restart after failure to archive");
        }
        int numFilesAfterGC = getNumberOfJournalFiles();
        JournalArchiveTest.LOG.info(("Num journal files after restart nd gc: " + numFilesAfterGC));
        Assert.assertTrue("Gc has happened", (numFilesAfterGC < numFilesAfterRestart));
        Assert.assertTrue("Gc has worked", (numFilesAfterGC < 4));
        File archiveDirectory = getStore().getJournal().getDirectoryArchive();
        Assert.assertEquals("verify files in archive dir", numFilesAfterSend, archiveDirectory.listFiles().length);
    }
}

