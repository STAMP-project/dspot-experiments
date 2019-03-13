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
package org.apache.hadoop.yarn.server;


import YarnConfiguration.AUTO_FAILOVER_ENABLED;
import YarnConfiguration.RECOVERY_ENABLED;
import YarnConfiguration.RM_ADDRESS;
import YarnConfiguration.RM_CLUSTER_ID;
import YarnConfiguration.RM_HA_ENABLED;
import YarnConfiguration.RM_HA_ID;
import YarnConfiguration.RM_HA_IDS;
import YarnConfiguration.TIMELINE_SERVICE_ADDRESS;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.YARN_MINICLUSTER_FIXED_PORTS;
import YarnConfiguration.YARN_MINICLUSTER_USE_RPC;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.HATestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestMiniYarnCluster {
    @Test
    public void testTimelineServiceStartInMiniCluster() throws Exception {
        Configuration conf = new YarnConfiguration();
        int numNodeManagers = 1;
        int numLocalDirs = 1;
        int numLogDirs = 1;
        boolean enableAHS;
        /* Timeline service should not start if TIMELINE_SERVICE_ENABLED == false
        and enableAHS flag == false
         */
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, false);
        enableAHS = false;
        try (MiniYARNCluster cluster = new MiniYARNCluster(TestMiniYarnCluster.class.getSimpleName(), numNodeManagers, numLocalDirs, numLogDirs, numLogDirs, enableAHS)) {
            cluster.init(conf);
            start();
            // verify that the timeline service is not started.
            Assert.assertNull("Timeline Service should not have been started", getApplicationHistoryServer());
        }
        /* Timeline service should start if TIMELINE_SERVICE_ENABLED == true
        and enableAHS == false
         */
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        enableAHS = false;
        try (MiniYARNCluster cluster = new MiniYARNCluster(TestMiniYarnCluster.class.getSimpleName(), numNodeManagers, numLocalDirs, numLogDirs, numLogDirs, enableAHS)) {
            cluster.init(conf);
            // Verify that the timeline-service starts on ephemeral ports by default
            String hostname = MiniYARNCluster.getHostname();
            Assert.assertEquals((hostname + ":0"), conf.get(TIMELINE_SERVICE_ADDRESS));
            start();
            // Timeline service may sometime take a while to get started
            int wait = 0;
            while (((getApplicationHistoryServer()) == null) && (wait < 20)) {
                Thread.sleep(500);
                wait++;
            } 
            // verify that the timeline service is started.
            Assert.assertNotNull("Timeline Service should have been started", getApplicationHistoryServer());
        }
        /* Timeline service should start if TIMELINE_SERVICE_ENABLED == false
        and enableAHS == true
         */
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, false);
        enableAHS = true;
        try (MiniYARNCluster cluster = new MiniYARNCluster(TestMiniYarnCluster.class.getSimpleName(), numNodeManagers, numLocalDirs, numLogDirs, numLogDirs, enableAHS)) {
            cluster.init(conf);
            start();
            // Timeline service may sometime take a while to get started
            int wait = 0;
            while (((getApplicationHistoryServer()) == null) && (wait < 20)) {
                Thread.sleep(500);
                wait++;
            } 
            // verify that the timeline service is started.
            Assert.assertNotNull("Timeline Service should have been started", getApplicationHistoryServer());
        }
    }

    @Test
    public void testMultiRMConf() throws IOException {
        String RM1_NODE_ID = "rm1";
        String RM2_NODE_ID = "rm2";
        int RM1_PORT_BASE = 10000;
        int RM2_PORT_BASE = 20000;
        Configuration conf = new YarnConfiguration();
        conf.set(RM_CLUSTER_ID, "yarn-test-cluster");
        conf.setBoolean(RECOVERY_ENABLED, true);
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.setBoolean(AUTO_FAILOVER_ENABLED, false);
        conf.set(RM_HA_IDS, ((RM1_NODE_ID + ",") + RM2_NODE_ID));
        HATestUtil.setRpcAddressForRM(RM1_NODE_ID, RM1_PORT_BASE, conf);
        HATestUtil.setRpcAddressForRM(RM2_NODE_ID, RM2_PORT_BASE, conf);
        conf.setBoolean(YARN_MINICLUSTER_FIXED_PORTS, true);
        conf.setBoolean(YARN_MINICLUSTER_USE_RPC, true);
        try (MiniYARNCluster cluster = new MiniYARNCluster(TestMiniYarnCluster.class.getName(), 2, 0, 1, 1)) {
            cluster.init(conf);
            Configuration conf1 = getResourceManager(0).getConfig();
            Configuration conf2 = getResourceManager(1).getConfig();
            Assert.assertFalse((conf1 == conf2));
            Assert.assertEquals("0.0.0.0:18032", conf1.get(HAUtil.addSuffix(RM_ADDRESS, RM1_NODE_ID)));
            Assert.assertEquals("0.0.0.0:28032", conf1.get(HAUtil.addSuffix(RM_ADDRESS, RM2_NODE_ID)));
            Assert.assertEquals("rm1", conf1.get(RM_HA_ID));
            Assert.assertEquals("0.0.0.0:18032", conf2.get(HAUtil.addSuffix(RM_ADDRESS, RM1_NODE_ID)));
            Assert.assertEquals("0.0.0.0:28032", conf2.get(HAUtil.addSuffix(RM_ADDRESS, RM2_NODE_ID)));
            Assert.assertEquals("rm2", conf2.get(RM_HA_ID));
        }
    }
}

