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
package org.apache.hadoop.hbase.regionserver.throttle;


import CompactionConfiguration.HBASE_HSTORE_COMPACTION_MIN_KEY;
import CompactionThroughputControllerFactory.HBASE_THROUGHPUT_CONTROLLER_KEY;
import HStore.BLOCKING_STOREFILES_KEY;
import PressureAwareCompactionThroughputController.HBASE_HSTORE_COMPACTION_MAX_THROUGHPUT_HIGHER_BOUND;
import PressureAwareCompactionThroughputController.HBASE_HSTORE_COMPACTION_MAX_THROUGHPUT_LOWER_BOUND;
import PressureAwareCompactionThroughputController.HBASE_HSTORE_COMPACTION_THROUGHPUT_TUNE_PERIOD;
import StoreEngine.STORE_ENGINE_CLASS_KEY;
import StripeStoreConfig.FLUSH_TO_L0_KEY;
import StripeStoreConfig.INITIAL_STRIPE_COUNT_KEY;
import StripeStoreConfig.MIN_FILES_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.regionserver.DefaultStoreEngine;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.StripeStoreEngine;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestCompactionWithThroughputController {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactionWithThroughputController.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactionWithThroughputController.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final double EPSILON = 1.0E-6;

    private final TableName tableName = TableName.valueOf(getClass().getSimpleName());

    private final byte[] family = Bytes.toBytes("f");

    private final byte[] qualifier = Bytes.toBytes("q");

    @Test
    public void testCompaction() throws Exception {
        long limitTime = testCompactionWithThroughputLimit();
        long noLimitTime = testCompactionWithoutThroughputLimit();
        TestCompactionWithThroughputController.LOG.info((((("With 1M/s limit, compaction use " + limitTime) + "ms; without limit, compaction use ") + noLimitTime) + "ms"));
        // usually the throughput of a compaction without limitation is about 40MB/sec at least, so this
        // is a very weak assumption.
        Assert.assertTrue((limitTime > (noLimitTime * 2)));
    }

    /**
     * Test the tuning task of {@link PressureAwareCompactionThroughputController}
     */
    @Test
    public void testThroughputTuning() throws Exception {
        Configuration conf = TestCompactionWithThroughputController.TEST_UTIL.getConfiguration();
        conf.set(STORE_ENGINE_CLASS_KEY, DefaultStoreEngine.class.getName());
        conf.setLong(HBASE_HSTORE_COMPACTION_MAX_THROUGHPUT_HIGHER_BOUND, ((20L * 1024) * 1024));
        conf.setLong(HBASE_HSTORE_COMPACTION_MAX_THROUGHPUT_LOWER_BOUND, ((10L * 1024) * 1024));
        conf.setInt(HBASE_HSTORE_COMPACTION_MIN_KEY, 4);
        conf.setInt(BLOCKING_STOREFILES_KEY, 6);
        conf.set(HBASE_THROUGHPUT_CONTROLLER_KEY, PressureAwareCompactionThroughputController.class.getName());
        conf.setInt(HBASE_HSTORE_COMPACTION_THROUGHPUT_TUNE_PERIOD, 1000);
        TestCompactionWithThroughputController.TEST_UTIL.startMiniCluster(1);
        Connection conn = ConnectionFactory.createConnection(conf);
        try {
            TestCompactionWithThroughputController.TEST_UTIL.getAdmin().createTable(TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(family)).setCompactionEnabled(false).build());
            TestCompactionWithThroughputController.TEST_UTIL.waitTableAvailable(tableName);
            HRegionServer regionServer = TestCompactionWithThroughputController.TEST_UTIL.getRSForFirstRegionInTable(tableName);
            PressureAwareCompactionThroughputController throughputController = ((PressureAwareCompactionThroughputController) (regionServer.compactSplitThread.getCompactionThroughputController()));
            Assert.assertEquals(((10L * 1024) * 1024), throughputController.getMaxThroughput(), TestCompactionWithThroughputController.EPSILON);
            Table table = conn.getTable(tableName);
            for (int i = 0; i < 5; i++) {
                byte[] value = new byte[0];
                table.put(addColumn(family, qualifier, value));
                TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            }
            Thread.sleep(2000);
            Assert.assertEquals(((15L * 1024) * 1024), throughputController.getMaxThroughput(), TestCompactionWithThroughputController.EPSILON);
            byte[] value1 = new byte[0];
            table.put(addColumn(family, qualifier, value1));
            TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            Thread.sleep(2000);
            Assert.assertEquals(((20L * 1024) * 1024), throughputController.getMaxThroughput(), TestCompactionWithThroughputController.EPSILON);
            byte[] value = new byte[0];
            table.put(addColumn(family, qualifier, value));
            TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            Thread.sleep(2000);
            Assert.assertEquals(Double.MAX_VALUE, throughputController.getMaxThroughput(), TestCompactionWithThroughputController.EPSILON);
            conf.set(HBASE_THROUGHPUT_CONTROLLER_KEY, NoLimitThroughputController.class.getName());
            regionServer.compactSplitThread.onConfigurationChange(conf);
            Assert.assertTrue(throughputController.isStopped());
            Assert.assertTrue(((regionServer.compactSplitThread.getCompactionThroughputController()) instanceof NoLimitThroughputController));
        } finally {
            conn.close();
            TestCompactionWithThroughputController.TEST_UTIL.shutdownMiniCluster();
        }
    }

    /**
     * Test the logic that we calculate compaction pressure for a striped store.
     */
    @Test
    public void testGetCompactionPressureForStripedStore() throws Exception {
        Configuration conf = TestCompactionWithThroughputController.TEST_UTIL.getConfiguration();
        conf.set(STORE_ENGINE_CLASS_KEY, StripeStoreEngine.class.getName());
        conf.setBoolean(FLUSH_TO_L0_KEY, false);
        conf.setInt(INITIAL_STRIPE_COUNT_KEY, 2);
        conf.setInt(MIN_FILES_KEY, 4);
        conf.setInt(BLOCKING_STOREFILES_KEY, 12);
        TestCompactionWithThroughputController.TEST_UTIL.startMiniCluster(1);
        Connection conn = ConnectionFactory.createConnection(conf);
        try {
            TestCompactionWithThroughputController.TEST_UTIL.getAdmin().createTable(TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(family)).setCompactionEnabled(false).build());
            TestCompactionWithThroughputController.TEST_UTIL.waitTableAvailable(tableName);
            HStore store = getStoreWithName(tableName);
            Assert.assertEquals(0, store.getStorefilesCount());
            Assert.assertEquals(0.0, store.getCompactionPressure(), TestCompactionWithThroughputController.EPSILON);
            Table table = conn.getTable(tableName);
            for (int i = 0; i < 4; i++) {
                byte[] value1 = new byte[0];
                table.put(addColumn(family, qualifier, value1));
                byte[] value = new byte[0];
                table.put(addColumn(family, qualifier, value));
                TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            }
            Assert.assertEquals(8, store.getStorefilesCount());
            Assert.assertEquals(0.0, store.getCompactionPressure(), TestCompactionWithThroughputController.EPSILON);
            byte[] value5 = new byte[0];
            table.put(addColumn(family, qualifier, value5));
            byte[] value4 = new byte[0];
            table.put(addColumn(family, qualifier, value4));
            TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            Assert.assertEquals(10, store.getStorefilesCount());
            Assert.assertEquals(0.5, store.getCompactionPressure(), TestCompactionWithThroughputController.EPSILON);
            byte[] value3 = new byte[0];
            table.put(addColumn(family, qualifier, value3));
            byte[] value2 = new byte[0];
            table.put(addColumn(family, qualifier, value2));
            TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            Assert.assertEquals(12, store.getStorefilesCount());
            Assert.assertEquals(1.0, store.getCompactionPressure(), TestCompactionWithThroughputController.EPSILON);
            byte[] value1 = new byte[0];
            table.put(addColumn(family, qualifier, value1));
            byte[] value = new byte[0];
            table.put(addColumn(family, qualifier, value));
            TestCompactionWithThroughputController.TEST_UTIL.flush(tableName);
            Assert.assertEquals(14, store.getStorefilesCount());
            Assert.assertEquals(2.0, store.getCompactionPressure(), TestCompactionWithThroughputController.EPSILON);
        } finally {
            conn.close();
            TestCompactionWithThroughputController.TEST_UTIL.shutdownMiniCluster();
        }
    }
}

