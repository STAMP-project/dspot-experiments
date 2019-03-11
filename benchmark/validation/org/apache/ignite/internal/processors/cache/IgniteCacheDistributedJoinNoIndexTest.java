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
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheDistributedJoinNoIndexTest extends GridCommonAbstractTest {
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
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin() throws Exception {
        Ignite client = grid(2);
        Affinity<Object> aff = client.affinity(IgniteCacheDistributedJoinNoIndexTest.PERSON_CACHE);
        final IgniteCache<Object, Object> personCache = client.cache(IgniteCacheDistributedJoinNoIndexTest.PERSON_CACHE);
        IgniteCache<Object, Object> orgCache = client.cache(IgniteCacheDistributedJoinNoIndexTest.ORG_CACHE);
        AtomicInteger pKey = new AtomicInteger(100000);
        AtomicInteger orgKey = new AtomicInteger();
        ClusterNode node0 = ignite(0).cluster().localNode();
        ClusterNode node1 = ignite(1).cluster().localNode();
        for (int i = 0; i < 3; i++) {
            int orgId = keyForNode(aff, orgKey, node0);
            orgCache.put(orgId, new IgniteCacheDistributedJoinNoIndexTest.Organization(("org-" + i)));
            for (int j = 0; j < i; j++)
                personCache.put(keyForNode(aff, pKey, node1), new IgniteCacheDistributedJoinNoIndexTest.Person(orgId, ("org-" + i)));

        }
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + ("from \"org\".Organization o, \"person\".Person p " + "where p.orgName = o.name")));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + ("from \"org\".Organization o inner join \"person\".Person p " + "on p.orgName = o.name")));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + ("from \"org\".Organization o, \"person\".Person p " + "where p.orgName > o.name")));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + ("from (select * from \"org\".Organization) o, \"person\".Person p " + "where p.orgName = o.name")));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + ("from \"org\".Organization o, (select *, _key from \"person\".Person) p " + "where p.orgName = o.name")));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + ("from (select * from \"org\".Organization) o, (select *, _key from \"person\".Person) p " + "where p.orgName = o.name")));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + "from \"org\".Organization o, \"person\".Person p"));
        checkNoIndexError(personCache, ("select o.name, p._key, p.orgName " + "from \"org\".Organization o, \"person\".Person p where o._key != p._key"));
        checkQuery(("select o.name, p._key, p.orgName " + ("from \"org\".Organization o, \"person\".Person p " + "where p._key = o._key and o.name=?")), personCache, 0, "aaa");
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
        String orgName;

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param orgName
         * 		Organization name.
         */
        public Person(int orgId, String orgName) {
            this.orgId = orgId;
            this.orgName = orgName;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinNoIndexTest.Person.class, this);
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
            return S.toString(IgniteCacheDistributedJoinNoIndexTest.Organization.class, this);
        }
    }
}

