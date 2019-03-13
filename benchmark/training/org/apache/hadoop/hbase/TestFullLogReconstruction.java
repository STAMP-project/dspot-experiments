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
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, LargeTests.class })
public class TestFullLogReconstruction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFullLogReconstruction.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("tabletest");

    private static final byte[] FAMILY = Bytes.toBytes("family");

    /**
     * Test the whole reconstruction loop. Build a table with regions aaa to zzz and load every one of
     * them multiple times with the same date and do a flush at some point. Kill one of the region
     * servers and scan the table. We should see all the rows.
     */
    @Test
    public void testReconstruction() throws Exception {
        Table table = TestFullLogReconstruction.TEST_UTIL.createMultiRegionTable(TestFullLogReconstruction.TABLE_NAME, TestFullLogReconstruction.FAMILY);
        // Load up the table with simple rows and count them
        int initialCount = TestFullLogReconstruction.TEST_UTIL.loadTable(table, TestFullLogReconstruction.FAMILY);
        int count = TestFullLogReconstruction.TEST_UTIL.countRows(table);
        Assert.assertEquals(initialCount, count);
        for (int i = 0; i < 4; i++) {
            TestFullLogReconstruction.TEST_UTIL.loadTable(table, TestFullLogReconstruction.FAMILY);
        }
        RegionServerThread rsThread = TestFullLogReconstruction.TEST_UTIL.getHBaseCluster().getRegionServerThreads().get(0);
        TestFullLogReconstruction.TEST_UTIL.expireRegionServerSession(0);
        // make sure that the RS is fully down before reading, so that we will read the data from other
        // RSes.
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return !(rsThread.isAlive());
            }

            @Override
            public String explainFailure() throws Exception {
                return (rsThread.getRegionServer()) + " is still alive";
            }
        });
        int newCount = TestFullLogReconstruction.TEST_UTIL.countRows(table);
        Assert.assertEquals(count, newCount);
        table.close();
    }
}

