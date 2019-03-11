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
package org.apache.hadoop.yarn.client;


import HAServiceProtocol.RequestSource;
import YarnConfiguration.AUTO_FAILOVER_ENABLED;
import YarnConfiguration.CLIENT_FAILOVER_PROXY_PROVIDER;
import YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS;
import YarnConfiguration.RM_CLUSTER_ID;
import YarnConfiguration.RM_HA_ENABLED;
import YarnConfiguration.RM_HA_IDS;
import YarnConfiguration.YARN_MINICLUSTER_FIXED_PORTS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.HATestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestHedgingRequestRMFailoverProxyProvider {
    @Test
    public void testHedgingRequestProxyProvider() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.setBoolean(AUTO_FAILOVER_ENABLED, false);
        conf.set(RM_CLUSTER_ID, "cluster1");
        conf.set(RM_HA_IDS, "rm1,rm2,rm3,rm4,rm5");
        conf.set(CLIENT_FAILOVER_PROXY_PROVIDER, RequestHedgingRMFailoverProxyProvider.class.getName());
        conf.setLong(RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, 2000);
        try (MiniYARNCluster cluster = new MiniYARNCluster("testHedgingRequestProxyProvider", 5, 0, 1, 1)) {
            HATestUtil.setRpcAddressForRM("rm1", 10000, conf);
            HATestUtil.setRpcAddressForRM("rm2", 20000, conf);
            HATestUtil.setRpcAddressForRM("rm3", 30000, conf);
            HATestUtil.setRpcAddressForRM("rm4", 40000, conf);
            HATestUtil.setRpcAddressForRM("rm5", 50000, conf);
            conf.setBoolean(YARN_MINICLUSTER_FIXED_PORTS, true);
            cluster.init(conf);
            cluster.start();
            final YarnClient client = YarnClient.createYarnClient();
            client.init(conf);
            client.start();
            // Transition rm5 to active;
            long start = System.currentTimeMillis();
            makeRMActive(cluster, 4);
            validateActiveRM(client);
            long end = System.currentTimeMillis();
            System.out.println(("Client call succeeded at " + end));
            // should return the response fast
            Assert.assertTrue(((end - start) <= 10000));
            // transition rm5 to standby
            cluster.getResourceManager(4).getRMContext().getRMAdminService().transitionToStandby(new org.apache.hadoop.ha.HAServiceProtocol.StateChangeRequestInfo(RequestSource.REQUEST_BY_USER));
            makeRMActive(cluster, 2);
            validateActiveRM(client);
        }
    }
}

