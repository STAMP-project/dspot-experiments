/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.spi.discovery.zk.internal;


import EventType.EVT_NODE_SEGMENTED;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.curator.TestingCluster;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.curator.TestingZooKeeperServer;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpiTestUtil;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.transactions.Transaction;
import org.apache.zookeeper.ZkTestClientCnxnSocketNIO;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.junit.Test;


/**
 * Tests for Zookeeper SPI discovery.
 */
public class ZookeeperDiscoverySegmentationAndConnectionRestoreTest extends ZookeeperDiscoverySpiTestBase {
    /**
     * Verifies correct handling of SEGMENTATION event with STOP segmentation policy: node is stopped successfully,
     * all its threads are shut down.
     *
     * @throws Exception
     * 		If failed.
     * @see <a href="https://issues.apache.org/jira/browse/IGNITE-9040">IGNITE-9040</a> ticket for more context of the test.
     */
    @Test
    public void testStopNodeOnSegmentaion() throws Exception {
        try {
            System.setProperty("IGNITE_WAL_LOG_TX_RECORDS", "true");
            sesTimeout = 2000;
            testSockNio = true;
            persistence = true;
            atomicityMode = CacheAtomicityMode.TRANSACTIONAL;
            backups = 2;
            final Ignite node0 = startGrid(0);
            sesTimeout = 10000;
            testSockNio = false;
            startGrid(1);
            node0.cluster().active(true);
            helper.clientMode(true);
            final IgniteEx client = startGrid(2);
            // first transaction
            client.transactions().txStart(PESSIMISTIC, READ_COMMITTED, 0, 0);
            client.cache(DEFAULT_CACHE_NAME).put(0, 0);
            // second transaction to create a deadlock with the first one
            // and guarantee transaction futures will be presented on segmented node
            // (erroneous write to WAL on segmented node stop happens
            // on completing transaction with NodeStoppingException)
            GridTestUtils.runAsync(new Runnable() {
                @Override
                public void run() {
                    Transaction tx2 = client.transactions().txStart(OPTIMISTIC, READ_COMMITTED, 0, 0);
                    client.cache(DEFAULT_CACHE_NAME).put(0, 0);
                    tx2.commit();
                }
            });
            // next block simulates Ignite node segmentation by closing socket of ZooKeeper client
            {
                final CountDownLatch l = new CountDownLatch(1);
                node0.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
                    @Override
                    public boolean apply(Event evt) {
                        l.countDown();
                        return false;
                    }
                }, EVT_NODE_SEGMENTED);
                ZkTestClientCnxnSocketNIO c0 = ZkTestClientCnxnSocketNIO.forNode(node0);
                c0.closeSocket(true);
                for (int i = 0; i < 10; i++) {
                    // noinspection BusyWait
                    Thread.sleep(1000);
                    if ((l.getCount()) == 0)
                        break;

                }
                info("Allow connect");
                c0.allowConnect();
                assertTrue(l.await(10, TimeUnit.SECONDS));
            }
            waitForNodeStop(node0.name());
            checkStoppedNodeThreads(node0.name());
        } finally {
            System.clearProperty("IGNITE_WAL_LOG_TX_RECORDS");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSegmentation1() throws Exception {
        sesTimeout = 2000;
        testSockNio = true;
        Ignite node0 = startGrid(0);
        final CountDownLatch l = new CountDownLatch(1);
        node0.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
            @Override
            public boolean apply(Event evt) {
                l.countDown();
                return false;
            }
        }, EVT_NODE_SEGMENTED);
        ZkTestClientCnxnSocketNIO c0 = ZkTestClientCnxnSocketNIO.forNode(node0);
        c0.closeSocket(true);
        for (int i = 0; i < 10; i++) {
            // noinspection BusyWait
            Thread.sleep(1000);
            if ((l.getCount()) == 0)
                break;

        }
        info("Allow connect");
        c0.allowConnect();
        assertTrue(l.await(10, TimeUnit.SECONDS));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSegmentation2() throws Exception {
        sesTimeout = 2000;
        Ignite node0 = startGrid(0);
        final CountDownLatch l = new CountDownLatch(1);
        node0.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
            @Override
            public boolean apply(Event evt) {
                l.countDown();
                return false;
            }
        }, EVT_NODE_SEGMENTED);
        try {
            ZookeeperDiscoverySpiTestBase.zkCluster.close();
            assertTrue(l.await(10, TimeUnit.SECONDS));
        } finally {
            ZookeeperDiscoverySpiTestBase.zkCluster = ZookeeperDiscoverySpiTestUtil.createTestingCluster(ZookeeperDiscoverySpiTestBase.ZK_SRVS);
            ZookeeperDiscoverySpiTestBase.zkCluster.start();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSegmentation3() throws Exception {
        sesTimeout = 5000;
        Ignite node0 = startGrid(0);
        final CountDownLatch l = new CountDownLatch(1);
        node0.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
            @Override
            public boolean apply(Event evt) {
                l.countDown();
                return false;
            }
        }, EVT_NODE_SEGMENTED);
        List<TestingZooKeeperServer> srvs = ZookeeperDiscoverySpiTestBase.zkCluster.getServers();
        assertEquals(3, srvs.size());
        try {
            srvs.get(0).stop();
            srvs.get(1).stop();
            QuorumPeer qp = srvs.get(2).getQuorumPeer();
            // Zookeeper's socket timeout [tickTime * initLimit] + 5 additional seconds for other logic
            assertTrue(l.await((((qp.getTickTime()) * (qp.getInitLimit())) + 5000), TimeUnit.MILLISECONDS));
        } finally {
            ZookeeperDiscoverySpiTestBase.zkCluster.close();
            ZookeeperDiscoverySpiTestBase.zkCluster = ZookeeperDiscoverySpiTestUtil.createTestingCluster(ZookeeperDiscoverySpiTestBase.ZK_SRVS);
            ZookeeperDiscoverySpiTestBase.zkCluster.start();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore1() throws Exception {
        testSockNio = true;
        Ignite node0 = startGrid(0);
        ZkTestClientCnxnSocketNIO c0 = ZkTestClientCnxnSocketNIO.forNode(node0);
        c0.closeSocket(false);
        startGrid(1);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore2() throws Exception {
        testSockNio = true;
        Ignite node0 = startGrid(0);
        ZkTestClientCnxnSocketNIO c0 = ZkTestClientCnxnSocketNIO.forNode(node0);
        c0.closeSocket(false);
        startGridsMultiThreaded(1, 5);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_NonCoordinator1() throws Exception {
        connectionRestore_NonCoordinator(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_NonCoordinator2() throws Exception {
        connectionRestore_NonCoordinator(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_Coordinator1() throws Exception {
        connectionRestore_Coordinator(1, 1, 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_Coordinator1_1() throws Exception {
        connectionRestore_Coordinator(1, 1, 1);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_Coordinator2() throws Exception {
        connectionRestore_Coordinator(1, 3, 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_Coordinator3() throws Exception {
        connectionRestore_Coordinator(3, 3, 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionRestore_Coordinator4() throws Exception {
        connectionRestore_Coordinator(3, 3, 1);
    }
}

