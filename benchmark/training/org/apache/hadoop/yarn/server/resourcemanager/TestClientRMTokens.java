/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager;


import AuthenticationMethod.KERBEROS;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import YarnConfiguration.DEFAULT_RM_ADDRESS;
import YarnConfiguration.RM_ADDRESS;
import YarnConfiguration.RM_PRINCIPAL;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.RMDelegationTokenIdentifierData;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestClientRMTokens {
    private static final Logger LOG = LoggerFactory.getLogger(TestClientRMTokens.class);

    @Test
    public void testDelegationToken() throws IOException, InterruptedException {
        final YarnConfiguration conf = new YarnConfiguration();
        conf.set(RM_PRINCIPAL, "testuser/localhost@apache.org");
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        ResourceScheduler scheduler = TestClientRMTokens.createMockScheduler(conf);
        long initialInterval = 10000L;
        long maxLifetime = 20000L;
        long renewInterval = 10000L;
        RMDelegationTokenSecretManager rmDtSecretManager = TestClientRMTokens.createRMDelegationTokenSecretManager(initialInterval, maxLifetime, renewInterval);
        rmDtSecretManager.startThreads();
        TestClientRMTokens.LOG.info(((((("Creating DelegationTokenSecretManager with initialInterval: " + initialInterval) + ", maxLifetime: ") + maxLifetime) + ", renewInterval: ") + renewInterval));
        final ClientRMService clientRMService = new TestClientRMTokens.ClientRMServiceForTest(conf, scheduler, rmDtSecretManager);
        clientRMService.init(conf);
        clientRMService.start();
        ApplicationClientProtocol clientRMWithDT = null;
        try {
            // Create a user for the renewr and fake the authentication-method
            UserGroupInformation loggedInUser = UserGroupInformation.createRemoteUser("testrenewer@APACHE.ORG");
            Assert.assertEquals("testrenewer", loggedInUser.getShortUserName());
            // Default realm is APACHE.ORG
            loggedInUser.setAuthenticationMethod(KERBEROS);
            Token token = getDelegationToken(loggedInUser, clientRMService, loggedInUser.getShortUserName());
            long tokenFetchTime = System.currentTimeMillis();
            TestClientRMTokens.LOG.info(("Got delegation token at: " + tokenFetchTime));
            // Now try talking to RMService using the delegation token
            clientRMWithDT = getClientRMProtocolWithDT(token, clientRMService.getBindAddress(), "loginuser1", conf);
            GetNewApplicationRequest request = Records.newRecord(GetNewApplicationRequest.class);
            try {
                clientRMWithDT.getNewApplication(request);
            } catch (IOException e) {
                Assert.fail(("Unexpected exception" + e));
            } catch (YarnException e) {
                Assert.fail(("Unexpected exception" + e));
            }
            // Renew after 50% of token age.
            while ((System.currentTimeMillis()) < (tokenFetchTime + (initialInterval / 2))) {
                Thread.sleep(500L);
            } 
            long nextExpTime = renewDelegationToken(loggedInUser, clientRMService, token);
            long renewalTime = System.currentTimeMillis();
            TestClientRMTokens.LOG.info(((("Renewed token at: " + renewalTime) + ", NextExpiryTime: ") + nextExpTime));
            // Wait for first expiry, but before renewed expiry.
            while (((System.currentTimeMillis()) > (tokenFetchTime + initialInterval)) && ((System.currentTimeMillis()) < nextExpTime)) {
                Thread.sleep(500L);
            } 
            Thread.sleep(50L);
            // Valid token because of renewal.
            try {
                clientRMWithDT.getNewApplication(request);
            } catch (IOException e) {
                Assert.fail(("Unexpected exception" + e));
            } catch (YarnException e) {
                Assert.fail(("Unexpected exception" + e));
            }
            // Wait for expiry.
            while ((System.currentTimeMillis()) < (renewalTime + renewInterval)) {
                Thread.sleep(500L);
            } 
            Thread.sleep(50L);
            TestClientRMTokens.LOG.info((("At time: " + (System.currentTimeMillis())) + ", token should be invalid"));
            // Token should have expired.
            try {
                clientRMWithDT.getNewApplication(request);
                Assert.fail("Should not have succeeded with an expired token");
            } catch (Exception e) {
                Assert.assertEquals(InvalidToken.class.getName(), e.getClass().getName());
                Assert.assertTrue(e.getMessage().contains("is expired"));
            }
            // Test cancellation
            // Stop the existing proxy, start another.
            if (clientRMWithDT != null) {
                RPC.stopProxy(clientRMWithDT);
                clientRMWithDT = null;
            }
            token = getDelegationToken(loggedInUser, clientRMService, loggedInUser.getShortUserName());
            tokenFetchTime = System.currentTimeMillis();
            TestClientRMTokens.LOG.info(("Got delegation token at: " + tokenFetchTime));
            // Now try talking to RMService using the delegation token
            clientRMWithDT = getClientRMProtocolWithDT(token, clientRMService.getBindAddress(), "loginuser2", conf);
            request = Records.newRecord(GetNewApplicationRequest.class);
            try {
                clientRMWithDT.getNewApplication(request);
            } catch (IOException e) {
                Assert.fail(("Unexpected exception" + e));
            } catch (YarnException e) {
                Assert.fail(("Unexpected exception" + e));
            }
            cancelDelegationToken(loggedInUser, clientRMService, token);
            if (clientRMWithDT != null) {
                RPC.stopProxy(clientRMWithDT);
                clientRMWithDT = null;
            }
            // Creating a new connection.
            clientRMWithDT = getClientRMProtocolWithDT(token, clientRMService.getBindAddress(), "loginuser2", conf);
            TestClientRMTokens.LOG.info(("Cancelled delegation token at: " + (System.currentTimeMillis())));
            // Verify cancellation worked.
            try {
                clientRMWithDT.getNewApplication(request);
                Assert.fail("Should not have succeeded with a cancelled delegation token");
            } catch (IOException e) {
            } catch (YarnException e) {
            }
            // Test new version token
            // Stop the existing proxy, start another.
            if (clientRMWithDT != null) {
                RPC.stopProxy(clientRMWithDT);
                clientRMWithDT = null;
            }
            token = getDelegationToken(loggedInUser, clientRMService, loggedInUser.getShortUserName());
            byte[] tokenIdentifierContent = token.getIdentifier().array();
            RMDelegationTokenIdentifier tokenIdentifier = new RMDelegationTokenIdentifier();
            DataInputBuffer dib = new DataInputBuffer();
            dib.reset(tokenIdentifierContent, tokenIdentifierContent.length);
            tokenIdentifier.readFields(dib);
            // Construct new version RMDelegationTokenIdentifier with additional field
            RMDelegationTokenIdentifierForTest newVersionTokenIdentifier = new RMDelegationTokenIdentifierForTest(tokenIdentifier, "message");
            org.apache.hadoop.security.token.Token<RMDelegationTokenIdentifier> newRMDTtoken = new org.apache.hadoop.security.token.Token<RMDelegationTokenIdentifier>(newVersionTokenIdentifier, rmDtSecretManager);
            Token newToken = BuilderUtils.newDelegationToken(newRMDTtoken.getIdentifier(), newRMDTtoken.getKind().toString(), newRMDTtoken.getPassword(), newRMDTtoken.getService().toString());
            // Now try talking to RMService using the new version delegation token
            clientRMWithDT = getClientRMProtocolWithDT(newToken, clientRMService.getBindAddress(), "loginuser3", conf);
            request = Records.newRecord(GetNewApplicationRequest.class);
            try {
                clientRMWithDT.getNewApplication(request);
            } catch (IOException e) {
                Assert.fail(("Unexpected exception" + e));
            } catch (YarnException e) {
                Assert.fail(("Unexpected exception" + e));
            }
        } finally {
            rmDtSecretManager.stopThreads();
            // TODO PRECOMMIT Close proxies.
            if (clientRMWithDT != null) {
                RPC.stopProxy(clientRMWithDT);
            }
        }
    }

    @Test
    public void testShortCircuitRenewCancel() throws IOException, InterruptedException {
        InetSocketAddress addr = NetUtils.createSocketAddr(InetAddress.getLocalHost().getHostName(), 123, null);
        checkShortCircuitRenewCancel(addr, addr, true);
    }

    @Test
    public void testShortCircuitRenewCancelWildcardAddress() throws IOException, InterruptedException {
        InetSocketAddress rmAddr = new InetSocketAddress(123);
        InetSocketAddress serviceAddr = NetUtils.createSocketAddr(InetAddress.getLocalHost().getHostName(), rmAddr.getPort(), null);
        checkShortCircuitRenewCancel(rmAddr, serviceAddr, true);
    }

    @Test
    public void testShortCircuitRenewCancelSameHostDifferentPort() throws IOException, InterruptedException {
        InetSocketAddress rmAddr = NetUtils.createSocketAddr(InetAddress.getLocalHost().getHostName(), 123, null);
        checkShortCircuitRenewCancel(rmAddr, new InetSocketAddress(rmAddr.getAddress(), ((rmAddr.getPort()) + 1)), false);
    }

    @Test
    public void testShortCircuitRenewCancelDifferentHostSamePort() throws IOException, InterruptedException {
        InetSocketAddress rmAddr = NetUtils.createSocketAddr(InetAddress.getLocalHost().getHostName(), 123, null);
        checkShortCircuitRenewCancel(rmAddr, new InetSocketAddress("1.1.1.1", rmAddr.getPort()), false);
    }

    @Test
    public void testShortCircuitRenewCancelDifferentHostDifferentPort() throws IOException, InterruptedException {
        InetSocketAddress rmAddr = NetUtils.createSocketAddr(InetAddress.getLocalHost().getHostName(), 123, null);
        checkShortCircuitRenewCancel(rmAddr, new InetSocketAddress("1.1.1.1", ((rmAddr.getPort()) + 1)), false);
    }

    @Test
    public void testReadOldFormatFields() throws IOException {
        RMDelegationTokenIdentifier token = new RMDelegationTokenIdentifier(new Text("alice"), new Text("bob"), new Text("colin"));
        token.setIssueDate(123);
        token.setMasterKeyId(321);
        token.setMaxDate(314);
        token.setSequenceNumber(12345);
        DataInputBuffer inBuf = new DataInputBuffer();
        DataOutputBuffer outBuf = new DataOutputBuffer();
        token.writeInOldFormat(outBuf);
        outBuf.writeLong(42);// renewDate

        inBuf.reset(outBuf.getData(), 0, outBuf.getLength());
        RMDelegationTokenIdentifier identifier = null;
        try {
            RMDelegationTokenIdentifierData identifierData = new RMDelegationTokenIdentifierData();
            identifierData.readFields(inBuf);
            Assert.fail((("Should have thrown a " + (InvalidProtocolBufferException.class.getName())) + " because the token is not a protobuf"));
        } catch (InvalidProtocolBufferException e) {
            identifier = new RMDelegationTokenIdentifier();
            inBuf.reset();
            identifier.readFieldsInOldFormat(inBuf);
            Assert.assertEquals(42, inBuf.readLong());
        }
        Assert.assertEquals("alice", identifier.getUser().getUserName());
        Assert.assertEquals(new Text("bob"), identifier.getRenewer());
        Assert.assertEquals("colin", identifier.getUser().getRealUser().getUserName());
        Assert.assertEquals(123, identifier.getIssueDate());
        Assert.assertEquals(321, identifier.getMasterKeyId());
        Assert.assertEquals(314, identifier.getMaxDate());
        Assert.assertEquals(12345, identifier.getSequenceNumber());
    }

    @SuppressWarnings("rawtypes")
    public static class YarnBadRPC extends YarnRPC {
        @Override
        public Object getProxy(Class protocol, InetSocketAddress addr, Configuration conf) {
            throw new RuntimeException("getProxy");
        }

        @Override
        public void stopProxy(Object proxy, Configuration conf) {
            throw new RuntimeException("stopProxy");
        }

        @Override
        public Server getServer(Class protocol, Object instance, InetSocketAddress addr, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, int numHandlers, String portRangeConfig) {
            throw new RuntimeException("getServer");
        }
    }

    class ClientRMServiceForTest extends ClientRMService {
        public ClientRMServiceForTest(Configuration conf, ResourceScheduler scheduler, RMDelegationTokenSecretManager rmDTSecretManager) {
            super(Mockito.mock(RMContext.class), scheduler, Mockito.mock(RMAppManager.class), new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager(scheduler, conf), rmDTSecretManager);
        }

        // Use a random port unless explicitly specified.
        @Override
        InetSocketAddress getBindAddress(Configuration conf) {
            return conf.getSocketAddr(RM_ADDRESS, DEFAULT_RM_ADDRESS, 0);
        }

        @Override
        protected void serviceStop() throws Exception {
            if ((rmDTSecretManager) != null) {
                rmDTSecretManager.stopThreads();
            }
            super.serviceStop();
        }
    }
}

