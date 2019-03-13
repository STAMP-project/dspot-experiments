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
import QueryUtils.DFLT_SCHEMA;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.apache.ignite.cache.query.QueryRetryException;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.query.QueryField;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.processors.query.schema.SchemaOperationException;
import org.apache.ignite.internal.util.GridConcurrentHashSet;
import org.apache.ignite.internal.util.typedef.T3;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgnitePredicate;
import org.junit.Test;


/**
 * Concurrency tests for dynamic index create/drop.
 */
@SuppressWarnings("unchecked")
public abstract class DynamicColumnsAbstractConcurrentSelfTest extends DynamicColumnsAbstractTest {
    /**
     * Test duration.
     */
    private static final long TEST_DUR = 10000L;

    /**
     * Large cache size.
     */
    private static final int LARGE_CACHE_SIZE = 100000;

    /**
     * Table name.
     */
    private static final String TBL_NAME = "PERSON";

    /**
     * Cache name.
     */
    private static final String CACHE_NAME = QueryUtils.createTableCacheName(DFLT_SCHEMA, DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME);

    /**
     * Attribute to filter node out of cache data nodes.
     */
    private static final String ATTR_FILTERED = "FILTERED";

    /**
     * SQL statement to create test table accompanied by template specification.
     */
    private final String createSql;

    /**
     * SQL statement to create test table with additional columns.
     */
    private final String createSql4Cols;

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
    DynamicColumnsAbstractConcurrentSelfTest(CacheMode cacheMode, CacheAtomicityMode atomicityMode) {
        this.cacheMode = cacheMode;
        this.atomicityMode = atomicityMode;
        final String template = " WITH \"template=TPL\"";
        createSql = (DynamicColumnsAbstractTest.CREATE_SQL) + template;
        createSql4Cols = (DynamicColumnsAbstractTest.CREATE_SQL_4_COLS) + template;
    }

    /**
     * Make sure that coordinator migrates correctly between nodes.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAddColumnCoordinatorChange() throws Exception {
        checkCoordinatorChange(true);
    }

    /**
     * Make sure that coordinator migrates correctly between nodes.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropColumnCoordinatorChange() throws Exception {
        checkCoordinatorChange(false);
    }

    /**
     * Test operations join.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOperationChaining() throws Exception {
        // 7 nodes * 2 columns = 14 latch countdowns.
        CountDownLatch finishLatch = new CountDownLatch(14);
        IgniteEx srv1 = DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4), finishLatch);
        createSqlCache(srv1);
        run(srv1, createSql);
        CountDownLatch idxLatch = DynamicColumnsAbstractConcurrentSelfTest.blockIndexing(srv1);
        QueryField c0 = DynamicColumnsAbstractTest.c("ID", Integer.class.getName());
        QueryField c1 = DynamicColumnsAbstractTest.c("NAME", String.class.getName());
        QueryField c2 = DynamicColumnsAbstractTest.c("age", Integer.class.getName());
        QueryField c3 = DynamicColumnsAbstractTest.c("city", String.class.getName());
        IgniteInternalFuture<?> colFut1 = DynamicColumnsAbstractConcurrentSelfTest.addCols(srv1, DFLT_SCHEMA, c2);
        IgniteInternalFuture<?> colFut2 = DynamicColumnsAbstractConcurrentSelfTest.dropCols(srv1, DFLT_SCHEMA, c1.name());
        IgniteInternalFuture<?> colFut3 = DynamicColumnsAbstractConcurrentSelfTest.addCols(srv1, DFLT_SCHEMA, c3);
        U.await(idxLatch);
        // Start even more nodes of different flavors
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(5), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(6, true), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(7), finishLatch);
        assert !(colFut1.isDone());
        assert !(colFut2.isDone());
        assert !(colFut3.isDone());
        DynamicColumnsAbstractConcurrentSelfTest.unblockIndexing(srv1);
        colFut1.get();
        colFut2.get();
        colFut3.get();
        U.await(finishLatch);
        DynamicColumnsAbstractTest.checkTableState(srv1, DFLT_SCHEMA, DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME, c0, c2, c3);
    }

    /**
     * Test node join on pending add column operation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeJoinOnPendingAddOperation() throws Exception {
        checkNodeJoinOnPendingOperation(true);
    }

    /**
     * Test node join on pending drop column operation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeJoinOnPendingDropOperation() throws Exception {
        checkNodeJoinOnPendingOperation(false);
    }

    /**
     * PUT/REMOVE data from cache and add/drop column concurrently.
     *
     * @throws Exception
     * 		If failed,
     */
    @Test
    public void testConcurrentPutRemove() throws Exception {
        CountDownLatch finishLatch = new CountDownLatch(4);
        // Start several nodes.
        IgniteEx srv1 = DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3), finishLatch);
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(4), finishLatch);
        awaitPartitionMapExchange();
        createSqlCache(srv1);
        run(srv1, createSql4Cols);
        // Start data change operations from several threads.
        final AtomicBoolean stopped = new AtomicBoolean();
        IgniteInternalFuture updateFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    int key = ThreadLocalRandom.current().nextInt(0, DynamicColumnsAbstractConcurrentSelfTest.LARGE_CACHE_SIZE);
                    int val = ThreadLocalRandom.current().nextInt();
                    IgniteCache<Object, BinaryObject> cache = node.cache(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME);
                    if (ThreadLocalRandom.current().nextBoolean())
                        cache.put(key(key), val(node, val));
                    else
                        cache.remove(key(key));

                } 
                return null;
            }
        }, 4);
        // Let some to arrive.
        Thread.sleep(500L);
        DynamicColumnsAbstractConcurrentSelfTest.addCols(srv1, DFLT_SCHEMA, DynamicColumnsAbstractTest.c("v", Integer.class.getName())).get();
        DynamicColumnsAbstractConcurrentSelfTest.dropCols(srv1, DFLT_SCHEMA, "CITY").get();
        // Stop updates once index is ready.
        stopped.set(true);
        updateFut.get();
        finishLatch.await();
        // Make sure new column is there.
        DynamicColumnsAbstractTest.checkTableState(srv1, DFLT_SCHEMA, DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME, DynamicColumnsAbstractTest.c("AGE", Integer.class.getName()), DynamicColumnsAbstractTest.c("v", Integer.class.getName()));
        run(srv1, ("update person set \"v\" = case when mod(id, 2) <> 0 then substring(name, 7, length(name) - 6) " + "else null end"));
        // Get expected values.
        Set<Integer> expKeys = new HashSet<>();
        IgniteCache<Object, BinaryObject> cache = srv1.cache(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME).withKeepBinary();
        for (int i = 0; i < (DynamicColumnsAbstractConcurrentSelfTest.LARGE_CACHE_SIZE); i++) {
            Object key = key(i);
            BinaryObject val = cache.get(key);
            if (val != null) {
                int id = ((Integer) (key));
                assertEquals(i, id);
                if ((id % 2) != 0)
                    expKeys.add(i);

            }
        }
        String valTypeName = srv1.context().query().types(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME).iterator().next().valueTypeName();
        // Validate query result.
        for (Ignite node : Ignition.allGrids()) {
            IgniteCache<Object, BinaryObject> nodeCache = node.cache(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME).withKeepBinary();
            SqlQuery qry = new SqlQuery(valTypeName, (("from " + (DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME)) + " where mod(id, 2) <> 0"));
            List<Entry<Object, BinaryObject>> res = nodeCache.query(qry).getAll();
            assertEquals((((("Cache size mismatch [exp=" + (expKeys.size())) + ", actual=") + (res.size())) + ']'), expKeys.size(), res.size());
            for (Entry<Object, BinaryObject> entry : res) {
                int key = ((Integer) (entry.getKey()));
                int v = entry.getValue().field("v");
                String name = entry.getValue().field("NAME");
                assertTrue(("Expected key is not in result set: " + key), expKeys.contains(key));
                assertEquals(Integer.parseInt(name.substring(6)), v);
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
    public void testAddConcurrentRebalance() throws Exception {
        checkConcurrentRebalance(true);
    }

    /**
     * Test index consistency on re-balance.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropConcurrentRebalance() throws Exception {
        checkConcurrentRebalance(false);
    }

    /**
     * Check what happens in case cache is destroyed before operation is started.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAddConcurrentCacheDestroy() throws Exception {
        checkConcurrentCacheDestroy(true);
    }

    /**
     * Check what happens in case cache is destroyed before operation is started.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropConcurrentCacheDestroy() throws Exception {
        checkConcurrentCacheDestroy(false);
    }

    /**
     * Make sure that contended operations on the same table from different nodes do not hang when we issue both
     * ADD/DROP COLUMN and SELECT statements.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryConsistencyMultithreaded() throws Exception {
        final int KEY_COUNT = 5000;
        // Start complex topology.
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        Ignite cli = DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        createSqlCache(cli);
        run(cli, createSql);
        put(cli, 0, KEY_COUNT);
        final AtomicBoolean stopped = new AtomicBoolean();
        final AtomicInteger dynColCnt = new AtomicInteger();
        final GridConcurrentHashSet<Integer> fields = new GridConcurrentHashSet();
        IgniteInternalFuture fut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    IgniteInternalFuture fut;
                    int fieldNum = ThreadLocalRandom.current().nextInt(0, ((dynColCnt.get()) + 1));
                    boolean removed = fields.remove(fieldNum);
                    if (removed)
                        fut = DynamicColumnsAbstractConcurrentSelfTest.dropCols(node, DFLT_SCHEMA, ("newCol" + fieldNum));
                    else {
                        fieldNum = dynColCnt.getAndIncrement();
                        fut = DynamicColumnsAbstractConcurrentSelfTest.addCols(node, DFLT_SCHEMA, DynamicColumnsAbstractTest.c(("newCol" + fieldNum), Integer.class.getName()));
                    }
                    try {
                        fut.get();
                        if (!removed)
                            fields.add(fieldNum);

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
                    try {
                        Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                        IgniteCache<BinaryObject, BinaryObject> cache = node.cache(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME).withKeepBinary();
                        String valTypeName = context().query().types(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME).iterator().next().valueTypeName();
                        List<Entry<BinaryObject, BinaryObject>> res = cache.query(new SqlQuery<BinaryObject, BinaryObject>(valTypeName, ("from " + (DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME)))).getAll();
                        assertEquals(KEY_COUNT, res.size());
                    } catch (Exception e) {
                        // Swallow retry exceptions.
                        if ((X.cause(e, QueryRetryException.class)) == null)
                            throw e;

                    }
                } 
                return null;
            }
        }, 8);
        Thread.sleep(DynamicColumnsAbstractConcurrentSelfTest.TEST_DUR);
        stopped.set(true);
        // Make sure nothing hanged.
        fut.get();
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
        checkClientReconnect(false, true);
    }

    /**
     * Make sure that client receives schema changes made while it was disconnected, even with cache recreation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectWithCacheRestart() throws Exception {
        checkClientReconnect(true, true);
    }

    /**
     * Make sure that client receives schema changes made while it was disconnected.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectWithNonDynamicCache() throws Exception {
        checkClientReconnect(false, false);
    }

    /**
     * Make sure that client receives schema changes made while it was disconnected, even with cache recreation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnectWithNonDynamicCacheRestart() throws Exception {
        checkClientReconnect(true, false);
    }

    /**
     * Test concurrent node start/stop along with add/drop column operations. Nothing should hang.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("StringConcatenationInLoop")
    @Test
    public void testConcurrentOperationsAndNodeStartStopMultithreaded() throws Exception {
        // Start several stable nodes.
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(1));
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(2));
        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(serverConfiguration(3, true));
        final IgniteEx cli = DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(clientConfiguration(4));
        createSqlCache(cli);
        run(cli, createSql);
        final AtomicBoolean stopped = new AtomicBoolean();
        // Start node start/stop worker.
        final AtomicInteger nodeIdx = new AtomicInteger(4);
        final AtomicInteger dynColCnt = new AtomicInteger();
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
                        DynamicColumnsAbstractConcurrentSelfTest.ignitionStart(cfg);
                        exists = true;
                    }
                    Thread.sleep(ThreadLocalRandom.current().nextLong(500L, 1500L));
                } 
                return null;
            }
        }, 1);
        final GridConcurrentHashSet<Integer> fields = new GridConcurrentHashSet();
        IgniteInternalFuture idxFut = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!(stopped.get())) {
                    Ignite node = grid(ThreadLocalRandom.current().nextInt(1, 5));
                    IgniteInternalFuture fut;
                    int fieldNum = ThreadLocalRandom.current().nextInt(0, ((dynColCnt.get()) + 1));
                    boolean removed = fields.remove(fieldNum);
                    if (removed)
                        fut = DynamicColumnsAbstractConcurrentSelfTest.dropCols(node, DFLT_SCHEMA, ("newCol" + fieldNum));
                    else {
                        fieldNum = dynColCnt.getAndIncrement();
                        fut = DynamicColumnsAbstractConcurrentSelfTest.addCols(node, DFLT_SCHEMA, DynamicColumnsAbstractTest.c(("newCol" + fieldNum), Integer.class.getName()));
                    }
                    try {
                        fut.get();
                        if (!removed)
                            fields.add(fieldNum);

                    } catch (SchemaOperationException e) {
                        // No-op.
                    } catch (Exception e) {
                        fail(("Unexpected exception: " + e));
                    }
                } 
                return null;
            }
        }, 1);
        Thread.sleep(DynamicColumnsAbstractConcurrentSelfTest.TEST_DUR);
        stopped.set(true);
        // Make sure nothing hanged.
        startStopFut.get();
        idxFut.get();
        // Make sure cache is operational at this point.
        createSqlCache(cli);
        QueryField[] expCols = new QueryField[fields.size()];
        // Too many index columns kills indexing internals, have to limit number of the columns
        // to build the index on.
        int idxColsCnt = Math.min(300, expCols.length);
        Integer[] args = new Integer[idxColsCnt];
        String updQry = ("UPDATE " + (DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME)) + " SET ";
        String idxQry = ("CREATE INDEX idx ON " + (DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME)) + '(';
        Integer[] sorted = fields.toArray(new Integer[fields.size()]);
        Arrays.sort(sorted);
        for (int i = 0; i < (expCols.length); i++) {
            int fieldNum = sorted[i];
            expCols[i] = DynamicColumnsAbstractTest.c(("newCol" + fieldNum), Integer.class.getName());
            if (i >= idxColsCnt)
                continue;

            if (i > 0) {
                updQry += ", ";
                idxQry += ", ";
            }
            updQry += ("\"newCol" + fieldNum) + "\" = id + ?";
            idxQry += ("\"newCol" + fieldNum) + '"';
            args[i] = i;
        }
        idxQry += ')';
        DynamicColumnsAbstractTest.checkTableState(cli, DFLT_SCHEMA, DynamicColumnsAbstractConcurrentSelfTest.TBL_NAME, expCols);
        put(cli, 0, 500);
        run(cli.cache(DynamicColumnsAbstractConcurrentSelfTest.CACHE_NAME), updQry, ((Object[]) (args)));
        run(cli, idxQry);
        run(cli, "DROP INDEX idx");
    }

    /**
     * Blocking indexing processor.
     */
    private static class BlockingIndexing extends IgniteH2Indexing {
        /**
         * {@inheritDoc }
         */
        @Override
        public void dynamicAddColumn(String schemaName, String tblName, List<QueryField> cols, boolean ifTblExists, boolean ifColNotExists) throws IgniteCheckedException {
            DynamicColumnsAbstractConcurrentSelfTest.awaitIndexing(ctx.localNodeId());
            super.dynamicAddColumn(schemaName, tblName, cols, ifTblExists, ifColNotExists);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void dynamicDropColumn(String schemaName, String tblName, List<String> cols, boolean ifTblExists, boolean ifColExists) throws IgniteCheckedException {
            DynamicColumnsAbstractConcurrentSelfTest.awaitIndexing(ctx.localNodeId());
            super.dynamicDropColumn(schemaName, tblName, cols, ifTblExists, ifColExists);
        }
    }

    /**
     * Runnable which can throw checked exceptions.
     */
    interface RunnableX {
        /**
         * Do run.
         *
         * @throws Exception
         * 		If failed.
         */
        @SuppressWarnings("UnnecessaryInterfaceModifier")
        public void run() throws Exception;
    }

    /**
     * Node filter.
     */
    protected static class NodeFilter implements Serializable , IgnitePredicate<ClusterNode> {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(ClusterNode node) {
            return (node.attribute(DynamicColumnsAbstractConcurrentSelfTest.ATTR_FILTERED)) == null;
        }
    }
}

