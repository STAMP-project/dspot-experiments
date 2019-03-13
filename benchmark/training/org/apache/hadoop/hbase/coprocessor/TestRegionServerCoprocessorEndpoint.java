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


import DummyRegionServerEndpointProtos.DummyRequest;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import java.io.FileNotFoundException;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyService;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcUtils;
import org.apache.hadoop.hbase.ipc.ServerRpcController;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestRegionServerCoprocessorEndpoint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerCoprocessorEndpoint.class);

    public static final FileNotFoundException WHAT_TO_THROW = new FileNotFoundException("/file.txt");

    private static HBaseTestingUtility TEST_UTIL = null;

    private static Configuration CONF = null;

    private static final String DUMMY_VALUE = "val";

    @Test
    public void testEndpoint() throws Exception {
        final ServerName serverName = TestRegionServerCoprocessorEndpoint.TEST_UTIL.getHBaseCluster().getRegionServer(0).getServerName();
        final ServerRpcController controller = new ServerRpcController();
        final CoprocessorRpcUtils.BlockingRpcCallback<DummyRegionServerEndpointProtos.DummyResponse> rpcCallback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
        DummyRegionServerEndpointProtos.DummyService service = ProtobufUtil.newServiceStub(DummyService.class, TestRegionServerCoprocessorEndpoint.TEST_UTIL.getAdmin().coprocessorService(serverName));
        service.dummyCall(controller, DummyRequest.getDefaultInstance(), rpcCallback);
        Assert.assertEquals(TestRegionServerCoprocessorEndpoint.DUMMY_VALUE, rpcCallback.get().getValue());
        if (controller.failedOnException()) {
            throw controller.getFailedOn();
        }
    }

    @Test
    public void testEndpointExceptions() throws Exception {
        final ServerName serverName = TestRegionServerCoprocessorEndpoint.TEST_UTIL.getHBaseCluster().getRegionServer(0).getServerName();
        final ServerRpcController controller = new ServerRpcController();
        final CoprocessorRpcUtils.BlockingRpcCallback<DummyRegionServerEndpointProtos.DummyResponse> rpcCallback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
        DummyRegionServerEndpointProtos.DummyService service = ProtobufUtil.newServiceStub(DummyService.class, TestRegionServerCoprocessorEndpoint.TEST_UTIL.getAdmin().coprocessorService(serverName));
        service.dummyThrow(controller, DummyRequest.getDefaultInstance(), rpcCallback);
        Assert.assertEquals(null, rpcCallback.get());
        Assert.assertTrue(controller.failedOnException());
        Assert.assertEquals(TestRegionServerCoprocessorEndpoint.WHAT_TO_THROW.getClass().getName().trim(), getClassName().trim());
    }

    public static class DummyRegionServerEndpoint extends DummyService implements RegionServerCoprocessor {
        @Override
        public Iterable<Service> getServices() {
            return Collections.singleton(this);
        }

        @Override
        public void dummyCall(RpcController controller, org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyRequest request, RpcCallback<DummyResponse> callback) {
            callback.run(DummyResponse.newBuilder().setValue(TestRegionServerCoprocessorEndpoint.DUMMY_VALUE).build());
        }

        @Override
        public void dummyThrow(RpcController controller, org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyRequest request, RpcCallback<DummyResponse> done) {
            CoprocessorRpcUtils.setControllerException(controller, TestRegionServerCoprocessorEndpoint.WHAT_TO_THROW);
        }
    }
}

