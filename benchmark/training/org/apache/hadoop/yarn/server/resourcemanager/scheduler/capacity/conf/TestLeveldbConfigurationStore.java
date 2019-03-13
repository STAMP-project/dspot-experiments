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


import LeveldbConfigurationStore.CURRENT_VERSION_INFO;
import YarnConfiguration.RM_SCHEDCONF_MAX_LOGS;
import YarnConfigurationStore.LogMutation;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.MutableConfigurationProvider;
import org.apache.hadoop.yarn.webapp.dao.SchedConfUpdateInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests {@link LeveldbConfigurationStore}.
 */
public class TestLeveldbConfigurationStore extends ConfigurationStoreBaseTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestLeveldbConfigurationStore.class);

    private static final File TEST_DIR = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestLeveldbConfigurationStore.class.getName());

    private ResourceManager rm;

    @Test
    public void testVersioning() throws Exception {
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertNull(confStore.getConfStoreVersion());
        confStore.checkVersion();
        Assert.assertEquals(CURRENT_VERSION_INFO, confStore.getConfStoreVersion());
        confStore.close();
    }

    @Test
    public void testPersistConfiguration() throws Exception {
        schedConf.set("key", "val");
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
        confStore.close();
        // Create a new configuration store, and check for old configuration
        confStore = createConfStore();
        schedConf.set("key", "badVal");
        // Should ignore passed-in scheduler configuration.
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
        confStore.close();
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
        confStore.close();
        // Create a new configuration store, and check for updated configuration
        confStore = createConfStore();
        schedConf.set("key", "badVal");
        // Should ignore passed-in scheduler configuration.
        confStore.initialize(conf, schedConf, rmContext);
        Assert.assertEquals("val", confStore.retrieve().get("key"));
        confStore.close();
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
        logs = ((LeveldbConfigurationStore) (confStore)).getLogs();
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals("val1", logs.get(0).getUpdates().get("key1"));
        confStore.confirmMutation(true);
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals("val1", logs.get(0).getUpdates().get("key1"));
        Map<String, String> update2 = new HashMap<>();
        update2.put("key2", "val2");
        mutation = new YarnConfigurationStore.LogMutation(update2, ConfigurationStoreBaseTest.TEST_USER);
        confStore.logMutation(mutation);
        logs = ((LeveldbConfigurationStore) (confStore)).getLogs();
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
        logs = ((LeveldbConfigurationStore) (confStore)).getLogs();
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("val2", logs.get(0).getUpdates().get("key2"));
        Assert.assertEquals("val3", logs.get(1).getUpdates().get("key3"));
        confStore.confirmMutation(true);
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("val2", logs.get(0).getUpdates().get("key2"));
        Assert.assertEquals("val3", logs.get(1).getUpdates().get("key3"));
        confStore.close();
    }

    /**
     * When restarting, RM should read from current state of store, including
     * any updates from the previous RM instance.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRestartReadsFromUpdatedStore() throws Exception {
        ResourceManager rm1 = new MockRM(conf);
        rm1.start();
        Assert.assertNull(getConfiguration().get("key"));
        // Update configuration on RM
        SchedConfUpdateInfo schedConfUpdateInfo = new SchedConfUpdateInfo();
        schedConfUpdateInfo.getGlobalParams().put("key", "val");
        MutableConfigurationProvider confProvider = getMutableConfProvider();
        UserGroupInformation user = UserGroupInformation.createUserForTesting(ConfigurationStoreBaseTest.TEST_USER, new String[0]);
        confProvider.logAndApplyMutation(user, schedConfUpdateInfo);
        rm1.getResourceScheduler().reinitialize(conf, rm1.getRMContext());
        Assert.assertEquals("val", getConfiguration().get("key"));
        confProvider.confirmPendingMutation(true);
        Assert.assertEquals("val", getConfStore().retrieve().get("key"));
        // Next update is not persisted, it should not be recovered
        schedConfUpdateInfo.getGlobalParams().put("key", "badVal");
        confProvider.logAndApplyMutation(user, schedConfUpdateInfo);
        rm1.close();
        // Start RM2 and verifies it starts with updated configuration
        ResourceManager rm2 = new MockRM(conf);
        rm2.start();
        Assert.assertEquals("val", getConfStore().retrieve().get("key"));
        Assert.assertEquals("val", getConfiguration().get("key"));
        rm2.close();
    }
}

