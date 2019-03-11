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
package org.apache.hadoop.yarn.server.resourcemanager.recovery;


import CommonConfigurationKeys.ZK_ADDRESS;
import FinalApplicationStatus.SUCCEEDED;
import HAServiceProtocol.HAServiceState;
import HAServiceProtocol.HAServiceState.ACTIVE;
import HAServiceProtocol.HAServiceState.STANDBY;
import HAServiceProtocol.RequestSource;
import Perms.ALL;
import RMAppAttemptState.FINISHED;
import RMAppEventType.APP_SAVE_FAILED;
import RMAppState.RUNNING;
import Service.STATE.STARTED;
import YarnConfiguration.DEFAULT_RM_ZK_ACL;
import YarnConfiguration.DEFAULT_ZK_APPID_NODE_SPLIT_INDEX;
import YarnConfiguration.RM_EPOCH;
import YarnConfiguration.RM_EPOCH_RANGE;
import YarnConfiguration.RM_HA_ENABLED;
import YarnConfiguration.RM_ZK_ACL;
import YarnConfiguration.RM_ZK_ZNODE_SIZE_LIMIT_BYTES;
import YarnConfiguration.ZK_APPID_NODE_SPLIT_INDEX;
import YarnConfiguration.ZK_RM_STATE_STORE_PARENT_PATH;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol.StateChangeRequestInfo;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerLaunchContextPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore.RMState;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationStateDataPBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AggregateAppResourceUsage;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.security.MasterKeyData;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RMStateStore.RM_DELEGATION_TOKENS_ROOT_ZNODE_NAME;
import static ZKRMStateStore.ROOT_ZNODE_NAME;


public class TestZKRMStateStore extends RMStateStoreTestBase {
    public static final Logger LOG = LoggerFactory.getLogger(TestZKRMStateStore.class);

    private static final int ZK_TIMEOUT_MS = 1000;

    private TestingServer curatorTestingServer;

    private CuratorFramework curatorFramework;

    class TestZKRMStateStoreTester implements RMStateStoreTestBase.RMStateStoreHelper {
        TestZKRMStateStore.TestZKRMStateStoreTester.TestZKRMStateStoreInternal store;

        String workingZnode;

        class TestZKRMStateStoreInternal extends ZKRMStateStore {
            TestZKRMStateStoreInternal(Configuration conf, String workingZnode) throws Exception {
                setResourceManager(new ResourceManager());
                init(conf);
                dispatcher.disableExitOnDispatchException();
                start();
                Assert.assertTrue(znodeWorkingPath.equals(workingZnode));
            }

            private String getVersionNode() {
                return ((((znodeWorkingPath) + "/") + (ROOT_ZNODE_NAME)) + "/") + (VERSION_NODE);
            }

            @Override
            public Version getCurrentVersion() {
                return CURRENT_VERSION_INFO;
            }

            private String getAppNode(String appId, int splitIdx) {
                String rootPath = ((((workingZnode) + "/") + (ROOT_ZNODE_NAME)) + "/") + (RM_APP_ROOT);
                String appPath = appId;
                if (splitIdx != 0) {
                    int idx = (appId.length()) - splitIdx;
                    appPath = ((appId.substring(0, idx)) + "/") + (appId.substring(idx));
                    return (((((rootPath + "/") + (RM_APP_ROOT_HIERARCHIES)) + "/") + (Integer.toString(splitIdx))) + "/") + appPath;
                }
                return (rootPath + "/") + appPath;
            }

            private String getAppNode(String appId) {
                return getAppNode(appId, 0);
            }

            private String getAttemptNode(String appId, String attemptId) {
                return ((getAppNode(appId)) + "/") + attemptId;
            }

            /**
             * Emulating retrying createRootDir not to raise NodeExist exception
             *
             * @throws Exception
             * 		
             */
            private void testRetryingCreateRootDir() throws Exception {
                create(znodeWorkingPath);
            }

            private String getDelegationTokenNode(int rmDTSequenceNumber, int splitIdx) {
                String rootPath = ((((((workingZnode) + "/") + (ROOT_ZNODE_NAME)) + "/") + (RM_DT_SECRET_MANAGER_ROOT)) + "/") + (RM_DELEGATION_TOKENS_ROOT_ZNODE_NAME);
                String nodeName = DELEGATION_TOKEN_PREFIX;
                if (splitIdx == 0) {
                    nodeName += rmDTSequenceNumber;
                } else {
                    nodeName += String.format("%04d", rmDTSequenceNumber);
                }
                String path = nodeName;
                if (splitIdx != 0) {
                    int idx = (nodeName.length()) - splitIdx;
                    path = (((splitIdx + "/") + (nodeName.substring(0, idx))) + "/") + (nodeName.substring(idx));
                }
                return (rootPath + "/") + path;
            }
        }

        private RMStateStore createStore(Configuration conf) throws Exception {
            workingZnode = "/jira/issue/3077/rmstore";
            conf.set(ZK_ADDRESS, curatorTestingServer.getConnectString());
            conf.set(ZK_RM_STATE_STORE_PARENT_PATH, workingZnode);
            conf.setLong(RM_EPOCH, epoch);
            conf.setLong(RM_EPOCH_RANGE, getEpochRange());
            this.store = new TestZKRMStateStore.TestZKRMStateStoreTester.TestZKRMStateStoreInternal(conf, workingZnode);
            return this.store;
        }

        public RMStateStore getRMStateStore(Configuration conf) throws Exception {
            return createStore(conf);
        }

        @Override
        public RMStateStore getRMStateStore() throws Exception {
            YarnConfiguration conf = new YarnConfiguration();
            return createStore(conf);
        }

        @Override
        public boolean isFinalStateValid() throws Exception {
            return 1 == (curatorFramework.getChildren().forPath(store.znodeWorkingPath).size());
        }

        @Override
        public void writeVersion(Version version) throws Exception {
            curatorFramework.setData().withVersion((-1)).forPath(store.getVersionNode(), getProto().toByteArray());
        }

        @Override
        public Version getCurrentVersion() throws Exception {
            return store.getCurrentVersion();
        }

        @Override
        public boolean appExists(RMApp app) throws Exception {
            String appIdPath = app.getApplicationId().toString();
            int split = getConfig().getInt(ZK_APPID_NODE_SPLIT_INDEX, DEFAULT_ZK_APPID_NODE_SPLIT_INDEX);
            return null != (curatorFramework.checkExists().forPath(store.getAppNode(appIdPath, split)));
        }

        @Override
        public boolean attemptExists(RMAppAttempt attempt) throws Exception {
            ApplicationAttemptId attemptId = attempt.getAppAttemptId();
            return null != (curatorFramework.checkExists().forPath(store.getAttemptNode(attemptId.getApplicationId().toString(), attemptId.toString())));
        }

        public boolean delegationTokenExists(RMDelegationTokenIdentifier token, int index) throws Exception {
            int rmDTSequenceNumber = token.getSequenceNumber();
            return (curatorFramework.checkExists().forPath(store.getDelegationTokenNode(rmDTSequenceNumber, index))) != null;
        }

        public int getDelegationTokenNodeSplitIndex() {
            return store.delegationTokenNodeSplitIndex;
        }
    }

    @Test(timeout = 60000)
    public void testZKRMStateStoreRealZK() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        testRMAppStateStore(zkTester);
        testRMDTSecretManagerStateStore(zkTester);
        testCheckVersion(zkTester);
        testEpoch(zkTester);
        testAppDeletion(zkTester);
        testDeleteStore(zkTester);
        testRemoveApplication(zkTester);
        testRemoveAttempt(zkTester);
        testAMRMTokenSecretManagerStateStore(zkTester);
        testReservationStateStore(zkTester);
        ((TestZKRMStateStore.TestZKRMStateStoreTester.TestZKRMStateStoreInternal) (zkTester.getRMStateStore())).testRetryingCreateRootDir();
        testProxyCA(zkTester);
    }

    @Test
    public void testZKNodeLimit() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        long submitTime = System.currentTimeMillis();
        long startTime = (System.currentTimeMillis()) + 1234;
        Configuration conf = new YarnConfiguration();
        conf.setInt(RM_ZK_ZNODE_SIZE_LIMIT_BYTES, 1);
        RMStateStore store = zkTester.getRMStateStore(conf);
        TestZKRMStateStore.TestAppRejDispatcher dispatcher = new TestZKRMStateStore.TestAppRejDispatcher();
        store.setRMDispatcher(dispatcher);
        ApplicationId appId1 = ApplicationId.fromString("application_1352994193343_0001");
        storeApp(store, appId1, submitTime, startTime);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return dispatcher.appsavefailedEvnt;
            }
        }, 100, 5000);
    }

    static class TestAppRejDispatcher extends RMStateStoreTestBase.TestDispatcher {
        private boolean appsavefailedEvnt;

        @Override
        public void handle(Event event) {
            if ((event instanceof RMAppEvent) && (event.getType().equals(APP_SAVE_FAILED))) {
                appsavefailedEvnt = true;
            }
        }
    }

    @Test(timeout = 60000)
    public void testCheckMajorVersionChange() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester() {
            Version VERSION_INFO = Version.newInstance(Integer.MAX_VALUE, 0);

            @Override
            public Version getCurrentVersion() throws Exception {
                return VERSION_INFO;
            }

            @Override
            public RMStateStore getRMStateStore() throws Exception {
                YarnConfiguration conf = new YarnConfiguration();
                workingZnode = "/jira/issue/3077/rmstore";
                conf.set(ZK_ADDRESS, curatorTestingServer.getConnectString());
                conf.set(ZK_RM_STATE_STORE_PARENT_PATH, workingZnode);
                this.store = new TestZKRMStateStore.TestZKRMStateStoreTester.TestZKRMStateStoreInternal(conf, workingZnode) {
                    Version storedVersion = null;

                    @Override
                    public Version getCurrentVersion() {
                        return VERSION_INFO;
                    }

                    @Override
                    protected synchronized Version loadVersion() throws Exception {
                        return storedVersion;
                    }

                    @Override
                    protected synchronized void storeVersion() throws Exception {
                        storedVersion = VERSION_INFO;
                    }
                };
                return this.store;
            }
        };
        // default version
        RMStateStore store = zkTester.getRMStateStore();
        Version defaultVersion = zkTester.getCurrentVersion();
        store.checkVersion();
        Assert.assertEquals("Store had wrong version", defaultVersion, store.loadVersion());
    }

    /**
     * Test if RM can successfully start in HA disabled mode if it was previously
     * running in HA enabled mode. And then start it in HA mode after running it
     * with HA disabled. NoAuth Exception should not be sent by zookeeper and RM
     * should start successfully.
     */
    @Test
    public void testZKRootPathAcls() throws Exception {
        StateChangeRequestInfo req = new StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);
        String parentPath = YarnConfiguration.DEFAULT_ZK_RM_STATE_STORE_PARENT_PATH;
        String rootPath = (parentPath + "/") + (ROOT_ZNODE_NAME);
        // Start RM with HA enabled
        Configuration conf = TestZKRMStateStore.createHARMConf("rm1,rm2", "rm1", 1234, false, curatorTestingServer);
        conf.set(RM_ZK_ACL, "world:anyone:rwca");
        int perm = 23;// rwca=1+2+4+16

        ResourceManager rm = new MockRM(conf);
        rm.start();
        rm.getRMContext().getRMAdminService().transitionToActive(req);
        ZKRMStateStore stateStore = ((ZKRMStateStore) (rm.getRMContext().getStateStore()));
        List<ACL> acls = stateStore.getACL(rootPath);
        Assert.assertEquals(acls.size(), 2);
        // CREATE and DELETE permissions for root node based on RM ID
        TestZKRMStateStore.verifyZKACL("digest", "localhost", ((Perms.CREATE) | (Perms.DELETE)), acls);
        TestZKRMStateStore.verifyZKACL("world", "anyone", ((Perms.ALL) ^ ((Perms.CREATE) | (Perms.DELETE))), acls);
        acls = stateStore.getACL(parentPath);
        Assert.assertEquals(1, acls.size());
        Assert.assertEquals(perm, acls.get(0).getPerms());
        rm.close();
        // Now start RM with HA disabled. NoAuth Exception should not be thrown.
        conf.setBoolean(RM_HA_ENABLED, false);
        conf.set(RM_ZK_ACL, DEFAULT_RM_ZK_ACL);
        rm = new MockRM(conf);
        rm.start();
        rm.getRMContext().getRMAdminService().transitionToActive(req);
        acls = stateStore.getACL(rootPath);
        Assert.assertEquals(acls.size(), 1);
        TestZKRMStateStore.verifyZKACL("world", "anyone", ALL, acls);
        rm.close();
        // Start RM with HA enabled.
        conf.setBoolean(RM_HA_ENABLED, true);
        rm = new MockRM(conf);
        rm.start();
        rm.getRMContext().getRMAdminService().transitionToActive(req);
        acls = stateStore.getACL(rootPath);
        Assert.assertEquals(acls.size(), 2);
        TestZKRMStateStore.verifyZKACL("digest", "localhost", ((Perms.CREATE) | (Perms.DELETE)), acls);
        TestZKRMStateStore.verifyZKACL("world", "anyone", ((Perms.ALL) ^ ((Perms.CREATE) | (Perms.DELETE))), acls);
        rm.close();
    }

    @Test
    public void testFencing() throws Exception {
        StateChangeRequestInfo req = new StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);
        Configuration conf1 = TestZKRMStateStore.createHARMConf("rm1,rm2", "rm1", 1234, false, curatorTestingServer);
        ResourceManager rm1 = new MockRM(conf1);
        rm1.start();
        rm1.getRMContext().getRMAdminService().transitionToActive(req);
        Assert.assertEquals("RM with ZKStore didn't start", STARTED, rm1.getServiceState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm1.getRMContext().getRMAdminService().getServiceStatus().getState());
        Configuration conf2 = TestZKRMStateStore.createHARMConf("rm1,rm2", "rm2", 5678, false, curatorTestingServer);
        ResourceManager rm2 = new MockRM(conf2);
        rm2.start();
        rm2.getRMContext().getRMAdminService().transitionToActive(req);
        Assert.assertEquals("RM with ZKStore didn't start", STARTED, rm2.getServiceState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        for (int i = 0; i < ((TestZKRMStateStore.ZK_TIMEOUT_MS) / 50); i++) {
            if ((HAServiceState.ACTIVE) == (rm1.getRMContext().getRMAdminService().getServiceStatus().getState())) {
                Thread.sleep(100);
            }
        }
        Assert.assertEquals("RM should have been fenced", STANDBY, rm1.getRMContext().getRMAdminService().getServiceStatus().getState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        rm1.close();
        rm2.close();
    }

    @Test
    public void testFencedState() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        RMStateStore store = zkTester.getRMStateStore();
        // Move state to FENCED from ACTIVE
        store.updateFencedState();
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        long submitTime = System.currentTimeMillis();
        long startTime = submitTime + 1000;
        // Add a new app
        RMApp mockApp = Mockito.mock(RMApp.class);
        ApplicationSubmissionContext context = new ApplicationSubmissionContextPBImpl();
        Mockito.when(mockApp.getSubmitTime()).thenReturn(submitTime);
        Mockito.when(mockApp.getStartTime()).thenReturn(startTime);
        Mockito.when(mockApp.getApplicationSubmissionContext()).thenReturn(context);
        Mockito.when(mockApp.getUser()).thenReturn("test");
        store.storeNewApplication(mockApp);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // Add a new attempt
        ClientToAMTokenSecretManagerInRM clientToAMTokenMgr = new ClientToAMTokenSecretManagerInRM();
        ApplicationAttemptId attemptId = ApplicationAttemptId.fromString("appattempt_1234567894321_0001_000001");
        SecretKey clientTokenMasterKey = clientToAMTokenMgr.createMasterKey(attemptId);
        RMAppAttemptMetrics mockRmAppAttemptMetrics = Mockito.mock(RMAppAttemptMetrics.class);
        Container container = new ContainerPBImpl();
        container.setId(ContainerId.fromString("container_1234567891234_0001_01_000001"));
        RMAppAttempt mockAttempt = Mockito.mock(RMAppAttempt.class);
        Mockito.when(mockAttempt.getAppAttemptId()).thenReturn(attemptId);
        Mockito.when(mockAttempt.getMasterContainer()).thenReturn(container);
        Mockito.when(mockAttempt.getClientTokenMasterKey()).thenReturn(clientTokenMasterKey);
        Mockito.when(mockAttempt.getRMAppAttemptMetrics()).thenReturn(mockRmAppAttemptMetrics);
        Mockito.when(mockRmAppAttemptMetrics.getAggregateAppResourceUsage()).thenReturn(new AggregateAppResourceUsage(new HashMap()));
        store.storeNewApplicationAttempt(mockAttempt);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        long finishTime = submitTime + 1000;
        // Update attempt
        ApplicationAttemptStateData newAttemptState = ApplicationAttemptStateData.newInstance(attemptId, container, store.getCredentialsFromAppAttempt(mockAttempt), startTime, FINISHED, "testUrl", "test", SUCCEEDED, 100, finishTime, new HashMap(), new HashMap());
        store.updateApplicationAttemptState(newAttemptState);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // Update app
        ApplicationStateData appState = ApplicationStateData.newInstance(submitTime, startTime, context, "test");
        store.updateApplicationState(appState);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // Remove app
        store.removeApplication(mockApp);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // store RM delegation token;
        RMDelegationTokenIdentifier dtId1 = new RMDelegationTokenIdentifier(new Text("owner1"), new Text("renewer1"), new Text("realuser1"));
        Long renewDate1 = new Long(System.currentTimeMillis());
        dtId1.setSequenceNumber(1111);
        Assert.assertFalse((("Token " + dtId1) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(dtId1, 0));
        store.storeRMDelegationToken(dtId1, renewDate1);
        Assert.assertFalse((("Token " + dtId1) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(dtId1, 0));
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        store.updateRMDelegationToken(dtId1, renewDate1);
        Assert.assertFalse((("Token " + dtId1) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(dtId1, 0));
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // remove delegation key;
        store.removeRMDelegationToken(dtId1);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // store delegation master key;
        DelegationKey key = new DelegationKey(1234, 4321, "keyBytes".getBytes());
        store.storeRMDTMasterKey(key);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // remove delegation master key;
        store.removeRMDTMasterKey(key);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        // store or update AMRMToken;
        store.storeOrUpdateAMRMTokenSecretManager(null, false);
        Assert.assertEquals("RMStateStore should have been in fenced state", true, store.isFencedState());
        store.close();
    }

    @Test
    public void testDuplicateRMAppDeletion() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        long submitTime = System.currentTimeMillis();
        long startTime = (System.currentTimeMillis()) + 1234;
        RMStateStore store = zkTester.getRMStateStore();
        RMStateStoreTestBase.TestDispatcher dispatcher = new RMStateStoreTestBase.TestDispatcher();
        store.setRMDispatcher(dispatcher);
        ApplicationAttemptId attemptIdRemoved = ApplicationAttemptId.fromString("appattempt_1352994193343_0002_000001");
        ApplicationId appIdRemoved = attemptIdRemoved.getApplicationId();
        storeApp(store, appIdRemoved, submitTime, startTime);
        storeAttempt(store, attemptIdRemoved, "container_1352994193343_0002_01_000001", null, null, dispatcher);
        ApplicationSubmissionContext context = new ApplicationSubmissionContextPBImpl();
        context.setApplicationId(appIdRemoved);
        ApplicationStateData appStateRemoved = ApplicationStateData.newInstance(submitTime, startTime, context, "user1");
        appStateRemoved.attempts.put(attemptIdRemoved, null);
        store.removeApplicationStateInternal(appStateRemoved);
        try {
            store.removeApplicationStateInternal(appStateRemoved);
        } catch (KeeperException nne) {
            Assert.fail("NoNodeException should not happen.");
        }
        store.close();
    }

    // Test to verify storing of apps and app attempts in ZK state store with app
    // node split index configured more than 0.
    @Test
    public void testAppNodeSplit() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        long submitTime = System.currentTimeMillis();
        long startTime = submitTime + 1234;
        Configuration conf = new YarnConfiguration();
        // Get store with app node split config set as 1.
        RMStateStore store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(1));
        RMStateStoreTestBase.TestDispatcher dispatcher = new RMStateStoreTestBase.TestDispatcher();
        store.setRMDispatcher(dispatcher);
        // Create RM Context and app token manager.
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(store);
        AMRMTokenSecretManager appTokenMgr = Mockito.spy(new AMRMTokenSecretManager(conf, rmContext));
        MasterKeyData masterKeyData = appTokenMgr.createNewMasterKey();
        Mockito.when(appTokenMgr.getMasterKey()).thenReturn(masterKeyData);
        ClientToAMTokenSecretManagerInRM clientToAMTokenMgr = new ClientToAMTokenSecretManagerInRM();
        // Store app1.
        ApplicationId appId1 = ApplicationId.newInstance(1352994193343L, 1);
        ApplicationAttemptId attemptId1 = ApplicationAttemptId.newInstance(appId1, 1);
        ApplicationAttemptId attemptId2 = ApplicationAttemptId.newInstance(appId1, 2);
        storeAppWithAttempts(store, dispatcher, submitTime, startTime, appTokenMgr, clientToAMTokenMgr, attemptId1, attemptId2);
        // Store app2 with app id application_1352994193343_120213.
        ApplicationId appId21 = ApplicationId.newInstance(1352994193343L, 120213);
        storeApp(store, appId21, submitTime, startTime);
        waitNotify(dispatcher);
        // Store another app which will be removed.
        ApplicationId appIdRemoved = ApplicationId.newInstance(1352994193343L, 2);
        ApplicationAttemptId attemptIdRemoved = ApplicationAttemptId.newInstance(appIdRemoved, 1);
        storeAppWithAttempts(store, dispatcher, submitTime, startTime, null, null, attemptIdRemoved);
        // Remove the app.
        RMApp mockRemovedApp = TestZKRMStateStore.createMockAppForRemove(appIdRemoved, attemptIdRemoved);
        store.removeApplication(mockRemovedApp);
        // Close state store
        store.close();
        // Load state store
        store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(1));
        store.setRMDispatcher(dispatcher);
        RMState state = store.loadState();
        // Check if application_1352994193343_120213 (i.e. app2) exists in state
        // store as per split index.
        TestZKRMStateStore.verifyAppPathPath(store, appId21, 1);
        // Verify loaded apps and attempts based on the operations we did before
        // reloading the state store.
        TestZKRMStateStore.verifyLoadedApp(state, appId1, submitTime, startTime, 0, false, Lists.newArrayList(attemptId1, attemptId2), Lists.newArrayList((-1000), (-1000)), Lists.newArrayList(((FinalApplicationStatus) (null)), null));
        // Update app state for app1.
        finishAppWithAttempts(state, store, dispatcher, attemptId2, submitTime, startTime, 100, 1234, false);
        // Test updating app/attempt for app whose initial state is not saved
        ApplicationId dummyAppId = ApplicationId.newInstance(1234, 10);
        ApplicationAttemptId dummyAttemptId = ApplicationAttemptId.newInstance(dummyAppId, 6);
        finishAppWithAttempts(state, store, dispatcher, dummyAttemptId, submitTime, startTime, 111, 1234, true);
        // Close the store
        store.close();
        // Check updated application state.
        store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(1));
        store.setRMDispatcher(dispatcher);
        RMState newRMState = store.loadState();
        TestZKRMStateStore.verifyLoadedApp(newRMState, dummyAppId, submitTime, startTime, 1234, true, Lists.newArrayList(dummyAttemptId), Lists.newArrayList(111), Lists.newArrayList(SUCCEEDED));
        TestZKRMStateStore.verifyLoadedApp(newRMState, appId1, submitTime, startTime, 1234, true, Lists.newArrayList(attemptId1, attemptId2), Lists.newArrayList((-1000), 100), Lists.newArrayList(null, SUCCEEDED));
        // assert store is in expected state after everything is cleaned
        Assert.assertTrue("Store is not in expected state", zkTester.isFinalStateValid());
        store.close();
    }

    // Test to verify storing of apps and app attempts in ZK state store with app
    // node split index config changing across restarts.
    @Test
    public void testAppNodeSplitChangeAcrossRestarts() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        long submitTime = System.currentTimeMillis();
        long startTime = submitTime + 1234;
        Configuration conf = new YarnConfiguration();
        // Create store with app node split set as 1.
        RMStateStore store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(1));
        RMStateStoreTestBase.TestDispatcher dispatcher = new RMStateStoreTestBase.TestDispatcher();
        store.setRMDispatcher(dispatcher);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(store);
        AMRMTokenSecretManager appTokenMgr = Mockito.spy(new AMRMTokenSecretManager(conf, rmContext));
        MasterKeyData masterKeyData = appTokenMgr.createNewMasterKey();
        Mockito.when(appTokenMgr.getMasterKey()).thenReturn(masterKeyData);
        ClientToAMTokenSecretManagerInRM clientToAMTokenMgr = new ClientToAMTokenSecretManagerInRM();
        // Store app1 with 2 attempts.
        ApplicationId appId1 = ApplicationId.newInstance(1442994194053L, 1);
        ApplicationAttemptId attemptId1 = ApplicationAttemptId.newInstance(appId1, 1);
        ApplicationAttemptId attemptId2 = ApplicationAttemptId.newInstance(appId1, 2);
        storeAppWithAttempts(store, dispatcher, submitTime, startTime, appTokenMgr, clientToAMTokenMgr, attemptId1, attemptId2);
        // Store app2 and associated attempt.
        ApplicationId appId11 = ApplicationId.newInstance(1442994194053L, 2);
        ApplicationAttemptId attemptId11 = ApplicationAttemptId.newInstance(appId11, 1);
        storeAppWithAttempts(store, dispatcher, attemptId11, submitTime, startTime);
        // Close state store
        store.close();
        // Load state store with app node split config of 2.
        store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(2));
        store.setRMDispatcher(dispatcher);
        RMState state = store.loadState();
        ApplicationId appId21 = ApplicationId.newInstance(1442994194053L, 120213);
        storeApp(store, dispatcher, appId21, submitTime, startTime);
        // Check if app is loaded correctly despite change in split index.
        TestZKRMStateStore.verifyLoadedApp(state, appId1, submitTime, startTime, 0, false, Lists.newArrayList(attemptId1, attemptId2), Lists.newArrayList((-1000), (-1000)), Lists.newArrayList(((FinalApplicationStatus) (null)), null));
        // Finish app/attempt state
        finishAppWithAttempts(state, store, dispatcher, attemptId2, submitTime, startTime, 100, 1234, false);
        // Test updating app/attempt for app whose initial state is not saved
        ApplicationId dummyAppId = ApplicationId.newInstance(1234, 10);
        ApplicationAttemptId dummyAttemptId = ApplicationAttemptId.newInstance(dummyAppId, 6);
        finishAppWithAttempts(state, store, dispatcher, dummyAttemptId, submitTime, startTime, 111, 1234, true);
        // Close the store
        store.close();
        // Load state store this time with split index of 0.
        store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(0));
        store.setRMDispatcher(dispatcher);
        state = store.loadState();
        Assert.assertEquals("Number of Apps loaded should be 4.", 4, state.getApplicationState().size());
        TestZKRMStateStore.verifyLoadedApp(state, appId1, submitTime, startTime, 1234, true, Lists.newArrayList(attemptId1, attemptId2), Lists.newArrayList((-1000), 100), Lists.newArrayList(null, SUCCEEDED));
        // Remove attempt1
        store.removeApplicationAttempt(attemptId1);
        ApplicationId appId31 = ApplicationId.newInstance(1442994195071L, 45);
        storeApp(store, dispatcher, appId31, submitTime, startTime);
        // Close state store.
        store.close();
        // Load state store with split index of 3.
        store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(3));
        store.setRMDispatcher(dispatcher);
        state = store.loadState();
        Assert.assertEquals("Number of apps loaded should be 5.", 5, state.getApplicationState().size());
        TestZKRMStateStore.verifyLoadedApp(state, dummyAppId, submitTime, startTime, 1234, true, Lists.newArrayList(dummyAttemptId), Lists.newArrayList(111), Lists.newArrayList(SUCCEEDED));
        TestZKRMStateStore.verifyLoadedApp(state, appId31, submitTime, startTime, 0, false, null);
        TestZKRMStateStore.verifyLoadedApp(state, appId21, submitTime, startTime, 0, false, null);
        TestZKRMStateStore.verifyLoadedApp(state, appId11, submitTime, startTime, 0, false, Lists.newArrayList(attemptId11), Lists.newArrayList((-1000)), Lists.newArrayList(((FinalApplicationStatus) (null))));
        TestZKRMStateStore.verifyLoadedApp(state, appId1, submitTime, startTime, 1234, true, Lists.newArrayList(attemptId2), Lists.newArrayList(100), Lists.newArrayList(SUCCEEDED));
        // Store another app.
        ApplicationId appId41 = ApplicationId.newInstance(1442994195087L, 1);
        storeApp(store, dispatcher, appId41, submitTime, startTime);
        // Check how many apps exist in each of the hierarchy based paths. 0 paths
        // should exist in "HIERARCHIES/4" path as app split index was never set
        // as 4 in tests above.
        TestZKRMStateStore.assertHierarchicalPaths(store, ImmutableMap.of(0, 2, 1, 1, 2, 2, 3, 1, 4, 0));
        TestZKRMStateStore.verifyAppInHierarchicalPath(store, "application_1442994195087_0001", 3);
        ApplicationId appId71 = ApplicationId.newInstance(1442994195087L, 7);
        // storeApp(store, dispatcher, appId71, submitTime, startTime);
        storeApp(store, appId71, submitTime, startTime);
        waitNotify(dispatcher);
        ApplicationAttemptId attemptId71 = ApplicationAttemptId.newInstance(appId71, 1);
        storeAttempt(store, ApplicationAttemptId.newInstance(appId71, 1), ContainerId.newContainerId(attemptId71, 1).toString(), null, null, dispatcher);
        // Remove applications.
        TestZKRMStateStore.removeApps(store, ImmutableMap.of(appId11, new ApplicationAttemptId[]{ attemptId11 }, appId71, new ApplicationAttemptId[]{ attemptId71 }, appId41, new ApplicationAttemptId[0], appId31, new ApplicationAttemptId[0], appId21, new ApplicationAttemptId[0]));
        TestZKRMStateStore.removeApps(store, ImmutableMap.of(dummyAppId, new ApplicationAttemptId[]{ dummyAttemptId }, appId1, new ApplicationAttemptId[]{ attemptId1, attemptId2 }));
        store.close();
        // Load state store with split index of 3 again. As all apps have been
        // removed nothing should be loaded back.
        store = zkTester.getRMStateStore(TestZKRMStateStore.createConfForAppNodeSplit(3));
        store.setRMDispatcher(dispatcher);
        state = store.loadState();
        Assert.assertEquals("Number of apps loaded should be 0.", 0, state.getApplicationState().size());
        // Close the state store.
        store.close();
    }

    @Test
    public void testDelegationTokenSplitIndexConfig() throws Exception {
        // Valid values
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit(0)).close();
        Assert.assertEquals("Incorrect split index", 0, zkTester.getDelegationTokenNodeSplitIndex());
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit(1)).close();
        Assert.assertEquals("Incorrect split index", 1, zkTester.getDelegationTokenNodeSplitIndex());
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit(2)).close();
        Assert.assertEquals("Incorrect split index", 2, zkTester.getDelegationTokenNodeSplitIndex());
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit(3)).close();
        Assert.assertEquals("Incorrect split index", 3, zkTester.getDelegationTokenNodeSplitIndex());
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit(4)).close();
        Assert.assertEquals("Incorrect split index", 4, zkTester.getDelegationTokenNodeSplitIndex());
        // Invalid values --> override to 0
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit((-1))).close();
        Assert.assertEquals("Incorrect split index", 0, zkTester.getDelegationTokenNodeSplitIndex());
        zkTester.getRMStateStore(TestZKRMStateStore.createConfForDelegationTokenNodeSplit(5)).close();
        Assert.assertEquals("Incorrect split index", 0, zkTester.getDelegationTokenNodeSplitIndex());
    }

    @Test
    public void testDelegationTokenNodeNoSplit() throws Exception {
        testDelegationTokenNode(0);
    }

    @Test
    public void testDelegationTokenNodeWithSplitOne() throws Exception {
        testDelegationTokenNode(1);
    }

    @Test
    public void testDelegationTokenNodeWithSplitTwo() throws Exception {
        testDelegationTokenNode(2);
    }

    @Test
    public void testDelegationTokenNodeWithSplitThree() throws Exception {
        testDelegationTokenNode(3);
    }

    @Test
    public void testDelegationTokenNodeWithSplitFour() throws Exception {
        testDelegationTokenNode(4);
    }

    @Test
    public void testDelegationTokenNodeWithSplitMultiple() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        Configuration conf = TestZKRMStateStore.createConfForDelegationTokenNodeSplit(1);
        RMStateStore store = zkTester.getRMStateStore(conf);
        // With the split set to 1, we can store 10 tokens under a znode (i.e. 0-9)
        // Try to store more than 10
        Map<RMDelegationTokenIdentifier, Long> tokensWithRenewal = new HashMap<>();
        Map<RMDelegationTokenIdentifier, Integer> tokensWithIndex = new HashMap<>();
        Set<RMDelegationTokenIdentifier> tokensToDelete = new HashSet<>();
        int sequenceNumber = 0;
        for (int i = 0; i <= 12; i++) {
            RMDelegationTokenIdentifier token = new RMDelegationTokenIdentifier(new Text(("owner" + i)), new Text(("renewer" + i)), new Text(("realuser" + i)));
            sequenceNumber = i;
            token.setSequenceNumber(sequenceNumber);
            Assert.assertFalse("Token should not exist but was found in ZooKeeper", zkTester.delegationTokenExists(token, 1));
            Long renewDate = System.currentTimeMillis();
            store.storeRMDelegationToken(token, renewDate);
            modifyRMDelegationTokenState();
            tokensWithRenewal.put(token, renewDate);
            tokensWithIndex.put(token, 1);
            switch (i) {
                case 0 :
                case 3 :
                case 6 :
                case 11 :
                    tokensToDelete.add(token);
                    break;
                default :
                    break;
            }
        }
        // Verify
        verifyDelegationTokensStateStore(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber);
        // Try deleting some tokens and adding some new ones
        for (RMDelegationTokenIdentifier tokenToDelete : tokensToDelete) {
            store.removeRMDelegationToken(tokenToDelete);
            tokensWithRenewal.remove(tokenToDelete);
            tokensWithIndex.remove(tokenToDelete);
        }
        for (int i = 13; i <= 22; i++) {
            RMDelegationTokenIdentifier token = new RMDelegationTokenIdentifier(new Text(("owner" + i)), new Text(("renewer" + i)), new Text(("realuser" + i)));
            sequenceNumber = i;
            token.setSequenceNumber(sequenceNumber);
            Long renewDate = System.currentTimeMillis();
            store.storeRMDelegationToken(token, renewDate);
            modifyRMDelegationTokenState();
            tokensWithRenewal.put(token, renewDate);
            tokensWithIndex.put(token, 1);
        }
        // Verify
        verifyDelegationTokensStateStore(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber);
        for (RMDelegationTokenIdentifier token : tokensToDelete) {
            Assert.assertFalse((("Token " + token) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(token, 1));
        }
        store.close();
    }

    @Test
    public void testDelegationTokenNodeWithSplitChangeAcrossRestarts() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        Map<RMDelegationTokenIdentifier, Long> tokensWithRenewal = new HashMap<>();
        Map<RMDelegationTokenIdentifier, Integer> tokensWithIndex = new HashMap<>();
        int sequenceNumber = 0;
        // Start the store with index 1
        Configuration conf = TestZKRMStateStore.createConfForDelegationTokenNodeSplit(1);
        RMStateStore store = zkTester.getRMStateStore(conf);
        // Store a token with index 1
        RMDelegationTokenIdentifier token1 = storeUpdateAndVerifyDelegationToken(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber, 1);
        store.close();
        // Start the store with index 2
        conf = TestZKRMStateStore.createConfForDelegationTokenNodeSplit(2);
        store = zkTester.getRMStateStore(conf);
        // Verify token1 is still there and under the /1/ znode
        verifyDelegationTokenInStateStore(zkTester, token1, tokensWithRenewal.get(token1), 1);
        // Store a token with index 2
        sequenceNumber++;
        RMDelegationTokenIdentifier token2 = storeUpdateAndVerifyDelegationToken(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber, 2);
        // Update and verify token1
        long renewDate1 = System.currentTimeMillis();
        zkTester.store.updateRMDelegationToken(token1, renewDate1);
        tokensWithRenewal.put(token1, renewDate1);
        verifyDelegationTokenInStateStore(zkTester, token1, tokensWithRenewal.get(token1), 1);
        store.close();
        // Start the store with index 0
        conf = TestZKRMStateStore.createConfForDelegationTokenNodeSplit(0);
        store = zkTester.getRMStateStore(conf);
        // Verify token1 is still there and under the /1/ znode
        verifyDelegationTokenInStateStore(zkTester, token1, tokensWithRenewal.get(token1), 1);
        // Verify token2 is still there and under the /2/ znode
        verifyDelegationTokenInStateStore(zkTester, token2, tokensWithRenewal.get(token2), 2);
        // Store a token with no index
        sequenceNumber++;
        RMDelegationTokenIdentifier token0 = storeUpdateAndVerifyDelegationToken(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber, 0);
        store.close();
        // Start the store with index 3
        conf = TestZKRMStateStore.createConfForDelegationTokenNodeSplit(3);
        store = zkTester.getRMStateStore(conf);
        // Verify token1 is still there and under the /1/ znode
        verifyDelegationTokenInStateStore(zkTester, token1, tokensWithRenewal.get(token1), 1);
        // Verify token2 is still there and under the /2/ znode
        verifyDelegationTokenInStateStore(zkTester, token2, tokensWithRenewal.get(token2), 2);
        // Verify token0 is still there and under the token root node
        verifyDelegationTokenInStateStore(zkTester, token0, tokensWithRenewal.get(token0), 0);
        // Delete all tokens and verify
        for (RMDelegationTokenIdentifier token : tokensWithRenewal.keySet()) {
            store.removeRMDelegationToken(token);
        }
        tokensWithRenewal.clear();
        tokensWithIndex.clear();
        verifyDelegationTokensStateStore(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber);
        Assert.assertFalse((("Token " + token1) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(token1, 1));
        Assert.assertFalse((("Token " + token1) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(token2, 2));
        Assert.assertFalse((("Token " + token1) + " should not exist but was found in ZooKeeper"), zkTester.delegationTokenExists(token0, 0));
        // Store a token with index 3
        sequenceNumber++;
        storeUpdateAndVerifyDelegationToken(zkTester, tokensWithRenewal, tokensWithIndex, sequenceNumber, 3);
        store.close();
    }

    @Test
    public void testAppSubmissionContextIsPrunedInFinalApplicationState() throws Exception {
        TestZKRMStateStore.TestZKRMStateStoreTester zkTester = new TestZKRMStateStore.TestZKRMStateStoreTester();
        ApplicationId appId = ApplicationId.fromString("application_1234_0010");
        Configuration conf = TestZKRMStateStore.createConfForDelegationTokenNodeSplit(1);
        RMStateStore store = zkTester.getRMStateStore(conf);
        ApplicationSubmissionContext ctx = new ApplicationSubmissionContextPBImpl();
        ctx.setApplicationId(appId);
        ctx.setQueue("a_queue");
        ContainerLaunchContextPBImpl containerLaunchCtx = new ContainerLaunchContextPBImpl();
        containerLaunchCtx.setCommands(Collections.singletonList("a_command"));
        ctx.setAMContainerSpec(containerLaunchCtx);
        Resource resource = new ResourcePBImpl();
        resource.setMemorySize(17L);
        ctx.setResource(resource);
        Map<String, String> schedulingPropertiesMap = Collections.singletonMap("a_key", "a_value");
        ctx.setApplicationSchedulingPropertiesMap(schedulingPropertiesMap);
        ApplicationStateDataPBImpl appState = new ApplicationStateDataPBImpl();
        appState.setState(RUNNING);
        appState.setApplicationSubmissionContext(ctx);
        store.storeApplicationStateInternal(appId, appState);
        RMState rmState = store.loadState();
        Assert.assertEquals(1, rmState.getApplicationState().size());
        ctx = rmState.getApplicationState().get(appId).getApplicationSubmissionContext();
        appState.setState(RUNNING);
        store.handleStoreEvent(new RMStateUpdateAppEvent(appState, false, null));
        rmState = store.loadState();
        ctx = rmState.getApplicationState().get(appId).getApplicationSubmissionContext();
        Assert.assertEquals(("ApplicationSchedulingPropertiesMap should not have been " + ("pruned from the application submission context before the " + "FINISHED state")), schedulingPropertiesMap, ctx.getApplicationSchedulingPropertiesMap());
        appState.setState(RMAppState.FINISHED);
        store.handleStoreEvent(new RMStateUpdateAppEvent(appState, false, null));
        rmState = store.loadState();
        ctx = rmState.getApplicationState().get(appId).getApplicationSubmissionContext();
        Assert.assertEquals(appId, ctx.getApplicationId());
        Assert.assertEquals("a_queue", ctx.getQueue());
        Assert.assertNotNull(ctx.getAMContainerSpec());
        Assert.assertEquals(17L, ctx.getResource().getMemorySize());
        Assert.assertEquals(("ApplicationSchedulingPropertiesMap should have been pruned" + " from the application submission context when in FINISHED STATE"), Collections.emptyMap(), ctx.getApplicationSchedulingPropertiesMap());
        store.close();
    }
}

