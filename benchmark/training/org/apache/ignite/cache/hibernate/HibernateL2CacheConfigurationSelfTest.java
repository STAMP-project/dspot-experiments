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
package org.apache.ignite.cache.hibernate;


import javax.cache.Cache;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.junit.Test;


/**
 * Tests Hibernate L2 cache configuration.
 */
public class HibernateL2CacheConfigurationSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    public static final String ENTITY1_NAME = HibernateL2CacheConfigurationSelfTest.Entity1.class.getName();

    /**
     *
     */
    public static final String ENTITY2_NAME = HibernateL2CacheConfigurationSelfTest.Entity2.class.getName();

    /**
     *
     */
    public static final String ENTITY3_NAME = HibernateL2CacheConfigurationSelfTest.Entity3.class.getName();

    /**
     *
     */
    public static final String ENTITY4_NAME = HibernateL2CacheConfigurationSelfTest.Entity4.class.getName();

    /**
     *
     */
    public static final String TIMESTAMP_CACHE = "org.hibernate.cache.spi.UpdateTimestampsCache";

    /**
     *
     */
    public static final String QUERY_CACHE = "org.hibernate.cache.internal.StandardQueryCache";

    /**
     *
     */
    public static final String CONNECTION_URL = "jdbc:h2:mem:example;DB_CLOSE_DELAY=-1";

    /**
     * Tests property {@link HibernateAccessStrategyFactory#REGION_CACHE_PROPERTY}.
     */
    @Test
    public void testPerRegionCacheProperty() {
        testCacheUsage(1, 1, 0, 1, 1);
    }

    /**
     * Test Hibernate entity1.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public static class Entity1 {
        /**
         *
         */
        private int id;

        /**
         *
         *
         * @return ID.
         */
        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public void setId(int id) {
            this.id = id;
        }
    }

    /**
     * Test Hibernate entity2.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public static class Entity2 {
        /**
         *
         */
        private int id;

        /**
         *
         *
         * @return ID.
         */
        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public void setId(int id) {
            this.id = id;
        }
    }

    /**
     * Test Hibernate entity3.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public static class Entity3 {
        /**
         *
         */
        private int id;

        /**
         *
         *
         * @return ID.
         */
        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public void setId(int id) {
            this.id = id;
        }
    }

    /**
     * Test Hibernate entity4.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public static class Entity4 {
        /**
         *
         */
        private int id;

        /**
         *
         *
         * @return ID.
         */
        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public void setId(int id) {
            this.id = id;
        }
    }
}

