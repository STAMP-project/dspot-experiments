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


import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.hibernate.SessionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.junit.Test;


/**
 * Tests Hibernate L2 cache configuration.
 */
@SuppressWarnings("unchecked")
public class HibernateL2CacheStrategySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String ENTITY1_NAME = HibernateL2CacheStrategySelfTest.Entity1.class.getName();

    /**
     *
     */
    private static final String ENTITY2_NAME = HibernateL2CacheStrategySelfTest.Entity2.class.getName();

    /**
     *
     */
    private static final String ENTITY3_NAME = HibernateL2CacheStrategySelfTest.Entity3.class.getName();

    /**
     *
     */
    private static final String ENTITY4_NAME = HibernateL2CacheStrategySelfTest.Entity4.class.getName();

    /**
     *
     */
    private static final String TIMESTAMP_CACHE = "org.hibernate.cache.spi.UpdateTimestampsCache";

    /**
     *
     */
    private static final String QUERY_CACHE = "org.hibernate.cache.internal.StandardQueryCache";

    /**
     *
     */
    private static final String CONNECTION_URL = "jdbc:h2:mem:example;DB_CLOSE_DELAY=-1";

    /**
     *
     */
    private SessionFactory sesFactory1;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEntityCacheReadWrite() throws Exception {
        for (AccessType accessType : new AccessType[]{ AccessType.READ_WRITE, AccessType.NONSTRICT_READ_WRITE })
            testEntityCacheReadWrite(accessType);

    }

    /**
     * Test Hibernate entity1.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    public static class Entity1 {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private String name;

        /**
         *
         */
        public Entity1() {
            // No-op.
        }

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        Entity1(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         *
         *
         * @return ID.
         */
        @Id
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

        /**
         *
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Test Hibernate entity2.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    public static class Entity2 {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private String name;

        /**
         *
         */
        public Entity2() {
            // No-op.
        }

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        Entity2(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         *
         *
         * @return ID.
         */
        @Id
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

        /**
         *
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Test Hibernate entity3.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    public static class Entity3 {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private String name;

        /**
         *
         */
        public Entity3() {
            // No-op.
        }

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        public Entity3(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         *
         *
         * @return ID.
         */
        @Id
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

        /**
         *
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Test Hibernate entity4.
     */
    @Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    @Cacheable
    public static class Entity4 {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private String name;

        /**
         *
         */
        public Entity4() {
            // No-op.
        }

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        public Entity4(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         *
         *
         * @return ID.
         */
        @Id
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

        /**
         *
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public void setName(String name) {
            this.name = name;
        }
    }
}

