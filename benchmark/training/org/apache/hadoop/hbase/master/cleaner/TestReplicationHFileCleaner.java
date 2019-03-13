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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.replication.ReplicationException;
import org.apache.hadoop.hbase.replication.ReplicationPeers;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.replication.master.ReplicationHFileCleaner;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestReplicationHFileCleaner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationHFileCleaner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationHFileCleaner.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Server server;

    private static ReplicationQueueStorage rq;

    private static ReplicationPeers rp;

    private static final String peerId = "TestReplicationHFileCleaner";

    private static Configuration conf = TestReplicationHFileCleaner.TEST_UTIL.getConfiguration();

    static FileSystem fs = null;

    Path root;

    @Test
    public void testIsFileDeletable() throws IOException, ReplicationException {
        // 1. Create a file
        Path file = new Path(root, "testIsFileDeletableWithNoHFileRefs");
        TestReplicationHFileCleaner.fs.createNewFile(file);
        // 2. Assert file is successfully created
        Assert.assertTrue("Test file not created!", TestReplicationHFileCleaner.fs.exists(file));
        ReplicationHFileCleaner cleaner = new ReplicationHFileCleaner();
        cleaner.setConf(TestReplicationHFileCleaner.conf);
        // 3. Assert that file as is should be deletable
        Assert.assertTrue(("Cleaner should allow to delete this file as there is no hfile reference node " + "for it in the queue."), cleaner.isFileDeletable(TestReplicationHFileCleaner.fs.getFileStatus(file)));
        List<Pair<Path, Path>> files = new ArrayList<>(1);
        files.add(new Pair(null, file));
        // 4. Add the file to hfile-refs queue
        TestReplicationHFileCleaner.rq.addHFileRefs(TestReplicationHFileCleaner.peerId, files);
        // 5. Assert file should not be deletable
        Assert.assertFalse(("Cleaner should not allow to delete this file as there is a hfile reference node " + "for it in the queue."), cleaner.isFileDeletable(TestReplicationHFileCleaner.fs.getFileStatus(file)));
    }

    @Test
    public void testGetDeletableFiles() throws Exception {
        // 1. Create two files and assert that they do not exist
        Path notDeletablefile = new Path(root, "testGetDeletableFiles_1");
        TestReplicationHFileCleaner.fs.createNewFile(notDeletablefile);
        Assert.assertTrue("Test file not created!", TestReplicationHFileCleaner.fs.exists(notDeletablefile));
        Path deletablefile = new Path(root, "testGetDeletableFiles_2");
        TestReplicationHFileCleaner.fs.createNewFile(deletablefile);
        Assert.assertTrue("Test file not created!", TestReplicationHFileCleaner.fs.exists(deletablefile));
        List<FileStatus> files = new ArrayList<>(2);
        FileStatus f = new FileStatus();
        f.setPath(deletablefile);
        files.add(f);
        f = new FileStatus();
        f.setPath(notDeletablefile);
        files.add(f);
        List<Pair<Path, Path>> hfiles = new ArrayList<>(1);
        hfiles.add(new Pair(null, notDeletablefile));
        // 2. Add one file to hfile-refs queue
        TestReplicationHFileCleaner.rq.addHFileRefs(TestReplicationHFileCleaner.peerId, hfiles);
        ReplicationHFileCleaner cleaner = new ReplicationHFileCleaner();
        cleaner.setConf(TestReplicationHFileCleaner.conf);
        Iterator<FileStatus> deletableFilesIterator = cleaner.getDeletableFiles(files).iterator();
        int i = 0;
        while ((deletableFilesIterator.hasNext()) && (i < 2)) {
            i++;
        } 
        // 5. Assert one file should not be deletable and it is present in the list returned
        if (i > 2) {
            Assert.fail((("File " + notDeletablefile) + " should not be deletable as its hfile reference node is not added."));
        }
        Assert.assertTrue(deletableFilesIterator.next().getPath().equals(deletablefile));
    }

    /**
     * ReplicationHFileCleaner should be able to ride over ZooKeeper errors without aborting.
     */
    @Test
    public void testZooKeeperAbort() throws Exception {
        ReplicationHFileCleaner cleaner = new ReplicationHFileCleaner();
        List<FileStatus> dummyFiles = Lists.newArrayList(new FileStatus(100, false, 3, 100, System.currentTimeMillis(), new Path("hfile1")), new FileStatus(100, false, 3, 100, System.currentTimeMillis(), new Path("hfile2")));
        TestReplicationHFileCleaner.FaultyZooKeeperWatcher faultyZK = new TestReplicationHFileCleaner.FaultyZooKeeperWatcher(TestReplicationHFileCleaner.conf, "testZooKeeperAbort-faulty", null);
        try {
            faultyZK.init();
            cleaner.setConf(TestReplicationHFileCleaner.conf, faultyZK);
            // should keep all files due to a ConnectionLossException getting the queues znodes
            Iterable<FileStatus> toDelete = cleaner.getDeletableFiles(dummyFiles);
            Assert.assertFalse(toDelete.iterator().hasNext());
            Assert.assertFalse(cleaner.isStopped());
        } finally {
            close();
        }
        // when zk is working both files should be returned
        cleaner = new ReplicationHFileCleaner();
        ZKWatcher zkw = new ZKWatcher(TestReplicationHFileCleaner.conf, "testZooKeeperAbort-normal", null);
        try {
            cleaner.setConf(TestReplicationHFileCleaner.conf, zkw);
            Iterable<FileStatus> filesToDelete = cleaner.getDeletableFiles(dummyFiles);
            Iterator<FileStatus> iter = filesToDelete.iterator();
            Assert.assertTrue(iter.hasNext());
            Assert.assertEquals(new Path("hfile1"), iter.next().getPath());
            Assert.assertTrue(iter.hasNext());
            Assert.assertEquals(new Path("hfile2"), iter.next().getPath());
            Assert.assertFalse(iter.hasNext());
        } finally {
            zkw.close();
        }
    }

    static class DummyServer implements Server {
        @Override
        public Configuration getConfiguration() {
            return TestReplicationHFileCleaner.TEST_UTIL.getConfiguration();
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
            // TODO Auto-generated method stub
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
            Mockito.doThrow(new KeeperException.ConnectionLossException()).when(zk).getData("/hbase/replication/hfile-refs", null, new Stat());
        }

        @Override
        public RecoverableZooKeeper getRecoverableZooKeeper() {
            return zk;
        }
    }
}

