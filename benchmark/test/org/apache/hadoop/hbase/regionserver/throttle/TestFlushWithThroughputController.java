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


import FlushThroughputControllerFactory.HBASE_FLUSH_THROUGHPUT_CONTROLLER_KEY;
import PressureAwareFlushThroughputController.HBASE_HSTORE_FLUSH_THROUGHPUT_TUNE_PERIOD;
import StoreEngine.STORE_ENGINE_CLASS_KEY;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.master.LoadBalancer;
import org.apache.hadoop.hbase.regionserver.DefaultStoreEngine;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.StripeStoreEngine;
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


@Category(MediumTests.class)
public class TestFlushWithThroughputController {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFlushWithThroughputController.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFlushWithThroughputController.class);

    private static final double EPSILON = 1.3E-6;

    private HBaseTestingUtility hbtu;

    @Rule
    public TestName testName = new TestName();

    private TableName tableName;

    private final byte[] family = Bytes.toBytes("f");

    private final byte[] qualifier = Bytes.toBytes("q");

    @Test
    public void testFlushControl() throws Exception {
        testFlushWithThroughputLimit();
    }

    /**
     * Test the tuning task of {@link PressureAwareFlushThroughputController}
     */
    @Test
    public void testFlushThroughputTuning() throws Exception {
        Configuration conf = hbtu.getConfiguration();
        setMaxMinThroughputs(((20L * 1024) * 1024), ((10L * 1024) * 1024));
        conf.set(STORE_ENGINE_CLASS_KEY, DefaultStoreEngine.class.getName());
        conf.setInt(HBASE_HSTORE_FLUSH_THROUGHPUT_TUNE_PERIOD, 3000);
        hbtu.startMiniCluster(1);
        Connection conn = ConnectionFactory.createConnection(conf);
        hbtu.getAdmin().createTable(TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(family)).setCompactionEnabled(false).build());
        hbtu.waitTableAvailable(tableName);
        HRegionServer regionServer = hbtu.getRSForFirstRegionInTable(tableName);
        double pressure = regionServer.getFlushPressure();
        TestFlushWithThroughputController.LOG.debug(("Flush pressure before flushing: " + pressure));
        PressureAwareFlushThroughputController throughputController = ((PressureAwareFlushThroughputController) (regionServer.getFlushThroughputController()));
        for (HRegion region : regionServer.getRegions()) {
            region.flush(true);
        }
        // We used to assert that the flush pressure is zero but after HBASE-15787 or HBASE-18294 we
        // changed to use heapSize instead of dataSize to calculate the flush pressure, and since
        // heapSize will never be zero, so flush pressure will never be zero either. So we changed the
        // assertion here.
        Assert.assertTrue(((regionServer.getFlushPressure()) < pressure));
        Thread.sleep(5000);
        boolean tablesOnMaster = LoadBalancer.isTablesOnMaster(hbtu.getConfiguration());
        if (tablesOnMaster) {
            // If no tables on the master, this math is off and I'm not sure what it is supposed to be
            // when meta is on the regionserver and not on the master.
            Assert.assertEquals(((10L * 1024) * 1024), throughputController.getMaxThroughput(), TestFlushWithThroughputController.EPSILON);
        }
        Table table = conn.getTable(tableName);
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                byte[] value = new byte[256 * 1024];
                rand.nextBytes(value);
                table.put(addColumn(family, qualifier, value));
            }
        }
        Thread.sleep(5000);
        double expectedThroughPut = ((10L * 1024) * 1024) * (1 + (regionServer.getFlushPressure()));
        Assert.assertEquals(expectedThroughPut, throughputController.getMaxThroughput(), TestFlushWithThroughputController.EPSILON);
        conf.set(HBASE_FLUSH_THROUGHPUT_CONTROLLER_KEY, NoLimitThroughputController.class.getName());
        regionServer.onConfigurationChange(conf);
        Assert.assertTrue(throughputController.isStopped());
        Assert.assertTrue(((regionServer.getFlushThroughputController()) instanceof NoLimitThroughputController));
        conn.close();
    }

    /**
     * Test the logic for striped store.
     */
    @Test
    public void testFlushControlForStripedStore() throws Exception {
        hbtu.getConfiguration().set(STORE_ENGINE_CLASS_KEY, StripeStoreEngine.class.getName());
        testFlushWithThroughputLimit();
    }
}

