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
package org.apache.hadoop.hbase.mob.compactions;


import CompactionType.ALL_FILES;
import CompactionType.PART_FILES;
import MobCompactPartitionPolicy.MONTHLY;
import MobCompactPartitionPolicy.WEEKLY;
import MobConstants.BULKLOAD_DIR_NAME;
import MobConstants.DEFAULT_MOB_COMPACTION_BATCH_SIZE;
import MobConstants.DEFAULT_MOB_COMPACTION_MERGEABLE_THRESHOLD;
import MobConstants.DEFAULT_MOB_DELFILE_MAX_COUNT;
import Type.Put;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.mob.MobConstants;
import org.apache.hadoop.hbase.mob.MobUtils;
import org.apache.hadoop.hbase.mob.compactions.MobCompactionRequest.CompactionType;
import org.apache.hadoop.hbase.mob.compactions.PartitionedMobCompactionRequest.CompactionDelPartition;
import org.apache.hadoop.hbase.mob.compactions.PartitionedMobCompactionRequest.CompactionPartition;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestPartitionedMobCompactor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPartitionedMobCompactor.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestPartitionedMobCompactor.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final String family = "family";

    private static final String qf = "qf";

    private final long DAY_IN_MS = ((1000 * 60) * 60) * 24;

    private static byte[] KEYS = Bytes.toBytes("012");

    private HColumnDescriptor hcd = new HColumnDescriptor(TestPartitionedMobCompactor.family);

    private Configuration conf = TestPartitionedMobCompactor.TEST_UTIL.getConfiguration();

    private CacheConfig cacheConf = new CacheConfig(conf);

    private FileSystem fs;

    private List<FileStatus> mobFiles = new ArrayList<>();

    private List<Path> delFiles = new ArrayList<>();

    private List<FileStatus> allFiles = new ArrayList<>();

    private Path basePath;

    private String mobSuffix;

    private String delSuffix;

    private static ExecutorService pool;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCompactionSelectAllFilesWeeklyPolicy() throws Exception {
        String tableName = "testCompactionSelectAllFilesWeeklyPolicy";
        testCompactionAtMergeSize(tableName, DEFAULT_MOB_COMPACTION_MERGEABLE_THRESHOLD, ALL_FILES, false, false, new Date(), WEEKLY, 1);
    }

    @Test
    public void testCompactionSelectPartFilesWeeklyPolicy() throws Exception {
        String tableName = "testCompactionSelectPartFilesWeeklyPolicy";
        testCompactionAtMergeSize(tableName, 4000, PART_FILES, false, false, new Date(), WEEKLY, 1);
    }

    @Test
    public void testCompactionSelectPartFilesWeeklyPolicyWithPastWeek() throws Exception {
        String tableName = "testCompactionSelectPartFilesWeeklyPolicyWithPastWeek";
        Date dateLastWeek = new Date(((System.currentTimeMillis()) - (7 * (DAY_IN_MS))));
        testCompactionAtMergeSize(tableName, 700, PART_FILES, false, false, dateLastWeek, WEEKLY, 7);
    }

    @Test
    public void testCompactionSelectAllFilesWeeklyPolicyWithPastWeek() throws Exception {
        String tableName = "testCompactionSelectAllFilesWeeklyPolicyWithPastWeek";
        Date dateLastWeek = new Date(((System.currentTimeMillis()) - (7 * (DAY_IN_MS))));
        testCompactionAtMergeSize(tableName, 3000, ALL_FILES, false, false, dateLastWeek, WEEKLY, 7);
    }

    @Test
    public void testCompactionSelectAllFilesMonthlyPolicy() throws Exception {
        String tableName = "testCompactionSelectAllFilesMonthlyPolicy";
        Date dateLastWeek = new Date(((System.currentTimeMillis()) - (7 * (DAY_IN_MS))));
        testCompactionAtMergeSize(tableName, DEFAULT_MOB_COMPACTION_MERGEABLE_THRESHOLD, ALL_FILES, false, false, dateLastWeek, MONTHLY, 7);
    }

    @Test
    public void testCompactionSelectNoFilesWithinCurrentWeekMonthlyPolicy() throws Exception {
        String tableName = "testCompactionSelectNoFilesWithinCurrentWeekMonthlyPolicy";
        testCompactionAtMergeSize(tableName, DEFAULT_MOB_COMPACTION_MERGEABLE_THRESHOLD, PART_FILES, false, false, new Date(), MONTHLY, 1);
    }

    @Test
    public void testCompactionSelectPartFilesMonthlyPolicy() throws Exception {
        String tableName = "testCompactionSelectPartFilesMonthlyPolicy";
        testCompactionAtMergeSize(tableName, 4000, PART_FILES, false, false, new Date(), MONTHLY, 1);
    }

    @Test
    public void testCompactionSelectPartFilesMonthlyPolicyWithPastWeek() throws Exception {
        String tableName = "testCompactionSelectPartFilesMonthlyPolicyWithPastWeek";
        Date dateLastWeek = new Date(((System.currentTimeMillis()) - (7 * (DAY_IN_MS))));
        Calendar calendar = Calendar.getInstance();
        Date firstDayOfCurrentMonth = MobUtils.getFirstDayOfMonth(calendar, new Date());
        CompactionType type = CompactionType.PART_FILES;
        long mergeSizeMultiFactor = 7;
        // The dateLastWeek may not really be last week, suppose that it runs at 2/1/2017, it is going
        // to be last month and the monthly policy is going to be applied here.
        if (dateLastWeek.before(firstDayOfCurrentMonth)) {
            type = CompactionType.ALL_FILES;
            mergeSizeMultiFactor *= 4;
        }
        testCompactionAtMergeSize(tableName, 700, type, false, false, dateLastWeek, MONTHLY, mergeSizeMultiFactor);
    }

    @Test
    public void testCompactionSelectAllFilesMonthlyPolicyWithPastWeek() throws Exception {
        String tableName = "testCompactionSelectAllFilesMonthlyPolicyWithPastWeek";
        Date dateLastWeek = new Date(((System.currentTimeMillis()) - (7 * (DAY_IN_MS))));
        testCompactionAtMergeSize(tableName, 3000, ALL_FILES, false, false, dateLastWeek, MONTHLY, 7);
    }

    @Test
    public void testCompactionSelectPartFilesMonthlyPolicyWithPastMonth() throws Exception {
        String tableName = "testCompactionSelectPartFilesMonthlyPolicyWithPastMonth";
        // back 5 weeks, it is going to be a past month
        Date dateLastMonth = new Date(((System.currentTimeMillis()) - ((7 * 5) * (DAY_IN_MS))));
        testCompactionAtMergeSize(tableName, 200, PART_FILES, false, false, dateLastMonth, MONTHLY, 28);
    }

    @Test
    public void testCompactionSelectAllFilesMonthlyPolicyWithPastMonth() throws Exception {
        String tableName = "testCompactionSelectAllFilesMonthlyPolicyWithPastMonth";
        // back 5 weeks, it is going to be a past month
        Date dateLastMonth = new Date(((System.currentTimeMillis()) - ((7 * 5) * (DAY_IN_MS))));
        testCompactionAtMergeSize(tableName, 750, ALL_FILES, false, false, dateLastMonth, MONTHLY, 28);
    }

    @Test
    public void testCompactionSelectWithAllFiles() throws Exception {
        String tableName = "testCompactionSelectWithAllFiles";
        // If there is only 1 file, it will not be compacted with _del files, so
        // It wont be CompactionType.ALL_FILES in this case, do not create with _del files.
        testCompactionAtMergeSize(tableName, DEFAULT_MOB_COMPACTION_MERGEABLE_THRESHOLD, ALL_FILES, false, false);
    }

    @Test
    public void testCompactionSelectWithPartFiles() throws Exception {
        String tableName = "testCompactionSelectWithPartFiles";
        testCompactionAtMergeSize(tableName, 4000, PART_FILES, false);
    }

    @Test
    public void testCompactionSelectWithForceAllFiles() throws Exception {
        String tableName = "testCompactionSelectWithForceAllFiles";
        testCompactionAtMergeSize(tableName, Long.MAX_VALUE, ALL_FILES, true);
    }

    @Test
    public void testCompactDelFilesWithDefaultBatchSize() throws Exception {
        testCompactDelFilesAtBatchSize(name.getMethodName(), DEFAULT_MOB_COMPACTION_BATCH_SIZE, DEFAULT_MOB_DELFILE_MAX_COUNT);
    }

    @Test
    public void testCompactDelFilesWithSmallBatchSize() throws Exception {
        testCompactDelFilesAtBatchSize(name.getMethodName(), 4, DEFAULT_MOB_DELFILE_MAX_COUNT);
    }

    @Test
    public void testCompactDelFilesChangeMaxDelFileCount() throws Exception {
        testCompactDelFilesAtBatchSize(name.getMethodName(), 4, 2);
    }

    @Test
    public void testCompactFilesWithDstDirFull() throws Exception {
        String tableName = name.getMethodName();
        fs = FileSystem.get(conf);
        TestPartitionedMobCompactor.FaultyDistributedFileSystem faultyFs = ((TestPartitionedMobCompactor.FaultyDistributedFileSystem) (fs));
        Path testDir = FSUtils.getRootDir(conf);
        Path mobTestDir = new Path(testDir, MobConstants.MOB_DIR_NAME);
        basePath = new Path(new Path(mobTestDir, tableName), TestPartitionedMobCompactor.family);
        try {
            int count = 2;
            // create 2 mob files.
            createStoreFiles(basePath, TestPartitionedMobCompactor.family, TestPartitionedMobCompactor.qf, count, Put, true, new Date());
            listFiles();
            TableName tName = TableName.valueOf(tableName);
            MobCompactor compactor = new PartitionedMobCompactor(conf, faultyFs, tName, hcd, TestPartitionedMobCompactor.pool);
            faultyFs.setThrowException(true);
            try {
                compactor.compact(allFiles, true);
            } catch (IOException e) {
                System.out.println("Expected exception, ignore");
            }
            // Verify that all the files in tmp directory are cleaned up
            Path tempPath = new Path(MobUtils.getMobHome(conf), MobConstants.TEMP_DIR_NAME);
            FileStatus[] ls = faultyFs.listStatus(tempPath);
            // Only .bulkload under this directory
            Assert.assertTrue(((ls.length) == 1));
            Assert.assertTrue(BULKLOAD_DIR_NAME.equalsIgnoreCase(ls[0].getPath().getName()));
            Path bulkloadPath = new Path(tempPath, new Path(MobConstants.BULKLOAD_DIR_NAME, new Path(tName.getNamespaceAsString(), tName.getQualifierAsString())));
            // Nothing in bulkLoad directory
            FileStatus[] lsBulkload = faultyFs.listStatus(bulkloadPath);
            Assert.assertTrue(((lsBulkload.length) == 0));
        } finally {
            faultyFs.setThrowException(false);
        }
    }

    @Test
    public void testCompactFilesWithoutDelFile() throws Exception {
        String tableName = "testCompactFilesWithoutDelFile";
        resetConf();
        init(tableName);
        createMobFile(basePath);
        listFiles();
        PartitionedMobCompactor compactor = new PartitionedMobCompactor(conf, fs, TableName.valueOf(tableName), hcd, TestPartitionedMobCompactor.pool) {
            @Override
            public List<Path> compact(List<FileStatus> files, boolean isForceAllFiles) throws IOException {
                if ((files == null) || (files.isEmpty())) {
                    return null;
                }
                PartitionedMobCompactionRequest request = select(files, isForceAllFiles);
                // Make sure that there is no del Partitions
                Assert.assertTrue(((request.getDelPartitions().size()) == 0));
                // Make sure that when there is no startKey/endKey for partition.
                for (CompactionPartition p : request.getCompactionPartitions()) {
                    Assert.assertTrue(((p.getStartKey()) == null));
                    Assert.assertTrue(((p.getEndKey()) == null));
                }
                return null;
            }
        };
        compactor.compact(allFiles, true);
    }

    static class MyPartitionedMobCompactor extends PartitionedMobCompactor {
        int delPartitionSize = 0;

        int PartitionsIncludeDelFiles = 0;

        CacheConfig cacheConfig = null;

        MyPartitionedMobCompactor(Configuration conf, FileSystem fs, TableName tableName, ColumnFamilyDescriptor column, ExecutorService pool, final int delPartitionSize, final CacheConfig cacheConf, final int PartitionsIncludeDelFiles) throws IOException {
            super(conf, fs, tableName, column, pool);
            this.delPartitionSize = delPartitionSize;
            this.cacheConfig = cacheConf;
            this.PartitionsIncludeDelFiles = PartitionsIncludeDelFiles;
        }

        @Override
        public List<Path> compact(List<FileStatus> files, boolean isForceAllFiles) throws IOException {
            if ((files == null) || (files.isEmpty())) {
                return null;
            }
            PartitionedMobCompactionRequest request = select(files, isForceAllFiles);
            Assert.assertTrue(((request.getDelPartitions().size()) == (delPartitionSize)));
            if ((request.getDelPartitions().size()) > 0) {
                for (CompactionPartition p : request.getCompactionPartitions()) {
                    Assert.assertTrue(((p.getStartKey()) != null));
                    Assert.assertTrue(((p.getEndKey()) != null));
                }
            }
            try {
                for (CompactionDelPartition delPartition : request.getDelPartitions()) {
                    for (Path newDelPath : delPartition.listDelFiles()) {
                        HStoreFile sf = new HStoreFile(TestPartitionedMobCompactor.this.fs, newDelPath, TestPartitionedMobCompactor.this.conf, this.cacheConfig, BloomType.NONE, true);
                        // pre-create reader of a del file to avoid race condition when opening the reader in
                        // each partition.
                        sf.initReader();
                        delPartition.addStoreFile(sf);
                    }
                }
                // Make sure that CompactionDelPartitions does not overlap
                CompactionDelPartition prevDelP = null;
                for (CompactionDelPartition delP : request.getDelPartitions()) {
                    Assert.assertTrue(((Bytes.compareTo(delP.getId().getStartKey(), delP.getId().getEndKey())) <= 0));
                    if (prevDelP != null) {
                        Assert.assertTrue(((Bytes.compareTo(prevDelP.getId().getEndKey(), delP.getId().getStartKey())) < 0));
                    }
                }
                int affectedPartitions = 0;
                // Make sure that only del files within key range for a partition is included in compaction.
                // compact the mob files by partitions in parallel.
                for (CompactionPartition partition : request.getCompactionPartitions()) {
                    List<HStoreFile> delFiles = getListOfDelFilesForPartition(partition, request.getDelPartitions());
                    if (!(request.getDelPartitions().isEmpty())) {
                        if (!(((Bytes.compareTo(request.getDelPartitions().get(0).getId().getStartKey(), partition.getEndKey())) > 0) || ((Bytes.compareTo(request.getDelPartitions().get(((request.getDelPartitions().size()) - 1)).getId().getEndKey(), partition.getStartKey())) < 0))) {
                            if ((delFiles.size()) > 0) {
                                Assert.assertTrue(((delFiles.size()) == 1));
                                affectedPartitions += delFiles.size();
                                Assert.assertTrue(((Bytes.compareTo(partition.getStartKey(), CellUtil.cloneRow(delFiles.get(0).getLastKey().get()))) <= 0));
                                Assert.assertTrue(((Bytes.compareTo(partition.getEndKey(), CellUtil.cloneRow(delFiles.get(((delFiles.size()) - 1)).getFirstKey().get()))) >= 0));
                            }
                        }
                    }
                }
                // The del file is only included in one partition
                Assert.assertTrue((affectedPartitions == (PartitionsIncludeDelFiles)));
            } finally {
                for (CompactionDelPartition delPartition : request.getDelPartitions()) {
                    for (HStoreFile storeFile : delPartition.getStoreFiles()) {
                        try {
                            storeFile.closeStoreFile(true);
                        } catch (IOException e) {
                            TestPartitionedMobCompactor.LOG.warn(("Failed to close the reader on store file " + (storeFile.getPath())), e);
                        }
                    }
                }
            }
            return null;
        }
    }

    @Test
    public void testCompactFilesWithOneDelFile() throws Exception {
        String tableName = "testCompactFilesWithOneDelFile";
        resetConf();
        init(tableName);
        // Create only del file.
        createMobFile(basePath);
        createMobDelFile(basePath, 2);
        listFiles();
        TestPartitionedMobCompactor.MyPartitionedMobCompactor compactor = new TestPartitionedMobCompactor.MyPartitionedMobCompactor(conf, fs, TableName.valueOf(tableName), hcd, TestPartitionedMobCompactor.pool, 1, cacheConf, 1);
        compactor.compact(allFiles, true);
    }

    @Test
    public void testCompactFilesWithMultiDelFiles() throws Exception {
        String tableName = "testCompactFilesWithMultiDelFiles";
        resetConf();
        init(tableName);
        // Create only del file.
        createMobFile(basePath);
        createMobDelFile(basePath, 0);
        createMobDelFile(basePath, 1);
        createMobDelFile(basePath, 2);
        listFiles();
        TestPartitionedMobCompactor.MyPartitionedMobCompactor compactor = new TestPartitionedMobCompactor.MyPartitionedMobCompactor(conf, fs, TableName.valueOf(tableName), hcd, TestPartitionedMobCompactor.pool, 3, cacheConf, 3);
        compactor.compact(allFiles, true);
    }

    /**
     * The customized Distributed File System Implementation
     */
    static class FaultyDistributedFileSystem extends DistributedFileSystem {
        private volatile boolean throwException = false;

        public FaultyDistributedFileSystem() {
            super();
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        public boolean rename(Path src, Path dst) throws IOException {
            if (throwException) {
                throw new IOException("No more files allowed");
            }
            return super.rename(src, dst);
        }
    }
}

