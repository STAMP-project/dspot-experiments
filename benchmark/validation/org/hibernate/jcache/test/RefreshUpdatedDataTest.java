/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.jcache.test;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Zhenlei Huang
 */
@TestForIssue(jiraKey = "HHH-10649")
public class RefreshUpdatedDataTest extends BaseUnitTestCase {
    private ServiceRegistry serviceRegistry;

    private SessionFactoryImplementor sessionFactory;

    @Test
    public void testUpdateAndFlushThenRefresh() {
        final String BEFORE = "before";
        inTransaction(sessionFactory, ( session) -> {
            org.hibernate.jcache.test.ReadWriteCacheableItem readWriteCacheableItem = new org.hibernate.jcache.test.ReadWriteCacheableItem(BEFORE);
            readWriteCacheableItem.getTags().add("Hibernate");
            readWriteCacheableItem.getTags().add("ORM");
            session.persist(readWriteCacheableItem);
            org.hibernate.jcache.test.ReadWriteVersionedCacheableItem readWriteVersionedCacheableItem = new org.hibernate.jcache.test.ReadWriteVersionedCacheableItem(BEFORE);
            readWriteVersionedCacheableItem.getTags().add("Hibernate");
            readWriteVersionedCacheableItem.getTags().add("ORM");
            session.persist(readWriteVersionedCacheableItem);
            org.hibernate.jcache.test.NonStrictReadWriteCacheableItem nonStrictReadWriteCacheableItem = new org.hibernate.jcache.test.NonStrictReadWriteCacheableItem(BEFORE);
            nonStrictReadWriteCacheableItem.getTags().add("Hibernate");
            nonStrictReadWriteCacheableItem.getTags().add("ORM");
            session.persist(nonStrictReadWriteCacheableItem);
            org.hibernate.jcache.test.NonStrictReadWriteVersionedCacheableItem nonStrictReadWriteVersionedCacheableItem = new org.hibernate.jcache.test.NonStrictReadWriteVersionedCacheableItem(BEFORE);
            nonStrictReadWriteVersionedCacheableItem.getTags().add("Hibernate");
            nonStrictReadWriteVersionedCacheableItem.getTags().add("ORM");
            session.persist(nonStrictReadWriteVersionedCacheableItem);
        });
        inTransaction(sessionFactory, ( s1) -> {
            final String AFTER = "after";
            org.hibernate.jcache.test.ReadWriteCacheableItem readWriteCacheableItem1 = s1.get(.class, 1L);
            readWriteCacheableItem1.setName(AFTER);
            readWriteCacheableItem1.getTags().remove("ORM");
            org.hibernate.jcache.test.ReadWriteVersionedCacheableItem readWriteVersionedCacheableItem1 = s1.get(.class, 1L);
            readWriteVersionedCacheableItem1.setName(AFTER);
            readWriteVersionedCacheableItem1.getTags().remove("ORM");
            org.hibernate.jcache.test.NonStrictReadWriteCacheableItem nonStrictReadWriteCacheableItem1 = s1.get(.class, 1L);
            nonStrictReadWriteCacheableItem1.setName(AFTER);
            nonStrictReadWriteCacheableItem1.getTags().remove("ORM");
            org.hibernate.jcache.test.NonStrictReadWriteVersionedCacheableItem nonStrictReadWriteVersionedCacheableItem1 = s1.get(.class, 1L);
            nonStrictReadWriteVersionedCacheableItem1.setName(AFTER);
            nonStrictReadWriteVersionedCacheableItem1.getTags().remove("ORM");
            s1.flush();
            s1.refresh(readWriteCacheableItem1);
            s1.refresh(readWriteVersionedCacheableItem1);
            s1.refresh(nonStrictReadWriteCacheableItem1);
            s1.refresh(nonStrictReadWriteVersionedCacheableItem1);
            assertEquals(AFTER, readWriteCacheableItem1.getName());
            assertEquals(1, readWriteCacheableItem1.getTags().size());
            assertEquals(AFTER, readWriteVersionedCacheableItem1.getName());
            assertEquals(1, readWriteVersionedCacheableItem1.getTags().size());
            assertEquals(AFTER, nonStrictReadWriteCacheableItem1.getName());
            assertEquals(1, nonStrictReadWriteCacheableItem1.getTags().size());
            assertEquals(AFTER, nonStrictReadWriteVersionedCacheableItem1.getName());
            assertEquals(1, nonStrictReadWriteVersionedCacheableItem1.getTags().size());
            inTransaction(sessionFactory, ( s2) -> {
                org.hibernate.jcache.test.ReadWriteCacheableItem readWriteCacheableItem2 = s2.get(.class, 1L);
                org.hibernate.jcache.test.ReadWriteVersionedCacheableItem readWriteVersionedCacheableItem2 = s2.get(.class, 1L);
                org.hibernate.jcache.test.NonStrictReadWriteCacheableItem nonStrictReadWriteCacheableItem2 = s2.get(.class, 1L);
                org.hibernate.jcache.test.NonStrictReadWriteVersionedCacheableItem nonStrictReadWriteVersionedCacheableItem2 = s2.get(.class, 1L);
                assertEquals(BEFORE, readWriteCacheableItem2.getName());
                assertEquals(2, readWriteCacheableItem2.getTags().size());
                assertEquals(BEFORE, readWriteVersionedCacheableItem2.getName());
                assertEquals(2, readWriteVersionedCacheableItem2.getTags().size());
                // AFTER because there is no locking to block the put from the second session
                assertEquals(AFTER, nonStrictReadWriteCacheableItem2.getName());
                assertEquals(1, nonStrictReadWriteCacheableItem2.getTags().size());
                assertEquals(AFTER, nonStrictReadWriteVersionedCacheableItem2.getName());
                assertEquals(1, nonStrictReadWriteVersionedCacheableItem2.getTags().size());
            });
        });
        inTransaction(sessionFactory, ( s) -> {
            s.delete(s.getReference(.class, 1L));
            s.delete(s.getReference(.class, 1L));
            s.delete(s.getReference(.class, 1L));
            s.delete(s.getReference(.class, 1L));
        });
    }

    @Entity(name = "RwItem")
    @Table(name = "RW_ITEM")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
    public static class ReadWriteCacheableItem {
        @Id
        @GeneratedValue(generator = "increment")
        private Long id;

        private String name;

        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        @ElementCollection
        private List<String> tags = new ArrayList<>();

        public ReadWriteCacheableItem() {
        }

        public ReadWriteCacheableItem(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }
    }

    @Entity(name = "RwVersionedItem")
    @Table(name = "RW_VERSIONED_ITEM")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
    public static class ReadWriteVersionedCacheableItem {
        @Id
        @GeneratedValue(generator = "increment")
        private Long id;

        private String name;

        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        @ElementCollection
        private List<String> tags = new ArrayList<>();

        @Version
        private int version;

        public ReadWriteVersionedCacheableItem() {
        }

        public ReadWriteVersionedCacheableItem(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }
    }

    @Entity(name = "NrwItem")
    @Table(name = "RW_NOSTRICT_ITEM")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "item")
    public static class NonStrictReadWriteCacheableItem {
        @Id
        @GeneratedValue(generator = "increment")
        private Long id;

        private String name;

        @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
        @ElementCollection
        private List<String> tags = new ArrayList<>();

        public NonStrictReadWriteCacheableItem() {
        }

        public NonStrictReadWriteCacheableItem(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }
    }

    @Entity(name = "NrwVersionedItem")
    @Table(name = "RW_NOSTRICT_VER_ITEM")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "item")
    public static class NonStrictReadWriteVersionedCacheableItem {
        @Id
        @GeneratedValue(generator = "increment")
        private Long id;

        private String name;

        @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
        @ElementCollection
        private List<String> tags = new ArrayList<>();

        @Version
        private int version;

        public NonStrictReadWriteVersionedCacheableItem() {
        }

        public NonStrictReadWriteVersionedCacheableItem(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }
    }
}

