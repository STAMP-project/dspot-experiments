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
package org.apache.hadoop.yarn.server.timeline.recovery;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.hadoop.service.ServiceStateException;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.yarn.server.timeline.recovery.TimelineStateStore.TimelineServiceState;
import org.junit.Assert;
import org.junit.Test;


public class TestLeveldbTimelineStateStore {
    private FileContext fsContext;

    private File fsPath;

    private Configuration conf;

    private TimelineStateStore store;

    @Test
    public void testTokenStore() throws Exception {
        initAndStartTimelineServiceStateStoreService();
        TimelineServiceState state = store.loadState();
        Assert.assertTrue("token state not empty", state.tokenState.isEmpty());
        Assert.assertTrue("key state not empty", state.tokenMasterKeyState.isEmpty());
        final DelegationKey key1 = new DelegationKey(1, 2, "keyData1".getBytes());
        final TimelineDelegationTokenIdentifier token1 = new TimelineDelegationTokenIdentifier(new Text("tokenOwner1"), new Text("tokenRenewer1"), new Text("tokenUser1"));
        token1.setSequenceNumber(1);
        token1.getBytes();
        final Long tokenDate1 = 1L;
        final TimelineDelegationTokenIdentifier token2 = new TimelineDelegationTokenIdentifier(new Text("tokenOwner2"), new Text("tokenRenewer2"), new Text("tokenUser2"));
        token2.setSequenceNumber(12345678);
        token2.getBytes();
        final Long tokenDate2 = 87654321L;
        store.storeTokenMasterKey(key1);
        try {
            store.storeTokenMasterKey(key1);
            Assert.fail("redundant store of key undetected");
        } catch (IOException e) {
            // expected
        }
        store.storeToken(token1, tokenDate1);
        store.storeToken(token2, tokenDate2);
        try {
            store.storeToken(token1, tokenDate1);
            Assert.fail("redundant store of token undetected");
        } catch (IOException e) {
            // expected
        }
        store.close();
        initAndStartTimelineServiceStateStoreService();
        state = store.loadState();
        Assert.assertEquals("incorrect loaded token count", 2, state.tokenState.size());
        Assert.assertTrue("missing token 1", state.tokenState.containsKey(token1));
        Assert.assertEquals("incorrect token 1 date", tokenDate1, state.tokenState.get(token1));
        Assert.assertTrue("missing token 2", state.tokenState.containsKey(token2));
        Assert.assertEquals("incorrect token 2 date", tokenDate2, state.tokenState.get(token2));
        Assert.assertEquals("incorrect master key count", 1, state.tokenMasterKeyState.size());
        Assert.assertTrue("missing master key 1", state.tokenMasterKeyState.contains(key1));
        Assert.assertEquals("incorrect latest sequence number", 12345678, state.getLatestSequenceNumber());
        final DelegationKey key2 = new DelegationKey(3, 4, "keyData2".getBytes());
        final DelegationKey key3 = new DelegationKey(5, 6, "keyData3".getBytes());
        final TimelineDelegationTokenIdentifier token3 = new TimelineDelegationTokenIdentifier(new Text("tokenOwner3"), new Text("tokenRenewer3"), new Text("tokenUser3"));
        token3.setSequenceNumber(12345679);
        token3.getBytes();
        final Long tokenDate3 = 87654321L;
        store.removeToken(token1);
        store.storeTokenMasterKey(key2);
        final Long newTokenDate2 = 975318642L;
        store.updateToken(token2, newTokenDate2);
        store.removeTokenMasterKey(key1);
        store.storeTokenMasterKey(key3);
        store.storeToken(token3, tokenDate3);
        store.close();
        initAndStartTimelineServiceStateStoreService();
        state = store.loadState();
        Assert.assertEquals("incorrect loaded token count", 2, state.tokenState.size());
        Assert.assertFalse("token 1 not removed", state.tokenState.containsKey(token1));
        Assert.assertTrue("missing token 2", state.tokenState.containsKey(token2));
        Assert.assertEquals("incorrect token 2 date", newTokenDate2, state.tokenState.get(token2));
        Assert.assertTrue("missing token 3", state.tokenState.containsKey(token3));
        Assert.assertEquals("incorrect token 3 date", tokenDate3, state.tokenState.get(token3));
        Assert.assertEquals("incorrect master key count", 2, state.tokenMasterKeyState.size());
        Assert.assertFalse("master key 1 not removed", state.tokenMasterKeyState.contains(key1));
        Assert.assertTrue("missing master key 2", state.tokenMasterKeyState.contains(key2));
        Assert.assertTrue("missing master key 3", state.tokenMasterKeyState.contains(key3));
        Assert.assertEquals("incorrect latest sequence number", 12345679, state.getLatestSequenceNumber());
        store.close();
    }

    @Test
    public void testCheckVersion() throws IOException {
        LeveldbTimelineStateStore store = initAndStartTimelineServiceStateStoreService();
        // default version
        Version defaultVersion = store.getCurrentVersion();
        Assert.assertEquals(defaultVersion, store.loadVersion());
        // compatible version
        Version compatibleVersion = Version.newInstance(defaultVersion.getMajorVersion(), ((defaultVersion.getMinorVersion()) + 2));
        store.storeVersion(compatibleVersion);
        Assert.assertEquals(compatibleVersion, store.loadVersion());
        store.stop();
        // overwrite the compatible version
        store = initAndStartTimelineServiceStateStoreService();
        Assert.assertEquals(defaultVersion, store.loadVersion());
        // incompatible version
        Version incompatibleVersion = Version.newInstance(((defaultVersion.getMajorVersion()) + 1), defaultVersion.getMinorVersion());
        store.storeVersion(incompatibleVersion);
        store.stop();
        try {
            initAndStartTimelineServiceStateStoreService();
            Assert.fail("Incompatible version, should expect fail here.");
        } catch (ServiceStateException e) {
            Assert.assertTrue("Exception message mismatch", e.getMessage().contains("Incompatible version for timeline state store"));
        }
    }
}

