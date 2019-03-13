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
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheDistributedJoinCollocatedAndNotTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String PERSON_CACHE = "person";

    /**
     *
     */
    private static final String ORG_CACHE = "org";

    /**
     *
     */
    private static final String ACCOUNT_CACHE = "acc";

    /**
     *
     */
    private boolean client;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin() throws Exception {
        Ignite client = grid(2);
        IgniteCache<Object, Object> personCache = client.cache(IgniteCacheDistributedJoinCollocatedAndNotTest.PERSON_CACHE);
        IgniteCache<Object, Object> orgCache = client.cache(IgniteCacheDistributedJoinCollocatedAndNotTest.ORG_CACHE);
        IgniteCache<Object, Object> accCache = client.cache(IgniteCacheDistributedJoinCollocatedAndNotTest.ACCOUNT_CACHE);
        Affinity<Object> aff = client.affinity(IgniteCacheDistributedJoinCollocatedAndNotTest.PERSON_CACHE);
        AtomicInteger orgKey = new AtomicInteger();
        AtomicInteger accKey = new AtomicInteger();
        ClusterNode node0 = ignite(0).cluster().localNode();
        ClusterNode node1 = ignite(1).cluster().localNode();
        /**
         * One organization, one person, two accounts.
         */
        int orgId1 = keyForNode(aff, orgKey, node0);
        orgCache.put(orgId1, new IgniteCacheDistributedJoinCollocatedAndNotTest.Organization(("obj-" + orgId1)));
        personCache.put(new IgniteCacheDistributedJoinCollocatedAndNotTest.PersonKey(1, orgId1), new IgniteCacheDistributedJoinCollocatedAndNotTest.Person(1, "o1-p1"));
        personCache.put(new IgniteCacheDistributedJoinCollocatedAndNotTest.PersonKey(2, orgId1), new IgniteCacheDistributedJoinCollocatedAndNotTest.Person(2, "o1-p2"));
        accCache.put(keyForNode(aff, accKey, node0), new IgniteCacheDistributedJoinCollocatedAndNotTest.Account(1, "a0"));
        accCache.put(keyForNode(aff, accKey, node1), new IgniteCacheDistributedJoinCollocatedAndNotTest.Account(1, "a1"));
        // Join on affinity keys equals condition should not be distributed.
        String qry = "select o.name, p._key, p.name " + ("from \"org\".Organization o, \"person\".Person p " + "where p.affKey = o._key");
        assertFalse(plan(qry, orgCache, false).contains("batched"));
        checkQuery(qry, orgCache, false, 2);
        checkQuery(("select o.name, p._key, p.name, a.name " + ("from \"org\".Organization o, \"person\".Person p, \"acc\".Account a " + "where p.affKey = o._key and p.id = a.personId")), orgCache, true, 2);
    }

    /**
     *
     */
    public static class PersonKey {
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
         * @param affKey
         * 		Affinity key.
         */
        public PersonKey(int id, int affKey) {
            this.id = id;
            this.affKey = affKey;
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

            IgniteCacheDistributedJoinCollocatedAndNotTest.PersonKey other = ((IgniteCacheDistributedJoinCollocatedAndNotTest.PersonKey) (o));
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
        int personId;

        /**
         *
         */
        String name;

        /**
         *
         *
         * @param personId
         * 		Person ID.
         * @param name
         * 		Name.
         */
        public Account(int personId, String name) {
            this.personId = personId;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinCollocatedAndNotTest.Account.class, this);
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        int id;

        /**
         *
         */
        String name;

        /**
         *
         *
         * @param id
         * 		Person ID.
         * @param name
         * 		Name.
         */
        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinCollocatedAndNotTest.Person.class, this);
        }
    }

    /**
     *
     */
    private static class Organization implements Serializable {
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
        public Organization(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinCollocatedAndNotTest.Organization.class, this);
        }
    }
}

