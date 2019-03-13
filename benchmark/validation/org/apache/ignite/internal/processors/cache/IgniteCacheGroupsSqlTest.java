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


import java.io.Serializable;
import java.util.concurrent.Callable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheGroupsSqlTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String GROUP1 = "grp1";

    /**
     *
     */
    private static final String GROUP2 = "grp2";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlQuery() throws Exception {
        Ignite node = ignite(0);
        IgniteCache c1 = node.createCache(personCacheConfiguration(IgniteCacheGroupsSqlTest.GROUP1, "c1"));
        IgniteCache c2 = node.createCache(personCacheConfiguration(IgniteCacheGroupsSqlTest.GROUP1, "c2"));
        SqlFieldsQuery qry = new SqlFieldsQuery("select name from Person where name=?");
        qry.setArgs("p1");
        assertEquals(0, c1.query(qry).getAll().size());
        assertEquals(0, c2.query(qry).getAll().size());
        c1.put(1, new IgniteCacheGroupsSqlTest.Person("p1"));
        assertEquals(1, c1.query(qry).getAll().size());
        assertEquals(0, c2.query(qry).getAll().size());
        c2.put(2, new IgniteCacheGroupsSqlTest.Person("p1"));
        assertEquals(1, c1.query(qry).getAll().size());
        assertEquals(1, c2.query(qry).getAll().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery1() throws Exception {
        joinQuery(IgniteCacheGroupsSqlTest.GROUP1, IgniteCacheGroupsSqlTest.GROUP2, CacheMode.REPLICATED, CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, CacheAtomicityMode.TRANSACTIONAL);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery2() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                joinQuery(IgniteCacheGroupsSqlTest.GROUP1, IgniteCacheGroupsSqlTest.GROUP1, CacheMode.REPLICATED, CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, CacheAtomicityMode.TRANSACTIONAL);
                return null;
            }
        }, IgniteCheckedException.class, "Cache mode mismatch for caches related to the same group");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery3() throws Exception {
        joinQuery(IgniteCacheGroupsSqlTest.GROUP1, IgniteCacheGroupsSqlTest.GROUP1, CacheMode.PARTITIONED, CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, CacheAtomicityMode.ATOMIC);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery4() throws Exception {
        joinQuery(IgniteCacheGroupsSqlTest.GROUP1, IgniteCacheGroupsSqlTest.GROUP1, CacheMode.REPLICATED, CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, CacheAtomicityMode.TRANSACTIONAL);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery5() throws Exception {
        joinQuery(IgniteCacheGroupsSqlTest.GROUP1, null, CacheMode.REPLICATED, CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, CacheAtomicityMode.TRANSACTIONAL);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery6() throws Exception {
        joinQuery(IgniteCacheGroupsSqlTest.GROUP1, null, CacheMode.PARTITIONED, CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, CacheAtomicityMode.ATOMIC);
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public Person(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheGroupsSqlTest.Person.class, this);
        }
    }

    /**
     *
     */
    private static class Account implements Serializable {
        /**
         *
         */
        Integer personId;

        /**
         *
         */
        String attr;

        /**
         *
         *
         * @param personId
         * 		Person ID.
         * @param attr
         * 		Attribute (some data).
         */
        public Account(Integer personId, String attr) {
            this.personId = personId;
            this.attr = attr;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheGroupsSqlTest.Account.class, this);
        }
    }
}

