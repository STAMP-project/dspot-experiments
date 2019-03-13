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


import HFileCleaner.DEFAULT_HFILE_DELETE_THREAD_CHECK_INTERVAL_MSEC;
import HFileCleaner.DEFAULT_HFILE_DELETE_THREAD_TIMEOUT_MSEC;
import HFileCleaner.HFILE_DELETE_THREAD_CHECK_INTERVAL_MSEC;
import HFileCleaner.HFILE_DELETE_THREAD_TIMEOUT_MSEC;
import HFileCleaner.HFILE_DELETE_THROTTLE_THRESHOLD;
import HFileCleaner.LARGE_HFILE_DELETE_THREAD_NUMBER;
import HFileCleaner.LARGE_HFILE_QUEUE_INIT_SIZE;
import HFileCleaner.MASTER_HFILE_CLEANER_PLUGINS;
import HFileCleaner.SMALL_HFILE_DELETE_THREAD_NUMBER;
import HFileCleaner.SMALL_HFILE_QUEUE_INIT_SIZE;
import TimeToLiveHFileCleaner.TTL_CONF_KEY;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.EnvironmentEdge;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestHFileCleaner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileCleaner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileCleaner.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testTTLCleaner() throws IOException, InterruptedException {
        FileSystem fs = TestHFileCleaner.UTIL.getDFSCluster().getFileSystem();
        Path root = TestHFileCleaner.UTIL.getDataTestDirOnTestFS();
        Path file = new Path(root, "file");
        fs.createNewFile(file);
        long createTime = System.currentTimeMillis();
        Assert.assertTrue("Test file not created!", fs.exists(file));
        TimeToLiveHFileCleaner cleaner = new TimeToLiveHFileCleaner();
        // update the time info for the file, so the cleaner removes it
        fs.setTimes(file, (createTime - 100), (-1));
        Configuration conf = TestHFileCleaner.UTIL.getConfiguration();
        conf.setLong(TTL_CONF_KEY, 100);
        cleaner.setConf(conf);
        Assert.assertTrue(((("File not set deletable - check mod time:" + (getFileStats(file, fs))) + " with create time:") + createTime), cleaner.isFileDeletable(fs.getFileStatus(file)));
    }

    @Test
    public void testHFileCleaning() throws Exception {
        final EnvironmentEdge originalEdge = EnvironmentEdgeManager.getDelegate();
        String prefix = "someHFileThatWouldBeAUUID";
        Configuration conf = TestHFileCleaner.UTIL.getConfiguration();
        // set TTL
        long ttl = 2000;
        conf.set(MASTER_HFILE_CLEANER_PLUGINS, "org.apache.hadoop.hbase.master.cleaner.TimeToLiveHFileCleaner");
        conf.setLong(TTL_CONF_KEY, ttl);
        Server server = new TestHFileCleaner.DummyServer();
        Path archivedHfileDir = new Path(TestHFileCleaner.UTIL.getDataTestDirOnTestFS(), HConstants.HFILE_ARCHIVE_DIRECTORY);
        FileSystem fs = FileSystem.get(conf);
        HFileCleaner cleaner = new HFileCleaner(1000, server, conf, fs, archivedHfileDir);
        // Create 2 invalid files, 1 "recent" file, 1 very new file and 30 old files
        final long createTime = System.currentTimeMillis();
        fs.delete(archivedHfileDir, true);
        fs.mkdirs(archivedHfileDir);
        // Case 1: 1 invalid file, which should be deleted directly
        fs.createNewFile(new Path(archivedHfileDir, "dfd-dfd"));
        // Case 2: 1 "recent" file, not even deletable for the first log cleaner
        // (TimeToLiveLogCleaner), so we are not going down the chain
        TestHFileCleaner.LOG.debug(("Now is: " + createTime));
        for (int i = 1; i < 32; i++) {
            // Case 3: old files which would be deletable for the first log cleaner
            // (TimeToLiveHFileCleaner),
            Path fileName = new Path(archivedHfileDir, ((prefix + ".") + (createTime + i)));
            fs.createNewFile(fileName);
            // set the creation time past ttl to ensure that it gets removed
            fs.setTimes(fileName, ((createTime - ttl) - 1), (-1));
            TestHFileCleaner.LOG.debug(("Creating " + (getFileStats(fileName, fs))));
        }
        // Case 2: 1 newer file, not even deletable for the first log cleaner
        // (TimeToLiveLogCleaner), so we are not going down the chain
        Path saved = new Path(archivedHfileDir, (prefix + ".00000000000"));
        fs.createNewFile(saved);
        // set creation time within the ttl
        fs.setTimes(saved, (createTime - (ttl / 2)), (-1));
        TestHFileCleaner.LOG.debug(("Creating " + (getFileStats(saved, fs))));
        for (FileStatus stat : fs.listStatus(archivedHfileDir)) {
            TestHFileCleaner.LOG.debug(stat.getPath().toString());
        }
        Assert.assertEquals(33, fs.listStatus(archivedHfileDir).length);
        // set a custom edge manager to handle time checking
        EnvironmentEdge setTime = new EnvironmentEdge() {
            @Override
            public long currentTime() {
                return createTime;
            }
        };
        EnvironmentEdgeManager.injectEdge(setTime);
        // run the chore
        cleaner.chore();
        // ensure we only end up with the saved file
        Assert.assertEquals(1, fs.listStatus(archivedHfileDir).length);
        for (FileStatus file : fs.listStatus(archivedHfileDir)) {
            TestHFileCleaner.LOG.debug(("Kept hfiles: " + (file.getPath().getName())));
        }
        // reset the edge back to the original edge
        EnvironmentEdgeManager.injectEdge(originalEdge);
    }

    @Test
    public void testRemovesEmptyDirectories() throws Exception {
        Configuration conf = TestHFileCleaner.UTIL.getConfiguration();
        // no cleaner policies = delete all files
        conf.setStrings(MASTER_HFILE_CLEANER_PLUGINS, "");
        Server server = new TestHFileCleaner.DummyServer();
        Path archivedHfileDir = new Path(TestHFileCleaner.UTIL.getDataTestDirOnTestFS(), HConstants.HFILE_ARCHIVE_DIRECTORY);
        // setup the cleaner
        FileSystem fs = TestHFileCleaner.UTIL.getDFSCluster().getFileSystem();
        HFileCleaner cleaner = new HFileCleaner(1000, server, conf, fs, archivedHfileDir);
        // make all the directories for archiving files
        Path table = new Path(archivedHfileDir, "table");
        Path region = new Path(table, "regionsomthing");
        Path family = new Path(region, "fam");
        Path file = new Path(family, "file12345");
        fs.mkdirs(family);
        if (!(fs.exists(family)))
            throw new RuntimeException(("Couldn't create test family:" + family));

        fs.create(file).close();
        if (!(fs.exists(file)))
            throw new RuntimeException(("Test file didn't get created:" + file));

        // run the chore to cleanup the files (and the directories above it)
        cleaner.chore();
        // make sure all the parent directories get removed
        Assert.assertFalse("family directory not removed for empty directory", fs.exists(family));
        Assert.assertFalse("region directory not removed for empty directory", fs.exists(region));
        Assert.assertFalse("table directory not removed for empty directory", fs.exists(table));
        Assert.assertTrue("archive directory", fs.exists(archivedHfileDir));
    }

    static class DummyServer implements Server {
        @Override
        public Configuration getConfiguration() {
            return TestHFileCleaner.UTIL.getConfiguration();
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

    @Test
    public void testThreadCleanup() throws Exception {
        Configuration conf = TestHFileCleaner.UTIL.getConfiguration();
        conf.setStrings(MASTER_HFILE_CLEANER_PLUGINS, "");
        Server server = new TestHFileCleaner.DummyServer();
        Path archivedHfileDir = new Path(TestHFileCleaner.UTIL.getDataTestDirOnTestFS(), HConstants.HFILE_ARCHIVE_DIRECTORY);
        // setup the cleaner
        FileSystem fs = TestHFileCleaner.UTIL.getDFSCluster().getFileSystem();
        HFileCleaner cleaner = new HFileCleaner(1000, server, conf, fs, archivedHfileDir);
        // clean up archive directory
        fs.delete(archivedHfileDir, true);
        fs.mkdirs(archivedHfileDir);
        // create some file to delete
        fs.createNewFile(new Path(archivedHfileDir, "dfd-dfd"));
        // launch the chore
        cleaner.chore();
        // call cleanup
        cleaner.cleanup();
        // wait awhile for thread to die
        Thread.sleep(100);
        for (Thread thread : cleaner.getCleanerThreads()) {
            Assert.assertFalse(thread.isAlive());
        }
    }

    @Test
    public void testLargeSmallIsolation() throws Exception {
        Configuration conf = TestHFileCleaner.UTIL.getConfiguration();
        // no cleaner policies = delete all files
        conf.setStrings(MASTER_HFILE_CLEANER_PLUGINS, "");
        conf.setInt(HFILE_DELETE_THROTTLE_THRESHOLD, (512 * 1024));
        Server server = new TestHFileCleaner.DummyServer();
        Path archivedHfileDir = new Path(TestHFileCleaner.UTIL.getDataTestDirOnTestFS(), HConstants.HFILE_ARCHIVE_DIRECTORY);
        // setup the cleaner
        FileSystem fs = TestHFileCleaner.UTIL.getDFSCluster().getFileSystem();
        HFileCleaner cleaner = new HFileCleaner(1000, server, conf, fs, archivedHfileDir);
        // clean up archive directory
        fs.delete(archivedHfileDir, true);
        fs.mkdirs(archivedHfileDir);
        // necessary set up
        final int LARGE_FILE_NUM = 5;
        final int SMALL_FILE_NUM = 20;
        createFilesForTesting(LARGE_FILE_NUM, SMALL_FILE_NUM, fs, archivedHfileDir);
        // call cleanup
        cleaner.chore();
        Assert.assertEquals(LARGE_FILE_NUM, cleaner.getNumOfDeletedLargeFiles());
        Assert.assertEquals(SMALL_FILE_NUM, cleaner.getNumOfDeletedSmallFiles());
    }

    @Test
    public void testOnConfigurationChange() throws Exception {
        // constants
        final int ORIGINAL_THROTTLE_POINT = 512 * 1024;
        final int ORIGINAL_QUEUE_INIT_SIZE = 512;
        final int UPDATE_THROTTLE_POINT = 1024;// small enough to change large/small check

        final int UPDATE_QUEUE_INIT_SIZE = 1024;
        final int LARGE_FILE_NUM = 5;
        final int SMALL_FILE_NUM = 20;
        final int LARGE_THREAD_NUM = 2;
        final int SMALL_THREAD_NUM = 4;
        final long THREAD_TIMEOUT_MSEC = 30 * 1000L;
        final long THREAD_CHECK_INTERVAL_MSEC = 500L;
        Configuration conf = TestHFileCleaner.UTIL.getConfiguration();
        // no cleaner policies = delete all files
        conf.setStrings(MASTER_HFILE_CLEANER_PLUGINS, "");
        conf.setInt(HFILE_DELETE_THROTTLE_THRESHOLD, ORIGINAL_THROTTLE_POINT);
        conf.setInt(LARGE_HFILE_QUEUE_INIT_SIZE, ORIGINAL_QUEUE_INIT_SIZE);
        conf.setInt(SMALL_HFILE_QUEUE_INIT_SIZE, ORIGINAL_QUEUE_INIT_SIZE);
        Server server = new TestHFileCleaner.DummyServer();
        Path archivedHfileDir = new Path(TestHFileCleaner.UTIL.getDataTestDirOnTestFS(), HConstants.HFILE_ARCHIVE_DIRECTORY);
        // setup the cleaner
        FileSystem fs = TestHFileCleaner.UTIL.getDFSCluster().getFileSystem();
        final HFileCleaner cleaner = new HFileCleaner(1000, server, conf, fs, archivedHfileDir);
        Assert.assertEquals(ORIGINAL_THROTTLE_POINT, cleaner.getThrottlePoint());
        Assert.assertEquals(ORIGINAL_QUEUE_INIT_SIZE, cleaner.getLargeQueueInitSize());
        Assert.assertEquals(ORIGINAL_QUEUE_INIT_SIZE, cleaner.getSmallQueueInitSize());
        Assert.assertEquals(DEFAULT_HFILE_DELETE_THREAD_TIMEOUT_MSEC, cleaner.getCleanerThreadTimeoutMsec());
        Assert.assertEquals(DEFAULT_HFILE_DELETE_THREAD_CHECK_INTERVAL_MSEC, cleaner.getCleanerThreadCheckIntervalMsec());
        // clean up archive directory and create files for testing
        fs.delete(archivedHfileDir, true);
        fs.mkdirs(archivedHfileDir);
        createFilesForTesting(LARGE_FILE_NUM, SMALL_FILE_NUM, fs, archivedHfileDir);
        // call cleaner, run as daemon to test the interrupt-at-middle case
        Thread t = new Thread() {
            @Override
            public void run() {
                cleaner.chore();
            }
        };
        t.setDaemon(true);
        t.start();
        // wait until file clean started
        while ((cleaner.getNumOfDeletedSmallFiles()) == 0) {
            Thread.yield();
        } 
        // trigger configuration change
        Configuration newConf = new Configuration(conf);
        newConf.setInt(HFILE_DELETE_THROTTLE_THRESHOLD, UPDATE_THROTTLE_POINT);
        newConf.setInt(LARGE_HFILE_QUEUE_INIT_SIZE, UPDATE_QUEUE_INIT_SIZE);
        newConf.setInt(SMALL_HFILE_QUEUE_INIT_SIZE, UPDATE_QUEUE_INIT_SIZE);
        newConf.setInt(LARGE_HFILE_DELETE_THREAD_NUMBER, LARGE_THREAD_NUM);
        newConf.setInt(SMALL_HFILE_DELETE_THREAD_NUMBER, SMALL_THREAD_NUM);
        newConf.setLong(HFILE_DELETE_THREAD_TIMEOUT_MSEC, THREAD_TIMEOUT_MSEC);
        newConf.setLong(HFILE_DELETE_THREAD_CHECK_INTERVAL_MSEC, THREAD_CHECK_INTERVAL_MSEC);
        TestHFileCleaner.LOG.debug(((("File deleted from large queue: " + (cleaner.getNumOfDeletedLargeFiles())) + "; from small queue: ") + (cleaner.getNumOfDeletedSmallFiles())));
        cleaner.onConfigurationChange(newConf);
        // check values after change
        Assert.assertEquals(UPDATE_THROTTLE_POINT, cleaner.getThrottlePoint());
        Assert.assertEquals(UPDATE_QUEUE_INIT_SIZE, cleaner.getLargeQueueInitSize());
        Assert.assertEquals(UPDATE_QUEUE_INIT_SIZE, cleaner.getSmallQueueInitSize());
        Assert.assertEquals((LARGE_THREAD_NUM + SMALL_THREAD_NUM), cleaner.getCleanerThreads().size());
        Assert.assertEquals(THREAD_TIMEOUT_MSEC, cleaner.getCleanerThreadTimeoutMsec());
        Assert.assertEquals(THREAD_CHECK_INTERVAL_MSEC, cleaner.getCleanerThreadCheckIntervalMsec());
        // make sure no cost when onConfigurationChange called with no change
        List<Thread> oldThreads = cleaner.getCleanerThreads();
        cleaner.onConfigurationChange(newConf);
        List<Thread> newThreads = cleaner.getCleanerThreads();
        Assert.assertArrayEquals(oldThreads.toArray(), newThreads.toArray());
        // wait until clean done and check
        t.join();
        TestHFileCleaner.LOG.debug(((("File deleted from large queue: " + (cleaner.getNumOfDeletedLargeFiles())) + "; from small queue: ") + (cleaner.getNumOfDeletedSmallFiles())));
        Assert.assertTrue(((("Should delete more than " + LARGE_FILE_NUM) + " files from large queue but actually ") + (cleaner.getNumOfDeletedLargeFiles())), ((cleaner.getNumOfDeletedLargeFiles()) > LARGE_FILE_NUM));
        Assert.assertTrue(((("Should delete less than " + SMALL_FILE_NUM) + " files from small queue but actually ") + (cleaner.getNumOfDeletedSmallFiles())), ((cleaner.getNumOfDeletedSmallFiles()) < SMALL_FILE_NUM));
    }
}

