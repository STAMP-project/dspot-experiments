/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Zhenlei Huang
 */
@TestForIssue(jiraKey = "HHH-10649")
@RequiresDialect({ H2Dialect.class })
public class RefreshUpdatedDataTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUpdateAndFlushThenRefresh() {
        // prepare data
        Session s = openSession();
        s.beginTransaction();
        final String BEFORE = "before";
        RefreshUpdatedDataTest.ReadWriteCacheableItem readWriteCacheableItem = new RefreshUpdatedDataTest.ReadWriteCacheableItem(BEFORE);
        readWriteCacheableItem.getTags().add("Hibernate");
        readWriteCacheableItem.getTags().add("ORM");
        s.persist(readWriteCacheableItem);
        RefreshUpdatedDataTest.ReadWriteVersionedCacheableItem readWriteVersionedCacheableItem = new RefreshUpdatedDataTest.ReadWriteVersionedCacheableItem(BEFORE);
        readWriteVersionedCacheableItem.getTags().add("Hibernate");
        readWriteVersionedCacheableItem.getTags().add("ORM");
        s.persist(readWriteVersionedCacheableItem);
        s.getTransaction().commit();
        s.close();
        Session s1 = openSession();
        s1.beginTransaction();
        final String AFTER = "after";
        RefreshUpdatedDataTest.ReadWriteCacheableItem readWriteCacheableItem1 = s1.get(RefreshUpdatedDataTest.ReadWriteCacheableItem.class, readWriteCacheableItem.getId());
        readWriteCacheableItem1.setName(AFTER);
        readWriteCacheableItem1.getTags().remove("ORM");
        RefreshUpdatedDataTest.ReadWriteVersionedCacheableItem readWriteVersionedCacheableItem1 = s1.get(RefreshUpdatedDataTest.ReadWriteVersionedCacheableItem.class, readWriteVersionedCacheableItem.getId());
        readWriteVersionedCacheableItem1.setName(AFTER);
        readWriteVersionedCacheableItem1.getTags().remove("ORM");
        s1.flush();
        s1.refresh(readWriteCacheableItem1);
        s1.refresh(readWriteVersionedCacheableItem1);
        Assert.assertEquals(AFTER, readWriteCacheableItem1.getName());
        Assert.assertEquals(1, readWriteCacheableItem1.getTags().size());
        Assert.assertEquals(AFTER, readWriteVersionedCacheableItem1.getName());
        Assert.assertEquals(1, readWriteVersionedCacheableItem1.getTags().size());
        // open another session
        Session s2 = openSession();
        try {
            s2.beginTransaction();
            RefreshUpdatedDataTest.ReadWriteCacheableItem readWriteCacheableItem2 = s2.get(RefreshUpdatedDataTest.ReadWriteCacheableItem.class, readWriteCacheableItem.getId());
            RefreshUpdatedDataTest.ReadWriteVersionedCacheableItem readWriteVersionedCacheableItem2 = s2.get(RefreshUpdatedDataTest.ReadWriteVersionedCacheableItem.class, readWriteVersionedCacheableItem.getId());
            Assert.assertEquals(BEFORE, readWriteCacheableItem2.getName());
            Assert.assertEquals(2, readWriteCacheableItem2.getTags().size());
            Assert.assertEquals(BEFORE, readWriteVersionedCacheableItem2.getName());
            Assert.assertEquals(2, readWriteVersionedCacheableItem2.getTags().size());
            s2.getTransaction().commit();
        } finally {
            if (s2.getTransaction().getStatus().canRollback()) {
                s2.getTransaction().rollback();
            }
            s2.close();
        }
        s1.getTransaction().rollback();
        s1.close();
        s = openSession();
        s.beginTransaction();
        s.delete(readWriteCacheableItem);
        s.delete(readWriteVersionedCacheableItem);
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "ReadWriteCacheableItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
    public static class ReadWriteCacheableItem {
        @Id
        @GeneratedValue
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

    @Entity(name = "ReadWriteVersionedCacheableItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
    public static class ReadWriteVersionedCacheableItem {
        @Id
        @GeneratedValue
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
}

