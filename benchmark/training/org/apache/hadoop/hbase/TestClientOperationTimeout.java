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
package org.apache.hadoop.hbase;


import ClientProtos.GetRequest;
import ClientProtos.GetResponse;
import ClientProtos.MutateRequest;
import ClientProtos.MutateResponse;
import ClientProtos.ScanRequest;
import ClientProtos.ScanResponse;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.RSRpcServices;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * These tests verify that the RPC timeouts ('hbase.client.operation.timeout' and
 * 'hbase.client.scanner.timeout.period') work correctly using a modified Region Server which
 * injects delays to get, scan and mutate operations.
 *
 * When 'hbase.client.operation.timeout' is set and client operation is not completed in time the
 * client will retry the operation 'hbase.client.retries.number' times. After that
 * {@link SocketTimeoutException} will be thrown.
 *
 * Using 'hbase.client.scanner.timeout.period' configuration property similar behavior can be
 * specified for scan related operations such as openScanner(), next(). If that times out
 * {@link RetriesExhaustedException} will be thrown.
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestClientOperationTimeout {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClientOperationTimeout.class);

    private static final HBaseTestingUtility TESTING_UTIL = new HBaseTestingUtility();

    // Activate the delays after table creation to test get/scan/put
    private static int DELAY_GET;

    private static int DELAY_SCAN;

    private static int DELAY_MUTATE;

    private final byte[] FAMILY = Bytes.toBytes("family");

    private final byte[] ROW = Bytes.toBytes("row");

    private final byte[] QUALIFIER = Bytes.toBytes("qualifier");

    private final byte[] VALUE = Bytes.toBytes("value");

    @Rule
    public TestName name = new TestName();

    private Table table;

    /**
     * Tests that a get on a table throws {@link SocketTimeoutException} when the operation takes
     * longer than 'hbase.client.operation.timeout'.
     */
    @Test(expected = SocketTimeoutException.class)
    public void testGetTimeout() throws Exception {
        TestClientOperationTimeout.DELAY_GET = 600;
        table.get(new Get(ROW));
    }

    /**
     * Tests that a put on a table throws {@link SocketTimeoutException} when the operation takes
     * longer than 'hbase.client.operation.timeout'.
     */
    @Test(expected = SocketTimeoutException.class)
    public void testPutTimeout() throws Exception {
        TestClientOperationTimeout.DELAY_MUTATE = 600;
        Put put = new Put(ROW);
        put.addColumn(FAMILY, QUALIFIER, VALUE);
        table.put(put);
    }

    /**
     * Tests that scan on a table throws {@link RetriesExhaustedException} when the operation takes
     * longer than 'hbase.client.scanner.timeout.period'.
     */
    @Test(expected = RetriesExhaustedException.class)
    public void testScanTimeout() throws Exception {
        TestClientOperationTimeout.DELAY_SCAN = 600;
        ResultScanner scanner = table.getScanner(new Scan());
        scanner.next();
    }

    private static class DelayedRegionServer extends MiniHBaseCluster.MiniHBaseClusterRegionServer {
        public DelayedRegionServer(Configuration conf) throws IOException, InterruptedException {
            super(conf);
        }

        @Override
        protected RSRpcServices createRpcServices() throws IOException {
            return new TestClientOperationTimeout.DelayedRSRpcServices(this);
        }
    }

    /**
     * This {@link RSRpcServices} class injects delay for Rpc calls and after executes super methods.
     */
    public static class DelayedRSRpcServices extends RSRpcServices {
        DelayedRSRpcServices(HRegionServer rs) throws IOException {
            super(rs);
        }

        @Override
        public GetResponse get(RpcController controller, ClientProtos.GetRequest request) throws ServiceException {
            try {
                Thread.sleep(TestClientOperationTimeout.DELAY_GET);
            } catch (InterruptedException e) {
                LOG.error("Sleep interrupted during get operation", e);
            }
            return super.get(controller, request);
        }

        @Override
        public MutateResponse mutate(RpcController rpcc, ClientProtos.MutateRequest request) throws ServiceException {
            try {
                Thread.sleep(TestClientOperationTimeout.DELAY_MUTATE);
            } catch (InterruptedException e) {
                LOG.error("Sleep interrupted during mutate operation", e);
            }
            return super.mutate(rpcc, request);
        }

        @Override
        public ScanResponse scan(RpcController controller, ClientProtos.ScanRequest request) throws ServiceException {
            try {
                Thread.sleep(TestClientOperationTimeout.DELAY_SCAN);
            } catch (InterruptedException e) {
                LOG.error("Sleep interrupted during scan operation", e);
            }
            return super.scan(controller, request);
        }
    }
}

