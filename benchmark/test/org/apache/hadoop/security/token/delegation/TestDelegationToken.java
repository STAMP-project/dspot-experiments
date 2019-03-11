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
package org.apache.hadoop.security.token.delegation;


import AuthenticationMethod.PROXY;
import AuthenticationMethod.TOKEN;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager.DelegationTokenInformation;
import org.apache.hadoop.util.Daemon;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDelegationToken {
    private static final Logger LOG = LoggerFactory.getLogger(TestDelegationToken.class);

    private static final Text KIND = new Text("MY KIND");

    public static class TestDelegationTokenIdentifier extends AbstractDelegationTokenIdentifier implements Writable {
        public TestDelegationTokenIdentifier() {
        }

        public TestDelegationTokenIdentifier(Text owner, Text renewer, Text realUser) {
            super(owner, renewer, realUser);
        }

        @Override
        public Text getKind() {
            return TestDelegationToken.KIND;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            super.write(out);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            super.readFields(in);
        }
    }

    public static class TestDelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<TestDelegationToken.TestDelegationTokenIdentifier> {
        public boolean isStoreNewMasterKeyCalled = false;

        public boolean isRemoveStoredMasterKeyCalled = false;

        public boolean isStoreNewTokenCalled = false;

        public boolean isRemoveStoredTokenCalled = false;

        public boolean isUpdateStoredTokenCalled = false;

        public TestDelegationTokenSecretManager(long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval) {
            super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
        }

        @Override
        public TestDelegationToken.TestDelegationTokenIdentifier createIdentifier() {
            return new TestDelegationToken.TestDelegationTokenIdentifier();
        }

        @Override
        protected byte[] createPassword(TestDelegationToken.TestDelegationTokenIdentifier t) {
            return super.createPassword(t);
        }

        @Override
        protected void storeNewMasterKey(DelegationKey key) throws IOException {
            isStoreNewMasterKeyCalled = true;
            super.storeNewMasterKey(key);
        }

        @Override
        protected void removeStoredMasterKey(DelegationKey key) {
            isRemoveStoredMasterKeyCalled = true;
            Assert.assertFalse(key.equals(allKeys.get(currentId)));
        }

        @Override
        protected void storeNewToken(TestDelegationToken.TestDelegationTokenIdentifier ident, long renewDate) throws IOException {
            super.storeNewToken(ident, renewDate);
            isStoreNewTokenCalled = true;
        }

        @Override
        protected void removeStoredToken(TestDelegationToken.TestDelegationTokenIdentifier ident) throws IOException {
            super.removeStoredToken(ident);
            isRemoveStoredTokenCalled = true;
        }

        @Override
        protected void updateStoredToken(TestDelegationToken.TestDelegationTokenIdentifier ident, long renewDate) throws IOException {
            super.updateStoredToken(ident, renewDate);
            isUpdateStoredTokenCalled = true;
        }

        public byte[] createPassword(TestDelegationToken.TestDelegationTokenIdentifier t, DelegationKey key) {
            return SecretManager.createPassword(getBytes(), key.getKey());
        }

        public Map<TestDelegationToken.TestDelegationTokenIdentifier, DelegationTokenInformation> getAllTokens() {
            return currentTokens;
        }

        public DelegationKey getKey(TestDelegationToken.TestDelegationTokenIdentifier id) {
            return allKeys.get(getMasterKeyId());
        }
    }

    public static class TokenSelector extends AbstractDelegationTokenSelector<TestDelegationToken.TestDelegationTokenIdentifier> {
        protected TokenSelector() {
            super(TestDelegationToken.KIND);
        }
    }

    @Test
    public void testSerialization() throws Exception {
        TestDelegationToken.TestDelegationTokenIdentifier origToken = new TestDelegationToken.TestDelegationTokenIdentifier(new Text("alice"), new Text("bob"), new Text("colin"));
        TestDelegationToken.TestDelegationTokenIdentifier newToken = new TestDelegationToken.TestDelegationTokenIdentifier();
        setIssueDate(123);
        setMasterKeyId(321);
        setMaxDate(314);
        setSequenceNumber(12345);
        // clone origToken into newToken
        DataInputBuffer inBuf = new DataInputBuffer();
        DataOutputBuffer outBuf = new DataOutputBuffer();
        origToken.write(outBuf);
        inBuf.reset(outBuf.getData(), 0, outBuf.getLength());
        newToken.readFields(inBuf);
        // now test the fields
        Assert.assertEquals("alice", getUser().getUserName());
        Assert.assertEquals(new Text("bob"), getRenewer());
        Assert.assertEquals("colin", getUser().getRealUser().getUserName());
        Assert.assertEquals(123, getIssueDate());
        Assert.assertEquals(321, getMasterKeyId());
        Assert.assertEquals(314, getMaxDate());
        Assert.assertEquals(12345, getSequenceNumber());
        Assert.assertEquals(origToken, newToken);
    }

    @Test
    public void testGetUserNullOwner() {
        TestDelegationToken.TestDelegationTokenIdentifier ident = new TestDelegationToken.TestDelegationTokenIdentifier(null, null, null);
        UserGroupInformation ugi = ident.getUser();
        Assert.assertNull(ugi);
    }

    @Test
    public void testGetUserWithOwner() {
        TestDelegationToken.TestDelegationTokenIdentifier ident = new TestDelegationToken.TestDelegationTokenIdentifier(new Text("owner"), null, null);
        UserGroupInformation ugi = ident.getUser();
        Assert.assertNull(ugi.getRealUser());
        Assert.assertEquals("owner", ugi.getUserName());
        Assert.assertEquals(TOKEN, ugi.getAuthenticationMethod());
    }

    @Test
    public void testGetUserWithOwnerEqualsReal() {
        Text owner = new Text("owner");
        TestDelegationToken.TestDelegationTokenIdentifier ident = new TestDelegationToken.TestDelegationTokenIdentifier(owner, null, owner);
        UserGroupInformation ugi = ident.getUser();
        Assert.assertNull(ugi.getRealUser());
        Assert.assertEquals("owner", ugi.getUserName());
        Assert.assertEquals(TOKEN, ugi.getAuthenticationMethod());
    }

    @Test
    public void testGetUserWithOwnerAndReal() {
        Text owner = new Text("owner");
        Text realUser = new Text("realUser");
        TestDelegationToken.TestDelegationTokenIdentifier ident = new TestDelegationToken.TestDelegationTokenIdentifier(owner, null, realUser);
        UserGroupInformation ugi = ident.getUser();
        Assert.assertNotNull(ugi.getRealUser());
        Assert.assertNull(ugi.getRealUser().getRealUser());
        Assert.assertEquals("owner", ugi.getUserName());
        Assert.assertEquals("realUser", ugi.getRealUser().getUserName());
        Assert.assertEquals(PROXY, ugi.getAuthenticationMethod());
        Assert.assertEquals(TOKEN, ugi.getRealUser().getAuthenticationMethod());
    }

    @Test
    public void testDelegationTokenSecretManager() throws Exception {
        final TestDelegationToken.TestDelegationTokenSecretManager dtSecretManager = new TestDelegationToken.TestDelegationTokenSecretManager((((24 * 60) * 60) * 1000), (3 * 1000), (1 * 1000), 3600000);
        try {
            startThreads();
            final Token<TestDelegationToken.TestDelegationTokenIdentifier> token = generateDelegationToken(dtSecretManager, "SomeUser", "JobTracker");
            Assert.assertTrue(dtSecretManager.isStoreNewTokenCalled);
            // Fake renewer should not be able to renew
            shouldThrow(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    dtSecretManager.renewToken(token, "FakeRenewer");
                    return null;
                }
            }, AccessControlException.class);
            long time = dtSecretManager.renewToken(token, "JobTracker");
            Assert.assertTrue(dtSecretManager.isUpdateStoredTokenCalled);
            Assert.assertTrue("renew time is in future", (time > (Time.now())));
            TestDelegationToken.TestDelegationTokenIdentifier identifier = new TestDelegationToken.TestDelegationTokenIdentifier();
            byte[] tokenId = token.getIdentifier();
            identifier.readFields(new DataInputStream(new ByteArrayInputStream(tokenId)));
            Assert.assertTrue((null != (retrievePassword(identifier))));
            TestDelegationToken.LOG.info("Sleep to expire the token");
            Thread.sleep(2000);
            // Token should be expired
            try {
                retrievePassword(identifier);
                // Should not come here
                Assert.fail("Token should have expired");
            } catch (InvalidToken e) {
                // Success
            }
            dtSecretManager.renewToken(token, "JobTracker");
            TestDelegationToken.LOG.info("Sleep beyond the max lifetime");
            Thread.sleep(2000);
            shouldThrow(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    dtSecretManager.renewToken(token, "JobTracker");
                    return null;
                }
            }, InvalidToken.class);
        } finally {
            stopThreads();
        }
    }

    @Test
    public void testCancelDelegationToken() throws Exception {
        final TestDelegationToken.TestDelegationTokenSecretManager dtSecretManager = new TestDelegationToken.TestDelegationTokenSecretManager((((24 * 60) * 60) * 1000), (10 * 1000), (1 * 1000), 3600000);
        try {
            startThreads();
            final Token<TestDelegationToken.TestDelegationTokenIdentifier> token = generateDelegationToken(dtSecretManager, "SomeUser", "JobTracker");
            // Fake renewer should not be able to renew
            shouldThrow(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    dtSecretManager.renewToken(token, "FakeCanceller");
                    return null;
                }
            }, AccessControlException.class);
            dtSecretManager.cancelToken(token, "JobTracker");
            Assert.assertTrue(dtSecretManager.isRemoveStoredTokenCalled);
            shouldThrow(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    dtSecretManager.renewToken(token, "JobTracker");
                    return null;
                }
            }, InvalidToken.class);
        } finally {
            stopThreads();
        }
    }

    @Test(timeout = 10000)
    public void testRollMasterKey() throws Exception {
        TestDelegationToken.TestDelegationTokenSecretManager dtSecretManager = new TestDelegationToken.TestDelegationTokenSecretManager(800, 800, (1 * 1000), 3600000);
        try {
            startThreads();
            // generate a token and store the password
            Token<TestDelegationToken.TestDelegationTokenIdentifier> token = generateDelegationToken(dtSecretManager, "SomeUser", "JobTracker");
            byte[] oldPasswd = token.getPassword();
            // store the length of the keys list
            int prevNumKeys = getAllKeys().length;
            rollMasterKey();
            Assert.assertTrue(dtSecretManager.isStoreNewMasterKeyCalled);
            // after rolling, the length of the keys list must increase
            int currNumKeys = getAllKeys().length;
            Assert.assertEquals(((currNumKeys - prevNumKeys) >= 1), true);
            // after rolling, the token that was generated earlier must
            // still be valid (retrievePassword will fail if the token
            // is not valid)
            ByteArrayInputStream bi = new ByteArrayInputStream(token.getIdentifier());
            TestDelegationToken.TestDelegationTokenIdentifier identifier = dtSecretManager.createIdentifier();
            identifier.readFields(new DataInputStream(bi));
            byte[] newPasswd = dtSecretManager.retrievePassword(identifier);
            // compare the passwords
            Assert.assertEquals(oldPasswd, newPasswd);
            // wait for keys to expire
            while (!(dtSecretManager.isRemoveStoredMasterKeyCalled)) {
                Thread.sleep(200);
            } 
        } finally {
            stopThreads();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDelegationTokenSelector() throws Exception {
        TestDelegationToken.TestDelegationTokenSecretManager dtSecretManager = new TestDelegationToken.TestDelegationTokenSecretManager((((24 * 60) * 60) * 1000), (10 * 1000), (1 * 1000), 3600000);
        try {
            startThreads();
            AbstractDelegationTokenSelector ds = new AbstractDelegationTokenSelector<TestDelegationToken.TestDelegationTokenIdentifier>(TestDelegationToken.KIND);
            // Creates a collection of tokens
            Token<TestDelegationToken.TestDelegationTokenIdentifier> token1 = generateDelegationToken(dtSecretManager, "SomeUser1", "JobTracker");
            token1.setService(new Text("MY-SERVICE1"));
            Token<TestDelegationToken.TestDelegationTokenIdentifier> token2 = generateDelegationToken(dtSecretManager, "SomeUser2", "JobTracker");
            token2.setService(new Text("MY-SERVICE2"));
            List<Token<TestDelegationToken.TestDelegationTokenIdentifier>> tokens = new ArrayList<Token<TestDelegationToken.TestDelegationTokenIdentifier>>();
            tokens.add(token1);
            tokens.add(token2);
            // try to select a token with a given service name (created earlier)
            Token<TestDelegationToken.TestDelegationTokenIdentifier> t = ds.selectToken(new Text("MY-SERVICE1"), tokens);
            Assert.assertEquals(t, token1);
        } finally {
            stopThreads();
        }
    }

    @Test
    public void testParallelDelegationTokenCreation() throws Exception {
        final TestDelegationToken.TestDelegationTokenSecretManager dtSecretManager = new TestDelegationToken.TestDelegationTokenSecretManager(2000, (((24 * 60) * 60) * 1000), ((((7 * 24) * 60) * 60) * 1000), 2000);
        try {
            startThreads();
            int numThreads = 100;
            final int numTokensPerThread = 100;
            class tokenIssuerThread implements Runnable {
                @Override
                public void run() {
                    for (int i = 0; i < numTokensPerThread; i++) {
                        generateDelegationToken(dtSecretManager, "auser", "arenewer");
                        try {
                            Thread.sleep(250);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            Thread[] issuers = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++) {
                issuers[i] = new Daemon(new tokenIssuerThread());
                issuers[i].start();
            }
            for (int i = 0; i < numThreads; i++) {
                issuers[i].join();
            }
            Map<TestDelegationToken.TestDelegationTokenIdentifier, DelegationTokenInformation> tokenCache = dtSecretManager.getAllTokens();
            Assert.assertEquals((numTokensPerThread * numThreads), tokenCache.size());
            Iterator<TestDelegationToken.TestDelegationTokenIdentifier> iter = tokenCache.keySet().iterator();
            while (iter.hasNext()) {
                TestDelegationToken.TestDelegationTokenIdentifier id = iter.next();
                DelegationTokenInformation info = tokenCache.get(id);
                Assert.assertTrue((info != null));
                DelegationKey key = dtSecretManager.getKey(id);
                Assert.assertTrue((key != null));
                byte[] storedPassword = dtSecretManager.retrievePassword(id);
                byte[] password = dtSecretManager.createPassword(id, key);
                Assert.assertTrue(Arrays.equals(password, storedPassword));
                // verify by secret manager api
                verifyToken(id, password);
            } 
        } finally {
            stopThreads();
        }
    }

    @Test
    public void testDelegationTokenNullRenewer() throws Exception {
        TestDelegationToken.TestDelegationTokenSecretManager dtSecretManager = new TestDelegationToken.TestDelegationTokenSecretManager((((24 * 60) * 60) * 1000), (10 * 1000), (1 * 1000), 3600000);
        startThreads();
        TestDelegationToken.TestDelegationTokenIdentifier dtId = new TestDelegationToken.TestDelegationTokenIdentifier(new Text("theuser"), null, null);
        Token<TestDelegationToken.TestDelegationTokenIdentifier> token = new Token<TestDelegationToken.TestDelegationTokenIdentifier>(dtId, dtSecretManager);
        Assert.assertTrue((token != null));
        try {
            dtSecretManager.renewToken(token, "");
            Assert.fail("Renewal must not succeed");
        } catch (IOException e) {
            // PASS
        }
    }

    @Test
    public void testSimpleDtidSerialization() throws IOException {
        Assert.assertTrue(testDelegationTokenIdentiferSerializationRoundTrip(new Text("owner"), new Text("renewer"), new Text("realUser")));
        Assert.assertTrue(testDelegationTokenIdentiferSerializationRoundTrip(new Text(""), new Text(""), new Text("")));
        Assert.assertTrue(testDelegationTokenIdentiferSerializationRoundTrip(new Text(""), new Text("b"), new Text("")));
    }

    @Test
    public void testOverlongDtidSerialization() throws IOException {
        byte[] bigBuf = new byte[(Text.DEFAULT_MAX_LEN) + 1];
        for (int i = 0; i < (bigBuf.length); i++) {
            bigBuf[i] = 0;
        }
        Assert.assertFalse(testDelegationTokenIdentiferSerializationRoundTrip(new Text(bigBuf), new Text("renewer"), new Text("realUser")));
        Assert.assertFalse(testDelegationTokenIdentiferSerializationRoundTrip(new Text("owner"), new Text(bigBuf), new Text("realUser")));
        Assert.assertFalse(testDelegationTokenIdentiferSerializationRoundTrip(new Text("owner"), new Text("renewer"), new Text(bigBuf)));
    }

    @Test
    public void testDelegationKeyEqualAndHash() {
        DelegationKey key1 = new DelegationKey(1111, 2222, "keyBytes".getBytes());
        DelegationKey key2 = new DelegationKey(1111, 2222, "keyBytes".getBytes());
        DelegationKey key3 = new DelegationKey(3333, 2222, "keyBytes".getBytes());
        Assert.assertEquals(key1, key2);
        Assert.assertFalse(key2.equals(key3));
    }

    @Test
    public void testEmptyToken() throws IOException {
        Token<?> token1 = new Token<org.apache.hadoop.security.token.TokenIdentifier>();
        Token<?> token2 = new Token<org.apache.hadoop.security.token.TokenIdentifier>(new byte[0], new byte[0], new Text(), new Text());
        Assert.assertEquals(token1, token2);
        Assert.assertEquals(token1.encodeToUrlString(), token2.encodeToUrlString());
        token2 = new Token<org.apache.hadoop.security.token.TokenIdentifier>(null, null, null, null);
        Assert.assertEquals(token1, token2);
        Assert.assertEquals(token1.encodeToUrlString(), token2.encodeToUrlString());
    }
}

