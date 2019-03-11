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


import java.util.concurrent.Callable;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 *
 */
public class CacheMvccSqlTxModesTest extends CacheMvccAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed
     */
    @Test
    public void testSqlTransactionModesNoMvcc() throws Exception {
        IgniteEx node = startGrid(0);
        IgniteCache<Object, Object> nonMvccCache = node.createCache(new org.apache.ignite.configuration.CacheConfiguration("no-mvcc-cache").setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL).setIndexedTypes(Integer.class, Integer.class));
        nonMvccCache.put(1, 1);
        try (Transaction tx = node.transactions().txStart(OPTIMISTIC, READ_COMMITTED)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(OPTIMISTIC, REPEATABLE_READ)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(OPTIMISTIC, SERIALIZABLE)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, READ_COMMITTED)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, SERIALIZABLE)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(OPTIMISTIC, REPEATABLE_READ)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer").setLocal(true)).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, SERIALIZABLE)) {
            nonMvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer").setLocal(true)).getAll();
            tx.commit();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed
     */
    @Test
    public void testSqlTransactionModesMvcc() throws Exception {
        IgniteEx node = startGrid(0);
        IgniteCache<Object, Object> mvccCache = node.createCache(new org.apache.ignite.configuration.CacheConfiguration("mvcc-cache").setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT).setIndexedTypes(Integer.class, Integer.class));
        mvccCache.put(1, 1);
        GridTestUtils.assertThrows(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Transaction tx = node.transactions().txStart(OPTIMISTIC, READ_COMMITTED)) {
                    mvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
                    tx.commit();
                }
                return null;
            }
        }, CacheException.class, "Only pessimistic transactions are supported when MVCC is enabled");
        GridTestUtils.assertThrows(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Transaction tx = node.transactions().txStart(OPTIMISTIC, REPEATABLE_READ)) {
                    mvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
                    tx.commit();
                }
                return null;
            }
        }, CacheException.class, "Only pessimistic transactions are supported when MVCC is enabled");
        GridTestUtils.assertThrows(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Transaction tx = node.transactions().txStart(OPTIMISTIC, SERIALIZABLE)) {
                    mvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
                    tx.commit();
                }
                return null;
            }
        }, CacheException.class, "Only pessimistic transactions are supported when MVCC is enabled");
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, READ_COMMITTED)) {
            mvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            mvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, SERIALIZABLE)) {
            mvccCache.query(new SqlFieldsQuery("SELECT * FROM Integer")).getAll();
            tx.commit();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed
     */
    @Test
    public void testConsequentMvccNonMvccOperations() throws Exception {
        IgniteEx node = startGrid(0);
        IgniteCache<Object, Object> mvccCache = node.createCache(new org.apache.ignite.configuration.CacheConfiguration("mvcc-cache").setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT).setIndexedTypes(Integer.class, Integer.class));
        IgniteCache<Object, Object> nonMvccCache = node.createCache(new org.apache.ignite.configuration.CacheConfiguration("no-mvcc-cache").setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL).setIndexedTypes(Integer.class, Integer.class));
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            nonMvccCache.put(1, 1);
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            mvccCache.query(new SqlFieldsQuery("INSERT INTO Integer (_key, _val) VALUES (3,3)")).getAll();
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            nonMvccCache.put(2, 2);
            tx.commit();
        }
        try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            mvccCache.query(new SqlFieldsQuery("INSERT INTO Integer (_key, _val) VALUES (5,5)")).getAll();
            tx.commit();
        }
    }
}

