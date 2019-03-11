/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.jcache.test;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.spi.support.DomainDataRegionTemplate;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for handling of data just inserted during a transaction being read from the database
 * and placed into cache.  Initially these cases went through putFromRead which causes problems because it
 * loses the context of that data having just been read.
 *
 * @author Steve Ebersole
 */
public class InsertedDataTest extends BaseUnitTestCase {
    private ServiceRegistry serviceRegistry;

    private SessionFactoryImplementor sessionFactory;

    @Test
    public void testInsert() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
        });
        Assert.assertTrue(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
        inTransaction(sessionFactory, ( s) -> {
            s.createQuery("delete CacheableItem").executeUpdate();
        });
    }

    @Test
    public void testInsertWithRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            s.getTransaction().markRollbackOnly();
        });
        Assert.assertFalse(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
    }

    @Test
    public void testInsertThenUpdate() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            item.setName("new data");
        });
        Assert.assertTrue(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
        inTransaction(sessionFactory, ( s) -> s.createQuery("delete CacheableItem").executeUpdate());
    }

    @Test
    public void testInsertThenUpdateThenRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            item.setName("new data");
            s.getTransaction().markRollbackOnly();
        });
        Assert.assertFalse(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
    }

    @Test
    public void testInsertWithRefresh() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            s.refresh(item);
        });
        Assert.assertTrue(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
        inTransaction(sessionFactory, ( s) -> s.createQuery("delete CacheableItem").executeUpdate());
    }

    @Test
    public void testInsertWithRefreshThenRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            s.refresh(item);
            s.getTransaction().markRollbackOnly();
        });
        inTransaction(sessionFactory, ( s) -> {
            final DomainDataRegionTemplate region = ((DomainDataRegionTemplate) (sessionFactory().getCache().getRegion("item")));
            final Object fromCache = region.getCacheStorageAccess().getFromCache(region.getEffectiveKeysFactory().createEntityKey(1L, sessionFactory().getMetamodel().entityPersister(.class), sessionFactory(), null), s);
            assertNotNull(fromCache);
            ExtraAssertions.assertTyping(.class, fromCache);
        });
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = s.get(.class, 1L);
            assertNull("it should be null", item);
        });
    }

    @Test
    public void testInsertWithClear() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            s.clear();
        });
        Assert.assertTrue(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
        inTransaction(sessionFactory, ( s) -> s.createQuery("delete CacheableItem").executeUpdate());
    }

    @Test
    public void testInsertWithClearThenRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        inTransaction(sessionFactory, ( s) -> {
            org.hibernate.jcache.test.CacheableItem item = new org.hibernate.jcache.test.CacheableItem("data");
            s.save(item);
            s.flush();
            s.clear();
            item = s.get(.class, item.getId());
            s.getTransaction().markRollbackOnly();
        });
        Assert.assertFalse(sessionFactory().getCache().containsEntity(InsertedDataTest.CacheableItem.class, 1L));
        inTransaction(sessionFactory, ( s) -> {
            final org.hibernate.jcache.test.CacheableItem item = s.get(.class, 1L);
            assertNull("it should be null", item);
        });
    }

    @Entity(name = "CacheableItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
    public static class CacheableItem {
        private Long id;

        private String name;

        public CacheableItem() {
        }

        public CacheableItem(String name) {
            this.name = name;
        }

        @Id
        @GeneratedValue(generator = "increment")
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
    }
}

