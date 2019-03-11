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
package org.apache.hadoop.ipc;


import RPC.Server;
import RpcErrorCodeProto.ERROR_APPLICATION;
import TestProtos.SleepRequestProto;
import TestProtos.SleepResponseProto;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.ipc.metrics.RpcMetrics;
import org.apache.hadoop.ipc.protobuf.TestProtos;
import org.apache.hadoop.ipc.protobuf.TestProtos.EchoRequestProto;
import org.apache.hadoop.ipc.protobuf.TestProtos.EchoResponseProto;
import org.apache.hadoop.ipc.protobuf.TestProtos.EmptyRequestProto;
import org.apache.hadoop.ipc.protobuf.TestProtos.EmptyResponseProto;
import org.apache.hadoop.ipc.protobuf.TestRpcServiceProtos.TestProtobufRpc2Proto;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for testing protocol buffer based RPC mechanism.
 * This test depends on test.proto definition of types in src/test/proto
 * and protobuf service definition from src/test/test_rpc_service.proto
 */
public class TestProtoBufRpc extends TestRpcBase {
    private static Server server;

    private static final int SLEEP_DURATION = 1000;

    @ProtocolInfo(protocolName = "testProto2", protocolVersion = 1)
    public interface TestRpcService2 extends TestProtobufRpc2Proto.BlockingInterface {}

    public static class PBServer2Impl implements TestProtoBufRpc.TestRpcService2 {
        @Override
        public EmptyResponseProto ping2(RpcController unused, EmptyRequestProto request) throws ServiceException {
            return EmptyResponseProto.newBuilder().build();
        }

        @Override
        public EchoResponseProto echo2(RpcController unused, EchoRequestProto request) throws ServiceException {
            return EchoResponseProto.newBuilder().setMessage(request.getMessage()).build();
        }

        @Override
        public SleepResponseProto sleep(RpcController controller, TestProtos.SleepRequestProto request) throws ServiceException {
            try {
                Thread.sleep(request.getMilliSeconds());
            } catch (InterruptedException ex) {
            }
            return SleepResponseProto.newBuilder().build();
        }
    }

    @Test(timeout = 5000)
    public void testProtoBufRpc() throws Exception {
        TestRpcBase.TestRpcService client = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
        TestProtoBufRpc.testProtoBufRpc(client);
    }

    @Test(timeout = 5000)
    public void testProtoBufRpc2() throws Exception {
        TestProtoBufRpc.TestRpcService2 client = getClient2();
        // Test ping method
        client.ping2(null, TestRpcBase.newEmptyRequest());
        // Test echo method
        EchoResponseProto echoResponse = client.echo2(null, TestRpcBase.newEchoRequest("hello"));
        Assert.assertEquals(echoResponse.getMessage(), "hello");
        // Ensure RPC metrics are updated
        MetricsRecordBuilder rpcMetrics = MetricsAsserts.getMetrics(TestProtoBufRpc.server.getRpcMetrics().name());
        MetricsAsserts.assertCounterGt("RpcQueueTimeNumOps", 0L, rpcMetrics);
        MetricsAsserts.assertCounterGt("RpcProcessingTimeNumOps", 0L, rpcMetrics);
        MetricsRecordBuilder rpcDetailedMetrics = MetricsAsserts.getMetrics(TestProtoBufRpc.server.getRpcDetailedMetrics().name());
        MetricsAsserts.assertCounterGt("Echo2NumOps", 0L, rpcDetailedMetrics);
    }

    @Test(timeout = 5000)
    public void testProtoBufRandomException() throws Exception {
        TestRpcBase.TestRpcService client = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
        try {
            client.error2(null, TestRpcBase.newEmptyRequest());
        } catch (ServiceException se) {
            Assert.assertTrue(((se.getCause()) instanceof RemoteException));
            RemoteException re = ((RemoteException) (se.getCause()));
            Assert.assertTrue(re.getClassName().equals(URISyntaxException.class.getName()));
            Assert.assertTrue(re.getMessage().contains("testException"));
            Assert.assertTrue(re.getErrorCode().equals(ERROR_APPLICATION));
        }
    }

    @Test(timeout = 6000)
    public void testExtraLongRpc() throws Exception {
        TestProtoBufRpc.TestRpcService2 client = getClient2();
        final String shortString = StringUtils.repeat("X", 4);
        // short message goes through
        EchoResponseProto echoResponse = client.echo2(null, TestRpcBase.newEchoRequest(shortString));
        Assert.assertEquals(shortString, echoResponse.getMessage());
        final String longString = StringUtils.repeat("X", 4096);
        try {
            client.echo2(null, TestRpcBase.newEchoRequest(longString));
            Assert.fail("expected extra-long RPC to fail");
        } catch (ServiceException se) {
            // expected
        }
    }

    @Test(timeout = 12000)
    public void testLogSlowRPC() throws ServiceException, IOException {
        TestProtoBufRpc.TestRpcService2 client = getClient2();
        // make 10 K fast calls
        for (int x = 0; x < 10000; x++) {
            try {
                client.ping2(null, TestRpcBase.newEmptyRequest());
            } catch (Exception ex) {
                throw ex;
            }
        }
        // Ensure RPC metrics are updated
        RpcMetrics rpcMetrics = TestProtoBufRpc.server.getRpcMetrics();
        Assert.assertTrue(((rpcMetrics.getProcessingSampleCount()) > 999L));
        long before = rpcMetrics.getRpcSlowCalls();
        // make a really slow call. Sleep sleeps for 1000ms
        client.sleep(null, TestRpcBase.newSleepRequest(((TestProtoBufRpc.SLEEP_DURATION) * 3)));
        long after = rpcMetrics.getRpcSlowCalls();
        // Ensure slow call is logged.
        Assert.assertEquals((before + 1L), after);
    }

    @Test(timeout = 12000)
    public void testEnsureNoLogIfDisabled() throws ServiceException, IOException {
        // disable slow RPC  logging
        TestProtoBufRpc.server.setLogSlowRPC(false);
        TestProtoBufRpc.TestRpcService2 client = getClient2();
        // make 10 K fast calls
        for (int x = 0; x < 10000; x++) {
            client.ping2(null, TestRpcBase.newEmptyRequest());
        }
        // Ensure RPC metrics are updated
        RpcMetrics rpcMetrics = TestProtoBufRpc.server.getRpcMetrics();
        Assert.assertTrue(((rpcMetrics.getProcessingSampleCount()) > 999L));
        long before = rpcMetrics.getRpcSlowCalls();
        // make a really slow call. Sleep sleeps for 1000ms
        client.sleep(null, TestRpcBase.newSleepRequest(TestProtoBufRpc.SLEEP_DURATION));
        long after = rpcMetrics.getRpcSlowCalls();
        // make sure we never called into Log slow RPC routine.
        Assert.assertEquals(before, after);
    }
}

