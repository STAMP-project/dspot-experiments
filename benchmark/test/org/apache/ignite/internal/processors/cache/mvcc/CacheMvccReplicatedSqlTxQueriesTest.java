/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.mvcc;


import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 *
 */
public class CacheMvccReplicatedSqlTxQueriesTest extends CacheMvccSqlTxQueriesAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReplicatedJoinPartitionedClient() throws Exception {
        checkReplicatedJoinPartitioned(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReplicatedJoinPartitionedServer() throws Exception {
        checkReplicatedJoinPartitioned(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReplicatedAndPartitionedUpdateSingleTransaction() throws Exception {
        ccfgs = new CacheConfiguration[]{ cacheConfiguration(CacheMode.REPLICATED, CacheWriteSynchronizationMode.FULL_SYNC, 0, DFLT_PARTITION_COUNT).setName("rep").setIndexedTypes(Integer.class, Integer.class), cacheConfiguration(CacheMode.PARTITIONED, CacheWriteSynchronizationMode.FULL_SYNC, 0, DFLT_PARTITION_COUNT).setIndexedTypes(Integer.class, CacheMvccSqlTxQueriesAbstractTest.MvccTestSqlIndexValue.class).setName("part") };
        startGridsMultiThreaded(3);
        client = true;
        startGrid(3);
        Random rnd = ThreadLocalRandom.current();
        Ignite node = grid(rnd.nextInt(4));
        List<List<?>> r;
        Cache<Integer, Integer> repCache = node.cache("rep");
        repCache.put(1, 1);
        repCache.put(2, 2);
        repCache.put(3, 3);
        Cache<Integer, CacheMvccSqlTxQueriesAbstractTest.MvccTestSqlIndexValue> partCache = node.cache("part");
        partCache.put(1, new CacheMvccSqlTxQueriesAbstractTest.MvccTestSqlIndexValue(1));
        partCache.put(2, new CacheMvccSqlTxQueriesAbstractTest.MvccTestSqlIndexValue(2));
        partCache.put(3, new CacheMvccSqlTxQueriesAbstractTest.MvccTestSqlIndexValue(3));
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            tx.timeout(TX_TIMEOUT);
            r = runSql(node, "UPDATE \"rep\".Integer SET _val = _key * 10");
            assertEquals(3L, r.get(0).get(0));
            r = runSql(node, "UPDATE  \"part\".MvccTestSqlIndexValue SET idxVal1 = _key * 10");
            assertEquals(3L, r.get(0).get(0));
            tx.commit();
        }
        r = runSql(node, ("SELECT COUNT(1) FROM \"rep\".Integer r JOIN \"part\".MvccTestSqlIndexValue p" + " ON r._key = p._key WHERE r._val = p.idxVal1"));
        assertEquals(3L, r.get(0).get(0));
        for (int n = 0; n < 3; ++n) {
            node = grid(n);
            r = runSqlLocal(node, "SELECT _key, _val FROM \"rep\".Integer ORDER BY _key");
            assertEquals(3L, r.size());
            assertEquals(1, r.get(0).get(0));
            assertEquals(2, r.get(1).get(0));
            assertEquals(3, r.get(2).get(0));
            assertEquals(10, r.get(0).get(1));
            assertEquals(20, r.get(1).get(1));
            assertEquals(30, r.get(2).get(1));
        }
    }
}

