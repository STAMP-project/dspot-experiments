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
package org.apache.hadoop.hbase.io.encoding;


import CacheConfig.CACHE_BLOCKS_ON_WRITE_KEY;
import Compression.Algorithm;
import DataBlockEncoding.PREFIX;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.TestMiniClusterLoadSequential;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Uses the load tester
 */
@Category({ IOTests.class, MediumTests.class })
public class TestLoadAndSwitchEncodeOnDisk extends TestMiniClusterLoadSequential {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLoadAndSwitchEncodeOnDisk.class);

    /**
     * We do not alternate the multi-put flag in this test.
     */
    private static final boolean USE_MULTI_PUT = true;

    public TestLoadAndSwitchEncodeOnDisk() {
        super(TestLoadAndSwitchEncodeOnDisk.USE_MULTI_PUT, PREFIX);
        conf.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, true);
    }

    @Override
    @Test
    public void loadTest() throws Exception {
        Admin admin = TestMiniClusterLoadSequential.TEST_UTIL.getAdmin();
        compression = Algorithm.GZ;// used for table setup

        super.loadTest();
        HColumnDescriptor hcd = getColumnDesc(admin);
        System.err.println((("\nDisabling encode-on-disk. Old column descriptor: " + hcd) + "\n"));
        Table t = TestMiniClusterLoadSequential.TEST_UTIL.getConnection().getTable(TestMiniClusterLoadSequential.TABLE);
        assertAllOnLine(t);
        admin.disableTable(TestMiniClusterLoadSequential.TABLE);
        admin.modifyColumnFamily(TestMiniClusterLoadSequential.TABLE, hcd);
        System.err.println("\nRe-enabling table\n");
        admin.enableTable(TestMiniClusterLoadSequential.TABLE);
        System.err.println((("\nNew column descriptor: " + (getColumnDesc(admin))) + "\n"));
        // The table may not have all regions on line yet.  Assert online before
        // moving to major compact.
        assertAllOnLine(t);
        System.err.println("\nCompacting the table\n");
        admin.majorCompact(TestMiniClusterLoadSequential.TABLE);
        // Wait until compaction completes
        Threads.sleepWithoutInterrupt(5000);
        HRegionServer rs = TestMiniClusterLoadSequential.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        while ((rs.compactSplitThread.getCompactionQueueSize()) > 0) {
            Threads.sleep(50);
        } 
        System.err.println("\nDone with the test, shutting down the cluster\n");
    }
}

