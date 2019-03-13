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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.org.apache.commons.cli.Options;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test performance improvement of joined scanners optimization:
 * https://issues.apache.org/jira/browse/HBASE-5416
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestJoinedScanners {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestJoinedScanners.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestJoinedScanners.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] cf_essential = Bytes.toBytes("essential");

    private static final byte[] cf_joined = Bytes.toBytes("joined");

    private static final byte[] col_name = Bytes.toBytes("a");

    private static final byte[] flag_yes = Bytes.toBytes("Y");

    private static final byte[] flag_no = Bytes.toBytes("N");

    private static DataBlockEncoding blockEncoding = DataBlockEncoding.FAST_DIFF;

    private static int selectionRatio = 30;

    private static int valueWidth = 128 * 1024;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testJoinedScanners() throws Exception {
        byte[][] families = new byte[][]{ TestJoinedScanners.cf_essential, TestJoinedScanners.cf_joined };
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        for (byte[] family : families) {
            HColumnDescriptor hcd = new HColumnDescriptor(family);
            hcd.setDataBlockEncoding(TestJoinedScanners.blockEncoding);
            desc.addFamily(hcd);
        }
        TestJoinedScanners.TEST_UTIL.getAdmin().createTable(desc);
        Table ht = TestJoinedScanners.TEST_UTIL.getConnection().getTable(tableName);
        long rows_to_insert = 1000;
        int insert_batch = 20;
        long time = System.nanoTime();
        Random rand = new Random(time);
        TestJoinedScanners.LOG.info((((("Make " + (Long.toString(rows_to_insert))) + " rows, total size = ") + (Float.toString((((rows_to_insert * (TestJoinedScanners.valueWidth)) / 1024) / 1024)))) + " MB"));
        byte[] val_large = new byte[TestJoinedScanners.valueWidth];
        List<Put> puts = new ArrayList<>();
        for (long i = 0; i < rows_to_insert; i++) {
            Put put = new Put(Bytes.toBytes(Long.toString(i)));
            if ((rand.nextInt(100)) <= (TestJoinedScanners.selectionRatio)) {
                put.addColumn(TestJoinedScanners.cf_essential, TestJoinedScanners.col_name, TestJoinedScanners.flag_yes);
            } else {
                put.addColumn(TestJoinedScanners.cf_essential, TestJoinedScanners.col_name, TestJoinedScanners.flag_no);
            }
            put.addColumn(TestJoinedScanners.cf_joined, TestJoinedScanners.col_name, val_large);
            puts.add(put);
            if ((puts.size()) >= insert_batch) {
                ht.put(puts);
                puts.clear();
            }
        }
        if (!(puts.isEmpty())) {
            ht.put(puts);
            puts.clear();
        }
        TestJoinedScanners.LOG.info((("Data generated in " + (Double.toString((((System.nanoTime()) - time) / 1.0E9)))) + " seconds"));
        boolean slow = true;
        for (int i = 0; i < 10; ++i) {
            runScanner(ht, slow);
            slow = !slow;
        }
        ht.close();
    }

    private static Options options = new Options();

    @Test(expected = DoNotRetryIOException.class)
    public void testWithReverseScan() throws Exception {
        try (Connection con = TestJoinedScanners.TEST_UTIL.getConnection();Admin admin = con.getAdmin()) {
            TableName tableName = TableName.valueOf(name.getMethodName());
            TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of("cf1")).setColumnFamily(ColumnFamilyDescriptorBuilder.of("cf2")).build();
            admin.createTable(tableDescriptor);
            try (Table table = con.getTable(tableName)) {
                SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes("cf1"), Bytes.toBytes("col"), CompareOperator.EQUAL, Bytes.toBytes("val"));
                filter.setFilterIfMissing(true);
                // Reverse scan with loading CFs on demand
                Scan scan = new Scan();
                scan.setFilter(filter);
                scan.setReversed(true);
                scan.setLoadColumnFamiliesOnDemand(true);
                try (ResultScanner scanner = table.getScanner(scan)) {
                    // DoNotRetryIOException should occur
                    scanner.next();
                }
            }
        }
    }
}

