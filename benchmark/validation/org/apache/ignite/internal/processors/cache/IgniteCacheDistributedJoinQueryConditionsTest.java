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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheDistributedJoinQueryConditionsTest extends GridCommonAbstractTest {
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
    private boolean client;

    /**
     *
     */
    private int total;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery1() throws Exception {
        joinQuery1(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery2() throws Exception {
        Ignite client = grid(2);
        try {
            CacheConfiguration ccfg1 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE).setQueryEntities(F.asList(personEntity(false, true)));
            CacheConfiguration ccfg2 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE).setQueryEntities(F.asList(organizationEntity(false)));
            IgniteCache<Object, Object> pCache = client.createCache(ccfg1);
            IgniteCache<Object, Object> orgCache = client.createCache(ccfg2);
            ClusterNode node0 = ignite(0).cluster().localNode();
            ClusterNode node1 = ignite(1).cluster().localNode();
            Affinity<Object> aff = client.affinity(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            AtomicInteger orgKey = new AtomicInteger();
            AtomicInteger pKey = new AtomicInteger();
            List<Integer> pIds = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Integer orgId = keyForNode(aff, orgKey, node0);
                orgCache.put(orgId, new IgniteCacheDistributedJoinQueryConditionsTest.Organization(("org-" + orgId)));
                Integer pId = keyForNode(aff, pKey, node1);
                pCache.put(pId, new IgniteCacheDistributedJoinQueryConditionsTest.Person(orgId, ("p-" + orgId)));
                pIds.add(pId);
            }
            checkQuery(("select o._key, o.name, p._key, p.name " + ("from \"org\".Organization o, Person p " + "where p.orgId = o._key and p._key >= 0")), pCache, 3);
            checkQuery((("select o._key, o.name, p._key, p.name " + ("from \"org\".Organization o, Person p " + "where p.orgId = o._key and p._key=")) + (pIds.get(0))), pCache, 1);
            checkQuery(((((("select o._key, o.name, p._key, p.name " + ("from \"org\".Organization o, Person p " + "where p.orgId = o._key and p._key in (")) + (pIds.get(0))) + ", ") + (pIds.get(1))) + ")"), pCache, 2);
        } finally {
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery4() throws Exception {
        Ignite client = grid(2);
        try {
            CacheConfiguration ccfg1 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE).setQueryEntities(F.asList(personEntity(true, false)));
            IgniteCache<Object, Object> pCache = client.createCache(ccfg1);
            ClusterNode node0 = ignite(0).cluster().localNode();
            ClusterNode node1 = ignite(1).cluster().localNode();
            Affinity<Object> aff = client.affinity(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            AtomicInteger pKey = new AtomicInteger();
            Integer pId0 = keyForNode(aff, pKey, node0);
            pCache.put(pId0, new IgniteCacheDistributedJoinQueryConditionsTest.Person(0, "p0"));
            for (int i = 0; i < 3; i++) {
                Integer pId = keyForNode(aff, pKey, node1);
                pCache.put(pId, new IgniteCacheDistributedJoinQueryConditionsTest.Person(0, "p"));
            }
            checkQuery(("select p1._key, p1.name, p2._key, p2.name " + ("from Person p1, Person p2 " + "where p2._key > p1._key")), pCache, 6);
            checkQuery((("select p1._key, p1.name, p2._key, p2.name " + ("from Person p1, Person p2 " + "where p2._key > p1._key and p1._key=")) + pId0), pCache, 3);
            checkQuery(("select p1._key, p1.name, p2._key, p2.name " + ("from Person p1, Person p2 " + "where p2._key > p1._key and p1.name='p0'")), pCache, 3);
            checkQuery(("select p1._key, p1.name, p2._key, p2.name " + ("from Person p1, Person p2 " + "where p1.name > p2.name")), pCache, 3);
        } finally {
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery5() throws Exception {
        Ignite client = grid(2);
        try {
            CacheConfiguration ccfg1 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE).setQueryEntities(F.asList(personEntity(false, true)));
            CacheConfiguration ccfg2 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE).setQueryEntities(F.asList(organizationEntity(false)));
            IgniteCache<Object, Object> pCache = client.createCache(ccfg1);
            IgniteCache<Object, Object> orgCache = client.createCache(ccfg2);
            ClusterNode node0 = ignite(0).cluster().localNode();
            ClusterNode node1 = ignite(1).cluster().localNode();
            Affinity<Object> aff = client.affinity(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            AtomicInteger orgKey = new AtomicInteger();
            AtomicInteger pKey = new AtomicInteger();
            Integer orgId = keyForNode(aff, orgKey, node0);
            orgCache.put(orgId, new IgniteCacheDistributedJoinQueryConditionsTest.Organization(("org-" + orgId)));
            Integer pId = keyForNode(aff, pKey, node1);
            pCache.put(pId, new IgniteCacheDistributedJoinQueryConditionsTest.Person(orgId, ("p-" + orgId)));
            checkQuery("select o._key from \"org\".Organization o, Person p where p.orgId = o._key", pCache, 1);
            // Distributed join is not enabled for expressions, just check query does not fail.
            checkQuery(("select o.name from \"org\".Organization o where o._key in " + "(select o._key from \"org\".Organization o, Person p where p.orgId = o._key)"), pCache, 0);
        } finally {
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinQuery6() throws Exception {
        Ignite client = grid(2);
        try {
            CacheConfiguration ccfg1 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE).setQueryEntities(F.asList(personEntity(true, true)));
            CacheConfiguration ccfg2 = cacheConfiguration(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE).setQueryEntities(F.asList(organizationEntity(true)));
            IgniteCache<Object, Object> pCache = client.createCache(ccfg1);
            client.createCache(ccfg2);
            putData1();
            checkQuery(("select _key, name from \"org\".Organization o " + "inner join (select orgId from Person) p on p.orgId = o._key"), pCache, total);
            checkQuery(("select o._key, o.name from (select _key, name from \"org\".Organization) o " + "inner join Person p on p.orgId = o._key"), pCache, total);
            checkQuery(("select o._key, o.name from (select _key, name from \"org\".Organization) o " + "inner join (select orgId from Person) p on p.orgId = o._key"), pCache, total);
            checkQuery(("select * from " + ((("(select _key, name from \"org\".Organization) o " + "inner join ") + "(select orgId from Person) p ") + "on p.orgId = o._key")), pCache, total);
        } finally {
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.PERSON_CACHE);
            client.destroyCache(IgniteCacheDistributedJoinQueryConditionsTest.ORG_CACHE);
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        int orgId;

        /**
         *
         */
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

