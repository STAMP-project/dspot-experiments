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
package org.apache.hadoop.hdfs.server.common;


import DFSConfigKeys.FS_DEFAULT_NAME_KEY;
import DFSConfigKeys.HADOOP_SECURITY_AUTHENTICATION;
import HdfsServerConstants.ReplicaState;
import JspHelper.DELEGATION_PARAMETER_NAME;
import JspHelper.NAMENODE_ADDRESS;
import NameNodeHttpServer.NAMENODE_ADDRESS_ATTRIBUTE_KEY;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.security.authorize.DefaultImpersonationProvider;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJspHelper {
    private final Configuration conf = new HdfsConfiguration();

    public static class DummySecretManager extends AbstractDelegationTokenSecretManager<DelegationTokenIdentifier> {
        public DummySecretManager(long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval) {
            super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
        }

        @Override
        public DelegationTokenIdentifier createIdentifier() {
            return null;
        }

        @Override
        public byte[] createPassword(DelegationTokenIdentifier dtId) {
            return new byte[1];
        }
    }

    @Test
    public void testGetUgi() throws IOException {
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:4321/");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ServletContext context = Mockito.mock(ServletContext.class);
        String user = "TheDoctor";
        Text userText = new Text(user);
        DelegationTokenIdentifier dtId = new DelegationTokenIdentifier(userText, userText, null);
        Token<DelegationTokenIdentifier> token = new Token<DelegationTokenIdentifier>(dtId, new TestJspHelper.DummySecretManager(0, 0, 0, 0));
        String tokenString = token.encodeToUrlString();
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        Mockito.when(request.getRemoteUser()).thenReturn(user);
        // Test attribute in the url to be used as service in the token.
        Mockito.when(request.getParameter(NAMENODE_ADDRESS)).thenReturn("1.1.1.1:1111");
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        verifyServiceInToken(context, request, "1.1.1.1:1111");
        // Test attribute name.node.address
        // Set the nnaddr url parameter to null.
        token.decodeIdentifier().clearCache();
        Mockito.when(request.getParameter(NAMENODE_ADDRESS)).thenReturn(null);
        InetSocketAddress addr = new InetSocketAddress("localhost", 2222);
        Mockito.when(context.getAttribute(NAMENODE_ADDRESS_ATTRIBUTE_KEY)).thenReturn(addr);
        verifyServiceInToken(context, request, ((addr.getAddress().getHostAddress()) + ":2222"));
        // Test service already set in the token and DN doesn't change service
        // when it doesn't know the NN service addr
        userText = new Text((user + "2"));
        dtId = new DelegationTokenIdentifier(userText, userText, null);
        token = new Token<DelegationTokenIdentifier>(dtId, new TestJspHelper.DummySecretManager(0, 0, 0, 0));
        token.setService(new Text("3.3.3.3:3333"));
        tokenString = token.encodeToUrlString();
        // Set the name.node.address attribute in Servlet context to null
        Mockito.when(context.getAttribute(NAMENODE_ADDRESS_ATTRIBUTE_KEY)).thenReturn(null);
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        verifyServiceInToken(context, request, "3.3.3.3:3333");
    }

    @Test
    public void testGetUgiFromToken() throws IOException {
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:4321/");
        ServletContext context = Mockito.mock(ServletContext.class);
        String realUser = "TheDoctor";
        String user = "TheNurse";
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi;
        HttpServletRequest request;
        Text ownerText = new Text(user);
        DelegationTokenIdentifier dtId = new DelegationTokenIdentifier(ownerText, ownerText, new Text(realUser));
        Token<DelegationTokenIdentifier> token = new Token<DelegationTokenIdentifier>(dtId, new TestJspHelper.DummySecretManager(0, 0, 0, 0));
        String tokenString = token.encodeToUrlString();
        // token with no auth-ed user
        request = getMockRequest(null, null, null);
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getRealUser().getShortUserName(), realUser);
        Assert.assertEquals(ugi.getShortUserName(), user);
        checkUgiFromToken(ugi);
        // token with auth-ed user
        request = getMockRequest(realUser, null, null);
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getRealUser().getShortUserName(), realUser);
        Assert.assertEquals(ugi.getShortUserName(), user);
        checkUgiFromToken(ugi);
        // completely different user, token trumps auth
        request = getMockRequest("rogue", null, null);
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getRealUser().getShortUserName(), realUser);
        Assert.assertEquals(ugi.getShortUserName(), user);
        checkUgiFromToken(ugi);
        // expected case
        request = getMockRequest(null, user, null);
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getRealUser().getShortUserName(), realUser);
        Assert.assertEquals(ugi.getShortUserName(), user);
        checkUgiFromToken(ugi);
        // can't proxy with a token!
        request = getMockRequest(null, null, "rogue");
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals(("Usernames not matched: name=rogue != expected=" + user), ioe.getMessage());
        }
        // can't proxy with a token!
        request = getMockRequest(null, user, "rogue");
        Mockito.when(request.getParameter(DELEGATION_PARAMETER_NAME)).thenReturn(tokenString);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals(("Usernames not matched: name=rogue != expected=" + user), ioe.getMessage());
        }
    }

    @Test
    public void testGetNonProxyUgi() throws IOException {
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:4321/");
        ServletContext context = Mockito.mock(ServletContext.class);
        String realUser = "TheDoctor";
        String user = "TheNurse";
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi;
        HttpServletRequest request;
        // have to be auth-ed with remote user
        request = getMockRequest(null, null, null);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals("Security enabled but user not authenticated by filter", ioe.getMessage());
        }
        request = getMockRequest(null, realUser, null);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals("Security enabled but user not authenticated by filter", ioe.getMessage());
        }
        // ugi for remote user
        request = getMockRequest(realUser, null, null);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getShortUserName(), realUser);
        checkUgiFromAuth(ugi);
        // ugi for remote user = real user
        request = getMockRequest(realUser, realUser, null);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getShortUserName(), realUser);
        checkUgiFromAuth(ugi);
        // ugi for remote user != real user
        request = getMockRequest(realUser, user, null);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals(((("Usernames not matched: name=" + user) + " != expected=") + realUser), ioe.getMessage());
        }
    }

    @Test
    public void testGetProxyUgi() throws IOException {
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:4321/");
        ServletContext context = Mockito.mock(ServletContext.class);
        String realUser = "TheDoctor";
        String user = "TheNurse";
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(realUser), "*");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(realUser), "*");
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi;
        HttpServletRequest request;
        // have to be auth-ed with remote user
        request = getMockRequest(null, null, user);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals("Security enabled but user not authenticated by filter", ioe.getMessage());
        }
        request = getMockRequest(null, realUser, user);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals("Security enabled but user not authenticated by filter", ioe.getMessage());
        }
        // proxy ugi for user via remote user
        request = getMockRequest(realUser, null, user);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getRealUser().getShortUserName(), realUser);
        Assert.assertEquals(ugi.getShortUserName(), user);
        checkUgiFromAuth(ugi);
        // proxy ugi for user vi a remote user = real user
        request = getMockRequest(realUser, realUser, user);
        ugi = JspHelper.getUGI(context, request, conf);
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertEquals(ugi.getRealUser().getShortUserName(), realUser);
        Assert.assertEquals(ugi.getShortUserName(), user);
        checkUgiFromAuth(ugi);
        // proxy ugi for user via remote user != real user
        request = getMockRequest(realUser, user, user);
        try {
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad request allowed");
        } catch (IOException ioe) {
            Assert.assertEquals(((("Usernames not matched: name=" + user) + " != expected=") + realUser), ioe.getMessage());
        }
        // try to get get a proxy user with unauthorized user
        try {
            request = getMockRequest(user, null, realUser);
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad proxy request allowed");
        } catch (AuthorizationException ae) {
            Assert.assertEquals(((("User: " + user) + " is not allowed to impersonate ") + realUser), ae.getMessage());
        }
        try {
            request = getMockRequest(user, user, realUser);
            JspHelper.getUGI(context, request, conf);
            Assert.fail("bad proxy request allowed");
        } catch (AuthorizationException ae) {
            Assert.assertEquals(((("User: " + user) + " is not allowed to impersonate ") + realUser), ae.getMessage());
        }
    }

    @Test
    public void testReadWriteReplicaState() {
        try {
            DataOutputBuffer out = new DataOutputBuffer();
            DataInputBuffer in = new DataInputBuffer();
            for (HdfsServerConstants.ReplicaState repState : ReplicaState.values()) {
                repState.write(out);
                in.reset(out.getData(), out.getLength());
                HdfsServerConstants.ReplicaState result = ReplicaState.read(in);
                Assert.assertTrue("testReadWrite error !!!", (repState == result));
                out.reset();
                in.reset();
            }
        } catch (Exception ex) {
            Assert.fail("testReadWrite ex error ReplicaState");
        }
    }

    private static String clientAddr = "1.1.1.1";

    private static String chainedClientAddr = (TestJspHelper.clientAddr) + ", 2.2.2.2";

    private static String proxyAddr = "3.3.3.3";

    @Test
    public void testRemoteAddr() {
        Assert.assertEquals(TestJspHelper.clientAddr, getRemoteAddr(TestJspHelper.clientAddr, null, false));
    }

    @Test
    public void testRemoteAddrWithUntrustedProxy() {
        Assert.assertEquals(TestJspHelper.proxyAddr, getRemoteAddr(TestJspHelper.clientAddr, TestJspHelper.proxyAddr, false));
    }

    @Test
    public void testRemoteAddrWithTrustedProxy() {
        Assert.assertEquals(TestJspHelper.clientAddr, getRemoteAddr(TestJspHelper.clientAddr, TestJspHelper.proxyAddr, true));
        Assert.assertEquals(TestJspHelper.clientAddr, getRemoteAddr(TestJspHelper.chainedClientAddr, TestJspHelper.proxyAddr, true));
    }

    @Test
    public void testRemoteAddrWithTrustedProxyAndEmptyClient() {
        Assert.assertEquals(TestJspHelper.proxyAddr, getRemoteAddr(null, TestJspHelper.proxyAddr, true));
        Assert.assertEquals(TestJspHelper.proxyAddr, getRemoteAddr("", TestJspHelper.proxyAddr, true));
    }
}

