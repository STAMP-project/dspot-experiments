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


import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.cache.spi.access.AccessType;
import org.junit.Test;


/**
 * Tests Hibernate L2 cache.
 */
public class HibernateL2CacheSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    public static final String CONNECTION_URL = "jdbc:h2:mem:example;DB_CLOSE_DELAY=-1";

    /**
     *
     */
    public static final String ENTITY_NAME = HibernateL2CacheSelfTest.Entity.class.getName();

    /**
     *
     */
    public static final String ENTITY2_NAME = HibernateL2CacheSelfTest.Entity2.class.getName();

    /**
     *
     */
    public static final String VERSIONED_ENTITY_NAME = HibernateL2CacheSelfTest.VersionedEntity.class.getName();

    /**
     *
     */
    public static final String PARENT_ENTITY_NAME = HibernateL2CacheSelfTest.ParentEntity.class.getName();

    /**
     *
     */
    public static final String CHILD_COLLECTION_REGION = (HibernateL2CacheSelfTest.ENTITY_NAME) + ".children";

    /**
     *
     */
    public static final String NATURAL_ID_REGION = "org.apache.ignite.cache.hibernate.HibernateL2CacheSelfTest$Entity##NaturalId";

    /**
     *
     */
    public static final String NATURAL_ID_REGION2 = "org.apache.ignite.cache.hibernate.HibernateL2CacheSelfTest$Entity2##NaturalId";

    /**
     *
     */
    private SessionFactory sesFactory1;

    /**
     *
     */
    private SessionFactory sesFactory2;

    /**
     * First Hibernate test entity.
     */
    @javax.persistence.Entity
    @NaturalIdCache
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    public static class Entity {
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
        private Collection<HibernateL2CacheSelfTest.ChildEntity> children;

        /**
         * Default constructor required by Hibernate.
         */
        public Entity() {
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
        public Entity(int id, String name) {
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
        @NaturalId(mutable = true)
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

        /**
         *
         *
         * @return Children.
         */
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JoinColumn(name = "ENTITY_ID")
        public Collection<HibernateL2CacheSelfTest.ChildEntity> getChildren() {
            return children;
        }

        /**
         *
         *
         * @param children
         * 		Children.
         */
        public void setChildren(Collection<HibernateL2CacheSelfTest.ChildEntity> children) {
            this.children = children;
        }
    }

    /**
     * Second Hibernate test entity.
     */
    @javax.persistence.Entity
    @NaturalIdCache
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
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
        private Collection<HibernateL2CacheSelfTest.ChildEntity> children;

        /**
         * Default constructor required by Hibernate.
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
        public Entity2(int id, String name) {
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
        @NaturalId(mutable = true)
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
     * Hibernate child entity referenced by {@link Entity}.
     */
    @javax.persistence.Entity
    @SuppressWarnings("PublicInnerClass")
    public static class ChildEntity {
        /**
         *
         */
        private int id;

        /**
         * Default constructor required by Hibernate.
         */
        public ChildEntity() {
            // No-op.
        }

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public ChildEntity(int id) {
            this.id = id;
        }

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
     * Hibernate entity referencing {@link Entity}.
     */
    @javax.persistence.Entity
    @SuppressWarnings("PublicInnerClass")
    public static class ParentEntity {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private HibernateL2CacheSelfTest.Entity entity;

        /**
         * Default constructor required by Hibernate.
         */
        public ParentEntity() {
            // No-op.
        }

        /**
         *
         *
         * @param id
         * 		ID.
         * @param entity
         * 		Referenced entity.
         */
        public ParentEntity(int id, HibernateL2CacheSelfTest.Entity entity) {
            this.id = id;
            this.entity = entity;
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
         * @return Referenced entity.
         */
        @OneToOne
        public HibernateL2CacheSelfTest.Entity getEntity() {
            return entity;
        }

        /**
         *
         *
         * @param entity
         * 		Referenced entity.
         */
        public void setEntity(HibernateL2CacheSelfTest.Entity entity) {
            this.entity = entity;
        }
    }

    /**
     * Hibernate entity.
     */
    @javax.persistence.Entity
    @SuppressWarnings({ "PublicInnerClass", "UnnecessaryFullyQualifiedName" })
    public static class VersionedEntity {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private long ver;

        /**
         * Default constructor required by Hibernate.
         */
        public VersionedEntity() {
        }

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public VersionedEntity(int id) {
            this.id = id;
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
         * @return Version.
         */
        @Version
        public long getVersion() {
            return ver;
        }

        /**
         *
         *
         * @param ver
         * 		Version.
         */
        public void setVersion(long ver) {
            this.ver = ver;
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCollectionCache() throws Exception {
        for (AccessType accessType : accessTypes())
            testCollectionCache(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEntityCache() throws Exception {
        for (AccessType accessType : accessTypes())
            testEntityCache(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTwoEntitiesSameCache() throws Exception {
        for (AccessType accessType : accessTypes())
            testTwoEntitiesSameCache(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testVersionedEntity() throws Exception {
        for (AccessType accessType : accessTypes())
            testVersionedEntity(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNaturalIdCache() throws Exception {
        for (AccessType accessType : accessTypes())
            testNaturalIdCache(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEntityCacheTransactionFails() throws Exception {
        for (AccessType accessType : accessTypes())
            testEntityCacheTransactionFails(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryCache() throws Exception {
        for (AccessType accessType : accessTypes())
            testQueryCache(accessType);

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRegionClear() throws Exception {
        for (AccessType accessType : accessTypes())
            testRegionClear(accessType);

    }
}

