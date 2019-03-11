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
package org.apache.hadoop.security;


import AuthenticationMethod.KERBEROS;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.ipc.TestRpcBase;
import org.apache.hadoop.security.authorize.DefaultImpersonationProvider;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test do as effective user.
 */
public class TestDoAsEffectiveUser extends TestRpcBase {
    private static final String REAL_USER_NAME = "realUser1@HADOOP.APACHE.ORG";

    private static final String REAL_USER_SHORT_NAME = "realUser1";

    private static final String PROXY_USER_NAME = "proxyUser";

    private static final String GROUP1_NAME = "group1";

    private static final String GROUP2_NAME = "group2";

    private static final String[] GROUP_NAMES = new String[]{ TestDoAsEffectiveUser.GROUP1_NAME, TestDoAsEffectiveUser.GROUP2_NAME };

    private TestRpcBase.TestRpcService client;

    private static final Configuration masterConf = new Configuration();

    public static final Logger LOG = LoggerFactory.getLogger(TestDoAsEffectiveUser.class);

    static {
        TestDoAsEffectiveUser.masterConf.set(HADOOP_SECURITY_AUTH_TO_LOCAL, ("RULE:[2:$1@$0](.*@HADOOP.APACHE.ORG)s/@.*//" + ("RULE:[1:$1@$0](.*@HADOOP.APACHE.ORG)s/@.*//" + "DEFAULT")));
    }

    /**
     * Test method for
     * {@link org.apache.hadoop.security.UserGroupInformation#createProxyUser(java.lang.String, org.apache.hadoop.security.UserGroupInformation)}
     * .
     */
    @Test
    public void testCreateProxyUser() throws Exception {
        // ensure that doAs works correctly
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUser(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi);
        UserGroupInformation curUGI = proxyUserUgi.doAs(new PrivilegedExceptionAction<UserGroupInformation>() {
            @Override
            public UserGroupInformation run() throws IOException {
                return UserGroupInformation.getCurrentUser();
            }
        });
        Assert.assertEquals(((((TestDoAsEffectiveUser.PROXY_USER_NAME) + " (auth:PROXY) via ") + (TestDoAsEffectiveUser.REAL_USER_NAME)) + " (auth:SIMPLE)"), curUGI.toString());
    }

    @Test(timeout = 4000)
    public void testRealUserSetup() throws IOException {
        final Configuration conf = new Configuration();
        conf.setStrings(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestDoAsEffectiveUser.REAL_USER_SHORT_NAME), "group1");
        configureSuperUserIPAddresses(conf, TestDoAsEffectiveUser.REAL_USER_SHORT_NAME);
        // Set RPC engine to protobuf RPC engine
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 5);
        refreshConf(conf);
        try {
            UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
            checkRemoteUgi(realUserUgi, conf);
            UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi, TestDoAsEffectiveUser.GROUP_NAMES);
            checkRemoteUgi(proxyUserUgi, conf);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            TestRpcBase.stop(server, client);
        }
    }

    @Test(timeout = 4000)
    public void testRealUserAuthorizationSuccess() throws IOException {
        final Configuration conf = new Configuration();
        configureSuperUserIPAddresses(conf, TestDoAsEffectiveUser.REAL_USER_SHORT_NAME);
        conf.setStrings(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestDoAsEffectiveUser.REAL_USER_SHORT_NAME), "group1");
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 5);
        refreshConf(conf);
        try {
            UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
            checkRemoteUgi(realUserUgi, conf);
            UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi, TestDoAsEffectiveUser.GROUP_NAMES);
            checkRemoteUgi(proxyUserUgi, conf);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            TestRpcBase.stop(server, client);
        }
    }

    /* Tests authorization of superuser's ip. */
    @Test
    public void testRealUserIPAuthorizationFailure() throws IOException {
        final Configuration conf = new Configuration();
        conf.setStrings(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestDoAsEffectiveUser.REAL_USER_SHORT_NAME), "20.20.20.20");// Authorized IP address

        conf.setStrings(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestDoAsEffectiveUser.REAL_USER_SHORT_NAME), "group1");
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 5);
        refreshConf(conf);
        try {
            UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
            UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi, TestDoAsEffectiveUser.GROUP_NAMES);
            String retVal = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws ServiceException {
                    client = TestRpcBase.getClient(TestRpcBase.addr, conf);
                    return client.getCurrentUser(null, TestRpcBase.newEmptyRequest()).getUser();
                }
            });
            Assert.fail(("The RPC must have failed " + retVal));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TestRpcBase.stop(server, client);
        }
    }

    @Test
    public void testRealUserIPNotSpecified() throws IOException {
        final Configuration conf = new Configuration();
        conf.setStrings(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestDoAsEffectiveUser.REAL_USER_SHORT_NAME), "group1");
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 2);
        refreshConf(conf);
        try {
            UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
            UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi, TestDoAsEffectiveUser.GROUP_NAMES);
            String retVal = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws ServiceException {
                    client = TestRpcBase.getClient(TestRpcBase.addr, conf);
                    return client.getCurrentUser(null, TestRpcBase.newEmptyRequest()).getUser();
                }
            });
            Assert.fail(("The RPC must have failed " + retVal));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TestRpcBase.stop(server, client);
        }
    }

    @Test
    public void testRealUserGroupNotSpecified() throws IOException {
        final Configuration conf = new Configuration();
        configureSuperUserIPAddresses(conf, TestDoAsEffectiveUser.REAL_USER_SHORT_NAME);
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 2);
        try {
            UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
            UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi, TestDoAsEffectiveUser.GROUP_NAMES);
            String retVal = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws ServiceException {
                    client = TestRpcBase.getClient(TestRpcBase.addr, conf);
                    return client.getCurrentUser(null, TestRpcBase.newEmptyRequest()).getUser();
                }
            });
            Assert.fail(("The RPC must have failed " + retVal));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TestRpcBase.stop(server, client);
        }
    }

    @Test
    public void testRealUserGroupAuthorizationFailure() throws IOException {
        final Configuration conf = new Configuration();
        configureSuperUserIPAddresses(conf, TestDoAsEffectiveUser.REAL_USER_SHORT_NAME);
        conf.setStrings(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestDoAsEffectiveUser.REAL_USER_SHORT_NAME), "group3");
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 2);
        refreshConf(conf);
        try {
            UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
            UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, realUserUgi, TestDoAsEffectiveUser.GROUP_NAMES);
            String retVal = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws ServiceException {
                    client = TestRpcBase.getClient(TestRpcBase.addr, conf);
                    return client.getCurrentUser(null, TestRpcBase.newEmptyRequest()).getUser();
                }
            });
            Assert.fail(("The RPC must have failed " + retVal));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TestRpcBase.stop(server, client);
        }
    }

    /* Tests the scenario when token authorization is used.
     The server sees only the the owner of the token as the
     user.
     */
    @Test
    public void testProxyWithToken() throws Exception {
        final Configuration conf = new Configuration(TestDoAsEffectiveUser.masterConf);
        TestRpcBase.TestTokenSecretManager sm = new TestRpcBase.TestTokenSecretManager();
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        RPC.setProtocolEngine(conf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(conf);
        final Server server = TestRpcBase.setupTestServer(conf, 5, sm);
        final UserGroupInformation current = UserGroupInformation.createRemoteUser(TestDoAsEffectiveUser.REAL_USER_NAME);
        TestRpcBase.TestTokenIdentifier tokenId = new TestRpcBase.TestTokenIdentifier(new Text(current.getUserName()), new Text("SomeSuperUser"));
        Token<TestRpcBase.TestTokenIdentifier> token = new Token(tokenId, sm);
        SecurityUtil.setTokenService(token, TestRpcBase.addr);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestDoAsEffectiveUser.PROXY_USER_NAME, current, TestDoAsEffectiveUser.GROUP_NAMES);
        proxyUserUgi.addToken(token);
        refreshConf(conf);
        String retVal = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                try {
                    client = TestRpcBase.getClient(TestRpcBase.addr, conf);
                    return client.getCurrentUser(null, TestRpcBase.newEmptyRequest()).getUser();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    TestRpcBase.stop(server, client);
                }
            }
        });
        // The user returned by server must be the one in the token.
        Assert.assertEquals(((TestDoAsEffectiveUser.REAL_USER_NAME) + " (auth:TOKEN) via SomeSuperUser (auth:SIMPLE)"), retVal);
    }

    /* The user gets the token via a superuser. Server should authenticate
    this user.
     */
    @Test
    public void testTokenBySuperUser() throws Exception {
        TestRpcBase.TestTokenSecretManager sm = new TestRpcBase.TestTokenSecretManager();
        final Configuration newConf = new Configuration(TestDoAsEffectiveUser.masterConf);
        SecurityUtil.setAuthenticationMethod(KERBEROS, newConf);
        // Set RPC engine to protobuf RPC engine
        RPC.setProtocolEngine(newConf, TestRpcBase.TestRpcService.class, ProtobufRpcEngine.class);
        UserGroupInformation.setConfiguration(newConf);
        final Server server = TestRpcBase.setupTestServer(newConf, 5, sm);
        final UserGroupInformation current = UserGroupInformation.createUserForTesting(TestDoAsEffectiveUser.REAL_USER_NAME, TestDoAsEffectiveUser.GROUP_NAMES);
        refreshConf(newConf);
        TestRpcBase.TestTokenIdentifier tokenId = new TestRpcBase.TestTokenIdentifier(new Text(current.getUserName()), new Text("SomeSuperUser"));
        Token<TestRpcBase.TestTokenIdentifier> token = new Token(tokenId, sm);
        SecurityUtil.setTokenService(token, TestRpcBase.addr);
        current.addToken(token);
        String retVal = current.doAs(new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                try {
                    client = TestRpcBase.getClient(TestRpcBase.addr, newConf);
                    return client.getCurrentUser(null, TestRpcBase.newEmptyRequest()).getUser();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    TestRpcBase.stop(server, client);
                }
            }
        });
        String expected = (TestDoAsEffectiveUser.REAL_USER_NAME) + " (auth:TOKEN) via SomeSuperUser (auth:SIMPLE)";
        Assert.assertEquals(((retVal + "!=") + expected), expected, retVal);
    }
}

