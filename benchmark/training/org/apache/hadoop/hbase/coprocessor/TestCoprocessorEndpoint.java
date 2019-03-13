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
package org.apache.hadoop.hbase.coprocessor;


import Batch.Call;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import TestProtos.EchoRequestProto;
import TestProtos.EmptyRequestProto;
import TestRpcServiceProtos.TestProtobufRpcProto;
import TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcUtils;
import org.apache.hadoop.hbase.ipc.ServerRpcController;
import org.apache.hadoop.hbase.ipc.protobuf.generated.TestProtos;
import org.apache.hadoop.hbase.ipc.protobuf.generated.TestRpcServiceProtos;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestEndpoint: test cases to verify coprocessor Endpoint
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestCoprocessorEndpoint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorEndpoint.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCoprocessorEndpoint.class);

    private static final TableName TEST_TABLE = TableName.valueOf("TestCoprocessorEndpoint");

    private static final byte[] TEST_FAMILY = Bytes.toBytes("TestFamily");

    private static final byte[] TEST_QUALIFIER = Bytes.toBytes("TestQualifier");

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static final int ROWSIZE = 20;

    private static final int rowSeperator1 = 5;

    private static final int rowSeperator2 = 12;

    private static byte[][] ROWS = TestCoprocessorEndpoint.makeN(TestCoprocessorEndpoint.ROW, TestCoprocessorEndpoint.ROWSIZE);

    private static HBaseTestingUtility util = new HBaseTestingUtility();

    @Test
    public void testAggregation() throws Throwable {
        Table table = TestCoprocessorEndpoint.util.getConnection().getTable(TestCoprocessorEndpoint.TEST_TABLE);
        Map<byte[], Long> results = sum(table, TestCoprocessorEndpoint.TEST_FAMILY, TestCoprocessorEndpoint.TEST_QUALIFIER, TestCoprocessorEndpoint.ROWS[0], TestCoprocessorEndpoint.ROWS[((TestCoprocessorEndpoint.ROWS.length) - 1)]);
        int sumResult = 0;
        int expectedResult = 0;
        for (Map.Entry<byte[], Long> e : results.entrySet()) {
            TestCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue();
        }
        for (int i = 0; i < (TestCoprocessorEndpoint.ROWSIZE); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        results.clear();
        // scan: for region 2 and region 3
        results = sum(table, TestCoprocessorEndpoint.TEST_FAMILY, TestCoprocessorEndpoint.TEST_QUALIFIER, TestCoprocessorEndpoint.ROWS[TestCoprocessorEndpoint.rowSeperator1], TestCoprocessorEndpoint.ROWS[((TestCoprocessorEndpoint.ROWS.length) - 1)]);
        sumResult = 0;
        expectedResult = 0;
        for (Map.Entry<byte[], Long> e : results.entrySet()) {
            TestCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue();
        }
        for (int i = TestCoprocessorEndpoint.rowSeperator1; i < (TestCoprocessorEndpoint.ROWSIZE); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        table.close();
    }

    @Test
    public void testCoprocessorService() throws Throwable {
        Table table = TestCoprocessorEndpoint.util.getConnection().getTable(TestCoprocessorEndpoint.TEST_TABLE);
        List<HRegionLocation> regions;
        try (RegionLocator rl = TestCoprocessorEndpoint.util.getConnection().getRegionLocator(TestCoprocessorEndpoint.TEST_TABLE)) {
            regions = rl.getAllRegionLocations();
        }
        final TestProtos.EchoRequestProto request = EchoRequestProto.newBuilder().setMessage("hello").build();
        final Map<byte[], String> results = Collections.synchronizedMap(new TreeMap<byte[], String>(Bytes.BYTES_COMPARATOR));
        try {
            // scan: for all regions
            final RpcController controller = new ServerRpcController();
            table.coprocessorService(TestProtobufRpcProto.class, TestCoprocessorEndpoint.ROWS[0], TestCoprocessorEndpoint.ROWS[((TestCoprocessorEndpoint.ROWS.length) - 1)], new Call<TestRpcServiceProtos.TestProtobufRpcProto, TestProtos.EchoResponseProto>() {
                @Override
                public TestProtos.EchoResponseProto call(TestRpcServiceProtos.TestProtobufRpcProto instance) throws IOException {
                    TestCoprocessorEndpoint.LOG.debug(("Default response is " + (TestProtos.EchoRequestProto.getDefaultInstance())));
                    CoprocessorRpcUtils.BlockingRpcCallback<TestProtos.EchoResponseProto> callback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                    instance.echo(controller, request, callback);
                    TestProtos.EchoResponseProto response = callback.get();
                    TestCoprocessorEndpoint.LOG.debug(("Batch.Call returning result " + response));
                    return response;
                }
            }, new Batch.Callback<TestProtos.EchoResponseProto>() {
                @Override
                public void update(byte[] region, byte[] row, TestProtos.EchoResponseProto result) {
                    assertNotNull(result);
                    assertEquals("hello", result.getMessage());
                    results.put(region, result.getMessage());
                }
            });
            for (Map.Entry<byte[], String> e : results.entrySet()) {
                TestCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            }
            Assert.assertEquals(3, results.size());
            for (HRegionLocation info : regions) {
                TestCoprocessorEndpoint.LOG.info(("Region info is " + (info.getRegionInfo().getRegionNameAsString())));
                Assert.assertTrue(results.containsKey(info.getRegionInfo().getRegionName()));
            }
            results.clear();
            // scan: for region 2 and region 3
            table.coprocessorService(TestProtobufRpcProto.class, TestCoprocessorEndpoint.ROWS[TestCoprocessorEndpoint.rowSeperator1], TestCoprocessorEndpoint.ROWS[((TestCoprocessorEndpoint.ROWS.length) - 1)], new Call<TestRpcServiceProtos.TestProtobufRpcProto, TestProtos.EchoResponseProto>() {
                @Override
                public TestProtos.EchoResponseProto call(TestRpcServiceProtos.TestProtobufRpcProto instance) throws IOException {
                    TestCoprocessorEndpoint.LOG.debug(("Default response is " + (TestProtos.EchoRequestProto.getDefaultInstance())));
                    CoprocessorRpcUtils.BlockingRpcCallback<TestProtos.EchoResponseProto> callback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                    instance.echo(controller, request, callback);
                    TestProtos.EchoResponseProto response = callback.get();
                    TestCoprocessorEndpoint.LOG.debug(("Batch.Call returning result " + response));
                    return response;
                }
            }, new Batch.Callback<TestProtos.EchoResponseProto>() {
                @Override
                public void update(byte[] region, byte[] row, TestProtos.EchoResponseProto result) {
                    assertNotNull(result);
                    assertEquals("hello", result.getMessage());
                    results.put(region, result.getMessage());
                }
            });
            for (Map.Entry<byte[], String> e : results.entrySet()) {
                TestCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            }
            Assert.assertEquals(2, results.size());
        } finally {
            table.close();
        }
    }

    @Test
    public void testCoprocessorServiceNullResponse() throws Throwable {
        Table table = TestCoprocessorEndpoint.util.getConnection().getTable(TestCoprocessorEndpoint.TEST_TABLE);
        List<HRegionLocation> regions;
        try (RegionLocator rl = TestCoprocessorEndpoint.util.getConnection().getRegionLocator(TestCoprocessorEndpoint.TEST_TABLE)) {
            regions = rl.getAllRegionLocations();
        }
        final TestProtos.EchoRequestProto request = EchoRequestProto.newBuilder().setMessage("hello").build();
        try {
            // scan: for all regions
            final RpcController controller = new ServerRpcController();
            // test that null results are supported
            Map<byte[], String> results = table.coprocessorService(TestProtobufRpcProto.class, TestCoprocessorEndpoint.ROWS[0], TestCoprocessorEndpoint.ROWS[((TestCoprocessorEndpoint.ROWS.length) - 1)], new Call<TestRpcServiceProtos.TestProtobufRpcProto, String>() {
                public String call(TestRpcServiceProtos.TestProtobufRpcProto instance) throws IOException {
                    CoprocessorRpcUtils.BlockingRpcCallback<TestProtos.EchoResponseProto> callback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                    instance.echo(controller, request, callback);
                    TestProtos.EchoResponseProto response = callback.get();
                    TestCoprocessorEndpoint.LOG.debug(("Batch.Call got result " + response));
                    return null;
                }
            });
            for (Map.Entry<byte[], String> e : results.entrySet()) {
                TestCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            }
            Assert.assertEquals(3, results.size());
            for (HRegionLocation region : regions) {
                HRegionInfo info = region.getRegionInfo();
                TestCoprocessorEndpoint.LOG.info(("Region info is " + (info.getRegionNameAsString())));
                Assert.assertTrue(results.containsKey(info.getRegionName()));
                Assert.assertNull(results.get(info.getRegionName()));
            }
        } finally {
            table.close();
        }
    }

    @Test
    public void testMasterCoprocessorService() throws Throwable {
        Admin admin = TestCoprocessorEndpoint.util.getAdmin();
        final TestProtos.EchoRequestProto request = EchoRequestProto.newBuilder().setMessage("hello").build();
        TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface service = TestProtobufRpcProto.newBlockingStub(admin.coprocessorService());
        Assert.assertEquals("hello", service.echo(null, request).getMessage());
    }

    @Test
    public void testCoprocessorError() throws Exception {
        Configuration configuration = new Configuration(TestCoprocessorEndpoint.util.getConfiguration());
        // Make it not retry forever
        configuration.setInt(HBASE_CLIENT_RETRIES_NUMBER, 1);
        Table table = TestCoprocessorEndpoint.util.getConnection().getTable(TestCoprocessorEndpoint.TEST_TABLE);
        try {
            CoprocessorRpcChannel protocol = table.coprocessorService(TestCoprocessorEndpoint.ROWS[0]);
            TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface service = TestProtobufRpcProto.newBlockingStub(protocol);
            service.error(null, EmptyRequestProto.getDefaultInstance());
            Assert.fail("Should have thrown an exception");
        } catch (ServiceException e) {
        } finally {
            table.close();
        }
    }

    @Test
    public void testMasterCoprocessorError() throws Throwable {
        Admin admin = TestCoprocessorEndpoint.util.getAdmin();
        TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface service = TestProtobufRpcProto.newBlockingStub(admin.coprocessorService());
        try {
            service.error(null, EmptyRequestProto.getDefaultInstance());
            Assert.fail("Should have thrown an exception");
        } catch (ServiceException e) {
        }
    }
}

