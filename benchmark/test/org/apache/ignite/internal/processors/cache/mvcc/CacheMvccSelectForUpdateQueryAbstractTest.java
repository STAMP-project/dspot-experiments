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


import TransactionConcurrency.PESSIMISTIC;
import TransactionIsolation.REPEATABLE_READ;
import java.util.ArrayList;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Test for {@code SELECT FOR UPDATE} queries.
 */
public abstract class CacheMvccSelectForUpdateQueryAbstractTest extends CacheMvccAbstractTest {
    /**
     *
     */
    private static final int CACHE_SIZE = 50;

    /**
     *
     */
    @Test
    public void testSelectForUpdateDistributed() throws Exception {
        doTestSelectForUpdateDistributed("Person", false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectForUpdateLocal() throws Exception {
        doTestSelectForUpdateLocal("Person", false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectForUpdateOutsideTxDistributed() throws Exception {
        doTestSelectForUpdateDistributed("Person", true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectForUpdateOutsideTxLocal() throws Exception {
        doTestSelectForUpdateLocal("Person", true);
    }

    /**
     *
     */
    @Test
    public void testSelectForUpdateWithUnion() {
        assertQueryThrows("select id from person union select 1 for update", "SELECT UNION FOR UPDATE is not supported.");
    }

    /**
     *
     */
    @Test
    public void testSelectForUpdateWithJoin() {
        assertQueryThrows("select p1.id from person p1 join person p2 on p1.id = p2.id for update", "SELECT FOR UPDATE with joins is not supported.");
    }

    /**
     *
     */
    @Test
    public void testSelectForUpdateWithLimit() {
        assertQueryThrows("select id from person limit 0,5 for update", "LIMIT/OFFSET clauses are not supported for SELECT FOR UPDATE.");
    }

    /**
     *
     */
    @Test
    public void testSelectForUpdateWithGroupings() {
        assertQueryThrows("select count(*) from person for update", "SELECT FOR UPDATE with aggregates and/or GROUP BY is not supported.");
        assertQueryThrows("select lastName, count(*) from person group by lastName for update", "SELECT FOR UPDATE with aggregates and/or GROUP BY is not supported.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectForUpdateAfterAbortedTx() throws Exception {
        assert disableScheduledVacuum;
        Ignite node = grid(0);
        IgniteCache<Integer, ?> cache = node.cache("Person");
        List<List<?>> res;
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            res = cache.query(new SqlFieldsQuery("update person set lastName=UPPER(lastName)")).getAll();
            assertEquals(((long) (CacheMvccSelectForUpdateQueryAbstractTest.CACHE_SIZE)), res.get(0).get(0));
            tx.rollback();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            res = cache.query(new SqlFieldsQuery("select id, * from person order by id for update")).getAll();
            assertEquals(CacheMvccSelectForUpdateQueryAbstractTest.CACHE_SIZE, res.size());
            List<Integer> keys = new ArrayList<>();
            for (List<?> r : res)
                keys.add(((Integer) (r.get(0))));

            checkLocks("Person", keys, true);
            tx.rollback();
            checkLocks("Person", keys, false);
        }
    }
}

