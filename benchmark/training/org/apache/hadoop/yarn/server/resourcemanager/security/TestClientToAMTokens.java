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
package org.apache.hadoop.yarn.server.resourcemanager.security;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import TestProtos.EmptyResponseProto;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.ipc.TestRpcBase;
import org.apache.hadoop.ipc.protobuf.TestProtos;
import org.apache.hadoop.ipc.protobuf.TestRpcServiceProtos;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.security.SecurityInfo;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.TokenInfo;
import org.apache.hadoop.security.token.TokenSelector;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenIdentifier;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenSecretManager;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenSelector;
import org.apache.hadoop.yarn.server.resourcemanager.ClientRMService;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRMWithCustomAMLauncher;
import org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestClientToAMTokens extends ParameterizedSchedulerTestBase {
    private YarnConfiguration conf;

    public TestClientToAMTokens(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    @TokenInfo(ClientToAMTokenSelector.class)
    @ProtocolInfo(protocolName = "org.apache.hadoop.yarn.server.resourcemanager.security$CustomProtocol", protocolVersion = 1)
    public interface CustomProtocol extends TestRpcServiceProtos.CustomProto.BlockingInterface {}

    private static class CustomSecurityInfo extends SecurityInfo {
        @Override
        public TokenInfo getTokenInfo(Class<?> protocol, Configuration conf) {
            return new TokenInfo() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public Class<? extends TokenSelector<? extends TokenIdentifier>> value() {
                    return ClientToAMTokenSelector.class;
                }
            };
        }

        @Override
        public KerberosInfo getKerberosInfo(Class<?> protocol, Configuration conf) {
            return null;
        }
    }

    private static class CustomAM extends AbstractService implements TestClientToAMTokens.CustomProtocol {
        private final ApplicationAttemptId appAttemptId;

        private final byte[] secretKey;

        private InetSocketAddress address;

        private boolean pinged = false;

        private ClientToAMTokenSecretManager secretMgr;

        public CustomAM(ApplicationAttemptId appId, byte[] secretKey) {
            super("CustomAM");
            this.appAttemptId = appId;
            this.secretKey = secretKey;
        }

        @Override
        public EmptyResponseProto ping(RpcController unused, TestProtos.EmptyRequestProto request) throws ServiceException {
            this.pinged = true;
            return EmptyResponseProto.newBuilder().build();
        }

        public ClientToAMTokenSecretManager getClientToAMTokenSecretManager() {
            return secretMgr;
        }

        @Override
        protected void serviceStart() throws Exception {
            Configuration conf = getConfig();
            // Set RPC engine to protobuf RPC engine
            RPC.setProtocolEngine(conf, TestClientToAMTokens.CustomProtocol.class, ProtobufRpcEngine.class);
            UserGroupInformation.setConfiguration(conf);
            BlockingService service = TestRpcServiceProtos.CustomProto.newReflectiveBlockingService(this);
            Server server;
            try {
                secretMgr = new ClientToAMTokenSecretManager(this.appAttemptId, secretKey);
                server = setProtocol(TestClientToAMTokens.CustomProtocol.class).setNumHandlers(1).setSecretManager(secretMgr).setInstance(service).build();
            } catch (Exception e) {
                throw new YarnRuntimeException(e);
            }
            server.start();
            this.address = NetUtils.getConnectAddress(server);
            super.serviceStart();
        }

        public void setClientSecretKey(byte[] key) {
            secretMgr.setMasterKey(key);
        }
    }

    @Test
    public void testClientToAMTokens() throws Exception {
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        // Set RPC engine to protobuf RPC engine
        RPC.setProtocolEngine(conf, TestClientToAMTokens.CustomProtocol.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        ContainerManagementProtocol containerManager = Mockito.mock(ContainerManagementProtocol.class);
        StartContainersResponse mockResponse = Mockito.mock(StartContainersResponse.class);
        Mockito.when(containerManager.startContainers(((StartContainersRequest) (ArgumentMatchers.any())))).thenReturn(mockResponse);
        MockRM rm = new MockRMWithCustomAMLauncher(conf, containerManager) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }

            @Override
            protected void doSecureLogin() throws IOException {
            }
        };
        start();
        // Submit an app
        RMApp app = rm.submitApp(1024);
        // Set up a node.
        MockNM nm1 = rm.registerNode("localhost:1234", 3072);
        nm1.nodeHeartbeat(true);
        rm.drainEvents();
        nm1.nodeHeartbeat(true);
        rm.drainEvents();
        ApplicationAttemptId appAttempt = app.getCurrentAppAttempt().getAppAttemptId();
        final MockAM mockAM = new MockAM(getRMContext(), getApplicationMasterService(), app.getCurrentAppAttempt().getAppAttemptId());
        UserGroupInformation appUgi = UserGroupInformation.createRemoteUser(appAttempt.toString());
        RegisterApplicationMasterResponse response = appUgi.doAs(new PrivilegedAction<RegisterApplicationMasterResponse>() {
            @Override
            public RegisterApplicationMasterResponse run() {
                RegisterApplicationMasterResponse response = null;
                try {
                    response = mockAM.registerAppAttempt();
                } catch (Exception e) {
                    Assert.fail("Exception was not expected");
                }
                return response;
            }
        });
        // Get the app-report.
        GetApplicationReportRequest request = Records.newRecord(GetApplicationReportRequest.class);
        request.setApplicationId(app.getApplicationId());
        GetApplicationReportResponse reportResponse = getClientRMService().getApplicationReport(request);
        ApplicationReport appReport = reportResponse.getApplicationReport();
        Token originalClientToAMToken = appReport.getClientToAMToken();
        // ClientToAMToken master key should have been received on register
        // application master response.
        Assert.assertNotNull(response.getClientToAMTokenMasterKey());
        Assert.assertTrue(((response.getClientToAMTokenMasterKey().array().length) > 0));
        // Start the AM with the correct shared-secret.
        ApplicationAttemptId appAttemptId = app.getAppAttempts().keySet().iterator().next();
        Assert.assertNotNull(appAttemptId);
        final TestClientToAMTokens.CustomAM am = new TestClientToAMTokens.CustomAM(appAttemptId, response.getClientToAMTokenMasterKey().array());
        am.init(conf);
        start();
        // Now the real test!
        // Set up clients to be able to pick up correct tokens.
        SecurityUtil.setSecurityInfoProviders(new TestClientToAMTokens.CustomSecurityInfo());
        // Verify denial for unauthenticated user
        try {
            TestClientToAMTokens.CustomProtocol client = RPC.getProxy(TestClientToAMTokens.CustomProtocol.class, 1L, am.address, conf);
            client.ping(null, TestRpcBase.newEmptyRequest());
            Assert.fail("Access by unauthenticated user should fail!!");
        } catch (Exception e) {
            Assert.assertFalse(am.pinged);
        }
        org.apache.hadoop.security.token.Token<ClientToAMTokenIdentifier> token = ConverterUtils.convertFromYarn(originalClientToAMToken, am.address);
        // Verify denial for a malicious user with tampered ID
        verifyTokenWithTamperedID(conf, am, token);
        // Verify denial for a malicious user with tampered user-name
        verifyTokenWithTamperedUserName(conf, am, token);
        // Now for an authenticated user
        verifyValidToken(conf, am, token);
        // Verify for a new version token
        verifyNewVersionToken(conf, am, token, rm);
        stop();
        stop();
    }

    @Test(timeout = 20000)
    public void testClientTokenRace() throws Exception {
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        ContainerManagementProtocol containerManager = Mockito.mock(ContainerManagementProtocol.class);
        StartContainersResponse mockResponse = Mockito.mock(StartContainersResponse.class);
        Mockito.when(containerManager.startContainers(((StartContainersRequest) (ArgumentMatchers.any())))).thenReturn(mockResponse);
        MockRM rm = new MockRMWithCustomAMLauncher(conf, containerManager) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }

            @Override
            protected void doSecureLogin() throws IOException {
            }
        };
        start();
        // Submit an app
        RMApp app = rm.submitApp(1024);
        // Set up a node.
        MockNM nm1 = rm.registerNode("localhost:1234", 3072);
        nm1.nodeHeartbeat(true);
        rm.drainEvents();
        nm1.nodeHeartbeat(true);
        rm.drainEvents();
        ApplicationAttemptId appAttempt = app.getCurrentAppAttempt().getAppAttemptId();
        final MockAM mockAM = new MockAM(getRMContext(), getApplicationMasterService(), app.getCurrentAppAttempt().getAppAttemptId());
        UserGroupInformation appUgi = UserGroupInformation.createRemoteUser(appAttempt.toString());
        RegisterApplicationMasterResponse response = appUgi.doAs(new PrivilegedAction<RegisterApplicationMasterResponse>() {
            @Override
            public RegisterApplicationMasterResponse run() {
                RegisterApplicationMasterResponse response = null;
                try {
                    response = mockAM.registerAppAttempt();
                } catch (Exception e) {
                    Assert.fail("Exception was not expected");
                }
                return response;
            }
        });
        // Get the app-report.
        GetApplicationReportRequest request = Records.newRecord(GetApplicationReportRequest.class);
        request.setApplicationId(app.getApplicationId());
        GetApplicationReportResponse reportResponse = getClientRMService().getApplicationReport(request);
        ApplicationReport appReport = reportResponse.getApplicationReport();
        Token originalClientToAMToken = appReport.getClientToAMToken();
        // ClientToAMToken master key should have been received on register
        // application master response.
        final ByteBuffer clientMasterKey = response.getClientToAMTokenMasterKey();
        Assert.assertNotNull(clientMasterKey);
        Assert.assertTrue(((clientMasterKey.array().length) > 0));
        // Start the AM with the correct shared-secret.
        ApplicationAttemptId appAttemptId = app.getAppAttempts().keySet().iterator().next();
        Assert.assertNotNull(appAttemptId);
        final TestClientToAMTokens.CustomAM am = new TestClientToAMTokens.CustomAM(appAttemptId, null);
        am.init(conf);
        start();
        // Now the real test!
        // Set up clients to be able to pick up correct tokens.
        SecurityUtil.setSecurityInfoProviders(new TestClientToAMTokens.CustomSecurityInfo());
        org.apache.hadoop.security.token.Token<ClientToAMTokenIdentifier> token = ConverterUtils.convertFromYarn(originalClientToAMToken, am.address);
        // Schedule the key to be set after a significant delay
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                am.setClientSecretKey(clientMasterKey.array());
            }
        };
        timer.schedule(timerTask, 250);
        // connect should pause waiting for the master key to arrive
        verifyValidToken(conf, am, token);
        stop();
        stop();
    }
}

