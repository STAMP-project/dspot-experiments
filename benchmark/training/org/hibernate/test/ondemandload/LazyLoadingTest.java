/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ondemandload;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class LazyLoadingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLazyCollectionLoadingWithClearedSession() {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        // first load the store, making sure collection is not initialized
        Store store = ((Store) (s.get(Store.class, 1)));
        Assert.assertNotNull(store);
        Assert.assertFalse(Hibernate.isInitialized(store.getInventories()));
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getSessionCloseCount());
        // then clear session and try to initialize collection
        s.clear();
        store.getInventories().size();
        Assert.assertTrue(Hibernate.isInitialized(store.getInventories()));
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
        s.clear();
        store = ((Store) (s.get(Store.class, 1)));
        Assert.assertNotNull(store);
        Assert.assertFalse(Hibernate.isInitialized(store.getInventories()));
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
        s.clear();
        store.getInventories().iterator();
        Assert.assertTrue(Hibernate.isInitialized(store.getInventories()));
        Assert.assertEquals(3, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionCloseCount());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLazyCollectionLoadingWithClosedSession() {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        // first load the store, making sure collection is not initialized
        Store store = ((Store) (s.get(Store.class, 1)));
        Assert.assertNotNull(store);
        Assert.assertFalse(Hibernate.isInitialized(store.getInventories()));
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getSessionCloseCount());
        // close the session and try to initialize collection
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
        store.getInventories().size();
        Assert.assertTrue(Hibernate.isInitialized(store.getInventories()));
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionCloseCount());
    }

    @Test
    public void testLazyEntityLoadingWithClosedSession() {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        s.beginTransaction();
        // first load the store, making sure it is not initialized
        Store store = ((Store) (s.load(Store.class, 1)));
        Assert.assertNotNull(store);
        Assert.assertFalse(Hibernate.isInitialized(store));
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getSessionCloseCount());
        // close the session and try to initialize store
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
        store.getName();
        Assert.assertTrue(Hibernate.isInitialized(store));
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionCloseCount());
    }
}

