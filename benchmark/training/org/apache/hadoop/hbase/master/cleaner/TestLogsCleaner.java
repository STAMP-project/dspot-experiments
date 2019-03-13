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
package org.apache.hadoop.hbase.master.cleaner;


import LogCleaner.DEFAULT_OLD_WALS_CLEANER_THREAD_SIZE;
import LogCleaner.DEFAULT_OLD_WALS_CLEANER_THREAD_TIMEOUT_MSEC;
import LogCleaner.OLD_WALS_CLEANER_THREAD_SIZE;
import LogCleaner.OLD_WALS_CLEANER_THREAD_TIMEOUT_MSEC;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.replication.ReplicationException;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.replication.ReplicationStorageFactory;
import org.apache.hadoop.hbase.replication.master.ReplicationLogCleaner;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestLogsCleaner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLogsCleaner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestLogsCleaner.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Path OLD_WALS_DIR = new Path(getDataTestDir(), HConstants.HREGION_OLDLOGDIR_NAME);

    private static final Path OLD_PROCEDURE_WALS_DIR = new Path(TestLogsCleaner.OLD_WALS_DIR, "masterProcedureWALs");

    private static Configuration conf;

    /**
     * This tests verifies LogCleaner works correctly with WALs and Procedure WALs located
     * in the same oldWALs directory.
     * Created files:
     * - 2 invalid files
     * - 5 old Procedure WALs
     * - 30 old WALs from which 3 are in replication
     * - 5 recent Procedure WALs
     * - 1 recent WAL
     * - 1 very new WAL (timestamp in future)
     * - masterProcedureWALs subdirectory
     * Files which should stay:
     * - 3 replication WALs
     * - 2 new WALs
     * - 5 latest Procedure WALs
     * - masterProcedureWALs subdirectory
     */
    @Test
    public void testLogCleaning() throws Exception {
        // set TTLs
        long ttlWAL = 2000;
        long ttlProcedureWAL = 4000;
        TestLogsCleaner.conf.setLong("hbase.master.logcleaner.ttl", ttlWAL);
        TestLogsCleaner.conf.setLong("hbase.master.procedurewalcleaner.ttl", ttlProcedureWAL);
        HMaster.decorateMasterConfiguration(TestLogsCleaner.conf);
        Server server = new TestLogsCleaner.DummyServer();
        ReplicationQueueStorage queueStorage = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), TestLogsCleaner.conf);
        String fakeMachineName = URLEncoder.encode(server.getServerName().toString(), StandardCharsets.UTF_8.name());
        final FileSystem fs = FileSystem.get(TestLogsCleaner.conf);
        fs.mkdirs(TestLogsCleaner.OLD_PROCEDURE_WALS_DIR);
        final long now = System.currentTimeMillis();
        // Case 1: 2 invalid files, which would be deleted directly
        fs.createNewFile(new Path(TestLogsCleaner.OLD_WALS_DIR, "a"));
        fs.createNewFile(new Path(TestLogsCleaner.OLD_WALS_DIR, ((fakeMachineName + ".") + "a")));
        // Case 2: 5 Procedure WALs that are old which would be deleted
        for (int i = 1; i <= 5; i++) {
            final Path fileName = new Path(TestLogsCleaner.OLD_PROCEDURE_WALS_DIR, String.format("pv2-%020d.log", i));
            fs.createNewFile(fileName);
        }
        // Sleep for sometime to get old procedure WALs
        Thread.sleep((ttlProcedureWAL - ttlWAL));
        // Case 3: old WALs which would be deletable
        for (int i = 1; i <= 30; i++) {
            Path fileName = new Path(TestLogsCleaner.OLD_WALS_DIR, ((fakeMachineName + ".") + (now - i)));
            fs.createNewFile(fileName);
            // Case 4: put 3 WALs in ZK indicating that they are scheduled for replication so these
            // files would pass TimeToLiveLogCleaner but would be rejected by ReplicationLogCleaner
            if ((i % (30 / 3)) == 0) {
                queueStorage.addWAL(server.getServerName(), fakeMachineName, fileName.getName());
                TestLogsCleaner.LOG.info(("Replication log file: " + fileName));
            }
        }
        // Case 5: 5 Procedure WALs that are new, will stay
        for (int i = 6; i <= 10; i++) {
            Path fileName = new Path(TestLogsCleaner.OLD_PROCEDURE_WALS_DIR, String.format("pv2-%020d.log", i));
            fs.createNewFile(fileName);
        }
        // Sleep for sometime to get newer modification time
        Thread.sleep(ttlWAL);
        fs.createNewFile(new Path(TestLogsCleaner.OLD_WALS_DIR, ((fakeMachineName + ".") + now)));
        // Case 6: 1 newer WAL, not even deletable for TimeToLiveLogCleaner,
        // so we are not going down the chain
        fs.createNewFile(new Path(TestLogsCleaner.OLD_WALS_DIR, ((fakeMachineName + ".") + (now + ttlWAL))));
        FileStatus[] status = fs.listStatus(TestLogsCleaner.OLD_WALS_DIR);
        TestLogsCleaner.LOG.info("File status: {}", Arrays.toString(status));
        // There should be 34 files and 1 masterProcedureWALs directory
        Assert.assertEquals(35, fs.listStatus(TestLogsCleaner.OLD_WALS_DIR).length);
        // 10 procedure WALs
        Assert.assertEquals(10, fs.listStatus(TestLogsCleaner.OLD_PROCEDURE_WALS_DIR).length);
        LogCleaner cleaner = new LogCleaner(1000, server, TestLogsCleaner.conf, fs, TestLogsCleaner.OLD_WALS_DIR);
        cleaner.chore();
        // In oldWALs we end up with the current WAL, a newer WAL, the 3 old WALs which
        // are scheduled for replication and masterProcedureWALs directory
        TestLogsCleaner.TEST_UTIL.waitFor(1000, ((Waiter.Predicate<Exception>) (() -> 6 == (fs.listStatus(OLD_WALS_DIR).length))));
        // In masterProcedureWALs we end up with 5 newer Procedure WALs
        TestLogsCleaner.TEST_UTIL.waitFor(1000, ((Waiter.Predicate<Exception>) (() -> 5 == (fs.listStatus(OLD_PROCEDURE_WALS_DIR).length))));
        if (TestLogsCleaner.LOG.isDebugEnabled()) {
            FileStatus[] statusOldWALs = fs.listStatus(TestLogsCleaner.OLD_WALS_DIR);
            FileStatus[] statusProcedureWALs = fs.listStatus(TestLogsCleaner.OLD_PROCEDURE_WALS_DIR);
            TestLogsCleaner.LOG.debug("Kept log file for oldWALs: {}", Arrays.toString(statusOldWALs));
            TestLogsCleaner.LOG.debug("Kept log file for masterProcedureWALs: {}", Arrays.toString(statusProcedureWALs));
        }
    }

    @Test(timeout = 10000)
    public void testZooKeeperAbortDuringGetListOfReplicators() throws Exception {
        ReplicationLogCleaner cleaner = new ReplicationLogCleaner();
        List<FileStatus> dummyFiles = Arrays.asList(new FileStatus(100, false, 3, 100, System.currentTimeMillis(), new Path("log1")), new FileStatus(100, false, 3, 100, System.currentTimeMillis(), new Path("log2")));
        TestLogsCleaner.FaultyZooKeeperWatcher faultyZK = new TestLogsCleaner.FaultyZooKeeperWatcher(TestLogsCleaner.conf, "testZooKeeperAbort-faulty", null);
        final AtomicBoolean getListOfReplicatorsFailed = new AtomicBoolean(false);
        try {
            faultyZK.init();
            ReplicationQueueStorage queueStorage = Mockito.spy(ReplicationStorageFactory.getReplicationQueueStorage(faultyZK, TestLogsCleaner.conf));
            Mockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    try {
                        return invocation.callRealMethod();
                    } catch (ReplicationException e) {
                        TestLogsCleaner.LOG.debug("Caught Exception", e);
                        getListOfReplicatorsFailed.set(true);
                        throw e;
                    }
                }
            }).when(queueStorage).getAllWALs();
            cleaner.setConf(TestLogsCleaner.conf, faultyZK, queueStorage);
            // should keep all files due to a ConnectionLossException getting the queues znodes
            cleaner.preClean();
            Iterable<FileStatus> toDelete = cleaner.getDeletableFiles(dummyFiles);
            Assert.assertTrue(getListOfReplicatorsFailed.get());
            Assert.assertFalse(toDelete.iterator().hasNext());
            Assert.assertFalse(cleaner.isStopped());
        } finally {
            close();
        }
    }

    /**
     * When zk is working both files should be returned
     *
     * @throws Exception
     * 		from ZK watcher
     */
    @Test(timeout = 10000)
    public void testZooKeeperNormal() throws Exception {
        ReplicationLogCleaner cleaner = new ReplicationLogCleaner();
        List<FileStatus> dummyFiles = Arrays.asList(new FileStatus(100, false, 3, 100, System.currentTimeMillis(), new Path("log1")), new FileStatus(100, false, 3, 100, System.currentTimeMillis(), new Path("log2")));
        ZKWatcher zkw = new ZKWatcher(TestLogsCleaner.conf, "testZooKeeperAbort-normal", null);
        try {
            cleaner.setConf(TestLogsCleaner.conf, zkw);
            cleaner.preClean();
            Iterable<FileStatus> filesToDelete = cleaner.getDeletableFiles(dummyFiles);
            Iterator<FileStatus> iter = filesToDelete.iterator();
            Assert.assertTrue(iter.hasNext());
            Assert.assertEquals(new Path("log1"), iter.next().getPath());
            Assert.assertTrue(iter.hasNext());
            Assert.assertEquals(new Path("log2"), iter.next().getPath());
            Assert.assertFalse(iter.hasNext());
        } finally {
            zkw.close();
        }
    }

    @Test
    public void testOnConfigurationChange() throws Exception {
        // Prepare environments
        Server server = new TestLogsCleaner.DummyServer();
        FileSystem fs = TestLogsCleaner.TEST_UTIL.getDFSCluster().getFileSystem();
        LogCleaner cleaner = new LogCleaner(3000, server, TestLogsCleaner.conf, fs, TestLogsCleaner.OLD_WALS_DIR);
        Assert.assertEquals(DEFAULT_OLD_WALS_CLEANER_THREAD_SIZE, cleaner.getSizeOfCleaners());
        Assert.assertEquals(DEFAULT_OLD_WALS_CLEANER_THREAD_TIMEOUT_MSEC, cleaner.getCleanerThreadTimeoutMsec());
        // Create dir and files for test
        int numOfFiles = 10;
        createFiles(fs, TestLogsCleaner.OLD_WALS_DIR, numOfFiles);
        FileStatus[] status = fs.listStatus(TestLogsCleaner.OLD_WALS_DIR);
        Assert.assertEquals(numOfFiles, status.length);
        // Start cleaner chore
        Thread thread = new Thread(() -> cleaner.chore());
        thread.setDaemon(true);
        thread.start();
        // change size of cleaners dynamically
        int sizeToChange = 4;
        long threadTimeoutToChange = 30 * 1000L;
        TestLogsCleaner.conf.setInt(OLD_WALS_CLEANER_THREAD_SIZE, sizeToChange);
        TestLogsCleaner.conf.setLong(OLD_WALS_CLEANER_THREAD_TIMEOUT_MSEC, threadTimeoutToChange);
        cleaner.onConfigurationChange(TestLogsCleaner.conf);
        Assert.assertEquals(sizeToChange, cleaner.getSizeOfCleaners());
        Assert.assertEquals(threadTimeoutToChange, cleaner.getCleanerThreadTimeoutMsec());
        // Stop chore
        thread.join();
        status = fs.listStatus(TestLogsCleaner.OLD_WALS_DIR);
        Assert.assertEquals(0, status.length);
    }

    static class DummyServer implements Server {
        @Override
        public Configuration getConfiguration() {
            return TestLogsCleaner.TEST_UTIL.getConfiguration();
        }

        @Override
        public ZKWatcher getZooKeeper() {
            try {
                return new ZKWatcher(getConfiguration(), "dummy server", this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public CoordinatedStateManager getCoordinatedStateManager() {
            return null;
        }

        @Override
        public ClusterConnection getConnection() {
            return null;
        }

        @Override
        public ServerName getServerName() {
            return ServerName.valueOf("regionserver,60020,000000");
        }

        @Override
        public void abort(String why, Throwable e) {
        }

        @Override
        public boolean isAborted() {
            return false;
        }

        @Override
        public void stop(String why) {
        }

        @Override
        public boolean isStopped() {
            return false;
        }

        @Override
        public ChoreService getChoreService() {
            return null;
        }

        @Override
        public ClusterConnection getClusterConnection() {
            return null;
        }

        @Override
        public FileSystem getFileSystem() {
            return null;
        }

        @Override
        public boolean isStopping() {
            return false;
        }

        @Override
        public Connection createConnection(Configuration conf) throws IOException {
            return null;
        }
    }

    static class FaultyZooKeeperWatcher extends ZKWatcher {
        private RecoverableZooKeeper zk;

        public FaultyZooKeeperWatcher(Configuration conf, String identifier, Abortable abortable) throws IOException, ZooKeeperConnectionException {
            super(conf, identifier, abortable);
        }

        public void init() throws Exception {
            this.zk = Mockito.spy(super.getRecoverableZooKeeper());
            Mockito.doThrow(new KeeperException.ConnectionLossException()).when(zk).getChildren("/hbase/replication/rs", null);
        }

        @Override
        public RecoverableZooKeeper getRecoverableZooKeeper() {
            return zk;
        }
    }
}

