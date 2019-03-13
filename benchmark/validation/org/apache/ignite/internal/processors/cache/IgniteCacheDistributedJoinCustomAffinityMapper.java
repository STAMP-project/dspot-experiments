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
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityKeyMapper;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheDistributedJoinCustomAffinityMapper extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String PERSON_CACHE = "person";

    /**
     *
     */
    private static final String PERSON_CACHE_CUSTOM_AFF = "personCustomAff";

    /**
     *
     */
    private static final String ORG_CACHE = "org";

    /**
     *
     */
    private static final String ORG_CACHE_REPL_CUSTOM = "orgReplCustomAff";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinCustomAffinityMapper() throws Exception {
        Ignite ignite = ignite(0);
        IgniteCache<Object, Object> cache = ignite.cache(IgniteCacheDistributedJoinCustomAffinityMapper.PERSON_CACHE);
        checkQueryFails(cache, ("select o._key k1, p._key k2 " + "from \"org\".Organization o, \"personCustomAff\".Person p where o._key=p.orgId"), false);
        checkQueryFails(cache, ("select o._key k1, p._key k2 " + "from \"personCustomAff\".Person p, \"org\".Organization o where o._key=p.orgId"), false);
        {
            // Check regular query does not fail.
            SqlFieldsQuery qry = new SqlFieldsQuery(("select o._key k1, p._key k2 " + "from \"org\".Organization o, \"personCustomAff\".Person p where o._key=p.orgId"));
            cache.query(qry).getAll();
        }
        {
            // Should not check affinity for replicated cache.
            SqlFieldsQuery qry = new SqlFieldsQuery(("select o1._key k1, p._key k2, o2._key k3 " + ("from \"org\".Organization o1, \"person\".Person p, \"orgReplCustomAff\".Organization o2 where " + "o1._key=p.orgId and o2._key=p.orgId")));
            cache.query(qry).getAll();
        }
    }

    /**
     *
     */
    static class TestMapper implements AffinityKeyMapper {
        /**
         * {@inheritDoc }
         */
        @Override
        public Object affinityKey(Object key) {
            return key;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void reset() {
            // No-op.
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
         *
         * @param orgId
         * 		Organization ID.
         */
        public Person(int orgId) {
            this.orgId = orgId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinCustomAffinityMapper.Person.class, this);
        }
    }

    /**
     *
     */
    // No-op.
    private static class Organization implements Serializable {}
}

