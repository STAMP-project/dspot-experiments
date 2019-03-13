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
package org.apache.hadoop.hbase.ipc;


import HConstants.CLUSTER_ID_DEFAULT;
import TestProtos.EmptyRequestProto;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestProtos;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestProtos.EchoRequestProto;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestProtos.EchoResponseProto;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test for testing protocol buffer based RPC mechanism. This test depends on test.proto definition
 * of types in <code>src/test/protobuf/test.proto</code> and protobuf service definition from
 * <code>src/test/protobuf/test_rpc_service.proto</code>
 */
@RunWith(Parameterized.class)
@Category({ RPCTests.class, MediumTests.class })
public class TestProtoBufRpc {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProtoBufRpc.class);

    public static final String ADDRESS = "localhost";

    public static int PORT = 0;

    private InetSocketAddress isa;

    private Configuration conf;

    private RpcServerInterface server;

    @Parameterized.Parameter(0)
    public String rpcServerImpl;

    @Test(expected = ServiceException.class)
    public void testProtoBufRpc() throws Exception {
        RpcClient rpcClient = RpcClientFactory.createClient(conf, CLUSTER_ID_DEFAULT);
        try {
            BlockingInterface stub = TestProtobufRpcServiceImpl.newBlockingStub(rpcClient, this.isa);
            // Test ping method
            TestProtos.EmptyRequestProto emptyRequest = EmptyRequestProto.newBuilder().build();
            stub.ping(null, emptyRequest);
            // Test echo method
            EchoRequestProto echoRequest = EchoRequestProto.newBuilder().setMessage("hello").build();
            EchoResponseProto echoResponse = stub.echo(null, echoRequest);
            Assert.assertEquals("hello", echoResponse.getMessage());
            stub.error(null, emptyRequest);
            Assert.fail("Expected exception is not thrown");
        } finally {
            rpcClient.close();
        }
    }
}

