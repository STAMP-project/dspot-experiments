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


import DFSConfigKeys.DFS_DATANODE_CACHE_REVOCATION_POLLING_MS;
import DFSConfigKeys.DFS_DATANODE_CACHE_REVOCATION_TIMEOUT_MS;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.ReadOption;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.impl.BlockReaderTestUtil;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.CacheManipulator;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFsDatasetCacheRevocation {
    private static final Logger LOG = LoggerFactory.getLogger(TestFsDatasetCacheRevocation.class);

    private static CacheManipulator prevCacheManipulator;

    private static TemporarySocketDirectory sockDir;

    private static final int BLOCK_SIZE = 4096;

    /**
     * Test that when a client has a replica mmapped, we will not un-mlock that
     * replica for a reasonable amount of time, even if an uncache request
     * occurs.
     */
    @Test(timeout = 120000)
    public void testPinning() throws Exception {
        Assume.assumeTrue(NativeCodeLoader.isNativeCodeLoaded());
        PlatformAssumptions.assumeNotWindows();
        Configuration conf = TestFsDatasetCacheRevocation.getDefaultConf();
        // Set a really long revocation timeout, so that we won't reach it during
        // this test.
        conf.setLong(DFS_DATANODE_CACHE_REVOCATION_TIMEOUT_MS, 1800000L);
        // Poll very often
        conf.setLong(DFS_DATANODE_CACHE_REVOCATION_POLLING_MS, 2L);
        MiniDFSCluster cluster = null;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        DistributedFileSystem dfs = cluster.getFileSystem();
        // Create and cache a file.
        final String TEST_FILE = "/test_file";
        DFSTestUtil.createFile(dfs, new Path(TEST_FILE), TestFsDatasetCacheRevocation.BLOCK_SIZE, ((short) (1)), 51966);
        dfs.addCachePool(new CachePoolInfo("pool"));
        long cacheDirectiveId = dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(new Path(TEST_FILE)).setReplication(((short) (1))).build());
        FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        DFSTestUtil.verifyExpectedCacheUsage(TestFsDatasetCacheRevocation.BLOCK_SIZE, 1, fsd);
        // Mmap the file.
        FSDataInputStream in = dfs.open(new Path(TEST_FILE));
        ByteBuffer buf = in.read(null, TestFsDatasetCacheRevocation.BLOCK_SIZE, EnumSet.noneOf(ReadOption.class));
        // Attempt to uncache file.  The file should still be cached.
        dfs.removeCacheDirective(cacheDirectiveId);
        Thread.sleep(500);
        DFSTestUtil.verifyExpectedCacheUsage(TestFsDatasetCacheRevocation.BLOCK_SIZE, 1, fsd);
        // Un-mmap the file.  The file should be uncached after this.
        in.releaseBuffer(buf);
        DFSTestUtil.verifyExpectedCacheUsage(0, 0, fsd);
        // Cleanup
        in.close();
        cluster.shutdown();
    }

    /**
     * Test that when we have an uncache request, and the client refuses to release
     * the replica for a long time, we will un-mlock it.
     */
    @Test(timeout = 120000)
    public void testRevocation() throws Exception {
        Assume.assumeTrue(NativeCodeLoader.isNativeCodeLoaded());
        PlatformAssumptions.assumeNotWindows();
        BlockReaderTestUtil.enableHdfsCachingTracing();
        BlockReaderTestUtil.enableShortCircuitShmTracing();
        Configuration conf = TestFsDatasetCacheRevocation.getDefaultConf();
        // Set a really short revocation timeout.
        conf.setLong(DFS_DATANODE_CACHE_REVOCATION_TIMEOUT_MS, 250L);
        // Poll very often
        conf.setLong(DFS_DATANODE_CACHE_REVOCATION_POLLING_MS, 2L);
        MiniDFSCluster cluster = null;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        DistributedFileSystem dfs = cluster.getFileSystem();
        // Create and cache a file.
        final String TEST_FILE = "/test_file2";
        DFSTestUtil.createFile(dfs, new Path(TEST_FILE), TestFsDatasetCacheRevocation.BLOCK_SIZE, ((short) (1)), 51966);
        dfs.addCachePool(new CachePoolInfo("pool"));
        long cacheDirectiveId = dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(new Path(TEST_FILE)).setReplication(((short) (1))).build());
        FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        DFSTestUtil.verifyExpectedCacheUsage(TestFsDatasetCacheRevocation.BLOCK_SIZE, 1, fsd);
        // Mmap the file.
        FSDataInputStream in = dfs.open(new Path(TEST_FILE));
        ByteBuffer buf = in.read(null, TestFsDatasetCacheRevocation.BLOCK_SIZE, EnumSet.noneOf(ReadOption.class));
        // Attempt to uncache file.  The file should get uncached.
        TestFsDatasetCacheRevocation.LOG.info("removing cache directive {}", cacheDirectiveId);
        dfs.removeCacheDirective(cacheDirectiveId);
        TestFsDatasetCacheRevocation.LOG.info("finished removing cache directive {}", cacheDirectiveId);
        Thread.sleep(1000);
        DFSTestUtil.verifyExpectedCacheUsage(0, 0, fsd);
        // Cleanup
        in.releaseBuffer(buf);
        in.close();
        cluster.shutdown();
    }
}

