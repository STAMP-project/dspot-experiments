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


import DataNode.LOG;
import FsDatasetSpi.FsVolumeReferences;
import com.google.common.base.Supplier;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.datanode.VolumeScanner.ScanResultHandler;
import org.apache.hadoop.hdfs.server.datanode.VolumeScanner.Statistics;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsVolumeImpl;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBlockScanner {
    public static final Logger LOG = LoggerFactory.getLogger(TestBlockScanner.class);

    private static class TestContext implements Closeable {
        final int numNameServices;

        final MiniDFSCluster cluster;

        final DistributedFileSystem[] dfs;

        final String[] bpids;

        final DataNode datanode;

        final BlockScanner blockScanner;

        final FsDatasetSpi<? extends FsVolumeSpi> data;

        final FsVolumeReferences volumes;

        TestContext(Configuration conf, int numNameServices) throws Exception {
            this.numNameServices = numNameServices;
            File basedir = new File(GenericTestUtils.getRandomizedTempPath());
            MiniDFSCluster.Builder bld = new MiniDFSCluster.Builder(conf, basedir).numDataNodes(1).storagesPerDatanode(1);
            if (numNameServices > 1) {
                bld.nnTopology(MiniDFSNNTopology.simpleFederatedTopology(numNameServices));
            }
            cluster = bld.build();
            cluster.waitActive();
            dfs = new DistributedFileSystem[numNameServices];
            for (int i = 0; i < numNameServices; i++) {
                dfs[i] = cluster.getFileSystem(i);
            }
            bpids = new String[numNameServices];
            for (int i = 0; i < numNameServices; i++) {
                bpids[i] = cluster.getNamesystem(i).getBlockPoolId();
            }
            datanode = cluster.getDataNodes().get(0);
            blockScanner = datanode.getBlockScanner();
            for (int i = 0; i < numNameServices; i++) {
                dfs[i].mkdirs(new Path("/test"));
            }
            data = datanode.getFSDataset();
            volumes = data.getFsVolumeReferences();
        }

        @Override
        public void close() throws IOException {
            volumes.close();
            if ((cluster) != null) {
                for (int i = 0; i < (numNameServices); i++) {
                    dfs[i].delete(new Path("/test"), true);
                    dfs[i].close();
                }
                cluster.shutdown();
            }
        }

        public void createFiles(int nsIdx, int numFiles, int length) throws Exception {
            for (int blockIdx = 0; blockIdx < numFiles; blockIdx++) {
                DFSTestUtil.createFile(dfs[nsIdx], getPath(blockIdx), length, ((short) (1)), 123L);
            }
        }

        public Path getPath(int fileIdx) {
            return new Path(("/test/" + fileIdx));
        }

        public ExtendedBlock getFileBlock(int nsIdx, int fileIdx) throws Exception {
            return DFSTestUtil.getFirstBlock(dfs[nsIdx], getPath(fileIdx));
        }

        public FsDatasetTestUtils.MaterializedReplica getMaterializedReplica(int nsIdx, int fileIdx) throws Exception {
            return cluster.getMaterializedReplica(0, getFileBlock(nsIdx, fileIdx));
        }
    }

    @Test(timeout = 60000)
    public void testVolumeIteratorWithoutCaching() throws Exception {
        testVolumeIteratorImpl(5, 0);
    }

    @Test(timeout = 300000)
    public void testVolumeIteratorWithCaching() throws Exception {
        testVolumeIteratorImpl(600, 100);
    }

    @Test(timeout = 60000)
    public void testDisableVolumeScanner() throws Exception {
        Configuration conf = new Configuration();
        TestBlockScanner.disableBlockScanner(conf);
        TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 1);
        try {
            Assert.assertFalse(ctx.datanode.getBlockScanner().isEnabled());
        } finally {
            ctx.close();
        }
    }

    public static class TestScanResultHandler extends ScanResultHandler {
        static class Info {
            boolean shouldRun = false;

            final Set<ExtendedBlock> badBlocks = new HashSet<ExtendedBlock>();

            final Set<ExtendedBlock> goodBlocks = new HashSet<ExtendedBlock>();

            long blocksScanned = 0;

            Semaphore sem = null;

            @Override
            public String toString() {
                final StringBuilder bld = new StringBuilder();
                bld.append("ScanResultHandler.Info{");
                bld.append("shouldRun=").append(shouldRun).append(", ");
                bld.append("blocksScanned=").append(blocksScanned).append(", ");
                bld.append("sem#availablePermits=").append(sem.availablePermits()).append(", ");
                bld.append("badBlocks=").append(badBlocks).append(", ");
                bld.append("goodBlocks=").append(goodBlocks);
                bld.append("}");
                return bld.toString();
            }
        }

        private VolumeScanner scanner;

        static final ConcurrentHashMap<String, TestBlockScanner.TestScanResultHandler.Info> infos = new ConcurrentHashMap<String, TestBlockScanner.TestScanResultHandler.Info>();

        static TestBlockScanner.TestScanResultHandler.Info getInfo(FsVolumeSpi volume) {
            TestBlockScanner.TestScanResultHandler.Info newInfo = new TestBlockScanner.TestScanResultHandler.Info();
            TestBlockScanner.TestScanResultHandler.Info prevInfo = TestBlockScanner.TestScanResultHandler.infos.putIfAbsent(volume.getStorageID(), newInfo);
            return prevInfo == null ? newInfo : prevInfo;
        }

        @Override
        public void setup(VolumeScanner scanner) {
            this.scanner = scanner;
            TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(scanner.volume);
            TestBlockScanner.LOG.info("about to start scanning.");
            synchronized(info) {
                while (!(info.shouldRun)) {
                    try {
                        info.wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
            TestBlockScanner.LOG.info("starting scanning.");
        }

        @Override
        public void handle(ExtendedBlock block, IOException e) {
            TestBlockScanner.LOG.info("handling block {} (exception {})", block, e);
            TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(scanner.volume);
            Semaphore sem;
            synchronized(info) {
                sem = info.sem;
            }
            if (sem != null) {
                try {
                    sem.acquire();
                } catch (InterruptedException ie) {
                    throw new RuntimeException("interrupted");
                }
            }
            synchronized(info) {
                if (!(info.shouldRun)) {
                    throw new RuntimeException("stopping volumescanner thread.");
                }
                if (e == null) {
                    info.goodBlocks.add(block);
                } else {
                    info.badBlocks.add(block);
                }
                (info.blocksScanned)++;
            }
        }
    }

    /**
     * Test scanning all blocks.  Set the scan period high enough that
     * we shouldn't rescan any block during this test.
     */
    @Test(timeout = 60000)
    public void testScanAllBlocksNoRescan() throws Exception {
        testScanAllBlocksImpl(false);
    }

    /**
     * Test scanning all blocks.  Set the scan period high enough that
     * we should rescan all blocks at least twice during this test.
     */
    @Test(timeout = 60000)
    public void testScanAllBlocksWithRescan() throws Exception {
        testScanAllBlocksImpl(true);
    }

    /**
     * Test that we don't scan too many blocks per second.
     */
    @Test(timeout = 120000)
    public void testScanRateLimit() throws Exception {
        Configuration conf = new Configuration();
        // Limit scan bytes per second dramatically
        conf.setLong(DFSConfigKeys.DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND, 4096L);
        // Scan continuously
        conf.setLong(Conf.INTERNAL_DFS_DATANODE_SCAN_PERIOD_MS, 1L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 1);
        final int NUM_EXPECTED_BLOCKS = 5;
        ctx.createFiles(0, NUM_EXPECTED_BLOCKS, 4096);
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        long startMs = Time.monotonicNow();
        synchronized(info) {
            info.shouldRun = true;
            info.notify();
        }
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    return (info.blocksScanned) > 0;
                }
            }
        }, 1, 30000);
        Thread.sleep(2000);
        synchronized(info) {
            long endMs = Time.monotonicNow();
            // Should scan no more than one block a second.
            long seconds = ((endMs + 999) - startMs) / 1000;
            long maxBlocksScanned = seconds * 1;
            Assert.assertTrue((((((("The number of blocks scanned is too large.  Scanned " + (info.blocksScanned)) + " blocks; only expected to scan at most ") + maxBlocksScanned) + " in ") + seconds) + " seconds."), ((info.blocksScanned) <= maxBlocksScanned));
        }
        ctx.close();
    }

    @Test(timeout = 120000)
    public void testCorruptBlockHandling() throws Exception {
        Configuration conf = new Configuration();
        conf.setLong(DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, 100L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 1);
        final int NUM_EXPECTED_BLOCKS = 5;
        final int CORRUPT_INDEX = 3;
        ctx.createFiles(0, NUM_EXPECTED_BLOCKS, 4);
        ExtendedBlock badBlock = ctx.getFileBlock(0, CORRUPT_INDEX);
        ctx.cluster.corruptBlockOnDataNodes(badBlock);
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        synchronized(info) {
            info.shouldRun = true;
            info.notify();
        }
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    return (info.blocksScanned) == NUM_EXPECTED_BLOCKS;
                }
            }
        }, 3, 30000);
        synchronized(info) {
            Assert.assertTrue(info.badBlocks.contains(badBlock));
            for (int i = 0; i < NUM_EXPECTED_BLOCKS; i++) {
                if (i != CORRUPT_INDEX) {
                    ExtendedBlock block = ctx.getFileBlock(0, i);
                    Assert.assertTrue(info.goodBlocks.contains(block));
                }
            }
        }
        ctx.close();
    }

    /**
     * Test that we save the scan cursor when shutting down the datanode, and
     * restart scanning from there when the datanode is restarted.
     */
    @Test(timeout = 120000)
    public void testDatanodeCursor() throws Exception {
        Configuration conf = new Configuration();
        conf.setLong(DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, 100L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        conf.setLong(Conf.INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS, 0L);
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 1);
        final int NUM_EXPECTED_BLOCKS = 10;
        ctx.createFiles(0, NUM_EXPECTED_BLOCKS, 1);
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        synchronized(info) {
            info.sem = new Semaphore(5);
            info.shouldRun = true;
            info.notify();
        }
        // Scan the first 5 blocks
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    return (info.blocksScanned) == 5;
                }
            }
        }, 3, 30000);
        synchronized(info) {
            Assert.assertEquals(5, info.goodBlocks.size());
            Assert.assertEquals(5, info.blocksScanned);
            info.shouldRun = false;
        }
        ctx.datanode.shutdown();
        URI vURI = ctx.volumes.get(0).getStorageLocation().getUri();
        File cursorPath = new File(new File(new File(new File(vURI), "current"), ctx.bpids[0]), "scanner.cursor");
        Assert.assertTrue(("Failed to find cursor save file in " + (cursorPath.getAbsolutePath())), cursorPath.exists());
        Set<ExtendedBlock> prevGoodBlocks = new HashSet<ExtendedBlock>();
        synchronized(info) {
            info.sem = new Semaphore(4);
            prevGoodBlocks.addAll(info.goodBlocks);
            info.goodBlocks.clear();
        }
        // The block that we were scanning when we shut down the DN won't get
        // recorded.
        // After restarting the datanode, we should scan the next 4 blocks.
        ctx.cluster.restartDataNode(0);
        synchronized(info) {
            info.shouldRun = true;
            info.notify();
        }
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    if ((info.blocksScanned) != 9) {
                        TestBlockScanner.LOG.info("Waiting for blocksScanned to reach 9.  It is at {}", info.blocksScanned);
                    }
                    return (info.blocksScanned) == 9;
                }
            }
        }, 3, 30000);
        synchronized(info) {
            Assert.assertEquals(4, info.goodBlocks.size());
            info.goodBlocks.addAll(prevGoodBlocks);
            Assert.assertEquals(9, info.goodBlocks.size());
            Assert.assertEquals(9, info.blocksScanned);
        }
        ctx.datanode.shutdown();
        // After restarting the datanode, we should not scan any more blocks.
        // This is because we reached the end of the block pool earlier, and
        // the scan period is much, much longer than the test time.
        synchronized(info) {
            info.sem = null;
            info.shouldRun = false;
            info.goodBlocks.clear();
        }
        ctx.cluster.restartDataNode(0);
        synchronized(info) {
            info.shouldRun = true;
            info.notify();
        }
        Thread.sleep(3000);
        synchronized(info) {
            Assert.assertTrue(info.goodBlocks.isEmpty());
        }
        ctx.close();
    }

    @Test(timeout = 120000)
    public void testMultipleBlockPoolScanning() throws Exception {
        Configuration conf = new Configuration();
        conf.setLong(DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, 100L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 3);
        // We scan 5 bytes per file (1 byte in file, 4 bytes of checksum)
        final int BYTES_SCANNED_PER_FILE = 5;
        int TOTAL_FILES = 16;
        ctx.createFiles(0, TOTAL_FILES, 1);
        // start scanning
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        synchronized(info) {
            info.shouldRun = true;
            info.notify();
        }
        // Wait for all the block pools to be scanned.
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    Statistics stats = ctx.blockScanner.getVolumeStats(ctx.volumes.get(0).getStorageID());
                    if ((stats.scansSinceRestart) < 3) {
                        TestBlockScanner.LOG.info("Waiting for scansSinceRestart to reach 3 (it is {})", stats.scansSinceRestart);
                        return false;
                    }
                    if (!(stats.eof)) {
                        TestBlockScanner.LOG.info("Waiting for eof.");
                        return false;
                    }
                    return true;
                }
            }
        }, 3, 30000);
        Statistics stats = ctx.blockScanner.getVolumeStats(ctx.volumes.get(0).getStorageID());
        Assert.assertEquals(TOTAL_FILES, stats.blocksScannedSinceRestart);
        Assert.assertEquals((BYTES_SCANNED_PER_FILE * TOTAL_FILES), stats.bytesScannedInPastHour);
        ctx.close();
    }

    @Test(timeout = 120000)
    public void testNextSorted() throws Exception {
        List<String> arr = new LinkedList<String>();
        arr.add("1");
        arr.add("3");
        arr.add("5");
        arr.add("7");
        Assert.assertEquals("3", FsVolumeImpl.nextSorted(arr, "2"));
        Assert.assertEquals("3", FsVolumeImpl.nextSorted(arr, "1"));
        Assert.assertEquals("1", FsVolumeImpl.nextSorted(arr, ""));
        Assert.assertEquals("1", FsVolumeImpl.nextSorted(arr, null));
        Assert.assertEquals(null, FsVolumeImpl.nextSorted(arr, "9"));
    }

    @Test(timeout = 120000)
    public void testCalculateNeededBytesPerSec() throws Exception {
        // If we didn't check anything the last hour, we should scan now.
        Assert.assertTrue(VolumeScanner.calculateShouldScan("test", 100, 0, 0, 60));
        // If, on average, we checked 101 bytes/s checked during the last hour,
        // stop checking now.
        Assert.assertFalse(VolumeScanner.calculateShouldScan("test", 100, (101 * 3600), 1000, 5000));
        // Target is 1 byte / s, but we didn't scan anything in the last minute.
        // Should scan now.
        Assert.assertTrue(VolumeScanner.calculateShouldScan("test", 1, 3540, 0, 60));
        // Target is 1000000 byte / s, but we didn't scan anything in the last
        // minute.  Should scan now.
        Assert.assertTrue(VolumeScanner.calculateShouldScan("test", 100000L, 354000000L, 0, 60));
        Assert.assertFalse(VolumeScanner.calculateShouldScan("test", 100000L, 365000000L, 0, 60));
    }

    /**
     * Test that we can mark certain blocks as suspect, and get them quickly
     * rescanned that way.  See HDFS-7686 and HDFS-7548.
     */
    @Test(timeout = 120000)
    public void testMarkSuspectBlock() throws Exception {
        Configuration conf = new Configuration();
        // Set a really long scan period.
        conf.setLong(DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, 100L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        conf.setLong(Conf.INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS, 0L);
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 1);
        final int NUM_EXPECTED_BLOCKS = 10;
        ctx.createFiles(0, NUM_EXPECTED_BLOCKS, 1);
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        String storageID = ctx.volumes.get(0).getStorageID();
        synchronized(info) {
            info.sem = new Semaphore(4);
            info.shouldRun = true;
            info.notify();
        }
        // Scan the first 4 blocks
        TestBlockScanner.LOG.info("Waiting for the first 4 blocks to be scanned.");
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    if ((info.blocksScanned) >= 4) {
                        TestBlockScanner.LOG.info("info = {}.  blockScanned has now reached 4.", info);
                        return true;
                    } else {
                        TestBlockScanner.LOG.info("info = {}.  Waiting for blockScanned to reach 4.", info);
                        return false;
                    }
                }
            }
        }, 50, 30000);
        // We should have scanned 4 blocks
        synchronized(info) {
            Assert.assertEquals("Expected 4 good blocks.", 4, info.goodBlocks.size());
            info.goodBlocks.clear();
            Assert.assertEquals("Expected 4 blocksScanned", 4, info.blocksScanned);
            Assert.assertEquals("Did not expect bad blocks.", 0, info.badBlocks.size());
            info.blocksScanned = 0;
        }
        ExtendedBlock first = ctx.getFileBlock(0, 0);
        ctx.datanode.getBlockScanner().markSuspectBlock(storageID, first);
        // When we increment the semaphore, the TestScanResultHandler will finish
        // adding the block that it was scanning previously (the 5th block).
        // We increment the semaphore twice so that the handler will also
        // get a chance to see the suspect block which we just requested the
        // VolumeScanner to process.
        info.sem.release(2);
        TestBlockScanner.LOG.info("Waiting for 2 more blocks to be scanned.");
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    if ((info.blocksScanned) >= 2) {
                        TestBlockScanner.LOG.info("info = {}.  blockScanned has now reached 2.", info);
                        return true;
                    } else {
                        TestBlockScanner.LOG.info("info = {}.  Waiting for blockScanned to reach 2.", info);
                        return false;
                    }
                }
            }
        }, 50, 30000);
        synchronized(info) {
            Assert.assertTrue((("Expected block " + first) + " to have been scanned."), info.goodBlocks.contains(first));
            Assert.assertEquals(2, info.goodBlocks.size());
            info.goodBlocks.clear();
            Assert.assertEquals("Did not expect bad blocks.", 0, info.badBlocks.size());
            Assert.assertEquals(2, info.blocksScanned);
            info.blocksScanned = 0;
        }
        // Re-mark the same block as suspect.
        ctx.datanode.getBlockScanner().markSuspectBlock(storageID, first);
        info.sem.release(10);
        TestBlockScanner.LOG.info("Waiting for 5 more blocks to be scanned.");
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    if ((info.blocksScanned) >= 5) {
                        TestBlockScanner.LOG.info("info = {}.  blockScanned has now reached 5.", info);
                        return true;
                    } else {
                        TestBlockScanner.LOG.info("info = {}.  Waiting for blockScanned to reach 5.", info);
                        return false;
                    }
                }
            }
        }, 50, 30000);
        synchronized(info) {
            Assert.assertEquals(5, info.goodBlocks.size());
            Assert.assertEquals(0, info.badBlocks.size());
            Assert.assertEquals(5, info.blocksScanned);
            // We should not have rescanned the "suspect block",
            // because it was recently rescanned by the suspect block system.
            // This is a test of the "suspect block" rate limiting.
            Assert.assertFalse((((("We should not " + "have rescanned block ") + first) + ", because it should have been ") + "in recentSuspectBlocks."), info.goodBlocks.contains(first));
            info.blocksScanned = 0;
        }
        ctx.close();
    }

    /**
     * Test that blocks which are in the wrong location are ignored.
     */
    @Test(timeout = 120000)
    public void testIgnoreMisplacedBlock() throws Exception {
        Configuration conf = new Configuration();
        // Set a really long scan period.
        conf.setLong(DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, 100L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        conf.setLong(Conf.INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS, 0L);
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, 1);
        final int NUM_FILES = 4;
        ctx.createFiles(0, NUM_FILES, 5);
        FsDatasetTestUtils.MaterializedReplica unreachableReplica = ctx.getMaterializedReplica(0, 1);
        ExtendedBlock unreachableBlock = ctx.getFileBlock(0, 1);
        unreachableReplica.makeUnreachable();
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        String storageID = ctx.volumes.get(0).getStorageID();
        synchronized(info) {
            info.sem = new Semaphore(NUM_FILES);
            info.shouldRun = true;
            info.notify();
        }
        // Scan the first 4 blocks
        TestBlockScanner.LOG.info("Waiting for the blocks to be scanned.");
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized(info) {
                    if ((info.blocksScanned) >= (NUM_FILES - 1)) {
                        TestBlockScanner.LOG.info(("info = {}.  blockScanned has now reached " + (info.blocksScanned)), info);
                        return true;
                    } else {
                        TestBlockScanner.LOG.info(("info = {}.  Waiting for blockScanned to reach " + (NUM_FILES - 1)), info);
                        return false;
                    }
                }
            }
        }, 50, 30000);
        // We should have scanned 4 blocks
        synchronized(info) {
            Assert.assertFalse(info.goodBlocks.contains(unreachableBlock));
            Assert.assertFalse(info.badBlocks.contains(unreachableBlock));
            Assert.assertEquals("Expected 3 good blocks.", 3, info.goodBlocks.size());
            info.goodBlocks.clear();
            Assert.assertEquals("Expected 3 blocksScanned", 3, info.blocksScanned);
            Assert.assertEquals("Did not expect bad blocks.", 0, info.badBlocks.size());
            info.blocksScanned = 0;
        }
        info.sem.release(1);
        ctx.close();
    }

    /**
     * Test concurrent append and scan.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 120000)
    public void testAppendWhileScanning() throws Exception {
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
        Configuration conf = new Configuration();
        // throttle the block scanner: 1MB per second
        conf.setLong(DFSConfigKeys.DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND, 1048576);
        // Set a really long scan period.
        conf.setLong(DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, 100L);
        conf.set(Conf.INTERNAL_VOLUME_SCANNER_SCAN_RESULT_HANDLER, TestBlockScanner.TestScanResultHandler.class.getName());
        conf.setLong(Conf.INTERNAL_DFS_BLOCK_SCANNER_CURSOR_SAVE_INTERVAL_MS, 0L);
        final int numExpectedFiles = 1;
        final int numExpectedBlocks = 1;
        final int numNameServices = 1;
        // the initial file length can not be too small.
        // Otherwise checksum file stream buffer will be pre-filled and
        // BlockSender will not see the updated checksum.
        final int initialFileLength = ((2 * 1024) * 1024) + 100;
        final TestBlockScanner.TestContext ctx = new TestBlockScanner.TestContext(conf, numNameServices);
        // create one file, with one block.
        ctx.createFiles(0, numExpectedFiles, initialFileLength);
        final TestBlockScanner.TestScanResultHandler.Info info = TestBlockScanner.TestScanResultHandler.getInfo(ctx.volumes.get(0));
        String storageID = ctx.volumes.get(0).getStorageID();
        synchronized(info) {
            info.sem = new Semaphore((numExpectedBlocks * 2));
            info.shouldRun = true;
            info.notify();
        }
        // VolumeScanner scans the first block when DN starts.
        // Due to throttler, this should take approximately 2 seconds.
        waitForRescan(info, numExpectedBlocks);
        // update throttler to schedule rescan immediately.
        // this number must be larger than initial file length, otherwise
        // throttler prevents immediate rescan.
        conf.setLong(DFSConfigKeys.DFS_BLOCK_SCANNER_VOLUME_BYTES_PER_SECOND, (initialFileLength + (32 * 1024)));
        BlockScanner.Conf newConf = new BlockScanner.Conf(conf);
        ctx.datanode.getBlockScanner().setConf(newConf);
        // schedule the first block for scanning
        ExtendedBlock first = ctx.getFileBlock(0, 0);
        ctx.datanode.getBlockScanner().markSuspectBlock(storageID, first);
        // append the file before VolumeScanner completes scanning the block,
        // which takes approximately 2 seconds to complete.
        FileSystem fs = ctx.cluster.getFileSystem();
        FSDataOutputStream os = fs.append(ctx.getPath(0));
        long seed = -1;
        int size = 200;
        final byte[] bytes = AppendTestUtil.randomBytes(seed, size);
        os.write(bytes);
        os.hflush();
        os.close();
        // verify that volume scanner does not find bad blocks after append.
        waitForRescan(info, numExpectedBlocks);
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.INFO);
        ctx.close();
    }
}

