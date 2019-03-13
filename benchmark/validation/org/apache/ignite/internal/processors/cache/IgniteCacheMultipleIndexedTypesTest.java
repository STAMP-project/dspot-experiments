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
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheMultipleIndexedTypesTest extends GridCommonAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleIndexedTypes() throws Exception {
        CacheConfiguration ccfg = new CacheConfiguration(DEFAULT_CACHE_NAME);
        List<QueryEntity> qryEntities = new ArrayList<>();
        {
            QueryEntity qryEntity = new QueryEntity();
            qryEntity.setKeyType(Integer.class.getName());
            qryEntity.setValueType(IgniteCacheMultipleIndexedTypesTest.Person.class.getName());
            LinkedHashMap<String, String> fields = new LinkedHashMap<>();
            fields.put("name", String.class.getName());
            qryEntity.setFields(fields);
            qryEntity.setIndexes(F.asList(new QueryIndex("name")));
            qryEntities.add(qryEntity);
        }
        {
            QueryEntity qryEntity = new QueryEntity();
            qryEntity.setKeyType(Integer.class.getName());
            qryEntity.setValueType(IgniteCacheMultipleIndexedTypesTest.Organization.class.getName());
            LinkedHashMap<String, String> fields = new LinkedHashMap<>();
            fields.put("name", String.class.getName());
            qryEntity.setFields(fields);
            qryEntity.setIndexes(F.asList(new QueryIndex("name")));
            qryEntities.add(qryEntity);
        }
        ccfg.setQueryEntities(qryEntities);
        Ignite ignite = ignite(0);
        IgniteCache<Object, Object> cache = ignite.createCache(ccfg);
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 0);
        cache.put(1, new IgniteCacheMultipleIndexedTypesTest.Person("a"));
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 1);
        cache.remove(1);
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 0);
        cache.put(1, new IgniteCacheMultipleIndexedTypesTest.Person("a"));
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 1);
        cache.put(1, new IgniteCacheMultipleIndexedTypesTest.Organization("a"));
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 0);
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Organization.class, 1);
        cache.put(1, new IgniteCacheMultipleIndexedTypesTest.Person("a"));
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 1);
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Organization.class, 0);
        cache.put(2, new IgniteCacheMultipleIndexedTypesTest.Person("a"));
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 2);
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Organization.class, 0);
        cache.put(1, new IgniteCacheMultipleIndexedTypesTest.Organization("a"));
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Person.class, 1);
        checkCount(cache, IgniteCacheMultipleIndexedTypesTest.Organization.class, 1);
    }

    /**
     *
     */
    static class Person implements Serializable {
        /**
         *
         */
        private String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public Person(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    static class Organization implements Serializable {
        /**
         *
         */
        private String name;

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

