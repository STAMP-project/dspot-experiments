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
package org.apache.ignite.ml.dataset.impl.cache;


import AffinityTopologyVersion.NONE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLock;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.IgniteCacheProxy;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtCacheAdapter;
import org.apache.ignite.internal.processors.cache.distributed.dht.topology.GridDhtLocalPartition;
import org.apache.ignite.internal.processors.cache.distributed.dht.topology.GridDhtPartitionTopology;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.dataset.primitive.data.SimpleDatasetData;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for {@link CacheBasedDataset}.
 */
public class CacheBasedDatasetTest extends GridCommonAbstractTest {
    /**
     * Number of nodes in grid.
     */
    private static final int NODE_COUNT = 4;

    /**
     * Ignite instance.
     */
    private Ignite ignite;

    /**
     * Tests that partitions of the upstream cache and the partition {@code context} cache are reserved during
     * computations on dataset. Reservation means that partitions won't be unloaded from the node before computation is
     * completed.
     */
    @Test
    public void testPartitionExchangeDuringComputeCall() {
        int partitions = 4;
        IgniteCache<Integer, String> upstreamCache = generateTestData(4, 0);
        CacheBasedDatasetBuilder<Integer, String> builder = new CacheBasedDatasetBuilder(ignite, upstreamCache);
        CacheBasedDataset<Integer, String, Long, SimpleDatasetData> dataset = builder.build(TestUtils.testEnvBuilder(), ( env, upstream, upstreamSize) -> upstreamSize, ( env, upstream, upstreamSize, ctx) -> new SimpleDatasetData(new double[0], 0));
        assertEquals("Upstream cache name from dataset", upstreamCache.getName(), dataset.getUpstreamCache().getName());
        assertTrue("Before computation all partitions should not be reserved", areAllPartitionsNotReserved(upstreamCache.getName(), dataset.getDatasetCache().getName()));
        UUID numOfStartedComputationsId = UUID.randomUUID();
        IgniteAtomicLong numOfStartedComputations = ignite.atomicLong(numOfStartedComputationsId.toString(), 0, true);
        UUID computationsLockId = UUID.randomUUID();
        IgniteLock computationsLock = ignite.reentrantLock(computationsLockId.toString(), false, true, true);
        // lock computations lock to stop computations in the middle
        computationsLock.lock();
        try {
            new Thread(() -> dataset.compute(( data, partIndex) -> {
                // track number of started computations
                ignite.atomicLong(numOfStartedComputationsId.toString(), 0, false).incrementAndGet();
                ignite.reentrantLock(computationsLockId.toString(), false, true, false).lock();
                ignite.reentrantLock(computationsLockId.toString(), false, true, false).unlock();
            })).start();
            // wait all computations to start
            while ((numOfStartedComputations.get()) < partitions) {
            } 
            assertTrue("During computation all partitions should be reserved", areAllPartitionsReserved(upstreamCache.getName(), dataset.getDatasetCache().getName()));
        } finally {
            computationsLock.unlock();
        }
        assertTrue("All partitions should be released", areAllPartitionsNotReserved(upstreamCache.getName(), dataset.getDatasetCache().getName()));
    }

    /**
     * Tests that partitions of the upstream cache and the partition {@code context} cache are reserved during
     * computations on dataset. Reservation means that partitions won't be unloaded from the node before computation is
     * completed.
     */
    @Test
    public void testPartitionExchangeDuringComputeWithCtxCall() {
        int partitions = 4;
        IgniteCache<Integer, String> upstreamCache = generateTestData(4, 0);
        CacheBasedDatasetBuilder<Integer, String> builder = new CacheBasedDatasetBuilder(ignite, upstreamCache);
        CacheBasedDataset<Integer, String, Long, SimpleDatasetData> dataset = builder.build(TestUtils.testEnvBuilder(), ( env, upstream, upstreamSize) -> upstreamSize, ( env, upstream, upstreamSize, ctx) -> new SimpleDatasetData(new double[0], 0));
        assertTrue("Before computation all partitions should not be reserved", areAllPartitionsNotReserved(upstreamCache.getName(), dataset.getDatasetCache().getName()));
        UUID numOfStartedComputationsId = UUID.randomUUID();
        IgniteAtomicLong numOfStartedComputations = ignite.atomicLong(numOfStartedComputationsId.toString(), 0, true);
        UUID computationsLockId = UUID.randomUUID();
        IgniteLock computationsLock = ignite.reentrantLock(computationsLockId.toString(), false, true, true);
        // lock computations lock to stop computations in the middle
        computationsLock.lock();
        try {
            new Thread(() -> dataset.computeWithCtx(( ctx, data, partIndex) -> {
                // track number of started computations
                ignite.atomicLong(numOfStartedComputationsId.toString(), 0, false).incrementAndGet();
                ignite.reentrantLock(computationsLockId.toString(), false, true, false).lock();
                ignite.reentrantLock(computationsLockId.toString(), false, true, false).unlock();
            })).start();
            // wait all computations to start
            while ((numOfStartedComputations.get()) < partitions) {
            } 
            assertTrue("During computation all partitions should be reserved", areAllPartitionsReserved(upstreamCache.getName(), dataset.getDatasetCache().getName()));
        } finally {
            computationsLock.unlock();
        }
        assertTrue("All partitions should be released", areAllPartitionsNotReserved(upstreamCache.getName(), dataset.getDatasetCache().getName()));
    }

    /**
     * Aggregated data about cache partitions in Ignite cluster.
     */
    private static class IgniteClusterPartitionsState {
        /**
         *
         */
        private final String cacheName;

        /**
         *
         */
        private final Map<UUID, CacheBasedDatasetTest.IgniteInstancePartitionsState> instances;

        /**
         *
         */
        static CacheBasedDatasetTest.IgniteClusterPartitionsState getCurrentState(String cacheName) {
            Map<UUID, CacheBasedDatasetTest.IgniteInstancePartitionsState> instances = new HashMap<>();
            for (Ignite ignite : G.allGrids()) {
                IgniteKernal igniteKernal = ((IgniteKernal) (ignite));
                IgniteCacheProxy<?, ?> cache = igniteKernal.context().cache().jcache(cacheName);
                GridDhtCacheAdapter<?, ?> dht = dht(cache);
                GridDhtPartitionTopology top = dht.topology();
                AffinityTopologyVersion topVer = dht.context().shared().exchange().readyAffinityVersion();
                List<GridDhtLocalPartition> parts = new ArrayList<>();
                for (int p = 0; p < (cache.context().config().getAffinity().partitions()); p++) {
                    GridDhtLocalPartition part = top.localPartition(p, NONE, false);
                    parts.add(part);
                }
                instances.put(ignite.cluster().localNode().id(), new CacheBasedDatasetTest.IgniteInstancePartitionsState(topVer, parts));
            }
            return new CacheBasedDatasetTest.IgniteClusterPartitionsState(cacheName, instances);
        }

        /**
         *
         */
        IgniteClusterPartitionsState(String cacheName, Map<UUID, CacheBasedDatasetTest.IgniteInstancePartitionsState> instances) {
            this.cacheName = cacheName;
            this.instances = instances;
        }

        /**
         *
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Cache ").append(cacheName).append(" is in following state:").append("\n");
            for (Map.Entry<UUID, CacheBasedDatasetTest.IgniteInstancePartitionsState> e : instances.entrySet()) {
                UUID instanceId = e.getKey();
                CacheBasedDatasetTest.IgniteInstancePartitionsState instanceState = e.getValue();
                builder.append("\n\t").append("Node ").append(instanceId).append(" with topology version [").append(instanceState.topVer.topologyVersion()).append(", ").append(instanceState.topVer.minorTopologyVersion()).append("] contains following partitions:").append("\n\n");
                builder.append("\t\t---------------------------------------------------------------------------------");
                builder.append("--------------------\n");
                builder.append("\t\t|  ID  |   STATE  |  RELOAD  |  RESERVATIONS  |  SHOULD BE RENTING  |  PRIMARY  |");
                builder.append("  DATA STORE SIZE  |\n");
                builder.append("\t\t---------------------------------------------------------------------------------");
                builder.append("--------------------\n");
                for (GridDhtLocalPartition partition : instanceState.parts)
                    if (partition != null) {
                        builder.append("\t\t").append(String.format("| %3d  |", partition.id())).append(String.format(" %7s  |", partition.state())).append(String.format(" %13s  |", partition.reservations())).append(String.format(" %8s  |", partition.primary(instanceState.topVer))).append(String.format(" %16d  |", partition.dataStore().fullSize())).append("\n");
                        builder.append("\t\t-------------------------------------------------------------------------");
                        builder.append("----------------------------\n");
                    }

            }
            return builder.toString();
        }
    }

    /**
     * Aggregated data about cache partitions in Ignite instance.
     */
    private static class IgniteInstancePartitionsState {
        /**
         *
         */
        private final AffinityTopologyVersion topVer;

        /**
         *
         */
        private final List<GridDhtLocalPartition> parts;

        /**
         *
         */
        IgniteInstancePartitionsState(AffinityTopologyVersion topVer, List<GridDhtLocalPartition> parts) {
            this.topVer = topVer;
            this.parts = parts;
        }

        /**
         *
         */
        public AffinityTopologyVersion getTopVer() {
            return topVer;
        }

        /**
         *
         */
        public List<GridDhtLocalPartition> getParts() {
            return parts;
        }
    }
}

