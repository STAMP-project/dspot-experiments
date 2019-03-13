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


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.KeyGenerator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestCredentials {
    private static final String DEFAULT_HMAC_ALGORITHM = "HmacSHA1";

    private static final File tmpDir = GenericTestUtils.getTestDir("mapred");

    @SuppressWarnings("unchecked")
    @Test
    public <T extends TokenIdentifier> void testReadWriteStorage() throws IOException, NoSuchAlgorithmException {
        // create tokenStorage Object
        Credentials ts = new Credentials();
        Token<T> token1 = new Token();
        Token<T> token2 = new Token();
        Text service1 = new Text("service1");
        Text service2 = new Text("service2");
        Text alias1 = new Text("sometoken1");
        Text alias2 = new Text("sometoken2");
        Collection<Text> services = new ArrayList<Text>();
        services.add(service1);
        services.add(service2);
        token1.setService(service1);
        token2.setService(service2);
        ts.addToken(alias1, token1);
        ts.addToken(alias2, token2);
        // create keys and put it in
        final KeyGenerator kg = KeyGenerator.getInstance(TestCredentials.DEFAULT_HMAC_ALGORITHM);
        String alias = "alias";
        Map<Text, byte[]> m = new HashMap<Text, byte[]>(10);
        for (int i = 0; i < 10; i++) {
            Key key = kg.generateKey();
            m.put(new Text((alias + i)), key.getEncoded());
            ts.addSecretKey(new Text((alias + i)), key.getEncoded());
        }
        // create file to store
        File tmpFileName = new File(TestCredentials.tmpDir, "tokenStorageTest");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(tmpFileName));
        ts.write(dos);
        dos.close();
        // open and read it back
        DataInputStream dis = new DataInputStream(new FileInputStream(tmpFileName));
        ts = new Credentials();
        ts.readFields(dis);
        dis.close();
        // get the tokens and compare the services
        Map<Text, Token<? extends TokenIdentifier>> tokenMap = ts.getTokenMap();
        Assert.assertEquals("getTokenMap should return collection of size 2", 2, tokenMap.size());
        Assert.assertTrue((("Token for alias " + alias1) + " must be present"), tokenMap.containsKey(alias1));
        Assert.assertTrue((("Token for alias " + alias2) + " must be present"), tokenMap.containsKey(alias2));
        Assert.assertEquals((("Token for service " + service1) + " must be present"), service1, tokenMap.get(alias1).getService());
        Assert.assertEquals((("Token for service " + service2) + " must be present"), service2, tokenMap.get(alias2).getService());
        // compare secret keys
        Map<Text, byte[]> secretKeyMap = ts.getSecretKeyMap();
        Assert.assertEquals("wrong number of keys in the Storage", m.size(), ts.numberOfSecretKeys());
        for (Map.Entry<Text, byte[]> entry : m.entrySet()) {
            byte[] key = secretKeyMap.get(entry.getKey());
            Assert.assertNotNull((("Secret key for alias " + (entry.getKey())) + " not found"), key);
            Assert.assertTrue(("Keys don't match for alias " + (entry.getKey())), Arrays.equals(key, entry.getValue()));
        }
        tmpFileName.delete();
    }

    @Test
    public void testBasicReadWriteProtoEmpty() throws IOException, NoSuchAlgorithmException {
        String testname = "testBasicReadWriteProtoEmpty";
        Credentials ts = new Credentials();
        writeCredentialsProto(ts, testname);
        Credentials ts2 = readCredentialsProto(testname);
        Assert.assertEquals("test empty tokens", 0, ts2.numberOfTokens());
        Assert.assertEquals("test empty keys", 0, ts2.numberOfSecretKeys());
    }

    @Test
    public void testBasicReadWriteProto() throws IOException, NoSuchAlgorithmException {
        String testname = "testBasicReadWriteProto";
        Text tok1 = new Text("token1");
        Text tok2 = new Text("token2");
        Text key1 = new Text("key1");
        Credentials ts = generateCredentials(tok1, tok2, key1);
        writeCredentialsProto(ts, testname);
        Credentials ts2 = readCredentialsProto(testname);
        assertCredentials(testname, tok1, key1, ts, ts2);
        assertCredentials(testname, tok2, key1, ts, ts2);
    }

    @Test
    public void testBasicReadWriteStreamEmpty() throws IOException, NoSuchAlgorithmException {
        String testname = "testBasicReadWriteStreamEmpty";
        Credentials ts = new Credentials();
        writeCredentialsStream(ts, testname);
        Credentials ts2 = readCredentialsStream(testname);
        Assert.assertEquals("test empty tokens", 0, ts2.numberOfTokens());
        Assert.assertEquals("test empty keys", 0, ts2.numberOfSecretKeys());
    }

    @Test
    public void testBasicReadWriteStream() throws IOException, NoSuchAlgorithmException {
        String testname = "testBasicReadWriteStream";
        Text tok1 = new Text("token1");
        Text tok2 = new Text("token2");
        Text key1 = new Text("key1");
        Credentials ts = generateCredentials(tok1, tok2, key1);
        writeCredentialsStream(ts, testname);
        Credentials ts2 = readCredentialsStream(testname);
        assertCredentials(testname, tok1, key1, ts, ts2);
        assertCredentials(testname, tok2, key1, ts, ts2);
    }

    /**
     * Verify the suitability of read/writeProto for use with Writable interface.
     * This test uses only empty credentials.
     */
    @Test
    public void testWritablePropertiesEmpty() throws IOException, NoSuchAlgorithmException {
        String testname = "testWritablePropertiesEmpty";
        Credentials ts = new Credentials();
        Credentials ts2 = new Credentials();
        writeCredentialsProtos(ts, ts2, testname);
        List<Credentials> clist = readCredentialsProtos(testname);
        Assert.assertEquals("test empty tokens 0", 0, clist.get(0).numberOfTokens());
        Assert.assertEquals("test empty keys 0", 0, clist.get(0).numberOfSecretKeys());
        Assert.assertEquals("test empty tokens 1", 0, clist.get(1).numberOfTokens());
        Assert.assertEquals("test empty keys 1", 0, clist.get(1).numberOfSecretKeys());
    }

    /**
     * Verify the suitability of read/writeProto for use with Writable interface.
     */
    @Test
    public void testWritableProperties() throws IOException, NoSuchAlgorithmException {
        String testname = "testWritableProperties";
        Text tok1 = new Text("token1");
        Text tok2 = new Text("token2");
        Text key1 = new Text("key1");
        Credentials ts = generateCredentials(tok1, tok2, key1);
        Text tok3 = new Text("token3");
        Text key2 = new Text("key2");
        Credentials ts2 = generateCredentials(tok1, tok3, key2);
        writeCredentialsProtos(ts, ts2, testname);
        List<Credentials> clist = readCredentialsProtos(testname);
        assertCredentials(testname, tok1, key1, ts, clist.get(0));
        assertCredentials(testname, tok2, key1, ts, clist.get(0));
        assertCredentials(testname, tok1, key2, ts2, clist.get(1));
        assertCredentials(testname, tok3, key2, ts2, clist.get(1));
    }

    static Text[] secret = new Text[]{ new Text("secret1"), new Text("secret2"), new Text("secret3"), new Text("secret4") };

    static Text[] service = new Text[]{ new Text("service1"), new Text("service2"), new Text("service3"), new Text("service4") };

    static Token<?>[] token = new Token<?>[]{ new Token<TokenIdentifier>(), new Token<TokenIdentifier>(), new Token<TokenIdentifier>(), new Token<TokenIdentifier>() };

    @Test
    public void addAll() {
        Credentials creds = new Credentials();
        creds.addToken(TestCredentials.service[0], TestCredentials.token[0]);
        creds.addToken(TestCredentials.service[1], TestCredentials.token[1]);
        creds.addSecretKey(TestCredentials.secret[0], TestCredentials.secret[0].getBytes());
        creds.addSecretKey(TestCredentials.secret[1], TestCredentials.secret[1].getBytes());
        Credentials credsToAdd = new Credentials();
        // one duplicate with different value, one new
        credsToAdd.addToken(TestCredentials.service[0], TestCredentials.token[3]);
        credsToAdd.addToken(TestCredentials.service[2], TestCredentials.token[2]);
        credsToAdd.addSecretKey(TestCredentials.secret[0], TestCredentials.secret[3].getBytes());
        credsToAdd.addSecretKey(TestCredentials.secret[2], TestCredentials.secret[2].getBytes());
        creds.addAll(credsToAdd);
        Assert.assertEquals(3, creds.numberOfTokens());
        Assert.assertEquals(3, creds.numberOfSecretKeys());
        // existing token & secret should be overwritten
        Assert.assertEquals(TestCredentials.token[3], creds.getToken(TestCredentials.service[0]));
        Assert.assertEquals(TestCredentials.secret[3], new Text(creds.getSecretKey(TestCredentials.secret[0])));
        // non-duplicate token & secret should be present
        Assert.assertEquals(TestCredentials.token[1], creds.getToken(TestCredentials.service[1]));
        Assert.assertEquals(TestCredentials.secret[1], new Text(creds.getSecretKey(TestCredentials.secret[1])));
        // new token & secret should be added
        Assert.assertEquals(TestCredentials.token[2], creds.getToken(TestCredentials.service[2]));
        Assert.assertEquals(TestCredentials.secret[2], new Text(creds.getSecretKey(TestCredentials.secret[2])));
    }

    @Test
    public void mergeAll() {
        Credentials creds = new Credentials();
        creds.addToken(TestCredentials.service[0], TestCredentials.token[0]);
        creds.addToken(TestCredentials.service[1], TestCredentials.token[1]);
        creds.addSecretKey(TestCredentials.secret[0], TestCredentials.secret[0].getBytes());
        creds.addSecretKey(TestCredentials.secret[1], TestCredentials.secret[1].getBytes());
        Credentials credsToAdd = new Credentials();
        // one duplicate with different value, one new
        credsToAdd.addToken(TestCredentials.service[0], TestCredentials.token[3]);
        credsToAdd.addToken(TestCredentials.service[2], TestCredentials.token[2]);
        credsToAdd.addSecretKey(TestCredentials.secret[0], TestCredentials.secret[3].getBytes());
        credsToAdd.addSecretKey(TestCredentials.secret[2], TestCredentials.secret[2].getBytes());
        creds.mergeAll(credsToAdd);
        Assert.assertEquals(3, creds.numberOfTokens());
        Assert.assertEquals(3, creds.numberOfSecretKeys());
        // existing token & secret should not be overwritten
        Assert.assertEquals(TestCredentials.token[0], creds.getToken(TestCredentials.service[0]));
        Assert.assertEquals(TestCredentials.secret[0], new Text(creds.getSecretKey(TestCredentials.secret[0])));
        // non-duplicate token & secret should be present
        Assert.assertEquals(TestCredentials.token[1], creds.getToken(TestCredentials.service[1]));
        Assert.assertEquals(TestCredentials.secret[1], new Text(creds.getSecretKey(TestCredentials.secret[1])));
        // new token & secret should be added
        Assert.assertEquals(TestCredentials.token[2], creds.getToken(TestCredentials.service[2]));
        Assert.assertEquals(TestCredentials.secret[2], new Text(creds.getSecretKey(TestCredentials.secret[2])));
    }

    @Test
    public void testAddTokensToUGI() {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("someone");
        Credentials creds = new Credentials();
        for (int i = 0; i < (TestCredentials.service.length); i++) {
            creds.addToken(TestCredentials.service[i], TestCredentials.token[i]);
        }
        ugi.addCredentials(creds);
        creds = ugi.getCredentials();
        for (int i = 0; i < (TestCredentials.service.length); i++) {
            Assert.assertSame(TestCredentials.token[i], creds.getToken(TestCredentials.service[i]));
        }
        Assert.assertEquals(TestCredentials.service.length, creds.numberOfTokens());
    }
}

