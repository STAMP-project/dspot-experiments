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


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 *
 */
public class CacheMvccSqlLockTimeoutTest extends CacheMvccAbstractTest {
    /**
     *
     */
    private static final int TIMEOUT_MILLIS = 200;

    /**
     *
     */
    private UnaryOperator<IgniteConfiguration> cfgCustomizer = UnaryOperator.identity();

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testLockTimeoutsForPartitionedCache() throws Exception {
        checkLockTimeouts(partitionedCacheConfig());
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testLockTimeoutsForReplicatedCache() throws Exception {
        checkLockTimeouts(replicatedCacheConfig());
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testLockTimeoutsAfterDefaultTxTimeoutForPartitionedCache() throws Exception {
        checkLockTimeoutsAfterDefaultTxTimeout(partitionedCacheConfig());
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testLockTimeoutsAfterDefaultTxTimeoutForReplicatedCache() throws Exception {
        checkLockTimeoutsAfterDefaultTxTimeout(replicatedCacheConfig());
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testConcurrentForPartitionedCache() throws Exception {
        checkTimeoutsConcurrent(partitionedCacheConfig());
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testConcurrentForReplicatedCache() throws Exception {
        checkTimeoutsConcurrent(replicatedCacheConfig());
    }

    /**
     *
     */
    private static class TimeoutChecker {
        /**
         *
         */
        final IgniteEx ignite;

        /**
         *
         */
        final String cacheName;

        /**
         *
         */
        TimeoutChecker(IgniteEx ignite, String cacheName) {
            this.ignite = ignite;
            this.cacheName = cacheName;
        }

        /**
         *
         */
        void checkScenario(CacheMvccSqlLockTimeoutTest.TimeoutMode timeoutMode, CacheMvccSqlLockTimeoutTest.TxStartMode txStartMode, int key) throws Exception {
            // 999 is used as bound to enforce query execution with obtaining cursor before enlist
            assert key <= 999;
            try (Transaction tx = ignite.transactions().txStart(PESSIMISTIC, REPEATABLE_READ, 60000, 1)) {
                ignite.cache(cacheName).query(new SqlFieldsQuery("merge into Integer(_key, _val) values(?, 1)").setArgs(key));
                tx.commit();
            }
            ensureTimeIsOut("insert into Integer(_key, _val) values(?, 42)", key, timeoutMode, txStartMode);
            ensureTimeIsOut("merge into Integer(_key, _val) values(?, 42)", key, timeoutMode, txStartMode);
            ensureTimeIsOut("update Integer set _val = 42 where _key = ?", key, timeoutMode, txStartMode);
            ensureTimeIsOut("update Integer set _val = 42 where _key = ? or _key > 999", key, timeoutMode, txStartMode);
            ensureTimeIsOut("delete from Integer where _key = ?", key, timeoutMode, txStartMode);
            ensureTimeIsOut("delete from Integer where _key = ? or _key > 999", key, timeoutMode, txStartMode);
            // SELECT ... FOR UPDATE locking entries has no meaning for implicit transaction
            if (txStartMode != (CacheMvccSqlLockTimeoutTest.TxStartMode.IMPLICIT)) {
                ensureTimeIsOut("select * from Integer where _key = ? for update", key, timeoutMode, txStartMode);
                ensureTimeIsOut("select * from Integer where _key = ? or _key > 999 for update", key, timeoutMode, txStartMode);
            }
        }

        /**
         *
         */
        void ensureTimeIsOut(String sql, int key, CacheMvccSqlLockTimeoutTest.TimeoutMode timeoutMode, CacheMvccSqlLockTimeoutTest.TxStartMode txStartMode) throws Exception {
            assert (txStartMode == (CacheMvccSqlLockTimeoutTest.TxStartMode.EXPLICIT)) || (timeoutMode != (CacheMvccSqlLockTimeoutTest.TimeoutMode.TX));
            IgniteCache<?, ?> cache = ignite.cache(cacheName);
            int oldVal = ((Integer) (cache.query(new SqlFieldsQuery("select _val from Integer where _key = ?").setArgs(key)).getAll().get(0).get(0)));
            try (Transaction tx1 = ignite.transactions().txStart(PESSIMISTIC, REPEATABLE_READ, 6000, 1)) {
                cache.query(new SqlFieldsQuery("update Integer set _val = 42 where _key = ?").setArgs(key));
                try {
                    CompletableFuture.runAsync(() -> {
                        SqlFieldsQuery qry = new SqlFieldsQuery(sql).setArgs(key);
                        try (Transaction tx2 = (txStartMode == (CacheMvccSqlLockTimeoutTest.TxStartMode.EXPLICIT)) ? startTx(timeoutMode) : null) {
                            if (timeoutMode == (CacheMvccSqlLockTimeoutTest.TimeoutMode.STMT))
                                qry.setTimeout(CacheMvccSqlLockTimeoutTest.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

                            cache.query(qry).getAll();
                            if (tx2 != null)
                                tx2.commit();

                        } finally {
                            ignite.context().cache().context().tm().resetContext();
                        }
                    }).get();
                    fail("Timeout exception should be thrown");
                } catch (ExecutionException e) {
                    assertTrue(((CacheMvccSqlLockTimeoutTest.msgContains(e, "Failed to acquire lock within provided timeout for transaction")) || (CacheMvccSqlLockTimeoutTest.msgContains(e, "Failed to finish transaction because it has been rolled back"))));
                }
                // assert that outer tx has not timed out
                cache.query(new SqlFieldsQuery("update Integer set _val = 42 where _key = ?").setArgs(key));
                tx1.rollback();
            }
            int newVal = ((Integer) (cache.query(new SqlFieldsQuery("select _val from Integer where _key = ?").setArgs(key)).getAll().get(0).get(0)));
            assertEquals(oldVal, newVal);
        }

        /**
         *
         */
        private Transaction startTx(CacheMvccSqlLockTimeoutTest.TimeoutMode timeoutMode) {
            return timeoutMode == (CacheMvccSqlLockTimeoutTest.TimeoutMode.TX) ? ignite.transactions().txStart(PESSIMISTIC, REPEATABLE_READ, CacheMvccSqlLockTimeoutTest.TIMEOUT_MILLIS, 1) : ignite.transactions().txStart(PESSIMISTIC, REPEATABLE_READ);
        }
    }

    /**
     *
     */
    private enum TimeoutMode {

        /**
         *
         */
        TX,
        /**
         *
         */
        TX_DEFAULT,
        /**
         *
         */
        STMT;}

    /**
     *
     */
    private enum TxStartMode {

        /**
         *
         */
        EXPLICIT,
        /**
         *
         */
        IMPLICIT;}
}

