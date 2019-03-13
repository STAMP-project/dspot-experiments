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
package org.apache.hadoop.hbase.quotas;


import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class which verifies that region sizes are reported to the master.
 */
@Category(MediumTests.class)
public class TestRegionSizeUse {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionSizeUse.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionSizeUse.class);

    private static final int SIZE_PER_VALUE = 256;

    private static final int NUM_SPLITS = 10;

    private static final String F1 = "f1";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private MiniHBaseCluster cluster;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testBasicRegionSizeReports() throws Exception {
        final long bytesWritten = (5L * 1024L) * 1024L;// 5MB

        final TableName tn = writeData(bytesWritten);
        TestRegionSizeUse.LOG.debug("Data was written to HBase");
        final Admin admin = TestRegionSizeUse.TEST_UTIL.getAdmin();
        // Push the data to disk.
        admin.flush(tn);
        TestRegionSizeUse.LOG.debug("Data flushed to disk");
        // Get the final region distribution
        final List<RegionInfo> regions = TestRegionSizeUse.TEST_UTIL.getAdmin().getRegions(tn);
        HMaster master = cluster.getMaster();
        MasterQuotaManager quotaManager = master.getMasterQuotaManager();
        Map<RegionInfo, Long> regionSizes = quotaManager.snapshotRegionSizes();
        // Wait until we get all of the region reports for our table
        // The table may split, so make sure we have at least as many as expected right after we
        // finished writing the data.
        int observedRegions = numRegionsForTable(tn, regionSizes);
        while (observedRegions < (regions.size())) {
            TestRegionSizeUse.LOG.debug(((("Expecting more regions. Saw " + observedRegions) + " region sizes reported, expected at least ") + (regions.size())));
            Thread.sleep(1000);
            regionSizes = quotaManager.snapshotRegionSizes();
            observedRegions = numRegionsForTable(tn, regionSizes);
        } 
        TestRegionSizeUse.LOG.debug(("Observed region sizes by the HMaster: " + regionSizes));
        long totalRegionSize = 0L;
        for (Long regionSize : regionSizes.values()) {
            totalRegionSize += regionSize;
        }
        Assert.assertTrue(((((("Expected region size report to exceed " + bytesWritten) + ", but was ") + totalRegionSize) + ". RegionSizes=") + regionSizes), (bytesWritten < totalRegionSize));
    }
}

