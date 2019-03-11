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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import DFSConfigKeys.DFS_PROVIDER_STORAGEUUID_DEFAULT;
import Options.HandleOpt;
import StorageType.PROVIDED;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathHandle;
import org.apache.hadoop.fs.Writer.Options;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.server.common.FileRegion;
import org.apache.hadoop.hdfs.server.common.blockaliasmap.BlockAliasMap;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataStorage;
import org.apache.hadoop.hdfs.server.datanode.DirectoryScanner;
import org.apache.hadoop.hdfs.server.datanode.DirectoryScanner.ScanInfoVolumeReport;
import org.apache.hadoop.hdfs.server.datanode.FinalizedProvidedReplica;
import org.apache.hadoop.hdfs.server.datanode.ProvidedReplica;
import org.apache.hadoop.hdfs.server.datanode.ReplicaInfo;
import org.apache.hadoop.hdfs.server.datanode.StorageLocation;
import org.apache.hadoop.hdfs.server.datanode.TestProvidedReplicaImpl;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi.BlockIterator;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.AutoCloseableLock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic test cases for provided implementation.
 */
public class TestProvidedImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestFsDatasetImpl.class);

    private static final String BASE_DIR = new FileSystemTestHelper().getTestRootDir();

    private static final int NUM_LOCAL_INIT_VOLUMES = 1;

    // only support one provided volume for now.
    private static final int NUM_PROVIDED_INIT_VOLUMES = 1;

    private static final String[] BLOCK_POOL_IDS = new String[]{ "bpid-0", "bpid-1" };

    private static final int NUM_PROVIDED_BLKS = 10;

    private static final long BLK_LEN = 128 * 1024;

    private static final int MIN_BLK_ID = 0;

    private static final int CHOSEN_BP_ID = 0;

    private static String providedBasePath = TestProvidedImpl.BASE_DIR;

    private Configuration conf;

    private DataNode datanode;

    private DataStorage storage;

    private FsDatasetImpl dataset;

    private static Map<Long, String> blkToPathMap;

    private static List<FsVolumeImpl> providedVolumes;

    private static long spaceUsed = 0;

    /**
     * A simple FileRegion iterator for tests.
     */
    public static class TestFileRegionIterator implements Iterator<FileRegion> {
        private int numBlocks;

        private int currentCount;

        private String basePath;

        public TestFileRegionIterator(String basePath, int minID, int numBlocks) {
            this.currentCount = minID;
            this.numBlocks = numBlocks;
            this.basePath = basePath;
        }

        @Override
        public boolean hasNext() {
            return (currentCount) < (numBlocks);
        }

        @Override
        public FileRegion next() {
            FileRegion region = null;
            if (hasNext()) {
                File newFile = new File(basePath, ("file" + (currentCount)));
                if (!(newFile.exists())) {
                    try {
                        TestProvidedImpl.LOG.info(("Creating file for blkid " + (currentCount)));
                        TestProvidedImpl.blkToPathMap.put(((long) (currentCount)), newFile.getAbsolutePath());
                        TestProvidedImpl.LOG.info(((("Block id " + (currentCount)) + " corresponds to file ") + (newFile.getAbsolutePath())));
                        newFile.createNewFile();
                        Writer writer = new OutputStreamWriter(new FileOutputStream(newFile.getAbsolutePath()), "utf-8");
                        for (int i = 0; i < ((TestProvidedImpl.BLK_LEN) / ((Integer.SIZE) / 8)); i++) {
                            writer.write(currentCount);
                        }
                        writer.flush();
                        writer.close();
                        TestProvidedImpl.spaceUsed += TestProvidedImpl.BLK_LEN;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                region = new FileRegion(currentCount, new Path(newFile.toString()), 0, TestProvidedImpl.BLK_LEN);
                (currentCount)++;
            }
            return region;
        }

        @Override
        public void remove() {
            // do nothing.
        }

        public void resetMinBlockId(int minId) {
            currentCount = minId;
        }

        public void resetBlockCount(int numBlocks) {
            this.numBlocks = numBlocks;
        }
    }

    /**
     * A simple FileRegion BlockAliasMap for tests.
     */
    public static class TestFileRegionBlockAliasMap extends BlockAliasMap<FileRegion> {
        private int minId;

        private int numBlocks;

        private Iterator<FileRegion> suppliedIterator;

        TestFileRegionBlockAliasMap() {
            this(null, TestProvidedImpl.MIN_BLK_ID, TestProvidedImpl.NUM_PROVIDED_BLKS);
        }

        TestFileRegionBlockAliasMap(Iterator<FileRegion> iterator, int minId, int numBlocks) {
            this.suppliedIterator = iterator;
            this.minId = minId;
            this.numBlocks = numBlocks;
        }

        @Override
        public Reader<FileRegion> getReader(Reader.Options opts, String blockPoolId) throws IOException {
            if (!(blockPoolId.equals(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID]))) {
                return null;
            }
            BlockAliasMap.Reader<FileRegion> reader = new BlockAliasMap.Reader<FileRegion>() {
                @Override
                public Iterator<FileRegion> iterator() {
                    if ((suppliedIterator) == null) {
                        return new <org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.providedBasePath>org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.TestFileRegionIterator(minId, numBlocks);
                    } else {
                        return suppliedIterator;
                    }
                }

                @Override
                public void close() throws IOException {
                }

                @Override
                public Optional<FileRegion> resolve(Block ident) throws IOException {
                    return null;
                }
            };
            return reader;
        }

        @Override
        public Writer<FileRegion> getWriter(Writer.Options opts, String blockPoolId) throws IOException {
            // not implemented
            return null;
        }

        @Override
        public void refresh() throws IOException {
            // do nothing!
        }

        @Override
        public void close() throws IOException {
            // do nothing
        }
    }

    @Test
    public void testProvidedVolumeImpl() throws IOException {
        Assert.assertEquals(((TestProvidedImpl.NUM_LOCAL_INIT_VOLUMES) + (TestProvidedImpl.NUM_PROVIDED_INIT_VOLUMES)), getNumVolumes());
        Assert.assertEquals(TestProvidedImpl.NUM_PROVIDED_INIT_VOLUMES, TestProvidedImpl.providedVolumes.size());
        Assert.assertEquals(0, dataset.getNumFailedVolumes());
        for (int i = 0; i < (TestProvidedImpl.providedVolumes.size()); i++) {
            // check basic information about provided volume
            Assert.assertEquals(DFS_PROVIDER_STORAGEUUID_DEFAULT, TestProvidedImpl.providedVolumes.get(i).getStorageID());
            Assert.assertEquals(PROVIDED, TestProvidedImpl.providedVolumes.get(i).getStorageType());
            long space = TestProvidedImpl.providedVolumes.get(i).getBlockPoolUsed(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID]);
            // check the df stats of the volume
            Assert.assertEquals(TestProvidedImpl.spaceUsed, space);
            Assert.assertEquals(TestProvidedImpl.NUM_PROVIDED_BLKS, TestProvidedImpl.providedVolumes.get(i).getNumBlocks());
            TestProvidedImpl.providedVolumes.get(i).shutdownBlockPool(TestProvidedImpl.BLOCK_POOL_IDS[(1 - (TestProvidedImpl.CHOSEN_BP_ID))], null);
            try {
                Assert.assertEquals(0, TestProvidedImpl.providedVolumes.get(i).getBlockPoolUsed(TestProvidedImpl.BLOCK_POOL_IDS[(1 - (TestProvidedImpl.CHOSEN_BP_ID))]));
                // should not be triggered
                Assert.assertTrue(false);
            } catch (IOException e) {
                TestProvidedImpl.LOG.info(("Expected exception: " + e));
            }
        }
    }

    @Test
    public void testBlockLoad() throws IOException {
        for (int i = 0; i < (TestProvidedImpl.providedVolumes.size()); i++) {
            FsVolumeImpl vol = TestProvidedImpl.providedVolumes.get(i);
            ReplicaMap volumeMap = new ReplicaMap(new AutoCloseableLock());
            vol.getVolumeMap(volumeMap, null);
            Assert.assertEquals(vol.getBlockPoolList().length, TestProvidedImpl.BLOCK_POOL_IDS.length);
            for (int j = 0; j < (TestProvidedImpl.BLOCK_POOL_IDS.length); j++) {
                if (j != (TestProvidedImpl.CHOSEN_BP_ID)) {
                    // this block pool should not have any blocks
                    Assert.assertEquals(null, volumeMap.replicas(TestProvidedImpl.BLOCK_POOL_IDS[j]));
                }
            }
            Assert.assertEquals(TestProvidedImpl.NUM_PROVIDED_BLKS, volumeMap.replicas(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID]).size());
        }
    }

    @Test
    public void testProvidedBlockRead() throws IOException {
        for (int id = 0; id < (TestProvidedImpl.NUM_PROVIDED_BLKS); id++) {
            ExtendedBlock eb = new ExtendedBlock(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID], id, TestProvidedImpl.BLK_LEN, HdfsConstants.GRANDFATHER_GENERATION_STAMP);
            InputStream ins = dataset.getBlockInputStream(eb, 0);
            String filepath = TestProvidedImpl.blkToPathMap.get(((long) (id)));
            TestProvidedReplicaImpl.verifyReplicaContents(new File(filepath), ins, 0, TestProvidedImpl.BLK_LEN);
        }
    }

    @Test
    public void testProvidedBlockIterator() throws IOException {
        for (int i = 0; i < (TestProvidedImpl.providedVolumes.size()); i++) {
            FsVolumeImpl vol = TestProvidedImpl.providedVolumes.get(i);
            BlockIterator iter = vol.newBlockIterator(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID], "temp");
            Set<Long> blockIdsUsed = new HashSet<Long>();
            Assert.assertEquals(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID], iter.getBlockPoolId());
            while (!(iter.atEnd())) {
                ExtendedBlock eb = iter.nextBlock();
                long blkId = eb.getBlockId();
                Assert.assertTrue(((blkId >= (TestProvidedImpl.MIN_BLK_ID)) && (blkId < (TestProvidedImpl.NUM_PROVIDED_BLKS))));
                // all block ids must be unique!
                Assert.assertTrue((!(blockIdsUsed.contains(blkId))));
                blockIdsUsed.add(blkId);
            } 
            Assert.assertEquals(TestProvidedImpl.NUM_PROVIDED_BLKS, blockIdsUsed.size());
            // rewind the block iterator
            iter.rewind();
            while (!(iter.atEnd())) {
                ExtendedBlock eb = iter.nextBlock();
                long blkId = eb.getBlockId();
                // the block should have already appeared in the first scan.
                Assert.assertTrue(blockIdsUsed.contains(blkId));
                blockIdsUsed.remove(blkId);
            } 
            // none of the blocks should remain in blockIdsUsed
            Assert.assertEquals(0, blockIdsUsed.size());
            // the other block pool should not contain any blocks!
            BlockIterator nonProvidedBpIter = vol.newBlockIterator(TestProvidedImpl.BLOCK_POOL_IDS[(1 - (TestProvidedImpl.CHOSEN_BP_ID))], "temp");
            Assert.assertEquals(null, nonProvidedBpIter.nextBlock());
        }
    }

    /**
     * Tests if the FileRegions provided by the FileRegionProvider
     * can belong to the Providevolume.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testProvidedVolumeContents() throws IOException {
        int expectedBlocks = 5;
        int minId = 0;
        // use a path which has the same prefix as providedBasePath
        // all these blocks can belong to the provided volume
        int blocksFound = getBlocksInProvidedVolumes(((TestProvidedImpl.providedBasePath) + "/test1/"), expectedBlocks, minId);
        Assert.assertEquals(("Number of blocks in provided volumes should be " + expectedBlocks), expectedBlocks, blocksFound);
        blocksFound = getBlocksInProvidedVolumes((("file:/" + (TestProvidedImpl.providedBasePath)) + "/test1/"), expectedBlocks, minId);
        Assert.assertEquals(("Number of blocks in provided volumes should be " + expectedBlocks), expectedBlocks, blocksFound);
        // use a path that is entirely different from the providedBasePath
        // none of these blocks can belong to the volume
        blocksFound = getBlocksInProvidedVolumes("randomtest1/", expectedBlocks, minId);
        Assert.assertEquals("Number of blocks in provided volumes should be 0", 0, blocksFound);
    }

    @Test
    public void testProvidedVolumeContainsBlock() throws URISyntaxException {
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(null, null));
        Assert.assertEquals(false, ProvidedVolumeImpl.containsBlock(new URI("file:/a"), null));
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(new URI("file:/a/b/c/"), new URI("file:/a/b/c/d/e.file")));
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(new URI("/a/b/c/"), new URI("file:/a/b/c/d/e.file")));
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(new URI("/a/b/c"), new URI("file:/a/b/c/d/e.file")));
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(new URI("/a/b/c/"), new URI("/a/b/c/d/e.file")));
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(new URI("file:/a/b/c/"), new URI("/a/b/c/d/e.file")));
        Assert.assertEquals(false, ProvidedVolumeImpl.containsBlock(new URI("/a/b/e"), new URI("file:/a/b/c/d/e.file")));
        Assert.assertEquals(false, ProvidedVolumeImpl.containsBlock(new URI("file:/a/b/e"), new URI("file:/a/b/c/d/e.file")));
        Assert.assertEquals(true, ProvidedVolumeImpl.containsBlock(new URI("s3a:/bucket1/dir1/"), new URI("s3a:/bucket1/dir1/temp.txt")));
        Assert.assertEquals(false, ProvidedVolumeImpl.containsBlock(new URI("s3a:/bucket2/dir1/"), new URI("s3a:/bucket1/dir1/temp.txt")));
        Assert.assertEquals(false, ProvidedVolumeImpl.containsBlock(new URI("s3a:/bucket1/dir1/"), new URI("s3a:/bucket1/temp.txt")));
        Assert.assertEquals(false, ProvidedVolumeImpl.containsBlock(new URI("/bucket1/dir1/"), new URI("s3a:/bucket1/dir1/temp.txt")));
    }

    @Test
    public void testProvidedReplicaSuffixExtraction() {
        Assert.assertEquals("B.txt", ProvidedVolumeImpl.getSuffix(new Path("file:///A/"), new Path("file:///A/B.txt")));
        Assert.assertEquals("B/C.txt", ProvidedVolumeImpl.getSuffix(new Path("file:///A/"), new Path("file:///A/B/C.txt")));
        Assert.assertEquals("B/C/D.txt", ProvidedVolumeImpl.getSuffix(new Path("file:///A/"), new Path("file:///A/B/C/D.txt")));
        Assert.assertEquals("D.txt", ProvidedVolumeImpl.getSuffix(new Path("file:///A/B/C/"), new Path("file:///A/B/C/D.txt")));
        Assert.assertEquals("file:/A/B/C/D.txt", ProvidedVolumeImpl.getSuffix(new Path("file:///X/B/C/"), new Path("file:///A/B/C/D.txt")));
        Assert.assertEquals("D.txt", ProvidedVolumeImpl.getSuffix(new Path("/A/B/C"), new Path("/A/B/C/D.txt")));
        Assert.assertEquals("D.txt", ProvidedVolumeImpl.getSuffix(new Path("/A/B/C/"), new Path("/A/B/C/D.txt")));
        Assert.assertEquals("data/current.csv", ProvidedVolumeImpl.getSuffix(new Path("wasb:///users/alice/"), new Path("wasb:///users/alice/data/current.csv")));
        Assert.assertEquals("current.csv", ProvidedVolumeImpl.getSuffix(new Path("wasb:///users/alice/data"), new Path("wasb:///users/alice/data/current.csv")));
        Assert.assertEquals("wasb:/users/alice/data/current.csv", ProvidedVolumeImpl.getSuffix(new Path("wasb:///users/bob/"), new Path("wasb:///users/alice/data/current.csv")));
    }

    @Test
    public void testProvidedReplicaPrefix() throws Exception {
        for (int i = 0; i < (TestProvidedImpl.providedVolumes.size()); i++) {
            FsVolumeImpl vol = TestProvidedImpl.providedVolumes.get(i);
            ReplicaMap volumeMap = new ReplicaMap(new AutoCloseableLock());
            vol.getVolumeMap(volumeMap, null);
            Path expectedPrefix = new Path(StorageLocation.normalizeFileURI(new File(TestProvidedImpl.providedBasePath).toURI()));
            for (ReplicaInfo info : volumeMap.replicas(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID])) {
                ProvidedReplica pInfo = ((ProvidedReplica) (info));
                Assert.assertEquals(expectedPrefix, pInfo.getPathPrefix());
            }
        }
    }

    @Test
    public void testScannerWithProvidedVolumes() throws Exception {
        DirectoryScanner scanner = new DirectoryScanner(dataset, conf);
        Collection<ScanInfoVolumeReport> reports = scanner.getVolumeReports();
        // no blocks should be reported for the Provided volume as long as
        // the directoryScanner is disabled.
        for (ScanInfoVolumeReport report : reports) {
            Assert.assertEquals(0, report.getScanInfo(TestProvidedImpl.BLOCK_POOL_IDS[TestProvidedImpl.CHOSEN_BP_ID]).size());
        }
    }

    /**
     * Tests that a ProvidedReplica supports path handles.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProvidedReplicaWithPathHandle() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        cluster.waitActive();
        DistributedFileSystem fs = cluster.getFileSystem();
        // generate random data
        int chunkSize = 512;
        Random r = new Random(12345L);
        byte[] data = new byte[chunkSize];
        r.nextBytes(data);
        Path file = new Path("/testfile");
        try (FSDataOutputStream fout = fs.create(file)) {
            fout.write(data);
        }
        PathHandle pathHandle = fs.getPathHandle(fs.getFileStatus(file), HandleOpt.changed(true), HandleOpt.moved(true));
        FinalizedProvidedReplica replica = new FinalizedProvidedReplica(0, file.toUri(), 0, chunkSize, 0, pathHandle, null, conf, fs);
        byte[] content = new byte[chunkSize];
        IOUtils.readFully(replica.getDataInputStream(0), content, 0, chunkSize);
        Assert.assertArrayEquals(data, content);
        fs.rename(file, new Path("/testfile.1"));
        // read should continue succeeding after the rename operation
        IOUtils.readFully(replica.getDataInputStream(0), content, 0, chunkSize);
        Assert.assertArrayEquals(data, content);
        replica.setPathHandle(null);
        try {
            // expected to fail as URI of the provided replica is no longer valid.
            replica.getDataInputStream(0);
            Assert.fail("Expected an exception");
        } catch (IOException e) {
            TestProvidedImpl.LOG.info(("Expected exception " + e));
        }
    }
}

