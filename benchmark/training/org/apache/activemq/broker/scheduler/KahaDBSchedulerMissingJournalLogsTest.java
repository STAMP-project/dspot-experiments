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
package org.apache.activemq.broker.scheduler;


import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.disk.journal.DataFile;
import org.apache.activemq.store.kahadb.scheduler.JobSchedulerStoreImpl;
import org.apache.activemq.util.IOHelper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the store recovers even if some log files are missing.
 */
public class KahaDBSchedulerMissingJournalLogsTest {
    static final Logger LOG = LoggerFactory.getLogger(KahaDBSchedulerIndexRebuildTest.class);

    private BrokerService broker = null;

    private JobSchedulerStoreImpl schedulerStore = null;

    private final int NUM_LOGS = 6;

    static String basedir;

    static {
        try {
            ProtectionDomain protectionDomain = SchedulerDBVersionTest.class.getProtectionDomain();
            KahaDBSchedulerMissingJournalLogsTest.basedir = new File(new File(protectionDomain.getCodeSource().getLocation().getPath()), "../.").getCanonicalPath();
        } catch (IOException e) {
            KahaDBSchedulerMissingJournalLogsTest.basedir = ".";
        }
    }

    private final File schedulerStoreDir = new File(KahaDBSchedulerMissingJournalLogsTest.basedir, "activemq-data/store/scheduler");

    private final File storeDir = new File(KahaDBSchedulerMissingJournalLogsTest.basedir, "activemq-data/store/");

    @Test(timeout = 120 * 1000)
    public void testMissingLogsCausesBrokerToFail() throws Exception {
        fillUpSomeLogFiles();
        int jobCount = schedulerStore.getJobScheduler("JMS").getAllJobs().size();
        KahaDBSchedulerMissingJournalLogsTest.LOG.info("There are {} jobs in the store.", jobCount);
        List<File> toDelete = new ArrayList<File>();
        Map<Integer, DataFile> files = schedulerStore.getJournal().getFileMap();
        for (int i = files.size(); i > ((files.size()) / 2); i--) {
            toDelete.add(files.get(i).getFile());
        }
        broker.stop();
        broker.waitUntilStopped();
        for (File file : toDelete) {
            KahaDBSchedulerMissingJournalLogsTest.LOG.info("File to delete: {}", file);
            IOHelper.delete(file);
        }
        try {
            createBroker();
            broker.start();
            Assert.fail("Should not start when logs are missing.");
        } catch (Exception e) {
        }
    }

    @Test(timeout = 120 * 1000)
    public void testRecoverWhenSomeLogsAreMissing() throws Exception {
        fillUpSomeLogFiles();
        int jobCount = schedulerStore.getJobScheduler("JMS").getAllJobs().size();
        KahaDBSchedulerMissingJournalLogsTest.LOG.info("There are {} jobs in the store.", jobCount);
        List<File> toDelete = new ArrayList<File>();
        Map<Integer, DataFile> files = schedulerStore.getJournal().getFileMap();
        for (int i = (files.size()) - 1; i > ((files.size()) / 2); i--) {
            toDelete.add(files.get(i).getFile());
        }
        broker.stop();
        broker.waitUntilStopped();
        for (File file : toDelete) {
            KahaDBSchedulerMissingJournalLogsTest.LOG.info("File to delete: {}", file);
            IOHelper.delete(file);
        }
        schedulerStore = createScheduler();
        schedulerStore.setIgnoreMissingJournalfiles(true);
        createBroker(schedulerStore);
        broker.start();
        broker.waitUntilStarted();
        int postRecoverJobCount = schedulerStore.getJobScheduler("JMS").getAllJobs().size();
        Assert.assertTrue((postRecoverJobCount > 0));
        Assert.assertTrue((postRecoverJobCount < jobCount));
    }
}

