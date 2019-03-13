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
package org.apache.hadoop.hbase.master;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Run tests that use the HBase clients; {@link org.apache.hadoop.hbase.client.HTable}.
 * Sets up the HBase mini cluster once at start and runs through all client tests.
 * Each creates a table named for the method and does its stuff against that.
 */
@Category({ MasterTests.class, LargeTests.class })
@SuppressWarnings("deprecation")
public class TestWarmupRegion {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWarmupRegion.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWarmupRegion.class);

    protected TableName TABLENAME = TableName.valueOf("testPurgeFutureDeletes");

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[] VALUE = Bytes.toBytes("testValue");

    private static byte[] COLUMN = Bytes.toBytes("column");

    private static int numRows = 10000;

    protected static int SLAVES = 3;

    private static MiniHBaseCluster myCluster;

    private static Table table;

    /**
     * Basic client side validation of HBASE-4536
     */
    @Test
    public void testWarmup() throws Exception {
        int serverid = 0;
        HRegion region = TestWarmupRegion.TEST_UTIL.getMiniHBaseCluster().getRegions(TABLENAME).get(0);
        RegionInfo info = region.getRegionInfo();
        runwarmup();
        for (int i = 0; i < 10; i++) {
            HRegionServer rs = TestWarmupRegion.TEST_UTIL.getMiniHBaseCluster().getRegionServer(serverid);
            byte[] destName = Bytes.toBytes(rs.getServerName().toString());
            Assert.assertTrue((destName != null));
            TestWarmupRegion.LOG.info(("i=" + i));
            TestWarmupRegion.TEST_UTIL.getMiniHBaseCluster().getMaster().move(info.getEncodedNameAsBytes(), destName);
            serverid = (serverid + 1) % 2;
        }
    }
}

