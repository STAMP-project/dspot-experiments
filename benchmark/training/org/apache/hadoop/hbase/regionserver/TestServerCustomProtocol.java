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


import Batch.Call;
import PingProtos.PingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.CoprocessorException;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.CountRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.CountResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.HelloRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.HelloResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.IncrementCountRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.IncrementCountResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.NoopRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.NoopResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.PingRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.PingResponse;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcUtils;
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
public class TestServerCustomProtocol {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestServerCustomProtocol.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestServerCustomProtocol.class);

    static final String WHOAREYOU = "Who are you?";

    static final String NOBODY = "nobody";

    static final String HELLO = "Hello, ";

    /* Test protocol implementation */
    public static class PingHandler extends PingProtos.PingService implements RegionCoprocessor {
        private int counter = 0;

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            if (env instanceof RegionCoprocessorEnvironment) {
                return;
            }
            throw new CoprocessorException("Must be loaded on a table region!");
        }

        @Override
        public void stop(CoprocessorEnvironment env) throws IOException {
            // Nothing to do.
        }

        @Override
        public void ping(RpcController controller, PingRequest request, RpcCallback<PingResponse> done) {
            (this.counter)++;
            done.run(PingResponse.newBuilder().setPong("pong").build());
        }

        @Override
        public void count(RpcController controller, CountRequest request, RpcCallback<CountResponse> done) {
            done.run(CountResponse.newBuilder().setCount(this.counter).build());
        }

        @Override
        public void increment(RpcController controller, IncrementCountRequest request, RpcCallback<IncrementCountResponse> done) {
            this.counter += request.getDiff();
            done.run(IncrementCountResponse.newBuilder().setCount(this.counter).build());
        }

        @Override
        public void hello(RpcController controller, HelloRequest request, RpcCallback<HelloResponse> done) {
            if (!(request.hasName())) {
                done.run(HelloResponse.newBuilder().setResponse(TestServerCustomProtocol.WHOAREYOU).build());
            } else
                if (request.getName().equals(TestServerCustomProtocol.NOBODY)) {
                    done.run(HelloResponse.newBuilder().build());
                } else {
                    done.run(HelloResponse.newBuilder().setResponse(((TestServerCustomProtocol.HELLO) + (request.getName()))).build());
                }

        }

        @Override
        public void noop(RpcController controller, NoopRequest request, RpcCallback<NoopResponse> done) {
            done.run(NoopResponse.newBuilder().build());
        }

        @Override
        public Iterable<Service> getServices() {
            return Collections.singleton(this);
        }
    }

    private static final TableName TEST_TABLE = TableName.valueOf("test");

    private static final byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static final byte[] ROW_A = Bytes.toBytes("aaa");

    private static final byte[] ROW_B = Bytes.toBytes("bbb");

    private static final byte[] ROW_C = Bytes.toBytes("ccc");

    private static final byte[] ROW_AB = Bytes.toBytes("abb");

    private static final byte[] ROW_BC = Bytes.toBytes("bcc");

    private static HBaseTestingUtility util = new HBaseTestingUtility();

    @Test
    public void testSingleProxy() throws Throwable {
        Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE);
        Map<byte[], String> results = ping(table, null, null);
        // There are three regions so should get back three results.
        Assert.assertEquals(3, results.size());
        for (Map.Entry<byte[], String> e : results.entrySet()) {
            Assert.assertEquals("Invalid custom protocol response", "pong", e.getValue());
        }
        hello(table, "George", ((TestServerCustomProtocol.HELLO) + "George"));
        TestServerCustomProtocol.LOG.info("Did george");
        hello(table, null, "Who are you?");
        TestServerCustomProtocol.LOG.info("Who are you");
        hello(table, TestServerCustomProtocol.NOBODY, null);
        TestServerCustomProtocol.LOG.info(TestServerCustomProtocol.NOBODY);
        Map<byte[], Integer> intResults = table.coprocessorService(PingService.class, null, null, new Call<PingProtos.PingService, Integer>() {
            @Override
            public Integer call(PingProtos.PingService instance) throws IOException {
                CoprocessorRpcUtils.BlockingRpcCallback<PingProtos.CountResponse> rpcCallback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                instance.count(null, PingProtos.CountRequest.newBuilder().build(), rpcCallback);
                return rpcCallback.get().getCount();
            }
        });
        int count = -1;
        for (Map.Entry<byte[], Integer> e : intResults.entrySet()) {
            Assert.assertTrue(((e.getValue()) > 0));
            count = e.getValue();
        }
        final int diff = 5;
        intResults = table.coprocessorService(PingService.class, null, null, new Call<PingProtos.PingService, Integer>() {
            @Override
            public Integer call(PingProtos.PingService instance) throws IOException {
                CoprocessorRpcUtils.BlockingRpcCallback<PingProtos.IncrementCountResponse> rpcCallback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                instance.increment(null, PingProtos.IncrementCountRequest.newBuilder().setDiff(diff).build(), rpcCallback);
                return rpcCallback.get().getCount();
            }
        });
        // There are three regions so should get back three results.
        Assert.assertEquals(3, results.size());
        for (Map.Entry<byte[], Integer> e : intResults.entrySet()) {
            Assert.assertEquals(e.getValue().intValue(), (count + diff));
        }
        table.close();
    }

    @Test
    public void testSingleMethod() throws Throwable {
        try (Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE);RegionLocator locator = TestServerCustomProtocol.util.getConnection().getRegionLocator(TestServerCustomProtocol.TEST_TABLE)) {
            Map<byte[], String> results = table.coprocessorService(PingService.class, null, TestServerCustomProtocol.ROW_A, new Call<PingProtos.PingService, String>() {
                @Override
                public String call(PingProtos.PingService instance) throws IOException {
                    CoprocessorRpcUtils.BlockingRpcCallback<PingProtos.PingResponse> rpcCallback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                    instance.ping(null, PingProtos.PingRequest.newBuilder().build(), rpcCallback);
                    return rpcCallback.get().getPong();
                }
            });
            // Should have gotten results for 1 of the three regions only since we specified
            // rows from 1 region
            Assert.assertEquals(1, results.size());
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_A);
            final String name = "NAME";
            results = hello(table, name, null, TestServerCustomProtocol.ROW_A);
            // Should have gotten results for 1 of the three regions only since we specified
            // rows from 1 region
            Assert.assertEquals(1, results.size());
            verifyRegionResults(locator, results, "Hello, NAME", TestServerCustomProtocol.ROW_A);
        }
    }

    @Test
    public void testRowRange() throws Throwable {
        try (Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE);RegionLocator locator = TestServerCustomProtocol.util.getConnection().getRegionLocator(TestServerCustomProtocol.TEST_TABLE)) {
            for (HRegionLocation e : locator.getAllRegionLocations()) {
                TestServerCustomProtocol.LOG.info(((("Region " + (e.getRegionInfo().getRegionNameAsString())) + ", servername=") + (e.getServerName())));
            }
            // Here are what regions looked like on a run:
            // 
            // test,,1355943549657.c65d4822d8bdecc033a96451f3a0f55d.
            // test,bbb,1355943549661.110393b070dd1ed93441e0bc9b3ffb7e.
            // test,ccc,1355943549665.c3d6d125141359cbbd2a43eaff3cdf74.
            Map<byte[], String> results = ping(table, null, TestServerCustomProtocol.ROW_A);
            // Should contain first region only.
            Assert.assertEquals(1, results.size());
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_A);
            // Test start row + empty end
            results = ping(table, TestServerCustomProtocol.ROW_BC, null);
            Assert.assertEquals(2, results.size());
            // should contain last 2 regions
            HRegionLocation loc = locator.getRegionLocation(TestServerCustomProtocol.ROW_A, true);
            Assert.assertNull("Should be missing region for row aaa (prior to start row)", results.get(loc.getRegionInfo().getRegionName()));
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_B);
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_C);
            // test empty start + end
            results = ping(table, null, TestServerCustomProtocol.ROW_BC);
            // should contain the first 2 regions
            Assert.assertEquals(2, results.size());
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_A);
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_B);
            loc = locator.getRegionLocation(TestServerCustomProtocol.ROW_C, true);
            Assert.assertNull("Should be missing region for row ccc (past stop row)", results.get(loc.getRegionInfo().getRegionName()));
            // test explicit start + end
            results = ping(table, TestServerCustomProtocol.ROW_AB, TestServerCustomProtocol.ROW_BC);
            // should contain first 2 regions
            Assert.assertEquals(2, results.size());
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_A);
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_B);
            loc = locator.getRegionLocation(TestServerCustomProtocol.ROW_C, true);
            Assert.assertNull("Should be missing region for row ccc (past stop row)", results.get(loc.getRegionInfo().getRegionName()));
            // test single region
            results = ping(table, TestServerCustomProtocol.ROW_B, TestServerCustomProtocol.ROW_BC);
            // should only contain region bbb
            Assert.assertEquals(1, results.size());
            verifyRegionResults(locator, results, TestServerCustomProtocol.ROW_B);
            loc = locator.getRegionLocation(TestServerCustomProtocol.ROW_A, true);
            Assert.assertNull("Should be missing region for row aaa (prior to start)", results.get(loc.getRegionInfo().getRegionName()));
            loc = locator.getRegionLocation(TestServerCustomProtocol.ROW_C, true);
            Assert.assertNull("Should be missing region for row ccc (past stop row)", results.get(loc.getRegionInfo().getRegionName()));
        }
    }

    @Test
    public void testCompoundCall() throws Throwable {
        try (Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE);RegionLocator locator = TestServerCustomProtocol.util.getConnection().getRegionLocator(TestServerCustomProtocol.TEST_TABLE)) {
            Map<byte[], String> results = compoundOfHelloAndPing(table, TestServerCustomProtocol.ROW_A, TestServerCustomProtocol.ROW_C);
            verifyRegionResults(locator, results, "Hello, pong", TestServerCustomProtocol.ROW_A);
            verifyRegionResults(locator, results, "Hello, pong", TestServerCustomProtocol.ROW_B);
            verifyRegionResults(locator, results, "Hello, pong", TestServerCustomProtocol.ROW_C);
        }
    }

    @Test
    public void testNullCall() throws Throwable {
        try (Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE);RegionLocator locator = TestServerCustomProtocol.util.getConnection().getRegionLocator(TestServerCustomProtocol.TEST_TABLE)) {
            Map<byte[], String> results = hello(table, null, TestServerCustomProtocol.ROW_A, TestServerCustomProtocol.ROW_C);
            verifyRegionResults(locator, results, "Who are you?", TestServerCustomProtocol.ROW_A);
            verifyRegionResults(locator, results, "Who are you?", TestServerCustomProtocol.ROW_B);
            verifyRegionResults(locator, results, "Who are you?", TestServerCustomProtocol.ROW_C);
        }
    }

    @Test
    public void testNullReturn() throws Throwable {
        try (Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE);RegionLocator locator = TestServerCustomProtocol.util.getConnection().getRegionLocator(TestServerCustomProtocol.TEST_TABLE)) {
            Map<byte[], String> results = hello(table, "nobody", TestServerCustomProtocol.ROW_A, TestServerCustomProtocol.ROW_C);
            verifyRegionResults(locator, results, null, TestServerCustomProtocol.ROW_A);
            verifyRegionResults(locator, results, null, TestServerCustomProtocol.ROW_B);
            verifyRegionResults(locator, results, null, TestServerCustomProtocol.ROW_C);
        }
    }

    @Test
    public void testEmptyReturnType() throws Throwable {
        try (Table table = TestServerCustomProtocol.util.getConnection().getTable(TestServerCustomProtocol.TEST_TABLE)) {
            Map<byte[], String> results = noop(table, TestServerCustomProtocol.ROW_A, TestServerCustomProtocol.ROW_C);
            Assert.assertEquals("Should have results from three regions", 3, results.size());
            // all results should be null
            for (Object v : results.values()) {
                Assert.assertNull(v);
            }
        }
    }
}

