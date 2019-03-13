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
import java.util.Map;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings("unchecked")
public class IgniteCacheJoinQueryWithAffinityKeyTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int NODES = 5;

    /**
     *
     */
    private boolean client;

    /**
     *
     */
    private boolean escape;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery() throws Exception {
        testJoinQuery(CacheMode.PARTITIONED, 0, false, true);
        testJoinQuery(CacheMode.PARTITIONED, 1, false, true);
        testJoinQuery(CacheMode.REPLICATED, 0, false, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQueryEscapeAll() throws Exception {
        escape = true;
        testJoinQuery();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQueryWithAffinityKey() throws Exception {
        testJoinQuery(CacheMode.PARTITIONED, 0, true, true);
        testJoinQuery(CacheMode.PARTITIONED, 1, true, true);
        testJoinQuery(CacheMode.REPLICATED, 0, true, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQueryWithAffinityKeyEscapeAll() throws Exception {
        escape = true;
        testJoinQueryWithAffinityKey();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQueryWithAffinityKeyNotQueryField() throws Exception {
        testJoinQuery(CacheMode.PARTITIONED, 0, true, false);
        testJoinQuery(CacheMode.PARTITIONED, 1, true, false);
        testJoinQuery(CacheMode.REPLICATED, 0, true, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQueryWithAffinityKeyNotQueryFieldEscapeAll() throws Exception {
        escape = true;
        testJoinQueryWithAffinityKeyNotQueryField();
    }

    /**
     *
     */
    private static class PutData {
        /**
         *
         */
        final Map<Integer, Integer> orgPersons;

        /**
         *
         */
        final Map<Object, Integer> personAccounts;

        /**
         *
         *
         * @param orgPersons
         * 		Organizations per person counts.
         * @param personAccounts
         * 		Accounts per person counts.
         */
        public PutData(Map<Integer, Integer> orgPersons, Map<Object, Integer> personAccounts) {
            this.orgPersons = orgPersons;
            this.personAccounts = personAccounts;
        }
    }

    /**
     *
     */
    public interface Id {
        /**
         *
         *
         * @return ID.
         */
        public int id();
    }

    /**
     *
     */
    public static class TestKey implements IgniteCacheJoinQueryWithAffinityKeyTest.Id {
        /**
         *
         */
        private int id;

        /**
         *
         *
         * @param id
         * 		Key.
         */
        public TestKey(int id) {
            this.id = id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgniteCacheJoinQueryWithAffinityKeyTest.TestKey other = ((IgniteCacheJoinQueryWithAffinityKeyTest.TestKey) (o));
            return (id) == (other.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     *
     */
    public static class TestKeyWithAffinity implements IgniteCacheJoinQueryWithAffinityKeyTest.Id {
        /**
         *
         */
        private int id;

        /**
         *
         */
        @AffinityKeyMapped
        private int affKey;

        /**
         *
         *
         * @param id
         * 		Key.
         */
        public TestKeyWithAffinity(int id) {
            this.id = id;
            affKey = id + 1;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgniteCacheJoinQueryWithAffinityKeyTest.TestKeyWithAffinity other = ((IgniteCacheJoinQueryWithAffinityKeyTest.TestKeyWithAffinity) (o));
            return (id) == (other.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     *
     */
    private static class Account implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private IgniteCacheJoinQueryWithAffinityKeyTest.TestKey personKey;

        /**
         *
         */
        @QuerySqlField
        private int personId;

        /**
         *
         *
         * @param personKey
         * 		Person key.
         */
        public Account(Object personKey) {
            this.personKey = ((IgniteCacheJoinQueryWithAffinityKeyTest.TestKey) (personKey));
            personId = this.personKey.id;
        }
    }

    /**
     *
     */
    private static class AccountKeyWithAffinity implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private IgniteCacheJoinQueryWithAffinityKeyTest.TestKeyWithAffinity personKey;

        /**
         *
         */
        @QuerySqlField
        private int personId;

        /**
         *
         *
         * @param personKey
         * 		Person key.
         */
        public AccountKeyWithAffinity(Object personKey) {
            this.personKey = ((IgniteCacheJoinQueryWithAffinityKeyTest.TestKeyWithAffinity) (personKey));
            personId = this.personKey.id;
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        @QuerySqlField
        int orgId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        public Person(int orgId, String name) {
            this.orgId = orgId;
            this.name = name;
        }
    }

    /**
     *
     */
    private static class Organization implements Serializable {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public Organization(String name) {
            this.name = name;
        }
    }
}

