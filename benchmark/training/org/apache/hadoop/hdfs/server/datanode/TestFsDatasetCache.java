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


import DatanodeProtocol.DNA_UNCACHE;
import NativeIO.POSIX;
import com.google.common.base.Supplier;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.jcip.annotations.NotThreadSafe;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HdfsBlockLocation;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.LogVerificationAppender;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.impl.BlockReaderTestUtil;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveEntry;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetCache;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetCache.PageRounder;
import org.apache.hadoop.hdfs.server.namenode.FSImage;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.protocol.DatanodeCommand;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.CacheManipulator;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.NoMlockCacheManipulator;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.apache.log4j.Logger.getRootLogger;


@NotThreadSafe
public class TestFsDatasetCache {
    private static final Logger LOG = LoggerFactory.getLogger(TestFsDatasetCache.class);

    // Most Linux installs allow a default of 64KB locked memory
    static final long CACHE_CAPACITY = 64 * 1024;

    // mlock always locks the entire page. So we don't need to deal with this
    // rounding, use the OS page size for the block size.
    private static final long PAGE_SIZE = POSIX.getCacheManipulator().getOperatingSystemPageSize();

    private static final long BLOCK_SIZE = TestFsDatasetCache.PAGE_SIZE;

    private static Configuration conf;

    private static MiniDFSCluster cluster = null;

    private static FileSystem fs;

    private static NameNode nn;

    private static FSImage fsImage;

    private static DataNode dn;

    private static FsDatasetSpi<?> fsd;

    private static DatanodeProtocolClientSideTranslatorPB spyNN;

    /**
     * Used to pause DN BPServiceActor threads. BPSA threads acquire the
     * shared read lock. The test acquires the write lock for exclusive access.
     */
    private static ReadWriteLock lock = new ReentrantReadWriteLock(true);

    private static final PageRounder rounder = new PageRounder();

    private static CacheManipulator prevCacheManipulator;

    private static DataNodeFaultInjector oldInjector;

    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(FsDatasetCache.class), Level.DEBUG);
    }

    @Test(timeout = 600000)
    public void testCacheAndUncacheBlockSimple() throws Exception {
        testCacheAndUncacheBlock();
    }

    /**
     * Run testCacheAndUncacheBlock with some failures injected into the mlock
     * call.  This tests the ability of the NameNode to resend commands.
     */
    @Test(timeout = 600000)
    public void testCacheAndUncacheBlockWithRetries() throws Exception {
        // We don't have to save the previous cacheManipulator
        // because it will be reinstalled by the @After function.
        POSIX.setCacheManipulator(new NoMlockCacheManipulator() {
            private final Set<String> seenIdentifiers = new HashSet<String>();

            @Override
            public void mlock(String identifier, ByteBuffer mmap, long length) throws IOException {
                if (seenIdentifiers.contains(identifier)) {
                    // mlock succeeds the second time.
                    TestFsDatasetCache.LOG.info(("mlocking " + identifier));
                    return;
                }
                seenIdentifiers.add(identifier);
                throw new IOException(("injecting IOException during mlock of " + identifier));
            }
        });
        testCacheAndUncacheBlock();
    }

    @Test(timeout = 600000)
    public void testFilesExceedMaxLockedMemory() throws Exception {
        TestFsDatasetCache.LOG.info("beginning testFilesExceedMaxLockedMemory");
        // Create some test files that will exceed total cache capacity
        final int numFiles = 5;
        final long fileSize = (TestFsDatasetCache.CACHE_CAPACITY) / (numFiles - 1);
        final Path[] testFiles = new Path[numFiles];
        final HdfsBlockLocation[][] fileLocs = new HdfsBlockLocation[numFiles][];
        final long[] fileSizes = new long[numFiles];
        for (int i = 0; i < numFiles; i++) {
            testFiles[i] = new Path(("/testFilesExceedMaxLockedMemory-" + i));
            DFSTestUtil.createFile(TestFsDatasetCache.fs, testFiles[i], fileSize, ((short) (1)), 3578L);
            fileLocs[i] = ((HdfsBlockLocation[]) (TestFsDatasetCache.fs.getFileBlockLocations(testFiles[i], 0, fileSize)));
            // Get the file size (sum of blocks)
            long[] sizes = TestFsDatasetCache.getBlockSizes(fileLocs[i]);
            for (int j = 0; j < (sizes.length); j++) {
                fileSizes[i] += sizes[j];
            }
        }
        // Cache the first n-1 files
        long total = 0;
        DFSTestUtil.verifyExpectedCacheUsage(0, 0, TestFsDatasetCache.fsd);
        for (int i = 0; i < (numFiles - 1); i++) {
            TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.cacheBlocks(fileLocs[i]));
            total = DFSTestUtil.verifyExpectedCacheUsage(TestFsDatasetCache.rounder.roundUp((total + (fileSizes[i]))), (4 * (i + 1)), TestFsDatasetCache.fsd);
        }
        // nth file should hit a capacity exception
        final LogVerificationAppender appender = new LogVerificationAppender();
        final org.apache.log4j.Logger logger = getRootLogger();
        logger.addAppender(appender);
        TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.cacheBlocks(fileLocs[(numFiles - 1)]));
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                int lines = appender.countLinesWithMessage(("more bytes in the cache: " + (DFSConfigKeys.DFS_DATANODE_MAX_LOCKED_MEMORY_KEY)));
                return lines > 0;
            }
        }, 500, 30000);
        // Also check the metrics for the failure
        Assert.assertTrue("Expected more than 0 failed cache attempts", ((TestFsDatasetCache.fsd.getNumBlocksFailedToCache()) > 0));
        // Uncache the n-1 files
        int curCachedBlocks = 16;
        for (int i = 0; i < (numFiles - 1); i++) {
            TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.uncacheBlocks(fileLocs[i]));
            long uncachedBytes = TestFsDatasetCache.rounder.roundUp(fileSizes[i]);
            total -= uncachedBytes;
            curCachedBlocks -= uncachedBytes / (TestFsDatasetCache.BLOCK_SIZE);
            DFSTestUtil.verifyExpectedCacheUsage(total, curCachedBlocks, TestFsDatasetCache.fsd);
        }
        TestFsDatasetCache.LOG.info("finishing testFilesExceedMaxLockedMemory");
    }

    @Test(timeout = 600000)
    public void testUncachingBlocksBeforeCachingFinishes() throws Exception {
        TestFsDatasetCache.LOG.info("beginning testUncachingBlocksBeforeCachingFinishes");
        final int NUM_BLOCKS = 5;
        DFSTestUtil.verifyExpectedCacheUsage(0, 0, TestFsDatasetCache.fsd);
        // Write a test file
        final Path testFile = new Path("/testCacheBlock");
        final long testFileLen = (TestFsDatasetCache.BLOCK_SIZE) * NUM_BLOCKS;
        DFSTestUtil.createFile(TestFsDatasetCache.fs, testFile, testFileLen, ((short) (1)), 43962L);
        // Get the details of the written file
        HdfsBlockLocation[] locs = ((HdfsBlockLocation[]) (TestFsDatasetCache.fs.getFileBlockLocations(testFile, 0, testFileLen)));
        Assert.assertEquals("Unexpected number of blocks", NUM_BLOCKS, locs.length);
        final long[] blockSizes = TestFsDatasetCache.getBlockSizes(locs);
        // Check initial state
        final long cacheCapacity = TestFsDatasetCache.fsd.getCacheCapacity();
        long cacheUsed = TestFsDatasetCache.fsd.getCacheUsed();
        long current = 0;
        Assert.assertEquals("Unexpected cache capacity", TestFsDatasetCache.CACHE_CAPACITY, cacheCapacity);
        Assert.assertEquals("Unexpected amount of cache used", current, cacheUsed);
        POSIX.setCacheManipulator(new NoMlockCacheManipulator() {
            @Override
            public void mlock(String identifier, ByteBuffer mmap, long length) throws IOException {
                TestFsDatasetCache.LOG.info(("An mlock operation is starting on " + identifier));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Assert.fail();
                }
            }
        });
        // Starting caching each block in succession.  The usedBytes amount
        // should increase, even though caching doesn't complete on any of them.
        for (int i = 0; i < NUM_BLOCKS; i++) {
            TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.cacheBlock(locs[i]));
            current = DFSTestUtil.verifyExpectedCacheUsage((current + (blockSizes[i])), (i + 1), TestFsDatasetCache.fsd);
        }
        TestFsDatasetCache.setHeartbeatResponse(new DatanodeCommand[]{ TestFsDatasetCache.getResponse(locs, DNA_UNCACHE) });
        // wait until all caching jobs are finished cancelling.
        current = DFSTestUtil.verifyExpectedCacheUsage(0, 0, TestFsDatasetCache.fsd);
        TestFsDatasetCache.LOG.info("finishing testUncachingBlocksBeforeCachingFinishes");
    }

    @Test(timeout = 60000)
    public void testUncacheUnknownBlock() throws Exception {
        // Create a file
        Path fileName = new Path("/testUncacheUnknownBlock");
        int fileLen = 4096;
        DFSTestUtil.createFile(TestFsDatasetCache.fs, fileName, fileLen, ((short) (1)), 65021);
        HdfsBlockLocation[] locs = ((HdfsBlockLocation[]) (TestFsDatasetCache.fs.getFileBlockLocations(fileName, 0, fileLen)));
        // Try to uncache it without caching it first
        TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.uncacheBlocks(locs));
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (TestFsDatasetCache.fsd.getNumBlocksFailedToUncache()) > 0;
            }
        }, 100, 10000);
    }

    @Test(timeout = 600000)
    public void testPageRounder() throws Exception {
        // Write a small file
        Path fileName = new Path("/testPageRounder");
        final int smallBlocks = 512;// This should be smaller than the page size

        Assert.assertTrue("Page size should be greater than smallBlocks!", ((TestFsDatasetCache.PAGE_SIZE) > smallBlocks));
        final int numBlocks = 5;
        final int fileLen = smallBlocks * numBlocks;
        FSDataOutputStream out = TestFsDatasetCache.fs.create(fileName, false, 4096, ((short) (1)), smallBlocks);
        out.write(new byte[fileLen]);
        out.close();
        HdfsBlockLocation[] locs = ((HdfsBlockLocation[]) (TestFsDatasetCache.fs.getFileBlockLocations(fileName, 0, fileLen)));
        // Cache the file and check the sizes match the page size
        TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.cacheBlocks(locs));
        DFSTestUtil.verifyExpectedCacheUsage(((TestFsDatasetCache.PAGE_SIZE) * numBlocks), numBlocks, TestFsDatasetCache.fsd);
        // Uncache and check that it decrements by the page size too
        TestFsDatasetCache.setHeartbeatResponse(TestFsDatasetCache.uncacheBlocks(locs));
        DFSTestUtil.verifyExpectedCacheUsage(0, 0, TestFsDatasetCache.fsd);
    }

    @Test(timeout = 60000)
    public void testUncacheQuiesces() throws Exception {
        // Create a file
        Path fileName = new Path("/testUncacheQuiesces");
        int fileLen = 4096;
        DFSTestUtil.createFile(TestFsDatasetCache.fs, fileName, fileLen, ((short) (1)), 65021);
        // Cache it
        DistributedFileSystem dfs = TestFsDatasetCache.cluster.getFileSystem();
        dfs.addCachePool(new CachePoolInfo("pool"));
        dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(fileName).setReplication(((short) (3))).build());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                MetricsRecordBuilder dnMetrics = MetricsAsserts.getMetrics(TestFsDatasetCache.dn.getMetrics().name());
                long blocksCached = MetricsAsserts.getLongCounter("BlocksCached", dnMetrics);
                return blocksCached > 0;
            }
        }, 1000, 30000);
        // Uncache it
        dfs.removeCacheDirective(1);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                MetricsRecordBuilder dnMetrics = MetricsAsserts.getMetrics(TestFsDatasetCache.dn.getMetrics().name());
                long blocksUncached = MetricsAsserts.getLongCounter("BlocksUncached", dnMetrics);
                return blocksUncached > 0;
            }
        }, 1000, 30000);
        // Make sure that no additional messages were sent
        Thread.sleep(10000);
        MetricsRecordBuilder dnMetrics = MetricsAsserts.getMetrics(TestFsDatasetCache.dn.getMetrics().name());
        MetricsAsserts.assertCounter("BlocksCached", 1L, dnMetrics);
        MetricsAsserts.assertCounter("BlocksUncached", 1L, dnMetrics);
    }

    @Test(timeout = 60000)
    public void testReCacheAfterUncache() throws Exception {
        final int TOTAL_BLOCKS_PER_CACHE = Ints.checkedCast(((TestFsDatasetCache.CACHE_CAPACITY) / (TestFsDatasetCache.BLOCK_SIZE)));
        BlockReaderTestUtil.enableHdfsCachingTracing();
        Assert.assertEquals(0, ((TestFsDatasetCache.CACHE_CAPACITY) % (TestFsDatasetCache.BLOCK_SIZE)));
        // Create a small file
        final Path SMALL_FILE = new Path("/smallFile");
        DFSTestUtil.createFile(TestFsDatasetCache.fs, SMALL_FILE, TestFsDatasetCache.BLOCK_SIZE, ((short) (1)), 51966);
        // Create a file that will take up the whole cache
        final Path BIG_FILE = new Path("/bigFile");
        DFSTestUtil.createFile(TestFsDatasetCache.fs, BIG_FILE, (TOTAL_BLOCKS_PER_CACHE * (TestFsDatasetCache.BLOCK_SIZE)), ((short) (1)), 48879);
        final DistributedFileSystem dfs = TestFsDatasetCache.cluster.getFileSystem();
        dfs.addCachePool(new CachePoolInfo("pool"));
        final long bigCacheDirectiveId = dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(BIG_FILE).setReplication(((short) (1))).build());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                MetricsRecordBuilder dnMetrics = MetricsAsserts.getMetrics(TestFsDatasetCache.dn.getMetrics().name());
                long blocksCached = MetricsAsserts.getLongCounter("BlocksCached", dnMetrics);
                if (blocksCached != TOTAL_BLOCKS_PER_CACHE) {
                    TestFsDatasetCache.LOG.info(((((("waiting for " + TOTAL_BLOCKS_PER_CACHE) + " to ") + "be cached.   Right now only ") + blocksCached) + " blocks are cached."));
                    return false;
                }
                TestFsDatasetCache.LOG.info((TOTAL_BLOCKS_PER_CACHE + " blocks are now cached."));
                return true;
            }
        }, 1000, 30000);
        // Try to cache a smaller file.  It should fail.
        final long shortCacheDirectiveId = dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(SMALL_FILE).setReplication(((short) (1))).build());
        Thread.sleep(10000);
        MetricsRecordBuilder dnMetrics = MetricsAsserts.getMetrics(TestFsDatasetCache.dn.getMetrics().name());
        Assert.assertEquals(TOTAL_BLOCKS_PER_CACHE, MetricsAsserts.getLongCounter("BlocksCached", dnMetrics));
        // Uncache the big file and verify that the small file can now be
        // cached (regression test for HDFS-6107)
        dfs.removeCacheDirective(bigCacheDirectiveId);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                RemoteIterator<CacheDirectiveEntry> iter;
                try {
                    iter = dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().build());
                    CacheDirectiveEntry entry;
                    do {
                        entry = iter.next();
                    } while ((entry.getInfo().getId()) != shortCacheDirectiveId );
                    if ((entry.getStats().getFilesCached()) != 1) {
                        TestFsDatasetCache.LOG.info(((("waiting for directive " + shortCacheDirectiveId) + " to be cached.  stats = ") + (entry.getStats())));
                        return false;
                    }
                    TestFsDatasetCache.LOG.info((("directive " + shortCacheDirectiveId) + " has been cached."));
                } catch (IOException e) {
                    Assert.fail(("unexpected exception" + (e.toString())));
                }
                return true;
            }
        }, 1000, 30000);
        dfs.removeCacheDirective(shortCacheDirectiveId);
    }
}

