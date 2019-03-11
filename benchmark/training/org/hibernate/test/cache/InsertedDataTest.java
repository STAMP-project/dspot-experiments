/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for handling of data just inserted during a transaction being read from the database
 * and placed into cache.  Initially these cases went through putFromRead which causes problems because it
 * loses the context of that data having just been read.
 *
 * @author Steve Ebersole
 */
public class InsertedDataTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testInsert() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.getTransaction().commit();
        s.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete CacheableItem").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testInsertWithRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        s.getTransaction().rollback();
        s.close();
        Assert.assertFalse(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
    }

    @Test
    public void testInsertThenUpdate() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        item.setName("new data");
        s.getTransaction().commit();
        s.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete CacheableItem").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testInsertThenUpdateThenRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        item.setName("new data");
        s.getTransaction().rollback();
        s.close();
        Assert.assertFalse(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete CacheableItem").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testInsertWithRefresh() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        s.refresh(item);
        s.getTransaction().commit();
        s.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete CacheableItem").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testInsertWithRefreshThenRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        s.refresh(item);
        s.getTransaction().rollback();
        s.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        // Object lock = cacheMap.values().iterator().next();
        // assertEquals( "org.hibernate.testing.cache.AbstractReadWriteAccessStrategy$Lock", lock.getClass().getName() );
        s = openSession();
        s.beginTransaction();
        item = s.get(CacheableItem.class, item.getId());
        s.getTransaction().commit();
        s.close();
        Assert.assertNull("it should be null", item);
    }

    @Test
    public void testInsertWithClear() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        s.clear();
        s.getTransaction().commit();
        s.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete CacheableItem").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testInsertWithClearThenRollback() {
        sessionFactory().getCache().evictEntityRegions();
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        CacheableItem item = new CacheableItem("data");
        s.save(item);
        s.flush();
        s.clear();
        item = ((CacheableItem) (s.get(CacheableItem.class, item.getId())));
        s.getTransaction().rollback();
        s.close();
        Assert.assertFalse(sessionFactory().getCache().containsEntity(CacheableItem.class, item.getId()));
        s = openSession();
        s.beginTransaction();
        item = ((CacheableItem) (s.get(CacheableItem.class, item.getId())));
        s.getTransaction().commit();
        s.close();
        Assert.assertNull("it should be null", item);
    }
}

