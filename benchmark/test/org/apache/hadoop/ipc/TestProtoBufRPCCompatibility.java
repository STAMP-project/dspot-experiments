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


import CommonConfigurationKeys.IPC_MAXIMUM_DATA_LENGTH;
import RPC.Server;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.protobuf.TestProtos.EmptyRequestProto;
import org.apache.hadoop.ipc.protobuf.TestProtos.EmptyResponseProto;
import org.apache.hadoop.ipc.protobuf.TestProtos.OptRequestProto;
import org.apache.hadoop.ipc.protobuf.TestProtos.OptResponseProto;
import org.apache.hadoop.ipc.protobuf.TestRpcServiceProtos.NewProtobufRpcProto;
import org.apache.hadoop.ipc.protobuf.TestRpcServiceProtos.NewerProtobufRpcProto;
import org.apache.hadoop.ipc.protobuf.TestRpcServiceProtos.OldProtobufRpcProto;
import org.apache.hadoop.net.NetUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestProtoBufRPCCompatibility {
    private static final String ADDRESS = "0.0.0.0";

    public static final int PORT = 0;

    private static InetSocketAddress addr;

    private static Server server;

    private static Configuration conf;

    @ProtocolInfo(protocolName = "testProto", protocolVersion = 1)
    public interface OldRpcService extends OldProtobufRpcProto.BlockingInterface {}

    @ProtocolInfo(protocolName = "testProto", protocolVersion = 2)
    public interface NewRpcService extends NewProtobufRpcProto.BlockingInterface {}

    @ProtocolInfo(protocolName = "testProto", protocolVersion = 2)
    public interface NewerRpcService extends NewerProtobufRpcProto.BlockingInterface {}

    public static class OldServerImpl implements TestProtoBufRPCCompatibility.OldRpcService {
        @Override
        public EmptyResponseProto ping(RpcController unused, EmptyRequestProto request) throws ServiceException {
            // Ensure clientId is received
            byte[] clientId = Server.getClientId();
            Assert.assertNotNull(Server.getClientId());
            Assert.assertEquals(16, clientId.length);
            return EmptyResponseProto.newBuilder().build();
        }

        @Override
        public EmptyResponseProto echo(RpcController unused, EmptyRequestProto request) throws ServiceException {
            // Ensure clientId is received
            byte[] clientId = Server.getClientId();
            Assert.assertNotNull(Server.getClientId());
            Assert.assertEquals(16, clientId.length);
            return EmptyResponseProto.newBuilder().build();
        }
    }

    public static class NewServerImpl implements TestProtoBufRPCCompatibility.NewRpcService {
        @Override
        public EmptyResponseProto ping(RpcController unused, EmptyRequestProto request) throws ServiceException {
            // Ensure clientId is received
            byte[] clientId = Server.getClientId();
            Assert.assertNotNull(Server.getClientId());
            Assert.assertEquals(16, clientId.length);
            return EmptyResponseProto.newBuilder().build();
        }

        @Override
        public OptResponseProto echo(RpcController unused, OptRequestProto request) throws ServiceException {
            return OptResponseProto.newBuilder().setMessage(request.getMessage()).build();
        }
    }

    @ProtocolInfo(protocolName = "testProto", protocolVersion = 2)
    public static class NewerServerImpl implements TestProtoBufRPCCompatibility.NewerRpcService {
        @Override
        public EmptyResponseProto ping(RpcController unused, EmptyRequestProto request) throws ServiceException {
            // Ensure clientId is received
            byte[] clientId = Server.getClientId();
            Assert.assertNotNull(Server.getClientId());
            Assert.assertEquals(16, clientId.length);
            return EmptyResponseProto.newBuilder().build();
        }

        @Override
        public EmptyResponseProto echo(RpcController unused, EmptyRequestProto request) throws ServiceException {
            // Ensure clientId is received
            byte[] clientId = Server.getClientId();
            Assert.assertNotNull(Server.getClientId());
            Assert.assertEquals(16, clientId.length);
            return EmptyResponseProto.newBuilder().build();
        }
    }

    @Test
    public void testProtocolVersionMismatch() throws ServiceException, IOException {
        TestProtoBufRPCCompatibility.conf = new Configuration();
        TestProtoBufRPCCompatibility.conf.setInt(IPC_MAXIMUM_DATA_LENGTH, 1024);
        // Set RPC engine to protobuf RPC engine
        RPC.setProtocolEngine(TestProtoBufRPCCompatibility.conf, TestProtoBufRPCCompatibility.NewRpcService.class, ProtobufRpcEngine.class);
        // Create server side implementation
        TestProtoBufRPCCompatibility.NewServerImpl serverImpl = new TestProtoBufRPCCompatibility.NewServerImpl();
        BlockingService service = NewProtobufRpcProto.newReflectiveBlockingService(serverImpl);
        // Get RPC server for server side implementation
        TestProtoBufRPCCompatibility.server = setProtocol(TestProtoBufRPCCompatibility.NewRpcService.class).setInstance(service).setBindAddress(TestProtoBufRPCCompatibility.ADDRESS).setPort(TestProtoBufRPCCompatibility.PORT).build();
        TestProtoBufRPCCompatibility.addr = NetUtils.getConnectAddress(TestProtoBufRPCCompatibility.server);
        TestProtoBufRPCCompatibility.server.start();
        RPC.setProtocolEngine(TestProtoBufRPCCompatibility.conf, TestProtoBufRPCCompatibility.OldRpcService.class, ProtobufRpcEngine.class);
        TestProtoBufRPCCompatibility.OldRpcService proxy = RPC.getProxy(TestProtoBufRPCCompatibility.OldRpcService.class, 0, TestProtoBufRPCCompatibility.addr, TestProtoBufRPCCompatibility.conf);
        // Verify that exception is thrown if protocolVersion is mismatch between
        // client and server.
        EmptyRequestProto emptyRequest = EmptyRequestProto.newBuilder().build();
        try {
            proxy.ping(null, emptyRequest);
            Assert.fail("Expected an exception to occur as version mismatch.");
        } catch (Exception e) {
            if (!(e.getMessage().contains("version mismatch"))) {
                // Exception type is not what we expected, re-throw it.
                throw new IOException(e);
            }
        }
        // Verify that missing of optional field is still compatible in RPC call.
        RPC.setProtocolEngine(TestProtoBufRPCCompatibility.conf, TestProtoBufRPCCompatibility.NewerRpcService.class, ProtobufRpcEngine.class);
        TestProtoBufRPCCompatibility.NewerRpcService newProxy = RPC.getProxy(TestProtoBufRPCCompatibility.NewerRpcService.class, 0, TestProtoBufRPCCompatibility.addr, TestProtoBufRPCCompatibility.conf);
        newProxy.echo(null, emptyRequest);
    }
}

