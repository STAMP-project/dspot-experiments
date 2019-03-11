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


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY;
import DFSConfigKeys.DFS_DATANODE_DIRECTORYSCAN_THREADS_KEY;
import DFSConfigKeys.DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY;
import DFSConfigKeys.DFS_DATANODE_MAX_LOCKED_MEMORY_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.DF;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.server.datanode.DirectoryScanner.ReportCompiler;
import org.apache.hadoop.hdfs.server.datanode.checker.VolumeCheckResult;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.DataNodeVolumeMetrics;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi.FsVolumeReferences;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeReference;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetTestUtil;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsVolumeImpl;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests {@link DirectoryScanner} handling of differences between blocks on the
 * disk and block in memory.
 */
public class TestDirectoryScanner {
    private static final Logger LOG = LoggerFactory.getLogger(TestDirectoryScanner.class);

    private static final Configuration CONF = new HdfsConfiguration();

    private static final int DEFAULT_GEN_STAMP = 9999;

    private MiniDFSCluster cluster;

    private String bpid;

    private DFSClient client;

    private FsDatasetSpi<? extends FsVolumeSpi> fds = null;

    private DirectoryScanner scanner = null;

    private final Random rand = new Random();

    private final Random r = new Random();

    private static final int BLOCK_LENGTH = 100;

    static {
        TestDirectoryScanner.CONF.setLong(DFS_BLOCK_SIZE_KEY, TestDirectoryScanner.BLOCK_LENGTH);
        TestDirectoryScanner.CONF.setInt(DFS_BYTES_PER_CHECKSUM_KEY, 1);
        TestDirectoryScanner.CONF.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        TestDirectoryScanner.CONF.setLong(DFS_DATANODE_MAX_LOCKED_MEMORY_KEY, getMemlockLimit(Long.MAX_VALUE));
    }

    @Test(timeout = 300000)
    public void testRetainBlockOnPersistentStorage() throws Exception {
        cluster = new MiniDFSCluster.Builder(TestDirectoryScanner.CONF).storageTypes(new StorageType[]{ StorageType.RAM_DISK, StorageType.DEFAULT }).numDataNodes(1).build();
        try {
            cluster.waitActive();
            bpid = cluster.getNamesystem().getBlockPoolId();
            fds = DataNodeTestUtils.getFSDataset(cluster.getDataNodes().get(0));
            client = cluster.getFileSystem().getClient();
            scanner = new DirectoryScanner(fds, TestDirectoryScanner.CONF);
            scanner.setRetainDiffs(true);
            FsDatasetTestUtil.stopLazyWriter(cluster.getDataNodes().get(0));
            // Add a file with 1 block
            List<LocatedBlock> blocks = createFile(GenericTestUtils.getMethodName(), TestDirectoryScanner.BLOCK_LENGTH, false);
            // Ensure no difference between volumeMap and disk.
            scan(1, 0, 0, 0, 0, 0);
            // Make a copy of the block on RAM_DISK and ensure that it is
            // picked up by the scanner.
            duplicateBlock(blocks.get(0).getBlock().getBlockId());
            scan(2, 1, 0, 0, 0, 0, 1);
            verifyStorageType(blocks.get(0).getBlock().getBlockId(), false);
            scan(1, 0, 0, 0, 0, 0);
        } finally {
            if ((scanner) != null) {
                scanner.shutdown();
                scanner = null;
            }
            cluster.shutdown();
            cluster = null;
        }
    }

    @Test(timeout = 300000)
    public void testDeleteBlockOnTransientStorage() throws Exception {
        cluster = new MiniDFSCluster.Builder(TestDirectoryScanner.CONF).storageTypes(new StorageType[]{ StorageType.RAM_DISK, StorageType.DEFAULT }).numDataNodes(1).build();
        try {
            cluster.waitActive();
            bpid = cluster.getNamesystem().getBlockPoolId();
            fds = DataNodeTestUtils.getFSDataset(cluster.getDataNodes().get(0));
            client = cluster.getFileSystem().getClient();
            scanner = new DirectoryScanner(fds, TestDirectoryScanner.CONF);
            scanner.setRetainDiffs(true);
            FsDatasetTestUtil.stopLazyWriter(cluster.getDataNodes().get(0));
            // Create a file on RAM_DISK
            List<LocatedBlock> blocks = createFile(GenericTestUtils.getMethodName(), TestDirectoryScanner.BLOCK_LENGTH, true);
            // Ensure no difference between volumeMap and disk.
            scan(1, 0, 0, 0, 0, 0);
            // Make a copy of the block on DEFAULT storage and ensure that it is
            // picked up by the scanner.
            duplicateBlock(blocks.get(0).getBlock().getBlockId());
            scan(2, 1, 0, 0, 0, 0, 1);
            // Ensure that the copy on RAM_DISK was deleted.
            verifyStorageType(blocks.get(0).getBlock().getBlockId(), false);
            scan(1, 0, 0, 0, 0, 0);
        } finally {
            if ((scanner) != null) {
                scanner.shutdown();
                scanner = null;
            }
            cluster.shutdown();
            cluster = null;
        }
    }

    @Test(timeout = 600000)
    public void testDirectoryScanner() throws Exception {
        // Run the test with and without parallel scanning
        for (int parallelism = 1; parallelism < 3; parallelism++) {
            runTest(parallelism);
        }
    }

    /**
     * Test that the timeslice throttle limits the report compiler thread's
     * execution time correctly. We test by scanning a large block pool and
     * comparing the time spent waiting to the time spent running.
     *
     * The block pool has to be large, or the ratio will be off. The throttle
     * allows the report compiler thread to finish its current cycle when blocking
     * it, so the ratio will always be a little lower than expected. The smaller
     * the block pool, the further off the ratio will be.
     *
     * @throws Exception
     * 		thrown on unexpected failure
     */
    @Test(timeout = 600000)
    public void testThrottling() throws Exception {
        Configuration conf = new Configuration(TestDirectoryScanner.CONF);
        // We need lots of blocks so the report compiler threads have enough to
        // keep them busy while we watch them.
        int blocks = 20000;
        int maxRetries = 3;
        cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            bpid = cluster.getNamesystem().getBlockPoolId();
            fds = DataNodeTestUtils.getFSDataset(cluster.getDataNodes().get(0));
            client = cluster.getFileSystem().getClient();
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 100);
            final int maxBlocksPerFile = ((int) (DFSConfigKeys.DFS_NAMENODE_MAX_BLOCKS_PER_FILE_DEFAULT));
            int numBlocksToCreate = blocks;
            while (numBlocksToCreate > 0) {
                final int toCreate = Math.min(maxBlocksPerFile, numBlocksToCreate);
                createFile(((GenericTestUtils.getMethodName()) + numBlocksToCreate), ((TestDirectoryScanner.BLOCK_LENGTH) * toCreate), false);
                numBlocksToCreate -= toCreate;
            } 
            float ratio = 0.0F;
            int retries = maxRetries;
            while ((retries > 0) && ((ratio < 7.0F) || (ratio > 10.0F))) {
                scanner = new DirectoryScanner(fds, conf);
                ratio = runThrottleTest(blocks);
                retries -= 1;
            } 
            // Waiting should be about 9x running.
            TestDirectoryScanner.LOG.info(("RATIO: " + ratio));
            Assert.assertTrue("Throttle is too restrictive", (ratio <= 10.0F));
            Assert.assertTrue("Throttle is too permissive", (ratio >= 7.0F));
            // Test with a different limit
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 200);
            ratio = 0.0F;
            retries = maxRetries;
            while ((retries > 0) && ((ratio < 2.75F) || (ratio > 4.5F))) {
                scanner = new DirectoryScanner(fds, conf);
                ratio = runThrottleTest(blocks);
                retries -= 1;
            } 
            // Waiting should be about 4x running.
            TestDirectoryScanner.LOG.info(("RATIO: " + ratio));
            Assert.assertTrue("Throttle is too restrictive", (ratio <= 4.5F));
            Assert.assertTrue("Throttle is too permissive", (ratio >= 2.75F));
            // Test with more than 1 thread
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THREADS_KEY, 3);
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 100);
            ratio = 0.0F;
            retries = maxRetries;
            while ((retries > 0) && ((ratio < 7.0F) || (ratio > 10.0F))) {
                scanner = new DirectoryScanner(fds, conf);
                ratio = runThrottleTest(blocks);
                retries -= 1;
            } 
            // Waiting should be about 9x running.
            TestDirectoryScanner.LOG.info(("RATIO: " + ratio));
            Assert.assertTrue("Throttle is too restrictive", (ratio <= 10.0F));
            Assert.assertTrue("Throttle is too permissive", (ratio >= 7.0F));
            // Test with no limit
            scanner = new DirectoryScanner(fds, TestDirectoryScanner.CONF);
            scanner.setRetainDiffs(true);
            scan(blocks, 0, 0, 0, 0, 0);
            scanner.shutdown();
            Assert.assertFalse(scanner.getRunStatus());
            Assert.assertTrue("Throttle appears to be engaged", ((scanner.timeWaitingMs.get()) < 10L));
            Assert.assertTrue("Report complier threads logged no execution time", ((scanner.timeRunningMs.get()) > 0L));
            // Test with a 1ms limit. This also tests whether the scanner can be
            // shutdown cleanly in mid stride.
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 1);
            ratio = 0.0F;
            retries = maxRetries;
            ScheduledExecutorService interruptor = Executors.newScheduledThreadPool(maxRetries);
            try {
                while ((retries > 0) && (ratio < 10)) {
                    scanner = new DirectoryScanner(fds, conf);
                    scanner.setRetainDiffs(true);
                    final AtomicLong nowMs = new AtomicLong();
                    // Stop the scanner after 2 seconds because otherwise it will take an
                    // eternity to complete it's run
                    interruptor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            nowMs.set(Time.monotonicNow());
                            scanner.shutdown();
                        }
                    }, 2L, TimeUnit.SECONDS);
                    scanner.reconcile();
                    Assert.assertFalse(scanner.getRunStatus());
                    long finalMs = nowMs.get();
                    // If the scan didn't complete before the shutdown was run, check
                    // that the shutdown was timely
                    if (finalMs > 0) {
                        TestDirectoryScanner.LOG.info((("Scanner took " + ((Time.monotonicNow()) - finalMs)) + "ms to shutdown"));
                        Assert.assertTrue("Scanner took too long to shutdown", (((Time.monotonicNow()) - finalMs) < 1000L));
                    }
                    ratio = ((float) (scanner.timeWaitingMs.get())) / (scanner.timeRunningMs.get());
                    retries -= 1;
                } 
            } finally {
                interruptor.shutdown();
            }
            // We just want to test that it waits a lot, but it also runs some
            TestDirectoryScanner.LOG.info(("RATIO: " + ratio));
            Assert.assertTrue("Throttle is too permissive", (ratio > 8));
            Assert.assertTrue("Report complier threads logged no execution time", ((scanner.timeRunningMs.get()) > 0L));
            // Test with a 0 limit, i.e. disabled
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 0);
            scanner = new DirectoryScanner(fds, conf);
            scanner.setRetainDiffs(true);
            scan(blocks, 0, 0, 0, 0, 0);
            scanner.shutdown();
            Assert.assertFalse(scanner.getRunStatus());
            Assert.assertTrue("Throttle appears to be engaged", ((scanner.timeWaitingMs.get()) < 10L));
            Assert.assertTrue("Report complier threads logged no execution time", ((scanner.timeRunningMs.get()) > 0L));
            // Test with a 1000 limit, i.e. disabled
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 1000);
            scanner = new DirectoryScanner(fds, conf);
            scanner.setRetainDiffs(true);
            scan(blocks, 0, 0, 0, 0, 0);
            scanner.shutdown();
            Assert.assertFalse(scanner.getRunStatus());
            Assert.assertTrue("Throttle appears to be engaged", ((scanner.timeWaitingMs.get()) < 10L));
            Assert.assertTrue("Report complier threads logged no execution time", ((scanner.timeRunningMs.get()) > 0L));
            // Test that throttle works from regular start
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THREADS_KEY, 1);
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_THROTTLE_LIMIT_MS_PER_SEC_KEY, 10);
            conf.setInt(DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY, 1);
            scanner = new DirectoryScanner(fds, conf);
            scanner.setRetainDiffs(true);
            scanner.start();
            int count = 50;
            while ((count > 0) && ((scanner.timeWaitingMs.get()) < 500L)) {
                Thread.sleep(100L);
                count -= 1;
            } 
            scanner.shutdown();
            Assert.assertFalse(scanner.getRunStatus());
            Assert.assertTrue("Throttle does not appear to be engaged", (count > 0));
        } finally {
            cluster.shutdown();
        }
    }

    private static class TestFsVolumeSpi implements FsVolumeSpi {
        @Override
        public String[] getBlockPoolList() {
            return new String[0];
        }

        @Override
        public FsVolumeReference obtainReference() throws ClosedChannelException {
            return null;
        }

        @Override
        public long getAvailable() throws IOException {
            return 0;
        }

        public File getFinalizedDir(String bpid) throws IOException {
            return new File((("/base/current/" + bpid) + "/finalized"));
        }

        @Override
        public StorageType getStorageType() {
            return StorageType.DEFAULT;
        }

        @Override
        public String getStorageID() {
            return "";
        }

        @Override
        public boolean isTransientStorage() {
            return false;
        }

        @Override
        public void reserveSpaceForReplica(long bytesToReserve) {
        }

        @Override
        public void releaseReservedSpace(long bytesToRelease) {
        }

        @Override
        public void releaseLockedMemory(long bytesToRelease) {
        }

        @Override
        public BlockIterator newBlockIterator(String bpid, String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BlockIterator loadBlockIterator(String bpid, String name) throws IOException {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public FsDatasetSpi getDataset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageLocation getStorageLocation() {
            return null;
        }

        @Override
        public URI getBaseURI() {
            return new File("/base").toURI();
        }

        @Override
        public DF getUsageStats(Configuration conf) {
            return null;
        }

        @Override
        public byte[] loadLastPartialChunkChecksum(File blockFile, File metaFile) throws IOException {
            return null;
        }

        @Override
        public void compileReport(String bpid, Collection<ScanInfo> report, ReportCompiler reportCompiler) throws IOException, InterruptedException {
        }

        @Override
        public FileIoProvider getFileIoProvider() {
            return null;
        }

        @Override
        public DataNodeVolumeMetrics getMetrics() {
            return null;
        }

        @Override
        public VolumeCheckResult check(VolumeCheckContext context) throws Exception {
            return VolumeCheckResult.HEALTHY;
        }
    }

    private static final TestDirectoryScanner.TestFsVolumeSpi TEST_VOLUME = new TestDirectoryScanner.TestFsVolumeSpi();

    private static final String BPID_1 = "BP-783049782-127.0.0.1-1370971773491";

    private static final String BPID_2 = "BP-367845636-127.0.0.1-5895645674231";

    @Test(timeout = 120000)
    public void TestScanInfo() throws Exception {
        testScanInfoObject(123, new File(TestDirectoryScanner.TEST_VOLUME.getFinalizedDir(TestDirectoryScanner.BPID_1).getAbsolutePath(), "blk_123"), new File(TestDirectoryScanner.TEST_VOLUME.getFinalizedDir(TestDirectoryScanner.BPID_1).getAbsolutePath(), "blk_123__1001.meta"));
        testScanInfoObject(464, new File(TestDirectoryScanner.TEST_VOLUME.getFinalizedDir(TestDirectoryScanner.BPID_1).getAbsolutePath(), "blk_123"), null);
        testScanInfoObject(523, null, new File(TestDirectoryScanner.TEST_VOLUME.getFinalizedDir(TestDirectoryScanner.BPID_1).getAbsolutePath(), "blk_123__1009.meta"));
        testScanInfoObject(789, null, null);
        testScanInfoObject(456);
        testScanInfoObject(123, new File(TestDirectoryScanner.TEST_VOLUME.getFinalizedDir(TestDirectoryScanner.BPID_2).getAbsolutePath(), "blk_567"), new File(TestDirectoryScanner.TEST_VOLUME.getFinalizedDir(TestDirectoryScanner.BPID_2).getAbsolutePath(), "blk_567__1004.meta"));
    }

    /**
     * Test the behavior of exception handling during directory scan operation.
     * Directory scanner shouldn't abort the scan on every directory just because
     * one had an error.
     */
    @Test(timeout = 60000)
    public void testExceptionHandlingWhileDirectoryScan() throws Exception {
        cluster = new MiniDFSCluster.Builder(TestDirectoryScanner.CONF).build();
        try {
            cluster.waitActive();
            bpid = cluster.getNamesystem().getBlockPoolId();
            fds = DataNodeTestUtils.getFSDataset(cluster.getDataNodes().get(0));
            client = cluster.getFileSystem().getClient();
            TestDirectoryScanner.CONF.setInt(DFS_DATANODE_DIRECTORYSCAN_THREADS_KEY, 1);
            // Add files with 2 blocks
            createFile(GenericTestUtils.getMethodName(), ((TestDirectoryScanner.BLOCK_LENGTH) * 2), false);
            // Inject error on #getFinalizedDir() so that ReportCompiler#call() will
            // hit exception while preparing the block info report list.
            List<FsVolumeSpi> volumes = new ArrayList<>();
            Iterator<FsVolumeSpi> iterator = fds.getFsVolumeReferences().iterator();
            while (iterator.hasNext()) {
                FsVolumeImpl volume = ((FsVolumeImpl) (iterator.next()));
                FsVolumeImpl spy = Mockito.spy(volume);
                Mockito.doThrow(new IOException("Error while getFinalizedDir")).when(spy).getFinalizedDir(volume.getBlockPoolList()[0]);
                volumes.add(spy);
            } 
            FsVolumeReferences volReferences = new FsVolumeReferences(volumes);
            FsDatasetSpi<? extends FsVolumeSpi> spyFds = Mockito.spy(fds);
            Mockito.doReturn(volReferences).when(spyFds).getFsVolumeReferences();
            scanner = new DirectoryScanner(spyFds, TestDirectoryScanner.CONF);
            scanner.setRetainDiffs(true);
            scanner.reconcile();
        } finally {
            if ((scanner) != null) {
                scanner.shutdown();
                scanner = null;
            }
            cluster.shutdown();
        }
    }

    @Test
    public void testDirectoryScannerInFederatedCluster() throws Exception {
        // Create Federated cluster with two nameservices and one DN
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDirectoryScanner.CONF).nnTopology(MiniDFSNNTopology.simpleHAFederatedTopology(2)).numDataNodes(1).build()) {
            cluster.waitActive();
            cluster.transitionToActive(1);
            cluster.transitionToActive(3);
            fds = DataNodeTestUtils.getFSDataset(cluster.getDataNodes().get(0));
            // Create one block in first nameservice
            FileSystem fs = cluster.getFileSystem(1);
            int bp1Files = 1;
            writeFile(fs, bp1Files);
            // Create two blocks in second nameservice
            FileSystem fs2 = cluster.getFileSystem(3);
            int bp2Files = 2;
            writeFile(fs2, bp2Files);
            // Call the Directory scanner
            scanner = new DirectoryScanner(fds, TestDirectoryScanner.CONF);
            scanner.setRetainDiffs(true);
            scanner.reconcile();
            // Check blocks in corresponding BP
            GenericTestUtils.waitFor(() -> {
                try {
                    bpid = cluster.getNamesystem(1).getBlockPoolId();
                    verifyStats(bp1Files, 0, 0, 0, 0, 0, 0);
                    bpid = cluster.getNamesystem(3).getBlockPoolId();
                    verifyStats(bp2Files, 0, 0, 0, 0, 0, 0);
                } catch ( ex) {
                    return false;
                }
                return true;
            }, 50, 2000);
        } finally {
            if ((scanner) != null) {
                scanner.shutdown();
                scanner = null;
            }
        }
    }
}

