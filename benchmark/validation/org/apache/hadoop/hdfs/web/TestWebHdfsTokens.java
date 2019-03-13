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
package org.apache.hadoop.hdfs.web;


import DFSConfigKeys.DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY;
import DFSConfigKeys.DFS_DATANODE_HTTPS_ADDRESS_KEY;
import DFSConfigKeys.DFS_HTTP_POLICY_KEY;
import DFSConfigKeys.DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY;
import DFSConfigKeys.DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY;
import GetOpParam.Op;
import GetOpParam.Op.GETDELEGATIONTOKEN;
import GetOpParam.Op.OPEN;
import HttpConfig.Policy.HTTPS_ONLY;
import PutOpParam.Op.CANCELDELEGATIONTOKEN;
import PutOpParam.Op.RENEWDELEGATIONTOKEN;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;
import org.apache.hadoop.security.ssl.KeyStoreTestUtil;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static PutOpParam.Op.CANCELDELEGATIONTOKEN;
import static PutOpParam.Op.RENEWDELEGATIONTOKEN;


public class TestWebHdfsTokens {
    private static Configuration conf;

    URI uri = null;

    @Test(timeout = 5000)
    public void testTokenForNonTokenOp() throws IOException {
        WebHdfsFileSystem fs = spyWebhdfsInSecureSetup();
        Token<?> token = Mockito.mock(Token.class);
        Mockito.doReturn(token).when(fs).getDelegationToken(null);
        // should get/set/renew token
        fs.toUrl(OPEN, null);
        Mockito.verify(fs).getDelegationToken();
        Mockito.verify(fs).getDelegationToken(null);
        Mockito.verify(fs).setDelegationToken(token);
        Mockito.reset(fs);
        // should return prior token
        fs.toUrl(OPEN, null);
        Mockito.verify(fs).getDelegationToken();
        Mockito.verify(fs, Mockito.never()).getDelegationToken(null);
        Mockito.verify(fs, Mockito.never()).setDelegationToken(token);
    }

    @Test(timeout = 5000)
    public void testNoTokenForGetToken() throws IOException {
        checkNoTokenForOperation(GETDELEGATIONTOKEN);
    }

    @Test(timeout = 5000)
    public void testNoTokenForRenewToken() throws IOException {
        checkNoTokenForOperation(RENEWDELEGATIONTOKEN);
    }

    @Test(timeout = 5000)
    public void testNoTokenForCancelToken() throws IOException {
        checkNoTokenForOperation(CANCELDELEGATIONTOKEN);
    }

    @Test(timeout = 10000)
    public void testGetOpRequireAuth() {
        for (HttpOpParam.Op op : Op.values()) {
            boolean expect = op == (Op.GETDELEGATIONTOKEN);
            Assert.assertEquals(expect, op.getRequireAuth());
        }
    }

    @Test(timeout = 10000)
    public void testPutOpRequireAuth() {
        for (HttpOpParam.Op op : PutOpParam.Op.values()) {
            boolean expect = (op == (RENEWDELEGATIONTOKEN)) || (op == (CANCELDELEGATIONTOKEN));
            Assert.assertEquals(expect, op.getRequireAuth());
        }
    }

    @Test(timeout = 10000)
    public void testPostOpRequireAuth() {
        for (HttpOpParam.Op op : PostOpParam.Op.values()) {
            Assert.assertFalse(op.getRequireAuth());
        }
    }

    @Test(timeout = 10000)
    public void testDeleteOpRequireAuth() {
        for (HttpOpParam.Op op : DeleteOpParam.Op.values()) {
            Assert.assertFalse(op.getRequireAuth());
        }
    }

    @Test
    public void testLazyTokenFetchForWebhdfs() throws Exception {
        MiniDFSCluster cluster = null;
        WebHdfsFileSystem fs = null;
        try {
            final Configuration clusterConf = new org.apache.hadoop.hdfs.HdfsConfiguration(TestWebHdfsTokens.conf);
            SecurityUtil.setAuthenticationMethod(AuthenticationMethod.SIMPLE, clusterConf);
            clusterConf.setBoolean(DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY, true);
            // trick the NN into thinking security is enabled w/o it trying
            // to login from a keytab
            UserGroupInformation.setConfiguration(clusterConf);
            cluster = new MiniDFSCluster.Builder(clusterConf).numDataNodes(1).build();
            cluster.waitActive();
            SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, clusterConf);
            UserGroupInformation.setConfiguration(clusterConf);
            uri = DFSUtil.createUri("webhdfs", cluster.getNameNode().getHttpAddress());
            validateLazyTokenFetch(clusterConf);
        } finally {
            IOUtils.cleanup(null, fs);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testLazyTokenFetchForSWebhdfs() throws Exception {
        MiniDFSCluster cluster = null;
        SWebHdfsFileSystem fs = null;
        String keystoresDir;
        String sslConfDir;
        try {
            final Configuration clusterConf = new org.apache.hadoop.hdfs.HdfsConfiguration(TestWebHdfsTokens.conf);
            SecurityUtil.setAuthenticationMethod(AuthenticationMethod.SIMPLE, clusterConf);
            clusterConf.setBoolean(DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY, true);
            String baseDir = GenericTestUtils.getTempPath(TestWebHdfsTokens.class.getSimpleName());
            clusterConf.set(DFS_HTTP_POLICY_KEY, HTTPS_ONLY.name());
            clusterConf.set(DFS_NAMENODE_HTTPS_ADDRESS_KEY, "localhost:0");
            clusterConf.set(DFS_DATANODE_HTTPS_ADDRESS_KEY, "localhost:0");
            File base = new File(baseDir);
            FileUtil.fullyDelete(base);
            base.mkdirs();
            keystoresDir = new File(baseDir).getAbsolutePath();
            sslConfDir = KeyStoreTestUtil.getClasspathDir(TestWebHdfsTokens.class);
            KeyStoreTestUtil.setupSSLConfig(keystoresDir, sslConfDir, clusterConf, false);
            clusterConf.set(DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY, KeyStoreTestUtil.getClientSSLConfigFileName());
            clusterConf.set(DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY, KeyStoreTestUtil.getServerSSLConfigFileName());
            // trick the NN into thinking security is enabled w/o it trying
            // to login from a keytab
            UserGroupInformation.setConfiguration(clusterConf);
            cluster = new MiniDFSCluster.Builder(clusterConf).numDataNodes(1).build();
            cluster.waitActive();
            InetSocketAddress addr = cluster.getNameNode().getHttpsAddress();
            String nnAddr = NetUtils.getHostPortString(addr);
            clusterConf.set(DFS_NAMENODE_HTTPS_ADDRESS_KEY, nnAddr);
            SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, clusterConf);
            UserGroupInformation.setConfiguration(clusterConf);
            uri = DFSUtil.createUri("swebhdfs", cluster.getNameNode().getHttpsAddress());
            validateLazyTokenFetch(clusterConf);
        } finally {
            IOUtils.cleanup(null, fs);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
        KeyStoreTestUtil.cleanupSSLConfig(keystoresDir, sslConfDir);
    }

    @Test
    public void testSetTokenServiceAndKind() throws Exception {
        MiniDFSCluster cluster = null;
        try {
            final Configuration clusterConf = new org.apache.hadoop.hdfs.HdfsConfiguration(TestWebHdfsTokens.conf);
            SecurityUtil.setAuthenticationMethod(AuthenticationMethod.SIMPLE, clusterConf);
            clusterConf.setBoolean(DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY, true);
            // trick the NN into thinking s[ecurity is enabled w/o it trying
            // to login from a keytab
            UserGroupInformation.setConfiguration(clusterConf);
            cluster = new MiniDFSCluster.Builder(clusterConf).numDataNodes(0).build();
            cluster.waitActive();
            SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, clusterConf);
            final WebHdfsFileSystem fs = WebHdfsTestUtil.getWebHdfsFileSystem(clusterConf, "webhdfs");
            Whitebox.setInternalState(fs, "canRefreshDelegationToken", true);
            URLConnectionFactory factory = new URLConnectionFactory(new ConnectionConfigurator() {
                @Override
                public HttpURLConnection configure(HttpURLConnection conn) throws IOException {
                    return conn;
                }
            }) {
                @Override
                public URLConnection openConnection(URL url) throws IOException {
                    return super.openConnection(new URL((url + "&service=foo&kind=bar")));
                }
            };
            Whitebox.setInternalState(fs, "connectionFactory", factory);
            Token<?> token1 = fs.getDelegationToken();
            Assert.assertEquals(new Text("bar"), token1.getKind());
            final HttpOpParam.Op op = Op.GETDELEGATIONTOKEN;
            Token<DelegationTokenIdentifier> token2 = fs.new FsPathResponseRunner<Token<DelegationTokenIdentifier>>(op, null, new RenewerParam(null)) {
                @Override
                Token<DelegationTokenIdentifier> decodeResponse(Map<?, ?> json) throws IOException {
                    return JsonUtilClient.toDelegationToken(json);
                }
            }.run();
            Assert.assertEquals(new Text("bar"), token2.getKind());
            Assert.assertEquals(new Text("foo"), token2.getService());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

