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
package org.apache.hadoop.hbase.master.normalizer;


import RegionInfo.COMPARATOR;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing {@link SimpleRegionNormalizer} on minicluster.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestSimpleRegionNormalizerOnCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSimpleRegionNormalizerOnCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSimpleRegionNormalizerOnCluster.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] FAMILYNAME = Bytes.toBytes("fam");

    private static Admin admin;

    @Rule
    public TestName name = new TestName();

    @Test
    @SuppressWarnings("deprecation")
    public void testRegionNormalizationSplitOnCluster() throws Exception {
        testRegionNormalizationSplitOnCluster(false);
        testRegionNormalizationSplitOnCluster(true);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testRegionNormalizationMergeOnCluster() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        MiniHBaseCluster cluster = TestSimpleRegionNormalizerOnCluster.TEST_UTIL.getHBaseCluster();
        HMaster m = cluster.getMaster();
        // create 5 regions with sizes to trigger merge of small regions
        try (Table ht = TestSimpleRegionNormalizerOnCluster.TEST_UTIL.createMultiRegionTable(tableName, TestSimpleRegionNormalizerOnCluster.FAMILYNAME, 5)) {
            // Need to get sorted list of regions here
            List<HRegion> generatedRegions = TestSimpleRegionNormalizerOnCluster.TEST_UTIL.getHBaseCluster().getRegions(tableName);
            Collections.sort(generatedRegions, Comparator.comparing(HRegion::getRegionInfo, COMPARATOR));
            HRegion region = generatedRegions.get(0);
            generateTestData(region, 1);
            region.flush(true);
            region = generatedRegions.get(1);
            generateTestData(region, 1);
            region.flush(true);
            region = generatedRegions.get(2);
            generateTestData(region, 3);
            region.flush(true);
            region = generatedRegions.get(3);
            generateTestData(region, 3);
            region.flush(true);
            region = generatedRegions.get(4);
            generateTestData(region, 5);
            region.flush(true);
        }
        HTableDescriptor htd = new HTableDescriptor(TestSimpleRegionNormalizerOnCluster.admin.getTableDescriptor(tableName));
        htd.setNormalizationEnabled(true);
        TestSimpleRegionNormalizerOnCluster.admin.modifyTable(tableName, htd);
        TestSimpleRegionNormalizerOnCluster.admin.flush(tableName);
        Assert.assertEquals(5, MetaTableAccessor.getRegionCount(TestSimpleRegionNormalizerOnCluster.TEST_UTIL.getConnection(), tableName));
        // Now trigger a merge and stop when the merge is in progress
        Thread.sleep(5000);// to let region load to update

        m.normalizeRegions();
        while ((MetaTableAccessor.getRegionCount(TestSimpleRegionNormalizerOnCluster.TEST_UTIL.getConnection(), tableName)) > 4) {
            TestSimpleRegionNormalizerOnCluster.LOG.info("Waiting for normalization merge to complete");
            Thread.sleep(100);
        } 
        Assert.assertEquals(4, MetaTableAccessor.getRegionCount(TestSimpleRegionNormalizerOnCluster.TEST_UTIL.getConnection(), tableName));
        TestSimpleRegionNormalizerOnCluster.admin.disableTable(tableName);
        TestSimpleRegionNormalizerOnCluster.admin.deleteTable(tableName);
    }
}

