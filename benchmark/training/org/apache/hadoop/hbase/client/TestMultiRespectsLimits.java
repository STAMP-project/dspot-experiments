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
package org.apache.hadoop.hbase.client;


import CellBuilderType.SHALLOW_COPY;
import DataBlockEncoding.FAST_DIFF;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.CellBuilderFactory;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.ipc.RpcServerInterface;
import org.apache.hadoop.hbase.metrics.BaseSource;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * This test sets the multi size WAAAAAY low and then checks to make sure that gets will still make
 * progress.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestMultiRespectsLimits {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiRespectsLimits.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final MetricsAssertHelper METRICS_ASSERT = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    private static final byte[] FAMILY = Bytes.toBytes("D");

    public static final int MAX_SIZE = 500;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMultiLimits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestMultiRespectsLimits.TEST_UTIL.createTable(tableName, TestMultiRespectsLimits.FAMILY);
        TestMultiRespectsLimits.TEST_UTIL.loadTable(t, TestMultiRespectsLimits.FAMILY, false);
        // Split the table to make sure that the chunking happens accross regions.
        try (final Admin admin = TestMultiRespectsLimits.TEST_UTIL.getAdmin()) {
            admin.split(tableName);
            TestMultiRespectsLimits.TEST_UTIL.waitFor(60000, new Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (admin.getTableRegions(tableName).size()) > 1;
                }
            });
        }
        List<Get> gets = new ArrayList<>(TestMultiRespectsLimits.MAX_SIZE);
        for (int i = 0; i < (TestMultiRespectsLimits.MAX_SIZE); i++) {
            gets.add(new Get(HBaseTestingUtility.ROWS[i]));
        }
        RpcServerInterface rpcServer = TestMultiRespectsLimits.TEST_UTIL.getHBaseCluster().getRegionServer(0).getRpcServer();
        BaseSource s = rpcServer.getMetrics().getMetricsSource();
        long startingExceptions = TestMultiRespectsLimits.METRICS_ASSERT.getCounter("exceptions", s);
        long startingMultiExceptions = TestMultiRespectsLimits.METRICS_ASSERT.getCounter("exceptions.multiResponseTooLarge", s);
        Result[] results = t.get(gets);
        TestCase.assertEquals(TestMultiRespectsLimits.MAX_SIZE, results.length);
        // Cells from TEST_UTIL.loadTable have a length of 27.
        // Multiplying by less than that gives an easy lower bound on size.
        // However in reality each kv is being reported as much higher than that.
        TestMultiRespectsLimits.METRICS_ASSERT.assertCounterGt("exceptions", (startingExceptions + (((TestMultiRespectsLimits.MAX_SIZE) * 25) / (TestMultiRespectsLimits.MAX_SIZE))), s);
        TestMultiRespectsLimits.METRICS_ASSERT.assertCounterGt("exceptions.multiResponseTooLarge", (startingMultiExceptions + (((TestMultiRespectsLimits.MAX_SIZE) * 25) / (TestMultiRespectsLimits.MAX_SIZE))), s);
    }

    @Test
    public void testBlockMultiLimits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor hcd = new HColumnDescriptor(TestMultiRespectsLimits.FAMILY);
        hcd.setDataBlockEncoding(FAST_DIFF);
        desc.addFamily(hcd);
        TestMultiRespectsLimits.TEST_UTIL.getAdmin().createTable(desc);
        Table t = TestMultiRespectsLimits.TEST_UTIL.getConnection().getTable(tableName);
        final HRegionServer regionServer = TestMultiRespectsLimits.TEST_UTIL.getHBaseCluster().getRegionServer(0);
        RpcServerInterface rpcServer = regionServer.getRpcServer();
        BaseSource s = rpcServer.getMetrics().getMetricsSource();
        long startingExceptions = TestMultiRespectsLimits.METRICS_ASSERT.getCounter("exceptions", s);
        long startingMultiExceptions = TestMultiRespectsLimits.METRICS_ASSERT.getCounter("exceptions.multiResponseTooLarge", s);
        byte[] row = Bytes.toBytes("TEST");
        byte[][] cols = new byte[][]{ Bytes.toBytes("0")// Get this
        , Bytes.toBytes("1")// Buffer
        , Bytes.toBytes("2")// Buffer
        , Bytes.toBytes("3")// Get This
        , Bytes.toBytes("4")// Buffer
        , Bytes.toBytes("5")// Buffer
         };
        // Set the value size so that one result will be less than the MAX_SIE
        // however the block being reference will be larger than MAX_SIZE.
        // This should cause the regionserver to try and send a result immediately.
        byte[] value = new byte[(TestMultiRespectsLimits.MAX_SIZE) - 100];
        ThreadLocalRandom.current().nextBytes(value);
        for (byte[] col : cols) {
            Put p = new Put(row);
            p.add(CellBuilderFactory.create(SHALLOW_COPY).setRow(row).setFamily(TestMultiRespectsLimits.FAMILY).setQualifier(col).setTimestamp(p.getTimestamp()).setType(Cell.Type.Put).setValue(value).build());
            t.put(p);
        }
        // Make sure that a flush happens
        try (final Admin admin = TestMultiRespectsLimits.TEST_UTIL.getAdmin()) {
            admin.flush(tableName);
            TestMultiRespectsLimits.TEST_UTIL.waitFor(60000, new Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (regionServer.getRegions(tableName).get(0).getMaxFlushedSeqId()) > 3;
                }
            });
        }
        List<Get> gets = new ArrayList<>(2);
        Get g0 = new Get(row);
        g0.addColumn(TestMultiRespectsLimits.FAMILY, cols[0]);
        gets.add(g0);
        Get g2 = new Get(row);
        g2.addColumn(TestMultiRespectsLimits.FAMILY, cols[3]);
        gets.add(g2);
        Result[] results = t.get(gets);
        TestCase.assertEquals(2, results.length);
        TestMultiRespectsLimits.METRICS_ASSERT.assertCounterGt("exceptions", startingExceptions, s);
        TestMultiRespectsLimits.METRICS_ASSERT.assertCounterGt("exceptions.multiResponseTooLarge", startingMultiExceptions, s);
    }
}

