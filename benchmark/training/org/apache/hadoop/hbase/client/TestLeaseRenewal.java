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


import java.util.Arrays;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ipc.MetricsHBaseServerSource;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestLeaseRenewal {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLeaseRenewal.class);

    public MetricsAssertHelper HELPER = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static final byte[] ANOTHERROW = Bytes.toBytes("anotherrow");

    private static final byte[] COL_QUAL = Bytes.toBytes("f1");

    private static final byte[] VAL_BYTES = Bytes.toBytes("v1");

    private static final byte[] ROW_BYTES = Bytes.toBytes("r1");

    private static final int leaseTimeout = (HConstants.DEFAULT_HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD) / 4;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testLeaseRenewal() throws Exception {
        Table table = TestLeaseRenewal.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestLeaseRenewal.FAMILY);
        Put p = new Put(TestLeaseRenewal.ROW_BYTES);
        p.addColumn(TestLeaseRenewal.FAMILY, TestLeaseRenewal.COL_QUAL, TestLeaseRenewal.VAL_BYTES);
        table.put(p);
        p = new Put(TestLeaseRenewal.ANOTHERROW);
        p.addColumn(TestLeaseRenewal.FAMILY, TestLeaseRenewal.COL_QUAL, TestLeaseRenewal.VAL_BYTES);
        table.put(p);
        Scan s = new Scan();
        s.setCaching(1);
        ResultScanner rs = table.getScanner(s);
        // we haven't open the scanner yet so nothing happens
        Assert.assertFalse(rs.renewLease());
        Assert.assertTrue(Arrays.equals(rs.next().getRow(), TestLeaseRenewal.ANOTHERROW));
        // renew the lease a few times, long enough to be sure
        // the lease would have expired otherwise
        Thread.sleep(((TestLeaseRenewal.leaseTimeout) / 2));
        Assert.assertTrue(rs.renewLease());
        Thread.sleep(((TestLeaseRenewal.leaseTimeout) / 2));
        Assert.assertTrue(rs.renewLease());
        Thread.sleep(((TestLeaseRenewal.leaseTimeout) / 2));
        Assert.assertTrue(rs.renewLease());
        // make sure we haven't advanced the scanner
        Assert.assertTrue(Arrays.equals(rs.next().getRow(), TestLeaseRenewal.ROW_BYTES));
        // renewLease should return false now as we have read all the data already
        Assert.assertFalse(rs.renewLease());
        // make sure scanner is exhausted now
        Assert.assertNull(rs.next());
        // renewLease should return false now
        Assert.assertFalse(rs.renewLease());
        rs.close();
        table.close();
        MetricsHBaseServerSource serverSource = TestLeaseRenewal.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0).getRpcServer().getMetrics().getMetricsSource();
        HELPER.assertCounter("exceptions.OutOfOrderScannerNextException", 0, serverSource);
    }
}

