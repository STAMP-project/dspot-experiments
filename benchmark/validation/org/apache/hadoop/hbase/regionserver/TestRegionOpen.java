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
package org.apache.hadoop.hbase.regionserver;


import ExecutorType.RS_OPEN_PRIORITY_REGION;
import HConstants.HIGH_QOS;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class, RegionServerTests.class })
public class TestRegionOpen {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionOpen.class);

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TestRegionOpen.class);

    private static final int NB_SERVERS = 1;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testPriorityRegionIsOpenedWithSeparateThreadPool() throws Exception {
        final TableName tableName = TableName.valueOf(TestRegionOpen.class.getSimpleName());
        ThreadPoolExecutor exec = TestRegionOpen.getRS().getExecutorService().getExecutorThreadPool(RS_OPEN_PRIORITY_REGION);
        long completed = exec.getCompletedTaskCount();
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.setPriority(HIGH_QOS);
        htd.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        try (Connection connection = ConnectionFactory.createConnection(TestRegionOpen.HTU.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(htd);
        }
        Assert.assertEquals((completed + 1), exec.getCompletedTaskCount());
    }

    @Test
    public void testNonExistentRegionReplica() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] FAMILYNAME = Bytes.toBytes("fam");
        FileSystem fs = TestRegionOpen.HTU.getTestFileSystem();
        Admin admin = TestRegionOpen.HTU.getAdmin();
        Configuration conf = TestRegionOpen.HTU.getConfiguration();
        Path rootDir = TestRegionOpen.HTU.getDataTestDirOnTestFS();
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(new HColumnDescriptor(FAMILYNAME));
        admin.createTable(htd);
        TestRegionOpen.HTU.waitUntilNoRegionsInTransition(60000);
        // Create new HRI with non-default region replica id
        HRegionInfo hri = new HRegionInfo(htd.getTableName(), Bytes.toBytes("A"), Bytes.toBytes("B"), false, System.currentTimeMillis(), 2);
        HRegionFileSystem regionFs = HRegionFileSystem.createRegionOnFileSystem(conf, fs, FSUtils.getTableDir(rootDir, hri.getTable()), hri);
        Path regionDir = regionFs.getRegionDir();
        try {
            HRegionFileSystem.loadRegionInfoFileContent(fs, regionDir);
        } catch (IOException e) {
            TestRegionOpen.LOG.info((("Caught expected IOE due missing .regioninfo file, due: " + (e.getMessage())) + " skipping region open."));
            // We should only have 1 region online
            List<HRegionInfo> regions = admin.getTableRegions(tableName);
            TestRegionOpen.LOG.info(("Regions: " + regions));
            if ((regions.size()) != 1) {
                Assert.fail(((("Table " + tableName) + " should have only one region, but got more: ") + regions));
            }
            return;
        }
        Assert.fail("Should have thrown IOE when attempting to open a non-existing region.");
    }
}

