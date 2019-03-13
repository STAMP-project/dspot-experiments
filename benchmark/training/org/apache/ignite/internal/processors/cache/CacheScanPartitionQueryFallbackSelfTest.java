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
package org.apache.ignite.internal.processors.cache;


import Cache.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.cache.query.GridCacheQueryRequest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests partition scan query fallback.
 */
public class CacheScanPartitionQueryFallbackSelfTest extends GridCommonAbstractTest {
    /**
     * Grid count.
     */
    private static final int GRID_CNT = 3;

    /**
     * Keys count.
     */
    private static final int KEYS_CNT = 50 * (RendezvousAffinityFunction.DFLT_PARTITION_COUNT);

    /**
     * Backups.
     */
    private int backups;

    /**
     * Cache mode.
     */
    private CacheMode cacheMode;

    /**
     * Client mode.
     */
    private volatile boolean clientMode;

    /**
     * Expected first node ID.
     */
    private static UUID expNodeId;

    /**
     * Communication SPI factory.
     */
    private CacheScanPartitionQueryFallbackSelfTest.CommunicationSpiFactory commSpiFactory;

    /**
     * Test entries.
     */
    private Map<Integer, Map<Integer, Integer>> entries = new HashMap<>();

    /**
     *
     */
    private boolean syncRebalance;

    /**
     * Scan should perform on the local node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanLocal() throws Exception {
        cacheMode = CacheMode.PARTITIONED;
        backups = 0;
        commSpiFactory = new CacheScanPartitionQueryFallbackSelfTest.TestLocalCommunicationSpiFactory();
        try {
            Ignite ignite = startGrids(CacheScanPartitionQueryFallbackSelfTest.GRID_CNT);
            IgniteCacheProxy<Integer, Integer> cache = fillCache(ignite);
            int part = CacheScanPartitionQueryFallbackSelfTest.anyLocalPartition(cache.context());
            QueryCursor<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>().setPartition(part));
            doTestScanQuery(qry, part);
        } finally {
            stopAllGrids();
        }
    }

    /**
     * Scan (with explicit {@code setLocal(true)}) should perform on the local node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanLocalExplicit() throws Exception {
        cacheMode = CacheMode.PARTITIONED;
        backups = 0;
        commSpiFactory = new CacheScanPartitionQueryFallbackSelfTest.TestLocalCommunicationSpiFactory();
        try {
            Ignite ignite = startGrids(CacheScanPartitionQueryFallbackSelfTest.GRID_CNT);
            IgniteCacheProxy<Integer, Integer> cache = fillCache(ignite);
            int part = CacheScanPartitionQueryFallbackSelfTest.anyLocalPartition(cache.context());
            QueryCursor<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>().setPartition(part).setLocal(true));
            doTestScanQuery(qry, part);
            GridTestUtils.assertThrows(log, ((Callable<Void>) (() -> {
                int remPart = remotePartition(cache.context()).getKey();
                cache.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>().setPartition(remPart).setLocal(true));
                return null;
            })), IgniteCheckedException.class, null);
        } finally {
            stopAllGrids();
        }
    }

    /**
     * Scan (with explicit {@code setLocal(true)}, no partition specified) should perform on the local node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanLocalExplicitNoPart() throws Exception {
        cacheMode = CacheMode.PARTITIONED;
        backups = 0;
        commSpiFactory = new CacheScanPartitionQueryFallbackSelfTest.TestLocalCommunicationSpiFactory();
        try {
            Ignite ignite = startGrids(CacheScanPartitionQueryFallbackSelfTest.GRID_CNT);
            IgniteCacheProxy<Integer, Integer> cache = fillCache(ignite);
            QueryCursor<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>().setLocal(true));
            assertFalse(qry.getAll().isEmpty());
        } finally {
            stopAllGrids();
        }
    }

    /**
     * Scan should perform on the remote node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanRemote() throws Exception {
        cacheMode = CacheMode.PARTITIONED;
        backups = 0;
        commSpiFactory = new CacheScanPartitionQueryFallbackSelfTest.TestRemoteCommunicationSpiFactory();
        try {
            Ignite ignite = startGrids(CacheScanPartitionQueryFallbackSelfTest.GRID_CNT);
            IgniteCacheProxy<Integer, Integer> cache = fillCache(ignite);
            IgniteBiTuple<Integer, UUID> tup = remotePartition(cache.context());
            int part = tup.get1();
            CacheScanPartitionQueryFallbackSelfTest.expNodeId = tup.get2();
            QueryCursor<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>().setPartition(part));
            doTestScanQuery(qry, part);
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanFallbackOnRebalancing() throws Exception {
        scanFallbackOnRebalancing(false);
    }

    /**
     * Scan should activate fallback mechanism when new nodes join topology and rebalancing happens in parallel with
     * scan query.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanFallbackOnRebalancingCursor1() throws Exception {
        cacheMode = CacheMode.PARTITIONED;
        clientMode = false;
        backups = 1;
        commSpiFactory = new CacheScanPartitionQueryFallbackSelfTest.TestFallbackOnRebalancingCommunicationSpiFactory();
        try {
            Ignite ignite = startGrids(CacheScanPartitionQueryFallbackSelfTest.GRID_CNT);
            fillCache(ignite);
            final AtomicBoolean done = new AtomicBoolean(false);
            IgniteInternalFuture fut1 = multithreadedAsync(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int i = 0; i < 5; i++) {
                        startGrid(((CacheScanPartitionQueryFallbackSelfTest.GRID_CNT) + i));
                        U.sleep(500);
                    }
                    done.set(true);
                    return null;
                }
            }, 1);
            final AtomicInteger nodeIdx = new AtomicInteger();
            IgniteInternalFuture fut2 = multithreadedAsync(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    int nodeId = nodeIdx.getAndIncrement();
                    IgniteCache<Integer, Integer> cache = grid(nodeId).cache(DEFAULT_CACHE_NAME);
                    int cntr = 0;
                    while (!(done.get())) {
                        int part = ThreadLocalRandom.current().nextInt(ignite(nodeId).affinity(DEFAULT_CACHE_NAME).partitions());
                        if (((cntr++) % 100) == 0)
                            info((((("Running query [node=" + nodeId) + ", part=") + part) + ']'));

                        try (QueryCursor<Entry<Integer, Integer>> cur = cache.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>(part).setPageSize(5))) {
                            doTestScanQueryCursor(cur, part);
                        }
                    } 
                    return null;
                }
            }, CacheScanPartitionQueryFallbackSelfTest.GRID_CNT);
            fut1.get();
            fut2.get();
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanFallbackOnRebalancingCursor2() throws Exception {
        scanFallbackOnRebalancing(true);
    }

    /**
     * Factory for tests specific communication SPI.
     */
    private interface CommunicationSpiFactory {
        /**
         *
         *
         * @return Communication SPI instance.
         */
        TcpCommunicationSpi create();
    }

    /**
     *
     */
    private static class TestLocalCommunicationSpiFactory implements CacheScanPartitionQueryFallbackSelfTest.CommunicationSpiFactory {
        /**
         * {@inheritDoc }
         */
        @Override
        public TcpCommunicationSpi create() {
            return new TcpCommunicationSpi() {
                @Override
                public void sendMessage(ClusterNode node, Message msg, IgniteInClosure<IgniteException> ackC) throws IgniteSpiException {
                    Object origMsg = message();
                    if (origMsg instanceof GridCacheQueryRequest)
                        fail();
                    // should use local node

                    super.sendMessage(node, msg, ackC);
                }
            };
        }
    }

    /**
     *
     */
    private static class TestRemoteCommunicationSpiFactory implements CacheScanPartitionQueryFallbackSelfTest.CommunicationSpiFactory {
        /**
         * {@inheritDoc }
         */
        @Override
        public TcpCommunicationSpi create() {
            return new TcpCommunicationSpi() {
                @Override
                public void sendMessage(ClusterNode node, Message msg, IgniteInClosure<IgniteException> ackC) throws IgniteSpiException {
                    Object origMsg = message();
                    if (origMsg instanceof GridCacheQueryRequest)
                        assertEquals(CacheScanPartitionQueryFallbackSelfTest.expNodeId, node.id());

                    super.sendMessage(node, msg, ackC);
                }
            };
        }
    }

    /**
     *
     */
    private static class TestFallbackOnRebalancingCommunicationSpiFactory implements CacheScanPartitionQueryFallbackSelfTest.CommunicationSpiFactory {
        /**
         * {@inheritDoc }
         */
        @Override
        public TcpCommunicationSpi create() {
            return new TcpCommunicationSpi();
        }
    }
}

