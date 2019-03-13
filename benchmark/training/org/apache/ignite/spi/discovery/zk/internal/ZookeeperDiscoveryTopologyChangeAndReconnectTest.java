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


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractFullApiSelfTest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for Zookeeper SPI discovery.
 */
public class ZookeeperDiscoveryTopologyChangeAndReconnectTest extends ZookeeperDiscoverySpiTestBase {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTopologyChangeMultithreaded() throws Exception {
        topologyChangeWithRestarts(false, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRandomTopologyChanges() throws Exception {
        randomTopologyChanges(false, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRandomTopologyChanges_CloseClients() throws Exception {
        randomTopologyChanges(false, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeployService1() throws Exception {
        startGridsMultiThreaded(3);
        grid(0).services(grid(0).cluster()).deployNodeSingleton("test", new GridCacheAbstractFullApiSelfTest.DummyServiceImpl());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeployService2() throws Exception {
        helper.clientMode(false);
        startGrid(0);
        helper.clientMode(true);
        startGrid(1);
        grid(0).services(grid(0).cluster()).deployNodeSingleton("test", new GridCacheAbstractFullApiSelfTest.DummyServiceImpl());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeployService3() throws Exception {
        IgniteInternalFuture fut = GridTestUtils.runAsync(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                helper.clientModeThreadLocal(true);
                startGrid(0);
                return null;
            }
        }, "start-node");
        helper.clientModeThreadLocal(false);
        startGrid(1);
        fut.get();
        grid(0).services(grid(0).cluster()).deployNodeSingleton("test", new GridCacheAbstractFullApiSelfTest.DummyServiceImpl());
    }

    /**
     * Test with large user attribute on coordinator node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLargeUserAttribute1() throws Exception {
        initLargeAttribute();
        startGrid(0);
        checkZkNodesCleanup();
        userAttrs = null;
        startGrid(1);
        helper.waitForEventsAcks(ignite(0));
        waitForTopology(2);
    }

    /**
     * Test with large user attribute on non-coordinator node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLargeUserAttribute2() throws Exception {
        startGrid(0);
        initLargeAttribute();
        startGrid(1);
        helper.waitForEventsAcks(ignite(0));
        checkZkNodesCleanup();
    }

    /**
     * Test with large user attributes on random nodes.
     * Also tests that big messages (more than 1MB) properly separated and processed by zk.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLargeUserAttribute3() throws Exception {
        Set<Integer> idxs = ThreadLocalRandom.current().ints(0, 10).distinct().limit(3).boxed().collect(Collectors.toSet());
        for (int i = 0; i < 10; i++) {
            info(("Iteration: " + i));
            if (idxs.contains(i))
                initLargeAttribute();
            else
                userAttrs = null;

            helper.clientMode((i > 5));
            startGrid(i);
        }
        waitForTopology(10);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLargeCustomEvent() throws Exception {
        Ignite srv0 = startGrid(0);
        // Send large message, single node in topology.
        IgniteCache<Object, Object> cache = srv0.createCache(largeCacheConfiguration("c1"));
        for (int i = 0; i < 100; i++)
            cache.put(i, i);

        assertEquals(1, cache.get(1));
        helper.waitForEventsAcks(ignite(0));
        startGridsMultiThreaded(1, 3);
        srv0.destroyCache("c1");
        // Send large message, multiple nodes in topology.
        cache = srv0.createCache(largeCacheConfiguration("c1"));
        for (int i = 0; i < 100; i++)
            cache.put(i, i);

        waitForTopology(4);
        ignite(3).createCache(largeCacheConfiguration("c2"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectSessionExpire1_1() throws Exception {
        clientReconnectSessionExpire(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectSessionExpire1_2() throws Exception {
        clientReconnectSessionExpire(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testForceClientReconnect() throws Exception {
        final int SRVS = 3;
        startGrids(SRVS);
        helper.clientMode(true);
        startGrid(SRVS);
        reconnectClientNodes(Collections.singletonList(ignite(SRVS)), new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ZookeeperDiscoverySpi spi = helper.waitSpi(getTestIgniteInstanceName(SRVS), spis);
                spi.clientReconnect();
                return null;
            }
        });
        waitForTopology((SRVS + 1));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testForcibleClientFail() throws Exception {
        final int SRVS = 3;
        startGrids(SRVS);
        helper.clientMode(true);
        startGrid(SRVS);
        reconnectClientNodes(Collections.singletonList(ignite(SRVS)), new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ZookeeperDiscoverySpi spi = helper.waitSpi(getTestIgniteInstanceName(0), spis);
                spi.failNode(ignite(SRVS).cluster().localNode().id(), "Test forcible node fail");
                return null;
            }
        });
        waitForTopology((SRVS + 1));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDuplicatedNodeId() throws Exception {
        UUID nodeId0 = nodeId = UUID.randomUUID();
        startGrid(0);
        int failingNodeIdx = 100;
        for (int i = 0; i < 5; i++) {
            final int idx = failingNodeIdx++;
            nodeId = nodeId0;
            info((((("Start node with duplicated ID [iter=" + i) + ", nodeId=") + (nodeId)) + ']'));
            Throwable err = GridTestUtils.assertThrows(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    startGrid(idx);
                    return null;
                }
            }, IgniteCheckedException.class, null);
            assertTrue((err instanceof IgniteCheckedException));
            assertTrue(((err.getMessage().contains("Failed to start processor:")) || (err.getMessage().contains("Failed to start manager:"))));
            nodeId = null;
            info((("Start node with unique ID [iter=" + i) + ']'));
            Ignite ignite = startGrid(idx);
            nodeId0 = ignite.cluster().localNode().id();
            waitForTopology((i + 2));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPing() throws Exception {
        sesTimeout = 5000;
        startGrids(3);
        final ZookeeperDiscoverySpi spi = helper.waitSpi(getTestIgniteInstanceName(1), spis);
        final UUID nodeId = ignite(2).cluster().localNode().id();
        IgniteInternalFuture<?> fut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
            @Override
            public void run() {
                assertTrue(spi.pingNode(nodeId));
            }
        }, 32, "ping");
        fut.get();
        fut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
            @Override
            public void run() {
                spi.pingNode(nodeId);
            }
        }, 32, "ping");
        U.sleep(100);
        stopGrid(2);
        fut.get();
        fut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
            @Override
            public void run() {
                assertFalse(spi.pingNode(nodeId));
            }
        }, 32, "ping");
        fut.get();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWithPersistence1() throws Exception {
        startWithPersistence(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWithPersistence2() throws Exception {
        startWithPersistence(true);
    }

    /**
     *
     */
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private static class TestAffinityFunction extends RendezvousAffinityFunction {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        private int[] dummyData;

        /**
         *
         *
         * @param dataSize
         * 		Dummy data size.
         */
        TestAffinityFunction(int dataSize) {
            dummyData = new int[dataSize];
            for (int i = 0; i < dataSize; i++)
                dummyData[i] = i;

        }
    }
}

