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


import TableName.META_TABLE_NAME;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.RSRpcServices;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanResponse;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the scenario where a HRegionServer#scan() call, while scanning, timeout at client side and
 * getting retried. This scenario should not result in some data being skipped at RS side.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestClientScannerRPCTimeout {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClientScannerRPCTimeout.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestClientScannerRPCTimeout.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] FAMILY = Bytes.toBytes("testFamily");

    private static final byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static final byte[] VALUE = Bytes.toBytes("testValue");

    private static final int rpcTimeout = 2 * 1000;

    private static final int CLIENT_RETRIES_NUMBER = 3;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testScannerNextRPCTimesout() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestClientScannerRPCTimeout.TEST_UTIL.createTable(tableName, TestClientScannerRPCTimeout.FAMILY);
        byte[] r0 = Bytes.toBytes("row-0");
        byte[] r1 = Bytes.toBytes("row-1");
        byte[] r2 = Bytes.toBytes("row-2");
        byte[] r3 = Bytes.toBytes("row-3");
        putToTable(ht, r0);
        putToTable(ht, r1);
        putToTable(ht, r2);
        putToTable(ht, r3);
        TestClientScannerRPCTimeout.LOG.info("Wrote our three values");
        TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.seqNoToSleepOn = 1;
        Scan scan = new Scan();
        scan.setCaching(1);
        ResultScanner scanner = ht.getScanner(scan);
        Result result = scanner.next();
        // fetched when openScanner
        Assert.assertTrue("Expected row: row-0", Bytes.equals(r0, result.getRow()));
        result = scanner.next();
        Assert.assertTrue("Expected row: row-1", Bytes.equals(r1, result.getRow()));
        TestClientScannerRPCTimeout.LOG.info("Got expected first row");
        long t1 = System.currentTimeMillis();
        result = scanner.next();
        Assert.assertTrue((((System.currentTimeMillis()) - t1) > (TestClientScannerRPCTimeout.rpcTimeout)));
        Assert.assertTrue("Expected row: row-2", Bytes.equals(r2, result.getRow()));
        TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.seqNoToSleepOn = -1;// No need of sleep

        result = scanner.next();
        Assert.assertTrue("Expected row: row-3", Bytes.equals(r3, result.getRow()));
        scanner.close();
        // test the case that RPC is always timesout
        scanner = ht.getScanner(scan);
        TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.sleepAlways = true;
        TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.tryNumber = 0;
        try {
            result = scanner.next();
        } catch (IOException ioe) {
            // catch the exception after max retry number
            TestClientScannerRPCTimeout.LOG.info(("Failed after maximal attempts=" + (TestClientScannerRPCTimeout.CLIENT_RETRIES_NUMBER)), ioe);
        }
        Assert.assertTrue(((("Expected maximal try number=" + (TestClientScannerRPCTimeout.CLIENT_RETRIES_NUMBER)) + ", actual =") + (TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.tryNumber)), ((TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.tryNumber) <= (TestClientScannerRPCTimeout.CLIENT_RETRIES_NUMBER)));
    }

    private static class RegionServerWithScanTimeout extends MiniHBaseCluster.MiniHBaseClusterRegionServer {
        public RegionServerWithScanTimeout(Configuration conf) throws IOException, InterruptedException {
            super(conf);
        }

        @Override
        protected RSRpcServices createRpcServices() throws IOException {
            return new TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout(this);
        }
    }

    private static class RSRpcServicesWithScanTimeout extends RSRpcServices {
        private long tableScannerId;

        private boolean slept;

        private static long seqNoToSleepOn = -1;

        private static boolean sleepAlways = false;

        private static int tryNumber = 0;

        public RSRpcServicesWithScanTimeout(HRegionServer rs) throws IOException {
            super(rs);
        }

        @Override
        public ScanResponse scan(final RpcController controller, final ScanRequest request) throws ServiceException {
            if (request.hasScannerId()) {
                ScanResponse scanResponse = super.scan(controller, request);
                if (((this.tableScannerId) == (request.getScannerId())) && ((TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.sleepAlways) || ((!(slept)) && ((TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.seqNoToSleepOn) == (request.getNextCallSeq()))))) {
                    try {
                        TestClientScannerRPCTimeout.LOG.info(("SLEEPING " + ((TestClientScannerRPCTimeout.rpcTimeout) + 500)));
                        Thread.sleep(((TestClientScannerRPCTimeout.rpcTimeout) + 500));
                    } catch (InterruptedException e) {
                    }
                    slept = true;
                    (TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.tryNumber)++;
                    if ((TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.tryNumber) > (2 * (TestClientScannerRPCTimeout.CLIENT_RETRIES_NUMBER))) {
                        TestClientScannerRPCTimeout.RSRpcServicesWithScanTimeout.sleepAlways = false;
                    }
                }
                return scanResponse;
            } else {
                ScanResponse scanRes = super.scan(controller, request);
                String regionName = Bytes.toString(request.getRegion().getValue().toByteArray());
                if (!(regionName.contains(META_TABLE_NAME.getNameAsString()))) {
                    tableScannerId = scanRes.getScannerId();
                }
                return scanRes;
            }
        }
    }
}

