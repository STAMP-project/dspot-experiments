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
package org.apache.hadoop.fs;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_CHECKSUM_TYPE_KEY;
import HdfsClientConfigKeys.DFS_CLIENT_CONTEXT;
import HdfsClientConfigKeys.Mmap.CACHE_SIZE_KEY;
import HdfsClientConfigKeys.Mmap.ENABLED_KEY;
import HdfsClientConfigKeys.Read.ShortCircuit.SKIP_CHECKSUM_KEY;
import NativeIO.POSIX;
import ReadOption.SKIP_CHECKSUMS;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.hadoop.hdfs.ClientContext;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.ExtendedBlockId;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsDataInputStream;
import org.apache.hadoop.hdfs.client.impl.BlockReaderTestUtil;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.shortcircuit.ShortCircuitCache;
import org.apache.hadoop.hdfs.shortcircuit.ShortCircuitCache.CacheVisitor;
import org.apache.hadoop.hdfs.shortcircuit.ShortCircuitReplica;
import org.apache.hadoop.io.ByteBufferPool;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.CacheManipulator;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests if EnhancedByteBufferAccess works correctly.
 */
public class TestEnhancedByteBufferAccess {
    private static final Logger LOG = LoggerFactory.getLogger(TestEnhancedByteBufferAccess.class.getName());

    private static TemporarySocketDirectory sockDir;

    private static CacheManipulator prevCacheManipulator;

    private static final int BLOCK_SIZE = ((int) (POSIX.getCacheManipulator().getOperatingSystemPageSize()));

    @Test
    public void testZeroCopyReads() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        FSDataInputStream fsIn = null;
        final int TEST_FILE_LENGTH = 3 * (TestEnhancedByteBufferAccess.BLOCK_SIZE);
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), 7567L);
            try {
                DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            } catch (InterruptedException e) {
                Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
            } catch (TimeoutException e) {
                Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
            }
            fsIn = fs.open(TEST_PATH);
            byte[] original = new byte[TEST_FILE_LENGTH];
            IOUtils.readFully(fsIn, original, 0, TEST_FILE_LENGTH);
            fsIn.close();
            fsIn = fs.open(TEST_PATH);
            ByteBuffer result = fsIn.read(null, TestEnhancedByteBufferAccess.BLOCK_SIZE, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, result.remaining());
            HdfsDataInputStream dfsIn = ((HdfsDataInputStream) (fsIn));
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, dfsIn.getReadStatistics().getTotalBytesRead());
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, dfsIn.getReadStatistics().getTotalZeroCopyBytesRead());
            Assert.assertArrayEquals(Arrays.copyOfRange(original, 0, TestEnhancedByteBufferAccess.BLOCK_SIZE), TestEnhancedByteBufferAccess.byteBufferToArray(result));
            fsIn.releaseBuffer(result);
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void testShortZeroCopyReads() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        FSDataInputStream fsIn = null;
        final int TEST_FILE_LENGTH = 3 * (TestEnhancedByteBufferAccess.BLOCK_SIZE);
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), 7567L);
            try {
                DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            } catch (InterruptedException e) {
                Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
            } catch (TimeoutException e) {
                Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
            }
            fsIn = fs.open(TEST_PATH);
            byte[] original = new byte[TEST_FILE_LENGTH];
            IOUtils.readFully(fsIn, original, 0, TEST_FILE_LENGTH);
            fsIn.close();
            fsIn = fs.open(TEST_PATH);
            // Try to read (2 * ${BLOCK_SIZE}), but only get ${BLOCK_SIZE} because of the block size.
            HdfsDataInputStream dfsIn = ((HdfsDataInputStream) (fsIn));
            ByteBuffer result = dfsIn.read(null, (2 * (TestEnhancedByteBufferAccess.BLOCK_SIZE)), EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, result.remaining());
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, dfsIn.getReadStatistics().getTotalBytesRead());
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, dfsIn.getReadStatistics().getTotalZeroCopyBytesRead());
            Assert.assertArrayEquals(Arrays.copyOfRange(original, 0, TestEnhancedByteBufferAccess.BLOCK_SIZE), TestEnhancedByteBufferAccess.byteBufferToArray(result));
            dfsIn.releaseBuffer(result);
            // Try to read (1 + ${BLOCK_SIZE}), but only get ${BLOCK_SIZE} because of the block size.
            result = dfsIn.read(null, (1 + (TestEnhancedByteBufferAccess.BLOCK_SIZE)), EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, result.remaining());
            Assert.assertArrayEquals(Arrays.copyOfRange(original, TestEnhancedByteBufferAccess.BLOCK_SIZE, (2 * (TestEnhancedByteBufferAccess.BLOCK_SIZE))), TestEnhancedByteBufferAccess.byteBufferToArray(result));
            dfsIn.releaseBuffer(result);
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void testZeroCopyReadsNoFallback() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        FSDataInputStream fsIn = null;
        final int TEST_FILE_LENGTH = 3 * (TestEnhancedByteBufferAccess.BLOCK_SIZE);
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), 7567L);
            try {
                DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            } catch (InterruptedException e) {
                Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
            } catch (TimeoutException e) {
                Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
            }
            fsIn = fs.open(TEST_PATH);
            byte[] original = new byte[TEST_FILE_LENGTH];
            IOUtils.readFully(fsIn, original, 0, TEST_FILE_LENGTH);
            fsIn.close();
            fsIn = fs.open(TEST_PATH);
            HdfsDataInputStream dfsIn = ((HdfsDataInputStream) (fsIn));
            ByteBuffer result;
            try {
                result = dfsIn.read(null, ((TestEnhancedByteBufferAccess.BLOCK_SIZE) + 1), EnumSet.noneOf(ReadOption.class));
                Assert.fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException e) {
                // expected
            }
            result = dfsIn.read(null, TestEnhancedByteBufferAccess.BLOCK_SIZE, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, result.remaining());
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, dfsIn.getReadStatistics().getTotalBytesRead());
            Assert.assertEquals(TestEnhancedByteBufferAccess.BLOCK_SIZE, dfsIn.getReadStatistics().getTotalZeroCopyBytesRead());
            Assert.assertArrayEquals(Arrays.copyOfRange(original, 0, TestEnhancedByteBufferAccess.BLOCK_SIZE), TestEnhancedByteBufferAccess.byteBufferToArray(result));
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    private static class CountingVisitor implements CacheVisitor {
        private final int expectedNumOutstandingMmaps;

        private final int expectedNumReplicas;

        private final int expectedNumEvictable;

        private final int expectedNumMmapedEvictable;

        CountingVisitor(int expectedNumOutstandingMmaps, int expectedNumReplicas, int expectedNumEvictable, int expectedNumMmapedEvictable) {
            this.expectedNumOutstandingMmaps = expectedNumOutstandingMmaps;
            this.expectedNumReplicas = expectedNumReplicas;
            this.expectedNumEvictable = expectedNumEvictable;
            this.expectedNumMmapedEvictable = expectedNumMmapedEvictable;
        }

        @Override
        public void visit(int numOutstandingMmaps, Map<ExtendedBlockId, ShortCircuitReplica> replicas, Map<ExtendedBlockId, InvalidToken> failedLoads, LinkedMap evictable, LinkedMap evictableMmapped) {
            if ((expectedNumOutstandingMmaps) >= 0) {
                Assert.assertEquals(expectedNumOutstandingMmaps, numOutstandingMmaps);
            }
            if ((expectedNumReplicas) >= 0) {
                Assert.assertEquals(expectedNumReplicas, replicas.size());
            }
            if ((expectedNumEvictable) >= 0) {
                Assert.assertEquals(expectedNumEvictable, evictable.size());
            }
            if ((expectedNumMmapedEvictable) >= 0) {
                Assert.assertEquals(expectedNumMmapedEvictable, evictableMmapped.size());
            }
        }
    }

    @Test
    public void testZeroCopyMmapCache() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        final int TEST_FILE_LENGTH = 5 * (TestEnhancedByteBufferAccess.BLOCK_SIZE);
        final int RANDOM_SEED = 23453;
        final String CONTEXT = "testZeroCopyMmapCacheContext";
        FSDataInputStream fsIn = null;
        ByteBuffer[] results = new ByteBuffer[]{ null, null, null, null };
        DistributedFileSystem fs = null;
        conf.set(DFS_CLIENT_CONTEXT, CONTEXT);
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        fs = cluster.getFileSystem();
        DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), RANDOM_SEED);
        try {
            DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
        } catch (InterruptedException e) {
            Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
        } catch (TimeoutException e) {
            Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
        }
        fsIn = fs.open(TEST_PATH);
        byte[] original = new byte[TEST_FILE_LENGTH];
        IOUtils.readFully(fsIn, original, 0, TEST_FILE_LENGTH);
        fsIn.close();
        fsIn = fs.open(TEST_PATH);
        final ShortCircuitCache cache = ClientContext.get(CONTEXT, conf).getShortCircuitCache();
        cache.accept(new TestEnhancedByteBufferAccess.CountingVisitor(0, 5, 5, 0));
        results[0] = fsIn.read(null, TestEnhancedByteBufferAccess.BLOCK_SIZE, EnumSet.of(SKIP_CHECKSUMS));
        fsIn.seek(0);
        results[1] = fsIn.read(null, TestEnhancedByteBufferAccess.BLOCK_SIZE, EnumSet.of(SKIP_CHECKSUMS));
        // The mmap should be of the first block of the file.
        final ExtendedBlock firstBlock = DFSTestUtil.getFirstBlock(fs, TEST_PATH);
        cache.accept(new CacheVisitor() {
            @Override
            public void visit(int numOutstandingMmaps, Map<ExtendedBlockId, ShortCircuitReplica> replicas, Map<ExtendedBlockId, InvalidToken> failedLoads, LinkedMap evictable, LinkedMap evictableMmapped) {
                ShortCircuitReplica replica = replicas.get(new ExtendedBlockId(firstBlock.getBlockId(), firstBlock.getBlockPoolId()));
                Assert.assertNotNull(replica);
                Assert.assertTrue(replica.hasMmap());
                // The replica should not yet be evictable, since we have it open.
                Assert.assertNull(replica.getEvictableTimeNs());
            }
        });
        // Read more blocks.
        results[2] = fsIn.read(null, TestEnhancedByteBufferAccess.BLOCK_SIZE, EnumSet.of(SKIP_CHECKSUMS));
        results[3] = fsIn.read(null, TestEnhancedByteBufferAccess.BLOCK_SIZE, EnumSet.of(SKIP_CHECKSUMS));
        // we should have 3 mmaps, 1 evictable
        cache.accept(new TestEnhancedByteBufferAccess.CountingVisitor(3, 5, 2, 0));
        // After we close the cursors, the mmaps should be evictable for
        // a brief period of time.  Then, they should be closed (we're
        // using a very quick timeout)
        for (ByteBuffer buffer : results) {
            if (buffer != null) {
                fsIn.releaseBuffer(buffer);
            }
        }
        fsIn.close();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            public Boolean get() {
                final MutableBoolean finished = new MutableBoolean(false);
                cache.accept(new CacheVisitor() {
                    @Override
                    public void visit(int numOutstandingMmaps, Map<ExtendedBlockId, ShortCircuitReplica> replicas, Map<ExtendedBlockId, InvalidToken> failedLoads, LinkedMap evictable, LinkedMap evictableMmapped) {
                        finished.setValue(evictableMmapped.isEmpty());
                    }
                });
                return finished.booleanValue();
            }
        }, 10, 60000);
        cache.accept(new TestEnhancedByteBufferAccess.CountingVisitor(0, (-1), (-1), (-1)));
        fs.close();
        cluster.shutdown();
    }

    /**
     * Test HDFS fallback reads.  HDFS streams support the ByteBufferReadable
     * interface.
     */
    @Test
    public void testHdfsFallbackReads() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        final int TEST_FILE_LENGTH = 16385;
        final int RANDOM_SEED = 23453;
        FSDataInputStream fsIn = null;
        DistributedFileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), RANDOM_SEED);
            try {
                DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            } catch (InterruptedException e) {
                Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
            } catch (TimeoutException e) {
                Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
            }
            fsIn = fs.open(TEST_PATH);
            byte[] original = new byte[TEST_FILE_LENGTH];
            IOUtils.readFully(fsIn, original, 0, TEST_FILE_LENGTH);
            fsIn.close();
            fsIn = fs.open(TEST_PATH);
            TestEnhancedByteBufferAccess.testFallbackImpl(fsIn, original);
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    private static class RestrictedAllocatingByteBufferPool implements ByteBufferPool {
        private final boolean direct;

        RestrictedAllocatingByteBufferPool(boolean direct) {
            this.direct = direct;
        }

        @Override
        public ByteBuffer getBuffer(boolean direct, int length) {
            Preconditions.checkArgument(((this.direct) == direct));
            return direct ? ByteBuffer.allocateDirect(length) : ByteBuffer.allocate(length);
        }

        @Override
        public void putBuffer(ByteBuffer buffer) {
        }
    }

    /**
     * Test the {@link ByteBufferUtil#fallbackRead} function directly.
     */
    @Test
    public void testFallbackRead() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        final int TEST_FILE_LENGTH = 16385;
        final int RANDOM_SEED = 23453;
        FSDataInputStream fsIn = null;
        DistributedFileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), RANDOM_SEED);
            try {
                DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            } catch (InterruptedException e) {
                Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
            } catch (TimeoutException e) {
                Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
            }
            fsIn = fs.open(TEST_PATH);
            byte[] original = new byte[TEST_FILE_LENGTH];
            IOUtils.readFully(fsIn, original, 0, TEST_FILE_LENGTH);
            fsIn.close();
            fsIn = fs.open(TEST_PATH);
            TestEnhancedByteBufferAccess.testFallbackImpl(fsIn, original);
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    /**
     * Test fallback reads on a stream which does not support the
     * ByteBufferReadable * interface.
     */
    @Test
    public void testIndirectFallbackReads() throws Exception {
        final String testPath = GenericTestUtils.getTestDir("indirectFallbackTestFile").getAbsolutePath();
        final int TEST_FILE_LENGTH = 16385;
        final int RANDOM_SEED = 23453;
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(testPath);
            Random random = new Random(RANDOM_SEED);
            byte[] original = new byte[TEST_FILE_LENGTH];
            random.nextBytes(original);
            fos.write(original);
            fos.close();
            fos = null;
            fis = new FileInputStream(testPath);
            TestEnhancedByteBufferAccess.testFallbackImpl(fis, original);
        } finally {
            IOUtils.cleanupWithLogger(TestEnhancedByteBufferAccess.LOG, fos, fis);
            new File(testPath).delete();
        }
    }

    /**
     * Test that we can zero-copy read cached data even without disabling
     * checksums.
     */
    @Test(timeout = 120000)
    public void testZeroCopyReadOfCachedData() throws Exception {
        BlockReaderTestUtil.enableShortCircuitShmTracing();
        BlockReaderTestUtil.enableBlockReaderFactoryTracing();
        BlockReaderTestUtil.enableHdfsCachingTracing();
        final int TEST_FILE_LENGTH = TestEnhancedByteBufferAccess.BLOCK_SIZE;
        final Path TEST_PATH = new Path("/a");
        final int RANDOM_SEED = 23453;
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        conf.setBoolean(SKIP_CHECKSUM_KEY, false);
        final String CONTEXT = "testZeroCopyReadOfCachedData";
        conf.set(DFS_CLIENT_CONTEXT, CONTEXT);
        conf.setLong(DFSConfigKeys.DFS_DATANODE_MAX_LOCKED_MEMORY_KEY, DFSTestUtil.roundUpToMultiple(TEST_FILE_LENGTH, ((int) (POSIX.getCacheManipulator().getOperatingSystemPageSize()))));
        MiniDFSCluster cluster = null;
        ByteBuffer result = null;
        ByteBuffer result2 = null;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        DistributedFileSystem fs = cluster.getFileSystem();
        DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), RANDOM_SEED);
        DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
        byte[] original = DFSTestUtil.calculateFileContentsFromSeed(RANDOM_SEED, TEST_FILE_LENGTH);
        // Prior to caching, the file can't be read via zero-copy
        FSDataInputStream fsIn = fs.open(TEST_PATH);
        try {
            result = fsIn.read(null, (TEST_FILE_LENGTH / 2), EnumSet.noneOf(ReadOption.class));
            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // Cache the file
        fs.addCachePool(new CachePoolInfo("pool1"));
        long directiveId = fs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(TEST_PATH).setReplication(((short) (1))).setPool("pool1").build());
        int numBlocks = ((int) (Math.ceil((((double) (TEST_FILE_LENGTH)) / (TestEnhancedByteBufferAccess.BLOCK_SIZE)))));
        DFSTestUtil.verifyExpectedCacheUsage(DFSTestUtil.roundUpToMultiple(TEST_FILE_LENGTH, TestEnhancedByteBufferAccess.BLOCK_SIZE), numBlocks, cluster.getDataNodes().get(0).getFSDataset());
        try {
            result = fsIn.read(null, TEST_FILE_LENGTH, EnumSet.noneOf(ReadOption.class));
        } catch (UnsupportedOperationException e) {
            Assert.fail("expected to be able to read cached file via zero-copy");
        }
        Assert.assertArrayEquals(Arrays.copyOfRange(original, 0, TestEnhancedByteBufferAccess.BLOCK_SIZE), TestEnhancedByteBufferAccess.byteBufferToArray(result));
        // Test that files opened after the cache operation has finished
        // still get the benefits of zero-copy (regression test for HDFS-6086)
        FSDataInputStream fsIn2 = fs.open(TEST_PATH);
        try {
            result2 = fsIn2.read(null, TEST_FILE_LENGTH, EnumSet.noneOf(ReadOption.class));
        } catch (UnsupportedOperationException e) {
            Assert.fail("expected to be able to read cached file via zero-copy");
        }
        Assert.assertArrayEquals(Arrays.copyOfRange(original, 0, TestEnhancedByteBufferAccess.BLOCK_SIZE), TestEnhancedByteBufferAccess.byteBufferToArray(result2));
        fsIn2.releaseBuffer(result2);
        fsIn2.close();
        // check that the replica is anchored
        final ExtendedBlock firstBlock = DFSTestUtil.getFirstBlock(fs, TEST_PATH);
        final ShortCircuitCache cache = ClientContext.get(CONTEXT, conf).getShortCircuitCache();
        waitForReplicaAnchorStatus(cache, firstBlock, true, true, 1);
        // Uncache the replica
        fs.removeCacheDirective(directiveId);
        waitForReplicaAnchorStatus(cache, firstBlock, false, true, 1);
        fsIn.releaseBuffer(result);
        waitForReplicaAnchorStatus(cache, firstBlock, false, false, 1);
        DFSTestUtil.verifyExpectedCacheUsage(0, 0, fsd);
        fsIn.close();
        fs.close();
        cluster.shutdown();
    }

    @Test
    public void testClientMmapDisable() throws Exception {
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        conf.setBoolean(ENABLED_KEY, false);
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        final int TEST_FILE_LENGTH = 16385;
        final int RANDOM_SEED = 23453;
        final String CONTEXT = "testClientMmapDisable";
        FSDataInputStream fsIn = null;
        DistributedFileSystem fs = null;
        conf.set(DFS_CLIENT_CONTEXT, CONTEXT);
        try {
            // With HdfsClientConfigKeys.Mmap.ENABLED_KEY set to false,
            // we should not do memory mapped reads.
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), RANDOM_SEED);
            DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            fsIn = fs.open(TEST_PATH);
            try {
                fsIn.read(null, 1, EnumSet.of(SKIP_CHECKSUMS));
                Assert.fail(("expected zero-copy read to fail when client mmaps " + "were disabled."));
            } catch (UnsupportedOperationException e) {
            }
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
        fsIn = null;
        fs = null;
        cluster = null;
        try {
            // Now try again with HdfsClientConfigKeys.Mmap.CACHE_SIZE_KEY == 0.
            conf.setBoolean(ENABLED_KEY, true);
            conf.setInt(CACHE_SIZE_KEY, 0);
            conf.set(DFS_CLIENT_CONTEXT, (CONTEXT + ".1"));
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), RANDOM_SEED);
            DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            fsIn = fs.open(TEST_PATH);
            ByteBuffer buf = fsIn.read(null, 1, EnumSet.of(SKIP_CHECKSUMS));
            fsIn.releaseBuffer(buf);
            // Test EOF behavior
            IOUtils.skipFully(fsIn, (TEST_FILE_LENGTH - 1));
            buf = fsIn.read(null, 1, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(null, buf);
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (fs != null)
                fs.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void test2GBMmapLimit() throws Exception {
        Assume.assumeTrue(BlockReaderTestUtil.shouldTestLargeFiles());
        HdfsConfiguration conf = TestEnhancedByteBufferAccess.initZeroCopyTest();
        final long TEST_FILE_LENGTH = 2469605888L;
        conf.set(DFS_CHECKSUM_TYPE_KEY, "NULL");
        conf.setLong(DFS_BLOCK_SIZE_KEY, TEST_FILE_LENGTH);
        MiniDFSCluster cluster = null;
        final Path TEST_PATH = new Path("/a");
        final String CONTEXT = "test2GBMmapLimit";
        conf.set(DFS_CLIENT_CONTEXT, CONTEXT);
        FSDataInputStream fsIn = null;
        FSDataInputStream fsIn2 = null;
        ByteBuffer buf1 = null;
        ByteBuffer buf2 = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_FILE_LENGTH, ((short) (1)), 11);
            DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            fsIn = fs.open(TEST_PATH);
            buf1 = fsIn.read(null, 1, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(1, buf1.remaining());
            fsIn.releaseBuffer(buf1);
            buf1 = null;
            fsIn.seek(2147483640L);
            buf1 = fsIn.read(null, 1024, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(7, buf1.remaining());
            Assert.assertEquals(Integer.MAX_VALUE, buf1.limit());
            fsIn.releaseBuffer(buf1);
            buf1 = null;
            Assert.assertEquals(2147483647L, fsIn.getPos());
            try {
                buf1 = fsIn.read(null, 1024, EnumSet.of(SKIP_CHECKSUMS));
                Assert.fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException e) {
                // expected; can't read past 2GB boundary.
            }
            fsIn.close();
            fsIn = null;
            // Now create another file with normal-sized blocks, and verify we
            // can read past 2GB
            final Path TEST_PATH2 = new Path("/b");
            conf.setLong(DFS_BLOCK_SIZE_KEY, 268435456L);
            DFSTestUtil.createFile(fs, TEST_PATH2, (1024 * 1024), TEST_FILE_LENGTH, 268435456L, ((short) (1)), 10);
            fsIn2 = fs.open(TEST_PATH2);
            fsIn2.seek(2147483640L);
            buf2 = fsIn2.read(null, 1024, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(8, buf2.remaining());
            Assert.assertEquals(2147483648L, fsIn2.getPos());
            fsIn2.releaseBuffer(buf2);
            buf2 = null;
            buf2 = fsIn2.read(null, 1024, EnumSet.of(SKIP_CHECKSUMS));
            Assert.assertEquals(1024, buf2.remaining());
            Assert.assertEquals(2147484672L, fsIn2.getPos());
            fsIn2.releaseBuffer(buf2);
            buf2 = null;
        } finally {
            if (buf1 != null) {
                fsIn.releaseBuffer(buf1);
            }
            if (buf2 != null) {
                fsIn2.releaseBuffer(buf2);
            }
            IOUtils.cleanup(null, fsIn, fsIn2);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

