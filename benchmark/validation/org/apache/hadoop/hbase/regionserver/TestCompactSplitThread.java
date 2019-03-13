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


import CompactSplit.LARGE_COMPACTION_THREADS;
import CompactSplit.SMALL_COMPACTION_THREADS;
import CompactSplit.SPLIT_THREADS;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestCompactSplitThread {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactSplitThread.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactSplitThread.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private final TableName tableName = TableName.valueOf(getClass().getSimpleName());

    private final byte[] family = Bytes.toBytes("f");

    private static final int NUM_RS = 1;

    private static final int blockingStoreFiles = 3;

    private static Path rootDir;

    private static FileSystem fs;

    @Test
    public void testThreadPoolSizeTuning() throws Exception {
        Configuration conf = TestCompactSplitThread.TEST_UTIL.getConfiguration();
        Connection conn = ConnectionFactory.createConnection(conf);
        try {
            HTableDescriptor htd = new HTableDescriptor(tableName);
            htd.addFamily(new HColumnDescriptor(family));
            htd.setCompactionEnabled(false);
            TestCompactSplitThread.TEST_UTIL.getAdmin().createTable(htd);
            TestCompactSplitThread.TEST_UTIL.waitTableAvailable(tableName);
            HRegionServer regionServer = TestCompactSplitThread.TEST_UTIL.getRSForFirstRegionInTable(tableName);
            // check initial configuration of thread pool sizes
            Assert.assertEquals(3, regionServer.compactSplitThread.getLargeCompactionThreadNum());
            Assert.assertEquals(4, regionServer.compactSplitThread.getSmallCompactionThreadNum());
            Assert.assertEquals(5, regionServer.compactSplitThread.getSplitThreadNum());
            // change bigger configurations and do online update
            conf.setInt(LARGE_COMPACTION_THREADS, 4);
            conf.setInt(SMALL_COMPACTION_THREADS, 5);
            conf.setInt(SPLIT_THREADS, 6);
            try {
                regionServer.compactSplitThread.onConfigurationChange(conf);
            } catch (IllegalArgumentException iae) {
                Assert.fail("Update bigger configuration failed!");
            }
            // check again after online update
            Assert.assertEquals(4, regionServer.compactSplitThread.getLargeCompactionThreadNum());
            Assert.assertEquals(5, regionServer.compactSplitThread.getSmallCompactionThreadNum());
            Assert.assertEquals(6, regionServer.compactSplitThread.getSplitThreadNum());
            // change smaller configurations and do online update
            conf.setInt(LARGE_COMPACTION_THREADS, 2);
            conf.setInt(SMALL_COMPACTION_THREADS, 3);
            conf.setInt(SPLIT_THREADS, 4);
            try {
                regionServer.compactSplitThread.onConfigurationChange(conf);
            } catch (IllegalArgumentException iae) {
                Assert.fail("Update smaller configuration failed!");
            }
            // check again after online update
            Assert.assertEquals(2, regionServer.compactSplitThread.getLargeCompactionThreadNum());
            Assert.assertEquals(3, regionServer.compactSplitThread.getSmallCompactionThreadNum());
            Assert.assertEquals(4, regionServer.compactSplitThread.getSplitThreadNum());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testFlushWithTableCompactionDisabled() throws Exception {
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.setCompactionEnabled(false);
        TestCompactSplitThread.TEST_UTIL.createTable(htd, new byte[][]{ family }, null);
        // load the table
        for (int i = 0; i < ((TestCompactSplitThread.blockingStoreFiles) + 1); i++) {
            TestCompactSplitThread.TEST_UTIL.loadTable(TestCompactSplitThread.TEST_UTIL.getConnection().getTable(tableName), family);
            TestCompactSplitThread.TEST_UTIL.flush(tableName);
        }
        // Make sure that store file number is greater than blockingStoreFiles + 1
        Path tableDir = FSUtils.getTableDir(TestCompactSplitThread.rootDir, tableName);
        Collection<String> hfiles = SnapshotTestingUtils.listHFileNames(TestCompactSplitThread.fs, tableDir);
        assert (hfiles.size()) > ((TestCompactSplitThread.blockingStoreFiles) + 1);
    }
}

