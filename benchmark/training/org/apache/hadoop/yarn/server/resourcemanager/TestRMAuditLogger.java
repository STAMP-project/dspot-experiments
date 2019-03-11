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
package org.apache.hadoop.yarn.server.resourcemanager;


import ClientId.BYTE_LENGTH;
import Keys.APPID;
import Keys.OPERATION;
import Keys.TARGET;
import Keys.USER;
import TestProtocol.versionID;
import TestProtos.EmptyRequestProto;
import TestProtos.EmptyResponseProto;
import TestRpcBase.PBServerImpl;
import TestRpcServiceProtos.TestProtobufRpcProto;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.ipc.TestRpcBase;
import org.apache.hadoop.ipc.TestRpcBase.TestRpcService;
import org.apache.hadoop.ipc.protobuf.TestProtos;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link RMAuditLogger}.
 */
public class TestRMAuditLogger {
    private static final String USER = "test";

    private static final String OPERATION = "oper";

    private static final String TARGET = "tgt";

    private static final String PERM = "admin group";

    private static final String DESC = "description of an audit log";

    private static final String QUEUE = "root";

    private static final ApplicationId APPID = Mockito.mock(ApplicationId.class);

    private static final ApplicationAttemptId ATTEMPTID = Mockito.mock(ApplicationAttemptId.class);

    private static final ContainerId CONTAINERID = Mockito.mock(ContainerId.class);

    private static final Resource RESOURCE = Mockito.mock(Resource.class);

    private static final String CALLER_CONTEXT = "context";

    private static final byte[] CALLER_SIGNATURE = "signature".getBytes();

    private static final String PARTITION = "label1";

    /**
     * Test the AuditLog format with key-val pair.
     */
    @Test
    public void testKeyValLogFormat() throws Exception {
        StringBuilder actLog = new StringBuilder();
        StringBuilder expLog = new StringBuilder();
        // add the first k=v pair and check
        RMAuditLogger.start(Keys.USER, TestRMAuditLogger.USER, actLog);
        expLog.append("USER=test");
        Assert.assertEquals(expLog.toString(), actLog.toString());
        // append another k1=v1 pair to already added k=v and test
        RMAuditLogger.add(Keys.OPERATION, TestRMAuditLogger.OPERATION, actLog);
        expLog.append("\tOPERATION=oper");
        Assert.assertEquals(expLog.toString(), actLog.toString());
        // append another k1=null pair and test
        RMAuditLogger.add(Keys.APPID, ((String) (null)), actLog);
        expLog.append("\tAPPID=null");
        Assert.assertEquals(expLog.toString(), actLog.toString());
        // now add the target and check of the final string
        RMAuditLogger.add(Keys.TARGET, TestRMAuditLogger.TARGET, actLog);
        expLog.append("\tTARGET=tgt");
        Assert.assertEquals(expLog.toString(), actLog.toString());
    }

    /**
     * Test {@link RMAuditLogger} without IP set.
     */
    @Test
    public void testRMAuditLoggerWithoutIP() throws Exception {
        // test without ip
        testSuccessLogFormat(false);
        testFailureLogFormat(false);
    }

    /**
     * A special extension of {@link TestImpl} RPC server with
     * {@link TestImpl#ping()} testing the audit logs.
     */
    private class MyTestRPCServer extends TestRpcBase.PBServerImpl {
        @Override
        public EmptyResponseProto ping(RpcController unused, TestProtos.EmptyRequestProto request) throws ServiceException {
            // Ensure clientId is received
            byte[] clientId = Server.getClientId();
            Assert.assertNotNull(clientId);
            Assert.assertEquals(BYTE_LENGTH, clientId.length);
            // test with ip set
            testSuccessLogFormat(true);
            testFailureLogFormat(true);
            return EmptyResponseProto.newBuilder().build();
        }
    }

    /**
     * Test {@link RMAuditLogger} with IP set.
     */
    @Test
    public void testRMAuditLoggerWithIP() throws Exception {
        Configuration conf = new Configuration();
        RPC.setProtocolEngine(conf, TestRpcService.class, ProtobufRpcEngine.class);
        // Create server side implementation
        TestRMAuditLogger.MyTestRPCServer serverImpl = new TestRMAuditLogger.MyTestRPCServer();
        BlockingService service = TestProtobufRpcProto.newReflectiveBlockingService(serverImpl);
        // start the IPC server
        Server server = setProtocol(TestRpcService.class).setInstance(service).setBindAddress("0.0.0.0").setPort(0).setNumHandlers(5).setVerbose(true).build();
        server.start();
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        // Make a client connection and test the audit log
        TestRpcService proxy = RPC.getProxy(TestRpcService.class, versionID, addr, conf);
        // Start the testcase
        TestProtos.EmptyRequestProto pingRequest = EmptyRequestProto.newBuilder().build();
        proxy.ping(null, pingRequest);
        server.stop();
        RPC.stopProxy(proxy);
    }
}

