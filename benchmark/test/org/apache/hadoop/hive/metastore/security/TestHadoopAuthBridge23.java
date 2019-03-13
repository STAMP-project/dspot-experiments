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
package org.apache.hadoop.hive.metastore.security;


import AuthMethod.DIGEST;
import HadoopThriftAuthBridge.Server.ServerMode;
import SaslRpcServer.SASL_DEFAULT_REALM;
import TSaslServerTransport.Factory;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager.DelegationTokenInformation;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.thrift.transport.TSaslServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.junit.Assert;
import org.junit.Test;

import static HadoopThriftAuthBridge.Server.<init>;


public class TestHadoopAuthBridge23 {
    /**
     * set to true when metastore token manager has intitialized token manager
     * through call to HadoopThriftAuthBridge23.Server.startDelegationTokenSecretManager
     */
    static volatile boolean isMetastoreTokenManagerInited;

    public static class MyTokenStore extends MemoryTokenStore {
        static volatile DelegationTokenStore TOKEN_STORE = null;

        public void init(Object hmsHandler, HadoopThriftAuthBridge.Server.ServerMode smode) throws TokenStoreException {
            super.init(hmsHandler, smode);
            TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE = this;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TestHadoopAuthBridge23.isMetastoreTokenManagerInited = true;
        }
    }

    private static class MyHadoopThriftAuthBridge23 extends HadoopThriftAuthBridge23 {
        @Override
        public TestHadoopAuthBridge23.MyHadoopThriftAuthBridge23.Server createServer(String keytabFile, String principalConf, String clientConf) throws TTransportException {
            // Create a Server that doesn't interpret any Kerberos stuff
            return new TestHadoopAuthBridge23.MyHadoopThriftAuthBridge23.Server();
        }

        static class Server extends HadoopThriftAuthBridge.Server {
            public Server() throws TTransportException {
                super();
            }

            @Override
            public TTransportFactory createTransportFactory(Map<String, String> saslProps) throws TTransportException {
                TSaslServerTransport.Factory transFactory = new TSaslServerTransport.Factory();
                transFactory.addServerDefinition(DIGEST.getMechanismName(), null, SASL_DEFAULT_REALM, saslProps, new SaslDigestCallbackHandler(secretManager));
                return new TUGIAssumingTransportFactory(transFactory, realUgi);
            }
        }
    }

    private HiveConf conf;

    /**
     * Test delegation token store/load from shared store.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDelegationTokenSharedStore() throws Exception {
        UserGroupInformation clientUgi = UserGroupInformation.getCurrentUser();
        TokenStoreDelegationTokenSecretManager tokenManager = new TokenStoreDelegationTokenSecretManager(0, ((60 * 60) * 1000), ((60 * 60) * 1000), 0, TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE);
        // initializes current key
        tokenManager.startThreads();
        tokenManager.stopThreads();
        String tokenStrForm = tokenManager.getDelegationToken(clientUgi.getShortUserName(), clientUgi.getShortUserName());
        Token<DelegationTokenIdentifier> t = new Token<DelegationTokenIdentifier>();
        t.decodeFromUrlString(tokenStrForm);
        // check whether the username in the token is what we expect
        DelegationTokenIdentifier d = new DelegationTokenIdentifier();
        d.readFields(new DataInputStream(new ByteArrayInputStream(t.getIdentifier())));
        Assert.assertTrue("Usernames don't match", clientUgi.getShortUserName().equals(d.getUser().getShortUserName()));
        DelegationTokenInformation tokenInfo = TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.getToken(d);
        Assert.assertNotNull("token not in store", tokenInfo);
        Assert.assertFalse("duplicate token add", TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.addToken(d, tokenInfo));
        // check keys are copied from token store when token is loaded
        TokenStoreDelegationTokenSecretManager anotherManager = new TokenStoreDelegationTokenSecretManager(0, 0, 0, 0, TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE);
        Assert.assertEquals("master keys empty on init", 0, anotherManager.getAllKeys().length);
        Assert.assertNotNull("token loaded", anotherManager.retrievePassword(d));
        anotherManager.renewToken(t, clientUgi.getShortUserName());
        Assert.assertEquals("master keys not loaded from store", TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.getMasterKeys().length, anotherManager.getAllKeys().length);
        // cancel the delegation token
        tokenManager.cancelDelegationToken(tokenStrForm);
        Assert.assertNull("token not removed from store after cancel", TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.getToken(d));
        Assert.assertFalse("token removed (again)", TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.removeToken(d));
        try {
            anotherManager.retrievePassword(d);
            Assert.fail("InvalidToken expected after cancel");
        } catch (InvalidToken ex) {
            // expected
        }
        // token expiration
        TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.addToken(d, new DelegationTokenInformation(0, t.getPassword()));
        Assert.assertNotNull(TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.getToken(d));
        anotherManager.removeExpiredTokens();
        Assert.assertNull("Expired token not removed", TestHadoopAuthBridge23.MyTokenStore.TOKEN_STORE.getToken(d));
        // key expiration - create an already expired key
        anotherManager.startThreads();// generates initial key

        anotherManager.stopThreads();
        DelegationKey expiredKey = new DelegationKey((-1), 0, anotherManager.getAllKeys()[0].getKey());
        anotherManager.logUpdateMasterKey(expiredKey);// updates key with sequence number

        Assert.assertTrue("expired key not in allKeys", anotherManager.reloadKeys().containsKey(expiredKey.getKeyId()));
        anotherManager.rollMasterKeyExt();
        Assert.assertFalse("Expired key not removed", anotherManager.reloadKeys().containsKey(expiredKey.getKeyId()));
    }

    @Test
    public void testSaslWithHiveMetaStore() throws Exception {
        setup();
        UserGroupInformation clientUgi = UserGroupInformation.getCurrentUser();
        obtainTokenAndAddIntoUGI(clientUgi, null);
        obtainTokenAndAddIntoUGI(clientUgi, "tokenForFooTablePartition");
    }

    @Test
    public void testMetastoreProxyUser() throws Exception {
        setup();
        final String proxyUserName = "proxyUser";
        // set the configuration up such that proxyUser can act on
        // behalf of all users belonging to the group foo_bar_group (
        // a dummy group)
        String[] groupNames = new String[]{ "foo_bar_group" };
        setGroupsInConf(groupNames, proxyUserName);
        final UserGroupInformation delegationTokenUser = UserGroupInformation.getCurrentUser();
        final UserGroupInformation proxyUserUgi = UserGroupInformation.createRemoteUser(proxyUserName);
        String tokenStrForm = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
            public String run() throws Exception {
                try {
                    // Since the user running the test won't belong to a non-existent group
                    // foo_bar_group, the call to getDelegationTokenStr will fail
                    return getDelegationTokenStr(delegationTokenUser, proxyUserUgi);
                } catch (AuthorizationException ae) {
                    return null;
                }
            }
        });
        Assert.assertTrue("Expected the getDelegationToken call to fail", (tokenStrForm == null));
        // set the configuration up such that proxyUser can act on
        // behalf of all users belonging to the real group(s) that the
        // user running the test belongs to
        setGroupsInConf(UserGroupInformation.getCurrentUser().getGroupNames(), proxyUserName);
        tokenStrForm = proxyUserUgi.doAs(new PrivilegedExceptionAction<String>() {
            public String run() throws Exception {
                try {
                    // Since the user running the test belongs to the group
                    // obtained above the call to getDelegationTokenStr will succeed
                    return getDelegationTokenStr(delegationTokenUser, proxyUserUgi);
                } catch (AuthorizationException ae) {
                    return null;
                }
            }
        });
        Assert.assertTrue("Expected the getDelegationToken call to not fail", (tokenStrForm != null));
        Token<DelegationTokenIdentifier> t = new Token<DelegationTokenIdentifier>();
        t.decodeFromUrlString(tokenStrForm);
        // check whether the username in the token is what we expect
        DelegationTokenIdentifier d = new DelegationTokenIdentifier();
        d.readFields(new DataInputStream(new ByteArrayInputStream(t.getIdentifier())));
        Assert.assertTrue("Usernames don't match", delegationTokenUser.getShortUserName().equals(d.getUser().getShortUserName()));
    }
}

