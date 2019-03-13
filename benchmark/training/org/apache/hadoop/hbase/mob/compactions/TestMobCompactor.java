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


import CompactType.MOB;
import HConstants.CIPHER_AES;
import HConstants.CRYPTO_KEY_ALGORITHM_CONF_KEY;
import HConstants.CRYPTO_MASTERKEY_NAME_CONF_KEY;
import MobCompactPartitionPolicy.DAILY;
import MobCompactPartitionPolicy.MONTHLY;
import MobCompactPartitionPolicy.WEEKLY;
import MobConstants.MOB_COMPACTION_MERGEABLE_THRESHOLD;
import MobConstants.MOB_SCAN_RAW;
import TimeToLiveHFileCleaner.TTL_CONF_KEY;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.io.crypto.aes.AES;
import org.apache.hadoop.hbase.mob.MobTestUtil;
import org.apache.hadoop.hbase.mob.MobUtils;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.regionserver.StoreFileInfo;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.security.EncryptionUtil;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestMobCompactor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMobCompactor.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMobCompactor.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf = null;

    private TableName tableName;

    private static Connection conn;

    private BufferedMutator bufMut;

    private Table table;

    private static Admin admin;

    private HTableDescriptor desc;

    private HColumnDescriptor hcd1;

    private HColumnDescriptor hcd2;

    private static FileSystem fs;

    private static final String family1 = "family1";

    private static final String family2 = "family2";

    private static final String qf1 = "qualifier1";

    private static final String qf2 = "qualifier2";

    private static long tsFor20150907Monday;

    private static long tsFor20151120Sunday;

    private static long tsFor20151128Saturday;

    private static long tsFor20151130Monday;

    private static long tsFor20151201Tuesday;

    private static long tsFor20151205Saturday;

    private static long tsFor20151228Monday;

    private static long tsFor20151231Thursday;

    private static long tsFor20160101Friday;

    private static long tsFor20160103Sunday;

    private static final byte[] mobKey01 = Bytes.toBytes("r01");

    private static final byte[] mobKey02 = Bytes.toBytes("r02");

    private static final byte[] mobKey03 = Bytes.toBytes("r03");

    private static final byte[] mobKey04 = Bytes.toBytes("r04");

    private static final byte[] mobKey05 = Bytes.toBytes("r05");

    private static final byte[] mobKey06 = Bytes.toBytes("r05");

    private static final byte[] mobKey1 = Bytes.toBytes("r1");

    private static final byte[] mobKey2 = Bytes.toBytes("r2");

    private static final byte[] mobKey3 = Bytes.toBytes("r3");

    private static final byte[] mobKey4 = Bytes.toBytes("r4");

    private static final byte[] mobKey5 = Bytes.toBytes("r5");

    private static final byte[] mobKey6 = Bytes.toBytes("r6");

    private static final byte[] mobKey7 = Bytes.toBytes("r7");

    private static final byte[] mobKey8 = Bytes.toBytes("r8");

    private static final String mobValue0 = "mobValue00000000000000000000000000";

    private static final String mobValue1 = "mobValue00000111111111111111111111";

    private static final String mobValue2 = "mobValue00000222222222222222222222";

    private static final String mobValue3 = "mobValue00000333333333333333333333";

    private static final String mobValue4 = "mobValue00000444444444444444444444";

    private static final String mobValue5 = "mobValue00000666666666666666666666";

    private static final String mobValue6 = "mobValue00000777777777777777777777";

    private static final String mobValue7 = "mobValue00000888888888888888888888";

    private static final String mobValue8 = "mobValue00000888888888888888888899";

    private static byte[] KEYS = Bytes.toBytes("012");

    private static int regionNum = TestMobCompactor.KEYS.length;

    private static int delRowNum = 1;

    private static int delCellNum = 6;

    private static int cellNumPerRow = 3;

    private static int rowNumPerFile = 2;

    private static ExecutorService pool;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMinorCompaction() throws Exception {
        resetConf();
        int mergeSize = 5000;
        // change the mob compaction merge size
        TestMobCompactor.conf.setLong(MOB_COMPACTION_MERGEABLE_THRESHOLD, mergeSize);
        // create a table with namespace
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create("ns").build();
        String tableNameAsString = "ns:testMinorCompaction";
        TestMobCompactor.admin.createNamespace(namespaceDescriptor);
        setUp(tableNameAsString);
        int count = 4;
        // generate mob files
        loadData(TestMobCompactor.admin, bufMut, tableName, count, TestMobCompactor.rowNumPerFile);
        int rowNumPerRegion = count * (TestMobCompactor.rowNumPerFile);
        Assert.assertEquals("Before deleting: mob rows count", ((TestMobCompactor.regionNum) * rowNumPerRegion), MobTestUtil.countMobRows(table));
        Assert.assertEquals("Before deleting: mob cells count", (((TestMobCompactor.regionNum) * (TestMobCompactor.cellNumPerRow)) * rowNumPerRegion), countMobCells(table));
        Assert.assertEquals("Before deleting: mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family1));
        int largeFilesCount = countLargeFiles(mergeSize, tableName, TestMobCompactor.family1);
        createDelFile(table, tableName, Bytes.toBytes(TestMobCompactor.family1), Bytes.toBytes(TestMobCompactor.qf1));
        Assert.assertEquals("Before compaction: mob rows count", ((TestMobCompactor.regionNum) * (rowNumPerRegion - (TestMobCompactor.delRowNum))), MobTestUtil.countMobRows(table));
        Assert.assertEquals("Before compaction: mob cells count", ((TestMobCompactor.regionNum) * (((TestMobCompactor.cellNumPerRow) * rowNumPerRegion) - (TestMobCompactor.delCellNum))), countMobCells(table));
        Assert.assertEquals("Before compaction: family1 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("Before compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("Before compaction: family1 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("Before compaction: family2 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family2));
        // do the mob file compaction
        MobCompactor compactor = new PartitionedMobCompactor(TestMobCompactor.conf, TestMobCompactor.fs, tableName, hcd1, TestMobCompactor.pool);
        compactor.compact();
        Assert.assertEquals("After compaction: mob rows count", ((TestMobCompactor.regionNum) * (rowNumPerRegion - (TestMobCompactor.delRowNum))), MobTestUtil.countMobRows(table));
        Assert.assertEquals("After compaction: mob cells count", ((TestMobCompactor.regionNum) * (((TestMobCompactor.cellNumPerRow) * rowNumPerRegion) - (TestMobCompactor.delCellNum))), countMobCells(table));
        // After the compaction, the files smaller than the mob compaction merge size
        // is merge to one file
        Assert.assertEquals("After compaction: family1 mob file count", (largeFilesCount + (TestMobCompactor.regionNum)), countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("After compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("After compaction: family1 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("After compaction: family2 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family2));
    }

    @Test
    public void testMinorCompactionWithWeeklyPolicy() throws Exception {
        resetConf();
        int mergeSize = 5000;
        // change the mob compaction merge size
        TestMobCompactor.conf.setLong(MOB_COMPACTION_MERGEABLE_THRESHOLD, mergeSize);
        commonPolicyTestLogic("testMinorCompactionWithWeeklyPolicy", WEEKLY, false, 6, new String[]{ "20150907", "20151120", "20151128", "20151130", "20151205", "20160103" }, true);
    }

    @Test
    public void testMajorCompactionWithWeeklyPolicy() throws Exception {
        resetConf();
        commonPolicyTestLogic("testMajorCompactionWithWeeklyPolicy", WEEKLY, true, 5, new String[]{ "20150907", "20151120", "20151128", "20151205", "20160103" }, true);
    }

    @Test
    public void testMinorCompactionWithMonthlyPolicy() throws Exception {
        resetConf();
        int mergeSize = 5000;
        // change the mob compaction merge size
        TestMobCompactor.conf.setLong(MOB_COMPACTION_MERGEABLE_THRESHOLD, mergeSize);
        commonPolicyTestLogic("testMinorCompactionWithMonthlyPolicy", MONTHLY, false, 4, new String[]{ "20150907", "20151130", "20151231", "20160103" }, true);
    }

    @Test
    public void testMajorCompactionWithMonthlyPolicy() throws Exception {
        resetConf();
        commonPolicyTestLogic("testMajorCompactionWithMonthlyPolicy", MONTHLY, true, 4, new String[]{ "20150907", "20151130", "20151231", "20160103" }, true);
    }

    @Test
    public void testMajorCompactionWithWeeklyFollowedByMonthly() throws Exception {
        resetConf();
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthly", WEEKLY, true, 5, new String[]{ "20150907", "20151120", "20151128", "20151205", "20160103" }, true);
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthly", MONTHLY, true, 4, new String[]{ "20150907", "20151128", "20151205", "20160103" }, false);
    }

    @Test
    public void testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByWeekly() throws Exception {
        resetConf();
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByWeekly", WEEKLY, true, 5, new String[]{ "20150907", "20151120", "20151128", "20151205", "20160103" }, true);
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByWeekly", MONTHLY, true, 4, new String[]{ "20150907", "20151128", "20151205", "20160103" }, false);
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByWeekly", WEEKLY, true, 4, new String[]{ "20150907", "20151128", "20151205", "20160103" }, false);
    }

    @Test
    public void testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByDaily() throws Exception {
        resetConf();
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByDaily", WEEKLY, true, 5, new String[]{ "20150907", "20151120", "20151128", "20151205", "20160103" }, true);
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByDaily", MONTHLY, true, 4, new String[]{ "20150907", "20151128", "20151205", "20160103" }, false);
        commonPolicyTestLogic("testMajorCompactionWithWeeklyFollowedByMonthlyFollowedByDaily", DAILY, true, 4, new String[]{ "20150907", "20151128", "20151205", "20160103" }, false);
    }

    @Test
    public void testCompactionWithHFileLink() throws IOException, InterruptedException {
        resetConf();
        String tableNameAsString = "testCompactionWithHFileLink";
        setUp(tableNameAsString);
        int count = 4;
        // generate mob files
        loadData(TestMobCompactor.admin, bufMut, tableName, count, TestMobCompactor.rowNumPerFile);
        int rowNumPerRegion = count * (TestMobCompactor.rowNumPerFile);
        long tid = System.currentTimeMillis();
        byte[] snapshotName1 = Bytes.toBytes(("snaptb-" + tid));
        // take a snapshot
        TestMobCompactor.admin.snapshot(snapshotName1, tableName);
        createDelFile(table, tableName, Bytes.toBytes(TestMobCompactor.family1), Bytes.toBytes(TestMobCompactor.qf1));
        Assert.assertEquals("Before compaction: mob rows count", ((TestMobCompactor.regionNum) * (rowNumPerRegion - (TestMobCompactor.delRowNum))), MobTestUtil.countMobRows(table));
        Assert.assertEquals("Before compaction: mob cells count", ((TestMobCompactor.regionNum) * (((TestMobCompactor.cellNumPerRow) * rowNumPerRegion) - (TestMobCompactor.delCellNum))), countMobCells(table));
        Assert.assertEquals("Before compaction: family1 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("Before compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("Before compaction: family1 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("Before compaction: family2 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family2));
        // do the mob compaction
        MobCompactor compactor = new PartitionedMobCompactor(TestMobCompactor.conf, TestMobCompactor.fs, tableName, hcd1, TestMobCompactor.pool);
        compactor.compact();
        Assert.assertEquals("After first compaction: mob rows count", ((TestMobCompactor.regionNum) * (rowNumPerRegion - (TestMobCompactor.delRowNum))), MobTestUtil.countMobRows(table));
        Assert.assertEquals("After first compaction: mob cells count", ((TestMobCompactor.regionNum) * (((TestMobCompactor.cellNumPerRow) * rowNumPerRegion) - (TestMobCompactor.delCellNum))), countMobCells(table));
        Assert.assertEquals("After first compaction: family1 mob file count", TestMobCompactor.regionNum, countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("After first compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("After first compaction: family1 del file count", 0, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("After first compaction: family2 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family2));
        Assert.assertEquals("After first compaction: family1 hfilelink count", 0, countHFileLinks(TestMobCompactor.family1));
        Assert.assertEquals("After first compaction: family2 hfilelink count", 0, countHFileLinks(TestMobCompactor.family2));
        TestMobCompactor.admin.disableTable(tableName);
        // Restore from snapshot, the hfilelink will exist in mob dir
        TestMobCompactor.admin.restoreSnapshot(snapshotName1);
        TestMobCompactor.admin.enableTable(tableName);
        Assert.assertEquals("After restoring snapshot: mob rows count", ((TestMobCompactor.regionNum) * rowNumPerRegion), MobTestUtil.countMobRows(table));
        Assert.assertEquals("After restoring snapshot: mob cells count", (((TestMobCompactor.regionNum) * (TestMobCompactor.cellNumPerRow)) * rowNumPerRegion), countMobCells(table));
        Assert.assertEquals("After restoring snapshot: family1 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("After restoring snapshot: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("After restoring snapshot: family1 del file count", 0, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("After restoring snapshot: family2 del file count", 0, countFiles(tableName, false, TestMobCompactor.family2));
        Assert.assertEquals("After restoring snapshot: family1 hfilelink count", ((TestMobCompactor.regionNum) * count), countHFileLinks(TestMobCompactor.family1));
        Assert.assertEquals("After restoring snapshot: family2 hfilelink count", 0, countHFileLinks(TestMobCompactor.family2));
        compactor.compact();
        Assert.assertEquals("After second compaction: mob rows count", ((TestMobCompactor.regionNum) * rowNumPerRegion), MobTestUtil.countMobRows(table));
        Assert.assertEquals("After second compaction: mob cells count", (((TestMobCompactor.regionNum) * (TestMobCompactor.cellNumPerRow)) * rowNumPerRegion), countMobCells(table));
        Assert.assertEquals("After second compaction: family1 mob file count", TestMobCompactor.regionNum, countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("After second compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("After second compaction: family1 del file count", 0, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("After second compaction: family2 del file count", 0, countFiles(tableName, false, TestMobCompactor.family2));
        Assert.assertEquals("After second compaction: family1 hfilelink count", 0, countHFileLinks(TestMobCompactor.family1));
        Assert.assertEquals("After second compaction: family2 hfilelink count", 0, countHFileLinks(TestMobCompactor.family2));
        assertRefFileNameEqual(TestMobCompactor.family1);
    }

    @Test
    public void testMajorCompactionFromAdmin() throws Exception {
        resetConf();
        int mergeSize = 5000;
        // change the mob compaction merge size
        TestMobCompactor.conf.setLong(MOB_COMPACTION_MERGEABLE_THRESHOLD, mergeSize);
        SecureRandom rng = new SecureRandom();
        byte[] keyBytes = new byte[AES.KEY_LENGTH];
        rng.nextBytes(keyBytes);
        String algorithm = TestMobCompactor.conf.get(CRYPTO_KEY_ALGORITHM_CONF_KEY, CIPHER_AES);
        Key cfKey = new SecretKeySpec(keyBytes, algorithm);
        byte[] encryptionKey = EncryptionUtil.wrapKey(TestMobCompactor.conf, TestMobCompactor.conf.get(CRYPTO_MASTERKEY_NAME_CONF_KEY, User.getCurrent().getShortName()), cfKey);
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor hcd1 = new HColumnDescriptor(TestMobCompactor.family1);
        hcd1.setMobEnabled(true);
        hcd1.setMobThreshold(0);
        hcd1.setEncryptionType(algorithm);
        hcd1.setEncryptionKey(encryptionKey);
        HColumnDescriptor hcd2 = new HColumnDescriptor(TestMobCompactor.family2);
        hcd2.setMobEnabled(true);
        hcd2.setMobThreshold(0);
        desc.addFamily(hcd1);
        desc.addFamily(hcd2);
        TestMobCompactor.admin.createTable(desc, getSplitKeys());
        Table table = TestMobCompactor.conn.getTable(tableName);
        BufferedMutator bufMut = TestMobCompactor.conn.getBufferedMutator(tableName);
        int count = 4;
        // generate mob files
        loadData(TestMobCompactor.admin, bufMut, tableName, count, TestMobCompactor.rowNumPerFile);
        int rowNumPerRegion = count * (TestMobCompactor.rowNumPerFile);
        Assert.assertEquals("Before deleting: mob rows count", ((TestMobCompactor.regionNum) * rowNumPerRegion), MobTestUtil.countMobRows(table));
        Assert.assertEquals("Before deleting: mob cells count", (((TestMobCompactor.regionNum) * (TestMobCompactor.cellNumPerRow)) * rowNumPerRegion), countMobCells(table));
        Assert.assertEquals("Before deleting: mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family1));
        createDelFile(table, tableName, Bytes.toBytes(TestMobCompactor.family1), Bytes.toBytes(TestMobCompactor.qf1));
        Assert.assertEquals("Before compaction: mob rows count", ((TestMobCompactor.regionNum) * (rowNumPerRegion - (TestMobCompactor.delRowNum))), MobTestUtil.countMobRows(table));
        Assert.assertEquals("Before compaction: mob cells count", ((TestMobCompactor.regionNum) * (((TestMobCompactor.cellNumPerRow) * rowNumPerRegion) - (TestMobCompactor.delCellNum))), countMobCells(table));
        Assert.assertEquals("Before compaction: family1 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("Before compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("Before compaction: family1 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("Before compaction: family2 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family2));
        // do the major mob compaction, it will force all files to compaction
        TestMobCompactor.admin.majorCompact(tableName, hcd1.getName(), MOB);
        waitUntilMobCompactionFinished(tableName);
        Assert.assertEquals("After compaction: mob rows count", ((TestMobCompactor.regionNum) * (rowNumPerRegion - (TestMobCompactor.delRowNum))), MobTestUtil.countMobRows(table));
        Assert.assertEquals("After compaction: mob cells count", ((TestMobCompactor.regionNum) * (((TestMobCompactor.cellNumPerRow) * rowNumPerRegion) - (TestMobCompactor.delCellNum))), countMobCells(table));
        Assert.assertEquals("After compaction: family1 mob file count", TestMobCompactor.regionNum, countFiles(tableName, true, TestMobCompactor.family1));
        Assert.assertEquals("After compaction: family2 mob file count", ((TestMobCompactor.regionNum) * count), countFiles(tableName, true, TestMobCompactor.family2));
        Assert.assertEquals("After compaction: family1 del file count", 0, countFiles(tableName, false, TestMobCompactor.family1));
        Assert.assertEquals("After compaction: family2 del file count", TestMobCompactor.regionNum, countFiles(tableName, false, TestMobCompactor.family2));
        Assert.assertTrue(verifyEncryption(tableName, TestMobCompactor.family1));
        table.close();
    }

    @Test
    public void testScannerOnBulkLoadRefHFiles() throws Exception {
        resetConf();
        setUp("testScannerOnBulkLoadRefHFiles");
        long ts = EnvironmentEdgeManager.currentTime();
        byte[] key0 = Bytes.toBytes("k0");
        byte[] key1 = Bytes.toBytes("k1");
        String value0 = "mobValue0";
        String value1 = "mobValue1";
        String newValue0 = "new";
        Put put0 = new Put(key0);
        put0.addColumn(Bytes.toBytes(TestMobCompactor.family1), Bytes.toBytes(TestMobCompactor.qf1), ts, Bytes.toBytes(value0));
        loadData(TestMobCompactor.admin, bufMut, tableName, new Put[]{ put0 });
        put0 = new Put(key0);
        put0.addColumn(Bytes.toBytes(TestMobCompactor.family1), Bytes.toBytes(TestMobCompactor.qf1), ts, Bytes.toBytes(newValue0));
        Put put1 = new Put(key1);
        put1.addColumn(Bytes.toBytes(TestMobCompactor.family1), Bytes.toBytes(TestMobCompactor.qf1), ts, Bytes.toBytes(value1));
        loadData(TestMobCompactor.admin, bufMut, tableName, new Put[]{ put0, put1 });
        // read the latest cell of key0.
        Get get = new Get(key0);
        Result result = table.get(get);
        Cell cell = result.getColumnLatestCell(hcd1.getName(), Bytes.toBytes(TestMobCompactor.qf1));
        Assert.assertEquals("Before compaction: mob value of k0", newValue0, Bytes.toString(CellUtil.cloneValue(cell)));
        TestMobCompactor.admin.majorCompact(tableName, hcd1.getName(), MOB);
        waitUntilMobCompactionFinished(tableName);
        // read the latest cell of key0, the cell seqId in bulk loaded file is not reset in the
        // scanner. The cell that has "new" value is still visible.
        result = table.get(get);
        cell = result.getColumnLatestCell(hcd1.getName(), Bytes.toBytes(TestMobCompactor.qf1));
        Assert.assertEquals("After compaction: mob value of k0", newValue0, Bytes.toString(CellUtil.cloneValue(cell)));
        // read the ref cell, not read further to the mob cell.
        get = new Get(key1);
        get.setAttribute(MOB_SCAN_RAW, Bytes.toBytes(true));
        result = table.get(get);
        cell = result.getColumnLatestCell(hcd1.getName(), Bytes.toBytes(TestMobCompactor.qf1));
        // the ref name is the new file
        Path mobFamilyPath = MobUtils.getMobFamilyPath(TestMobCompactor.TEST_UTIL.getConfiguration(), tableName, hcd1.getNameAsString());
        List<Path> paths = new ArrayList<>();
        if (TestMobCompactor.fs.exists(mobFamilyPath)) {
            FileStatus[] files = TestMobCompactor.fs.listStatus(mobFamilyPath);
            for (FileStatus file : files) {
                if (!(StoreFileInfo.isDelFile(file.getPath()))) {
                    paths.add(file.getPath());
                }
            }
        }
        Assert.assertEquals("After compaction: number of mob files:", 1, paths.size());
        Assert.assertEquals("After compaction: mob file name:", MobUtils.getMobFileName(cell), paths.get(0).getName());
    }

    /**
     * This case tests the following mob compaction and normal compaction scenario,
     * after mob compaction, the mob reference in new bulkloaded hfile will win even after it
     * is compacted with some other normal hfiles. This is to make sure the mvcc is included
     * after compaction for mob enabled store files.
     */
    @Test
    public void testGetAfterCompaction() throws Exception {
        resetConf();
        TestMobCompactor.conf.setLong(TTL_CONF_KEY, 0);
        String famStr = "f1";
        byte[] fam = Bytes.toBytes(famStr);
        byte[] qualifier = Bytes.toBytes("q1");
        byte[] mobVal = Bytes.toBytes("01234567890");
        HTableDescriptor hdt = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        hdt.addCoprocessor(TestMobCompactor.CompactTwoLatestHfilesCopro.class.getName());
        HColumnDescriptor hcd = new HColumnDescriptor(fam);
        hcd.setMobEnabled(true);
        hcd.setMobThreshold(10);
        hcd.setMaxVersions(1);
        hdt.addFamily(hcd);
        try {
            Table table = TestMobCompactor.TEST_UTIL.createTable(hdt, null);
            HRegion r = TestMobCompactor.TEST_UTIL.getMiniHBaseCluster().getRegions(hdt.getTableName()).get(0);
            Put p = new Put(Bytes.toBytes("r1"));
            p.addColumn(fam, qualifier, mobVal);
            table.put(p);
            // Create mob file mob1 and reference file ref1
            TestMobCompactor.TEST_UTIL.flush(table.getName());
            // Make sure that it is flushed.
            FileSystem fs = r.getRegionFileSystem().getFileSystem();
            Path path = r.getRegionFileSystem().getStoreDir(famStr);
            waitUntilFilesShowup(fs, path, 1);
            p = new Put(Bytes.toBytes("r2"));
            p.addColumn(fam, qualifier, mobVal);
            table.put(p);
            // Create mob file mob2 and reference file ref2
            TestMobCompactor.TEST_UTIL.flush(table.getName());
            waitUntilFilesShowup(fs, path, 2);
            // Do mob compaction to create mob3 and ref3
            TestMobCompactor.TEST_UTIL.getAdmin().compact(hdt.getTableName(), fam, MOB);
            waitUntilFilesShowup(fs, path, 3);
            // Compact ref3 and ref2 into ref4
            TestMobCompactor.TEST_UTIL.getAdmin().compact(hdt.getTableName(), fam);
            waitUntilFilesShowup(fs, path, 2);
            // Sleep for some time, since TimeToLiveHFileCleaner is 0, the next run of
            // clean chore is guaranteed to clean up files in archive
            Thread.sleep(100);
            // Run cleaner to make sure that files in archive directory are cleaned up
            TestMobCompactor.TEST_UTIL.getMiniHBaseCluster().getMaster().getHFileCleaner().choreForTesting();
            // Get "r2"
            Get get = new Get(Bytes.toBytes("r2"));
            try {
                Result result = table.get(get);
                Assert.assertTrue(Arrays.equals(result.getValue(fam, qualifier), mobVal));
            } catch (IOException e) {
                Assert.assertTrue("The MOB file doesn't exist", false);
            }
        } finally {
            TestMobCompactor.TEST_UTIL.deleteTable(hdt.getTableName());
        }
    }

    /**
     * This copro overwrites the default compaction policy. It always chooses two latest hfiles and
     * compacts them into a new one.
     */
    public static class CompactTwoLatestHfilesCopro implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends StoreFile> candidates, CompactionLifeCycleTracker tracker) throws IOException {
            int count = candidates.size();
            if (count >= 2) {
                for (int i = 0; i < (count - 2); i++) {
                    candidates.remove(0);
                }
                c.bypass();
            }
        }
    }
}

