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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.conf;


import HAServiceProtocol.HAServiceState;
import HAServiceProtocol.HAServiceState.ACTIVE;
import HAServiceProtocol.HAServiceState.STANDBY;
import HAServiceProtocol.RequestSource;
import HAServiceProtocol.StateChangeRequestInfo;
import Service.STATE.STARTED;
import YarnConfiguration.RM_SCHEDCONF_MAX_LOGS;
import YarnConfigurationStore.LogMutation;
import ZKConfigurationStore.CURRENT_VERSION_INFO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.MutableConfigurationProvider;
import org.apache.hadoop.yarn.webapp.dao.QueueConfigInfo;
import org.apache.hadoop.yarn.webapp.dao.SchedConfUpdateInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests {@link ZKConfigurationStore}.
 */
public class TestZKConfigurationStore extends ConfigurationStoreBaseTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestZKConfigurationStore.class);

    private static final int ZK_TIMEOUT_MS = 10000;

    private TestingServer curatorTestingServer;

    private CuratorFramework curatorFramework;

    private ResourceManager rm;

    @Test
    public void testVersioning() throws Exception {
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertNull(confStore.getConfStoreVersion());
        confStore.checkVersion();
        Assert.assertEquals(CURRENT_VERSION_INFO, confStore.getConfStoreVersion());
    }

    @Test
    public void testPersistConfiguration() throws Exception {
        schedConf.set("key", "val");
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
        // Create a new configuration store, and check for old configuration
        confStore = createConfStore();
        schedConf.set("key", "badVal");
        // Should ignore passed-in scheduler configuration.
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
    }

    @Test
    public void testPersistUpdatedConfiguration() throws Exception {
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertNull(confStore.retrieve().get("key"));
        Map<String, String> update = new HashMap<>();
        update.put("key", "val");
        YarnConfigurationStore.LogMutation mutation = new YarnConfigurationStore.LogMutation(update, ConfigurationStoreBaseTest.TEST_USER);
        confStore.logMutation(mutation);
        confStore.confirmMutation(true);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
        // Create a new configuration store, and check for updated configuration
        confStore = createConfStore();
        schedConf.set("key", "badVal");
        // Should ignore passed-in scheduler configuration.
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
    }

    @Test
    public void testMaxLogs() throws Exception {
        conf.setLong(RM_SCHEDCONF_MAX_LOGS, 2);
        confStore.initialize(conf, schedConf, rmContext);
        LinkedList<YarnConfigurationStore.LogMutation> logs = getLogs();
        Assert.assertEquals(0, logs.size());
        Map<String, String> update1 = new HashMap<>();
        update1.put("key1", "val1");
        YarnConfigurationStore.LogMutation mutation = new YarnConfigurationStore.LogMutation(update1, ConfigurationStoreBaseTest.TEST_USER);
        confStore.logMutation(mutation);
        logs = ((ZKConfigurationStore) (confStore)).getLogs();
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals("val1", logs.get(0).getUpdates().get("key1"));
        confStore.confirmMutation(true);
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals("val1", logs.get(0).getUpdates().get("key1"));
        Map<String, String> update2 = new HashMap<>();
        update2.put("key2", "val2");
        mutation = new YarnConfigurationStore.LogMutation(update2, ConfigurationStoreBaseTest.TEST_USER);
        confStore.logMutation(mutation);
        logs = ((ZKConfigurationStore) (confStore)).getLogs();
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("val1", logs.get(0).getUpdates().get("key1"));
        Assert.assertEquals("val2", logs.get(1).getUpdates().get("key2"));
        confStore.confirmMutation(true);
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("val1", logs.get(0).getUpdates().get("key1"));
        Assert.assertEquals("val2", logs.get(1).getUpdates().get("key2"));
        // Next update should purge first update from logs.
        Map<String, String> update3 = new HashMap<>();
        update3.put("key3", "val3");
        mutation = new YarnConfigurationStore.LogMutation(update3, ConfigurationStoreBaseTest.TEST_USER);
        confStore.logMutation(mutation);
        logs = ((ZKConfigurationStore) (confStore)).getLogs();
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("val2", logs.get(0).getUpdates().get("key2"));
        Assert.assertEquals("val3", logs.get(1).getUpdates().get("key3"));
        confStore.confirmMutation(true);
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("val2", logs.get(0).getUpdates().get("key2"));
        Assert.assertEquals("val3", logs.get(1).getUpdates().get("key3"));
    }

    /**
     * When failing over, new active RM should read from current state of store,
     * including any updates when the new active RM was in standby.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailoverReadsFromUpdatedStore() throws Exception {
        HAServiceProtocol.StateChangeRequestInfo req = new HAServiceProtocol.StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);
        Configuration conf1 = createRMHAConf("rm1,rm2", "rm1", 1234);
        ResourceManager rm1 = new MockRM(conf1);
        rm1.start();
        rm1.getRMContext().getRMAdminService().transitionToActive(req);
        Assert.assertEquals("RM with ZKStore didn't start", STARTED, rm1.getServiceState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm1.getRMContext().getRMAdminService().getServiceStatus().getState());
        Assert.assertNull(getConfiguration().get("key"));
        Configuration conf2 = createRMHAConf("rm1,rm2", "rm2", 5678);
        ResourceManager rm2 = new MockRM(conf2);
        rm2.start();
        Assert.assertEquals("RM should be Standby", STANDBY, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        // Update configuration on RM1
        SchedConfUpdateInfo schedConfUpdateInfo = new SchedConfUpdateInfo();
        schedConfUpdateInfo.getGlobalParams().put("key", "val");
        MutableConfigurationProvider confProvider = getMutableConfProvider();
        UserGroupInformation user = UserGroupInformation.createUserForTesting(ConfigurationStoreBaseTest.TEST_USER, new String[0]);
        confProvider.logAndApplyMutation(user, schedConfUpdateInfo);
        rm1.getResourceScheduler().reinitialize(conf1, rm1.getRMContext());
        Assert.assertEquals("val", getConfiguration().get("key"));
        confProvider.confirmPendingMutation(true);
        Assert.assertEquals("val", getConfStore().retrieve().get("key"));
        // Next update is not persisted, it should not be recovered
        schedConfUpdateInfo.getGlobalParams().put("key", "badVal");
        confProvider.logAndApplyMutation(user, schedConfUpdateInfo);
        // Start RM2 and verifies it starts with updated configuration
        rm2.getRMContext().getRMAdminService().transitionToActive(req);
        Assert.assertEquals("RM with ZKStore didn't start", STARTED, rm2.getServiceState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        for (int i = 0; i < ((TestZKConfigurationStore.ZK_TIMEOUT_MS) / 50); i++) {
            if ((HAServiceState.ACTIVE) == (rm1.getRMContext().getRMAdminService().getServiceStatus().getState())) {
                Thread.sleep(100);
            }
        }
        Assert.assertEquals("RM should have been fenced", STANDBY, rm1.getRMContext().getRMAdminService().getServiceStatus().getState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        Assert.assertEquals("val", getConfStore().retrieve().get("key"));
        Assert.assertEquals("val", getConfiguration().get("key"));
        // Transition to standby will set RM's HA status and then reinitialize in
        // a separate thread. Despite asserting for STANDBY state, it's
        // possible for reinitialization to be unfinished. Wait here for it to
        // finish, otherwise closing rm1 will close zkManager and the unfinished
        // reinitialization will throw an exception.
        Thread.sleep(10000);
        rm1.close();
        rm2.close();
    }

    /**
     * When failing over, if RM1 stopped and removed a queue that RM2 has in
     * memory, failing over to RM2 should not throw an exception.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailoverAfterRemoveQueue() throws Exception {
        HAServiceProtocol.StateChangeRequestInfo req = new HAServiceProtocol.StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);
        Configuration conf1 = createRMHAConf("rm1,rm2", "rm1", 1234);
        ResourceManager rm1 = new MockRM(conf1);
        rm1.start();
        rm1.getRMContext().getRMAdminService().transitionToActive(req);
        Assert.assertEquals("RM with ZKStore didn't start", STARTED, rm1.getServiceState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm1.getRMContext().getRMAdminService().getServiceStatus().getState());
        Configuration conf2 = createRMHAConf("rm1,rm2", "rm2", 5678);
        ResourceManager rm2 = new MockRM(conf2);
        rm2.start();
        Assert.assertEquals("RM should be Standby", STANDBY, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        UserGroupInformation user = UserGroupInformation.createUserForTesting(ConfigurationStoreBaseTest.TEST_USER, new String[0]);
        MutableConfigurationProvider confProvider = getMutableConfProvider();
        // Add root.a
        SchedConfUpdateInfo schedConfUpdateInfo = new SchedConfUpdateInfo();
        Map<String, String> addParams = new HashMap<>();
        addParams.put("capacity", "100");
        QueueConfigInfo addInfo = new QueueConfigInfo("root.a", addParams);
        schedConfUpdateInfo.getAddQueueInfo().add(addInfo);
        // Stop root.default
        Map<String, String> stopParams = new HashMap<>();
        stopParams.put("state", "STOPPED");
        stopParams.put("capacity", "0");
        QueueConfigInfo stopInfo = new QueueConfigInfo("root.default", stopParams);
        schedConfUpdateInfo.getUpdateQueueInfo().add(stopInfo);
        confProvider.logAndApplyMutation(user, schedConfUpdateInfo);
        rm1.getResourceScheduler().reinitialize(conf1, rm1.getRMContext());
        confProvider.confirmPendingMutation(true);
        Assert.assertTrue(Arrays.asList(getConfiguration().get("yarn.scheduler.capacity.root.queues").split(",")).contains("a"));
        // Remove root.default
        schedConfUpdateInfo.getUpdateQueueInfo().clear();
        schedConfUpdateInfo.getAddQueueInfo().clear();
        schedConfUpdateInfo.getRemoveQueueInfo().add("root.default");
        confProvider.logAndApplyMutation(user, schedConfUpdateInfo);
        rm1.getResourceScheduler().reinitialize(conf1, rm1.getRMContext());
        confProvider.confirmPendingMutation(true);
        Assert.assertEquals("a", getConfiguration().get("yarn.scheduler.capacity.root.queues"));
        // Start RM2 and verifies it starts with updated configuration
        rm2.getRMContext().getRMAdminService().transitionToActive(req);
        Assert.assertEquals("RM with ZKStore didn't start", STARTED, rm2.getServiceState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        for (int i = 0; i < ((TestZKConfigurationStore.ZK_TIMEOUT_MS) / 50); i++) {
            if ((HAServiceState.ACTIVE) == (rm1.getRMContext().getRMAdminService().getServiceStatus().getState())) {
                Thread.sleep(100);
            }
        }
        Assert.assertEquals("RM should have been fenced", STANDBY, rm1.getRMContext().getRMAdminService().getServiceStatus().getState());
        Assert.assertEquals("RM should be Active", ACTIVE, rm2.getRMContext().getRMAdminService().getServiceStatus().getState());
        Assert.assertEquals("a", getConfStore().retrieve().get("yarn.scheduler.capacity.root.queues"));
        Assert.assertEquals("a", getConfiguration().get("yarn.scheduler.capacity.root.queues"));
        // Transition to standby will set RM's HA status and then reinitialize in
        // a separate thread. Despite asserting for STANDBY state, it's
        // possible for reinitialization to be unfinished. Wait here for it to
        // finish, otherwise closing rm1 will close zkManager and the unfinished
        // reinitialization will throw an exception.
        Thread.sleep(10000);
        rm1.close();
        rm2.close();
    }
}

