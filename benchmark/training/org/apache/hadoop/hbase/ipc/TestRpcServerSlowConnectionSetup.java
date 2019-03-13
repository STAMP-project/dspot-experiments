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


import AuthMethod.SIMPLE;
import EmptyResponseProto.Builder;
import HConstants.RPC_HEADER;
import TestRpcServiceProtos.TestProtobufRpcProto;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.MetricsConnection;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestProtos.EmptyRequestProto;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestProtos.EmptyResponseProto;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RPCProtos.ConnectionHeader;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RPCProtos.RequestHeader;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RPCProtos.ResponseHeader;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ RPCTests.class, MediumTests.class })
public class TestRpcServerSlowConnectionSetup {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRpcServerSlowConnectionSetup.class);

    private RpcServer server;

    private Socket socket;

    @Parameterized.Parameter
    public Class<? extends RpcServer> rpcServerImpl;

    @Test
    public void test() throws IOException, InterruptedException {
        int rpcHeaderLen = RPC_HEADER.length;
        byte[] preamble = new byte[rpcHeaderLen + 2];
        System.arraycopy(RPC_HEADER, 0, preamble, 0, rpcHeaderLen);
        preamble[rpcHeaderLen] = HConstants.RPC_CURRENT_VERSION;
        preamble[(rpcHeaderLen + 1)] = SIMPLE.code;
        socket.getOutputStream().write(preamble, 0, (rpcHeaderLen + 1));
        socket.getOutputStream().flush();
        Thread.sleep(5000);
        socket.getOutputStream().write(preamble, (rpcHeaderLen + 1), 1);
        socket.getOutputStream().flush();
        ConnectionHeader header = ConnectionHeader.newBuilder().setServiceName(TestProtobufRpcProto.getDescriptor().getFullName()).setVersionInfo(ProtobufUtil.getVersionInfo()).build();
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(header.getSerializedSize());
        header.writeTo(dos);
        dos.flush();
        int callId = 10;
        Call call = new Call(callId, org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestRpcServiceProtos.TestProtobufRpcProto.getDescriptor().findMethodByName("ping"), EmptyRequestProto.getDefaultInstance(), null, EmptyResponseProto.getDefaultInstance(), 1000, HConstants.NORMAL_QOS, null, MetricsConnection.newCallStats());
        RequestHeader requestHeader = IPCUtil.buildRequestHeader(call, null);
        dos.writeInt(IPCUtil.getTotalSizeWhenWrittenDelimited(requestHeader, call.param));
        requestHeader.writeDelimitedTo(dos);
        call.param.writeDelimitedTo(dos);
        dos.flush();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        int size = dis.readInt();
        ResponseHeader responseHeader = ResponseHeader.parseDelimitedFrom(dis);
        Assert.assertEquals(callId, responseHeader.getCallId());
        EmptyResponseProto.Builder builder = EmptyResponseProto.newBuilder();
        builder.mergeDelimitedFrom(dis);
        Assert.assertEquals(size, IPCUtil.getTotalSizeWhenWrittenDelimited(responseHeader, builder.build()));
    }
}

