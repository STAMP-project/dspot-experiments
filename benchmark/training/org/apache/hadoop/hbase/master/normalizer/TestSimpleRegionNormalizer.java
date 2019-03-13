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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.master.MasterRpcServices;
import org.apache.hadoop.hbase.master.MasterServices;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests logic of {@link SimpleRegionNormalizer}.
 */
@Category({ MasterTests.class, SmallTests.class })
public class TestSimpleRegionNormalizer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSimpleRegionNormalizer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSimpleRegionNormalizer.class);

    private static RegionNormalizer normalizer;

    // mocks
    private static MasterServices masterServices;

    private static MasterRpcServices masterRpcServices;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testPlanComparator() {
        Comparator<NormalizationPlan> comparator = new SimpleRegionNormalizer.PlanComparator();
        NormalizationPlan splitPlan1 = new SplitNormalizationPlan(null, null);
        NormalizationPlan splitPlan2 = new SplitNormalizationPlan(null, null);
        NormalizationPlan mergePlan1 = new MergeNormalizationPlan(null, null);
        NormalizationPlan mergePlan2 = new MergeNormalizationPlan(null, null);
        Assert.assertTrue(((comparator.compare(splitPlan1, splitPlan2)) == 0));
        Assert.assertTrue(((comparator.compare(splitPlan2, splitPlan1)) == 0));
        Assert.assertTrue(((comparator.compare(mergePlan1, mergePlan2)) == 0));
        Assert.assertTrue(((comparator.compare(mergePlan2, mergePlan1)) == 0));
        Assert.assertTrue(((comparator.compare(splitPlan1, mergePlan1)) < 0));
        Assert.assertTrue(((comparator.compare(mergePlan1, splitPlan1)) > 0));
    }

    @Test
    public void testNoNormalizationForMetaTable() throws HBaseIOException {
        TableName testTable = TableName.META_TABLE_NAME;
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(testTable);
        Assert.assertTrue((plans == null));
    }

    @Test
    public void testNoNormalizationIfTooFewRegions() throws HBaseIOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 10);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 15);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertTrue((plans == null));
    }

    @Test
    public void testNoNormalizationOnNormalizedCluster() throws HBaseIOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 10);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 15);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 8);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        regionSizes.put(hri4.getRegionName(), 10);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertTrue((plans == null));
    }

    @Test
    public void testMergeOfSmallRegions() throws HBaseIOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 15);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 5);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 5);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri4.getRegionName(), 15);
        RegionInfo hri5 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("eee")).setEndKey(Bytes.toBytes("fff")).build();
        RegionInfo.add(hri5);
        regionSizes.put(hri5.getRegionName(), 16);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        NormalizationPlan plan = plans.get(0);
        Assert.assertTrue((plan instanceof MergeNormalizationPlan));
        Assert.assertEquals(hri2, getFirstRegion());
        Assert.assertEquals(hri3, getSecondRegion());
    }

    // Test for situation illustrated in HBASE-14867
    @Test
    public void testMergeOfSecondSmallestRegions() throws HBaseIOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 1);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 10000);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 10000);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri4.getRegionName(), 10000);
        RegionInfo hri5 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("eee")).setEndKey(Bytes.toBytes("fff")).build();
        RegionInfo.add(hri5);
        regionSizes.put(hri5.getRegionName(), 2700);
        RegionInfo hri6 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("fff")).setEndKey(Bytes.toBytes("ggg")).build();
        RegionInfo.add(hri6);
        regionSizes.put(hri6.getRegionName(), 2700);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        NormalizationPlan plan = plans.get(0);
        Assert.assertTrue((plan instanceof MergeNormalizationPlan));
        Assert.assertEquals(hri5, getFirstRegion());
        Assert.assertEquals(hri6, getSecondRegion());
    }

    @Test
    public void testMergeOfSmallNonAdjacentRegions() throws HBaseIOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 15);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 5);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 16);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri4.getRegionName(), 15);
        RegionInfo hri5 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri5.getRegionName(), 5);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertTrue((plans == null));
    }

    @Test
    public void testSplitOfLargeRegion() throws HBaseIOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 8);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 6);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 10);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri4.getRegionName(), 30);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        NormalizationPlan plan = plans.get(0);
        Assert.assertTrue((plan instanceof SplitNormalizationPlan));
        Assert.assertEquals(hri4, getRegionInfo());
    }

    @Test
    public void testSplitWithTargetRegionCount() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 20);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 40);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 60);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri4.getRegionName(), 80);
        RegionInfo hri5 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("eee")).setEndKey(Bytes.toBytes("fff")).build();
        RegionInfo.add(hri5);
        regionSizes.put(hri5.getRegionName(), 100);
        RegionInfo hri6 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("fff")).setEndKey(Bytes.toBytes("ggg")).build();
        RegionInfo.add(hri6);
        regionSizes.put(hri6.getRegionName(), 120);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        // test when target region size is 20
        Mockito.when(TestSimpleRegionNormalizer.masterServices.getTableDescriptors().get(ArgumentMatchers.any()).getNormalizerTargetRegionSize()).thenReturn(20L);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertEquals(4, plans.size());
        for (NormalizationPlan plan : plans) {
            Assert.assertTrue((plan instanceof SplitNormalizationPlan));
        }
        // test when target region size is 200
        Mockito.when(TestSimpleRegionNormalizer.masterServices.getTableDescriptors().get(ArgumentMatchers.any()).getNormalizerTargetRegionSize()).thenReturn(200L);
        plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertEquals(2, plans.size());
        NormalizationPlan plan = plans.get(0);
        Assert.assertTrue((plan instanceof MergeNormalizationPlan));
        Assert.assertEquals(hri1, getFirstRegion());
        Assert.assertEquals(hri2, getSecondRegion());
    }

    @Test
    public void testSplitWithTargetRegionSize() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<RegionInfo> RegionInfo = new ArrayList<>();
        Map<byte[], Integer> regionSizes = new HashMap<>();
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("aaa")).setEndKey(Bytes.toBytes("bbb")).build();
        RegionInfo.add(hri1);
        regionSizes.put(hri1.getRegionName(), 20);
        RegionInfo hri2 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("bbb")).setEndKey(Bytes.toBytes("ccc")).build();
        RegionInfo.add(hri2);
        regionSizes.put(hri2.getRegionName(), 40);
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ccc")).setEndKey(Bytes.toBytes("ddd")).build();
        RegionInfo.add(hri3);
        regionSizes.put(hri3.getRegionName(), 60);
        RegionInfo hri4 = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("ddd")).setEndKey(Bytes.toBytes("eee")).build();
        RegionInfo.add(hri4);
        regionSizes.put(hri4.getRegionName(), 80);
        setupMocksForNormalizer(regionSizes, RegionInfo);
        // test when target region count is 8
        Mockito.when(TestSimpleRegionNormalizer.masterServices.getTableDescriptors().get(ArgumentMatchers.any()).getNormalizerTargetRegionCount()).thenReturn(8);
        List<NormalizationPlan> plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertEquals(2, plans.size());
        for (NormalizationPlan plan : plans) {
            Assert.assertTrue((plan instanceof SplitNormalizationPlan));
        }
        // test when target region count is 3
        Mockito.when(TestSimpleRegionNormalizer.masterServices.getTableDescriptors().get(ArgumentMatchers.any()).getNormalizerTargetRegionCount()).thenReturn(3);
        plans = TestSimpleRegionNormalizer.normalizer.computePlanForTable(tableName);
        Assert.assertEquals(1, plans.size());
        NormalizationPlan plan = plans.get(0);
        Assert.assertTrue((plan instanceof MergeNormalizationPlan));
        Assert.assertEquals(hri1, getFirstRegion());
        Assert.assertEquals(hri2, getSecondRegion());
    }
}

