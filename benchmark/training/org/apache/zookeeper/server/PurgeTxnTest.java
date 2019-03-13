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
package org.apache.zookeeper.server;


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.persistence.Util;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PurgeTxnTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeTxnTest.class);

    private static String HOSTPORT = "127.0.0.1:" + (PortAssignment.unique());

    private static final int CONNECTION_TIMEOUT = 3000;

    private static final long OP_TIMEOUT_IN_MILLIS = 90000;

    private File tmpDir;

    /**
     * test the purge
     *
     * @throws Exception
     * 		an exception might be thrown here
     */
    @Test
    public void testPurge() throws Exception {
        tmpDir = ClientBase.createTmpDir();
        ClientBase.setupTestEnv();
        ZooKeeperServer zks = new ZooKeeperServer(tmpDir, tmpDir, 3000);
        SyncRequestProcessor.setSnapCount(100);
        final int PORT = Integer.parseInt(PurgeTxnTest.HOSTPORT.split(":")[1]);
        ServerCnxnFactory f = ServerCnxnFactory.createFactory(PORT, (-1));
        f.startup(zks);
        Assert.assertTrue("waiting for server being up ", ClientBase.waitForServerUp(PurgeTxnTest.HOSTPORT, PurgeTxnTest.CONNECTION_TIMEOUT));
        ZooKeeper zk = ClientBase.createZKClient(PurgeTxnTest.HOSTPORT);
        try {
            for (int i = 0; i < 2000; i++) {
                zk.create(("/invalidsnap-" + i), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
            }
        } finally {
            zk.close();
        }
        f.shutdown();
        zks.getTxnLogFactory().close();
        Assert.assertTrue("waiting for server to shutdown", ClientBase.waitForServerDown(PurgeTxnTest.HOSTPORT, PurgeTxnTest.CONNECTION_TIMEOUT));
        // now corrupt the snapshot
        PurgeTxnLog.purge(tmpDir, tmpDir, 3);
        FileTxnSnapLog snaplog = new FileTxnSnapLog(tmpDir, tmpDir);
        List<File> listLogs = snaplog.findNRecentSnapshots(4);
        int numSnaps = 0;
        for (File ff : listLogs) {
            if (ff.getName().startsWith("snapshot")) {
                numSnaps++;
            }
        }
        Assert.assertTrue("exactly 3 snapshots ", (numSnaps == 3));
        snaplog.close();
        zks.shutdown();
    }

    /**
     * Tests purge when logs are rolling or a new snapshot is created, then
     * these newer files should alse be excluded in the current cycle.
     *
     * For frequent snapshotting, configured SnapCount to 30. There are three
     * threads which will create 1000 znodes each and simultaneously do purge
     * call
     */
    @Test
    public void testPurgeWhenLogRollingInProgress() throws Exception {
        tmpDir = ClientBase.createTmpDir();
        ClientBase.setupTestEnv();
        ZooKeeperServer zks = new ZooKeeperServer(tmpDir, tmpDir, 3000);
        SyncRequestProcessor.setSnapCount(30);
        final int PORT = Integer.parseInt(PurgeTxnTest.HOSTPORT.split(":")[1]);
        ServerCnxnFactory f = ServerCnxnFactory.createFactory(PORT, (-1));
        f.startup(zks);
        Assert.assertTrue("waiting for server being up ", ClientBase.waitForServerUp(PurgeTxnTest.HOSTPORT, PurgeTxnTest.CONNECTION_TIMEOUT));
        final ZooKeeper zk = ClientBase.createZKClient(PurgeTxnTest.HOSTPORT);
        final CountDownLatch doPurge = new CountDownLatch(1);
        final CountDownLatch purgeFinished = new CountDownLatch(1);
        final AtomicBoolean opFailed = new AtomicBoolean(false);
        new Thread() {
            public void run() {
                try {
                    doPurge.await(((PurgeTxnTest.OP_TIMEOUT_IN_MILLIS) / 2), TimeUnit.MILLISECONDS);
                    PurgeTxnLog.purge(tmpDir, tmpDir, 3);
                } catch (IOException ioe) {
                    PurgeTxnTest.LOG.error("Exception when purge", ioe);
                    opFailed.set(true);
                } catch (InterruptedException ie) {
                    PurgeTxnTest.LOG.error("Exception when purge", ie);
                    opFailed.set(true);
                } finally {
                    purgeFinished.countDown();
                }
            }
        }.start();
        final int thCount = 3;
        List<String> znodes = manyClientOps(zk, doPurge, thCount, "/invalidsnap");
        Assert.assertTrue("Purging is not finished!", purgeFinished.await(PurgeTxnTest.OP_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertFalse("Purging failed!", opFailed.get());
        for (String znode : znodes) {
            try {
                zk.getData(znode, false, null);
            } catch (Exception ke) {
                PurgeTxnTest.LOG.error("Unexpected exception when visiting znode!", ke);
                Assert.fail("Unexpected exception when visiting znode!");
            }
        }
        zk.close();
        f.shutdown();
        zks.shutdown();
        zks.getTxnLogFactory().close();
    }

    /**
     * Tests finding n recent snapshots from set of snapshots and data logs
     */
    @Test
    public void testFindNRecentSnapshots() throws Exception {
        int nRecentSnap = 4;// n recent snap shots

        int nRecentCount = 30;
        int offset = 0;
        tmpDir = ClientBase.createTmpDir();
        File version2 = new File(tmpDir.toString(), "version-2");
        Assert.assertTrue(("Failed to create version_2 dir:" + (version2.toString())), version2.mkdir());
        // Test that with no snaps, findNRecentSnapshots returns empty list
        FileTxnSnapLog txnLog = new FileTxnSnapLog(tmpDir, tmpDir);
        List<File> foundSnaps = txnLog.findNRecentSnapshots(1);
        Assert.assertEquals(0, foundSnaps.size());
        List<File> expectedNRecentSnapFiles = new ArrayList<File>();
        int counter = offset + (2 * nRecentCount);
        for (int i = 0; i < nRecentCount; i++) {
            // simulate log file
            File logFile = new File(((version2 + "/log.") + (Long.toHexString((--counter)))));
            Assert.assertTrue(("Failed to create log File:" + (logFile.toString())), logFile.createNewFile());
            // simulate snapshot file
            File snapFile = new File(((version2 + "/snapshot.") + (Long.toHexString((--counter)))));
            Assert.assertTrue(("Failed to create snap File:" + (snapFile.toString())), snapFile.createNewFile());
            // add the n recent snap files for assertion
            if (i < nRecentSnap) {
                expectedNRecentSnapFiles.add(snapFile);
            }
        }
        // Test that when we ask for recent snaps we get the number we asked for and
        // the files we expected
        List<File> nRecentSnapFiles = txnLog.findNRecentSnapshots(nRecentSnap);
        Assert.assertEquals("exactly 4 snapshots ", 4, nRecentSnapFiles.size());
        expectedNRecentSnapFiles.removeAll(nRecentSnapFiles);
        Assert.assertEquals("Didn't get the recent snap files", 0, expectedNRecentSnapFiles.size());
        // Test that when asking for more snaps than we created, we still only get snaps
        // not logs or anything else (per ZOOKEEPER-2420)
        nRecentSnapFiles = txnLog.findNRecentSnapshots((nRecentCount + 5));
        Assert.assertEquals(nRecentCount, nRecentSnapFiles.size());
        for (File f : nRecentSnapFiles) {
            Assert.assertTrue(("findNRecentSnapshots() returned a non-snapshot: " + (f.getPath())), ((Util.getZxidFromName(f.getName(), "snapshot")) != (-1)));
        }
        txnLog.close();
    }

    /**
     * Tests purge where the data directory contains old snapshots and data
     * logs, newest snapshots and data logs, (newest + n) snapshots and data
     * logs
     */
    @Test
    public void testSnapFilesGreaterThanToRetain() throws Exception {
        int nRecentCount = 4;
        int fileAboveRecentCount = 4;
        int fileToPurgeCount = 2;
        AtomicInteger offset = new AtomicInteger(0);
        tmpDir = ClientBase.createTmpDir();
        File version2 = new File(tmpDir.toString(), "version-2");
        Assert.assertTrue(("Failed to create version_2 dir:" + (version2.toString())), version2.mkdir());
        List<File> snapsToPurge = new ArrayList<File>();
        List<File> logsToPurge = new ArrayList<File>();
        List<File> snaps = new ArrayList<File>();
        List<File> logs = new ArrayList<File>();
        List<File> snapsAboveRecentFiles = new ArrayList<File>();
        List<File> logsAboveRecentFiles = new ArrayList<File>();
        createDataDirFiles(offset, fileToPurgeCount, false, version2, snapsToPurge, logsToPurge);
        createDataDirFiles(offset, nRecentCount, false, version2, snaps, logs);
        logs.add(logsToPurge.remove(0));// log that precedes first retained snapshot is also retained

        createDataDirFiles(offset, fileAboveRecentCount, false, version2, snapsAboveRecentFiles, logsAboveRecentFiles);
        /**
         * The newest log file preceding the oldest retained snapshot is not removed as it may
         * contain transactions newer than the oldest snapshot.
         */
        logsToPurge.remove(0);
        FileTxnSnapLog txnLog = new FileTxnSnapLog(tmpDir, tmpDir);
        PurgeTxnLog.purgeOlderSnapshots(txnLog, snaps.get(((snaps.size()) - 1)));
        txnLog.close();
        verifyFilesAfterPurge(snapsToPurge, false);
        verifyFilesAfterPurge(logsToPurge, false);
        verifyFilesAfterPurge(snaps, true);
        verifyFilesAfterPurge(logs, true);
        verifyFilesAfterPurge(snapsAboveRecentFiles, true);
        verifyFilesAfterPurge(logsAboveRecentFiles, true);
    }

    /**
     * Tests purge where the data directory contains snap files and log files equals to the
     * number of files to be retained
     */
    @Test
    public void testSnapFilesEqualsToRetain() throws Exception {
        internalTestSnapFilesEqualsToRetain(false);
    }

    /**
     * Tests purge where the data directory contains snap files equals to the
     * number of files to be retained, and a log file that precedes the earliest snapshot
     */
    @Test
    public void testSnapFilesEqualsToRetainWithPrecedingLog() throws Exception {
        internalTestSnapFilesEqualsToRetain(true);
    }

    /**
     * Tests purge where the data directory contains old snapshots and data
     * logs, newest snapshots and data logs
     */
    @Test
    public void testSnapFilesLessThanToRetain() throws Exception {
        int nRecentCount = 4;
        int fileToPurgeCount = 2;
        AtomicInteger offset = new AtomicInteger(0);
        tmpDir = ClientBase.createTmpDir();
        File version2 = new File(tmpDir.toString(), "version-2");
        Assert.assertTrue(("Failed to create version_2 dir:" + (version2.toString())), version2.mkdir());
        List<File> snapsToPurge = new ArrayList<File>();
        List<File> logsToPurge = new ArrayList<File>();
        List<File> snaps = new ArrayList<File>();
        List<File> logs = new ArrayList<File>();
        createDataDirFiles(offset, fileToPurgeCount, false, version2, snapsToPurge, logsToPurge);
        createDataDirFiles(offset, nRecentCount, false, version2, snaps, logs);
        logs.add(logsToPurge.remove(0));// log that precedes first retained snapshot is also retained

        /**
         * The newest log file preceding the oldest retained snapshot is not removed as it may
         * contain transactions newer than the oldest snapshot.
         */
        logsToPurge.remove(0);
        FileTxnSnapLog txnLog = new FileTxnSnapLog(tmpDir, tmpDir);
        PurgeTxnLog.purgeOlderSnapshots(txnLog, snaps.get(((snaps.size()) - 1)));
        txnLog.close();
        verifyFilesAfterPurge(snapsToPurge, false);
        verifyFilesAfterPurge(logsToPurge, false);
        verifyFilesAfterPurge(snaps, true);
        verifyFilesAfterPurge(logs, true);
    }

    /**
     * PurgeTxnLog is called with dataLogDir snapDir -n count This test case
     * verify these values are parsed properly and functionality works fine
     */
    @Test
    public void testPurgeTxnLogWithDataDir() throws Exception {
        tmpDir = ClientBase.createTmpDir();
        File dataDir = new File(tmpDir, "dataDir");
        File dataLogDir = new File(tmpDir, "dataLogDir");
        File dataDirVersion2 = new File(dataDir, "version-2");
        dataDirVersion2.mkdirs();
        File dataLogDirVersion2 = new File(dataLogDir, "version-2");
        dataLogDirVersion2.mkdirs();
        // create dummy log and transaction file
        int totalFiles = 20;
        // create transaction and snapshot files in different-different
        // directories
        for (int i = 0; i < totalFiles; i++) {
            // simulate log file
            File logFile = new File(dataLogDirVersion2, ("log." + (Long.toHexString(i))));
            logFile.createNewFile();
            // simulate snapshot file
            File snapFile = new File(dataDirVersion2, ("snapshot." + (Long.toHexString(i))));
            snapFile.createNewFile();
        }
        int numberOfSnapFilesToKeep = 10;
        // scenario where four parameter are passed
        String[] args = new String[]{ dataLogDir.getAbsolutePath(), dataDir.getAbsolutePath(), "-n", Integer.toString(numberOfSnapFilesToKeep) };
        PurgeTxnLog.main(args);
        Assert.assertEquals(numberOfSnapFilesToKeep, dataDirVersion2.listFiles().length);
        // Since for each snapshot we have a log file with same zxid, expect same # logs as snaps to be kept
        Assert.assertEquals(numberOfSnapFilesToKeep, dataLogDirVersion2.listFiles().length);
        ClientBase.recursiveDelete(tmpDir);
    }

    /**
     * PurgeTxnLog is called with dataLogDir -n count This test case verify
     * these values are parsed properly and functionality works fine
     */
    @Test
    public void testPurgeTxnLogWithoutDataDir() throws Exception {
        tmpDir = ClientBase.createTmpDir();
        File dataDir = new File(tmpDir, "dataDir");
        File dataLogDir = new File(tmpDir, "dataLogDir");
        File dataDirVersion2 = new File(dataDir, "version-2");
        dataDirVersion2.mkdirs();
        File dataLogDirVersion2 = new File(dataLogDir, "version-2");
        dataLogDirVersion2.mkdirs();
        // create dummy log and transaction file
        int totalFiles = 20;
        // create transaction and snapshot files in data directory
        for (int i = 0; i < totalFiles; i++) {
            // simulate log file
            File logFile = new File(dataLogDirVersion2, ("log." + (Long.toHexString(i))));
            logFile.createNewFile();
            // simulate snapshot file
            File snapFile = new File(dataLogDirVersion2, ("snapshot." + (Long.toHexString(i))));
            snapFile.createNewFile();
        }
        int numberOfSnapFilesToKeep = 10;
        // scenario where only three parameter are passed
        String[] args = new String[]{ dataLogDir.getAbsolutePath(), "-n", Integer.toString(numberOfSnapFilesToKeep) };
        PurgeTxnLog.main(args);
        // Since for each snapshot we have a log file with same zxid, expect same # logs as snaps to be kept
        Assert.assertEquals((numberOfSnapFilesToKeep * 2), dataLogDirVersion2.listFiles().length);
        ClientBase.recursiveDelete(tmpDir);
    }

    /**
     * Verifies that purge does not delete any log files which started before the oldest retained
     * snapshot but which might extend beyond it.
     *
     * @throws Exception
     * 		an exception might be thrown here
     */
    @Test
    public void testPurgeDoesNotDeleteOverlappingLogFile() throws Exception {
        // Setting used for snapRetainCount in this test.
        final int SNAP_RETAIN_COUNT = 3;
        // Number of znodes this test creates in each snapshot.
        final int NUM_ZNODES_PER_SNAPSHOT = 100;
        /**
         * Set a sufficiently high snapCount to ensure that we don't rollover the log.  Normally,
         * the default value (100K at time of this writing) would ensure this, but we make that
         * dependence explicit here to make the test future-proof.  Not rolling over the log is
         * important for this test since we are testing retention of the one and only log file which
         * predates each retained snapshot.
         */
        SyncRequestProcessor.setSnapCount(((SNAP_RETAIN_COUNT * NUM_ZNODES_PER_SNAPSHOT) * 10));
        // Create Zookeeper and connect to it.
        tmpDir = ClientBase.createTmpDir();
        ClientBase.setupTestEnv();
        ZooKeeperServer zks = new ZooKeeperServer(tmpDir, tmpDir, 3000);
        final int PORT = Integer.parseInt(PurgeTxnTest.HOSTPORT.split(":")[1]);
        ServerCnxnFactory f = ServerCnxnFactory.createFactory(PORT, (-1));
        f.startup(zks);
        Assert.assertTrue("waiting for server being up ", ClientBase.waitForServerUp(PurgeTxnTest.HOSTPORT, PurgeTxnTest.CONNECTION_TIMEOUT));
        ZooKeeper zk = ClientBase.createZKClient(PurgeTxnTest.HOSTPORT);
        // Unique identifier for each znode that we create.
        int unique = 0;
        try {
            /**
             * Create some znodes and take a snapshot.  Repeat this until we have SNAP_RETAIN_COUNT
             * snapshots.  Do not rollover the log.
             */
            for (int snapshotCount = 0; snapshotCount < SNAP_RETAIN_COUNT; snapshotCount++) {
                for (int i = 0; i < 100; i++ , unique++) {
                    zk.create(("/snap-" + unique), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
                }
                zks.takeSnapshot();
            }
            // Create some additional znodes without taking a snapshot afterwards.
            for (int i = 0; i < 100; i++ , unique++) {
                zk.create(("/snap-" + unique), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
            }
        } finally {
            zk.close();
        }
        // Shutdown Zookeeper.
        f.shutdown();
        zks.getTxnLogFactory().close();
        zks.shutdown();
        Assert.assertTrue("waiting for server to shutdown", ClientBase.waitForServerDown(PurgeTxnTest.HOSTPORT, PurgeTxnTest.CONNECTION_TIMEOUT));
        // Purge snapshot and log files.
        PurgeTxnLog.purge(tmpDir, tmpDir, SNAP_RETAIN_COUNT);
        // Initialize Zookeeper again from the same dataDir.
        zks = new ZooKeeperServer(tmpDir, tmpDir, 3000);
        f = ServerCnxnFactory.createFactory(PORT, (-1));
        f.startup(zks);
        zk = ClientBase.createZKClient(PurgeTxnTest.HOSTPORT);
        /**
         * Verify that the last znode that was created above exists.  This znode's creation was
         * captured by the transaction log which was created before any of the above
         * SNAP_RETAIN_COUNT snapshots were created, but it's not captured in any of these
         * snapshots.  So for it it exist, the (only) existing log file should not have been purged.
         */
        final String lastZnode = "/snap-" + (unique - 1);
        final Stat stat = zk.exists(lastZnode, false);
        Assert.assertNotNull(("Last znode does not exist: " + lastZnode), stat);
        // Shutdown for the last time.
        f.shutdown();
        zks.getTxnLogFactory().close();
        zks.shutdown();
    }
}

