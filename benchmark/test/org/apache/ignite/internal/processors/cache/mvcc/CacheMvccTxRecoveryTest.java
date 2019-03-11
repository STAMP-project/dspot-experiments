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
package org.apache.ignite.internal.processors.cache.mvcc;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.TestRecordingCommunicationSpi;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtTxPrepareRequest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridNearTxLocal;
import org.apache.ignite.internal.processors.cache.transactions.IgniteInternalTx;
import org.apache.ignite.internal.processors.cache.transactions.TransactionProxyImpl;
import org.apache.ignite.internal.processors.cache.version.GridCacheVersion;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 *
 */
public class CacheMvccTxRecoveryTest extends CacheMvccAbstractTest {
    /**
     *
     */
    public enum TxEndResult {

        /**
         *
         */
        COMMIT,
        /**
         *
         */
        ROLLBAK;}

    /**
     *
     */
    public enum NodeMode {

        /**
         *
         */
        SERVER,
        /**
         *
         */
        CLIENT;}

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryCommitNearFailure1() throws Exception {
        checkRecoveryNearFailure(CacheMvccTxRecoveryTest.TxEndResult.COMMIT, CacheMvccTxRecoveryTest.NodeMode.CLIENT);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryCommitNearFailure2() throws Exception {
        checkRecoveryNearFailure(CacheMvccTxRecoveryTest.TxEndResult.COMMIT, CacheMvccTxRecoveryTest.NodeMode.SERVER);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryRollbackNearFailure1() throws Exception {
        checkRecoveryNearFailure(CacheMvccTxRecoveryTest.TxEndResult.ROLLBAK, CacheMvccTxRecoveryTest.NodeMode.CLIENT);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryRollbackNearFailure2() throws Exception {
        checkRecoveryNearFailure(CacheMvccTxRecoveryTest.TxEndResult.ROLLBAK, CacheMvccTxRecoveryTest.NodeMode.SERVER);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryCommitPrimaryFailure1() throws Exception {
        checkRecoveryPrimaryFailure(CacheMvccTxRecoveryTest.TxEndResult.COMMIT, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryRollbackPrimaryFailure1() throws Exception {
        checkRecoveryPrimaryFailure(CacheMvccTxRecoveryTest.TxEndResult.ROLLBAK, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryCommitPrimaryFailure2() throws Exception {
        checkRecoveryPrimaryFailure(CacheMvccTxRecoveryTest.TxEndResult.COMMIT, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryRollbackPrimaryFailure2() throws Exception {
        checkRecoveryPrimaryFailure(CacheMvccTxRecoveryTest.TxEndResult.ROLLBAK, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRecoveryCommit() throws Exception {
        startGridsMultiThreaded(2);
        client = true;
        IgniteEx ign = startGrid(2);
        IgniteCache<Object, Object> cache = ign.getOrCreateCache(CacheMvccTxRecoveryTest.basicCcfg());
        AtomicInteger keyCntr = new AtomicInteger();
        ArrayList<Integer> keys = new ArrayList<>();
        ign.cluster().forServers().nodes().forEach(( node) -> keys.add(keyForNode(ign.affinity(DEFAULT_CACHE_NAME), keyCntr, node)));
        GridTestUtils.runAsync(() -> {
            // run in separate thread to exclude tx from thread-local map
            Transaction tx = ign.transactions().txStart(PESSIMISTIC, REPEATABLE_READ);
            for (Integer k : keys)
                cache.query(new SqlFieldsQuery("insert into Integer(_key, _val) values(?, 42)").setArgs(k));

            ((TransactionProxyImpl) (tx)).tx().prepareNearTxLocal().get();
            return null;
        }).get();
        // drop near
        stopGrid(2, true);
        IgniteEx srvNode = grid(0);
        CacheMvccTxRecoveryTest.assertConditionEventually(() -> (srvNode.cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery("select * from Integer")).getAll().size()) == 2);
        assertPartitionCountersAreConsistent(keys, G.allGrids());
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCountersNeighborcastServerFailed() throws Exception {
        // Reopen https://issues.apache.org/jira/browse/IGNITE-10766 if starts failing
        int srvCnt = 4;
        startGridsMultiThreaded(srvCnt);
        client = true;
        IgniteEx ign = startGrid(srvCnt);
        IgniteCache<Object, Object> cache = ign.getOrCreateCache(CacheMvccTxRecoveryTest.basicCcfg().setBackups(2));
        ArrayList<Integer> keys = new ArrayList<>();
        int vid = 3;
        IgniteEx victim = grid(vid);
        Affinity<Object> aff = ign.affinity(DEFAULT_CACHE_NAME);
        for (int i = 0; i < 100; i++) {
            if ((aff.isPrimary(victim.localNode(), i)) && (!(aff.isBackup(grid(0).localNode(), i)))) {
                keys.add(i);
                break;
            }
        }
        for (int i = 0; i < 100; i++) {
            if ((aff.isPrimary(victim.localNode(), i)) && (!(aff.isBackup(grid(1).localNode(), i)))) {
                keys.add(i);
                break;
            }
        }
        assert ((keys.size()) == 2) && (!(keys.contains(99)));
        // prevent prepare on one backup
        ((TestRecordingCommunicationSpi) (victim.configuration().getCommunicationSpi())).blockMessages(GridDhtTxPrepareRequest.class, grid(0).name());
        GridNearTxLocal nearTx = tx();
        for (Integer k : keys)
            cache.query(new SqlFieldsQuery("insert into Integer(_key, _val) values(?, 42)").setArgs(k));

        List<IgniteInternalTx> txs = IntStream.range(0, srvCnt).mapToObj(this::grid).filter(( g) -> g != victim).map(( g) -> txsOnNode(g, nearTx.xidVersion())).flatMap(Collection::stream).collect(Collectors.toList());
        nearTx.commitAsync();
        // await tx partially prepared
        CacheMvccTxRecoveryTest.assertConditionEventually(() -> txs.stream().anyMatch(( tx) -> (tx.state()) == PREPARED));
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        IgniteInternalFuture<Object> backgroundTxFut = GridTestUtils.runAsync(() -> {
            try (Transaction ignored = ign.transactions().txStart()) {
                boolean upd = false;
                for (int i = 100; i < 200; i++) {
                    if (!(aff.isPrimary(victim.localNode(), i))) {
                        cache.put(i, 11);
                        upd = true;
                        break;
                    }
                }
                assert upd;
                latch1.countDown();
                latch2.await(getTestTimeout(), TimeUnit.MILLISECONDS);
            }
            return null;
        });
        latch1.await(getTestTimeout(), TimeUnit.MILLISECONDS);
        // drop primary
        victim.close();
        // do all assertions before rebalance
        CacheMvccTxRecoveryTest.assertConditionEventually(() -> txs.stream().allMatch(( tx) -> (tx.state()) == ROLLED_BACK));
        List<IgniteEx> liveNodes = grids(srvCnt, ( i) -> i != vid);
        assertPartitionCountersAreConsistent(keys, liveNodes);
        latch2.countDown();
        backgroundTxFut.get(getTestTimeout());
        assertTrue(liveNodes.stream().map(( node) -> node.cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery("select * from Integer")).getAll()).allMatch(Collection::isEmpty));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testUpdateCountersGapIsClosed() throws Exception {
        int srvCnt = 3;
        startGridsMultiThreaded(srvCnt);
        client = true;
        IgniteEx ign = startGrid(srvCnt);
        IgniteCache<Object, Object> cache = ign.getOrCreateCache(CacheMvccTxRecoveryTest.basicCcfg().setBackups(2));
        int vid = 1;
        IgniteEx victim = grid(vid);
        ArrayList<Integer> keys = new ArrayList<>();
        Integer part = null;
        Affinity<Object> aff = ign.affinity(DEFAULT_CACHE_NAME);
        for (int i = 0; i < 2000; i++) {
            int p = aff.partition(i);
            if (aff.isPrimary(victim.localNode(), i)) {
                if (part == null)
                    part = p;

                if (p == part)
                    keys.add(i);

                if ((keys.size()) == 2)
                    break;

            }
        }
        assert (keys.size()) == 2;
        Transaction txA = ign.transactions().txStart(PESSIMISTIC, REPEATABLE_READ);
        // prevent first transaction prepare on backups
        blockMessages(new org.apache.ignite.lang.IgniteBiPredicate<ClusterNode, Message>() {
            final AtomicInteger limiter = new AtomicInteger();

            @Override
            public boolean apply(ClusterNode node, Message msg) {
                if (msg instanceof GridDhtTxPrepareRequest)
                    return (limiter.getAndIncrement()) < 2;

                return false;
            }
        });
        cache.query(new SqlFieldsQuery("insert into Integer(_key, _val) values(?, 42)").setArgs(keys.get(0)));
        txA.commitAsync();
        GridCacheVersion aXidVer = ((TransactionProxyImpl) (txA)).tx().xidVersion();
        CacheMvccTxRecoveryTest.assertConditionEventually(() -> txsOnNode(victim, aXidVer).stream().anyMatch(( tx) -> (tx.state()) == PREPARING));
        GridTestUtils.runAsync(() -> {
            try (Transaction txB = ign.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
                cache.query(new SqlFieldsQuery("insert into Integer(_key, _val) values(?, 42)").setArgs(keys.get(1)));
                txB.commit();
            }
        }).get();
        long victimUpdCntr = CacheMvccTxRecoveryTest.updateCounter(victim.cachex(DEFAULT_CACHE_NAME).context(), keys.get(0));
        List<IgniteEx> backupNodes = grids(srvCnt, ( i) -> i != vid);
        List<IgniteInternalTx> backupTxsA = backupNodes.stream().map(( node) -> txsOnNode(node, aXidVer)).flatMap(Collection::stream).collect(Collectors.toList());
        // drop primary
        victim.close();
        CacheMvccTxRecoveryTest.assertConditionEventually(() -> backupTxsA.stream().allMatch(( tx) -> (tx.state()) == ROLLED_BACK));
        backupNodes.stream().map(( node) -> node.cache(DEFAULT_CACHE_NAME)).forEach(( c) -> {
            assertEquals(1, c.query(new SqlFieldsQuery("select * from Integer")).getAll().size());
        });
        backupNodes.forEach(( node) -> {
            for (Integer k : keys)
                assertEquals(victimUpdCntr, updateCounter(node.cachex(DEFAULT_CACHE_NAME).context(), k));

        });
    }
}

