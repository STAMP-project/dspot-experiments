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
package org.apache.ignite.internal.processors.cache.index;


import Cache.Entry;
import QueryIndex.DFLT_INLINE_SIZE;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.query.QueryIndexDescriptorImpl;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.processors.query.schema.SchemaIndexCacheVisitor;
import org.apache.ignite.internal.processors.query.schema.SchemaOperationException;
import org.apache.ignite.internal.util.typedef.T3;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;


/**
 * Concurrency tests for dynamic index create/drop.
 */
@SuppressWarnings("unchecked")
public abstract class DynamicIndexAbstractConcurrentSelfTest extends DynamicIndexAbstractSelfTest {
    /**
     * Test duration.
     */
    private static final long TEST_DUR = 10000L;

    /**
     * Large cache size.
     */
    private static final int LARGE_CACHE_SIZE = 100000;

    /**
     * Latches to block certain index operations.
     */
    private static final ConcurrentHashMap<UUID, T3<CountDownLatch, AtomicBoolean, CountDownLatch>> BLOCKS = new ConcurrentHashMap<>();

    /**
     * Cache mode.
     */
    private final CacheMode cacheMode;

    /**
     * Atomicity mode.
     */
    private final CacheAtomicityMode atomicityMode;

    /**
     * Constructor.
     *
     * @param cacheMode
     * 		Cache mode.
     * @param atomicityMode
     * 		Atomicity mode.
     */
    DynamicIndexAbstractConcurrentSelfTest(CacheMode cacheMode, CacheAtomicityMode atomicityMode) {
        this.cacheMode = cacheMode;
        this.atomicityMode = atomicityMode;
    }

    /**
     * Make sure that coordinator migrates correctly between nodes.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCoordinatorChange() throws Exception {
        // Start servers.
        Ignite srv1 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        Ignite srv2 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(4));
        UUID srv1Id = srv1.cluster().localNode().id();
        UUID srv2Id = srv2.cluster().localNode().id();
        // Start client which will execute operations.
        Ignite cli = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(5));
        createSqlCache(cli);
        DynamicIndexAbstractSelfTest.put(srv1, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        // Test migration between normal servers.
        CountDownLatch idxLatch = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv1Id);
        QueryIndex idx1 = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        IgniteInternalFuture<?> idxFut1 = AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx1, false, 0);
        idxLatch.await();
        // srv1.close();
        Ignition.stop(srv1.name(), true);
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv1Id);
        idxFut1.get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
        // Test migration from normal server to non-affinity server.
        idxLatch = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv2Id);
        QueryIndex idx2 = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_2, AbstractSchemaSelfTest.field(DynamicIndexAbstractConcurrentSelfTest.aliasUnescaped(AbstractSchemaSelfTest.FIELD_NAME_2)));
        IgniteInternalFuture<?> idxFut2 = AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx2, false, 0);
        idxLatch.await();
        // srv2.close();
        Ignition.stop(srv2.name(), true);
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv2Id);
        idxFut2.get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_2, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(DynamicIndexAbstractConcurrentSelfTest.aliasUnescaped(AbstractSchemaSelfTest.FIELD_NAME_2)));
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_2, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_2, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_2, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * Test operations join.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOperationChaining() throws Exception {
        Ignite srv1 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        createSqlCache(srv1);
        CountDownLatch idxLatch = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv1);
        QueryIndex idx1 = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        QueryIndex idx2 = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_2, AbstractSchemaSelfTest.field(DynamicIndexAbstractConcurrentSelfTest.aliasUnescaped(AbstractSchemaSelfTest.FIELD_NAME_2)));
        IgniteInternalFuture<?> idxFut1 = AbstractSchemaSelfTest.queryProcessor(srv1).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx1, false, 0);
        IgniteInternalFuture<?> idxFut2 = AbstractSchemaSelfTest.queryProcessor(srv1).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx2, false, 0);
        idxLatch.await();
        // Start even more nodes of different flavors
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(5));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(6, true));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(7));
        assert !(idxFut1.isDone());
        assert !(idxFut2.isDone());
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv1);
        idxFut1.get();
        idxFut2.get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_2, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(DynamicIndexAbstractConcurrentSelfTest.aliasUnescaped(AbstractSchemaSelfTest.FIELD_NAME_2)));
        DynamicIndexAbstractSelfTest.put(srv1, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_2, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_2, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_2, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * Test node join on pending operation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeJoinOnPendingOperation() throws Exception {
        Ignite srv1 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        createSqlCache(srv1);
        CountDownLatch idxLatch = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv1);
        QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        IgniteInternalFuture<?> idxFut = AbstractSchemaSelfTest.queryProcessor(srv1).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, false, 0);
        idxLatch.await();
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        assert !(idxFut.isDone());
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv1);
        idxFut.get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.put(srv1, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * PUT/REMOVE data from cache and build index concurrently.
     *
     * @throws Exception
     * 		If failed,
     */
    @Test
    public void testConcurrentPutRemove() throws Exception {
        // Start several nodes.
        Ignite srv1 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(4));
        awaitPartitionMapExchange();
        IgniteCache<BinaryObject, BinaryObject> cache = createSqlCache(srv1).withKeepBinary();
        // Start data change operations from several threads.
        final AtomicBoolean stopped = new AtomicBoolean();
        IgniteInternalFuture updateFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    int key = ThreadLocalRandom.current().nextInt(0, DynamicIndexAbstractConcurrentSelfTest.LARGE_CACHE_SIZE);
                    int val = ThreadLocalRandom.current().nextInt();
                    BinaryObject keyObj = DynamicIndexAbstractSelfTest.key(node, key);
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        BinaryObject valObj = DynamicIndexAbstractSelfTest.value(node, val);
                        node.cache(AbstractSchemaSelfTest.CACHE_NAME).put(keyObj, valObj);
                    } else
                        node.cache(AbstractSchemaSelfTest.CACHE_NAME).remove(keyObj);

                } 
                return null;
            }
        }, 4);
        // Let some to arrive.
        Thread.sleep(500L);
        // Create index.
        QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        AbstractSchemaSelfTest.queryProcessor(srv1).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, false, 0).get();
        // Stop updates once index is ready.
        stopped.set(true);
        updateFut.get();
        // Make sure index is there.
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        // Get expected values.
        Map<Long, Long> expKeys = new HashMap<>();
        for (int i = 0; i < (DynamicIndexAbstractConcurrentSelfTest.LARGE_CACHE_SIZE); i++) {
            BinaryObject val = cache.get(DynamicIndexAbstractSelfTest.key(srv1, i));
            if (val != null) {
                long fieldVal = val.field(AbstractSchemaSelfTest.FIELD_NAME_1);
                if (fieldVal >= (DynamicIndexAbstractSelfTest.SQL_ARG_1))
                    expKeys.put(((long) (i)), fieldVal);

            }
        }
        // Validate query result.
        for (Ignite node : Ignition.allGrids()) {
            IgniteCache<BinaryObject, BinaryObject> nodeCache = node.cache(AbstractSchemaSelfTest.CACHE_NAME).withKeepBinary();
            SqlQuery qry = new SqlQuery(AbstractSchemaSelfTest.typeName(AbstractSchemaSelfTest.ValueClass.class), DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1).setArgs(DynamicIndexAbstractSelfTest.SQL_ARG_1);
            List<Entry<BinaryObject, BinaryObject>> res = nodeCache.query(qry).getAll();
            assertEquals((((("Cache size mismatch [exp=" + (expKeys.size())) + ", actual=") + (res.size())) + ']'), expKeys.size(), res.size());
            for (Entry<BinaryObject, BinaryObject> entry : res) {
                long key = entry.getKey().field(AbstractSchemaSelfTest.FIELD_KEY);
                Long fieldVal = entry.getValue().field(AbstractSchemaSelfTest.FIELD_NAME_1);
                assertTrue(("Expected key is not in result set: " + key), expKeys.containsKey(key));
                assertEquals((((((("Unexpected value [key=" + key) + ", expVal=") + (expKeys.get(key))) + ", actualVal=") + fieldVal) + ']'), expKeys.get(key), fieldVal);
            }
        }
    }

    /**
     * Test index consistency on re-balance.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentRebalance() throws Exception {
        // Start cache and populate it with data.
        Ignite srv1 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        Ignite srv2 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        createSqlCache(srv1);
        awaitPartitionMapExchange();
        DynamicIndexAbstractSelfTest.put(srv1, 0, DynamicIndexAbstractConcurrentSelfTest.LARGE_CACHE_SIZE);
        // Start index operation in blocked state.
        CountDownLatch idxLatch1 = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv1);
        CountDownLatch idxLatch2 = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv2);
        QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        final IgniteInternalFuture<?> idxFut = AbstractSchemaSelfTest.queryProcessor(srv1).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, false, 0);
        idxLatch1.await();
        idxLatch2.await();
        // Start two more nodes and unblock index operation in the middle.
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3));
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv1);
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv2);
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(4));
        awaitPartitionMapExchange();
        // Validate index state.
        idxFut.get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractConcurrentSelfTest.LARGE_CACHE_SIZE) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * Check what happen in case cache is destroyed before operation is started.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentCacheDestroy() throws Exception {
        // Start complex topology.
        Ignite srv1 = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        Ignite cli = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        // Start cache and populate it with data.
        createSqlCache(cli);
        DynamicIndexAbstractSelfTest.put(cli, DynamicIndexAbstractSelfTest.KEY_AFTER);
        // Start index operation and block it on coordinator.
        CountDownLatch idxLatch = DynamicIndexAbstractConcurrentSelfTest.blockIndexing(srv1);
        QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        final IgniteInternalFuture<?> idxFut = AbstractSchemaSelfTest.queryProcessor(srv1).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, false, 0);
        idxLatch.await();
        // Destroy cache (drop table).
        destroySqlCache(cli);
        // Unblock indexing and see what happens.
        DynamicIndexAbstractConcurrentSelfTest.unblockIndexing(srv1);
        try {
            idxFut.get();
            fail("Exception has not been thrown.");
        } catch (SchemaOperationException e) {
            // No-op.
        }
    }

    /**
     * Make sure that contended operations on the same index from different nodes do not hang.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentOperationsMultithreaded() throws Exception {
        // Start complex topology.
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        Ignite cli = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        createSqlCache(cli);
        final AtomicBoolean stopped = new AtomicBoolean();
        // Start several threads which will mess around indexes.
        final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        IgniteInternalFuture idxFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                boolean exists = false;
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    IgniteInternalFuture fut;
                    if (exists) {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true);
                        exists = false;
                    } else {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0);
                        exists = true;
                    }
                    try {
                        fut.get();
                    } catch (SchemaOperationException e) {
                        // No-op.
                    } catch (Exception e) {
                        fail(("Unexpected exception: " + e));
                    }
                } 
                return null;
            }
        }, 8);
        Thread.sleep(DynamicIndexAbstractConcurrentSelfTest.TEST_DUR);
        stopped.set(true);
        // Make sure nothing hanged.
        idxFut.get();
        AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true).get();
        AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0).get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.put(cli, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * Make sure that contended operations on the same index from different nodes do not hang when we issue both
     * CREATE/DROP and SELECT statements.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryConsistencyMultithreaded() throws Exception {
        // Start complex topology.
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        Ignite cli = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        createSqlCache(cli);
        DynamicIndexAbstractSelfTest.put(cli, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        final AtomicBoolean stopped = new AtomicBoolean();
        // Thread which will mess around indexes.
        final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        IgniteInternalFuture idxFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                boolean exists = false;
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    IgniteInternalFuture fut;
                    if (exists) {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true);
                        exists = false;
                    } else {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0);
                        exists = true;
                    }
                    try {
                        fut.get();
                    } catch (SchemaOperationException e) {
                        // No-op.
                    } catch (Exception e) {
                        fail(("Unexpected exception: " + e));
                    }
                } 
                return null;
            }
        }, 1);
        IgniteInternalFuture qryFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    DynamicIndexAbstractSelfTest.assertSqlSimpleData(node, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
                } 
                return null;
            }
        }, 8);
        Thread.sleep(DynamicIndexAbstractConcurrentSelfTest.TEST_DUR);
        stopped.set(true);
        // Make sure nothing hanged.
        idxFut.get();
        qryFut.get();
    }

    /**
     * Make sure that client receives schema changes made while it was disconnected.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnect() throws Exception {
        checkClientReconnect(false);
    }

    /**
     * Make sure that client receives schema changes made while it was disconnected, even with cache recreation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectWithCacheRestart() throws Exception {
        checkClientReconnect(true);
    }

    /**
     * Test concurrent node start/stop along with index operations. Nothing should hang.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentOperationsAndNodeStartStopMultithreaded() throws Exception {
        // Start several stable nodes.
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        final Ignite cli = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        createSqlCache(cli);
        final AtomicBoolean stopped = new AtomicBoolean();
        // Start node start/stop worker.
        final AtomicInteger nodeIdx = new AtomicInteger(4);
        IgniteInternalFuture startStopFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                boolean exists = false;
                int lastIdx = 0;
                while (!(stopped.get())) {
                    if (exists) {
                        stopGrid(lastIdx);
                        exists = false;
                    } else {
                        lastIdx = nodeIdx.incrementAndGet();
                        IgniteConfiguration cfg;
                        switch (ThreadLocalRandom.current().nextInt(0, 3)) {
                            case 1 :
                                cfg = serverConfiguration(lastIdx, false);
                                break;
                            case 2 :
                                cfg = serverConfiguration(lastIdx, true);
                                break;
                            default :
                                cfg = clientConfiguration(lastIdx);
                        }
                        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(cfg);
                        exists = true;
                    }
                    Thread.sleep(ThreadLocalRandom.current().nextLong(500L, 1500L));
                } 
                return null;
            }
        }, 1);
        // Start several threads which will mess around indexes.
        final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        IgniteInternalFuture idxFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                boolean exists = false;
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    IgniteInternalFuture fut;
                    if (exists) {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true);
                        exists = false;
                    } else {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0);
                        exists = true;
                    }
                    try {
                        fut.get();
                    } catch (SchemaOperationException e) {
                        // No-op.
                    } catch (Exception e) {
                        fail(("Unexpected exception: " + e));
                    }
                } 
                return null;
            }
        }, 1);
        Thread.sleep(DynamicIndexAbstractConcurrentSelfTest.TEST_DUR);
        stopped.set(true);
        // Make sure nothing hanged.
        startStopFut.get();
        idxFut.get();
        // Make sure cache is operational at this point.
        createSqlCache(cli);
        AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true).get();
        AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0).get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.put(cli, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * Multithreaded cache start/stop along with index operations. Nothing should hang.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentOperationsAndCacheStartStopMultithreaded() throws Exception {
        // Start complex topology.
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicIndexAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        Ignite cli = DynamicIndexAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        final AtomicBoolean stopped = new AtomicBoolean();
        // Start cache create/destroy worker.
        IgniteInternalFuture startStopFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                boolean exists = false;
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    if (exists) {
                        destroySqlCache(node);
                        exists = false;
                    } else {
                        createSqlCache(node);
                        exists = true;
                    }
                    Thread.sleep(ThreadLocalRandom.current().nextLong(200L, 400L));
                } 
                return null;
            }
        }, 1);
        // Start several threads which will mess around indexes.
        final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        IgniteInternalFuture idxFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                boolean exists = false;
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    IgniteInternalFuture fut;
                    if (exists) {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true);
                        exists = false;
                    } else {
                        fut = AbstractSchemaSelfTest.queryProcessor(node).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0);
                        exists = true;
                    }
                    try {
                        fut.get();
                    } catch (SchemaOperationException e) {
                        // No-op.
                    } catch (Exception e) {
                        fail(("Unexpected exception: " + e));
                    }
                } 
                return null;
            }
        }, 8);
        Thread.sleep(DynamicIndexAbstractConcurrentSelfTest.TEST_DUR);
        stopped.set(true);
        // Make sure nothing hanged.
        startStopFut.get();
        idxFut.get();
        // Make sure cache is operational at this point.
        createSqlCache(cli);
        AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true).get();
        AbstractSchemaSelfTest.queryProcessor(cli).dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0).get();
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        DynamicIndexAbstractSelfTest.put(cli, 0, DynamicIndexAbstractSelfTest.KEY_AFTER);
        DynamicIndexAbstractSelfTest.assertIndexUsed(AbstractSchemaSelfTest.IDX_NAME_1, DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, DynamicIndexAbstractSelfTest.SQL_ARG_1);
        DynamicIndexAbstractSelfTest.assertSqlSimpleData(DynamicIndexAbstractSelfTest.SQL_SIMPLE_FIELD_1, ((DynamicIndexAbstractSelfTest.KEY_AFTER) - (DynamicIndexAbstractSelfTest.SQL_ARG_1)));
    }

    /**
     * Blocking indexing processor.
     */
    private static class BlockingIndexing extends IgniteH2Indexing {
        /**
         * {@inheritDoc }
         */
        @Override
        public void dynamicIndexCreate(@NotNull
        String schemaName, String tblName, QueryIndexDescriptorImpl idxDesc, boolean ifNotExists, SchemaIndexCacheVisitor cacheVisitor) throws IgniteCheckedException {
            DynamicIndexAbstractConcurrentSelfTest.awaitIndexing(ctx.localNodeId());
            super.dynamicIndexCreate(schemaName, tblName, idxDesc, ifNotExists, cacheVisitor);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void dynamicIndexDrop(@NotNull
        String schemaName, String idxName, boolean ifExists) throws IgniteCheckedException {
            DynamicIndexAbstractConcurrentSelfTest.awaitIndexing(ctx.localNodeId());
            super.dynamicIndexDrop(schemaName, idxName, ifExists);
        }
    }
}

