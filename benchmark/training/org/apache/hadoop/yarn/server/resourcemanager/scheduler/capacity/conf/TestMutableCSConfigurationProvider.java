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


import YarnConfiguration.FS_CONFIGURATION_STORE;
import YarnConfiguration.MEMORY_CONFIGURATION_STORE;
import YarnConfiguration.SCHEDULER_CONFIGURATION_FS_PATH;
import YarnConfiguration.SCHEDULER_CONFIGURATION_STORE_CLASS;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.AdminService;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.webapp.dao.SchedConfUpdateInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link MutableCSConfigurationProvider}.
 */
public class TestMutableCSConfigurationProvider {
    private MutableCSConfigurationProvider confProvider;

    private RMContext rmContext;

    private SchedConfUpdateInfo goodUpdate;

    private SchedConfUpdateInfo badUpdate;

    private CapacityScheduler cs;

    private AdminService adminService;

    private static final UserGroupInformation TEST_USER = UserGroupInformation.createUserForTesting("testUser", new String[]{  });

    @Test
    public void testInMemoryBackedProvider() throws Exception {
        Configuration conf = new Configuration();
        conf.set(SCHEDULER_CONFIGURATION_STORE_CLASS, MEMORY_CONFIGURATION_STORE);
        confProvider.init(conf);
        Assert.assertNull(confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.goodKey"));
        confProvider.logAndApplyMutation(TestMutableCSConfigurationProvider.TEST_USER, goodUpdate);
        confProvider.confirmPendingMutation(true);
        Assert.assertEquals("goodVal", confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.goodKey"));
        Assert.assertNull(confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.badKey"));
        confProvider.logAndApplyMutation(TestMutableCSConfigurationProvider.TEST_USER, badUpdate);
        confProvider.confirmPendingMutation(false);
        Assert.assertNull(confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.badKey"));
    }

    @Test
    public void testHDFSBackedProvider() throws Exception {
        File testSchedulerConfigurationDir = new File(((TestMutableCSConfigurationProvider.class.getResource("").getPath()) + (TestMutableCSConfigurationProvider.class.getSimpleName())));
        FileUtils.deleteDirectory(testSchedulerConfigurationDir);
        testSchedulerConfigurationDir.mkdirs();
        Configuration conf = new Configuration(false);
        conf.set(SCHEDULER_CONFIGURATION_STORE_CLASS, FS_CONFIGURATION_STORE);
        conf.set(SCHEDULER_CONFIGURATION_FS_PATH, testSchedulerConfigurationDir.getAbsolutePath());
        writeConf(conf, testSchedulerConfigurationDir.getAbsolutePath());
        confProvider.init(conf);
        Assert.assertNull(confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.goodKey"));
        confProvider.logAndApplyMutation(TestMutableCSConfigurationProvider.TEST_USER, goodUpdate);
        confProvider.confirmPendingMutation(true);
        Assert.assertEquals("goodVal", confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.goodKey"));
        Assert.assertNull(confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.badKey"));
        confProvider.logAndApplyMutation(TestMutableCSConfigurationProvider.TEST_USER, badUpdate);
        confProvider.confirmPendingMutation(false);
        Assert.assertNull(confProvider.loadConfiguration(conf).get("yarn.scheduler.capacity.root.a.badKey"));
    }
}

