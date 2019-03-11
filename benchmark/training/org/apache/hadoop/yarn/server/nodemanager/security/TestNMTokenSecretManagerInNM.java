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
package org.apache.hadoop.yarn.server.nodemanager.security;


import YarnConfiguration.NM_RECOVERY_ENABLED;
import java.io.IOException;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.NMTokenIdentifier;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMMemoryStateStoreService;
import org.apache.hadoop.yarn.server.security.BaseNMTokenSecretManager;
import org.junit.Assert;
import org.junit.Test;


public class TestNMTokenSecretManagerInNM {
    @Test
    public void testRecovery() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        final NodeId nodeId = NodeId.newInstance("somehost", 1234);
        final ApplicationAttemptId attempt1 = ApplicationAttemptId.newInstance(ApplicationId.newInstance(1, 1), 1);
        final ApplicationAttemptId attempt2 = ApplicationAttemptId.newInstance(ApplicationId.newInstance(2, 2), 2);
        TestNMTokenSecretManagerInNM.NMTokenKeyGeneratorForTest keygen = new TestNMTokenSecretManagerInNM.NMTokenKeyGeneratorForTest();
        NMMemoryStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        start();
        NMTokenSecretManagerInNM secretMgr = new NMTokenSecretManagerInNM(stateStore);
        secretMgr.setNodeId(nodeId);
        MasterKey currentKey = keygen.generateKey();
        secretMgr.setMasterKey(currentKey);
        NMTokenIdentifier attemptToken1 = getNMTokenId(secretMgr.createNMToken(attempt1, nodeId, "user1"));
        NMTokenIdentifier attemptToken2 = getNMTokenId(secretMgr.createNMToken(attempt2, nodeId, "user2"));
        secretMgr.appAttemptStartContainer(attemptToken1);
        secretMgr.appAttemptStartContainer(attemptToken2);
        Assert.assertTrue(secretMgr.isAppAttemptNMTokenKeyPresent(attempt1));
        Assert.assertTrue(secretMgr.isAppAttemptNMTokenKeyPresent(attempt2));
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken1));
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken2));
        // restart and verify key is still there and token still valid
        secretMgr = new NMTokenSecretManagerInNM(stateStore);
        secretMgr.recover();
        secretMgr.setNodeId(nodeId);
        Assert.assertEquals(currentKey, secretMgr.getCurrentKey());
        Assert.assertTrue(secretMgr.isAppAttemptNMTokenKeyPresent(attempt1));
        Assert.assertTrue(secretMgr.isAppAttemptNMTokenKeyPresent(attempt2));
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken1));
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken2));
        // roll master key and remove an app
        currentKey = keygen.generateKey();
        secretMgr.setMasterKey(currentKey);
        secretMgr.appFinished(attempt1.getApplicationId());
        // restart and verify attempt1 key is still valid due to prev key persist
        secretMgr = new NMTokenSecretManagerInNM(stateStore);
        secretMgr.recover();
        secretMgr.setNodeId(nodeId);
        Assert.assertEquals(currentKey, secretMgr.getCurrentKey());
        Assert.assertFalse(secretMgr.isAppAttemptNMTokenKeyPresent(attempt1));
        Assert.assertTrue(secretMgr.isAppAttemptNMTokenKeyPresent(attempt2));
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken1));
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken2));
        // roll master key again, restart, and verify attempt1 key is bad but
        // attempt2 is still good due to app key persist
        currentKey = keygen.generateKey();
        secretMgr.setMasterKey(currentKey);
        secretMgr = new NMTokenSecretManagerInNM(stateStore);
        secretMgr.recover();
        secretMgr.setNodeId(nodeId);
        Assert.assertEquals(currentKey, secretMgr.getCurrentKey());
        Assert.assertFalse(secretMgr.isAppAttemptNMTokenKeyPresent(attempt1));
        Assert.assertTrue(secretMgr.isAppAttemptNMTokenKeyPresent(attempt2));
        try {
            secretMgr.retrievePassword(attemptToken1);
            Assert.fail("attempt token should not still be valid");
        } catch (InvalidToken e) {
            // expected
        }
        Assert.assertNotNull(secretMgr.retrievePassword(attemptToken2));
        // remove last attempt, restart, verify both tokens are now bad
        secretMgr.appFinished(attempt2.getApplicationId());
        secretMgr = new NMTokenSecretManagerInNM(stateStore);
        secretMgr.recover();
        secretMgr.setNodeId(nodeId);
        Assert.assertEquals(currentKey, secretMgr.getCurrentKey());
        Assert.assertFalse(secretMgr.isAppAttemptNMTokenKeyPresent(attempt1));
        Assert.assertFalse(secretMgr.isAppAttemptNMTokenKeyPresent(attempt2));
        try {
            secretMgr.retrievePassword(attemptToken1);
            Assert.fail("attempt token should not still be valid");
        } catch (InvalidToken e) {
            // expected
        }
        try {
            secretMgr.retrievePassword(attemptToken2);
            Assert.fail("attempt token should not still be valid");
        } catch (InvalidToken e) {
            // expected
        }
        close();
    }

    private static class NMTokenKeyGeneratorForTest extends BaseNMTokenSecretManager {
        public MasterKey generateKey() {
            return createNewMasterKey().getMasterKey();
        }
    }
}

