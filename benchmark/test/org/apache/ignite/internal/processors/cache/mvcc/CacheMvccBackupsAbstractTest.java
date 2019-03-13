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


import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.TestRecordingCommunicationSpi;
import org.apache.ignite.internal.processors.cache.KeyCacheObject;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtTxQueryEnlistResponse;
import org.apache.ignite.internal.processors.cache.persistence.CacheDataRow;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Backups tests.
 */
@SuppressWarnings("unchecked")
public abstract class CacheMvccBackupsAbstractTest extends CacheMvccAbstractTest {
    /**
     * Test timeout.
     */
    private final long txLongTimeout = (getTestTimeout()) / 4;

    /**
     * Tests backup consistency.
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testBackupsCoherenceSimple() throws Exception {
        disableScheduledVacuum = true;
        ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 2, 10).setIndexedTypes(Integer.class, Integer.class);
        final int KEYS_CNT = 5000;
        assert (KEYS_CNT % 2) == 0;
        startGrids(3);
        Ignite node0 = grid(0);
        Ignite node1 = grid(1);
        Ignite node2 = grid(2);
        client = true;
        Ignite client = startGrid();
        awaitPartitionMapExchange();
        IgniteCache clientCache = client.cache(DEFAULT_CACHE_NAME);
        IgniteCache cache0 = node0.cache(DEFAULT_CACHE_NAME);
        IgniteCache cache1 = node1.cache(DEFAULT_CACHE_NAME);
        IgniteCache cache2 = node2.cache(DEFAULT_CACHE_NAME);
        try (Transaction tx = client.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            tx.timeout(txLongTimeout);
            for (int i = 0; i < (KEYS_CNT / 2); i += 2) {
                SqlFieldsQuery qry = new SqlFieldsQuery((((((((("INSERT INTO Integer (_key, _val) values (" + i) + ',') + (i * 2)) + "), (") + (i + 1)) + ',') + ((i + 1) * 2)) + ')'));
                clientCache.query(qry).getAll();
            }
            tx.commit();
        }
        try (Transaction tx = client.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            tx.timeout(txLongTimeout);
            for (int i = 0; i < 10; i++) {
                SqlFieldsQuery qry = new SqlFieldsQuery(("DELETE from Integer WHERE _key = " + i));
                clientCache.query(qry).getAll();
            }
            for (int i = 10; i < (KEYS_CNT + 1); i++) {
                SqlFieldsQuery qry = new SqlFieldsQuery(((("UPDATE Integer SET _val=" + (i * 10)) + " WHERE _key = ") + i));
                clientCache.query(qry).getAll();
            }
            tx.commit();
        }
        Map<KeyCacheObject, List<CacheDataRow>> vers0 = allVersions(cache0);
        List res0 = getAll(cache0, "Integer");
        stopGrid(0);
        awaitPartitionMapExchange();
        Map<KeyCacheObject, List<CacheDataRow>> vers1 = allVersions(cache1);
        assertVersionsEquals(vers0, vers1);
        List res1 = getAll(cache1, "Integer");
        assertEqualsCollections(res0, res1);
        try (Transaction tx = client.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            tx.timeout(txLongTimeout);
            for (int i = 10; i < 20; i++) {
                SqlFieldsQuery qry = new SqlFieldsQuery(("DELETE from Integer WHERE _key = " + i));
                clientCache.query(qry).getAll();
            }
            for (int i = 20; i < (KEYS_CNT + 1); i++) {
                SqlFieldsQuery qry = new SqlFieldsQuery(((("UPDATE Integer SET _val=" + (i * 100)) + " WHERE _key = ") + i));
                clientCache.query(qry).getAll();
            }
            tx.commit();
        }
        vers1 = allVersions(cache1);
        res1 = getAll(cache2, "Integer");
        stopGrid(1);
        awaitPartitionMapExchange();
        Map<KeyCacheObject, List<CacheDataRow>> vers2 = allVersions(cache2);
        assertVersionsEquals(vers1, vers2);
        List res2 = getAll(cache2, "Integer");
        assertEqualsCollections(res1, res2);
    }

    /**
     * Checks cache backups consistency with in-flight batches overflow.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBackupsCoherenceWithInFlightBatchesOverflow() throws Exception {
        testSpi = true;
        disableScheduledVacuum = true;
        ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 1, DFLT_PARTITION_COUNT).setIndexedTypes(Integer.class, Integer.class);
        final int KEYS_CNT = 30000;
        assert (KEYS_CNT % 2) == 0;
        startGrids(2);
        Ignite node1 = grid(0);
        Ignite node2 = grid(1);
        client = true;
        Ignite client = startGrid();
        awaitPartitionMapExchange();
        IgniteCache<?, ?> clientCache = client.cache(DEFAULT_CACHE_NAME);
        IgniteCache<?, ?> cache1 = node1.cache(DEFAULT_CACHE_NAME);
        IgniteCache<?, ?> cache2 = node2.cache(DEFAULT_CACHE_NAME);
        AtomicInteger keyGen = new AtomicInteger();
        Affinity affinity = affinity(clientCache);
        ClusterNode cNode1 = localNode();
        ClusterNode cNode2 = localNode();
        StringBuilder insert = new StringBuilder("INSERT INTO Integer (_key, _val) values ");
        for (int i = 0; i < KEYS_CNT; i++) {
            if (i > 0)
                insert.append(',');

            // To make big batches in near results future.
            Integer key = (i < (KEYS_CNT / 2)) ? keyForNode(affinity, keyGen, cNode1) : keyForNode(affinity, keyGen, cNode2);
            assert key != null;
            insert.append('(').append(key).append(',').append((key * 10)).append(')');
        }
        String qryStr = insert.toString();
        try (Transaction tx = client.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            tx.timeout(txLongTimeout);
            SqlFieldsQuery qry = new SqlFieldsQuery(qryStr);
            clientCache.query(qry).getAll();
            tx.commit();
        }
        // Add a delay to simulate batches overflow.
        TestRecordingCommunicationSpi spi1 = TestRecordingCommunicationSpi.spi(node1);
        TestRecordingCommunicationSpi spi2 = TestRecordingCommunicationSpi.spi(node2);
        spi1.closure(new org.apache.ignite.lang.IgniteBiInClosure<ClusterNode, Message>() {
            @Override
            public void apply(ClusterNode node, Message msg) {
                if (msg instanceof GridDhtTxQueryEnlistResponse)
                    doSleep(100);

            }
        });
        spi2.closure(new org.apache.ignite.lang.IgniteBiInClosure<ClusterNode, Message>() {
            @Override
            public void apply(ClusterNode node, Message msg) {
                if (msg instanceof GridDhtTxQueryEnlistResponse)
                    doSleep(100);

            }
        });
        qryStr = "DELETE FROM Integer WHERE _key >= " + 10;
        try (Transaction tx = client.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            tx.timeout(txLongTimeout);
            SqlFieldsQuery qry = new SqlFieldsQuery(qryStr);
            clientCache.query(qry).getAll();
            tx.commit();
        }
        Map<KeyCacheObject, List<CacheDataRow>> cache1Vers = allVersions(cache1);
        List res1 = getAll(cache1, "Integer");
        stopGrid(0);
        awaitPartitionMapExchange();
        Map<KeyCacheObject, List<CacheDataRow>> cache2Vers = allVersions(cache2);
        assertVersionsEquals(cache1Vers, cache2Vers);
        List res2 = getAll(cache2, "Integer");
        assertEqualsCollections(res1, res2);
    }

    /**
     * Tests concurrent updates backups coherence.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBackupsCoherenceWithConcurrentUpdates2ServersNoClients() throws Exception {
        checkBackupsCoherenceWithConcurrentUpdates(2, 0);
    }

    /**
     * Tests concurrent updates backups coherence.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBackupsCoherenceWithConcurrentUpdates4ServersNoClients() throws Exception {
        checkBackupsCoherenceWithConcurrentUpdates(4, 0);
    }

    /**
     * Tests concurrent updates backups coherence.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBackupsCoherenceWithConcurrentUpdates3Servers1Client() throws Exception {
        checkBackupsCoherenceWithConcurrentUpdates(3, 1);
    }

    /**
     * Tests concurrent updates backups coherence.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBackupsCoherenceWithConcurrentUpdates5Servers2Clients() throws Exception {
        checkBackupsCoherenceWithConcurrentUpdates(5, 2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoForceKeyRequestDelayedRebalanceNoVacuum() throws Exception {
        disableScheduledVacuum = true;
        doTestRebalanceNodeAdd(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoForceKeyRequestDelayedRebalance() throws Exception {
        doTestRebalanceNodeAdd(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoForceKeyRequestNoVacuum() throws Exception {
        disableScheduledVacuum = true;
        doTestRebalanceNodeAdd(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoForceKeyRequest() throws Exception {
        doTestRebalanceNodeAdd(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRebalanceNodeLeaveClient() throws Exception {
        doTestRebalanceNodeLeave(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRebalanceNodeLeaveServer() throws Exception {
        doTestRebalanceNodeLeave(false);
    }
}

