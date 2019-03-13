/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.cluster.zookeeper;


import com.navercorp.pinpoint.test.utils.TestAwaitTaskUtils;
import com.navercorp.pinpoint.test.utils.TestAwaitUtils;
import com.navercorp.pinpoint.web.config.WebConfig;
import com.navercorp.pinpoint.web.util.PinpointWebTestUtils;
import java.util.List;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Taejin Koo
 */
public class ZookeeperClusterTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_IP = PinpointWebTestUtils.getRepresentationLocalV4Ip();

    private static int acceptorPort;

    private static int zookeeperPort;

    private static WebConfig webConfig;

    private static TestAwaitUtils awaitUtils = new TestAwaitUtils(100, 10000);

    private static final String COLLECTOR_NODE_PATH = "/pinpoint-cluster/collector";

    private static final String COLLECTOR_TEST_NODE_PATH = "/pinpoint-cluster/collector/test";

    private static String CLUSTER_NODE_PATH;

    private static TestingServer ts = null;

    // test for zookeeper agents to be registered correctly at the cluster as expected
    @Test
    public void clusterTest1() throws Exception {
        ZooKeeper zookeeper = null;
        ZookeeperClusterDataManager manager = null;
        try {
            zookeeper = new ZooKeeper((((ZookeeperClusterTest.DEFAULT_IP) + ":") + (ZookeeperClusterTest.zookeeperPort)), 5000, null);
            createPath(zookeeper, ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, true);
            zookeeper.setData(ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, "a:b:1".getBytes(), (-1));
            manager = new ZookeeperClusterDataManager((((ZookeeperClusterTest.DEFAULT_IP) + ":") + (ZookeeperClusterTest.zookeeperPort)), 5000, 60000);
            manager.start();
            awaitClusterManagerConnected(manager);
            awaitCheckAgentRegistered(manager, "a", "b", 1L);
            List<String> agentList = manager.getRegisteredAgentList("a", "b", 1L);
            Assert.assertEquals(1, agentList.size());
            Assert.assertEquals("test", agentList.get(0));
            agentList = manager.getRegisteredAgentList("b", "c", 1L);
            Assert.assertEquals(0, agentList.size());
            zookeeper.setData(ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, "".getBytes(), (-1));
            final ZookeeperClusterDataManager finalManager = manager;
            boolean await = ZookeeperClusterTest.awaitUtils.await(new TestAwaitTaskUtils() {
                @Override
                public boolean checkCompleted() {
                    return finalManager.getRegisteredAgentList("a", "b", 1L).isEmpty();
                }
            });
            Assert.assertTrue(await);
        } finally {
            if (zookeeper != null) {
                zookeeper.close();
            }
            if (manager != null) {
                manager.stop();
            }
        }
    }

    @Test
    public void clusterTest2() throws Exception {
        ZooKeeper zookeeper = null;
        ZookeeperClusterDataManager manager = null;
        try {
            zookeeper = new ZooKeeper((((ZookeeperClusterTest.DEFAULT_IP) + ":") + (ZookeeperClusterTest.zookeeperPort)), 5000, null);
            createPath(zookeeper, ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, true);
            zookeeper.setData(ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, "a:b:1".getBytes(), (-1));
            manager = new ZookeeperClusterDataManager((((ZookeeperClusterTest.DEFAULT_IP) + ":") + (ZookeeperClusterTest.zookeeperPort)), 5000, 60000);
            manager.start();
            awaitClusterManagerConnected(manager);
            awaitCheckAgentRegistered(manager, "a", "b", 1L);
            List<String> agentList = manager.getRegisteredAgentList("a", "b", 1L);
            Assert.assertEquals(1, agentList.size());
            Assert.assertEquals("test", agentList.get(0));
            zookeeper.setData(ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, "a:b:1\r\nc:d:2".getBytes(), (-1));
            awaitCheckAgentRegistered(manager, "c", "d", 2L);
            agentList = manager.getRegisteredAgentList("a", "b", 1L);
            Assert.assertEquals(1, agentList.size());
            Assert.assertEquals("test", agentList.get(0));
            agentList = manager.getRegisteredAgentList("c", "d", 2L);
            Assert.assertEquals(1, agentList.size());
            Assert.assertEquals("test", agentList.get(0));
            zookeeper.delete(ZookeeperClusterTest.COLLECTOR_TEST_NODE_PATH, (-1));
            awaitCheckAgentUnRegistered(manager, "a", "b", 1L);
            agentList = manager.getRegisteredAgentList("a", "b", 1L);
            Assert.assertEquals(0, agentList.size());
            agentList = manager.getRegisteredAgentList("c", "d", 2L);
            Assert.assertEquals(0, agentList.size());
        } finally {
            if (zookeeper != null) {
                zookeeper.close();
            }
            if (manager != null) {
                manager.stop();
            }
        }
    }
}

