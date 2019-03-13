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


import CompactionConfiguration.HBASE_HSTORE_COMPACTION_MAX_SIZE_OFFPEAK_KEY;
import CompactionConfiguration.HBASE_HSTORE_OFFPEAK_END_HOUR;
import CompactionConfiguration.HBASE_HSTORE_OFFPEAK_START_HOUR;
import HConstants.MAJOR_COMPACTION_PERIOD;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify that the Online config Changes on the HRegionServer side are actually
 * happening. We should add tests for important configurations which will be
 * changed online.
 */
@Category({ MediumTests.class })
public class TestRegionServerOnlineConfigChange {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerOnlineConfigChange.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerOnlineConfigChange.class.getName());

    private static HBaseTestingUtility hbaseTestingUtility = new HBaseTestingUtility();

    private static Configuration conf = null;

    private static Table t1 = null;

    private static HRegionServer rs1 = null;

    private static byte[] r1name = null;

    private static Region r1 = null;

    private static final String table1Str = "table1";

    private static final String columnFamily1Str = "columnFamily1";

    private static final TableName TABLE1 = TableName.valueOf(TestRegionServerOnlineConfigChange.table1Str);

    private static final byte[] COLUMN_FAMILY1 = Bytes.toBytes(TestRegionServerOnlineConfigChange.columnFamily1Str);

    /**
     * Check if the number of compaction threads changes online
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testNumCompactionThreadsOnlineChange() throws IOException {
        Assert.assertTrue(((TestRegionServerOnlineConfigChange.rs1.compactSplitThread) != null));
        int newNumSmallThreads = (TestRegionServerOnlineConfigChange.rs1.compactSplitThread.getSmallCompactionThreadNum()) + 1;
        int newNumLargeThreads = (TestRegionServerOnlineConfigChange.rs1.compactSplitThread.getLargeCompactionThreadNum()) + 1;
        TestRegionServerOnlineConfigChange.conf.setInt("hbase.regionserver.thread.compaction.small", newNumSmallThreads);
        TestRegionServerOnlineConfigChange.conf.setInt("hbase.regionserver.thread.compaction.large", newNumLargeThreads);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newNumSmallThreads, TestRegionServerOnlineConfigChange.rs1.compactSplitThread.getSmallCompactionThreadNum());
        Assert.assertEquals(newNumLargeThreads, TestRegionServerOnlineConfigChange.rs1.compactSplitThread.getLargeCompactionThreadNum());
    }

    /**
     * Test that the configurations in the CompactionConfiguration class change
     * properly.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCompactionConfigurationOnlineChange() throws IOException {
        String strPrefix = "hbase.hstore.compaction.";
        Store s = TestRegionServerOnlineConfigChange.r1.getStore(TestRegionServerOnlineConfigChange.COLUMN_FAMILY1);
        if (!(s instanceof HStore)) {
            TestRegionServerOnlineConfigChange.LOG.error(("Can't test the compaction configuration of HStore class. " + "Got a different implementation other than HStore"));
            return;
        }
        HStore hstore = ((HStore) (s));
        // Set the new compaction ratio to a different value.
        double newCompactionRatio = (hstore.getStoreEngine().getCompactionPolicy().getConf().getCompactionRatio()) + 0.1;
        TestRegionServerOnlineConfigChange.conf.setFloat((strPrefix + "ratio"), ((float) (newCompactionRatio)));
        // Notify all the observers, which includes the Store object.
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        // Check if the compaction ratio got updated in the Compaction Configuration
        Assert.assertEquals(newCompactionRatio, hstore.getStoreEngine().getCompactionPolicy().getConf().getCompactionRatio(), 1.0E-5);
        // Check if the off peak compaction ratio gets updated.
        double newOffPeakCompactionRatio = (hstore.getStoreEngine().getCompactionPolicy().getConf().getCompactionRatioOffPeak()) + 0.1;
        TestRegionServerOnlineConfigChange.conf.setFloat((strPrefix + "ratio.offpeak"), ((float) (newOffPeakCompactionRatio)));
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newOffPeakCompactionRatio, hstore.getStoreEngine().getCompactionPolicy().getConf().getCompactionRatioOffPeak(), 1.0E-5);
        // Check if the throttle point gets updated.
        long newThrottlePoint = (hstore.getStoreEngine().getCompactionPolicy().getConf().getThrottlePoint()) + 10;
        TestRegionServerOnlineConfigChange.conf.setLong("hbase.regionserver.thread.compaction.throttle", newThrottlePoint);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newThrottlePoint, hstore.getStoreEngine().getCompactionPolicy().getConf().getThrottlePoint());
        // Check if the minFilesToCompact gets updated.
        int newMinFilesToCompact = (hstore.getStoreEngine().getCompactionPolicy().getConf().getMinFilesToCompact()) + 1;
        TestRegionServerOnlineConfigChange.conf.setLong((strPrefix + "min"), newMinFilesToCompact);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newMinFilesToCompact, hstore.getStoreEngine().getCompactionPolicy().getConf().getMinFilesToCompact());
        // Check if the maxFilesToCompact gets updated.
        int newMaxFilesToCompact = (hstore.getStoreEngine().getCompactionPolicy().getConf().getMaxFilesToCompact()) + 1;
        TestRegionServerOnlineConfigChange.conf.setLong((strPrefix + "max"), newMaxFilesToCompact);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newMaxFilesToCompact, hstore.getStoreEngine().getCompactionPolicy().getConf().getMaxFilesToCompact());
        // Check OffPeak hours is updated in an online fashion.
        TestRegionServerOnlineConfigChange.conf.setLong(HBASE_HSTORE_OFFPEAK_START_HOUR, 6);
        TestRegionServerOnlineConfigChange.conf.setLong(HBASE_HSTORE_OFFPEAK_END_HOUR, 7);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertFalse(hstore.getOffPeakHours().isOffPeakHour(4));
        // Check if the minCompactSize gets updated.
        long newMinCompactSize = (hstore.getStoreEngine().getCompactionPolicy().getConf().getMinCompactSize()) + 1;
        TestRegionServerOnlineConfigChange.conf.setLong((strPrefix + "min.size"), newMinCompactSize);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newMinCompactSize, hstore.getStoreEngine().getCompactionPolicy().getConf().getMinCompactSize());
        // Check if the maxCompactSize gets updated.
        long newMaxCompactSize = (hstore.getStoreEngine().getCompactionPolicy().getConf().getMaxCompactSize()) - 1;
        TestRegionServerOnlineConfigChange.conf.setLong((strPrefix + "max.size"), newMaxCompactSize);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newMaxCompactSize, hstore.getStoreEngine().getCompactionPolicy().getConf().getMaxCompactSize());
        // Check if the offPeakMaxCompactSize gets updated.
        long newOffpeakMaxCompactSize = (hstore.getStoreEngine().getCompactionPolicy().getConf().getOffPeakMaxCompactSize()) - 1;
        TestRegionServerOnlineConfigChange.conf.setLong(HBASE_HSTORE_COMPACTION_MAX_SIZE_OFFPEAK_KEY, newOffpeakMaxCompactSize);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newOffpeakMaxCompactSize, hstore.getStoreEngine().getCompactionPolicy().getConf().getOffPeakMaxCompactSize());
        // Check if majorCompactionPeriod gets updated.
        long newMajorCompactionPeriod = (hstore.getStoreEngine().getCompactionPolicy().getConf().getMajorCompactionPeriod()) + 10;
        TestRegionServerOnlineConfigChange.conf.setLong(MAJOR_COMPACTION_PERIOD, newMajorCompactionPeriod);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newMajorCompactionPeriod, hstore.getStoreEngine().getCompactionPolicy().getConf().getMajorCompactionPeriod());
        // Check if majorCompactionJitter gets updated.
        float newMajorCompactionJitter = (hstore.getStoreEngine().getCompactionPolicy().getConf().getMajorCompactionJitter()) + 0.02F;
        TestRegionServerOnlineConfigChange.conf.setFloat("hbase.hregion.majorcompaction.jitter", newMajorCompactionJitter);
        TestRegionServerOnlineConfigChange.rs1.getConfigurationManager().notifyAllObservers(TestRegionServerOnlineConfigChange.conf);
        Assert.assertEquals(newMajorCompactionJitter, hstore.getStoreEngine().getCompactionPolicy().getConf().getMajorCompactionJitter(), 1.0E-5);
    }
}

