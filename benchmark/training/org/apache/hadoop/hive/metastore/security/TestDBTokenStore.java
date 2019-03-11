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


import HadoopThriftAuthBridge.Server.ServerMode.METASTORE;
import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.apache.hadoop.hive.metastore.HiveMetaStore.HMSHandler;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.security.DelegationTokenStore.TokenStoreException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager.DelegationTokenInformation;
import org.apache.hadoop.security.token.delegation.HiveDelegationTokenSupport;
import org.junit.Assert;


public class TestDBTokenStore extends TestCase {
    public void testDBTokenStore() throws IOException, MetaException, TokenStoreException {
        DelegationTokenStore ts = new DBTokenStore();
        ts.init(new HMSHandler("Test handler"), METASTORE);
        TestCase.assertEquals(0, ts.getMasterKeys().length);
        TestCase.assertEquals(false, ts.removeMasterKey((-1)));
        try {
            ts.updateMasterKey((-1), "non-existent-key");
            TestCase.fail("Updated non-existent key.");
        } catch (TokenStoreException e) {
            TestCase.assertTrue(((e.getCause()) instanceof NoSuchObjectException));
        }
        int keySeq = ts.addMasterKey("key1Data");
        int keySeq2 = ts.addMasterKey("key2Data");
        int keySeq2same = ts.addMasterKey("key2Data");
        TestCase.assertEquals("keys sequential", (keySeq + 1), keySeq2);
        TestCase.assertEquals("keys sequential", (keySeq + 2), keySeq2same);
        TestCase.assertEquals("expected number of keys", 3, ts.getMasterKeys().length);
        TestCase.assertTrue(ts.removeMasterKey(keySeq));
        TestCase.assertTrue(ts.removeMasterKey(keySeq2same));
        TestCase.assertEquals("expected number of keys", 1, ts.getMasterKeys().length);
        TestCase.assertEquals("key2Data", ts.getMasterKeys()[0]);
        ts.updateMasterKey(keySeq2, "updatedData");
        TestCase.assertEquals("updatedData", ts.getMasterKeys()[0]);
        TestCase.assertTrue(ts.removeMasterKey(keySeq2));
        // tokens
        TestCase.assertEquals(0, ts.getAllDelegationTokenIdentifiers().size());
        DelegationTokenIdentifier tokenId = new DelegationTokenIdentifier(new Text("owner"), new Text("renewer"), new Text("realUser"));
        TestCase.assertNull(ts.getToken(tokenId));
        TestCase.assertFalse(ts.removeToken(tokenId));
        DelegationTokenInformation tokenInfo = new DelegationTokenInformation(99, "password".getBytes());
        TestCase.assertTrue(ts.addToken(tokenId, tokenInfo));
        TestCase.assertFalse(ts.addToken(tokenId, tokenInfo));
        DelegationTokenInformation tokenInfoRead = ts.getToken(tokenId);
        TestCase.assertEquals(tokenInfo.getRenewDate(), tokenInfoRead.getRenewDate());
        TestCase.assertNotSame(tokenInfo, tokenInfoRead);
        Assert.assertArrayEquals(HiveDelegationTokenSupport.encodeDelegationTokenInformation(tokenInfo), HiveDelegationTokenSupport.encodeDelegationTokenInformation(tokenInfoRead));
        List<DelegationTokenIdentifier> allIds = ts.getAllDelegationTokenIdentifiers();
        TestCase.assertEquals(1, allIds.size());
        Assert.assertEquals(TokenStoreDelegationTokenSecretManager.encodeWritable(tokenId), TokenStoreDelegationTokenSecretManager.encodeWritable(allIds.get(0)));
        TestCase.assertTrue(ts.removeToken(tokenId));
        TestCase.assertEquals(0, ts.getAllDelegationTokenIdentifiers().size());
        TestCase.assertNull(ts.getToken(tokenId));
        ts.close();
    }
}

