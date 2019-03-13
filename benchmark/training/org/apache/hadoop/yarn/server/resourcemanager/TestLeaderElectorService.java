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
package org.apache.hadoop.yarn.server.resourcemanager;


import HAServiceState.ACTIVE;
import HAServiceState.STANDBY;
import YarnConfiguration.RM_HA_ID;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.KillSession;
import org.apache.curator.test.TestingCluster;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol.HAServiceState;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.MemoryRMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;


public class TestLeaderElectorService {
    private static final String RM1_ADDRESS = "1.1.1.1:1";

    private static final String RM1_NODE_ID = "rm1";

    private static final String RM2_ADDRESS = "0.0.0.0:0";

    private static final String RM2_NODE_ID = "rm2";

    Configuration conf;

    MockRM rm1;

    MockRM rm2;

    TestingCluster zkCluster;

    // 1. rm1 active
    // 2. rm2 standby
    // 3. stop rm1
    // 4. rm2 become active
    @Test(timeout = 20000)
    public void testRMShutDownCauseFailover() throws Exception {
        rm1 = startRM("rm1", ACTIVE);
        rm2 = startRM("rm2", STANDBY);
        // wait for some time to make sure rm2 will not become active;
        Thread.sleep(5000);
        waitFor(rm2, STANDBY);
        stop();
        // rm2 should become active;
        waitFor(rm2, ACTIVE);
    }

    // 1. rm1 active
    // 2. rm2 standby
    // 3. submit a job to rm1 which triggers state-store failure.
    // 4. rm2 become
    @Test
    public void testStateStoreFailureCauseFailover() throws Exception {
        conf.set(RM_HA_ID, "rm1");
        MemoryRMStateStore memStore = new MemoryRMStateStore() {
            @Override
            public synchronized void storeApplicationStateInternal(ApplicationId appId, ApplicationStateData appState) throws Exception {
                throw new Exception("store app failure.");
            }
        };
        memStore.init(conf);
        rm1 = new MockRM(conf, memStore, true);
        rm1.init(conf);
        start();
        waitFor(rm1, ACTIVE);
        rm2 = startRM("rm2", STANDBY);
        // submit an app which will trigger state-store failure.
        rm1.submitApp(200, "app1", "user1", null, "default", false);
        waitFor(rm1, STANDBY);
        // rm2 should become active;
        waitFor(rm2, ACTIVE);
        stop();
        // rm1 will become active again
        waitFor(rm1, ACTIVE);
    }

    // 1. rm1 active
    // 2. restart zk cluster
    // 3. rm1 will first relinquish leadership and re-acquire leadership
    @Test
    public void testZKClusterDown() throws Exception {
        rm1 = startRM("rm1", ACTIVE);
        // stop zk cluster
        zkCluster.stop();
        waitFor(rm1, STANDBY);
        Collection<InstanceSpec> instanceSpecs = zkCluster.getInstances();
        zkCluster = new TestingCluster(instanceSpecs);
        zkCluster.start();
        // rm becomes active again
        waitFor(rm1, ACTIVE);
    }

    // 1. rm1 active
    // 2. kill the zk session between the rm and zk cluster.
    // 3. rm1 will first relinquish leadership and re-acquire leadership
    @Test
    public void testExpireCurrentZKSession() throws Exception {
        rm1 = startRM("rm1", ACTIVE);
        CuratorBasedElectorService service = ((CuratorBasedElectorService) (getRMContext().getLeaderElectorService()));
        CuratorZookeeperClient client = service.getCuratorClient().getZookeeperClient();
        // this will expire current curator client session. curator will re-establish
        // the session. RM will first relinquish leadership and re-acquire leadership
        KillSession.kill(client.getZooKeeper(), client.getCurrentConnectionString());
        waitFor(rm1, ACTIVE);
    }

    // 1. rm1 fail to become active.
    // 2. rm1 will rejoin leader election and retry the leadership
    @Test
    public void testRMFailToTransitionToActive() throws Exception {
        conf.set(RM_HA_ID, "rm1");
        final AtomicBoolean throwException = new AtomicBoolean(true);
        Thread launchRM = new Thread() {
            @Override
            public void run() {
                rm1 = new MockRM(conf, true) {
                    @Override
                    synchronized void transitionToActive() throws Exception {
                        if (throwException.get()) {
                            throw new Exception("Fail to transition to active");
                        } else {
                            super.transitionToActive();
                        }
                    }
                };
                rm1.init(conf);
                start();
            }
        };
        launchRM.start();
        // wait some time, rm will keep retry the leadership;
        Thread.sleep(5000);
        throwException.set(false);
        waitFor(rm1, ACTIVE);
    }

    // 1. rm1 active
    // 2. rm2 standby
    // 3. kill the current connected zk instance
    // 4. either rm1 or rm2 will become active.
    @Test
    public void testKillZKInstance() throws Exception {
        rm1 = startRM("rm1", ACTIVE);
        rm2 = startRM("rm2", STANDBY);
        CuratorBasedElectorService service = ((CuratorBasedElectorService) (getRMContext().getLeaderElectorService()));
        ZooKeeper zkClient = service.getCuratorClient().getZookeeperClient().getZooKeeper();
        InstanceSpec connectionInstance = zkCluster.findConnectionInstance(zkClient);
        zkCluster.killServer(connectionInstance);
        // wait for rm1 or rm2 to be active by randomness
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    HAServiceState rm1State = rm1.getAdminService().getServiceStatus().getState();
                    HAServiceState rm2State = rm2.getAdminService().getServiceStatus().getState();
                    return ((rm1State.equals(ACTIVE)) && (rm2State.equals(STANDBY))) || ((rm1State.equals(STANDBY)) && (rm2State.equals(ACTIVE)));
                } catch (IOException e) {
                }
                return false;
            }
        }, 2000, 15000);
    }
}

