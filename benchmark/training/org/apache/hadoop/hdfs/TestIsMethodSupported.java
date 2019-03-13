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
package org.apache.hadoop.hdfs;


import RPC.RpcKind.RPC_PROTOCOL_BUFFER;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocolPB.ClientDatanodeProtocolTranslatorPB;
import org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolPB;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.protocolPB.InterDatanodeProtocolTranslatorPB;
import org.apache.hadoop.hdfs.protocolPB.JournalProtocolTranslatorPB;
import org.apache.hadoop.hdfs.protocolPB.NamenodeProtocolPB;
import org.apache.hadoop.hdfs.server.protocol.JournalProtocol;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RefreshCallQueueProtocol;
import org.apache.hadoop.ipc.RpcClientUtil;
import org.apache.hadoop.ipc.protocolPB.RefreshCallQueueProtocolClientSideTranslatorPB;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.RefreshUserMappingsProtocol;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.RefreshAuthorizationPolicyProtocol;
import org.apache.hadoop.security.protocolPB.RefreshAuthorizationPolicyProtocolClientSideTranslatorPB;
import org.apache.hadoop.security.protocolPB.RefreshUserMappingsProtocolClientSideTranslatorPB;
import org.apache.hadoop.tools.GetUserMappingsProtocol;
import org.apache.hadoop.tools.protocolPB.GetUserMappingsProtocolClientSideTranslatorPB;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases to verify that client side translators correctly implement the
 * isMethodSupported method in ProtocolMetaInterface.
 */
public class TestIsMethodSupported {
    private static MiniDFSCluster cluster = null;

    private static final HdfsConfiguration conf = new HdfsConfiguration();

    private static InetSocketAddress nnAddress = null;

    private static InetSocketAddress dnAddress = null;

    @Test
    public void testNamenodeProtocol() throws IOException {
        NamenodeProtocol np = NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, NamenodeProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy();
        boolean exists = RpcClientUtil.isMethodSupported(np, NamenodeProtocolPB.class, RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(NamenodeProtocolPB.class), "rollEditLog");
        Assert.assertTrue(exists);
        exists = RpcClientUtil.isMethodSupported(np, NamenodeProtocolPB.class, RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(NamenodeProtocolPB.class), "bogusMethod");
        Assert.assertFalse(exists);
    }

    @Test
    public void testDatanodeProtocol() throws IOException {
        DatanodeProtocolClientSideTranslatorPB translator = new DatanodeProtocolClientSideTranslatorPB(TestIsMethodSupported.nnAddress, TestIsMethodSupported.conf);
        Assert.assertTrue(translator.isMethodSupported("sendHeartbeat"));
    }

    @Test
    public void testClientDatanodeProtocol() throws IOException {
        ClientDatanodeProtocolTranslatorPB translator = new ClientDatanodeProtocolTranslatorPB(TestIsMethodSupported.nnAddress, UserGroupInformation.getCurrentUser(), TestIsMethodSupported.conf, NetUtils.getDefaultSocketFactory(TestIsMethodSupported.conf));
        // Namenode doesn't implement ClientDatanodeProtocol
        Assert.assertFalse(translator.isMethodSupported("refreshNamenodes"));
        translator = new ClientDatanodeProtocolTranslatorPB(TestIsMethodSupported.dnAddress, UserGroupInformation.getCurrentUser(), TestIsMethodSupported.conf, NetUtils.getDefaultSocketFactory(TestIsMethodSupported.conf));
        Assert.assertTrue(translator.isMethodSupported("refreshNamenodes"));
    }

    @Test
    public void testClientNamenodeProtocol() throws IOException {
        ClientProtocol cp = NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, ClientProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy();
        RpcClientUtil.isMethodSupported(cp, ClientNamenodeProtocolPB.class, RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(ClientNamenodeProtocolPB.class), "mkdirs");
    }

    @Test
    public void tesJournalProtocol() throws IOException {
        JournalProtocolTranslatorPB translator = ((JournalProtocolTranslatorPB) (NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, JournalProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy()));
        // Nameode doesn't implement JournalProtocol
        Assert.assertFalse(translator.isMethodSupported("startLogSegment"));
    }

    @Test
    public void testInterDatanodeProtocol() throws IOException {
        InterDatanodeProtocolTranslatorPB translator = new InterDatanodeProtocolTranslatorPB(TestIsMethodSupported.nnAddress, UserGroupInformation.getCurrentUser(), TestIsMethodSupported.conf, NetUtils.getDefaultSocketFactory(TestIsMethodSupported.conf), 0);
        // Not supported at namenode
        Assert.assertFalse(translator.isMethodSupported("initReplicaRecovery"));
        translator = new InterDatanodeProtocolTranslatorPB(TestIsMethodSupported.dnAddress, UserGroupInformation.getCurrentUser(), TestIsMethodSupported.conf, NetUtils.getDefaultSocketFactory(TestIsMethodSupported.conf), 0);
        Assert.assertTrue(translator.isMethodSupported("initReplicaRecovery"));
    }

    @Test
    public void testGetUserMappingsProtocol() throws IOException {
        GetUserMappingsProtocolClientSideTranslatorPB translator = ((GetUserMappingsProtocolClientSideTranslatorPB) (NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, GetUserMappingsProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy()));
        Assert.assertTrue(translator.isMethodSupported("getGroupsForUser"));
    }

    @Test
    public void testRefreshAuthorizationPolicyProtocol() throws IOException {
        RefreshAuthorizationPolicyProtocolClientSideTranslatorPB translator = ((RefreshAuthorizationPolicyProtocolClientSideTranslatorPB) (NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, RefreshAuthorizationPolicyProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy()));
        Assert.assertTrue(translator.isMethodSupported("refreshServiceAcl"));
    }

    @Test
    public void testRefreshUserMappingsProtocol() throws IOException {
        RefreshUserMappingsProtocolClientSideTranslatorPB translator = ((RefreshUserMappingsProtocolClientSideTranslatorPB) (NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, RefreshUserMappingsProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy()));
        Assert.assertTrue(translator.isMethodSupported("refreshUserToGroupsMappings"));
    }

    @Test
    public void testRefreshCallQueueProtocol() throws IOException {
        RefreshCallQueueProtocolClientSideTranslatorPB translator = ((RefreshCallQueueProtocolClientSideTranslatorPB) (NameNodeProxies.createNonHAProxy(TestIsMethodSupported.conf, TestIsMethodSupported.nnAddress, RefreshCallQueueProtocol.class, UserGroupInformation.getCurrentUser(), true).getProxy()));
        Assert.assertTrue(translator.isMethodSupported("refreshCallQueue"));
    }
}

