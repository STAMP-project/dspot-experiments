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
package com.navercorp.pinpoint.web.cluster;


import KeeperException.Code.CONNECTIONLOSS;
import com.navercorp.pinpoint.rpc.client.SimpleMessageListener;
import com.navercorp.pinpoint.test.client.TestPinpointClient;
import com.navercorp.pinpoint.test.utils.TestAwaitUtils;
import com.navercorp.pinpoint.web.cluster.connection.ClusterConnectionManager;
import com.navercorp.pinpoint.web.cluster.zookeeper.ZookeeperClusterDataManager;
import com.navercorp.pinpoint.web.util.PinpointWebTestUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.KeeperException;
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
public class ClusterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterTest.class);

    private static final Charset UTF_8_CHARSET = StandardCharsets.UTF_8;

    // some tests may fail when executed in local environment
    // when failures happen, you have to copy pinpoint-web.properties of resource-test to resource-local. Tests will succeed.
    private static TestAwaitUtils awaitUtils = new TestAwaitUtils(100, 10000);

    private static final String DEFAULT_IP = PinpointWebTestUtils.getRepresentationLocalV4Ip();

    static ClusterConnectionManager clusterConnectionManager;

    static ZookeeperClusterDataManager clusterDataManager;

    private static String CLUSTER_NODE_PATH;

    private static int acceptorPort;

    private static int zookeeperPort;

    private static String acceptorAddress;

    private static String zookeeperAddress;

    private static TestingServer ts = null;

    @Test
    public void clusterTest1() throws Exception {
        ZooKeeper zookeeper = new ZooKeeper(ClusterTest.zookeeperAddress, 5000, null);
        awaitZookeeperConnected(zookeeper);
        if (zookeeper != null) {
            zookeeper.close();
        }
    }

    @Test
    public void clusterTest2() throws Exception {
        ZooKeeper zookeeper = new ZooKeeper(ClusterTest.zookeeperAddress, 5000, null);
        awaitZookeeperConnected(zookeeper);
        ClusterTest.ts.stop();
        awaitZookeeperDisconnected(zookeeper);
        try {
            zookeeper.getData(ClusterTest.CLUSTER_NODE_PATH, null, null);
            Assert.fail();
        } catch (KeeperException e) {
            Assert.assertEquals(CONNECTIONLOSS, e.code());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ClusterTest.ts.restart();
        getNodeAndCompareContents(zookeeper);
        if (zookeeper != null) {
            zookeeper.close();
        }
    }

    @Test
    public void clusterTest3() throws Exception {
        ZooKeeper zookeeper = null;
        TestPinpointClient testPinpointClient = new TestPinpointClient(SimpleMessageListener.INSTANCE);
        try {
            zookeeper = new ZooKeeper(ClusterTest.zookeeperAddress, 5000, null);
            awaitZookeeperConnected(zookeeper);
            Assert.assertEquals(0, ClusterTest.clusterConnectionManager.getClusterList().size());
            testPinpointClient.connect(ClusterTest.DEFAULT_IP, ClusterTest.acceptorPort);
            awaitPinpointClientConnected(ClusterTest.clusterConnectionManager);
            Assert.assertEquals(1, ClusterTest.clusterConnectionManager.getClusterList().size());
        } finally {
            testPinpointClient.closeAll();
            if (zookeeper != null) {
                zookeeper.close();
            }
        }
    }
}

