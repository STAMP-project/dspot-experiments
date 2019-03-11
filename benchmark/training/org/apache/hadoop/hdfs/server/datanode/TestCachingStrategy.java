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
package org.apache.hadoop.hdfs.server.datanode;


import DFSConfigKeys.DFS_DATANODE_DROP_CACHE_BEHIND_READS_KEY;
import DFSConfigKeys.DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_KEY;
import HdfsClientConfigKeys.DFS_CLIENT_CACHE_DROP_BEHIND_READS;
import HdfsClientConfigKeys.DFS_CLIENT_CACHE_DROP_BEHIND_WRITES;
import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.CacheManipulator;
import org.apache.hadoop.io.nativeio.NativeIOException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCachingStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(TestCachingStrategy.class);

    private static final int MAX_TEST_FILE_LEN = 1024 * 1024;

    private static final int WRITE_PACKET_SIZE = HdfsClientConfigKeys.DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT;

    private static final TestCachingStrategy.TestRecordingCacheTracker tracker = new TestCachingStrategy.TestRecordingCacheTracker();

    private static class Stats {
        private final String fileName;

        private final boolean[] dropped = new boolean[TestCachingStrategy.MAX_TEST_FILE_LEN];

        Stats(String fileName) {
            this.fileName = fileName;
        }

        synchronized void fadvise(int offset, int len, int flags) {
            TestCachingStrategy.LOG.debug("got fadvise(offset={}, len={}, flags={})", offset, len, flags);
            if (flags == (POSIX.POSIX_FADV_DONTNEED)) {
                for (int i = 0; i < len; i++) {
                    dropped[(offset + i)] = true;
                }
            }
        }

        synchronized void assertNotDroppedInRange(int start, int end) {
            for (int i = start; i < end; i++) {
                if (dropped[i]) {
                    throw new RuntimeException((((("in file " + (fileName)) + ", we ") + "dropped the cache at offset ") + i));
                }
            }
        }

        synchronized void assertDroppedInRange(int start, int end) {
            for (int i = start; i < end; i++) {
                if (!(dropped[i])) {
                    throw new RuntimeException((((("in file " + (fileName)) + ", we ") + "did not drop the cache at offset ") + i));
                }
            }
        }

        synchronized void clear() {
            Arrays.fill(dropped, false);
        }
    }

    private static class TestRecordingCacheTracker extends CacheManipulator {
        private final Map<String, TestCachingStrategy.Stats> map = new TreeMap<>();

        @Override
        public void posixFadviseIfPossible(String name, FileDescriptor fd, long offset, long len, int flags) throws NativeIOException {
            if ((len < 0) || (len > (Integer.MAX_VALUE))) {
                throw new RuntimeException((("invalid length of " + len) + " passed to posixFadviseIfPossible"));
            }
            if ((offset < 0) || (offset > (Integer.MAX_VALUE))) {
                throw new RuntimeException((("invalid offset of " + offset) + " passed to posixFadviseIfPossible"));
            }
            TestCachingStrategy.Stats stats = map.get(name);
            if (stats == null) {
                stats = new TestCachingStrategy.Stats(name);
                map.put(name, stats);
            }
            stats.fadvise(((int) (offset)), ((int) (len)), flags);
            super.posixFadviseIfPossible(name, fd, offset, len, flags);
        }

        synchronized void clear() {
            map.clear();
        }

        synchronized TestCachingStrategy.Stats getStats(String fileName) {
            return map.get(fileName);
        }

        public synchronized String toString() {
            StringBuilder bld = new StringBuilder();
            bld.append("TestRecordingCacheManipulator{");
            String prefix = "";
            for (String fileName : map.keySet()) {
                bld.append(prefix);
                prefix = ", ";
                bld.append(fileName);
            }
            bld.append("}");
            return bld.toString();
        }
    }

    @Test(timeout = 120000)
    public void testFadviseAfterWriteThenRead() throws Exception {
        // start a cluster
        TestCachingStrategy.LOG.info("testFadviseAfterWriteThenRead");
        TestCachingStrategy.tracker.clear();
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        String TEST_PATH = "/test";
        int TEST_PATH_LEN = TestCachingStrategy.MAX_TEST_FILE_LEN;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create new file
            TestCachingStrategy.createHdfsFile(fs, new Path(TEST_PATH), TEST_PATH_LEN, true);
            // verify that we dropped everything from the cache during file creation.
            ExtendedBlock block = cluster.getNameNode().getRpcServer().getBlockLocations(TEST_PATH, 0, Long.MAX_VALUE).get(0).getBlock();
            String fadvisedFileName = cluster.getBlockFile(0, block).getName();
            TestCachingStrategy.Stats stats = TestCachingStrategy.tracker.getStats(fadvisedFileName);
            stats.assertDroppedInRange(0, (TEST_PATH_LEN - (TestCachingStrategy.WRITE_PACKET_SIZE)));
            stats.clear();
            // read file
            TestCachingStrategy.readHdfsFile(fs, new Path(TEST_PATH), Long.MAX_VALUE, true);
            // verify that we dropped everything from the cache.
            Assert.assertNotNull(stats);
            stats.assertDroppedInRange(0, (TEST_PATH_LEN - (TestCachingStrategy.WRITE_PACKET_SIZE)));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * *
     * Test the scenario where the DataNode defaults to not dropping the cache,
     * but our client defaults are set.
     */
    @Test(timeout = 120000)
    public void testClientDefaults() throws Exception {
        // start a cluster
        TestCachingStrategy.LOG.info("testClientDefaults");
        TestCachingStrategy.tracker.clear();
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_DATANODE_DROP_CACHE_BEHIND_READS_KEY, false);
        conf.setBoolean(DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_KEY, false);
        conf.setBoolean(DFS_CLIENT_CACHE_DROP_BEHIND_READS, true);
        conf.setBoolean(DFS_CLIENT_CACHE_DROP_BEHIND_WRITES, true);
        MiniDFSCluster cluster = null;
        String TEST_PATH = "/test";
        int TEST_PATH_LEN = TestCachingStrategy.MAX_TEST_FILE_LEN;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create new file
            TestCachingStrategy.createHdfsFile(fs, new Path(TEST_PATH), TEST_PATH_LEN, null);
            // verify that we dropped everything from the cache during file creation.
            ExtendedBlock block = cluster.getNameNode().getRpcServer().getBlockLocations(TEST_PATH, 0, Long.MAX_VALUE).get(0).getBlock();
            String fadvisedFileName = cluster.getBlockFile(0, block).getName();
            TestCachingStrategy.Stats stats = TestCachingStrategy.tracker.getStats(fadvisedFileName);
            stats.assertDroppedInRange(0, (TEST_PATH_LEN - (TestCachingStrategy.WRITE_PACKET_SIZE)));
            stats.clear();
            // read file
            TestCachingStrategy.readHdfsFile(fs, new Path(TEST_PATH), Long.MAX_VALUE, null);
            // verify that we dropped everything from the cache.
            Assert.assertNotNull(stats);
            stats.assertDroppedInRange(0, (TEST_PATH_LEN - (TestCachingStrategy.WRITE_PACKET_SIZE)));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 120000)
    public void testFadviseSkippedForSmallReads() throws Exception {
        // start a cluster
        TestCachingStrategy.LOG.info("testFadviseSkippedForSmallReads");
        TestCachingStrategy.tracker.clear();
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_DATANODE_DROP_CACHE_BEHIND_READS_KEY, true);
        conf.setBoolean(DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_KEY, true);
        MiniDFSCluster cluster = null;
        String TEST_PATH = "/test";
        int TEST_PATH_LEN = TestCachingStrategy.MAX_TEST_FILE_LEN;
        FSDataInputStream fis = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create new file
            TestCachingStrategy.createHdfsFile(fs, new Path(TEST_PATH), TEST_PATH_LEN, null);
            // Since the DataNode was configured with drop-behind, and we didn't
            // specify any policy, we should have done drop-behind.
            ExtendedBlock block = cluster.getNameNode().getRpcServer().getBlockLocations(TEST_PATH, 0, Long.MAX_VALUE).get(0).getBlock();
            String fadvisedFileName = cluster.getBlockFile(0, block).getName();
            TestCachingStrategy.Stats stats = TestCachingStrategy.tracker.getStats(fadvisedFileName);
            stats.assertDroppedInRange(0, (TEST_PATH_LEN - (TestCachingStrategy.WRITE_PACKET_SIZE)));
            stats.clear();
            stats.assertNotDroppedInRange(0, TEST_PATH_LEN);
            // read file
            fis = fs.open(new Path(TEST_PATH));
            byte[] buf = new byte[17];
            fis.readFully(4096, buf, 0, buf.length);
            // we should not have dropped anything because of the small read.
            stats = TestCachingStrategy.tracker.getStats(fadvisedFileName);
            stats.assertNotDroppedInRange(0, (TEST_PATH_LEN - (TestCachingStrategy.WRITE_PACKET_SIZE)));
        } finally {
            IOUtils.cleanup(null, fis);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 120000)
    public void testNoFadviseAfterWriteThenRead() throws Exception {
        // start a cluster
        TestCachingStrategy.LOG.info("testNoFadviseAfterWriteThenRead");
        TestCachingStrategy.tracker.clear();
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        String TEST_PATH = "/test";
        int TEST_PATH_LEN = TestCachingStrategy.MAX_TEST_FILE_LEN;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create new file
            TestCachingStrategy.createHdfsFile(fs, new Path(TEST_PATH), TEST_PATH_LEN, false);
            // verify that we did not drop everything from the cache during file creation.
            ExtendedBlock block = cluster.getNameNode().getRpcServer().getBlockLocations(TEST_PATH, 0, Long.MAX_VALUE).get(0).getBlock();
            String fadvisedFileName = cluster.getBlockFile(0, block).getName();
            TestCachingStrategy.Stats stats = TestCachingStrategy.tracker.getStats(fadvisedFileName);
            Assert.assertNull(stats);
            // read file
            TestCachingStrategy.readHdfsFile(fs, new Path(TEST_PATH), Long.MAX_VALUE, false);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 120000)
    public void testSeekAfterSetDropBehind() throws Exception {
        // start a cluster
        TestCachingStrategy.LOG.info("testSeekAfterSetDropBehind");
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        String TEST_PATH = "/test";
        int TEST_PATH_LEN = TestCachingStrategy.MAX_TEST_FILE_LEN;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            TestCachingStrategy.createHdfsFile(fs, new Path(TEST_PATH), TEST_PATH_LEN, false);
            // verify that we can seek after setDropBehind
            try (FSDataInputStream fis = fs.open(new Path(TEST_PATH))) {
                Assert.assertTrue(((fis.read()) != (-1)));// create BlockReader

                fis.setDropBehind(false);// clear BlockReader

                fis.seek(2);// seek

            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

