/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.qjournal.server;


import HdfsConstants.RollingUpgradeAction.FINALIZE;
import HdfsConstants.RollingUpgradeAction.PREPARE;
import HdfsConstants.RollingUpgradeAction.QUERY;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.RollingUpgradeInfo;
import org.apache.hadoop.hdfs.qjournal.MiniJournalCluster;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.FileJournalManager;
import org.apache.hadoop.hdfs.server.namenode.FileJournalManager.EditLogFile;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Unit test for Journal Node formatting upon re-installation and syncing.
 */
public class TestJournalNodeSync {
    private Configuration conf;

    private MiniQJMHACluster qjmhaCluster;

    private MiniDFSCluster dfsCluster;

    private MiniJournalCluster jCluster;

    private FSNamesystem namesystem;

    private int editsPerformed = 0;

    private final String jid = "ns1";

    private int activeNNindex = 0;

    private static final int DFS_HA_TAILEDITS_PERIOD_SECONDS = 1;

    @Rule
    public TestName testName = new TestName();

    @Test(timeout = 30000)
    public void testJournalNodeSync() throws Exception {
        // As by default 3 journal nodes are started;
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(true, jCluster.getJournalNode(i).getJournalSyncerStatus("ns1"));
        }
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        // Generate some edit logs and delete one.
        long firstTxId = generateEditLog();
        generateEditLog();
        File missingLog = deleteEditLog(firstJournalCurrentDir, firstTxId);
        GenericTestUtils.waitFor(editLogExists(Lists.newArrayList(missingLog)), 500, 10000);
    }

    @Test(timeout = 30000)
    public void testSyncForMultipleMissingLogs() throws Exception {
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        // Generate some edit logs and delete two.
        long firstTxId = generateEditLog();
        long nextTxId = generateEditLog();
        List<File> missingLogs = Lists.newArrayList();
        missingLogs.add(deleteEditLog(firstJournalCurrentDir, firstTxId));
        missingLogs.add(deleteEditLog(firstJournalCurrentDir, nextTxId));
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 10000);
    }

    @Test(timeout = 30000)
    public void testSyncForDiscontinuousMissingLogs() throws Exception {
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        // Generate some edit logs and delete two discontinuous logs.
        long firstTxId = generateEditLog();
        generateEditLog();
        long nextTxId = generateEditLog();
        List<File> missingLogs = Lists.newArrayList();
        missingLogs.add(deleteEditLog(firstJournalCurrentDir, firstTxId));
        missingLogs.add(deleteEditLog(firstJournalCurrentDir, nextTxId));
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 10000);
    }

    @Test(timeout = 30000)
    public void testMultipleJournalsMissingLogs() throws Exception {
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        File secondJournalDir = jCluster.getJournalDir(1, jid);
        StorageDirectory sd = new StorageDirectory(secondJournalDir);
        File secondJournalCurrentDir = sd.getCurrentDir();
        // Generate some edit logs and delete one log from two journals.
        long firstTxId = generateEditLog();
        generateEditLog();
        List<File> missingLogs = Lists.newArrayList();
        missingLogs.add(deleteEditLog(firstJournalCurrentDir, firstTxId));
        missingLogs.add(deleteEditLog(secondJournalCurrentDir, firstTxId));
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 10000);
    }

    @Test(timeout = 60000)
    public void testMultipleJournalsMultipleMissingLogs() throws Exception {
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        File secondJournalDir = jCluster.getJournalDir(1, jid);
        File secondJournalCurrentDir = new StorageDirectory(secondJournalDir).getCurrentDir();
        File thirdJournalDir = jCluster.getJournalDir(2, jid);
        File thirdJournalCurrentDir = new StorageDirectory(thirdJournalDir).getCurrentDir();
        // Generate some edit logs and delete multiple logs in multiple journals.
        long firstTxId = generateEditLog();
        long secondTxId = generateEditLog();
        long thirdTxId = generateEditLog();
        List<File> missingLogs = Lists.newArrayList();
        missingLogs.add(deleteEditLog(firstJournalCurrentDir, firstTxId));
        missingLogs.add(deleteEditLog(secondJournalCurrentDir, firstTxId));
        missingLogs.add(deleteEditLog(secondJournalCurrentDir, secondTxId));
        missingLogs.add(deleteEditLog(thirdJournalCurrentDir, thirdTxId));
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
    }

    // Test JournalNode Sync by randomly deleting edit logs from one or two of
    // the journals.
    @Test(timeout = 60000)
    public void testRandomJournalMissingLogs() throws Exception {
        List<File> missingLogs = deleteEditLogsFromRandomJN();
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
    }

    // Test JournalNode Sync when a JN id down while NN is actively writing
    // logs and comes back up after some time.
    @Test(timeout = 300000)
    public void testSyncAfterJNdowntime() throws Exception {
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        File secondJournalDir = jCluster.getJournalDir(1, jid);
        File secondJournalCurrentDir = new StorageDirectory(secondJournalDir).getCurrentDir();
        long[] startTxIds = new long[10];
        startTxIds[0] = generateEditLog();
        startTxIds[1] = generateEditLog();
        // Stop the first JN
        jCluster.getJournalNode(0).stop(0);
        // Roll some more edits while the first JN is down
        for (int i = 2; i < 10; i++) {
            startTxIds[i] = generateEditLog(5);
        }
        // Re-start the first JN
        jCluster.restartJournalNode(0);
        // Roll an edit to update the committed tx id of the first JN
        generateEditLog();
        // List the edit logs rolled during JN down time.
        List<File> missingLogs = Lists.newArrayList();
        for (int i = 2; i < 10; i++) {
            EditLogFile logFile = FileJournalManager.getLogFile(secondJournalCurrentDir, startTxIds[i], false);
            missingLogs.add(new File(firstJournalCurrentDir, logFile.getFile().getName()));
        }
        // Check that JNSync downloaded the edit logs rolled during JN down time.
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
    }

    /**
     * Test JournalNode Sync when a JN id down while NN is actively writing
     * logs and comes back up after some time with no edit log queueing.
     * Queuing disabled during the cluster setup {@link #setUpMiniCluster()}
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSyncAfterJNdowntimeWithoutQJournalQueue() throws Exception {
        // QJournal Queuing is disabled during the cluster setup
        // {@link #setUpMiniCluster()}
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        File secondJournalDir = jCluster.getJournalDir(1, jid);
        File secondJournalCurrentDir = new StorageDirectory(secondJournalDir).getCurrentDir();
        long[] startTxIds = new long[10];
        startTxIds[0] = generateEditLog();
        startTxIds[1] = generateEditLog(2);
        // Stop the first JN
        jCluster.getJournalNode(0).stop(0);
        // Roll some more edits while the first JN is down
        for (int i = 2; i < 10; i++) {
            startTxIds[i] = generateEditLog(5);
        }
        // Re-start the first JN
        jCluster.restartJournalNode(0);
        // After JN restart and before rolling another edit, the missing edit
        // logs will not by synced as the committed tx id of the JN will be
        // less than the start tx id's of the missing edit logs and edit log queuing
        // has been disabled.
        // Roll an edit to update the committed tx id of the first JN
        generateEditLog(2);
        // List the edit logs rolled during JN down time.
        List<File> missingLogs = Lists.newArrayList();
        for (int i = 2; i < 10; i++) {
            EditLogFile logFile = FileJournalManager.getLogFile(secondJournalCurrentDir, startTxIds[i], false);
            missingLogs.add(new File(firstJournalCurrentDir, logFile.getFile().getName()));
        }
        // Check that JNSync downloaded the edit logs rolled during JN down time.
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
        // Check that all the missing edit logs have been downloaded via
        // JournalNodeSyncer alone (as the edit log queueing has been disabled)
        long numEditLogsSynced = jCluster.getJournalNode(0).getOrCreateJournal(jid).getMetrics().getNumEditLogsSynced().value();
        Assert.assertTrue(((("Edit logs downloaded outside syncer. Expected 8 or " + "more downloads, got ") + numEditLogsSynced) + " downloads instead"), (numEditLogsSynced >= 8));
    }

    // Test JournalNode Sync when a JN is formatted while NN is actively writing
    // logs.
    @Test(timeout = 300000)
    public void testSyncAfterJNformat() throws Exception {
        File firstJournalDir = jCluster.getJournalDir(0, jid);
        File firstJournalCurrentDir = new StorageDirectory(firstJournalDir).getCurrentDir();
        File secondJournalDir = jCluster.getJournalDir(1, jid);
        File secondJournalCurrentDir = new StorageDirectory(secondJournalDir).getCurrentDir();
        long[] startTxIds = new long[10];
        startTxIds[0] = generateEditLog(1);
        startTxIds[1] = generateEditLog(2);
        startTxIds[2] = generateEditLog(4);
        startTxIds[3] = generateEditLog(6);
        Journal journal1 = jCluster.getJournalNode(0).getOrCreateJournal(jid);
        NamespaceInfo nsInfo = journal1.getStorage().getNamespaceInfo();
        // Delete contents of current directory of one JN
        for (File file : firstJournalCurrentDir.listFiles()) {
            file.delete();
        }
        // Format the JN
        journal1.format(nsInfo, false);
        // Roll some more edits
        for (int i = 4; i < 10; i++) {
            startTxIds[i] = generateEditLog(5);
        }
        // List the edit logs rolled during JN down time.
        List<File> missingLogs = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            EditLogFile logFile = FileJournalManager.getLogFile(secondJournalCurrentDir, startTxIds[i], false);
            missingLogs.add(new File(firstJournalCurrentDir, logFile.getFile().getName()));
        }
        // Check that the formatted JN has all the edit logs.
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
    }

    // Test JournalNode Sync during a Rolling Upgrade of NN.
    @Test(timeout = 300000)
    public void testSyncDuringRollingUpgrade() throws Exception {
        DistributedFileSystem dfsActive;
        int standbyNNindex;
        if (dfsCluster.getNameNode(0).isActiveState()) {
            activeNNindex = 0;
            standbyNNindex = 1;
        } else {
            activeNNindex = 1;
            standbyNNindex = 0;
        }
        dfsActive = dfsCluster.getFileSystem(activeNNindex);
        // Prepare for rolling upgrade
        final RollingUpgradeInfo info = dfsActive.rollingUpgrade(PREPARE);
        // query rolling upgrade
        Assert.assertEquals(info, dfsActive.rollingUpgrade(QUERY));
        // Restart the Standby NN with rollingUpgrade option
        dfsCluster.restartNameNode(standbyNNindex, true, "-rollingUpgrade", "started");
        Assert.assertEquals(info, dfsActive.rollingUpgrade(QUERY));
        // Do some edits and delete some edit logs
        List<File> missingLogs = deleteEditLogsFromRandomJN();
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
        // Transition the active NN to standby and standby to active
        dfsCluster.transitionToStandby(activeNNindex);
        // Let Standby NN catch up tailing edit logs before transitioning it to
        // active
        Thread.sleep(((30 * (TestJournalNodeSync.DFS_HA_TAILEDITS_PERIOD_SECONDS)) * 1000));
        dfsCluster.transitionToActive(standbyNNindex);
        dfsCluster.waitActive();
        activeNNindex = standbyNNindex;
        standbyNNindex = ((activeNNindex) + 1) % 2;
        dfsActive = dfsCluster.getFileSystem(activeNNindex);
        Assert.assertTrue(dfsCluster.getNameNode(activeNNindex).isActiveState());
        Assert.assertFalse(dfsCluster.getNameNode(standbyNNindex).isActiveState());
        // Restart the current standby NN (previously active)
        dfsCluster.restartNameNode(standbyNNindex, true, "-rollingUpgrade", "started");
        Assert.assertEquals(info, dfsActive.rollingUpgrade(QUERY));
        dfsCluster.waitActive();
        // Do some edits and delete some edit logs
        missingLogs.addAll(deleteEditLogsFromRandomJN());
        // Check that JNSync downloaded the edit logs rolled during rolling upgrade.
        GenericTestUtils.waitFor(editLogExists(missingLogs), 500, 30000);
        // finalize rolling upgrade
        final RollingUpgradeInfo finalize = dfsActive.rollingUpgrade(FINALIZE);
        Assert.assertTrue(finalize.isFinalized());
        // Check the missing edit logs exist after finalizing rolling upgrade
        for (File editLog : missingLogs) {
            Assert.assertTrue("Edit log missing after finalizing rolling upgrade", editLog.exists());
        }
    }
}

