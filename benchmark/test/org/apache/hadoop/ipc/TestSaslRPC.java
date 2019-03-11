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


import Client.LOG;
import CommonConfigurationKeys.IPC_CLIENT_PING_KEY;
import CommonConfigurationKeys.IPC_PING_INTERVAL_DEFAULT;
import CommonConfigurationKeys.IPC_PING_INTERVAL_KEY;
import CommonConfigurationKeysPublic.HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY;
import CommonConfigurationKeysPublic.IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY;
import QualityOfProtection.AUTHENTICATION;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.Client.ConnectionId;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.SaslRpcServer.QualityOfProtection;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.apache.hadoop.ipc.TestRpcBase.TestTokenIdentifier.<init>;


/**
 * Unit tests for using Sasl over RPC.
 */
@RunWith(Parameterized.class)
public class TestSaslRPC extends TestRpcBase {
    QualityOfProtection[] qop;

    QualityOfProtection expectedQop;

    String saslPropertiesResolver;

    public TestSaslRPC(QualityOfProtection[] qop, QualityOfProtection expectedQop, String saslPropertiesResolver) {
        this.qop = qop;
        this.expectedQop = expectedQop;
        this.saslPropertiesResolver = saslPropertiesResolver;
    }

    public static final Logger LOG = LoggerFactory.getLogger(TestSaslRPC.class);

    static final String ERROR_MESSAGE = "Token is invalid";

    static final String SERVER_KEYTAB_KEY = "test.ipc.server.keytab";

    static final String SERVER_PRINCIPAL_1 = "p1/foo@BAR";

    // If this is set to true AND the auth-method is not simple, secretManager
    // will be enabled.
    static Boolean enableSecretManager = null;

    // If this is set to true, secretManager will be forecefully enabled
    // irrespective of auth-method.
    static Boolean forceSecretManager = null;

    static Boolean clientFallBackToSimpleAllowed = true;

    enum UseToken {

        NONE(),
        VALID(),
        INVALID(),
        OTHER();}

    static {
        GenericTestUtils.setLogLevel(Client.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(Server.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(SaslRpcClient.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(SaslRpcServer.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(SaslInputStream.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(SecurityUtil.LOG, Level.TRACE);
    }

    public static class BadTokenSecretManager extends TestRpcBase.TestTokenSecretManager {
        @Override
        public byte[] retrievePassword(TestRpcBase.TestTokenIdentifier id) throws InvalidToken {
            throw new InvalidToken(TestSaslRPC.ERROR_MESSAGE);
        }
    }

    public static class CustomSecurityInfo extends SecurityInfo {
        @Override
        public KerberosInfo getKerberosInfo(Class<?> protocol, Configuration conf) {
            return new KerberosInfo() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String serverPrincipal() {
                    return TestRpcBase.SERVER_PRINCIPAL_KEY;
                }

                @Override
                public String clientPrincipal() {
                    return null;
                }
            };
        }

        @Override
        public TokenInfo getTokenInfo(Class<?> protocol, Configuration conf) {
            return new TokenInfo() {
                @Override
                public Class<? extends TokenSelector<? extends TokenIdentifier>> value() {
                    return TestRpcBase.TestTokenSelector.class;
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }
            };
        }
    }

    @Test
    public void testDigestRpc() throws Exception {
        TestRpcBase.TestTokenSecretManager sm = new TestRpcBase.TestTokenSecretManager();
        final Server server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5, sm);
        doDigestRpc(server, sm);
    }

    @Test
    public void testDigestRpcWithoutAnnotation() throws Exception {
        TestRpcBase.TestTokenSecretManager sm = new TestRpcBase.TestTokenSecretManager();
        try {
            SecurityUtil.setSecurityInfoProviders(new TestSaslRPC.CustomSecurityInfo());
            final Server server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5, sm);
            doDigestRpc(server, sm);
        } finally {
            SecurityUtil.setSecurityInfoProviders();
        }
    }

    @Test
    public void testErrorMessage() throws Exception {
        TestSaslRPC.BadTokenSecretManager sm = new TestSaslRPC.BadTokenSecretManager();
        final Server server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5, sm);
        boolean succeeded = false;
        try {
            doDigestRpc(server, sm);
        } catch (ServiceException e) {
            Assert.assertTrue(((e.getCause()) instanceof RemoteException));
            RemoteException re = ((RemoteException) (e.getCause()));
            TestSaslRPC.LOG.info(("LOGGING MESSAGE: " + (re.getLocalizedMessage())));
            Assert.assertEquals(TestSaslRPC.ERROR_MESSAGE, re.getLocalizedMessage());
            Assert.assertTrue(((re.unwrapRemoteException()) instanceof InvalidToken));
            succeeded = true;
        }
        Assert.assertTrue(succeeded);
    }

    @Test
    public void testPingInterval() throws Exception {
        Configuration newConf = new Configuration(TestRpcBase.conf);
        newConf.set(TestRpcBase.SERVER_PRINCIPAL_KEY, TestSaslRPC.SERVER_PRINCIPAL_1);
        TestRpcBase.conf.setInt(IPC_PING_INTERVAL_KEY, IPC_PING_INTERVAL_DEFAULT);
        // set doPing to true
        newConf.setBoolean(IPC_CLIENT_PING_KEY, true);
        ConnectionId remoteId = ConnectionId.getConnectionId(new InetSocketAddress(0), TestRpcBase.TestRpcService.class, null, 0, null, newConf);
        Assert.assertEquals(IPC_PING_INTERVAL_DEFAULT, remoteId.getPingInterval());
        // set doPing to false
        newConf.setBoolean(IPC_CLIENT_PING_KEY, false);
        remoteId = ConnectionId.getConnectionId(new InetSocketAddress(0), TestRpcBase.TestRpcService.class, null, 0, null, newConf);
        Assert.assertEquals(0, remoteId.getPingInterval());
    }

    @Test
    public void testPerConnectionConf() throws Exception {
        TestRpcBase.TestTokenSecretManager sm = new TestRpcBase.TestTokenSecretManager();
        final Server server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5, sm);
        final UserGroupInformation current = UserGroupInformation.getCurrentUser();
        final InetSocketAddress addr = NetUtils.getConnectAddress(server);
        TestRpcBase.TestTokenIdentifier tokenId = new TestRpcBase.TestTokenIdentifier(new org.apache.hadoop.io.Text(current.getUserName()));
        Token<TestRpcBase.TestTokenIdentifier> token = new Token(tokenId, sm);
        SecurityUtil.setTokenService(token, addr);
        current.addToken(token);
        Configuration newConf = new Configuration(TestRpcBase.conf);
        newConf.set(HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY, "");
        Client client = null;
        TestRpcBase.TestRpcService proxy1 = null;
        TestRpcBase.TestRpcService proxy2 = null;
        TestRpcBase.TestRpcService proxy3 = null;
        int[] timeouts = new int[]{ 111222, 3333333 };
        try {
            newConf.setInt(IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY, timeouts[0]);
            proxy1 = TestRpcBase.getClient(addr, newConf);
            proxy1.getAuthMethod(null, TestRpcBase.newEmptyRequest());
            client = ProtobufRpcEngine.getClient(newConf);
            Set<ConnectionId> conns = client.getConnectionIds();
            Assert.assertEquals("number of connections in cache is wrong", 1, conns.size());
            // same conf, connection should be re-used
            proxy2 = TestRpcBase.getClient(addr, newConf);
            proxy2.getAuthMethod(null, TestRpcBase.newEmptyRequest());
            Assert.assertEquals("number of connections in cache is wrong", 1, conns.size());
            // different conf, new connection should be set up
            newConf.setInt(IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY, timeouts[1]);
            proxy3 = TestRpcBase.getClient(addr, newConf);
            proxy3.getAuthMethod(null, TestRpcBase.newEmptyRequest());
            Assert.assertEquals("number of connections in cache is wrong", 2, conns.size());
            // now verify the proxies have the correct connection ids and timeouts
            ConnectionId[] connsArray = new ConnectionId[]{ RPC.getConnectionIdForProxy(proxy1), RPC.getConnectionIdForProxy(proxy2), RPC.getConnectionIdForProxy(proxy3) };
            Assert.assertEquals(connsArray[0], connsArray[1]);
            Assert.assertEquals(connsArray[0].getMaxIdleTime(), timeouts[0]);
            Assert.assertFalse(connsArray[0].equals(connsArray[2]));
            Assert.assertNotSame(connsArray[2].getMaxIdleTime(), timeouts[1]);
        } finally {
            server.stop();
            // this is dirty, but clear out connection cache for next run
            if (client != null) {
                client.getConnectionIds().clear();
            }
            if (proxy1 != null)
                RPC.stopProxy(proxy1);

            if (proxy2 != null)
                RPC.stopProxy(proxy2);

            if (proxy3 != null)
                RPC.stopProxy(proxy3);

        }
    }

    @Test
    public void testSaslPlainServer() throws IOException {
        runNegotiation(new TestSaslRPC.TestPlainCallbacks.Client("user", "pass"), new TestSaslRPC.TestPlainCallbacks.Server("user", "pass"));
    }

    @Test
    public void testSaslPlainServerBadPassword() {
        SaslException e = null;
        try {
            runNegotiation(new TestSaslRPC.TestPlainCallbacks.Client("user", "pass1"), new TestSaslRPC.TestPlainCallbacks.Server("user", "pass2"));
        } catch (SaslException se) {
            e = se;
        }
        Assert.assertNotNull(e);
        String message = e.getMessage();
        assertContains("PLAIN auth failed", message);
        assertContains("wrong password", message);
    }

    static class TestPlainCallbacks {
        public static class Client implements CallbackHandler {
            String user = null;

            String password = null;

            Client(String user, String password) {
                this.user = user;
                this.password = password;
            }

            @Override
            public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
                for (Callback callback : callbacks) {
                    if (callback instanceof NameCallback) {
                        ((NameCallback) (callback)).setName(user);
                    } else
                        if (callback instanceof PasswordCallback) {
                            ((PasswordCallback) (callback)).setPassword(password.toCharArray());
                        } else {
                            throw new UnsupportedCallbackException(callback, "Unrecognized SASL PLAIN Callback");
                        }

                }
            }
        }

        public static class Server implements CallbackHandler {
            String user = null;

            String password = null;

            Server(String user, String password) {
                this.user = user;
                this.password = password;
            }

            @Override
            public void handle(Callback[] callbacks) throws UnsupportedCallbackException, SaslException {
                NameCallback nc = null;
                PasswordCallback pc = null;
                AuthorizeCallback ac = null;
                for (Callback callback : callbacks) {
                    if (callback instanceof NameCallback) {
                        nc = ((NameCallback) (callback));
                        Assert.assertEquals(user, nc.getName());
                    } else
                        if (callback instanceof PasswordCallback) {
                            pc = ((PasswordCallback) (callback));
                            if (!(password.equals(new String(pc.getPassword())))) {
                                throw new IllegalArgumentException("wrong password");
                            }
                        } else
                            if (callback instanceof AuthorizeCallback) {
                                ac = ((AuthorizeCallback) (callback));
                                Assert.assertEquals(user, ac.getAuthorizationID());
                                Assert.assertEquals(user, ac.getAuthenticationID());
                                ac.setAuthorized(true);
                                ac.setAuthorizedID(ac.getAuthenticationID());
                            } else {
                                throw new UnsupportedCallbackException(callback, "Unsupported SASL PLAIN Callback");
                            }


                }
                Assert.assertNotNull(nc);
                Assert.assertNotNull(pc);
                Assert.assertNotNull(ac);
            }
        }
    }

    private static Pattern BadToken = Pattern.compile(".*DIGEST-MD5: digest response format violation.*");

    private static Pattern KrbFailed = Pattern.compile((".*Failed on local exception:.* " + "Failed to specify server's Kerberos principal name.*"));

    private static Pattern NoTokenAuth = Pattern.compile((".*IllegalArgumentException: " + "TOKEN authentication requires a secret manager"));

    private static Pattern NoFallback = Pattern.compile((".*Failed on local exception:.* " + ("Server asks us to fall back to SIMPLE auth, " + "but this client is configured to only allow secure connections.*")));

    /* simple server */
    @Test
    public void testSimpleServer() throws Exception {
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.OTHER));
        // SASL methods are normally reverted to SIMPLE
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.OTHER));
    }

    @Test
    public void testNoClientFallbackToSimple() throws Exception {
        TestSaslRPC.clientFallBackToSimpleAllowed = false;
        // tokens are irrelevant w/o secret manager enabled
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.INVALID));
        // A secure client must not fallback
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoFallback, getAuthMethod(KERBEROS, SIMPLE));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoFallback, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoFallback, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoFallback, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.INVALID));
        // Now set server to simple and also force the secret-manager. Now server
        // should have both simple and token enabled.
        TestSaslRPC.forceSecretManager = true;
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.INVALID));
        // A secure client must not fallback
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoFallback, getAuthMethod(KERBEROS, SIMPLE));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoFallback, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.INVALID));
        // doesn't try SASL
        TestSaslRPC.assertAuthEquals(TestSaslRPC.Denied(SIMPLE), getAuthMethod(SIMPLE, TOKEN));
        // does try SASL
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN), getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN), getAuthMethod(KERBEROS, TOKEN));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN), getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.INVALID));
    }

    @Test
    public void testSimpleServerWithTokens() throws Exception {
        // Client not using tokens
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE));
        // SASL methods are reverted to SIMPLE
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE));
        // Use tokens. But tokens are ignored because client is reverted to simple
        // due to server not using tokens
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.OTHER));
        // server isn't really advertising tokens
        TestSaslRPC.enableSecretManager = true;
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.OTHER));
        // now the simple server takes tokens
        TestSaslRPC.forceSecretManager = true;
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.OTHER));
    }

    @Test
    public void testSimpleServerWithInvalidTokens() throws Exception {
        // Tokens are ignored because client is reverted to simple
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.enableSecretManager = true;
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(SIMPLE, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.forceSecretManager = true;
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(SIMPLE, SIMPLE, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(KERBEROS, SIMPLE, TestSaslRPC.UseToken.INVALID));
    }

    /* token server */
    @Test
    public void testTokenOnlyServer() throws Exception {
        // simple client w/o tokens won't try SASL, so server denies
        TestSaslRPC.assertAuthEquals(TestSaslRPC.Denied(SIMPLE), getAuthMethod(SIMPLE, TOKEN));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN), getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.OTHER));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN), getAuthMethod(KERBEROS, TOKEN));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN), getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.OTHER));
    }

    @Test
    public void testTokenOnlyServerWithTokens() throws Exception {
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.enableSecretManager = false;
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoTokenAuth, getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoTokenAuth, getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.VALID));
    }

    @Test
    public void testTokenOnlyServerWithInvalidTokens() throws Exception {
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.enableSecretManager = false;
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoTokenAuth, getAuthMethod(SIMPLE, TOKEN, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.NoTokenAuth, getAuthMethod(KERBEROS, TOKEN, TestSaslRPC.UseToken.INVALID));
    }

    /* kerberos server */
    @Test
    public void testKerberosServer() throws Exception {
        // doesn't try SASL
        TestSaslRPC.assertAuthEquals(TestSaslRPC.Denied(SIMPLE), getAuthMethod(SIMPLE, KERBEROS));
        // does try SASL
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(TOKEN, KERBEROS), getAuthMethod(SIMPLE, KERBEROS, TestSaslRPC.UseToken.OTHER));
        // no tgt
        TestSaslRPC.assertAuthEquals(TestSaslRPC.KrbFailed, getAuthMethod(KERBEROS, KERBEROS));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.KrbFailed, getAuthMethod(KERBEROS, KERBEROS, TestSaslRPC.UseToken.OTHER));
    }

    @Test
    public void testKerberosServerWithTokens() throws Exception {
        // can use tokens regardless of auth
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(SIMPLE, KERBEROS, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TOKEN, getAuthMethod(KERBEROS, KERBEROS, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.enableSecretManager = false;
        // shouldn't even try token because server didn't tell us to
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(KERBEROS), getAuthMethod(SIMPLE, KERBEROS, TestSaslRPC.UseToken.VALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.KrbFailed, getAuthMethod(KERBEROS, KERBEROS, TestSaslRPC.UseToken.VALID));
    }

    @Test
    public void testKerberosServerWithInvalidTokens() throws Exception {
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(SIMPLE, KERBEROS, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.BadToken, getAuthMethod(KERBEROS, KERBEROS, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.enableSecretManager = false;
        TestSaslRPC.assertAuthEquals(TestSaslRPC.No(KERBEROS), getAuthMethod(SIMPLE, KERBEROS, TestSaslRPC.UseToken.INVALID));
        TestSaslRPC.assertAuthEquals(TestSaslRPC.KrbFailed, getAuthMethod(KERBEROS, KERBEROS, TestSaslRPC.UseToken.INVALID));
    }

    // ensure that for all qop settings, client can handle postponed rpc
    // responses.  basically ensures that the rpc server isn't encrypting
    // and queueing the responses out of order.
    @Test(timeout = 10000)
    public void testSaslResponseOrdering() throws Exception {
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.TOKEN, TestRpcBase.conf);
        UserGroupInformation.setConfiguration(TestRpcBase.conf);
        TestRpcBase.TestTokenSecretManager sm = new TestRpcBase.TestTokenSecretManager();
        Server server = TestRpcBase.setupTestServer(TestRpcBase.conf, 1, sm);
        try {
            final InetSocketAddress addr = NetUtils.getConnectAddress(server);
            final UserGroupInformation clientUgi = UserGroupInformation.createRemoteUser("client");
            clientUgi.setAuthenticationMethod(AuthenticationMethod.TOKEN);
            TestRpcBase.TestTokenIdentifier tokenId = new TestRpcBase.TestTokenIdentifier(new org.apache.hadoop.io.Text(clientUgi.getUserName()));
            Token<?> token = new Token(tokenId, sm);
            SecurityUtil.setTokenService(token, addr);
            clientUgi.addToken(token);
            clientUgi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    final TestRpcBase.TestRpcService proxy = TestRpcBase.getClient(addr, TestRpcBase.conf);
                    final ExecutorService executor = Executors.newCachedThreadPool();
                    final AtomicInteger count = new AtomicInteger();
                    try {
                        // queue up a bunch of futures for postponed calls serviced
                        // in a random order.
                        Future<?>[] futures = new Future<?>[10];
                        for (int i = 0; i < (futures.length); i++) {
                            futures[i] = executor.submit(new Callable<Void>() {
                                @Override
                                public Void call() throws Exception {
                                    String expect = "future" + (count.getAndIncrement());
                                    String answer = TestRpcBase.convert(proxy.echoPostponed(null, TestRpcBase.newEchoRequest(expect)));
                                    Assert.assertEquals(expect, answer);
                                    return null;
                                }
                            });
                            try {
                                // ensures the call is initiated and the response is blocked.
                                futures[i].get(100, TimeUnit.MILLISECONDS);
                            } catch (TimeoutException te) {
                                continue;// expected.

                            }
                            Assert.fail((("future" + i) + " did not block"));
                        }
                        // triggers responses to be unblocked in a random order.  having
                        // only 1 handler ensures that the prior calls are already
                        // postponed.  1 handler also ensures that this call will
                        // timeout if the postponing doesn't work (ie. free up handler)
                        proxy.sendPostponed(null, TestRpcBase.newEmptyRequest());
                        for (int i = 0; i < (futures.length); i++) {
                            TestSaslRPC.LOG.info(("waiting for future" + i));
                            futures[i].get();
                        }
                    } finally {
                        RPC.stopProxy(proxy);
                        executor.shutdownNow();
                    }
                    return null;
                }
            });
        } finally {
            server.stop();
        }
    }

    /* Class used to test overriding QOP values using SaslPropertiesResolver */
    static class AuthSaslPropertiesResolver extends SaslPropertiesResolver {
        @Override
        public Map<String, String> getServerProperties(InetAddress address) {
            Map<String, String> newPropertes = new HashMap<String, String>(getDefaultProperties());
            newPropertes.put(Sasl.QOP, AUTHENTICATION.getSaslQop());
            return newPropertes;
        }
    }
}

